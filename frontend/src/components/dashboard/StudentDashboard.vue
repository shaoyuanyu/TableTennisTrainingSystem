<template>
  <div class="student-dashboard">
    <!-- 欢迎横幅 - 集成学习统计 -->
    <div class="welcome-banner-enhanced">
      <div class="banner-main">
        <div class="welcome-content">
          <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}！</h1>
          <p class="welcome-subtitle">今天也要加油训练哦！🏓</p>
          
          <!-- 今日概览 -->
          <div class="today-overview">
            <div class="overview-item">
              <div class="overview-icon">📚</div>
              <div class="overview-content">
                <span class="overview-value">{{ todayStats.courses }}</span>
                <span class="overview-label">今日课程</span>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-icon">⏱️</div>
              <div class="overview-content">
                <span class="overview-value">{{ todayStats.hours }}</span>
                <span class="overview-label">今日训练</span>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-icon">💰</div>
              <div class="overview-content">
                <span class="overview-value">¥{{ userBalance }}</span>
                <span class="overview-label">账户余额</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="avatar-container">
          <el-avatar :size="80" :src="userStore.userInfo.avatar" class="user-avatar">
            <el-icon size="40">
              <User />
            </el-icon>
          </el-avatar>
          <div class="avatar-badge">
            <el-tag type="primary" size="small">学员</el-tag>
          </div>
          <div class="avatar-decoration">✨</div>
        </div>
      </div>
      
      <!-- 学习统计数据 -->
      <div class="stats-overview">
        <h3 class="stats-title">
          <el-icon class="title-icon">
            <TrendCharts />
          </el-icon>
          学习进度总览
        </h3>
        <div class="stats-grid">
          <div class="mini-stat-card" v-for="stat in studentStats" :key="stat.key" @click="handleStatClick(stat)">
            <div class="mini-stat-icon" :style="{ background: stat.gradient }">
              <span class="emoji-icon">{{ stat.icon }}</span>
            </div>
            <div class="mini-stat-info">
              <div class="mini-stat-value">{{ stat.value }}</div>
              <div class="mini-stat-label">{{ stat.label }}</div>
              <div class="mini-stat-trend" :class="stat.trend">
                <el-icon v-if="stat.trend === 'up'" size="12">
                  <TrendCharts />
                </el-icon>
                <span>{{ stat.trendText }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要功能区域 -->
    <el-row :gutter="24" class="main-content">
      <!-- 左侧内容 -->
      <el-col :xs="24" :lg="13">
        <!-- 今日课程安排 -->
        <div class="content-card schedule-card">
          <div class="card-header">
            <div class="header-left">
              <div class="header-icon-wrapper">
                <el-icon class="header-icon">
                  <Calendar />
                </el-icon>
              </div>
              <div class="header-text">
                <h3>今日课程</h3>
                <p>今天的训练安排</p>
              </div>
            </div>
            <el-button type="primary" size="small" @click="goToSchedule" class="modern-button">
              <el-icon><View /></el-icon>
              查看完整课表
            </el-button>
          </div>

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
            <div class="empty-content">
              <div class="empty-icon">📚</div>
              <h4>今天没有课程安排</h4>
              <p>快去预约您感兴趣的课程吧！</p>
              <el-button type="primary" @click="goToBooking" class="modern-button">
                <el-icon><Plus /></el-icon>
                预约课程
              </el-button>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="content-card actions-card">
          <div class="card-header">
            <div class="header-left">
              <div class="header-icon-wrapper">
                <el-icon class="header-icon">
                  <Operation />
                </el-icon>
              </div>
              <div class="header-text">
                <h3>快捷操作</h3>
                <p>常用功能入口</p>
              </div>
            </div>
          </div>
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
        </div>

        <!-- 账户余额 -->
        <div class="content-card balance-card">
          <div class="card-header">
            <div class="header-left">
              <div class="header-icon-wrapper">
                <el-icon class="header-icon">
                  <Wallet />
                </el-icon>
              </div>
              <div class="header-text">
                <h3>账户余额</h3>
                <p>我的财务状况</p>
              </div>
            </div>
          </div>
          <div class="balance-content">
            <div class="balance-amount">¥{{ userBalance }}</div>
            <div class="balance-status">
              <el-tag :type="getBalanceStatus().type" size="small">
                {{ getBalanceStatus().text }}
              </el-tag>
            </div>
            <div class="balance-actions">
              <el-button type="primary" @click="goToRecharge" class="modern-button">
                <el-icon><CreditCard /></el-icon>
                立即充值
              </el-button>
              <el-button @click="viewTransactions" class="modern-button">
                <el-icon><List /></el-icon>
                消费记录
              </el-button>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :xs="24" :lg="11">
        <!-- 学习进度 -->
        <div class="content-card progress-card">
          <div class="card-header">
            <div class="header-left">
              <div class="header-icon-wrapper">
                <el-icon class="header-icon">
                  <TrendCharts />
                </el-icon>
              </div>
              <div class="header-text">
                <h3>学习进度</h3>
                <p>技能提升轨迹</p>
              </div>
            </div>
          </div>
          <div class="progress-stats">
            <div v-for="progress in progressData" :key="progress.key" class="progress-item">
              <div class="progress-header">
                <div class="progress-info">
                  <div class="progress-label">{{ progress.label }}</div>
                </div>
                <div class="progress-percentage">{{ progress.percentage }}%</div>
              </div>
              <div class="progress-bar-container">
                <el-progress
                  :percentage="progress.percentage"
                  :color="progress.color"
                  :stroke-width="12"
                  :show-text="false"
                  class="enhanced-progress"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 我的教练 -->
        <div class="content-card coaches-card">
          <div class="card-header">
            <div class="header-left">
              <div class="header-icon-wrapper">
                <el-icon class="header-icon">
                  <UserFilled />
                </el-icon>
              </div>
              <div class="header-text">
                <h3>我的教练</h3>
                <p>专属教练团队 ({{ myCoaches.length }}/2)</p>
              </div>
            </div>
            <el-button v-if="myCoaches.length < 2" type="primary" size="small" @click="goToFindCoach" class="modern-button">
              <el-icon><Plus /></el-icon>
              寻找教练
            </el-button>
            <el-link v-else type="primary" @click="goToCoaches" class="modern-link">
              <el-icon><ArrowRight /></el-icon>
              管理教练
            </el-link>
          </div>

          <div v-if="myCoaches.length > 0" class="coaches-list">
            <div v-for="coach in myCoaches" :key="coach.id" class="coach-item-enhanced">
              <div class="coach-avatar">
                <el-avatar :size="45" :src="coach.avatar">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
                <div class="coach-status" :class="coach.status"></div>
              </div>
              <div class="coach-info">
                <div class="coach-name">{{ coach.name }}</div>
                <div class="coach-specialty">{{ coach.specialty }}</div>
                <div class="coach-experience">{{ coach.experience }}</div>
                <div class="coach-rating">
                  <el-rate :model-value="coach.rating" disabled show-score text-color="#ff9900" size="small" />
                </div>
              </div>
              <div class="coach-actions">
                <el-button size="small" type="primary" @click="contactCoach(coach)" class="modern-button">
                  <el-icon><ChatDotRound /></el-icon>
                  联系
                </el-button>
                <el-button size="small" @click="viewCoachProfile(coach)" class="modern-button">
                  <el-icon><View /></el-icon>
                  详情
                </el-button>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-coaches">
            <div class="empty-content">
              <div class="empty-icon">👨‍🏫</div>
              <h4>还没有专属教练</h4>
              <p>找到适合的教练，开始您的训练之旅！</p>
              <el-button type="primary" @click="goToFindCoach" class="modern-button">
                <el-icon><Plus /></el-icon>
                寻找教练
              </el-button>
            </div>
          </div>
        </div>
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
  Operation,
  View,
  Plus,
  ArrowRight,
  ChatDotRound,
  CreditCard,
  List,
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
    icon: '📚',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    trend: 'up',
    trendText: '+2 本月',
  },
  {
    key: 'hours',
    label: '训练时长',
    value: '48h',
    icon: '⏱️',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    trend: 'up',
    trendText: '+8h 本周',
  },
  {
    key: 'coaches',
    label: '专属教练',
    value: '3',
    icon: '👨‍🏫',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    trend: 'stable',
    trendText: '保持稳定',
  },
  {
    key: 'level',
    label: '技能等级',
    value: 'B+',
    icon: '🏆',
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
    experience: '5年教学经验',
    status: 'active',
  },
  {
    id: 2,
    name: '李教练',
    specialty: '技术提升',
    rating: 4.9,
    avatar: '',
    experience: '8年教学经验',
    status: 'active',
  },
])

