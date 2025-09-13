<template>
    <div class="empty glass-card" :class="sizeClass">
        <div class="icon" v-if="icon">
            <component :is="icon" />
        </div>
        <p class="title">{{ title }}</p>
        <p v-if="description" class="desc">{{ description }}</p>
        <div class="actions">
            <slot name="actions" />
        </div>
    </div>
</template>

<script setup>
defineOptions({ name: 'GlassEmpty' })
import { computed } from 'vue'

const props = defineProps({
    title: { type: String, default: '暂无数据' },
    description: { type: String, default: '' },
    icon: { type: [Object, Function, String], default: null },
    size: { type: String, default: 'md', validator: (v) => ['sm', 'md', 'lg'].includes(v) },
})

const sizeClass = computed(() => `size-${props.size}`)
</script>

<style scoped>
.empty {
    text-align: center;
}

.icon {
    font-size: 40px;
    margin-bottom: var(--spacing-md);
}

.title {
    font-weight: var(--font-weight-semibold);
    color: var(--white-alpha-90);
}

.desc {
    color: var(--white-alpha-80);
    margin-top: var(--spacing-xs);
}

.actions {
    margin-top: var(--spacing-lg);
}

.size-sm {
    padding: var(--spacing-lg);
}

.size-md {
    padding: var(--spacing-2xl);
}

.size-lg {
    padding: var(--spacing-3xl);
}
</style>
