<template>
  <div class="dashboard">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <div class="floating-element ping-pong-1">🏓</div>
      <div class="floating-element ping-pong-2">🏓</div>
      <div class="floating-element ping-pong-3">🏓</div>
      <div class="floating-element trophy-1">🏆</div>
      <div class="floating-element star-1">⭐</div>
      <div class="floating-element star-2">⭐</div>
      <div class="geometric-shape shape-1"></div>
      <div class="geometric-shape shape-2"></div>
      <div class="geometric-shape shape-3"></div>
      <div class="artistic-text main-title">PING PONG</div>
      <div class="artistic-text sub-title">培训管理系统</div>
    </div>

    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-left">
        <div class="welcome-content">
          <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userName }}！</h1>
          <p class="welcome-subtitle">{{ getWelcomeSubtitle() }}</p>
          <div class="banner-stats">
            <div class="stat-item">
              <span class="stat-value">{{ getTodayStats().today }}</span>
              <span class="stat-label">今日活动</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ getTodayStats().week }}</span>
              <span class="stat-label">本周目标</span>
            </div>
          </div>
        </div>
      </div>
      <div class="banner-right">
        <div class="avatar-container">
          <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
            <el-icon size="40"><User /></el-icon>
          </el-avatar>
          <div class="avatar-badge">
            <el-tag :type="getUserStatusType()" size="small">{{ getUserStatus() }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.gradient }">
              <component :is="stat.icon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-trend" :class="stat.trend">
                <el-icon v-if="stat.trend === 'up'"><TrendCharts /></el-icon>
                <el-icon v-if="stat.trend === 'down'"><TrendCharts /></el-icon>
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
                <el-icon class="header-icon"><Calendar /></el-icon>
                <span>今日安排</span>
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
                </div>
              </div>
              <div class="timeline-status">
                <el-button 
                  v-if="item.status === 'upcoming'" 
                  size="small" 
                  type="primary"
                  @click="handleScheduleAction(item)"
                >
                  {{ getActionText(item.type) }}
                </el-button>
                <el-tag v-else :type="getStatusType(item.status)" size="small">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-schedule">
            <el-empty description="今天没有安排" :image-size="100">
              <template #image>
                <div class="empty-icon">🎯</div>
              </template>
              <el-button type="primary" @click="createSchedule">安排新活动</el-button>
            </el-empty>
          </div>
        </el-card>

        <!-- 最新动态 -->
        <el-card class="content-card activity-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="header-icon"><Bell /></el-icon>
                <span>最新动态</span>
              </div>
              <el-link type="primary" @click="goToMessages">查看全部</el-link>
            </div>
          </template>
          
          <div class="activity-feed">
            <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
              <div class="activity-avatar">
                <el-avatar :size="32" :src="activity.avatar">
                  <el-icon><component :is="activity.icon" /></el-icon>
                </el-avatar>
              </div>
              <div class="activity-content">
                <div class="activity-text">{{ activity.text }}</div>
                <div class="activity-time">{{ formatTime(activity.createdAt) }}</div>
              </div>
              <div class="activity-action" v-if="activity.actionable">
                <el-button size="small" type="primary" text @click="handleActivity(activity)">
                  查看
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧边栏 -->
      <el-col :xs="24" :lg="8">
        <!-- 天气信息 -->
        <el-card class="sidebar-card weather-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Sunny /></el-icon>
              <span>今日天气</span>
            </div>
          </template>
          <div class="weather-content">
            <div class="weather-main">
              <div class="weather-temp">{{ weather.temperature }}°C</div>
              <div class="weather-desc">{{ weather.description }}</div>
            </div>
            <div class="weather-icon">{{ weather.icon }}</div>
          </div>
          <div class="weather-details">
            <div class="weather-detail">
              <span>湿度</span>
              <span>{{ weather.humidity }}%</span>
            </div>
            <div class="weather-detail">
              <span>风速</span>
              <span>{{ weather.windSpeed }} km/h</span>
            </div>
          </div>
          <div class="weather-suggestion">
            <el-alert :title="weather.suggestion" type="info" :closable="false" />
          </div>
        </el-card>

        <!-- 快捷操作 -->
        <el-card class="sidebar-card actions-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Operation /></el-icon>
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

        <!-- 本周统计 -->
        <el-card class="sidebar-card weekly-stats-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><TrendCharts /></el-icon>
              <span>本周统计</span>
            </div>
          </template>
          <div class="weekly-stats">
            <div v-for="stat in weeklyStats" :key="stat.key" class="weekly-stat-item">
              <div class="weekly-stat-info">
                <div class="weekly-stat-label">{{ stat.label }}</div>
                <div class="weekly-stat-value">{{ stat.value }}</div>
              </div>
              <div class="weekly-stat-progress">
                <el-progress 
                  :percentage="stat.percentage" 
                  :color="stat.color"
                  :stroke-width="6"
                  :show-text="false"
                />
              </div>
            </div>
          </div>
        </el-card>

        <!-- 成就徽章 -->
        <el-card class="sidebar-card achievements-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon class="header-icon"><Trophy /></el-icon>
              <span>最新成就</span>
            </div>
          </template>
          <div class="achievements">
            <div v-for="achievement in recentAchievements" :key="achievement.id" class="achievement-item">
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  User,
  Bell,
  Calendar,
  UserFilled,
  Search,
  Wallet,
  Trophy,
  Document,
  Management,
  OfficeBuilding,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

