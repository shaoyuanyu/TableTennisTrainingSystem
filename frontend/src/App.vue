<script setup>
import {RouterView} from 'vue-router'
import {onMounted} from 'vue'
import {useUserStore} from '@/stores/user'
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

/* Element Plus 样式覆盖 */

/* 响应式布局 */
@media (max-width: 768px) {
}
</style>
