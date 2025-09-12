<template>
  <div :class="contentClasses">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'DebugCardContent',
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
      const classes = ['debug-card-content'];
      
      if (this.type !== 'default') {
        classes.push(`debug-card-content--${this.type}`);
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
.debug-card-content {
  /* 基础内容样式 - 与DevToolsView保持一致 */
  margin: 0;
  padding: 0;
}

/* 网格布局类型（用于status-grid等） */
.debug-card-content--grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 表单布局类型（用于存储信息等） */
.debug-card-content--form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 操作按钮布局类型 */
.debug-card-content--actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: 12px;
  align-items: stretch;
  margin-top: 16px;
}

/* 确保内部组件与DevToolsView样式一致 */
.debug-card-content :deep(.storage-item),
.debug-card-content :deep(.test-item),
.debug-card-content :deep(.status-item) {
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

.debug-card-content :deep(.storage-item:hover),
.debug-card-content :deep(.test-item:hover),
.debug-card-content :deep(.status-item:hover) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
  border-color: rgba(255, 255, 255, 0.2);
}

.debug-card-content :deep(.storage-item label),
.debug-card-content :deep(.test-item label),
.debug-card-content :deep(.status-item label) {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
  margin: 0;
  flex: 0 0 auto;
  min-width: fit-content;
}

/* 表单项特殊样式 */
.debug-card-content--form :deep(.storage-item) {
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding: 16px;
}

.debug-card-content--form :deep(.storage-item label) {
  display: block;
  margin-bottom: 8px;
}

/* 文本域增强样式 */
.debug-card-content :deep(.enhanced-textarea .el-textarea__inner) {
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

.debug-card-content :deep(.enhanced-textarea .el-textarea__inner:hover) {
  border-color: rgba(255, 255, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.12) !important;
}

.debug-card-content :deep(.enhanced-textarea .el-textarea__inner:focus) {
  border-color: #409eff !important;
  background: rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .debug-card-content--actions {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .debug-card-content :deep(.storage-item),
  .debug-card-content :deep(.test-item),
  .debug-card-content :deep(.status-item) {
    padding: 10px 14px;
    min-height: 40px;
  }
}

@media (max-width: 480px) {
  .debug-card-content--actions {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .debug-card-content :deep(.storage-item),
  .debug-card-content :deep(.test-item),
  .debug-card-content :deep(.status-item) {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 12px;
  }
  
  .debug-card-content :deep(.storage-item > *:last-child),
  .debug-card-content :deep(.test-item > *:last-child),
  .debug-card-content :deep(.status-item > *:last-child) {
    align-self: flex-end;
  }
}
</style>
