<template>
  <div class="coach-dashboard">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <div class="whistle-icon">🔔</div>
      <div class="coaching-icon">👨‍🏫</div>
      <div class="trophy-icon">🏆</div>
      <div class="artistic-text coach-text">COACH</div>
      <div class="artistic-text dashboard-text">教练面板</div>
      <div class="floating-element element-1"></div>
      <div class="floating-element element-2"></div>
      <div class="floating-element element-3"></div>
    </div>

    <!-- 今日课程安排 -->
    <el-card class="dashboard-card course-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Calendar />
          </el-icon>
          <span>今日课程</span>
        </div>
      </template>
      <div v-if="todayCourses.length > 0" class="course-list">
        <div v-for="course in todayCourses" :key="course.id" class="course-item">
          <div class="course-time">{{ course.time }}</div>
          <div class="course-info">
            <div class="course-student">{{ course.studentName }}</div>
            <div class="course-location">{{ course.location }}</div>
          </div>
          <div class="course-status">
            <el-tag :type="getStatusType(course.status)">
              {{ getStatusText(course.status) }}
            </el-tag>
          </div>
        </div>
      </div>
      <el-empty v-else description="今天没有安排课程" :image-size="60" />
    </el-card>

    <!-- 待确认预约 -->
    <el-card class="dashboard-card appointment-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Clock />
          </el-icon>
          <span>待确认预约</span>
          <el-badge
            :value="pendingAppointments.length"
            :hidden="pendingAppointments.length === 0"
          />
          <el-link type="primary" @click="$router.push('/coach/appointments')"> 去处理 </el-link>
        </div>
      </template>
      <div v-if="pendingAppointments.length > 0" class="appointment-list">
        <div
          v-for="appointment in pendingAppointments"
          :key="appointment.id"
          class="appointment-item"
        >
          <div class="appointment-info">
            <div class="appointment-student">{{ appointment.studentName }}</div>
            <div class="appointment-time">{{ appointment.time }}</div>
          </div>
          <div class="appointment-actions">
            <el-button
              size="small"
              type="success"
              @click="handleAppointment(appointment.id, 'approve')"
            >
              通过
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleAppointment(appointment.id, 'reject')"
            >
              拒绝
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无待确认预约" :image-size="60" />
    </el-card>

    <!-- 学员反馈 -->
    <el-card class="dashboard-card feedback-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <ChatDotRound />
          </el-icon>
          <span>最新学员反馈</span>
        </div>
      </template>
      <div v-if="studentFeedback.length > 0" class="feedback-list">
        <div v-for="feedback in studentFeedback" :key="feedback.id" class="feedback-item">
          <div class="feedback-header">
            <span class="feedback-student">{{ feedback.studentName }}</span>
            <el-rate v-model="feedback.rating" disabled size="small" />
            <span class="feedback-date">{{ formatDate(feedback.date) }}</span>
          </div>
          <div class="feedback-content">{{ feedback.content }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无学员反馈" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Clock, ChatDotRound } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

// 今日课程
const todayCourses = ref([])

// 待确认预约
const pendingAppointments = ref([])

// 学员反馈
const studentFeedback = ref([])

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    confirmed: 'success',
    pending: 'warning',
    completed: 'info',
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    confirmed: '已确认',
    pending: '待确认',
    completed: '已完成',
  }
  return textMap[status] || '未知'
}

