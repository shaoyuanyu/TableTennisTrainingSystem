<template>
  <div class="schedule-view ultra">
    <div class="schedule-header ultra">
      <h2><span class="schedule-icon">🏓</span> 我的课表</h2>
      <div class="schedule-controls ultra">
        <div class="view-switch-group">
          <component :is="currentView === 'week' ? 'PrimaryButton' : 'OutlineButton'" @click="setView('week')"
            class="header-btn ultra-btn" size="md">
            <span class="btn-icon">📅</span> 周视图
          </component>
          <component :is="currentView === 'month' ? 'PrimaryButton' : 'OutlineButton'" @click="setView('month')"
            class="header-btn ultra-btn" size="md">
            <span class="btn-icon">🗓️</span> 月视图
          </component>
        </div>
        <div class="date-navigation ultra">
          <IconButton @click="previousPeriod" variant="glass" class="nav-btn ultra-btn">
            <el-icon>
              <ArrowLeft />
            </el-icon>
          </IconButton>
          <span class="current-period ultra">{{ currentPeriodText }}</span>
          <IconButton @click="nextPeriod" variant="glass" class="nav-btn ultra-btn">
            <el-icon>
              <ArrowRight />
            </el-icon>
          </IconButton>
          <PrimaryButton @click="goToToday" class="today-btn ultra-btn">
            <span class="btn-icon">⭐</span> 今天
          </PrimaryButton>
        </div>
        
        <!-- 新增功能按钮 -->
        <div class="schedule-actions ultra">
          <el-button @click="addCourse" class="action-btn ultra-btn">
            <span class="btn-icon">➕</span> 添加课程
          </el-button>
          <el-button @click="refreshSchedule" class="action-btn ultra-btn">
            <span class="btn-icon">🔄</span> 刷新课表
          </el-button>
          <el-button @click="exportSchedule" class="action-btn ultra-btn">
            <span class="btn-icon">📤</span> 导出课表
          </el-button>
          <el-button @click="sendScheduleEmail" class="action-btn ultra-btn">
            <span class="btn-icon">📧</span> 邮件发送
          </el-button>
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
              <div class="schedule-coach">
                <span class="field-icon">🧑‍🏫</span>{{ schedule.coach }}
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
                <span class="dot-icon">●</span> {{ schedule.title }}
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
          <el-descriptions-item label="教练">
            <span class="field-icon">🧑‍🏫</span> {{ selectedSchedule.coach }}
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
            <el-button
              type="primary"
              text
              @click="addEmailRecipient"
              class="add-recipient-btn"
            >
              + 添加收件人
            </el-button>
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
          <el-button @click="showEmailDialog = false">取消</el-button>
          <el-button
            type="primary"
            @click="handleEmailSend"
            :disabled="emailOptions.recipients.filter(email => email.trim()).length === 0"
          >
            发送邮件
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 同步设置对话框 -->
    <el-dialog
      v-model="showSyncDialog"
      title="⚙️ 同步设置"
      width="500px"
      :before-close="() => showSyncDialog = false"
    >
      <el-form :model="syncOptions" label-width="120px">
        <el-form-item label="同步平台">
          <el-select v-model="syncOptions.platform" style="width: 100%">
            <el-option label="Google Calendar" value="google" />
            <el-option label="Outlook Calendar" value="outlook" />
            <el-option label="Apple Calendar" value="apple" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="提醒设置">
          <el-checkbox v-model="syncOptions.enableReminder">
            启用课程提醒
          </el-checkbox>
        </el-form-item>
        
        <el-form-item label="同步频率">
          <el-select v-model="syncOptions.syncFrequency" style="width: 100%">
            <el-option label="实时同步" value="realtime" />
            <el-option label="每小时" value="hourly" />
            <el-option label="每日同步" value="daily" />
            <el-option label="手动同步" value="manual" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showSyncDialog = false">取消</el-button>
          <el-button type="primary" @click="showSyncDialog = false">
            保存设置
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 课程管理对话框 -->
    <el-dialog
      v-model="showCourseManagementDialog"
      title="📋 我的课程管理"
      width="80%"
      :before-close="() => showCourseManagementDialog = false"
    >
      <div class="course-management-panel">
        <!-- 筛选工具栏 -->
        <div class="management-toolbar">
          <div class="toolbar-left">
            <el-select v-model="courseFilter.status" placeholder="课程状态" clearable @change="filterCourses">
              <el-option label="全部" value="" />
              <el-option label="已预约" value="scheduled" />
              <el-option label="已确认" value="confirmed" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
            <el-select v-model="courseFilter.type" placeholder="课程类型" clearable @change="filterCourses" style="margin-left: 10px;">
              <el-option label="全部" value="" />
              <el-option label="一对一" value="individual" />
              <el-option label="小组课" value="group" />
            </el-select>
            <el-date-picker
              v-model="courseFilter.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="filterCourses"
              style="margin-left: 10px;"
            />
          </div>
          <div class="toolbar-right">
            <el-button @click="refreshCourses" :loading="courseListLoading">
              🔄 刷新
            </el-button>
          </div>
        </div>

        <!-- 课程列表 -->
        <el-table 
          :data="filteredCourses" 
          v-loading="courseListLoading"
          style="width: 100%; margin-top: 20px;"
          @row-click="viewCourseDetail"
        >
          <el-table-column prop="date" label="日期" width="120">
            <template #default="{ row }">
              <el-tag :type="isToday(row.date) ? 'success' : 'info'" size="small">
                {{ formatDate(row.date) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="时间" width="140">
            <template #default="{ row }">
              <div class="time-range">
                <span>{{ row.startTime }} - {{ row.endTime }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="title" label="课程名称" min-width="150" />

          <el-table-column prop="type" label="类型" width="80">
            <template #default="{ row }">
              <el-tag :type="row.type === 'individual' ? 'warning' : 'success'" size="small">
                {{ row.type === 'individual' ? '一对一' : '小组课' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="coachName" label="教练" width="100" />

          <el-table-column prop="location" label="地点" width="120" />

          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="enrollmentStatus" label="报名状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getEnrollmentStatusType(row.enrollmentStatus)" size="small">
                {{ getEnrollmentStatusText(row.enrollmentStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button 
                size="small" 
                @click.stop="viewCourseDetail(row)"
              >
                详情
              </el-button>
              <el-button 
                v-if="canCancelCourse(row)"
                size="small" 
                type="danger" 
                @click.stop="cancelCourseEnrollment(row)"
              >
                取消
              </el-button>
              <el-button 
                v-if="canFeedback(row)"
                size="small" 
                type="success" 
                @click.stop="openFeedbackDialog(row)"
              >
                反馈
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="coursePagination.page"
            v-model:page-size="coursePagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="coursePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadStudentCourses"
            @current-change="loadStudentCourses"
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCourseManagementDialog = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 课程详情对话框 -->
    <el-dialog 
      v-model="showCourseDetailDialog" 
      title="课程详情" 
      width="60%"
      @close="selectedCourseDetail = null"
    >
      <div v-if="selectedCourseDetail" class="course-detail">
        <div class="detail-header">
          <h3>{{ selectedCourseDetail.title }}</h3>
          <div class="course-badges">
            <el-tag :type="selectedCourseDetail.type === 'individual' ? 'warning' : 'success'">
              {{ selectedCourseDetail.type === 'individual' ? '一对一课程' : '小组课程' }}
            </el-tag>
            <el-tag :type="getStatusType(selectedCourseDetail.status)">
              {{ getStatusText(selectedCourseDetail.status) }}
            </el-tag>
          </div>
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="日期时间">
            {{ selectedCourseDetail.date }} {{ selectedCourseDetail.startTime }} - {{ selectedCourseDetail.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="课程时长">
            {{ selectedCourseDetail.duration }} 分钟
          </el-descriptions-item>
          <el-descriptions-item label="教练">
            {{ selectedCourseDetail.coachName }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            {{ selectedCourseDetail.location }}
          </el-descriptions-item>
          <el-descriptions-item label="课程等级">
            {{ getLevelText(selectedCourseDetail.level) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程价格">
            ¥{{ selectedCourseDetail.price }}
          </el-descriptions-item>
          <el-descriptions-item label="报名状态">
            <el-tag :type="getEnrollmentStatusType(selectedCourseDetail.enrollmentStatus)">
              {{ getEnrollmentStatusText(selectedCourseDetail.enrollmentStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPaymentStatusType(selectedCourseDetail.paymentStatus)">
              {{ getPaymentStatusText(selectedCourseDetail.paymentStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">
            {{ selectedCourseDetail.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedCourseDetail.feedback" class="feedback-section">
          <h4>我的评价</h4>
          <el-rate v-model="selectedCourseDetail.rating" disabled />
          <p>{{ selectedCourseDetail.feedback }}</p>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCourseDetailDialog = false">关闭</el-button>
          <el-button 
            v-if="selectedCourseDetail && canCancelCourse(selectedCourseDetail)"
            type="danger" 
            @click="cancelCourseEnrollment(selectedCourseDetail)"
          >
            取消课程
          </el-button>
          <el-button 
            v-if="selectedCourseDetail && canFeedback(selectedCourseDetail)"
            type="primary" 
            @click="openFeedbackDialog(selectedCourseDetail)"
          >
            课程反馈
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 课程反馈对话框 -->
    <el-dialog v-model="showFeedbackDialog" title="课程反馈" width="40%">
      <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef">
        <el-form-item label="课程评分" prop="rating">
          <el-rate v-model="feedbackForm.rating" :max="5" show-text />
        </el-form-item>
        <el-form-item label="反馈内容" prop="feedback">
          <el-input 
            v-model="feedbackForm.feedback" 
            type="textarea" 
            :rows="4"
            placeholder="请输入您对本次课程的反馈..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="closeFeedbackDialog">取消</el-button>
        <el-button type="primary" @click="submitFeedback" :loading="feedbackLoading">
          提交反馈
        </el-button>
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
import { PrimaryButton, OutlineButton, IconButton, DangerButton } from '@/components/buttons'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { useScheduleSync } from '@/utils/scheduleSyncExamples'

// 响应式数据
const currentView = ref('week')
const currentDate = ref(dayjs())
const schedules = ref([])
const loading = ref(false)
const showScheduleDialog = ref(false)
const selectedSchedule = ref(null)

// 新增功能相关数据
const showSyncDialog = ref(false)
const showEmailDialog = ref(false)
const syncOptions = ref({
  platform: 'google',
  enableReminder: true,
  syncFrequency: 'hourly'
})
const emailOptions = ref({
  recipients: [],
  includeAttachment: true,
  sendReminder: false
})

// 初始化同步功能
const scheduleSync = useScheduleSync()

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
  // element官方 dayjs 的 startOf('week') 默认为周日
  // 所以我们需要调整为周一
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

  // 让日历从周一开始，日历头部也从周一开始
  // dayjs的startOf('week')是周日，需要+1天
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

// 新增功能方法
const addCourse = () => {
  showCourseManagementDialog.value = true
  loadStudentCourses()
}

const refreshSchedule = () => {
  loading.value = true
  fetchSchedules().finally(() => {
    ElMessage.success('课表已刷新')
  })
}

const exportSchedule = () => {
  try {
    const filename = `学生课表_${dayjs().format('YYYY-MM-DD')}.ics`
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

    let startDate, endDate
    if (currentView.value === 'week') {
      // 以周一为开始
      startDate = currentDate.value.startOf('week').add(1, 'day').format('YYYY-MM-DD')
      endDate = currentDate.value.startOf('week').add(7, 'day').format('YYYY-MM-DD')
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

// 课程管理功能
const showCourseManagementDialog = ref(false)
const showCourseDetailDialog = ref(false)
const showFeedbackDialog = ref(false)
const selectedCourseDetail = ref(null)
const courseListLoading = ref(false)
const feedbackLoading = ref(false)

// 课程列表数据
const studentCourses = ref([])
const filteredCourses = ref([])
const courseFilter = ref({
  status: '',
  type: '',
  dateRange: []
})
const coursePagination = ref({
  page: 1,
  pageSize: 20,
  total: 0
})

// 反馈表单
const feedbackForm = ref({
  rating: 5,
  feedback: ''
})
const feedbackRules = {
  rating: [{ required: true, message: '请给出评分', trigger: 'change' }],
  feedback: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}
const feedbackFormRef = ref()
const feedbackCourse = ref(null)

// 导入API函数
import { 
  getStudentCourses, 
  getStudentCourseDetail, 
  cancelEnrollment, 
  submitCourseFeedback 
} from '@/api/courses'

// 添加课程按钮功能（已在上面定义）

// 加载学员课程列表
const loadStudentCourses = async () => {
  try {
    courseListLoading.value = true
    const params = {
      page: coursePagination.value.page,
      pageSize: coursePagination.value.pageSize
    }

    if (courseFilter.value.status) params.status = [courseFilter.value.status]
    if (courseFilter.value.type) params.type = courseFilter.value.type
    if (courseFilter.value.dateRange && courseFilter.value.dateRange.length === 2) {
      params.startDate = courseFilter.value.dateRange[0]
      params.endDate = courseFilter.value.dateRange[1]
    }

    const response = await getStudentCourses(params)
    if (response.success) {
      studentCourses.value = response.data.courses || []
      filteredCourses.value = studentCourses.value
      coursePagination.value.total = response.data.pagination?.total || 0
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败：' + error.message)
  } finally {
    courseListLoading.value = false
  }
}

// 筛选课程
const filterCourses = () => {
  loadStudentCourses()
}

// 刷新课程
const refreshCourses = () => {
  loadStudentCourses()
  fetchSchedules() // 同时刷新日历视图
}

// 查看课程详情
const viewCourseDetail = async (course) => {
  try {
    const response = await getStudentCourseDetail(course.id)
    if (response.success) {
      selectedCourseDetail.value = response.data
      showCourseDetailDialog.value = true
    }
  } catch (error) {
    ElMessage.error('获取课程详情失败：' + error.message)
  }
}

// 取消课程报名
const cancelCourseEnrollment = async (course) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个课程吗？取消后可能会产生费用，请仔细阅读取消政策。',
      '确认取消',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '再想想',
        type: 'warning',
      }
    )

    const response = await cancelEnrollment(course.id)
    if (response.success) {
      ElMessage.success('课程已取消')
      if (response.data.refundAmount) {
        ElMessage.info(`退款金额：¥${response.data.refundAmount}`)
      }
      loadStudentCourses()
      fetchSchedules() // 刷新日历视图
      if (showCourseDetailDialog.value) {
        showCourseDetailDialog.value = false
      }
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消课程失败：' + error.message)
    }
  }
}

// 打开反馈对话框
const openFeedbackDialog = (course) => {
  feedbackCourse.value = course
  // 如果已有反馈，填充表单
  if (course.feedback) {
    feedbackForm.value.rating = course.rating || 5
    feedbackForm.value.feedback = course.feedback || ''
  } else {
    feedbackForm.value.rating = 5
    feedbackForm.value.feedback = ''
  }
  showFeedbackDialog.value = true
}

// 关闭反馈对话框
const closeFeedbackDialog = () => {
  showFeedbackDialog.value = false
  feedbackCourse.value = null
  feedbackForm.value.rating = 5
  feedbackForm.value.feedback = ''
  if (feedbackFormRef.value) {
    feedbackFormRef.value.clearValidate()
  }
}

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  
  try {
    await feedbackFormRef.value.validate()
    feedbackLoading.value = true
    
    const response = await submitCourseFeedback(feedbackCourse.value.id, {
      rating: feedbackForm.value.rating,
      feedback: feedbackForm.value.feedback
    })
    
    if (response.success) {
      ElMessage.success('反馈提交成功')
      closeFeedbackDialog()
      loadStudentCourses()
    }
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    ElMessage.error('提交反馈失败：' + error.message)
  } finally {
    feedbackLoading.value = false
  }
}

// 工具函数
const canCancelCourse = (course) => {
  const courseDate = dayjs(`${course.date} ${course.startTime}`)
  const now = dayjs()
  return (
    ['scheduled', 'confirmed'].includes(course.status) &&
    ['enrolled'].includes(course.enrollmentStatus) &&
    courseDate.isAfter(now.add(24, 'hour')) // 至少提前24小时取消
  )
}

const canFeedback = (course) => {
  return course.status === 'completed' && course.enrollmentStatus === 'enrolled'
}

const isToday = (date) => {
  return dayjs(date).isSame(dayjs(), 'day')
}

const formatDate = (date) => {
  const day = dayjs(date)
  if (day.isSame(dayjs(), 'day')) return '今天'
  if (day.isSame(dayjs().add(1, 'day'), 'day')) return '明天'
  return day.format('MM-DD')
}

const getStatusType = (status) => {
  const typeMap = {
    'scheduled': 'info',
    'confirmed': 'success',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'scheduled': '已预约',
    'confirmed': '已确认',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

const getEnrollmentStatusType = (status) => {
  const typeMap = {
    'enrolled': 'success',
    'waitlist': 'warning',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getEnrollmentStatusText = (status) => {
  const textMap = {
    'enrolled': '已报名',
    'waitlist': '候补中',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

const getPaymentStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'paid': 'success',
    'refunded': 'info'
  }
  return typeMap[status] || 'info'
}

const getPaymentStatusText = (status) => {
  const textMap = {
    'pending': '待支付',
    'paid': '已支付',
    'refunded': '已退款'
  }
  return textMap[status] || status
}

const getLevelText = (level) => {
  const textMap = {
    'beginner': '初级',
    'intermediate': '中级',
    'advanced': '高级'
  }
  return textMap[level] || level
}

// 监听视图和日期变化
watch([currentView, currentDate], fetchSchedules, { immediate: false })

// 组件挂载时获取数据
onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
.schedule-view.ultra {
  min-height: 100vh;
  padding: var(--spacing-2xl);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: var(--font-family-base);
}

.schedule-header.ultra {
  background: var(--white-alpha-20);
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-header h2 {
  margin: 0;
  color: var(--text-white);
  font-size: 2rem;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.schedule-icon {
  font-size: 1.8rem;
  margin-right: var(--spacing-sm);
  vertical-align: middle;
}

.schedule-controls.ultra {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.view-switch-group {
  display: inline-flex;
  gap: var(--spacing-sm);
}

.header-btn.ultra-btn {
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border: 1px solid var(--white-alpha-30);
  border-radius: var(--radius-lg);
  padding: var(--spacing-sm) var(--spacing-md);
  color: var(--text-white);
  font-weight: 500;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.header-btn.ultra-btn .btn-icon {
  font-size: 1.2em;
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

.week-view.ultra,
.month-view.ultra {
  background: var(--white-alpha-10);
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
}

.week-header.ultra {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--white-alpha-30);
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
  max-height: 600px;
  overflow-y: auto;
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

.schedule-item.ultra .schedule-coach,
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
}

.month-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid var(--white-alpha-20);
}

.month-day {
  min-height: 120px;
  padding: var(--spacing-sm);
  border-right: 1px solid var(--white-alpha-20);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
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
  .schedule-view.ultra {
    padding: var(--spacing-lg);
  }

  .schedule-header.ultra {
    flex-direction: column;
    gap: var(--spacing-md);
    align-items: stretch;
    padding: var(--spacing-lg);
  }

  .schedule-controls.ultra {
    flex-direction: column;
    gap: var(--spacing-md);
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
}

/* 课表管理功能样式 */
.schedule-actions {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.schedule-actions h3 {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-primary);
  font-size: 1.125rem;
  font-weight: 600;
}

.schedule-actions .action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.schedule-actions .action-button {
  flex: 1;
  min-width: 140px;
  max-width: 200px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  font-size: 0.875rem;
  border-radius: var(--border-radius-md);
  transition: all var(--transition-duration);
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: var(--text-primary);
}

.schedule-actions .action-button:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.schedule-actions .action-button.primary {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  border-color: var(--color-primary);
  color: white;
}

.schedule-actions .action-button.primary:hover {
  background: linear-gradient(135deg, var(--color-primary-dark) 0%, var(--color-primary) 100%);
  box-shadow: 0 4px 20px rgba(var(--color-primary-rgb), 0.3);
}

/* 邮件预览样式 */
.email-recipients {
  margin-bottom: var(--spacing-md);
}

.email-recipients .el-form-item {
  margin-bottom: var(--spacing-sm);
}

.email-preview {
  background: rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  margin-top: var(--spacing-md);
  font-family: var(--font-mono);
  font-size: 0.875rem;
  line-height: 1.5;
  color: var(--text-secondary);
  max-height: 200px;
  overflow-y: auto;
}

.email-preview h4 {
  margin: 0 0 var(--spacing-xs) 0;
  color: var(--text-primary);
  font-size: 0.875rem;
  font-weight: 600;
}

/* 对话框增强样式 */
.ultra-dialog .el-dialog__header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding: var(--spacing-lg) var(--spacing-lg) var(--spacing-md);
}

.ultra-dialog .el-dialog__title {
  color: var(--text-primary);
  font-weight: 600;
  font-size: 1.125rem;
}

.ultra-dialog .el-form-item__label {
  color: var(--text-primary);
  font-weight: 500;
}

.ultra-dialog .el-input__inner,
.ultra-dialog .el-textarea__inner {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: var(--text-primary);
}

.ultra-dialog .el-input__inner:focus,
.ultra-dialog .el-textarea__inner:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(var(--color-primary-rgb), 0.2);
}

.ultra-dialog .el-switch__core {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.2);
}

.ultra-dialog .el-switch.is-checked .el-switch__core {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .schedule-actions .action-buttons {
    flex-direction: column;
  }
  
  .schedule-actions .action-button {
    flex: none;
    min-width: 100%;
    max-width: 100%;
  }
  
  .email-preview {
    font-size: 0.8125rem;
    max-height: 150px;
  }
}

/* 课程管理样式 */
.course-management-panel {
  padding: 20px 0;
}

.management-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.course-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-header h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #1f2937;
}

.course-badges {
  display: flex;
  gap: 8px;
}

.feedback-section {
  margin-top: 20px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.feedback-section h4 {
  margin: 0 0 12px 0;
  color: #374151;
}

.feedback-section p {
  margin: 8px 0 0 0;
  color: #6b7280;
  line-height: 1.6;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
