<template>
  <div class="booking-training-view">
    <!-- 页面头部 -->
    <GlassHeaderCard title="课程预约" icon="📅">
      <template #description>
        选择您喜欢的时间和教练，开始您的乒乓球训练之旅
      </template>
      <template #headerActions>
        <el-button @click="refreshData" type="primary" :icon="Refresh" circle />
      </template>
    </GlassHeaderCard>

    <!-- 预约步骤 -->
    <GlassCard class="booking-steps-card">
      <el-steps :active="currentStep" finish-status="success" align-center>
        <el-step title="选择教练" description="选择合适的教练" />
        <el-step title="选择时间" description="选择上课时间" />
        <el-step title="填写信息" description="完善课程详情" />
        <el-step title="确认预约" description="确认并支付" />
      </el-steps>
    </GlassCard>

    <div class="content-container">
      <!-- 步骤1：选择教练 -->
      <div v-if="currentStep === 0">
        <GlassCard class="step-card">
          <template #header>
            <h3>选择教练</h3>
          </template>

          <!-- 教练筛选 -->
          <div class="coach-filters">
            <el-form :model="coachFilters" inline>
              <el-form-item label="教练等级">
                <el-select v-model="coachFilters.level" placeholder="全部等级" clearable>
                  <el-option label="初级教练" value="初级" />
                  <el-option label="中级教练" value="中级" />
                  <el-option label="高级教练" value="高级" />
                  <el-option label="特级教练" value="特级" />
                </el-select>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="loadCoaches">筛选</el-button>
                <el-button @click="resetFilters">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 教练列表 -->
          <div class="coaches-grid">
            <el-card
              v-for="coach in coaches"
              :key="coach.uuid"
              class="coach-card"
              :class="{ selected: selectedCoach?.uuid === coach.uuid }"
              @click="selectCoach(coach)"
            >
              <div class="coach-content">
                <el-avatar :size="60" :src="coach.photoUrl || undefined">
                  {{ coach.realName?.charAt(0) }}
                </el-avatar>
                <h4>{{ coach.realName }}</h4>
                <p class="coach-level">{{ coach.level }}</p>
                <div class="coach-price">¥{{ coach.hourlyRate }}/小时</div>
              </div>
            </el-card>
          </div>

          <template #footer>
            <div class="step-actions">
              <PrimaryButton
                @click="nextStep"
                :disabled="!selectedCoach"
                v-if="coaches.length > 0"
              >
                下一步
                <template #icon-right>
                  <el-icon><ArrowRight /></el-icon>
                </template>
              </PrimaryButton>
              <el-alert
                v-else
                title="暂无符合条件的教练"
                type="info"
                show-icon
                style="margin-top: 20px"
              />
            </div>
          </template>
        </GlassCard>
      </div>

      <!-- 步骤2：选择时间 -->
      <div v-if="currentStep === 1">
        <GlassCard class="step-card">
          <template #header>
            <h3>选择上课时间</h3>
            <div class="selected-coach-info">
              <el-avatar :size="32" :src="selectedCoach?.photoUrl || undefined">
                {{ selectedCoach?.realName?.charAt(0) }}
              </el-avatar>
              <span>{{ selectedCoach?.realName }}</span>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <h4>选择日期</h4>
              <el-calendar v-model="selectedDate" @panel-change="loadCoachSchedule">
                <template #date-cell="{ data }">
                  <div
                    class="calendar-cell"
                    :class="{
                      available: hasAvailableSlots(data.day),
                      selected: selectedDate && formatDate(selectedDate) === data.day,
                    }"
                    @click="selectDate(data.day)"
                  >
                    <span class="date-text">{{ data.day.split('-')[2] }}</span>
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
                <el-card
                  v-for="slot in getAvailableSlots(formatDate(selectedDate))"
                  :key="slot.id"
                  class="time-slot"
                  :class="{ selected: selectedTimeSlot?.id === slot.id }"
                  @click="selectTimeSlot(slot)"
                >
                  <div class="slot-content">
                    <div class="slot-time">{{ slot.startTime }} - {{ slot.endTime }}</div>
                    <div class="slot-duration">{{ slot.duration }}分钟</div>
                  </div>
                </el-card>
              </div>
              <div v-else class="no-date">
                <el-empty description="请先选择日期" />
              </div>
            </el-col>
          </el-row>

          <template #footer>
            <div class="step-actions">
              <OutlineButton @click="prevStep">
                <template #icon-left>
                  <el-icon><ArrowLeft /></el-icon>
                </template>
                上一步
              </OutlineButton>
              <PrimaryButton
                @click="nextStep"
                :disabled="!selectedTimeSlot"
                v-if="selectedDate"
              >
                下一步
                <template #icon-right>
                  <el-icon><ArrowRight /></el-icon>
                </template>
              </PrimaryButton>
            </div>
          </template>
        </GlassCard>
      </div>

      <!-- 步骤3：填写信息 -->
      <div v-if="currentStep === 2">
        <GlassCard class="step-card">
          <template #header>
            <h3>完善课程信息</h3>
          </template>

          <el-form
            ref="courseFormRef"
            :model="courseForm"
            :rules="courseRules"
            label-width="120px"
            class="course-form"
          >
            <el-form-item label="课程标题" prop="title">
              <el-input v-model="courseForm.title" placeholder="请输入课程标题" />
            </el-form-item>

            <el-form-item label="课程描述">
              <el-input
                v-model="courseForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入课程描述（可选）"
              />
            </el-form-item>

            <el-form-item label="上课地点" prop="location">
              <el-input v-model="courseForm.location" placeholder="请输入上课地点" />
            </el-form-item>

            <el-form-item label="特殊要求">
              <el-input
                v-model="courseForm.requirements"
                type="textarea"
                :rows="3"
                placeholder="请描述您的特殊要求或需要重点练习的内容（可选）"
              />
            </el-form-item>
          </el-form>

          <template #footer>
            <div class="step-actions">
              <OutlineButton @click="prevStep">
                <template #icon-left>
                  <el-icon><ArrowLeft /></el-icon>
                </template>
                上一步
              </OutlineButton>
              <PrimaryButton @click="nextStep">
                下一步
                <template #icon-right>
                  <el-icon><ArrowRight /></el-icon>
                </template>
              </PrimaryButton>
            </div>
          </template>
        </GlassCard>
      </div>

      <!-- 步骤4：确认预约 -->
      <div v-if="currentStep === 3">
        <GlassCard class="step-card">
          <template #header>
            <h3>确认预约信息</h3>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="教练">
              <div class="coach-info">
                <el-avatar :size="32" :src="selectedCoach?.photoUrl || undefined">
                  {{ selectedCoach?.realName?.charAt(0) }}
                </el-avatar>
                <span>{{ selectedCoach?.realName }}</span>
              </div>
            </el-descriptions-item>

            <el-descriptions-item label="教练等级">
              {{ selectedCoach?.level }}
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

            <el-descriptions-item label="课程费用">
              <span class="price-text">¥{{ calculatePrice() }}</span>
            </el-descriptions-item>

            <el-descriptions-item label="课程标题">
              {{ courseForm.title }}
            </el-descriptions-item>

            <el-descriptions-item label="上课地点" v-if="courseForm.location">
              {{ courseForm.location }}
            </el-descriptions-item>

            <el-descriptions-item label="课程描述" v-if="courseForm.description" :span="2">
              {{ courseForm.description }}
            </el-descriptions-item>

            <el-descriptions-item label="特殊要求" v-if="courseForm.requirements" :span="2">
              {{ courseForm.requirements }}
            </el-descriptions-item>
          </el-descriptions>

          <template #footer>
            <div class="step-actions">
              <OutlineButton @click="prevStep">
                <template #icon-left>
                  <el-icon><ArrowLeft /></el-icon>
                </template>
                上一步
              </OutlineButton>
              <PrimaryButton
                @click="confirmBooking"
                :loading="submitting"
              >
                <template #icon-left>
                  <el-icon><Check /></el-icon>
                </template>
                确认预约
              </PrimaryButton>
            </div>
          </template>
        </GlassCard>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {ArrowLeft, ArrowRight, Check, Refresh} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import GlassCard from '@/components/cards/base/GlassCard.vue'
