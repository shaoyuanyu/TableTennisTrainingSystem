<template>
    <div class="card-showcase">
        <div class="showcase-header">
            <h2 class="showcase-title">{{ title }}</h2>
            <p v-if="description" class="showcase-description">{{ description }}</p>
        </div>

        <GlassCardsGrid :layout="layout" :gap="gap">
            <slot></slot>
        </GlassCardsGrid>
    </div>
</template>

<script>
import GlassCardsGrid from '../layout/GlassCardsGrid.vue'

export default {
    name: 'CardShowcase',
    components: {
        GlassCardsGrid
    },
    props: {
        // 展示标题
        title: {
            type: String,
            required: true
        },
        // 展示描述
        description: {
            type: String,
            default: ''
        },
        // 网格布局
        layout: {
            type: String,
            default: 'auto',
            validator: value => ['auto', 'fixed-2', 'fixed-3', 'fixed-4', 'masonry'].includes(value)
        },
        // 卡片间距
        gap: {
            type: String,
            default: 'var(--spacing-2xl)'
        }
    }
}
</script>

<style scoped>
.card-showcase {
    padding: var(--spacing-3xl) var(--spacing-2xl);
    max-width: 1400px;
    margin: 0 auto;
}

.showcase-header {
    text-align: center;
    margin-bottom: var(--spacing-3xl);
}

.showcase-title {
    color: white;
    font-size: var(--font-size-3xl);
    font-weight: var(--font-weight-bold);
    margin: 0 0 var(--spacing-lg) 0;
    text-shadow: 0 2px 4px var(--black-alpha-30);
    background: linear-gradient(135deg, #ffffff, #e2e8f0);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.showcase-description {
    color: var(--white-alpha-85);
    font-size: var(--font-size-lg);
    line-height: 1.6;
    margin: 0;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .card-showcase {
        padding: var(--spacing-2xl) var(--spacing-lg);
    }

    .showcase-title {
        font-size: var(--font-size-2xl);
    }

    .showcase-description {
        font-size: var(--font-size-md);
    }
}

@media (max-width: 480px) {
    .card-showcase {
        padding: var(--spacing-xl) var(--spacing-md);
    }

    .showcase-header {
        margin-bottom: var(--spacing-2xl);
    }
}
</style>
