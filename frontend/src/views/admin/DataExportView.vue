<template>
  <div class="data-export">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>数据导出</h2>
      <el-button type="primary" @click="showScheduleDialog" :icon="Timer"> 计划导出 </el-button>
    </div>

    <!-- 导出类型卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="export-card" @click="showExportDialog('users')">
          <div class="export-icon">
            <el-icon><User /></el-icon>
          </div>
          <h3>用户数据</h3>
          <p>导出学员、教练、管理员等用户信息</p>
          <div class="export-stats">
            <span>{{ userStats.total }} 条记录</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="export-card" @click="showExportDialog('bookings')">
          <div class="export-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <h3>预约数据</h3>
          <p>导出课程预约、场地预约等记录</p>
          <div class="export-stats">
            <span>{{ bookingStats.total }} 条记录</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="export-card" @click="showExportDialog('payments')">
          <div class="export-icon">
            <el-icon><Money /></el-icon>
          </div>
          <h3>财务数据</h3>
          <p>导出支付记录、充值记录、财务报表</p>
          <div class="export-stats">
            <span>{{ paymentStats.total }} 条记录</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card class="export-card" @click="showExportDialog('tournaments')">
          <div class="export-icon">
            <el-icon><Trophy /></el-icon>
          </div>
          <h3>比赛数据</h3>
          <p>导出比赛记录、报名信息、成绩等</p>
          <div class="export-stats">
            <span>{{ tournamentStats.total }} 条记录</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="export-card" @click="showExportDialog('courses')">
          <div class="export-icon">
            <el-icon><Reading /></el-icon>
          </div>
          <h3>课程数据</h3>
          <p>导出课程信息、上课记录、评价等</p>
          <div class="export-stats">
            <span>{{ courseStats.total }} 条记录</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="export-card" @click="showExportDialog('system')">
          <div class="export-icon">
            <el-icon><Setting /></el-icon>
          </div>
          <h3>系统数据</h3>
          <p>导出日志记录、系统配置、统计数据</p>
          <div class="export-stats">
            <span>{{ systemStats.total }} 条记录</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 导出历史 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>导出历史</span>
          <el-button text @click="fetchExportHistory">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="exportHistory" v-loading="historyLoading">
        <el-table-column prop="type" label="导出类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="fileName" label="文件名" min-width="200" />

        <el-table-column prop="format" label="格式" width="80">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.format.toUpperCase() }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="records" label="记录数" width="100" />

        <el-table-column prop="fileSize" label="文件大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'completed'"
              size="small"
              type="primary"
              @click="downloadFile(row)"
            >
              下载
            </el-button>
            <el-button size="small" type="danger" @click="deleteExport(row)"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 导出配置对话框 -->
    <el-dialog
      v-model="exportDialogVisible"
      :title="`导出${getTypeText(selectedType)}数据`"
      width="600px"
    >
      <el-form :model="exportForm" :rules="exportRules" ref="exportFormRef" label-width="100px">
        <el-form-item label="导出格式" prop="format">
          <el-radio-group v-model="exportForm.format">
            <el-radio label="csv">CSV</el-radio>
            <el-radio label="excel">Excel</el-radio>
            <el-radio label="json">JSON</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="时间范围" prop="dateRange">
          <el-date-picker
            v-model="exportForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item v-if="selectedType === 'users'" label="用户类型">
          <el-checkbox-group v-model="exportForm.userTypes">
            <el-checkbox label="student">学员</el-checkbox>
            <el-checkbox label="coach">教练</el-checkbox>
            <el-checkbox label="campus_admin">校区管理员</el-checkbox>
            <el-checkbox label="super_admin">超级管理员</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item v-if="selectedType === 'bookings'" label="预约状态">
          <el-checkbox-group v-model="exportForm.bookingStatus">
            <el-checkbox label="pending">待确认</el-checkbox>
            <el-checkbox label="confirmed">已确认</el-checkbox>
            <el-checkbox label="completed">已完成</el-checkbox>
            <el-checkbox label="cancelled">已取消</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item v-if="selectedType === 'payments'" label="支付状态">
          <el-checkbox-group v-model="exportForm.paymentStatus">
            <el-checkbox label="pending">待支付</el-checkbox>
            <el-checkbox label="paid">已支付</el-checkbox>
            <el-checkbox label="refunded">已退款</el-checkbox>
            <el-checkbox label="failed">支付失败</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="校区筛选">
          <el-select v-model="exportForm.campusId" placeholder="全部校区" clearable>
            <el-option
              v-for="campus in campusList"
              :key="campus.id"
              :label="campus.name"
              :value="campus.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="文件名" prop="fileName">
          <el-input v-model="exportForm.fileName" placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="包含字段">
          <el-checkbox-group v-model="exportForm.fields">
            <template v-for="field in availableFields" :key="field.value">
              <el-checkbox :label="field.value">{{ field.label }}</el-checkbox>
            </template>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="startExport" :loading="exporting"> 开始导出 </el-button>
      </template>
    </el-dialog>

    <!-- 计划导出对话框 -->
    <el-dialog v-model="scheduleDialogVisible" title="计划导出" width="500px">
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="导出类型">
          <el-select v-model="scheduleForm.type" placeholder="选择导出类型">
            <el-option label="用户数据" value="users" />
            <el-option label="预约数据" value="bookings" />
            <el-option label="财务数据" value="payments" />
            <el-option label="比赛数据" value="tournaments" />
            <el-option label="课程数据" value="courses" />
            <el-option label="系统数据" value="system" />
          </el-select>
        </el-form-item>

        <el-form-item label="执行频率">
          <el-radio-group v-model="scheduleForm.frequency">
            <el-radio label="daily">每日</el-radio>
            <el-radio label="weekly">每周</el-radio>
            <el-radio label="monthly">每月</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="执行时间">
          <el-time-picker
            v-model="scheduleForm.time"
            format="HH:mm"
            value-format="HH:mm"
            placeholder="选择时间"
          />
        </el-form-item>

        <el-form-item label="邮件发送">
          <el-switch v-model="scheduleForm.sendEmail" />
        </el-form-item>

        <el-form-item v-if="scheduleForm.sendEmail" label="接收邮箱">
          <el-input v-model="scheduleForm.emails" placeholder="多个邮箱用逗号分隔" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSchedule"> 保存计划 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Timer,
  User,
  Calendar,
  Money,
  Trophy,
  Reading,
  Setting,
  Refresh,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'

