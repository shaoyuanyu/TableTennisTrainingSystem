import { createRouter, createWebHistory } from 'vue-router'
// 权限相关导入 (在开发模式下可以暂时不使用)
// import { useUserStore } from '@/stores/user'
// import { hasPagePermission, getDefaultHomePage, normalizeRole } from '@/utils/permissions'
// import { ElMessage } from 'element-plus'

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
      meta: { requiresAuth: true }, // 即使这里是true，也会被我们的新守卫放行
      children: [
        // 仪表盘 - 根据角色显示对应仪表板，游客也可访问
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/HomeView.vue'),
          meta: {
            requiresAuth: false, // 允许游客访问
            title: '仪表盘'
          }
        },

        // 超级管理员页面
        {
          path: 'admin/campus',
          name: 'CampusManagement',
          component: () => import('@/views/admin/CampusManagementViewNew.vue'),
          meta: {
            requiresAuth: true,
            title: '校区管理',
            roles: ['super_admin']
          }
        },
        {
          path: 'admin/service',
          name: 'ServiceStatus',
          component: () => import('@/views/admin/ServiceStatusView.vue'),
          meta: {
            requiresAuth: true,
            title: '服务状态',
            roles: ['super_admin']
          }
        },
        {
          path: 'admin/data',
          name: 'DataExport',
          component: () => import('@/views/admin/DataExportView.vue'),
          meta: {
            requiresAuth: true,
            title: '数据导出',
            roles: ['super_admin']
          }
        },
        {
          path: 'admin/logs',
          name: 'SystemLogs',
          component: () => import('@/views/admin/SystemLogsView.vue'),
          meta: {
            requiresAuth: true,
            title: '系统日志',
            roles: ['super_admin']
          }
        },

        // 校区管理员页面
        {
          path: 'campus/students',
          name: 'StudentManagement',
          component: () => import('@/views/campus/StudentManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '学员管理',
            roles: ['campus_admin']
          }
        },
        {
          path: 'campus/coaches',
          name: 'CoachManagement',
          component: () => import('@/views/campus/CoachManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练管理',
            roles: ['campus_admin']
          }
        },
        {
          path: 'campus/appointments',
          name: 'AppointmentManagement',
          component: () => import('@/views/campus/AppointmentManagementView.vue'),
          meta: {
            requiresAuth: true,
            title: '预约管理',
            roles: ['campus_admin']
          }
        },
        {
          path: 'campus/logs',
          name: 'SystemLogs',
          component: () => import('@/views/campus/SystemLogsView.vue'),
          meta: {
            requiresAuth: true,
            title: '系统日志',
            roles: ['campus_admin', 'super_admin']
          }
        },

        // 学员页面
        {
          path: 'student/dashboard',
          name: 'StudentDashboard',
          component: () => import('@/views/student/StudentDashboardView.vue'),
          meta: {
            requiresAuth: true,
            title: '学员仪表盘',
            roles: ['student']
          }
        },
        {
          path: 'student/find-coach',
          name: 'FindCoach',
          component: () => import('@/views/student/FindCoachView.vue'),
          meta: {
            requiresAuth: true,
            title: '找教练',
            roles: ['student']
          }
        },
        {
          path: 'student/coach-profile/:id',
          name: 'CoachProfile',
          component: () => import('@/views/student/CoachProfileView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练详情',
            roles: ['student']
          }
        },
        {
          path: 'student/my-coaches',
          name: 'MyCoaches',
          component: () => import('@/views/student/MyCoachesView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的教练',
            roles: ['student']
          }
        },
        {
          path: 'student/book-training',
          name: 'BookTraining',
          component: () => import('@/views/student/BookTrainingView.vue'),
          meta: {
            requiresAuth: true,
            title: '预约训练',
            roles: ['student']
          }
        },
        {
          path: 'student/schedule',
          name: 'StudentSchedule',
          component: () => import('@/views/student/ScheduleView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的课表',
            roles: ['student']
          }
        },
        {
          path: 'student/account-recharge',
          name: 'AccountRecharge',
          component: () => import('@/views/student/AccountRechargeView.vue'),
          meta: {
            requiresAuth: true,
            title: '账户充值',
            roles: ['student']
          }
        },
        {
          path: 'student/tournament-registration',
          name: 'TournamentRegistration',
          component: () => import('@/views/student/TournamentRegistrationView.vue'),
          meta: {
            requiresAuth: true,
            title: '比赛报名',
            roles: ['student']
          }
        },
        {
          path: 'student/matches',
          name: 'MyMatches',
          component: () => import('@/views/student/MyMatchesView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的比赛',
            roles: ['student']
          }
        },
        {
          path: 'student/evaluation',
          name: 'TrainingEvaluation',
          component: () => import('@/views/student/TrainingEvaluationView.vue'),
          meta: {
            requiresAuth: true,
            title: '训练评价',
            roles: ['student']
          }
        },

        // 教练页面
        {
          path: 'coach/dashboard',
          name: 'CoachDashboard',
          component: () => import('@/views/coach/CoachDashboardView.vue'),
          meta: {
            requiresAuth: true,
            title: '教练仪表盘',
            roles: ['coach']
          }
        },
        {
          path: 'coach/appointment-approval',
          name: 'AppointmentApproval',
          component: () => import('@/views/coach/AppointmentApprovalView.vue'),
          meta: {
            requiresAuth: true,
            title: '预约审核',
            roles: ['coach']
          }
        },
        {
          path: 'coach/schedule',
          name: 'CoachSchedule',
          component: () => import('@/views/coach/ScheduleView.vue'),
          meta: {
            requiresAuth: true,
            title: '我的课表',
            roles: ['coach']
          }
        },
        {
          path: 'coach/student-feedback',
          name: 'StudentFeedback',
          component: () => import('@/views/coach/StudentFeedbackView.vue'),
          meta: {
            requiresAuth: true,
            title: '学员反馈',
            roles: ['coach']
          }
        },

        // 通用页面
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: {
            requiresAuth: true,
            title: '个人中心',
            roles: ['super_admin', 'campus_admin', 'student', 'coach']
          }
        },
        {
          path: 'messages',
          name: 'Messages',
          component: () => import('@/views/MessagesView.vue'),
          meta: {
            requiresAuth: true,
            title: '消息中心',
            roles: ['super_admin', 'campus_admin', 'student', 'coach']
          }
        },
        {
          path: 'dev-tools',
          name: 'DevTools',
          component: () => import('@/views/DevToolsView.vue'),
          meta: {
            requiresAuth: false, // 允许游客访问
            title: '开发工具'
          }
        },
        {
          path: 'design-system-test',
          name: 'DesignSystemTest',
          component: () => import('@/views/DesignSystemTestView.vue'),
          meta: {
            requiresAuth: false, // 允许游客访问
            title: '设计语言样板间'
          }
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

// [开发模式] 临时的路由守卫，取消所有权限验证，允许访问所有页面
router.beforeEach((to, from, next) => {
  console.log(`[DEV MODE] Bypassing auth checks for: ${to.path}`);

  // 设置页面标题 (这部分逻辑可以保留)
  if (to.meta.title) {
    document.title = `${to.meta.title} - 乒乓球培训管理系统`
  }
  
  // 无条件放行
  next();
});

export default router