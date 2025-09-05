<template>
  <div class="campus-admin-dashboard">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <!-- 管理相关图标 -->
      <div class="admin-icon manage-icon">📊</div>
      <div class="admin-icon chart-icon">📈</div>
      <div class="admin-icon folder-icon">📁</div>
      <div class="admin-icon settings-icon">⚙️</div>

      <!-- 校区管理装饰 -->
      <div class="campus-decoration building-icon">🏢</div>
      <div class="campus-decoration users-icon">👥</div>
      <div class="campus-decoration calendar-icon">📅</div>

      <!-- 艺术字装饰 -->
      <div class="art-text">
        <div class="art-text-main">ADMIN</div>
        <div class="art-text-sub">校区管理</div>
      </div>

      <!-- 几何装饰 -->
      <div class="geometric-decoration geo-1"></div>
      <div class="geometric-decoration geo-2"></div>
      <div class="geometric-decoration geo-3"></div>
    </div>

    <!-- 待审核事项 -->
    <el-card class="dashboard-card pending-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Warning />
          </el-icon>
          <span>待审核事项</span>
          <el-badge :value="totalPending" :hidden="totalPending === 0" />
        </div>
      </template>
      <div class="pending-items">
        <div class="pending-item" @click="$router.push('/campus/coaches')">
          <div class="pending-icon">
            <el-icon>
              <UserFilled />
            </el-icon>
          </div>
          <div class="pending-info">
            <div class="pending-title">待审核教练</div>
            <div class="pending-count">{{ pendingCoaches }}人</div>
          </div>
          <el-icon class="arrow-icon">
            <ArrowRight />
          </el-icon>
        </div>
        <div class="pending-item" @click="$router.push('/campus/appointments')">
          <div class="pending-icon">
            <el-icon>
              <Calendar />
            </el-icon>
          </div>
          <div class="pending-info">
            <div class="pending-title">异常预约</div>
            <div class="pending-count">{{ abnormalAppointments }}个</div>
          </div>
          <el-icon class="arrow-icon">
            <ArrowRight />
          </el-icon>
        </div>
      </div>
    </el-card>

    <!-- 本月数据统计 -->
    <el-card class="dashboard-card stats-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <TrendCharts />
          </el-icon>
          <span>本月统计</span>
        </div>
      </template>
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ monthlyStats.newStudents }}</div>
          <div class="stat-label">新增学员</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ monthlyStats.totalCourses }}</div>
          <div class="stat-label">课程总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ monthlyStats.revenue }}</div>
          <div class="stat-label">收入金额</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ monthlyStats.tournaments }}</div>
          <div class="stat-label">比赛报名</div>
        </div>
      </div>
    </el-card>

    <!-- 最新活动 -->
    <el-card class="dashboard-card activity-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Bell />
          </el-icon>
          <span>最新活动</span>
        </div>
      </template>
      <div class="activity-list">
        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
          <div class="activity-icon" :class="activity.type">
            <component :is="activity.icon" />
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-time">{{ formatTime(activity.time) }}</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 系统通知 -->
    <el-card class="dashboard-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon>
            <Message />
          </el-icon>
          <span>系统通知</span>
        </div>
      </template>
      <div v-if="systemNotices.length > 0" class="notice-list">
        <div
          v-for="notice in systemNotices"
          :key="notice.id"
          class="notice-item"
          :class="{ unread: !notice.read }"
        >
          <div class="notice-content">
            <div class="notice-title">{{ notice.title }}</div>
            <div class="notice-summary">{{ notice.summary }}</div>
          </div>
          <div class="notice-time">{{ formatDate(notice.time) }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无系统通知" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  Warning,
  UserFilled,
  Calendar,
  ArrowRight,
  TrendCharts,
  Bell,
  Message,
  Plus,
  Edit,
  Delete,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)

// 待审核数据
const pendingCoaches = ref(3)
const abnormalAppointments = ref(2)

// 总待审核数
const totalPending = computed(() => pendingCoaches.value + abnormalAppointments.value)

// 本月统计数据
const monthlyStats = ref({
  newStudents: 28,
  totalCourses: 156,
  revenue: '¥12,800',
  tournaments: 23,
})

// 最新活动
const recentActivities = ref([])

// 系统通知
const systemNotices = ref([])

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).fromNow()
}

const formatDate = (time) => {
  return dayjs(time).format('MM-DD HH:mm')
}

