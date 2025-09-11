<template>
  <div class="dev-tools">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <span class="header-icon">🛠️</span>
          <div class="header-text">
            <h1>开发工具</h1>
            <p>系统开发和调试工具集合</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 权限调试工具 -->
      <div v-if="currentTool === 'debug'" class="tool-panel">
        <div class="panel-header">
          <h2>🔧 权限调试工具</h2>
          <p>系统权限和认证状态诊断</p>
        </div>

        <div class="debug-cards">
          <!-- 用户状态卡片 -->
          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">👤</span>
                <span>用户状态</span>
              </div>
            </template>
            <div class="status-grid">
              <div class="status-item">
                <label>登录状态:</label>
                <el-tag :type="isLoggedIn ? 'success' : 'danger'" size="small">
                  {{ isLoggedIn ? '已登录' : '未登录' }}
                </el-tag>
              </div>
              <div class="status-item">
                <label>用户角色:</label>
                <el-tag type="primary" size="small">{{ userRole || '游客' }}</el-tag>
              </div>
              <div class="status-item">
                <label>用户名:</label>
                <span>{{ userName || '无' }}</span>
              </div>
              <div class="status-item">
                <label>用户ID:</label>
                <span>{{ userId || '无' }}</span>
              </div>
              <div class="status-item">
                <label>校区ID:</label>
                <span>{{ campusId || '无' }}</span>
              </div>
            </div>
          </el-card>

          <!-- 本地存储卡片 -->
          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">💾</span>
                <span>本地存储</span>
              </div>
            </template>
            <div class="storage-info">
              <div class="storage-item">
                <label>Token存储:</label>
                <el-tag :type="token ? 'success' : 'info'" size="small">
                  {{ token ? '已设置' : '未设置' }}
                </el-tag>
              </div>
              <div class="storage-item">
                <label>Token值:</label>
                <el-input
                  v-model="token"
                  size="small"
                  placeholder="Token内容"
                  type="textarea"
                  :rows="2"
                  readonly
                />
              </div>
              <div class="storage-item">
                <label>用户信息:</label>
                <el-input
                  v-model="userInfoDisplay"
                  size="small"
                  placeholder="用户信息JSON"
                  type="textarea"
                  :rows="4"
                  readonly
                />
              </div>
            </div>
          </el-card>

          <!-- 权限测试卡片 -->
          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">🔐</span>
                <span>权限测试</span>
              </div>
            </template>
            <div class="permission-tests">
              <div class="test-item">
                <label>超级管理员权限:</label>
                <el-tag :type="isSuperAdmin ? 'success' : 'info'" size="small">
                  {{ isSuperAdmin ? '有权限' : '无权限' }}
                </el-tag>
              </div>
              <div class="test-item">
                <label>校区管理员权限:</label>
                <el-tag :type="isCampusAdmin ? 'success' : 'info'" size="small">
                  {{ isCampusAdmin ? '有权限' : '无权限' }}
                </el-tag>
              </div>
              <div class="test-item">
                <label>学员权限:</label>
                <el-tag :type="isStudent ? 'success' : 'info'" size="small">
                  {{ isStudent ? '有权限' : '无权限' }}
                </el-tag>
              </div>
              <div class="test-item">
                <label>教练权限:</label>
                <el-tag :type="isCoach ? 'success' : 'info'" size="small">
                  {{ isCoach ? '有权限' : '无权限' }}
                </el-tag>
              </div>
            </div>
          </el-card>

          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">⚡</span>
                <span>操作工具</span>
              </div>
            </template>
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon>
                刷新数据
              </el-button>
                            <el-button type="warning" size="small" @click="resetTestStatus">
                <el-icon><Refresh /></el-icon>
                重置全部
              </el-button>
              <el-button type="info" size="small" @click="exportTestReport">
                <el-icon><Download /></el-icon>
                导出结果
              </el-button>
              <el-button type="primary" size="small" @click="openAllPages">
                <el-icon><FolderOpened /></el-icon>
                批量打开
              </el-button>
            </div>
          </el-card>

          <!-- 环境信息卡片 -->
          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">💻</span>
                <span>环境信息</span>
              </div>
            </template>
            <div class="status-grid">
              <div class="status-item">
                <label>浏览器:</label>
                <el-tag type="info" size="small">{{ browserInfo }}</el-tag>
              </div>
              <div class="status-item">
                <label>视口大小:</label>
                <span>{{ viewportSize }}</span>
              </div>
              <div class="status-item">
                <label>在线状态:</label>
                <el-tag :type="isOnline ? 'success' : 'danger'" size="small">
                  {{ isOnline ? '在线' : '离线' }}
                </el-tag>
              </div>
              <div class="status-item">
                <label>Cookie启用:</label>
                <el-tag :type="cookieEnabled ? 'success' : 'danger'" size="small">
                  {{ cookieEnabled ? '启用' : '禁用' }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 角色测试工具 -->
      <div v-else-if="currentTool === 'roles'" class="tool-panel">
        <div class="panel-header">
          <h2>🎭 角色测试工具</h2>
          <p>用户角色切换和权限测试</p>
        </div>

        <div class="role-tester">
          <el-card class="role-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">🎭</span>
                <span>角色控制台</span>
                <el-tag :type="currentRoleTag.type" size="small">
                  {{ currentRoleTag.label }}
                </el-tag>
              </div>
            </template>

            <div class="current-role-display">
              <div class="role-avatar">{{ currentRoleInfo.icon }}</div>
              <div class="role-details">
                <h3>{{ currentRoleInfo.name }}</h3>
                <p>{{ currentRoleInfo.description }}</p>
                <el-alert
                  title="注意"
                  description="角色切换仅影响测试工具，不会改变您的实际登录状态"
                  type="warning"
                  :closable="false"
                  show-icon
                  size="small"
                />
              </div>
            </div>

            <div class="role-switcher">
              <h4>切换测试角色</h4>
              <div class="role-buttons">
                <el-button
                  v-for="role in availableRoles"
                  :key="role.value"
                  :type="currentRole === role.value ? 'primary' : 'default'"
                  :disabled="currentRole === role.value"
                  size="small"
                  class="role-btn"
                  @click="switchToTestRole(role.value)"
                >
                  {{ role.icon }} {{ role.label }}
                </el-button>
              </div>
            </div>

            <div class="role-actions">
              <el-button type="success" size="small" @click="testCurrentRole">
                <el-icon><View /></el-icon>
                测试当前角色
              </el-button>
              <el-button type="warning" size="small" @click="clearRoleData">
                <el-icon><Delete /></el-icon>
                重置测试状态
              </el-button>
              <el-button type="info" size="small" @click="showRolePermissions">
                <el-icon><InfoFilled /></el-icon>
                权限说明
              </el-button>
              <el-button type="primary" size="small" @click="resetToActualRole">
                <el-icon><Refresh /></el-icon>
                恢复实际角色
              </el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 页面测试工具 -->
      <div v-else-if="currentTool === 'pages'" class="tool-panel">
        <div class="panel-header">
          <h2>📄 页面测试工具</h2>
          <p>系统页面访问和功能测试</p>
        </div>

        <!-- 测试统计和管理 -->
        <el-card class="test-management-card" shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div class="card-header">
              <span class="card-icon">📊</span>
              <span>测试管理</span>
            </div>
          </template>
          <div class="test-stats">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-number">{{ totalPages }}</div>
                <div class="stat-label">总页面数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ testedPages }}</div>
                <div class="stat-label">已测试</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ passRate }}%</div>
                <div class="stat-label">完成率</div>
              </div>
            </div>
            <div class="test-actions">
              <el-button type="success" size="small" @click="markAllTested">
                <el-icon><Check /></el-icon>
                全部标记已测试
              </el-button>
              <el-button type="warning" size="small" @click="resetTestStatus">
                <el-icon><Refresh /></el-icon>
                重置测试状态
              </el-button>
              <el-button type="primary" size="small" @click="exportTestReport">
                <el-icon><Download /></el-icon>
                导出测试报告
              </el-button>
              <el-button type="info" size="small" @click="openAllPages">
                <el-icon><FolderOpened /></el-icon>
                批量打开页面
              </el-button>
            </div>
          </div>
        </el-card>

        <div class="page-categories">
          <div v-for="category in pageCategories" :key="category.name" class="category-section">
            <h3 class="category-title">
              <span class="category-icon">{{ category.icon }}</span>
              {{ category.name }}
              <el-tag :type="category.tagType" size="small">{{ category.pages.length }}个页面</el-tag>
            </h3>
            <div class="page-grid">
              <div
                v-for="page in category.pages"
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
          </div>
        </div>
      </div>

      <!-- 默认首页 -->
      <div v-else class="tool-panel">
        <div class="panel-header">
          <h2>🏠 开发工具首页</h2>
          <p>欢迎使用开发工具，请从左侧菜单选择工具</p>
        </div>

        <div class="welcome-content">
          <el-card class="welcome-card" shadow="hover">
            <div class="welcome-info">
              <span class="welcome-icon">🛠️</span>
              <h3>开发工具集合</h3>
              <p>为开发和测试提供便捷的工具集合</p>
              <el-divider />
              <div class="tool-list">
                <div class="tool-item">
                  <span>🔧</span>
                  <span>权限调试：用户状态和权限诊断</span>
                </div>
                <div class="tool-item">
                  <span>🎭</span>
                  <span>角色测试：用户角色切换和测试</span>
                </div>
                <div class="tool-item">
                  <span>📄</span>
                  <span>页面测试：系统页面访问测试</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Refresh,
  Delete,
  InfoFilled,
  View,
  Check,
  Download,
  FolderOpened,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

