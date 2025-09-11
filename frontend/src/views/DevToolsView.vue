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
                  :rows="3"
                  readonly
                  class="enhanced-textarea"
                />
              </div>
              <div class="storage-item">
                <label>用户信息:</label>
                <el-input
                  v-model="userInfoDisplay"
                  size="small"
                  placeholder="用户信息JSON"
                  type="textarea"
                  :rows="6"
                  readonly
                  class="enhanced-textarea"
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
              <el-button type="default" size="small" @click="debugTestStatus">
                <el-icon><InfoFilled /></el-icon>
                调试状态
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
                @contextmenu.prevent="showPageMenu(page)"
              >
                <div class="page-icon">{{ page.icon }}</div>
                <div class="page-info">
                  <div class="page-title">{{ page.title }}</div>
                  <div class="page-description">{{ page.description }}</div>
                  <div class="page-path">{{ page.path }}</div>
                </div>
                <div class="page-actions">
                  <div class="page-status">
                    <el-tag :type="page.tested ? 'success' : 'warning'" size="small">
                      {{ page.tested ? '已测试' : '未测试' }}
                    </el-tag>
                  </div>
                  <div class="page-buttons">
                    <el-button 
                      v-if="!page.tested"
                      type="success" 
                      size="small" 
                      @click.stop="markPageTested(page)"
                    >
                      <el-icon><Check /></el-icon>
                    </el-button>
                    <el-button 
                      v-if="page.tested"
                      type="warning" 
                      size="small" 
                      @click.stop="resetPageStatus(page)"
                    >
                      <el-icon><Refresh /></el-icon>
                    </el-button>
                    <el-button 
                      type="primary" 
                      size="small" 
                      @click.stop="navigateToPage(page)"
                    >
                      <el-icon><View /></el-icon>
                    </el-button>
                  </div>
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
// 计算统计数据 - 基于 pageCategories
const totalPages = computed(() => {
  return pageCategories.value.flatMap(category => category.pages).length
})

const testedPages = computed(() => {
  return pageCategories.value.flatMap(category => category.pages).filter(page => page.tested).length
})

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
// 存储测试状态到 localStorage
const getTestStatus = (path) => {
  const testStatus = JSON.parse(localStorage.getItem('pageTestStatus') || '{}')
  return testStatus[path] || false
}

