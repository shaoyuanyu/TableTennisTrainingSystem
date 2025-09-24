<template>
  <div class="system-logs">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>系统日志</h2>
      <div class="header-actions">
        <OutlineButton @click="exportLogs">
          <template #icon-left>
            <el-icon>
              <Download />
            </el-icon>
          </template>
          导出日志
        </OutlineButton>
        <OutlineButton color="danger" @click="clearLogs">
          <template #icon-left>
            <el-icon>
              <Delete />
            </el-icon>
          </template>
          清空日志
        </OutlineButton>
      </div>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchLogs">
        <el-form-item label="日志级别">
          <el-select v-model="filters.level" placeholder="全部级别" clearable>
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="错误" value="error" />
            <el-option label="调试" value="debug" />
          </el-select>
        </el-form-item>

        <el-form-item label="模块">
          <el-select v-model="filters.module" placeholder="全部模块" clearable>
            <el-option label="用户管理" value="user" />
            <el-option label="预约系统" value="booking" />
            <el-option label="支付系统" value="payment" />
            <el-option label="比赛系统" value="tournament" />
            <el-option label="系统管理" value="system" />
          </el-select>
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker v-model="filters.dateRange" type="datetimerange" range-separator="至" start-placeholder="开始时间"
            end-placeholder="结束时间" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>

        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="搜索关键词" style="width: 200px" @keyup.enter="fetchLogs" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchLogs" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon info">
              <el-icon>
                <InfoFilled />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ logStats.info }}</div>
              <div class="stat-label">信息日志</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon>
                <WarningFilled />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ logStats.warning }}</div>
              <div class="stat-label">警告日志</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon error">
              <el-icon>
                <CircleCloseFilled />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ logStats.error }}</div>
              <div class="stat-label">错误日志</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon debug">
              <el-icon>
                <Setting />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ logStats.debug }}</div>
              <div class="stat-label">调试日志</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日志列表 -->
    <el-card>
      <el-table :data="logList" v-loading="loading" stripe>
        <el-table-column prop="timestamp" label="时间" width="180" sortable>
          <template #default="{ row }">
            {{ formatTime(row.timestamp) }}
          </template>
        </el-table-column>

        <el-table-column prop="level" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="module" label="模块" width="100">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ getModuleText(row.module) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="action" label="操作" width="150" />

        <el-table-column prop="user" label="用户" width="120">
          <template #default="{ row }">
            {{ row.user || '系统' }}
          </template>
        </el-table-column>

        <el-table-column prop="ip" label="IP地址" width="120" />

        <el-table-column prop="message" label="详细信息" min-width="250">
          <template #default="{ row }">
            <div class="log-message" :class="{ 'error-message': row.level === 'error' }">
              {{ row.message }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showLogDetail(row)"> 详情 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" :page-sizes="[20, 50, 100, 200]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchLogs" @current-change="fetchLogs" />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="日志详情" width="800px">
      <div v-if="selectedLog">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="时间">
            {{ formatTime(selectedLog.timestamp) }}
          </el-descriptions-item>
          <el-descriptions-item label="级别">
            <el-tag :type="getLevelType(selectedLog.level)">
              {{ getLevelText(selectedLog.level) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="模块">
            {{ getModuleText(selectedLog.module) }}
          </el-descriptions-item>
          <el-descriptions-item label="操作">
            {{ selectedLog.action }}
          </el-descriptions-item>
          <el-descriptions-item label="用户">
            {{ selectedLog.user || '系统' }}
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">
            {{ selectedLog.ip || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="用户代理" :span="2">
            {{ selectedLog.userAgent || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="详细信息" :span="2">
            <div class="log-detail-message">{{ selectedLog.message }}</div>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedLog.stackTrace" label="堆栈信息" :span="2">
            <pre class="stack-trace">{{ selectedLog.stackTrace }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  CircleCloseFilled,
  Delete,
  Download,
  InfoFilled,
  Refresh,
  Search,
  Setting,
  WarningFilled,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import OutlineButton from '@/components/buttons/OutlineButton.vue'

// 数据列表
const logList = ref([])
const selectedLog = ref(null)

// 状态
const loading = ref(false)
const detailDialogVisible = ref(false)

// 筛选器
const filters = reactive({
  level: '',
  module: '',
  dateRange: [],
  keyword: '',
})

// 分页
const pagination = reactive({
  page: 1,
  size: 50,
  total: 0,
})

// 日志统计
const logStats = reactive({
  info: 0,
  warning: 0,
  error: 0,
  debug: 0,
})

// 获取日志列表
const fetchLogs = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      level: filters.level,
      module: filters.module,
      keyword: filters.keyword,
    }

    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startTime = filters.dateRange[0]
      params.endTime = filters.dateRange[1]
    }

    const response = await api.get('/admin/logs', { params })
    logList.value = response.data.list || []
    pagination.total = response.data.total || 0

    // 获取统计信息
    const statsResponse = await api.get('/admin/logs/stats', { params })
    Object.assign(logStats, statsResponse.data || {})
  } catch {
    ElMessage.error('获取日志失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    level: '',
    module: '',
    dateRange: [],
    keyword: '',
  })
  fetchLogs()
}

// 显示日志详情
const showLogDetail = (log) => {
  selectedLog.value = log
  detailDialogVisible.value = true
}

// 导出日志
const exportLogs = async () => {
  try {
    const params = {
      level: filters.level,
      module: filters.module,
      keyword: filters.keyword,
    }

    if (filters.dateRange && filters.dateRange.length === 2) {
      params.startTime = filters.dateRange[0]
      params.endTime = filters.dateRange[1]
    }

    const response = await api.get('/admin/logs/export', {
      params,
      responseType: 'blob',
    })

    const blob = new Blob([response.data], { type: 'text/csv' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `system_logs_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.csv`
    link.click()
    URL.revokeObjectURL(url)

    ElMessage.success('日志导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

// 清空日志
const clearLogs = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有日志吗？此操作不可恢复！', '确认清空', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await api.delete('/admin/logs')
    ElMessage.success('日志清空成功')
    fetchLogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 格式化时间
const formatTime = (timestamp) => {
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
}

// 获取级别类型
const getLevelType = (level) => {
  const types = {
    info: '',
    warning: 'warning',
    error: 'danger',
    debug: 'info',
  }
  return types[level] || ''
}

// 获取级别文本
const getLevelText = (level) => {
  const texts = {
    info: '信息',
    warning: '警告',
    error: '错误',
    debug: '调试',
  }
  return texts[level] || level
}

// 获取模块文本
const getModuleText = (module) => {
  const texts = {
    user: '用户管理',
    booking: '预约系统',
    payment: '支付系统',
    tournament: '比赛系统',
    system: '系统管理',
  }
  return texts[module] || module
}

// 组件挂载
onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.system-logs {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.info {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.error {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.debug {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-text {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.log-message {
  word-break: break-all;
  line-height: 1.4;
}

.log-message.error-message {
  color: #f56c6c;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.log-detail-message {
  white-space: pre-wrap;
  word-break: break-all;
  line-height: 1.5;
  max-height: 200px;
  overflow-y: auto;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.stack-trace {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.4;
  max-height: 300px;
  overflow-y: auto;
  color: #666;
}

:deep(.el-table .el-button) {
  margin: 0;
}
</style>
