<template>
  <div v-if="showMonitor" class="performance-monitor">
    <div class="monitor-header">
      <span>性能监控</span>
      <el-button @click="toggleMonitor" size="small" type="text">
        {{ showDetails ? '隐藏' : '显示' }}
      </el-button>
      <el-button @click="closeMonitor" size="small" type="text">✕</el-button>
    </div>
    
    <div v-if="showDetails" class="monitor-content">
      <div class="metric">
        <span class="label">FPS:</span>
        <span class="value" :class="getFpsClass()">{{ fps }}</span>
      </div>
      
      <div class="metric">
        <span class="label">内存:</span>
        <span class="value">{{ memoryUsage }}MB</span>
      </div>
      
      <div class="metric">
        <span class="label">INP:</span>
        <span class="value" :class="getInpClass()">{{ inp }}ms</span>
      </div>
      
      <div class="metric">
        <span class="label">组件数:</span>
        <span class="value">{{ componentCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const showMonitor = ref(false)
const showDetails = ref(true)
const fps = ref(60)
const memoryUsage = ref(0)
const inp = ref(0)
const componentCount = ref(0)

let animationId = null
let lastTime = 0
let frameCount = 0
let performanceObserver = null

// 切换监控器可见性
const toggleMonitor = () => {
  showDetails.value = !showDetails.value
}

// 关闭监控器
const closeMonitor = () => {
  showMonitor.value = false
}

// 获取FPS颜色类
const getFpsClass = () => {
  if (fps.value >= 50) return 'good'
  if (fps.value >= 30) return 'warning'
  return 'bad'
}

// 获取INP颜色类
const getInpClass = () => {
  if (inp.value <= 200) return 'good'
  if (inp.value <= 500) return 'warning'
  return 'bad'
}

// 计算FPS
const updateFPS = (currentTime) => {
  frameCount++
  
  if (currentTime - lastTime >= 1000) {
    fps.value = Math.round((frameCount * 1000) / (currentTime - lastTime))
    frameCount = 0
    lastTime = currentTime
  }
  
  animationId = requestAnimationFrame(updateFPS)
}

// 更新内存使用情况
const updateMemoryUsage = () => {
  if (performance.memory) {
    memoryUsage.value = Math.round(performance.memory.usedJSHeapSize / 1024 / 1024)
  }
}

// 监控Interaction to Next Paint (INP)
const monitorINP = () => {
  if ('PerformanceObserver' in window) {
    try {
      performanceObserver = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        for (const entry of entries) {
          if (entry.entryType === 'event') {
            const duration = entry.processingEnd - entry.processingStart
            if (duration > inp.value) {
              inp.value = Math.round(duration)
            }
          }
        }
      })
      
      performanceObserver.observe({ 
        type: 'event',
        buffered: true 
      })
    } catch (error) {
      // 降级处理
      console.warn('PerformanceObserver 不支持事件类型', error)
    }
  }
}

// 计算组件数量（减少频率）
const updateComponentCount = () => {
  try {
    // 简化计算，减少性能开销
    componentCount.value = document.querySelectorAll('.el-card, .el-menu-item').length
  } catch {
    // 降级处理：使用固定值避免错误
    componentCount.value = 10
  }
}

// 定期更新性能指标（降低频率）
const updateMetrics = () => {
  updateMemoryUsage()
  // 减少组件计数频率
  if (Math.random() > 0.7) {
    updateComponentCount()
  }
}

onMounted(() => {
  // 仅在开发环境或手动启用时显示
  const isDev = import.meta.env.DEV
  const forceShow = localStorage.getItem('showPerformanceMonitor') === 'true'
  
  if (isDev || forceShow) {
    showMonitor.value = true
    
    // 启动FPS监控
    lastTime = performance.now()
    updateFPS(lastTime)
    
    // 启动INP监控
    monitorINP()
    
    // 定期更新其他指标（降低频率到2秒）
    const interval = setInterval(updateMetrics, 2000)
    
    onBeforeUnmount(() => {
      clearInterval(interval)
    })
  }
})

onBeforeUnmount(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  
  if (performanceObserver) {
    performanceObserver.disconnect()
  }
})

// 暴露给全局，方便调试
if (import.meta.env.DEV) {
  window.togglePerformanceMonitor = () => {
    showMonitor.value = !showMonitor.value
    localStorage.setItem('showPerformanceMonitor', showMonitor.value)
  }
}
</script>

<style scoped>
.performance-monitor {
  position: fixed;
  top: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 12px;
  border-radius: 8px;
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 12px;
  z-index: 9999;
  min-width: 160px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: bold;
}

.monitor-header .el-button {
  color: #999;
  padding: 0;
  font-size: 11px;
}

.monitor-header .el-button:hover {
  color: white;
}

.monitor-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  color: #ccc;
}

.value {
  font-weight: bold;
}

.value.good {
  color: #4CAF50;
}

.value.warning {
  color: #FF9800;
}

.value.bad {
  color: #F44336;
}
</style>
