<template>
  <div class="my-matches">
    <PageHeader title="我的比赛" :centered="true" />
    
    <!-- 我参加的比赛 -->
    <el-card class="my-competitions" v-if="myCompetitions.length > 0">
      <template #header>
        <div class="card-header">
          <span>我参加的比赛</span>
        </div>
      </template>
      
      <el-table :data="myCompetitions" style="width: 100%">
        <el-table-column prop="name" label="比赛名称" />
        <el-table-column prop="type" label="比赛类型" />
        <el-table-column prop="date" label="比赛时间" />
        <el-table-column prop="status" label="比赛状态">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '进行中'" type="success">进行中</el-tag>
            <el-tag v-else-if="scope.row.status === '已结束'" type="info">已结束</el-tag>
            <el-tag v-else-if="scope.row.status === '报名中'" type="warning">报名中</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 删除查看详细赛程安排按钮 -->
      <!-- <div class="action-section" style="margin-top: 20px;">
        <el-button type="primary" @click="viewSchedule">
          查看详细赛程安排
        </el-button>
      </div> -->
    </el-card>
    
    <el-card class="my-competitions" v-else>
      <el-result
        icon="info"
        title="尚未报名"
        sub-title="您还没有报名参加任何比赛"
      >
        
      </el-result>
    </el-card>
    
    <!-- 可报名的比赛 -->
    <el-card class="available-competitions" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>可报名的比赛</span>
        </div>
      </template>
      
      <el-table :data="availableCompetitions" style="width: 100%">
        <el-table-column prop="name" label="比赛名称" />
        <el-table-column prop="type" label="比赛类型" />
        <el-table-column prop="date" label="比赛时间" />
        <el-table-column prop="registrationDeadline" label="报名截止时间" />
        <el-table-column prop="fee" label="报名费(元)">
          <template #default="scope">
            {{ scope.row.fee }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button 
              type="primary" 
              @click="goToRegistrationForCompetition(scope.row)"
            >
              前往报名
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="no-data" v-if="availableCompetitions.length === 0">
        <el-result
          icon="info"
          title="暂无比赛"
          sub-title="当前没有可报名的比赛，请等待管理员创建比赛"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import api from '@/utils/api'

const router = useRouter()

// 状态管理
const myCompetitions = ref([]) // 我参加的比赛
const availableCompetitions = ref([]) // 可报名的比赛

// // 删除查看赛程安排方法
// // 查看赛程安排
// const viewSchedule = () => {
//   router.push('/student/match-schedule')
// }

// 获取我参加的比赛
const fetchMyCompetitions = async () => {
  try {
    // 获取用户已报名的比赛信息
    const signupResponse = await api.get('/competition/signup')
    
    // 如果用户已报名
    if (signupResponse.data && Array.isArray(signupResponse.data) && signupResponse.data.length > 0) {
      // 获取本校区比赛列表
      const competitionsResponse = await api.get('/competition/self-campus')
      const allCompetitions = competitionsResponse.data
      
      // 查找用户报名的比赛（根据实际数据结构调整）
      // 这里假设用户报名的比赛ID与competition列表中的ID匹配
      const signedUpCompetitionIds = signupResponse.data.map(item => item.competitionId)
      const userCompetitions = allCompetitions.filter(competition => 
        signedUpCompetitionIds.includes(competition.id)
      ).map(competition => {
        return {
          id: competition.id,
          name: competition.name,
          type: competition.type,
          date: competition.date,
          status: competition.status || '进行中' // 使用后端返回的状态，如果没有则默认为进行中
        }
      })
      
      myCompetitions.value = userCompetitions
    } else {
      myCompetitions.value = []
    }
  } catch (error) {
    console.error('获取我的比赛记录失败:', error)
    myCompetitions.value = []
    // 不显示错误消息，因为可能是用户未报名任何比赛
  }
}

// 获取可报名的比赛
const fetchAvailableCompetitions = async () => {
  try {
    // 获取用户已报名的比赛信息
    const signupResponse = await api.get('/competition/signup')
    const signedUpCompetitionIds = signupResponse.data && Array.isArray(signupResponse.data) && signupResponse.data.length > 0
      ? signupResponse.data.map(item => item.competitionId)
      : []
    
    // 获取本校区比赛列表
    const response = await api.get('/competition/self-campus')
    const allCompetitions = response.data
    
    // 过滤掉已报名的比赛
    availableCompetitions.value = allCompetitions.filter(competition => 
      !signedUpCompetitionIds.includes(competition.id)
    ).map(competition => {
      return {
        id: competition.id,
        name: competition.name,
        type: competition.type,
        date: competition.date,
        registrationDeadline: competition.registrationDeadline,
        fee: competition.fee,
        description: competition.description,
        status: competition.status // 使用后端返回的状态
      }
    })
  } catch (error) {
    console.error('获取可报名比赛失败:', error)
    availableCompetitions.value = []
    ElMessage.error('获取可报名比赛失败: ' + (error.response?.data?.error || error.message))
  }
}

// 前往报名页面
const goToRegistration = () => {
  router.push('/student/tournament-registration')
}

// 为特定比赛前往报名页面
const goToRegistrationForCompetition = (competition) => {
  // 跳转到报名页面，并传递比赛ID作为参数
  router.push(`/student/tournament-registration/${competition.id}`)
}

// 页面激活时刷新数据
onActivated(() => {
  fetchMyCompetitions()
  fetchAvailableCompetitions()
})

// 生命周期
onMounted(() => {
  fetchMyCompetitions()
  fetchAvailableCompetitions()
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

.my-competitions,
.available-competitions {
  margin-bottom: 20px;
}

.action-section {
  text-align: center;
}

.no-data {
  text-align: center;
  padding: 20px 0;
}
</style>