import GlassHeaderCard from '@/components/cards/specialized/GlassHeaderCard.vue'

const route = useRoute()
const router = useRouter()

// 步骤控制
const currentStep = ref(0)
const submitting = ref(false)

// 数据
const coaches = ref([])
const selectedCoach = ref(null)
const selectedDate = ref(null)
const selectedTimeSlot = ref(null)
const coachSchedule = ref({})

// 教练筛选
const coachFilters = reactive({
  level: ''
})

// 课程表单
const courseForm = reactive({
  title: '',
  description: '',
  location: '',
  requirements: ''
})

const courseFormRef = ref()

// 表单验证规则
const courseRules = {
  title: [{ required: true, message: '请输入课程标题', trigger: 'blur' }],
  location: [{ required: true, message: '请输入上课地点', trigger: 'blur' }]
}

// 加载教练列表
const loadCoaches = async () => {
  try {
    const response = await api.get('/coach/coaches')
    coaches.value = response.data || []
  } catch (error) {
    ElMessage.error('获取教练列表失败: ' + (error.message || '未知错误'))
  }
}

// 重置筛选条件
const resetFilters = () => {
  coachFilters.level = ''
  loadCoaches()
}

// 选择教练
const selectCoach = (coach) => {
  selectedCoach.value = coach
}

