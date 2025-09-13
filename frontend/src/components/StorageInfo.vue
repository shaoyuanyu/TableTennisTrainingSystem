<template>
  <div class="storage-info">
    <div 
      v-for="(item, index) in items" 
      :key="index"
      class="storage-item"
    >
      <label>{{ item.label }}:</label>
      
      <!-- 状态标签类型 -->
      <el-tag 
        v-if="item.type === 'status'"
        :type="item.status ? 'success' : 'info'" 
        :size="item.size || 'small'"
      >
        {{ item.status ? (item.trueText || '已设置') : (item.falseText || '未设置') }}
      </el-tag>
      
      <!-- 文本域类型 -->
      <el-input
        v-else-if="item.type === 'textarea'"
        :model-value="item.value"
        size="small"
        :placeholder="item.placeholder || ''"
        type="textarea"
        :rows="item.rows || 3"
        readonly
        class="enhanced-textarea"
      />
      
      <!-- 普通文本 -->
      <span v-else>{{ item.value || '无' }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StorageInfo',
  props: {
    // 存储项数组
    // 格式: [{ label: '标签', value: '值', type: 'text|status|textarea', placeholder?: '提示', rows?: 行数 }]
    items: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style scoped>
.storage-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0;
}

.storage-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.storage-item:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.2);
}

.storage-item label {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
  margin: 0 0 8px 0;
  display: block;
}

/* 状态标签样式 */
.storage-item :deep(.el-tag) {
  align-self: flex-start;
  background: linear-gradient(135deg, 
    rgba(64, 158, 255, 0.9) 0%, 
    rgba(102, 126, 234, 0.9) 100%);
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
}

.storage-item :deep(.el-tag--success) {
  background: linear-gradient(135deg, 
    rgba(16, 185, 129, 0.9) 0%, 
    rgba(5, 150, 105, 0.9) 100%);
  border-color: rgba(16, 185, 129, 0.8);
  box-shadow: 
    0 2px 8px rgba(16, 185, 129, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.storage-item :deep(.el-tag--info) {
  background: linear-gradient(135deg, 
    rgba(99, 102, 241, 0.9) 0%, 
    rgba(79, 70, 229, 0.9) 100%);
  border-color: rgba(99, 102, 241, 0.8);
  box-shadow: 
    0 2px 8px rgba(99, 102, 241, 0.25),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

/* 文本域增强样式 - 与DevToolsView保持一致 */
.enhanced-textarea :deep(.el-textarea__inner) {
  background: rgba(255, 255, 255, 0.08) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
  border-radius: 8px !important;
  line-height: 1.5 !important;
  resize: vertical !important;
  font-family: 'Courier New', monospace !important;
  font-size: 13px !important;
  transition: all 0.3s ease !important;
  word-wrap: break-word;
  word-break: break-all;
}

.enhanced-textarea :deep(.el-textarea__inner):hover {
  border-color: rgba(255, 255, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.12) !important;
}

.enhanced-textarea :deep(.el-textarea__inner):focus {
  border-color: #409eff !important;
  background: rgba(255, 255, 255, 0.15) !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2) !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .storage-item {
    padding: 14px;
  }
  
  .storage-item label {
    font-size: 13px;
  }
  
  .enhanced-textarea :deep(.el-textarea__inner) {
    font-size: 12px !important;
  }
}

@media (max-width: 480px) {
  .storage-item {
    padding: 12px;
  }
  
  .enhanced-textarea :deep(.el-textarea__inner) {
    font-size: 11px !important;
  }
}
</style>
