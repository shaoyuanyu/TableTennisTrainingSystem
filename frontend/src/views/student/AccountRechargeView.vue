<template>
  <div class="account-recharge">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>账户充值</h1>
      <p>为您的账户充值，以便预约课程和享受其他服务</p>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：余额和充值表单 -->
      <el-col :xs="24" :lg="12">
        <div class="left-column">
          <!-- 当前余额卡片 -->
          <el-card class="balance-card">
            <template #header>
              <div class="balance-header">
                <span>当前余额</span>
              </div>
            </template>
            <div class="balance-content">
              <div class="balance-amount">¥{{ currentBalance.toFixed(2) }}</div>
              <div class="balance-info">
                
              </div>
            </div>
          </el-card>

          <!-- 充值表单 -->
          <el-card class="recharge-card">
            <template #header>
              <div class="recharge-header">
                <span>充值金额</span>
              </div>
            </template>
            <el-form
              ref="rechargeFormRef"
              :model="rechargeForm"
              :rules="rechargeRules"
              label-position="top"
              @submit.prevent="handleRecharge"
            >
              <el-form-item label="充值金额 (元)" prop="amount">
                <el-input-number
                  v-model="rechargeForm.amount"
                  :min="0.01"
                  :max="10000"
                  :step="100"
                  placeholder="请输入充值金额"
                  style="width: 100%"
                  controls-position="right"
                />
                <div class="amount-hints">
                  <el-tag
                    v-for="hint in amountHints"
                    :key="hint.value"
                    class="amount-hint"
                    @click="selectAmount(hint.value)"
                  >
                    ¥{{ hint.value }}
                  </el-tag>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  class="recharge-button"
                  :loading="recharging"
                  @click="handleRecharge"
                >
                  立即充值
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </el-col>

      <!-- 右侧：充值记录 -->
      <el-col :xs="24" :lg="12">
        <div class="right-column">
          <el-card class="history-card">
            <template #header>
              <div class="history-header">
                <span>充值记录</span>
              </div>
            </template>
            <el-table
              :data="rechargeHistory"
              v-loading="loadingHistory"
              empty-text="暂无充值记录"
              style="width: 100%"
            >
              <el-table-column prop="createdAt" label="充值时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="充值金额" width="120">
                <template #default="{ row }">
                  <span :class="row.amount > 0 ? 'positive-amount' : 'negative-amount'">
                    {{ row.amount > 0 ? '+' : '' }}¥{{ Math.abs(row.amount).toFixed(2) }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="说明">
                <template #default="{ row }">
                  {{ row.description || '账户充值' }}
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination-wrapper" v-if="rechargeHistory.length > 0">
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
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'
import dayjs from 'dayjs'

// 表单引用
const rechargeFormRef = ref()

// 状态
const currentBalance = ref(0)
const recharging = ref(false)
const loadingHistory = ref(false)

// 充值表单
const rechargeForm = reactive({
  amount: null
})

// 充值记录
const rechargeHistory = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表单验证规则
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '充值金额必须大于0', trigger: 'blur' },
    { type: 'number', max: 10000, message: '单次充值金额不能超过10000元', trigger: 'blur' }
  ]
}

// 快捷金额提示
const amountHints = [
  { value: 100, label: '¥100' },
  { value: 200, label: '¥200' },
  { value: 500, label: '¥500' },
  { value: 1000, label: '¥1000' }
]

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 选择快捷金额
const selectAmount = (amount) => {
  rechargeForm.amount = amount
}