// 当前工具（通过URL参数控制）
const currentTool = computed(() => route.query.tool || 'home')

// 测试页面统计
const testPages = [
  { name: 'Home', route: '/', tested: true },
  { name: 'Profile', route: '/profile', tested: true },
  { name: 'Messages', route: '/messages', tested: false },
  { name: 'Student Dashboard', route: '/student/dashboard', tested: true },
  { name: 'Coach Dashboard', route: '/coach/dashboard', tested: false },
  { name: 'Campus Admin Dashboard', route: '/campus-admin/dashboard', tested: false },
  { name: 'Super Admin Dashboard', route: '/super-admin/dashboard', tested: true },
  { name: 'Login', route: '/auth/login', tested: true },
  { name: 'Student Register', route: '/auth/student-register', tested: false },
  { name: 'Coach Register', route: '/auth/coach-register', tested: false }
]

const totalPages = computed(() => testPages.length)
const testedPages = computed(() => testPages.filter(page => page.tested).length)
const passRate = computed(() => {
  return totalPages.value > 0 ? Math.round((testedPages.value / totalPages.value) * 100) : 0
})

// 环境信息
const browserInfo = computed(() => {
  const userAgent = navigator.userAgent
  let browser = 'Unknown'
  
  if (userAgent.includes('Chrome')) browser = 'Chrome'
  else if (userAgent.includes('Firefox')) browser = 'Firefox'
  else if (userAgent.includes('Safari')) browser = 'Safari'
  else if (userAgent.includes('Edge')) browser = 'Edge'
  
  return `${browser} ${navigator.appVersion.split(' ')[0]}`
})

