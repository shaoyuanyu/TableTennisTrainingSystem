<template>
  <div class="book-training">
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
          <div
            v-for="coach in availableCoaches"
            :key="coach.id"
            class="coach-item"
            :class="{ selected: selectedCoach?.id === coach.id }"
            @click="selectCoach(coach)"
          >
            <el-avatar :size="60" :src="coach.avatar || ''">
              {{ coach.realName ? coach.realName.charAt(0) : '?' }}
            </el-avatar>
            <h4>{{ coach.realName || '未知教练' }}</h4>
            <p>{{ coach.level || '未评级' }}</p>
            <div class="coach-price">
              ¥{{ coach.hourlyRate || getCoachPrice(coach.level) }}/小时
            </div>
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
                <div
                  class="calendar-cell"
                  :class="{
                    available: hasAvailableSlots(data.day),
                    selected: selectedDate === data.day,
                  }"
                  @click="() => selectDate(data.day)"
                >
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
              <div
                v-for="slot in getAvailableSlots(selectedDate)"
                :key="slot.id"
                class="time-slot"
                :class="{ selected: selectedTimeSlot?.id === slot.id }"
                @click="selectTimeSlot(slot)"
              >
                <div class="slot-time">{{ slot.startTime }} - {{ slot.endTime }}</div>
                <div class="slot-info">{{ slot.duration }}分钟</div>
              </div>

              <!-- 如果没有可用时间段，显示提示 -->
              <div v-if="getAvailableSlots(selectedDate).length === 0" style="text-align: center; padding: 20px; color: #999;">
                暂无可用时间段
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
              <el-option
                v-for="slot in getAvailableSlots(selectedDate)"
                :key="slot.NO"
                :label="`${slot.startTime} - ${slot.endTime}`"
                :value="slot.NO"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="选择球桌">
            <el-select v-model="selectedTable" placeholder="请选择球桌（可选）" clearable>
              <el-option
                v-for="table in availableTables"
                :key="table.id"
                :label="`${table.name} (${table.location})`"
                :value="table.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="特殊要求">
            <el-input
              v-model="courseForm.description"
              type="textarea"
              :rows="3"
              placeholder="请描述您的特殊要求或需要重点练习的内容"
            />
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

          <el-descriptions-item v-if="selectedTable" label="预定球桌">
            {{ availableTables.find((t) => t.id === selectedTable)?.name || '未知球桌' }}
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
import { onMounted, reactive, ref, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight, Check } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { getCoachSchedule, getCoachScheduleForStudent } from '@/api/courses'
import { useUserStore } from '@/stores/user'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'

// 从session中获取当前用户的UUID
const getCurrentUserUUID = async () => {
  try {
    // 首先尝试从userStore获取
    const userStore = useUserStore()
    if (userStore.userInfo?.id) {
      // 检查是否为有效的UUID格式
      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i
      if (uuidRegex.test(userStore.userInfo.id)) {
        return userStore.userInfo.id
      }
    }

    // 如果store中没有有效的UUID，尝试调用后端API获取用户信息
    try {
      const response = await api.get('/user/info')
      if (response.data?.uuid) {
        // 更新store中的用户信息
        userStore.userInfo.id = response.data.uuid
        // 更新localStorage中的用户信息
        const storedUserInfo = localStorage.getItem('userInfo')
        if (storedUserInfo) {
          const userInfo = JSON.parse(storedUserInfo)
          userInfo.id = response.data.uuid
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
        }
        return response.data.uuid
      }
    } catch (apiError) {
      console.error('获取用户信息API调用失败:', apiError)
    }

    // 如果API调用失败，尝试从localStorage获取
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
      const userInfo = JSON.parse(storedUserInfo)
      if (userInfo.id) {
        // 检查是否为有效的UUID格式
        const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i
        if (uuidRegex.test(userInfo.id)) {
          return userInfo.id
        }
      }
    }

    // 如果以上都没有，返回null
    return null
  } catch (error) {
    console.error('获取用户UUID时出错:', error)
    return null
  }
}

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
const userBalance = ref(0)
const paymentMethod = ref('balance')
const availableTables = ref([]) // 可用球桌列表
const selectedTable = ref(null) // 选择的球桌

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
    // 从双选关系中提取基础教练信息
    const coaches = relations.map((relation) => ({
      id: relation.coachId,
      realName: relation.coachName || '未知教练',
      level: '未评级', // 当前接口没有返回教练级别
      avatar: '',
      hourlyRate: 100, // 默认价格
    }))

    // 根据每个教练的ID获取详细信息
    const detailedCoaches = await Promise.all(
      coaches.map(async (coach) => {
        try {
          const detailResponse = await api.post('/coach/queryCoachByUuid', {
            username: coach.id,
          })
          const detail = detailResponse.data
          return {
            ...coach,
            level: detail?.level || '未评级',
            avatar: detail?.avatar || '',
            hourlyRate: detail?.hourlyRate || 100, // 使用教练信息中的价格，如果没有则使用默认价格
          }
        } catch (error) {
          console.error(`获取教练 ${coach.id} 详细信息失败:`, error)
          return coach // 返回原始信息
        }
      }),
    )

    availableCoaches.value = detailedCoaches
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
const selectDate = async (date) => {
  // 直接使用日期字符串，避免时区转换问题
  if (typeof date === 'string') {
    selectedDate.value = date
  } else {
    // 确保日期格式化时使用正确的格式，避免时区转换导致的日期偏移
    selectedDate.value = dayjs(date).format('YYYY-MM-DD')
  }
  selectedTimeSlot.value = null

  // 自动加载该日期的教练排班信息
  if (selectedCoach.value && selectedDate.value) {
    await loadCoachSchedule()
    // 等待DOM更新后再继续
    await nextTick()
  }
}

