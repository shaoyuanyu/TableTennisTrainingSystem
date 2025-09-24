<template>
  <div class="schedule-calendar ultra">
    <!-- 控制面板 -->
    <div class="schedule-controls ultra">
      <div class="view-switch-group">
        <ModernButton 
          :variant="currentView === 'week' ? 'solid' : 'outline'"
          color="primary"
          @click="setView('week')"
          text="周视图"
          icon-left="📅"
        />
        <ModernButton 
          :variant="currentView === 'month' ? 'solid' : 'outline'"
          color="primary"
          @click="setView('month')"
          text="月视图"
          icon-left="🗓️"
        />
      </div>
      
      <div class="date-navigation ultra">
        <ModernButton 
          variant="glass"
          color="primary"
          @click="previousPeriod"
          icon-left="ArrowLeft"
          class="nav-btn ultra-btn"
          square
        />
        <span class="current-period ultra">{{ currentPeriodText }}</span>
        <ModernButton 
          variant="glass"
          color="primary"
          @click="nextPeriod"
          icon-left="ArrowRight"
          class="nav-btn ultra-btn"
          square
        />
        <ModernButton 
          variant="solid"
          color="primary"
          @click="goToToday"
          text="今天"
          icon-left="⭐"
          class="today-btn ultra-btn"
        />
      </div>
      
      <!-- 功能按钮 -->
      <div class="schedule-actions ultra">
        <ModernButton 
          variant="glass"
          color="primary"
          @click="refreshSchedule"
          text="刷新课表"
          icon-left="🔄"
          class="action-btn ultra-btn"
        />
        <ModernButton 
          variant="glass"
          color="primary"
          @click="exportSchedule"
          text="导出课表"
          icon-left="📤"
          class="action-btn ultra-btn"
        />
        <ModernButton 
          variant="glass"
          color="primary"
          @click="sendScheduleEmail"
          text="邮件发送"
          icon-left="📧"
          class="action-btn ultra-btn"
        />
      </div>
    </div>

    <!-- 周视图 -->
    <div v-if="currentView === 'week'" class="week-view ultra">
      <div class="week-header ultra">
        <div class="time-column">时间</div>
        <div 
          v-for="day in weekDays" 
          :key="day.date" 
          class="day-header" 
          :class="{ 'is-today': day.isToday }"
        >
          <div class="day-name">{{ day.name }}</div>
          <div class="day-date">{{ day.date }}</div>
        </div>
      </div>
      <div class="week-body">
        <div v-for="hour in timeSlots" :key="hour" class="time-row">
          <div class="time-column">{{ hour }}</div>
          <div 
            v-for="day in weekDays" 
            :key="`${day.date}-${hour}`" 
            class="day-cell"
          >
            <div 
              v-for="schedule in getSchedulesForDayAndHour(day.date, hour)" 
              :key="schedule.id"
              class="schedule-item ultra" 
              :class="getScheduleClass(schedule)"
              @click.stop="handleScheduleClick(schedule)"
            >
              <div class="schedule-title">{{ schedule.title }}</div>
              <div class="schedule-participant">
                <span class="field-icon">{{ isStudentView ? '🧑‍🏫' : '👤' }}</span>
                {{ isStudentView ? schedule.coachName : getStudentNames(schedule) }}
              </div>
              <div class="schedule-location">
                <span class="field-icon">📍</span>{{ schedule.location }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 月视图 -->
    <div v-if="currentView === 'month'" class="month-view ultra">
      <div class="month-header ultra">
        <div v-for="dayName in weekDayNames" :key="dayName" class="month-day-header">
          {{ dayName }}
        </div>
      </div>
      <div class="month-body">
        <div 
          v-for="week in monthWeeks" 
          :key="week[0]?.date || Math.random()" 
          class="month-week"
        >
          <div 
            v-for="day in week" 
            :key="day?.date || Math.random()" 
            class="month-day" 
            :class="{
              'is-today': day?.isToday,
              'is-other-month': day?.isOtherMonth,
              'has-schedules': day && getSchedulesForDay(day.date).length > 0,
            }" 
            @click="handleDayClick(day)"
          >
            <div v-if="day" class="day-number ultra">{{ day.dayNumber }}</div>
            <div v-if="day" class="day-schedules ultra">
              <div 
                v-for="schedule in getSchedulesForDay(day.date).slice(0, 3)" 
                :key="schedule.id"
                class="month-schedule-item ultra" 
                :class="getScheduleClass(schedule)"
                @click.stop="handleScheduleClick(schedule)" 
                :title="schedule.title"
              >
                <span class="dot-icon">●</span> {{ schedule.title }}
              </div>
              <div 
                v-if="getSchedulesForDay(day.date).length > 3" 
                class="more-schedules ultra"
              >
                +{{ getSchedulesForDay(day.date).length - 3 }} 更多
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog 
      v-model="showScheduleDialog" 
      :title="isStudentView ? '课程详情' : '教学详情'" 
      width="500px" 
      class="ultra-dialog"
    >
      <div v-if="selectedSchedule" class="schedule-detail ultra">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            <span class="field-icon">🏓</span> {{ selectedSchedule.title }}
          </el-descriptions-item>
          <el-descriptions-item :label="isStudentView ? '教练' : '学员'">
            <span class="field-icon">{{ isStudentView ? '🧑‍🏫' : '👤' }}</span> 
            {{ isStudentView ? selectedSchedule.coachName : getStudentNames(selectedSchedule) }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            <span class="field-icon">⏰</span> {{ formatScheduleTime(selectedSchedule) }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            <span class="field-icon">📍</span> {{ selectedSchedule.location }}
          </el-descriptions-item>
          <el-descriptions-item label="课程状态">
            <el-tag :type="getStatusTagType(selectedSchedule.status)">
              <span class="field-icon">🔖</span> {{ getStatusText(selectedSchedule.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedSchedule.description" label="课程描述">
            <span class="field-icon">📝</span> {{ selectedSchedule.description }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <div class="dialog-footer ultra">
          <DangerButton 
            v-if="!isStudentView && canCancelSchedule(selectedSchedule)" 
            @click="cancelSchedule"
            text="取消课程"
            icon-left="❌"
          />
          <ModernButton 
            v-if="isStudentView && canCancelSchedule(selectedSchedule)" 
            variant="danger"
            @click="cancelSchedule"
            text="取消预约"
            icon-left="❌"
          />
          <ModernButton 
            @click="showScheduleDialog = false"
            text="关闭"
            icon-left="✅"
          />
        </div>
    </el-dialog>

    <!-- 邮件发送对话框 -->
    <el-dialog
      v-model="showEmailDialog"
      title="📧 发送课表邮件"
      width="600px"
      :before-close="() => showEmailDialog = false"
    >
      <el-form :model="emailOptions" label-width="120px">
        <el-form-item label="收件人邮箱" required>
          <div class="email-recipients">
            <div 
              v-for="(email, index) in emailOptions.recipients" 
              :key="index"
              class="recipient-item"
            >
              <el-input
                v-model="emailOptions.recipients[index]"
                placeholder="请输入邮箱地址"
                type="email"
              />
              <el-button
                type="danger"
                text
                @click="removeEmailRecipient(index)"
                style="margin-left: 8px;"
              >
                删除
              </el-button>
            </div>
            <ModernButton
              variant="text"
              color="primary"
              @click="addEmailRecipient"
              text="+ 添加收件人"
              class="add-recipient-btn"
            />
          </div>
        </el-form-item>
        
        <el-form-item label="附件选项">
          <el-checkbox v-model="emailOptions.includeAttachment">
            包含 iCal 课表文件
          </el-checkbox>
        </el-form-item>
        
        <el-form-item label="提醒设置">
          <el-checkbox v-model="emailOptions.sendReminder">
            启用课程提醒邮件
          </el-checkbox>
        </el-form-item>
        
        <el-form-item label="邮件预览">
          <div class="email-preview">
            <p><strong>主题：</strong>您的乒乓球训练课表 - {{ dayjs().format('YYYY年MM月DD日') }}</p>
            <p><strong>内容：</strong>包含完整的训练课表安排和日历文件</p>
            <p><strong>收件人数：</strong>{{ emailOptions.recipients.filter(email => email.trim()).length }} 人</p>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <ModernButton 
            variant="outline"
            color="secondary"
            @click="showEmailDialog = false"
            text="取消"
          />
          <ModernButton
            variant="solid"
            color="primary"
            @click="handleEmailSend"
            :disabled="emailOptions.recipients.filter(email => email.trim()).length === 0"
            text="发送邮件"
          />
        </div>
      </template>
    </el-dialog>

    <!-- 加载状态 -->
    <el-loading 
      v-loading="loading" 
      element-loading-text="加载课表数据..." 
      element-loading-spinner="el-icon-loading"
      class="ultra-loading" 
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ModernButton, DangerButton } from '@/components/buttons'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { useScheduleSync } from '@/utils/scheduleSyncExamples'
import { useUserStore } from '@/stores/user'

// 定义组件属性
const props = defineProps({
  userType: {
    type: String,
    default: 'student', // 'student' 或 'coach'
    validator: (value) => ['student', 'coach'].includes(value)
  },
  userId: {
    type: String,
    required: true
  }
})

// 计算属性
const isStudentView = computed(() => props.userType === 'student')

// 响应式数据
const currentView = ref('week')
const currentDate = ref(dayjs())
const schedules = ref([])
const loading = ref(false)
const showScheduleDialog = ref(false)
const selectedSchedule = ref(null)

// 邮件功能相关数据
const showEmailDialog = ref(false)
const emailOptions = ref({
  recipients: [],
  includeAttachment: true,
  sendReminder: false
})

// 初始化同步功能
const scheduleSync = useScheduleSync()

// 获取用户状态用于调试
const userStore = useUserStore()

// 时间段设置（8:00-22:00）
const timeSlots = [
  '08:00',
  '09:00',
  '10:00',
  '11:00',
  '12:00',
  '13:00',
  '14:00',
  '15:00',
  '16:00',
  '17:00',
  '18:00',
  '19:00',
  '20:00',
  '21:00',
]

// 顺序改为周一到周日
const weekDayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 计算属性
const currentPeriodText = computed(() => {
  if (currentView.value === 'week') {
    const weekStart = currentDate.value.startOf('week').add(1, 'day')
    const weekEnd = weekStart.add(6, 'day')
    return `${weekStart.format('YYYY年MM月DD日')} - ${weekEnd.format('MM月DD日')}`
  } else {
    return currentDate.value.format('YYYY年MM月')
  }
})

// 以周一开始
const weekDays = computed(() => {
  let weekStart = currentDate.value.startOf('week').add(1, 'day')
  const days = []
  for (let i = 0; i < 7; i++) {
    const day = weekStart.add(i, 'day')
    days.push({
      date: day.format('YYYY-MM-DD'),
      name: weekDayNames[i],
      dayNumber: day.date(),
      isToday: day.isSame(dayjs(), 'day'),
    })
  }
  return days
})

// 月视图顺序也调整为周一到周日
const monthWeeks = computed(() => {
  const monthStart = currentDate.value.startOf('month')
  const monthEnd = currentDate.value.endOf('month')
  const calendarStart = monthStart.startOf('week').add(1, 'day')
  const calendarEnd = monthEnd.endOf('week').add(1, 'day')
  const weeks = []
  let currentWeekStart = calendarStart

  while (currentWeekStart.isBefore(calendarEnd)) {
    const week = []
    for (let i = 0; i < 7; i++) {
      const day = currentWeekStart.add(i, 'day')
      week.push({
        date: day.format('YYYY-MM-DD'),
        dayNumber: day.date(),
        isToday: day.isSame(dayjs(), 'day'),
        isOtherMonth: !day.isSame(currentDate.value, 'month'),
      })
    }
    weeks.push(week)
    currentWeekStart = currentWeekStart.add(7, 'day')
  }

  return weeks
})

// 方法
const setView = (view) => {
  currentView.value = view
}

const previousPeriod = () => {
  if (currentView.value === 'week') {
    currentDate.value = currentDate.value.subtract(1, 'week')
  } else {
    currentDate.value = currentDate.value.subtract(1, 'month')
  }
}

const nextPeriod = () => {
  if (currentView.value === 'week') {
    currentDate.value = currentDate.value.add(1, 'week')
  } else {
    currentDate.value = currentDate.value.add(1, 'month')
  }
}

const goToToday = () => {
  currentDate.value = dayjs()
}

const getSchedulesForDay = (date) => {
  return schedules.value.filter((schedule) => schedule.date === date)
}

const getSchedulesForDayAndHour = (date, hour) => {
  return schedules.value.filter((schedule) => {
    if (schedule.date !== date) return false
    const scheduleHour = dayjs(schedule.startTime, 'HH:mm').format('HH:00')
    return scheduleHour === hour
  })
}

const getScheduleClass = (schedule) => {
  return [`schedule-${schedule.type?.toLowerCase() || 'individual'}`, `status-${schedule.status?.toLowerCase()}`]
}

const getStudentNames = (schedule) => {
  if (!schedule.students || schedule.students.length === 0) return '无学员'
  if (Array.isArray(schedule.students)) {
    return schedule.students.map(s => s.name || s.studentName).join('，')
  }
  return schedule.students
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'COMPLETED': 'info',
    'CANCELLED': 'danger'
  }
  return typeMap[status] || 'info'
}

const formatScheduleTime = (schedule) => {
  return `${schedule.date} ${schedule.startTime} - ${schedule.endTime}`
}

const canCancelSchedule = (schedule) => {
  const scheduleDateTime = dayjs(`${schedule.date} ${schedule.startTime}`)
  const now = dayjs()
  // 至少提前24小时取消
  return scheduleDateTime.diff(now, 'hour') >= 24
}

const handleDayClick = (day) => {
  if (day && !day.isOtherMonth) {
    currentDate.value = dayjs(day.date)
    if (currentView.value === 'month') {
      setView('week')
    }
  }
}

const handleScheduleClick = (schedule) => {
  selectedSchedule.value = schedule
  showScheduleDialog.value = true
}

const cancelSchedule = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个课程吗？取消后将无法恢复。', 
      '确认取消', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    loading.value = true
    // 根据用户类型调用不同的API
    const endpoint = isStudentView.value 
      ? `/courses/${selectedSchedule.value.id}/cancel` 
      : `/courses/${selectedSchedule.value.id}/cancel`
    
    await api.put(endpoint)

    // 更新本地数据
    const index = schedules.value.findIndex((s) => s.id === selectedSchedule.value.id)
    if (index !== -1) {
      schedules.value[index].status = 'CANCELLED'
    }

    showScheduleDialog.value = false
    ElMessage.success('课程已取消')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消课程失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

// 功能方法
const refreshSchedule = () => {
  loading.value = true
  fetchSchedules().finally(() => {
    ElMessage.success('课表已刷新')
  })
}

const exportSchedule = () => {
  try {
    const filename = `${isStudentView.value ? '学生' : '教练'}课表_${dayjs().format('YYYY-MM-DD')}.ics`
    scheduleSync.exportSchedule(schedules.value, filename)
    ElMessage.success('课表导出成功！')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('课表导出失败，请重试')
  }
}

const sendScheduleEmail = () => {
  showEmailDialog.value = true
}

const handleEmailSend = async () => {
  try {
    if (emailOptions.value.recipients.filter(email => email.trim()).length === 0) {
      ElMessage.warning('请至少添加一个收件人')
      return
    }
    
    ElMessage.info('正在发送邮件...')
    
    // 获取收件人邮箱
    const recipients = emailOptions.value.recipients.map(email => ({
      name: email.split('@')[0], // 简单从邮箱提取名称
      email: email
    }))
    
    // 批量发送邮件
    for (const recipient of recipients) {
      await scheduleSync.sendEmail('schedule', {
        email: recipient.email,
        name: recipient.name,
        scheduleData: schedules.value
      })
    }
    
    ElMessage.success(`邮件发送成功！共发送 ${recipients.length} 封邮件`)
    showEmailDialog.value = false
    
    // 如果启用了提醒，也发送课程提醒
    if (emailOptions.value.sendReminder) {
      setTimeout(() => {
        ElMessage.info('课程提醒也将在适当时间自动发送')
      }, 1000)
    }
    
  } catch (error) {
    console.error('邮件发送失败:', error)
    ElMessage.error('邮件发送失败，请重试')
  }
}

const addEmailRecipient = () => {
  emailOptions.value.recipients.push('')
}

const removeEmailRecipient = (index) => {
  emailOptions.value.recipients.splice(index, 1)
}

const fetchSchedules = async () => {
  try {
    loading.value = true

    // 首先验证当前用户session状态
    try {
      const userInfoResponse = await api.get('/user/info')
      console.log('ScheduleCalendar - 当前用户session信息:', userInfoResponse.data)
    } catch (userInfoError) {
      console.error('ScheduleCalendar - 获取用户信息失败:', userInfoError)
    }

    let startDate, endDate
    if (currentView.value === 'week') {
      // 以周一为开始
      startDate = currentDate.value.startOf('week').add(1, 'day').format('YYYY-MM-DD')
      endDate = currentDate.value.endOf('week').add(1, 'day').format('YYYY-MM-DD')
    } else {
      startDate = currentDate.value.startOf('month').format('YYYY-MM-DD')
      endDate = currentDate.value.endOf('month').format('YYYY-MM-DD')
    }

    // 输出调试信息
    const userStore = useUserStore() // 获取用户store实例
    console.log('ScheduleCalendar - fetchSchedules 调试信息:', {
      userType: props.userType,
      userId: props.userId,
      isStudentView: isStudentView.value,
      startDate,
      endDate,
      userStoreState: {
        isLoggedIn: userStore.isLoggedIn,
        userId: userStore.userId,
        userRole: userStore.userRole,
        isCoach: userStore.isCoach,
        isStudent: userStore.isStudent,
        rawUserInfo: userStore.userInfo
      }
    })

    // 根据后端实际路由代码，学生和教练使用不同的端点
    // 学生：/courses/my-schedule (在学生权限组中)
    // 教练：/courses/coach-schedule (在教练权限组中)
    const endpoint = isStudentView.value ? '/courses/my-schedule' : '/courses/coach-schedule'
    
    console.log('ScheduleCalendar - 使用端点:', endpoint)
    
    const response = await api.get(endpoint, {
      params: { 
        dateFrom: startDate, 
        dateTo: endDate 
      },
    })

    schedules.value = response.data || []
    console.log('ScheduleCalendar - 成功获取课表数据:', schedules.value.length, '条记录')
  } catch (error) {
    console.error('ScheduleCalendar - 获取课表失败:', error)
    
    // 提供更详细的错误信息
    if (error.response?.status === 401) {
      const errorMsg = error.response?.data?.message || '认证失败'
      ElMessage.error(`认证失败：${errorMsg}，请重新登录`)
      
      // 如果是认证问题，可能需要重新登录
      const userStore = useUserStore()
      if (userStore.isLoggedIn) {
        console.warn('检测到认证问题，但用户状态显示已登录，可能存在session不一致')
      }
    } else if (error.response?.status === 403) {
      const errorMsg = error.response?.data?.message || '权限不足'
      ElMessage.error(`权限错误：${errorMsg}`)
      
      // 输出更多调试信息
      console.error('权限错误详情:', {
        status: error.response.status,
        data: error.response.data,
        userType: props.userType,
        isStudentView: isStudentView.value
      })
    } else if (error.response?.status === 422) {
      const errorMsg = error.response?.data?.message || '请求参数错误'
      ElMessage.error(`参数错误：${errorMsg}`)
    } else {
      const errorMsg = error.response?.data?.message || '获取课表失败'
      ElMessage.error(`请求失败：${errorMsg}，请重试`)
    }
  } finally {
    loading.value = false
  }
}

// 监听视图和日期变化
watch([currentView, currentDate], fetchSchedules, { immediate: false })

// 组件挂载时获取数据
onMounted(() => {
  fetchSchedules()
})

// 暴露方法给父组件
defineExpose({
  refreshSchedule
})
</script>

<style scoped>
.schedule-calendar.ultra {
  display: flex;
  flex-direction: column;
  font-family: var(--font-family-base);
  height: 100%;
}

.schedule-controls.ultra {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  background: var(--white-alpha-20);
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-lg);
  flex-wrap: wrap;
}

.view-switch-group {
  display: inline-flex;
  gap: var(--spacing-sm);
}

.today-btn.ultra-btn {
  background: var(--gradient-primary);
  color: var(--text-white) !important;
  border: 1px solid var(--white-alpha-30);
  border-radius: var(--radius-full);
  padding: var(--spacing-sm) var(--spacing-md);
  font-weight: 600;
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.today-btn.ultra-btn .btn-icon {
  font-size: 1.2em;
}

.today-btn.ultra-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-xl);
}

.nav-btn.ultra-btn {
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  color: var(--text-white);
  border: 1px solid var(--white-alpha-30);
  border-radius: 50% !important;
  box-shadow: var(--shadow-md);
}

.date-navigation.ultra {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  background: var(--white-alpha-10);
  backdrop-filter: blur(10px);
  border: 1px solid var(--white-alpha-20);
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
}

.current-period.ultra {
  font-weight: 600;
  color: var(--text-white);
  min-width: 200px;
  text-align: center;
  font-size: 1.1rem;
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border: 1px solid var(--white-alpha-30);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.schedule-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.week-view.ultra,
.month-view.ultra {
  background: var(--white-alpha-10);
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 关键：允许子元素收缩 */
}

.week-header.ultra {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--white-alpha-30);
  flex-shrink: 0; /* 防止头部被压缩 */
}

.time-column {
  padding: var(--spacing-md);
  font-weight: 600;
  text-align: center;
  border-right: 1px solid var(--white-alpha-30);
  background: var(--white-alpha-10);
  color: var(--text-white-80);
}

.day-header {
  padding: var(--spacing-md) 0;
  text-align: center;
  border-right: 1px solid var(--white-alpha-30);
  color: var(--text-white-80);
  transition: all var(--transition-normal);
}

.day-header.is-today {
  background: var(--gradient-primary);
  color: var(--text-white);
  box-shadow: var(--shadow-md);
}

.day-name {
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
  font-size: 1rem;
}

.day-date {
  font-size: 1.2rem;
  font-weight: 700;
}

.week-body {
  flex: 1;
  overflow-y: auto;
  min-height: 0; /* 关键：允许内容区域收缩 */
}

.time-row {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  border-bottom: 1px solid var(--white-alpha-20);
  min-height: 64px;
}

.day-cell {
  border-right: 1px solid var(--white-alpha-20);
  padding: var(--spacing-xs);
  position: relative;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.day-cell:hover {
  background: var(--white-alpha-10);
}

.schedule-item.ultra {
  background: var(--gradient-primary);
  color: var(--text-white);
  padding: var(--spacing-sm);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-xs);
  font-size: 0.875rem;
  cursor: pointer;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--white-alpha-30);
  backdrop-filter: blur(10px);
  transition: all var(--transition-normal);
  display: flex;
  flex-direction: column;
  position: relative;
  align-items: flex-start;
}

.schedule-item.ultra .field-icon {
  font-size: 1em;
  margin-right: var(--spacing-xs);
  vertical-align: middle;
}

.schedule-item.ultra .schedule-title {
  font-weight: 600;
  margin-bottom: var(--spacing-xs);
  font-size: 0.875rem;
}

.schedule-item.ultra .schedule-participant,
.schedule-item.ultra .schedule-location {
  font-size: 0.75rem;
  opacity: 0.9;
  margin-top: 2px;
  font-weight: 400;
}

.schedule-item.ultra.schedule-individual {
  background: var(--gradient-primary);
}

.schedule-item.ultra.schedule-group {
  background: var(--gradient-success);
}

.schedule-item.ultra.schedule-match {
  background: var(--gradient-warning);
}

.schedule-item.ultra.schedule-tournament {
  background: var(--gradient-danger);
}

.schedule-item.ultra.status-pending {
  opacity: 0.7;
  border: 1px dashed var(--white-alpha-50);
}

.schedule-item.ultra.status-cancelled {
  background: var(--color-gray-500);
  text-decoration: line-through;
  opacity: 0.6;
}

.month-header.ultra {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--white-alpha-30);
  flex-shrink: 0; /* 防止头部被压缩 */
}

