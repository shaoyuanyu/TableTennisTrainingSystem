// 用户角色常量
export const USER_ROLES = {
  SUPER_ADMIN: 'super_admin',
  CAMPUS_ADMIN: 'campus_admin',
  STUDENT: 'student',
  COACH: 'coach',
}

// 页面权限配置
export const PAGE_PERMISSIONS = {
  // 公共页面
  '/': ['*'],
  '/login': ['*'],
  '/register/student': ['*'],
  '/register/coach': ['*'],
  '/dashboard': [
    USER_ROLES.SUPER_ADMIN,
    USER_ROLES.CAMPUS_ADMIN,
    USER_ROLES.STUDENT,
    USER_ROLES.COACH,
  ],

  // 超级管理员页面
  '/admin/campus': [USER_ROLES.SUPER_ADMIN],
  '/admin/service': [USER_ROLES.SUPER_ADMIN],

  // 校区管理员页面
  '/campus/students': [USER_ROLES.CAMPUS_ADMIN],
  '/campus/coaches': [USER_ROLES.CAMPUS_ADMIN],
  '/campus/appointments': [USER_ROLES.CAMPUS_ADMIN],
  '/campus/logs': [USER_ROLES.CAMPUS_ADMIN, USER_ROLES.SUPER_ADMIN],

  // 学员页面
  '/student/coaches': [USER_ROLES.STUDENT],
  '/student/my-coaches': [USER_ROLES.STUDENT],
  '/student/appointment': [USER_ROLES.STUDENT],
  '/student/schedule': [USER_ROLES.STUDENT],
  '/student/recharge': [USER_ROLES.STUDENT],
  '/student/tournament': [USER_ROLES.STUDENT],
  '/student/matches': [USER_ROLES.STUDENT],
  '/student/evaluation': [USER_ROLES.STUDENT],

  // 教练页面
  '/coach/appointments': [USER_ROLES.COACH],
  '/coach/schedule': [USER_ROLES.COACH],
  '/coach/evaluation': [USER_ROLES.COACH],

  // 通用页面
  '/profile': [
    USER_ROLES.SUPER_ADMIN,
    USER_ROLES.CAMPUS_ADMIN,
    USER_ROLES.STUDENT,
    USER_ROLES.COACH,
  ],
  '/messages': [
    USER_ROLES.SUPER_ADMIN,
    USER_ROLES.CAMPUS_ADMIN,
    USER_ROLES.STUDENT,
    USER_ROLES.COACH,
  ],
}

/**
 * 检查用户是否有访问某个页面的权限
 * @param {string} path 页面路径
 * @param {string} userRole 用户角色
 * @returns {boolean} 是否有权限
 */
export function hasPagePermission(path, userRole) {
  const permissions = PAGE_PERMISSIONS[path]

  if (!permissions) {
    return false
  }

  // 如果是公共页面（'*'）
  if (permissions.includes('*')) {
    return true
  }

  // 检查是否包含用户角色
  return permissions.includes(userRole)
}

/**
 * 根据用户角色获取默认首页路径
 * @param {string} userRole 用户角色
 * @returns {string} 默认首页路径
 */
export function getDefaultHomePage(userRole) {
  switch (userRole) {
    case USER_ROLES.SUPER_ADMIN:
      return '/admin/campus'
    case USER_ROLES.CAMPUS_ADMIN:
      return '/campus/students'
    case USER_ROLES.STUDENT:
      return '/student/coaches'
    case USER_ROLES.COACH:
      return '/coach/appointments'
    default:
      return '/dashboard'
  }
}