// 选择时间段
const selectTimeSlot = (slot) => {
  selectedTimeSlot.value = slot
  // 同时设置课程表单中的NO值
  courseForm.NO = slot.NO
}

// 加载教练排班
const loadCoachSchedule = async () => {
  // 确保教练ID和日期都存在且有效
  if (!selectedCoach.value?.id || !selectedDate.value) return

  // 简单验证教练ID是否为UUID格式
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i
  if (!uuidRegex.test(selectedCoach.value.id)) {
    console.error('无效的教练ID格式:', selectedCoach.value.id)
    ElMessage.error('教练信息无效')
    return
  }

  // 初始化排班数据
  const schedule = {}
  
  try {
    // 调用API获取教练在指定日期的排班信息
    // 确保日期格式为 YYYY-MM-DD 字符串，强制转换为字符串类型
    const params = {
      dateFrom: String(selectedDate.value),
      dateTo: String(selectedDate.value)
    }
    
    // 使用学生查询教练课表的专用接口
    const courses = await getCoachScheduleForStudent(selectedCoach.value.id, params)

    // 确保courses是数组
    const validCourses = Array.isArray(courses) ? courses : []
    
    // 定义固定的课程时间段（根据NO值）
    const fixedTimeSlots = [
      { id: 1, NO: 1, startTime: '08:00', endTime: '09:00', duration: 60 },
      { id: 2, NO: 2, startTime: '09:00', endTime: '10:00', duration: 60 },
      { id: 3, NO: 3, startTime: '10:00', endTime: '11:00', duration: 60 },
      { id: 4, NO: 4, startTime: '11:00', endTime: '12:00', duration: 60 },
      { id: 5, NO: 5, startTime: '14:00', endTime: '15:00', duration: 60 },
      { id: 6, NO: 6, startTime: '15:00', endTime: '16:00', duration: 60 },
      { id: 7, NO: 7, startTime: '16:00', endTime: '17:00', duration: 60 },
      { id: 8, NO: 8, startTime: '17:00', endTime: '18:00', duration: 60 },
      { id: 9, NO: 9, startTime: '19:00', endTime: '20:00', duration: 60 },
      { id: 10, NO: 10, startTime: '20:00', endTime: '21:00', duration: 60 }
    ]
    
    // 根据教练已有的课程计算出已占用的时间段
    const bookedSlots = validCourses.map(course => {
      // 根据开始时间确定NO值，处理不同的时间格式
      const startTime = course.startTime
      if (typeof startTime === 'string') {
        // 处理 "HH:MM:SS" 或 "HH:MM" 格式
        if (startTime.startsWith('08:00')) return 1
        if (startTime.startsWith('09:00')) return 2
        if (startTime.startsWith('10:00')) return 3
        if (startTime.startsWith('11:00')) return 4
        if (startTime.startsWith('14:00')) return 5
        if (startTime.startsWith('15:00')) return 6
        if (startTime.startsWith('16:00')) return 7
        if (startTime.startsWith('17:00')) return 8
        if (startTime.startsWith('19:00')) return 9
        if (startTime.startsWith('20:00')) return 10
      }
      return null
    }).filter(no => no !== null)
    
    // 过滤出空闲的时间段
    const availableSlots = fixedTimeSlots.filter(slot => !bookedSlots.includes(slot.NO))
    
    schedule[selectedDate.value] = availableSlots
    
    // 添加日志，显示从后端获取的数据和过滤后的空闲时间段
    console.log('从后端获取的教练排课数据:', validCourses)
    console.log('已占用的时间段NO:', bookedSlots)
    console.log('过滤后的空闲时间段:', availableSlots)
  } catch (error) {
    console.error('获取排班信息失败:', error)
    ElMessage.error('获取排班信息失败: ' + (error.message || '未知错误'))
    
    // 使用模拟数据作为备选方案
    const fixedTimeSlots = [
      { id: 1, NO: 1, startTime: '08:00', endTime: '09:00', duration: 60 },
      { id: 2, NO: 2, startTime: '09:00', endTime: '10:00', duration: 60 },
      { id: 5, NO: 5, startTime: '14:00', endTime: '15:00', duration: 60 },
      { id: 6, NO: 6, startTime: '15:00', endTime: '16:00', duration: 60 },
      { id: 9, NO: 9, startTime: '19:00', endTime: '20:00', duration: 60 }
    ]
    schedule[selectedDate.value] = fixedTimeSlots
    
    console.log('使用模拟数据作为备选方案:', fixedTimeSlots)
  }
  
  coachSchedule.value = schedule
  
  // 强制更新视图
  await nextTick()
}

