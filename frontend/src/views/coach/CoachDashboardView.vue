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
                <ChatLineSquare />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>学员反馈</h3>
              <p>查看学员评价和反馈</p>
              <PrimaryButton size="sm" to="/coach/student-feedback">前往</PrimaryButton>
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
import {ElMessage} from 'element-plus'
import {
  Bell,
  Calendar,
  ChatLineSquare,
  Clock,
  Money,
  Star,
  TrendCharts,
  User,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'

const router = useRouter()
const userStore = useUserStore()

// 数据状态
const stats = reactive({
  totalStudents: 28,
  totalClasses: 156,
  rating: 4.8,
  monthIncome: 8520,
})

const incomeData = reactive({
  today: 480,
  week: 2100,
  month: 8520,
})

const pendingAppointments = ref(3)
const todayClasses = ref([
  {
    id: 1,
    time: '09:00',
    student: { name: '张小明', avatar: '' },
    type: '基础技术课',
    duration: 60,
    location: '训练场A',
    fee: 120,
    status: 'upcoming',
  },
  {
    id: 2,
    time: '10:30',
    student: { name: '李小红', avatar: '' },
    type: '进阶技术课',
    duration: 90,
    location: '训练场B',
    fee: 180,
    status: 'upcoming',
  },
  {
    id: 3,
    time: '14:00',
    student: { name: '王小强', avatar: '' },
    type: '实战训练',
    duration: 120,
    location: '训练场A',
    fee: 200,
    status: 'ongoing',
  },
])

const recentReviews = ref([
  {
    id: 1,
    student: { name: '张小明', avatar: '' },
    rating: 5,
    content: '教练非常专业，技术指导很到位，我的发球技术有了明显提升！',
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000),
  },
  {
    id: 2,
    student: { name: '李小红', avatar: '' },
    rating: 5,
    content: '课程安排合理，教练耐心细致，学到了很多实用的技巧。',
    createdAt: new Date(Date.now() - 4 * 60 * 60 * 1000),
  },
  {
    id: 3,
    student: { name: '王小强', avatar: '' },
    rating: 4,
    content: '教练的实战经验丰富，让我对比赛有了更深的理解。',
    createdAt: new Date(Date.now() - 6 * 60 * 60 * 1000),
  },
])

const pendingTasks = ref([
  {
    id: 1,
    type: 'appointment',
    title: '新的课程预约',
    description: '张小明预约了明天下午的基础技术课',
    createdAt: new Date(Date.now() - 30 * 60 * 1000),
  },
  {
    id: 2,
    type: 'feedback',
    title: '学员反馈',
    description: '李小红对上节课程进行了评价',
    createdAt: new Date(Date.now() - 45 * 60 * 1000),
  },
])

// 获取仪表板数据
const fetchDashboardData = async () => {
  try {
    await api.get('/coach/dashboard')
    // 在实际应用中，这里会更新响应式数据
  } catch {
    ElMessage.error('获取仪表板数据失败')
  }
}

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path)
}

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

// 开始上课
const startClass = async (classItem) => {
  try {
    await api.put(`/coach/classes/${classItem.id}/start`)
    classItem.status = 'ongoing'
    ElMessage.success('课程开始')
  } catch {
    ElMessage.error('操作失败')
  }
}

// 结束课程
const completeClass = async (classItem) => {
  try {
    await api.put(`/coach/classes/${classItem.id}/complete`)
    classItem.status = 'completed'
    ElMessage.success('课程结束')
  } catch {
    ElMessage.error('操作失败')
  }
}

// 处理待办事项
const handleTask = (task) => {
  switch (task.type) {
    case 'appointment':
      navigateTo('/coach/appointment-approval')
      break
    case 'feedback':
      navigateTo('/coach/student-feedback')
      break
    default:
      break
  }
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

// 格式化日期时间
const formatDateTime = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

// 获取课程状态类型
const getClassStatusType = (status) => {
  const types = {
    upcoming: 'primary',
    ongoing: 'success',
    completed: 'info',
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
    feedback: ChatLineSquare,
    system: Bell,
  }
  return icons[type] || Bell
}

// 组件挂载
onMounted(() => {
  fetchDashboardData()
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

.chart-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
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

:deep(.el-card.welcome-banner .el-card__body) {
  background: transparent;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #409eff;
}
</style>
