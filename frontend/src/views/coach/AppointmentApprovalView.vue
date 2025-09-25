<template>
  <div class="appointment-approval">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>课程审批</h2>
      <p>审核学员的课程预约申请</p>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchAppointments">
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="CONFIRMED" />
            <el-option label="已拒绝" value="CANCELLED" />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker v-model="filters.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="学员搜索">
          <el-input v-model="filters.keyword" placeholder="学员姓名或手机号" style="width: 200px"
            @keyup.enter="fetchAppointments" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchAppointments" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedAppointments.length > 0">
      <span class="selection-info"> 已选择 {{ selectedAppointments.length }} 个预约 </span>
      <el-button type="success" @click="batchApprove" :disabled="!canBatchApprove">
        批量通过
      </el-button>
      <el-button type="danger" @click="batchReject" :disabled="!canBatchReject">
        批量拒绝
      </el-button>
    </div>

    <!-- 课程列表（GlassTable 包裹） -->
    <el-card>
      <GlassTable title="课程列表" :data="appointmentList" :loading="loading" density="md" :stripe="true"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" :selectable="isSelectable" />

        <el-table-column prop="id" label="课程ID" width="100" />

        <el-table-column prop="student" label="学员信息" width="200">
          <template #default="{ row }">
            <div class="student-info">
              <el-avatar :size="32" :src="row.student.avatar">
                {{ row.student.name.charAt(0) }}
              </el-avatar>
              <div class="student-details">
                <div class="student-name">{{ row.student.name }}</div>
                <div class="student-phone">{{ row.student.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="date" label="预约日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>

        <el-table-column label="时间段" width="120">
          <template #default="{ row }"> {{ getStartTime(row.NO) }} - {{ getEndTime(row.NO) }} </template>
        </el-table-column>

        <el-table-column prop="title" label="课程标题" width="150" />

        <el-table-column prop="price" label="费用" width="100">
          <template #default="{ row }"> ¥{{ row.price }} </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" size="small" type="success" @click="approveAppointment(row)">
              通过
            </el-button>
            <el-button v-if="row.status === 'PENDING'" size="small" type="danger" @click="showRejectDialog(row)">
              拒绝
            </el-button>
            <el-button size="small" @click="showDetailDialog(row)"> 详情 </el-button>
          </template>
        </el-table-column>
      </GlassTable>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchAppointments" @current-change="fetchAppointments" />
      </div>
    </el-card>

    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝预约" width="500px" @close="resetRejectForm">
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="100px">
        <el-form-item label="拒绝原因" prop="reason">
          <el-select v-model="rejectForm.reason" placeholder="选择拒绝原因">
            <el-option label="时间冲突" value="time_conflict" />
            <el-option label="个人安排" value="personal_schedule" />
            <el-option label="学员水平不符" value="level_mismatch" />
            <el-option label="课程类型不匹配" value="course_mismatch" />
            <el-option label="其他原因" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="详细说明">
          <el-input v-model="rejectForm.details" type="textarea" :rows="3" placeholder="请详细说明拒绝原因，以便学员理解" />
        </el-form-item>

        <el-form-item label="建议时间">
          <el-input v-model="rejectForm.suggestion" placeholder="可选：建议其他可用时间" />
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="rejectDialogVisible = false">取消</OutlineButton>
        <DangerButton @click="confirmReject" :loading="rejecting"> 确认拒绝 </DangerButton>
      </template>
    </el-dialog>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="课程详情" width="600px">
      <div v-if="selectedAppointment">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程ID">
            {{ selectedAppointment.id }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedAppointment.status)">
              {{ getStatusText(selectedAppointment.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学员姓名">
            {{ selectedAppointment.student?.name }}
          </el-descriptions-item>
          <el-descriptions-item label="学员手机">
            {{ selectedAppointment.student?.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="预约日期">
            {{ formatDate(selectedAppointment.date) }}
          </el-descriptions-item>
          <el-descriptions-item label="时间段">
            {{ getStartTime(selectedAppointment.NO) }} - {{ getEndTime(selectedAppointment.NO) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程标题">
            {{ selectedAppointment.title }}
          </el-descriptions-item>
          <el-descriptions-item label="课程费用">
            ¥{{ selectedAppointment.price }}
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDateTime(selectedAppointment.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="特殊要求" :span="2">
            {{ selectedAppointment.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Refresh, Search} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import DangerButton from '@/components/buttons/DangerButton.vue'
import GlassTable from '@/components/data/Table.vue'

// 数据列表
const appointmentList = ref([])
const selectedAppointments = ref([])
const selectedAppointment = ref(null)

// 状态
const loading = ref(false)
const rejecting = ref(false)
const rejectDialogVisible = ref(false)
const detailDialogVisible = ref(false)

// 筛选器
const filters = reactive({
  status: 'PENDING', // 默认显示待审核
  dateRange: [],
  keyword: '',
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
})

// 拒绝表单
const rejectForm = reactive({
  reason: '',
  details: '',
  suggestion: '',
})

const rejectFormRef = ref()

// 表单验证规则
const rejectRules = {
  reason: [{ required: true, message: '请选择拒绝原因', trigger: 'change' }],
}

// 计算属性
const canBatchApprove = computed(() => {
  return selectedAppointments.value.every((item) => item.status === 'PENDING')
})

const canBatchReject = computed(() => {
  return selectedAppointments.value.every((item) => item.status === 'PENDING')
})

// 获取课程列表
const fetchAppointments = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    // 添加状态筛选
    if (filters.status) {
      params.status = filters.status
    }
    
    // 添加日期范围筛选
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.dateFrom = filters.dateRange[0]
      params.dateTo = filters.dateRange[1]
    }
    
    // 添加关键词搜索
    if (filters.keyword) {
      params.keyword = filters.keyword
    }

    // 获取教练的所有课程
    const response = await api.get('/courses/querypending', { params })
    appointmentList.value = response.data.map(course => {
      // 确保每个课程都有学生信息
      return {
        ...course,
        student: course.student || {
          name: course.studentName || '未知学员',
          phone: '无手机号',
          avatar: ''
        }
      }
    }) || []
    pagination.total = appointmentList.value.length
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: 'PENDING',
    dateRange: [],
    keyword: '',
  })
  fetchAppointments()
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedAppointments.value = selection
}

// 检查是否可选择
const isSelectable = (row) => {
  return row.status === 'PENDING'
}

// 通过课程预约
const approveAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过学员 ${appointment.student?.name || '未知学员'} 的课程预约吗？`, 
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    // 调用API通过课程预约
    await api.post('/courses/coach-judge', {
      courseId: appointment.id,
      confirmed: true
    })

    ElMessage.success('课程预约已通过')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 显示拒绝对话框
const showRejectDialog = (appointment) => {
  selectedAppointment.value = appointment
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  if (!rejectFormRef.value) return

  try {
    const valid = await rejectFormRef.value.validate()
    if (!valid) return

    rejecting.value = true

    // 调用API拒绝课程预约
    await api.post('/courses/coach-judge', {
      courseId: selectedAppointment.value.id,
      confirmed: false
    })

    ElMessage.success('课程预约已拒绝')
    rejectDialogVisible.value = false
    fetchAppointments()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败: ' + (error.message || '未知错误'))
  } finally {
    rejecting.value = false
  }
}

// 批量通过
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要通过选中的 ${selectedAppointments.value.length} 个课程预约吗？`,
      '批量通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
      },
    )

    // 批量通过课程预约
    for (const appointment of selectedAppointments.value) {
      await api.post('/courses/coach-judge', {
        courseId: appointment.id,
        confirmed: true
      })
    }

    ElMessage.success('批量操作成功')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 批量拒绝
const batchReject = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝选中的 ${selectedAppointments.value.length} 个课程预约吗？`,
      '批量拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )

    // 批量拒绝课程预约
    for (const appointment of selectedAppointments.value) {
      await api.post('/courses/coach-judge', {
        courseId: appointment.id,
        confirmed: false
      })
    }

    ElMessage.success('批量操作成功')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 显示详情对话框
const showDetailDialog = (appointment) => {
  selectedAppointment.value = appointment
  detailDialogVisible.value = true
}

// 重置拒绝表单
const resetRejectForm = () => {
  if (rejectFormRef.value) {
    rejectFormRef.value.resetFields()
  }
  Object.assign(rejectForm, {
    reason: '',
    details: '',
    suggestion: '',
  })
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 格式化日期时间
const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    CONFIRMED: 'success',
    CANCELLED: 'danger',
    COMPLETED: 'info'
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待审核',
    CONFIRMED: '已通过',
    CANCELLED: '已拒绝',
    COMPLETED: '已完成'
  }
  return texts[status] || status
}

// 根据课程节数获取开始时间
const getStartTime = (NO) => {
  const timeMap = {
    1: '08:00',
    2: '09:00',
    3: '10:00',
    4: '11:00',
    5: '14:00',
    6: '15:00',
    7: '16:00',
    8: '17:00',
    9: '19:00',
    10: '20:00'
  }
  return timeMap[NO] || '未知时间'
}

// 根据课程节数获取结束时间
const getEndTime = (NO) => {
  const timeMap = {
    1: '09:00',
    2: '10:00',
    3: '11:00',
    4: '12:00',
    5: '15:00',
    6: '16:00',
    7: '17:00',
    8: '18:00',
    9: '20:00',
    10: '21:00'
  }
  return timeMap[NO] || '未知时间'
}

// 组件挂载
onMounted(() => {
  fetchAppointments()
})
</script>

<style scoped>
.appointment-approval {
  padding: 20px;
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

.batch-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #b3d8ff;
}

.selection-info {
  color: #409eff;
  font-weight: 500;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-details {
  flex: 1;
}

.student-name {
  font-weight: 500;
  color: #333;
}

.student-phone {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
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