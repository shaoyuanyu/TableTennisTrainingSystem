<template>
  <div class="mutual-selection">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>教练双选申请</h2>
      <p>向教练提交双选申请，建立师生关系</p>
    </div>

    <!-- 申请状态说明 -->
    <el-card class="info-card">
      <h3>我的申请状态</h3>
      <el-steps :active="currentStep" align-center finish-status="success">
        <el-step title="选择教练" description="浏览并选择合适的教练"></el-step>
        <el-step title="提交申请" description="向教练发送双选申请"></el-step>
        <el-step title="等待审核" description="教练审核您的申请"></el-step>
        <el-step title="建立关系" description="审核通过后建立师生关系"></el-step>
      </el-steps>

      <!-- 状态说明 -->
      <div v-if="applications.length > 0" class="status-summary">
        <p>
          <span v-if="pendingCount > 0 && approvedCount > 0">
            您有 {{ pendingCount }} 个申请正在等待审核，{{ approvedCount }} 个申请已通过审核
          </span>
          <span v-else-if="pendingCount > 0">
            您有 {{ pendingCount }} 个申请正在等待审核
          </span>
          <span v-else-if="approvedCount > 0">
            您有 {{ approvedCount }} 个申请已通过审核
          </span>
          <span v-else-if="rejectedCount > 0">
            您有 {{ rejectedCount }} 个申请被拒绝
          </span>
          <span v-else>您当前没有进行中的申请</span>
        </p>
      </div>
    </el-card>

    <!-- 我的申请列表 -->
    <el-card class="application-card">
      <template #header>
        <div class="card-header">
          <span>我的申请记录</span>
          <el-button type="primary" @click="refreshApplications" :icon="Refresh">刷新</el-button>
        </div>
      </template>

      <el-table :data="applications" v-loading="loading" style="width: 100%">
        <el-table-column prop="coachName" label="教练姓名" width="150">
          <template #default="{ row }">
            <div class="coach-info">
              <el-avatar :size="32" :src="row.coachAvatar">
                {{ row.coachName.charAt(0) }}
              </el-avatar>
              <span>{{ row.coachName }}</span>
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
              type="danger"
              @click="withdrawApplicationHandler(row)"
            >
              撤回
            </el-button>
            <el-button
              v-if="row.status === 'APPROVED' || row.status === 'ACTIVE'"
              size="small"
              type="danger"
              @click="cancelApprovedRelationHandler(row)"
            >
              取消关系
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
          <el-descriptions-item label="教练姓名">
            {{ selectedApplication.coachName }}
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            {{ formatDateTime(selectedApplication.applicationTime) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.reviewTime" label="审核时间">
            {{ formatDateTime(selectedApplication.reviewTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedApplication.status === 'REJECTED'" class="rejection-info">
          <h4>拒绝原因</h4>
          <p>{{ selectedApplication.rejectionReason || '教练未提供具体原因' }}</p>
        </div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { getStudentApplications, withdrawApplication, cancelApprovedRelation } from '@/api/mutualSelection'

// 数据状态
const applications = ref([])
const selectedApplication = ref(null)
const loading = ref(false)
const detailDialogVisible = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 计算属性
const currentStep = computed(() => {
  // 如果没有申请记录，默认显示第1步（选择教练）
  if (!applications.value || applications.value.length === 0) {
    return 1
  }

  // 检查是否有待处理的申请
  const hasPendingApplications = applications.value.some(app => app.status === 'PENDING')

  // 检查是否有已批准或活跃的申请
  const hasApprovedOrActive = applications.value.some(app =>
    app.status === 'APPROVED' || app.status === 'ACTIVE'
  )

  // 如果有正在等待审核的申请，显示第3步（等待审核）
  if (hasPendingApplications) {
    return 3
  }

  // 如果没有正在等待审核的申请，但有已批准或活跃的申请，显示第4步（建立关系）
  if (!hasPendingApplications && hasApprovedOrActive) {
    return 4
  }

  // 默认显示第2步（提交申请）
  return 2
})

const hasApprovedApplications = computed(() => {
  return applications.value.some(app =>
    app.status === 'APPROVED' || app.status === 'ACTIVE'
  )
})

const hasPendingApplications = computed(() => {
  return applications.value.some(app => app.status === 'PENDING')
})

const approvedCount = computed(() => {
  return applications.value.filter(app =>
    app.status === 'APPROVED' || app.status === 'ACTIVE'
  ).length
})

const pendingCount = computed(() => {
  return applications.value.filter(app => app.status === 'PENDING').length
})

const rejectedCount = computed(() => {
  return applications.value.filter(app => app.status === 'REJECTED').length
})

// 获取申请列表
const fetchApplications = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    const response = await getStudentApplications(params)
    applications.value = response.content || response.list || response
    pagination.total = response.totalElements || response.total || 0

    // 如果有已批准的申请，显示成功消息
    if (hasApprovedApplications.value) {
      ElMessage.success(`您有 ${approvedCount.value} 个申请已通过审核`)
    }
  } catch (error) {
    ElMessage.error('获取申请列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 刷新申请列表
const refreshApplications = () => {
  fetchApplications()
}

// 查看申请详情
const viewApplicationDetail = (application) => {
  selectedApplication.value = application
  detailDialogVisible.value = true
}

// 撤回申请
const withdrawApplicationHandler = async (application) => {
  try {
    await ElMessageBox.confirm(
      `确定要撤回向 ${application.coachName} 提交的申请吗？`,
      '确认撤回',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await withdrawApplication(application.selectionId)

    ElMessage.success('申请已撤回')
    await fetchApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤回失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 取消已批准的关系
const cancelApprovedRelationHandler = async (application) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消与 ${application.coachName} 的双选关系吗？此操作不可恢复。`,
      '确认取消关系',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await cancelApprovedRelation(application.selectionId)

    ElMessage.success('双选关系已取消')
    await fetchApplications()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消关系失败: ' + (error.response?.data?.message || error.message))
    }
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
.mutual-selection {
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

.info-card {
  margin-bottom: 20px;
}

.info-card h3 {
  margin-top: 0;
}

.status-summary {
  margin-top: 20px;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border: 1px solid #b3d8ff;
  text-align: center;
}

.status-summary p {
  margin: 0;
  color: #409eff;
  font-weight: 500;
}

.application-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coach-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rejection-info {
  margin-top: 20px;
  padding: 15px;
  background-color: #fef0f0;
  border-radius: 4px;
  border: 1px solid #fde2e2;
}

.rejection-info h4 {
  margin-top: 0;
  color: #f56c6c;
}

.rejection-info p {
  margin: 0;
  color: #666;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

</style>