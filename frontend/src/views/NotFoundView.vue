<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="not-found-icon">
        <el-icon size="120" color="#e6e6e6">
          <WarningFilled />
        </el-icon>
      </div>
      <h1 class="not-found-title">404</h1>
      <p class="not-found-subtitle">抱歉，您访问的页面不存在</p>
      <div class="not-found-actions">
        <el-button type="primary" @click="goHome"> 返回首页 </el-button>
        <el-button @click="goBack"> 返回上页 </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
// 原权限相关导入 - 暂时注释掉，权限管理将在后端实现
// import { useUserStore } from '@/stores/user'
// import { getDefaultHomePage } from '@/utils/permissions'
import { WarningFilled } from '@element-plus/icons-vue'

const router = useRouter()
// const userStore = useUserStore()

const goHome = () => {
  // 原有的权限检查逻辑 - 暂时注释掉
  /*
  if (userStore.isLoggedIn) {
    const homePage = getDefaultHomePage(userStore.userRole)
    router.push(homePage)
  } else {
    router.push('/login')
  }
  */

  // 临时简化逻辑 - 直接跳转到仪表盘
  router.push('/dashboard')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.not-found-content {
  text-align: center;
  background: white;
  padding: 60px 40px;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
}

.not-found-icon {
  margin-bottom: 20px;
}

.not-found-title {
  font-size: 72px;
  font-weight: 700;
  color: #333;
  margin: 0 0 16px 0;
  line-height: 1;
}

.not-found-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0 0 40px 0;
}

.not-found-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

@media (max-width: 480px) {
  .not-found-content {
    padding: 40px 20px;
  }

  .not-found-title {
    font-size: 48px;
  }

  .not-found-subtitle {
    font-size: 16px;
  }

  .not-found-actions {
    flex-direction: column;
  }
}
</style>
