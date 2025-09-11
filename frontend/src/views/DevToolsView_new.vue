<template>
  <div class="dev-tools">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <div class="header-icon">🛠️</div>
          <div class="header-text">
            <h1>开发工具</h1>
            <p>开发调试和页面测试工具集</p>
          </div>
        </div>
        <div class="header-stats">
          <el-tag type="success" size="large">
            <el-icon><View /></el-icon>
            测试模式: {{ currentRoleInfo.name }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 权限调试工具 -->
      <div v-if="activeTool === 'debug'" class="tool-panel">
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
                <label>仪表盘权限:</label>
                <el-tag :type="hasPermission('/dashboard') ? 'success' : 'danger'" size="small">
                  {{ hasPermission('/dashboard') ? '有权限' : '无权限' }}
                </el-tag>
              </div>
              <div class="test-item">
                <label>管理员权限:</label>
                <el-tag :type="hasPermission('/admin/campus') ? 'success' : 'danger'" size="small">
                  {{ hasPermission('/admin/campus') ? '有权限' : '无权限' }}
                </el-tag>
              </div>
              <div class="test-item">
                <label>学员权限:</label>
                <el-tag :type="hasPermission('/student/coaches') ? 'success' : 'danger'" size="small">
                  {{ hasPermission('/student/coaches') ? '有权限' : '无权限' }}
                </el-tag>
              </div>
            </div>
          </el-card>

          <!-- 本地存储信息 -->
          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">💾</span>
                <span>本地存储</span>
              </div>
            </template>
            <div class="storage-info">
              <div class="storage-item">
                <label>Token:</label>
                <el-input
                  v-model="localStorageData.token"
                  :placeholder="localStorageData.token || '无'"
                  readonly
                  size="small"
                />
              </div>
              <div class="storage-item">
                <label>用户信息:</label>
                <el-input
                  v-model="localStorageData.userInfo"
                  type="textarea"
                  :rows="3"
                  readonly
                  size="small"
                />
              </div>
              <div class="action-buttons">
                <el-button type="danger" size="small" @click="clearLocalStorage">
                  <el-icon><Delete /></el-icon>
                  清理存储
                </el-button>
                <el-button type="primary" size="small" @click="refreshLocalStorage">
                  <el-icon><Refresh /></el-icon>
                  刷新数据
                </el-button>
              </div>
            </div>
          </el-card>

          <!-- 系统信息 -->
          <el-card class="debug-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="card-icon">ℹ️</span>
                <span>系统信息</span>
              </div>
            </template>
            <div class="action-buttons">
              <el-button type="info" @click="viewSystemInfo">
                <el-icon><InfoFilled /></el-icon>
                查看系统信息
              </el-button>
              <el-button type="warning" @click="userStore.resetAuth">
                <el-icon><Refresh /></el-icon>
                重置认证状态
              </el-button>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 角色测试工具 -->
      <div v-else-if="activeTool === 'role'" class="tool-panel">
        <div class="panel-header">
          <h2>🎭 角色测试工具</h2>
          <p>切换不同角色进行功能测试</p>
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
                  description="角色切换仅影响测试控制台，不会改变您的实际登录状态"
                  type="warning"
                  :closable="false"
                  show-icon
                  size="small"
                />
              </div>
            </div>

            <div class="role-switcher">
              <h4>🎯 测试角色切换</h4>
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
      <div v-else-if="activeTool === 'pages'" class="tool-panel">
        <div class="panel-header">
          <h2>📄 页面测试工具</h2>
          <p>按角色分类的页面访问测试</p>
        </div>

        <div class="page-categories">
          <div
            v-for="category in pageCategories"
            :key="category.name"
            class="category-section"
          >
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

      <!-- 开发套件工具 -->
      <div v-else-if="activeTool === 'toolkit'" class="tool-panel">
        <div class="panel-header">
          <h2>🧰 开发套件</h2>
          <p>常用开发工具和实用功能</p>
        </div>

        <div class="toolkit-grid">
          <el-card class="toolkit-card" shadow="hover" @click="markAllTested">
            <div class="toolkit-item">
              <div class="toolkit-icon">✅</div>
              <div class="toolkit-info">
                <h4>标记全部已测试</h4>
                <p>将所有页面标记为已测试状态</p>
              </div>
            </div>
          </el-card>

          <el-card class="toolkit-card" shadow="hover" @click="resetTestStatus">
            <div class="toolkit-item">
              <div class="toolkit-icon">🔄</div>
              <div class="toolkit-info">
                <h4>重置测试状态</h4>
                <p>清除所有页面的测试状态</p>
              </div>
            </div>
          </el-card>

          <el-card class="toolkit-card" shadow="hover" @click="exportTestReport">
            <div class="toolkit-item">
              <div class="toolkit-icon">📊</div>
              <div class="toolkit-info">
                <h4>导出测试报告</h4>
                <p>生成页面测试状态报告</p>
              </div>
            </div>
          </el-card>

          <el-card class="toolkit-card" shadow="hover" @click="viewSystemInfo">
            <div class="toolkit-item">
              <div class="toolkit-icon">ℹ️</div>
              <div class="toolkit-info">
                <h4>系统信息</h4>
                <p>查看浏览器和系统详细信息</p>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 默认显示 - 工具概览 -->
      <div v-else class="tool-panel">
        <div class="panel-header">
          <h2>🛠️ 开发工具概览</h2>
          <p>请从左侧菜单选择要使用的工具</p>
        </div>

        <div class="overview-grid">
          <el-card shadow="hover" class="overview-card" @click="switchTool('debug')">
            <div class="overview-item">
              <div class="overview-icon">🔧</div>
              <h3>权限调试</h3>
              <p>检查用户权限和认证状态</p>
            </div>
          </el-card>

          <el-card shadow="hover" class="overview-card" @click="switchTool('role')">
            <div class="overview-item">
              <div class="overview-icon">🎭</div>
              <h3>角色测试</h3>
              <p>切换不同角色进行功能测试</p>
            </div>
          </el-card>

          <el-card shadow="hover" class="overview-card" @click="switchTool('pages')">
            <div class="overview-item">
              <div class="overview-icon">📄</div>
              <h3>页面测试</h3>
              <p>按角色分类测试所有页面</p>
            </div>
          </el-card>

          <el-card shadow="hover" class="overview-card" @click="switchTool('toolkit')">
            <div class="overview-item">
              <div class="overview-icon">🧰</div>
              <h3>开发套件</h3>
              <p>常用开发工具和实用功能</p>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  View,
  Refresh,
  Delete,
  InfoFilled,
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { hasPagePermission } from '@/utils/permissions'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 从URL参数获取当前工具
const activeTool = ref(route.query.tool || 'overview')

// 监听路由变化
watch(
  () => route.query.tool,
  (newTool) => {
    activeTool.value = newTool || 'overview'
  }
)

// 切换工具
const switchTool = (tool) => {
  router.push({ path: '/dev-tools', query: { tool } })
}

// 用户状态
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userRole = computed(() => userStore.userRole)
const userName = computed(() => userStore.userName)
const userId = computed(() => userStore.userId)

// 权限检查
const hasPermission = (path) => {
  return hasPagePermission(path, userRole.value)
}

// 本地存储数据
const localStorageData = ref({
  token: '',
  userInfo: ''
})

// 刷新本地存储数据
const refreshLocalStorage = () => {
  localStorageData.value = {
    token: localStorage.getItem('token') || '无',
    userInfo: JSON.stringify(userStore.userInfo, null, 2) || '无'
  }
  ElMessage.success('本地存储数据已刷新')
}

// 清理本地存储
const clearLocalStorage = () => {
  ElMessageBox.confirm('确定要清理所有本地存储数据吗？', '确认清理', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.clear()
    refreshLocalStorage()
    ElMessage.success('本地存储已清理')
  })
}