.month-day-header {
  padding: var(--spacing-md) 0;
  text-align: center;
  font-weight: 600;
  border-right: 1px solid var(--white-alpha-30);
  color: var(--text-white-80);
  font-size: 1rem;
}

.month-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0; /* 关键：允许内容区域收缩 */
}

.month-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid var(--white-alpha-20);
  flex: 1;
  min-height: 0; /* 关键：允许行被压缩 */
}

.month-day {
  min-height: 120px;
  padding: var(--spacing-sm);
  border-right: 1px solid var(--white-alpha-20);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  display: flex;
  flex-direction: column;
}

.month-day:hover {
  background: var(--white-alpha-10);
  box-shadow: var(--shadow-sm);
}

.month-day.is-today {
  background: var(--gradient-primary);
  border: 2px solid var(--white-alpha-50);
  box-shadow: var(--shadow-lg);
}

.month-day.is-other-month {
  color: var(--text-white-40);
  opacity: 0.5;
}

.month-day.has-schedules .day-number.ultra {
  font-weight: 700;
  color: var(--text-white);
}

.day-number.ultra {
  font-size: 1.125rem;
  margin-bottom: var(--spacing-xs);
  font-weight: 600;
  color: var(--text-white-80);
}

.day-schedules.ultra {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  flex: 1;
}

