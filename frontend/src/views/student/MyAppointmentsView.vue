<template>
  <div class="my-appointments">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>我的预约</h2>
      <p>查看和管理您的课程预约</p>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchAppointments">
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="CONFIRMED" />
            <el-option label="已拒绝" value="CANCELLED" />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker 
            v-model="filters.dateRange" 
            type="daterange" 
            range-separator="至" 
            start-placeholder="开始日期"
            end-placeholder="结束日期" 
            format="YYYY-MM-DD" 
            value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchAppointments"> 搜索 </el-button>
          <el-button @click="resetFilters"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预约列表 -->
    <el-card>
      <el-table 
        :data="appointments" 
        v-loading="loading"
        stripe
        style="width: 100%">
        <el-table-column prop="id" label="课程ID" width="80" />
        
        <el-table-column prop="title" label="课程标题" width="150" />
        
        <el-table-column label="教练信息" width="180">
          <template #default="{ row }">
            <div class="coach-info">
              <el-avatar :size="32" :src="row.coach?.avatar || ''">
                {{ row.coach?.name ? row.coach?.name.charAt(0) : '?' }}
              </el-avatar>
              <div class="coach-details">
                <div class="coach-name">{{ row.coach?.name || '未知教练' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ row.date }} {{ row.startTime }} - {{ row.endTime }}
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="费用(¥)" width="100">
          <template #default="{ row }">
            {{ row.price }}
          </template>
        </el-table-column>
        
        <el-table-column label="球桌号" width="100">
          <template #default="{ row }">
            <span v-if="row.tableIndex">{{ row.tableIndex }}</span>
            <span v-else>未分配</span>
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="showDetailDialog(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" v-if="pagination.total > 0">
        <el-pagination 
          v-model:current-page="pagination.page" 
          v-model:page-size="pagination.size"
          :total="pagination.total" 
          :page-sizes="[10, 20, 50, 100]" 
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchAppointments" 
          @current-change="fetchAppointments" />
      </div>
      
      <el-empty v-else description="暂无预约记录" />
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="课程详情" width="600px">
      <div v-if="selectedAppointment">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程ID">
            {{ selectedAppointment.id }}
          </el-descriptions-item>
          
          <el-descriptions-item label="课程标题">
            {{ selectedAppointment.title }}
          </el-descriptions-item>
          
          <el-descriptions-item label="教练姓名">
            {{ selectedAppointment.coach?.name || '未知教练' }}
          </el-descriptions-item>
          
          <el-descriptions-item label="预约日期">
            {{ selectedAppointment.date }}
          </el-descriptions-item>
          
          <el-descriptions-item label="上课时间">
            {{ selectedAppointment.startTime }} - {{ selectedAppointment.endTime }}
          </el-descriptions-item>
          
          <el-descriptions-item label="课程费用">
            ¥{{ selectedAppointment.price }}
          </el-descriptions-item>
          
          <el-descriptions-item label="球桌号">
            <span v-if="selectedAppointment.tableIndex">{{ selectedAppointment.tableIndex }}</span>
            <span v-else>未分配</span>
          </el-descriptions-item>
          
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedAppointment.status)">
              {{ getStatusText(selectedAppointment.status) }}
            </el-tag>
          </el-descriptions-item>
          
          <el-descriptions-item label="校区">
            {{ selectedAppointment.campusName || '未知校区' }}
          </el-descriptions-item>
          
          <el-descriptions-item label="特殊要求" :span="2">
            {{ selectedAppointment.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <OutlineButton @click="detailDialogVisible = false">关闭</OutlineButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyAppointments } from '@/api/courses'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import dayjs from 'dayjs'

// 数据列表
const appointments = ref([])
const selectedAppointment = ref(null)
const detailDialogVisible = ref(false)

// 状态
const loading = ref(false)

// 筛选器
const filters = reactive({
  status: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 获取我的预约列表
const fetchAppointments = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    // 添加状态筛选条件
    if (filters.status) {
      params.status = filters.status
    }
    
    // 添加日期筛选条件
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.dateFrom = filters.dateRange[0]
      params.dateTo = filters.dateRange[1]
    }

    const data = await getMyAppointments(params)
    appointments.value = data || []
    pagination.total = appointments.value.length
  } catch (error) {
    console.error('获取预约列表失败:', error)
    ElMessage.error('获取预约列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  filters.status = ''
  filters.dateRange = []
  fetchAppointments()
}

// 显示详情对话框
const showDetailDialog = (appointment) => {
  selectedAppointment.value = appointment
  detailDialogVisible.value = true
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    CONFIRMED: 'success',
    CANCELLED: 'danger'
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待审核',
    CONFIRMED: '审核通过',
    CANCELLED: '已拒绝'
  }
  return texts[status] || status
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchAppointments()
})
</script>

<style scoped>
.my-appointments {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.filter-card {
  margin-bottom: 20px;
}

.coach-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.coach-details {
  flex: 1;
}

.coach-name {
  font-weight: 500;
  color: #333;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-table .el-button) {
  margin-right: 8px;
}

:deep(.el-table .el-button:last-child) {
  margin-right: 0;
}

:deep(.el-descriptions-item__label) {
  font-weight: 500;
}
</style>