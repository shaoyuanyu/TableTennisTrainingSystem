<template>
  <div class="design-language-showcase">
    <!-- 页面头部 -->
    <div class="design-language-hero">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-icon">🎨</span>
          <span>设计语言 v2.0</span>
        </div>
        <h1 class="hero-title">乒乓球训练系统设计语言样板间</h1>
        <p class="hero-description">
          基于 Glassmorphism 设计理念的现代化组件库展示空间，
          <br>展示统一的视觉语言、交互模式和组件规范
        </p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">42</div>
            <div class="stat-label">设计变量</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">15+</div>
            <div class="stat-label">组件类型</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">100%</div>
            <div class="stat-label">响应式设计</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">A11Y</div>
            <div class="stat-label">无障碍支持</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 设计原则展示 -->
    <div class="design-principles">
      <h2 class="section-title">🎯 设计原则</h2>
      <div class="principles-grid">
        <div class="principle-card">
          <div class="principle-icon">🌟</div>
          <h3>简洁优雅</h3>
          <p>追求简洁的视觉表达，去除冗余元素，突出核心功能</p>
        </div>
        <div class="principle-card">
          <div class="principle-icon">🎨</div>
          <h3>一致性</h3>
          <p>统一的颜色、字体、间距系统，确保整体体验的一致性</p>
        </div>
        <div class="principle-card">
          <div class="principle-icon">⚡</div>
          <h3>高效响应</h3>
          <p>快速响应用户操作，提供即时反馈和流畅的交互体验</p>
        </div>
        <div class="principle-card">
          <div class="principle-icon">♿</div>
          <h3>无障碍</h3>
          <p>遵循无障碍设计标准，确保所有用户都能顺畅使用</p>
        </div>
      </div>
    </div>

    <!-- 组件导航 -->
    <div class="component-navigation">
      <h2 class="section-title">📦 组件库导航</h2>
      <div class="nav-grid">
        <a href="#glass-cards" class="nav-card">
          <div class="nav-icon">🎴</div>
          <h3>玻璃卡片</h3>
          <p>GlassCard 系列</p>
        </a>
        <a href="#buttons" class="nav-card">
          <div class="nav-icon">🔘</div>
          <h3>现代按钮</h3>
          <p>ModernButton</p>
        </a>
        <a href="#forms" class="nav-card">
          <div class="nav-icon">📝</div>
          <h3>表单组件</h3>
          <p>FormInput & More</p>
        </a>
        <a href="#feedback" class="nav-card">
          <div class="nav-icon">💬</div>
          <h3>反馈组件</h3>
          <p>TagBadge & Status</p>
        </a>
        <a href="#development" class="nav-card">
          <div class="nav-icon">🛠️</div>
          <h3>开发工具</h3>
          <p>调试与监控</p>
        </a>
        <a href="#utilities" class="nav-card">
          <div class="nav-icon">🔧</div>
          <h3>工具组件</h3>
          <p>布局与辅助</p>
        </a>
      </div>
    </div>

    <!-- 玻璃卡片组件展示 -->
    <div class="component-showcase" id="glass-cards">
      <h2 class="section-title">� 玻璃卡片组件系列</h2>
      <p class="section-description">
        基于 Glassmorphism 设计理念打造的卡片组件系列，提供不同场景下的最佳视觉效果
      </p>
      
      <div class="showcase-grid">
        <!-- GlassDisplayCard - 展示型卡片 -->
        <GlassDisplayCard
          title="GlassDisplayCard"
          subtitle="展示型玻璃卡片"
          icon="🎯"
          description="适用于内容展示和信息呈现的基础玻璃卡片组件，具有良好的可读性和现代感。"
          type="glass"
          :tags="[
            { text: '展示组件', type: 'primary' },
            { text: '可复用', type: 'success' }
          ]"
        >
          <div class="component-demo">
            <p>这是 GlassDisplayCard 组件的内容展示区域，支持任意内容插入。</p>
            <div class="demo-features">
              <span class="feature-tag">✨ 玻璃形态</span>
              <span class="feature-tag">📱 响应式</span>
              <span class="feature-tag">🎨 可定制</span>
            </div>
          </div>
        </GlassDisplayCard>
        
        <!-- GlassCardWithHeader - 带头部的卡片 -->
        <GlassDisplayCard
          title="GlassCardWithHeader"
          subtitle="带头部的玻璃卡片"
          icon="🎖️"
          description="专为需要明确标题和操作区域的场景设计，提供结构化的内容组织方式。"
          type="glass-enhanced"
          :tags="[
            { text: '结构化', type: 'warning' },
            { text: '功能完整', type: 'info' }
          ]"
        >
          <div class="component-demo">
            <div class="demo-preview">
              <GlassCardWithHeader 
                title="示例卡片" 
                icon="🔧" 
                type="primary"
              >
                <div class="demo-content">
                  <p>这是 GlassCardWithHeader 的预览示例</p>
                  <div class="status-items">
                    <div class="status-item">
                      <span>状态:</span>
                      <el-tag type="success" size="small">正常</el-tag>
                    </div>
                  </div>
                </div>
              </GlassCardWithHeader>
            </div>
          </div>
        </GlassDisplayCard>
        
        <DemoCard
          title="内容区域卡片"
          subtitle="content-card"
          icon="📄"
          description="适用于主要内容区域，具有更高的透明度和更大的内边距。"
          type="content"
          :tags="[
            { text: '主要', type: 'success' },
            { text: '内容', type: 'primary' }
          ]"
        />
        
        <DemoCard
          title="侧边栏卡片"
          subtitle="sidebar-card"
          icon="📋"
          description="专为侧边栏设计的紧凑型卡片，空间利用更高效。"
          type="content"
          extra-classes="sidebar-card"
          :tags="[
            { text: '紧凑', type: 'danger' },
            { text: '侧边栏', type: 'warning' }
          ]"
        />
      </div>
    </div>

    <!-- 按钮测试 -->
    <div class="test-section" id="buttons">
      <h2 class="section-title">🔘 按钮系统测试</h2>
      <div class="buttons-demo">
        <div class="button-group">
          <h4>语义化按钮</h4>
          <div class="button-row">
            <ModernButton text="主要按钮" type="primary" />
            <ModernButton text="次要按钮" type="secondary" />
            <ModernButton text="成功按钮" type="success" />
            <ModernButton text="警告按钮" type="warning" />
            <ModernButton text="危险按钮" type="danger" />
          </div>
        </div>
        
        <div class="button-group">
          <h4>按钮尺寸</h4>
          <div class="button-row">
            <ModernButton text="小按钮" type="primary" size="small" />
            <ModernButton text="标准按钮" type="primary" size="medium" />
            <ModernButton text="大按钮" type="primary" size="large" />
          </div>
        </div>

        <div class="button-group">
          <h4>带图标按钮</h4>
          <div class="button-row">
            <ModernButton text="添加内容" type="primary" icon-left="➕" />
            <ModernButton text="确认操作" type="success" icon-left="✅" />
            <ModernButton text="删除" type="danger" icon-left="🗑️" />
          </div>
        </div>

        <div class="button-group">
          <h4>按钮状态</h4>
          <div class="button-row">
            <ModernButton text="正常状态" type="primary" />
            <ModernButton text="禁用状态" type="primary" :disabled="true" />
            <ModernButton text="加载中..." type="primary" :loading="true" />
          </div>
        </div>
      </div>
    </div>

    <!-- 仪表板组件测试 -->
    <div class="test-section" id="dashboard">
      <h2 class="section-title">📊 仪表板组件测试</h2>
      
      <!-- 欢迎横幅测试 -->
      <div class="welcome-banner">
        <div class="banner-main">
          <div>
            <h1 class="welcome-title">欢迎使用设计系统</h1>
            <p class="welcome-subtitle">体验现代化的Glassmorphism风格界面</p>
          </div>
        </div>
        <div class="today-overview">
          <div class="overview-item">
            <span class="overview-icon">📈</span>
            <div>
              <div class="overview-value">156</div>
              <div class="overview-label">组件数量</div>
            </div>
          </div>
          <div class="overview-item">
            <span class="overview-icon">🎨</span>
            <div>
              <div class="overview-value">42</div>
              <div class="overview-label">设计变量</div>
            </div>
          </div>
          <div class="overview-item">
            <span class="overview-icon">⚡</span>
            <div>
              <div class="overview-value">8</div>
              <div class="overview-label">动画效果</div>
            </div>
          </div>
          <div class="overview-item">
            <span class="overview-icon">📱</span>
            <div>
              <div class="overview-value">100%</div>
              <div class="overview-label">响应式</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 调试卡片测试 -->
      <div class="debug-cards">
        <div class="content-card debug-demo-card">
          <div class="demo-card-header">
            <div class="demo-header-left">
              <div class="demo-icon-wrapper">
                <i class="demo-icon">🔧</i>
              </div>
              <div class="demo-header-text">
                <h3>系统状态</h3>
                <p>实时监控组件运行状态</p>
              </div>
            </div>
          </div>
          <div class="demo-card-content">
            <div class="status-grid">
              <div class="status-item">
                <label>设计系统版本</label>
                <span class="tag-glass tag-success">v2.1.0</span>
              </div>
              <div class="status-item">
                <label>组件库状态</label>
                <span class="tag-glass tag-success">正常</span>
              </div>
              <div class="status-item">
                <label>样式文件大小</label>
                <span class="tag-glass tag-primary">15.8KB</span>
              </div>
            </div>
          </div>
        </div>

        <div class="content-card debug-demo-card">
          <div class="demo-card-header">
            <div class="demo-header-left">
              <div class="demo-icon-wrapper">
                <i class="demo-icon">📊</i>
              </div>
              <div class="demo-header-text">
                <h3>性能指标</h3>
                <p>设计系统性能监控</p>
              </div>
            </div>
          </div>
          <div class="demo-card-content">
            <div class="performance-metrics">
              <div class="metric-item">
                <label>CSS变量数量</label>
                <div class="progress-glass" style="height: 8px;">
                  <div class="progress-bar" style="width: 85%;"></div>
                </div>
                <span class="metric-value">42/50</span>
              </div>
              <div class="metric-item">
                <label>组件复用率</label>
                <div class="progress-glass" style="height: 8px;">
                  <div class="progress-bar" style="width: 92%;"></div>
                </div>
                <span class="metric-value">92%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 表单组件测试 -->
    <div class="test-section" id="forms">
      <h2 class="section-title">📝 表单组件测试</h2>
      <div class="forms-demo">
        <div class="form-glass">
          <h3>登录表单示例</h3>
          <FormInput
            label="用户名"
            placeholder="请输入用户名"
            model-value="demo@example.com"
            icon-left="👤"
          />
          <FormInput
            label="密码"
            type="password"
            placeholder="请输入密码"
            model-value="••••••••"
            icon-left="🔒"
          />
          <div class="form-field">
            <div class="checkbox-group">
              <input type="checkbox" id="remember" checked>
              <label for="remember">7天内自动登录</label>
            </div>
          </div>
          <div class="form-actions">
            <ModernButton text="登录" type="primary" size="large" />
            <ModernButton text="忘记密码" type="secondary" />
          </div>
        </div>

        <div class="form-glass">
          <h3>注册表单示例</h3>
          <FormInput
            label="邮箱地址"
            type="email"
            placeholder="example@domain.com"
            icon-left="📧"
            help-text="我们将向此邮箱发送验证信息"
          />
          <FormInput
            label="手机号码"
            type="tel"
            placeholder="13800138000"
            icon-left="📱"
            :required="true"
          />
          <div class="form-field">
            <label>用户类型</label>
            <select class="input-glass">
              <option>学生</option>
              <option>教练</option>
              <option>管理员</option>
            </select>
          </div>
          <div class="form-field">
            <label>个人简介</label>
            <textarea class="input-glass" rows="3" placeholder="请简单介绍一下自己..."></textarea>
          </div>
          <div class="form-actions">
            <ModernButton text="注册" type="success" size="large" />
            <ModernButton text="清空" type="secondary" />
          </div>
        </div>
      </div>
    </div>

    <!-- 反馈组件测试 -->
    <div class="test-section" id="feedback">
      <h2 class="section-title">💬 反馈组件测试</h2>
      
      <!-- 标签测试 -->
      <div class="feedback-demo">
        <div class="content-card">
          <h4>状态标签</h4>
          <div class="tags-container">
            <TagBadge text="主要" type="primary" />
            <TagBadge text="成功" type="success" />
            <TagBadge text="警告" type="warning" />
            <TagBadge text="危险" type="danger" />
            <TagBadge text="信息" type="info" />
            <TagBadge text="次要" type="secondary" />
          </div>
        </div>
        
        <div class="content-card">
          <h4>带图标标签</h4>
          <div class="tags-container">
            <TagBadge text="新功能" type="primary" icon-left="✨" />
            <TagBadge text="已完成" type="success" icon-left="✅" />
            <TagBadge text="待处理" type="warning" icon-left="⏳" />
            <TagBadge text="可关闭" type="info" :closable="true" />
          </div>
        </div>

        <div class="content-card">
          <h4>进度条展示</h4>
          <div class="progress-demo">
            <div class="progress-item">
              <div class="progress-header">
                <label>项目进度</label>
                <span class="progress-text">75%</span>
              </div>
              <div class="progress-glass" style="height: 12px;">
                <div class="progress-bar" style="width: 75%;"></div>
              </div>
            </div>
            
            <div class="progress-item">
              <div class="progress-header">
                <label>设计完成度</label>
                <span class="progress-text">90%</span>
              </div>
              <div class="progress-glass" style="height: 12px;">
                <div class="progress-bar" style="width: 90%;"></div>
              </div>
            </div>
            
            <div class="progress-item">
              <div class="progress-header">
                <label>功能实现</label>
                <span class="progress-text">60%</span>
              </div>
              <div class="progress-glass" style="height: 12px;">
                <div class="progress-bar" style="width: 60%;"></div>
              </div>
            </div>
          </div>
        </div>

        <div class="content-card">
          <h4>通知消息</h4>
          <div class="notification-demo">
            <div class="notification success">
              <span class="notification-icon">✅</span>
              <div class="notification-content">
                <strong>操作成功</strong>
                <p>设计系统组件已成功加载</p>
              </div>
            </div>
            
            <div class="notification warning">
              <span class="notification-icon">⚠️</span>
              <div class="notification-content">
                <strong>注意事项</strong>
                <p>某些旧版浏览器可能不支持backdrop-filter</p>
              </div>
            </div>
            
            <div class="notification info">
              <span class="notification-icon">ℹ️</span>
              <div class="notification-content">
                <strong>信息提示</strong>
                <p>建议使用Chrome、Firefox或Safari浏览器</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 开发工具组件展示 -->
    <div class="component-showcase" id="development">
      <h2 class="section-title">🛠️ 开发工具组件系列</h2>
      <p class="section-description">
        专为开发和调试场景设计的专业组件，提供系统状态监控、权限测试、环境信息展示等功能
      </p>
      
      <DebugCardsContainer>
        <!-- 用户状态调试卡片 -->
        <GlassCardWithHeader 
          title="用户状态监控" 
          icon="👤" 
          type="primary"
        >
          <StatusGrid :items="userStatusItems" />
        </GlassCardWithHeader>

        <!-- 系统环境调试卡片 -->
        <GlassCardWithHeader 
          title="系统环境信息" 
          icon="💻" 
          type="info"
        >
          <StatusGrid :items="systemEnvItems" />
        </GlassCardWithHeader>

        <!-- 本地存储调试卡片 -->
        <GlassCardWithHeader 
          title="本地存储" 
          icon="💾" 
          type="warning"
        >
          <DebugCardContent type="form">
            <StorageInfo :items="storageItems" />
          </DebugCardContent>
        </GlassCardWithHeader>

        <!-- 权限测试调试卡片 - 使用GlassCardWithHeader组件 -->
        <GlassCardWithHeader 
          title="权限测试 (GlassCardWithHeader)" 
          icon="🔐"
        >
          <StatusGrid :items="permissionItems" />
        </GlassCardWithHeader>

        <!-- 权限测试调试卡片 - 原生结构对比 -->
        <el-card class="glass-card-with-header" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-icon">🔐</span>
              <span>权限测试 (原生)</span>
            </div>
          </template>
          <div class="permission-tests">
            <div class="test-item">
              <label>超级管理员权限:</label>
              <el-tag type="info" size="small">无权限</el-tag>
            </div>
            <div class="test-item">
              <label>校区管理员权限:</label>
              <el-tag type="success" size="small">有权限</el-tag>
            </div>
            <div class="test-item">
              <label>学员权限:</label>
              <el-tag type="info" size="small">无权限</el-tag>
            </div>
            <div class="test-item">
              <label>教练权限:</label>
              <el-tag type="info" size="small">无权限</el-tag>
            </div>
          </div>
        </el-card>

        <!-- 操作工具调试卡片 -->
        <GlassCardWithHeader 
          title="开发工具" 
          icon="⚡" 
          type="success"
        >
          <ActionButtons 
            :actions="debugActions" 
            layout="grid" 
            :columns="2"
            @actionClick="handleDebugAction"
          />
        </GlassCardWithHeader>

        <!-- 高级调试功能卡片 -->
        <GlassCardWithHeader 
          title="高级调试" 
          icon="🔬" 
          type="default"
        >
          <DebugCardContent type="grid">
            <StatusGrid :items="advancedDebugItems" />
            <div style="margin-top: 16px;">
              <ActionButtons 
                :actions="advancedActions" 
                layout="horizontal"
                @actionClick="handleAdvancedAction"
              />
            </div>
          </DebugCardContent>
        </GlassCardWithHeader>

        <!-- 带有header操作的调试卡片 -->
        <GlassCardWithHeader 
          title="角色控制台" 
          icon="🎭" 
          type="primary"
        >
          <template #actions>
            <el-tag type="success" size="small">
              开发者模式
            </el-tag>
          </template>
          <StatusGrid :items="roleStatusItems" />
        </GlassCardWithHeader>
      </DebugCardsContainer>
      
      <!-- 使用说明 -->
      <div class="usage-note">
        <h3>💡 使用说明</h3>
        <p>这些调试卡片组件与DevToolsView中的样式完全一致，可以直接替换使用：</p>
        <ul>
          <li><strong>DebugCard</strong>：主卡片容器，支持不同类型的头部主题</li>
          <li><strong>StatusGrid</strong>：状态信息网格，支持标签、状态、文本等类型</li>
          <li><strong>StorageInfo</strong>：存储信息展示，支持文本域、状态标签等</li>
          <li><strong>ActionButtons</strong>：操作按钮组，支持水平、垂直、网格布局</li>
          <li><strong>DebugCardContent</strong>：卡片内容容器，提供统一的布局样式</li>
        </ul>
      </div>
    </div>

    <!-- 动画效果测试 -->
    <div class="test-section" id="animations">
      <h2 class="section-title">⚡ 动画效果测试</h2>
      <div class="animations-grid">
        <DemoCard
          title="光晕扫过效果"
          subtitle=".shimmer-effect"
          icon="💫"
          description="这个卡片具有光晕扫过的动画效果，创造动态的视觉吸引力。"
          type="glass"
          extra-classes="shimmer-effect"
          :tags="[
            { text: '动画', type: 'primary' },
            { text: '视觉效果', type: 'secondary' }
          ]"
        />
        
        <DemoCard
          title="脉冲效果"
          subtitle=".pulse-effect"
          icon="💓"
          description="这个卡片具有脉冲动画效果，适合需要吸引注意力的内容。"
          type="glass"
          extra-classes="pulse-effect"
          :tags="[
            { text: '脉冲', type: 'warning' },
            { text: '交互', type: 'primary' }
          ]"
        />
        
        <DemoCard
          title="悬浮效果"
          subtitle=".hover-lift"
          icon="🚀"
          description="鼠标悬浮时这个卡片会上升，提供直观的交互反馈。"
          type="glass"
          extra-classes="hover-lift"
          :tags="[
            { text: '悬浮', type: 'success' },
            { text: '反馈', type: 'secondary' }
          ]"
        />

        <DemoCard
          title="淡入上升"
          subtitle=".fade-in-up"
          icon="📈"
          description="页面加载时的淡入上升动画，创造优雅的入场效果。"
          type="glass"
          extra-classes="fade-in-up"
          :tags="[
            { text: '淡入', type: 'info' },
            { text: '入场', type: 'primary' }
          ]"
        />

        <DemoCard
          title="延迟动画"
          subtitle=".fade-in-delay-1"
          icon="⏰"
          description="带有延迟的动画效果，可以创造层次感的动画序列。"
          type="glass-enhanced"
          extra-classes="fade-in-delay-1"
          :tags="[
            { text: '延迟', type: 'warning' },
            { text: '层次', type: 'secondary' }
          ]"
        />

        <DemoCard
          title="组合动画"
          subtitle=".slide-in-up.fade-in-delay-2"
          icon="🎬"
          description="多种动画效果的组合使用，创造丰富的视觉体验。"
          type="glass"
          extra-classes="slide-in-up fade-in-delay-2"
          :tags="[
            { text: '组合', type: 'primary' },
            { text: '多效果', type: 'success' }
          ]"
        />
      </div>
    </div>

    <!-- 工具类测试 -->
    <div class="test-section" id="utilities">
      <h2 class="section-title">🛠️ 工具类测试</h2>
      <div class="utilities-demo">
        <div class="content-card">
          <h4>布局工具类</h4>
          <div class="utility-examples">
            <div class="flex flex-between mb-lg p-md rounded-lg" style="background: var(--white-alpha-10);">
              <span class="text-white">flex-between</span>
              <span class="text-white-80">两端对齐</span>
            </div>
            
            <div class="flex flex-center mb-lg p-md rounded-lg" style="background: var(--white-alpha-10);">
              <span class="text-white">flex-center (居中对齐)</span>
            </div>
            
            <div class="flex flex-col mb-lg p-md rounded-lg" style="background: var(--white-alpha-10);">
              <span class="text-white mb-sm">flex-col</span>
              <span class="text-white-80">垂直排列</span>
            </div>
          </div>
        </div>

        <div class="content-card">
          <h4>间距工具类</h4>
          <div class="utility-examples">
            <div class="mb-lg">
              <div class="p-xs rounded-md" style="background: var(--white-alpha-15);">
                <span class="text-white text-sm">p-xs (4px)</span>
              </div>
            </div>
            <div class="mb-lg">
              <div class="p-md rounded-md" style="background: var(--white-alpha-15);">
                <span class="text-white text-sm">p-md (12px)</span>
              </div>
            </div>
            <div class="mb-lg">
              <div class="p-lg rounded-md" style="background: var(--white-alpha-15);">
                <span class="text-white text-sm">p-lg (16px)</span>
              </div>
            </div>
          </div>
        </div>

        <div class="content-card">
          <h4>文本工具类</h4>
          <div class="utility-examples">
            <p class="text-white text-xs mb-sm">text-white text-xs (12px)</p>
            <p class="text-white text-sm mb-sm">text-white text-sm (13px)</p>
            <p class="text-white text-md mb-sm">text-white text-md (14px)</p>
            <p class="text-white text-lg mb-sm">text-white text-lg (16px)</p>
            <p class="text-white text-xl mb-sm">text-white text-xl (18px)</p>
            <p class="text-white-80 text-md mb-sm">text-white-80 (80% 透明度)</p>
            <p class="text-white font-bold text-md">text-white font-bold</p>
          </div>
        </div>

        <div class="content-card">
          <h4>视觉效果工具类</h4>
          <div class="utility-examples">
            <div class="mb-lg">
              <div class="p-md rounded-sm shadow-sm" style="background: var(--white-alpha-15);">
                <span class="text-white text-sm">rounded-sm shadow-sm</span>
              </div>
            </div>
            <div class="mb-lg">
              <div class="p-md rounded-lg shadow-md" style="background: var(--white-alpha-15);">
                <span class="text-white text-sm">rounded-lg shadow-md</span>
              </div>
            </div>
            <div class="mb-lg">
              <div class="p-md rounded-xl shadow-lg" style="background: var(--white-alpha-15);">
                <span class="text-white text-sm">rounded-xl shadow-lg</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 实际应用示例 -->
    <div class="test-section">
      <h2 class="section-title">💼 实际应用示例</h2>
      <div class="application-examples">
        <DemoCard
          title="学生仪表盘预览"
          subtitle="真实应用场景展示"
          icon="👨‍🎓"
          type="content"
        >
          <template #actions>
            <router-link to="/dashboard" class="btn-modern btn-primary btn-small">查看完整版</router-link>
          </template>
          
          <div class="mini-dashboard">
            <div class="mini-welcome-banner">
              <h4 class="welcome-title" style="font-size: var(--font-size-xl);">欢迎回来，张同学</h4>
              <div class="today-overview" style="gap: var(--spacing-lg);">
                <div class="overview-item">
                  <span class="overview-icon">📅</span>
                  <div>
                    <div class="overview-value">3</div>
                    <div class="overview-label">今日课程</div>
                  </div>
                </div>
                <div class="overview-item">
                  <span class="overview-icon">⏰</span>
                  <div>
                    <div class="overview-value">2</div>
                    <div class="overview-label">待完成</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </DemoCard>

        <DemoCard
          title="登录页面预览"
          subtitle="认证界面应用示例"
          icon="🔐"
          type="content"
        >
          <template #actions>
            <router-link to="/login" class="btn-modern btn-primary btn-small">查看完整版</router-link>
          </template>
          
          <div class="mini-login">
            <div class="form-glass" style="max-width: 300px; margin: 0 auto;">
              <h4 style="color: white; text-align: center; margin-bottom: var(--spacing-lg);">系统登录</h4>
              <FormInput
                placeholder="用户名"
                icon-left="👤"
                style="margin-bottom: var(--spacing-md);"
              />
              <FormInput
                type="password"
                placeholder="密码"
                icon-left="🔒"
                style="margin-bottom: var(--spacing-lg);"
              />
              <ModernButton text="登录" type="primary" :block="true" />
            </div>
          </div>
        </DemoCard>
      </div>
    </div>

    <!-- 页面底部 -->
    <div class="page-footer-section">
      <div class="page-footer-text">
        🎨 设计系统测试完成 - 基于 Glassmorphism 设计理念
      </div>
      <div class="page-footer-links">
        <router-link to="/navigation-test">
          <ModernButton text="返回导航" type="secondary" size="small" />
        </router-link>
        <router-link to="/dashboard">
          <ModernButton text="查看仪表盘" type="primary" size="small" />
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import GlassDisplayCard from '@/components/GlassDisplayCard.vue'
import ModernButton from '@/components/ModernButton.vue'
import FormInput from '@/components/FormInput.vue'
import TagBadge from '@/components/TagBadge.vue'
import GlassCardWithHeader from '@/components/GlassCardWithHeader.vue'
import StatusGrid from '@/components/StatusGrid.vue'
import StorageInfo from '@/components/StorageInfo.vue'
import ActionButtons from '@/components/ActionButtons.vue'
import DebugCardsContainer from '@/components/DebugCardsContainer.vue'
import DebugCardContent from '@/components/DebugCardContent.vue'

