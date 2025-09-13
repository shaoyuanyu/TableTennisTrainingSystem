<template>
  <el-card :class="cardClasses" shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="card-icon">{{ icon }}</span>
        <span>{{ title }}</span>
        <div v-if="$slots.actions" class="header-actions">
          <slot name="actions"></slot>
        </div>
      </div>
    </template>
    
    <div class="card-content">
      <slot></slot>
    </div>
  </el-card>
</template>

<script>
export default {
  name: 'GlassCardWithHeader',
  props: {
    // 卡片标题
    title: {
      type: String,
      required: true
    },
    // 卡片图标
    icon: {
      type: String,
      default: '🔧'
    },
    // 卡片类型（影响头部颜色主题）
    type: {
      type: String,
      default: 'default',
      validator: value => ['default', 'primary', 'success', 'warning', 'danger', 'info'].includes(value)
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: ''
    }
  },
  computed: {
    cardClasses() {
      const classes = ['glass-card-with-header'];
      
      if (this.type !== 'default') {
        classes.push(`glass-card-with-header--${this.type}`);
      }
      
      if (this.extraClasses) {
        classes.push(this.extraClasses);
      }
      
      return classes.join(' ');
    }
  }
}
</script>

<style>
/* 直接覆盖Element Plus卡片样式，与DevToolsView完全一致 */
.glass-card-with-header {
  background: var(--white-alpha-15) !important;
  backdrop-filter: var(--blur-xl) !important;
  border: 1px solid var(--white-alpha-20) !important;
  border-radius: var(--radius-xl) !important;
  box-shadow: var(--shadow-lg) !important;
  transition: var(--transition-slow) !important;
  overflow: hidden !important;
  position: relative !important;
}

.glass-card-with-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
  opacity: 0;
  z-index: 1;
  pointer-events: none;
}

.glass-card-with-header:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.3);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 16px;
  color: #1a202c;
  background: rgba(255, 255, 255, 0.8);
  margin: -20px -20px 20px -20px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;
}

.card-icon {
  font-size: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Element Plus样式覆盖 */
.glass-card-with-header :deep(.el-card__body) {
  padding: 20px;
}
</style>
