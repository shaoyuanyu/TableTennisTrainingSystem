import api from '@/utils/api'
import {useUserStore} from '@/stores/user'

/**
 * 课程表相关API
 *
 * 根据后端实际路由代码：
 * - 学生使用：/courses/my-schedule (在学生权限组中)
 * - 教练使用：/courses/coach-schedule (在教练权限组中)
 */

// 获取学生课程安排
export const getStudentSchedule = async (params = {}) => {
  const response = await api.get('/courses/my-schedule', { params })
  return response.data
}

// 获取教练课程安排
export const getCoachSchedule = async (params = {}) => {
  const response = await api.get('/courses/coach-schedule', { params })
  return response.data
}

// 获取当前用户的课程安排（自动检测角色）
export const getMySchedule = async (params = {}) => {
  const userStore = useUserStore()
  const userRole = userStore.user?.role

  // 根据角色选择不同的端点
  let endpoint = '/courses/my-schedule' // 默认学生端点
  if (userRole === 'coach') {
    endpoint = '/courses/coach-schedule'
  }

  if (import.meta.env?.DEV) {
    console.log(`[courses.js] getMySchedule - 用户角色: ${userRole}, 使用端点: ${endpoint}`)
    console.log(`[courses.js] getMySchedule - 请求参数:`, params)
  }

  const response = await api.get(endpoint, { params })
  return response.data
}

// 学生查看指定教练的时间安排（用于预约课程）
export const getCoachScheduleForStudent = async (coachId, params = {}) => {
  const response = await api.get(`/courses/coach-schedule/${coachId}`, { params })
  return response.data
}

// 学生提交课程反馈
export const submitStudentFeedback = async (feedbackData) => {
  const response = await api.post('/courses/feedback', feedbackData)
  return response.data
}

// 教练更新课程状态
export const updateCourseStatus = async (courseId, status) => {
  const response = await api.put(`/courses/${courseId}/status`, null, {
    params: { status }
  })
  return response.data
}

// 创建课程（管理员权限）
export const createCourse = async (courseData) => {
  const response = await api.post('/courses/create', courseData)
  return response.data
}

// 查询课程（管理员权限）
export const queryCourses = async (queryData) => {
  const response = await api.post('/courses/query', queryData)
  return response.data
}

/**
 * 其他课程相关API（如果需要的话）
 */

// 获取可用时间段
export const getAvailableTimeSlots = async (params) => {
  const response = await api.get('/courses/available-slots', { params })
  return response.data
}

// 获取课程统计信息
export const getCourseStatistics = async () => {
  const response = await api.get('/courses/statistics')
  return response.data
}

// 导出课程表为iCal格式
export const exportScheduleAsIcal = async (params = {}) => {
  const response = await api.get('/courses/export/ical', {
    params,
    responseType: 'blob'
  })
  return response.data
}

// 发送课程表邮件
export const sendScheduleEmail = async (emailData) => {
  const response = await api.post('/courses/send-email', emailData)
  return response.data
}

// 同步到外部日历
export const syncToExternalCalendar = async (syncData) => {
  const response = await api.post('/courses/sync-calendar', syncData)
  return response.data
}

// 获取当前时间段的可用球桌
export const getAvailableTables = async (params) => {
  const response = await api.get('/courses/available-tables', { params })
  return response.data
}

// 获取我的预约列表
export const getMyAppointments = async (params = {}) => {
  const response = await api.get('/courses/my-ordered-courses', { params })
  return response.data
}