export default {
  name: 'DesignLanguageShowcase',
  components: {
    GlassDisplayCard,
    DemoCard: GlassDisplayCard, // 使用别名支持旧的组件名
    ModernButton,
    FormInput,
    TagBadge,
    GlassCardWithHeader,
    StatusGrid,
    StorageInfo,
    ActionButtons,
    DebugCardsContainer,
    DebugCardContent
  },
  data() {
    return {
      // 用户状态测试数据
      userStatusItems: [
        { label: '登录状态', type: 'status', status: true, trueText: '已登录', falseText: '未登录' },
        { label: '用户角色', type: 'tag', value: '开发者', tagType: 'primary' },
        { label: '用户名', value: 'admin@example.com' },
        { label: '用户ID', value: '12345' },
        { label: '校区ID', value: 'campus-001' }
      ],
      
      // 系统环境测试数据
      systemEnvItems: [
        { label: '浏览器', type: 'tag', value: 'Chrome 119', tagType: 'info' },
        { label: '视口大小', value: '1920x1080' },
        { label: '在线状态', type: 'status', status: true, trueText: '在线', falseText: '离线' },
        { label: 'Cookie启用', type: 'status', status: true, trueText: '启用', falseText: '禁用' },
        { label: '本地存储', type: 'status', status: true, trueText: '可用', falseText: '不可用' }
      ],
      
      // 权限测试数据
      permissionItems: [
        { label: '超级管理员', type: 'status', status: false, trueText: '有权限', falseText: '无权限' },
        { label: '校区管理员', type: 'status', status: true, trueText: '有权限', falseText: '无权限' },
        { label: '学员权限', type: 'status', status: false, trueText: '有权限', falseText: '无权限' },
        { label: '教练权限', type: 'status', status: false, trueText: '有权限', falseText: '无权限' }
      ],

      // 本地存储数据
      storageItems: [
        { 
          label: 'Token存储', 
          type: 'status', 
          status: true, 
          trueText: '已设置', 
          falseText: '未设置' 
        },
        { 
          label: 'Token值', 
          type: 'textarea', 
          value: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c',
          placeholder: 'Token内容',
          rows: 3
        },
        { 
          label: '用户信息', 
          type: 'textarea', 
          value: JSON.stringify({
            id: 1,
            username: 'demo_user',
            role: 'student',
            campus_id: 1,
            email: 'demo@example.com'
          }, null, 2),
          placeholder: '用户信息JSON',
          rows: 6
        }
      ],
      
      // 调试操作按钮
      debugActions: [
        { text: '刷新数据', type: 'primary', action: 'refresh' },
        { text: '重置状态', type: 'warning', action: 'reset' },
        { text: '导出报告', type: 'info', action: 'export' },
        { text: '清理缓存', type: 'danger', action: 'clear' }
      ],

      // 高级调试数据
      advancedDebugItems: [
        { label: 'CPU使用率', value: '45%', type: 'tag', tagType: 'warning' },
        { label: '内存占用', value: '2.3GB', type: 'tag', tagType: 'info' },
        { label: '网络状态', type: 'status', status: true, trueText: '正常', falseText: '异常' },
        { label: '缓存大小', value: '156MB', type: 'text' }
      ],

      // 高级操作按钮
      advancedActions: [
        { text: '性能分析', type: 'info', action: 'performance' },
        { text: '内存检测', type: 'warning', action: 'memory' },
        { text: '网络监控', type: 'success', action: 'network' }
      ],

      // 角色状态数据
      roleStatusItems: [
        { label: '当前角色', value: '开发者', type: 'tag', tagType: 'primary' },
        { label: '权限级别', value: '超级管理员', type: 'tag', tagType: 'danger' },
        { label: '登录状态', type: 'status', status: true, trueText: '已认证', falseText: '未认证' },
        { label: '会话时长', value: '2小时30分', type: 'text' }
      ]
    }
  },
  methods: {
    handleDebugAction({ action }) {
      console.log('调试操作:', action.action, action.text);
      // 这里可以添加具体的调试操作逻辑
      switch (action.action) {
        case 'refresh':
          this.refreshDebugData();
          break;
        case 'reset':
          this.resetDebugStatus();
          break;
        case 'export':
          this.exportDebugReport();
          break;
        case 'clear':
          this.clearDebugCache();
          break;
      }
    },
    
    refreshDebugData() {
      // 模拟刷新数据
      console.log('刷新调试数据');
    },
    
    resetDebugStatus() {
      // 模拟重置状态
      console.log('重置调试状态');
    },
    
    exportDebugReport() {
      // 模拟导出报告
      console.log('导出调试报告');
    },
    
    clearDebugCache() {
      // 模拟清理缓存
      console.log('清理调试缓存');
    },

    handleAdvancedAction({ action }) {
      console.log('高级调试操作:', action.action, action.text);
      // 高级调试操作逻辑
      switch (action.action) {
        case 'performance':
          this.performanceAnalysis();
          break;
        case 'memory':
          this.memoryDetection();
          break;
        case 'network':
          this.networkMonitoring();
          break;
        default:
          console.log('未知高级操作:', action.action);
      }
    },

    performanceAnalysis() {
      console.log('开始性能分析...');
      // 模拟性能分析逻辑
    },

    memoryDetection() {
      console.log('检测内存使用情况...');
      // 模拟内存检测逻辑
    },

    networkMonitoring() {
      console.log('监控网络请求...');
      // 模拟网络监控逻辑
    }
  }
}
</script>

