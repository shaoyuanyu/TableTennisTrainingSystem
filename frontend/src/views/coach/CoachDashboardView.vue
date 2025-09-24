<template>
  <div class="coach-dashboard">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h2>{{ getGreeting() }}，{{ userStore.userInfo.name || '老师' }}教练！</h2>
          <p>今天有 {{ todayClasses.length }} 节课程安排</p>
        </div>
        <div class="coach-avatar">
          <el-avatar :size="80" :src="userStore.userInfo.avatar">
            {{ userStore.userInfo.name?.charAt(0) || 'T' }}
          </el-avatar>
          <el-tag v-if="userStore.userInfo.isOnline" type="success" size="small" class="online-status">
            在线
          </el-tag>
        </div>
      </div>
    </el-card>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon schedule">
              <el-icon>
                <Calendar />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>课程安排</h3>
              <p>查看和管理课程时间</p>
              <PrimaryButton size="sm" to="/coach/schedule">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon approval">
              <el-icon>
                <Bell />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>预约审批</h3>
              <p>{{ pendingAppointments }}个待处理</p>
              <PrimaryButton size="sm" to="/coach/appointment-approval">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon feedback">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>学生双选</h3>
              <p>{{ pendingApplications }}个待处理</p>
              <PrimaryButton size="sm" to="/coach/mutual-selection">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon income">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>收入统计</h3>
              <p>查看收入和课时统计</p>
              <PrimaryButton size="sm" to="/coach/income-statistics">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据统计 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon students">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.totalStudents }}</div>
              <div class="stat-label">总学员数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon classes">
              <el-icon>
                <Clock />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.totalClasses }}</div>
              <div class="stat-label">总课时数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rating">
              <el-icon>
                <Star />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.rating }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon income">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">¥{{ stats.monthIncome }}</div>
              <div class="stat-label">本月收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日课程和收入趋势 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="today-schedule">
          <template #header>
            <div class="card-header">
              <span>今日课程安排</span>
              <el-tag type="info">{{ formatDate(new Date()) }}</el-tag>
            </div>
          </template>

          <div v-if="todayClasses.length === 0" class="empty-state">
            <el-empty description="今天没有课程安排" />
            <PrimaryButton to="/coach/schedule">查看课程表</PrimaryButton>
          </div>

          <el-timeline v-else>
            <el-timeline-item v-for="classItem in todayClasses" :key="classItem.id" :timestamp="classItem.time"
              placement="top">
              <el-card class="class-item">
                <div class="class-content">
                  <div class="class-info">
                    <h4>{{ classItem.student.name }} - {{ classItem.type }}</h4>
                    <p>课程时长：{{ classItem.duration }}分钟</p>
                    <p>上课地点：{{ classItem.location }}</p>
                    <p>课程费用：¥{{ classItem.fee }}</p>
                  </div>
                  <div class="class-actions">
                    <el-tag :type="getClassStatusType(classItem.status)">
                      {{ getClassStatusText(classItem.status) }}
                    </el-tag>
                    <el-button v-if="classItem.status === 'upcoming'" size="small" type="primary"
                      @click="startClass(classItem)">
                      开始上课
                    </el-button>
                    <el-button v-if="classItem.status === 'ongoing'" size="small" type="success"
                      @click="completeClass(classItem)">
                      结束课程
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="income-chart">
          <template #header>
            <span>收入趋势</span>
          </template>

          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon>
                <TrendCharts />
              </el-icon>
              <p>最近7天收入趋势图</p>
              <div class="income-summary">
                <div class="summary-item">
                  <span class="label">今日收入</span>
                  <span class="value">¥{{ incomeData.today }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">本周收入</span>
                  <span class="value">¥{{ incomeData.week }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">本月收入</span>
                  <span class="value">¥{{ incomeData.month }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新学员评价 -->
    <el-card class="recent-reviews">
      <template #header>
        <div class="card-header">
          <span>最新学员评价</span>
          <OutlineButton size="small" variant="glass" to="/coach/student-feedback">
            查看更多
          </OutlineButton>
        </div>
      </template>

      <div class="reviews-list">
        <div v-for="review in recentReviews" :key="review.id" class="review-item">
          <div class="review-header">
            <el-avatar :size="40" :src="review.student.avatar">
              {{ review.student.name.charAt(0) }}
            </el-avatar>
            <div class="review-info">
              <h5>{{ review.student.name }}</h5>
              <div class="review-rating">
                <el-rate v-model="review.rating" disabled size="small" />
                <span class="review-date">{{ formatDateTime(review.createdAt) }}</span>
              </div>
            </div>
          </div>
          <p class="review-content">{{ review.content }}</p>
        </div>
      </div>
    </el-card>

    <!-- 待处理事项 -->
    <el-card class="pending-tasks">
      <template #header>
        <span>待处理事项</span>
      </template>

      <el-empty v-if="pendingTasks.length === 0" description="暂无待处理事项" />

      <div v-else class="tasks-list">
        <div v-for="task in pendingTasks" :key="task.id" class="task-item" @click="handleTask(task)">
          <div class="task-icon">
            <el-icon>
              <component :is="getTaskIcon(task.type)" />
            </el-icon>
          </div>
          <div class="task-content">
            <h5>{{ task.title }}</h5>
            <p>{{ task.description }}</p>
            <span class="task-time">{{ formatDateTime(task.createdAt) }}</span>
          </div>
          <div class="task-action">
            <el-button size="small" type="primary"> 处理 </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import {
  Bell,
  Calendar,
  ChatLineSquare,
  Clock,
  Document,
  Money,
  Star,
  TrendCharts,
  User
} from '@element-plus/icons-vue'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { getPendingApplicationCount } from '@/api/mutualSelection'

// 用户状态
const userStore = useUserStore()
const router = useRouter()

// 数据状态
const todayClasses = ref([])
const recentReviews = ref([])
const pendingTasks = ref([])
const stats = reactive({
  totalStudents: 0,
  totalClasses: 0,
  rating: 0,
  monthIncome: 0,
})
const incomeData = reactive({
  today: 0,
  week: 0,
  month: 0,
})
const pendingAppointments = ref(0)
const pendingApplications = ref(0) // 新增：待处理的双选申请

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取总学员数
    const studentsResponse = await api.get('/coach/students')
    stats.totalStudents = studentsResponse.data.length || 0

    // 获取总课时数
    const classesResponse = await api.get('/coach/classes')
    stats.totalClasses = classesResponse.data.length || 0

    // 获取平均评分
    const ratingResponse = await api.get('/coach/rating')
    stats.rating = ratingResponse.data.average || 0

    // 获取本月收入
    const incomeResponse = await api.get('/coach/income')
    stats.monthIncome = incomeResponse.data.month || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取今日课程
const fetchTodayClasses = async () => {
  try {
    const response = await api.get('/coach/today-classes')
    todayClasses.value = response.data || []
  } catch (error) {
    console.error('获取今日课程失败:', error)
  }
}

// 获取最新评价
const fetchRecentReviews = async () => {
  try {
    const response = await api.get('/coach/reviews/latest')
    recentReviews.value = response.data || []
  } catch (error) {
    console.error('获取最新评价失败:', error)
  }
}

// 获取待处理预约数量
const fetchPendingAppointments = async () => {
  try {
    const response = await api.get('/coach/pending-appointments/count')
    pendingAppointments.value = response.data.count || 0
  } catch (error) {
    console.error('获取待处理预约数量失败:', error)
  }
}

// 获取待处理双选申请数量
const fetchPendingApplications = async () => {
  try {
    const response = await getPendingApplicationCount()
    pendingApplications.value = response.count || 0
  } catch (error) {
    console.error('获取待处理双选申请数量失败:', error)
  }
}

// 获取收入数据
const fetchIncomeData = async () => {
  try {
    const response = await api.get('/coach/income/stats')
    Object.assign(incomeData, response.data)
  } catch (error) {
    console.error('获取收入数据失败:', error)
  }
}

// 获取待处理事项
const fetchPendingTasks = async () => {
  try {
    const response = await api.get('/coach/pending-tasks')
    pendingTasks.value = response.data || []
  } catch (error) {
    console.error('获取待处理事项失败:', error)
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

// 格式化日期时间
const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取课程状态类型
const getClassStatusType = (status) => {
  const types = {
    upcoming: 'warning',
    ongoing: 'primary',
    completed: 'success',
    cancelled: 'danger',
  }
  return types[status] || ''
}

// 获取课程状态文本
const getClassStatusText = (status) => {
  const texts = {
    upcoming: '即将开始',
    ongoing: '进行中',
    completed: '已完成',
    cancelled: '已取消',
  }
  return texts[status] || status
}

// 获取任务图标
const getTaskIcon = (type) => {
  const icons = {
    appointment: Calendar,
    review: ChatLineSquare,
    class: Clock,
    document: Document,
  }
  return icons[type] || Document
}

// 开始上课
const startClass = (classItem) => {
  router.push({
    path: '/coach/class-session',
    query: { classId: classItem.id },
  })
}

// 结束课程
const completeClass = (classItem) => {
  router.push({
    path: '/coach/class-completion',
    query: { classId: classItem.id },
  })
}

// 处理任务
const handleTask = (task) => {
  // 根据任务类型跳转到相应页面
  switch (task.type) {
    case 'appointment':
      router.push('/coach/appointment-approval')
      break
    case 'review':
      router.push('/coach/student-feedback')
      break
    case 'class':
      router.push('/coach/schedule')
      break
    default:
      router.push('/coach/dashboard')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStats()
  fetchTodayClasses()
  fetchRecentReviews()
  fetchPendingAppointments()
  fetchPendingApplications() // 新增：获取待处理双选申请数量
  fetchIncomeData()
  fetchPendingTasks()
})
</script>

<style scoped>
.coach-dashboard {
  padding: 20px;
}

.welcome-banner {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.banner-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
}

.welcome-text p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.coach-avatar {
  position: relative;
}

.online-status {
  position: absolute;
  top: -5px;
  right: -5px;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 120px;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.action-content {
  display: flex;
  align-items: center;
  gap: 16px;
  height: 100%;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.action-icon.schedule {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.action-icon.approval {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.action-icon.feedback {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.action-icon.income {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.action-text h3 {
  margin: 0 0 4px 0;
  color: #333;
  font-size: 16px;
}

.action-text p {
  margin: 0;
  color: #666;
  font-size: 12px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.students {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.classes {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.rating {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.income {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-text {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.today-schedule,
.income-chart {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.class-item {
  margin-bottom: 16px;
}

.class-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.class-info h4 {
  margin: 0 0 8px 0;
  color: #333;
}

.class-info p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.class-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #999;
}

.income-summary {
  margin-top: 20px;
  display: flex;
  justify-content: space-around;
}

.summary-item {
  text-align: center;
}

.summary-item .label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.summary-item .value {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.recent-reviews {
  margin-bottom: 20px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-info h5 {
  margin: 0;
  color: #333;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.pending-tasks {
  margin-bottom: 20px;
}

.tasks-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.task-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.task-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.task-content {
  flex: 1;
}

.task-content h5 {
  margin: 0 0 4px 0;
  color: #333;
}

.task-content p {
  margin: 0 0 4px 0;
  color: #666;
  font-size: 14px;
}

.task-time {
  font-size: 12px;
  color: #999;
}

.task-action {
  flex-shrink: 0;
}

</style>
