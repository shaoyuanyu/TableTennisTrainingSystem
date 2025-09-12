<template>
  <div class="campus-admin-dashboard-optimized">
    <!-- 性能监控 -->
    <PerformanceDebugger v-if="isDev" />
    
    <!-- 简化的欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userStore.userName }}！</h1>
        <p class="welcome-subtitle">{{ getCampusName() }}校区运营概览</p>
      </div>
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

    <!-- 核心数据统计 - 只显示关键指标 -->
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
        <!-- 概览标签 -->
        <el-tab-pane label="运营概览" name="overview">
          <div v-if="activeTab === 'overview'">
            <div class="overview-content">
              <el-alert title="运营概览功能" type="info" description="这里将显示运营概览数据" />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 营收分析标签 -->
        <el-tab-pane label="营收分析" name="revenue">
          <div v-if="activeTab === 'revenue'">
            <div class="revenue-content">
              <el-alert title="营收分析功能" type="success" description="这里将显示营收分析图表" />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 教练管理标签 -->
        <el-tab-pane label="教练管理" name="coaches">
          <div v-if="activeTab === 'coaches'">
            <div class="coaches-content">
              <el-alert title="教练管理功能" type="warning" description="这里将显示教练管理界面" />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 学员分析标签 -->
        <el-tab-pane label="学员分析" name="students">
          <div v-if="activeTab === 'students'">
            <div class="students-content">
              <el-alert title="学员分析功能" type="info" description="这里将显示学员分析数据" />
            </div>
          </div>
        </el-tab-pane>
        
        <!-- 场地管理标签 -->
        <el-tab-pane label="场地管理" name="venues">
          <div v-if="activeTab === 'venues'">
            <div class="venues-content">
              <el-alert title="场地管理功能" type="success" description="这里将显示场地管理界面" />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 快捷操作 - 简化版 -->
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
import { Calendar, User, Management, Money } from '@element-plus/icons-vue'

// 懒加载组件
const PerformanceDebugger = defineAsyncComponent(() => import('@/components/PerformanceDebugger.vue'))

const userStore = useUserStore()
const isDev = import.meta.env.DEV

// 响应式数据 - 大幅简化
const activeTab = ref('overview')

// 今日核心统计
const todayStats = ref({
  revenue: '¥12,580',
  courses: 28,
  utilization: 85
})

// 简化的核心统计指标 - 只保留最重要的4个
const coreStats = ref([
  {
    key: 'revenue',
    label: '月度营收',
    value: '¥245,000',
    icon: Money,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    key: 'students',
    label: '活跃学员',
    value: '156',
    icon: User,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    key: 'coaches',
    label: '在职教练',
    value: '12',
    icon: Management,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    key: 'courses',
    label: '本月课程',
    value: '568',
    icon: Calendar,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 简化的快捷操作
const quickActions = ref([
  { key: 'add-coach', label: '添加教练', type: 'primary', icon: 'Plus' },
  { key: 'schedule', label: '课程安排', type: 'success', icon: 'Calendar' },
  { key: 'reports', label: '报表导出', type: 'warning', icon: 'Download' },
  { key: 'settings', label: '系统设置', type: 'info', icon: 'Setting' }
])

// 计算属性
const getWelcomeMessage = () => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
}

const getCampusName = () => {
  return userStore.userInfo?.campus || '未知校区'
}

// 事件处理
const handleTabChange = async (tabName) => {
  // 使用nextTick确保DOM更新后再处理
  await nextTick()
  console.log(`切换到标签: ${tabName}`)
}

const handleQuickAction = (actionKey) => {
  console.log(`执行快捷操作: ${actionKey}`)
  // 这里添加具体的路由跳转或操作逻辑
}
</script>

<style scoped>
.campus-admin-dashboard-optimized {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px;
  border-radius: 16px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.15);
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
