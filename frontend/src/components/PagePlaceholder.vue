<template>
  <div class="page-placeholder">
    <el-card>
      <template #header>
        <h2>{{ title }}</h2>
      </template>
      <el-empty :description="`${title}页面正在开发中`" />
      <div class="placeholder-actions">
        <OutlineButton @click="$router.go(-1)">
          <template #icon-left>
            <el-icon>
              <ArrowLeft />
            </el-icon>
          </template>
          返回上页
        </OutlineButton>
        <PrimaryButton to="/dashboard">
          <template #icon-left>
            <el-icon>
              <House />
            </el-icon>
          </template>
          回到首页
        </PrimaryButton>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { PrimaryButton, OutlineButton } from '@/components/buttons'
import { ArrowLeft, House } from '@element-plus/icons-vue'
defineProps({
  title: {
    type: String,
    default: '页面',
  },
})
</script>

<style scoped>
.page-placeholder {
  max-width: 600px;
  margin: 0 auto;
}

.placeholder-actions {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
}

/* 让按钮在卡片中更紧凑、统一 */
.placeholder-actions :deep(.btn-modern) {
  min-width: 120px;
}
</style>
