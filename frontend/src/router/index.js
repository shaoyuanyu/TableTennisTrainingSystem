import {createRouter, createWebHistory} from 'vue-router'
// 权限相关导入
import {useUserStore} from '@/stores/user'
import {getDefaultHomePage, hasPagePermission, normalizeRole} from '@/utils/permissions'
import {ElMessage} from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard', // 游客和用户都跳转到首页
    },

    // 认证相关页面
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/register/student',
      name: 'StudentRegister',
      component: () => import('@/views/auth/StudentRegisterView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/register/coach',
      name: 'CoachRegister',
      component: () => import('@/views/auth/CoachRegisterView.vue'),
      meta: { requiresAuth: false },
    },

    // 主应用布局
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        // 仪表盘 - 根据角色显示对应仪表板，游客也可访问
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/HomeView.vue'),
          meta: {
            requiresAuth: false, // 允许游客访问
            title: '仪表盘',
          },
        },

        // 超级管理员页面
        {
          path: 'admin/campus',
          name: 'CampusManagement',
          component: () => import('@/views/admin/CampusManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '校区管理',
            roles: ['super_admin'],
          },
        },
        {
          path: 'admin/dashboard',
          name: 'SuperAdminDashboard',
          component: () => import('@/views/admin/SuperAdminDashboardView.vue'),
          meta: {
            requiresAuth: true,
            title: '超级管理员仪表盘',
            roles: ['super_admin'],
          },
        },
        {
          path: 'admin/service',
          name: 'ServiceStatus',
          component: () => import('@/views/admin/ServiceStatusView.vue'),
          meta: {
            requiresAuth: true,
            title: '服务状态',
            roles: ['super_admin'],
          },
        },
        {
          path: 'admin/data',
          name: 'DataExport',
          component: () => import('@/views/admin/DataExportView.vue'),
          meta: {
            requiresAuth: true,
            title: '数据导出',
            roles: ['super_admin'],
          },
        },
        {
          path: 'admin/logs',
          name: 'SystemLogs',
          component: () => import('@/views/admin/SystemLogsView.vue'),
          meta: {
            requiresAuth: true,
            title: '系统日志',
            roles: ['super_admin'],
          },
        },

        // 校区管理员页面
        {
          path: 'campus/students',
          name: 'StudentManagement',
          component: () => import('@/views/campus/StudentManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '学员管理',
            roles: ['campus_admin', 'super_admin'],
          },
        },
        {
          path: 'campus/coaches',
          name: 'CoachManagement',
          component: () => import('@/views/campus/CoachManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练管理',
            roles: ['campus_admin', 'super_admin'],
          },
        },
        {
          path: 'campus/tables',
          name: 'TableManagement',
          component: () => import('@/views/campus/TableManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '球台管理',
            roles: ['campus_admin', 'super_admin'],
          },
        },
        {
          path: 'campus/appointments',
          name: 'AppointmentManagement',
          component: () => import('@/views/campus/AppointmentManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '预约管理',
            roles: ['campus_admin'],
          },
        },
        {
          path: 'campus/tournaments',
          name: 'TournamentManagement',
          component: () => import('@/views/campus/TournamentManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '比赛管理',
            roles: ['campus_admin', 'super_admin'],
          },
        },
        {
          path: 'campus/logs',
          name: 'SystemLogs',
          component: () => import('@/views/campus/SystemLogsView.vue'),
          meta: {
            requiresAuth: true,
            title: '系统日志',
            roles: ['campus_admin', 'super_admin'],
          },
        },

        // 学员页面
        {
          path: 'student/dashboard',
          name: 'StudentDashboard',
          component: () => import('@/views/student/StudentDashboardView.vue'),
          meta: {
            requiresAuth: true,
            title: '学员仪表盘',
            roles: ['student'],
          },
        },
        {
          path: 'student/find-coach',
          name: 'FindCoach',
          component: () => import('@/views/student/FindCoachView.vue'),
          meta: {
            requiresAuth: true,
            title: '找教练',
            roles: ['student'],
          },
        },
        {
          path: 'student/coach-profile/:id',
          name: 'CoachProfile',
          component: () => import('@/views/student/CoachProfileView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练详情',
            roles: ['student'],
          },
        },
        {
          path: 'student/my-coaches',
          name: 'MyCoaches',
          component: () => import('@/views/student/MyCoachesView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的教练',
            roles: ['student'],
          },
        },
        {
          path: 'student/book-training',
          name: 'BookTraining',
          component: () => import('@/views/student/BookTrainingView.vue'),
          meta: {
            requiresAuth: true,
            title: '预约训练',
            roles: ['student'],
          },
        },
        {
          path: 'student/schedule',
          name: 'StudentSchedule',
          component: () => import('@/views/student/ScheduleView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的课表',
            roles: ['student'],
          },
        },
        {
          path: 'student/account-recharge',
          name: 'AccountRecharge',
          component: () => import('@/views/student/AccountRechargeView.vue'),
          meta: {
            requiresAuth: true,
            title: '账户充值',
            roles: ['student'],
          },
        },
        {
          path: 'student/tournament-registration',
          name: 'TournamentRegistration',
          component: () => import('@/views/student/TournamentRegistrationView.vue'),
          meta: {
            requiresAuth: true,
            title: '比赛报名',
            roles: ['student'],
          },
        },
        {
          path: 'student/mutual-selection',
          name: 'StudentMutualSelection',
          component: () => import('@/views/student/MutualSelectionView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练双选',
            roles: ['student'],
          },
        },
        {
          path: 'student/matches',
          name: 'MyMatches',
          component: () => import('@/views/student/MyMatchesView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的比赛',
            roles: ['student'],
          },
        },
        {
          path: 'student/evaluation',
          name: 'TrainingEvaluation',
          component: () => import('@/views/student/TrainingEvaluationView.vue'),
          meta: {
            requiresAuth: true,
            title: '训练评价',
            roles: ['student'],
          },
        },
        {
          path: 'student/my-appointments',
          name: 'MyAppointments',
          component: () => import('@/views/student/MyAppointmentsView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的预约',
            roles: ['student'],
          },
        },

        // 教练页面
        {
          path: 'coach/dashboard',
          name: 'CoachDashboard',
          component: () => import('@/views/coach/CoachDashboardView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练仪表盘',
            roles: ['coach'],
          },
        },
        {
          path: 'coach/appointment-approval',
          name: 'AppointmentApproval',
          component: () => import('@/views/coach/AppointmentApprovalView.vue'),
          meta: {
            requiresAuth: true,
            title: '预约审核',
            roles: ['coach'],
          },
        },
        {
          path: 'coach/schedule',
          name: 'CoachSchedule',
          component: () => import('@/views/coach/ScheduleView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的课表',
            roles: ['coach'],
          },
        },
        {
          path: 'coach/student-feedback',
          name: 'StudentFeedback',
          component: () => import('@/views/coach/StudentFeedbackView.vue'),
          meta: {
            requiresAuth: true,
            title: '学员反馈',
            roles: ['coach'],
          },
        },
        {
          path: 'coach/mutual-selection',
          name: 'CoachMutualSelection',
          component: () => import('@/views/coach/MutualSelectionView.vue'),
          meta: {
            requiresAuth: true,
            title: '学生双选',
            roles: ['coach'],
          },
        },

        // 通用页面
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: {
            requiresAuth: true,
            title: '个人中心',
            roles: ['super_admin', 'campus_admin', 'student', 'coach'],
          },
        },
        {
          path: 'messages',
          name: 'Messages',
          component: () => import('@/views/MessagesView.vue'),
          meta: {
            requiresAuth: true,
            title: '消息中心',
            roles: ['super_admin', 'campus_admin', 'student', 'coach'],
          },
        },
        {
          path: 'dev-tools',
          name: 'DevTools',
          component: () => import('@/views/DevToolsView.vue'),
          meta: {
            requiresAuth: false, // 允许游客访问
            title: '开发工具',
          },
        },
        {
          path: 'design-system-test',
          name: 'DesignSystemTest',
          component: () => import('@/views/DesignSystemTestView.vue'),
          meta: {
            requiresAuth: false, // 允许游客访问
            title: '设计语言样板间',
          },
        },
      ],
    },

    // 404页面
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/views/NotFoundView.vue'),
    },
  ],
})

