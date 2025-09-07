<template>
  <div class="schedule-view">
    <div class="schedule-header">
      <h2>我的课表</h2>
      <div class="schedule-controls">
        <el-button-group>
          <el-button
            :type="currentView === 'week' ? 'primary' : 'default'"
            @click="setView('week')"
          >
            周视图
          </el-button>
          <el-button
            :type="currentView === 'month' ? 'primary' : 'default'"
            @click="setView('month')"
          >
            月视图
          </el-button>
        </el-button-group>
        <div class="date-navigation">
          <el-button :icon="ArrowLeft" @click="previousPeriod" circle />
          <span class="current-period">{{ currentPeriodText }}</span>
          <el-button :icon="ArrowRight" @click="nextPeriod" circle />
          <el-button @click="goToToday">今天</el-button>
        </div>
      </div>
    </div>

    <!-- 周视图 -->
    <div v-if="currentView === 'week'" class="week-view">
      <div class="week-header">
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
            @click="handleCellClick(day.date, hour)"
          >
            <div
              v-for="schedule in getSchedulesForDayAndHour(day.date, hour)"
              :key="schedule.id"
              class="schedule-item"
              :class="getScheduleClass(schedule)"
              @click.stop="handleScheduleClick(schedule)"
            >
              <div class="schedule-title">{{ schedule.title }}</div>
              <div class="schedule-coach">{{ schedule.coach }}</div>
              <div class="schedule-location">{{ schedule.location }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 月视图 -->
    <div v-if="currentView === 'month'" class="month-view">
      <div class="month-header">
        <div v-for="dayName in weekDayNames" :key="dayName" class="month-day-header">
          {{ dayName }}
        </div>
      </div>
      <div class="month-body">
        <div v-for="week in monthWeeks" :key="week[0]?.date || Math.random()" class="month-week">
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
            <div v-if="day" class="day-number">{{ day.dayNumber }}</div>
            <div v-if="day" class="day-schedules">
              <div
                v-for="schedule in getSchedulesForDay(day.date).slice(0, 3)"
                :key="schedule.id"
                class="month-schedule-item"
                :class="getScheduleClass(schedule)"
                @click.stop="handleScheduleClick(schedule)"
              >
                {{ schedule.title }}
              </div>
              <div v-if="getSchedulesForDay(day.date).length > 3" class="more-schedules">
                +{{ getSchedulesForDay(day.date).length - 3 }} 更多
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="showScheduleDialog" title="课程详情" width="500px">
      <div v-if="selectedSchedule" class="schedule-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            {{ selectedSchedule.title }}
          </el-descriptions-item>
          <el-descriptions-item label="教练">
            {{ selectedSchedule.coach }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ formatScheduleTime(selectedSchedule) }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            {{ selectedSchedule.location }}
          </el-descriptions-item>
          <el-descriptions-item label="课程类型">
            <el-tag :type="getScheduleTagType(selectedSchedule.type)">
              {{ getScheduleTypeName(selectedSchedule.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程状态">
            <el-tag :type="getStatusTagType(selectedSchedule.status)">
              {{ getStatusName(selectedSchedule.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedSchedule.description" label="课程描述">
            {{ selectedSchedule.description }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showScheduleDialog = false">关闭</el-button>
          <el-button
            v-if="selectedSchedule?.status === 'confirmed' && canCancelSchedule(selectedSchedule)"
            type="danger"
            @click="cancelSchedule"
          >
            取消课程
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 加载状态 -->
    <el-loading
      v-loading="loading"
      element-loading-text="加载课表数据..."
      element-loading-spinner="el-icon-loading"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'

// 响应式数据
const currentView = ref('week')
const currentDate = ref(dayjs())
const schedules = ref([])
const loading = ref(false)
const showScheduleDialog = ref(false)
const selectedSchedule = ref(null)

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

const weekDayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

// 计算属性
const currentPeriodText = computed(() => {
  if (currentView.value === 'week') {
    const weekStart = currentDate.value.startOf('week')
    const weekEnd = currentDate.value.endOf('week')
    return `${weekStart.format('YYYY年MM月DD日')} - ${weekEnd.format('MM月DD日')}`
  } else {
    return currentDate.value.format('YYYY年MM月')
  }
})

const weekDays = computed(() => {
  const weekStart = currentDate.value.startOf('week')
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

const monthWeeks = computed(() => {
  const monthStart = currentDate.value.startOf('month')
  const monthEnd = currentDate.value.endOf('month')
  const calendarStart = monthStart.startOf('week')
  const calendarEnd = monthEnd.endOf('week')

  const weeks = []
  let currentWeekStart = calendarStart

  while (currentWeekStart.isBefore(calendarEnd) || currentWeekStart.isSame(calendarEnd, 'day')) {
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
    currentWeekStart = currentWeekStart.add(1, 'week')
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
  return [`schedule-${schedule.type}`, `status-${schedule.status}`]
}

const getScheduleTypeName = (type) => {
  const typeNames = {
    individual: '一对一训练',
    group: '小组训练',
    match: '比赛',
    tournament: '锦标赛',
  }
  return typeNames[type] || type
}

const getScheduleTagType = (type) => {
  const tagTypes = {
    individual: 'primary',
    group: 'success',
    match: 'warning',
    tournament: 'danger',
  }
  return tagTypes[type] || 'info'
}

const getStatusName = (status) => {
  const statusNames = {
    pending: '待确认',
    confirmed: '已确认',
    cancelled: '已取消',
    completed: '已完成',
  }
  return statusNames[status] || status
}

const getStatusTagType = (status) => {
  const tagTypes = {
    pending: 'warning',
    confirmed: 'success',
    cancelled: 'danger',
    completed: 'info',
  }
  return tagTypes[status] || 'info'
}

const formatScheduleTime = (schedule) => {
  return `${schedule.date} ${schedule.startTime} - ${schedule.endTime}`
}

const canCancelSchedule = (schedule) => {
  const scheduleDateTime = dayjs(`${schedule.date} ${schedule.startTime}`)
  const now = dayjs()
  return scheduleDateTime.diff(now, 'hour') >= 24 // 至少提前24小时取消
}

const handleCellClick = (date, hour) => {
  // 处理空白单元格点击，可以用于快速预约
  console.log('Clicked cell:', date, hour)
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
    await ElMessageBox.confirm('确定要取消这个课程吗？取消后将无法恢复。', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    loading.value = true
    await api.put(`/schedules/${selectedSchedule.value.id}/cancel`)

    // 更新本地数据
    const index = schedules.value.findIndex((s) => s.id === selectedSchedule.value.id)
    if (index !== -1) {
      schedules.value[index].status = 'cancelled'
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

const fetchSchedules = async () => {
  try {
    loading.value = true

    let startDate, endDate
    if (currentView.value === 'week') {
      startDate = currentDate.value.startOf('week').format('YYYY-MM-DD')
      endDate = currentDate.value.endOf('week').format('YYYY-MM-DD')
    } else {
      startDate = currentDate.value.startOf('month').format('YYYY-MM-DD')
      endDate = currentDate.value.endOf('month').format('YYYY-MM-DD')
    }

    const response = await api.get('/schedules/student', {
      params: { startDate, endDate },
    })

    schedules.value = response.data || []
  } catch (error) {
    console.error('获取课表失败:', error)
    // 使用模拟数据用于开发测试
    schedules.value = generateMockSchedules()
  } finally {
    loading.value = false
  }
}

// 生成模拟数据用于开发测试
const generateMockSchedules = () => {
  const mockSchedules = []
  const today = dayjs()

  // 生成接下来两周的一些示例课程
  for (let i = 0; i < 14; i++) {
    const date = today.add(i, 'day')

    // 随机生成一些课程
    if (Math.random() > 0.6) {
      const startHour = 9 + Math.floor(Math.random() * 10)
      const startTime = `${startHour.toString().padStart(2, '0')}:00`
      const endTime = `${(startHour + 1).toString().padStart(2, '0')}:00`

      mockSchedules.push({
        id: `mock-${i}-1`,
        title: ['一对一训练', '小组训练', '技术指导', '战术练习'][Math.floor(Math.random() * 4)],
        coach: ['张教练', '李教练', '王教练', '刘教练'][Math.floor(Math.random() * 4)],
        date: date.format('YYYY-MM-DD'),
        startTime,
        endTime,
        location: `训练场${Math.floor(Math.random() * 5) + 1}号`,
        type: ['individual', 'group', 'match'][Math.floor(Math.random() * 3)],
        status: ['confirmed', 'pending'][Math.floor(Math.random() * 2)],
        description: '这是一个示例课程描述',
      })
    }

    // 可能有第二个课程
    if (Math.random() > 0.8) {
      const startHour = 15 + Math.floor(Math.random() * 4)
      const startTime = `${startHour.toString().padStart(2, '0')}:00`
      const endTime = `${(startHour + 1).toString().padStart(2, '0')}:00`

      mockSchedules.push({
        id: `mock-${i}-2`,
        title: '比赛训练',
        coach: '陈教练',
        date: date.format('YYYY-MM-DD'),
        startTime,
        endTime,
        location: `比赛场地`,
        type: 'match',
        status: 'confirmed',
        description: '准备下周比赛的专项训练',
      })
    }
  }

  return mockSchedules
}

// 监听视图和日期变化
watch([currentView, currentDate], fetchSchedules, { immediate: false })

// 组件挂载时获取数据
onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
.schedule-view {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.schedule-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-header h2 {
  margin: 0;
  color: #333;
}

.schedule-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.date-navigation {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-period {
  font-weight: 500;
  color: #333;
  min-width: 200px;
  text-align: center;
}

/* 周视图样式 */
.week-view {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.week-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.time-column {
  padding: 15px 10px;
  font-weight: 500;
  text-align: center;
  border-right: 1px solid #e0e0e0;
  background: #f8f9fa;
}

.day-header {
  padding: 15px;
  text-align: center;
  border-right: 1px solid #e0e0e0;
}

.day-header.is-today {
  background: #e3f2fd;
  color: #1976d2;
}

.day-name {
  font-weight: 500;
  margin-bottom: 5px;
}

.day-date {
  font-size: 20px;
  font-weight: 600;
}

.week-body {
  max-height: 600px;
  overflow-y: auto;
}

.time-row {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  border-bottom: 1px solid #e0e0e0;
  min-height: 60px;
}

.day-cell {
  border-right: 1px solid #e0e0e0;
  padding: 5px;
  position: relative;
  cursor: pointer;
  transition: background-color 0.2s;
}

.day-cell:hover {
  background: #f5f5f5;
}

.schedule-item {
  background: #2196f3;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  margin-bottom: 2px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.schedule-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.schedule-item.schedule-individual {
  background: #2196f3;
}

.schedule-item.schedule-group {
  background: #4caf50;
}

.schedule-item.schedule-match {
  background: #ff9800;
}

.schedule-item.schedule-tournament {
  background: #f44336;
}

.schedule-item.status-pending {
  opacity: 0.7;
  border: 2px dashed rgba(255, 255, 255, 0.5);
}

.schedule-item.status-cancelled {
  background: #9e9e9e;
  text-decoration: line-through;
}

.schedule-title {
  font-weight: 500;
  margin-bottom: 2px;
}

.schedule-coach,
.schedule-location {
  font-size: 10px;
  opacity: 0.9;
}

/* 月视图样式 */
.month-view {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.month-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.month-day-header {
  padding: 15px;
  text-align: center;
  font-weight: 500;
  border-right: 1px solid #e0e0e0;
}

.month-body {
  display: flex;
  flex-direction: column;
}

.month-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid #e0e0e0;
}

.month-day {
  min-height: 120px;
  padding: 8px;
  border-right: 1px solid #e0e0e0;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.month-day:hover {
  background: #f5f5f5;
}

.month-day.is-today {
  background: #e3f2fd;
}

.month-day.is-other-month {
  color: #bbb;
  background: #fafafa;
}

.month-day.has-schedules .day-number {
  font-weight: 600;
}

.day-number {
  font-size: 16px;
  margin-bottom: 5px;
}

.day-schedules {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.month-schedule-item {
  background: #2196f3;
  color: white;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.month-schedule-item:hover {
  transform: scale(1.02);
}

.month-schedule-item.schedule-group {
  background: #4caf50;
}

.month-schedule-item.schedule-match {
  background: #ff9800;
}

.month-schedule-item.schedule-tournament {
  background: #f44336;
}

.more-schedules {
  font-size: 10px;
  color: #666;
  text-align: center;
  margin-top: 2px;
}

/* 对话框样式 */
.schedule-detail {
  padding: 10px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .schedule-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .schedule-controls {
    flex-direction: column;
    gap: 15px;
  }

  .date-navigation {
    justify-content: center;
  }

  .current-period {
    min-width: auto;
  }

  .week-header,
  .time-row {
    grid-template-columns: 60px repeat(7, 1fr);
  }

  .time-column {
    padding: 10px 5px;
    font-size: 12px;
  }

  .day-header {
    padding: 10px 5px;
  }

  .day-name {
    font-size: 12px;
  }

  .day-date {
    font-size: 16px;
  }

  .month-day {
    min-height: 80px;
    padding: 4px;
  }

  .day-number {
    font-size: 14px;
  }

  .month-schedule-item {
    font-size: 10px;
    padding: 1px 4px;
  }
}
</style>