<style scoped>
/* 设计语言样板间主体样式 */
.design-language-showcase {
  min-height: 100vh;
  padding: var(--spacing-2xl);
  margin: 0;
  background: transparent;
  width: 100%;
  position: relative;
}

/* Hero区域样式 */
.design-language-hero {
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.15) 0%, 
    rgba(118, 75, 162, 0.15) 50%, 
    rgba(240, 147, 251, 0.15) 100%);
  backdrop-filter: var(--blur-xl);
  border-bottom: 1px solid var(--white-alpha-20);
  padding: var(--spacing-5xl) 0 var(--spacing-4xl) 0;
  text-align: center;
  position: relative;
  overflow: hidden;
  margin: calc(-1 * var(--spacing-2xl)) calc(-1 * var(--spacing-2xl)) var(--spacing-2xl) calc(-1 * var(--spacing-2xl));
}

.design-language-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
  pointer-events: none;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
  padding: 0 var(--spacing-2xl);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm);
  background: var(--white-alpha-25);
  border: 1px solid var(--white-alpha-40);
  border-radius: var(--radius-full);
  padding: var(--spacing-md) var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
  backdrop-filter: var(--blur-lg);
  color: white;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  box-shadow: var(--shadow-md);
  transition: var(--transition-normal);
}

.hero-badge:hover {
  background: var(--white-alpha-30);
  border-color: var(--white-alpha-50);
  transform: translateY(-1px);
  box-shadow: var(--shadow-lg);
}