// 最近交易记录
// 学习进度
const progressData = ref([
  { key: 'basic', label: '基础技能', percentage: 85, color: '#67c23a' },
  { key: 'advanced', label: '高级技巧', percentage: 65, color: '#409eff' },
  { key: 'tactics', label: '战术理解', percentage: 70, color: '#e6a23c' },
  { key: 'physical', label: '体能训练', percentage: 80, color: '#f56c6c' },
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

const viewCoachProfile = (coach) => {
  router.push(`/student/coach-profile?id=${coach.id}`)
}

const goToFindCoach = () => {
  router.push('/student/find-coach')
}

// 处理统计卡片点击
const handleStatClick = (stat) => {
  const routeMap = {
    courses: '/student/schedule',
    hours: '/student/training-history', 
    coaches: '/student/my-coaches',
    level: '/student/skill-assessment'
  }
  
  const route = routeMap[stat.key]
  if (route) {
    router.push(route)
  }
}

onMounted(() => {
  // 可以在这里加载用户数据
})
</script>

<style scoped>
/* 主容器 - 完全透明背景 */
.student-dashboard {
  min-height: 100vh;
  background: transparent;
  padding: var(--spacing-xl);
  position: relative;
}

/* 欢迎横幅 - 玻璃形态设计 */

.ball-1 {
  top: 10%;
  left: 15%;
  animation-delay: 0s;
}

.ball-2 {
  top: 70%;
  right: 20%;
  animation-delay: 2s;
}

.ball-3 {
  bottom: 20%;
  left: 10%;
  animation-delay: 4s;
}

.ping-pong-paddle {
  position: absolute;
  width: 50px;
  height: 70px;
  background: rgba(139, 69, 19, 0.2);
  border-radius: 25px 25px 8px 8px;
  transform: rotate(45deg);
  animation: rotate 12s linear infinite;
  backdrop-filter: blur(1px);
}

.paddle-1 {
  top: 20%;
  right: 10%;
  animation-delay: 1s;
}

.paddle-2 {
  bottom: 15%;
  right: 15%;
  animation-delay: 3s;
  transform: rotate(-45deg);
}

/* Emoji装饰样式 - 更微妙的效果 */
.emoji-decoration {
  position: absolute;
  font-size: 32px;
  opacity: 0.25;
  pointer-events: none;
  user-select: none;
  animation: float 10s ease-in-out infinite;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.ping-pong-emoji-1 {
  top: 15%;
  left: 10%;
  animation-delay: 0s;
  animation-duration: 8s;
}

.ping-pong-emoji-2 {
  top: 75%;
  right: 15%;
  animation-delay: 2s;
  animation-duration: 7s;
}

.trophy-emoji {
  top: 20%;
  right: 20%;
  font-size: 35px;
  animation: pulse 4s ease-in-out infinite;
  animation-delay: 1s;
}

.star-emoji {
  top: 35%;
  left: 5%;
  font-size: 25px;
  animation: pulse 5s ease-in-out infinite;
  animation-delay: 1.5s;
}

.fire-emoji {
  top: 80%;
  right: 10%;
  font-size: 30px;
  animation: wiggle 2s ease-in-out infinite;
  animation-delay: 2.5s;
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  animation: float 12s ease-in-out infinite;
  backdrop-filter: blur(1px);
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 30%;
  left: 5%;
  animation-delay: 0.5s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 5%;
  animation-delay: 2.5s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 15%;
  right: 30%;
  animation-delay: 4.5s;
}

/* 动画效果 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
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
  0%, 100% {
    transform: scale(1);
    opacity: 0.3;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.5;
  }
}

@keyframes wiggle {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(5deg);
  }
  75% {
    transform: rotate(-5deg);
  }
}

/* 欢迎横幅 - 玻璃形态设计 */
/* 增强版欢迎横幅 */
.welcome-banner-enhanced {
  background: var(--white-alpha-15);
  backdrop-filter: var(--blur-xl);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-2xl);
  padding: var(--spacing-3xl);
  color: white;
  margin-bottom: var(--spacing-3xl);
  box-shadow: var(--shadow-lg);
  position: relative;
  z-index: 1;
  transition: var(--transition-normal);
}

.welcome-banner-enhanced:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-xl);
}

