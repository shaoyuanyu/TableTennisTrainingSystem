<template>
  <div class="dashboard-analyzer">
    <el-card>
      <template #header>
        <h3>📊 仪表板性能分析报告</h3>
      </template>
      
      <div class="analysis-summary">
        <el-alert
          title="基于代码分析的性能评估"
          type="info"
          description="分析所有仪表板组件的复杂度、数据量和潜在性能瓶颈"
          show-icon
        />
      </div>

      <div class="dashboard-cards">
        <el-row :gutter="20">
          <el-col :span="12" v-for="dashboard in dashboardAnalysis" :key="dashboard.name">
            <el-card class="dashboard-card" :class="dashboard.riskLevel">
              <template #header>
                <div class="card-header">
                  <span class="dashboard-name">{{ dashboard.name }}</span>
                  <el-tag :type="getRiskTagType(dashboard.riskLevel)">
                    {{ dashboard.riskLevel }}
                  </el-tag>
                </div>
              </template>
              
              <div class="metrics">
                <div class="metric">
                  <span class="metric-label">代码行数:</span>
                  <span class="metric-value" :class="getMetricClass(dashboard.lineCount, 600)">
                    {{ dashboard.lineCount }}
                  </span>
                </div>
                
                <div class="metric">
                  <span class="metric-label">组件复杂度:</span>
                  <span class="metric-value" :class="getMetricClass(dashboard.complexity, 20)">
                    {{ dashboard.complexity }}
                  </span>
                </div>
                
                <div class="metric">
                  <span class="metric-label">数据对象数:</span>
                  <span class="metric-value" :class="getMetricClass(dashboard.dataObjects, 15)">
                    {{ dashboard.dataObjects }}
                  </span>
                </div>
                
                <div class="metric">
                  <span class="metric-label">估计DOM节点:</span>
                  <span class="metric-value" :class="getMetricClass(dashboard.estimatedNodes, 1000)">
                    {{ dashboard.estimatedNodes }}
                  </span>
                </div>
              </div>
              
              <div class="issues" v-if="dashboard.issues.length > 0">
                <h5>⚠️ 潜在问题:</h5>
                <ul>
                  <li v-for="issue in dashboard.issues" :key="issue">{{ issue }}</li>
                </ul>
              </div>
              
              <div class="recommendations" v-if="dashboard.recommendations.length > 0">
                <h5>💡 优化建议:</h5>
                <ul>
                  <li v-for="rec in dashboard.recommendations" :key="rec">{{ rec }}</li>
                </ul>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <div class="optimization-plan" v-if="urgentOptimizations.length > 0">
        <el-card>
          <template #header>
            <h4>🚨 紧急优化计划</h4>
          </template>
          <ol>
            <li v-for="plan in urgentOptimizations" :key="plan">{{ plan }}</li>
          </ol>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 基于实际代码分析的数据
const dashboardAnalysis = [
  {
    name: '超级管理员仪表板',
    component: 'SuperAdminDashboard',
    lineCount: 542,
    complexity: 15,
    dataObjects: 8,
    estimatedNodes: 800,
    riskLevel: '中等风险',
    issues: [
      '包含多个实时数据统计',
      '图表和表格组件较多'
    ],
    recommendations: [
      '已使用优化后的MinimalSuperAdminDashboard',
      '考虑懒加载非关键数据'
    ]
  },
  {
    name: '校区管理员仪表板', 
    component: 'CampusAdminDashboard',
    lineCount: 1258,
    complexity: 35,
    dataObjects: 14,
    estimatedNodes: 1500,
    riskLevel: '高风险',
    issues: [
      '代码行数过多(1258行)',
      '数据对象复杂(14个ref对象)',
      '估计DOM节点数超过1500个',
      '包含大量图表和统计组件'
    ],
    recommendations: [
      '🔥 紧急需要拆分组件',
      '实施虚拟化或懒加载',
      '减少初始渲染的数据量',
      '考虑分页或标签页布局'
    ]
  },
  {
    name: '教练仪表板',
    component: 'CoachDashboard', 
    lineCount: 1216,
    complexity: 32,
    dataObjects: 14,
    estimatedNodes: 1400,
    riskLevel: '高风险',
    issues: [
      '代码行数过多(1216行)',
      '复杂度高(32个组件/功能)',
      '数据对象过多(14个)',
      '时间线组件可能卡顿'
    ],
    recommendations: [
      '🔥 紧急拆分为多个子组件',
      '优化时间线渲染',
      '实施数据懒加载',
      '减少实时更新频率'
    ]
  },
  {
    name: '学员仪表板',
    component: 'StudentDashboard',
    lineCount: 976,
    complexity: 25,
    dataObjects: 9,
    estimatedNodes: 1200,
    riskLevel: '中等风险',
    issues: [
      '代码行数较多(976行)',
      'DOM节点数偏高',
      '多个数据列表组件'
    ],
    recommendations: [
      '优化课程列表渲染',
      '实施虚拟滚动',
      '考虑组件拆分',
      '延迟加载非核心功能'
    ]
  }
]

const urgentOptimizations = computed(() => {
  const urgent = []
  
  dashboardAnalysis.forEach(dashboard => {
    if (dashboard.riskLevel === '高风险') {
      urgent.push(`${dashboard.name}: 立即进行组件拆分和性能优化`)
    }
  })
  
  return urgent
})

const getRiskTagType = (riskLevel) => {
  switch (riskLevel) {
    case '高风险': return 'danger'
    case '中等风险': return 'warning'
    case '低风险': return 'success'
    default: return 'info'
  }
}

const getMetricClass = (value, threshold) => {
  if (value > threshold * 1.5) return 'metric-danger'
  if (value > threshold) return 'metric-warning'
  return 'metric-success'
}
</script>

<style scoped>
.dashboard-analyzer {
  max-width: 1400px;
  margin: 0 auto;
}

.analysis-summary {
  margin-bottom: 24px;
}

.dashboard-cards {
  margin-bottom: 24px;
}

.dashboard-card {
  height: 100%;
  transition: all 0.3s;
}

.dashboard-card.高风险 {
  border-left: 4px solid #f56565;
}

.dashboard-card.中等风险 {
  border-left: 4px solid #ed8936;
}

.dashboard-card.低风险 {
  border-left: 4px solid #48bb78;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-name {
  font-weight: 600;
  font-size: 16px;
}

.metrics {
  margin-bottom: 16px;
}

.metric {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  padding: 4px 0;
}

.metric-label {
  color: #666;
}

.metric-value {
  font-weight: 600;
}

.metric-success {
  color: #67c23a;
}

.metric-warning {
  color: #e6a23c;
}

.metric-danger {
  color: #f56565;
}

.issues h5, .recommendations h5 {
  margin: 12px 0 8px 0;
  font-size: 14px;
}

.issues ul, .recommendations ul {
  margin: 0;
  padding-left: 20px;
}

.issues li {
  color: #e6a23c;
  margin-bottom: 4px;
  font-size: 13px;
}

.recommendations li {
  color: #409eff;
  margin-bottom: 4px;
  font-size: 13px;
}

.optimization-plan ol {
  padding-left: 20px;
}

.optimization-plan li {
  margin-bottom: 8px;
  color: #f56565;
  font-weight: 500;
}
</style>
