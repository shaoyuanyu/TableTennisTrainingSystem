<template>
  <div :class="cardClasses" :style="cardStyles">
    <!-- 卡片头部 -->
    <div v-if="hasHeader" class="glass-card-header">
      <div class="glass-card-header-content">
        <!-- 图标 -->
        <div v-if="icon" class="glass-card-icon-wrapper">
          <span class="glass-card-icon">{{ icon }}</span>
        </div>

        <!-- 标题区域 -->
        <div class="glass-card-title-area">
          <h3 v-if="title" class="glass-card-title">{{ title }}</h3>
          <p v-if="subtitle" class="glass-card-subtitle">{{ subtitle }}</p>
        </div>
      </div>

      <!-- 头部操作区域 -->
      <div v-if="$slots.headerActions" class="glass-card-header-actions">
        <slot name="headerActions"></slot>
      </div>
    </div>

    <!-- 卡片内容 -->
    <div class="glass-card-content" :class="{ 'glass-card-content--with-header': hasHeader }">
      <!-- 描述文字 -->
      <p v-if="description" class="glass-card-description">{{ description }}</p>

      <!-- 主要内容 -->
      <slot></slot>

      <!-- 标签区域 -->
      <div v-if="tags && tags.length > 0" class="glass-card-tags">
        <span
          v-for="tag in tags"
          :key="tag.text"
          :class="['tag-glass', `tag-${tag.type || 'primary'}`]"
        >
          {{ tag.text }}
        </span>
      </div>
    </div>

    <!-- 卡片底部 -->
    <div v-if="$slots.footer" class="glass-card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'GlassCardBase',
  props: {
    // 卡片标题
    title: {
      type: String,
      default: '',
    },
    // 副标题
    subtitle: {
      type: String,
      default: '',
    },
    // 图标
    icon: {
      type: String,
      default: '',
    },
    // 描述文字
    description: {
      type: String,
      default: '',
    },
    // 标签数组 [{ text: '标签名', type: 'primary|success|warning|danger|info|secondary' }]
    tags: {
      type: Array,
      default: () => [],
    },
    // 卡片变体类型
    variant: {
      type: String,
      default: 'base', // base, display, content, enhanced, minimal
      validator: (value) => ['base', 'display', 'content', 'enhanced', 'minimal'].includes(value),
    },
    // 尺寸大小
    size: {
      type: String,
      default: 'medium', // small, medium, large
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    // 是否可悬浮
    hoverable: {
      type: Boolean,
      default: true,
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: '',
    },
    // 自定义宽度
    width: {
      type: String,
      default: '',
    },
    // 自定义高度
    height: {
      type: String,
      default: '',
    },
  },
  computed: {
    hasHeader() {
      return !!(this.title || this.subtitle || this.icon || this.$slots.headerActions)
    },
    cardClasses() {
      const classes = [
        'glass-card-base',
        `glass-card-base--${this.variant}`,
        `glass-card-base--${this.size}`,
      ]

      if (this.hoverable) {
        classes.push('glass-card-base--hoverable')
      }

      if (this.extraClasses) {
        classes.push(this.extraClasses)
      }

      return classes.join(' ')
    },
    cardStyles() {
      const styles = {}

      if (this.width) {
        styles.width = this.width
      }

      if (this.height) {
        styles.height = this.height
      }

      return styles
    },
  },
}
</script>

<style scoped>
/* 基础卡片容器 */
.glass-card-base {
  position: relative;
  border-radius: var(--radius-xl);
  backdrop-filter: var(--blur-xl);
  border: 1px solid var(--white-alpha-20);
  box-shadow: var(--shadow-lg);
  transition: var(--transition-normal);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 变体样式 */
.glass-card-base--base {
  background: var(--white-alpha-15);
}

.glass-card-base--display {
  background: var(--white-alpha-18);
  border-color: var(--white-alpha-25);
}

.glass-card-base--content {
  background: var(--white-alpha-20);
  border-color: var(--white-alpha-25);
}

.glass-card-base--enhanced {
  background: var(--white-alpha-25);
  border-color: var(--white-alpha-30);
  box-shadow: var(--shadow-xl);
}

.glass-card-base--minimal {
  background: var(--white-alpha-10);
  border-color: var(--white-alpha-15);
  box-shadow: var(--shadow-sm);
}

/* 尺寸变体 */
.glass-card-base--small {
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
}

.glass-card-base--medium {
  padding: var(--spacing-xl);
}

.glass-card-base--large {
  padding: var(--spacing-2xl);
  border-radius: var(--radius-2xl);
}

/* 悬浮效果 */
.glass-card-base--hoverable:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow:
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 卡片头部 */
.glass-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--white-alpha-15);
}

.glass-card-header-content {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  flex: 1;
}

.glass-card-icon-wrapper {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--white-alpha-25), var(--white-alpha-10));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  box-shadow: var(--shadow-sm);
  flex-shrink: 0;
}

.glass-card-icon {
  font-size: var(--font-size-xl);
  filter: drop-shadow(0 1px 2px var(--black-alpha-30));
}

.glass-card-title-area {
  flex: 1;
  min-width: 0;
}

.glass-card-title {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: white;
  text-shadow: 0 1px 2px var(--black-alpha-30);
  line-height: 1.3;
}

.glass-card-subtitle {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--white-alpha-80);
  font-family: 'Courier New', monospace;
  background: var(--white-alpha-15);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  display: inline-block;
  line-height: 1.4;
}

.glass-card-header-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: flex-start;
  flex-shrink: 0;
}

/* 卡片内容 */
.glass-card-content {
  flex: 1;
}

.glass-card-content--with-header {
  margin-top: 0;
}

.glass-card-description {
  color: var(--white-alpha-85);
  font-size: var(--font-size-md);
  line-height: 1.6;
  margin: 0 0 var(--spacing-lg) 0;
}

/* 标签区域 */
.glass-card-tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  margin-top: var(--spacing-lg);
}

/* 卡片底部 */
.glass-card-footer {
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--white-alpha-15);
}

/* 小尺寸特殊样式 */
.glass-card-base--small .glass-card-icon-wrapper {
  width: 36px;
  height: 36px;
}

.glass-card-base--small .glass-card-icon {
  font-size: var(--font-size-lg);
}

.glass-card-base--small .glass-card-title {
  font-size: var(--font-size-md);
}

.glass-card-base--small .glass-card-subtitle {
  font-size: var(--font-size-xs);
}

/* 大尺寸特殊样式 */
.glass-card-base--large .glass-card-icon-wrapper {
  width: 56px;
  height: 56px;
}

.glass-card-base--large .glass-card-icon {
  font-size: var(--font-size-2xl);
}

.glass-card-base--large .glass-card-title {
  font-size: var(--font-size-xl);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .glass-card-base--medium {
    padding: var(--spacing-lg);
  }

  .glass-card-base--large {
    padding: var(--spacing-xl);
  }

  .glass-card-header {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: flex-start;
  }

  .glass-card-header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .glass-card-header-content {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .glass-card-base--small,
  .glass-card-base--medium,
  .glass-card-base--large {
    padding: var(--spacing-md);
  }

  .glass-card-tags {
    gap: var(--spacing-xs);
  }
}
</style>
