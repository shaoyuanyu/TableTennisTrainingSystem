<template>
  <div v-if="showDebugger" class="performance-debugger">
    <div class="debugger-header">
      <h4>性能调试器</h4>
      <el-button size="small" @click="toggleDebugger">{{ showDebugger ? '隐藏' : '显示' }}</el-button>
    </div>
    
    <div class="debugger-content">
      <div class="memory-info">
        <strong>内存使用:</strong>
        <div>已用: {{ memoryInfo.used }}MB</div>
        <div>总计: {{ memoryInfo.total }}MB</div>
        <div>限制: {{ memoryInfo.limit }}MB</div>
      </div>
      
      <div class="performance-info">
        <strong>性能指标:</strong>
        <div>FPS: {{ fps }}</div>
        <div>最近测量: {{ lastMeasurement }}</div>
      </div>
      
      <div class="debug-logs">
        <strong>调试日志:</strong>
        <div class="log-container">
          <div 
            v-for="(log, index) in recentLogs" 
            :key="index"
            :class="['log-item', log.type]"
          >
            [{{ log.time }}] {{ log.message }}
          </div>
        </div>
      </div>
      
      <div class="actions">
        <el-button size="small" @click="clearLogs">清空日志</el-button>
        <el-button size="small" @click="forceGC">强制GC</el-button>
        <el-button size="small" type="danger" @click="simulateError">模拟错误</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'

const showDebugger = ref(true)
const fps = ref(0)
const lastMeasurement = ref('')
const recentLogs = ref([])

const memoryInfo = reactive({
  used: 0,
  total: 0,
  limit: 0
})

let frameCount = 0
let lastTime = performance.now()
let animationId = null

// 更新内存信息
const updateMemoryInfo = () => {
  if (performance.memory) {
    memoryInfo.used = Math.round(performance.memory.usedJSHeapSize / 1024 / 1024)
    memoryInfo.total = Math.round(performance.memory.totalJSHeapSize / 1024 / 1024)
    memoryInfo.limit = Math.round(performance.memory.jsHeapSizeLimit / 1024 / 1024)
  }
}

// FPS计算
const updateFPS = () => {
  frameCount++
  const currentTime = performance.now()
  
  if (currentTime >= lastTime + 1000) {
    fps.value = Math.round((frameCount * 1000) / (currentTime - lastTime))
    frameCount = 0
    lastTime = currentTime
    updateMemoryInfo()
  }
  
  animationId = requestAnimationFrame(updateFPS)
}

// 添加日志
const addLog = (message, type = 'info') => {
  const now = new Date()
  const time = now.toTimeString().split(' ')[0]
  
  recentLogs.value.unshift({
    message,
    type,
    time,
    timestamp: now.getTime()
  })
  
  // 只保留最近50条日志
  if (recentLogs.value.length > 50) {
    recentLogs.value = recentLogs.value.slice(0, 50)
  }
}

// 监听性能测量
const observePerformance = () => {
  if ('PerformanceObserver' in window) {
    const observer = new PerformanceObserver((list) => {
      const entries = list.getEntries()
      entries.forEach(entry => {
        if (entry.entryType === 'measure') {
          lastMeasurement.value = `${entry.name}: ${entry.duration.toFixed(2)}ms`
          addLog(`性能测量: ${entry.name} = ${entry.duration.toFixed(2)}ms`, 'performance')
        }
      })
    })
    
    observer.observe({ entryTypes: ['measure'] })
  }
}

// 监听长任务
const observeLongTasks = () => {
  if ('PerformanceObserver' in window) {
    try {
      const observer = new PerformanceObserver((list) => {
        const entries = list.getEntries()
        entries.forEach(entry => {
          if (entry.duration > 50) {
            addLog(`⚠️ 长任务检测: ${entry.duration.toFixed(2)}ms`, 'warning')
          }
        })
      })
      
      observer.observe({ entryTypes: ['longtask'] })
    } catch (error) {
      addLog(`长任务监控不支持: ${error.message}`, 'info')
    }
  }
}

// 清空日志
const clearLogs = () => {
  recentLogs.value = []
  addLog('日志已清空', 'info')
}

// 强制垃圾回收
const forceGC = () => {
  if (window.gc) {
    window.gc()
    addLog('已执行垃圾回收', 'info')
  } else {
    addLog('垃圾回收不可用', 'warning')
  }
}

// 模拟错误
const simulateError = () => {
  try {
    throw new Error('这是一个测试错误')
  } catch (e) {
    addLog(`❌ 模拟错误: ${e.message}`, 'error')
  }
}

// 切换调试器显示
const toggleDebugger = () => {
  showDebugger.value = !showDebugger.value
}

// 全局错误监听
const handleError = (event) => {
  addLog(`❌ 全局错误: ${event.error?.message || event.message}`, 'error')
}

const handleUnhandledRejection = (event) => {
  addLog(`❌ 未处理的Promise拒绝: ${event.reason}`, 'error')
}

// 导出给全局使用
window.debuggerAddLog = addLog

onMounted(() => {
  updateFPS()
  observePerformance()
  observeLongTasks()
  
  window.addEventListener('error', handleError)
  window.addEventListener('unhandledrejection', handleUnhandledRejection)
  
  addLog('性能调试器已启动', 'info')
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  
  window.removeEventListener('error', handleError)
  window.removeEventListener('unhandledrejection', handleUnhandledRejection)
})
</script>

<style scoped>
.performance-debugger {
  position: fixed;
  top: 10px;
  right: 10px;
  width: 350px;
  background: rgba(0, 0, 0, 0.9);
  color: white;
  border-radius: 8px;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  z-index: 9999;
  max-height: 500px;
  overflow-y: auto;
}

.debugger-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  border-bottom: 1px solid #333;
  padding-bottom: 8px;
}

.debugger-header h4 {
  margin: 0;
  color: #00ff00;
}

.debugger-content > div {
  margin-bottom: 10px;
  padding: 5px;
  border-left: 2px solid #333;
  padding-left: 8px;
}

.memory-info strong,
.performance-info strong,
.debug-logs strong {
  color: #00ff00;
  display: block;
  margin-bottom: 5px;
}

.log-container {
  max-height: 150px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.05);
  padding: 5px;
  border-radius: 4px;
}

.log-item {
  margin-bottom: 2px;
  word-break: break-all;
}

.log-item.info {
  color: #87ceeb;
}

.log-item.warning {
  color: #ffa500;
}

.log-item.error {
  color: #ff6b6b;
}

.log-item.performance {
  color: #98fb98;
}

.actions {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.actions .el-button {
  font-size: 10px;
  padding: 4px 8px;
}
</style>
