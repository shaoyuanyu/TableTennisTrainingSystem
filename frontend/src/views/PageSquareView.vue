<template>
  <div class="page-square">
    <div class="background-decorations">
      <div class="test-icon code-icon">💻</div>
      <div class="test-icon test-tube-icon">🧪</div>
      <div class="test-icon check-icon">✅</div>
      <div class="test-icon rocket-icon">🚀</div>

      <div class="navigation-decoration map-icon">🗺️</div>
      <div class="navigation-decoration compass-icon">🧭</div>
      <div class="navigation-decoration target-icon">🎯</div>

      <div class="art-text">
        <div class="art-text-main">CONSOLE</div>
        <div class="art-text-sub">开发测试控制台</div>
      </div>

      <div class="geometric-decoration geo-1"></div>
      <div class="geometric-decoration geo-2"></div>
      <div class="geometric-decoration geo-3"></div>
    </div>

    <div class="container">
      <!-- 角色控制台 -->
      <el-card class="role-console" shadow="hover">
        <template #header>
          <div class="console-header">
            <span class="icon">🎭</span>
            <span>用户角色控制台</span>
            <el-tag :type="currentRoleTag.type" :color="currentRoleTag.color" size="small">
              {{ currentRoleTag.label }}
            </el-tag>
          </div>
        </template>
        <div class="role-controls">
          <div class="current-role">
            <div class="role-avatar">{{ currentRoleInfo.icon }}</div>
            <div class="role-info">
              <div class="role-name">{{ currentRoleInfo.name }}</div>
              <div class="role-desc">{{ currentRoleInfo.description }}</div>
            </div>
          </div>
          <div class="role-switcher">
            <el-button-group>
              <el-button
                v-for="role in availableRoles"
                :key="role.value"
                :type="currentRole === role.value ? 'primary' : 'default'"
                :disabled="currentRole === role.value"
                size="small"
                @click="switchRole(role.value)"
              >
                {{ role.icon }} {{ role.label }}
              </el-button>
            </el-button-group>
          </div>
          <div class="role-actions">
            <el-button type="success" size="small" @click="testCurrentRole">
              <el-icon>
                <View />
              </el-icon>
              测试当前角色
            </el-button>
            <el-button type="warning" size="small" @click="clearRoleData">
              <el-icon>
                <Delete />
              </el-icon>
              清除角色数据
            </el-button>
            <el-button type="info" size="small" @click="showRolePermissions">
              <el-icon>
                <InfoFilled />
              </el-icon>
              权限说明
            </el-button>
          </div>
        </div>
      </el-card>

      <div class="page-header">
        <h1>🎯 开发测试控制台 - 测试导航中心</h1>
        <p>乒乓球培训管理系统 - 所有页面功能测试</p>
        <div class="stats">
          <span class="stat-item">总页面: {{ totalPages }}</span>
          <span class="stat-item">已测试: {{ testedPages }}</span>
          <span class="stat-item">通过率: {{ passRate }}%</span>
        </div>
      </div>

      <el-card class="section-card auth-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">🔐</span>
            <span>认证页面</span>
            <el-tag type="info" size="small">{{ authPages.length }}个页面</el-tag>
          </div>
        </template>
        <div class="page-grid">
          <div
            v-for="page in authPages"
            :key="page.path"
            class="page-card"
            @click="navigateToPage(page)"
          >
            <div class="page-icon">{{ page.icon }}</div>
            <div class="page-info">
              <div class="page-title">{{ page.title }}</div>
              <div class="page-description">{{ page.description }}</div>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <div class="page-status">
              <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                {{ page.tested ? '已测试' : '未测试' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="section-card admin-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">👑</span>
            <span>超级管理员</span>
            <el-tag type="danger" size="small">{{ adminPages.length }}个页面</el-tag>
          </div>
        </template>
        <div class="page-grid">
          <div
            v-for="page in adminPages"
            :key="page.path"
            class="page-card"
            @click="navigateToPage(page)"
          >
            <div class="page-icon">{{ page.icon }}</div>
            <div class="page-info">
              <div class="page-title">{{ page.title }}</div>
              <div class="page-description">{{ page.description }}</div>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <div class="page-status">
              <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                {{ page.tested ? '已测试' : '未测试' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="section-card campus-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">🏢</span>
            <span>校区管理员</span>
            <el-tag type="warning" size="small">{{ campusPages.length }}个页面</el-tag>
          </div>
        </template>
        <div class="page-grid">
          <div
            v-for="page in campusPages"
            :key="page.path"
            class="page-card"
            @click="navigateToPage(page)"
          >
            <div class="page-icon">{{ page.icon }}</div>
            <div class="page-info">
              <div class="page-title">{{ page.title }}</div>
              <div class="page-description">{{ page.description }}</div>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <div class="page-status">
              <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                {{ page.tested ? '已测试' : '未测试' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="section-card student-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">🎓</span>
            <span>学员页面</span>
            <el-tag type="primary" size="small">{{ studentPages.length }}个页面</el-tag>
          </div>
        </template>
        <div class="page-grid">
          <div
            v-for="page in studentPages"
            :key="page.path"
            class="page-card"
            @click="navigateToPage(page)"
          >
            <div class="page-icon">{{ page.icon }}</div>
            <div class="page-info">
              <div class="page-title">{{ page.title }}</div>
              <div class="page-description">{{ page.description }}</div>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <div class="page-status">
              <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                {{ page.tested ? '已测试' : '未测试' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="section-card coach-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">👨‍🏫</span>
            <span>教练页面</span>
            <el-tag type="success" size="small">{{ coachPages.length }}个页面</el-tag>
          </div>
        </template>
        <div class="page-grid">
          <div
            v-for="page in coachPages"
            :key="page.path"
            class="page-card"
            @click="navigateToPage(page)"
          >
            <div class="page-icon">{{ page.icon }}</div>
            <div class="page-info">
              <div class="page-title">{{ page.title }}</div>
              <div class="page-description">{{ page.description }}</div>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <div class="page-status">
              <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                {{ page.tested ? '已测试' : '未测试' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="section-card common-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">⚙️</span>
            <span>通用页面</span>
            <el-tag size="small">{{ commonPages.length }}个页面</el-tag>
          </div>
        </template>
        <div class="page-grid">
          <div
            v-for="page in commonPages"
            :key="page.path"
            class="page-card"
            @click="navigateToPage(page)"
          >
            <div class="page-icon">{{ page.icon }}</div>
            <div class="page-info">
              <div class="page-title">{{ page.title }}</div>
              <div class="page-description">{{ page.description }}</div>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <div class="page-status">
              <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                {{ page.tested ? '已测试' : '未测试' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="section-card tools-card" shadow="hover">
        <template #header>
          <div class="section-header">
            <span class="icon">🔧</span>
            <span>开发工具</span>
          </div>
        </template>
        <div class="tools-grid">
          <el-button type="danger" size="large" @click="goToDebugPage">
            <el-icon>
              <Tools />
            </el-icon>
            权限系统调试
          </el-button>
          <el-button type="primary" @click="markAllTested">
            <el-icon>
              <Check />
            </el-icon>
            标记全部已测试
          </el-button>
          <el-button type="warning" @click="resetTestStatus">
            <el-icon>
              <Refresh />
            </el-icon>
            重置测试状态
          </el-button>
          <el-button type="success" @click="exportTestReport">
            <el-icon>
              <Download />
            </el-icon>
            导出测试报告
          </el-button>
          <el-button type="info" @click="openAllPages">
            <el-icon>
              <FolderOpened />
            </el-icon>
            批量打开页面
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  Refresh,
  Download,
  FolderOpened,
  View,
  Delete,
  InfoFilled,
  Tools,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

// 角色控制台相关状态
const currentRole = ref('guest')

// 可用角色配置
const availableRoles = ref([
  { value: 'guest', label: '游客', icon: '👤' },
  { value: 'student', label: '学员', icon: '🎓' },
  { value: 'coach', label: '教练', icon: '👨‍🏫' },
  { value: 'campus_admin', label: '校区管理员', icon: '🏢' },
  { value: 'super_admin', label: '超级管理员', icon: '👑' },
])

// 当前角色信息
const currentRoleInfo = computed(() => {
  const roleInfoMap = {
    guest: {
      name: '游客用户',
      description: '未登录状态，只能访问公开页面',
      icon: '👤',
      permissions: ['查看登录页面', '查看注册页面', '查看页面广场'],
    },
    student: {
      name: '学员用户',
      description: '已注册的学员，可以预约课程、查看教练',
      icon: '🎓',
      permissions: ['预约课程', '查看教练', '管理个人信息', '充值账户', '参与比赛'],
    },
    coach: {
      name: '教练用户',
      description: '认证教练，可以管理课程、查看学员反馈',
      icon: '👨‍🏫',
      permissions: ['管理课程安排', '审核预约', '查看学员反馈', '管理个人档案'],
    },
    campus_admin: {
      name: '校区管理员',
      description: '校区级管理员，管理本校区的学员和教练',
      icon: '🏢',
      permissions: ['管理学员', '管理教练', '管理预约', '查看校区数据', '系统日志'],
    },
    super_admin: {
      name: '超级管理员',
      description: '系统最高权限，管理所有校区和系统设置',
      icon: '👑',
      permissions: ['管理所有校区', '系统配置', '数据导出', '服务监控', '完整权限'],
    },
  }
  return roleInfoMap[currentRole.value] || roleInfoMap.guest
})

// 当前角色标签样式
const currentRoleTag = computed(() => {
  const tagMap = {
    guest: { type: 'info', color: '#909399', label: '游客模式' },
    student: { type: 'primary', color: '#409EFF', label: '学员模式' },
    coach: { type: 'success', color: '#67C23A', label: '教练模式' },
    campus_admin: { type: 'warning', color: '#E6A23C', label: '校区管理员模式' },
    super_admin: { type: 'danger', color: '#F56C6C', label: '超级管理员模式' },
  }
  return tagMap[currentRole.value] || tagMap.guest
})

// 切换角色
const switchRole = (role) => {
  currentRole.value = role

  // 模拟设置用户信息到 userStore
  if (role === 'guest') {
    // 清除用户信息
    userStore.token = ''
    userStore.userInfo = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  } else {
    // 设置模拟用户信息
    const mockUser = createMockUser(role)
    userStore.token = 'mock-token-' + role
    userStore.userInfo = mockUser
    localStorage.setItem('token', userStore.token)
    localStorage.setItem('userInfo', JSON.stringify(mockUser))
  }

  // 保存当前角色到本地存储
  localStorage.setItem('testRole', role)

  ElMessage.success(`已切换到${currentRoleInfo.value.name}模式`)
}

// 创建模拟用户数据
const createMockUser = (role) => {
  const userMap = {
    student: {
      id: 1001,
      name: '张同学',
      role: 'student',
      email: 'student@test.com',
      phone: '13800138001',
      campusId: 1,
      balance: 1500.0,
      level: '初级',
    },
    coach: {
      id: 2001,
      name: '李教练',
      role: 'coach',
      email: 'coach@test.com',
      phone: '13800138002',
      campusId: 1,
      experience: 5,
      specialties: ['基础训练', '技术提升'],
    },
    campus_admin: {
      id: 3001,
      name: '王管理员',
      role: 'campus_admin',
      email: 'admin@test.com',
      phone: '13800138003',
      campusId: 1,
      campusName: '测试校区',
    },
    super_admin: {
      id: 4001,
      name: '系统管理员',
      role: 'super_admin',
      email: 'superadmin@test.com',
      phone: '13800138004',
      permissions: ['all'],
    },
  }
  return userMap[role] || {}
}

// 测试当前角色
const testCurrentRole = () => {
  const rolePages = {
    guest: ['/login', '/register/student', '/register/coach'],
    student: ['/dashboard', '/student/find-coach', '/student/book-training'],
    coach: ['/dashboard', '/coach/appointment-approval', '/coach/schedule'],
    campus_admin: ['/dashboard', '/campus/students', '/campus/coaches'],
    super_admin: ['/dashboard', '/admin/campus', '/admin/service'],
  }

  const pages = rolePages[currentRole.value] || ['/page-square']

  ElMessageBox.confirm(
    `将在新标签页中打开 ${currentRoleInfo.value.name} 的主要页面进行测试`,
    '测试当前角色',
    {
      confirmButtonText: '开始测试',
      cancelButtonText: '取消',
      type: 'info',
    },
  ).then(() => {
    pages.forEach((page, index) => {
      setTimeout(() => {
        const url = window.location.origin + page
        window.open(url, '_blank')
      }, index * 800)
    })
    ElMessage.success(`正在打开 ${currentRoleInfo.value.name} 的测试页面...`)
  })
}

// 清除角色数据
const clearRoleData = () => {
  ElMessageBox.confirm('这将清除所有角色相关的本地数据，确定继续吗？', '清除角色数据', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    currentRole.value = 'guest'
    userStore.token = ''
    userStore.userInfo = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('testRole')
    ElMessage.success('角色数据已清除，已切换到游客模式')
  })
}

// 显示角色权限
const showRolePermissions = () => {
  const permissions = currentRoleInfo.value.permissions.join('、')
  ElMessageBox.alert(
    `${currentRoleInfo.value.name}的权限包括：\n\n${permissions}`,
    '角色权限说明',
    {
      confirmButtonText: '知道了',
      type: 'info',
    },
  )
}

// 跳转到调试页面
const goToDebugPage = () => {
  router.push('/debug')
  ElMessage.success('正在跳转到权限系统调试页面...')
}

// 从 localStorage 获取测试状态
const getTestStatusFromStorage = () => {
  try {
    const stored = localStorage.getItem('pageSquare_testStatus')
    return stored ? JSON.parse(stored) : {}
  } catch {
    return {}
  }
}

// 保存测试状态到 localStorage
const saveTestStatusToStorage = () => {
  const allPages = [
    ...authPages.value,
    ...adminPages.value,
    ...campusPages.value,
    ...studentPages.value,
    ...coachPages.value,
    ...commonPages.value,
  ]

  const testStatus = {}
  allPages.forEach((page) => {
    testStatus[page.path] = page.tested
  })

  localStorage.setItem('pageSquare_testStatus', JSON.stringify(testStatus))
}

const authPages = ref([
  {
    path: '/login',
    title: '用户登录',
    description: '系统登录入口，支持学员、教练、管理员登录',
    icon: '🔑',
    tested: false,
  },
  {
    path: '/register/student',
    title: '学员注册',
    description: '学员用户注册页面',
    icon: '🎓',
    tested: false,
  },
  {
    path: '/register/coach',
    title: '教练注册',
    description: '教练用户注册页面',
    icon: '👨‍🏫',
    tested: false,
  },
])

const adminPages = ref([
  {
    path: '/dashboard',
    title: '系统仪表板',
    description: '超级管理员系统概览和控制中心',
    icon: '📊',
    tested: false,
  },
  {
    path: '/admin/campus',
    title: '校区管理',
    description: '管理所有校区的基本信息和状态',
    icon: '🏢',
    tested: false,
  },
  {
    path: '/admin/service',
    title: '服务状态',
    description: '查看系统服务状态和性能监控',
    icon: '⚡',
    tested: false,
  },
])

const campusPages = ref([
  {
    path: '/campus/students',
    title: '学员管理',
    description: '管理校区内的学员信息和状态',
    icon: '👥',
    tested: false,
  },
  {
    path: '/campus/coaches',
    title: '教练管理',
    description: '管理校区内的教练信息和审核',
    icon: '👨‍🏫',
    tested: false,
  },
  {
    path: '/campus/appointments',
    title: '预约管理',
    description: '管理课程预约和异常处理',
    icon: '📅',
    tested: false,
  },
  {
    path: '/campus/logs',
    title: '系统日志',
    description: '查看校区相关的系统操作日志',
    icon: '📋',
    tested: false,
  },
])

const studentPages = ref([
  {
    path: '/student/dashboard',
    title: '学员仪表板',
    description: '学员个人中心和学习概览',
    icon: '🎯',
    tested: false,
  },
  {
    path: '/student/find-coach',
    title: '寻找教练',
    description: '浏览和搜索教练信息',
    icon: '🔍',
    tested: false,
  },
  {
    path: '/student/my-coaches',
    title: '我的教练',
    description: '管理已关注的教练列表',
    icon: '⭐',
    tested: false,
  },
  {
    path: '/student/book-training',
    title: '课程预约',
    description: '预约训练课程',
    icon: '📝',
    tested: false,
  },
  {
    path: '/student/schedule',
    title: '我的课表',
    description: '查看个人课程安排',
    icon: '📆',
    tested: false,
  },
  {
    path: '/student/account-recharge',
    title: '账户充值',
    description: '学员账户余额充值',
    icon: '💰',
    tested: false,
  },
  {
    path: '/student/tournament-registration',
    title: '比赛报名',
    description: '参与乒乓球比赛报名',
    icon: '🏆',
    tested: false,
  },
  {
    path: '/student/matches',
    title: '我的比赛',
    description: '查看个人比赛记录',
    icon: '🏅',
    tested: false,
  },
  {
    path: '/student/evaluation',
    title: '训练评价',
    description: '对教练和课程进行评价',
    icon: '⭐',
    tested: false,
  },
])

const coachPages = ref([
  {
    path: '/coach/dashboard',
    title: '教练仪表板',
    description: '教练个人中心和教学概览',
    icon: '📈',
    tested: false,
  },
  {
    path: '/coach/appointment-approval',
    title: '预约审核',
    description: '处理学员的课程预约申请',
    icon: '✅',
    tested: false,
  },
  {
    path: '/coach/schedule',
    title: '课程安排',
    description: '管理个人教学时间表',
    icon: '📅',
    tested: false,
  },
  {
    path: '/coach/student-feedback',
    title: '学员反馈',
    description: '查看学员评价和反馈',
    icon: '💬',
    tested: false,
  },
])

const commonPages = ref([
  {
    path: '/profile',
    title: '个人资料',
    description: '编辑个人信息和设置',
    icon: '👤',
    tested: false,
  },
  {
    path: '/messages',
    title: '消息中心',
    description: '查看系统通知和消息',
    icon: '📨',
    tested: false,
  },
  {
    path: '/debug',
    title: '权限系统调试',
    description: '权限系统状态诊断和调试工具',
    icon: '🔧',
    tested: false,
  },
  {
    path: '/page-square',
    title: '开发测试控制台',
    description: '测试导航中心（当前页面）',
    icon: '🎯',
    tested: true,
  },
])

// 初始化测试状态
const initializeTestStatus = () => {
  const testStatus = getTestStatusFromStorage()
  const allPages = [
    ...authPages.value,
    ...adminPages.value,
    ...campusPages.value,
    ...studentPages.value,
    ...coachPages.value,
    ...commonPages.value,
  ]

  allPages.forEach((page) => {
    if (Object.prototype.hasOwnProperty.call(testStatus, page.path)) {
      page.tested = testStatus[page.path]
    } else if (page.path === '/page-square') {
      page.tested = true
    }
  })
}

const totalPages = computed(() => {
  return (
    authPages.value.length +
    adminPages.value.length +
    campusPages.value.length +
    studentPages.value.length +
    coachPages.value.length +
    commonPages.value.length
  )
})

const testedPages = computed(() => {
  const allPages = [
    ...authPages.value,
    ...adminPages.value,
    ...campusPages.value,
    ...studentPages.value,
    ...coachPages.value,
    ...commonPages.value,
  ]
  return allPages.filter((page) => page.tested).length
})

const passRate = computed(() => {
  return totalPages.value > 0 ? Math.round((testedPages.value / totalPages.value) * 100) : 0
})

const navigateToPage = (page) => {
  try {
    // 标记为已测试
    page.tested = true
    // 保存状态到 localStorage
    saveTestStatusToStorage()

    // 在新标签页中打开页面
    const url = window.location.origin + page.path
    window.open(url, '_blank')

    ElMessage.success(`已在新标签页打开: ${page.title}`)
  } catch (error) {
    ElMessage.error(`打开失败: ${error.message}`)
  }
}

const markAllTested = () => {
  const allPages = [
    ...authPages.value,
    ...adminPages.value,
    ...campusPages.value,
    ...studentPages.value,
    ...coachPages.value,
    ...commonPages.value,
  ]

  allPages.forEach((page) => {
    page.tested = true
  })

  // 保存状态到 localStorage
  saveTestStatusToStorage()

  ElMessage.success('已标记所有页面为已测试')
}

const resetTestStatus = () => {
  ElMessageBox.confirm('确定要重置所有页面的测试状态吗？', '确认重置', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    const allPages = [
      ...authPages.value,
      ...adminPages.value,
      ...campusPages.value,
      ...studentPages.value,
      ...coachPages.value,
      ...commonPages.value,
    ]

    allPages.forEach((page) => {
      if (page.path !== '/page-square') {
        page.tested = false
      }
    })

    // 保存状态到 localStorage
    saveTestStatusToStorage()

    ElMessage.success('测试状态已重置')
  })
}

const exportTestReport = () => {
  const allPages = [
    ...authPages.value,
    ...adminPages.value,
    ...campusPages.value,
    ...studentPages.value,
    ...coachPages.value,
    ...commonPages.value,
  ]

  const report = {
    timestamp: new Date().toISOString(),
    totalPages: totalPages.value,
    testedPages: testedPages.value,
    passRate: passRate.value,
    pages: allPages.map((page) => ({
      path: page.path,
      title: page.title,
      tested: page.tested,
    })),
  }

  const blob = new Blob([JSON.stringify(report, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `page-test-report-${new Date().toISOString().split('T')[0]}.json`
  a.click()
  URL.revokeObjectURL(url)

  ElMessage.success('测试报告已导出')
}

const openAllPages = () => {
  ElMessageBox.confirm(
    '这将在新标签页中打开所有页面，可能会影响浏览器性能。确定继续吗？',
    '批量打开页面',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    },
  ).then(() => {
    const allPages = [
      ...authPages.value,
      ...adminPages.value,
      ...campusPages.value,
      ...studentPages.value,
      ...coachPages.value,
      ...commonPages.value,
    ]

    // 标记所有页面为已测试
    allPages.forEach((page) => {
      page.tested = true
    })

    // 保存状态到 localStorage
    saveTestStatusToStorage()

    // 批量打开页面（在新标签页中）
    allPages.forEach((page, index) => {
      setTimeout(() => {
        const url = window.location.origin + page.path
        window.open(url, '_blank')
      }, index * 500)
    })

    ElMessage.success('正在批量打开页面...')
  })
}

// 组件挂载时初始化测试状态
onMounted(() => {
  initializeTestStatus()
  // 初始化角色状态
  const savedRole = localStorage.getItem('testRole') || 'guest'
  switchRole(savedRole)
})
</script>

<style scoped>
.page-square {
  background: linear-gradient(
    135deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #f5576c 75%,
    #4facfe 100%
  );
  min-height: 100vh;
  padding: 24px;
  position: relative;
  overflow: hidden;
}

.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.test-icon {
  position: absolute;
  font-size: 55px;
  opacity: 0.08;
  animation: float 9s ease-in-out infinite;
}

.code-icon {
  top: 20%;
  left: 8%;
  animation-delay: 0s;
}

.test-tube-icon {
  top: 70%;
  right: 10%;
  animation-delay: 2s;
}

.check-icon {
  bottom: 25%;
  left: 12%;
  animation-delay: 4s;
}

.rocket-icon {
  top: 40%;
  left: 5%;
  animation-delay: 6s;
}

.navigation-decoration {
  position: absolute;
  font-size: 60px;
  opacity: 0.06;
  animation: rotate 20s linear infinite;
}

.map-icon {
  top: 15%;
  right: 15%;
  animation-delay: 1s;
}

.compass-icon {
  bottom: 30%;
  right: 8%;
  animation-delay: 3s;
}

.target-icon {
  top: 60%;
  left: 15%;
  animation-delay: 5s;
}

.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.12;
  z-index: 1;
  pointer-events: none;
  user-select: none;
}

.art-text-main {
  font-size: 120px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 15px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.3);
  font-family: 'Arial Black', sans-serif;
  -webkit-text-stroke: 1px rgba(255, 255, 255, 0.2);
}

.art-text-sub {
  font-size: 45px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 8px;
  margin-top: -20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.geometric-decoration {
  position: absolute;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.geo-1 {
  top: 25%;
  left: 3%;
  width: 90px;
  height: 90px;
  animation: pulse 6s ease-in-out infinite;
}

.geo-2 {
  bottom: 40%;
  right: 5%;
  width: 70px;
  height: 70px;
  animation: pulse 8s ease-in-out infinite;
  animation-delay: 2s;
}

.geo-3 {
  top: 55%;
  right: 12%;
  width: 50px;
  height: 50px;
  animation: pulse 10s ease-in-out infinite;
  animation-delay: 4s;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 10;
}

/* 角色控制台样式 */
.role-console {
  margin-bottom: 30px;
  border: none;
  border-radius: 16px;
  backdrop-filter: blur(15px);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(138, 43, 226, 0.08));
  box-shadow: 0 10px 35px rgba(138, 43, 226, 0.15);
  transition: all 0.3s ease;
}

.role-console:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 45px rgba(138, 43, 226, 0.2);
}

.console-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 18px;
  color: #333;
}

.console-header .icon {
  font-size: 24px;
}

.role-controls {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.current-role {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8), rgba(138, 43, 226, 0.05));
  border-radius: 12px;
  border: 1px solid rgba(138, 43, 226, 0.1);
}

.role-avatar {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  box-shadow: 0 4px 15px rgba(138, 43, 226, 0.3);
}

.role-info {
  flex: 1;
}

.role-name {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.role-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.role-switcher {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
}

.role-switcher .el-button-group {
  flex-wrap: wrap;
}

.role-switcher .el-button {
  margin: 2px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.role-switcher .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.role-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.role-actions .el-button {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.role-actions .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.page-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.stats {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.stat-item {
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.section-card {
  margin-bottom: 30px;
  border: none;
  border-radius: 16px;
  backdrop-filter: blur(15px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.section-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.18);
}

.auth-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(102, 126, 234, 0.1));
}

.admin-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 87, 108, 0.1));
}

.campus-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(245, 166, 35, 0.1));
}

.student-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(79, 172, 254, 0.1));
}

.coach-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(82, 196, 26, 0.1));
}

.common-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(118, 75, 162, 0.1));
}

