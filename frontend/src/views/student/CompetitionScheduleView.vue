<template>
  <div class="competition-schedule-view">
    <!-- 页面头部 -->
    <div class="page-header hover-lift">
      <div class="header-left">
        <div class="header-icon-wrapper"><span class="header-icon">🏆</span></div>
        <div class="header-text">
          <h3>赛程安排</h3>
          <p>查看您报名参加的比赛及其详细赛程安排</p>
        </div>
      </div>
    </div>

    <!-- 已报名比赛列表 -->
    <GlassHeaderCard title="我报名的比赛" icon="📝" size="medium">
      <el-table
        :data="signedUpCompetitions"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="competitionName" label="比赛名称" min-width="160" />
        <el-table-column prop="campusName" label="校区" min-width="120" />
        <el-table-column prop="group" label="组别" min-width="80" />
        <el-table-column prop="createdAt" label="报名时间" min-width="140" />
        <el-table-column label="报名人数" width="120">
          <template #default="scope">
            {{ getCompetitionBySignup(scope.row).currentSignupStudentNumber ?? 0 }} 人
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getCompetitionStatusType(getCompetitionBySignup(scope.row))">
              {{ getCompetitionStatusText(getCompetitionBySignup(scope.row)) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="viewSchedule(scope.row)"
              :disabled="getCompetitionBySignup(scope.row).status === '未开始'"
            >
              查看赛程
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <div v-if="!loading && signedUpCompetitions.length === 0" class="empty-state">
        <el-empty description="您还没有报名任何比赛">
          <PrimaryButton @click="goToCompetitions">查看可报名比赛</PrimaryButton>
        </el-empty>
      </div>
    </GlassHeaderCard>

    <!-- 赛程详情对话框 -->
    <el-dialog 
      v-model="scheduleDialogVisible" 
      :title="selectedCompetition?.competitionName + ' - 赛程安排'" 
      width="800px"
      @close="resetScheduleDialog"
    >
      <div v-if="selectedCompetition && scheduleDetails" class="schedule-details">
        <el-table :data="scheduleDetails" style="width: 100%" stripe>
          <el-table-column prop="turnNumber" label="轮次" width="100">
            <template #default="scope">
              第 {{ scope.row.turnNumber }} 轮
            </template>
          </el-table-column>
          <el-table-column prop="realNameOfPlayerA" label="选手A" min-width="150" />
          <el-table-column prop="realNameOfPlayerB" label="选手B" min-width="150" />
          <el-table-column label="球台" width="100">
            <template #default="scope">
              {{ scope.row.tableId }}号台
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getScheduleStatusType(scope.row.status)">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div v-if="!scheduleDetails && scheduleLoading" class="loading-schedule">
        <el-skeleton animated>
          <template #template>
            <el-skeleton-item variant="p" style="height: 30px; margin-bottom: 10px" v-for="i in 5" :key="i" />
          </template>
        </el-skeleton>
      </div>

      <div v-if="scheduleDetails && scheduleDetails.length === 0" class="empty-schedule">
        <el-empty description="暂无赛程安排" />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <PrimaryButton @click="scheduleDialogVisible = false">关闭</PrimaryButton>
        </span>
      </template>
      
      <div v-if="selectedCompetition" class="competition-info">
        <p><strong>比赛类型：</strong>{{ selectedCompetition.type }}</p>
        <p><strong>比赛日期：</strong>{{ selectedCompetition.date }}</p>
        <p><strong>报名截止：</strong>{{ selectedCompetition.registrationDeadline }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { GlassHeaderCard } from '@/components/cards'
import { PrimaryButton } from '@/components/buttons'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'

// 获取用户状态
const userStore = useUserStore()
const router = useRouter()

// 数据状态
const signedUpCompetitions = ref([])
const allCompetitions = ref([]) // 存储所有比赛信息
const loading = ref(false)
const scheduleDialogVisible = ref(false)
const selectedCompetition = ref(null)
const scheduleDetails = ref(null)
const scheduleLoading = ref(false)

// 获取所有比赛信息
const fetchAllCompetitions = async () => {
  try {
    const response = await api.get('/competition/self-campus')
    allCompetitions.value = response.data || []
  } catch (error) {
    ElMessage.error('获取比赛信息失败：' + (error.response?.data?.message || error.message))
  }
}

// 根据报名信息获取比赛信息
const getCompetitionBySignup = (signup) => {
  if (!signup || !signup.competitionId) return {}
  
  const competition = allCompetitions.value.find(c => c.id === signup.competitionId)
  return competition || {}
}

// 获取已报名的比赛列表
const fetchSignedUpCompetitions = async () => {
  try {
    loading.value = true
    // 先获取所有比赛信息
    await fetchAllCompetitions()
    
    // 再获取报名信息
    const response = await api.get('/competition/signup')
    signedUpCompetitions.value = response.data || []
  } catch (error) {
    ElMessage.error('获取比赛列表失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 获取比赛状态文本
const getCompetitionStatusText = (competition) => {
  return competition.status || '未知'
}

// 获取比赛状态标签类型
const getCompetitionStatusType = (competition) => {
  switch (competition.status) {
    case '未开始':
      return 'warning' // 黄色
    case '进行中':
      return 'success' // 绿色
    case '已结束':
      return 'info' // 灰色
    default:
      return 'info'
  }
}

// 获取赛程状态标签类型
const getScheduleStatusType = (status) => {
  switch (status) {
    case 'SCHEDULED':
      return 'warning'
    case 'ONGOING':
      return 'success'
    case 'COMPLETED':
      return 'info'
    default:
      return 'info'
  }
}

// 查看赛程安排
const viewSchedule = async (signup) => {
  const competition = getCompetitionBySignup(signup)
  
  if (competition.status === '未开始') {
    ElMessage.warning('比赛尚未开始，暂无赛程安排')
    return
  }

  try {
    selectedCompetition.value = competition
    scheduleDialogVisible.value = true
    scheduleLoading.value = true
    scheduleDetails.value = null
    
    const response = await api.get(`/competition/my-schedule/${signup.competitionId}`)
    scheduleDetails.value = response.data || []
  } catch (error) {
    ElMessage.error('获取赛程安排失败：' + (error.response?.data?.message || error.message))
    scheduleDialogVisible.value = false
  } finally {
    scheduleLoading.value = false
  }
}

// 重置赛程对话框
const resetScheduleDialog = () => {
  selectedCompetition.value = null
  scheduleDetails.value = null
}

// 跳转到比赛列表页面
const goToCompetitions = () => {
  router.push('/campus/tournaments')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchSignedUpCompetitions()
})
</script>

<style scoped>
.competition-schedule-view {
  padding: var(--spacing-2xl);
  min-height: 100vh;
}

.page-header {
  margin-bottom: var(--spacing-2xl);
}

.glass-header-card + .glass-header-card {
  margin-top: var(--spacing-2xl);
}

.empty-state {
  padding: var(--spacing-3xl) 0;
  text-align: center;
}

.schedule-details {
  max-height: 400px;
  overflow-y: auto;
}

.loading-schedule,
.empty-schedule {
  padding: var(--spacing-xl) 0;
  text-align: center;
}

.dialog-footer {
  text-align: right;
}

.competition-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.competition-info p {
  margin: 5px 0;
}

/* 表格细节微调 */
:deep(.el-table__header th) {
  background: #f7f7f8;
  color: var(--gray-700);
}

:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  color: var(--gray-800);
}

:deep(.el-table .cell),
:deep(.el-table td),
:deep(.el-table th) {
  color: var(--gray-800);
}
</style>