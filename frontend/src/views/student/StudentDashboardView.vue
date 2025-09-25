<template>
  <div class="student-dashboard">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userStore.userInfo.name || '同学' }}！</h2>
          <p>今天也要努力练习乒乓球哦！</p>
        </div>
        <div class="user-avatar">
          <el-avatar :size="80" :src="userStore.userInfo.avatar">
            {{ userStore.userInfo.name?.charAt(0) || 'U' }}
          </el-avatar>
        </div>
      </div>
    </el-card>

    <!-- 快捷操作 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>选择教练</h3>
              <p>浏览教练信息，选择合适的教练</p>
              <PrimaryButton size="sm" to="/student/find-coach">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon">
              <el-icon>
                <Calendar />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>预约课程</h3>
              <p>预约你喜欢的课程时间</p>
              <PrimaryButton size="sm" to="/student/book-training">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon">
              <el-icon>
                <CreditCard />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>账户充值</h3>
              <p>为你的账户充值余额</p>
              <PrimaryButton size="sm" to="/student/account-recharge">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="action-card">
          <div class="action-content">
            <div class="action-icon">
              <el-icon>
                <Trophy />
              </el-icon>
            </div>
            <div class="action-text">
              <h3>我的比赛</h3>
              <p>查看比赛安排和赛程</p>
              <PrimaryButton size="sm" to="/student/matches">前往</PrimaryButton>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 个人统计 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="18">
        <el-card class="stats-card">
          <template #header>
            <span>学习统计</span>
          </template>

          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.totalClasses }}</div>
                <div class="stat-label">总课程数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.totalHours }}</div>
                <div class="stat-label">总学时</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.currentLevel }}</div>
                <div class="stat-label">当前等级</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.monthClasses }}</div>
                <div class="stat-label">本月课程</div>
              </div>
            </el-col>
          </el-row>

          <!-- 学习进度 -->
          <div class="progress-section">
            <h4>技能进度</h4>
            <div class="skill-progress">
              <div class="skill-item">
                <span>基础技术</span>
                <el-progress :percentage="skills.basic" :color="progressColor" />
              </div>
              <div class="skill-item">
                <span>发球技术</span>
                <el-progress :percentage="skills.serve" :color="progressColor" />
              </div>
              <div class="skill-item">
                <span>接发球</span>
                <el-progress :percentage="skills.receive" :color="progressColor" />
              </div>
              <div class="skill-item">
                <span>实战能力</span>
                <el-progress :percentage="skills.combat" :color="progressColor" />
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="balance-card">
          <template #header>
            <div class="balance-header">
              <span>账户余额</span>
              <PrimaryButton size="sm" to="/student/account-recharge">充值</PrimaryButton>
            </div>
          </template>

          <div class="balance-content">
            <div class="balance-amount">¥{{ userBalance.toFixed(2) }}</div>
            <div class="balance-info">
              <p>可预约课程 {{ Math.floor(userBalance / 100) }} 节</p>
              <p>今日消费 ¥{{ todayExpense }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日课程 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="today-classes">
          <template #header>
            <div class="card-header">
              <span>今日课程</span>
              <el-tag type="info">{{ formatDate(new Date()) }}</el-tag>
            </div>
          </template>

          <div v-if="todayClasses.length === 0" class="empty-state">
            <el-empty description="今天没有课程安排" />
            <PrimaryButton to="/student/book-training">立即预约</PrimaryButton>
          </div>

          <el-timeline v-else>
            <el-timeline-item v-for="classItem in todayClasses" :key="classItem.id" :timestamp="classItem.time"
              placement="top">
              <el-card class="class-item">
                <div class="class-content">
                  <div class="class-info">
                    <h4>{{ classItem.type }} - {{ classItem.coach.name }}</h4>
                    <p>课程时长：{{ classItem.duration }}分钟</p>
                    <p>上课地点：{{ classItem.location }}</p>
                  </div>
                  <div class="class-status">
                    <el-tag :type="getClassStatusType(classItem.status)">
                      {{ getClassStatusText(classItem.status) }}
                    </el-tag>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="recent-activity">
          <template #header>
            <span>最近活动</span>
          </template>

          <el-timeline>
            <el-timeline-item v-for="activity in recentActivities" :key="activity.id"
              :timestamp="formatDateTime(activity.time)" :type="getActivityType(activity.type)" size="small">
              <div class="activity-content">
                <span class="activity-title">{{ activity.title }}</span>
                <p class="activity-desc">{{ activity.description }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 推荐教练 -->
    <el-card class="recommended-coaches">
      <template #header>
        <div class="card-header">
          <span>推荐教练</span>
          <OutlineButton size="sm" variant="glass" to="/student/find-coach">查看更多</OutlineButton>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col v-for="coach in recommendedCoaches" :key="coach.id" :span="6">
          <el-card class="coach-card" @click="viewCoachDetail(coach)">
            <div class="coach-content">
              <el-avatar :size="60" :src="coach.avatar">
                {{ coach.name.charAt(0) }}
              </el-avatar>
              <h4>{{ coach.name }}</h4>
              <p>{{ getLevelText(coach.level) }}</p>
              <div class="coach-rating">
                <el-rate v-model="coach.rating" disabled show-score text-color="#ff9900" score-template="{value}" />
              </div>
              <div class="coach-price">¥{{ coach.hourlyRate }}/小时</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 学习成就 -->
    <el-card class="achievements">
      <template #header>
        <span>学习成就</span>
      </template>

      <div class="achievement-grid">
        <div v-for="achievement in achievements" :key="achievement.id" class="achievement-item"
          :class="{ achieved: achievement.achieved }">
          <div class="achievement-icon">
            <el-icon>
              <component :is="achievement.icon" />
            </el-icon>
          </div>
          <div class="achievement-text">
            <h5>{{ achievement.title }}</h5>
            <p>{{ achievement.description }}</p>
          </div>
          <div v-if="achievement.achieved" class="achievement-badge">
            <el-icon>
              <CircleCheck />
            </el-icon>
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
  Calendar,
  CircleCheck,
  Clock,
  CreditCard,
  Medal,
  Star,
  Trophy,
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
  totalClasses: 32,
  totalHours: 48,
  currentLevel: '中级',
  monthClasses: 8,
})

