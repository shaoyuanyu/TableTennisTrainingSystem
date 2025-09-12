import api from '@/utils/api'

// 获取消息列表
export const fetchMessages = (params) => {
  return api.get('/messages', { params })
}

// 标记单条消息为已读
export const markMessageAsRead = (id) => {
  return api.post(`/messages/${id}/read`)
}

// 标记全部消息为已读
export const markAllMessagesAsRead = () => {
  return api.post('/messages/read-all')
}

// 删除单条消息
export const deleteMessage = (id) => {
  return api.delete(`/messages/${id}`)
}

// 清空所有消息
export const clearAllMessages = () => {
  return api.delete('/messages/clear')
}
