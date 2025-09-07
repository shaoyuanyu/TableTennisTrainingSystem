import { createRouter, createWebHistory } from 'vue-router'
// 原权限相关导入 - 暂时注释掉，权限管理将在后端实现
// import { useUserStore } from '@/stores/user'
// import { hasPagePermission, getDefaultHomePage } from '@/utils/permissions'
// import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
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
      meta: { requiresAuth: false },
      children: [
        // 仪表盘 - 根据角色显示对应仪表板
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/HomeView.vue'),
        },

        // 超级管理员页面
        {
          path: 'admin/campus',
          name: 'CampusManagement',
          component: () => import('@/views/admin/CampusManagementView.vue'),
        },
        {
          path: 'admin/service',
          name: 'ServiceStatus',
          component: () => import('@/views/admin/ServiceStatusView.vue'),
        },

        // 校区管理员页面
        {
          path: 'campus/students',
          name: 'StudentManagement',
          component: () => import('@/views/campus/StudentManagementView.vue'),
        },
        {
          path: 'campus/coaches',
          name: 'CoachManagement',
          component: () => import('@/views/campus/CoachManagementView.vue'),
        },
        {
          path: 'campus/appointments',
          name: 'AppointmentManagement',
          component: () => import('@/views/campus/AppointmentManagementView.vue'),
        },
        {
          path: 'campus/logs',
          name: 'SystemLogs',
          component: () => import('@/views/campus/SystemLogsView.vue'),
        },

        // 学员页面
        {
          path: 'student/dashboard',
          name: 'StudentDashboard',
          component: () => import('@/views/student/StudentDashboardView.vue'),
        },
        {
          path: 'student/find-coach',
          name: 'FindCoach',
          component: () => import('@/views/student/FindCoachView.vue'),
        },
        {
          path: 'student/coach-profile/:id',
          name: 'CoachProfile',
          component: () => import('@/views/student/CoachProfileView.vue'),
        },
        {
          path: 'student/my-coaches',
          name: 'MyCoaches',
          component: () => import('@/views/student/MyCoachesView.vue'),
        },
        {
          path: 'student/book-training',
          name: 'BookTraining',
          component: () => import('@/views/student/BookTrainingView.vue'),
        },
        {
          path: 'student/schedule',
          name: 'StudentSchedule',
          component: () => import('@/views/student/ScheduleView.vue'),
        },
        {
          path: 'student/account-recharge',
          name: 'AccountRecharge',
          component: () => import('@/views/student/AccountRechargeView.vue'),
        },
        {
          path: 'student/tournament-registration',
          name: 'TournamentRegistration',
          component: () => import('@/views/student/TournamentRegistrationView.vue'),
        },
        {
          path: 'student/matches',
          name: 'MyMatches',
          component: () => import('@/views/student/MyMatchesView.vue'),
        },
        {
          path: 'student/evaluation',
          name: 'TrainingEvaluation',
          component: () => import('@/views/student/TrainingEvaluationView.vue'),
        },

        // 教练页面
        {
          path: 'coach/dashboard',
          name: 'CoachDashboard',
          component: () => import('@/views/coach/CoachDashboardView.vue'),
        },
        {
          path: 'coach/appointment-approval',
          name: 'AppointmentApproval',
          component: () => import('@/views/coach/AppointmentApprovalView.vue'),
        },
        {
          path: 'coach/schedule',
          name: 'CoachSchedule',
          component: () => import('@/views/coach/ScheduleView.vue'),
        },
        {
          path: 'coach/student-feedback',
          name: 'StudentFeedback',
          component: () => import('@/views/coach/StudentFeedbackView.vue'),
        },

        // 通用页面
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/ProfileView.vue'),
        },
        {
          path: 'messages',
          name: 'Messages',
          component: () => import('@/views/MessagesView.vue'),
        },
        {
          path: 'page-square',
          name: 'PageSquare',
          component: () => import('@/views/PageSquareView.vue'),
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

// 原有的路由守卫 - 暂时注释掉，权限管理将在后端实现
/*
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 检查是否需要认证
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 如果已登录且访问登录页，重定向到对应首页
  if (userStore.isLoggedIn && to.path === '/login') {
    const homePage = getDefaultHomePage(userStore.userRole)
    next(homePage)
    return
  }

  // 检查页面权限
  if (userStore.isLoggedIn && !hasPagePermission(to.path, userStore.userRole)) {
    ElMessage.error('您没有权限访问该页面')
    next(false)
    return
  }

  next()
})
*/

// 临时的简化路由守卫 - 取消所有权限限制
router.beforeEach((to, from, next) => {
  // 允许访问所有路由，不进行权限检查
  next()
})

export default router
