<template>
  <div class="action-buttons" :class="{ [`action-buttons--${layout}`]: layout !== 'horizontal' }">
    <el-button
      v-for="(action, index) in actions"
      :key="index"
      :type="action.type || 'primary'"
      :size="action.size || 'small'"
      :loading="action.loading"
      :disabled="action.disabled"
      :icon="action.iconComponent"
      @click="handleActionClick(action, index)"
      :class="action.extraClasses"
    >
      <el-icon v-if="action.icon && !action.iconComponent">
        <component :is="action.icon" />
      </el-icon>
      {{ action.text }}
    </el-button>
  </div>
</template>

<script>
export default {
  name: 'ActionButtons',
  props: {
    // 操作按钮数组
    // 格式: [{ text: '按钮文字', type: 'primary|success|warning|danger|info', icon?: 'icon-name', loading?: boolean, disabled?: boolean }]
    actions: {
      type: Array,
      default: () => []
    },
    // 布局方式
    layout: {
      type: String,
      default: 'horizontal', // horizontal, vertical, grid
      validator: value => ['horizontal', 'vertical', 'grid'].includes(value)
    },
    // 网格列数（仅在 layout 为 grid 时有效）
    columns: {
      type: Number,
      default: 2
    }
  },
  emits: ['actionClick'],
  methods: {
    handleActionClick(action, index) {
      if (!action.disabled && !action.loading) {
        this.$emit('actionClick', { action, index });
      }
    }
  }
}
</script>

<style scoped>
.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin: 0; /* 确保与DevToolsView一致 */
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

/* Element Plus 按钮增强样式 - 与DevToolsView保持完全一致 */
:deep(.el-button) {
  border-radius: 12px !important;
  font-weight: 600 !important;
  padding: 10px 16px !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative !important;
  overflow: hidden !important;
  font-size: 13px !important;
  min-width: 80px;
  white-space: nowrap;
  height: 36px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-sizing: border-box !important;
  flex: 1;
}

:deep(.el-button[size="large"]) {
  padding: 12px 20px !important;
  font-size: 14px !important;
  min-width: 100px;
  height: 40px !important;
}

:deep(.el-button[size="small"]) {
  padding: 8px 12px !important;
  font-size: 12px !important;
  min-width: 70px;
  height: 32px !important;
}

:deep(.el-button .el-icon) {
  margin-right: 4px !important;
  font-size: 14px !important;
  position: relative;
  z-index: 2;
}

:deep(.el-button::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s;
  z-index: 1;
}

:deep(.el-button:hover::before) {
  left: 100%;
}

:deep(.el-button span) {
  position: relative;
  z-index: 2;
  line-height: 1;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  color: white !important;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4) !important;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.5) !important;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #48bb78, #38a169) !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(72, 187, 120, 0.3) !important;
}

:deep(.el-button--success:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(72, 187, 120, 0.5) !important;
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #ed8936, #dd6b20) !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(237, 137, 54, 0.3) !important;
}

:deep(.el-button--warning:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(237, 137, 54, 0.5) !important;
}

:deep(.el-button--info) {
  background: linear-gradient(135deg, #4299e1, #3182ce) !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(66, 153, 225, 0.3) !important;
}

:deep(.el-button--info:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(66, 153, 225, 0.5) !important;
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #f56565, #e53e3e) !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(245, 101, 101, 0.3) !important;
}

:deep(.el-button--danger:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(245, 101, 101, 0.5) !important;
}

:deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #2d3748 !important;
  border: 2px solid rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(10px) !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1) !important;
}

:deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.3) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
  border-color: rgba(255, 255, 255, 0.4) !important;
}

:deep(.el-button:disabled) {
  opacity: 0.6 !important;
  transform: none !important;
  cursor: not-allowed !important;
  box-shadow: none !important;
}

:deep(.el-button.is-loading) {
  pointer-events: none !important;
}

/* 响应式调整 - 与DevToolsView一致 */
@media (max-width: 1200px) {
  .action-buttons,
  .action-buttons--grid {
    gap: 10px;
  }
  
  :deep(.el-button) {
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
  
  :deep(.el-button) {
    min-width: unset;
    padding: 8px 12px !important;
    font-size: 11px !important;
    height: 32px !important;
  }
  
  :deep(.el-button[size="small"]) {
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
