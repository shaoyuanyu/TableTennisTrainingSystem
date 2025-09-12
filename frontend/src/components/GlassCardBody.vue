<template>
  <div :class="contentClasses">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'GlassCardBody',
  props: {
    // 内容类型，影响布局和样式
    type: {
      type: String,
      default: 'default',
      validator: value => ['default', 'grid', 'form', 'actions'].includes(value)
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: ''
    }
  },
  computed: {
    contentClasses() {
      const classes = ['glass-card-body'];
      
      if (this.type !== 'default') {
        classes.push(`glass-card-body--${this.type}`);
      }
      
      if (this.extraClasses) {
        classes.push(this.extraClasses);
      }
      
      return classes.join(' ');
    }
  }
}
</script>

<style scoped>
.glass-card-body {
  /* 基础内容样式 - 统一的卡片内容布局 */
  margin: 0;
  padding: 0;
}

/* 网格布局类型（用于status-grid等） */
.glass-card-body--grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 表单布局类型（用于存储信息等） */
.glass-card-body--form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 操作按钮布局类型 */
.glass-card-body--actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 12px;
  align-items: stretch;
  margin-top: 16px;
}

/* 确保内部组件样式一致 */
.glass-card-body :deep(.storage-item),
.glass-card-body :deep(.test-item),
.glass-card-body :deep(.status-item) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  min-height: 44px;
  box-sizing: border-box;
}

.glass-card-body :deep(.storage-item:hover),
.glass-card-body :deep(.test-item:hover),
.glass-card-body :deep(.status-item:hover) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
  border-color: rgba(255, 255, 255, 0.2);
}

.glass-card-body :deep(.storage-item label),
.glass-card-body :deep(.test-item label),
.glass-card-body :deep(.status-item label) {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
  margin: 0;
  flex: 0 0 auto;
  min-width: fit-content;
}

/* 表单项特殊样式 */
.glass-card-body--form :deep(.storage-item) {
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding: 16px;
}

.glass-card-body--form :deep(.storage-item label) {
  display: block;
  margin-bottom: 8px;
}

/* 文本域增强样式 */
.glass-card-body :deep(.enhanced-textarea .el-textarea__inner) {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
  border-radius: 8px !important;
  line-height: 1.5 !important;
  resize: vertical !important;
  font-family: 'Courier New', monospace !important;
  font-size: 13px !important;
  transition: all 0.3s ease !important;
}

.glass-card-body :deep(.enhanced-textarea .el-textarea__inner:hover) {
  border-color: rgba(255, 255, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.12) !important;
}

.glass-card-body :deep(.enhanced-textarea .el-textarea__inner:focus) {
  border-color: #409eff !important;
  background: rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-card-body--actions {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .glass-card-body :deep(.storage-item),
  .glass-card-body :deep(.test-item),
  .glass-card-body :deep(.status-item) {
    padding: 10px 14px;
    min-height: 40px;
  }
}

@media (max-width: 480px) {
  .glass-card-body--actions {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .glass-card-body :deep(.storage-item),
  .glass-card-body :deep(.test-item),
  .glass-card-body :deep(.status-item) {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 12px;
  }
  
  .glass-card-body :deep(.storage-item > *:last-child),
  .glass-card-body :deep(.test-item > *:last-child),
  .glass-card-body :deep(.status-item > *:last-child) {
    align-self: flex-end;
  }
}
</style>
