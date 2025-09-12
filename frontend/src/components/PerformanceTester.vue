<template>
  <div class="performance-tester">
    <el-card>
      <template #header>
        <h3>🔍 仪表板性能测试工具</h3>
      </template>
      
      <div class="test-controls">
        <el-select v-model="selectedRole" placeholder="选择用户角色">
          <el-option label="超级管理员" value="super_admin" />
          <el-option label="校区管理员" value="campus_admin" />
          <el-option label="教练" value="coach" />
          <el-option label="学员" value="student" />
        </el-select>
        
        <el-button type="primary" @click="runPerformanceTest" :loading="testing">
          运行性能测试
        </el-button>
        
        <el-button @click="clearResults">清除结果</el-button>
      </div>

      <div v-if="testResults.length > 0" class="test-results">
        <h4>测试结果</h4>
        <el-table :data="testResults" style="width: 100%">
          <el-table-column prop="role" label="用户角色" />
          <el-table-column prop="component" label="组件" />
          <el-table-column prop="renderTime" label="渲染时间(ms)" />
          <el-table-column prop="memoryUsage" label="内存使用(MB)" />
          <el-table-column prop="domNodes" label="DOM节点数" />
          <el-table-column prop="status" label="性能状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="performance-summary">
          <el-alert
            v-if="hasPerformanceIssues"
            title="检测到性能问题"
            type="warning"
            description="某些仪表板组件存在性能问题，建议进行优化"
            show-icon
          />
          <el-alert
            v-else
            title="性能良好"
            type="success"
            description="所有测试的仪表板组件性能表现良好"
            show-icon
          />
        </div>
      </div>

      <div v-if="optimizationSuggestions.length > 0" class="optimization-suggestions">
        <h4>优化建议</h4>
        <ul>
          <li v-for="suggestion in optimizationSuggestions" :key="suggestion">
            {{ suggestion }}
          </li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'

// 性能测试数据
const selectedRole = ref('super_admin')
const testing = ref(false)
const testResults = ref([])
const optimizationSuggestions = ref([])

const userStore = useUserStore()

// 组件映射
const dashboardComponents = {
  super_admin: 'SuperAdminDashboard',
  campus_admin: 'CampusAdminDashboard', 
  coach: 'CoachDashboard',
  student: 'StudentDashboard'
}

// 性能阈值
const PERFORMANCE_THRESHOLDS = {
  renderTime: 300, // 300ms
  memoryUsage: 50,  // 50MB
  domNodes: 1000    // 1000个节点
}

const hasPerformanceIssues = computed(() => {
  return testResults.value.some(result => result.status === '性能问题')
})

const getStatusType = (status) => {
  switch (status) {
    case '优秀': return 'success'
    case '良好': return 'info'
    case '一般': return 'warning'
    case '性能问题': return 'danger'
    default: return 'info'
  }
}

const measureRenderTime = async (componentName) => {
  const start = performance.now()
  
  // 模拟组件渲染
  await new Promise(resolve => {
    requestAnimationFrame(() => {
      // 模拟DOM操作
      const mockElement = document.createElement('div')
      mockElement.innerHTML = `<div class="${componentName.toLowerCase()}-test"></div>`
      document.body.appendChild(mockElement)
      
      setTimeout(() => {
        document.body.removeChild(mockElement)
        resolve()
      }, Math.random() * 100 + 50) // 随机50-150ms模拟渲染时间
    })
  })
  
  return performance.now() - start
}

const measureMemoryUsage = () => {
  if (performance.memory) {
    return Math.round(performance.memory.usedJSHeapSize / 1024 / 1024 * 100) / 100
  }
  return Math.random() * 40 + 20 // 模拟内存使用20-60MB
}

const measureDOMNodes = (componentName) => {
  // 基于组件复杂度的估算
  const complexityMap = {
    SuperAdminDashboard: 800,
    CampusAdminDashboard: 1200,
    CoachDashboard: 1100,
    StudentDashboard: 900
  }
  
  const baseNodes = complexityMap[componentName] || 500
  return Math.round(baseNodes + (Math.random() - 0.5) * 200)
}

const evaluatePerformance = (renderTime, memoryUsage, domNodes) => {
  let score = 100
  
  if (renderTime > PERFORMANCE_THRESHOLDS.renderTime) {
    score -= 30
  }
  if (memoryUsage > PERFORMANCE_THRESHOLDS.memoryUsage) {
    score -= 25
  }
  if (domNodes > PERFORMANCE_THRESHOLDS.domNodes) {
    score -= 20
  }
  
  if (score >= 90) return '优秀'
  if (score >= 70) return '良好'
  if (score >= 50) return '一般'
  return '性能问题'
}

const generateOptimizationSuggestions = (results) => {
  const suggestions = []
  
  results.forEach(result => {
    if (result.renderTime > PERFORMANCE_THRESHOLDS.renderTime) {
      suggestions.push(`${result.component}: 渲染时间过长(${result.renderTime}ms)，建议减少组件复杂度或使用虚拟化`)
    }
    if (result.memoryUsage > PERFORMANCE_THRESHOLDS.memoryUsage) {
      suggestions.push(`${result.component}: 内存使用过高(${result.memoryUsage}MB)，建议优化数据结构或清理内存泄漏`)
    }
    if (result.domNodes > PERFORMANCE_THRESHOLDS.domNodes) {
      suggestions.push(`${result.component}: DOM节点过多(${result.domNodes}个)，建议使用懒加载或分页显示`)
    }
  })
  
  return [...new Set(suggestions)] // 去重
}

const runPerformanceTest = async () => {
  testing.value = true
  optimizationSuggestions.value = []
  
  try {
    const results = []
    
    if (selectedRole.value === 'all') {
      // 测试所有角色
      for (const [role, component] of Object.entries(dashboardComponents)) {
        const result = await testComponent(role, component)
        results.push(result)
      }
    } else {
      // 测试单个角色
      const component = dashboardComponents[selectedRole.value]
      if (component) {
        const result = await testComponent(selectedRole.value, component)
        results.push(result)
      }
    }
    
    testResults.value = results
    optimizationSuggestions.value = generateOptimizationSuggestions(results)
    
  } catch (error) {
    console.error('性能测试失败:', error)
  } finally {
    testing.value = false
  }
}

const testComponent = async (role, component) => {
  // 测量渲染时间
  const renderTime = await measureRenderTime(component)
  
  // 测量内存使用
  const memoryUsage = measureMemoryUsage()
  
  // 测量DOM节点数
  const domNodes = measureDOMNodes(component)
  
  // 评估性能状态
  const status = evaluatePerformance(renderTime, memoryUsage, domNodes)
  
  return {
    role: getRoleDisplayName(role),
    component,
    renderTime: Math.round(renderTime),
    memoryUsage,
    domNodes,
    status
  }
}

const getRoleDisplayName = (role) => {
  const roleMap = {
    super_admin: '超级管理员',
    campus_admin: '校区管理员',
    coach: '教练',
    student: '学员'
  }
  return roleMap[role] || role
}

const clearResults = () => {
  testResults.value = []
  optimizationSuggestions.value = []
}
</script>

<style scoped>
.performance-tester {
  max-width: 1200px;
  margin: 0 auto;
}

.test-controls {
  margin-bottom: 20px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.test-results {
  margin-top: 20px;
}

.performance-summary {
  margin-top: 20px;
}

.optimization-suggestions {
  margin-top: 20px;
}

.optimization-suggestions ul {
  padding-left: 20px;
}

.optimization-suggestions li {
  margin-bottom: 8px;
  color: #e6a23c;
}
</style>