const setTestStatus = (path, tested) => {
  const testStatus = JSON.parse(localStorage.getItem('pageTestStatus') || '{}')
  testStatus[path] = tested
  localStorage.setItem('pageTestStatus', JSON.stringify(testStatus))
}

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
        tested: getTestStatus('/login'),
      },
      {
        path: '/register/student',
        title: '学员注册',
        description: '学员注册页面',
        icon: '🎓',
        tested: getTestStatus('/register/student'),
      },
      {
        path: '/register/coach',
        title: '教练注册',
        description: '教练注册页面',
        icon: '👨‍🏫',
        tested: getTestStatus('/register/coach'),
      },
    ],
  },
  {
    name: '超级管理员页面',
    icon: '👑',
    tagType: 'danger',
    pages: [
      {
        path: '/admin/campus',
        title: '校区管理',
        description: '管理所有校区信息',
        icon: '🏢',
        tested: getTestStatus('/admin/campus'),
      },
      {
        path: '/admin/service',
        title: '服务状态',
        description: '系统服务监控',
        icon: '⚡',
        tested: getTestStatus('/admin/service'),
      },
    ],
  },
  {
    name: '校区管理员页面',
    icon: '🛠️',
    tagType: 'warning',
    pages: [
      {
        path: '/campus/students',
        title: '学员管理',
        description: '管理校区学员信息',
        icon: '👥',
        tested: getTestStatus('/campus/students'),
      },
      {
        path: '/campus/coaches',
        title: '教练管理',
        description: '管理校区教练信息',
        icon: '👨‍🏫',
        tested: getTestStatus('/campus/coaches'),
      },
      {
        path: '/campus/appointments',
        title: '预约管理',
        description: '管理课程预约',
        icon: '📅',
        tested: getTestStatus('/campus/appointments'),
      },
      {
        path: '/campus/logs',
        title: '系统日志',
        description: '查看系统操作日志',
        icon: '📋',
        tested: getTestStatus('/campus/logs'),
      },
    ],
  },
  {
    name: '学员页面',
    icon: '🎓',
    tagType: 'primary',
    pages: [
      {
        path: '/student/dashboard',
        title: '学员仪表盘',
        description: '学员专用仪表盘',
        icon: '📊',
        tested: getTestStatus('/student/dashboard'),
      },
      {
        path: '/student/find-coach',
        title: '寻找教练',
        description: '浏览教练信息',
        icon: '🔍',
        tested: getTestStatus('/student/find-coach'),
      },
      {
        path: '/student/my-coaches',
        title: '我的教练',
        description: '管理我的教练',
        icon: '👨‍🏫',
        tested: getTestStatus('/student/my-coaches'),
      },
      {
        path: '/student/book-training',
        title: '课程预约',
        description: '预约训练课程',
        icon: '📝',
        tested: getTestStatus('/student/book-training'),
      },
      {
        path: '/student/schedule',
        title: '我的课表',
        description: '查看课程安排',
        icon: '📆',
        tested: getTestStatus('/student/schedule'),
      },
      {
        path: '/student/account-recharge',
        title: '账户充值',
        description: '充值账户余额',
        icon: '💰',
        tested: getTestStatus('/student/account-recharge'),
      },
      {
        path: '/student/tournament-registration',
        title: '比赛报名',
        description: '参加乒乓球比赛',
        icon: '🏆',
        tested: getTestStatus('/student/tournament-registration'),
      },
      {
        path: '/student/matches',
        title: '我的比赛',
        description: '查看比赛记录',
        icon: '🥇',
        tested: getTestStatus('/student/matches'),
      },
      {
        path: '/student/evaluation',
        title: '训练评价',
        description: '对训练课程评价',
        icon: '⭐',
        tested: getTestStatus('/student/evaluation'),
      },
    ],
  },
  {
    name: '教练页面',
    icon: '👨‍🏫',
    tagType: 'success',
    pages: [
      {
        path: '/coach/dashboard',
        title: '教练仪表盘',
        description: '教练专用仪表盘',
        icon: '📊',
        tested: getTestStatus('/coach/dashboard'),
      },
      {
        path: '/coach/appointment-approval',
        title: '预约审核',
        description: '处理学员预约申请',
        icon: '✅',
        tested: getTestStatus('/coach/appointment-approval'),
      },
      {
        path: '/coach/schedule',
        title: '课程安排',
        description: '管理教学时间表',
        icon: '📅',
        tested: getTestStatus('/coach/schedule'),
      },
      {
        path: '/coach/student-feedback',
        title: '学员反馈',
        description: '查看学员评价反馈',
        icon: '💬',
        tested: getTestStatus('/coach/student-feedback'),
      },
    ],
  },
  {
    name: '通用页面',
    icon: '🛠️',
    tagType: 'default',
    pages: [
      {
        path: '/dashboard',
        title: '仪表板',
        description: '系统首页',
        icon: '📊',
        tested: getTestStatus('/dashboard'),
      },
      {
        path: '/profile',
        title: '个人资料',
        description: '编辑个人信息',
        icon: '👤',
        tested: getTestStatus('/profile'),
      },
      {
        path: '/messages',
        title: '消息中心',
        description: '查看系统通知',
        icon: '📨',
        tested: getTestStatus('/messages'),
      },
      {
        path: '/dev-tools',
        title: '开发工具',
        description: '系统开发调试工具',
        icon: '🛠️',
        tested: getTestStatus('/dev-tools'),
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
// 导航到页面并标记为已测试
const navigateToPage = (page) => {
  try {
    // 标记为已测试
    page.tested = true
    setTestStatus(page.path, true)
    
    // 打开页面
    const url = window.location.origin + page.path
    window.open(url, '_blank')
    ElMessage.success(`已在新标签页打开: ${page.title}`)
    
    // 自动刷新统计数据
    refreshTestStats()
  } catch (error) {
    ElMessage.error(`打开失败: ${error.message}`)
  }
}

// 刷新测试统计数据
// 刷新测试统计数据
const refreshTestStats = () => {
  // 强制触发响应式更新 - 深度克隆数据
  pageCategories.value = pageCategories.value.map(category => ({
    ...category,
    pages: category.pages.map(page => ({
      ...page,
      tested: getTestStatus(page.path) // 从 localStorage 重新读取最新状态
    }))
  }))
}

// 标记单个页面为已测试
const markPageTested = (page) => {
  page.tested = true
  setTestStatus(page.path, true)
  refreshTestStats()
  ElMessage.success(`已标记 ${page.title} 为已测试`)
}

// 重置单个页面测试状态
const resetPageStatus = (page) => {
  page.tested = false
  setTestStatus(page.path, false)
  refreshTestStats()
  ElMessage.success(`已重置 ${page.title} 的测试状态`)
}

// 显示页面右键菜单
const showPageMenu = (page) => {
  // 简单的右键菜单实现
  const actions = [
    page.tested ? '重置状态' : '标记已测试',
    '在新窗口打开',
    '复制路径'
  ]
  
  ElMessageBox.confirm(
    `选择对 "${page.title}" 的操作`,
    '页面操作',
    {
      distinguishCancelAndClose: true,
      confirmButtonText: actions[0],
      cancelButtonText: actions[1],
      type: 'info',
    }
  ).then(() => {
    // 主要操作：切换测试状态
    if (page.tested) {
      resetPageStatus(page)
    } else {
      markPageTested(page)
    }
  }).catch((action) => {
    if (action === 'cancel') {
      // 在新窗口打开
      navigateToPage(page)
    }
  })
}

// 调试测试状态
const debugTestStatus = () => {
  const allPages = pageCategories.value.flatMap(category => category.pages)
  const localStorage_status = JSON.parse(localStorage.getItem('pageTestStatus') || '{}')
  
  const debugInfo = allPages.map(page => ({
    path: page.path,
    title: page.title,
    currentTested: page.tested,
    localStorageTested: localStorage_status[page.path] || false,
    consistent: page.tested === (localStorage_status[page.path] || false)
  }))
  
  console.table(debugInfo)
  
  const inconsistent = debugInfo.filter(item => !item.consistent)
  if (inconsistent.length > 0) {
    ElMessage.warning(`发现 ${inconsistent.length} 个状态不一致的页面，请查看控制台`)
  } else {
    ElMessage.success(`所有页面状态一致！总计 ${allPages.length} 个页面，${allPages.filter(p => p.tested).length} 个已测试`)
  }
}

// 批量打开所有页面进行测试
const openAllPages = () => {
  const allPages = pageCategories.value.flatMap(category => category.pages)
  
  ElMessageBox.confirm(
    `将在新标签页中打开 ${allPages.length} 个页面进行批量测试，这可能会占用较多系统资源。`,
    '批量测试确认',
    {
      confirmButtonText: '开始批量测试',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    let openedCount = 0
    allPages.forEach((page, index) => {
      setTimeout(() => {
        try {
          const url = window.location.origin + page.path
          window.open(url, '_blank')
          page.tested = true
          setTestStatus(page.path, true)
          openedCount++
          
          if (openedCount === allPages.length) {
            refreshTestStats()
            ElMessage.success(`批量测试完成，已打开 ${openedCount} 个页面`)
          }
        } catch (error) {
          console.error(`打开页面失败: ${page.path}`, error)
        }
      }, index * 500) // 间隔500ms打开，避免浏览器阻止
    })
  })
}
// 标记所有页面为已测试
const markAllTested = () => {
  pageCategories.value.forEach(category => {
    category.pages.forEach(page => {
      page.tested = true
      setTestStatus(page.path, true)
    })
  })
  refreshTestStats()
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
        setTestStatus(page.path, false)
      })
    })
    refreshTestStats()
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
// 刷新数据
const refreshData = () => {
  initializeRole()
  ElMessage.success('数据已刷新')
}