.tools-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(240, 147, 251, 0.1));
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 18px;
  color: #333;
}

.section-header .icon {
  font-size: 24px;
}

.page-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.page-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-card:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.15);
}

.page-icon {
  font-size: 32px;
  min-width: 40px;
}

.page-info {
  flex: 1;
  min-width: 0;
}

.page-title {
  font-weight: 600;
  font-size: 16px;
  color: #333;
  margin-bottom: 4px;
}

.page-description {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 6px;
}

.page-path {
  font-size: 12px;
  color: #999;
  font-family: 'Courier New', monospace;
  background: rgba(0, 0, 0, 0.05);
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
}

.page-status {
  min-width: 60px;
}

.tools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-12px) rotate(2deg);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.06;
    transform: scale(1);
  }

  50% {
    opacity: 0.12;
    transform: scale(1.1);
  }
}

@media (max-width: 768px) {
  .page-square {
    padding: 16px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .stats {
    flex-direction: column;
    gap: 10px;
  }

  .page-grid {
    grid-template-columns: 1fr;
  }

  .tools-grid {
    grid-template-columns: 1fr;
  }

  .art-text-main {
    font-size: 80px;
  }

  .art-text-sub {
    font-size: 30px;
  }

  /* 角色控制台响应式 */
  .current-role {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .role-avatar {
    width: 60px;
    height: 60px;
    font-size: 36px;
  }

  .role-switcher {
    justify-content: center;
  }

  .role-switcher .el-button-group {
    justify-content: center;
  }

  .role-switcher .el-button {
    font-size: 12px;
    padding: 6px 12px;
  }

  .role-actions {
    flex-direction: column;
    align-items: center;
  }

  .role-actions .el-button {
    width: 100%;
    max-width: 200px;
  }
}
</style>