.banner-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-2xl);
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-size: var(--font-size-4xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-md) 0;
  text-shadow: 0 2px 4px var(--black-alpha-30);
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: var(--font-size-xl);
  opacity: 0.9;
  margin: 0 0 var(--spacing-xl) 0;
  text-shadow: 0 1px 2px var(--black-alpha-20);
}

/* 今日概览 */
.today-overview {
  display: flex;
  gap: var(--spacing-2xl);
  flex-wrap: wrap;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--white-alpha-15);
  border-radius: var(--radius-lg);
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  transition: var(--transition-normal);
}

.overview-item:hover {
  background: var(--white-alpha-25);
  transform: translateY(-1px);
}

.overview-icon {
  font-size: var(--font-size-xl);
  filter: drop-shadow(0 1px 2px var(--black-alpha-30));
}

.overview-content {
  display: flex;
  flex-direction: column;
}

.overview-value {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  line-height: 1;
  text-shadow: 0 1px 2px var(--black-alpha-20);
}

.overview-label {
  font-size: var(--font-size-xs);
  opacity: 0.8;
  margin-top: 2px;
}

/* 学习统计概览 */
.stats-overview {
  border-top: 1px solid var(--white-alpha-10);
  padding-top: var(--spacing-2xl);
}

.stats-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  margin: 0 0 var(--spacing-lg) 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  text-shadow: 0 1px 2px var(--black-alpha-20);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.mini-stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  cursor: pointer;
}

