<template>
  <div class="messages-page">
    <GlassHeaderCard title="消息中心" icon="💬" size="large">
      <template #headerActions>
        <div class="header-actions">
          <el-input v-model="searchQuery" placeholder="搜索消息内容" clearable class="search-input" @clear="clearSearch"
            @keyup.enter="searchMessages">
            <template #prefix>
              <el-icon>
                <Search />
              </el-icon>
            </template>
          </el-input>
          <el-dropdown trigger="click">
            <PrimaryButton :disabled="!hasUnread">
              <span>标为已读</span>
              <el-icon class="el-icon--right">
                <ArrowDown />
              </el-icon>
            </PrimaryButton>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="messageStore.markAllRead">全部标为已读</el-dropdown-item>
                <el-dropdown-item @click="markPageRead">本页标为已读</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <OutlineButton color="danger" @click="messageStore.clearAll">清空消息</OutlineButton>
          <el-badge :value="unreadCount" :max="99" class="unread-badge" />
        </div>
      </template>

      <!-- 筛选：采用 TagBadge 语义标签作为过滤器 -->
      <div class="filters-bar">
        <div class="filters">
          <TagBadge v-for="f in filters" :key="f.value" :text="f.label"
            :variant="activeFilter === f.value ? 'solid' : 'glass'" :type="f.tagType" size="small" clickable
            @click="() => setFilter(f.value)" />
        </div>
      </div>

      <!-- 列表 -->
      <div class="message-list">
        <div v-for="message in paginatedMessages" :key="message.id" class="message-row glass-item"
          :class="{ unread: !message.read }" @click="handleMessageClick(message)">
          <div class="left">
            <el-avatar :size="44" :src="message.avatar">
              <component :is="getMessageIcon(message.type)" />
            </el-avatar>
          </div>
          <div class="center">
            <div class="title-line">
              <span class="title">{{ message.title }}</span>
              <span class="time">{{ formatTime(message.createdAt) }}</span>
            </div>
            <div class="summary">{{ message.summary }}</div>
            <div class="meta">
              <TagBadge :text="getMessageTypeText(message.type)" :type="typeToTag(message.type)" size="small" />
              <span v-if="message.from" class="from">来自：{{ message.from }}</span>
            </div>
          </div>
          <div class="right">
            <OutlineButton size="sm" color="danger" @click.stop="messageStore.deleteMessage(message.id)">
              删除
            </OutlineButton>
          </div>
        </div>

        <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="totalMessages"
          :page-size="5" layout="total, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handlePageChange" />
      </div>
    </GlassHeaderCard>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" :title="selectedMessage?.title" width="600px">
      <div v-if="selectedMessage" class="message-detail">
        <div class="detail-meta">
          <span class="detail-from">发送者: {{ selectedMessage.from || '系统' }}</span>
          <span class="detail-time">{{ formatDetailTime(selectedMessage.createdAt) }}</span>
        </div>
        <div class="detail-content" v-html="selectedMessage.content"></div>
      </div>
      <template #footer>
        <OutlineButton @click="detailDialogVisible = false">关闭</OutlineButton>
        <PrimaryButton v-if="!selectedMessage?.read" @click="messageStore.markAsRead(selectedMessage.id)">标为已读
        </PrimaryButton>
      </template>
    </el-dialog>

    <!-- 浮动操作 -->
    <div class="floating-action-buttons">
      <transition-group name="fab-item">
        <el-tooltip v-if="fabMenuOpen" content="全部标为已读" placement="left">
          <IconButton class="fab-item" color="success" @click="messageStore.markAllRead">
            <el-icon>
              <Check />
            </el-icon>
          </IconButton>
        </el-tooltip>
        <el-tooltip v-if="fabMenuOpen" content="清空消息" placement="left">
          <IconButton class="fab-item" color="danger" @click="messageStore.clearAll">
            <el-icon>
              <Delete />
            </el-icon>
          </IconButton>
        </el-tooltip>
        <el-tooltip content="快捷操作" placement="left">
          <IconButton class="fab-main" @click="toggleFabMenu">
            <el-icon>
              <More />
            </el-icon>
          </IconButton>
        </el-tooltip>
      </transition-group>
    </div>
  </div>

</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMessageStore } from '@/stores/messageStore'
import { storeToRefs } from 'pinia'
import { PrimaryButton, OutlineButton, IconButton } from '@/components/buttons'
import TagBadge from '@/components/TagBadge.vue'
import { GlassHeaderCard } from '@/components/cards'
import { ArrowDown, Search, Check, Delete, More, Bell, Setting, Calendar, Star, User, Trophy } from '@element-plus/icons-vue'

const messageStore = useMessageStore()
const { unreadCount, hasUnread, activeFilter, searchQuery, currentPage, pageSize, detailDialogVisible, selectedMessage, filteredMessages, paginatedMessages, totalMessages } = storeToRefs(messageStore)

const fabMenuOpen = ref(false)

