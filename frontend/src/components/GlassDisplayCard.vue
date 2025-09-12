<template>
  <div :class="cardClasses">
    <!-- 卡片头部 -->
    <div class="glass-display-card-header">
      <div class="glass-display-header-left">
        <div class="glass-display-icon-wrapper">
          <i class="glass-display-icon">{{ icon }}</i>
        </div>
        <div class="glass-display-header-text">
          <h3>{{ title }}</h3>
          <p>{{ subtitle }}</p>
        </div>
      </div>
      <div v-if="$slots.actions" class="glass-display-header-actions">
        <slot name="actions"></slot>
      </div>
    </div>
    
    <!-- 卡片内容 -->
    <div class="glass-display-card-content">
      <p v-if="description">{{ description }}</p>
      <slot></slot>
      
      <!-- 标签区域 -->
      <div v-if="tags && tags.length > 0" class="glass-display-tags">
        <span 
          v-for="tag in tags" 
          :key="tag.text"
          :class="['tag-glass', `tag-${tag.type || 'primary'}`]"
        >
          {{ tag.text }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GlassDisplayCard',
  props: {
    // 卡片标题
    title: {
      type: String,
      required: true
    },
    // 副标题（通常是CSS类名）
    subtitle: {
      type: String,
      default: ''
    },
    // 图标
    icon: {
      type: String,
      default: '🔧'
    },
    // 描述文字
    description: {
      type: String,
      default: ''
    },
    // 标签数组 [{ text: '标签名', type: 'primary|secondary|success|warning|info' }]
    tags: {
      type: Array,
      default: () => []
    },
    // 卡片类型（影响基础样式）
    type: {
      type: String,
      default: 'glass', // glass, content, glass-enhanced
      validator: value => ['glass', 'content', 'glass-enhanced'].includes(value)
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: ''
    }
  },
  computed: {
    cardClasses() {
      const baseClass = this.type === 'glass' ? 'glass-display-card' : 
                       this.type === 'content' ? 'glass-display-card--content' : 'glass-display-card--enhanced';
      
      return [baseClass, this.extraClasses].filter(Boolean).join(' ');
    }
  }
}
</script>

<style scoped>
/* 基础卡片容器样式 */
.glass-display-card,
.glass-display-card--content,
.glass-display-card--enhanced {
  background: var(--white-alpha-15);
  backdrop-filter: var(--blur-xl);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  transition: var(--transition-slow);
  overflow: hidden;
  position: relative;
  padding: var(--spacing-xl);
}

.glass-display-card::before,
.glass-display-card--content::before,
.glass-display-card--enhanced::before {
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

.glass-display-card:hover,
.glass-display-card--content:hover,
.glass-display-card--enhanced:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 内容类型卡片的特殊样式 */
.glass-display-card--content {
  background: var(--white-alpha-20);
  border-color: var(--white-alpha-25);
}

/* 增强类型卡片的特殊样式 */
.glass-display-card--enhanced {
  background: var(--white-alpha-25);
  border-color: var(--white-alpha-30);
  box-shadow: var(--shadow-xl);
}

/* 卡片头部样式 */
.glass-display-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--white-alpha-15);
}

.glass-display-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.glass-display-icon-wrapper {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--white-alpha-25), var(--white-alpha-10));
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  box-shadow: var(--shadow-sm);
}

.glass-display-icon {
  color: white;
  font-size: var(--font-size-lg);
}

.glass-display-header-text h3 {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: white;
  text-shadow: 0 1px 2px var(--black-alpha-30);
}

.glass-display-header-text p {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--white-alpha-80);
  font-family: 'Courier New', monospace;
  background: var(--white-alpha-15);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  display: inline-block;
}

.glass-display-card-content {
  margin-top: var(--spacing-lg);
}

.glass-display-card-content > p {
  color: var(--white-alpha-85);
  font-size: var(--font-size-md);
  line-height: 1.5;
  margin-bottom: var(--spacing-lg);
}

.glass-display-tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  margin-top: var(--spacing-md);
}

.glass-display-header-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-display-header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
  
  .glass-display-card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
}
</style>
