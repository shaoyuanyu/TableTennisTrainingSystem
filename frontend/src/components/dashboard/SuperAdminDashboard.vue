<template>
  <div class="super-admin-dashboard">
    <!-- 极简背景 - 完全移除装饰元素以提升性能 -->

    <!-- 系统概览 -->
    <el-card class="dashboard-card overview-card" shadow="never">
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
    <el-card class="dashboard-card service-card" shadow="never">
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
    <el-card class="dashboard-card campus-card" shadow="never">
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
    <el-card class="dashboard-card log-card" shadow="never">
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

// 格式化时间（缓存结果以提升性能）
const timeCache = new Map()
const formatTime = (time) => {
  const key = time.toString()
  if (!timeCache.has(key)) {
    timeCache.set(key, dayjs(time).fromNow())
  }
  return timeCache.get(key)
}

// 获取日志图标（静态映射，避免重复计算）
const iconMap = {
  success: SuccessFilled,
  warning: WarningFilled,
  error: CircleCloseFilled,
  info: InfoFilled,
}

const getLogIcon = (level) => {
  return iconMap[level] || InfoFilled
}

// 获取数据（一次性加载，避免重复请求）
const fetchData = async () => {
  // 模拟数据 - 实际项目中这里应该是API调用
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
  gap: 20px;
  padding: 20px;
  /* 极简背景，提升性能 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  /* 性能优化：启用硬件加速 */
  transform: translateZ(0);
  will-change: auto;
}

.dashboard-card {
  border: none;
  border-radius: 12px;
  /* 移除复杂效果 */
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
  /* 极简过渡效果 */
  transition: transform 0.15s ease;
  transform: translateZ(0);
}

.dashboard-card:hover {
  transform: translateY(-2px);
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
  font-size: 18px;
  color: #667eea;
}

.card-header .el-link {
  margin-left: auto;
  font-weight: 500;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  /* 极简过渡 */
  transition: background-color 0.15s ease;
}

.overview-item:hover {
  background: rgba(255, 255, 255, 0.9);
}

.overview-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.overview-icon.campus {
  background: #1890ff;
}

.overview-icon.users {
  background: #52c41a;
}

.overview-icon.revenue {
  background: #faad14;
}

.overview-icon.courses {
  background: #f759ab;
}

.overview-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
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
  gap: 12px;
}

.service-item,
.campus-item,
.log-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  /* 极简过渡 */
  transition: background-color 0.15s ease;
}

.service-item:hover,
.campus-item:hover,
.log-item:hover {
  background: rgba(255, 255, 255, 0.85);
}

.log-item {
  gap: 12px;
}

.service-name,
.campus-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
  font-size: 15px;
}

.service-detail,
.campus-details {
  font-size: 13px;
  color: #666;
}

.detail-item {
  margin-right: 12px;
  font-weight: 500;
}

.log-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.log-icon.success {
  background: #52c41a;
}

.log-icon.info {
  background: #1890ff;
}

.log-icon.warning {
  background: #faad14;
}

.log-icon.error {
  background: #ff4d4f;
}

.log-content {
  flex: 1;
  min-width: 0;
}

.log-message {
  font-size: 14px;
  margin-bottom: 4px;
  font-weight: 500;
  color: #333;
}

.log-time {
  font-size: 12px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .super-admin-dashboard {
    padding: 16px;
  }

  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .overview-item {
    padding: 12px;
  }
}

@media (max-width: 480px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }
}

/* Element Plus 样式覆盖 - 性能优化版本 */
.dashboard-card :deep(.el-card__header) {
  padding: 16px 18px;
  background: transparent;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.dashboard-card :deep(.el-card__body) {
  padding: 18px;
}
</style>