// 查看系统信息
const viewSystemInfo = () => {
  const info = {
    浏览器: navigator.userAgent,
    语言: navigator.language,
    屏幕分辨率: `${screen.width}x${screen.height}`,
    视口大小: `${window.innerWidth}x${window.innerHeight}`,
    时区: Intl.DateTimeFormat().resolvedOptions().timeZone,
    在线状态: navigator.onLine ? '在线' : '离线',
    Cookie启用: navigator.cookieEnabled ? '启用' : '禁用'
  }

  const infoText = Object.entries(info)
    .map(([key, value]) => `${key}: ${value}`)
    .join('\n')

  ElMessageBox.alert(infoText, '系统信息', {
    confirmButtonText: '知道了',
    type: 'info'
  })
}

// 角色测试相关
const actualRole = ref('guest')
const currentRole = ref('guest')

const availableRoles = ref([
  { value: 'guest', label: '游客', icon: '👤' },
  { value: 'student', label: '学员', icon: '🎓' },
  { value: 'coach', label: '教练', icon: '👨‍🏫' },
  { value: 'campus_admin', label: '校区管理员', icon: '🏢' },
  { value: 'super_admin', label: '超级管理员', icon: '👑' }
])

const currentRoleInfo = computed(() => {
  const roleInfoMap = {
    guest: {
      name: '游客用户',
      description: '未登录状态，只能访问公开页面',
      icon: '👤'
    },
    student: {
      name: '学员用户',
      description: '已注册的学员，可以预约课程、查看教练',
      icon: '🎓'
    },
    coach: {
      name: '教练用户',
      description: '认证教练，可以管理课程、查看学员反馈',
      icon: '👨‍🏫'
    },
    campus_admin: {
      name: '校区管理员',
      description: '校区级管理员，管理本校区的学员和教练',
      icon: '🏢'
    },
    super_admin: {
      name: '超级管理员',
      description: '系统最高权限，管理所有校区和系统设置',
      icon: '👑'
    }
  }
  return roleInfoMap[currentRole.value] || roleInfoMap.guest
})