// 导入角色特定组件
import StudentDashboard from '@/components/dashboard/StudentDashboard.vue'
import CoachDashboard from '@/components/dashboard/CoachDashboard.vue'
import CampusAdminDashboard from '@/components/dashboard/CampusAdminDashboard.vue'
import SuperAdminDashboard from '@/components/dashboard/SuperAdminDashboard.vue'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userName = computed(() => userStore.userName)
const userInfo = computed(() => userStore.userInfo)
const isStudent = computed(() => userStore.isStudent)
const isCoach = computed(() => userStore.isCoach)
const isCampusAdmin = computed(() => userStore.isCampusAdmin)
const isSuperAdmin = computed(() => userStore.isSuperAdmin)

// 最新消息
const recentMessages = ref([])

// 统计数据
const stats = computed(() => {
  if (isStudent.value) {
    return [
      { key: 'courses', label: '本月课程', value: '12', color: '#409eff', icon: Calendar },
      { key: 'coaches', label: '我的教练', value: '2', color: '#67c23a', icon: UserFilled },
      { key: 'balance', label: '账户余额', value: '¥380', color: '#e6a23c', icon: Wallet },
      { key: 'matches', label: '参赛次数', value: '3', color: '#f56c6c', icon: Trophy },
    ]
  } else if (isCoach.value) {
    return [
      { key: 'students', label: '指导学员', value: '18', color: '#409eff', icon: UserFilled },
      { key: 'pending', label: '待审核预约', value: '5', color: '#e6a23c', icon: Calendar },
      { key: 'courses', label: '本月课程', value: '24', color: '#67c23a', icon: Calendar },
      { key: 'rating', label: '评价得分', value: '4.8', color: '#f56c6c', icon: Trophy },
    ]
  } else if (isCampusAdmin.value) {
    return [
      { key: 'students', label: '校区学员', value: '156', color: '#409eff', icon: UserFilled },
      { key: 'coaches', label: '校区教练', value: '8', color: '#67c23a', icon: UserFilled },
      { key: 'pending', label: '待审核', value: '3', color: '#e6a23c', icon: Document },
      { key: 'courses', label: '本月课程', value: '268', color: '#f56c6c', icon: Calendar },
    ]
  } else if (isSuperAdmin.value) {
    return [
      { key: 'campuses', label: '校区总数', value: '5', color: '#409eff', icon: OfficeBuilding },
      { key: 'users', label: '活跃用户', value: '892', color: '#67c23a', icon: UserFilled },
      { key: 'revenue', label: '本月收入', value: '¥38,500', color: '#e6a23c', icon: Wallet },
      { key: 'service', label: '服务状态', value: '正常', color: '#67c23a', icon: Management },
    ]
  }
  return []
})

