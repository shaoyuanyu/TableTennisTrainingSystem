<template>
  <div class="mutual-selection-coach">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>双选申请处理</h2>
      <p>处理学员提交的双选申请</p>
    </div>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card pending-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.pendingCount }}</div>
              <div class="stat-label">待处理申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card approved-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.currentStudents }}</div>
              <div class="stat-label">当前学员</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card capacity-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.capacity }}/{{ stats.maxCapacity }}</div>
              <div class="stat-label">容量使用</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchApplications">
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="已批准" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="非活跃" value="INACTIVE" />
          </el-select>
        </el-form-item>

        <el-form-item label="申请日期">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="学员搜索">
          <el-input
            v-model="filters.keyword"
            placeholder="学员姓名"
            style="width: 200px"
            @keyup.enter="fetchApplications"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchApplications" :icon="Search">搜索</el-button>
          <el-button @click="resetFilters" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 申请列表 -->
    <el-card class="application-card">
      <template #header>
        <div class="card-header">
          <span>双选申请列表</span>
          <el-button type="primary" @click="refreshApplications" :icon="Refresh">刷新</el-button>
        </div>
      </template>

      <el-table :data="applications" v-loading="loading" style="width: 100%">
        <el-table-column prop="studentName" label="学员姓名" width="150">
          <template #default="{ row }">
            <div class="student-info">
              <el-avatar :size="32" :src="row.studentAvatar">
                {{ row.studentName.charAt(0) }}
              </el-avatar>
              <span>{{ row.studentName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="申请状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getApplicationStatusType(row.status)">
              {{ getApplicationStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="applicationTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.applicationTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewApplicationDetail(row)">详情</el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="success"
              @click="showApproveDialog(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="danger"
              @click="showRejectDialog(row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" v-if="applications.length > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <el-empty v-if="!loading && applications.length === 0" description="暂无申请记录" />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewDialogVisible" :title="reviewAction === 'approve' ? '通过申请' : '拒绝申请'" width="500px">
      <el-form
        ref="reviewFormRef"
        :model="reviewForm"
        :rules="reviewRules"
        label-width="100px"
        v-if="selectedApplication"
      >
        <el-form-item label="学员姓名">
          <el-input v-model="selectedApplication.studentName" disabled />
        </el-form-item>

        <el-form-item label="申请时间">
          <el-input v-model="selectedApplication.applicationTime" disabled />
        </el-form-item>

        <el-form-item
          v-if="reviewAction === 'reject'"
          label="拒绝原因"
          prop="rejectionReason"
        >
          <el-input
            v-model="reviewForm.rejectionReason"
            type="textarea"
            :rows="3"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button
          v-if="reviewAction === 'approve'"
          type="success"
          @click="confirmApprove"
          :loading="reviewing"
        >
          确认通过
        </el-button>
        <el-button
          v-if="reviewAction === 'reject'"
          type="danger"
          @click="confirmReject"
          :loading="reviewing"
        >
          确认拒绝
        </el-button>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="申请详情" width="600px">
      <div v-if="selectedApplication">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请编号">
            {{ selectedApplication.selectionId }}
          </el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getApplicationStatusType(selectedApplication.status)">
              {{ getApplicationStatusText(selectedApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学员姓名">
            {{ selectedApplication.studentName }}
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDateTime(selectedApplication.applicationTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.reviewTime" label="审核时间">
            {{ formatDateTime(selectedApplication.reviewTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.rejectionReason" label="拒绝原因" :span="2">
            {{ selectedApplication.rejectionReason }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, User, DataAnalysis, Refresh, Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { getPendingApplicationCount, reviewApplication } from '@/api/mutualSelection'

// 数据状态
const applications = ref([])
const selectedApplication = ref(null)
const loading = ref(false)
const reviewing = ref(false)
const reviewDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const reviewAction = ref('') // 'approve' or 'reject'

// 统计信息
const stats = reactive({
  pendingCount: 0,
  currentStudents: 0,
  capacity: 0,
  maxCapacity: 20
})

// 筛选器
const filters = reactive({
  status: 'PENDING', // 默认显示待处理
  dateRange: [],
  keyword: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 审核表单
const reviewForm = reactive({
  rejectionReason: ''
})

const reviewFormRef = ref()

// 表单验证规则
const reviewRules = {
  rejectionReason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
}

// 获取申请列表
const fetchApplications = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      status: filters.status,
      startDate: filters.dateRange && filters.dateRange[0] ? filters.dateRange[0] : undefined,
      endDate: filters.dateRange && filters.dateRange[1] ? filters.dateRange[1] : undefined,
      keyword: filters.keyword || undefined
    }

    // 移除值为undefined的参数
    Object.keys(params).forEach(key => {
      if (params[key] === undefined) {
        delete params[key]
      }
    })

    const response = await api.get('/mutual-selection/coach-applications', { params })
    applications.value = response.data.content || response.data.list || response.data
    pagination.total = response.data.totalElements || response.data.total || 0

    // 获取统计信息
    await fetchStats()
  } catch (error) {
    ElMessage.error('获取申请列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 获取统计信息
const fetchStats = async () => {
  try {
    // 获取待处理申请数量
    const pendingResponse = await getPendingApplicationCount()
    stats.pendingCount = pendingResponse.count || pendingResponse || 0

    // 获取当前学员数量
    const studentsResponse = await api.get('/mutual-selection/coach/current-students')
    stats.currentStudents = studentsResponse.data?.length || 0

    // 设置容量信息
    stats.capacity = stats.currentStudents
  } catch (error) {
    console.error('获取统计信息失败:', error)
  }
}

// 刷新申请列表
const refreshApplications = () => {
  fetchApplications()
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: 'PENDING',
    dateRange: [],
    keyword: ''
  })
  fetchApplications()
}

// 查看申请详情
const viewApplicationDetail = (application) => {
  selectedApplication.value = application
  detailDialogVisible.value = true
}

// 显示通过对话框
const showApproveDialog = (application) => {
  selectedApplication.value = application
  reviewAction.value = 'approve'
  reviewDialogVisible.value = true
}

// 显示拒绝对话框
const showRejectDialog = (application) => {
  selectedApplication.value = application
  reviewAction.value = 'reject'
  reviewForm.rejectionReason = ''
  reviewDialogVisible.value = true
}

// 确认通过申请
const confirmApprove = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要通过学员 ${selectedApplication.value.studentName} 的申请吗？`,
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    reviewing.value = true
    await reviewApplication(selectedApplication.value.selectionId, 'APPROVED')

    ElMessage.success('申请已通过')
    reviewDialogVisible.value = false
    await fetchApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
    }
  } finally {
    reviewing.value = false
  }
}

// 确认拒绝申请
const confirmReject = async () => {
  if (!reviewFormRef.value) return

  try {
    const valid = await reviewFormRef.value.validate()
    if (!valid) return

    await ElMessageBox.confirm(
      `确定要拒绝学员 ${selectedApplication.value.studentName} 的申请吗？`,
      '确认拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    reviewing.value = true
    await reviewApplication(
      selectedApplication.value.selectionId,
      'REJECTED',
      reviewForm.rejectionReason
    )

    ElMessage.success('申请已拒绝')
    reviewDialogVisible.value = false
    await fetchApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
    }
  } finally {
    reviewing.value = false
  }
}

// 处理分页变化
const handleSizeChange = (val) => {
  pagination.size = val
  fetchApplications()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchApplications()
}

// 获取申请状态类型
const getApplicationStatusType = (status) => {
  const types = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    ACTIVE: 'success',
    INACTIVE: 'info'
  }
  return types[status] || 'info'
}

// 获取申请状态文本
const getApplicationStatusText = (status) => {
  const texts = {
    PENDING: '待处理',
    APPROVED: '已批准',
    REJECTED: '已拒绝',
    ACTIVE: '活跃',
    INACTIVE: '非活跃'
  }
  return texts[status] || status
}

// 格式化日期时间
const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchApplications()
})
</script>

<style scoped>
.mutual-selection-coach {
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

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 120px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  font-size: 32px;
  margin-right: 20px;
  padding: 15px;
  border-radius: 8px;
}

.pending-card .stat-icon {
  color: #e6a23c;
  background-color: #fdf6ec;
}

.approved-card .stat-icon {
  color: #67c23a;
  background-color: #f0f9ec;
}

.capacity-card .stat-icon {
  color: #409eff;
  background-color: #ecf5ff;
}

.stat-info .stat-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-info .stat-label {
  color: #666;
}

.filter-card {
  margin-bottom: 20px;
}

.application-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

</style>

