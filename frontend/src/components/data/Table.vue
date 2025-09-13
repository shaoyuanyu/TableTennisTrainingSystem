<template>
    <div class="glass-card">
        <div v-if="$slots.header || title" class="table-header">
            <h3 v-if="title" class="title">{{ title }}</h3>
            <slot name="header" />
        </div>

        <div class="table-wrapper" :class="[`density-${density}`]">
            <el-table v-loading="loading" :data="data" :border="border" :stripe="stripe" :size="size" v-bind="$attrs"
                class="glass-el-table" style="width: 100%">
                <slot />
            </el-table>

            <div v-if="!loading && (!data || data.length === 0)" class="table-empty">
                <GlassEmpty :title="emptyTitle" :description="emptyDescription" />
            </div>

            <div v-if="loading" class="table-skeleton">
                <GlassSkeleton v-for="i in 3" :key="i" size="lg" class="mb-sm" />
            </div>
        </div>

        <div v-if="$slots.footer" class="table-footer">
            <slot name="footer" />
        </div>
    </div>
</template>

<script setup>
defineOptions({ name: 'GlassTable', inheritAttrs: false })
import GlassEmpty from '@/components/feedback/Empty.vue'
import GlassSkeleton from '@/components/feedback/Skeleton.vue'

defineProps({
    title: String,
    data: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false },
    emptyTitle: { type: String, default: '暂无数据' },
    emptyDescription: { type: String, default: '' },
    density: { type: String, default: 'md', validator: (v) => ['sm', 'md', 'lg'].includes(v) },
    border: { type: Boolean, default: false },
    stripe: { type: Boolean, default: true },
    size: { type: String, default: 'default' },
})
</script>

<style scoped>
.table-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: var(--spacing-md);
}

.title {
    margin: 0;
    color: var(--white-alpha-90);
}

.table-wrapper :deep(.el-table) {
    background: transparent;
}

.table-wrapper :deep(.el-table__inner-wrapper::before) {
    display: none;
}

.table-wrapper :deep(.el-table__cell) {
    background: transparent;
    color: var(--white-alpha-90);
}

.table-wrapper :deep(.el-table__row:hover .el-table__cell) {
    background: var(--white-alpha-10);
}

.density-sm :deep(.el-table__cell) {
    padding: 6px 8px;
}

.density-md :deep(.el-table__cell) {
    padding: 10px 12px;
}

.density-lg :deep(.el-table__cell) {
    padding: 14px 16px;
}

.table-empty {
    padding: var(--spacing-xl) 0;
}

.table-skeleton {
    padding: var(--spacing-md) 0;
}
</style>