.month-schedule-item.ultra {
  background: var(--gradient-primary);
  color: var(--text-white);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  cursor: pointer;
  transition: all var(--transition-normal);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: 1px solid var(--white-alpha-30);
  backdrop-filter: blur(10px);
  margin-bottom: 2px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.month-schedule-item.ultra .dot-icon {
  color: var(--text-white);
  font-size: 1.2em;
}

.month-schedule-item.ultra.schedule-group {
  background: var(--gradient-success);
}

.month-schedule-item.ultra.schedule-match {
  background: var(--gradient-warning);
}

.month-schedule-item.ultra.schedule-tournament {
  background: var(--gradient-danger);
}

.more-schedules.ultra {
  font-size: 0.75rem;
  color: var(--text-white-80);
  text-align: center;
  margin-top: 2px;
  font-weight: 600;
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border: 1px solid var(--white-alpha-30);
  border-radius: var(--radius-sm);
  padding: 2px 0;
}

.schedule-detail.ultra {
  padding: var(--spacing-lg) 0 var(--spacing-sm) 0;
  background: var(--white-alpha-10);
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-lg);
}

.schedule-detail.ultra .field-icon {
  font-size: 1.1em;
  margin-right: var(--spacing-xs);
  vertical-align: middle;
}

.ultra-dialog ::deep(.el-dialog__body) {
  background: var(--white-alpha-20);
  backdrop-filter: blur(20px);
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
}