// 组件挂载时初始化
onMounted(() => {
  initializeRole()
  refreshTestStats() // 从 localStorage 加载测试状态
})

// 监听路由变化
watch(() => route.query.tool, () => {
  // 路由变化时可以做一些初始化工作
}, { immediate: true })
watch(() => route.query.tool, () => {
  // 路由变化时可以做一些初始化工作
}, { immediate: true })
</script>

<style scoped>
/* 主体背景 - 动态渐变效果 */
.dev-tools {
  min-height: 100vh;
  background: linear-gradient(135deg, 
    #667eea 0%, 
    #764ba2 25%, 
    #f093fb 50%, 
    #f5576c 75%, 
    #4facfe 100%);
  background-size: 300% 300%;
  animation: gradientShift 8s ease infinite;
  position: relative;
}

.dev-tools::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
  z-index: 0;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 页面头部 - 玻璃形态效果 */
.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-bottom: 1px solid rgba(0, 0, 0, 0.08);
  padding: 24px 32px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.12),
    0 2px 6px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  position: relative;
  z-index: 1;
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
  gap: 20px;
}

.header-icon {
  font-size: 52px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.header-text h1 {
  margin: 0;
  font-size: 32px;
  background: linear-gradient(135deg, #2d3748, #4a5568);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.header-text p {
  margin: 6px 0 0 0;
  color: #64748b;
  font-size: 15px;
  font-weight: 500;
}

/* 主内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 32px;
  position: relative;
  z-index: 1;
}

.tool-panel {
  max-width: 100%;
}

.panel-header {
  margin-bottom: 36px;
  text-align: center;
}

.panel-header h2 {
  margin: 0 0 12px 0;
  font-size: 28px;
  background: linear-gradient(135deg, #fff, #f8fafc);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.panel-header p {
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  font-weight: 500;
}

/* 调试卡片样式 - 玻璃形态设计 */
.debug-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.debug-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 6px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  position: relative;
}

.debug-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
  opacity: 0.8;
}

