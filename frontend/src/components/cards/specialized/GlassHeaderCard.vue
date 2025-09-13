<template>
  <el-card :class="cardClasses" shadow="hover">
    <!-- 自定义头部 -->
    <template #header>
      <div class="glass-header-card-header">
        <span v-if="icon" class="card-icon">{{ icon }}</span>
        <span class="card-title">{{ title }}</span>
        <div v-if="$slots.headerActions" class="header-actions">
          <slot name="headerActions"></slot>
        </div>
      </div>
    </template>

    <!-- 卡片内容 -->
    <div class="glass-header-card-content">
      <slot></slot>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'GlassHeaderCard',
  props: {
    // 卡片标题
    title: {
      type: String,
      required: true,
    },
    // 图标
    icon: {
      type: String,
      default: '',
    },
    // 卡片尺寸
    size: {
      type: String,
      default: 'medium', // small, medium, large
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: '',
    },
  },
  computed: {
    cardClasses() {
      const classes = ['glass-header-card', `glass-header-card--${this.size}`]

      if (this.extraClasses) {
        classes.push(this.extraClasses)
      }

      return classes.join(' ')
    },
  },
}
</script>

<style scoped>
/* 基础卡片容器样式 */
.glass-header-card {
  background: var(--white-alpha-15);
  backdrop-filter: var(--blur-xl);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  transition: var(--transition-normal);
  overflow: hidden;
  position: relative;
}

.glass-header-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow:
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 自定义头部样式 - 修复padding问题 */
.glass-header-card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-md);
  color: #1a202c;
  background: rgba(255, 255, 255, 0.9);
  /* 修改：去掉负边距，使用正常的padding */
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;
  backdrop-filter: var(--blur-sm);
  /* 新增：确保头部有合适的边距 */
  margin-bottom: var(--spacing-md);
}

.glass-header-card-header .card-icon {
  font-size: var(--font-size-xl);
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
  /* 新增：确保图标有合适的显示空间 */
  min-width: 1.5em;
  text-align: center;
}

.glass-header-card-header .card-title {
  flex: 1;
  font-weight: var(--font-weight-bold);
  color: #1a202c;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  /* 新增：确保标题有合适的行高 */
  line-height: 1.4;
}

.glass-header-card-header .header-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
  margin-left: auto;
  /* 新增：确保操作区域不会过度挤压 */
  flex-shrink: 0;
}

/* 内容区域 */
.glass-header-card-content {
  position: relative;
  z-index: 1;
  /* 修复：避免与 .el-card__body 的 padding 叠加，导致左右不对齐 */
  padding: 0;
}

/* 尺寸变体 */
.glass-header-card--small {
  border-radius: var(--radius-lg);
}

.glass-header-card--small .glass-header-card-header {
  font-size: var(--font-size-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  gap: var(--spacing-sm);
}

.glass-header-card--small .glass-header-card-header .card-icon {
  font-size: var(--font-size-lg);
}

.glass-header-card--large {
  border-radius: var(--radius-2xl);
}

.glass-header-card--large .glass-header-card-header {
  font-size: var(--font-size-lg);
  padding: var(--spacing-xl) var(--spacing-2xl);
  gap: var(--spacing-lg);
}

.glass-header-card--large .glass-header-card-header .card-icon {
  font-size: var(--font-size-2xl);
}

/* Element Plus 卡片样式覆盖 */
:deep(.el-card__header) {
  padding: 0;
  border-bottom: none;
  background: transparent;
}

:deep(.el-card__body) {
  padding: var(--spacing-xl);
}

.glass-header-card--small :deep(.el-card__body) {
  padding: var(--spacing-lg);
}

.glass-header-card--large :deep(.el-card__body) {
  padding: var(--spacing-2xl);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-header-card-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
    padding: var(--spacing-md);
  }

  .glass-header-card-header .header-actions {
    width: 100%;
    justify-content: flex-end;
    margin-left: 0;
  }

  :deep(.el-card__body) {
    padding: var(--spacing-lg);
  }
}

@media (max-width: 480px) {
  .glass-header-card-header {
    font-size: var(--font-size-sm);
    padding: var(--spacing-sm);
  }

  .glass-header-card-header .card-icon {
    font-size: var(--font-size-lg);
  }

  :deep(.el-card__body) {
    padding: var(--spacing-md);
  }
}
</style>
