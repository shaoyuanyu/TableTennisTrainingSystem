<template>
  <div class="minimal-dashboard">
    <h1>超级管理员 - 极简版本</h1>
    
    <div class="cards-container">
      <!-- 系统概览 -->
      <div class="simple-card">
        <h3>系统概览</h3>
        <div class="stats-grid">
          <div class="stat-item">
            <span class="value">5</span>
            <span class="label">校区总数</span>
          </div>
          <div class="stat-item">
            <span class="value">892</span>
            <span class="label">活跃用户</span>
          </div>
          <div class="stat-item">
            <span class="value">¥85,600</span>
            <span class="label">月收入</span>
          </div>
          <div class="stat-item">
            <span class="value">1248</span>
            <span class="label">总课程数</span>
          </div>
        </div>
      </div>

      <!-- 服务状态 -->
      <div class="simple-card">
        <h3>服务状态</h3>
        <div class="status-list">
          <div class="status-item">
            <span>系统服务</span>
            <span class="status-good">运行正常</span>
          </div>
          <div class="status-item">
            <span>数据库</span>
            <span class="status-good">正常</span>
          </div>
          <div class="status-item">
            <span>服务到期</span>
            <span class="status-good">正常</span>
          </div>
        </div>
      </div>

      <!-- 快速操作 -->
      <div class="simple-card">
        <h3>快速操作</h3>
        <div class="action-buttons">
          <button class="action-btn" @click="goTo('/admin/campus')">校区管理</button>
          <button class="action-btn" @click="goTo('/admin/service')">服务状态</button>
          <button class="action-btn" @click="goTo('/campus/logs')">系统日志</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.minimal-dashboard {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: white;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.simple-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  color: #333;
}

.simple-card h3 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item .value {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-item .label {
  font-size: 12px;
  color: #666;
}

.status-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.status-good {
  color: #52c41a;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  padding: 12px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s ease;
}

.action-btn:hover {
  background: #40a9ff;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .cards-container {
    grid-template-columns: 1fr;
  }
}
</style>
