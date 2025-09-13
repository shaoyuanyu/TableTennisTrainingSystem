<template>
  <div class="action-buttons" :class="{ [`action-buttons--${layout}`]: layout !== 'horizontal' }">
    <BaseButton
      v-for="(action, index) in actions"
      :key="index"
      :label="action.text"
      :color="action.type || 'primary'"
      :size="normalizeSize(action.size || 'small')"
      :loading="action.loading"
      :disabled="action.disabled"
      :variant="action.variant || 'solid'"
      :extra-classes="action.extraClasses"
      @click="handleActionClick(action, index)"
    >
      <template v-if="action.icon" #icon-left>
        <component :is="action.icon" />
      </template>
    </BaseButton>
  </div>
</template>

<script>
import BaseButton from './buttons/BaseButton.vue'
export default {
  name: 'ActionButtons',
  components: { BaseButton },
  props: {
    // 操作按钮数组
    // 格式: [{ text: '按钮文字', type: 'primary|success|warning|danger|info', icon?: 'icon-name', loading?: boolean, disabled?: boolean }]
    actions: {
      type: Array,
      default: () => [],
    },
    // 布局方式
    layout: {
      type: String,
      default: 'horizontal', // horizontal, vertical, grid
      validator: (value) => ['horizontal', 'vertical', 'grid'].includes(value),
    },
    // 网格列数（仅在 layout 为 grid 时有效）
    columns: {
      type: Number,
      default: 2,
    },
  },
  emits: ['actionClick'],
  methods: {
    normalizeSize(size) {
      const map = { small: 'sm', medium: 'md', large: 'lg' }
      return map[size] || size
    },
    handleActionClick(action, index) {
      if (!action.disabled && !action.loading) {
        this.$emit('actionClick', { action, index })
      }
    },
  },
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin: 0;
  /* 确保与DevToolsView一致 */
  align-items: stretch;
}

.action-buttons--vertical {
  flex-direction: column;
  align-items: stretch;
}

.action-buttons--grid {
  display: grid;
  grid-template-columns: repeat(v-bind(columns), 1fr);
  gap: 12px;
  align-items: stretch;
}

/* 适配 BaseButton 的容器布局 */
.action-buttons :deep(.btn-modern) {
  border-radius: 12px;
  font-weight: 600;
  min-width: 80px;
  height: 36px;
}

/* 响应式调整 - 与DevToolsView一致 */
@media (max-width: 1200px) {
  .action-buttons,
  .action-buttons--grid {
    gap: 10px;
  }

  :deep(.btn-modern) {
    min-width: 70px;
    padding: 8px 12px !important;
    font-size: 12px !important;
    height: 34px !important;
  }
}

@media (max-width: 768px) {
  .action-buttons,
  .action-buttons--grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  :deep(.btn-modern) {
    min-width: unset;
    padding: 8px 12px !important;
    font-size: 11px !important;
    height: 32px !important;
  }

  :deep(.btn-small) {
    padding: 6px 10px !important;
    font-size: 10px !important;
    height: 28px !important;
  }
}

@media (max-width: 480px) {
  .action-buttons,
  .action-buttons--grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }

  .action-buttons--vertical {
    gap: 8px;
  }
}
</style>
