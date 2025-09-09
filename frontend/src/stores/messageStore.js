import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

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
  const fetchMessages = async () => {
    messages.value = await mockFetchMessages()
  }

  const markAsRead = async (id) => {
    const message = messages.value.find(msg => msg.id === id)
    if (message) message.read = true
  }

  const markAllRead = async () => {
    messages.value.forEach(msg => { msg.read = true })
  }

  const deleteMessage = async (id) => {
    messages.value = messages.value.filter(msg => msg.id !== id)
    if (selectedMessage.value?.id === id) {
      selectedMessage.value = null
    }
  }

  const clearAll = async () => {
    messages.value = []
    selectedMessage.value = null
  }

  // 模拟 API
  const mockFetchMessages = async () => {
    return new Promise(resolve => {
      setTimeout(() => {
        resolve([
          // 这里放你的消息数据，与之前相同
          {
            id: 1,
            type: 'system',
            title: '系统通知',
            summary: '欢迎使用乒乓球训练系统',
            content: '<p>欢迎使用乒乓球训练系统！</p><p>系统将于每周日凌晨2:00-4:00进行例行维护。</p>',
            from: '系统管理员',
            createdAt: new Date(),
            read: false,
            avatar: ''
          },
          {
            id: 2,
            type: 'appointment',
            title: '预约成功',
            summary: '您的课程预约已成功',
            content: '<p>您已成功预约明天14:00-15:00的乒乓球训练课程</p>',
            from: '预约系统',
            createdAt: new Date(Date.now() - 3600000),
            read: false,
            avatar: ''
          },
          {
            id: 3,
            type: 'evaluation',
            title: '新评价',
            summary: '您收到一条新的课程评价',
            content: '<p>学员张三给您的课程打了5星好评！</p>',
            from: '张三',
            createdAt: new Date(Date.now() - 86400000),
            read: false,
            avatar: ''
          },
          {
            id: 4,
            type: 'system',
            title: '系统更新通知',
            summary: '系统已更新至最新版本',
            content: '<p>系统已更新至v2.1.0版本，新增以下功能：</p><ul><li>消息中心优化</li><li>预约流程简化</li><li>性能提升</li></ul>',
            from: '系统管理员',
            createdAt: new Date(Date.now() - 172800000),
            read: false,
            avatar: ''
          },
          {
            id: 5,
            type: 'reminder',
            title: '课程取消通知',
            summary: '由于教练临时有事，今天的课程已取消',
            content: '<p>抱歉通知您，由于教练临时有急事，今天16:00-17:00的课程需要取消。</p><p>我们会尽快为您重新安排时间，给您带来的不便深表歉意。</p>',
            from: '李教练',
            createdAt: new Date(Date.now() - 259200000),
            read: false,
            avatar: ''
          },
           {
            id: 6,
            type: 'system',
            title: '新教练申请',
            summary: '有新的教练申请需要审核',
            content: `
              <p>尊敬的管理员：</p>
              <p>您有1条新的教练申请需要审核：</p>
              <ul>
                <li>申请人：李教练</li>
                <li>申请时间：${new Date().toLocaleString()}</li>
                <li>资质：国家一级运动员</li>
              </ul>
              <p>请及时处理。</p>
            `,
            from: '教练管理系统',
            createdAt: new Date(Date.now() - 7200000), // 2小时前
            read: false,
            avatar: ''
          },
          {
            id: 7,
            type: 'system',
            title: '操作日志变更',
            summary: '系统检测到关键配置被修改',
            content: `
              <p>安全提醒：</p>
              <p>系统检测到以下关键配置被修改：</p>
              <table border="1" style="width: 100%; border-collapse: collapse;">
                <tr>
                  <th style="padding: 8px;">操作类型</th>
                  <th style="padding: 8px;">操作内容</th>
                  <th style="padding: 8px;">操作人</th>
                </tr>
                <tr>
                  <td style="padding: 8px;">权限变更</td>
                  <td style="padding: 8px;">用户权限升级</td>
                  <td style="padding: 8px;">admin</td>
                </tr>
              </table>
              <p>如果不是您本人操作，请立即检查账户安全。</p>
            `,
            from: '系统安全中心',
            createdAt: new Date(Date.now() - 1800000), // 30分钟前
            read: false,
            avatar: ''
          },
          {
            id: 8,
            type: 'event',
            title: '月度乒乓球比赛通知',
            summary: '本月校园乒乓球联赛即将开始报名',
            content: `
              <p>亲爱的学员：</p>
              <p>本月校园乒乓球联赛即将开始，详情如下：</p>
              <ul>
                <li>比赛时间：${new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toLocaleDateString()}</li>
                <li>比赛地点：学校体育馆</li>
                <li>报名截止：${new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toLocaleDateString()}</li>
              </ul>
              <p>点击 <a href="/events/register" style="color: #409EFF;">此处</a> 立即报名</p>
            `,
            from: '校园体育部',
            createdAt: new Date(Date.now() - 86400000), // 1天前
            read: false,
            avatar: ''
          },
           {
            id: 9,
            type: 'reminder',
            title: '课程开始提醒',
            summary: '您预约的乒乓球课程即将开始',
            content: `
              <p>上课提醒：</p>
              <p>您预约的课程即将开始：</p>
              <ul>
                <li>课程时间：今天 14:00-15:00</li>
                <li>教练：张教练</li>
                <li>地点：球台3</li>
              </ul>
              <p>请提前10分钟到场准备。</p>
              <p>如无法参加，请及时 <a href="/cancel" style="color: #F56C6C;">取消预约</a></p>
            `,
            from: '课程预约系统',
            createdAt: new Date(),
            read: false,
            avatar: ''
          }
        ])
      }, 500)
    })
  }

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