const viewportSize = computed(() => {
  return `${window.innerWidth} × ${window.innerHeight}`
})

const isOnline = computed(() => navigator.onLine)

const cookieEnabled = computed(() => navigator.cookieEnabled)

// 用户状态
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userRole = computed(() => userStore.userRole)
const userName = computed(() => userStore.userName)
const userId = computed(() => userStore.userId)
const campusId = computed(() => userStore.campusId)
const isSuperAdmin = computed(() => userStore.isSuperAdmin)
const isCampusAdmin = computed(() => userStore.isCampusAdmin)
const isStudent = computed(() => userStore.isStudent)
const isCoach = computed(() => userStore.isCoach)

// 存储数据
const token = computed(() => userStore.token)
const userInfoDisplay = computed(() => 
  JSON.stringify(userStore.userInfo, null, 2)
)

// 角色测试相关
const currentRole = ref('guest')
const actualRole = ref('guest')
const actualUserInfo = ref({})
const actualToken = ref('')

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
    },
    student: {
      name: '学员用户',
      description: '已注册的学员，可以预约课程、查看教练',
      icon: '🎓',
    },
    coach: {
      name: '教练用户',
      description: '认证教练，可以管理课程、查看学员反馈',
      icon: '👨‍🏫',
    },
    campus_admin: {
      name: '校区管理员',
      description: '校区级管理员，管理本校区的学员和教练',
      icon: '🏢',
    },
    super_admin: {
      name: '超级管理员',
      description: '系统最高权限，管理所有校区和系统设置',
      icon: '👑',
    },
  }
  return roleInfoMap[currentRole.value] || roleInfoMap.guest
})

