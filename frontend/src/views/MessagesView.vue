<template>
  <div class="messages-view">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <!-- 消息相关图标 -->
      <div class="message-icon mail-icon">📧</div>
      <div class="message-icon chat-icon">💬</div>
      <div class="message-icon bell-icon">🔔</div>
      <div class="message-icon envelope-icon">✉️</div>

      <!-- 通信装饰 -->
      <div class="communication-decoration signal-icon">📶</div>
      <div class="communication-decoration connect-icon">🔗</div>
      <div class="communication-decoration globe-icon">🌐</div>

      <div class="art-text">
        <div class="art-text-main">MESSAGE</div>
        <div class="art-text-sub">消息中心</div>
      </div>

      <div class="geometric-decoration geo-1"></div>
      <div class="geometric-decoration geo-2"></div>
      <div class="geometric-decoration geo-3"></div>
    </div>

    <el-card class="main-card">
      <template #header>
        <div class="messages-header">
          <div class="header-title">
            <span class="icon">💌</span>
            <span>消息中心</span>
          </div>
          <div class="header-actions">
            <el-button @click="markAllRead" :disabled="!hasUnread">全部标为已读</el-button>
            <el-button @click="clearAll" type="danger" plain>清空消息</el-button>
          </div>
        </div>
      </template>

      <div class="message-filters">
        <el-radio-group v-model="activeFilter" @change="filterMessages">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="unread">未读</el-radio-button>
          <el-radio-button label="system">系统通知</el-radio-button>
          <el-radio-button label="appointment">预约相关</el-radio-button>
          <el-radio-button label="evaluation">评价反馈</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 消息列表 -->
      <div class="message-list">
        <div
          v-for="message in filteredMessages"
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
              v-if="!message.read"
              size="small"
              type="primary"
              @click.stop="markAsRead(message.id)"
            >
              标为已读
            </el-button>
            <el-button size="small" type="danger" plain @click.stop="deleteMessage(message.id)">
              删除
            </el-button>
          </div>
        </div>

        <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" :title="selectedMessage?.title" width="600px">
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
          @click="markAsRead(selectedMessage.id)"
        >
          标为已读
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Bell, Calendar, Star, Setting, UserFilled } from '@element-plus/icons-vue'

// 消息列表
const messages = ref([])
const filteredMessages = ref([])

// 筛选条件
const activeFilter = ref('all')

// 消息详情对话框
const detailDialogVisible = ref(false)
const selectedMessage = ref(null)

// 是否有未读消息
const hasUnread = computed(() => {
  return messages.value.some((msg) => !msg.read)
})

// 获取消息图标
const getMessageIcon = (type) => {
  const iconMap = {
    system: Setting,
    appointment: Calendar,
    evaluation: Star,
    user: UserFilled,
  }
  return iconMap[type] || Bell
}

// 获取消息类型颜色
const getMessageTypeColor = (type) => {
  const colorMap = {
    system: 'info',
    appointment: 'warning',
    evaluation: 'success',
    user: 'primary',
  }
  return colorMap[type] || 'info'
}

