<template>
  <div :class="contentClasses" :style="contentStyles">
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
      validator: (value) =>
        ['default', 'grid', 'form', 'actions', 'flex', 'status'].includes(value),
    },
    // 方向（仅在 flex 类型下有效）
    direction: {
      type: String,
      default: 'column', // row, column
      validator: (value) => ['row', 'column'].includes(value),
    },
    // 对齐方式
    align: {
      type: String,
      default: 'stretch', // stretch, start, center, end, baseline
      validator: (value) => ['stretch', 'start', 'center', 'end', 'baseline'].includes(value),
    },
    // 内容间距
    gap: {
      type: String,
      default: 'var(--spacing-md)',
    },
    // 网格列数（仅在 grid 类型下有效）
    columns: {
      type: Number,
      default: 1,
    },
    // 最小列宽（仅在 actions 类型下有效）
    minColumnWidth: {
      type: String,
      default: '120px',
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: '',
    },
  },
  computed: {
    contentClasses() {
      const classes = ['glass-card-body']

      if (this.type !== 'default') {
        classes.push(`glass-card-body--${this.type}`)
      }

      if (this.type === 'flex' && this.direction !== 'column') {
        classes.push(`glass-card-body--${this.direction}`)
      }

      if (this.align !== 'stretch') {
        classes.push(`glass-card-body--align-${this.align}`)
      }

      if (this.extraClasses) {
        classes.push(this.extraClasses)
      }

      return classes.join(' ')
    },
    contentStyles() {
      const styles = {}

      if (this.type === 'grid' && this.columns > 1) {
        styles.gridTemplateColumns = `repeat(${this.columns}, 1fr)`
      }

      if (this.type === 'actions') {
        styles.gridTemplateColumns = `repeat(auto-fit, minmax(${this.minColumnWidth}, 1fr))`
      }

      return styles
    },
  },
}
</script>

<style scoped>
.glass-card-body {
  /* 基础内容样式 - 统一的卡片内容布局 */
  margin: 0;
  padding: 0;
  width: 100%;
}

/* 网格布局类型 */
.glass-card-body--grid {
  display: grid;
  gap: v-bind(gap);
  align-items: v-bind(align);
}

/* 弹性布局类型 */
.glass-card-body--flex {
  display: flex;
  flex-direction: v-bind(direction);
  gap: v-bind(gap);
  align-items: v-bind(align);
}

.glass-card-body--row {
  flex-direction: row;
}

/* 表单布局类型 */
.glass-card-body--form {
  display: flex;
  flex-direction: column;
  gap: v-bind(gap);
}

/* 操作按钮布局类型 */
.glass-card-body--actions {
  display: grid;
  gap: v-bind(gap);
  align-items: stretch;
  margin-top: var(--spacing-lg);
}

/* 状态显示布局类型 */
.glass-card-body--status {
  display: flex;
  flex-direction: column;
  gap: v-bind(gap);
}

/* 对齐方式 */
.glass-card-body--align-start {
  align-items: flex-start;
}

.glass-card-body--align-center {
  align-items: center;
}

.glass-card-body--align-end {
  align-items: flex-end;
}

.glass-card-body--align-baseline {
  align-items: baseline;
}

/* 确保内部组件样式一致 */
.glass-card-body :deep(.storage-item),
.glass-card-body :deep(.test-item),
.glass-card-body :deep(.status-item) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--white-alpha-20);
  border-radius: var(--radius-lg);
  border: 1px solid var(--white-alpha-15);
  transition: var(--transition-normal);
  min-height: 44px;
  box-sizing: border-box;
  backdrop-filter: var(--blur-sm);
}

.glass-card-body :deep(.storage-item:hover),
.glass-card-body :deep(.test-item:hover),
.glass-card-body :deep(.status-item:hover) {
  background: var(--white-alpha-30);
  transform: translateX(4px);
  border-color: var(--white-alpha-25);
  box-shadow: var(--shadow-sm);
}

.glass-card-body :deep(.storage-item label),
.glass-card-body :deep(.test-item label),
.glass-card-body :deep(.status-item label) {
  font-weight: var(--font-weight-semibold);
  color: white;
  font-size: var(--font-size-md);
  margin: 0;
  flex: 0 0 auto;
  min-width: fit-content;
  text-shadow: 0 1px 2px var(--black-alpha-30);
}

.glass-card-body :deep(.storage-item > *:last-child),
.glass-card-body :deep(.test-item > *:last-child),
.glass-card-body :deep(.status-item > *:last-child) {
  color: var(--white-alpha-90);
  font-size: var(--font-size-sm);
}

/* 表单项特殊样式 */
.glass-card-body--form :deep(.storage-item) {
  flex-direction: column;
  align-items: flex-start;
  gap: var(--spacing-sm);
  padding: var(--spacing-lg);
}

.glass-card-body--form :deep(.storage-item label) {
  display: block;
  margin-bottom: var(--spacing-sm);
}

/* 文本域增强样式 */
.glass-card-body :deep(.enhanced-textarea .el-textarea__inner) {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
  border-radius: var(--radius-md) !important;
  line-height: 1.5 !important;
  resize: vertical !important;
  font-family: 'Courier New', monospace !important;
  font-size: var(--font-size-sm) !important;
  transition: var(--transition-normal) !important;
}

.glass-card-body :deep(.enhanced-textarea .el-textarea__inner:hover) {
  border-color: rgba(255, 255, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.12) !important;
}

.glass-card-body :deep(.enhanced-textarea .el-textarea__inner:focus) {
  border-color: var(--primary-color) !important;
  background: rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-card-body--actions {
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-sm);
  }

  .glass-card-body--flex.glass-card-body--row {
    flex-direction: column;
  }

  .glass-card-body :deep(.storage-item),
  .glass-card-body :deep(.test-item),
  .glass-card-body :deep(.status-item) {
    padding: var(--spacing-sm) var(--spacing-md);
    min-height: 40px;
  }
}

@media (max-width: 480px) {
  .glass-card-body--actions {
    grid-template-columns: 1fr;
    gap: var(--spacing-xs);
  }

  .glass-card-body :deep(.storage-item),
  .glass-card-body :deep(.test-item),
  .glass-card-body :deep(.status-item) {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-xs);
    padding: var(--spacing-md);
  }

  .glass-card-body :deep(.storage-item > *:last-child),
  .glass-card-body :deep(.test-item > *:last-child),
  .glass-card-body :deep(.status-item > *:last-child) {
    align-self: flex-end;
  }
}
</style>
