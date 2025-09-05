<template>
  <div class="super-admin-dashboard">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <!-- 系统级图标 -->
      <div class="system-icon database-icon">🗄️</div>
      <div class="system-icon server-icon">🖥️</div>
      <div class="system-icon network-icon">🌐</div>
      <div class="system-icon security-icon">🔐</div>

      <!-- 高级管理装饰 -->
      <div class="admin-decoration crown-icon">👑</div>
      <div class="admin-decoration shield-icon">🛡️</div>
      <div class="admin-decoration control-icon">🎛️</div>
      <div class="admin-decoration analytics-icon">📊</div>

      <!-- 艺术字装饰 -->
      <div class="art-text">
        <div class="art-text-main">SUPER</div>
        <div class="art-text-sub">系统管理</div>
      </div>

      <!-- 高级几何装饰 -->
      <div class="advanced-decoration adv-1"></div>
      <div class="advanced-decoration adv-2"></div>
      <div class="advanced-decoration adv-3"></div>
      <div class="advanced-decoration adv-4"></div>
    </div>

    <!-- 系统概览 -->
    <el-card class="dashboard-card overview-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Monitor />
          </el-icon>
          <span>系统概览</span>
        </div>
      </template>
      <div class="overview-grid">
        <div class="overview-item">
          <div class="overview-icon campus">
            <el-icon>
              <OfficeBuilding />
            </el-icon>
          </div>
          <div class="overview-info">
            <div class="overview-value">{{ systemOverview.totalCampuses }}</div>
            <div class="overview-label">校区总数</div>
          </div>
        </div>
        <div class="overview-item">
          <div class="overview-icon users">
            <el-icon>
              <UserFilled />
            </el-icon>
          </div>
          <div class="overview-info">
            <div class="overview-value">{{ systemOverview.activeUsers }}</div>
            <div class="overview-label">活跃用户</div>
          </div>
        </div>
        <div class="overview-item">
          <div class="overview-icon revenue">
            <el-icon>
              <Wallet />
            </el-icon>
          </div>
          <div class="overview-info">
            <div class="overview-value">{{ systemOverview.monthlyRevenue }}</div>
            <div class="overview-label">月收入</div>
          </div>
        </div>
        <div class="overview-item">
          <div class="overview-icon courses">
            <el-icon>
              <Calendar />
            </el-icon>
          </div>
          <div class="overview-info">
            <div class="overview-value">{{ systemOverview.totalCourses }}</div>
            <div class="overview-label">总课程数</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 服务状态 -->
    <el-card class="dashboard-card service-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Setting />
          </el-icon>
          <span>服务状态</span>
          <el-link type="primary" @click="$router.push('/admin/service')"> 查看详情 </el-link>
        </div>
      </template>
      <div class="service-status">
        <div class="service-item">
          <div class="service-info">
            <div class="service-name">系统服务</div>
            <div class="service-detail">当前版本: v1.0.0</div>
          </div>
          <el-tag type="success">运行正常</el-tag>
        </div>
        <div class="service-item">
          <div class="service-info">
            <div class="service-name">数据库</div>
            <div class="service-detail">连接状态良好</div>
          </div>
          <el-tag type="success">正常</el-tag>
        </div>
        <div class="service-item">
          <div class="service-info">
            <div class="service-name">服务到期时间</div>
            <div class="service-detail">{{ serviceExpiry }}</div>
          </div>
          <el-tag :type="getExpiryType()">{{ getExpiryStatus() }}</el-tag>
        </div>
      </div>
    </el-card>

    <!-- 校区管理快捷入口 -->
    <el-card class="dashboard-card campus-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <OfficeBuilding />
          </el-icon>
          <span>校区管理</span>
          <el-link type="primary" @click="$router.push('/admin/campus')"> 管理校区 </el-link>
        </div>
      </template>
      <div class="campus-list">
        <div v-for="campus in campusList" :key="campus.id" class="campus-item">
          <div class="campus-info">
            <div class="campus-name">{{ campus.name }}</div>
            <div class="campus-details">
              <span class="detail-item">学员: {{ campus.studentCount }}人</span>
              <span class="detail-item">教练: {{ campus.coachCount }}人</span>
            </div>
          </div>
          <div class="campus-status">
            <el-tag :type="campus.status === 'active' ? 'success' : 'warning'">
              {{ campus.status === 'active' ? '运营中' : '暂停' }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 系统日志 -->
    <el-card class="dashboard-card log-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Document />
          </el-icon>
          <span>系统日志</span>
          <el-link type="primary" @click="$router.push('/campus/logs')"> 查看更多 </el-link>
        </div>
      </template>
      <div class="log-list">
        <div v-for="log in recentLogs" :key="log.id" class="log-item">
          <div class="log-icon" :class="log.level">
            <component :is="getLogIcon(log.level)" />
          </div>
          <div class="log-content">
            <div class="log-message">{{ log.message }}</div>
            <div class="log-time">{{ formatTime(log.time) }}</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Monitor,
  OfficeBuilding,
  UserFilled,
  Wallet,
  Calendar,
  Setting,
  Document,
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  InfoFilled,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'

dayjs.extend(relativeTime)

// 系统概览数据
const systemOverview = ref({
  totalCampuses: 5,
  activeUsers: 892,
  monthlyRevenue: '¥85,600',
  totalCourses: 1248,
})

// 服务到期时间
const serviceExpiry = ref('2025-12-31')

// 校区列表
const campusList = ref([])

// 最近日志
const recentLogs = ref([])

// 计算服务到期状态
const getExpiryType = () => {
  const days = dayjs(serviceExpiry.value).diff(dayjs(), 'day')
  if (days > 30) return 'success'
  if (days > 7) return 'warning'
  return 'danger'
}

const getExpiryStatus = () => {
  const days = dayjs(serviceExpiry.value).diff(dayjs(), 'day')
  if (days > 30) return '正常'
  if (days > 7) return '即将到期'
  return '紧急'
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).fromNow()
}

