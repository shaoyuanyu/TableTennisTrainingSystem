<template>
  <div class="debug-cards" :class="{ [`debug-cards--${layout}`]: layout !== 'auto' }">
    <slot></slot>
  </div>
</template>

<script>
export default {
  name: 'DebugCardsContainer',
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
.debug-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(v-bind(minWidth), 1fr));
  gap: v-bind(gap);
}

.debug-cards--fixed-2 {
  grid-template-columns: repeat(2, 1fr);
}

.debug-cards--fixed-3 {
  grid-template-columns: repeat(3, 1fr);
}

.debug-cards--fixed-4 {
  grid-template-columns: repeat(4, 1fr);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .debug-cards--fixed-4 {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 968px) {
  .debug-cards--fixed-3,
  .debug-cards--fixed-4 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .debug-cards,
  .debug-cards--fixed-2,
  .debug-cards--fixed-3,
  .debug-cards--fixed-4 {
    grid-template-columns: 1fr;
  }
}
</style>
