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
    // 暂时使用模拟数据，待后端API开发完成后替换
    // const response = await api.get('/campuses')
    // return response.data
    
    // 模拟校区数据
    return [
      { id: 1, name: '北京总部校区' },
      { id: 2, name: '上海浦东校区' },
      { id: 3, name: '广州天河校区' },
      { id: 4, name: '深圳南山校区' },
      { id: 5, name: '杭州西湖校区' }
    ]
  } catch (error) {
    throw error
  }
}