// 获取日志图标
const getLogIcon = (level) => {
  const iconMap = {
    success: SuccessFilled,
    warning: WarningFilled,
    error: CircleCloseFilled,
    info: InfoFilled,
  }
  return iconMap[level] || InfoFilled
}

// 获取数据
const fetchData = async () => {
  // 模拟数据
  campusList.value = [
    {
      id: 1,
      name: '中心校区',
      studentCount: 156,
      coachCount: 8,
      status: 'active',
    },
    {
      id: 2,
      name: '东城校区',
      studentCount: 89,
      coachCount: 5,
      status: 'active',
    },
    {
      id: 3,
      name: '西城校区',
      studentCount: 124,
      coachCount: 6,
      status: 'active',
    },
    {
      id: 4,
      name: '南城校区',
      studentCount: 67,
      coachCount: 4,
      status: 'active',
    },
    {
      id: 5,
      name: '北城校区',
      studentCount: 0,
      coachCount: 0,
      status: 'inactive',
    },
  ]

  recentLogs.value = [
    {
      id: 1,
      level: 'success',
      message: '系统备份完成',
      time: new Date(Date.now() - 30 * 60 * 1000),
    },
    {
      id: 2,
      level: 'info',
      message: '新用户注册: 小王',
      time: new Date(Date.now() - 2 * 60 * 60 * 1000),
    },
    {
      id: 3,
      level: 'warning',
      message: '校区管理员权限变更',
      time: new Date(Date.now() - 4 * 60 * 60 * 1000),
    },
    {
      id: 4,
      level: 'error',
      message: '支付接口异常已修复',
      time: new Date(Date.now() - 6 * 60 * 60 * 1000),
    },
  ]
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.super-admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(
    135deg,
    #667eea 0%,
    #764ba2 25%,
    #667eea 50%,
    #764ba2 75%,
    #667eea 100%
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

/* 系统级图标装饰 */
.system-icon {
  position: absolute;
  font-size: 65px;
  opacity: 0.06;
  animation: float 10s ease-in-out infinite;
}

.database-icon {
  top: 20%;
  left: 8%;
  animation-delay: 0s;
}

.server-icon {
  top: 65%;
  right: 10%;
  animation-delay: 2.5s;
}

.network-icon {
  bottom: 30%;
  left: 12%;
  animation-delay: 5s;
}

.security-icon {
  top: 45%;
  left: 3%;
  animation-delay: 7.5s;
}

/* 高级管理装饰 */
.admin-decoration {
  position: absolute;
  font-size: 75px;
  opacity: 0.08;
  animation: rotate 25s linear infinite;
}

.crown-icon {
  top: 30%;
  right: 5%;
  animation-delay: 1s;
}

.shield-icon {
  bottom: 20%;
  right: 18%;
  animation-delay: 3s;
}

.control-icon {
  top: 75%;
  left: 18%;
  animation-delay: 5s;
}

.analytics-icon {
  top: 10%;
  right: 25%;
  animation-delay: 7s;
}

/* 艺术字装饰 */
.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.15;
  z-index: 1;
  pointer-events: none;
  user-select: none;
}

.art-text-main {
  font-size: 140px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.8);
  letter-spacing: 15px;
  text-shadow: 4px 4px 8px rgba(0, 0, 0, 0.4);
  font-family: 'Arial Black', sans-serif;
  -webkit-text-stroke: 2px rgba(255, 255, 255, 0.3);
  background: linear-gradient(45deg, #667eea, #764ba2, #667eea);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.art-text-sub {
  font-size: 50px;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 10px;
  margin-top: -25px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.3);
}

