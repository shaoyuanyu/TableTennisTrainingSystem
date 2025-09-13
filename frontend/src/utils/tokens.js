// 读取 CSS 自定义属性的工具
export function getCssVar(name, fallback = '') {
  const value = getComputedStyle(document.documentElement).getPropertyValue(name)
  return (value && value.trim()) || fallback
}

export const tokens = {
  primary: () => getCssVar('--primary-color'),
  success: () => getCssVar('--success-color'),
  warning: () => getCssVar('--warning-color'),
  danger: () => getCssVar('--danger-color'),
  info: () => getCssVar('--info-color'),
  gray: (level = 500) => getCssVar(`--gray-${level}`),
  spacing: (name = 'md') => getCssVar(`--spacing-${name}`),
  radius: (name = 'md') => getCssVar(`--radius-${name}`),
}
