import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

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
    const { response } = error
    
    if (response) {
      const { status, data } = response

      switch (status) {
        case 401: {
          ElMessage.error('会话已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          break
        }
        case 403:
          ElMessage.error('权限不足，无法访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 422:
          ElMessage.error(data?.message || '请求参数错误')
          break
        case 429:
          ElMessage.error('请求过于频繁，请稍后再试')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        case 502:
        case 503:
        case 504:
          ElMessage.error('服务暂时不可用，请稍后重试')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
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
