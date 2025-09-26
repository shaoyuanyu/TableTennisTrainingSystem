<template>
  <div class="status-grid">
    <div
      v-for="(item, index) in items"
      :key="index"
      class="status-item"
      :class="{ 'status-item--clickable': item.clickable }"
      @click="handleItemClick(item, index)"
    >
      <label>{{ item.label }}:</label>

      <!-- 标签类型值 -->
      <el-tag
        v-if="item.type === 'tag'"
        :type="item.tagType || 'primary'"
        :size="item.size || 'small'"
      >
        {{ item.value }}
      </el-tag>

      <!-- 状态类型值 -->
      <el-tag
        v-else-if="item.type === 'status'"
        :type="item.status ? 'success' : 'danger'"
        :size="item.size || 'small'"
      >
        {{ item.status ? item.trueText || '是' : item.falseText || '否' }}
      </el-tag>

      <!-- 普通文本值 -->
      <span v-else>{{ item.value || '无' }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StatusGrid',
  props: {
    // 状态项数组
    // 格式: [{ label: '标签', value: '值', type: 'text|tag|status', tagType?: 'primary|success|warning|danger|info', status?: boolean }]
    items: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['itemClick'],
  methods: {
    handleItemClick(item, index) {
      if (item.clickable) {
        this.$emit('itemClick', { item, index })
      }
    },
  },
}
</script>

<style scoped>
.status-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0; /* 确保与DevToolsView一致 */
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  min-height: 44px; /* 确保与DevToolsView的.status-item、.test-item高度一致 */
  box-sizing: border-box;
}

.status-item:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
  border-color: rgba(255, 255, 255, 0.2);
}

.status-item--clickable {
  cursor: pointer;
}

.status-item--clickable:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: translateX(6px);
  border-color: rgba(255, 255, 255, 0.25);
}

.status-item label {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
  margin: 0;
  flex: 0 0 auto; /* 防止标签被压缩 */
  min-width: fit-content;
}

/* 值容器 */
.status-item > *:last-child {
  flex: 0 0 auto;
  text-align: right;
}

/* 普通文本样式优化 */
.status-item > span:not(.el-tag) {
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.1);
  padding: 4px 8px;
  border-radius: 6px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  min-width: 60px;
  text-align: center;
}

/* Element Plus 标签增强样式 - 与DevToolsView保持一致 */
:deep(.el-tag) {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.9) 0%, rgba(102, 126, 234, 0.9) 100%);
  color: #ffffff;
  border: 1px solid rgba(64, 158, 255, 0.8);
  box-shadow:
    0 2px 8px rgba(64, 158, 255, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  font-weight: 600;
  font-size: 13px;
  padding: 4px 10px;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  min-width: 60px;
  text-align: center;
}

:deep(.el-tag--success) {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.9) 0%, rgba(5, 150, 105, 0.9) 100%);
  border-color: rgba(16, 185, 129, 0.8);
  box-shadow:
    0 2px 8px rgba(16, 185, 129, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

:deep(.el-tag--warning) {
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.9) 0%, rgba(217, 119, 6, 0.9) 100%);
  border-color: rgba(245, 158, 11, 0.8);
  box-shadow:
    0 2px 8px rgba(245, 158, 11, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

:deep(.el-tag--danger) {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.9) 0%, rgba(220, 38, 127, 0.9) 100%);
  border-color: rgba(239, 68, 68, 0.8);
  box-shadow:
    0 2px 8px rgba(239, 68, 68, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

:deep(.el-tag--info) {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.9) 0%, rgba(79, 70, 229, 0.9) 100%);
  border-color: rgba(99, 102, 241, 0.8);
  box-shadow:
    0 2px 8px rgba(99, 102, 241, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

:deep(.el-tag:hover) {
  transform: translateY(-1px) scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-tag::before) {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.2) 50%,
    transparent 100%
  );
  transition: left 0.4s ease;
}

.status-item:hover :deep(.el-tag::before) {
  left: 100%;
}

/* 小尺寸标签调整 */
:deep(.el-tag--small) {
  font-size: 12px;
  padding: 3px 8px;
  min-width: 50px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .status-item {
    padding: 10px 14px;
    min-height: 40px;
  }

  .status-item label {
    font-size: 13px;
  }

  :deep(.el-tag) {
    font-size: 12px;
    padding: 3px 8px;
    min-width: 50px;
  }
}

@media (max-width: 480px) {
  .status-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
    padding: 12px;
  }

  .status-item > *:last-child {
    align-self: flex-end;
  }
}
</style>