.dialog-footer.ultra {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding-top: var(--spacing-md);
}

.ultra-loading {
  z-index: 9999;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .schedule-controls.ultra {
    flex-direction: column;
    gap: var(--spacing-md);
    padding: var(--spacing-lg);
  }

  .date-navigation.ultra {
    justify-content: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-sm);
  }

  .current-period.ultra {
    min-width: auto;
    font-size: 1rem;
    padding: var(--spacing-xs);
  }

  .week-header.ultra,
  .time-row {
    grid-template-columns: 64px repeat(7, 1fr);
  }

  .time-column {
    padding: var(--spacing-xs);
    font-size: 0.75rem;
  }

  .day-header {
    padding: var(--spacing-xs);
    font-size: 0.875rem;
  }

  .day-name {
    font-size: 0.75rem;
  }

  .day-date {
    font-size: 0.9375rem;
  }

  .month-day {
    min-height: 78px;
    padding: var(--spacing-xs);
    font-size: 0.75rem;
  }

  .day-number.ultra {
    font-size: 0.875rem;
    margin-bottom: var(--spacing-xs);
  }

  .month-schedule-item.ultra {
    font-size: 0.6875rem;
    padding: 2px var(--spacing-xs);
  }

  .schedule-detail.ultra {
    padding: var(--spacing-sm) 2px 0 2px;
  }

  .ultra-dialog ::deep(.el-dialog__body) {
    padding: var(--spacing-md);
  }

  .dialog-footer.ultra {
    gap: var(--spacing-sm);
  }
  
  .schedule-actions {
    width: 100%;
    justify-content: center;
  }
}
</style>