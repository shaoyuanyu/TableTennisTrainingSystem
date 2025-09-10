import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value.role || '')
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
      const response = await api.post('/auth/login', credentials)
      const { token: newToken, user } = response.data

      token.value = newToken
      userInfo.value = user

      // 保存到localStorage
      localStorage.setItem('token', newToken)
      localStorage.setItem('userInfo', JSON.stringify(user))

      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      const response = await api.post('/user/signup', registerData)
      return response.data
    } catch (error) {
      console.error('注册失败:', error)
      // 处理后端返回的错误信息
      if (error.response && error.response.data) {
        throw new Error(error.response.data)
      }
      throw error
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = {}

    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')

    router.push('/login')
  }

  // 更新用户信息
  const updateUserInfo = async (newInfo) => {
    try {
      const response = await api.put('/user/profile', newInfo)
      userInfo.value = { ...userInfo.value, ...response.data }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return true
    } catch (error) {
      console.error('更新用户信息失败:', error)
      return false
    }
  }

  // 修改密码
  const changePassword = async (passwordData) => {
    try {
      await api.put('/user/password', passwordData)
      return true
    } catch (error) {
      console.error('修改密码失败:', error)
      return false
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
  }
})
