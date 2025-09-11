// 用户角色常量
export const USER_ROLES = {
  SUPER_ADMIN: 'super_admin',
  CAMPUS_ADMIN: 'campus_admin', 
  STUDENT: 'student',
  COACH: 'coach',
}

// 角色标准化函数 - 将后端返回的角色转换为统一格式
export function normalizeRole(role) {
  if (!role) return ''
  const roleMap = {
    'SUPER_ADMIN': 'super_admin',
    'CAMPUS_ADMIN': 'campus_admin',
    'STUDENT': 'student',
    'COACH': 'coach',
    'super_admin': 'super_admin',
    'campus_admin': 'campus_admin', 
    'student': 'student',
    'coach': 'coach'
  }
  return roleMap[role] || role.toLowerCase()
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
    // 如果页面没有在权限配置中，默认拒绝访问
    console.warn(`页面 ${path} 没有配置权限`)
    return false
  }

  // 如果是公共页面（'*'）
  if (permissions.includes('*')) {
    return true
  }

  // 标准化用户角色后进行检查
  const normalizedRole = normalizeRole(userRole)
  console.log('权限检查:', { path, userRole, normalizedRole, permissions })
  
  // 检查是否包含标准化后的用户角色
  return permissions.includes(normalizedRole)
}

/**
 * 检查用户是否有执行某个操作的权限
 * @param {string} action 操作类型
 * @param {string} userRole 用户角色
 * @param {object} context 上下文信息（如校区ID等）
 * @returns {boolean} 是否有权限
 */
export function hasActionPermission(action, userRole, context = {}) {
  // eslint-disable-next-line no-unused-vars
  const { campusId, resourceId } = context // 为将来的扩展预留
  
  // 标准化用户角色
  const normalizedRole = normalizeRole(userRole)
  
  switch (action) {
    case 'manage_students':
      return normalizedRole === USER_ROLES.CAMPUS_ADMIN || normalizedRole === USER_ROLES.SUPER_ADMIN
    
    case 'manage_coaches':
      return normalizedRole === USER_ROLES.CAMPUS_ADMIN || normalizedRole === USER_ROLES.SUPER_ADMIN
    
    case 'manage_campus':
      return normalizedRole === USER_ROLES.SUPER_ADMIN
    
    case 'approve_appointments':
      return normalizedRole === USER_ROLES.COACH
    
    case 'book_training':
      return normalizedRole === USER_ROLES.STUDENT
    
    case 'view_system_logs':
      return normalizedRole === USER_ROLES.SUPER_ADMIN || normalizedRole === USER_ROLES.CAMPUS_ADMIN
    
    case 'export_data':
      return normalizedRole === USER_ROLES.SUPER_ADMIN
    
    default:
      return false
  }
}

/**
 * 获取用户可访问的菜单项
 * @param {string} userRole 用户角色
 * @returns {Array} 可访问的菜单项
 */
export function getAccessibleMenus(userRole) {
  const menus = []
  
  // 通用菜单
  menus.push({ 
    path: '/dashboard', 
    title: '仪表盘', 
    icon: 'Odometer' 
  })
  
  // 角色特定菜单
  switch (userRole) {
    case USER_ROLES.SUPER_ADMIN:
      menus.push(
        {
          title: '系统管理',
          icon: 'Setting',
          children: [
            { path: '/admin/campus', title: '校区管理', icon: 'OfficeBuilding' },
            { path: '/admin/service', title: '服务状态', icon: 'CreditCard' },
          ]
        }
      )
      break
      
    case USER_ROLES.CAMPUS_ADMIN:
      menus.push(
        {
          title: '校区管理',
          icon: 'Management',
          children: [
            { path: '/campus/students', title: '学员管理', icon: 'User' },
            { path: '/campus/coaches', title: '教练管理', icon: 'Avatar' },
            { path: '/campus/appointments', title: '预约管理', icon: 'Calendar' },
            { path: '/campus/logs', title: '日志查询', icon: 'Document' },
          ]
        }
      )
      break
      
    case USER_ROLES.STUDENT:
      menus.push(
        {
          title: '学员中心',
          icon: 'User',
          children: [
            { path: '/student/find-coach', title: '教练查询', icon: 'Search' },
            { path: '/student/my-coaches', title: '我的教练', icon: 'UserFilled' },
            { path: '/student/book-training', title: '课程预约', icon: 'Calendar' },
            { path: '/student/schedule', title: '我的课表', icon: 'Clock' },
            { path: '/student/account-recharge', title: '账户充值', icon: 'Wallet' },
            { path: '/student/tournament-registration', title: '比赛报名', icon: 'Trophy' },
            { path: '/student/matches', title: '我的比赛', icon: 'Medal' },
            { path: '/student/evaluation', title: '训练评价', icon: 'EditPen' },
          ]
        }
      )
      break
      
    case USER_ROLES.COACH:
      menus.push(
        {
          title: '教练中心',
          icon: 'Avatar',
          children: [
            { path: '/coach/appointment-approval', title: '预约审核', icon: 'Checked' },
            { path: '/coach/schedule', title: '我的课表', icon: 'Clock' },
            { path: '/coach/student-feedback', title: '学员评价', icon: 'EditPen' },
          ]
        }
      )
      break
  }
  
  // 通用菜单项
  menus.push(
    { path: '/profile', title: '个人中心', icon: 'User' },
    { path: '/messages', title: '消息中心', icon: 'Bell' }
  )
  
  return menus
}

/**
 * 根据用户角色获取默认首页路径
 * @param {string} userRole 用户角色（当前统一跳转到仪表盘，暂不使用此参数）
 * @returns {string} 默认首页路径
 */
export function getDefaultHomePage(userRole) {
  // eslint-disable-next-line no-unused-vars
  const _ = userRole // 避免参数未使用的警告
  
  // 所有用户登录后都跳转到统一的仪表盘首页
  return '/dashboard'
  
  // 如果需要按角色跳转到不同页面，可以使用以下配置：
  // const normalizedRole = normalizeRole(userRole)
  // switch (normalizedRole) {
  //   case USER_ROLES.SUPER_ADMIN:
  //     return '/admin/campus'
  //   case USER_ROLES.CAMPUS_ADMIN:
  //     return '/campus/students'
  //   case USER_ROLES.STUDENT:
  //     return '/dashboard'  // 学生也跳转到仪表盘
  //   case USER_ROLES.COACH:
  //     return '/dashboard'  // 教练也跳转到仪表盘
  //   default:
  //     return '/dashboard'
  // }
}
