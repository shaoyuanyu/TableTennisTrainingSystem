<template>
  <div class="schedule-course">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>课程预约</h2>
      <p>选择您喜欢的时间和教练，开始您的乒乓球学习之旅</p>
    </div>

    <!-- 预约步骤 -->
    <el-steps :active="currentStep" finish-status="success" class="booking-steps">
      <el-step title="选择教练" description="选择合适的教练" />
      <el-step title="选择时间" description="选择上课时间" />
      <el-step title="课程信息" description="填写课程详情" />
      <el-step title="确认预约" description="确认并支付" />
    </el-steps>

    <!-- 步骤内容 -->
    <el-card class="step-content">
      <!-- 步骤1：选择教练 -->
      <div v-if="currentStep === 0" class="step-coach">
        <h3>选择教练</h3>

        <!-- 教练筛选 -->
        <el-form :model="coachFilters" :inline="true" class="coach-filters">
          <el-form-item label="教练等级">
            <el-select v-model="coachFilters.level" placeholder="全部等级" clearable>
              <el-option label="初级教练" value="初级" />
              <el-option label="中级教练" value="中级" />
              <el-option label="高级教练" value="高级" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="searchCoaches">筛选</el-button>
          </el-form-item>
        </el-form>

        <!-- 教练列表 -->
        <div class="coaches-grid">
          <div v-for="coach in availableCoaches" :key="coach.id" class="coach-item"
            :class="{ selected: selectedCoach?.id === coach.id }" @click="selectCoach(coach)">
            <el-avatar :size="60" :src="coach.avatar || ''">
              {{ coach.realName ? coach.realName.charAt(0) : '?' }}
            </el-avatar>
            <h4>{{ coach.realName || '未知教练' }}</h4>
            <p>{{ coach.level || '未评级' }}</p>
            <div class="coach-rating" v-if="coach.rating">
              <el-rate v-model="coach.rating" disabled show-score text-color="#ff9900" score-template="{value}" />
            </div>
            <div class="coach-price">¥{{ getCoachPrice(coach.level) }}/小时</div>
          </div>
        </div>
      </div>

      <!-- 步骤2：选择时间 -->
      <div v-if="currentStep === 1" class="step-time">
        <h3>选择上课时间</h3>

        <el-row :gutter="20">
          <el-col :span="12">
            <h4>选择日期</h4>
            <el-calendar v-model="selectedDate" @panel-change="loadCoachSchedule">
              <template #date-cell="{ data }">
                <div class="calendar-cell" :class="{
                  available: hasAvailableSlots(data.day),
                  selected: selectedDate === data.day,
                }" @click="selectDate(data.day)">
                  <span class="date-text">{{ data.day.split('-').slice(2).join('') }}</span>
                  <div v-if="hasAvailableSlots(data.day)" class="available-indicator">
                    {{ getAvailableSlots(data.day).length }}个时段
                  </div>
                </div>
              </template>
            </el-calendar>
          </el-col>

          <el-col :span="12">
            <h4>选择时间段</h4>
            <div v-if="selectedDate" class="time-slots">
              <div v-for="slot in getAvailableSlots(selectedDate)" :key="slot.id" class="time-slot"
                :class="{ selected: selectedTimeSlot?.id === slot.id }" @click="selectTimeSlot(slot)">
                <div class="slot-time">{{ slot.startTime }} - {{ slot.endTime }}</div>
                <div class="slot-info">{{ slot.duration }}分钟</div>
              </div>
            </div>
            <div v-else class="no-date">
              <el-empty description="请先选择日期" />
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 步骤3：课程信息 -->
      <div v-if="currentStep === 2" class="step-info">
        <h3>课程信息</h3>

        <el-form ref="courseFormRef" :model="courseForm" :rules="courseRules" label-width="120px">
          <el-form-item label="课程标题" prop="title">
            <el-input v-model="courseForm.title" placeholder="请输入课程标题" />
          </el-form-item>

          <el-form-item label="课程节数" prop="NO">
            <el-select v-model="courseForm.NO" placeholder="请选择课程节数">
              <el-option label="第1节" :value="1" />
              <el-option label="第2节" :value="2" />
              <el-option label="第3节" :value="3" />
              <el-option label="第4节" :value="4" />
            </el-select>
          </el-form-item>

          <el-form-item label="特殊要求">
            <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="请描述您的特殊要求或需要重点练习的内容" />
          </el-form-item>
        </el-form>
      </div>

      <!-- 步骤4：确认预约 -->
      <div v-if="currentStep === 3" class="step-confirm">
        <h3>确认预约信息</h3>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="教练">
            <div class="coach-info">
              <el-avatar :size="32" :src="selectedCoach?.avatar || ''">
                {{ selectedCoach?.realName ? selectedCoach?.realName.charAt(0) : '?' }}
              </el-avatar>
              <span>{{ selectedCoach?.realName || '未知教练' }}</span>
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="教练等级">
            {{ selectedCoach?.level || '未评级' }}
          </el-descriptions-item>

          <el-descriptions-item label="上课日期">
            {{ formatDate(selectedDate) }}
          </el-descriptions-item>

          <el-descriptions-item label="上课时间">
            {{ selectedTimeSlot?.startTime }} - {{ selectedTimeSlot?.endTime }}
          </el-descriptions-item>

          <el-descriptions-item label="课程时长">
            {{ selectedTimeSlot?.duration }}分钟
          </el-descriptions-item>

          <el-descriptions-item label="课程标题">
            {{ courseForm.title }}
          </el-descriptions-item>

          <el-descriptions-item label="课程费用">
            <span class="price-text">¥{{ calculatePrice() }}</span>
          </el-descriptions-item>

          <el-descriptions-item v-if="courseForm.description" label="特殊要求" :span="2">
            {{ courseForm.description }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 支付方式 -->
        <div class="payment-section">
          <h4>支付方式</h4>
          <el-radio-group v-model="paymentMethod">
            <el-radio label="balance"> 账户余额 (当前余额：¥{{ userBalance }}) </el-radio>
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
          </el-radio-group>
        </div>
      </div>
    </el-card>

    <!-- 操作按钮 -->
    <div class="step-actions">
      <OutlineButton v-if="currentStep > 0" @click="prevStep">
        <template #icon-left>
          <el-icon>
            <ArrowLeft />
          </el-icon>
        </template>
        上一步
      </OutlineButton>

      <PrimaryButton v-if="currentStep < 3" @click="nextStep" :disabled="!canProceed()">
        下一步
        <template #icon-right>
          <el-icon>
            <ArrowRight />
          </el-icon>
        </template>
      </PrimaryButton>

      <PrimaryButton v-if="currentStep === 3" @click="confirmBooking" :loading="submitting">
        <template #icon-left>
          <el-icon>
            <Check />
          </el-icon>
        </template>
        确认预约
      </PrimaryButton>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, Check } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 步骤控制