// 当前角色标签样式
const currentRoleTag = computed(() => {
  const tagMap = {
    guest: { type: 'info', label: '游客模式' },
    student: { type: 'primary', label: '学员模式' },
    coach: { type: 'success', label: '教练模式' },
    campus_admin: { type: 'warning', label: '校区管理员模式' },
    super_admin: { type: 'danger', label: '超级管理员模式' },
  }
  return tagMap[currentRole.value] || tagMap.guest
})

// 页面分类数据
const pageCategories = ref([
  {
    name: '认证页面',
    icon: '🔐',
    tagType: 'info',
    pages: [
      {
        path: '/login',
        title: '用户登录',
        description: '系统登录入口',
        icon: '🔑',
        tested: false,
      },
      {
        path: '/register/student',
        title: '学员注册',
        description: '学员注册页面',
        icon: '🎓',
        tested: false,
      },
      {
        path: '/register/coach',
        title: '教练注册',
        description: '教练注册页面',
        icon: '👨‍🏫',
        tested: false,
      },
    ],
  },
  {
    name: '管理页面',
    icon: '👑',
    tagType: 'danger',
    pages: [
      {
        path: '/admin/campus',
        title: '校区管理',
        description: '管理所有校区信息',
        icon: '🏢',
        tested: false,
      },
      {
        path: '/admin/service',
        title: '服务状态',
        description: '系统服务监控',
        icon: '⚡',
        tested: false,
      },
    ],
  },
  {
    name: '学员页面',
    icon: '🎓',
    tagType: 'primary',
    pages: [
      {
        path: '/student/find-coach',
        title: '寻找教练',
        description: '浏览教练信息',
        icon: '🔍',
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
        description: '查看课程安排',
        icon: '📆',
        tested: false,
      },
    ],
  },
  {
    name: '教练页面',
    icon: '👨‍🏫',
    tagType: 'success',
    pages: [
      {
        path: '/coach/appointment-approval',
        title: '预约审核',
        description: '处理学员预约申请',
        icon: '✅',
        tested: false,
      },
      {
        path: '/coach/schedule',
        title: '课程安排',
        description: '管理教学时间表',
        icon: '📅',
        tested: false,
      },
    ],
  },
  {
    name: '通用页面',
    icon: '⚙️',
    tagType: 'default',
    pages: [
      {
        path: '/profile',
        title: '个人资料',
        description: '编辑个人信息',
        icon: '👤',
        tested: false,
      },
      {
        path: '/messages',
        title: '消息中心',
        description: '查看系统通知',
        icon: '📨',
        tested: false,
      },
      {
        path: '/dashboard',
        title: '仪表板',
        description: '系统首页',
        icon: '📊',
        tested: false,
      },
    ],
  },
])

// 切换到测试角色
const switchToTestRole = (role) => {
  currentRole.value = role
  ElMessage.success(`已切换到${currentRoleInfo.value.name}测试模式`)
}

