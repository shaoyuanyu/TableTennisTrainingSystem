import api from '@/utils/api'

// 用户注册
export const registerUser = async (userData) => {
  const response = await api.post('/user/signup', userData)
  return response.data
}

// 获取校区列表
export const getCampusList = async () => {
  const response = await api.get('/campus/names')
  return response.data.campuses || []
}