// 处理充值
const handleRecharge = async () => {
  if (!rechargeFormRef.value) return

  try {
    const valid = await rechargeFormRef.value.validate()
    if (!valid) return

    recharging.value = true

    // 发送充值请求
    const response = await api.post('/wallet/recharge', { amount: rechargeForm.amount })

    // 更新余额
    currentBalance.value = response.data.balance

    // 重置表单
    rechargeForm.amount = null

    // 刷新充值记录 - 重置到第一页以确保看到最新记录
    pagination.page = 1
    await fetchRechargeHistory()

    ElMessage.success(`充值成功！当前余额：¥${currentBalance.value.toFixed(2)}`)
  } catch (error) {
    console.error('充值失败:', error)
    ElMessage.error(error.response?.data?.message || '充值失败，请稍后重试')
  } finally {
    recharging.value = false
  }
}

// 获取当前余额
const fetchBalance = async () => {
  try {
    const response = await api.get('/wallet/balance')
    currentBalance.value = response.data.balance
  } catch (error) {
    console.error('获取余额失败:', error)
    ElMessage.error('获取余额失败，请稍后重试')
  }
}

// 获取充值记录
const fetchRechargeHistory = async () => {
  loadingHistory.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    const response = await api.get('/wallet/recharge/history', { params })
    const data = response.data
    
    console.log('充值记录响应数据:', data) // 调试日志

    // 正确处理后端返回的数据格式
    // 后端返回的是 {first: [...], second: ...} 格式
    if (data && data.first && data.second !== undefined) {
      // 处理充值记录数组
      const records = Array.isArray(data.first) ? data.first : []
      console.log('处理后的记录:', records) // 调试日志
      rechargeHistory.value = records.map(record => ({
        ...record,
        createdAt: new Date(record.createdAt) // 确保时间字段正确解析
      }))
      pagination.total = typeof data.second === 'number' ? data.second : 0
    } else if (Array.isArray(data) && data.length === 2) {
      // 处理充值记录数组
      const records = Array.isArray(data[0]) ? data[0] : []
      console.log('处理后的记录:', records) // 调试日志
      rechargeHistory.value = records.map(record => ({
        ...record,
        createdAt: new Date(record.createdAt) // 确保时间字段正确解析
      }))
      pagination.total = typeof data[1] === 'number' ? data[1] : 0
    } else {
      // 兼容其他可能的返回格式
      let records = []
      if (Array.isArray(data)) {
        records = data
      } else if (data.content && Array.isArray(data.content)) {
        records = data.content
      } else if (data.list && Array.isArray(data.list)) {
        records = data.list
      }
      
      console.log('兼容格式记录:', records) // 调试日志
      rechargeHistory.value = records.map(record => ({
        ...record,
        createdAt: new Date(record.createdAt) // 确保时间字段正确解析
      }))
      
      pagination.total = data.totalElements || data.total || records.length || 0
    }
    
    console.log('最终充值记录:', rechargeHistory.value) // 调试日志
    console.log('总记录数:', pagination.total) // 调试日志
  } catch (error) {
    console.error('获取充值记录失败:', error)
    ElMessage.error('获取充值记录失败，请稍后重试')
  } finally {
    loadingHistory.value = false
  }
}

// 处理分页变化
const handleSizeChange = (val) => {
  pagination.size = val
  fetchRechargeHistory()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchRechargeHistory()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchBalance()
  fetchRechargeHistory()
})
</script>

<style scoped>
.account-recharge {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #666;
}

.left-column,
.right-column {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.balance-card,
.recharge-card,
.history-card {
  margin-bottom: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.balance-card,
.recharge-card {
  flex: 0 0 auto;
}

.history-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.history-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.balance-header,
.recharge-header,
.history-header {
  font-weight: bold;
  font-size: 16px;
}

.balance-content {
  text-align: center;
  padding: 20px 0;
}

.balance-amount {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.balance-info p {
  margin: 0;
  color: #666;
}

.amount-hints {
  margin-top: 15px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.amount-hint {
  cursor: pointer;
}

.amount-hint:hover {
  transform: translateY(-2px);
  transition: all 0.3s ease;
}

.recharge-button {
  width: 100%;
}

.positive-amount {
  color: #67c23a;
  font-weight: bold;
}

.negative-amount {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-table) {
  flex: 1;
}
</style>