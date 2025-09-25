<template>
  <div class="tournament-registration">
    <PageHeader title="月赛报名" />
    
    <el-card class="registration-card">
      <div class="registration-info">
        <h3>报名须知</h3>
        <ul>
          <li>报名费：每人30元</li>
          <li v-if="tournamentInfo.registrationDeadline">报名截止时间：{{ tournamentInfo.registrationDeadline }}</li>
          <li v-else>报名时间：每月1-25日</li>
          <li v-if="tournamentInfo.date">比赛时间：{{ tournamentInfo.date }}</li>
          <li v-else>比赛时间：每月第四个周日</li>
          <li>比赛地点：各校区训练馆</li>
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
      >
        <el-form-item label="参赛组别" prop="group">
          <el-select v-model="formData.group" placeholder="请选择参赛组别">
            <el-option label="甲组" value="甲组" />
            <el-option label="乙组" value="乙组" />
            <el-option label="丙组" value="丙组" />
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
    </el-card>
    
    <el-card class="registration-status" v-if="registrationStatus">
      <template #header>
        <div class="card-header">
          <span>报名状态</span>
        </div>
      </template>
      
      <div class="status-content">
        <el-result
          icon="success"
          title="报名成功"
          sub-title="您已成功报名本次月赛"
        >
          <template #extra>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="参赛组别">{{ registrationStatus.group }}</el-descriptions-item>
              <el-descriptions-item label="报名时间">{{ registrationStatus.registeredAt }}</el-descriptions-item>
              <el-descriptions-item label="扣费金额">30元</el-descriptions-item>
            </el-descriptions>
            
            <el-button type="primary" @click="viewSchedule">
              查看比赛安排
            </el-button>
          </template>
        </el-result>
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
const tournamentInfo = ref({
  date: '',
  registrationDeadline: ''
})

// 表单验证规则
const rules = {
  group: [
    { required: true, message: '请选择参赛组别', trigger: 'change' }
  ]
}

// 计算属性
const isBalanceInsufficient = computed(() => {
  return userBalance.value < 30
})

const balanceStatus = computed(() => {
  if (userBalance.value >= 30) {
    return { type: 'success' }
  } else {
    return { type: 'warning' }
  }
})

// 方法
const fetchTournamentInfo = async () => {
  try {
    const response = await api.get('/api/competition/latest')
    tournamentInfo.value = response.data
  } catch (error) {
    console.error('获取比赛信息失败:', error)
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

const submitRegistration = async () => {
  if (!formData.value.group) {
    ElMessage.warning('请选择参赛组别')
    return
  }
  
  if (isBalanceInsufficient.value) {
    ElMessage.warning('账户余额不足，请先充值')
    return
  }
  
  try {
    loading.value = true
    await api.post('/competition/signup', {
      group: formData.value.group
    })
    
    ElMessage.success('报名成功')
    registrationStatus.value = {
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
})
</script>

<style scoped>
.tournament-registration {
  padding: 20px;
}

.registration-card,
.registration-status {
  margin-bottom: 20px;
}

.registration-info ul {
  padding-left: 20px;
}

.registration-info li {
  margin-bottom: 8px;
}

.balance-info {
  margin: 20px 0;
}

.registration-form {
  margin-top: 20px;
}

.status-content {
  text-align: center;
}
</style>