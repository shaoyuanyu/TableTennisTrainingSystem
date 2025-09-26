// 工具函数：课程预约页面-教练筛选与等级显示

/**
 * 规范化教练等级，兼容“初级教练/初级”等写法
 * @param {string} level
 * @returns {string} 初级|中级|高级|""
 */
export const normalizeLevel = (level) => {
  if (!level || typeof level !== 'string') return ''
  const s = level.replace('教练', '')
  if (s.includes('初')) return '初级'
  if (s.includes('中')) return '中级'
  if (s.includes('高')) return '高级'
  return ''
}

/**
 * 展示用的等级文案
 * @param {string} level
 * @returns {string}
 */
export const displayLevel = (level) => {
  const n = normalizeLevel(level)
  return n ? `${n}教练` : '未评级'
}

/**
 * 按等级过滤教练列表
 * @param {Array} coaches - 教练数组
 * @param {string} level - 期望等级（初级|中级|高级 或 空）
 * @returns {Array}
 */
export const filterCoachesByLevel = (coaches, level) => {
  if (!level) return coaches || []
  const target = level
  return (coaches || []).filter((c) => normalizeLevel(c.level) === target)
}