.badge-icon {
  font-size: var(--font-size-lg);
}

.hero-title {
  font-size: var(--font-size-5xl);
  font-weight: var(--font-weight-extrabold);
  color: white;
  margin: 0 0 var(--spacing-2xl) 0;
  text-shadow: 0 2px 4px var(--black-alpha-30);
  line-height: 1.2;
}

.hero-description {
  font-size: var(--font-size-lg);
  color: var(--white-alpha-90);
  line-height: 1.6;
  margin: 0 0 var(--spacing-4xl) 0;
  text-shadow: 0 1px 2px var(--black-alpha-20);
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: var(--spacing-4xl);
  flex-wrap: wrap;
  margin-top: var(--spacing-2xl);
}

.stat-item {
  text-align: center;
  padding: var(--spacing-lg);
  background: var(--white-alpha-10);
  border-radius: var(--radius-lg);
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  transition: var(--transition-normal);
  min-width: 120px;
}

.stat-item:hover {
  background: var(--white-alpha-15);
  border-color: var(--white-alpha-30);
  transform: translateY(-2px);
}

.stat-number {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-extrabold);
  color: white;
  margin-bottom: var(--spacing-xs);
  text-shadow: 0 2px 4px var(--black-alpha-30);
  background: linear-gradient(135deg, #ffffff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--white-alpha-80);
  font-weight: var(--font-weight-medium);
}

