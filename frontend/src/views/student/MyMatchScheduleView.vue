<template>
  <div class="my-match-schedule">
    <PageHeader title="我的比赛安排" :centered="true" />
    
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import api from '@/utils/api'

// 状态管理
const loading = ref(false)
const matches = ref([])

// 获取比赛安排
const fetchMatches = async () => {
  try {
    loading.value = true
    // 这里需要调用获取比赛安排的API
    // 暂时使用模拟数据
    matches.value = [
      {
        round: 1,
        player1: '张三',
        player2: '李四',
        tableId: 3
      },
      {
        round: 2,
        player1: '张三',
        player2: '王五',
        tableId: 1
      }
    ]
  } catch (error) {
    console.error('获取比赛安排失败:', error)
    ElMessage.error('获取比赛安排失败: ' + (error.response?.data?.error || error.message))
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  fetchMatches()
})
</script>

<style scoped>
.my-match-schedule {
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
</style>