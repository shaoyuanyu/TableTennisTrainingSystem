import api from '@/utils/api'

/**
 * 双选系统相关API
 * 注意：后端 Ktor 对以下端点使用 call.receiveParameters() 解析：
 * /apply, /withdraw, /cancel-approved, /review, /admin-create, /admin/relation/{id}
 * 因此前端需以 multipart/form-data（或 application/x-www-form-urlencoded）提交，而不能直接用 JSON。
 * 若未来后端改为接收 JSON (call.receive<DTO>()), 可去掉 FormData 写法。
 */

// 学生申请教练
export const applyForCoach = async (coachId) => {
  const formData = new FormData()
  formData.append('coachId', coachId)
  const response = await api.post('/mutual-selection/apply', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

// 学生获取自己的申请记录
export const getStudentApplications = async (params = {}) => {
  const response = await api.get('/mutual-selection/student-applications', { params })
  return response.data
}

// 学生撤回申请
export const withdrawApplication = async (relationId) => {
  const formData = new FormData()
  formData.append('relationId', relationId)
  const response = await api.post('/mutual-selection/withdraw', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

// 学生取消已批准的关系
export const cancelApprovedRelation = async (relationId) => {
  const formData = new FormData()
  formData.append('relationId', relationId)
  const response = await api.post('/mutual-selection/cancel-approved', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

// 教练获取收到的申请记录
export const getCoachApplications = async (params = {}) => {
  const response = await api.get('/mutual-selection/coach-applications', { params })
  return response.data
}

// 教练审核学生申请
export const reviewApplication = async (relationId, decision, rejectionReason = null) => {
  // 使用FormData发送数据，以匹配后端的receiveParameters()期望
  const formData = new FormData()
  formData.append('selectionId', relationId)
  // 后端期望的是approve参数（布尔值），而不是decision参数
  formData.append('approve', decision === 'APPROVED')
  if (rejectionReason) {
    formData.append('rejectionReason', rejectionReason)
  }
  const response = await api.post('/mutual-selection/review', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

// 教练获取当前学生列表
export const getCoachCurrentStudents = async () => {
  const response = await api.get('/mutual-selection/coach/current-students')
  return response.data
}

// 学生获取当前教练列表
export const getStudentCurrentCoaches = async () => {
  const response = await api.get('/mutual-selection/student/current-coaches')
  return response.data
}

// 教练获取历史学生列表
export const getCoachHistoricalStudents = async () => {
  const response = await api.get('/mutual-selection/coach/historical-students')
  return response.data
}

// 获取教练待处理申请数量（暂未使用专门接口）
export const getPendingApplicationCount = async () => {
  const response = await api.get('/mutual-selection/coach-applications')
  return response.data
}

// 管理员获取所有关系记录
export const getAllRelations = async (params = {}) => {
  const response = await api.get('/mutual-selection/all', { params })
  return response.data
}

// 管理员根据ID获取关系详情
export const getRelationById = async (relationId) => {
  const response = await api.get(`/mutual-selection/admin/relation/${relationId}`)
  return response.data
}

// 管理员更新关系信息
export const updateRelation = async (relationId, status) => {
  const formData = new FormData()
  if (status) formData.append('status', status)
  const response = await api.post(`/mutual-selection/admin/relation/${relationId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

// 管理员直接建立关系
export const adminCreateRelation = async (studentId, coachId) => {
  const formData = new FormData()
  formData.append('studentId', studentId)
  formData.append('coachId', coachId)
  const response = await api.post('/mutual-selection/admin-create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}