// 刷新数据
const refreshData = () => {
  loadCoaches()
  if (currentStep.value === 1 && selectedCoach.value) {
    loadCoachSchedule()
  }
}

// 选择日期
const selectDate = (date) => {
  if (hasAvailableSlots(date)) {
    selectedDate.value = dayjs(date).toDate()
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
    const dateFrom = dayjs(selectedDate.value || new Date()).startOf('month').format('YYYY-MM-DD')
    const dateTo = dayjs(selectedDate.value || new Date()).endOf('month').format('YYYY-MM-DD')

    const response = await api.get(`/courses/coach-schedule/${selectedCoach.value.uuid}`, {
      params: { dateFrom, dateTo }
    })

    // 按日期分组课程
    const schedule = {}
    ;(response.data || []).forEach(course => {
      if (!schedule[course.date]) {
        schedule[course.date] = []
      }
      schedule[course.date].push(course)
    })

    coachSchedule.value = schedule
  } catch (error) {
    ElMessage.error('获取教练排班信息失败: ' + (error.message || '未知错误'))
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

// 计算价格
const calculatePrice = () => {
  if (!selectedCoach.value || !selectedTimeSlot.value) return 0
  const hours = selectedTimeSlot.value.duration / 60
  return Math.round(selectedCoach.value.hourlyRate * hours)
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 下一步
const nextStep = async () => {
  // 表单验证
  if (currentStep.value === 2) {
    try {
      await courseFormRef.value.validate()
    } catch {
      return
    }
  }

  // 加载教练排班信息
  if (currentStep.value === 0 && selectedCoach.value) {
    await loadCoachSchedule()
  }

  currentStep.value++
}

// 上一步
const prevStep = () => {
  currentStep.value--
}

// 确认预约
const confirmBooking = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要预约这节课程吗？费用：¥${calculatePrice()}`,
      '确认预约',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    submitting.value = true

    const bookingData = {
      title: courseForm.title || `与${selectedCoach.value.realName}的课程`,
      description: courseForm.description,
      date: formatDate(selectedDate.value),
      startTime: selectedTimeSlot.value.startTime,
      endTime: selectedTimeSlot.value.endTime,
      location: courseForm.location || '默认场地',
      price: calculatePrice() * 100, // 转换为分
      coachId: selectedCoach.value.uuid,
      studentId: '', // 后端会从session获取
      campusId: selectedCoach.value.campusId
    }

    await api.post('/courses/create', bookingData)

    ElMessage.success('预约成功！')
    await router.push('/student/schedule')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('预约失败: ' + (error.message || '未知错误'))
    }
  } finally {
    submitting.value = false
  }
}

// 组件挂载
onMounted(() => {
  loadCoaches()

  // 如果URL中有教练ID，直接选中
  if (route.query.coachId) {
    setTimeout(() => {
      const coach = coaches.value.find(c => c.uuid === route.query.coachId)
      if (coach) {
        selectCoach(coach)
      }
    }, 500)
  }
})
</script>

<style scoped>
.booking-training-view {
  padding: 20px;
}

.content-container {
  margin-top: 20px;
}

.step-card {
  margin-bottom: 20px;
}

.coach-filters {
  margin-bottom: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.coaches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.coach-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.coach-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.coach-card.selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

.coach-content {
  text-align: center;
  padding: 16px;
}

.coach-level {
  margin: 8px 0;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.coach-price {
  font-weight: bold;
  color: var(--el-color-primary);
}

.selected-coach-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 4px;
}

.calendar-cell.available:hover {
  background: var(--el-color-primary-light-9);
}

.calendar-cell.selected {
  background: var(--el-color-primary);
  color: white;
}

.date-text {
  font-size: 16px;
  font-weight: bold;
}

.available-indicator {
  font-size: 10px;
  color: var(--el-color-success);
  margin-top: 2px;
}

.time-slots {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 400px;
  overflow-y: auto;
}

.time-slot {
  cursor: pointer;
  transition: all 0.3s ease;
}

.time-slot:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.time-slot.selected {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
}

.slot-content {
  text-align: center;
  padding: 12px;
}

.slot-time {
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.slot-duration {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.no-date {
  text-align: center;
  padding: 40px 0;
}

.coach-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-text {
  font-size: 18px;
  font-weight: bold;
  color: var(--el-color-warning);
}

.course-form {
  max-width: 600px;
  margin: 0 auto;
}

.step-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px 0;
}
</style>
