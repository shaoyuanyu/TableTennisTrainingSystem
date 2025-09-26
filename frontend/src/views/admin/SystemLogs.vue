<template>
  <div class="system-logs">
    <!-- 页面头部 -->
    <PageHeader title="系统日志" />

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchLogs">
        <el-form-item label="日期">
          <el-date-picker
            v-model="filters.date"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchLogs" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志列表 -->
    <el-card>
      <el-table :data="logList" v-loading="loading" stripe>
        <el-table-column label="日志内容" min-width="300">
          <template #default="{ row }">
            <div class="log-line">{{ row }}</div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.totalElements"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import api from '@/utils/api'
import PageHeader from '@/components/PageHeader.vue'

// 数据列表
const logList = ref([])

// 状态
const loading = ref(false)

// 筛选器
const filters = reactive({
  date: '',
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  totalPages: 0,
  totalElements: 0,
  date: null
})

// 获取日志列表
const fetchLogs = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    // 如果选择了日期，则添加日期参数
    if (filters.date) {
      params.date = filters.date
    }

    const response = await api.get('/logs', { params })

    const data = response.data
    logList.value = data.content || []
    pagination.totalPages = data.totalPages || 0
    pagination.totalElements = data.totalElements || 0
    pagination.date = data.date || null
  } catch (error) {
    console.error('获取日志失败:', error)
    ElMessage.error('获取日志失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  filters.date = ''
  pagination.page = 1
  fetchLogs()
}

// 处理页面大小变化
const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  fetchLogs()
}

// 处理页码变化
const handlePageChange = (newPage) => {
  pagination.page = newPage
  fetchLogs()
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

.filter-card {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.log-line {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-all;
  line-height: 1.4;
}
</style>