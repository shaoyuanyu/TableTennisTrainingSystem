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
            v-for="(match, index) in matches"
            :key="index"
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
      
      <!-- 比赛记录部分 -->
      <el-card class="competition-history" v-if="competitions.length > 0">
        <template #header>
          <div class="card-header">
            <span>我的比赛记录</span>
          </div>
        </template>
        
        <el-table :data="competitions" style="width: 100%">
          <el-table-column prop="name" label="比赛名称" />
          <el-table-column prop="type" label="比赛类型" />
          <el-table-column prop="date" label="比赛时间" />
          <el-table-column prop="status" label="比赛状态">
            <template #default="scope">
              <el-tag v-if="scope.row.status === '进行中'" type="success">进行中</el-tag>
              <el-tag v-else-if="scope.row.status === '已结束'" type="info">已结束</el-tag>
              <el-tag v-else type="warning">未知状态</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import api from '@/utils/api'

const router = useRouter()

// 状态管理
const loading = ref(false)
const hasRegistered = ref(false)
const matches = ref([])
const competitions = ref([]) // 比赛记录

// 获取组别文本
const getGroupText = (groupValue) => {
  const groupMap = {
    'A': '甲组',
    'B': '乙组',
    'C': '丙组'
  }
  return groupMap[groupValue] || groupValue
}


// 获取用户已报名的比赛记录
const fetchCompetitions = async () => {
  try {
    // 先获取用户报名信息
    const signupResponse = await api.get('/competition/signup')
  
  } catch (error) {
    console.error('获取比赛记录失败:', error)
    ElMessage.error('获取比赛记录失败: ' + (error.response?.data?.error || error.message))
    competitions.value = []
  }
}

const goToRegistration = () => {
  router.push('/student/tournament-registration')
}

// 生命周期
onMounted(() => {
  fetchCompetitions()
})
</script>

<style scoped>
.my-matches {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-section {
  margin-bottom: 20px;
}

.competition-history {
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

.dialog-footer {
  text-align: right;
}
</style>