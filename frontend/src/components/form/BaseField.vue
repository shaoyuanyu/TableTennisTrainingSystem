<template>
    <div class="base-field" :class="[
        densityClass,
        { 'is-error': !!error, 'is-disabled': disabled, 'is-required': required }
    ]">
        <label v-if="label" class="field-label">
            <slot name="label">{{ label }}</slot>
            <span v-if="required" class="required">*</span>
            <span v-if="hint" class="hint">{{ hint }}</span>
        </label>

        <div class="field-control">
            <slot />
        </div>

        <p v-if="error" class="field-error">{{ error }}</p>
        <p v-else-if="help" class="field-help">{{ help }}</p>
    </div>
</template>

<script setup>
import {computed} from 'vue'

const props = defineProps({
    label: String,
    required: Boolean,
    disabled: Boolean,
    hint: String,
    help: String,
    error: String,
    density: {
        type: String,
        default: 'md',
        validator: (v) => ['sm', 'md', 'lg'].includes(v)
    }
})

const densityClass = computed(() => `density-${props.density}`)
</script>

<style scoped>
.base-field {
    display: block;
    margin-bottom: var(--spacing-xl);
}

.field-label {
    display: flex;
    align-items: baseline;
    gap: var(--spacing-sm);
    margin-bottom: var(--spacing-sm);
    color: var(--white-alpha-90);
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-medium);
}

.required {
    color: #ff6b6b;
}

.hint {
    margin-left: auto;
    color: var(--white-alpha-80);
    font-size: var(--font-size-xs);
}

.field-control :deep(.input-glass),
.field-control :deep(.el-input),
.field-control :deep(input),
.field-control :deep(textarea),
.field-control :deep(select) {
    width: 100%;
}

.field-error {
    margin-top: var(--spacing-sm);
    color: #ff6b6b;
    font-size: var(--font-size-xs);
    font-weight: var(--font-weight-medium);
}

.field-help {
    margin-top: var(--spacing-sm);
    color: var(--white-alpha-80);
    font-size: var(--font-size-xs);
}

/* 密度控制：影响内部控件 padding 与字体尺寸（通过类选择器让子控件继承） */
.density-sm :deep(.input-glass),
.density-sm :deep(.el-input__inner),
.density-sm :deep(input),
.density-sm :deep(textarea) {
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--font-size-sm);
}

.density-lg :deep(.input-glass),
.density-lg :deep(.el-input__inner),
.density-lg :deep(input),
.density-lg :deep(textarea) {
    padding: var(--spacing-lg) var(--spacing-xl);
    font-size: var(--font-size-lg);
}

/* 状态样式 */
.is-disabled {
    opacity: 0.6;
    pointer-events: none;
}

.is-error :deep(.input-glass),
.is-error :deep(.el-input__wrapper),
.is-error :deep(input),
.is-error :deep(textarea),
.is-error :deep(select) {
    border-color: rgba(255, 107, 107, 0.5);
    box-shadow: 0 0 0 var(--border-width-sm) rgba(255, 107, 107, 0.3);
}

.is-error :deep(.input-glass:focus),
.is-error :deep(input:focus),
.is-error :deep(textarea:focus) {
    border-color: rgba(255, 107, 107, 0.7);
    box-shadow: 0 0 0 var(--focus-ring-width) rgba(255, 107, 107, 0.4);
}
</style>
