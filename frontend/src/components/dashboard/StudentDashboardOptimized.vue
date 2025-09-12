<template>
  <div class="student-dashboard-optimized">
    <!-- 性能监控 -->
    <PerformanceDebugger v-if="isDev" />
    
    <!-- 简化的欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}！</h1>
        <p class="welcome-subtitle">今天也要加油训练哦！</p>
      </div>
      <div class="banner-stats">
        <div class="stat-item">
          <span class="stat-value">{{ todayStats.courses }}</span>
          <span class="stat-label">今日课程</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">{{ todayStats.hours }}</span>
          <span class="stat-label">训练时长</span>
        </div>
        <div class="stat-item">
          <span class="stat-value">¥{{ userBalance }}</span>
          <span class="stat-label">账户余额</span>
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
        <el-tab-pane label="今日课程" name="schedule">
          <div v-if="activeTab === 'schedule'">
            <div class="schedule-content">
              <el-alert 
                title="今日课程安排" 
                type="info" 
                description="这里将显示今日的训练课程安排"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 我的教练标签 -->
        <el-tab-pane label="我的教练" name="coaches">
          <div v-if="activeTab === 'coaches'">
            <div class="coaches-content">
              <el-alert 
                title="我的教练" 
                type="success" 
                description="这里将显示我的教练信息和评价"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 学习进度标签 -->
        <el-tab-pane label="学习进度" name="progress">
          <div v-if="activeTab === 'progress'">
            <div class="progress-content">
              <el-alert 
                title="学习进度" 
                type="warning" 
                description="这里将显示学习进度和技能提升情况"
                show-icon
              />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 账户管理标签 -->
        <el-tab-pane label="账户管理" name="account">
          <div v-if="activeTab === 'account'">
            <div class="account-content">
              <el-alert 
                title="账户管理" 
                type="info" 
                description="这里将显示账户余额和消费记录"
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
import { Calendar, User, Trophy, CreditCard } from '@element-plus/icons-vue'

// 懒加载组件
const PerformanceDebugger = defineAsyncComponent(() => import('@/components/PerformanceDebugger.vue'))

const userStore = useUserStore()
const isDev = import.meta.env.DEV

// 响应式数据 - 大幅简化
const activeTab = ref('schedule')
const userBalance = ref(1250.0)

// 今日核心统计
const todayStats = ref({
  courses: 2,
  hours: '3小时',
})

// 简化的核心统计指标
const coreStats = ref([
  {
    key: 'courses',
    label: '本月课程',
    value: '24',
    icon: Calendar,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    key: 'coaches',
    label: '我的教练',
    value: '3',
    icon: User,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    key: 'achievement',
    label: '成就徽章',
    value: '8',
    icon: Trophy,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    key: 'balance',
    label: '账户余额',
    value: '¥1,250',
    icon: CreditCard,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 简化的快捷操作
const quickActions = ref([
  { key: 'book-course', label: '预约课程', type: 'primary', icon: 'Calendar' },
  { key: 'find-coach', label: '寻找教练', type: 'success', icon: 'Search' },
  { key: 'recharge', label: '账户充值', type: 'warning', icon: 'Wallet' },
  { key: 'progress', label: '学习进度', type: 'info', icon: 'TrendCharts' }
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
.student-dashboard-optimized {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #4299e1 0%, #667eea 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 24px rgba(66, 153, 225, 0.15);
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

.schedule-content,
.coaches-content,
.progress-content,
.account-content {
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
