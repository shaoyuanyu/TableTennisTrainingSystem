<template>
  <div class="messages-view">
    <!-- 3D背景装饰 -->
    <div class="background-3d">
      <div class="particle-network"></div>
      <div class="floating-icons">
        <div class="floating-icon" style="top: 15%; left: 10%; animation-delay: 0s;">
          <el-icon><Message /></el-icon>
        </div>
        <div class="floating-icon" style="top: 25%; right: 12%; animation-delay: 1s;">
          <el-icon><Bell /></el-icon>
        </div>
        <div class="floating-icon" style="bottom: 20%; left: 15%; animation-delay: 2s;">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="floating-icon" style="bottom: 30%; right: 10%; animation-delay: 3s;">
          <el-icon><Postcard /></el-icon>
        </div>
      </div>
    </div>

    <!-- 主卡片区域 -->
    <el-card class="main-card">
      <template #header>
        <div class="messages-header">
          <div class="header-title">
            <el-icon class="header-icon">
              <Message />
            </el-icon>
            <span>消息中心</span>
            <el-badge :value="unreadCount" :max="99" class="unread-badge" />
          </div>
          <div class="header-actions">
            <el-dropdown trigger="click">
              <el-button type="primary" :disabled="!hasUnread">
                <span>标为已读</span>
                <el-icon class="el-icon--right">
                  <ArrowDown />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="messageStore.markAllRead">全部标为已读</el-dropdown-item>
                  <el-dropdown-item @click="markPageRead">本页标为已读</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button @click="messageStore.clearAll" type="danger" plain>清空消息</el-button>
          </div>
        </div>
      </template>

      <!-- 筛选区域 -->
      <div class="message-filters">
        <el-radio-group v-model="activeFilter" @change="filterMessages">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="system">系统通知</el-radio-button>
          <el-radio-button label="appointment">预约相关</el-radio-button>
          <el-radio-button label="evaluation">评价反馈</el-radio-button>
          <el-radio-button label="event">赛事消息</el-radio-button>
          <el-radio-button label="reminder">课程提醒</el-radio-button>
        </el-radio-group>

        <div class="filter-right">
          <el-input
            v-model="searchQuery"
            placeholder="搜索消息内容"
            clearable
            style="width: 250px; margin-left: 16px;"
            @clear="clearSearch"
            @keyup.enter="searchMessages"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div
          v-for="message in paginatedMessages"
          :key="message.id"
          class="message-item"
          :class="{ unread: !message.read }"
          @click="handleMessageClick(message)"
        >
          <div class="message-icon">
            <el-avatar :size="40" :src="message.avatar">
              <component :is="getMessageIcon(message.type)" />
            </el-avatar>
          </div>

          <div class="message-content">
            <div class="message-header">
              <span class="message-title">{{ message.title }}</span>
              <span class="message-time">{{ formatTime(message.createdAt) }}</span>
            </div>
            <div class="message-summary">{{ message.summary }}</div>
            <div class="message-meta">
              <el-tag :type="getMessageTypeColor(message.type)" size="small">
                {{ getMessageTypeText(message.type) }}
              </el-tag>
              <span v-if="message.from" class="message-from">来自: {{ message.from }}</span>
            </div>
          </div>

          <div class="message-actions">
            <el-button
              size="small"
              type="danger"
              plain
              @click.stop="messageStore.deleteMessage(message.id)"
            >
              删除
            </el-button>
          </div>
        </div>

        <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
      </div>

      <!-- 分页控制 -->
      <div class="pagination-control">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalMessages"
          :page-size="5"
          layout="total, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedMessage?.title"
      width="600px"
    >
      <div v-if="selectedMessage" class="message-detail">
        <div class="detail-meta">
          <span class="detail-from">发送者: {{ selectedMessage.from || '系统' }}</span>
          <span class="detail-time">{{ formatDetailTime(selectedMessage.createdAt) }}</span>
        </div>
        <div class="detail-content" v-html="selectedMessage.content"></div>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="!selectedMessage?.read"
          type="primary"
          @click="messageStore.markAsRead(selectedMessage.id)"
        >
          标为已读
        </el-button>
      </template>
    </el-dialog>

    <!-- 浮动操作按钮 -->
    <div class="floating-action-buttons">
      <transition-group name="fab-item">
        <el-tooltip v-if="fabMenuOpen" content="全部标为已读" placement="left">
          <el-button class="fab-item" type="success" circle @click="messageStore.markAllRead">
            <el-icon><Check /></el-icon>
          </el-button>
        </el-tooltip>

        <el-tooltip v-if="fabMenuOpen" content="清空消息" placement="left">
          <el-button class="fab-item" type="danger" circle @click="messageStore.clearAll">
            <el-icon><Delete /></el-icon>
          </el-button>
        </el-tooltip>

        <el-tooltip content="快捷操作" placement="left">
          <el-button class="fab-main" type="primary" circle @click="toggleFabMenu">
            <el-icon><More /></el-icon>
          </el-button>
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
import {
  Message, Bell, ChatDotRound, Postcard, ArrowDown, Search, Check, Delete, More,
  Setting, Calendar, Star, User, Trophy
} from '@element-plus/icons-vue'

const messageStore = useMessageStore()
const {
  unreadCount,
  hasUnread,
  activeFilter,
  searchQuery,
  currentPage,
  pageSize,
  detailDialogVisible,
  selectedMessage,
  filteredMessages,
  paginatedMessages,
  totalMessages,
} = storeToRefs(messageStore)

