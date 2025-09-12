import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getErrorMessage } from './errorHandler'

// 创建axios实例，配置session认证（通过cookie）
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true // session认证：所有请求自动携带cookie
})

// 请求拦截器，确保所有请求都带 withCredentials（session认证）
api.interceptors.request.use(
  (config) => {
    // session认证模式，确保所有请求都携带cookie
    config.withCredentials = true
    
    // session认证不需要Authorization头，注释掉JWT相关代码
    // const userStore = useUserStore()
    // if (userStore.token && userStore.token !== 'session-authenticated') {
    //   config.headers.Authorization = `Bearer ${userStore.token}`
    // }
    
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