// 获取数据
const fetchData = async () => {
  // 模拟数据
  recentActivities.value = [
    {
      id: 1,
      type: 'success',
      icon: Plus,
      title: '新学员"小王"注册成功',
      time: new Date(Date.now() - 30 * 60 * 1000),
    },
    {
      id: 2,
      type: 'info',
      icon: Edit,
      title: '教练"张老师"更新了个人信息',
      time: new Date(Date.now() - 2 * 60 * 60 * 1000),
    },
    {
      id: 3,
      type: 'warning',
      icon: Calendar,
      title: '学员"小李"取消了明天的课程',
      time: new Date(Date.now() - 4 * 60 * 60 * 1000),
    },
    {
      id: 4,
      type: 'danger',
      icon: Delete,
      title: '教练申请被拒绝',
      time: new Date(Date.now() - 6 * 60 * 60 * 1000),
    },
  ]

  systemNotices.value = [
    {
      id: 1,
      title: '系统维护通知',
      summary: '系统将于本周日凌晨2:00-4:00进行维护升级',
      time: new Date(Date.now() - 24 * 60 * 60 * 1000),
      read: false,
    },
    {
      id: 2,
      title: '新功能上线',
      summary: '学员评价功能已上线，支持课后评价和反馈',
      time: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
      read: true,
    },
    {
      id: 3,
      title: '比赛报名开始',
      summary: '本月乒乓球比赛报名已开始，截止时间为月底',
      time: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
      read: true,
    },
  ]
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.campus-admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(
    135deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #f5576c 75%,
    #4facfe 100%
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

/* 管理图标装饰 */
.admin-icon {
  position: absolute;
  font-size: 60px;
  opacity: 0.08;
  animation: float 8s ease-in-out infinite;
}

.manage-icon {
  top: 15%;
  left: 10%;
  animation-delay: 0s;
}

.chart-icon {
  top: 60%;
  right: 12%;
  animation-delay: 2s;
}

.folder-icon {
  bottom: 25%;
  left: 8%;
  animation-delay: 4s;
}

.settings-icon {
  top: 40%;
  left: 5%;
  animation-delay: 6s;
}

/* 校区装饰 */
.campus-decoration {
  position: absolute;
  font-size: 70px;
  opacity: 0.06;
  animation: rotate 20s linear infinite;
}

.building-icon {
  top: 25%;
  right: 8%;
  animation-delay: 1s;
}

.users-icon {
  bottom: 15%;
  right: 15%;
  animation-delay: 3s;
}

.calendar-icon {
  top: 70%;
  left: 15%;
  animation-delay: 5s;
}

/* 艺术字装饰 */
.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.12;
  z-index: 1;
  pointer-events: none;
  user-select: none;
}

.art-text-main {
  font-size: 120px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 10px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.3);
  font-family: 'Arial Black', sans-serif;
  -webkit-text-stroke: 1px rgba(255, 255, 255, 0.2);
}

.art-text-sub {
  font-size: 48px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 8px;
  margin-top: -20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

/* 几何装饰 */
.geometric-decoration {
  position: absolute;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.geo-1 {
  top: 30%;
  left: 5%;
  width: 100px;
  height: 100px;
  animation: rotate 25s linear infinite;
}

.geo-2 {
  bottom: 35%;
  right: 10%;
  width: 80px;
  height: 80px;
  animation: rotate 30s linear infinite reverse;
}

.geo-3 {
  top: 50%;
  right: 5%;
  width: 60px;
  height: 60px;
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

.pending-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 87, 108, 0.1));
}

.stats-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(102, 126, 234, 0.1));
}

.activity-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(79, 172, 254, 0.1));
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
  color: #667eea;
}

.pending-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.pending-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateX(4px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.pending-icon {
  width: 45px;
  height: 45px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.pending-info {
  flex: 1;
}

.pending-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.pending-count {
  color: #666;
  font-size: 14px;
}

.arrow-icon {
  color: #999;
  transition: all 0.3s ease;
}

.pending-item:hover .arrow-icon {
  color: #667eea;
  transform: translateX(4px);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.activity-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.activity-icon.success {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.activity-icon.warning {
  background: linear-gradient(135deg, #faad14, #ffc53d);
}

.activity-icon.info {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.activity-icon.danger {
  background: linear-gradient(135deg, #ff4d4f, #ff7875);
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.activity-time {
  color: #666;
  font-size: 12px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.notice-item.unread {
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), rgba(255, 255, 255, 0.6));
  border-left: 4px solid #1890ff;
}

.notice-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.notice-content {
  flex: 1;
}

.notice-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.notice-summary {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.notice-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-15px) rotate(3deg);
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
    opacity: 0.06;
    transform: scale(1);
  }

  50% {
    opacity: 0.12;
    transform: scale(1.1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .campus-admin-dashboard {
    padding: 16px;
  }

  .art-text-main {
    font-size: 80px;
  }

  .art-text-sub {
    font-size: 32px;
  }

  .admin-icon,
  .campus-decoration {
    font-size: 40px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
