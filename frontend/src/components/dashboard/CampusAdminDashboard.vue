<template>
  <div class="campus-admin-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-left">
        <div class="welcome-content">
          <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}！</h1>
          <p class="welcome-subtitle">{{ getCampusName() }}校区运营概览</p>
          <div class="banner-stats">
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.revenue }}</span>
              <span class="stat-label">今日营收</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.courses }}</span>
              <span class="stat-label">今日课程</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ todayStats.utilization }}%</span>
              <span class="stat-label">场地利用率</span>
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
            <el-tag type="warning" size="small">校区管理员</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 核心数据统计 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in campusStats" :key="stat.key">
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
        <!-- 营收统计图表 -->
        <el-card class="content-card revenue-chart-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <TrendCharts />
                </el-icon>
                <span>营收趋势</span>
              </div>
              <div class="header-actions">
                <el-radio-group v-model="revenueTimeRange" size="small">
                  <el-radio-button label="week">7天</el-radio-button>
                  <el-radio-button label="month">30天</el-radio-button>
                  <el-radio-button label="quarter">90天</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="revenue-overview">
            <div class="revenue-summary">
              <div class="summary-item">
                <span class="summary-label">本月总营收</span>
                <span class="summary-value">¥{{ monthlyRevenue.toLocaleString() }}</span>
                <span class="summary-trend up">+15.2%</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">平均客单价</span>
                <span class="summary-value">¥{{ averagePrice }}</span>
                <span class="summary-trend up">+8.5%</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">会员续费率</span>
                <span class="summary-value">{{ renewalRate }}%</span>
                <span class="summary-trend stable">持平</span>
              </div>
            </div>
            <div class="revenue-chart">
              <div class="chart-placeholder">📊 营收趋势图表区域</div>
            </div>
          </div>
        </el-card>

        <!-- 教练管理 -->
        <el-card class="content-card coaches-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <UserFilled />
                </el-icon>
                <span>教练团队</span>
              </div>
              <PrimaryButton size="sm" @click="manageCoaches">管理教练</PrimaryButton>
            </div>
          </template>
          <div class="coaches-grid">
            <div v-for="coach in coaches" :key="coach.id" class="coach-card">
              <div class="coach-header">
                <el-avatar :size="50" :src="coach.avatar">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
                <div class="coach-info">
                  <div class="coach-name">{{ coach.name }}</div>
                  <div class="coach-level">{{ coach.level }}级教练</div>
                  <div class="coach-rating">
                    <el-rate :model-value="coach.rating" disabled size="small" />
                    <span>({{ coach.rating }})</span>
                  </div>
                </div>
                <div class="coach-status">
                  <el-tag :type="getCoachStatusType(coach.status)" size="small">
                    {{ coach.status }}
                  </el-tag>
                </div>
              </div>
              <div class="coach-stats">
                <div class="stat">
                  <span class="stat-label">学员</span>
                  <span class="stat-value">{{ coach.students }}</span>
                </div>
                <div class="stat">
                  <span class="stat-label">课程</span>
                  <span class="stat-value">{{ coach.courses }}</span>
                </div>
                <div class="stat">
                  <span class="stat-label">收入</span>
                  <span class="stat-value">¥{{ coach.income }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 学员管理 -->
        <el-card class="content-card students-overview-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon">
                  <Avatar />
                </el-icon>
                <span>学员概览</span>
              </div>
              <el-link type="primary" @click="manageStudents">详细管理</el-link>
            </div>
          </template>
          <div class="students-overview">
            <div class="overview-chart">
              <div class="chart-item">
                <div class="chart-circle active">
                  <span class="circle-value">{{ studentStats.active }}</span>
                  <span class="circle-label">活跃学员</span>
                </div>
              </div>
              <div class="chart-item">
                <div class="chart-circle new">
                  <span class="circle-value">{{ studentStats.new }}</span>
                  <span class="circle-label">新增学员</span>
                </div>
              </div>
              <div class="chart-item">
                <div class="chart-circle retention">
                  <span class="circle-value">{{ studentStats.retention }}%</span>
                  <span class="circle-label">留存率</span>
                </div>
              </div>
            </div>
            <div class="recent-students">
              <div class="recent-title">最新注册学员</div>
              <div v-for="student in recentStudents" :key="student.id" class="recent-student">
                <el-avatar :size="36" :src="student.avatar">
                  <el-icon>
                    <User />
                  </el-icon>
                </el-avatar>
                <div class="student-info">
                  <div class="student-name">{{ student.name }}</div>
                  <div class="student-time">{{ formatTime(student.registeredAt) }}</div>
                </div>
                <el-tag size="small" type="success">新用户</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :xs="24" :lg="8">
        <!-- 场地利用率 -->
        <el-card class="sidebar-card venue-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <OfficeBuilding />
              </el-icon>
              <span>场地使用情况</span>
            </div>
          </template>
          <div class="venue-overview">
            <div class="venue-stats">
              <div class="venue-stat">
                <span class="stat-label">总场地数</span>
                <span class="stat-value">{{ venueStats.total }}</span>
              </div>
              <div class="venue-stat">
                <span class="stat-label">使用中</span>
                <span class="stat-value busy">{{ venueStats.busy }}</span>
              </div>
              <div class="venue-stat">
                <span class="stat-label">空闲</span>
                <span class="stat-value free">{{ venueStats.free }}</span>
              </div>
            </div>
            <div class="venue-utilization">
              <div class="utilization-title">今日利用率</div>
              <el-progress :percentage="venueStats.utilization" :stroke-width="12" />
              <div class="utilization-text">{{ venueStats.utilization }}% 运营中</div>
            </div>
          </div>
          <div class="venue-list">
            <div v-for="venue in venues" :key="venue.id" class="venue-item">
              <div class="venue-info">
                <div class="venue-name">{{ venue.name }}</div>
                <div class="venue-status" :class="venue.status">
                  {{ getVenueStatusText(venue.status) }}
                </div>
              </div>
              <div class="venue-time">{{ venue.currentTime || '--' }}</div>
            </div>
          </div>
        </el-card>

        <!-- 系统通知 -->
        <el-card class="sidebar-card notifications-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Bell />
              </el-icon>
              <span>系统通知</span>
              <el-badge :value="notifications.length" class="badge" />
            </div>
          </template>
          <div class="notifications-list">
            <div v-for="notification in notifications" :key="notification.id" class="notification-item">
              <div class="notification-icon" :class="notification.type">
                <el-icon>
                  <component :is="getNotificationIcon(notification.type)" />
                </el-icon>
              </div>
              <div class="notification-content">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-time">{{ formatTime(notification.time) }}</div>
              </div>
              <div class="notification-action">
                <PrimaryButton size="sm" @click="handleNotification(notification)">处理</PrimaryButton>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 财务概览 -->
        <el-card class="sidebar-card finance-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon">
                <Money />
              </el-icon>
              <span>财务概览</span>
            </div>
          </template>
          <div class="finance-overview">
            <div class="finance-item">
              <div class="finance-label">本月收入</div>
              <div class="finance-value income">¥{{ monthlyRevenue.toLocaleString() }}</div>
            </div>
            <div class="finance-item">
              <div class="finance-label">本月支出</div>
              <div class="finance-value expense">¥{{ monthlyExpense.toLocaleString() }}</div>
            </div>
            <div class="finance-item">
              <div class="finance-label">净利润</div>
              <div class="finance-value profit">
                ¥{{ (monthlyRevenue - monthlyExpense).toLocaleString() }}
              </div>
            </div>
          </div>
          <div class="finance-chart">
            <div class="chart-placeholder">💰 收支对比图</div>
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
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import dayjs from 'dayjs'
import {OutlineButton, PrimaryButton} from '@/components/buttons'
import {
  Avatar,
  Bell,
  Calendar,
  Money,
  OfficeBuilding,
  Operation,
  TrendCharts,
  User,
  UserFilled,
  Warning,
} from '@element-plus/icons-vue'
// 导入API函数
import { getCampusStudents, getCampusCoaches, getCampusRechargeRecords } from '@/api/admin'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const revenueTimeRange = ref('month')
const monthlyRevenue = ref(245000)
const monthlyExpense = ref(180000)
const averagePrice = ref(320)
const renewalRate = ref(87)

const todayStats = ref({
  revenue: '¥12,580',
  courses: '32',
  utilization: 78,
})

// 校区统计数据
const campusStats = ref([
  {
    key: 'revenue',
    label: '月度营收',
    value: '¥245K',
    icon: Money,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    trend: 'up',
    trendText: '+15.2%',
  },
  {
    key: 'students',
    label: '在读学员',
    value: '168',
    icon: Avatar,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    trend: 'up',
    trendText: '+12',
  },
  {
    key: 'coaches',
    label: '教练团队',
    value: '8',
    icon: UserFilled,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    trend: 'stable',
    trendText: '稳定',
  },
  {
    key: 'utilization',
    label: '场地利用率',
    value: '78%',
    icon: OfficeBuilding,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    trend: 'up',
    trendText: '+5%',
  },
])

// 教练团队
const coaches = ref([
  {
    id: 1,
    name: '李教练',
    level: '高',
    rating: 4.9,
    status: '在线',
    students: 25,
    courses: 8,
    income: 15000,
    avatar: '',
  },
  {
    id: 2,
    name: '王教练',
    level: '中',
    rating: 4.7,
    status: '授课中',
    students: 18,
    courses: 6,
    income: 12000,
    avatar: '',
  },
  {
    id: 3,
    name: '张教练',
    level: '高',
    rating: 4.8,
    status: '休假',
    students: 22,
    courses: 7,
    income: 14000,
    avatar: '',
  },
  {
    id: 4,
    name: '赵教练',
    level: '中',
    rating: 4.6,
    status: '在线',
    students: 15,
    courses: 5,
    income: 10000,
    avatar: '',
  },
])

// 学员统计
const studentStats = ref({
  active: 168,
  new: 12,
  retention: 87,
})

// 最新注册学员
const recentStudents = ref([
  {
    id: 1,
    name: '小明',
    registeredAt: dayjs().subtract(2, 'hour').toDate(),
    avatar: '',
  },
  {
    id: 2,
    name: '小红',
    registeredAt: dayjs().subtract(5, 'hour').toDate(),
    avatar: '',
  },
  {
    id: 3,
    name: '小华',
    registeredAt: dayjs().subtract(1, 'day').toDate(),
    avatar: '',
  },
])

// 场地统计
const venueStats = ref({
  total: 6,
  busy: 4,
  free: 2,
  utilization: 78,
})

// 场地列表
const venues = ref([
  { id: 1, name: '训练室A', status: 'busy', currentTime: '09:00-11:00' },
  { id: 2, name: '训练室B', status: 'busy', currentTime: '10:00-12:00' },
  { id: 3, name: '训练室C', status: 'free', currentTime: null },
  { id: 4, name: '比赛厅', status: 'busy', currentTime: '14:00-16:00' },
  { id: 5, name: '多功能厅', status: 'busy', currentTime: '15:00-17:00' },
  { id: 6, name: '体能训练室', status: 'free', currentTime: null },
])

// 系统通知
const notifications = ref([
  {
    id: 1,
    type: 'warning',
    title: '设备维护提醒',
    time: dayjs().subtract(30, 'minute').toDate(),
  },
  {
    id: 2,
    type: 'info',
    title: '新教练入职审核',
    time: dayjs().subtract(2, 'hour').toDate(),
  },
  {
    id: 3,
    type: 'error',
    title: '学员投诉处理',
    time: dayjs().subtract(4, 'hour').toDate(),
  },
])

// 快捷操作
const quickActions = ref([
  {
    key: 'coaches',
    label: '教练管理',
    color: 'primary',
    icon: UserFilled,
    handler: () => router.push('/campus/coaches'),
  },
  {
    key: 'students',
    label: '学员管理',
    color: 'success',
    icon: Avatar,
    handler: () => router.push('/campus/students'),
  },
  {
    key: 'finance',
    label: '财务报表',
    color: 'warning',
    icon: Money,
    handler: () => router.push('/campus/finance'),
  },
  {
    key: 'schedule',
    label: '课程安排',
    color: 'info',
    icon: Calendar,
    handler: () => router.push('/campus/schedule'),
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

const getCampusName = () => {
  return userStore.userInfo.campus || '东校区'
}

const getCoachStatusType = (status) => {
  const statusMap = {
    在线: 'success',
    授课中: 'warning',
    休假: 'info',
    离线: 'danger',
  }
  return statusMap[status] || 'info'
}

const getVenueStatusText = (status) => {
  const textMap = {
    busy: '使用中',
    free: '空闲',
    maintenance: '维护中',
  }
  return textMap[status] || status
}

const getNotificationIcon = (type) => {
  const iconMap = {
    warning: Warning,
    info: Bell,
    error: Warning,
  }
  return iconMap[type] || Bell
}

const formatTime = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

// 导航方法
const manageCoaches = () => {
  router.push('/campus/coaches')
}

const manageStudents = () => {
  router.push('/campus/students')
}

const handleNotification = (notification) => {
  console.log('处理通知:', notification)
}

// 获取营收数据
const fetchRevenueData = async () => {
  try {
    // 获取充值记录用于计算营收
    const rechargeData = await getCampusRechargeRecords({ page: 1, size: 100 })
    
    // 计算本月营收
    const now = new Date()
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    
    const monthlyRevenueValue = rechargeData
      .filter(record => new Date(record.createdAt) >= startOfMonth)
      .reduce((sum, record) => sum + record.amount, 0)
    
    monthlyRevenue.value = monthlyRevenueValue
    
    // 更新校区统计数据中的营收值
    const revenueStat = campusStats.value.find(stat => stat.key === 'revenue')
    if (revenueStat) {
      revenueStat.value = `¥${(monthlyRevenueValue / 1000).toFixed(1)}K`
    }
    
    console.log('营收数据加载完成')
  } catch (error) {
    console.error('获取营收数据失败:', error)
  }
}

// 获取学员数据
const fetchStudentData = async () => {
  try {
    // 获取校区学员列表
    const students = await getCampusStudents({ page: 1, size: 100 })
    
    // 更新学员统计数据
    studentStats.value.active = students.length
    
    // 计算本月新增学员数
    const now = new Date()
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
    studentStats.value.new = students.filter(
      student => new Date(student.createdAt) >= startOfMonth
    ).length
    
    // 更新校区统计数据中的学员数
    const studentStat = campusStats.value.find(stat => stat.key === 'students')
    if (studentStat) {
      studentStat.value = students.length.toString()
    }
    
    console.log('学员数据加载完成')
  } catch (error) {
    console.error('获取学员数据失败:', error)
  }
}

// 获取教练数据
const fetchCoachData = async () => {
  try {
    // 获取校区教练列表
    const coachData = await getCampusCoaches({ page: 1, size: 100 })
    
    // 更新教练团队数据
    coaches.value = coachData.map(coach => ({
      id: coach.coachId,
      name: coach.realName,
      level: coach.level || '未评级',
      rating: 5, // 默认评分
      status: '在线', // 默认状态
      students: coach.currentStudents || 0,
      courses: 0, // 需要从其他接口获取
      income: coach.balance || 0,
      avatar: coach.photoUrl || ''
    }))
    
    // 更新校区统计数据中的教练数
    const coachStat = campusStats.value.find(stat => stat.key === 'coaches')
    if (coachStat) {
      coachStat.value = coachData.length.toString()
    }
    
    console.log('教练数据加载完成')
  } catch (error) {
    console.error('获取教练数据失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  fetchRevenueData()
  fetchStudentData()
  fetchCoachData()
})
</script>

<style scoped>
.campus-admin-dashboard {
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

/* 营收概览 */
.revenue-overview {
  margin-bottom: 20px;
}

.revenue-summary {
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

/* 教练网格 */
.coaches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.coach-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
}

.coach-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.coach-info {
  flex: 1;
}

.coach-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.coach-level {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.coach-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.coach-stats {
  display: flex;
  justify-content: space-around;
  border-top: 1px solid #e9ecef;
  padding-top: 12px;
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

/* 学员概览 */
.students-overview {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.overview-chart {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chart-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
}

.chart-circle.active {
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
}

.chart-circle.new {
  background: linear-gradient(135deg, #2196f3 0%, #1565c0 100%);
}

.chart-circle.retention {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
}

.circle-value {
  font-size: 24px;
  margin-bottom: 4px;
}

.circle-label {
  font-size: 12px;
  opacity: 0.9;
}

.recent-students {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recent-title {
  font-weight: 600;
  margin-bottom: 8px;
}

.recent-student {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 8px;
}

.student-info {
  flex: 1;
}

.student-name {
  font-weight: 600;
  margin-bottom: 2px;
}

.student-time {
  font-size: 12px;
  color: #666;
}

/* 场地使用情况 */
.venue-overview {
  margin-bottom: 20px;
}

.venue-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.venue-stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.stat-value.busy {
  color: #f56c6c;
}

.stat-value.free {
  color: #67c23a;
}

.venue-utilization {
  text-align: center;
  margin-bottom: 20px;
}

.utilization-title {
  font-weight: 600;
  margin-bottom: 12px;
}

.utilization-text {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}

.venue-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.venue-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.venue-name {
  font-weight: 600;
}

.venue-status {
  font-size: 12px;
  margin-top: 2px;
}

.venue-status.busy {
  color: #f56c6c;
}

.venue-status.free {
  color: #67c23a;
}

.venue-time {
  font-size: 12px;
  color: #666;
}

/* 通知列表 */
.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.notification-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.notification-icon.warning {
  background: #ff9800;
}

.notification-icon.info {
  background: #2196f3;
}

.notification-icon.error {
  background: #f44336;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: 600;
  margin-bottom: 2px;
}

.notification-time {
  font-size: 12px;
  color: #666;
}

/* 财务概览 */
.finance-overview {
  margin-bottom: 20px;
}

.finance-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.finance-item:last-child {
  border-bottom: none;
}

.finance-label {
  font-weight: 600;
  color: #333;
}

.finance-value {
  font-size: 18px;
  font-weight: 700;
}

.finance-value.income {
  color: #67c23a;
}

.finance-value.expense {
  color: #f56c6c;
}

.finance-value.profit {
  color: #409eff;
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

  .revenue-summary {
    grid-template-columns: 1fr;
  }

  .coaches-grid {
    grid-template-columns: 1fr;
  }

  .students-overview {
    grid-template-columns: 1fr;
  }

  .overview-chart {
    flex-direction: row;
    justify-content: space-around;
  }

  .chart-circle {
    width: 80px;
    height: 80px;
  }

  .circle-value {
    font-size: 18px;
  }

  .circle-label {
    font-size: 10px;
  }
}
</style>
