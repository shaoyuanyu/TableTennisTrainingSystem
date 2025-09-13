<template>
  <div class="form-field" :class="{ 'form-field-error': hasError }">
    <!-- 标签 -->
    <label v-if="label" :for="inputId" class="form-label">
      {{ label }}
      <span v-if="required" class="required-star">*</span>
    </label>

    <!-- 输入框容器 -->
    <div class="input-container">
      <!-- 左侧图标 -->
      <i v-if="iconLeft" :class="iconLeft" class="input-icon input-icon-left"></i>

      <!-- 输入框 -->
      <input
        :id="inputId"
        :type="type"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :value="modelValue"
        :class="inputClasses"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
        @keyup.enter="handleEnter"
      />

      <!-- 右侧图标 -->
      <i v-if="iconRight" :class="iconRight" class="input-icon input-icon-right"></i>
    </div>

    <!-- 帮助文字或错误信息 -->
    <div v-if="helpText || errorMessage" class="form-help">
      <span v-if="errorMessage" class="error-message">{{ errorMessage }}</span>
      <span v-else-if="helpText" class="help-text">{{ helpText }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FormInput',
  props: {
    // v-model 绑定的值
    modelValue: {
      type: [String, Number],
      default: '',
    },
    // 输入框类型
    type: {
      type: String,
      default: 'text',
    },
    // 标签文字
    label: {
      type: String,
      default: '',
    },
    // 占位符
    placeholder: {
      type: String,
      default: '',
    },
    // 是否必填
    required: {
      type: Boolean,
      default: false,
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false,
    },
    // 是否只读
    readonly: {
      type: Boolean,
      default: false,
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
    // 帮助文字
    helpText: {
      type: String,
      default: '',
    },
    // 错误信息
    errorMessage: {
      type: String,
      default: '',
    },
    // 输入框大小
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    // 额外的CSS类
    extraClasses: {
      type: String,
      default: '',
    },
  },
  emits: ['update:modelValue', 'focus', 'blur', 'enter'],
  computed: {
    inputId() {
      return `input-${Math.random().toString(36).substr(2, 9)}`
    },
    inputClasses() {
      const classes = ['input-glass']

      if (this.size === 'small') classes.push('input-small')
      if (this.size === 'large') classes.push('input-large')

      if (this.iconLeft) classes.push('input-with-icon-left')
      if (this.iconRight) classes.push('input-with-icon-right')

      if (this.extraClasses) classes.push(this.extraClasses)

      return classes.join(' ')
    },
    hasError() {
      return !!this.errorMessage
    },
  },
  methods: {
    handleInput(event) {
      this.$emit('update:modelValue', event.target.value)
    },
    handleFocus(event) {
      this.$emit('focus', event)
    },
    handleBlur(event) {
      this.$emit('blur', event)
    },
    handleEnter(event) {
      this.$emit('enter', event)
    },
  },
}
</script>

<style scoped>
.form-field {
  margin-bottom: var(--spacing-lg);
}

.form-label {
  display: block;
  color: white;
  margin-bottom: var(--spacing-sm);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
}

.required-star {
  color: #ff6b6b;
  margin-left: var(--spacing-xs);
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.input-glass {
  /* 基础样式继承自 design-system.css */
  width: 100%;
}

.input-icon {
  position: absolute;
  color: var(--white-alpha-60);
  font-size: var(--font-size-md);
  z-index: 1;
  pointer-events: none;
}

.input-icon-left {
  left: var(--spacing-md);
}

.input-icon-right {
  right: var(--spacing-md);
}

.input-with-icon-left {
  padding-left: calc(var(--spacing-md) * 2.5);
}

.input-with-icon-right {
  padding-right: calc(var(--spacing-md) * 2.5);
}

.form-help {
  margin-top: var(--spacing-sm);
  font-size: var(--font-size-xs);
  line-height: 1.4;
}

.help-text {
  color: var(--white-alpha-70);
}

.error-message {
  color: #ff6b6b;
  font-weight: var(--font-weight-medium);
}

.form-field-error .input-glass {
  border-color: rgba(255, 107, 107, 0.5);
  box-shadow: 0 0 0 1px rgba(255, 107, 107, 0.3);
}

.form-field-error .input-glass:focus {
  border-color: rgba(255, 107, 107, 0.7);
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.3);
}

/* 输入框大小变体 */
.input-small {
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--font-size-sm);
}

.input-large {
  padding: var(--spacing-lg) var(--spacing-xl);
  font-size: var(--font-size-lg);
}
</style>
