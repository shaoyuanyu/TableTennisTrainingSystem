<template>
  <div class="debug-page">
    <div class="debug-header">
      <h1>🔧 权限系统调试页面</h1>
      <p>用于诊断和测试权限系统状态</p>
    </div>

    <div class="debug-sections">
      <!-- 用户状态信息 -->
      <div class="debug-section">
        <h2>👤 用户状态</h2>
        <div class="status-grid">
          <div class="status-item">
            <label>登录状态:</label>
            <span :class="isLoggedIn ? 'success' : 'error'">
              {{ isLoggedIn ? '✅ 已登录' : '❌ 未登录' }}
            </span>
          </div>
          <div class="status-item">
            <label>用户角色:</label>
            <span>{{ userRole || '无' }}</span>
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
      </div>

      <!-- 存储状态 -->
      <div class="debug-section">
        <h2>💾 存储状态</h2>
        <div class="storage-info">
          <div class="storage-item">
            <label>Session标识:</label>
            <code>{{ token || '无' }}</code>
          </div>
          <div class="storage-item">
            <label>用户信息:</label>
            <pre>{{ JSON.stringify(userInfo, null, 2) }}</pre>
          </div>
          <div class="storage-item">
            <label>LocalStorage Session:</label>
            <code>{{ localStorageToken || '无' }}</code>
          </div>
          <div class="storage-item">
            <label>LocalStorage UserInfo:</label>
            <pre>{{ localStorageUserInfo || '无' }}</pre>
          </div>
        </div>
      </div>

      <!-- 权限测试 -->
      <div class="debug-section">
        <h2>🔐 权限测试</h2>
        <div class="permission-tests">
          <div class="test-item">
            <label>可访问仪表盘:</label>
            <span :class="canAccessDashboard ? 'success' : 'error'">
              {{ canAccessDashboard ? '✅ 是' : '❌ 否' }}
            </span>
          </div>
          <div class="test-item">
            <label>管理员权限:</label>
            <span :class="hasManagementAccess ? 'success' : 'error'">
              {{ hasManagementAccess ? '✅ 是' : '❌ 否' }}
            </span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="debug-section">
        <h2>🛠️ 调试操作</h2>
        <div class="action-buttons">
          <el-button type="primary" @click="refreshState">刷新状态</el-button>
          <el-button type="warning" @click="resetAuth">重置认证</el-button>
          <el-button type="success" @click="goToLogin">前往登录</el-button>
          <el-button type="info" @click="checkAuth">检查认证</el-button>
          <el-button type="default" @click="goToConsole">
            <el-icon>
              <Grid />
            </el-icon>
            返回控制台
          </el-button>
        </div>
      </div>

      <!-- 路由测试 -->
      <div class="debug-section">
        <h2>🛣️ 路由测试</h2>
        <div class="route-buttons">
          <el-button size="small" @click="testRoute('/dashboard')">仪表盘</el-button>
          <el-button size="small" @click="testRoute('/admin/campus')">校区管理</el-button>
          <el-button size="small" @click="testRoute('/student/coaches')">找教练</el-button>
          <el-button size="small" @click="testRoute('/profile')">个人中心</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { usePermissions } from '@/composables/usePermissions'
import { ElMessage } from 'element-plus'
import { Grid } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const { canAccessPage, hasManagementAccess } = usePermissions()

// 响应式状态
const localStorageToken = ref('')
const localStorageUserInfo = ref('')

// 用户状态（session认证模式）
const { isLoggedIn, userRole, userName, userId, token, userInfo } = userStore

// 权限测试
const canAccessDashboard = computed(() => canAccessPage('/dashboard'))

// 刷新本地存储状态
const refreshLocalStorage = () => {
  localStorageToken.value = localStorage.getItem('token') || '' // session标识
  localStorageUserInfo.value = localStorage.getItem('userInfo') || ''
}

// 刷新状态
const refreshState = () => {
  refreshLocalStorage()
  ElMessage.success('状态已刷新')
}

// 重置认证
const resetAuth = () => {
  userStore.resetAuth()
  refreshLocalStorage()
  ElMessage.warning('认证状态已重置')
}

// 前往登录
const goToLogin = () => {
  router.push('/login')
}

// 返回开发测试控制台
const goToConsole = () => {
  router.push('/page-square')
  ElMessage.success('返回开发测试控制台')
}

// 检查认证
const checkAuth = async () => {
  try {
    const result = await userStore.checkAuthStatus()
    ElMessage.success(result ? '认证有效' : '认证无效')
    refreshState()
  } catch (error) {
    ElMessage.error('检查认证失败: ' + error.message)
  }
}

// 测试路由
const testRoute = (path) => {
  try {
    router.push(path)
  } catch (error) {
    ElMessage.error('路由跳转失败: ' + error.message)
  }
}

// 组件挂载时初始化
onMounted(() => {
  refreshLocalStorage()
})
</script>

<style scoped>
.debug-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Monaco', 'Menlo', monospace;
  min-height: 100vh;
  max-height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
}

.debug-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
}

.debug-header h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.debug-sections {
  display: grid;
  gap: 20px;
}

.debug-section {
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.debug-section h2 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 8px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.status-item label {
  font-weight: bold;
  color: #666;
}

.success {
  color: #67c23a;
  font-weight: bold;
}

.error {
  color: #f56c6c;
  font-weight: bold;
}

.storage-info, .permission-tests {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.storage-item, .test-item {
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.storage-item label, .test-item label {
  display: block;
  font-weight: bold;
  color: #666;
  margin-bottom: 5px;
}

.storage-item code {
  background: #eee;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
}

.storage-item pre {
  background: #eee;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  margin: 0;
}

.action-buttons, .route-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.route-buttons {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .debug-page {
    padding: 10px;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons, .route-buttons {
    flex-direction: column;
  }
}
</style>