.debug-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.3);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 16px;
  color: #1a202c;
  background: rgba(255, 255, 255, 0.8);
  margin: -20px -20px 20px -20px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.card-icon {
  font-size: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.status-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.status-item:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
}

.status-item label {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.storage-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.storage-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

.permission-tests {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.test-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.test-item:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
}

.test-item label {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

/* 富文本框增强样式 */
.enhanced-textarea :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
  border-radius: 8px !important;
  line-height: 1.5 !important;
  resize: vertical !important;
  font-family: 'Courier New', monospace !important;
  font-size: 13px !important;
  transition: all 0.3s ease !important;
}

.enhanced-textarea :deep(.el-textarea__inner):hover {
  border-color: rgba(255, 255, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.12) !important;
}

.enhanced-textarea :deep(.el-textarea__inner):focus {
  border-color: #409eff !important;
  background: rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
}

/* 统一按钮设计 - 适用于所有按钮 */
.el-button {
  border-radius: 16px !important;
  font-weight: 700 !important;
  padding: 14px 24px !important;
  border: none !important;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative !important;
  overflow: hidden !important;
  font-size: 14px !important;
  min-width: 120px;
  white-space: nowrap;
  height: 44px !important;  /* 统一按钮高度 */
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-sizing: border-box !important;
}

.el-button[size="small"] {
  padding: 12px 20px !important;
  font-size: 13px !important;
  min-width: 100px;
  height: 38px !important;  /* 统一小按钮高度 */
}

.el-button[size="large"] {
  padding: 16px 28px !important;
  font-size: 15px !important;
  min-width: 140px;
  height: 48px !important;  /* 统一大按钮高度 */
}

.el-button .el-icon {
  margin-right: 6px !important;
  font-size: 16px !important;
  position: relative;
  z-index: 2;
}

.el-button[size="small"] .el-icon {
  margin-right: 5px !important;
  font-size: 14px !important;
}

.el-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s;
  z-index: 1;
}

.el-button:hover::before {
  left: 100%;
}

.el-button span {
  position: relative;
  z-index: 2;
  line-height: 1;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4) !important;
}

.el-button--primary:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.6) !important;
}

.el-button--success {
  background: linear-gradient(135deg, #48bb78, #38a169) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(72, 187, 120, 0.4) !important;
}

.el-button--success:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 12px 30px rgba(72, 187, 120, 0.6) !important;
}

.el-button--warning {
  background: linear-gradient(135deg, #ed8936, #dd6b20) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(237, 137, 54, 0.4) !important;
}

.el-button--warning:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 12px 30px rgba(237, 137, 54, 0.6) !important;
}

.el-button--info {
  background: linear-gradient(135deg, #4299e1, #3182ce) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(66, 153, 225, 0.4) !important;
}

.el-button--info:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 12px 30px rgba(66, 153, 225, 0.6) !important;
}

.el-button--danger {
  background: linear-gradient(135deg, #f56565, #e53e3e) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(245, 101, 101, 0.4) !important;
}

.el-button--danger:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 12px 30px rgba(245, 101, 101, 0.6) !important;
}

.el-button--default {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #2d3748 !important;
  border: 2px solid rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(10px) !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1) !important;
}

.el-button--default:hover {
  background: rgba(255, 255, 255, 0.3) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
}

.el-button:disabled {
  opacity: 0.6 !important;
  transform: none !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
}

/* 按钮组容器优化 - 确保对齐 */
.action-buttons,
.role-actions,
.role-buttons,
.test-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 16px;
  margin-top: 20px;
  align-items: stretch;  /* 确保所有按钮高度一致 */
}

.role-buttons {
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  margin-bottom: 24px;
}

/* 响应式按钮布局 */
@media (max-width: 1200px) {
  .action-buttons,
  .role-actions,
  .test-actions {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 12px;
  }
  
  .role-buttons {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  }
}