// 恢复到实际角色
const resetToActualRole = () => {
  currentRole.value = actualRole.value
  ElMessage.success(`已恢复到实际登录状态：${currentRoleInfo.value.name}`)
}

// 初始化角色状态
const initializeRole = () => {
  if (userStore.token && userStore.userInfo && userStore.userInfo.role) {
    actualRole.value = userStore.userInfo.role
    actualUserInfo.value = { ...userStore.userInfo }
    actualToken.value = userStore.token
    currentRole.value = userStore.userInfo.role
  } else {
    actualRole.value = 'guest'
    actualUserInfo.value = {}
    actualToken.value = ''
    currentRole.value = 'guest'
  }
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

  const pages = rolePages[currentRole.value] || ['/dev-tools']

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
  ElMessageBox.confirm('这将重置测试控制台到实际登录状态，确定继续吗？', '重置测试状态', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    resetToActualRole()
    ElMessage.success('测试状态已重置到实际登录状态')
  })
}

// 显示角色权限
const showRolePermissions = () => {
  const permissions = [
    '查看登录页面',
    '查看注册页面', 
    '查看开发工具'
  ]
  ElMessageBox.alert(
    `${currentRoleInfo.value.name}的权限包括：\n\n${permissions.join('、')}`,
    '角色权限说明',
    {
      confirmButtonText: '知道了',
      type: 'info',
    },
  )
}

// 页面导航
const navigateToPage = (page) => {
  try {
    page.tested = true
    saveTestStatusToStorage()
    const url = window.location.origin + page.path
    window.open(url, '_blank')
    ElMessage.success(`已在新标签页打开: ${page.title}`)
  } catch (error) {
    ElMessage.error(`打开失败: ${error.message}`)
  }
}

// 保存测试状态
const saveTestStatusToStorage = () => {
  const allPages = pageCategories.value.flatMap(category => category.pages)
  const testStatus = {}
  allPages.forEach((page) => {
    testStatus[page.path] = page.tested
  })
  localStorage.setItem('devTools_testStatus', JSON.stringify(testStatus))
}

// 加载测试状态
const loadTestStatusFromStorage = () => {
  try {
    const stored = localStorage.getItem('devTools_testStatus')
    if (stored) {
      const testStatus = JSON.parse(stored)
      pageCategories.value.forEach(category => {
        category.pages.forEach(page => {
          if (testStatus[page.path] !== undefined) {
            page.tested = testStatus[page.path]
          }
        })
      })
    }
  } catch (error) {
    console.error('加载测试状态失败:', error)
  }
}

// 标记全部已测试
const markAllTested = () => {
  pageCategories.value.forEach(category => {
    category.pages.forEach(page => {
      page.tested = true
    })
  })
  saveTestStatusToStorage()
  ElMessage.success('已标记所有页面为已测试')
}

// 重置测试状态
const resetTestStatus = () => {
  ElMessageBox.confirm('确定要重置所有页面的测试状态吗？', '确认重置', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    pageCategories.value.forEach(category => {
      category.pages.forEach(page => {
        page.tested = false
      })
    })
    saveTestStatusToStorage()
    ElMessage.success('测试状态已重置')
  })
}

// 导出测试报告
const exportTestReport = () => {
  const allPages = pageCategories.value.flatMap(category => category.pages)
  const totalPages = allPages.length
  const testedPages = allPages.filter(page => page.tested).length
  const passRate = totalPages > 0 ? Math.round((testedPages / totalPages) * 100) : 0

  const report = {
    timestamp: new Date().toISOString(),
    totalPages,
    testedPages,
    passRate,
    categories: pageCategories.value.map(category => ({
      name: category.name,
      pages: category.pages.map(page => ({
        path: page.path,
        title: page.title,
        tested: page.tested,
      })),
    })),
  }

  const blob = new Blob([JSON.stringify(report, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `dev-tools-test-report-${new Date().toISOString().split('T')[0]}.json`
  a.click()
  URL.revokeObjectURL(url)

  ElMessage.success('测试报告已导出')
}

// 批量打开页面
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
    const allPages = pageCategories.value.flatMap(category => category.pages)

    allPages.forEach((page) => {
      page.tested = true
    })
    saveTestStatusToStorage()

    allPages.forEach((page, index) => {
      setTimeout(() => {
        const url = window.location.origin + page.path
        window.open(url, '_blank')
      }, index * 500)
    })

    ElMessage.success('正在批量打开页面...')
  })
}