// 路由守卫 - 身份验证和权限控制
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()

  console.log('🔍 路由守卫检查:', {
    "目标路径": to.path,
    "来源路径": from.path,
    "需要认证": to.meta.requiresAuth,
    "用户登录状态": userStore.isLoggedIn,
    "用户角色": userStore.userRole,
    "有用户信息": !!userStore.userInfo.id,
    "token": userStore.token ? '有' : '无',
    "要求角色": to.meta.roles,
  })

  // 如果是开发工具或测试页面，直接通过
  if (to.path === '/dev-tools' || to.path === '/design-system-test') {
    console.log('✅ 开发工具或测试页面，直接通过')
    next()
    return
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    console.log('❌ 需要认证但用户未登录，重定向到登录页')
    ElMessage.warning('请先登录')
    next('/login')
    return
  }

  // 如果已登录且访问登录页，重定向到对应首页
  if (userStore.isLoggedIn && to.path === '/login') {
    console.log('✅ 用户已登录访问登录页，重定向到首页')
    const homePage = getDefaultHomePage(userStore.userRole)
    next(homePage)
    return
  }

  // 如果是需要认证的页面，检查角色权限
  if (to.meta.requiresAuth && userStore.isLoggedIn) {
    // 优先检查路由元信息中的角色配置
    if (to.meta.roles && Array.isArray(to.meta.roles)) {
      const normalizedUserRole = normalizeRole(userStore.userRole)
      const hasRolePermission = to.meta.roles.includes(normalizedUserRole)
      console.log('角色权限检查:', {
        "原始角色": userStore.userRole,
        "标准化角色": normalizedUserRole,
        "要求角色": to.meta.roles,
        "有权限": hasRolePermission,
      })

      if (!hasRolePermission) {
        console.log('❌ 角色权限不足:', {
          "用户角色": normalizedUserRole,
          "要求角色": to.meta.roles,
        })
        ElMessage.error('您没有权限访问该页面')
        const homePage = getDefaultHomePage(userStore.userRole)
        next(homePage)
        return
      }
    } else {
      // 回退到权限配置文件检查
      if (!hasPagePermission(to.path, userStore.userRole)) {
        console.log('❌ 页面权限检查失败:', {
          "页面": to.path,
          "用户角色": userStore.userRole,
        })
        ElMessage.error('您没有权限访问该页面')
        const homePage = getDefaultHomePage(userStore.userRole)
        next(homePage)
        return
      }
    }
  }

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 乒乓球培训管理系统`
  }

  console.log('✅ 路由守卫检查通过，允许访问:', to.path)

  next()
})

export default router
