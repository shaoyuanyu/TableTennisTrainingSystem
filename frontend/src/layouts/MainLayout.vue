<template>
  <el-container class="main-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <el-button :icon="Expand" @click="toggleSidebar" circle text class="sidebar-toggle" />
        <h1 class="app-title">乒乓球培训管理系统</h1>
      </div>

      <div class="header-right">
        <!-- 消息通知 -->
        <el-tooltip content="消息中心" placement="bottom" v-if="userStore.isLoggedIn">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-button :icon="Bell" @click="goToMessages" circle text class="header-button" />
          </el-badge>
        </el-tooltip>

        <!-- 用户下拉菜单（登录用户） -->
        <el-dropdown @command="handleUserCommand" class="user-dropdown" v-if="userStore.isLoggedIn">
          <span class="user-info">
            <el-avatar :size="32" :src="userInfo.avatar">
              <el-icon>
                <User />
              </el-icon>
            </el-avatar>
            <span class="user-name">{{ userName }}</span>
            <el-icon class="dropdown-icon">
              <ArrowDown />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon>
                  <User />
                </el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon>
                  <Setting />
                </el-icon>
                系统设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon>
                  <SwitchButton />
                </el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 游客登录按钮 -->
        <div class="guest-actions" v-else>
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-dropdown @command="handleRegisterCommand">
            <el-button>
              注册
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="student">
                  <el-icon>
                    <User />
                  </el-icon>
                  学员注册
                </el-dropdown-item>
                <el-dropdown-item command="coach">
                  <el-icon>
                    <User />
                  </el-icon>
                  教练注册
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth" class="sidebar">
        <AppSidebar :collapsed="sidebarCollapsed" />
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { Expand, Bell, User, ArrowDown, Setting, SwitchButton } from '@element-plus/icons-vue'
import AppSidebar from '@/components/AppSidebar.vue'
import { useMessageStore } from '@/stores/messageStore'
import { storeToRefs } from 'pinia'
import { initializeAuth } from '@/utils/auth'

// 使用 Pinia Store
const messageStore = useMessageStore()
const { unreadCount } = storeToRefs(messageStore)

const router = useRouter()
const userStore = useUserStore()

// 侧边栏状态
const sidebarCollapsed = ref(false)
const sidebarWidth = computed(() => (sidebarCollapsed.value ? '64px' : '220px'))

// 用户信息
const userInfo = computed(() => userStore.userInfo)
const userName = computed(() => userStore.userName)

// 初始化认证状态
onMounted(async () => {
  await initializeAuth()
})

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 前往消息页面
const goToMessages = () => {
  router.push('/messages')
}

// 前往登录页面
const goToLogin = () => {
  router.push('/login')
}

// 处理注册命令
const handleRegisterCommand = (command) => {
  if (command === 'student') {
    router.push('/register/student')
  } else if (command === 'coach') {
    router.push('/register/coach')
  }
}

// 处理用户菜单命令
const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      // TODO: 实现设置页面
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
        userStore.logout()
      } catch {
        // 用户取消退出
      }
      break
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
  background-color: #f0f2f5;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sidebar-toggle {
  color: #666;
}

.sidebar-toggle:hover {
  color: #1890ff;
}

.app-title {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  margin-right: 8px;
}

.header-button {
  color: #666;
}

.header-button:hover {
  color: #1890ff;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f0f0f0;
}

.user-name {
  font-size: 14px;
  color: #333;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-icon {
  color: #999;
  font-size: 12px;
}

.guest-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sidebar {
  background: #fff;
  border-right: 1px solid #e8e8e8;
  transition: width 0.3s;
  overflow: hidden;
}

.main-content {
  padding: 24px;
  background-color: #f0f2f5;
  overflow-y: auto;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
