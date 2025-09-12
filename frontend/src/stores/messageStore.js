
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  fetchMessages as apiFetchMessages,
  markMessageAsRead,
  markAllMessagesAsRead,
  deleteMessage as apiDeleteMessage,
  clearAllMessages
} from '@/api/message'

export const useMessageStore = defineStore('messages', () => {
  // 状态
  const messages = ref([])
  const activeFilter = ref('all')
  const searchQuery = ref('')
  const currentPage = ref(1)
  const pageSize = ref(5)
  const detailDialogVisible = ref(false)
  const selectedMessage = ref(null)

  // 计算属性
  const unreadCount = computed(() => messages.value.filter(msg => !msg.read).length)
  const hasUnread = computed(() => unreadCount.value > 0)
  const filteredMessages = computed(() => {
    let result = [...messages.value]
    if (activeFilter.value !== 'all') {
      result = activeFilter.value === 'unread' 
        ? result.filter(msg => !msg.read)
        : result.filter(msg => msg.type === activeFilter.value)
    }
    if (searchQuery.value) {
      const query = searchQuery.value.toLowerCase()
      result = result.filter(msg => 
        msg.title.toLowerCase().includes(query) || 
        msg.summary.toLowerCase().includes(query)
      )
    }
    return result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  })
  const totalMessages = computed(() => filteredMessages.value.length)
  const paginatedMessages = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return filteredMessages.value.slice(start, start + pageSize.value)
  })

  // 方法

  // 获取消息列表
  const fetchMessages = async (params = {}) => {
    const res = await apiFetchMessages(params)
    // 假设后端返回 { data: [...] }
    messages.value = res.data || []
  }


  // 标记单条消息为已读
  const markAsRead = async (id) => {
    await markMessageAsRead(id)
    const message = messages.value.find(msg => msg.id === id)
    if (message) message.read = true
  }


  // 标记全部为已读
  const markAllRead = async () => {
    await markAllMessagesAsRead()
    messages.value.forEach(msg => { msg.read = true })
  }


  // 删除单条消息
  const deleteMessage = async (id) => {
    await apiDeleteMessage(id)
    messages.value = messages.value.filter(msg => msg.id !== id)
    if (selectedMessage.value?.id === id) {
      selectedMessage.value = null
    }
  }


  // 清空所有消息
  const clearAll = async () => {
    await clearAllMessages()
    messages.value = []
    selectedMessage.value = null
  }


  // 已移除 mockFetchMessages，全部走 API

  return {
    messages,
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
    fetchMessages,
    markAsRead,
    markAllRead,
    deleteMessage,
    clearAll
  }
})