<template>
  <div class="page-placeholder">
    <el-card>
      <template #header>
        <h2>{{ title }}</h2>
      </template>
      <el-empty :description="`${title}页面正在开发中`" />
      <div class="placeholder-actions">
        <el-button type="primary" @click="$router.go(-1)"> 返回上页 </el-button>
        <el-button @click="$router.push('/dashboard')"> 回到首页 </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
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
  text-align: center;
}

.placeholder-actions .el-button {
  margin: 0 8px;
}
</style>
