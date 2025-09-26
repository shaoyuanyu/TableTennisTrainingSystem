import api from '@/utils/api'

/**
 * 钱包相关API
 */

// 获取钱包余额
export const getWalletBalance = async () => {
  const response = await api.get('/wallet/balance')
  return response.data
}

// 获取充值记录
export const getRechargeHistory = async (params = {}) => {
  const response = await api.get('/wallet/recharge/history', { params })
  return response.data
}

// 充值钱包
export const rechargeWallet = async (amount) => {
  const formData = new FormData()
  formData.append('amount', amount)
  const response = await api.post('/wallet/recharge', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}