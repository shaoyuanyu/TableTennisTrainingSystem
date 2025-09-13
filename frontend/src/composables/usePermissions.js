import { computed, ref, watchEffect } from 'vue'
import { useUserStore } from '@/stores/user'
import { hasPagePermission, hasActionPermission, getAccessibleMenus } from '@/utils/permissions'

/**
 * 权限管理 Composable
 * 提供统一的权限检查功能
 */
export function usePermissions() {
  const userStore = useUserStore()

  // 性能优化：缓存权限计算结果
  const cachedPermissions = ref({
    isLoggedIn: false,
    userRole: '',
    isSuperAdmin: false,
    isCampusAdmin: false,
    isStudent: false,
    isCoach: false,
    hasManagementAccess: false,
  })

  // 监听用户状态变化，更新缓存
  watchEffect(() => {
    const userRole = userStore.userRole
    const isLoggedIn = userStore.isLoggedIn

    cachedPermissions.value = {
      isLoggedIn,
      userRole,
      isSuperAdmin: userStore.isSuperAdmin,
      isCampusAdmin: userStore.isCampusAdmin,
      isStudent: userStore.isStudent,
      isCoach: userStore.isCoach,
      hasManagementAccess: userStore.isSuperAdmin || userStore.isCampusAdmin,
    }
  })

  // 当前用户角色
  const userRole = computed(() => cachedPermissions.value.userRole)

  // 是否已登录
  const isLoggedIn = computed(() => cachedPermissions.value.isLoggedIn)

  // 角色判断
  const isSuperAdmin = computed(() => cachedPermissions.value.isSuperAdmin)
  const isCampusAdmin = computed(() => cachedPermissions.value.isCampusAdmin)
  const isStudent = computed(() => cachedPermissions.value.isStudent)
  const isCoach = computed(() => cachedPermissions.value.isCoach)

  /**
   * 检查页面访问权限
   */
  const canAccessPage = (path) => {
    if (!cachedPermissions.value.isLoggedIn) return false
    return hasPagePermission(path, cachedPermissions.value.userRole)
  }

  /**
   * 检查操作权限
   */
  const canPerformAction = (action, context = {}) => {
    if (!cachedPermissions.value.isLoggedIn) return false
    return hasActionPermission(action, cachedPermissions.value.userRole, context)
  }

  /**
   * 获取用户可访问的菜单
   */
  const accessibleMenus = computed(() => {
    if (!cachedPermissions.value.isLoggedIn) return []
    return getAccessibleMenus(cachedPermissions.value.userRole)
  })

  /**
   * 检查是否有任意一个权限
   */
  const hasAnyPermission = (permissions) => {
    if (!cachedPermissions.value.isLoggedIn) return false
    return permissions.some((permission) => {
      if (typeof permission === 'string') {
        // 页面权限
        return hasPagePermission(permission, cachedPermissions.value.userRole)
      } else if (typeof permission === 'object') {
        // 操作权限
        return hasActionPermission(
          permission.action,
          cachedPermissions.value.userRole,
          permission.context,
        )
      }
      return false
    })
  }

  /**
   * 检查是否拥有所有权限
   */
  const hasAllPermissions = (permissions) => {
    if (!cachedPermissions.value.isLoggedIn) return false
    return permissions.every((permission) => {
      if (typeof permission === 'string') {
        // 页面权限
        return hasPagePermission(permission, cachedPermissions.value.userRole)
      } else if (typeof permission === 'object') {
        // 操作权限
        return hasActionPermission(
          permission.action,
          cachedPermissions.value.userRole,
          permission.context,
        )
      }
      return false
    })
  }

  /**
   * 检查是否有管理权限
   */
  const hasManagementAccess = computed(() => {
    return cachedPermissions.value.hasManagementAccess
  })

  /**
   * 检查校区访问权限
   */
  const canAccessCampus = (campusId) => {
    if (cachedPermissions.value.isSuperAdmin) return true
    if (cachedPermissions.value.isCampusAdmin) {
      // 校区管理员只能访问自己管理的校区
      return userStore.campusId === campusId
    }
    return false
  }

  return {
    // 用户信息
    userRole,
    isLoggedIn,
    isSuperAdmin,
    isCampusAdmin,
    isStudent,
    isCoach,
    hasManagementAccess,

    // 权限检查方法
    canAccessPage,
    canPerformAction,
    hasAnyPermission,
    hasAllPermissions,
    canAccessCampus,

    // 菜单相关
    accessibleMenus,
  }
}

/**
 * 权限检查 Vue 指令
 * 使用方式：v-permission="'manage_students'" 或 v-permission="['/admin/campus']"
 */
export const permissionDirective = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()

    if (!userStore.isLoggedIn) {
      el.style.display = 'none'
      return
    }

    let hasPermission = false

    if (Array.isArray(value)) {
      // 数组：检查是否有任意一个权限
      hasPermission = value.some((permission) => {
        if (typeof permission === 'string' && permission.startsWith('/')) {
          return hasPagePermission(permission, userStore.userRole)
        } else {
          return hasActionPermission(permission, userStore.userRole)
        }
      })
    } else if (typeof value === 'string') {
      if (value.startsWith('/')) {
        // 页面权限
        hasPermission = hasPagePermission(value, userStore.userRole)
      } else {
        // 操作权限
        hasPermission = hasActionPermission(value, userStore.userRole)
      }
    }

    if (!hasPermission) {
      el.style.display = 'none'
    }
  },

  updated(el, binding) {
    // 当绑定值更新时重新检查权限
    permissionDirective.mounted(el, binding)
  },
}
