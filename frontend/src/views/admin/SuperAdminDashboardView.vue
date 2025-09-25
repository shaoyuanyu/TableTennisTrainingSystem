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
        <div class="header-extras">
          <el-tag type="success">系统运行正常</el-tag>
          <el-tag type="primary" v-if="unreadCount > 0">未读消息 {{ unreadCount }}</el-tag>
        </div>
      </template>
    </GlassHeaderCard>

    <!-- 系统概览 -->
    <GlassCardsGrid layout="auto" class="overview-grid">
      <template v-if="loading.overview">
        <GlassDisplayCard v-for="n in 4" :key="`sk-${n}`" title="加载中" icon="⏳" variant="content">
          <div class="overview-content">
            <el-skeleton :rows="1" animated style="margin-bottom: 12px;" />
            <el-skeleton :rows="2" animated />
          </div>
        </GlassDisplayCard>
      </template>
      <template v-else>
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
      </template>
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

        <div style="padding: 8px 0;">
          <GlassTable
            :data="campusList"
            :loading="loading.campuses"
            density="sm"
            :stripe="true"
            empty-title="暂无校区"
            empty-description="点击右上角进入校区管理创建"
          >
            <el-table-column prop="campusName" label="校区名称" />
          </GlassTable>
        </div>
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

    <!-- 最近消息预览 -->
    <GlassCardsGrid layout="fixed-2">
      <GlassCardWithHeader title="最近消息" class="system-logs-card">
        <template #headerActions>
          <PrimaryButton @click="$router.push('/messages')" size="sm">查看全部</PrimaryButton>
        </template>
        <div style="padding: 8px 0;">
          <GlassTable :data="recentMessages" :loading="loading.messages" density="sm">
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="createdAt" label="时间" width="180">
              <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
            </el-table-column>
          </GlassTable>
        </div>
      </GlassCardWithHeader>
    </GlassCardsGrid>
  </div>

</template>

<script setup>
import { ref, onMounted } from 'vue'
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

// Router（如需路由跳转可启用）
// import { useRouter } from 'vue-router'
// const router = useRouter()

// 数据状态
const loading = ref({
  campuses: false,
  messages: false,
  overview: false
})

// 系统概览数据
const overviewItems = ref([
  { key: 'campuses', title: '校区总数', icon: '🏢', variant: 'display', value: '0', description: '所有运营中的校区' },
  { key: 'users', title: '用户总数', icon: '👥', variant: 'content', value: '0', description: '系统注册用户数' },
  { key: 'revenue', title: '本月充值', icon: '💰', variant: 'enhanced', value: '¥0', description: '所有用户本月充值合计（近似）' },
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

// 预留：系统统计（后续如需回归可启用）
// const systemStats = ref({ totalDataSize: 0, lastBackup: '无' })

// 最近消息
const recentMessages = ref([])
const unreadCount = ref(0)

// 统计格式化函数预留（当前未使用）
// const formatBytes = (bytes) => {
//   if (bytes === 0) return '0 Bytes'
//   const k = 1024
//   const sizes = ['Bytes', 'KB', 'MB', 'GB']
//   const i = Math.floor(Math.log(bytes) / Math.log(k))
//   return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
// }

// 格式化时间
const formatTime = (timestamp) => {
  return dayjs(timestamp).format('MM-DD HH:mm:ss')
}

// 获取系统概览数据（真实 API 对接）
const fetchOverviewData = async () => {
  loading.value.overview = true
  try {
    // 校区总数（/campus/names 返回 Pair -> second 为总数）
    const campusResp = await api.get('/campus/names?page=1&size=1')
    const campusTotal = campusResp?.data?.second ?? 0

    // 用户总数（仅超管）
    let usersTotal = 0
    try {
      const usersResp = await api.get('/user/users?page=1&size=1')
      usersTotal = usersResp?.data?.totalCount ?? 0
    } catch {
      usersTotal = 0
    }

    // 本月充值（近似：取前200条记录按 createdAt 过滤）
    let monthRevenue = 0
    try {
      const recResp = await api.get('/wallet/recharge/records?page=1&size=200')
      const records = recResp?.data?.first || recResp?.data || []
      console.log(records)
      const startOfMonth = dayjs().startOf('month')
      monthRevenue = (records || [])
        .filter(r => r.createdAt && dayjs(r.createdAt).isAfter(startOfMonth))
        .reduce((sum, r) => sum + (Number(r.amount) || 0), 0)
    } catch {
      monthRevenue = 0
    }

    overviewItems.value = [
      { key: 'campuses', title: '校区总数', icon: '🏢', variant: 'display', value: String(campusTotal), description: '所有运营中的校区' },
      { key: 'users', title: '用户总数', icon: '👥', variant: 'content', value: String(usersTotal), description: '系统注册用户数' },
      { key: 'revenue', title: '本月充值', icon: '💰', variant: 'enhanced', value: `¥${Number(monthRevenue).toLocaleString() }`, description: '所有用户本月充值合计（近似）' },
      { key: 'unread', title: '未读消息', icon: '✉️', variant: 'minimal', value: String(unreadCount.value || 0), description: '消息中心未读数量' },
    ]
  } catch (error) {
    console.error('获取系统概览数据失败:', error)
    ElMessage.error('获取系统概览数据失败')
  } finally {
    loading.value.overview = false
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

// 获取最近消息
const fetchRecentMessages = async () => {
  loading.value.messages = true
  try {
    const resp = await api.get('/messages?unreadOnly=false&page=0&size=5')
    const list = resp?.data?.messages || []
    recentMessages.value = list
  } catch (error) {
    console.error('获取最近消息失败:', error)
    ElMessage.error('获取最近消息失败')
  } finally {
    loading.value.messages = false
  }
}

// 获取未读消息数量
const fetchUnreadCount = async () => {
  try {
    const resp = await api.get('/messages/unread-count')
    unreadCount.value = resp?.data?.count ?? 0
  } catch {
    unreadCount.value = 0
  }
}

// 初始化数据
const initializeData = async () => {
  // 先拿未读消息数量，确保概览卡片能显示
  await fetchUnreadCount()
  await Promise.all([
    fetchOverviewData(),
    fetchCampusList(),
    fetchRecentMessages(),
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
