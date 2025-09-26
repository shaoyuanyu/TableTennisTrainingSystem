<template>
  <div class="my-coaches">
    <div class="page-header">
      <h2>我的教练</h2>
      <p>查看和管理您已选择的教练</p>
    </div>
    
    <el-card>
      <template #header>
        <div class="card-header">
          <span>当前教练</span>
          <el-tag type="success">最多可选择2位教练</el-tag>
        </div>
      </template>
      
      <div class="coaches-content">
        <el-alert
          title="功能说明"
          description="教练双选功能已迁移至专门的双选页面，请点击下方按钮前往查看和管理您的教练关系。"
          type="info"
          show-icon
          :closable="false"
        />
        
        <div class="actions">
          <PrimaryButton @click="goToMutualSelection">前往教练双选页面</PrimaryButton>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'

const router = useRouter()

const goToMutualSelection = () => {
  router.push('/student/mutual-selection')
}
</script>

<style scoped>
.my-coaches {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.coaches-content {
  padding: 20px 0;
}

.actions {
  text-align: center;
  margin-top: 30px;
}
</style>