// 数据
const exportHistory = ref([])
const campusList = ref([])

// 状态
const historyLoading = ref(false)
const exporting = ref(false)
const exportDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)
const selectedType = ref('')

// 统计数据
const userStats = reactive({ total: 0 })
const bookingStats = reactive({ total: 0 })
const paymentStats = reactive({ total: 0 })
const tournamentStats = reactive({ total: 0 })
const courseStats = reactive({ total: 0 })
const systemStats = reactive({ total: 0 })

// 表单
const exportForm = reactive({
  format: 'excel',
  dateRange: [],
  userTypes: [],
  bookingStatus: [],
  paymentStatus: [],
  campusId: '',
  fileName: '',
  fields: [],
})

const scheduleForm = reactive({
  type: '',
  frequency: 'daily',
  time: '02:00',
  sendEmail: false,
  emails: '',
})

// 表单引用
const exportFormRef = ref()

// 表单验证
const exportRules = {
  format: [{ required: true, message: '请选择导出格式', trigger: 'change' }],
}

// 可用字段配置
const fieldsConfig = {
  users: [
    { label: '用户ID', value: 'id' },
    { label: '用户名', value: 'username' },
    { label: '姓名', value: 'name' },
    { label: '手机号', value: 'phone' },
    { label: '邮箱', value: 'email' },
    { label: '用户类型', value: 'role' },
    { label: '注册时间', value: 'createdAt' },
    { label: '最后登录', value: 'lastLoginAt' },
  ],
  bookings: [
    { label: '预约ID', value: 'id' },
    { label: '用户', value: 'user' },
    { label: '教练', value: 'coach' },
    { label: '预约时间', value: 'bookingTime' },
    { label: '状态', value: 'status' },
    { label: '创建时间', value: 'createdAt' },
  ],
  payments: [
    { label: '支付ID', value: 'id' },
    { label: '用户', value: 'user' },
    { label: '金额', value: 'amount' },
    { label: '支付方式', value: 'method' },
    { label: '状态', value: 'status' },
    { label: '支付时间', value: 'paidAt' },
  ],
}

