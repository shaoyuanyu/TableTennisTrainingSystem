<template>
  <div class="appointment-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>预约管理</h2>
      <div class="header-actions">
        <OutlineButton @click="exportData">
          <template #icon-left>
            <el-icon>
              <Download />
            </el-icon>
          </template>
          导出数据
        </OutlineButton>
        <PrimaryButton @click="showAddDialog">
          <template #icon-left>
            <el-icon>
              <Plus />
            </el-icon>
          </template>
          新增预约
        </PrimaryButton>
      </div>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchAppointments">
        <el-form-item label="预约状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>

        <el-form-item label="教练">
          <el-select v-model="filters.coachId" placeholder="全部教练" clearable filterable>
            <el-option v-for="coach in coachList" :key="coach.id" :label="coach.name" :value="coach.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期">
          <el-date-picker v-model="filters.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="搜索">
          <el-input v-model="filters.keyword" placeholder="学员姓名/手机号" style="width: 200px"
            @keyup.enter="fetchAppointments" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchAppointments" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预约统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon>
                <Clock />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.pending }}</div>
              <div class="stat-label">待确认</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon today">
              <el-icon>
                <Calendar />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.today }}</div>
              <div class="stat-label">今日预约</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon>
                <CircleCheck />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.completed }}</div>
              <div class="stat-label">本月完成</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">¥{{ stats.revenue }}</div>
              <div class="stat-label">本月收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日课程表 -->
    <el-card class="schedule-card" v-if="todaySchedule.length > 0">
      <template #header>
        <div class="card-header">
          <span>今日课程安排</span>
          <el-tag type="info">{{ formatDate(new Date()) }}</el-tag>
        </div>
      </template>

      <el-timeline>
        <el-timeline-item v-for="item in todaySchedule" :key="item.id" :timestamp="item.time" placement="top">
          <el-card class="schedule-item">
            <div class="schedule-content">
              <div class="schedule-info">
                <h4>{{ item.student.name }} - {{ item.coach.name }}</h4>
                <p>课程时长：{{ item.duration }}分钟</p>
                <p>课程内容：{{ item.content }}</p>
              </div>
              <div class="schedule-actions">
                <el-tag :type="getStatusType(item.status)">
                  {{ getStatusText(item.status) }}
                </el-tag>
                <el-button size="small" @click="handleScheduleAction(item)">
                  {{ getActionText(item.status) }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 预约列表 -->
    <el-card>
      <el-table :data="appointmentList" v-loading="loading" stripe>
        <el-table-column prop="id" label="预约编号" width="120" />

        <el-table-column prop="student" label="学员" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" :src="row.student.avatar">
                {{ row.student.name.charAt(0) }}
              </el-avatar>
              <span>{{ row.student.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="coach" label="教练" width="120">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="32" :src="row.coach.avatar">
                {{ row.coach.name.charAt(0) }}
              </el-avatar>
              <span>{{ row.coach.name }}</span>
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

        <el-table-column prop="duration" label="时长" width="80">
          <template #default="{ row }"> {{ row.duration }}分钟 </template>
        </el-table-column>

        <el-table-column prop="type" label="课程类型" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ getCourseTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="fee" label="费用" width="100">
          <template #default="{ row }"> ¥{{ row.fee }} </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="120">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'pending'" size="small" type="success" @click="confirmAppointment(row)">
              确认
            </el-button>
            <el-button v-if="row.status === 'confirmed'" size="small" type="primary" @click="startClass(row)">
              开始上课
            </el-button>
            <el-button v-if="row.status === 'ongoing'" size="small" type="warning" @click="completeClass(row)">
              结束课程
            </el-button>
            <el-button size="small" @click="showDetailDialog(row)"> 详情 </el-button>
            <el-button v-if="canCancel(row)" size="small" type="danger" @click="cancelAppointment(row)">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
          :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchAppointments" @current-change="fetchAppointments" />
      </div>
    </el-card>

    <!-- 新增预约对话框 -->
    <el-dialog v-model="dialogVisible" title="新增预约" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="appointmentForm" :rules="formRules" label-width="100px">
        <el-form-item label="选择学员" prop="studentId">
          <el-select v-model="appointmentForm.studentId" placeholder="选择学员" filterable style="width: 100%">
            <el-option v-for="student in studentList" :key="student.id" :label="`${student.name} (${student.phone})`"
              :value="student.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="选择教练" prop="coachId">
          <el-select v-model="appointmentForm.coachId" placeholder="选择教练" @change="loadCoachSchedule"
            style="width: 100%">
            <el-option v-for="coach in availableCoaches" :key="coach.id" :label="`${coach.name} (${coach.level})`"
              :value="coach.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker v-model="appointmentForm.appointmentDate" type="date" placeholder="选择日期"
            :disabled-date="disabledDate" @change="loadAvailableSlots" style="width: 100%" />
        </el-form-item>

        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="appointmentForm.timeSlot" placeholder="选择时间段" style="width: 100%">
            <el-option v-for="slot in availableSlots" :key="slot.value" :label="slot.label" :value="slot.value"
              :disabled="!slot.available" />
          </el-select>
        </el-form-item>

        <el-form-item label="课程类型" prop="type">
          <el-select v-model="appointmentForm.type" placeholder="选择课程类型">
            <el-option label="基础技术课" value="basic" />
            <el-option label="进阶技术课" value="advanced" />
            <el-option label="实战训练课" value="practice" />
            <el-option label="体能训练课" value="fitness" />
          </el-select>
        </el-form-item>

        <el-form-item label="课程时长" prop="duration">
          <el-radio-group v-model="appointmentForm.duration">
            <el-radio :label="60">1小时</el-radio>
            <el-radio :label="90">1.5小时</el-radio>
            <el-radio :label="120">2小时</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="appointmentForm.notes" type="textarea" :rows="3" placeholder="课程要求或备注信息" />
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="dialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveAppointment" :loading="saving"> 创建预约 </PrimaryButton>
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
          <el-descriptions-item label="学员">
            {{ selectedAppointment.student?.name }}
          </el-descriptions-item>
          <el-descriptions-item label="学员手机">
            {{ selectedAppointment.student?.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="教练">
            {{ selectedAppointment.coach?.name }}
          </el-descriptions-item>
          <el-descriptions-item label="教练等级">
            {{ getLevelText(selectedAppointment.coach?.level) }}
          </el-descriptions-item>
          <el-descriptions-item label="预约日期">
            {{ formatDate(selectedAppointment.appointmentDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="时间段">
            {{ selectedAppointment.startTime }} - {{ selectedAppointment.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="课程类型">
            {{ getCourseTypeText(selectedAppointment.type) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程时长">
            {{ selectedAppointment.duration }}分钟
          </el-descriptions-item>
          <el-descriptions-item label="费用"> ¥{{ selectedAppointment.fee }} </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(selectedAppointment.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ selectedAppointment.notes || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 课程记录 -->
        <div v-if="selectedAppointment.classRecord" style="margin-top: 20px">
          <h4>课程记录</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="实际开始时间">
              {{ formatDateTime(selectedAppointment.classRecord.startTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="实际结束时间">
              {{ formatDateTime(selectedAppointment.classRecord.endTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="课程内容">
              {{ selectedAppointment.classRecord.content }}
            </el-descriptions-item>
            <el-descriptions-item label="学员评价">
              <el-rate v-model="selectedAppointment.classRecord.studentRating" disabled show-score
                text-color="#ff9900" />
            </el-descriptions-item>
            <el-descriptions-item label="教练评价">
              {{ selectedAppointment.classRecord.coachFeedback }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Download,
  Clock,
  Calendar,
  CircleCheck,
  Money,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'

// 数据列表
const appointmentList = ref([])
const coachList = ref([])
const studentList = ref([])
const availableCoaches = ref([])
const availableSlots = ref([])
const todaySchedule = ref([])
const selectedAppointment = ref(null)

// 状态
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)

// 统计数据
const stats = reactive({
  pending: 0,
  today: 0,
  completed: 0,
  revenue: 0,
})

// 筛选器
const filters = reactive({
  status: '',
  coachId: '',
  dateRange: [],
  keyword: '',
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
})

// 表单数据
const appointmentForm = reactive({
  studentId: '',
  coachId: '',
  appointmentDate: '',
  timeSlot: '',
  type: '',
  duration: 60,
  notes: '',
})

// 表单引用
const formRef = ref()

// 表单验证规则
const formRules = {
  studentId: [{ required: true, message: '请选择学员', trigger: 'change' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  appointmentDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  type: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
}

// 获取预约列表
const fetchAppointments = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...filters,
    }

    const response = await api.get('/campus/appointments', { params })
    appointmentList.value = response.data.list || []
    pagination.total = response.data.total || 0

    // 获取统计数据
    const statsResponse = await api.get('/campus/appointments/stats')
    Object.assign(stats, statsResponse.data || {})

    // 获取今日课程安排
    const scheduleResponse = await api.get('/campus/appointments/today-schedule')
    todaySchedule.value = scheduleResponse.data || []
  } catch {
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 获取教练和学员列表
const fetchBasicData = async () => {
  try {
    const [coachRes, studentRes] = await Promise.all([
      api.get('/campus/coaches?status=active'),
      api.get('/campus/students?status=active'),
    ])

    coachList.value = coachRes.data || []
    studentList.value = studentRes.data || []
    availableCoaches.value = coachRes.data || []
  } catch {
    ElMessage.error('获取基础数据失败')
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: '',
    coachId: '',
    dateRange: [],
    keyword: '',
  })
  fetchAppointments()
}

// 显示新增对话框
const showAddDialog = () => {
  dialogVisible.value = true
}

// 显示详情对话框
const showDetailDialog = (appointment) => {
  selectedAppointment.value = appointment
  detailDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(appointmentForm, {
    studentId: '',
    coachId: '',
    appointmentDate: '',
    timeSlot: '',
    type: '',
    duration: 60,
    notes: '',
  })
  availableSlots.value = []
}

// 禁用日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 不能选择过去的日期
}

// 加载教练排班
const loadCoachSchedule = async () => {
  if (appointmentForm.coachId && appointmentForm.appointmentDate) {
    await loadAvailableSlots()
  }
}

// 加载可用时间段
const loadAvailableSlots = async () => {
  if (!appointmentForm.coachId || !appointmentForm.appointmentDate) return

  try {
    const response = await api.get('/campus/appointments/available-slots', {
      params: {
        coachId: appointmentForm.coachId,
        date: appointmentForm.appointmentDate,
      },
    })
    availableSlots.value = response.data || []
  } catch {
    ElMessage.error('获取可用时间段失败')
  }
}

// 保存预约
const saveAppointment = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    saving.value = true

    await api.post('/campus/appointments', appointmentForm)
    ElMessage.success('预约创建成功')

    dialogVisible.value = false
    fetchAppointments()
  } catch {
    ElMessage.error('创建预约失败')
  } finally {
    saving.value = false
  }
}

// 确认预约
const confirmAppointment = async (appointment) => {
  try {
    await api.put(`/campus/appointments/${appointment.id}/confirm`)
    ElMessage.success('预约确认成功')
    fetchAppointments()
  } catch {
    ElMessage.error('确认失败')
  }
}

// 开始上课
const startClass = async (appointment) => {
  try {
    await api.put(`/campus/appointments/${appointment.id}/start`)
    ElMessage.success('课程开始')
    fetchAppointments()
  } catch {
    ElMessage.error('操作失败')
  }
}

// 结束课程
const completeClass = async (appointment) => {
  try {
    await api.put(`/campus/appointments/${appointment.id}/complete`)
    ElMessage.success('课程结束')
    fetchAppointments()
  } catch {
    ElMessage.error('操作失败')
  }
}

// 取消预约
const cancelAppointment = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await api.put(`/campus/appointments/${appointment.id}/cancel`)
    ElMessage.success('预约已取消')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 处理今日课程操作
const handleScheduleAction = (item) => {
  switch (item.status) {
    case 'confirmed':
      startClass(item)
      break
    case 'ongoing':
      completeClass(item)
      break
    default:
      showDetailDialog(item)
  }
}

// 导出数据
const exportData = async () => {
  try {
    const response = await api.get('/campus/appointments/export', {
      params: filters,
      responseType: 'blob',
    })

    const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `预约数据_${dayjs().format('YYYY-MM-DD')}.xlsx`
    link.click()
    URL.revokeObjectURL(url)

    ElMessage.success('数据导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

// 判断是否可以取消
const canCancel = (appointment) => {
  return ['pending', 'confirmed'].includes(appointment.status)
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
    confirmed: 'primary',
    ongoing: 'success',
    completed: 'info',
    cancelled: 'danger',
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待确认',
    confirmed: '已确认',
    ongoing: '进行中',
    completed: '已完成',
    cancelled: '已取消',
  }
  return texts[status] || status
}

// 获取操作文本
const getActionText = (status) => {
  const texts = {
    confirmed: '开始上课',
    ongoing: '结束课程',
  }
  return texts[status] || '查看详情'
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

// 获取等级文本
const getLevelText = (level) => {
  const texts = {
    junior: '初级教练',
    intermediate: '中级教练',
    senior: '高级教练',
    expert: '特级教练',
  }
  return texts[level] || level
}

// 组件挂载
onMounted(() => {
  fetchAppointments()
  fetchBasicData()
})
</script>

<style scoped>
.appointment-management {
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

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.today {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
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

.schedule-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.schedule-item {
  margin-bottom: 16px;
}

.schedule-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.schedule-info h4 {
  margin: 0 0 8px 0;
  color: #333;
}

.schedule-info p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.schedule-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
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

:deep(.el-timeline-item__timestamp) {
  font-weight: bold;
  color: #409eff;
}
</style>
