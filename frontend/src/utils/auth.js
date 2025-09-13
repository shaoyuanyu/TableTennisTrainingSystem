import { useUserStore } from '@/stores/user'
import router from '@/router'

/**
 * 初始化应用认证状态
 * 在应用启动时调用，检查本地存储的认证信息是否有效
 */
export async function initializeAuth() {
  const userStore = useUserStore()

  try {
    // 如果本地有用户信息，尝试验证
    if (userStore.isLoggedIn) {
      const result = await userStore.checkAuthStatus()
      if (!result) {
        // 认证失效，清除本地数据
        console.warn('认证状态已失效，清除本地数据')
        userStore.logout()
      }
    }
  } catch (error) {
    console.error('初始化认证状态失败:', error)
    // 发生错误时清除认证信息
    userStore.logout()
  }
}

/**
 * 检查是否需要登录
 * 用于页面组件中的手动检查
 */
export function requireAuth() {
  const userStore = useUserStore()

  if (!userStore.isLoggedIn) {
    router.push('/login')
    return false
  }

  return true
}

/**
 * 检查角色权限
 */
export function requireRole(roles) {
  const userStore = useUserStore()

  if (!userStore.isLoggedIn) {
    router.push('/login')
    return false
  }

  const hasRole = Array.isArray(roles)
    ? roles.includes(userStore.userRole)
    : userStore.userRole === roles

  if (!hasRole) {
    router.push('/dashboard')
    return false
  }

  return true
}

/**
 * 获取用户令牌（如果使用token认证）
 */
export function getAuthToken() {
  const userStore = useUserStore()
  return userStore.token
}

/**
 * 检查是否为管理员
 */
export function isAdmin() {
  const userStore = useUserStore()
  return userStore.isSuperAdmin || userStore.isCampusAdmin
}

/**
 * 退出登录并清理状态
 */
export function logout() {
  const userStore = useUserStore()
  userStore.logout()
}

export default {
  initializeAuth,
  requireAuth,
  requireRole,
  getAuthToken,
  isAdmin,
  logout,
}