.mini-stat-card:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.mini-stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 
    0 3px 8px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.mini-stat-info {
  flex: 1;
  min-width: 0;
}

.mini-stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.mini-stat-label {
  font-size: 13px;
  opacity: 0.8;
  margin: 2px 0 4px 0;
}

.mini-stat-trend {
  font-size: 11px;
  opacity: 0.7;
  display: flex;
  align-items: center;
  gap: 4px;
}

.mini-stat-trend.up {
  color: #4ade80;
}

.mini-stat-trend.stable {
  color: #fbbf24;
}

.stat-icon {
  font-size: 24px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.avatar-container {
  text-align: center;
  position: relative;
}

.user-avatar {
  border: 4px solid rgba(255, 255, 255, 0.3);
  box-shadow: 
    0 8px 24px rgba(0, 0, 0, 0.2),
    inset 0 2px 0 rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 
    0 12px 32px rgba(0, 0, 0, 0.25),
    inset 0 2px 0 rgba(255, 255, 255, 0.4);
}

.avatar-badge {
  margin-top: 10px;
}

.avatar-decoration {
  position: absolute;
  top: -10px;
  right: -10px;
  font-size: 20px;
  animation: pulse 2s ease-in-out infinite;
}

/* 统计部分 */
.stats-section {
  margin-bottom: 40px;
  position: relative;
  z-index: 1;
}

.section-header {
  text-align: center;
  margin-bottom: 32px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.title-icon {
  font-size: 28px;
  color: rgba(255, 255, 255, 0.9);
  margin-right: 8px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.section-subtitle {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.stats-row {
  position: relative;
  z-index: 1;
}

/* 旧的统计卡片样式已移除 */

/* 主要内容区域 */
.main-content {
  position: relative;
  z-index: 1;
}

/* 内容卡片和边栏卡片 - 统一玻璃形态设计 */
.content-card,
.sidebar-card {
  background: var(--white-alpha-15);
  backdrop-filter: var(--blur-xl);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl);
  margin-bottom: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
  transition: var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.content-card:hover,
.sidebar-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  border-color: var(--white-alpha-30);
}

/* 卡片头部设计 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-2xl);
  padding-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--white-alpha-15);
  position: relative;
  z-index: 2;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.header-icon-wrapper {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--white-alpha-25), var(--white-alpha-10));
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  box-shadow: var(--shadow-sm);
}

.header-icon {
  color: white;
  font-size: var(--font-size-xl);
}

.header-text h3 {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: white;
  text-shadow: 0 1px 2px var(--black-alpha-30);
}

.header-text p {
  margin: 0;
  font-size: var(--font-size-md);
  color: var(--white-alpha-80);
}

/* 统一的按钮和标签样式 */
/* 主按钮样式 */
.el-button--primary.modern-button {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.8), rgba(100, 181, 246, 0.6)) !important;
  border: 1px solid rgba(64, 158, 255, 0.3) !important;
  color: white !important;
  backdrop-filter: blur(10px) !important;
  border-radius: 12px !important;
  font-weight: 600 !important;
  padding: 8px 16px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative !important;
  overflow: hidden !important;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2) !important;
}

