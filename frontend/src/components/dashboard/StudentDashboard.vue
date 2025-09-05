<template>
  <div class="student-dashboard">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <div class="floating-ball ball-1">🏓</div>
      <div class="floating-ball ball-2">🏓</div>
      <div class="floating-ball ball-3">🏓</div>
      <div class="artistic-text student-text">STUDENT</div>
      <div class="artistic-text dashboard-text">学习面板</div>
      <div class="geometric-decoration geo-1"></div>
      <div class="geometric-decoration geo-2"></div>
    </div>

    <!-- 今日课程提醒 -->
    <el-card class="dashboard-card course-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Calendar />
          </el-icon>
          <span>今日课程</span>
        </div>
      </template>
      <div v-if="todayCourses.length > 0" class="course-list">
        <div v-for="course in todayCourses" :key="course.id" class="course-item">
          <div class="course-time">{{ course.time }}</div>
          <div class="course-info">
            <div class="course-coach">{{ course.coachName }} 教练</div>
            <div class="course-location">{{ course.location }}</div>
          </div>
          <div class="course-status">
            <el-tag :type="course.status === 'confirmed' ? 'success' : 'warning'">
              {{ course.status === 'confirmed' ? '已确认' : '待确认' }}
            </el-tag>
          </div>
        </div>
      </div>
      <el-empty v-else description="今天没有安排课程" :image-size="60" />
    </el-card>

    <!-- 最近评价 -->
    <el-card class="dashboard-card evaluation-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <Star />
          </el-icon>
          <span>最近评价</span>
        </div>
      </template>
      <div v-if="recentEvaluations.length > 0" class="evaluation-list">
        <div v-for="evaluation in recentEvaluations" :key="evaluation.id" class="evaluation-item">
          <div class="evaluation-header">
            <span class="evaluation-course">{{ evaluation.courseName }}</span>
            <span class="evaluation-date">{{ formatDate(evaluation.date) }}</span>
          </div>
          <div class="evaluation-content">{{ evaluation.content }}</div>
        </div>
      </div>
      <el-empty v-else description="暂无训练评价" :image-size="60" />
    </el-card>

    <!-- 可选教练推荐 -->
    <el-card class="dashboard-card coach-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon">
            <UserFilled />
          </el-icon>
          <span>推荐教练</span>
          <el-link type="primary" @click="$router.push('/student/coaches')"> 查看更多 </el-link>
        </div>
      </template>
      <div v-if="recommendedCoaches.length > 0" class="coach-list">
        <div
          v-for="coach in recommendedCoaches"
          :key="coach.id"
          class="coach-item"
          @click="viewCoachDetail(coach.id)"
        >
          <el-avatar :size="40" :src="coach.avatar">
            <el-icon>
              <UserFilled />
            </el-icon>
          </el-avatar>
          <div class="coach-info">
            <div class="coach-name">{{ coach.name }}</div>
            <div class="coach-level">{{ coach.level }}</div>
          </div>
          <el-rate
            v-model="coach.rating"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}"
            size="small"
          />
        </div>
      </div>
      <el-empty v-else description="暂无推荐教练" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Star, UserFilled } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()

// 今日课程
const todayCourses = ref([])

// 最近评价
const recentEvaluations = ref([])

// 推荐教练
const recommendedCoaches = ref([])

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('MM-DD')
}

// 查看教练详情
const viewCoachDetail = (coachId) => {
  router.push(`/student/coaches/${coachId}`)
}

// 获取数据
const fetchData = async () => {
  // 模拟数据
  todayCourses.value = [
    {
      id: 1,
      time: '14:00-15:00',
      coachName: '张老师',
      location: '球台1',
      status: 'confirmed',
    },
    {
      id: 2,
      time: '16:00-17:00',
      coachName: '李老师',
      location: '球台3',
      status: 'pending',
    },
  ]

  recentEvaluations.value = [
    {
      id: 1,
      courseName: '基础训练',
      date: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000),
      content: '今天练习了正手攻球，有所进步，需要继续加强发力的稳定性。',
    },
    {
      id: 2,
      courseName: '技战术训练',
      date: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
      content: '学习了新的战术组合，反手技术有明显提升。',
    },
  ]

  recommendedCoaches.value = [
    {
      id: 1,
      name: '王教练',
      level: '高级教练',
      avatar: '',
      rating: 4.8,
    },
    {
      id: 2,
      name: '赵教练',
      level: '中级教练',
      avatar: '',
      rating: 4.6,
    },
  ]
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.student-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 24px;
  background: linear-gradient(
    135deg,
    #a8e6cf 0%,
    #88d8c0 25%,
    #7fcdcd 50%,
    #81c784 75%,
    #66bb6a 100%
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

.floating-ball {
  position: absolute;
  font-size: 60px;
  opacity: 0.08;
  animation: float 8s ease-in-out infinite;
}

.ball-1 {
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.ball-2 {
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.ball-3 {
  bottom: 20%;
  left: 15%;
  animation-delay: 4s;
}

.artistic-text {
  position: absolute;
  font-family: 'Arial Black', Arial, sans-serif;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.06);
  user-select: none;
}

.student-text {
  top: 15%;
  right: 8%;
  font-size: 80px;
  transform: rotate(-15deg);
  animation: shimmer 5s ease-in-out infinite;
}

.dashboard-text {
  bottom: 25%;
  left: 5%;
  font-size: 60px;
  transform: rotate(10deg);
  animation: shimmer 5s ease-in-out infinite 2.5s;
}

.geometric-decoration {
  position: absolute;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.geo-1 {
  top: 40%;
  left: 8%;
  width: 80px;
  height: 80px;
  animation: rotate 20s linear infinite;
}

.geo-2 {
  bottom: 30%;
  right: 15%;
  width: 120px;
  height: 120px;
  animation: rotate 25s linear infinite reverse;
}

.dashboard-card {
  border: none;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  position: relative;
  z-index: 10;
  overflow: hidden;
  transition: all 0.3s ease;
}

.dashboard-card:hover {
  transform: translateY(-4px);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.3) inset;
}

.course-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(129, 199, 132, 0.1));
}

.evaluation-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 193, 7, 0.1));
}

.coach-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(33, 150, 243, 0.1));
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
  color: #1890ff;
}

.card-header .el-link {
  margin-left: auto;
  color: #1890ff;
  font-weight: 500;
}

.course-list,
.evaluation-list,
.coach-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.course-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.course-time {
  font-weight: 600;
  color: #1890ff;
  min-width: 80px;
  font-size: 14px;
}

.course-info {
  flex: 1;
}

.course-coach {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.course-location {
  font-size: 12px;
  color: #666;
}

.evaluation-item {
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.evaluation-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.evaluation-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.evaluation-course {
  font-weight: 600;
  color: #333;
}

.evaluation-date {
  color: #666;
  font-size: 12px;
}

.evaluation-content {
  color: #555;
  line-height: 1.5;
}

.coach-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.coach-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.coach-info {
  flex: 1;
}

.coach-name {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
}

.coach-level {
  color: #666;
  font-size: 12px;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes shimmer {
  0%,
  100% {
    opacity: 0.06;
  }

  50% {
    opacity: 0.12;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .student-dashboard {
    padding: 16px;
  }

  .artistic-text {
    font-size: 50px !important;
  }

  .floating-ball {
    font-size: 40px;
  }

  .course-item,
  .evaluation-item,
  .coach-item {
    padding: 12px;
  }
}
</style>
