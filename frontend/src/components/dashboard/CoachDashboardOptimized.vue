<template>
  <div class="coach-dashboard-optimized">
    <!-- 性能监控 -->
    <PerformanceDebugger v-if="isDev" />
    
    <!-- 简化的欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}教练！</h1>
        <p class="welcome-subtitle">专业授课，成就每一位学员</p>
      </div>
      <div class="banner-stats">
        <div class="stat-item">
          <span class="stat-value">{{ todayStats.courses }}</span>
          <span class="stat-label">今日课程</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ todayStats.students }}</span>
          <span class="stat-label">教授学员</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">¥{{ todayStats.income }}</span>
          <span class="stat-label">今日收入</span>
        </div>
      </div>
    </div>

    <!-- 核心数据统计 - 简化版 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="12" :sm="6" v-for="stat in coreStats" :key="stat.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: stat.gradient }">
              <component :is="stat.icon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 标签页布局 - 分散复杂度 -->
    <el-card class="main-content" shadow="hover">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 今日课程标签 -->
        <el-tab-pane label="今日课程" name="today">
          <div v-if="activeTab === 'today'">
            <div class="today-content">
              <el-alert 
                title="今日课程安排" 
                type="info" 
                description="这里将显示今日的课程安排和时间表"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 我的学员标签 -->
        <el-tab-pane label="我的学员" name="students">
          <div v-if="activeTab === 'students'">
            <div class="students-content">
              <el-alert 
                title="我的学员" 
                type="success" 
                description="这里将显示学员列表和学习进度"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 收入统计标签 -->
        <el-tab-pane label="收入统计" name="income">
          <div v-if="activeTab === 'income'">
            <div class="income-content">
              <el-alert 
                title="收入统计" 
                type="warning" 
                description="这里将显示收入分析和统计图表"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 评价反馈标签 -->
        <el-tab-pane label="评价反馈" name="feedback">
          <div v-if="activeTab === 'feedback'">
            <div class="feedback-content">
              <el-alert 
                title="评价反馈" 
                type="info" 
                description="这里将显示学员评价和反馈信息"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 预约管理标签 -->
        <el-tab-pane label="预约管理" name="appointments">
          <div v-if="activeTab === 'appointments'">
            <div class="appointments-content">
              <el-alert 
                title="预约管理" 
                type="success" 
                description="这里将显示预约申请和管理功能"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 快捷操作 -->
    <el-card class="quick-actions" shadow="hover">
      <template #header>
        <h4>快捷操作</h4>
      </template>
      <el-row :gutter="16">
        <el-col :span="6" v-for="action in quickActions" :key="action.key">
          <el-button 
            :type="action.type" 
            :icon="action.icon"
            @click="handleQuickAction(action.key)"
            class="action-btn"
          >
            {{ action.label }}
          </el-button>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick, defineAsyncComponent } from 'vue'
import { useUserStore } from '@/stores/user'
import { Calendar, User, Money, Star } from '@element-plus/icons-vue'

// 懒加载组件
const PerformanceDebugger = defineAsyncComponent(() => import('@/components/PerformanceDebugger.vue'))

const userStore = useUserStore()
const isDev = import.meta.env.DEV

// 响应式数据 - 大幅简化
const activeTab = ref('today')

// 今日核心统计
const todayStats = ref({
  courses: 5,
  students: 12,
  income: 750
})

// 简化的核心统计指标
const coreStats = ref([
  {
    key: 'income',
    label: '月度收入',
    value: '¥18,500',
    icon: Money,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    key: 'students',
    label: '总学员',
    value: '45',
    icon: User,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    key: 'courses',
    label: '本月课程',
    value: '124',
    icon: Calendar,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    key: 'rating',
    label: '评分',
    value: '4.8分',
    icon: Star,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 简化的快捷操作
const quickActions = ref([
  { key: 'schedule', label: '查看课表', type: 'primary', icon: 'Calendar' },
  { key: 'add-student', label: '添加学员', type: 'success', icon: 'Plus' },
  { key: 'income-report', label: '收入报告', type: 'warning', icon: 'DocumentCopy' },
  { key: 'feedback', label: '学员反馈', type: 'info', icon: 'ChatDotSquare' }
])

// 计算属性
const getWelcomeMessage = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

// 事件处理
const handleTabChange = async (tabName) => {
  await nextTick()
  console.log(`切换到标签: ${tabName}`)
}

const handleQuickAction = (actionKey) => {
  console.log(`执行快捷操作: ${actionKey}`)
}
</script>

<style scoped>
.coach-dashboard-optimized {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #48bb78 0%, #38f9d7 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 24px rgba(72, 187, 120, 0.15);
}

.banner-content h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.banner-content p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.banner-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  height: 120px;
  border: none;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  font-size: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
}

.main-content {
  margin-bottom: 24px;
  min-height: 400px;
}

.quick-actions {
  margin-bottom: 24px;
}

.action-btn {
  width: 100%;
  height: 48px;
}

.today-content,
.students-content,
.income-content,
.feedback-content,
.appointments-content {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .banner-stats {
    justify-content: center;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 8px;
  }
}

/* 硬件加速 */
.stat-card,
.welcome-banner,
.main-content {
  will-change: transform;
  transform: translateZ(0);
}
</style>