@media (max-width: 768px) {
  .action-buttons,
  .role-actions,
  .role-buttons,
  .test-actions {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  
  .el-button {
    min-width: unset;
    padding: 12px 16px !important;
    font-size: 12px !important;
    height: 36px !important;
  }
  
  .el-button[size="small"] {
    padding: 10px 14px !important;
    font-size: 11px !important;
    height: 32px !important;
  }
}

@media (max-width: 480px) {
  .action-buttons,
  .role-actions,
  .role-buttons,
  .test-actions {
    grid-template-columns: 1fr;
    gap: 10px;
  }
}

/* 角色测试样式 - 升级版 */
.role-tester {
  max-width: 900px;
  margin: 0 auto;
}

.role-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 6px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

.current-role-display {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  padding: 28px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.25), rgba(255, 255, 255, 0.1));
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.role-avatar {
  font-size: 56px;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2, #f093fb);
  border-radius: 50%;
  box-shadow: 
    0 8px 24px rgba(0, 0, 0, 0.2),
    inset 0 2px 0 rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.role-avatar::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transform: rotate(45deg);
  animation: shimmer 2s linear infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.role-details h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #1a202c;
  font-weight: 700;
}

.role-details p {
  margin: 0 0 16px 0;
  color: #4a5568;
  line-height: 1.6;
  font-size: 15px;
}

.role-switcher h4 {
  margin: 0 0 16px 0;
  color: #1a202c;
  font-size: 18px;
  font-weight: 700;
}

/* 页面测试样式 - 现代化设计 */
.page-categories {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.category-section {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 6px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

.category-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0 0 24px 0;
  font-size: 20px;
  color: #1a202c;
  font-weight: 700;
}

.category-icon {
  font-size: 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.page-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.page-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.page-card:hover::before {
  left: 100%;
}

.page-card:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  border-color: rgba(255, 255, 255, 0.3);
}

.page-icon {
  font-size: 28px;
  min-width: 28px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-info {
  flex: 1;
  min-width: 0;
}

.page-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.page-buttons {
  display: flex;
  gap: 6px;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s ease;
}

.page-card:hover .page-buttons {
  opacity: 1;
  transform: translateX(0);
}

.page-status {
  margin-bottom: 4px;
}

.page-title {
  font-weight: 700;
  font-size: 16px;
  color: #1a202c;
  margin-bottom: 6px;
}

.page-description {
  font-size: 14px;
  color: #4a5568;
  margin-bottom: 6px;
  line-height: 1.4;
}

.page-path {
  font-size: 14px !important;
  color: #e2e8f0 !important;
  font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace !important;
  background: rgba(255, 255, 255, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  padding: 8px 12px !important;
  border-radius: 8px !important;
  display: inline-block !important;
  font-weight: 600 !important;
  backdrop-filter: blur(8px) !important;
  transition: all 0.3s ease !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3) !important;
  letter-spacing: 0.5px !important;
  margin-top: 4px !important;
}

.page-path:hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  color: #fff !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2) !important;
}

/* 测试管理卡片 */
.test-management-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 6px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

.test-stats {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #1a202c;
  margin-bottom: 4px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 14px;
  color: #4a5568;
  font-weight: 600;
}

.test-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

/* 欢迎页面样式 - 重新设计 */
.welcome-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
}

.welcome-card {
  max-width: 700px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 24px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 2px 6px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

.welcome-info {
  text-align: center;
  padding: 48px;
}

.welcome-icon {
  font-size: 80px;
  margin-bottom: 24px;
  display: block;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.welcome-info h3 {
  margin: 0 0 16px 0;
  font-size: 28px;
  color: #1a202c;
  font-weight: 800;
}

.welcome-info p {
  margin: 0 0 24px 0;
  color: #4a5568;
  font-size: 18px;
  font-weight: 500;
  line-height: 1.6;
}

.tool-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-align: left;
  max-width: 500px;
  margin: 0 auto;
}

.tool-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.tool-item:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(8px);
}

.tool-item span:first-child {
  font-size: 24px;
  min-width: 24px;
}

.tool-item span:last-child {
  color: #2d3748;
  font-weight: 600;
  font-size: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .debug-cards,
  .page-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .current-role-display {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .role-avatar {
    width: 80px;
    height: 80px;
    font-size: 44px;
  }

  .header-left {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }

  .main-content {
    padding: 24px 16px;
  }
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
}

/* 自定义Element Plus组件样式 */
.el-card :deep(.el-card__body) {
  padding: 20px;
}

.el-tag {
  border-radius: 8px;
  font-weight: 600;
  padding: 4px 12px;
}

.el-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.el-input :deep(.el-input__wrapper):hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(102, 126, 234, 0.3);
}

.el-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.el-alert {
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}
</style>