// 计算可用字段
const availableFields = computed(() => {
  return fieldsConfig[selectedType.value] || []
})

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await api.get('/admin/export/stats')
    const stats = response.data || {}

    Object.assign(userStats, stats.users || {})
    Object.assign(bookingStats, stats.bookings || {})
    Object.assign(paymentStats, stats.payments || {})
    Object.assign(tournamentStats, stats.tournaments || {})
    Object.assign(courseStats, stats.courses || {})
    Object.assign(systemStats, stats.system || {})
  } catch {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取导出历史
const fetchExportHistory = async () => {
  historyLoading.value = true
  try {
    const response = await api.get('/admin/exports')
    exportHistory.value = response.data || []
  } catch {
    ElMessage.error('获取导出历史失败')
  } finally {
    historyLoading.value = false
  }
}

// 获取校区列表
const fetchCampusList = async () => {
  try {
    const response = await api.get('/admin/campuses')
    campusList.value = response.data || []
  } catch {
    ElMessage.error('获取校区列表失败')
  }
}

// 显示导出对话框
const showExportDialog = (type) => {
  selectedType.value = type

  // 重置表单
  Object.assign(exportForm, {
    format: 'excel',
    dateRange: [],
    userTypes: [],
    bookingStatus: [],
    paymentStatus: [],
    campusId: '',
    fileName: '',
    fields: availableFields.value.map((f) => f.value),
  })

  exportDialogVisible.value = true
}

// 显示计划对话框
const showScheduleDialog = () => {
  scheduleDialogVisible.value = true
}

// 开始导出
const startExport = async () => {
  if (!exportFormRef.value) return

  try {
    await exportFormRef.value.validate()

    exporting.value = true

    const data = {
      type: selectedType.value,
      ...exportForm,
    }

    await api.post('/admin/export', data)

    ElMessage.success('导出任务已创建，请在导出历史中查看进度')
    exportDialogVisible.value = false
    fetchExportHistory()
  } catch {
    ElMessage.error('创建导出任务失败')
  } finally {
    exporting.value = false
  }
}

// 保存计划
const saveSchedule = async () => {
  try {
    await api.post('/admin/export/schedule', scheduleForm)
    ElMessage.success('计划导出设置成功')
    scheduleDialogVisible.value = false
  } catch {
    ElMessage.error('设置失败')
  }
}

// 下载文件
const downloadFile = async (record) => {
  try {
    const response = await api.get(`/admin/export/${record.id}/download`, {
      responseType: 'blob',
    })

    const blob = new Blob([response.data])
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = record.fileName
    link.click()
    URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('下载失败')
  }
}

// 删除导出记录
const deleteExport = async (record) => {
  try {
    await ElMessageBox.confirm('确定要删除这个导出记录吗？', '确认删除', {
      type: 'warning',
    })

    await api.delete(`/admin/export/${record.id}`)
    ElMessage.success('删除成功')
    fetchExportHistory()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化时间
const formatTime = (timestamp) => {
  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
}

// 获取类型文本
const getTypeText = (type) => {
  const texts = {
    users: '用户数据',
    bookings: '预约数据',
    payments: '财务数据',
    tournaments: '比赛数据',
    courses: '课程数据',
    system: '系统数据',
  }
  return texts[type] || type
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    failed: 'danger',
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '等待中',
    processing: '处理中',
    completed: '已完成',
    failed: '失败',
  }
  return texts[status] || status
}

// 组件挂载
onMounted(() => {
  fetchStats()
  fetchExportHistory()
  fetchCampusList()
})
</script>

<style scoped>
.data-export {
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

.export-card {
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.export-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.export-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 12px;
}

.export-card h3 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 18px;
}

.export-card p {
  margin: 0 0 12px 0;
  color: #666;
  font-size: 14px;
}

.export-stats {
  font-size: 12px;
  color: #999;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

:deep(.el-checkbox-group) {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

:deep(.el-table .el-button) {
  margin-right: 8px;
}

:deep(.el-table .el-button:last-child) {
  margin-right: 0;
}
</style>
