<template>
    <div class="toast-container" aria-live="polite" aria-atomic="true">
        <transition-group name="toast-list" tag="div">
            <div v-for="t in toasts" :key="t.id" class="toast-item" :class="`toast-${t.type}`" role="status">
                <span class="toast-message">{{ t.message }}</span>
                <button class="toast-close" @click="remove(t.id)" aria-label="关闭">×</button>
            </div>
        </transition-group>
    </div>
</template>

<script setup>
import { storeToRefs } from 'pinia'
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()
const { toasts } = storeToRefs(toastStore)
const { remove } = toastStore
</script>

<style scoped>
.toast-container {
    position: fixed;
    top: var(--spacing-2xl);
    right: var(--spacing-2xl);
    z-index: var(--z-tooltip);
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);
    pointer-events: none;
    /* 容器不抢事件，单条可点击 */
}

.toast-item {
    pointer-events: auto;
    min-width: 260px;
    max-width: 420px;
    padding: var(--spacing-md) var(--spacing-lg);
    background: var(--surface-3);
    border: var(--border-width-sm) solid var(--border-color);
    backdrop-filter: var(--blur-lg);
    border-radius: var(--radius-lg);
    box-shadow: var(--elevation-3);
    color: var(--white-alpha-90);
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: var(--spacing-lg);
}

.toast-success {
    border-color: rgba(34, 197, 94, 0.35);
    box-shadow: 0 8px 24px rgba(34, 197, 94, 0.25);
}

.toast-info {
    border-color: rgba(59, 130, 246, 0.35);
    box-shadow: 0 8px 24px rgba(59, 130, 246, 0.25);
}

.toast-warning {
    border-color: rgba(245, 158, 11, 0.35);
    box-shadow: 0 8px 24px rgba(245, 158, 11, 0.25);
}

.toast-error {
    border-color: rgba(239, 68, 68, 0.35);
    box-shadow: 0 8px 24px rgba(239, 68, 68, 0.25);
}

.toast-message {
    flex: 1;
}

.toast-close {
    appearance: none;
    border: none;
    background: transparent;
    color: var(--white-alpha-85);
    font-size: 18px;
    cursor: pointer;
}

/* 进出场动画 */
.toast-list-enter-active,
.toast-list-leave-active {
    transition: all .25s ease;
}

.toast-list-enter-from {
    opacity: 0;
    transform: translateY(-6px) scale(.98);
}

.toast-list-enter-to {
    opacity: 1;
    transform: translateY(0) scale(1);
}

.toast-list-leave-from {
    opacity: 1;
    transform: translateY(0) scale(1);
}

.toast-list-leave-to {
    opacity: 0;
    transform: translateY(-6px) scale(.98);
}
</style>