// 过滤器配置（与设计语言一致的语义标签）
const filters = [
  { value: 'all', label: '全部', tagType: 'primary' },
  { value: 'unread', label: '未读', tagType: 'info' },
  { value: 'system', label: '系统通知', tagType: 'primary' },
  { value: 'appointment', label: '预约相关', tagType: 'warning' },
  { value: 'evaluation', label: '评价反馈', tagType: 'success' },
  { value: 'event', label: '赛事消息', tagType: 'danger' },
  { value: 'reminder', label: '课程提醒', tagType: 'info' },
]

const setFilter = (val) => {
  activeFilter.value = val
  currentPage.value = 1
}

// 类型映射
const getMessageIcon = (type) => {
  const iconMap = { system: Setting, appointment: Calendar, evaluation: Star, user: User, event: Trophy, reminder: Bell }
  return iconMap[type] || Bell
}
const typeToTag = (type) => ({ system: 'info', appointment: 'warning', evaluation: 'success', user: 'primary', event: 'danger', reminder: 'info' }[type] || 'info')
const getMessageTypeText = (type) => ({ system: '系统通知', appointment: '预约相关', evaluation: '评价反馈', user: '用户消息', event: '赛事消息', reminder: '课程提醒' }[type] || '其他')

// 时间格式化
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))
  if (diffDays === 0) {
    const h = String(date.getHours()).padStart(2, '0')
    const m = String(date.getMinutes()).padStart(2, '0')
    return `今天 ${h}:${m}`
  } else if (diffDays === 1) {
    const h = String(date.getHours()).padStart(2, '0')
    const m = String(date.getMinutes()).padStart(2, '0')
    return `昨天 ${h}:${m}`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    const mm = String(date.getMonth() + 1).padStart(2, '0')
    const dd = String(date.getDate()).padStart(2, '0')
    return `${mm}-${dd}`
  }
}
const formatDetailTime = (time) => {
  const d = new Date(time)
  const y = d.getFullYear()
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const m = String(d.getMinutes()).padStart(2, '0')
  return `${y}-${mm}-${dd} ${h}:${m}`
}

// 行为
const handleMessageClick = (message) => {
  selectedMessage.value = message
  detailDialogVisible.value = true
  if (!message.read) messageStore.markAsRead(message.id)
}

const markPageRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将本页消息标为已读吗？', '确认操作', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    paginatedMessages.value.filter((m) => !m.read).forEach((m) => messageStore.markAsRead(m.id))
    ElMessage.success('本页消息已标为已读')
  } catch {
    // 用户取消或失败时静默处理
  }
}

const searchMessages = () => { currentPage.value = 1 }
const clearSearch = () => { searchQuery.value = ''; currentPage.value = 1 }
const handleSizeChange = (size) => { pageSize.value = size; currentPage.value = 1 }
const handlePageChange = (page) => { currentPage.value = page }
const toggleFabMenu = () => { fabMenuOpen.value = !fabMenuOpen.value }

onMounted(() => {
  try { messageStore.fetchMessages() } catch (e) { console.error('fetchMessages failed', e) }
})
</script>

<style scoped>
.messages-page {
  padding: var(--spacing-2xl);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.search-input {
  width: 260px;
}

.unread-badge :deep(.el-badge__content) {
  background: #ff4d4f;
  border: 2px solid white;
}

.filters-bar {
  margin-bottom: var(--spacing-xl);
}

.filters {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.glass-item {
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: var(--spacing-lg);
  align-items: start;
  background: var(--white-alpha-15);
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  transition: var(--transition-normal);
}

.glass-item:hover {
  background: var(--white-alpha-20);
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.glass-item.unread {
  border-left: 4px solid #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.title-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xs);
}

.title {
  font-weight: var(--font-weight-semibold);
  color: white;
}

.time {
  font-size: var(--font-size-xs);
  color: var(--white-alpha-80);
  white-space: nowrap;
}

.summary {
  color: var(--white-alpha-85);
  line-height: 1.5;
  margin-bottom: var(--spacing-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.from {
  font-size: var(--font-size-xs);
  color: var(--white-alpha-80);
}

.right {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: var(--spacing-xl);
  display: flex;
  justify-content: flex-end;
}

/* 浮动操作按钮 */
.floating-action-buttons {
  position: fixed;
  right: 24px;
  bottom: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  z-index: 100;
}

.fab-item-enter-active,
.fab-item-leave-active {
  transition: all 0.3s ease;
}

.fab-item-enter-from,
.fab-item-leave-to {
  opacity: 0;
  transform: translateY(16px);
}

@media (max-width: 768px) {
  .messages-page {
    padding: var(--spacing-lg);
  }

  .search-input {
    width: 100%;
  }

  .header-actions {
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .glass-item {
    grid-template-columns: auto 1fr;
  }

  .right {
    justify-content: flex-end;
    grid-column: span 2;
  }
}
</style>
