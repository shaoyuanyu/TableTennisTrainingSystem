<template>
  <div class="tournament-registration">
    <PageHeader title="月赛报名" :centered="true" />
    
    <el-card class="registration-card">
      <div class="registration-info" v-if="tournamentInfo && tournamentInfo.id && !hasRegistered">
        <h3 class="section-title">报名须知</h3>
        <ul class="info-list">
          <li class="info-item">比赛名称：{{ tournamentInfo.name }}</li>
          <li class="info-item">报名费：每人{{ tournamentInfo.fee }}元</li>
          <li class="info-item" v-if="tournamentInfo.registrationDeadline">报名截止时间：{{ tournamentInfo.registrationDeadline }}</li>
          
          <li class="info-item" v-if="tournamentInfo.date">比赛时间：{{ tournamentInfo.date }}</li>
          
          <li class="info-item">比赛地点：各校区训练馆</li>
          <li class="info-item" v-if="tournamentInfo.description">比赛说明：{{ tournamentInfo.description }}</li>
        </ul>
        
        <div class="balance-info">
          <el-alert
            :title="`当前账户余额：${userBalance}元`"
            :type="balanceStatus.type"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      
      <el-form
        ref="registrationForm"
        :model="formData"
        :rules="rules"
        label-width="100px"
        class="registration-form"
        v-if="tournamentInfo && tournamentInfo.id && !hasRegistered"
      >
        <el-form-item label="参赛组别" prop="group">
          <el-select v-model="formData.group" placeholder="请选择参赛组别" @change="handleGroupChange">
            <el-option label="甲组" value="A" />
            <el-option label="乙组" value="B" />
            <el-option label="丙组" value="C" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="submitRegistration"
            :loading="loading"
            :disabled="isBalanceInsufficient"
          >
            立即报名
          </el-button>
          
          <el-button 
            v-if="isBalanceInsufficient"
            type="warning" 
            @click="goToRecharge"
          >
            账户充值
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="already-registered" v-else-if="hasRegistered">
        <el-result
          icon="success"
          title="已报名"
          sub-title="您已成功报名本次月赛"
        >
          <template #extra>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="参赛组别">{{ getGroupText(registrationInfo.group) }}</el-descriptions-item>
              <el-descriptions-item label="报名时间">{{ registrationInfo.registeredAt }}</el-descriptions-item>
            </el-descriptions>
            
            <el-button type="primary" @click="viewSchedule">
              查看比赛安排
            </el-button>
          </template>
        </el-result>
      </div>
      
      <div class="no-tournament" v-else>
        <el-result
          icon="info"
          title="暂无比赛"
          sub-title="当前没有可报名的比赛，请等待管理员创建比赛后再进行报名。"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import PageHeader from '@/components/PageHeader.vue'
import api from '@/utils/api'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userBalance = ref(0)
const loading = ref(false)
const formData = ref({
  group: ''
})

// 比赛信息
const tournamentInfo = ref(null)

// 报名状态
const hasRegistered = ref(false)
const registrationInfo = ref({})

// 表单验证规则
const rules = {
  group: [
    { required: true, message: '请选择参赛组别', trigger: 'change' }
  ]
}

// 计算属性
const isBalanceInsufficient = computed(() => {
  return userBalance.value < (tournamentInfo.value?.fee || 30)
})

const balanceStatus = computed(() => {
  if (userBalance.value >= (tournamentInfo.value?.fee || 30)) {
    return { type: 'success' }
  } else {
    return { type: 'warning' }
  }
})

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
const fetchTournamentInfo = async () => {
  try {
    // 获取本校区所有比赛，然后选择最近的一个
    const response = await api.get('/competition/self-campus')
    const competitions = response.data
    
    // 如果有比赛，选择第一个作为最新比赛
    if (Array.isArray(competitions) && competitions.length > 0) {
      tournamentInfo.value = competitions[0]
    } else {
      // 如果没有比赛，设置为null以显示"暂无比赛"
      tournamentInfo.value = null
    }
  } catch (error) {
    console.error('获取比赛信息失败:', error)
    // 出错时也显示"暂无比赛"
    tournamentInfo.value = null
  }
}

const fetchUserBalance = async () => {
  try {
    const response = await api.get('/wallet/balance')
    userBalance.value = response.data.balance
  } catch (error) {
    ElMessage.error('获取账户余额失败')
  }
}

// 检查用户是否已报名
const checkRegistrationStatus = async () => {
  try {
    const response = await api.get('/competition/querysignup')
    hasRegistered.value = true
    registrationInfo.value = {
      group: response.data.group,
      registeredAt: new Date().toLocaleString()
    }
  } catch (error) {
    hasRegistered.value = false
  }
}

// 处理组别变化
const handleGroupChange = (value) => {
  formData.value.group = value
}

const submitRegistration = async () => {
  if (!formData.value.group) {
    ElMessage.warning('请选择参赛组别')
    return
  }
  
  if (!tournamentInfo.value || !tournamentInfo.value.id) {
    ElMessage.warning('比赛信息不完整')
    return
  }
  
  if (isBalanceInsufficient.value) {
    ElMessage.warning('账户余额不足，请先充值')
    return
  }
  
  try {
    loading.value = true
    await api.post('/competition/signup', {
      competitionId: tournamentInfo.value.id,
      group: formData.value.group
    })
    
    ElMessage.success('报名成功')
    hasRegistered.value = true
    registrationInfo.value = {
      group: formData.value.group,
      registeredAt: new Date().toLocaleString()
    }
    
    // 重新获取余额
    await fetchUserBalance()
  } catch (error) {
    ElMessage.error('报名失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const goToRecharge = () => {
  router.push('/student/account-recharge')
}

const viewSchedule = () => {
  router.push('/student/matches')
}

// 生命周期
onMounted(() => {
  fetchUserBalance()
  fetchTournamentInfo()
  checkRegistrationStatus()
})
</script>

<style scoped>
.tournament-registration {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.registration-card {
  margin-bottom: 20px;
}

.section-title {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.info-list {
  padding-left: 0;
  list-style: none;
}

.info-item {
  margin-bottom: 12px;
  padding: 8px 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
}

.info-item:before {
  content: "•";
  color: #409eff;
  display: inline-block;
  width: 1em;
  margin-left: -1em;
}

.balance-info {
  margin: 20px 0;
}

.registration-form {
  margin-top: 20px;
}

.already-registered,
.no-tournament {
  text-align: center;
  padding: 40px 0;
}
</style>