.el-button--primary.modern-button:hover {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.9), rgba(100, 181, 246, 0.7)) !important;
  border-color: rgba(64, 158, 255, 0.5) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3) !important;
}

/* 次要按钮样式 */
.el-button.modern-button:not(.el-button--primary) {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(10px) !important;
  border-radius: 12px !important;
  font-weight: 500 !important;
  padding: 8px 16px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1) !important;
}

.el-button.modern-button:not(.el-button--primary):hover {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

/* 小尺寸按钮 */
.el-button--small.modern-button {
  padding: 6px 12px !important;
  font-size: 13px !important;
  border-radius: 10px !important;
}

/* 标签样式统一 */
.el-tag {
  background: rgba(255, 255, 255, 0.15) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(8px) !important;
  border-radius: 8px !important;
  font-weight: 500 !important;
  padding: 4px 8px !important;
  transition: all 0.3s ease !important;
}

/* 不同类型的标签 */
.el-tag--primary {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.3), rgba(100, 181, 246, 0.2)) !important;
  border-color: rgba(64, 158, 255, 0.4) !important;
  color: rgba(255, 255, 255, 0.95) !important;
}

.el-tag--success {
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.3), rgba(139, 195, 74, 0.2)) !important;
  border-color: rgba(103, 194, 58, 0.4) !important;
  color: rgba(255, 255, 255, 0.95) !important;
}

.el-tag--warning {
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.3), rgba(255, 193, 7, 0.2)) !important;
  border-color: rgba(230, 162, 60, 0.4) !important;
  color: rgba(255, 255, 255, 0.95) !important;
}

.el-tag--danger {
  background: linear-gradient(135deg, rgba(245, 108, 108, 0.3), rgba(244, 67, 54, 0.2)) !important;
  border-color: rgba(245, 108, 108, 0.4) !important;
  color: rgba(255, 255, 255, 0.95) !important;
}

