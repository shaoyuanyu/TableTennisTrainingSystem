<template>
  <div class="super-admin-dashboard">
    <!-- 页面头部 -->
    <GlassHeaderCard
      title="超级管理员仪表盘"
      subtitle="系统概览与关键指标"
      icon="👑"
      class="dashboard-header"
    >
      <template #extra>
        <el-tag type="success">系统运行正常</el-tag>
      </template>
    </GlassHeaderCard>

    <!-- 系统概览 -->
    <GlassCardsGrid layout="auto" class="overview-grid">
      <GlassDisplayCard
        v-for="item in overviewItems"
        :key="item.key"
        :title="item.title"
        :icon="item.icon"
        :variant="item.variant"
      >
        <div class="overview-content">
          <div class="overview-value">{{ item.value }}</div>
          <div class="overview-description">{{ item.description }}</div>
        </div>
      </GlassDisplayCard>
    </GlassCardsGrid>

    <!-- 校区管理与系统状态 -->
    <GlassCardsGrid layout="fixed-2">
      <!-- 校区列表 -->
      <GlassCardWithHeader
        title="校区管理"
        icon="🏢"
        class="campus-management-card"
      >
        <template #headerActions>
          <PrimaryButton @click="$router.push('/admin/campus')" size="sm">
            管理校区
          </PrimaryButton>
        </template>

        <div style="padding: 16px 0;">
          <GlassTable
            :data="campusList"
            :loading="loading.campuses"
            density="sm"
            :stripe="true"
          >
            <el-table-column prop="campusName" label="校区名称" />
            <el-table-column prop="address" label="地址" />
            <el-table-column prop="contactPerson" label="联系人" />
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                  {{ row.status === 'active' ? '运营中' : '待启用' }}
                </el-tag>
              </template>
            </el-table-column>
          </GlassTable>
        </div>

        <GlassTable
          :data="campusList"
          :loading="loading.campuses"
          density="sm"
          :stripe="true"
        >
          <el-table-column prop="campusName" label="校区名称" />
          <el-table-column prop="address" label="地址" />
          <el-table-column prop="contactPerson" label="联系人" />
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '运营中' : '待启用' }}
              </el-tag>
            </template>
          </el-table-column>
        </GlassTable>
      </GlassCardWithHeader>

      <!-- 系统状态 -->
      <GlassCardWithHeader
        title="系统状态"
        icon="⚙️"
        class="system-status-card"
      >
        <template #headerActions>
          <PrimaryButton @click="$router.push('/admin/service')" size="sm">
            服务详情
          </PrimaryButton>
        </template>

        <div style="padding: 16px 0;">
          <StatusGrid :status="systemStatus" />
        </div>
      </GlassCardWithHeader>
    </GlassCardsGrid>

    <!-- 数据导出与系统日志 -->
    <GlassCardsGrid layout="fixed-2">
      <!-- 数据导出 -->
      <GlassCardWithHeader
        title="数据导出"
        icon="📊"
        class="data-export-card"
      >
        <template #headerActions>
          <PrimaryButton @click="$router.push('/admin/data')" size="sm">
            导出数据
          </PrimaryButton>
        </template>

        <div class="data-export-content" style="padding: 16px 0;">
          <p>系统累计数据量: {{ formatBytes(systemStats.totalDataSize) }}</p>
          <p>最近备份: {{ systemStats.lastBackup }}</p>
        </div>
      </GlassCardWithHeader>

      <!-- 系统日志 -->
      <GlassCardWithHeader
        title="系统日志"
        icon="📝"
        class="system-logs-card"
      >
        <template #headerActions>
          <PrimaryButton @click="$router.push('/admin/logs')" size="sm">
            查看日志
          </PrimaryButton>
        </template>

        <div style="padding: 16px 0;">
          <GlassTable
            :data="recentLogs"
            :loading="loading.logs"
            density="sm"
          >
            <el-table-column prop="level" label="级别" width="80">
              <template #default="{ row }">
                <el-tag :type="getLogTagType(row.level)">
                  {{ getLogLevelText(row.level) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="消息" />
            <el-table-column prop="timestamp" label="时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.timestamp) }}
              </template>
            </el-table-column>
          </GlassTable>
        </div>
      </GlassCardWithHeader>
    </GlassCardsGrid>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import {
  GlassHeaderCard,
  GlassDisplayCard,
  GlassCardWithHeader,
  GlassCardsGrid
} from '@/components/cards'
import GlassTable from '@/components/data/Table.vue'
import { PrimaryButton } from '@/components/buttons'
import StatusGrid from '@/components/StatusGrid.vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

// Router
const router = useRouter()

// 数据状态
const loading = ref({
  campuses: false,
  logs: false
})

// 系统概览数据
const overviewItems = ref([
  {
    key: 'campuses',
    title: '校区总数',
    icon: '🏢',
    variant: 'display',
    value: '0',
    description: '所有运营中的校区'
  },
  {
    key: 'users',
    title: '用户总数',
    icon: '👥',
    variant: 'content',
    value: '0',
    description: '系统注册用户数'
  },
  {
    key: 'revenue',
    title: '本月收入',
    icon: '💰',
    variant: 'enhanced',
    value: '¥0',
    description: '所有校区收入合计'
  },
  {
    key: 'courses',
    title: '课程总数',
    icon: '📚',
    variant: 'minimal',
    value: '0',
    description: '已完成和进行中的课程'
  }
])

// 校区列表
const campusList = ref([])

// 系统状态
const systemStatus = ref([
  {
    label: '数据库',
    type: 'status',
    status: true,
    trueText: '连接正常',
    falseText: '连接失败'
  },
  {
    label: 'API服务',
    type: 'status',
    status: true,
    trueText: '运行中',
    falseText: '已停止'
  },
  {
    label: '认证服务',
    type: 'status',
    status: true,
    trueText: '正常',
    falseText: '异常'
  }
])

// 系统统计
const systemStats = ref({
  totalDataSize: 0,
  lastBackup: '无'
})

// 最近日志
const recentLogs = ref([])

// 格式化字节大小
const formatBytes = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 获取日志级别文本
const getLogLevelText = (level) => {
  const levelMap = {
    error: '错误',
    warning: '警告',
    info: '信息',
    debug: '调试'
  }
  return levelMap[level] || level
}

// 获取日志标签类型
const getLogTagType = (level) => {
  const typeMap = {
    error: 'danger',
    warning: 'warning',
    info: 'primary',
    debug: 'info'
  }
  return typeMap[level] || 'info'
}

// 格式化时间
const formatTime = (timestamp) => {
  return dayjs(timestamp).format('MM-DD HH:mm:ss')
}

// 获取系统概览数据
const fetchOverviewData = async () => {
  try {
    // 在实际项目中，这里应该调用真实的API
    // 暂时使用模拟数据
    overviewItems.value = [
      {
        key: 'campuses',
        title: '校区总数',
        icon: '🏢',
        variant: 'display',
        value: '5',
        description: '所有运营中的校区'
      },
      {
        key: 'users',
        title: '用户总数',
        icon: '👥',
        variant: 'content',
        value: '1,248',
        description: '系统注册用户数'
      },
      {
        key: 'revenue',
        title: '本月收入',
        icon: '💰',
        variant: 'enhanced',
        value: '¥85,600',
        description: '所有校区收入合计'
      },
      {
        key: 'courses',
        title: '课程总数',
        icon: '📚',
        variant: 'minimal',
        value: '3,421',
        description: '已完成和进行中的课程'
      }
    ]
  } catch (error) {
    console.error('获取系统概览数据失败:', error)
    ElMessage.error('获取系统概览数据失败')
  }
}

// 获取校区列表
const fetchCampusList = async () => {
  loading.value.campuses = true
  try {
    const response = await api.get('/campus/names?page=1&size=5')
    campusList.value = (response.data.first || []).map(campus => ({
      ...campus,
      status: 'active' // 模拟状态数据
    }))
  } catch (error) {
    console.error('获取校区列表失败:', error)
    ElMessage.error('获取校区列表失败')
  } finally {
    loading.value.campuses = false
  }
}

// 获取系统日志
const fetchSystemLogs = async () => {
  loading.value.logs = true
  try {
    // 模拟日志数据
    recentLogs.value = [
      {
        id: 1,
        level: 'info',
        message: '系统启动完成',
        timestamp: new Date(Date.now() - 1000 * 60 * 5)
      },
      {
        id: 2,
        level: 'warning',
        message: '用户登录失败次数过多',
        timestamp: new Date(Date.now() - 1000 * 60 * 30)
      },
      {
        id: 3,
        level: 'info',
        message: '数据备份完成',
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 2)
      },
      {
        id: 4,
        level: 'error',
        message: '数据库连接超时',
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 5)
      },
      {
        id: 5,
        level: 'debug',
        message: 'API请求处理完成',
        timestamp: new Date(Date.now() - 1000 * 60 * 60 * 8)
      }
    ]

    // 模拟系统统计数据
    systemStats.value = {
      totalDataSize: 1024 * 1024 * 15, // 15MB
      lastBackup: dayjs(Date.now() - 1000 * 60 * 60 * 24).format('YYYY-MM-DD HH:mm')
    }
  } catch (error) {
    console.error('获取系统日志失败:', error)
    ElMessage.error('获取系统日志失败')
  } finally {
    loading.value.logs = false
  }
}

// 初始化数据
const initializeData = async () => {
  await Promise.all([
    fetchOverviewData(),
    fetchCampusList(),
    fetchSystemLogs()
  ])
}

// 组件挂载时获取数据
onMounted(() => {
  initializeData()
})
</script>

<style scoped>
.super-admin-dashboard {
  padding: 24px;
  width: 100%;
}

.dashboard-header {
  margin-bottom: 24px;
}

.overview-grid {
  margin-bottom: 24px;
}

.overview-content {
  text-align: center;
  padding: 16px 0;
}

.overview-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}

.overview-description {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.data-export-content {
  padding: 24px 0;
}

.data-export-content p {
  margin: 12px 0;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

</style>
