<template>
  <div class="glass-cards-grid" :class="{ [`glass-cards-grid--${layout}`]: layout !== 'auto' }">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'GlassCardsGrid',
  props: {
    // 布局方式
    layout: {
      type: String,
      default: 'auto', // auto, fixed-2, fixed-3, fixed-4
      validator: value => ['auto', 'fixed-2', 'fixed-3', 'fixed-4'].includes(value)
    },
    // 最小卡片宽度（仅在 auto 布局下有效）
    minWidth: {
      type: String,
      default: '320px'
    },
    // 间距大小
    gap: {
      type: String,
      default: 'var(--spacing-2xl)'
    }
  }
}
</script>

<style scoped>
.glass-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(v-bind(minWidth), 1fr));
  gap: v-bind(gap);
}

.glass-cards-grid--fixed-2 {
  grid-template-columns: repeat(2, 1fr);
}

.glass-cards-grid--fixed-3 {
  grid-template-columns: repeat(3, 1fr);
}

.glass-cards-grid--fixed-4 {
  grid-template-columns: repeat(4, 1fr);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .glass-cards-grid--fixed-4 {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 968px) {
  .glass-cards-grid--fixed-3,
  .glass-cards-grid--fixed-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .glass-cards-grid,
  .glass-cards-grid--fixed-2,
  .glass-cards-grid--fixed-3,
  .glass-cards-grid--fixed-4 {
    grid-template-columns: 1fr;
  }
}
</style>
