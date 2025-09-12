// 调试工具
const debugSidebar = {
  // 记录侧边栏点击事件
  logMenuClick(menuIndex, menuTitle) {
    console.log(`[DEBUG] Menu clicked: ${menuIndex} - ${menuTitle}`)
    console.log(`[DEBUG] Current route: ${window.location.pathname}`)
    console.log(`[DEBUG] Time: ${new Date().toISOString()}`)
  },

  // 记录路由变化
  logRouteChange(from, to) {
    console.log(`[DEBUG] Route change: ${from.path} -> ${to.path}`)
    console.log(`[DEBUG] Route params:`, to.params)
    console.log(`[DEBUG] Route query:`, to.query)
  },

  // 记录组件加载
  logComponentLoad(componentName) {
    console.log(`[DEBUG] Component loading: ${componentName}`)
    console.log(`[DEBUG] Memory usage:`, performance.memory ? {
      used: Math.round(performance.memory.usedJSHeapSize / 1024 / 1024) + 'MB',
      total: Math.round(performance.memory.totalJSHeapSize / 1024 / 1024) + 'MB',
      limit: Math.round(performance.memory.jsHeapSizeLimit / 1024 / 1024) + 'MB'
    } : 'N/A')
  },

  // 记录错误
  logError(error, context) {
    console.error(`[DEBUG] Error in ${context}:`, error)
    console.error(`[DEBUG] Stack trace:`, error.stack)
  },

  // 性能监控
  startPerfMeasure(name) {
    performance.mark(`${name}-start`)
  },

  endPerfMeasure(name) {
    try {
      performance.mark(`${name}-end`)
      performance.measure(name, `${name}-start`, `${name}-end`)
      const measure = performance.getEntriesByName(name)[0]
      if (measure) {
        console.log(`[DEBUG] Performance ${name}: ${measure.duration.toFixed(2)}ms`)
      }
    } catch (error) {
      console.warn(`[DEBUG] Performance measure failed for ${name}:`, error)
    }
  }
}

// 全局错误处理
window.addEventListener('error', (event) => {
  debugSidebar.logError(event.error, 'Window Error')
})

window.addEventListener('unhandledrejection', (event) => {
  debugSidebar.logError(event.reason, 'Unhandled Promise Rejection')
})

// 导出给全局使用
window.debugSidebar = debugSidebar

export { debugSidebar }