/* 设计原则区域 */
.design-principles {
  padding: var(--spacing-3xl) var(--spacing-2xl);
  text-align: center;
  max-width: 1200px;
  margin: 0 auto var(--spacing-2xl) auto;
}

.design-principles h2 {
  color: white;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-xl) 0;
  text-shadow: 0 2px 4px var(--black-alpha-30);
}

.principles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-2xl);
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-2xl);
}

.principle-card {
  background: var(--white-alpha-10);
  backdrop-filter: var(--blur-md);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-xl);
  padding: var(--spacing-2xl);
  text-align: center;
  transition: var(--transition-normal);
}

.principle-card:hover {
  transform: translateY(-4px);
  background: var(--white-alpha-15);
  border-color: var(--white-alpha-30);
}

.principle-icon {
  font-size: var(--font-size-4xl);
  margin-bottom: var(--spacing-lg);
}

.principle-card h3 {
  color: white;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-md) 0;
}

.principle-card p {
  color: var(--white-alpha-85);
  font-size: var(--font-size-md);
  line-height: 1.5;
  margin: 0;
}

/* 组件导航区域 */
.component-navigation {
  padding: var(--spacing-3xl) 0;
  background: var(--white-alpha-05);
  backdrop-filter: var(--blur-sm);
  text-align: center;
  margin: 0 calc(-1 * var(--spacing-2xl)) var(--spacing-2xl) calc(-1 * var(--spacing-2xl));
}