.el-tag--info {
  background: linear-gradient(135deg, rgba(144, 147, 153, 0.3), rgba(158, 158, 158, 0.2)) !important;
  border-color: rgba(144, 147, 153, 0.4) !important;
  color: rgba(255, 255, 255, 0.95) !important;
}

.modern-link {
  color: rgba(255, 255, 255, 0.9) !important;
  font-weight: 600 !important;
  display: flex !important;
  align-items: center !important;
  gap: 4px !important;
  transition: all 0.3s ease !important;
}

.modern-link:hover {
  color: white !important;
  transform: translateX(4px) !important;
}

/* 课程时间线 */
.schedule-timeline {
  max-height: 400px;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px; /* 为滚动条留出空间 */
}

.timeline-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  margin-bottom: 16px;
  margin-right: 4px; /* 防止悬停时溢出 */
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.timeline-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.timeline-time {
  width: 80px;
  font-weight: 700;
  color: white;
  text-align: center;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.15);
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
}

.timeline-content {
  flex: 1;
}

.timeline-title {
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin-bottom: 6px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.timeline-description {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8px;
  line-height: 1.4;
}

.timeline-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  flex-wrap: wrap;
}

.timeline-status {
  min-width: 100px;
  text-align: right;
}

/* 时间轴按钮样式 */
.timeline-status .el-button {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.8), rgba(100, 181, 246, 0.6)) !important;
  border: 1px solid rgba(64, 158, 255, 0.3) !important;
  color: white !important;
  border-radius: 8px !important;
  font-size: 12px !important;
  padding: 4px 12px !important;
  backdrop-filter: blur(8px) !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2) !important;
  transition: all 0.3s ease !important;
}

.timeline-status .el-button:hover {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.9), rgba(100, 181, 246, 0.7)) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3) !important;
}

/* 空状态设计 */
.empty-schedule {
  text-align: center;
  padding: 60px 20px;
}

.empty-content {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 40px 30px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.8;
}

.empty-content h4 {
  margin: 0 0 12px 0;
  font-size: 20px;
  color: white;
  font-weight: 600;
}

