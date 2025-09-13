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
    const campuses = response.data.first || []
    
    // 将API返回的数据格式转换为前端需要的格式
    // API返回: { id, campusName } -> 前端需要: { id, name }
    return campuses.map(campus => ({
      id: campus.id,
      name: campus.campusName
    }))
  } catch (error) {
    throw error
  }
}