/* 高级几何装饰 */
.advanced-decoration {
  position: absolute;
  background: linear-gradient(45deg, rgba(255, 255, 255, 0.08), rgba(255, 255, 255, 0.04));
  border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  backdrop-filter: blur(2px);
}

.adv-1 {
  top: 25%;
  left: 2%;
  width: 120px;
  height: 120px;
  animation: morphFloat 20s ease-in-out infinite;
}

.adv-2 {
  bottom: 40%;
  right: 8%;
  width: 100px;
  height: 100px;
  animation: morphFloat 25s ease-in-out infinite reverse;
}

.adv-3 {
  top: 60%;
  right: 3%;
  width: 80px;
  height: 80px;
  animation: morphPulse 15s ease-in-out infinite;
}

.adv-4 {
  bottom: 15%;
  left: 5%;
  width: 90px;
  height: 90px;
  animation: morphFloat 30s ease-in-out infinite;
  animation-delay: 5s;
}

.dashboard-card {
  border: none;
  border-radius: 20px;
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow:
    0 10px 40px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.25) inset;
  position: relative;
  z-index: 10;
  overflow: hidden;
  transition: all 0.4s ease;
}

.dashboard-card:hover {
  transform: translateY(-6px);
  box-shadow:
    0 15px 50px rgba(0, 0, 0, 0.2),
    0 0 0 1px rgba(255, 255, 255, 0.35) inset;
}

.overview-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(102, 126, 234, 0.08));
}

.service-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(118, 75, 162, 0.08));
}

.campus-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(102, 126, 234, 0.08));
}

.log-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(118, 75, 162, 0.08));
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
  font-size: 22px;
  color: #667eea;
}

.card-header .el-link {
  margin-left: auto;
  font-weight: 500;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(15px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  transition: all 0.3s ease;
}

.overview-item:hover {
  transform: translateY(-3px);
  background: rgba(255, 255, 255, 0.85);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.12);
}

.overview-icon {
  width: 55px;
  height: 55px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
}

.overview-icon.campus {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.overview-icon.users {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.overview-icon.revenue {
  background: linear-gradient(135deg, #faad14, #ffc53d);
}

.overview-icon.courses {
  background: linear-gradient(135deg, #f759ab, #ff85c0);
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 6px;
}

.overview-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.service-status,
.campus-list,
.log-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-item,
.campus-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.service-item:hover,
.campus-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.service-name,
.campus-name {
  font-weight: 600;
  margin-bottom: 6px;
  color: #333;
  font-size: 15px;
}

.service-detail,
.campus-details {
  font-size: 13px;
  color: #666;
}

.detail-item {
  margin-right: 16px;
  font-weight: 500;
}

.log-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.log-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.log-icon {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.log-icon.success {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.log-icon.info {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.log-icon.warning {
  background: linear-gradient(135deg, #faad14, #ffc53d);
}

.log-icon.error {
  background: linear-gradient(135deg, #ff4d4f, #ff7875);
}

.log-content {
  flex: 1;
}

.log-message {
  font-size: 15px;
  margin-bottom: 6px;
  font-weight: 500;
  color: #333;
}

.log-time {
  font-size: 12px;
  color: #666;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  33% {
    transform: translateY(-12px) rotate(2deg);
  }

  66% {
    transform: translateY(-8px) rotate(-1deg);
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

@keyframes morphFloat {
  0%,
  100% {
    transform: translateY(0px);
    border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  }

  25% {
    transform: translateY(-15px);
    border-radius: 60% 40% 30% 70% / 60% 30% 70% 40%;
  }

  50% {
    transform: translateY(-8px);
    border-radius: 40% 60% 70% 30% / 40% 70% 30% 60%;
  }

  75% {
    transform: translateY(-12px);
    border-radius: 70% 30% 40% 60% / 30% 60% 40% 70%;
  }
}

@keyframes morphPulse {
  0%,
  100% {
    opacity: 0.08;
    transform: scale(1);
    border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  }

  50% {
    opacity: 0.15;
    transform: scale(1.2);
    border-radius: 70% 30% 30% 70% / 70% 70% 30% 30%;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .super-admin-dashboard {
    padding: 16px;
  }

  .art-text-main {
    font-size: 100px;
    letter-spacing: 8px;
  }

  .art-text-sub {
    font-size: 36px;
    letter-spacing: 6px;
  }

  .system-icon,
  .admin-decoration {
    font-size: 45px;
  }

  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .overview-item {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
