<template>
  <span :class="tagClasses" @click="handleClick">
    <!-- 左侧图标 -->
    <i v-if="iconLeft" :class="iconLeft" class="tag-icon tag-icon-left"></i>

    <!-- 标签文字 -->
    <span class="tag-text">
      <slot>{{ text }}</slot>
    </span>

    <!-- 右侧图标或关闭按钮 -->
    <i v-if="iconRight" :class="iconRight" class="tag-icon tag-icon-right"></i>
    <i v-else-if="closable" class="tag-close" @click.stop="handleClose">×</i>
  </span>
</template>

<script>
export default {
  name: 'TagBadge',
  props: {
    // 标签文字
    text: {
      type: String,
      default: '',
    },
    // 标签类型
    type: {
      type: String,
      default: 'primary',
      validator: (value) =>
        ['primary', 'secondary', 'success', 'warning', 'danger', 'info'].includes(value),
    },
    // 标签大小
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    // 标签变体
    variant: {
      type: String,
      default: 'glass',
      validator: (value) => ['solid', 'outline', 'glass'].includes(value),
    },
    // 左侧图标
    iconLeft: {
      type: String,
      default: '',
    },
    // 右侧图标
    iconRight: {
      type: String,
      default: '',
    },
    // 是否可关闭
    closable: {
      type: Boolean,
      default: false,
    },
    // 是否可点击
    clickable: {
      type: Boolean,
      default: false,
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: '',
    },
  },
  emits: ['click', 'close'],
  computed: {
    tagClasses() {
      const classes = ['tag-glass']

      // 标签类型
      classes.push(`tag-${this.type}`)

      // 标签大小
      if (this.size === 'small') classes.push('tag-small')
      if (this.size === 'large') classes.push('tag-large')

      // 标签变体
      if (this.variant === 'solid') classes.push('tag-solid')
      if (this.variant === 'outline') classes.push('tag-outline')

      // 可点击状态
      if (this.clickable || this.closable) classes.push('tag-clickable')

      // 额外类名
      if (this.extraClasses) classes.push(this.extraClasses)

      return classes.join(' ')
    },
  },
  methods: {
    handleClick(event) {
      if (this.clickable) {
        this.$emit('click', event)
      }
    },
    handleClose(event) {
      this.$emit('close', event)
    },
  },
}
</script>

<style scoped>
/* 基础标签样式继承自 design-system.css 的 .tag-glass */

.tag-icon {
  font-size: 0.8em;
  line-height: 1;
}

.tag-icon-left {
  margin-right: var(--spacing-xs);
}

.tag-icon-right {
  margin-left: var(--spacing-xs);
}

.tag-text {
  display: inline-block;
}

.tag-close {
  margin-left: var(--spacing-xs);
  cursor: pointer;
  font-size: 1.1em;
  line-height: 1;
  font-weight: bold;
  color: inherit;
  opacity: 0.7;
  transition: opacity var(--transition-fast);
}

.tag-close:hover {
  opacity: 1;
}

.tag-clickable {
  cursor: pointer;
  transition: all var(--transition-fast);
}

.tag-clickable:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* 标签大小变体 */
.tag-small {
  padding: calc(var(--spacing-xs) / 2) var(--spacing-xs);
  font-size: var(--font-size-xs);
  border-radius: var(--radius-sm);
}

.tag-large {
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--font-size-md);
  border-radius: var(--radius-lg);
}

/* 标签变体样式 */
.tag-solid {
  background: var(--primary-alpha-80);
  color: white;
  border: none;
}

.tag-solid.tag-secondary {
  background: var(--white-alpha-60);
  color: var(--text-dark);
}

.tag-solid.tag-success {
  background: rgba(103, 194, 58, 0.8);
  color: white;
}

.tag-solid.tag-warning {
  background: rgba(230, 162, 60, 0.8);
  color: white;
}

.tag-solid.tag-danger {
  background: rgba(255, 107, 107, 0.8);
  color: white;
}

.tag-solid.tag-info {
  background: rgba(64, 158, 255, 0.8);
  color: white;
}

.tag-outline {
  background: transparent;
  border: 1px solid var(--primary-alpha-50);
  color: var(--primary-alpha-90);
}

.tag-outline.tag-secondary {
  border-color: var(--white-alpha-50);
  color: var(--white-alpha-90);
}

.tag-outline.tag-success {
  border-color: rgba(103, 194, 58, 0.5);
  color: rgba(103, 194, 58, 0.9);
}

.tag-outline.tag-warning {
  border-color: rgba(230, 162, 60, 0.5);
  color: rgba(230, 162, 60, 0.9);
}

.tag-outline.tag-danger {
  border-color: rgba(255, 107, 107, 0.5);
  color: rgba(255, 107, 107, 0.9);
}

.tag-outline.tag-info {
  border-color: rgba(64, 158, 255, 0.5);
  color: rgba(64, 158, 255, 0.9);
}
</style>