// 获取消息类型文本
const getMessageTypeText = (type) => {
  const textMap = {
    system: '系统通知',
    appointment: '预约相关',
    evaluation: '评价反馈',
    user: '用户消息',
  }
  return textMap[type] || '其他'
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${month}-${day} ${hours}:${minutes}`
}

const formatDetailTime = (time) => {
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 筛选消息
const filterMessages = () => {
  if (activeFilter.value === 'all') {
    filteredMessages.value = messages.value
  } else if (activeFilter.value === 'unread') {
    filteredMessages.value = messages.value.filter((msg) => !msg.read)
  } else {
    filteredMessages.value = messages.value.filter((msg) => msg.type === activeFilter.value)
  }
}

// 处理消息点击
const handleMessageClick = (message) => {
  selectedMessage.value = message
  detailDialogVisible.value = true

  // 如果是未读消息，自动标为已读
  if (!message.read) {
    markAsRead(message.id)
  }
}

// 标记为已读
const markAsRead = (messageId) => {
  const message = messages.value.find((msg) => msg.id === messageId)
  if (message) {
    message.read = true
    filterMessages()
    ElMessage.success('已标为已读')
  }
}

// 全部标为已读
const markAllRead = async () => {
  try {
    await ElMessageBox.confirm('确定要将所有消息标为已读吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    messages.value.forEach((msg) => {
      msg.read = true
    })

    filterMessages()
    ElMessage.success('所有消息已标为已读')
  } catch {
    // 用户取消
  }
}

// 删除消息
const deleteMessage = async (messageId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const index = messages.value.findIndex((msg) => msg.id === messageId)
    if (index > -1) {
      messages.value.splice(index, 1)
      filterMessages()
      ElMessage.success('消息已删除')
    }
  } catch {
    // 用户取消
  }
}

// 清空所有消息
const clearAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有消息吗？此操作不可恢复！', '确认清空', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    messages.value = []
    filteredMessages.value = []
    ElMessage.success('所有消息已清空')
  } catch {
    // 用户取消
  }
}

// 获取消息数据
const fetchMessages = async () => {
  // 模拟数据
  messages.value = [
    {
      id: 1,
      type: 'appointment',
      title: '课程预约确认',
      summary: '您预约的明天14:00-15:00课程已被教练确认',
      content:
        '<p>亲爱的学员，您好！</p><p>您预约的明天14:00-15:00的乒乓球训练课程已被张教练确认。</p><p>训练地点：球台3</p><p>请准时参加训练。</p>',
      from: '张教练',
      createdAt: new Date(Date.now() - 30 * 60 * 1000),
      read: false,
      avatar: '',
    },
    {
      id: 2,
      type: 'evaluation',
      title: '收到新的课程评价',
      summary: '学员小明给您的课程评价了5星',
      content:
        '<p>学员小明对您的课程给出了5星评价：</p><p>"老师教学很认真，技术指导很到位，进步很大！"</p>',
      from: '小明',
      createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000),
      read: true,
      avatar: '',
    },
    {
      id: 3,
      type: 'system',
      title: '系统维护通知',
      summary: '系统将于本周日凌晨2:00-4:00进行维护升级',
      content:
        '<p>尊敬的用户，您好！</p><p>为了提供更好的服务，系统将于本周日（9月8日）凌晨2:00-4:00进行维护升级。</p><p>维护期间可能影响部分功能的正常使用，给您带来的不便敬请谅解。</p>',
      from: null,
      createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000),
      read: false,
      avatar: '',
    },
    {
      id: 4,
      type: 'appointment',
      title: '课程取消通知',
      summary: '由于教练临时有事，今天的课程已取消',
      content:
        '<p>抱歉通知您，由于教练临时有急事，今天16:00-17:00的课程需要取消。</p><p>我们会尽快为您重新安排时间，给您带来的不便深表歉意。</p>',
      from: '李教练',
      createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
      read: true,
      avatar: '',
    },
  ]

  filterMessages()
}

onMounted(() => {
  fetchMessages()
})
</script>

<style scoped>
.messages-view {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #667eea 100%);
  min-height: 100vh;
  padding: 24px;
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

/* 消息图标装饰 */
.message-icon {
  position: absolute;
  font-size: 55px;
  opacity: 0.08;
  animation: float 9s ease-in-out infinite;
}

.mail-icon {
  top: 20%;
  left: 8%;
  animation-delay: 0s;
}

.chat-icon {
  top: 70%;
  right: 10%;
  animation-delay: 2s;
}

.bell-icon {
  bottom: 25%;
  left: 12%;
  animation-delay: 4s;
}

.envelope-icon {
  top: 40%;
  left: 5%;
  animation-delay: 6s;
}

/* 通信装饰 */
.communication-decoration {
  position: absolute;
  font-size: 60px;
  opacity: 0.06;
  animation: rotate 18s linear infinite;
}

.signal-icon {
  top: 15%;
  right: 15%;
  animation-delay: 1s;
}

.connect-icon {
  bottom: 30%;
  right: 8%;
  animation-delay: 3s;
}

.globe-icon {
  top: 60%;
  left: 15%;
  animation-delay: 5s;
}

/* 艺术字装饰 */
.art-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  opacity: 0.12;
  z-index: 1;
  pointer-events: none;
  user-select: none;
}

.art-text-main {
  font-size: 110px;
  font-weight: 900;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 12px;
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.3);
  font-family: 'Arial Black', sans-serif;
  -webkit-text-stroke: 1px rgba(255, 255, 255, 0.2);
}

.art-text-sub {
  font-size: 42px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 8px;
  margin-top: -15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

/* 几何装饰 */
.geometric-decoration {
  position: absolute;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.geo-1 {
  top: 25%;
  left: 3%;
  width: 90px;
  height: 90px;
  animation: pulse 6s ease-in-out infinite;
}

.geo-2 {
  bottom: 40%;
  right: 5%;
  width: 70px;
  height: 70px;
  animation: pulse 8s ease-in-out infinite;
  animation-delay: 2s;
}

.geo-3 {
  top: 55%;
  right: 12%;
  width: 50px;
  height: 50px;
  animation: pulse 10s ease-in-out infinite;
  animation-delay: 4s;
}

.main-card {
  max-width: 1000px;
  margin: 0 auto;
  border: none;
  border-radius: 20px;
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);
  box-shadow:
    0 10px 40px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.25) inset;
  position: relative;
  z-index: 10;
  overflow: hidden;
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
  color: #333;
}

.header-title .icon {
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.message-filters {
  margin-bottom: 24px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.message-list {
  max-height: 600px;
  overflow-y: auto;
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

.message-item:last-child {
  border-bottom: none;
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

.message-title {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.message-time {
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
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-from {
  font-size: 12px;
  color: #999;
}

.message-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  white-space: nowrap;
}

.message-detail {
  line-height: 1.6;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
  font-size: 14px;
  color: #666;
}

.detail-content {
  color: #333;
}

.detail-content :deep(p) {
  margin: 8px 0;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }

  50% {
    transform: translateY(-12px) rotate(2deg);
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

@keyframes pulse {
  0%,
  100% {
    opacity: 0.06;
    transform: scale(1);
  }

  50% {
    opacity: 0.12;
    transform: scale(1.1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-view {
    padding: 16px;
  }

  .art-text-main {
    font-size: 70px;
  }

  .art-text-sub {
    font-size: 28px;
  }

  .message-icon,
  .communication-decoration {
    font-size: 35px;
  }

  .message-item {
    padding: 16px;
  }

  .header-title {
    font-size: 16px;
  }
}
</style>
