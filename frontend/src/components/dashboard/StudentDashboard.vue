<template>
  <div class="student-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-left">
        <div class="welcome-content">
          <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}！</h1>
          <p class="welcome-subtitle">今天也要加油训练哦！</p>
          <div class="banner-stats">
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.courses }}</span>
              <span class="stat-label">今日课程</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.hours }}</span>
              <span class="stat-label">训练时长</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">¥{{ userBalance }}</span>
              <span class="stat-label">账户余额</span>
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
            <el-tag type="primary" size="small">学员</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in studentStats" :key="stat.key">
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
                <span>今日课程</span>
              </div>
              <el-button type="primary" size="small" @click="goToSchedule">查看完整课表</el-button>
            </div>
          </template>

          <div v-if="todaySchedule.length > 0" class="schedule-timeline">
            <div v-for="item in todaySchedule" :key="item.id" class="timeline-item">
              <div class="timeline-time">{{ item.time }}</div>
              <div class="timeline-content">
                <div class="timeline-title">{{ item.title }}</div>
                <div class="timeline-description">{{ item.description }}</div>
                <div class="timeline-meta">
                  <el-tag :type="getScheduleType(item.type)" size="small">{{ item.type }}</el-tag>
                  <span class="timeline-location">{{ item.location }}</span>
                  <span class="timeline-coach">教练：{{ item.coach }}</span>
                </div>
              </div>
              <div class="timeline-status">
                <el-button v-if="item.status === 'upcoming'" size="small" type="primary">
                  开始训练
                </el-button>
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
              <el-button type="primary" @click="goToBooking">预约课程</el-button>
            </el-empty>
          </div>
        </el-card>

        <!-- 我的教练 -->
        <el-card class="content-card coaches-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <UserFilled />
                </el-icon>
                <span>我的教练</span>
              </div>
              <el-link type="primary" @click="goToCoaches">查看全部</el-link>
            </div>
          </template>

          <div class="coaches-list">
            <div v-for="coach in myCoaches" :key="coach.id" class="coach-item">
              <div class="coach-avatar">
                <el-avatar :size="48" :src="coach.avatar">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
              </div>
              <div class="coach-info">
                <div class="coach-name">{{ coach.name }}</div>
                <div class="coach-specialty">{{ coach.specialty }}</div>
                <div class="coach-rating">
                  <el-rate :model-value="coach.rating" disabled show-score text-color="#ff9900" />
                </div>
              </div>
              <div class="coach-action">
                <el-button size="small" type="primary" @click="contactCoach(coach)">
                  联系教练
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :xs="24" :lg="8">
        <!-- 账户余额 -->
        <el-card class="sidebar-card balance-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Wallet />
              </el-icon>
              <span>账户余额</span>
            </div>
          </template>
          <div class="balance-content">
            <div class="balance-amount">¥{{ userBalance }}</div>
            <div class="balance-status">
              <el-tag :type="getBalanceStatus().type" size="small">
                {{ getBalanceStatus().text }}
              </el-tag>
            </div>
            <div class="balance-actions">
              <el-button type="primary" @click="goToRecharge">立即充值</el-button>
              <el-button @click="viewTransactions">消费记录</el-button>
            </div>
          </div>

          <div class="recent-transactions">
            <div class="transactions-title">最近消费</div>
            <div
              v-for="transaction in recentTransactions"
              :key="transaction.id"
              class="transaction-item"
            >
              <div class="transaction-info">
                <div class="transaction-title">{{ transaction.title }}</div>
                <div class="transaction-time">{{ formatTime(transaction.time) }}</div>
              </div>
              <div class="transaction-amount" :class="transaction.type">
                {{ transaction.type === 'expense' ? '-' : '+' }}¥{{ transaction.amount }}
              </div>
            </div>
          </div>
        </el-card>

        <!-- 学习进度 -->
        <el-card class="sidebar-card progress-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <TrendCharts />
              </el-icon>
              <span>学习进度</span>
            </div>
          </template>
          <div class="progress-stats">
            <div v-for="progress in learningProgress" :key="progress.key" class="progress-item">
              <div class="progress-info">
                <div class="progress-label">{{ progress.label }}</div>
                <div class="progress-value">{{ progress.value }}</div>
              </div>
              <div class="progress-bar">
                <el-progress
                  :percentage="progress.percentage"
                  :color="progress.color"
                  :stroke-width="8"
                />
              </div>
            </div>
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
            <el-button
              v-for="action in quickActions"
              :key="action.key"
              :type="action.type"
              @click="action.handler"
              class="action-button"
              size="large"
            >
              <template #icon>
                <component :is="action.icon" />
              </template>
              {{ action.label }}
            </el-button>
          </div>
        </el-card>

        <!-- 最新成就 -->
        <el-card class="sidebar-card achievements-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Trophy />
              </el-icon>
              <span>最新成就</span>
            </div>
          </template>
          <div class="achievements">
            <div
              v-for="achievement in recentAchievements"
              :key="achievement.id"
              class="achievement-item"
            >
              <div class="achievement-icon">{{ achievement.icon }}</div>
              <div class="achievement-info">
                <div class="achievement-title">{{ achievement.title }}</div>
                <div class="achievement-date">{{ formatDate(achievement.date) }}</div>
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
import {
  User,
  Calendar,
  Star,
  TrendCharts,
  UserFilled,
  Wallet,
  Trophy,
  Operation,
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const userBalance = ref(1250.0)
const todayStats = ref({
  courses: 2,
  hours: '3h',
})

// 学生统计数据
const studentStats = ref([
  {
    key: 'courses',
    label: '已报课程',
    value: '12',
    icon: Calendar,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    trend: 'up',
    trendText: '+2 本月',
  },
  {
    key: 'hours',
    label: '训练时长',
    value: '48h',
    icon: TrendCharts,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    trend: 'up',
    trendText: '+8h 本周',
  },
  {
    key: 'coaches',
    label: '专属教练',
    value: '3',
    icon: UserFilled,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    trend: 'stable',
    trendText: '保持稳定',
  },
  {
    key: 'level',
    label: '技能等级',
    value: 'B+',
    icon: Star,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    trend: 'up',
    trendText: '最近提升',
  },
])

// 今日课程
const todaySchedule = ref([
  {
    id: 1,
    time: '09:00',
    title: '基础训练课',
    description: '基础技能训练，包括正手、反手练习',
    type: '基础训练',
    location: '训练室A',
    coach: '张教练',
    status: 'upcoming',
  },
  {
    id: 2,
    time: '16:00',
    title: '技术提升课',
    description: '高级技巧训练，战术指导',
    type: '技术提升',
    location: '训练室B',
    coach: '李教练',
    status: 'upcoming',
  },
])

// 我的教练
const myCoaches = ref([
  {
    id: 1,
    name: '张教练',
    specialty: '基础技能',
    rating: 4.8,
    avatar: '',
  },
  {
    id: 2,
    name: '李教练',
    specialty: '技术提升',
    rating: 4.9,
    avatar: '',
  },
  {
    id: 3,
    name: '王教练',
    specialty: '战术分析',
    rating: 4.7,
    avatar: '',
  },
])

// 最近交易记录
const recentTransactions = ref([
  {
    id: 1,
    title: '基础训练课',
    amount: 120,
    type: 'expense',
    time: dayjs().subtract(1, 'hour').toDate(),
  },
  {
    id: 2,
    title: '账户充值',
    amount: 500,
    type: 'income',
    time: dayjs().subtract(1, 'day').toDate(),
  },
  {
    id: 3,
    title: '技术提升课',
    amount: 150,
    type: 'expense',
    time: dayjs().subtract(2, 'day').toDate(),
  },
])

// 学习进度
const learningProgress = ref([
  { key: 'basic', label: '基础技能', value: '85%', percentage: 85, color: '#67c23a' },
  { key: 'advanced', label: '高级技巧', value: '65%', percentage: 65, color: '#409eff' },
  { key: 'tactics', label: '战术理解', value: '70%', percentage: 70, color: '#e6a23c' },
  { key: 'physical', label: '体能训练', value: '80%', percentage: 80, color: '#f56c6c' },
])

// 快捷操作
const quickActions = ref([
  {
    key: 'booking',
    label: '预约课程',
    type: 'primary',
    icon: Calendar,
    handler: () => router.push('/student/book-training'),
  },
  {
    key: 'coaches',
    label: '找教练',
    type: 'success',
    icon: UserFilled,
    handler: () => router.push('/student/find-coach'),
  },
  {
    key: 'recharge',
    label: '账户充值',
    type: 'warning',
    icon: Wallet,
    handler: () => router.push('/student/account-recharge'),
  },
  {
    key: 'evaluation',
    label: '课程评价',
    type: 'info',
    icon: Star,
    handler: () => router.push('/student/training-evaluation'),
  },
])

// 最新成就
const recentAchievements = ref([
  { id: 1, icon: '🏆', title: '连续训练7天', date: dayjs().subtract(1, 'day').toDate() },
  { id: 2, icon: '⭐', title: '技能等级提升', date: dayjs().subtract(3, 'day').toDate() },
  { id: 3, icon: '🎯', title: '完成月度目标', date: dayjs().subtract(1, 'week').toDate() },
])

// 计算属性和方法
const getWelcomeMessage = () => {
  const hour = dayjs().hour()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

const getBalanceStatus = () => {
  if (userBalance.value > 1000) {
    return { type: 'success', text: '余额充足' }
  } else if (userBalance.value > 500) {
    return { type: 'warning', text: '余额适中' }
  } else {
    return { type: 'danger', text: '余额不足' }
  }
}

const getScheduleType = (type) => {
  const typeMap = {
    基础训练: 'primary',
    技术提升: 'success',
    战术训练: 'warning',
    比赛: 'danger',
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

const formatTime = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

const formatDate = (date) => {
  return dayjs(date).format('MM月DD日')
}

// 导航方法
const goToSchedule = () => {
  router.push('/student/schedule')
}

const goToBooking = () => {
  router.push('/student/book-training')
}

const goToCoaches = () => {
  router.push('/student/my-coaches')
}

const goToRecharge = () => {
  router.push('/student/account-recharge')
}

const viewTransactions = () => {
  router.push('/student/transactions')
}

const contactCoach = (coach) => {
  router.push(`/student/coach-profile?id=${coach.id}`)
}

onMounted(() => {
  // 可以在这里加载用户数据
})
</script>

<style scoped>
.student-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 40px;
  color: white;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
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
  color: #667eea;
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
  color: #667eea;
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

.timeline-description {
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

/* 教练列表 */
.coaches-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coach-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.coach-info {
  flex: 1;
}

.coach-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.coach-specialty {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

/* 余额卡片 */
.balance-content {
  text-align: center;
  margin-bottom: 20px;
}

.balance-amount {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.balance-status {
  margin-bottom: 20px;
}

.balance-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.recent-transactions {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.transactions-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-title {
  font-size: 14px;
  color: #333;
}

.transaction-time {
  font-size: 12px;
  color: #999;
}

.transaction-amount {
  font-weight: 600;
}

.transaction-amount.expense {
  color: #f56c6c;
}

.transaction-amount.income {
  color: #67c23a;
}

/* 学习进度 */
.progress-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-label {
  font-size: 14px;
  color: #666;
}

.progress-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.progress-bar {
  width: 100px;
  margin-left: 12px;
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

/* 成就 */
.achievements {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.achievement-icon {
  font-size: 24px;
}

.achievement-title {
  font-size: 14px;
  font-weight: 600;
}

.achievement-date {
  font-size: 12px;
  color: #999;
}

/* 空状态 */
.empty-schedule {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
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

  .balance-actions {
    flex-direction: column;
  }

  .timeline-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .coach-item {
    flex-direction: column;
    text-align: center;
  }
}
</style>