.empty-content p {
  margin: 0 0 24px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
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
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.coach-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.coach-avatar {
  position: relative;
}

.coach-avatar .el-avatar {
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.coach-info {
  flex: 1;
  min-width: 0;
}

.coach-name {
  font-weight: 600;
  color: white;
  margin-bottom: 4px;
  font-size: 16px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.coach-specialty {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 6px;
}

/* 增强版教练卡片 */
.coach-item-enhanced {
  display: flex;
  align-items: center;
  gap: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
  margin-bottom: 16px;
  padding: 16px 20px;
  overflow: hidden;
}

.coach-item-enhanced:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.coach-avatar {
  position: relative;
  flex-shrink: 0;
}

.coach-avatar .el-avatar {
  border: 2px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.coach-status {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
}

.coach-status.active {
  background: #67c23a;
}

.coach-status.inactive {
  background: #909399;
}

.coach-experience {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 4px;
}

.coach-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-shrink: 0;
}

.coach-actions .modern-button {
  font-size: 12px;
  padding: 6px 12px;
  height: auto;
  min-width: 60px;
  border-radius: 8px !important;
  font-weight: 500 !important;
}

/* 空状态 */
.empty-coaches {
  text-align: center;
  padding: 40px 20px;
}

.empty-coaches .empty-content {
  color: rgba(255, 255, 255, 0.8);
}

.empty-coaches .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-coaches h4 {
  color: white;
  margin: 0 0 8px 0;
  font-size: 16px;
}

.empty-coaches p {
  margin: 0 0 20px 0;
  font-size: 14px;
  opacity: 0.8;
}

/* 余额卡片 */
.balance-content {
  text-align: center;
  margin-bottom: 24px;
}

.balance-amount {
  font-size: 48px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #fff 0%, #f8fafc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.balance-status {
  margin-bottom: 24px;
}

.balance-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.recent-transactions {
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  padding-top: 20px;
}

.transactions-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  margin-bottom: 16px;
  color: white;
  font-size: 16px;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.transaction-item:hover {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.2);
}

.transaction-title {
  font-size: 14px;
  color: white;
  font-weight: 500;
}

.transaction-time {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.transaction-amount {
  font-weight: 600;
  font-size: 14px;
}

.transaction-amount.expense {
  color: #ff6b6b;
}

.transaction-amount.income {
  color: #51cf66;
}

/* 学习进度 */
.progress-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-item {
  padding: 20px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.progress-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.8s ease;
}

.progress-item:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.progress-item:hover::before {
  left: 100%;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.progress-info {
  flex: 1;
}

.progress-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 4px;
  font-weight: 500;
}

.progress-percentage {
  font-size: 24px;
  font-weight: 800;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  opacity: 0.9;
}

.progress-bar-container {
  position: relative;
}

.enhanced-progress {
  width: 100%;
}

/* 增强版进度条样式 */
.enhanced-progress .el-progress-bar__outer {
  background-color: rgba(255, 255, 255, 0.1) !important;
  border-radius: 12px !important;
  backdrop-filter: blur(4px) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  height: 12px !important;
  position: relative !important;
  overflow: hidden !important;
}

.enhanced-progress .el-progress-bar__outer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, 
    rgba(255, 255, 255, 0.02) 25%, 
    transparent 25%, 
    transparent 50%, 
    rgba(255, 255, 255, 0.02) 50%, 
    rgba(255, 255, 255, 0.02) 75%, 
    transparent 75%, 
    transparent);
  background-size: 8px 8px;
  animation: progressStripes 1s linear infinite;
}

@keyframes progressStripes {
  0% { background-position: 0 0; }
  100% { background-position: 8px 0; }
}

.enhanced-progress .el-progress-bar__inner {
  border-radius: 12px !important;
  position: relative !important;
  overflow: hidden !important;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.3) !important;
  background: linear-gradient(135deg, 
    var(--el-color-primary) 0%, 
    var(--el-color-primary-light-3) 50%, 
    var(--el-color-primary) 100%) !important;
  transition: all 0.3s ease !important;
}

.enhanced-progress .el-progress-bar__inner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, 
    rgba(255, 255, 255, 0.0) 0%, 
    rgba(255, 255, 255, 0.3) 50%, 
    rgba(255, 255, 255, 0.0) 100%);
  transform: translateX(-100%);
  animation: progressShine 2s ease-in-out infinite;
}

@keyframes progressShine {
  0% { transform: translateX(-100%); }
  50% { transform: translateX(100%); }
  100% { transform: translateX(100%); }
}

@keyframes progressPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}

/* 为高进度值添加脉冲效果 */
.progress-item:nth-child(1) .enhanced-progress .el-progress-bar__inner,
.progress-item:nth-child(4) .enhanced-progress .el-progress-bar__inner {
  animation: progressPulse 3s ease-in-out infinite;
}

/* 进度条容器增强 */
.progress-bar-container {
  position: relative;
  margin-top: 8px;
}

.progress-bar-container::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(255, 255, 255, 0.2) 20%, 
    rgba(255, 255, 255, 0.4) 50%, 
    rgba(255, 255, 255, 0.2) 80%, 
    transparent 100%);
  transform: translateY(-50%);
  opacity: 0.3;
  z-index: -1;
}

.enhanced-progress .el-progress-bar__inner::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 50%;
  background: linear-gradient(to bottom, 
    rgba(255, 255, 255, 0.4), 
    transparent);
  border-radius: 12px 12px 0 0;
}

/* 悬停时的进度条增强效果 */
.progress-item:hover .enhanced-progress .el-progress-bar__inner {
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    0 0 20px var(--el-color-primary-light-5) !important;
  filter: brightness(1.1) !important;
}

