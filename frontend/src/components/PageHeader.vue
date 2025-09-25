<template>
  <div class="page-header">
    <div class="header-content">
      <h1 class="page-title">{{ title }}</h1>
      <div class="header-actions" v-if="$slots.default">
        <slot></slot>
      </div>
    </div>
    <div class="header-description" v-if="description">
      <p>{{ description }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
  padding: 20px 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-description {
  margin-top: 8px;
}

.header-description p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .page-title {
    font-size: 20px;
  }
}
</style>