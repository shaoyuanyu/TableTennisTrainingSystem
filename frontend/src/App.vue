<script setup>
import { RouterView } from 'vue-router'
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import ToastContainer from '@/components/ToastContainer.vue'

const userStore = useUserStore()

// 应用启动时初始化认证状态
onMounted(() => {
  if (import.meta.env.DEV) {
    console.log('应用启动，当前认证状态:', {
      isLoggedIn: userStore.isLoggedIn,
      userRole: userStore.userRole,
      hasUserInfo: !!userStore.userInfo.id,
    })
  }
})
</script>

<template>
  <router-view />
  <ToastContainer />

</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

#app {
  height: 100vh;
  overflow: hidden;
}

/* Element Plus 样式覆盖 */
.el-main {
  padding: 0;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .el-aside {
    width: 200px !important;
  }
}
</style>