.component-navigation h2 {
  color: white;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-xl) 0;
  text-shadow: 0 2px 4px var(--black-alpha-30);
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-xl);
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-2xl);
}

.nav-card {
  display: block;
  background: var(--white-alpha-15);
  backdrop-filter: var(--blur-lg);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  text-decoration: none;
  text-align: center;
  transition: var(--transition-normal);
  color: inherit;
}

.nav-card:hover {
  transform: translateY(-6px) scale(1.02);
  background: var(--white-alpha-20);
  border-color: var(--white-alpha-30);
  box-shadow: var(--shadow-xl);
}

.nav-icon {
  font-size: var(--font-size-3xl);
  margin-bottom: var(--spacing-md);
}

.nav-card h3 {
  color: white;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-sm) 0;
}

.nav-card p {
  color: var(--white-alpha-80);
  font-size: var(--font-size-sm);
  margin: 0;
  font-family: 'Courier New', monospace;
}

/* 组件展示区域 */
.component-showcase {
  padding: var(--spacing-3xl) var(--spacing-2xl);
  max-width: 1400px;
  margin: 0 auto;
  text-align: center;
}

.component-showcase h2 {
  color: white;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--spacing-xl) 0;
  text-shadow: 0 2px 4px var(--black-alpha-30);
}

