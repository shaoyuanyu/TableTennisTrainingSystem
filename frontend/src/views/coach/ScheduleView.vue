<template>
  <div class="schedule-view ultra">
    <div class="schedule-header ultra">
      <h2><span class="schedule-icon">🏓</span> 我的课表</h2>
      <div class="schedule-controls ultra">
        <div class="view-toggle">
          <OutlineButton :class="['header-btn', 'ultra-btn']" :disabled="currentView === 'week'"
            @click="setView('week')">
            <template #icon-left>📅</template>
            周视图
          </OutlineButton>
          <OutlineButton :class="['header-btn', 'ultra-btn']" :disabled="currentView === 'month'"
            @click="setView('month')">
            <template #icon-left>🗓️</template>
            月视图
          </OutlineButton>
        </div>
        <div class="date-navigation ultra">
          <OutlineButton @click="previousPeriod" class="nav-btn ultra-btn" circle>
            <template #icon-left>
              <el-icon>
                <ArrowLeft />
              </el-icon>
            </template>
          </OutlineButton>
          <span class="current-period ultra">{{ currentPeriodText }}</span>
          <OutlineButton @click="nextPeriod" class="nav-btn ultra-btn" circle>
            <template #icon-left>
              <el-icon>
                <ArrowRight />
              </el-icon>
            </template>
          </OutlineButton>
          <PrimaryButton @click="goToToday" class="today-btn ultra-btn">
            <template #icon-left>⭐</template>
            今天
          </PrimaryButton>
        </div>
      </div>
    </div>

    <!-- 周视图 -->
    <div v-if="currentView === 'week'" class="week-view ultra">
      <div class="week-header ultra">
        <div class="time-column">时间</div>
        <div v-for="day in weekDays" :key="day.date" class="day-header" :class="{ 'is-today': day.isToday }">
          <div class="day-name">{{ day.name }}</div>
          <div class="day-date">{{ day.date }}</div>
        </div>
      </div>
      <div class="week-body">
        <div v-for="hour in timeSlots" :key="hour" class="time-row">
          <div class="time-column">{{ hour }}</div>
          <div v-for="day in weekDays" :key="`${day.date}-${hour}`" class="day-cell"
            @click="handleCellClick(day.date, hour)">
            <div v-for="schedule in getSchedulesForDayAndHour(day.date, hour)" :key="schedule.id"
              class="schedule-item ultra" :class="getScheduleClass(schedule)"
              @click.stop="handleScheduleClick(schedule)">
              <div class="schedule-title">{{ schedule.title }}</div>
              <div class="schedule-student">
                <span class="field-icon">👤</span>{{ studentNames(schedule.students) }}
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
        <div v-for="week in monthWeeks" :key="week[0]?.date || Math.random()" class="month-week">
          <div v-for="day in week" :key="day?.date || Math.random()" class="month-day" :class="{
            'is-today': day?.isToday,
            'is-other-month': day?.isOtherMonth,
            'has-schedules': day && getSchedulesForDay(day.date).length > 0,
          }" @click="handleDayClick(day)">
            <div v-if="day" class="day-number ultra">{{ day.dayNumber }}</div>
            <div v-if="day" class="day-schedules ultra">
              <div v-for="schedule in getSchedulesForDay(day.date).slice(0, 3)" :key="schedule.id"
                class="month-schedule-item ultra" :class="getScheduleClass(schedule)"
                @click.stop="handleScheduleClick(schedule)" :title="schedule.title">
                <span class="dot-icon">●</span> {{ schedule.title
                }}<span v-if="schedule.students && schedule.students.length">（{{ studentNames(schedule.students)
                }}）</span>
              </div>
              <div v-if="getSchedulesForDay(day.date).length > 3" class="more-schedules ultra">
                +{{ getSchedulesForDay(day.date).length - 3 }} 更多
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="showScheduleDialog" title="课程详情" width="500px" class="ultra-dialog">
      <div v-if="selectedSchedule" class="schedule-detail ultra">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">
            <span class="field-icon">🏓</span> {{ selectedSchedule.title }}
          </el-descriptions-item>
          <el-descriptions-item label="学生">
            <span class="field-icon">👤</span> {{ studentNames(selectedSchedule.students) }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            <span class="field-icon">⏰</span> {{ formatScheduleTime(selectedSchedule) }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            <span class="field-icon">📍</span> {{ selectedSchedule.location }}
          </el-descriptions-item>
          <el-descriptions-item label="课程类型">
            <el-tag :type="getScheduleTagType(selectedSchedule.type)">
              <span class="field-icon">🏷️</span> {{ getScheduleTypeName(selectedSchedule.type) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程状态">
            <el-tag :type="getStatusTagType(selectedSchedule.status)">
              <span class="field-icon">🔖</span> {{ getStatusName(selectedSchedule.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedSchedule.description" label="课程描述">
            <span class="field-icon">📝</span> {{ selectedSchedule.description }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer ultra">
          <OutlineButton @click="showScheduleDialog = false">关闭</OutlineButton>
          <DangerButton v-if="selectedSchedule?.status === 'confirmed' && canCancelSchedule(selectedSchedule)"
            @click="cancelSchedule">
            <span class="field-icon">❌</span> 取消课程
          </DangerButton>
        </span>
      </template>
    </el-dialog>

    <!-- 加载状态 -->
    <el-loading v-loading="loading" element-loading-text="加载课表数据..." element-loading-spinner="el-icon-loading"
      class="ultra-loading" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import DangerButton from '@/components/buttons/DangerButton.vue'

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

// 教练课表显示学生名单
const studentNames = (students) => {
  if (!students || students.length === 0) return '无'
  // 假定 students 是数组 [{name: '张三'}, {name: '李四'}]
  return students.map((s) => s.name).join('，')
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
  // 可以预留教练预约课程或加课接口
  console.log('Coach clicked cell:', date, hour)
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
    // 教练取消课程接口，和学生接口一致，只是权限校验在后端
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
      // 以周一为开始
      startDate = currentDate.value.startOf('week').add(1, 'day').format('YYYY-MM-DD')
      endDate = currentDate.value.startOf('week').add(7, 'day').format('YYYY-MM-DD')
    } else {
      startDate = currentDate.value.startOf('month').format('YYYY-MM-DD')
      endDate = currentDate.value.endOf('month').format('YYYY-MM-DD')
    }

    const response = await api.get('/schedules/coach', {
      params: { startDate, endDate },
    })

    schedules.value = response.data || []
  } catch (error) {
    console.error('获取课表失败:', error)
    // 使用模拟数据用于开发测试
    schedules.value = generateMockCoachSchedules()
  } finally {
    loading.value = false
  }
}

// 生成模拟数据用于开发测试（教练视角）
const generateMockCoachSchedules = () => {
  const mockSchedules = []
  const today = dayjs()
  const mockStudentPool = [
    { name: '小明' },
    { name: '小红' },
    { name: '小强' },
    { name: '小丽' },
    { name: '小刚' },
  ]
  // 生成两周示例课程
  for (let i = 0; i < 14; i++) {
    const date = today.add(i, 'day')

    // 随机生成一对一、小组课
    if (Math.random() > 0.6) {
      const startHour = 9 + Math.floor(Math.random() * 10)
      const startTime = `${startHour.toString().padStart(2, '0')}:00`
      const endTime = `${(startHour + 1).toString().padStart(2, '0')}:00`
      const typeArr = ['individual', 'group', 'match']
      const type = typeArr[Math.floor(Math.random() * typeArr.length)]

      // 一对一课、只一个学生；小组课、随机2-4个学生
      let students = []
      if (type === 'individual') {
        students = [mockStudentPool[Math.floor(Math.random() * mockStudentPool.length)]]
      } else if (type === 'group') {
        const count = 2 + Math.floor(Math.random() * 3)
        // 随机抽取count个学生
        students = [...mockStudentPool].sort(() => Math.random() - 0.5).slice(0, count)
      } else {
        students = [...mockStudentPool].slice(0, 2)
      }

      mockSchedules.push({
        id: `coach-mock-${i}-1`,
        title: ['一对一训练', '小组训练', '比赛指导', '战术练习'][Math.floor(Math.random() * 4)],
        students,
        date: date.format('YYYY-MM-DD'),
        startTime,
        endTime,
        location: `训练场${Math.floor(Math.random() * 5) + 1}号`,
        type,
        status: ['confirmed', 'pending'][Math.floor(Math.random() * 2)],
        description: '教练安排的示例课程',
      })
    }

    // 可能有第二个课程
    if (Math.random() > 0.8) {
      const startHour = 15 + Math.floor(Math.random() * 4)
      const startTime = `${startHour.toString().padStart(2, '0')}:00`
      const endTime = `${(startHour + 1).toString().padStart(2, '0')}:00`
      mockSchedules.push({
        id: `coach-mock-${i}-2`,
        title: '比赛训练',
        students: [...mockStudentPool].slice(0, 3),
        date: date.format('YYYY-MM-DD'),
        startTime,
        endTime,
        location: `比赛场地`,
        type: 'match',
        status: 'confirmed',
        description: '教练带队专项训练',
      })
    }
  }
  return mockSchedules
}

// 监听视图和日期变化
watch([currentView, currentDate], fetchSchedules, { immediate: false })

onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
/* 样式不变，仅供参考。你的原有样式可以保留 */
.schedule-view.ultra {
  padding: 40px 0 40px 0;
  background: radial-gradient(circle at 40% 60%, #f3f6fd 0%, #e7f0fa 60%, #aee2ff 100%);
  min-height: 100vh;
  font-family: 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
}

.schedule-header.ultra {
  background: linear-gradient(90deg, #e3f2fd 0%, #ffffff 100%);
  padding: 32px 38px 28px 38px;
  border-radius: 22px;
  margin-bottom: 44px;
  box-shadow: 0 8px 40px rgba(33, 150, 243, 0.12);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 2px solid #2196f350;
}

.schedule-header h2 {
  margin: 0;
  color: #1565c0;
  font-size: 2.6rem;
  font-weight: 900;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px #b3e5fc60;
  display: flex;
  align-items: center;
}

.schedule-icon {
  font-size: 2rem;
  margin-right: 12px;
  vertical-align: middle;
  filter: drop-shadow(0 2px 8px #b3e5fc60);
}

.schedule-controls.ultra {
  display: flex;
  align-items: center;
  gap: 32px;
}

.header-btn.ultra-btn {
  font-size: 1.08rem;
  padding: 7px 22px;
  border-radius: 10px !important;
  box-shadow: 0 2px 6px #21cbf350;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  transition:
    background 0.2s,
    color 0.2s;
}

.header-btn.ultra-btn .btn-icon {
  font-size: 1.2em;
  margin-right: 2px;
}

.today-btn.ultra-btn {
  background: linear-gradient(90deg, #2196f3 0%, #21cbf3 100%);
  color: white !important;
  border-radius: 24px !important;
  font-weight: 700;
  box-shadow: 0 0 16px #21cbf380;
  padding: 7px 20px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.today-btn.ultra-btn .btn-icon {
  font-size: 1.2em;
}

.today-btn.ultra-btn:hover {
  box-shadow: 0 0 22px #21cbf3;
}

.nav-btn.ultra-btn {
  background: #f5f8fa;
  color: #1976d2;
  border-radius: 50% !important;
  box-shadow: 0 2px 8px #b9eaff40;
}

.date-navigation.ultra {
  display: flex;
  align-items: center;
  gap: 22px;
  background: #f5f8fa;
  padding: 12px 22px;
  border-radius: 12px;
  box-shadow: 0 2px 12px #aee2ff22;
}

.current-period.ultra {
  font-weight: 700;
  color: #1976d2;
  min-width: 220px;
  text-align: center;
  font-size: 1.18rem;
  background: #e3f2fd;
  padding: 6px 18px;
  border-radius: 10px;
  box-shadow: 0 2px 8px #b9eaff22;
  letter-spacing: 1px;
}

.week-view.ultra,
.month-view.ultra {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.09);
}

.week-header.ultra {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  background: linear-gradient(90deg, #e8f1fc 0%, #f8f9fa 100%);
  border-bottom: 2px solid #e0e0e0;
}

.time-column {
  padding: 20px 12px;
  font-weight: 700;
  text-align: center;
  border-right: 1.5px solid #e0e0e0;
  background: #eafdff;
  font-size: 1.08rem;
  color: #2196f3;
}

.day-header {
  padding: 20px 0 10px 0;
  text-align: center;
  border-right: 1.5px solid #e0e0e0;
  transition: background-color 0.2s;
}

.day-header.is-today {
  background: linear-gradient(90deg, #e3f2fd 60%, #b9eaff 100%);
  color: #1976d2;
  box-shadow: 0 2px 12px #2196f344;
  border-bottom: 2px solid #2196f3;
}

.day-name {
  font-weight: 700;
  margin-bottom: 2px;
  font-size: 1.1rem;
  letter-spacing: 1px;
}

.day-date {
  font-size: 1.3rem;
  font-weight: 800;
  letter-spacing: 1px;
}

.week-body {
  max-height: 600px;
  overflow-y: auto;
}

.time-row {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  border-bottom: 1.5px solid #e0e0e0;
  min-height: 64px;
  background: #fcfcfd;
}

.day-cell {
  border-right: 1.5px solid #e0e0e0;
  padding: 7px 2px;
  position: relative;
  cursor: pointer;
  transition: background-color 0.18s;
  border-radius: 7px;
}

.day-cell:hover {
  background: #eaf6fe;
}

.schedule-item.ultra {
  background: #2196f3;
  color: white;
  padding: 8px 14px 6px 14px;
  border-radius: 10px;
  margin-bottom: 4px;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 12px #2196f333;
  border: 2px solid #ffffff;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  position: relative;
  align-items: flex-start;
}

.schedule-item.ultra .field-icon {
  font-size: 1em;
  margin-right: 3px;
  vertical-align: middle;
}

.schedule-item.ultra .schedule-title {
  font-weight: 700;
  margin-bottom: 2px;
  font-size: 14px;
  letter-spacing: 1px;
}

.schedule-item.ultra .schedule-coach,
.schedule-item.ultra .schedule-location {
  font-size: 12px;
  opacity: 0.85;
  margin-top: 1px;
  font-weight: 500;
}

.schedule-item.ultra.schedule-individual {
  background: linear-gradient(90deg, #2196f3 60%, #21cbf3 100%);
}

.schedule-item.ultra.schedule-group {
  background: linear-gradient(90deg, #4caf50 60%, #8bc34a 100%);
}

.schedule-item.ultra.schedule-match {
  background: linear-gradient(90deg, #ff9800 60%, #ffd54f 100%);
}

.schedule-item.ultra.schedule-tournament {
  background: linear-gradient(90deg, #f44336 60%, #ff8a65 100%);
}

.schedule-item.ultra.status-pending {
  opacity: 0.7;
  border: 2px dashed rgba(255, 255, 255, 0.7);
}

.schedule-item.ultra.status-cancelled {
  background: #bdbdbd;
  text-decoration: line-through;
  color: #fff;
}

.month-view.ultra {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.09);
}

.month-header.ultra {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: linear-gradient(90deg, #e8f1fc 0%, #f8f9fa 100%);
  border-bottom: 2px solid #e0e0e0;
}

.month-day-header {
  padding: 19px 0 11px 0;
  text-align: center;
  font-weight: 700;
  border-right: 1.5px solid #e0e0e0;
  color: #0d121b;
  font-size: 1.12rem;
  background: linear-gradient(90deg, #697a88 0%, #c2ddfb 100%);
  letter-spacing: 1px;
}

.month-body {
  display: flex;
  flex-direction: column;
}

.month-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1.5px solid #e0e0e0;
  background: #fcfcfd;
}

.month-day {
  min-height: 120px;
  padding: 12px 5px;
  border-right: 1.5px solid #e0e0e0;
  cursor: pointer;
  transition: background-color 0.18s;
  position: relative;
  border-radius: 10px;
}

.month-day:hover {
  background: #eaf6fe;
  box-shadow: 0 2px 8px #2196f322;
}

.month-day.is-today {
  background: linear-gradient(120deg, #e3f2fd 60%, #b9eaff 100%);
  border: 3px solid #2196f3;
  box-shadow: 0 2px 16px #2196f344;
}

.month-day.is-other-month {
  color: #bbb;
  background: #fafafa;
}

.month-day.has-schedules .day-number.ultra {
  font-weight: 800;
  color: #1976d2;
  text-shadow: 0 2px 8px #b9eaff50;
}

.day-number.ultra {
  font-size: 19px;
  margin-bottom: 7px;
  font-weight: 700;
  letter-spacing: 1px;
}

.day-schedules.ultra {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.month-schedule-item.ultra {
  background: #2196f3;
  color: white;
  padding: 4px 14px;
  border-radius: 7px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: 2px solid #fff;
  margin-bottom: 2px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.month-schedule-item.ultra .dot-icon {
  color: #1565c0;
  font-size: 1.2em;
  margin-right: 1px;
}

.month-schedule-item.ultra.schedule-group {
  background: linear-gradient(90deg, #4caf50 60%, #8bc34a 100%);
}

.month-schedule-item.ultra.schedule-match {
  background: linear-gradient(90deg, #ff9800 60%, #ffd54f 100%);
}

.month-schedule-item.ultra.schedule-tournament {
  background: linear-gradient(90deg, #f44336 60%, #ff8a65 100%);
}

.more-schedules.ultra {
  font-size: 12px;
  color: #666;
  text-align: center;
  margin-top: 2px;
  font-weight: 700;
  letter-spacing: 1px;
  background: #e3f2fd;
  border-radius: 6px;
  padding: 2px 0;
}

.schedule-detail.ultra {
  padding: 24px 0 6px 0;
  font-size: 1.17rem;
  background: linear-gradient(110deg, #e3f2fd 80%, #f8f9fa 100%);
  border-radius: 12px;
}

.schedule-detail.ultra .field-icon {
  font-size: 1.1em;
  margin-right: 4px;
  vertical-align: middle;
}

.ultra-dialog ::deep(.el-dialog__body) {
  background: linear-gradient(110deg, #e0eafc 80%, #ffffff 100%);
  padding: 36px 28px 20px 28px;
  border-radius: 14px;
}

.dialog-footer.ultra {
  display: flex;
  justify-content: flex-end;
  gap: 22px;
  padding-top: 16px;
}

.ultra-loading {
  z-index: 9999;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .schedule-header.ultra {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
    padding: 22px 7px;
  }

  .schedule-controls.ultra {
    flex-direction: column;
    gap: 19px;
  }

  .date-navigation.ultra {
    justify-content: center;
    gap: 12px;
    padding: 10px 10px;
  }

  .current-period.ultra {
    min-width: auto;
    font-size: 1.05rem;
    padding: 4px 4px;
  }

  .week-header.ultra,
  .time-row {
    grid-template-columns: 64px repeat(7, 1fr);
  }

  .time-column {
    padding: 9px 3px;
    font-size: 12px;
  }

  .day-header {
    padding: 9px 3px;
    font-size: 14px;
  }

  .day-name {
    font-size: 12px;
  }

  .day-date {
    font-size: 15px;
  }

  .month-day {
    min-height: 78px;
    padding: 5px;
    font-size: 12px;
  }

  .day-number.ultra {
    font-size: 14px;
    margin-bottom: 3px;
  }

  .month-schedule-item.ultra {
    font-size: 11px;
    padding: 2px 6px;
  }

  .schedule-detail.ultra {
    padding: 10px 2px 0 2px;
    font-size: 1rem;
  }

  .ultra-dialog ::deep(.el-dialog__body) {
    padding: 14px 8px 12px 8px;
  }

  .dialog-footer.ultra {
    gap: 9px;
  }
}
</style>