// 获取用户余额
const fetchUserBalance = async () => {
  try {
    const response = await api.get('/wallet/balance')
    userBalance.value = response.data.balance || 0
    console.log('获取到的用户余额:', userBalance.value)
  } catch (error) {
    console.error('获取账户余额失败:', error)
    ElMessage.error('获取账户余额失败: ' + (error.message || '未知错误'))
  }
}

// 获取可用球桌列表
const fetchAvailableTables = async () => {
  try {
    // 调用后端API获取本校区的空闲球桌列表
    const response = await api.get('/campus/queryFreeTables')

    // 处理后端返回的球桌数据
    const tables = response.data || []
    availableTables.value = tables.map(table => ({
      id: table.id,
      name: `${table.indexInCampus}号球桌`,
      location: table.group || '未知区域'
    }))

    console.log('获取到的球桌列表:', availableTables.value)
  } catch (error) {
    console.error('获取球桌列表失败:', error)
    ElMessage.error('获取球桌列表失败: ' + (error.message || '未知错误'))
  }
}

// 检查日期是否有可用时段
const hasAvailableSlots = (date) => {
  const slots = coachSchedule.value[date] || []
  const hasSlots = slots.length > 0
  return hasSlots
}

// 获取可用时间段
const getAvailableSlots = (date) => {
  const slots = coachSchedule.value[date] || []
  // 将响应式数据转换为普通数组
  const plainSlots = Array.from(slots)
  return plainSlots
}

// 获取教练价格
const getCoachPrice = (level) => {
  const prices = {
    初级: 80,
    中级: 150,
    高级: 200,
  }
  return prices[level] || 100
}

// 计算价格
const calculatePrice = () => {
  if (!selectedCoach.value || !selectedTimeSlot.value) return 0
  const hours = selectedTimeSlot.value.duration / 60
  // 使用教练信息中的hourlyRate，如果没有则使用默认价格计算
  const pricePerHour = selectedCoach.value.hourlyRate || getCoachPrice(selectedCoach.value.level)
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
  if (currentStep.value === 0 && selectedCoach.value) {
    // 当从第一步进入第二步时，如果已选择教练和日期，则加载排班信息
    if (selectedDate.value) {
      await loadCoachSchedule()
    }
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

    // 使用新函数获取当前用户的UUID
    const studentUUID = await getCurrentUserUUID()

    // 确保用户信息存在
    const currentUser = userStore.userInfo || {}
    console.log('当前用户信息:', currentUser)

    // 确保日期是正确格式的字符串
    let dateString = ''
    if (selectedDate.value) {
      if (typeof selectedDate.value === 'string') {
        dateString = selectedDate.value
      } else if (selectedDate.value instanceof Date) {
        // 将Date对象格式化为'YYYY-MM-DD'字符串格式
        dateString = dayjs(selectedDate.value).format('YYYY-MM-DD')
      }
    }
    
    // 如果dateString仍然为空但selectedDate有值，则直接使用selectedDate
    if (!dateString && selectedDate.value) {
      dateString = selectedDate.value;
    }

    // 构建预约数据，确保所有字段类型正确
    const bookingData = {
      title: String(courseForm.title || ''),
      description: String(courseForm.description || ''),
      coachId: String(selectedCoach.value?.id || ''),
      studentId: String(studentUUID || currentUser.id || userStore.userId || ''),
      date: String(dateString || ''),
      NO: Number.parseInt(courseForm.NO) || 0,
      campusId: Number.parseInt(currentUser.campusId) || 0,
      price: Number.parseFloat(calculatePrice()) || 0.0,
      tableId: selectedTable.value ? String(selectedTable.value) : null
    }

    // 添加验证确保必填字段不为空
    if (!bookingData.coachId || bookingData.coachId === '') {
      throw new Error('教练信息缺失')
    }

    if (!bookingData.studentId || bookingData.studentId === '') {
      throw new Error('学生信息缺失，请重新登录')
    }

    if (!bookingData.date || bookingData.date === '') {
      throw new Error('预约日期缺失')
    }

    if (!bookingData.NO || bookingData.NO <= 0) {
      throw new Error('课程节数无效')
    }

    if (!bookingData.campusId || bookingData.campusId <= 0) {
      throw new Error('校区信息缺失')
    }

    console.log('预约提交数据:', bookingData)  // 添加日志，方便调试

    await api.post('/courses/book', bookingData)

    ElMessage.success('预约成功！')
    router.push('/student/schedule')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error('预约失败: ' + (error.message || '未知错误'))
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
  fetchAvailableTables() // 获取球桌信息

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
.book-training {
  padding: 20px;
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
