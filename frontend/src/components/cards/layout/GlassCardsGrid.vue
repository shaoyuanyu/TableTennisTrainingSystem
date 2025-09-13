<template>
  <div class="glass-cards-grid" :class="gridClasses" :style="gridStyles">
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
      default: 'auto', // auto, fixed-2, fixed-3, fixed-4, masonry
      validator: (value) => ['auto', 'fixed-2', 'fixed-3', 'fixed-4', 'masonry'].includes(value),
    },
    // 最小卡片宽度（仅在 auto 布局下有效）
    minWidth: {
      type: String,
      default: '320px',
    },
    // 最大卡片宽度（仅在 auto 布局下有效）
    maxWidth: {
      type: String,
      default: '1fr',
    },
    // 间距大小
    gap: {
      type: String,
      default: 'var(--spacing-2xl)',
    },
    // 对齐方式
    align: {
      type: String,
      default: 'stretch', // stretch, start, center, end
      validator: (value) => ['stretch', 'start', 'center', 'end'].includes(value),
    },
    // 自定义列数（仅在 custom 布局下有效）
    columns: {
      type: Number,
      default: 3,
    },
  },
  computed: {
    gridClasses() {
      const classes = []

      if (this.layout !== 'auto') {
        classes.push(`glass-cards-grid--${this.layout}`)
      }

      if (this.align !== 'stretch') {
        classes.push(`glass-cards-grid--align-${this.align}`)
      }

      return classes.join(' ')
    },
    gridStyles() {
      const styles = {
        gap: this.gap,
      }

      if (this.layout === 'auto') {
        styles.gridTemplateColumns = `repeat(auto-fit, minmax(${this.minWidth}, ${this.maxWidth}))`
      } else if (this.layout === 'custom') {
        styles.gridTemplateColumns = `repeat(${this.columns}, 1fr)`
      }

      return styles
    },
  },
}
</script>

<style scoped>
.glass-cards-grid {
  display: grid;
  align-items: v-bind(align);
}

/* 固定列数布局 */
.glass-cards-grid--fixed-2 {
  grid-template-columns: repeat(2, 1fr);
}

.glass-cards-grid--fixed-3 {
  grid-template-columns: repeat(3, 1fr);
}

.glass-cards-grid--fixed-4 {
  grid-template-columns: repeat(4, 1fr);
}

/* 瀑布流布局 */
.glass-cards-grid--masonry {
  display: block;
  column-count: 3;
  column-gap: v-bind(gap);
}

.glass-cards-grid--masonry :deep(.glass-card-base) {
  break-inside: avoid;
  margin-bottom: v-bind(gap);
}

/* 对齐方式 */
.glass-cards-grid--align-start {
  align-items: start;
}

.glass-cards-grid--align-center {
  align-items: center;
}

.glass-cards-grid--align-end {
  align-items: end;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .glass-cards-grid--fixed-4 {
    grid-template-columns: repeat(3, 1fr);
  }

  .glass-cards-grid--masonry {
    column-count: 3;
  }
}

@media (max-width: 1200px) {
  .glass-cards-grid--fixed-3,
  .glass-cards-grid--fixed-4 {
    grid-template-columns: repeat(2, 1fr);
  }

  .glass-cards-grid--masonry {
    column-count: 2;
  }
}

@media (max-width: 768px) {
  .glass-cards-grid,
  .glass-cards-grid--fixed-2,
  .glass-cards-grid--fixed-3,
  .glass-cards-grid--fixed-4 {
    grid-template-columns: 1fr;
  }

  .glass-cards-grid--masonry {
    column-count: 1;
  }
}

@media (max-width: 480px) {
  .glass-cards-grid {
    gap: var(--spacing-lg);
  }
}
</style>