.showcase-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: var(--spacing-2xl);
  margin-top: var(--spacing-2xl);
  padding: 0 var(--spacing-2xl);
}

.component-demo {
  background: var(--white-alpha-10);
  border-radius: var(--radius-md);
  padding: var(--spacing-lg);
  border: 1px solid var(--white-alpha-15);
}

.demo-features {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  margin-top: var(--spacing-md);
}

.feature-tag {
  background: var(--white-alpha-20);
  color: white;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.demo-preview {
  background: var(--white-alpha-05);
  border-radius: var(--radius-md);
  padding: var(--spacing-lg);
  border: 1px dashed var(--white-alpha-20);
}

.demo-content {
  color: var(--white-alpha-85);
}

.status-items {
  margin-top: var(--spacing-md);
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm);
  background: var(--white-alpha-10);
  border-radius: var(--radius-sm);
}

/* 原有样式清理 - 移除旧的测试部分 */
.design-system-test {
  display: none; /* 隐藏旧的测试内容 */
}

.test-section {
  margin-bottom: var(--spacing-3xl);
  scroll-margin-top: var(--spacing-2xl);
}

.section-title {
  color: white;
  margin-bottom: var(--spacing-xl);
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  text-shadow: 0 2px 4px var(--black-alpha-30);
  padding: 0 var(--spacing-2xl);
}

.section-description {
  color: var(--white-alpha-80);
  font-size: var(--font-size-md);
  line-height: 1.6;
  margin-bottom: var(--spacing-2xl);
  text-align: center;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 var(--spacing-2xl);
}

/* 快速导航 */
.quick-nav {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-xl);
  padding: 0 var(--spacing-2xl);
}

.nav-link {
  text-decoration: none;
  transition: var(--transition-normal);
}

.nav-link:hover {
  transform: translateY(-2px);
}

/* 卡片网格 */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: var(--spacing-xl);
  margin-bottom: var(--spacing-2xl);
  padding: 0 var(--spacing-2xl);
}

/* 卡片演示专用样式 */
.card-demo {
  padding: var(--spacing-xl) !important;
}

.demo-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--white-alpha-15);
}

.demo-header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.demo-icon-wrapper {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--white-alpha-25), var(--white-alpha-10));
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-md);
  box-shadow: var(--shadow-sm);
}

.demo-icon {
  color: white;
  font-size: var(--font-size-lg);
}

.demo-header-text h3 {
  margin: 0 0 var(--spacing-xs) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: white;
  text-shadow: 0 1px 2px var(--black-alpha-30);
}

.demo-header-text p {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--white-alpha-80);
  font-family: 'Courier New', monospace;
  background: var(--white-alpha-15);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  display: inline-block;
}

.demo-card-content {
  margin-top: var(--spacing-lg);
}

.demo-card-content p {
  color: var(--white-alpha-85);
  font-size: var(--font-size-md);
  line-height: 1.5;
  margin-bottom: var(--spacing-lg);
}

.demo-tags {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

/* 调试卡片演示样式 */
.debug-demo-card {
  position: relative;
}

.debug-demo-card::before {
  display: none !important; /* 移除顶部渐变线条 */
}

.debug-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--spacing-xl);
}

/* 按钮演示 */
.buttons-demo {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: var(--spacing-xl);
  padding: 0 var(--spacing-2xl);
}

.button-group {
  background: var(--white-alpha-10);
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  backdrop-filter: var(--blur-md);
  border: 1px solid var(--white-alpha-15);
}

