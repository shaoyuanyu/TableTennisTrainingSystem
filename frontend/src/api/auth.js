import api from '@/utils/api'

// 用户注册
export const registerUser = async (userData) => {
  try {
    const response = await api.post('/user/signup', userData)
    return response.data
  } catch (error) {
    throw error
  }
}

// 获取校区列表
export const getCampusList = async () => {
  try {
    const response = await api.get('/campus/names')
    return response.data.campuses || []
  } catch (error) {
    throw error
  }
}