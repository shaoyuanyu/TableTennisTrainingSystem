import api from '@/utils/api'

/**
 * 学员课程表相关API
 */

// 获取学员课程表列表
export const getStudentCourses = async (params = {}) => {
  try {
    const response = await api.get('/student/courses', { params })
    return response.data
  } catch (error) {
    throw error
  }
}

// 获取学员课程详情
export const getStudentCourseDetail = async (courseId) => {
  try {
    const response = await api.get(`/student/courses/${courseId}`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 学员报名课程
export const enrollCourse = async (courseId, data = {}) => {
  try {
    const response = await api.post(`/student/courses/${courseId}/enroll`, data)
    return response.data
  } catch (error) {
    throw error
  }
}

// 学员取消课程
export const cancelEnrollment = async (courseId) => {
  try {
    const response = await api.delete(`/student/courses/${courseId}/enroll`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 学员课程反馈
export const submitCourseFeedback = async (courseId, feedback) => {
  try {
    const response = await api.put(`/student/courses/${courseId}/feedback`, feedback)
    return response.data
  } catch (error) {
    throw error
  }
}

/**
 * 教练课程表相关API
 */

// 获取教练课程表列表
export const getCoachCourses = async (params = {}) => {
  try {
    const response = await api.get('/coach/courses', { params })
    return response.data
  } catch (error) {
    throw error
  }
}

// 获取教练课程详情
export const getCoachCourseDetail = async (courseId) => {
  try {
    const response = await api.get(`/coach/courses/${courseId}`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 创建新课程
export const createCourse = async (courseData) => {
  try {
    const response = await api.post('/coach/courses', courseData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 更新课程信息
export const updateCourse = async (courseId, courseData) => {
  try {
    const response = await api.put(`/coach/courses/${courseId}`, courseData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 删除课程
export const deleteCourse = async (courseId) => {
  try {
    const response = await api.delete(`/coach/courses/${courseId}`)
    return response.data
  } catch (error) {
    throw error
  }
}

// 更新课程出勤
export const updateCourseAttendance = async (courseId, attendanceData) => {
  try {
    const response = await api.put(`/coach/courses/${courseId}/attendance`, attendanceData)
    return response.data
  } catch (error) {
    throw error
  }
}

/**
 * 通用查询API
 */

// 获取可用时间段
export const getAvailableTimeSlots = async (params) => {
  try {
    const response = await api.get('/courses/available-slots', { params })
    return response.data
  } catch (error) {
    throw error
  }
}

// 获取课程统计信息
export const getCourseStatistics = async () => {
  try {
    const response = await api.get('/courses/statistics')
    return response.data
  } catch (error) {
    throw error
  }
}

// 获取教练列表（用于课程筛选）
export const getCoachesList = async (campusId = null) => {
  try {
    const params = campusId ? { campusId } : {}
    const response = await api.get('/coaches', { params })
    return response.data
  } catch (error) {
    throw error
  }
}

// 获取校区列表
export const getCampusList = async () => {
  try {
    const response = await api.get('/campus/names')
    return response.data
  } catch (error) {
    throw error
  }
}

/**
 * 课程表导出和同步功能
 */

// 导出课程表为iCal格式
export const exportScheduleAsIcal = async (params = {}) => {
  try {
    const response = await api.get('/courses/export/ical', { 
      params,
      responseType: 'blob'
    })
    return response.data
  } catch (error) {
    throw error
  }
}

// 发送课程表邮件
export const sendScheduleEmail = async (emailData) => {
  try {
    const response = await api.post('/courses/send-email', emailData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 同步到外部日历
export const syncToExternalCalendar = async (syncData) => {
  try {
    const response = await api.post('/courses/sync-calendar', syncData)
    return response.data
  } catch (error) {
    throw error
  }
}