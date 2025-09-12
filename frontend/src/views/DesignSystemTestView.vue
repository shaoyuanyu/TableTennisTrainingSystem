<template>
  <div class="design-system-test">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">设计系统测试页面</h1>
      <p class="page-subtitle">验证各个组件的视觉效果和交互</p>
    </div>

    <!-- 卡片测试 -->
    <div class="test-section">
      <h2 class="section-title">卡片组件测试</h2>
      <div class="cards-grid">
        <div class="glass-card">
          <h3>基础玻璃卡片</h3>
          <p>这是一个基础的玻璃形态卡片，具有半透明背景和模糊效果。</p>
        </div>
        
        <div class="glass-card-enhanced">
          <h3>增强版玻璃卡片</h3>
          <p>具有光晕扫过动画效果的卡片，提供更丰富的视觉体验。</p>
        </div>
        
        <div class="content-card">
          <h3>内容区域卡片</h3>
          <p>适用于主要内容区域的卡片样式，具有更高的透明度。</p>
        </div>
        
        <div class="sidebar-card">
          <h3>侧边栏卡片</h3>
          <p>专为侧边栏设计的紧凑型卡片。</p>
        </div>
      </div>
    </div>

    <!-- 按钮测试 -->
    <div class="test-section">
      <h2 class="section-title">按钮组件测试</h2>
      <div class="buttons-grid">
        <div class="button-group">
          <h4>基础按钮</h4>
          <button class="btn-modern btn-primary">主要按钮</button>
          <button class="btn-modern btn-secondary">次要按钮</button>
          <button class="btn-modern btn-success">成功按钮</button>
          <button class="btn-modern btn-warning">警告按钮</button>
          <button class="btn-modern btn-danger">危险按钮</button>
        </div>
        
        <div class="button-group">
          <h4>不同尺寸</h4>
          <button class="btn-modern btn-primary btn-small">小按钮</button>
          <button class="btn-modern btn-primary">标准按钮</button>
          <button class="btn-modern btn-primary btn-large">大按钮</button>
        </div>
      </div>
    </div>

    <!-- 标签测试 -->
    <div class="test-section">
      <h2 class="section-title">标签组件测试</h2>
      <div class="tags-container">
        <span class="tag-glass tag-primary">主要标签</span>
        <span class="tag-glass tag-success">成功标签</span>
        <span class="tag-glass tag-warning">警告标签</span>
        <span class="tag-glass tag-danger">危险标签</span>
      </div>
    </div>

    <!-- 表单测试 -->
    <div class="test-section">
      <h2 class="section-title">表单组件测试</h2>
      <div class="form-test-container">
        <div class="form-glass">
          <h3>登录表单示例</h3>
          <input type="text" class="input-glass" placeholder="用户名">
          <input type="password" class="input-glass" placeholder="密码">
          <button class="btn-modern btn-primary">登录</button>
        </div>
      </div>
    </div>

    <!-- 进度条测试 -->
    <div class="test-section">
      <h2 class="section-title">进度条组件测试</h2>
      <div class="progress-container">
        <div class="progress-item">
          <label>进度 25%</label>
          <div class="progress-glass" style="height: 12px; width: 100%;">
            <div class="progress-bar" style="width: 25%;"></div>
          </div>
        </div>
        
        <div class="progress-item">
          <label>进度 50%</label>
          <div class="progress-glass" style="height: 12px; width: 100%;">
            <div class="progress-bar" style="width: 50%;"></div>
          </div>
        </div>
        
        <div class="progress-item">
          <label>进度 75%</label>
          <div class="progress-glass" style="height: 12px; width: 100%;">
            <div class="progress-bar" style="width: 75%;"></div>
          </div>
        </div>
        
        <div class="progress-item">
          <label>进度 100%</label>
          <div class="progress-glass" style="height: 12px; width: 100%;">
            <div class="progress-bar" style="width: 100%;"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 工具类测试 -->
    <div class="test-section">
      <h2 class="section-title">工具类测试</h2>
      <div class="utilities-test">
        <div class="glass-card">
          <div class="flex flex-between mb-lg">
            <span class="text-white">左侧内容</span>
            <span class="text-white-80">右侧内容</span>
          </div>
          
          <div class="flex flex-center mb-lg">
            <span class="text-white">居中内容</span>
          </div>
          
          <div class="mt-lg p-md rounded-lg shadow-md" style="background: var(--white-alpha-10);">
            <span class="text-white-60">带间距、圆角和阴影的元素</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 动画效果测试 -->
    <div class="test-section">
      <h2 class="section-title">动画效果测试</h2>
      <div class="animations-grid">
        <div class="glass-card shimmer-effect">
          <h4>光晕扫过效果</h4>
          <p>这个卡片具有光晕扫过的动画效果。</p>
        </div>
        
        <div class="glass-card pulse-effect">
          <h4>脉冲效果</h4>
          <p>这个卡片具有脉冲动画效果。</p>
        </div>
        
        <div class="glass-card hover-lift">
          <h4>悬浮效果</h4>
          <p>鼠标悬浮时这个卡片会上升。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DesignSystemTest',
  data() {
    return {}
  }
}
</script>

<style scoped>
.design-system-test {
  min-height: 100vh;
  padding: var(--spacing-2xl);
}

.test-section {
  margin-bottom: var(--spacing-3xl);
}

.section-title {
  color: white;
  margin-bottom: var(--spacing-xl);
  font-size: 1.5em;
  font-weight: 600;
}

/* 卡片网格 */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
}

/* 按钮网格 */
.buttons-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.button-group {
  background: var(--white-alpha-10);
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  backdrop-filter: blur(20px);
}

.button-group h4 {
  color: white;
  margin-bottom: var(--spacing-md);
}

.button-group button {
  margin-right: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

/* 标签容器 */
.tags-container {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

/* 表单测试容器 */
.form-test-container {
  display: flex;
  justify-content: center;
}

.form-glass {
  max-width: 400px;
  width: 100%;
}

/* 进度条容器 */
.progress-container {
  max-width: 600px;
}

.progress-item {
  margin-bottom: var(--spacing-lg);
}

.progress-item label {
  color: white;
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 500;
}

/* 工具类测试 */
.utilities-test {
  max-width: 600px;
}

/* 动画网格 */
.animations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-xl);
}

/* 响应式 */
@media (max-width: 768px) {
  .design-system-test {
    padding: var(--spacing-lg);
  }
  
  .cards-grid {
    grid-template-columns: 1fr;
  }
  
  .buttons-grid {
    grid-template-columns: 1fr;
  }
  
  .animations-grid {
    grid-template-columns: 1fr;
  }
}
</style>
