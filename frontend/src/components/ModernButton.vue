<template>
  <button 
    :class="buttonClasses" 
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <!-- 加载状态图标 -->
    <span v-if="loading" class="loading-spinner"></span>
    
    <!-- 左侧图标 -->
    <i v-if="iconLeft && !loading" :class="iconLeft" class="btn-icon btn-icon-left"></i>
    
    <!-- 按钮文字 -->
    <span class="btn-text">
      <slot>{{ text }}</slot>
    </span>
    
    <!-- 右侧图标 -->
    <i v-if="iconRight && !loading" :class="iconRight" class="btn-icon btn-icon-right"></i>
  </button>
</template>

<script>
export default {
  name: 'ModernButton',
  props: {
    // 按钮文字
    text: {
      type: String,
      default: ''
    },
    // 按钮类型
    type: {
      type: String,
      default: 'primary',
      validator: value => ['primary', 'secondary', 'success', 'warning', 'danger', 'info'].includes(value)
    },
    // 按钮大小
    size: {
      type: String,
      default: 'medium',
      validator: value => ['small', 'medium', 'large'].includes(value)
    },
    // 按钮变体
    variant: {
      type: String,
      default: 'solid',
      validator: value => ['solid', 'outline', 'glass'].includes(value)
    },
    // 左侧图标
    iconLeft: {
      type: String,
      default: ''
    },
    // 右侧图标
    iconRight: {
      type: String,
      default: ''
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否加载中
    loading: {
      type: Boolean,
      default: false
    },
    // 是否块级按钮
    block: {
      type: Boolean,
      default: false
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: ''
    }
  },
  computed: {
    buttonClasses() {
      const classes = ['btn-modern'];
      
      // 按钮类型
      classes.push(`btn-${this.type}`);
      
      // 按钮大小
      if (this.size === 'small') classes.push('btn-small');
      if (this.size === 'large') classes.push('btn-large');
      
      // 按钮变体
      if (this.variant === 'outline') classes.push('btn-outline');
      if (this.variant === 'glass') classes.push('btn-glass');
      
      // 块级按钮
      if (this.block) classes.push('btn-block');
      
      // 加载状态
      if (this.loading) classes.push('btn-loading');
      
      // 额外类名
      if (this.extraClasses) classes.push(this.extraClasses);
      
      return classes.join(' ');
    }
  },
  methods: {
    handleClick(event) {
      if (!this.disabled && !this.loading) {
        this.$emit('click', event);
      }
    }
  }
}
</script>

<style scoped>
/* 基础按钮样式继承自 design-system.css 的 .btn-modern */

.loading-spinner {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: var(--spacing-sm);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.btn-icon {
  font-size: var(--font-size-sm);
  line-height: 1;
}

.btn-icon-left {
  margin-right: var(--spacing-sm);
}

.btn-icon-right {
  margin-left: var(--spacing-sm);
}

.btn-text {
  display: inline-block;
}

/* 块级按钮 */
.btn-block {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 加载状态样式 */
.btn-loading {
  position: relative;
  cursor: wait;
}

.btn-loading .btn-text {
  opacity: 0.7;
}

/* 禁用状态已在 design-system.css 中定义 */
</style>
