<template>
    <el-tooltip :content="content" :placement="placement" :effect="effect" :show-after="showAfter"
        :hide-after="hideAfter" :disabled="disabled" :persistent="persistent"
        :popper-class="'glass-tooltip ' + popperClass">
        <slot />
    </el-tooltip>
</template>

<script setup>
defineOptions({ name: 'GlassTooltip' })
defineProps({
    content: { type: String, default: '' },
    placement: { type: String, default: 'top' },
    effect: { type: String, default: 'dark' },
    showAfter: { type: Number, default: 100 },
    hideAfter: { type: Number, default: 0 },
    disabled: { type: Boolean, default: false },
    persistent: { type: Boolean, default: true },
    popperClass: { type: String, default: '' },
})
</script>

<style scoped>
:deep(.glass-tooltip .el-popper) {
    background: var(--surface-3);
    border: var(--border-width-sm) solid var(--border-color);
    backdrop-filter: var(--blur-lg);
    border-radius: var(--radius-md);
    box-shadow: var(--elevation-3);
}

:deep(.glass-tooltip .el-popper__arrow::before) {
    background: var(--surface-3);
    border: var(--border-width-sm) solid var(--border-color);
}
</style>
