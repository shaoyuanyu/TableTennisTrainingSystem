<template>
  <el-container class="main-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <IconButton @click="toggleSidebar" variant="glass" size="sm" class="sidebar-toggle">
          <el-icon>
            <Expand />
          </el-icon>
        </IconButton>
        <h1 class="app-title">乒乓球培训管理系统</h1>
      </div>

      <div class="header-right">
        <!-- 消息通知 -->
        <el-tooltip content="消息中心" placement="bottom" v-if="userStore.isLoggedIn">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <IconButton :color="'secondary'" variant="glass" size="sm" square to="/messages" class="header-button">
              <template #default>
                <el-icon>
                  <Bell />
                </el-icon>
              </template>
            </IconButton>
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
          <PrimaryButton to="/login">登录</PrimaryButton>
          <el-dropdown @command="handleRegisterCommand">
            <OutlineButton>
              注册
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </OutlineButton>
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
        <AppSidebarNew :collapsed="sidebarCollapsed" />
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import {ElMessageBox} from 'element-plus'
import {ArrowDown, Bell, Expand, Setting, SwitchButton, User} from '@element-plus/icons-vue'
import AppSidebarNew from '@/components/AppSidebarNew.vue'
import {useMessageStore} from '@/stores/messageStore'
import {storeToRefs} from 'pinia'
import {initializeAuth} from '@/utils/auth'
import {IconButton, OutlineButton, PrimaryButton} from '@/components/buttons'

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

// 按钮已使用 `to` 属性进行导航，无需显式方法

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
  background: transparent;
}

.header {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(25px) saturate(150%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow:
    0 2px 8px rgba(0, 0, 0, 0.1),
    0 1px 3px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  z-index: 1000;
  transition: all 0.3s ease;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sidebar-toggle.btn-modern {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.sidebar-toggle.btn-modern:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.app-title {
  font-size: 20px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
  text-shadow:
    0 2px 4px rgba(0, 0, 0, 0.4),
    0 1px 2px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.3px;
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
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.header-button:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  text-shadow:
    0 1px 3px rgba(0, 0, 0, 0.4),
    0 1px 2px rgba(0, 0, 0, 0.3);
}

.dropdown-icon {
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3));
}

.guest-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.guest-actions .btn-modern {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: #ffffff;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.guest-actions .btn-modern:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.guest-actions .btn-modern {
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #ffffff;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.guest-actions .btn-modern:not(.btn-primary) {
  background: rgba(255, 255, 255, 0.1);
}

.guest-actions .btn-modern:not(.btn-primary):hover {
  background: rgba(255, 255, 255, 0.2);
}

.sidebar {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(15px);
  border-right: 1px solid rgba(255, 255, 255, 0.25);
  transition: width 0.3s ease;
  overflow: hidden;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);
}

.main-content {
  padding: 0;
  background: transparent;
  overflow-y: auto;
  height: calc(100vh - 60px);
  /* 减去header高度 */
}

/* Element Plus的el-main样式调整 */
:deep(.el-main) {
  padding: 0;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
