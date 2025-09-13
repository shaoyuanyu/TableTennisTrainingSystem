<template>
  <div class="appointment-approval">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>预约审批</h2>
      <p>审核学员的课程预约申请</p>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchAppointments">
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>

        <el-form-item label="课程类型">
          <el-select v-model="filters.courseType" placeholder="全部类型" clearable>
            <el-option label="基础技术课" value="basic" />
            <el-option label="进阶技术课" value="advanced" />
            <el-option label="实战训练课" value="practice" />
            <el-option label="体能训练课" value="fitness" />
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

    <!-- 预约列表 -->
    <el-card>
      <el-table :data="appointmentList" v-loading="loading" @selection-change="handleSelectionChange" stripe>
        <el-table-column type="selection" width="55" :selectable="isSelectable" />

        <el-table-column prop="id" label="预约编号" width="120" />

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

        <el-table-column prop="appointmentDate" label="预约日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.appointmentDate) }}
          </template>
        </el-table-column>

        <el-table-column prop="timeSlot" label="时间段" width="120">
          <template #default="{ row }"> {{ row.startTime }} - {{ row.endTime }} </template>
        </el-table-column>

        <el-table-column prop="courseType" label="课程类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getCourseTypeText(row.courseType) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="duration" label="时长" width="80">
          <template #default="{ row }"> {{ row.duration }}分钟 </template>
        </el-table-column>

        <el-table-column prop="fee" label="费用" width="100">
          <template #default="{ row }"> ¥{{ row.fee }} </template>
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
            <el-button v-if="row.status === 'pending'" size="small" type="success" @click="approveAppointment(row)">
              通过
            </el-button>
            <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="showRejectDialog(row)">
              拒绝
            </el-button>
            <el-button size="small" @click="showDetailDialog(row)"> 详情 </el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 预约详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="600px">
      <div v-if="selectedAppointment">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预约编号">
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
            {{ formatDate(selectedAppointment.appointmentDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="时间段">
            {{ selectedAppointment.startTime }} - {{ selectedAppointment.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="课程类型">
            {{ getCourseTypeText(selectedAppointment.courseType) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程时长">
            {{ selectedAppointment.duration }}分钟
          </el-descriptions-item>
          <el-descriptions-item label="费用"> ¥{{ selectedAppointment.fee }} </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDateTime(selectedAppointment.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程目标" :span="2">
            <el-tag v-for="goal in selectedAppointment.goals" :key="goal" size="small" style="margin-right: 8px">
              {{ getGoalText(goal) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学员水平">
            {{ getLevelText(selectedAppointment.studentLevel) }}
          </el-descriptions-item>
          <el-descriptions-item label="特殊要求" :span="2">
            {{ selectedAppointment.requirements || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 审核记录 -->
        <div v-if="selectedAppointment.reviewRecord" style="margin-top: 20px">
          <h4>审核记录</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="审核时间">
              {{ formatDateTime(selectedAppointment.reviewRecord.reviewedAt) }}
            </el-descriptions-item>
            <el-descriptions-item v-if="selectedAppointment.status === 'rejected'" label="拒绝原因">
              {{ getRejectReasonText(selectedAppointment.reviewRecord.reason) }}
            </el-descriptions-item>
            <el-descriptions-item v-if="selectedAppointment.reviewRecord.details" label="详细说明">
              {{ selectedAppointment.reviewRecord.details }}
            </el-descriptions-item>
            <el-descriptions-item v-if="selectedAppointment.reviewRecord.suggestion" label="建议时间">
              {{ selectedAppointment.reviewRecord.suggestion }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import DangerButton from '@/components/buttons/DangerButton.vue'

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
  status: 'pending', // 默认显示待审核
  courseType: '',
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
  return selectedAppointments.value.every((item) => item.status === 'pending')
})

const canBatchReject = computed(() => {
  return selectedAppointments.value.every((item) => item.status === 'pending')
})

// 获取预约列表
const fetchAppointments = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...filters,
    }

    const response = await api.get('/coach/appointments', { params })
    appointmentList.value = response.data.list || []
    pagination.total = response.data.total || 0
  } catch {
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: 'pending',
    courseType: '',
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
  return row.status === 'pending'
}

// 通过预约
const approveAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要通过这个预约申请吗？', '确认通过', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success',
    })

    await api.put(`/coach/appointments/${appointment.id}/approve`)
    ElMessage.success('预约已通过')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
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

    await api.put(`/coach/appointments/${selectedAppointment.value.id}/reject`, rejectForm)
    ElMessage.success('预约已拒绝')

    rejectDialogVisible.value = false
    fetchAppointments()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    rejecting.value = false
  }
}

// 批量通过
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要通过选中的 ${selectedAppointments.value.length} 个预约申请吗？`,
      '批量通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
      },
    )

    const ids = selectedAppointments.value.map((item) => item.id)
    await api.put('/coach/appointments/batch-approve', { ids })
    ElMessage.success('批量操作成功')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
    }
  }
}

// 批量拒绝
const batchReject = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝选中的 ${selectedAppointments.value.length} 个预约申请吗？`,
      '批量拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )

    const ids = selectedAppointments.value.map((item) => item.id)
    await api.put('/coach/appointments/batch-reject', {
      ids,
      reason: 'batch_operation',
      details: '批量拒绝操作',
    })
    ElMessage.success('批量操作成功')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量操作失败')
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
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
  }
  return texts[status] || status
}

// 获取课程类型文本
const getCourseTypeText = (type) => {
  const texts = {
    basic: '基础技术课',
    advanced: '进阶技术课',
    practice: '实战训练课',
    fitness: '体能训练课',
  }
  return texts[type] || type
}

// 获取目标文本
const getGoalText = (goal) => {
  const texts = {
    basic_techniques: '基础技术提升',
    serve_practice: '发球练习',
    receive_practice: '接发球练习',
    footwork: '步法训练',
    match_practice: '比赛实战',
  }
  return texts[goal] || goal
}

// 获取等级文本
const getLevelText = (level) => {
  const texts = {
    beginner: '初学者',
    intermediate: '中级',
    advanced: '高级',
  }
  return texts[level] || level
}

// 获取拒绝原因文本
const getRejectReasonText = (reason) => {
  const texts = {
    time_conflict: '时间冲突',
    personal_schedule: '个人安排',
    level_mismatch: '学员水平不符',
    course_mismatch: '课程类型不匹配',
    other: '其他原因',
    batch_operation: '批量操作',
  }
  return texts[reason] || reason
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
