import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.DEV ? '/api' : 'http://49.140.92.33:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 直接返回响应数据，让各个API自己处理
    return response
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401: {
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          break
        }
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(data?.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }

    return Promise.reject(error)
  },
)

export default api
