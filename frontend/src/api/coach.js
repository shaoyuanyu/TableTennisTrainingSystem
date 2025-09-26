import api from '@/utils/api'

// 获取教练的统计数据
export const getCoachStats = async () => {
  try {
    const response = await api.get('/coach/stats')
    return response.data
  } catch (error) {
    console.error('获取教练统计数据失败:', error)
    throw error
  }
}

// 获取教练今日课程安排
export const getTodayClasses = async () => {
  try {
    const response = await api.get('/courses/coach-schedule')
    return response.data
  } catch (error) {
    console.error('获取教练课程安排失败:', error)
    throw error
  }
}

// 获取教练当前学生列表
export const getCoachStudents = async () => {
  try {
    const response = await api.get('/mutual-selection/coach/current-students')
    return response.data
  } catch (error) {
    console.error('获取教练当前学生列表失败:', error)
    throw error
  }
}

// 获取教练待处理的双选申请
export const getPendingApplications = async () => {
  try {
    const response = await api.get('/mutual-selection/coach-applications', {
      params: { status: 'PENDING' }
    })
    return response.data
  } catch (error) {
    console.error('获取教练待处理的双选申请失败:', error)
    throw error
  }
}

// 获取教练待处理的课程预约
export const getPendingAppointments = async () => {
  try {
    const response = await api.get('/courses/querypending')
    return response.data
  } catch (error) {
    console.error('获取教练待处理的课程预约失败:', error)
    throw error
  }
}