// 快捷操作
const quickActions = computed(() => {
  if (isStudent.value) {
    return [
      {
        key: 'appointment',
        label: '预约课程',
        type: 'primary',
        icon: Calendar,
        handler: () => router.push('/student/appointment'),
      },
      {
        key: 'coaches',
        label: '查找教练',
        type: 'success',
        icon: Search,
        handler: () => router.push('/student/coaches'),
      },
      {
        key: 'recharge',
        label: '账户充值',
        type: 'warning',
        icon: Wallet,
        handler: () => router.push('/student/recharge'),
      },
      {
        key: 'tournament',
        label: '比赛报名',
        type: 'danger',
        icon: Trophy,
        handler: () => router.push('/student/tournament'),
      },
    ]
  } else if (isCoach.value) {
    return [
      {
        key: 'schedule',
        label: '查看课表',
        type: 'primary',
        icon: Calendar,
        handler: () => router.push('/coach/schedule'),
      },
      {
        key: 'appointments',
        label: '审核预约',
        type: 'warning',
        icon: Document,
        handler: () => router.push('/coach/appointments'),
      },
      {
        key: 'evaluation',
        label: '学员评价',
        type: 'success',
        icon: UserFilled,
        handler: () => router.push('/coach/evaluation'),
      },
    ]
  } else if (isCampusAdmin.value) {
    return [
      {
        key: 'students',
        label: '学员管理',
        type: 'primary',
        icon: UserFilled,
        handler: () => router.push('/campus/students'),
      },
      {
        key: 'coaches',
        label: '教练管理',
        type: 'success',
        icon: UserFilled,
        handler: () => router.push('/campus/coaches'),
      },
      {
        key: 'appointments',
        label: '预约管理',
        type: 'warning',
        icon: Calendar,
        handler: () => router.push('/campus/appointments'),
      },
      {
        key: 'logs',
        label: '日志查询',
        type: 'info',
        icon: Document,
        handler: () => router.push('/campus/logs'),
      },
    ]
  } else if (isSuperAdmin.value) {
    return [
      {
        key: 'campus',
        label: '校区管理',
        type: 'primary',
        icon: OfficeBuilding,
        handler: () => router.push('/admin/campus'),
      },
      {
        key: 'service',
        label: '服务状态',
        type: 'success',
        icon: Management,
        handler: () => router.push('/admin/service'),
      },
    ]
  }
  return []
})

// 获取欢迎信息
const getWelcomeMessage = () => {
  const hour = new Date().getHours()
  if (hour < 12) {
    return '早上好'
  } else if (hour < 18) {
    return '下午好'
  } else {
    return '晚上好'
  }
}

const getWelcomeSubtitle = () => {
  if (isStudent.value) {
    return '今天也要努力训练哦！'
  } else if (isCoach.value) {
    return '今天有新的教学任务等着您'
  } else if (isCampusAdmin.value) {
    return '管理好校区，服务好每一位学员'
  } else if (isSuperAdmin.value) {
    return '掌控全局，确保系统稳定运行'
  }
  return '欢迎使用乒乓球培训管理系统'
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).fromNow()
}

// 前往消息页面
const goToMessages = () => {
  router.push('/messages')
}

// 前往消息详情
const goToMessageDetail = (id) => {
  router.push(`/messages/${id}`)
}

// 获取最新消息
const fetchRecentMessages = async () => {
  // 模拟数据
  recentMessages.value = [
    {
      id: 1,
      title: '您有新的课程预约待确认',
      createdAt: new Date(Date.now() - 30 * 60 * 1000),
      read: false,
      avatar: '',
    },
    {
      id: 2,
      title: '教练张老师给您留了评价',
      createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000),
      read: true,
      avatar: '',
    },
    {
      id: 3,
      title: '本月比赛报名即将截止',
      createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000),
      read: false,
      avatar: '',
    },
  ]
}

onMounted(() => {
  fetchRecentMessages()
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  color: white;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.welcome-avatar {
  margin-left: 20px;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  margin-bottom: 16px;
  border: none;
  border-radius: 12px;
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
  color: white;
  font-size: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.main-content {
  margin-bottom: 24px;
}

.sidebar-card {
  margin-bottom: 24px;
  border: none;
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 500;
}

.message-list {
  max-height: 300px;
  overflow-y: auto;
}

.message-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.message-item:hover {
  background-color: #f9f9f9;
  margin: 0 -16px;
  padding-left: 16px;
  padding-right: 16px;
  border-radius: 6px;
}

.message-item:last-child {
  border-bottom: none;
}

.message-content {
  flex: 1;
}

.message-title {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 4px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-status {
  width: 20px;
  display: flex;
  justify-content: center;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-button {
  justify-content: flex-start;
  text-align: left;
}

@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .welcome-avatar {
    margin-left: 0;
  }

  .welcome-title {
    font-size: 24px;
  }
}
</style>
