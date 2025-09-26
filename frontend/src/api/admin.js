import api from '@/utils/api'

/**
 * 校区管理员相关API
 */

// 获取校区学生列表
export const getCampusStudents = async (params = {}) => {
  const response = await api.get('/admin/students', { params })
  return response.data
}

// 获取校区教练列表
export const getCampusCoaches = async (params = {}) => {
  const response = await api.get('/admin/coaches', { params })
  return response.data
}

// 获取校区充值记录（用于计算营收）
export const getCampusRechargeRecords = async (params = {}) => {
  const response = await api.get('/wallet/recharge/records/campus', { params })
  return response.data
}

// 获取全部校区用户总数
export const getAllCampusTotalUsers = async () => {
  const response = await api.get('/user/allTotalUserNum')
  return response.data
}

// 获取所有用户列表
export const getAllUsers = async (params = {}) => {
  const response = await api.get('/user/allUsers', { params })
  return response.data
}

// 删除用户
export const deleteUser = async (userId) => {
  const response = await api.delete(`/user/${userId}`)
  return response.data
}