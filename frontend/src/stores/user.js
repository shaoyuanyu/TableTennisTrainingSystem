import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'
import { normalizeRole } from '@/utils/permissions'
import { getErrorMessage } from '@/utils/errorHandler'

export const useUserStore = defineStore('user', () => {
  // 安全解析localStorage中的数据
  const getStoredUserInfo = () => {
    try {
      const stored = localStorage.getItem('userInfo')
      return stored ? JSON.parse(stored) : {}
    } catch (error) {
      console.error('解析用户信息失败:', error)
      localStorage.removeItem('userInfo')
      return {}
    }
  }

  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(getStoredUserInfo())

  // 重置状态方法（用于调试）
  const resetAuth = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    console.log('认证状态已重置')
  }

  // 计算属性
  const isLoggedIn = computed(() => {
    // session认证模式：检查是否有有效的用户信息
    // 不依赖token，因为session认证通过cookie维持状态
    return !!(userInfo.value && userInfo.value.id)
  })
  const userRole = computed(() => normalizeRole(userInfo.value.role) || '')
  const userName = computed(() => userInfo.value.name || '')
  const userId = computed(() => userInfo.value.id || '')
  const campusId = computed(() => userInfo.value.campusId || '')

  // 角色判断
  const isSuperAdmin = computed(() => userRole.value === 'super_admin')
  const isCampusAdmin = computed(() => userRole.value === 'campus_admin')
  const isStudent = computed(() => userRole.value === 'student')
  const isCoach = computed(() => userRole.value === 'coach')

  // 登录
  const login = async (credentials) => {
    try {
      // 使用表单数据格式，适配后端的auth-form认证
      const loginData = new URLSearchParams()
      loginData.append('username', credentials.username.trim())
      loginData.append('password', credentials.password)

      const response = await api.post('/user/login', loginData, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        withCredentials: true,
      })

      if (response.status === 200 && response.data && response.data.username) {
        // 假设后端返回用户信息，如果没有则从其他接口获取
        const userData = response.data

        // 设置用户信息（这里可能需要根据后端实际返回调整）
        userInfo.value = {
          id: userData.id || userData.username,
          name: userData.name || userData.username,
          username: userData.username,
          role: userData.role || 'STUDENT', // 保持后端返回的原始格式
          campusId: userData.campusId,
          email: userData.email,
          phone: userData.phone,
          avatar: userData.avatar,
        }

        // session认证模式，用户登录状态通过cookie维持
        // 设置一个本地标识帮助前端快速判断登录状态
        token.value = 'session-active'
        localStorage.setItem('token', 'session-active')

        // 保存用户信息到localStorage
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))

        console.log('登录成功，用户信息已保存:', {
          userInfo: userInfo.value,
          normalizedRole: normalizeRole(userInfo.value.role),
        })

        return { success: true, user: userInfo.value }
      } else {
        return { success: false, message: response.data?.message || '登录失败' }
      }
    } catch (error) {
      console.error('登录失败:', error)

      // 处理不同类型的错误
      let errorMessage = '登录失败，请稍后重试'

      if (error.response) {
        const { status, data } = error.response
        switch (status) {
          case 401:
            // 优先使用后端返回的具体错误信息，如"用户名不存在"、"密码错误"等
            errorMessage = getErrorMessage(data, '用户名或密码错误')
            break
          case 422:
            errorMessage = getErrorMessage(data, '请求参数错误')
            break
          case 429:
            errorMessage = getErrorMessage(data, '登录尝试过于频繁，请稍后再试')
            break
          case 500:
            errorMessage = getErrorMessage(data, '服务器内部错误，请稍后重试')
            break
          default:
            errorMessage = getErrorMessage(data, `登录失败 (${status})`)
        }
      } else if (error.code === 'NETWORK_ERROR' || error.message === 'Network Error') {
        errorMessage = '网络连接失败，请检查网络状态'
      } else if (error.code === 'ECONNABORTED') {
        errorMessage = '请求超时，请稍后重试'
      }

      return {
        success: false,
        message: errorMessage,
      }
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      const response = await api.post('/user/signup', registerData)
      if (response.status === 200 || response.status === 201) {
        return { success: true, data: response.data }
      } else {
        return { success: false, message: response.data?.message || '注册失败' }
      }
    } catch (error) {
      console.error('注册失败:', error)

      let errorMessage = '注册失败，请稍后重试'

      if (error.response) {
        const { status, data } = error.response
        switch (status) {
          case 400:
            errorMessage = getErrorMessage(data, '请求参数错误')
            break
          case 409:
            errorMessage = getErrorMessage(data, '用户名已存在')
            break
          case 422:
            errorMessage = getErrorMessage(data, '请求参数错误')
            break
          case 500:
            errorMessage = getErrorMessage(data, '服务器内部错误，请稍后重试')
            break
          default:
            errorMessage = getErrorMessage(data, `注册失败 (${status})`)
        }
      } else if (error.code === 'NETWORK_ERROR' || error.message === 'Network Error') {
        errorMessage = '网络连接失败，请检查网络状态'
      } else if (error.code === 'ECONNABORTED') {
        errorMessage = '请求超时，请稍后重试'
      }

      return { success: false, message: errorMessage }
    }
  }

  // 登出
  const logout = async () => {
    try {
      // 检查是否确实需要登出（避免重复调用）
      if (!userInfo.value.id) {
        console.log('用户未登录，无需登出')
        return
      }

      // session认证模式：调用后端登出接口清除服务端session
      await api.post('/user/logout', {}, { withCredentials: true })
    } catch (error) {
      console.error('调用登出接口失败:', error)
      // 即使后端登出失败，也要清除前端状态
    } finally {
      // 清除前端存储的状态
      token.value = ''
      userInfo.value = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')

      // 使用动态导入避免循环依赖
      const { default: router } = await import('@/router')

      // 只有当前不在登录页时才跳转
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
  }

  // 更新用户信息（session认证模式暂不实现）
  const updateUserInfo = async (newInfo) => {
    try {
      // session认证模式：暂时只更新本地信息
      // 后续可以添加具体的更新接口路径
      userInfo.value = { ...userInfo.value, ...newInfo }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      console.log('用户信息已在本地更新:', userInfo.value)
      return true
    } catch (error) {
      console.error('更新用户信息失败:', error)
      return false
    }
  }

  // 检查登录状态（session认证模式）
  const checkAuthStatus = async () => {
    try {
      // session认证模式：只需要检查本地是否有用户信息
      // 不需要调用后端验证，因为所有API请求都会自动携带session cookie
      if (!userInfo.value.id) {
        console.log('本地无用户信息，认证状态无效')
        return false
      }

      console.log('本地有用户信息，认证状态有效')
      return true
    } catch (error) {
      console.error('检查认证状态失败:', error)
      return false
    }
  }

  // 获取用户详细信息（session认证模式暂不实现）
  const fetchUserProfile = async () => {
    try {
      // session认证模式：用户信息在登录时已获取并存储
      // 如果需要更新用户信息，可以重新登录或调用其他专门的更新接口
      if (userInfo.value.id) {
        return { success: true, data: userInfo.value }
      } else {
        return { success: false, message: '用户未登录' }
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return {
        success: false,
        message: error.response?.data?.message || '获取用户信息失败',
      }
    }
  }

  // 修改密码（session认证模式暂不实现）
  const changePassword = async (passwordData) => {
    try {
      // session认证模式：暂时返回未实现提示
      // 后续可以添加具体的修改密码接口路径
      console.log('修改密码功能暂未实现:', passwordData)
      return { success: false, message: '修改密码功能暂未实现，请联系管理员' }
    } catch (error) {
      console.error('修改密码失败:', error)
      return {
        success: false,
        message: error.response?.data?.message || '修改密码失败',
      }
    }
  }

  return {
    // 状态
    token,
    userInfo,

    // 计算属性
    isLoggedIn,
    userRole,
    userName,
    userId,
    campusId,
    isSuperAdmin,
    isCampusAdmin,
    isStudent,
    isCoach,

    // 方法
    login,
    register,
    logout,
    updateUserInfo,
    changePassword,
    checkAuthStatus,
    fetchUserProfile,
    resetAuth,
  }
})