.button-group h4 {
  color: white;
  margin-bottom: var(--spacing-lg);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.button-row {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

/* 按钮状态样式 */
.loading-btn {
  position: relative;
}

.loading-spinner {
  display: inline-block;
  width: 12px;
  height: 12px;
  border: 2px solid transparent;
  border-top: 2px solid currentColor;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: var(--spacing-sm);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 表单演示 */
.forms-demo {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: var(--spacing-2xl);
}

.form-field {
  margin-bottom: var(--spacing-lg);
}

.form-field label {
  display: block;
  color: white;
  margin-bottom: var(--spacing-sm);
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
}

.checkbox-group {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.checkbox-group input[type="checkbox"] {
  width: 16px;
  height: 16px;
}

.checkbox-group label {
  margin: 0;
  color: var(--white-alpha-85);
  font-size: var(--font-size-sm);
}

.form-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
}

/* 反馈组件演示 */
.feedback-demo {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.tags-container {
  display: flex;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

/* 进度条演示 */
.progress-demo {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.progress-header label {
  color: white;
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-sm);
}

.progress-text {
  color: var(--white-alpha-80);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}

/* 通知消息演示 */
.notification-demo {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.notification {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-15);
  backdrop-filter: var(--blur-sm);
}

.notification.success {
  border-color: rgba(103, 194, 58, 0.3);
  background: linear-gradient(135deg, rgba(103, 194, 58, 0.15), var(--white-alpha-10));
}

.notification.warning {
  border-color: rgba(230, 162, 60, 0.3);
  background: linear-gradient(135deg, rgba(230, 162, 60, 0.15), var(--white-alpha-10));
}

.notification.info {
  border-color: rgba(64, 158, 255, 0.3);
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.15), var(--white-alpha-10));
}

.notification-icon {
  font-size: var(--font-size-lg);
  line-height: 1;
}

.notification-content {
  flex: 1;
}

.notification-content strong {
  color: white;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  display: block;
  margin-bottom: var(--spacing-xs);
}

.notification-content p {
  color: var(--white-alpha-80);
  font-size: var(--font-size-xs);
  margin: 0;
  line-height: 1.4;
}

/* 性能指标 */
.performance-metrics {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.metric-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.metric-item label {
  color: white;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.metric-value {
  color: var(--white-alpha-80);
  font-size: var(--font-size-xs);
  text-align: right;
  margin-top: var(--spacing-xs);
}

/* 动画网格 */
.animations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-xl);
}

/* 动画演示专用样式 */
.animation-demo-card {
  min-height: 280px;
  display: flex;
  flex-direction: column;
}

.animation-demo-card .demo-card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.animation-demos {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-lg);
}

.animation-item {
  text-align: center;
  padding: var(--spacing-lg);
  border-radius: var(--radius-md);
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-20);
  transition: all var(--transition-smooth);
}

.animation-item:hover {
  transform: translateY(-2px);
  background: var(--white-alpha-15);
  box-shadow: var(--shadow-lg);
}

.animation-item p {
  margin: var(--spacing-sm) 0 0 0 !important;
  font-size: var(--font-size-sm) !important;
  color: var(--white-alpha-70) !important;
}

.animations-grid .glass-card h4 {
  color: white;
  margin-bottom: var(--spacing-md);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.animations-grid .glass-card p {
  color: var(--white-alpha-80);
  margin-bottom: var(--spacing-md);
  font-size: var(--font-size-sm);
  line-height: 1.5;
}

.animations-grid .glass-card code {
  display: inline-block;
  background: var(--white-alpha-20);
  color: #64ffda;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-family: 'Courier New', monospace;
}

/* 工具类演示 */
.utilities-demo {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var(--spacing-xl);
}

.utility-examples {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

/* 应用示例 */
.application-examples {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: var(--spacing-xl);
}

.mini-dashboard {
  margin-top: var(--spacing-lg);
}

.mini-welcome-banner {
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-15);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  backdrop-filter: var(--blur-md);
}

.mini-login {
  margin-top: var(--spacing-lg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: var(--font-size-4xl);
    line-height: 1.3;
  }
  
  .hero-description {
    font-size: var(--font-size-md);
    margin-bottom: var(--spacing-3xl);
  }
  
  .hero-stats {
    gap: var(--spacing-2xl);
    margin-top: var(--spacing-xl);
  }
  
  .stat-item {
    min-width: 100px;
    padding: var(--spacing-md);
  }
  
  .stat-number {
    font-size: var(--font-size-2xl);
  }
  
  .design-language-hero {
    padding: var(--spacing-4xl) 0 var(--spacing-3xl) 0;
  }
  
  .hero-content {
    padding: 0 var(--spacing-lg);
  }
  
  .design-system-test {
    padding: var(--spacing-lg);
  }
  
  .cards-grid {
    grid-template-columns: 1fr;
  }
  
  .buttons-demo {
    grid-template-columns: 1fr;
  }
  
  .forms-demo {
    grid-template-columns: 1fr;
  }
  
  .feedback-demo {
    grid-template-columns: 1fr;
  }
  
  .utilities-demo {
    grid-template-columns: 1fr;
  }
  
  .application-examples {
    grid-template-columns: 1fr;
  }
  
  .animations-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-nav {
    justify-content: center;
  }
  
  .button-row {
    justify-content: center;
  }
  
  .form-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .button-row {
    flex-direction: column;
    align-items: center;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .quick-nav {
    flex-direction: column;
    align-items: center;
  }
}

/* 使用说明样式 */
.usage-note {
  margin-top: var(--spacing-3xl);
  padding: var(--spacing-2xl);
  background: rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(255, 255, 255, 0.15);
  backdrop-filter: var(--blur-md);
}

.usage-note h3 {
  margin: 0 0 var(--spacing-lg) 0;
  color: white;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
}

.usage-note p {
  color: var(--white-alpha-85);
  font-size: var(--font-size-md);
  line-height: 1.6;
  margin-bottom: var(--spacing-lg);
}

.usage-note ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.usage-note li {
  color: var(--white-alpha-85);
  font-size: var(--font-size-md);
  line-height: 1.6;
  margin-bottom: var(--spacing-md);
  padding-left: var(--spacing-lg);
  position: relative;
}

.usage-note li::before {
  content: '•';
  color: #667eea;
  font-weight: bold;
  position: absolute;
  left: 0;
  top: 0;
}

.usage-note strong {
  color: white;
  font-weight: var(--font-weight-bold);
}

/* 使用说明响应式调整 */
@media (max-width: 768px) {
  .usage-note {
    padding: var(--spacing-xl);
    margin-top: var(--spacing-2xl);
  }
  
  .usage-note h3 {
    font-size: var(--font-size-lg);
  }
  
  .usage-note p,
  .usage-note li {
    font-size: var(--font-size-sm);
  }
}

/* 完全覆盖Element Plus Card样式 */
.debug-card.el-card,
.glass-card-with-header.el-card {
  background: rgba(255, 255, 255, 0.15) !important;
  backdrop-filter: blur(16px) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  border-radius: 16px !important;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15) !important;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1) !important;
  overflow: hidden !important;
  position: relative !important;
}

.debug-card.el-card::before,
.glass-card-with-header.el-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
  opacity: 0;
  z-index: 1;
  pointer-events: none;
}

.debug-card.el-card:hover,
.glass-card-with-header.el-card:hover {
  transform: translateY(-8px) scale(1.02) !important;
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
}

.debug-card .el-card__header,
.glass-card-with-header .el-card__header {
  padding: 0 !important;
  border-bottom: none !important;
  background: transparent !important;
}

.debug-card .el-card__body,
.glass-card-with-header .el-card__body {
  padding: 20px !important;
  margin: 0 !important;
  background: transparent !important;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 16px;
  color: #1a202c;
  background: rgba(255, 255, 255, 0.8);
  margin: -20px -20px 20px -20px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;
}

.card-icon {
  font-size: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.permission-tests {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.test-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.test-item:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
}

.test-item label {
  font-weight: 600;
  color: #2d3748;
  font-size: 14px;
}

/* GlassCardWithHeader组件样式 - 与DevToolsView完全一致 */
.glass-card-with-header {
  background: var(--white-alpha-15) !important;
  backdrop-filter: var(--blur-xl) !important;
  border: 1px solid var(--white-alpha-20) !important;
  border-radius: var(--radius-xl) !important;
  box-shadow: var(--shadow-lg) !important;
  transition: var(--transition-slow) !important;
  overflow: hidden !important;
  position: relative !important;
}

.glass-card-with-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
  opacity: 0;
  z-index: 1;
  pointer-events: none;
}

.glass-card-with-header:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 
    0 20px 50px rgba(0, 0, 0, 0.15),
    0 8px 16px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  border-color: rgba(255, 255, 255, 0.3);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 16px;
  color: #1a202c;
  background: rgba(255, 255, 255, 0.8);
  margin: -20px -20px 20px -20px;
  padding: 16px 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;
}

.card-icon {
  font-size: 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.el-card :deep(.el-card__body) {
  padding: 20px;
}
</style>
