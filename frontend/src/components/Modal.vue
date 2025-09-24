<template>
    <el-dialog v-model="model" :title="title" :width="width" :close-on-click-modal="closeOnClickModal" append-to-body
        class="glass-modal">
        <div class="modal-body">
            <slot />
        </div>
        <template #footer>
            <div class="modal-footer">
                <slot name="footer" />
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import {computed} from 'vue'

defineOptions({ name: 'GlassModal' })

const props = defineProps({
    modelValue: { type: Boolean, default: false },
    title: { type: String, default: '' },
    width: { type: String, default: '520px' },
    closeOnClickModal: { type: Boolean, default: true },
})

const emit = defineEmits(['update:modelValue'])
const model = computed({
    get: () => props.modelValue,
    set: (v) => emit('update:modelValue', v),
})
</script>

<style scoped>
/* 玻璃风格覆盖 */
:deep(.glass-modal .el-dialog__header) {
    margin-right: 0;
}

:deep(.glass-modal .el-dialog) {
    background: var(--surface-3);
    border: var(--border-width-sm) solid var(--border-color);
    backdrop-filter: var(--blur-xl);
    border-radius: var(--radius-xl);
    box-shadow: var(--elevation-4);
}

:deep(.glass-modal .el-dialog__title) {
    color: var(--white-alpha-90);
}

:deep(.glass-modal .el-dialog__headerbtn .el-dialog__close) {
    color: var(--white-alpha-85);
}

.modal-body {
    color: var(--white-alpha-90);
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: var(--spacing-md);
}
</style>
