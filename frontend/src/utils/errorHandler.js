/**
 * 错误处理工具函数
 */

/**
 * 从后端响应中提取错误消息
 * @param {any} data - 后端返回的数据
 * @param {string} fallback - 默认错误消息
 * @returns {string} 提取的错误消息
 */
export const getErrorMessage = (data, fallback = '操作失败') => {
  // 如果data本身就是字符串，直接返回
  if (typeof data === 'string' && data.trim()) {
    return data.trim()
  }

  // 尝试从对象中提取错误消息，支持多种常见字段名
  if (data && typeof data === 'object') {
    // 优先级：message > error > msg > details > description
    const errorFields = ['message', 'error', 'msg', 'details', 'description']

    for (const field of errorFields) {
      if (data[field] && typeof data[field] === 'string' && data[field].trim()) {
        return data[field].trim()
      }
    }

    // 如果有errors数组，取第一个错误
    if (Array.isArray(data.errors) && data.errors.length > 0) {
      const firstError = data.errors[0]
      if (typeof firstError === 'string') {
        return firstError.trim()
      }
      if (firstError && typeof firstError === 'object') {
        return getErrorMessage(firstError, fallback)
      }
    }
  }

  return fallback
}

/**
 * 根据HTTP状态码获取通用错误消息
 * @param {number} status - HTTP状态码
 * @param {any} data - 后端返回的数据
 * @returns {string} 错误消息
 */
export const getStatusErrorMessage = (status, data) => {
  const statusMessages = {
    400: '请求参数错误',
    401: '认证失败',
    403: '权限不足',
    404: '资源不存在',
    409: '数据冲突',
    422: '请求参数错误',
    429: '请求过于频繁',
    500: '服务器内部错误',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时',
  }

  const fallback = statusMessages[status] || `请求失败 (${status})`
  return getErrorMessage(data, fallback)
}

/**
 * 处理网络错误
 * @param {Error} error - 错误对象
 * @returns {string} 错误消息
 */
export const getNetworkErrorMessage = (error) => {
  if (error.code === 'NETWORK_ERROR' || error.message === 'Network Error') {
    return '网络连接失败，请检查网络状态'
  }

  if (error.code === 'ECONNABORTED') {
    return '请求超时，请稍后重试'
  }

  if (error.code === 'ERR_NETWORK') {
    return '网络错误，请检查网络连接'
  }

  return '网络错误，请稍后重试'
}

/**
 * 统一的错误处理函数
 * @param {Error} error - 错误对象
 * @returns {string} 最终的错误消息
 */
export const handleError = (error) => {
  console.error('错误详情:', error)

  if (error.response) {
    // HTTP错误响应
    const { status, data } = error.response
    return getStatusErrorMessage(status, data)
  } else {
    // 网络错误或其他错误
    return getNetworkErrorMessage(error)
  }
}
