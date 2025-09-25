<template>
  <div class="tournament-registration">
    <PageHeader title="月赛报名" :centered="true" />
    
    <el-card class="registration-card">
      <div class="registration-info" v-if="tournamentInfo.id !== null">
        <h3 class="section-title">报名须知</h3>
        <ul class="info-list">
          <li class="info-item">报名费：每人{{ tournamentInfo.fee }}元</li>
          <li class="info-item" v-if="tournamentInfo.registrationDeadline">报名截止时间：{{ tournamentInfo.registrationDeadline }}</li>
          
          <li class="info-item" v-if="tournamentInfo.date">比赛时间：{{ tournamentInfo.date }}</li>
          
          <li class="info-item">比赛地点：各校区训练馆</li>
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
        v-if="tournamentInfo.id !== null"
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
      
      <div class="no-tournament" v-else>
        <el-result
          icon="info"
          title="暂无比赛"
          sub-title="当前没有可报名的比赛，请等待管理员创建比赛后再进行报名。"
        />
      </div>
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
              <el-descriptions-item label="扣费金额">{{ tournamentInfo.fee || 30 }}元</el-descriptions-item>
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
  id: null,
  date: '',
  registrationDeadline: '',
  fee: 30
})

// 报名状态
const registrationStatus = ref(null)

// 表单验证规则
const rules = {
  group: [
    { required: true, message: '请选择参赛组别', trigger: 'change' }
  ]
}

// 计算属性
const isBalanceInsufficient = computed(() => {
  return userBalance.value < (tournamentInfo.value.fee || 30)
})

const balanceStatus = computed(() => {
  if (userBalance.value >= (tournamentInfo.value.fee || 30)) {
    return { type: 'success' }
  } else {
    return { type: 'warning' }
  }
})

// 方法
const fetchTournamentInfo = async () => {
  try {
    const response = await api.get('/competition/latest')
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
  max-width: 800px;
  margin: 0 auto;
}

.registration-card,
.registration-status {
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

.status-content {
  text-align: center;
}

.no-tournament {
  text-align: center;
  padding: 40px 0;
}
</style>