const currentStep = ref(0)
const submitting = ref(false)

// 选择的数据
const selectedCoach = ref(null)
const selectedDate = ref('')
const selectedTimeSlot = ref(null)
const availableCoaches = ref([])
const coachSchedule = ref({})
const userBalance = ref(0) // 默认余额0元，实际应从用户信息获取
const paymentMethod = ref('balance')

// 教练筛选
const coachFilters = reactive({
  level: '',
})

// 课程表单
const courseForm = reactive({
  title: '',
  NO: 1,
  description: '',
})

const courseFormRef = ref()

// 表单验证规则
const courseRules = {
  title: [{ required: true, message: '请输入课程标题', trigger: 'blur' }],
  NO: [{ required: true, message: '请选择课程节数', trigger: 'change' }],
}

// 搜索教练（获取已建立双选关系的教练）
const searchCoaches = async () => {
  try {
    const response = await api.get('/mutual-selection/student/current-coaches')
    // 后端返回的是双选关系列表，需要提取教练信息
    const relations = response.data || []
    // 从双选关系中提取教练信息
    availableCoaches.value = relations.map(relation => ({
      id: relation.coachId,
      realName: relation.coachName || '未知教练',
      // 由于后端接口没有返回教练的级别和价格信息，我们使用默认值
      level: '未评级', 
      avatar: '',
      // 添加默认价格，后续可以通过其他方式获取
      hourlyRate: 100
    }))
    console.log('获取到的教练列表:', availableCoaches.value)
  } catch (error) {
    console.error('获取已建立双选关系的教练列表失败:', error)
    ElMessage.error('获取教练列表失败: ' + (error.message || '未知错误'))
  }
}

// 选择教练
const selectCoach = (coach) => {
  selectedCoach.value = coach
}

// 选择日期
const selectDate = (date) => {
  if (hasAvailableSlots(date)) {
    selectedDate.value = date
    selectedTimeSlot.value = null
  }
}

// 选择时间段
const selectTimeSlot = (slot) => {
  selectedTimeSlot.value = slot
}

// 加载教练排班
const loadCoachSchedule = async () => {
  if (!selectedCoach.value) return

  try {
    const month = dayjs(selectedDate.value || new Date()).format('YYYY-MM')
    // 这里应该调用获取教练课表的API
    // 模拟数据
    const schedule = {}
    for (let i = 1; i <= 30; i++) {
      const date = dayjs().add(i, 'day').format('YYYY-MM-DD')
      schedule[date] = [
        { id: 1, startTime: '09:00', endTime: '10:00', duration: 60 },
        { id: 2, startTime: '14:00', endTime: '15:00', duration: 60 },
        { id: 3, startTime: '19:00', endTime: '20:00', duration: 60 }
      ]
    }
    coachSchedule.value = schedule
  } catch (error) {
    console.error('获取排班信息失败:', error)
    ElMessage.error('获取排班信息失败')
  }
}

