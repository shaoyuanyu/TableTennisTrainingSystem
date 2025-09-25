<template>
  <div class="my-matches">
    <PageHeader title="我的比赛安排" :centered="true" />
    
    <el-card class="matches-overview" v-if="!hasRegistered">
      <el-result
        icon="info"
        title="尚未报名"
        sub-title="您还没有报名参加本次月赛"
      >
        <template #extra>
          <el-button type="primary" @click="goToRegistration">
            前往报名
          </el-button>
        </template>
      </el-result>
    </el-card>
    
    <div v-else>
      <el-card class="registration-info">
        <el-result
          icon="success"
          title="已报名"
          sub-title="请等待安排比赛"
        >
          <template #extra>
            <el-descriptions :column="3" border>
              <el-descriptions-item label="参赛组别">{{ getGroupText(registrationInfo.group) }}</el-descriptions-item>
              <el-descriptions-item label="球台编号">{{ registrationInfo.tableId }}</el-descriptions-item>
              <el-descriptions-item label="对手">{{ registrationInfo.opponentUsername || '待分配' }}</el-descriptions-item>
            </el-descriptions>
          </template>
        </el-result>
      </el-card>
      
      <el-card class="schedule-section">
        <template #header>
          <div class="card-header">
            <span>个人赛程安排</span>
          </div>
        </template>
        
        <div v-if="loading" class="loading-container">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="p" style="width: 30%" />
              <div style="margin-top: 20px">
                <el-skeleton-item variant="p" style="width: 50%" />
              </div>
            </template>
          </el-skeleton>
        </div>
        
        <div v-else-if="matches.length === 0" class="no-matches">
          <el-empty description="暂无比赛安排" />
        </div>
        
        <el-timeline v-else>
          <el-timeline-item
            v-for="match in matches"
            :key="`${match.round}-${match.player1}-${match.player2}`"
            :timestamp="`第 ${match.round} 轮`"
            placement="top"
          >
            <el-card>
              <div class="match-item">
                <div class="match-players">
                  <span class="player">{{ match.player1 || '轮空' }}</span>
                  <span class="vs">VS</span>
                  <span class="player">{{ match.player2 || '轮空' }}</span>
                </div>
                <div class="match-details">
                  <el-tag type="primary">球台 {{ match.tableId }}</el-tag>
                </div>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import api from '@/utils/api'

const router = useRouter()

// 状态管理
const loading = ref(false)
const hasRegistered = ref(false)
const registrationInfo = ref({})
const matches = ref([])

// 获取组别文本
const getGroupText = (groupValue) => {
  const groupMap = {
    'A': '甲组',
    'B': '乙组',
    'C': '丙组'
  }
  return groupMap[groupValue] || groupValue
}

// 方法
const fetchRegistrationInfo = async () => {
  try {
    const response = await api.get('/competition/querysignup')
    registrationInfo.value = response.data
    hasRegistered.value = true
  } catch (error) {
    hasRegistered.value = false
  }
}

const fetchMySchedule = async () => {
  try {
    loading.value = true
    const response = await api.get('/competition/my-schedule')
    matches.value = response.data.sort((a, b) => a.round - b.round)
  } catch (error) {
    ElMessage.error('获取比赛安排失败')
  } finally {
    loading.value = false
  }
}

const goToRegistration = () => {
  router.push('/student/tournament-registration')
}

// 生命周期
onMounted(() => {
  fetchRegistrationInfo()
  fetchMySchedule()
})
</script>

<style scoped>
.my-matches {
  padding: 20px;
}

.registration-info {
  margin-bottom: 20px;
}

.schedule-section {
  margin-bottom: 20px;
}

.match-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.match-players {
  display: flex;
  align-items: center;
  gap: 15px;
}

.player {
  font-weight: bold;
}

.vs {
  color: #909399;
}

.match-details {
  display: flex;
  gap: 10px;
}

.loading-container {
  padding: 20px;
}

.no-matches {
  padding: 40px 0;
}
</style>