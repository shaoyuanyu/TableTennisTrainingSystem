import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getErrorMessage } from './errorHandler'

// 运行时调试开关（在 .env* 中通过 VITE_ENABLE_DEBUG / VITE_LOG_LEVEL 控制）
const ENABLE_DEBUG = import.meta.env.VITE_ENABLE_DEBUG === 'true'

// 辅助：简单判断是否同源（协议 + 主机 + 端口）
function isSameOrigin(baseUrl) {
  try {
    const loc = window.location
    const u = new URL(baseUrl, loc.href)
    return u.protocol === loc.protocol && u.hostname === loc.hostname && u.port === loc.port
  } catch {
    return false
  }
}

// 在开发模式下给出跨域 + Cookie 相关风险提示
function devCookieDiagnostic(baseUrl) {
  if (!ENABLE_DEBUG) return
  if (typeof window === 'undefined') return
  const same = isSameOrigin(baseUrl)
  if (!same) {
    // 说明：如果不同源而服务端 Set-Cookie 未设置 SameSite=None; Secure，则另一台电脑访问会丢失 session
    // 另外还需要服务端响应头: Access-Control-Allow-Credentials: true
    // 以及前端 axios withCredentials = true（已设置）
    // 输出一次即可
    if (!window.__TT_COOKIE_DIAG_PRINTED) {
      window.__TT_COOKIE_DIAG_PRINTED = true
      console.group('%c[Cookie跨域诊断]', 'color:#e67e22;font-weight:bold;')
      console.warn('检测到前端与 API 不同源，若另一台电脑登录后请求未携带 Cookie，请核对以下要点:')
      console.log('1. 服务端 Set-Cookie 必须包含: SameSite=None; Secure (HTTPS 环境) 或 设置 SameSite=None 并在开发中启用 HTTPS 代理')
      console.log('2. 服务端 CORS 需允许: Access-Control-Allow-Credentials: true')
      console.log('3. 服务端 CORS 的 Access-Control-Allow-Origin 不能为 * ，需为具体源，如 http://localhost:5173 或实际域名')
      console.log('4. 前端 axios 必须设置 withCredentials=true (已在实例中强制)')
      console.log('5. 若通过 IP 访问后端，而 Set-Cookie 的 Domain 绑定为域名，将不会发送，需匹配或省略 Domain')
      console.log('6. 确认另一台电脑访问的前端地址与后端地址协议一致 (http vs https)')
      console.log('当前页面 origin:', window.location.origin)
      console.log('API baseURL:', baseUrl)
      console.groupEnd()
    }
  }
}

// 创建axios实例，配置session认证（通过cookie）
const apiBaseURL = import.meta.env.VITE_API_BASE_URL
const api = axios.create({
  baseURL: apiBaseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // session认证：所有请求自动携带cookie
})

devCookieDiagnostic(apiBaseURL)

// 请求拦截器，确保所有请求都带 withCredentials（session认证）
api.interceptors.request.use(
  (config) => {
    // session认证模式，确保所有请求都携带cookie（某些自定义 axios 调用可能覆盖）
    config.withCredentials = true

    if (ENABLE_DEBUG) {
      // 仅在调试时输出关键信息，不打印 body
      // 用于在另一台电脑复现时比对
      console.debug('[api][request]', {
        url: config.url,
        method: config.method,
        baseURL: config.baseURL,
        withCredentials: config.withCredentials,
      })
    }

    // 添加请求时间戳，用于调试
    config.metadata = { startTime: new Date() }

    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  },
)

// 响应拦截器 - 增强错误处理和自动重试
api.interceptors.response.use(
  (response) => {
    // 计算请求耗时
    const endTime = new Date()
    const duration = endTime - response.config.metadata?.startTime
    if (duration > 5000) {
      console.warn(`慢请求警告: ${response.config.url} 耗时 ${duration}ms`)
    }

    return response
  },
  async (error) => {
    const { response, config } = error

    if (response) {
      const { status, data } = response

      switch (status) {
        case 401: {
          // 防止无限循环：只有在非登录、非登出请求时才处理401错误
          const isLoginRequest = config.url?.includes('/user/login')
          const isLogoutRequest = config.url?.includes('/user/logout')

          if (!isLoginRequest && !isLogoutRequest) {
            // 对于非登录请求的401，显示会话过期消息
            const errorMsg = getErrorMessage(data, '会话已过期，请重新登录')
            ElMessage.error(errorMsg)
            const userStore = useUserStore()
            // 避免重复调用logout，检查用户是否确实已登录
            if (userStore.isLoggedIn) {
              userStore.logout()
            }
          }
          if (ENABLE_DEBUG) {
            // 输出当前 cookie 便于定位未携带问题（忽略潜在敏感内容，仅调试时启用）
            console.warn('[api][401] 当前 document.cookie =', document.cookie)
          }
          // 登录和登出请求的401错误由具体业务逻辑处理，这里不显示消息
          break
        }
        case 403: {
          const errorMsg = getErrorMessage(data, '权限不足，无法访问该资源')
          ElMessage.error(errorMsg)
          break
        }
        case 404: {
          const errorMsg = getErrorMessage(data, '请求的资源不存在')
          ElMessage.error(errorMsg)
          break
        }
        case 422:
          // 422 业务相关错误让具体业务逻辑处理
          break
        case 429:
          // 429 业务相关错误让具体业务逻辑处理
          break
        case 500: {
          const errorMsg = getErrorMessage(data, '服务器内部错误')
          ElMessage.error(errorMsg)
          break
        }
        case 502:
        case 503:
        case 504: {
          const errorMsg = getErrorMessage(data, '服务暂时不可用，请稍后重试')
          ElMessage.error(errorMsg)
          break
        }
        default:
          // 对于其他状态码，不在拦截器中显示消息，让具体业务逻辑处理
          break
      }
    } else if (error.code === 'NETWORK_ERROR' || error.message === 'Network Error') {
      ElMessage.error('网络连接失败，请检查网络状态')
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }

    return Promise.reject(error)
  },
)

export default api
