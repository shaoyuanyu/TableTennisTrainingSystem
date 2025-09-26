<template>
    <div class="skeleton" :class="[`shape-${shape}`, `size-${size}`]">
        <div class="shimmer" />
    </div>
</template>

<script setup>
defineOptions({ name: 'GlassSkeleton' })
defineProps({
    shape: { type: String, default: 'rect', validator: (v) => ['rect', 'circle'].includes(v) },
    size: { type: String, default: 'md', validator: (v) => ['sm', 'md', 'lg'].includes(v) },
})
</script>

<style scoped>
.skeleton {
    background: var(--white-alpha-15);
    border: 1px solid var(--white-alpha-20);
    border-radius: var(--radius-md);
    position: relative;
    overflow: hidden;
}

.shape-circle {
    border-radius: 999px;
}

.size-sm {
    height: 12px;
}

.size-md {
    height: 16px;
}

.size-lg {
    height: 24px;
}

.shimmer {
    position: absolute;
    inset: 0;
}

.shimmer::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, var(--white-alpha-30), transparent);
    animation: shimmer 2s ease-in-out infinite;
}
</style>