/* 不同颜色进度条的特定样式 */
.progress-item:nth-child(1) .enhanced-progress .el-progress-bar__inner {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 50%, #67c23a 100%) !important;
}

.progress-item:nth-child(1):hover .enhanced-progress .el-progress-bar__inner {
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    0 0 20px rgba(103, 194, 58, 0.5) !important;
}

.progress-item:nth-child(2) .enhanced-progress .el-progress-bar__inner {
  background: linear-gradient(135deg, #409eff 0%, #79bbff 50%, #409eff 100%) !important;
}

.progress-item:nth-child(2):hover .enhanced-progress .el-progress-bar__inner {
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    0 0 20px rgba(64, 158, 255, 0.5) !important;
}

.progress-item:nth-child(3) .enhanced-progress .el-progress-bar__inner {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 50%, #e6a23c 100%) !important;
}

.progress-item:nth-child(3):hover .enhanced-progress .el-progress-bar__inner {
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    0 0 20px rgba(230, 162, 60, 0.5) !important;
}

.progress-item:nth-child(4) .enhanced-progress .el-progress-bar__inner {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 50%, #f56c6c 100%) !important;
}

.progress-item:nth-child(4):hover .enhanced-progress .el-progress-bar__inner {
  box-shadow: 
    0 4px 16px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.4),
    0 0 20px rgba(245, 108, 108, 0.5) !important;
}

/* 评分组件样式 */
.coach-rating .el-rate {
  display: flex;
  align-items: center;
}

.coach-rating .el-rate__icon {
  color: #ffd700 !important;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3)) !important;
}

.coach-rating .el-rate__decimal {
  color: #ffd700 !important;
}

.coach-rating .el-rate__text {
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 12px !important;
  font-weight: 500 !important;
  margin-left: 8px !important;
}

/* 快捷操作 */
.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.action-button {
  justify-content: flex-start !important;
  height: 40px !important;
  border-radius: 10px !important;
  transition: all 0.3s ease !important;
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  color: white !important;
  backdrop-filter: blur(8px) !important;
  font-size: 13px !important;
  padding: 0 12px !important;
  font-weight: 500 !important;
}

.action-button:hover {
  transform: translateY(-2px) !important;
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.25) !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
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
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.achievement-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, 0.25);
}

.achievement-icon {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.achievement-info {
  flex: 1;
}

.achievement-title {
  font-size: 14px;
  font-weight: 600;
  color: white;
  margin-bottom: 4px;
}

.achievement-date {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .student-dashboard {
    padding: var(--spacing-lg);
  }

  .welcome-banner-enhanced {
    padding: var(--spacing-2xl) var(--spacing-xl);
  }

  .banner-main {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-xl);
    margin-bottom: var(--spacing-xl);
  }

  .today-overview {
    justify-content: center;
    gap: var(--spacing-lg);
  }

  .overview-item {
    flex-direction: column;
    text-align: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-md) var(--spacing-lg);
  }

  .stats-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-md);
  }

  .mini-stat-card {
    padding: var(--spacing-lg) var(--spacing-xl);
  }

  .welcome-title {
    font-size: var(--font-size-3xl);
  }

  .main-content .el-col {
    margin-bottom: 20px;
  }

  .balance-actions,
  .quick-actions {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .timeline-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .coach-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .today-overview {
    flex-direction: column;
    gap: 12px;
  }

  .overview-item {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .mini-stat-card {
    padding: 16px;
  }

  .content-card,
  .sidebar-card {
    padding: 20px;
  }

  .stats-title {
    font-size: 16px;
    flex-direction: column;
    gap: 8px;
  }
}

/* emoji图标样式 */
.emoji-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  user-select: none;
}

.stat-icon {
  position: relative;
  overflow: hidden;
}

.stat-icon::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transform: rotate(45deg);
  animation: shimmer 4s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%) translateY(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) translateY(100%) rotate(45deg);
  }
}
</style>
