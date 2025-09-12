<template>
  <div class="navigation-test">
    <div class="page-header">
      <h1 class="page-title">乒乓球训练系统 - 页面导航</h1>
      <p class="page-subtitle">快速访问各个页面进行设计系统测试</p>
    </div>

    <div class="nav-grid">
      <!-- 主要页面 -->
      <div class="nav-section">
        <h2 class="section-title">主要页面</h2>
        <div class="nav-cards">
          <router-link to="/dashboard" class="nav-card glass-card">
            <h3>仪表盘</h3>
            <p>查看学生仪表盘的设计效果</p>
          </router-link>
          
          <router-link to="/design-system-test" class="nav-card glass-card-enhanced">
            <h3>设计系统测试</h3>
            <p>完整的设计系统组件展示</p>
          </router-link>
          
          <router-link to="/dev-tools" class="nav-card glass-card">
            <h3>开发工具</h3>
            <p>开发者工具和系统信息</p>
          </router-link>
        </div>
      </div>

      <!-- 认证页面 -->
      <div class="nav-section">
        <h2 class="section-title">认证页面</h2>
        <div class="nav-cards">
          <router-link to="/login" class="nav-card glass-card">
            <h3>登录页面</h3>
            <p>用户登录界面</p>
          </router-link>
          
          <router-link to="/register/student" class="nav-card glass-card">
            <h3>学生注册</h3>
            <p>学生注册表单页面</p>
          </router-link>
          
          <router-link to="/register/coach" class="nav-card glass-card">
            <h3>教练注册</h3>
            <p>教练注册表单页面</p>
          </router-link>
        </div>
      </div>

      <!-- 设计系统信息 -->
      <div class="nav-section">
        <h2 class="section-title">设计系统信息</h2>
        <div class="info-cards">
          <div class="info-card content-card">
            <h3>🎨 设计理念</h3>
            <ul>
              <li>Glassmorphism（玻璃形态）设计</li>
              <li>半透明背景 + backdrop-filter</li>
              <li>现代渐变背景动画</li>
              <li>统一的视觉语言</li>
            </ul>
          </div>
          
          <div class="info-card content-card">
            <h3>🔧 核心组件</h3>
            <ul>
              <li>玻璃卡片（4种变体）</li>
              <li>现代按钮系统</li>
              <li>表单组件</li>
              <li>标签和进度条</li>
            </ul>
          </div>
          
          <div class="info-card content-card">
            <h3>⚡ 动画效果</h3>
            <ul>
              <li>光晕扫过动画</li>
              <li>悬浮上升效果</li>
              <li>脉冲呼吸动画</li>
              <li>平滑过渡</li>
            </ul>
          </div>
          
          <div class="info-card content-card">
            <h3>📱 响应式</h3>
            <ul>
              <li>移动端优化</li>
              <li>弹性网格布局</li>
              <li>自适应间距</li>
              <li>触摸友好</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 使用指南 -->
      <div class="nav-section">
        <h2 class="section-title">快速使用指南</h2>
        <div class="guide-cards">
          <div class="guide-card sidebar-card">
            <h4>基础卡片</h4>
            <code>.glass-card</code>
            <p>半透明背景的基础卡片</p>
          </div>
          
          <div class="guide-card sidebar-card">
            <h4>增强卡片</h4>
            <code>.glass-card-enhanced</code>
            <p>带光晕动画的增强卡片</p>
          </div>
          
          <div class="guide-card sidebar-card">
            <h4>现代按钮</h4>
            <code>.btn-modern .btn-primary</code>
            <p>统一样式的现代化按钮</p>
          </div>
          
          <div class="guide-card sidebar-card">
            <h4>玻璃表单</h4>
            <code>.form-glass .input-glass</code>
            <p>透明样式的表单组件</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NavigationTest',
  data() {
    return {}
  }
}
</script>

<style scoped>
.navigation-test {
  min-height: 100vh;
  padding: var(--spacing-2xl);
}

.nav-grid {
  max-width: 1200px;
  margin: 0 auto;
}

.nav-section {
  margin-bottom: var(--spacing-3xl);
}

.section-title {
  color: white;
  margin-bottom: var(--spacing-xl);
  font-size: 1.5em;
  font-weight: 600;
}

/* 导航卡片网格 */
.nav-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-xl);
}

.nav-card {
  display: block;
  text-decoration: none;
  transition: all var(--transition-normal);
  color: inherit;
}

.nav-card:hover {
  transform: translateY(-4px);
  color: inherit;
}

.nav-card h3 {
  color: white;
  margin-bottom: var(--spacing-sm);
  font-size: 1.2em;
}

.nav-card p {
  color: var(--text-white-80);
  margin: 0;
  font-size: 0.9em;
}

/* 信息卡片网格 */
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-xl);
}

.info-card h3 {
  color: white;
  margin-bottom: var(--spacing-md);
  font-size: 1.1em;
}

.info-card ul {
  margin: 0;
  padding-left: var(--spacing-lg);
  color: var(--text-white-80);
}

.info-card li {
  margin-bottom: var(--spacing-xs);
  font-size: 0.9em;
}

/* 指南卡片网格 */
.guide-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.guide-card h4 {
  color: white;
  margin-bottom: var(--spacing-xs);
  font-size: 1em;
}

.guide-card code {
  background: var(--white-alpha-20);
  color: #64ffda;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: 0.8em;
  display: block;
  margin-bottom: var(--spacing-sm);
  word-break: break-all;
}

.guide-card p {
  color: var(--text-white-80);
  margin: 0;
  font-size: 0.85em;
}

/* 响应式 */
@media (max-width: 768px) {
  .navigation-test {
    padding: var(--spacing-lg);
  }
  
  .nav-cards {
    grid-template-columns: 1fr;
  }
  
  .info-cards {
    grid-template-columns: 1fr;
  }
  
  .guide-cards {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
}

@media (max-width: 480px) {
  .guide-cards {
    grid-template-columns: 1fr;
  }
}
</style>