const skills = reactive({
  basic: 85,
  serve: 72,
  receive: 68,
  combat: 75,
})

const userBalance = ref(1580.5)
const todayExpense = ref(120)
const todayClasses = ref([
  {
    id: 1,
    time: '09:00',
    type: '基础技术课',
    coach: { name: '李教练' },
    duration: 60,
    location: '训练场A',
    status: 'upcoming',
  },
  {
    id: 2,
    time: '15:30',
    type: '实战练习',
    coach: { name: '王教练' },
    duration: 90,
    location: '训练场B',
    status: 'upcoming',
  },
])

const recentActivities = ref([
  {
    id: 1,
    time: new Date(Date.now() - 2 * 60 * 60 * 1000),
    type: 'booking',
    title: '预约成功',
    description: '成功预约了明天的基础技术课',
  },
  {
    id: 2,
    time: new Date(Date.now() - 4 * 60 * 60 * 1000),
    type: 'achievement',
    title: '获得成就',
    description: '连续学习7天，获得"坚持不懈"徽章',
  },
  {
    id: 3,
    time: new Date(Date.now() - 8 * 60 * 60 * 1000),
    type: 'payment',
    title: '充值成功',
    description: '账户充值500元',
  },
])

const recommendedCoaches = ref([
  {
    id: 1,
    name: '张明',
    level: 'senior',
    avatar: '',
    rating: 4.8,
    hourlyRate: 150,
  },
  {
    id: 2,
    name: '李华',
    level: 'intermediate',
    avatar: '',
    rating: 4.6,
    hourlyRate: 120,
  },
  {
    id: 3,
    name: '王强',
    level: 'expert',
    avatar: '',
    rating: 4.9,
    hourlyRate: 200,
  },
  {
    id: 4,
    name: '赵丽',
    level: 'senior',
    avatar: '',
    rating: 4.7,
    hourlyRate: 160,
  },
])

const achievements = ref([
  {
    id: 1,
    title: '初学者',
    description: '完成第一次训练',
    icon: Star,
    achieved: true,
  },
  {
    id: 2,
    title: '坚持不懈',
    description: '连续学习7天',
    icon: Clock,
    achieved: true,
  },
  {
    id: 3,
    title: '技术精进',
    description: '基础技术达到80分',
    icon: Medal,
    achieved: false,
  },
])

// 进度条颜色
const progressColor = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 },
]

// 获取仪表板数据
const fetchDashboardData = async () => {
  try {
    await api.get('/student/dashboard')
    // 在实际应用中，这里会更新响应式数据
  } catch {
    ElMessage.error('获取仪表板数据失败')
  }
}


// 查看教练详情
const viewCoachDetail = (coach) => {
  router.push(`/student/coach-profile/${coach.id}`)
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

// 获取活动类型
const getActivityType = (type) => {
  const types = {
    booking: 'primary',
    payment: 'success',
    achievement: 'warning',
    system: 'info',
  }
  return types[type] || 'primary'
}

// 获取等级文本
const getLevelText = (level) => {
  const texts = {
    junior: '初级教练',
    intermediate: '中级教练',
    senior: '高级教练',
    expert: '特级教练',
  }
  return texts[level] || level
}

// 组件挂载
onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.student-dashboard {
  padding: 20px;
}

.welcome-banner {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
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

.stats-card {
  height: 100%;
}

.stat-item {
  text-align: center;
  padding: 16px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
}

.progress-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.progress-section h4 {
  margin: 0 0 16px 0;
  color: #333;
}

.skill-progress {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.skill-item span {
  width: 80px;
  font-size: 14px;
  color: #666;
}

.balance-card {
  height: 100%;
}

.balance-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.balance-content {
  text-align: center;
}

.balance-amount {
  font-size: 36px;
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 16px;
}

.balance-info p {
  margin: 4px 0;
  color: #666;
  font-size: 14px;
}

.today-classes,
.recent-activity {
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

.activity-content {
  padding-left: 8px;
}

.activity-title {
  font-weight: bold;
  color: #333;
}

.activity-desc {
  margin: 4px 0 0 0;
  color: #666;
  font-size: 14px;
}

.recommended-coaches {
  margin-bottom: 20px;
}

.coach-card {
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.coach-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.coach-content h4 {
  margin: 12px 0 8px 0;
  color: #333;
}

.coach-content p {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
}

.coach-rating {
  margin-bottom: 8px;
}

.coach-price {
  font-weight: bold;
  color: #409eff;
}

.achievements {
  margin-bottom: 20px;
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
}

.achievement-item.achieved {
  background: #f0f9ff;
  border-color: #409eff;
}

.achievement-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #666;
}

.achievement-item.achieved .achievement-icon {
  background: #409eff;
  color: white;
}

.achievement-text h5 {
  margin: 0 0 4px 0;
  color: #333;
}

.achievement-text p {
  margin: 0;
  color: #666;
  font-size: 12px;
}

.achievement-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  color: #67c23a;
  font-size: 20px;
}

:deep(.el-card.welcome-banner .el-card__body) {
  background: transparent;
}

:deep(.el-progress-bar__outer) {
  background-color: #f0f2f5;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #409eff;
}
</style>