const currentRoleTag = computed(() => {
  const tagMap = {
    guest: { type: 'info', label: '游客模式' },
    student: { type: 'primary', label: '学员模式' },
    coach: { type: 'success', label: '教练模式' },
    campus_admin: { type: 'warning', label: '校区管理员模式' },
    super_admin: { type: 'danger', label: '超级管理员模式' }
  }
  return tagMap[currentRole.value] || tagMap.guest
})

// 切换测试角色
const switchToTestRole = (role) => {
  currentRole.value = role
  ElMessage.success(`已切换到${currentRoleInfo.value.name}测试模式`)
}

// 恢复实际角色
const resetToActualRole = () => {
  currentRole.value = actualRole.value
  ElMessage.success(`已恢复到实际登录状态：${currentRoleInfo.value.name}`)
}

// 测试当前角色
const testCurrentRole = () => {
  ElMessage.info('角色测试功能开发中...')
}

// 清除角色数据
const clearRoleData = () => {
  resetToActualRole()
  ElMessage.success('测试状态已重置到实际登录状态')
}

// 显示角色权限
const showRolePermissions = () => {
  ElMessageBox.alert(`${currentRoleInfo.value.name}的详细权限说明`, '角色权限说明', {
    confirmButtonText: '知道了',
    type: 'info'
  })
}

// 初始化角色状态
const initializeRole = () => {
  if (userStore.token && userStore.userInfo && userStore.userInfo.role) {
    actualRole.value = userStore.userInfo.role
    currentRole.value = userStore.userInfo.role
  } else {
    actualRole.value = 'guest'
    currentRole.value = 'guest'
  }
}

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
        tested: false
      },
      {
        path: '/register/student',
        title: '学员注册',
        description: '学员用户注册',
        icon: '🎓',
        tested: false
      },
      {
        path: '/register/coach',
        title: '教练注册',
        description: '教练用户注册',
        icon: '👨‍🏫',
        tested: false
      }
    ]
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
        tested: false
      },
      {
        path: '/admin/service',
        title: '服务状态',
        description: '系统服务监控',
        icon: '⚡',
        tested: false
      }
    ]
  }
])

// 导航到页面
const navigateToPage = (page) => {
  page.tested = true
  const url = window.location.origin + page.path
  window.open(url, '_blank')
  ElMessage.success(`已在新标签页打开: ${page.title}`)
}

// 标记全部已测试
const markAllTested = () => {
  pageCategories.value.forEach(category => {
    category.pages.forEach(page => {
      page.tested = true
    })
  })
  ElMessage.success('已标记所有页面为已测试')
}

// 重置测试状态
const resetTestStatus = () => {
  ElMessageBox.confirm('确定要重置所有页面的测试状态吗？', '确认重置', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    pageCategories.value.forEach(category => {
      category.pages.forEach(page => {
        page.tested = false
      })
    })
    ElMessage.success('测试状态已重置')
  })
}

// 导出测试报告
const exportTestReport = () => {
  const allPages = pageCategories.value.flatMap(category => category.pages)
  const totalPages = allPages.length
  const testedPages = allPages.filter(page => page.tested).length

  const report = {
    timestamp: new Date().toISOString(),
    totalPages,
    testedPages,
    passRate: totalPages > 0 ? Math.round((testedPages / totalPages) * 100) : 0,
    pages: allPages.map(page => ({
      path: page.path,
      title: page.title,
      tested: page.tested
    }))
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

// 组件挂载时初始化
onMounted(() => {
  refreshLocalStorage()
  initializeRole()
})
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

.debug-cards,
.overview-grid,
.toolkit-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.debug-card,
.overview-card,
.toolkit-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.overview-card,
.toolkit-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.overview-card:hover,
.toolkit-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
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

.status-grid,
.permission-tests,
.storage-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item,
.test-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.status-item label,
.test-item label {
  font-weight: 500;
  color: #666;
}

.storage-item label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #666;
}

.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

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

.overview-item,
.toolkit-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.overview-icon,
.toolkit-icon {
  font-size: 48px;
  min-width: 48px;
}

.overview-item h3,
.toolkit-info h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.overview-item p,
.toolkit-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .debug-cards,
  .overview-grid,
  .toolkit-grid,
  .page-grid {
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

  .role-buttons,
  .role-actions {
    justify-content: center;
  }

  .overview-item,
  .toolkit-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}
</style>