const fabMenuOpen = ref(false)

// 消息类型相关函数
const getMessageIcon = (type) => {
  const iconMap = {
    system: Setting,
    appointment: Calendar,
    evaluation: Star,
    user: User,
    event: Trophy,
    reminder: Bell
  }
  return iconMap[type] || Bell
}

const getMessageTypeColor = (type) => {
  const colorMap = {
    system: 'info',
    appointment: 'warning',
    evaluation: 'success',
    user: 'primary',
    event: 'danger',
    reminder: 'info'
  }
  return colorMap[type] || 'info'
}

const getMessageTypeText = (type) => {
  const textMap = {
    system: '系统通知',
    appointment: '预约相关',
    evaluation: '评价反馈',
    user: '用户消息',
    event: '赛事消息',
    reminder: '课程提醒',
  }
  return textMap[type] || '其他'
}

const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `今天 ${hours}:${minutes}`
  } else if (diffDays === 1) {
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `昨天 ${hours}:${minutes}`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${month}-${day}`
  }
}

const formatDetailTime = (time) => {
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const handleMessageClick = (message) => {
  selectedMessage.value = message
  detailDialogVisible.value = true
  if (!message.read) {
    messageStore.markAsRead(message.id)
  }
}

const markPageRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将本页消息标为已读吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    paginatedMessages.value
      .filter(msg => !msg.read)
      .forEach(msg => messageStore.markAsRead(msg.id))
    ElMessage.success('本页消息已标为已读')
  } catch {
    // 用户取消
  }
}

const filterMessages = () => {
  currentPage.value = 1
}

const searchMessages = () => {
  currentPage.value = 1
}

const clearSearch = () => {
  searchQuery.value = ''
  currentPage.value = 1
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const toggleFabMenu = () => {
  fabMenuOpen.value = !fabMenuOpen.value
}

onMounted(() => {
  console.log('MessagesViewFinal mounted')
  try {
    messageStore.fetchMessages()
  } catch (error) {
    console.error('Error fetching messages:', error)
  }
})
</script>

<style scoped>
.messages-view {
  position: relative;
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.background-3d {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;

}

.background-3d .particle-network {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, rgba(255,255,255,0.1) 0%, transparent 70%);
}

.background-3d .floating-icons {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.background-3d .floating-icons .floating-icon {
  position: absolute;
  font-size: 32px;
  color: rgba(255, 255, 255, 0.15);
  animation: float 6s ease-in-out infinite;
}

.main-card {
  position: relative;
  z-index: 10;
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;

}

.main-card :deep(.el-card__header) {
  background: linear-gradient(to right, #409EFF, #64b5ff);
  color: white;
  padding: 16px 24px;
  border-bottom: none;
}

.messages-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 600;
  font-size: 18px;
  color: white;
}

.header-title .header-icon {
  font-size: 24px;
}

.header-title .unread-badge :deep(.el-badge__content) {
  background: #ff4d4f;
  border: 2px solid white;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-actions .el-dropdown .el-button {
  min-width: 100px;
  padding: 0 15px;
}

.header-actions .el-dropdown .el-button span {
  display: inline-block;
  width: 100%;
  text-align: center;
}

.message-filters {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  margin: 16px 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);

}

.message-filters .filter-right {
  display: flex;
  align-items: center;
}

.message-list {
  max-height: 600px;
  overflow-y: auto;
  padding: 0 24px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  margin-bottom: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);

}

.message-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.message-item.unread {
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.1), rgba(255, 255, 255, 0.7));
  border-left: 4px solid #1890ff;
  box-shadow: 0 2px 15px rgba(24, 144, 255, 0.1);
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;

}

.message-header .message-title {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.message-header .message-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.message-summary {
  color: #666;
  line-height: 1.5;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 12px;

}

.message-meta .message-from {
  font-size: 12px;
  color: #999;
}

.message-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  white-space: nowrap;
}

.pagination-control {
  padding: 16px 24px;
  background: white;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
}

.message-detail {
  line-height: 1.6;

}

.message-detail .detail-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
  font-size: 14px;
  color: #666;
}

.message-detail .detail-content {
  color: #333;
}

.message-detail .detail-content :deep(p) {
  margin: 8px 0;
}

.floating-action-buttons {
  position: fixed;
  right: 40px;
  bottom: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  z-index: 100;

}

.floating-action-buttons .fab-main {
  width: 56px;
  height: 56px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.floating-action-buttons .fab-main .el-icon {
  font-size: 24px;
}

.floating-action-buttons .fab-item {
  width: 48px;
  height: 48px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.floating-action-buttons .fab-item .el-icon {
  font-size: 20px;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-12px) rotate(2deg);
  }
}

.fab-item-enter-active,
.fab-item-leave-active {
  transition: all 0.3s ease;
}

.fab-item-enter-from,
.fab-item-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-view {
    padding: 16px;
  }

}

@media (max-width: 768px) {
  .message-filters {
    flex-direction: column;
    gap: 12px;
  }

  .message-filters .filter-right {
    width: 100%;
  }

  .message-filters .filter-right .el-input {
    width: 100%;
  }

  .message-item {
    padding: 16px;
    flex-direction: column;
  }

  .message-actions {
    flex-direction: row;
    justify-content: flex-end;
    width: 100%;
    margin-top: 12px;
  }

  .floating-action-buttons {
    right: 20px;
    bottom: 20px;
  }
}
</style>
