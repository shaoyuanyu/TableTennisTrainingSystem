<template>
  <div class="coach-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-left">
        <div class="welcome-content">
          <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}教练！</h1>
          <p class="welcome-subtitle">专业授课，成就每一位学员</p>
          <div class="banner-stats">
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.courses }}</span>
              <span class="stat-label">今日课程</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.students }}</span>
              <span class="stat-label">教授学员</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">¥{{ todayStats.income }}</span>
              <span class="stat-label">今日收入</span>
            </div>
          </div>
        </div>
      </div>
      <div class="banner-right">
        <div class="avatar-container">
          <el-avatar :size="80" :src="userStore.userInfo.avatar" class="user-avatar">
            <el-icon size="40">
              <User />
            </el-icon>
          </el-avatar>
          <div class="avatar-badge">
            <el-tag type="success" size="small">教练</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 收入统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in coachStats" :key="stat.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.gradient }">
              <component :is="stat.icon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-trend" :class="stat.trend">
                <el-icon v-if="stat.trend === 'up'">
                  <TrendCharts />
                </el-icon>
                <span>{{ stat.trendText }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 主要功能区域 -->
    <el-row :gutter="24" class="main-content">
      <!-- 左侧内容 -->
      <el-col :xs="24" :lg="16">
        <!-- 今日课程安排 -->
        <el-card class="content-card schedule-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <Calendar />
                </el-icon>
                <span>今日课程安排</span>
              </div>
              <PrimaryButton size="sm" to="/coach/schedule">完整课表</PrimaryButton>
            </div>
          </template>

          <div v-if="todaySchedule.length > 0" class="schedule-timeline">
            <div v-for="item in todaySchedule" :key="item.id" class="timeline-item">
              <div class="timeline-time">{{ item.time }}</div>
              <div class="timeline-content">
                <div class="timeline-title">{{ item.title }}</div>
                <div class="timeline-student">学员：{{ item.student }}</div>
                <div class="timeline-meta">
                  <el-tag :type="getScheduleType(item.type)" size="small">{{ item.type }}</el-tag>
                  <span class="timeline-location">{{ item.location }}</span>
                  <span class="timeline-fee">费用：¥{{ item.fee }}</span>
                </div>
              </div>
              <div class="timeline-status">
                <PrimaryButton v-if="item.status === 'upcoming'" size="sm">开始授课</PrimaryButton>
                <el-tag v-else :type="getStatusType(item.status)" size="small">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>
            </div>
          </div>

          <div v-else class="empty-schedule">
            <el-empty description="今天没有课程安排" :image-size="100">
              <template #image>
                <div class="empty-icon">📚</div>
              </template>
              <PrimaryButton to="/coach/schedule-management">安排课程</PrimaryButton>
            </el-empty>
          </div>
        </el-card>

        <!-- 我的学员 -->
        <el-card class="content-card students-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <UserFilled />
                </el-icon>
                <span>我的学员</span>
              </div>
              <el-link type="primary" @click="goToStudents">管理学员</el-link>
            </div>
          </template>

          <div class="students-grid">
            <div v-for="student in myStudents" :key="student.id" class="student-card">
              <div class="student-header">
                <el-avatar :size="40" :src="student.avatar">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
                <div class="student-info">
                  <div class="student-name">{{ student.name }}</div>
                  <div class="student-level">等级：{{ student.level }}</div>
                </div>
                <div class="student-status">
                  <el-tag :type="getStudentStatusType(student.status)" size="small">
                    {{ student.status }}
                  </el-tag>
                </div>
              </div>
              <div class="student-stats">
                <div class="stat">
                  <span class="stat-label">课程</span>
                  <span class="stat-value">{{ student.courses }}</span>
                </div>
                <div class="stat">
                  <span class="stat-label">进度</span>
                  <span class="stat-value">{{ student.progress }}%</span>
                </div>
                <div class="stat">
                  <span class="stat-label">评分</span>
                  <span class="stat-value">{{ student.rating }}</span>
                </div>
              </div>
              <div class="student-action">
                <OutlineButton size="sm" @click="viewStudentDetail(student)">查看详情</OutlineButton>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 收入分析 -->
        <el-card class="content-card income-chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <TrendCharts />
                </el-icon>
                <span>收入趋势</span>
              </div>
              <div class="header-actions">
                <el-radio-group v-model="incomeTimeRange" size="small">
                  <el-radio-button label="week">7天</el-radio-button>
                  <el-radio-button label="month">30天</el-radio-button>
                  <el-radio-button label="quarter">3个月</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="income-overview">
            <div class="income-summary">
              <div class="summary-item">
                <span class="summary-label">本月收入</span>
                <span class="summary-value">¥{{ monthlyIncome.toLocaleString() }}</span>
                <span class="summary-trend up">+12.5%</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">课程单价</span>
                <span class="summary-value">¥{{ averageFee }}</span>
                <span class="summary-trend stable">持平</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">学员满意度</span>
                <span class="summary-value">{{ satisfaction }}%</span>
                <span class="summary-trend up">+2.3%</span>
              </div>
            </div>
            <div class="income-chart">
              <div class="chart-placeholder">📈 收入趋势图表区域</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :xs="24" :lg="8">
        <!-- 教学评价 -->
        <el-card class="sidebar-card evaluation-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Star />
              </el-icon>
              <span>学员评价</span>
            </div>
          </template>
          <div class="evaluation-overview">
            <div class="rating-summary">
              <div class="overall-rating">
                <span class="rating-value">{{ overallRating }}</span>
                <div class="rating-stars">
                  <el-rate :model-value="overallRating" disabled />
                </div>
                <div class="rating-count">基于 {{ evaluationCount }} 个评价</div>
              </div>
            </div>
            <div class="recent-evaluations">
              <div class="evaluation-title">最新评价</div>
              <div v-for="evaluation in recentEvaluations" :key="evaluation.id" class="evaluation-item">
                <div class="evaluation-header">
                  <span class="student-name">{{ evaluation.studentName }}</span>
                  <el-rate :model-value="evaluation.rating" disabled size="small" />
                </div>
                <div class="evaluation-comment">{{ evaluation.comment }}</div>
                <div class="evaluation-time">{{ formatTime(evaluation.time) }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 课程申请 -->
        <el-card class="sidebar-card applications-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Bell />
              </el-icon>
              <span>课程申请</span>
              <el-badge :value="pendingApplications.length" class="badge" />
            </div>
          </template>
          <div class="applications-list">
            <div v-for="application in pendingApplications" :key="application.id" class="application-item">
              <div class="application-info">
                <div class="student-name">{{ application.studentName }}</div>
                <div class="application-details">
                  <div class="course-type">{{ application.courseType }}</div>
                  <div class="preferred-time">{{ application.preferredTime }}</div>
                </div>
              </div>
              <div class="application-actions">
                <OutlineButton size="sm" color="success" @click="approveApplication(application)">同意</OutlineButton>
                <OutlineButton size="sm" color="danger" @click="rejectApplication(application)">拒绝</OutlineButton>
              </div>
            </div>
          </div>
          <div v-if="pendingApplications.length === 0" class="empty-applications">
            <el-empty description="暂无待处理申请" :image-size="80">
              <template #image>
                <div class="empty-icon">📝</div>
              </template>
            </el-empty>
          </div>
        </el-card>

        <!-- 快捷操作 -->
        <el-card class="sidebar-card actions-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Operation />
              </el-icon>
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <OutlineButton v-for="action in quickActions" :key="action.key" :color="action.color"
              @click="action.handler" class="action-button" size="lg">
              <el-icon style="margin-right:8px">
                <component :is="action.icon" />
              </el-icon>
              {{ action.label }}
            </OutlineButton>
          </div>
        </el-card>

        <!-- 本月统计 -->
        <el-card class="sidebar-card monthly-stats-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <DataAnalysis />
              </el-icon>
              <span>本月统计</span>
            </div>
          </template>
          <div class="monthly-stats">
            <div v-for="stat in monthlyStats" :key="stat.key" class="monthly-stat-item">
              <div class="stat-info">
                <div class="stat-label">{{ stat.label }}</div>
                <div class="stat-value">{{ stat.value }}</div>
              </div>
              <div class="stat-progress">
                <el-progress :percentage="stat.percentage" :color="stat.color" :stroke-width="6" />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'
import { PrimaryButton, OutlineButton } from '@/components/buttons'
import {
  User,
  Calendar,
  Star,
  TrendCharts,
  UserFilled,
  Bell,
  Operation,
  DataAnalysis,
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const incomeTimeRange = ref('month')
const monthlyIncome = ref(18500)
const averageFee = ref(150)
const satisfaction = ref(94)
const overallRating = ref(4.8)
const evaluationCount = ref(156)

const todayStats = ref({
  courses: 5,
  students: 12,
  income: '720',
})

// 教练统计数据
const coachStats = ref([
  {
    key: 'income',
    label: '本月收入',
    value: '¥18.5K',
    icon: TrendCharts,
    gradient: 'linear-gradient(135deg, #ff9800 0%, #f57c00 100%)',
    trend: 'up',
    trendText: '+12.5%',
  },
  {
    key: 'students',
    label: '教授学员',
    value: '28',
    icon: UserFilled,
    gradient: 'linear-gradient(135deg, #4caf50 0%, #2e7d32 100%)',
    trend: 'up',
    trendText: '+3人',
  },
  {
    key: 'courses',
    label: '完成课程',
    value: '124',
    icon: Calendar,
    gradient: 'linear-gradient(135deg, #2196f3 0%, #1565c0 100%)',
    trend: 'up',
    trendText: '+8节',
  },
  {
    key: 'rating',
    label: '学员评分',
    value: '4.8',
    icon: Star,
    gradient: 'linear-gradient(135deg, #9c27b0 0%, #6a1b9a 100%)',
    trend: 'up',
    trendText: '+0.2',
  },
])

// 今日课程安排
const todaySchedule = ref([
  {
    id: 1,
    time: '09:00',
    title: '基础训练课',
    student: '小明',
    type: '基础训练',
    location: '训练室A',
    fee: 150,
    status: 'upcoming',
  },
  {
    id: 2,
    time: '11:00',
    title: '技术提升课',
    student: '小红',
    type: '技术提升',
    location: '训练室B',
    fee: 200,
    status: 'upcoming',
  },
  {
    id: 3,
    time: '14:00',
    title: '比赛训练',
    student: '小华',
    type: '比赛训练',
    location: '比赛厅',
    fee: 250,
    status: 'completed',
  },
])

// 我的学员
const myStudents = ref([
  {
    id: 1,
    name: '小明',
    level: 'B+',
    status: '活跃',
    courses: 24,
    progress: 78,
    rating: 4.9,
    avatar: '',
  },
  {
    id: 2,
    name: '小红',
    level: 'A-',
    status: '活跃',
    courses: 18,
    progress: 85,
    rating: 4.8,
    avatar: '',
  },
  {
    id: 3,
    name: '小华',
    level: 'C+',
    status: '请假',
    courses: 12,
    progress: 65,
    rating: 4.7,
    avatar: '',
  },
  {
    id: 4,
    name: '小李',
    level: 'B',
    status: '活跃',
    courses: 20,
    progress: 72,
    rating: 4.6,
    avatar: '',
  },
])

// 最新评价
const recentEvaluations = ref([
  {
    id: 1,
    studentName: '小明',
    rating: 5,
    comment: '教练很专业，讲解清晰，进步很快！',
    time: dayjs().subtract(2, 'hour').toDate(),
  },
  {
    id: 2,
    studentName: '小红',
    rating: 5,
    comment: '技术指导很到位，很有耐心。',
    time: dayjs().subtract(1, 'day').toDate(),
  },
  {
    id: 3,
    studentName: '小华',
    rating: 4,
    comment: '课程安排合理，收获满满。',
    time: dayjs().subtract(2, 'day').toDate(),
  },
])

// 待处理申请
const pendingApplications = ref([
  {
    id: 1,
    studentName: '张同学',
    courseType: '基础训练',
    preferredTime: '周三 14:00',
  },
  {
    id: 2,
    studentName: '李同学',
    courseType: '技术提升',
    preferredTime: '周五 16:00',
  },
])

// 快捷操作
const quickActions = ref([
  {
    key: 'schedule',
    label: '查看课表',
    color: 'primary',
    icon: Calendar,
    handler: () => router.push('/coach/schedule'),
  },
  {
    key: 'students',
    label: '学员管理',
    color: 'success',
    icon: UserFilled,
    handler: () => router.push('/coach/students'),
  },
  {
    key: 'applications',
    label: '申请审批',
    color: 'warning',
    icon: Bell,
    handler: () => router.push('/coach/applications'),
  },
  {
    key: 'feedback',
    label: '学员反馈',
    color: 'info',
    icon: Star,
    handler: () => router.push('/coach/feedback'),
  },
])

// 本月统计
const monthlyStats = ref([
  { key: 'courses', label: '完成课程', value: '124节', percentage: 82, color: '#67c23a' },
  { key: 'income', label: '收入目标', value: '18.5K', percentage: 92, color: '#409eff' },
  { key: 'satisfaction', label: '满意度', value: '94%', percentage: 94, color: '#e6a23c' },
  { key: 'growth', label: '学员成长', value: '78%', percentage: 78, color: '#f56c6c' },
])

// 计算属性和方法
const getWelcomeMessage = () => {
  const hour = dayjs().hour()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

const getScheduleType = (type) => {
  const typeMap = {
    基础训练: 'primary',
    技术提升: 'success',
    比赛训练: 'warning',
    私人定制: 'danger',
  }
  return typeMap[type] || 'info'
}

const getStatusType = (status) => {
  const statusMap = {
    completed: 'success',
    upcoming: 'warning',
    cancelled: 'danger',
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    completed: '已完成',
    upcoming: '即将开始',
    cancelled: '已取消',
  }
  return textMap[status] || status
}

const getStudentStatusType = (status) => {
  const statusMap = {
    活跃: 'success',
    请假: 'warning',
    停课: 'danger',
  }
  return statusMap[status] || 'info'
}

const formatTime = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

// 导航方法
const goToStudents = () => {
  router.push('/coach/students')
}

const viewStudentDetail = (student) => {
  router.push(`/coach/student-detail?id=${student.id}`)
}

const approveApplication = (application) => {
  console.log('同意申请:', application)
  // 实际应用中这里会调用API
}

const rejectApplication = (application) => {
  console.log('拒绝申请:', application)
  // 实际应用中这里会调用API
}

onMounted(() => {
  // 可以在这里加载教练数据
})
</script>

<style scoped>
.coach-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  border-radius: 20px;
  padding: 40px;
  color: white;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 30px rgba(255, 152, 0, 0.3);
}

.banner-left {
  flex: 1;
}

.welcome-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
}

.welcome-subtitle {
  font-size: 18px;
  opacity: 0.9;
  margin: 0 0 20px 0;
}

.banner-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.avatar-container {
  text-align: center;
}

.user-avatar {
  border: 4px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.avatar-badge {
  margin-top: 10px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #67c23a;
}

.stat-trend.stable {
  color: #909399;
}

/* 内容卡片 */
.content-card,
.sidebar-card {
  border: none;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #333;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon {
  color: #ff9800;
}

/* 课程时间线 */
.schedule-timeline {
  max-height: 400px;
  overflow-y: auto;
}

.timeline-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.timeline-item:last-child {
  border-bottom: none;
}

.timeline-time {
  width: 60px;
  font-weight: 600;
  color: #ff9800;
  text-align: center;
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.timeline-student {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.timeline-fee {
  color: #67c23a;
  font-weight: 600;
}

/* 学员网格 */
.students-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.student-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
}

.student-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.student-info {
  flex: 1;
}

.student-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.student-level {
  font-size: 14px;
  color: #666;
}

.student-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 12px;
  padding-top: 12px;
  border-top: 1px solid #e9ecef;
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 2px;
}

.stat-value {
  font-weight: 600;
  color: #333;
}

.student-action {
  text-align: center;
}

/* 收入概览 */
.income-overview {
  margin-bottom: 20px;
}

.income-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.summary-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.summary-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.summary-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.summary-trend {
  font-size: 12px;
  font-weight: 600;
}

.summary-trend.up {
  color: #67c23a;
}

.summary-trend.stable {
  color: #909399;
}

.chart-placeholder {
  height: 200px;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #666;
}

/* 教学评价 */
.evaluation-overview {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.rating-summary {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.overall-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.rating-value {
  font-size: 36px;
  font-weight: 700;
  color: #ff9800;
}

.rating-count {
  font-size: 12px;
  color: #666;
}

.recent-evaluations {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.evaluation-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.evaluation-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.evaluation-comment {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.evaluation-time {
  font-size: 12px;
  color: #666;
}

/* 申请列表 */
.applications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.application-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.application-info {
  flex: 1;
}

.student-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.application-details {
  font-size: 12px;
  color: #666;
}

.course-type {
  margin-bottom: 2px;
}

.application-actions {
  display: flex;
  gap: 8px;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-button {
  justify-content: flex-start;
  height: 48px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.action-button:hover {
  transform: translateX(4px);
}

/* 本月统计 */
.monthly-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.monthly-stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.stat-progress {
  width: 100px;
  margin-left: 12px;
}

/* 空状态 */
.empty-schedule,
.empty-applications {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.badge {
  margin-left: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
    text-align: center;
    gap: 20px;
    padding: 30px 20px;
  }

  .banner-stats {
    justify-content: center;
  }

  .welcome-title {
    font-size: 24px;
  }

  .income-summary {
    grid-template-columns: 1fr;
  }

  .students-grid {
    grid-template-columns: 1fr;
  }

  .evaluation-overview {
    gap: 16px;
  }

  .timeline-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .application-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .application-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