// 刷新数据
const refreshData = () => {
  initializeRole()
  loadTestStatusFromStorage()
  ElMessage.success('数据已刷新')
}

// 组件挂载时初始化
onMounted(() => {
  initializeRole()
  loadTestStatusFromStorage()
})

// 监听路由变化
watch(() => route.query.tool, () => {
  // 路由变化时可以做一些初始化工作
}, { immediate: true })
onMounted(() => {
  initializeRole()
  loadTestStatusFromStorage()
})

// 监听路由变化
watch(() => route.query.tool, () => {
  // 路由变化时可以做一些初始化工作
}, { immediate: true })
</script>

<style scoped>
.dev-tools {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.page-header {
  background: white;
  border-bottom: 1px solid #e1e6ea;
  padding: 20px 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  font-size: 48px;
}

.header-text h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
  font-weight: 700;
}

.header-text p {
  margin: 4px 0 0 0;
  color: #666;
  font-size: 14px;
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px;
}

.tool-panel {
  max-width: 100%;
}

.panel-header {
  margin-bottom: 30px;
}

.panel-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #333;
}

.panel-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 调试卡片样式 */
.debug-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.debug-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
}

.card-icon {
  font-size: 18px;
}

.status-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.status-item label {
  font-weight: 500;
  color: #666;
}

.storage-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.storage-item label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #666;
}

.permission-tests {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.test-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.test-item label {
  font-weight: 500;
  color: #666;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 角色测试样式 */
.role-tester {
  max-width: 800px;
}

.role-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.current-role-display {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
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
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.role-details h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #333;
}

.role-details p {
  margin: 0 0 12px 0;
  color: #666;
  line-height: 1.4;
}

.role-switcher h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.role-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.role-btn {
  border-radius: 8px;
}

.role-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 页面测试样式 */
.page-categories {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.category-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.category-icon {
  font-size: 20px;
}

.page-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.page-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
}

.page-card:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.page-icon {
  font-size: 24px;
  min-width: 24px;
}

.page-info {
  flex: 1;
  min-width: 0;
}

.page-title {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.page-description {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.page-path {
  font-size: 11px;
  color: #999;
  font-family: 'Courier New', monospace;
  background: rgba(0, 0, 0, 0.05);
  padding: 2px 4px;
  border-radius: 3px;
  display: inline-block;
}

/* 系统工具样式 */
.toolkit-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.toolkit-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.toolkit-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.toolkit-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.toolkit-icon {
  font-size: 32px;
  min-width: 32px;
}

.toolkit-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.toolkit-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

/* 欢迎页面样式 */
.welcome-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.welcome-card {
  max-width: 600px;
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-info {
  text-align: center;
  padding: 40px;
}

.welcome-icon {
  font-size: 64px;
  margin-bottom: 20px;
  display: block;
}

.welcome-info h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  color: #333;
}

.welcome-info p {
  margin: 0 0 20px 0;
  color: #666;
  font-size: 16px;
}

.tool-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  text-align: left;
}

.tool-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  color: #666;
}

.tool-item span:first-child {
  font-size: 20px;
  min-width: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .debug-cards,
  .page-grid,
  .toolkit-grid {
    grid-template-columns: 1fr;
  }

  .current-role-display {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .role-avatar {
    width: 60px;
    height: 60px;
    font-size: 36px;
  }

  .role-buttons {
    justify-content: center;
  }

  .role-actions {
    flex-direction: column;
    align-items: center;
  }

  .role-actions .el-button {
    width: 100%;
    max-width: 200px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