// 获取用户余额
const fetchUserBalance = async () => {
  try {
    // 获取学生账户余额
    const response = await api.get('/wallet/balance')
    userBalance.value = response.data.balance || 0
    console.log('获取到的用户余额:', userBalance.value)
  } catch (error) {
    console.error('获取账户余额失败:', error)
    ElMessage.error('获取账户余额失败')
  }
}

// 检查日期是否有可用时段
const hasAvailableSlots = (date) => {
  return (coachSchedule.value[date] || []).length > 0
}

// 获取可用时间段
const getAvailableSlots = (date) => {
  return coachSchedule.value[date] || []
}

// 获取教练价格
const getCoachPrice = (level) => {
  // 由于从双选关系接口获取不到教练级别，我们需要使用默认价格
  // 或者后续通过其他方式获取教练详细信息
  const prices = {
    '初级': 80,
    '中级': 150,
    '高级': 200
  }
  // 如果没有级别信息，返回默认价格
  return prices[level] || 100
}

// 计算价格
const calculatePrice = () => {
  if (!selectedCoach.value || !selectedTimeSlot.value) return 0
  const hours = selectedTimeSlot.value.duration / 60
  const pricePerHour = getCoachPrice(selectedCoach.value.level)
  return Math.round(pricePerHour * hours)
}

// 检查是否可以进入下一步
const canProceed = () => {
  switch (currentStep.value) {
    case 0:
      return selectedCoach.value !== null
    case 1:
      return selectedDate.value && selectedTimeSlot.value
    case 2:
      return courseForm.title && courseForm.NO
    default:
      return false
  }
}

// 下一步
const nextStep = async () => {
  if (currentStep.value === 1 && selectedCoach.value) {
    await loadCoachSchedule()
  }

  if (currentStep.value === 2) {
    // 验证表单
    try {
      await courseFormRef.value?.validate()
    } catch {
      return
    }
  }

  if (canProceed()) {
    currentStep.value++
  }
}

// 上一步
const prevStep = () => {
  currentStep.value--
}

// 确认预约
const confirmBooking = async () => {
  try {
    await ElMessageBox.confirm(`确定要预约这节课程吗？费用：¥${calculatePrice()}`, '确认预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    submitting.value = true

    const bookingData = {
      title: courseForm.title,
      description: courseForm.description,
      coachId: selectedCoach.value.id,
      studentId: userStore.user.id,
      date: selectedDate.value,
      NO: courseForm.NO,
      campusId: userStore.user.campusId,
      price: calculatePrice(),
      tableId: null // 球桌ID，可以为空，后端会自动分配
    }

    await api.post('/courses/book', bookingData)

    ElMessage.success('预约成功！')
    router.push('/student/schedule')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error('预约失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

// 组件挂载
onMounted(() => {
  searchCoaches()
  fetchUserBalance()

  // 如果URL中有教练ID，直接选中
  if (route.query.coachId) {
    const coachId = route.query.coachId
    // 等待教练列表加载后选中
    setTimeout(() => {
      const coach = availableCoaches.value.find((c) => c.id === coachId)
      if (coach) {
        selectedCoach.value = coach
      }
    }, 1000)
  }
})
</script>

<style scoped>
.schedule-course {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.booking-steps {
  margin-bottom: 30px;
}

.step-content {
  margin-bottom: 30px;
  min-height: 400px;
}

.step-content h3 {
  margin: 0 0 20px 0;
  color: #333;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.coach-filters {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.coaches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.coach-item {
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.coach-item:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.coach-item.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.coach-item h4 {
  margin: 12px 0 4px 0;
  color: #333;
}

.coach-item p {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.coach-rating {
  margin-bottom: 8px;
}

.coach-price {
  font-weight: bold;
  color: #409eff;
}

.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
}

.calendar-cell.available:hover {
  background: #f0f9ff;
}

.calendar-cell.selected {
  background: #409eff;
  color: white;
}

.date-text {
  font-size: 16px;
  font-weight: bold;
}

.available-indicator {
  font-size: 10px;
  color: #67c23a;
  margin-top: 2px;
}

.time-slots {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.time-slot {
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.time-slot:hover {
  border-color: #409eff;
}

.time-slot.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.slot-time {
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.slot-info {
  font-size: 12px;
  color: #666;
}

.no-date {
  text-align: center;
  padding: 60px 0;
}

.coach-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-text {
  font-size: 18px;
  font-weight: bold;
  color: #e6a23c;
}

.payment-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.payment-section h4 {
  margin: 0 0 16px 0;
  color: #333;
}

.step-actions {
  text-align: center;
  padding: 20px 0;
}

.step-actions .btn-modern {
  margin: 0 8px;
  min-width: 120px;
}

:deep(.el-calendar-table .el-calendar-day) {
  height: 80px;
  padding: 4px;
}

:deep(.el-descriptions-item__label) {
  font-weight: 500;
}

:deep(.el-checkbox-group .el-checkbox) {
  margin-right: 20px;
  margin-bottom: 12px;
}
</style>