// 处理预约
const handleAppointment = async (appointmentId, action) => {
  try {
    const actionText = action === 'approve' ? '通过' : '拒绝'
    await ElMessageBox.confirm(`确定要${actionText}这个预约吗？`, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // 这里应该调用API
    // await api.put(`/appointments/${appointmentId}`, { status: action })

    // 从列表中移除
    const index = pendingAppointments.value.findIndex((item) => item.id === appointmentId)
    if (index > -1) {
      pendingAppointments.value.splice(index, 1)
    }

    ElMessage.success(`预约已${actionText}`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请重试')
    }
  }
}

// 获取数据
const fetchData = async () => {
  // 模拟数据
  todayCourses.value = [
    {
      id: 1,
      time: '09:00-10:00',
      studentName: '小明',
      location: '球台1',
      status: 'confirmed',
    },
    {
      id: 2,
      time: '14:00-15:00',
      studentName: '小红',
      location: '球台2',
      status: 'confirmed',
    },
    {
      id: 3,
      time: '16:00-17:00',
      studentName: '小华',
      location: '球台3',
      status: 'pending',
    },
  ]

  pendingAppointments.value = [
    {
      id: 1,
      studentName: '小李',
      time: '明天 10:00-11:00',
    },
    {
      id: 2,
      studentName: '小张',
      time: '后天 15:00-16:00',
    },
  ]

  studentFeedback.value = [
    {
      id: 1,
      studentName: '小明',
      rating: 5,
      date: new Date(Date.now() - 2 * 60 * 60 * 1000),
      content: '老师教学很认真，技术指导很到位，进步很大！',
    },
    {
      id: 2,
      studentName: '小红',
      rating: 4,
      date: new Date(Date.now() - 24 * 60 * 60 * 1000),
      content: '训练强度适中，动作纠正及时，很满意。',
    },
  ]
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.coach-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(
    135deg,
    #ff6b6b 0%,
    #feca57 25%,
    #48dbfb 50%,
    #0abde3 75%,
    #006ba6 100%
  );
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 背景装饰元素 */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.whistle-icon {
  position: absolute;
  top: 15%;
  left: 10%;
  font-size: 80px;
  opacity: 0.08;
  animation: swing 4s ease-in-out infinite;
}

.coaching-icon {
  position: absolute;
  top: 50%;
  right: 8%;
  font-size: 100px;
  opacity: 0.06;
  animation: float 6s ease-in-out infinite;
}

.trophy-icon {
  position: absolute;
  bottom: 20%;
  left: 8%;
  font-size: 90px;
  opacity: 0.08;
  animation: glow 3s ease-in-out infinite;
}

.artistic-text {
  position: absolute;
  font-family: 'Arial Black', Arial, sans-serif;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.06);
  user-select: none;
}

.coach-text {
  top: 20%;
  right: 5%;
  font-size: 80px;
  transform: rotate(-20deg);
  animation: shimmer 5s ease-in-out infinite;
}

.dashboard-text {
  bottom: 15%;
  right: 10%;
  font-size: 60px;
  transform: rotate(15deg);
  animation: shimmer 5s ease-in-out infinite 2.5s;
}

.floating-element {
  position: absolute;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.element-1 {
  top: 30%;
  left: 5%;
  width: 100px;
  height: 100px;
  animation: rotate 20s linear infinite;
}

.element-2 {
  bottom: 40%;
  right: 15%;
  width: 60px;
  height: 60px;
  animation: float 8s ease-in-out infinite reverse;
}

.element-3 {
  top: 60%;
  left: 20%;
  width: 80px;
  height: 80px;
  animation: pulse 4s ease-in-out infinite;
}

.dashboard-card {
  border: none;
  border-radius: 16px;
  backdrop-filter: blur(15px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.12),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  position: relative;
  z-index: 10;
  overflow: hidden;
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  transform: translateY(-4px);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.18),
    0 0 0 1px rgba(255, 255, 255, 0.3) inset;
}

.course-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 107, 107, 0.1));
}

.appointment-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(254, 202, 87, 0.1));
}

.feedback-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(72, 219, 251, 0.1));
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.header-icon {
  font-size: 20px;
  color: #ff6b6b;
}

.card-header .el-link {
  margin-left: auto;
  color: #ff6b6b;
  font-weight: 500;
}

.course-list,
.appointment-list,
.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.course-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.course-time {
  font-weight: 600;
  color: #ff6b6b;
  min-width: 100px;
  font-size: 14px;
}

.course-info {
  flex: 1;
}

.course-student {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.course-location {
  font-size: 12px;
  color: #666;
}

.appointment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.appointment-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.appointment-info {
  flex: 1;
}

.appointment-student {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.appointment-time {
  color: #666;
  font-size: 12px;
}

.appointment-actions {
  display: flex;
  gap: 8px;
}

.feedback-item {
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.feedback-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.feedback-student {
  font-weight: 600;
  color: #333;
}

.feedback-date {
  color: #666;
  font-size: 12px;
}

.feedback-content {
  color: #555;
  line-height: 1.5;
}

.feedback-rating {
  margin-top: 8px;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes swing {
  0%,
  100% {
    transform: rotate(0deg);
  }

  25% {
    transform: rotate(10deg);
  }

  75% {
    transform: rotate(-10deg);
  }
}

@keyframes glow {
  0%,
  100% {
    opacity: 0.08;
    transform: scale(1);
  }

  50% {
    opacity: 0.15;
    transform: scale(1.05);
  }
}

@keyframes shimmer {
  0%,
  100% {
    opacity: 0.06;
  }

  50% {
    opacity: 0.12;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }

  to {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%,
  100% {
    opacity: 0.08;
    transform: scale(1);
  }

  50% {
    opacity: 0.15;
    transform: scale(1.1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coach-dashboard {
    padding: 16px;
  }

  .artistic-text {
    font-size: 50px !important;
  }

  .whistle-icon,
  .coaching-icon,
  .trophy-icon {
    font-size: 60px;
  }

  .course-item,
  .appointment-item,
  .feedback-item {
    padding: 12px;
  }
}
</style>
