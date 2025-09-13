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
          <h3>通用卡片</h3>
          <p>GlassCard 系列</p>
        </a>
        <a href="#utilities" class="nav-card">
          <div class="nav-icon">🔧</div>
          <h3>工具组件</h3>
          <p>布局与辅助</p>
        </a>
      </div>
    </div>

    <!-- 玻璃卡片组件系列展示（重构版） -->
    <CardShowcase id="glass-cards" title="🎴 玻璃卡片组件系列"
      description="按层次展示已封装好的卡片组件：基础 Base、布局 Layout、专用 Specialized、展示 Showcase。示例最小可行，便于对照与复用。" layout="fixed-3">

      <!-- Base 基础层 -->
      <GlassDisplayCard title="GlassCardBase" subtitle="base/GlassCardBase.vue" icon="🏗️" variant="base"
        description="统一的玻璃态卡片基础：标题/副标题/图标/描述/标签/插槽 与 5 种变体样式。" :tags="[
          { text: 'Base', type: 'primary' }, { text: '5变体', type: 'info' }
        ]">
        <div class="variant-grid">
          <div class="mini-card variant-base">base</div>
          <div class="mini-card variant-display">display</div>
          <div class="mini-card variant-content">content</div>
          <div class="mini-card variant-enhanced">enhanced</div>
          <div class="mini-card variant-minimal">minimal</div>
        </div>
      </GlassDisplayCard>

      <GlassDisplayCard title="GlassCard" subtitle="base/GlassCard.vue" icon="🎴" variant="display"
        description="标准功能卡片：头部 actions 与底部 footer 插槽，满足通用信息容器场景。">
        <div class="card-preview">
          <div class="card-header-demo"><span class="demo-icon">🔧</span><span class="demo-title">标准卡片</span></div>
          <div class="card-content-demo">主内容区域</div>
          <div class="card-footer-demo"><button class="demo-btn">操作</button></div>
        </div>
      </GlassDisplayCard>

      <GlassDisplayCard title="GlassCardWithHeader" subtitle="base/GlassCardWithHeader.vue" icon="🎯" variant="content"
        description="带独立头部结构的卡片，向后兼容，适合需要明显头部区块的场景。">
        <div class="api-compatibility">
          <div class="compat-item">✅ 向后兼容</div>
          <div class="compat-item">🔄 API 统一</div>
          <div class="compat-item">📦 开箱即用</div>
        </div>
      </GlassDisplayCard>

      <GlassDisplayCard title="GlassCardBody" subtitle="base/GlassCardBody.vue" icon="📄" variant="minimal"
        description="独立的卡片内容容器，支持 grid/form/actions/flex/status 等布局。">
        <div class="layout-grid">
          <div class="layout-item">grid</div>
          <div class="layout-item">form</div>
          <div class="layout-item">actions</div>
          <div class="layout-item">flex</div>
          <div class="layout-item">status</div>
        </div>
      </GlassDisplayCard>

      <!-- Layout 布局层 -->
      <GlassDisplayCard title="GlassCardsGrid" subtitle="layout/GlassCardsGrid.vue" icon="📐" variant="enhanced"
        description="卡片网格系统：auto 自适应 / fixed 固定列。">
        <div class="grid-demo-container">
          <div class="grid-demo">
            <div class="grid-label">auto 自适应</div>
            <div class="mini-grid auto-grid">
              <div class="mini-item"></div>
              <div class="mini-item"></div>
              <div class="mini-item"></div>
            </div>
          </div>
          <div class="grid-demo">
            <div class="grid-label">fixed-3 固定列</div>
            <div class="mini-grid fixed-grid">
              <div class="mini-item"></div>
              <div class="mini-item"></div>
              <div class="mini-item"></div>
            </div>
          </div>
        </div>
      </GlassDisplayCard>

      <!-- Specialized 专用层 -->
      <GlassDisplayCard title="GlassDisplayCard" subtitle="specialized/GlassDisplayCard.vue" icon="🎨" variant="display"
        description="用于内容介绍与特性说明的展示卡片，完整信息结构与标签支持。">
        <div class="feature-list simple">
          <div class="feature-item">✨ 标题/副标题/图标</div>
          <div class="feature-item">📝 描述与标签</div>
          <div class="feature-item">🎨 5种视觉变体</div>
        </div>
      </GlassDisplayCard>

      <!-- 真实组件演示：GlassHeaderCard（替代旧说明卡片） -->
      <GlassHeaderCard title="GlassHeaderCard" icon="💼">
        <div class="feature-list simple">
          <div class="feature-item">✅ 白色头部样式，信息层次清晰</div>
          <div class="feature-item">🧩 headerActions 插槽：放置搜索、筛选、操作按钮</div>
          <div class="feature-item">📦 与 Element Plus 卡片配合，适合面板/设置/项目页</div>
          <div class="feature-item">📱 响应式：小屏头部纵向排列，内容区留白合理</div>
        </div>
        <div class="path-badge">
          <span class="type-label">specialized/GlassHeaderCard.vue</span>
        </div>
        <template #headerActions>
          <button class="demo-btn" style="font-size:12px;">操作</button>
        </template>
      </GlassHeaderCard>

      <!-- Showcase 展示层 -->
      <GlassDisplayCard title="CardShowcase" subtitle="showcase/CardShowcase.vue" icon="🎪" variant="minimal"
        description="文档/设计系统中的展示容器，与网格组合统一排版。">
        <div class="showcase-demo">
          <div class="showcase-header">
            <div class="demo-title">示例标题</div>
            <div class="demo-desc">示例描述</div>
          </div>
          <div class="showcase-grid">
            <div class="showcase-item">A</div>
            <div class="showcase-item">B</div>
            <div class="showcase-item">C</div>
          </div>
        </div>
      </GlassDisplayCard>

    </CardShowcase>

    <!-- 按钮组件系列展示 -->
    <CardShowcase id="buttons" title="🔘 现代按钮组件系列" description="完整的按钮组件系统，支持多种类型、尺寸、状态和交互效果，确保一致的用户体验" layout="fixed-2">
      <!-- 语义化按钮 -->
      <GlassDisplayCard title="语义化按钮系统" subtitle="Semantic Button Types" icon="🎨" variant="display"
        description="基于语义的按钮类型系统，通过颜色和样式传达不同的操作意图。" :tags="[
          { text: '语义化', type: 'primary' },
          { text: '一致性', type: 'success' }
        ]">
        <div class="button-showcase">
          <div class="button-row">
            <ModernButton text="主要" type="primary" size="small" />
            <ModernButton text="次要" type="secondary" size="small" />
            <ModernButton text="成功" type="success" size="small" />
            <ModernButton text="警告" type="warning" size="small" />
            <ModernButton text="危险" type="danger" size="small" />
          </div>
        </div>
        <div class="button-features">
          <div class="feature-item">✅ 5种语义类型</div>
          <div class="feature-item">🎯 明确的操作意图</div>
          <div class="feature-item">🌈 统一的色彩语言</div>
        </div>
      </GlassDisplayCard>

      <!-- 尺寸和状态 -->
      <GlassDisplayCard title="尺寸与状态系统" subtitle="Size & State Management" icon="📐" variant="enhanced"
        description="完整的按钮尺寸和状态管理系统，适应不同场景下的使用需求。" :tags="[
          { text: '多尺寸', type: 'info' },
          { text: '状态管理', type: 'warning' }
        ]">
        <div class="size-states-demo">
          <div class="size-group">
            <h5 style="color: white; margin: 0 0 8px 0; font-size: 12px;">尺寸系统</h5>
            <div class="button-row">
              <ModernButton text="小" type="primary" size="small" />
              <ModernButton text="中" type="primary" size="medium" />
              <ModernButton text="大" type="primary" size="large" />
            </div>
          </div>
          <div class="state-group">
            <h5 style="color: white; margin: 12px 0 8px 0; font-size: 12px;">状态系统</h5>
            <div class="button-row">
              <ModernButton text="正常" type="primary" size="small" />
              <ModernButton text="禁用" type="primary" size="small" :disabled="true" />
              <ModernButton text="加载" type="primary" size="small" :loading="true" />
            </div>
          </div>
        </div>
      </GlassDisplayCard>

      <!-- 图标按钮 -->
      <GlassDisplayCard title="图标与交互增强" subtitle="Icon & Interaction Enhancement" icon="⚡" variant="content"
        description="支持图标的按钮组件，提供更丰富的视觉信息和更好的用户体验。" :tags="[
          { text: '图标支持', type: 'secondary' },
          { text: '交互增强', type: 'primary' }
        ]">
        <div class="icon-buttons-demo">
          <div class="button-row">
            <ModernButton text="添加" type="primary" icon-left="➕" size="small" />
            <ModernButton text="确认" type="success" icon-left="✅" size="small" />
            <ModernButton text="删除" type="danger" icon-left="🗑️" size="small" />
          </div>
          <div class="interaction-features">
            <div class="feature-item">🖱️ 悬浮效果</div>
            <div class="feature-item">🎭 点击反馈</div>
            <div class="feature-item">⌨️ 键盘支持</div>
          </div>
        </div>
      </GlassDisplayCard>

      <!-- 按钮应用场景 -->
      <GlassDisplayCard title="应用场景与最佳实践" subtitle="Use Cases & Best Practices" icon="💡" variant="minimal"
        description="不同场景下的按钮应用指南和最佳实践建议。" :tags="[
          { text: '最佳实践', type: 'info' },
          { text: '应用指南', type: 'success' }
        ]">
        <div class="use-cases">
          <div class="case-item">
            <div class="case-label">表单提交</div>
            <ModernButton text="提交" type="primary" size="small" />
          </div>
          <div class="case-item">
            <div class="case-label">操作确认</div>
            <ModernButton text="确认" type="success" size="small" />
          </div>
          <div class="case-item">
            <div class="case-label">危险操作</div>
            <ModernButton text="删除" type="danger" size="small" />
          </div>
          <div class="case-item">
            <div class="case-label">次要操作</div>
            <ModernButton text="取消" type="secondary" size="small" />
          </div>
        </div>
      </GlassDisplayCard>
    </CardShowcase>

    <!-- 表单输入组件系列展示 -->
    <CardShowcase id="forms" title="📝 表单输入组件系列" description="完整的表单输入组件系统，提供优雅的用户输入体验和强大的验证功能" layout="fixed-2">
      <!-- 基础输入组件 -->
      <GlassDisplayCard title="基础输入组件" subtitle="Form Input Basics" icon="✏️" variant="display"
        description="支持多种输入类型的基础表单组件，提供统一的样式和交互体验。" :tags="[
          { text: '基础组件', type: 'primary' },
          { text: '多类型', type: 'info' }
        ]">
        <div class="form-showcase">
          <FormInput v-model="formData.username" label="用户名" placeholder="请输入用户名" />
          <FormInput v-model="formData.email" label="邮箱地址" type="email" placeholder="请输入邮箱地址" />
          <FormInput v-model="formData.password" label="密码" type="password" placeholder="请输入密码" />
        </div>
      </GlassDisplayCard>

      <!-- 输入状态管理 -->
      <GlassDisplayCard title="状态与验证系统" subtitle="State & Validation" icon="🔍" variant="enhanced"
        description="完善的输入状态管理和验证反馈系统，提供即时的用户反馈。" :tags="[
          { text: '状态管理', type: 'warning' },
          { text: '验证反馈', type: 'success' }
        ]">
        <div class="form-showcase">
          <FormInput v-model="formData.success" label="成功状态" placeholder="验证成功" status="success" />
          <FormInput v-model="formData.error" label="错误状态" placeholder="验证失败" status="error" error="用户名已存在" />
          <FormInput v-model="formData.disabled" label="禁用状态" placeholder="不可编辑" disabled />
        </div>
      </GlassDisplayCard>
    </CardShowcase>

    <!-- 状态与反馈组件系列展示 -->
    <CardShowcase id="feedback" title="💬 状态与反馈组件系列" description="丰富的状态展示和用户反馈组件，提供清晰的信息传达和良好的用户体验" layout="fixed-2">
      <!-- 状态展示组件 -->
      <GlassDisplayCard title="状态网格展示" subtitle="Status Grid System" icon="📊" variant="display"
        description="系统化的状态展示组件，支持多种状态类型和实时数据更新。" :tags="[
          { text: '状态展示', type: 'info' },
          { text: '实时更新', type: 'success' }
        ]">
        <StatusGrid :status="systemStatus" />
      </GlassDisplayCard>

      <!-- 标签徽章系统 -->
      <GlassDisplayCard title="标签徽章系统" subtitle="Tag Badge Components" icon="🏷️" variant="enhanced"
        description="灵活的标签徽章组件，支持多种样式和状态，适用于分类、状态标记等场景。" :tags="[
          { text: '标签系统', type: 'secondary' },
          { text: '多样式', type: 'primary' }
        ]">
        <div class="badge-showcase">
          <div class="badge-row">
            <TagBadge variant="primary">主要标签</TagBadge>
            <TagBadge variant="success">成功标签</TagBadge>
            <TagBadge variant="warning">警告标签</TagBadge>
            <TagBadge variant="danger">危险标签</TagBadge>
          </div>
          <div class="badge-row">
            <TagBadge variant="info">信息标签</TagBadge>
            <TagBadge variant="default">默认标签</TagBadge>
          </div>
        </div>
      </GlassDisplayCard>
    </CardShowcase>

    <!-- 工具与辅助组件系列展示 -->
    <CardShowcase id="utilities" title="🔧 工具与辅助组件系列" description="实用的工具和辅助组件，提供完整的开发和用户体验支持" layout="fixed-2">
      <!-- 存储信息组件 -->
      <GlassDisplayCard title="存储信息管理" subtitle="Storage Management" icon="💾" variant="display"
        description="本地存储管理组件，提供Token、用户信息等重要数据的可视化管理。" :tags="[
          { text: '存储管理', type: 'info' },
          { text: '数据可视化', type: 'success' }
        ]">
        <StorageInfo :storage-items="storageItems" />
      </GlassDisplayCard>

      <!-- 操作按钮组件 -->
      <GlassDisplayCard title="操作按钮组合" subtitle="Action Button Groups" icon="⚡" variant="enhanced"
        description="预定义的操作按钮组合，支持调试、清理、导出等常用操作。" :tags="[
          { text: '操作组合', type: 'warning' },
          { text: '快捷功能', type: 'primary' }
        ]">
        <ActionButtons :actions="debugActions" />
      </GlassDisplayCard>
    </CardShowcase>

    <!-- 使用说明 -->
    <div class="usage-note">
      <h3>🚀 设计系统使用指南</h3>
      <p>本设计系统采用现代化的Glassmorphism风格，为乒乓球训练系统提供统一、优雅的用户界面。</p>
      <ul>
        <li><strong>统一的卡片系统：</strong>基于GlassCardBase的统一卡片组件，支持多种变体和样式</li>
        <li><strong>现代按钮组件：</strong>支持多种类型、尺寸和状态的按钮系统</li>
        <li><strong>完整的表单组件：</strong>包含输入框、状态管理和验证反馈</li>
        <li><strong>丰富的反馈组件：</strong>状态展示、标签徽章等用户反馈组件</li>
        <li><strong>实用的工具组件：</strong>存储管理、操作按钮等开发辅助工具</li>
        <li><strong>响应式设计：</strong>完全适配桌面端、平板端和移动端设备</li>
      </ul>
    </div>
  </div>
</template>

<script>
import { GlassDisplayCard, CardShowcase, GlassHeaderCard } from '@/components/cards'
import ModernButton from '@/components/ModernButton.vue'
import FormInput from '@/components/FormInput.vue'
import TagBadge from '@/components/TagBadge.vue'
import StatusGrid from '@/components/StatusGrid.vue'
import StorageInfo from '@/components/StorageInfo.vue'
import ActionButtons from '@/components/ActionButtons.vue'

export default {
  name: 'DesignLanguageShowcase',
  components: {
    GlassDisplayCard,
    GlassHeaderCard,
    CardShowcase,
    ModernButton,
    FormInput,
    TagBadge,
    StatusGrid,
    StorageInfo,
    ActionButtons
  },
  data() {
    return {
      // 表单数据
      formData: {
        username: '',
        email: '',
        password: '',
        success: '验证成功的输入',
        error: '错误的输入',
        disabled: '这是禁用的输入'
      },

      // 系统状态数据
      systemStatus: [
        {
          label: '系统状态',
          type: 'status',
          status: true,
          trueText: '运行中',
          falseText: '已停止'
        },
        {
          label: '数据库连接',
          type: 'status',
          status: true,
          trueText: '已连接',
          falseText: '连接失败'
        },
        {
          label: 'API服务',
          type: 'status',
          status: true,
          trueText: '正常',
          falseText: '异常'
        },
        {
          label: '缓存服务',
          type: 'status',
          status: false,
          trueText: '可用',
          falseText: '不可用'
        }
      ],

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
/* 卡片系列展示样式（保留在用的简单徽章/路径） */
.type-label {
  background: var(--white-alpha-15);
  color: white;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-family: 'Courier New', monospace;
}

.path-badge {
  margin-top: var(--spacing-xs);
}

/* 按钮展示样式 */
.button-showcase,
.size-states-demo,
.icon-buttons-demo {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.button-row {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  align-items: center;
}

.button-features,
.interaction-features {
  margin-top: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

/* 通用特性列表条目（统一定义见文件靠后处）保留后面的定义，这里删除重复项 */

/* 响应式调整：按钮行等在后文已有更细致规则，这里去除过期项 */

/* 原有样式 */
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

/* 组件展示区域（旧版）样式已移除 */

/* 旧 demo 类（demo-features/feature-tag/demo-preview/demo-content/status-items/status-item）已移除 */

/* 旧演示/调试/动画/工具/应用样式已移除 */

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

  /* 删除旧演示布局的移动端覆盖 */

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

/* 新增样式 - 表单和反馈组件展示 */
.form-showcase {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.badge-showcase {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.badge-row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  align-items: center;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-showcase {
    gap: var(--spacing-md);
  }

  .badge-row {
    justify-content: center;
  }
}

/* 状态卡片样式 */

.status-content .status-tag.success {
  background: linear-gradient(135deg, #48bb78, #38a169);
}

.status-content .status-tag.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.status-content .status-tag.warning {
  background: linear-gradient(135deg, #ed8936, #dd6b20);
}

.status-features {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  margin-top: var(--spacing-lg);
}

.status-features .feature-item {
  background: var(--white-alpha-15);
  color: var(--white-alpha-90);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-md);
  font-size: var(--font-size-xs);
  border: 1px solid var(--white-alpha-20);
  backdrop-filter: var(--blur-sm);
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

/* 新的演示内容样式 */
.demo-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.progress-item {
  background: rgba(255, 255, 255, 0.1);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.task-name {
  font-weight: var(--font-weight-semibold);
  color: white;
  font-size: var(--font-size-sm);
}

.progress-percent {
  font-size: var(--font-size-sm);
  color: var(--white-alpha-80);
  font-family: 'Courier New', monospace;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.demo-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-sm);
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.demo-features .feature-item {
  font-size: var(--font-size-sm);
  color: var(--white-alpha-85);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

/* 新的卡片展示样式 */
.variant-examples {
  margin-top: var(--spacing-md);
}

.variant-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
}

.mini-card {
  background: var(--white-alpha-15);
  padding: var(--spacing-sm);
  border-radius: var(--radius-sm);
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--white-alpha-85);
  border: 1px solid var(--white-alpha-10);
  transition: all 0.2s ease;
}

.mini-card:hover {
  background: var(--white-alpha-20);
  transform: translateY(-2px);
}

.variant-base {
  background: var(--white-alpha-15);
}

.variant-display {
  background: var(--white-alpha-18);
}

.variant-content {
  background: var(--white-alpha-20);
}

.variant-enhanced {
  background: var(--white-alpha-25);
}

.variant-minimal {
  background: var(--white-alpha-10);
}

.feature-note {
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--white-alpha-70);
  font-style: italic;
}

.card-preview {
  background: var(--white-alpha-10);
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--white-alpha-15);
}

.card-header-demo {
  background: rgba(255, 255, 255, 0.1);
  padding: var(--spacing-sm) var(--spacing-md);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  border-bottom: 1px solid var(--white-alpha-15);
}

.demo-icon {
  font-size: var(--font-size-md);
}

/* 统一在下方的 .demo-title 定义，这里删除重复 */

.card-content-demo {
  padding: var(--spacing-md);
  color: var(--white-alpha-85);
  font-size: var(--font-size-sm);
}

.card-footer-demo {
  padding: var(--spacing-sm) var(--spacing-md);
  background: rgba(255, 255, 255, 0.05);
  border-top: 1px solid var(--white-alpha-15);
}

.demo-btn {
  background: var(--white-alpha-20);
  border: 1px solid var(--white-alpha-30);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  cursor: pointer;
}

.api-compatibility {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.compat-item {
  font-size: var(--font-size-sm);
  color: var(--white-alpha-85);
  padding: var(--spacing-xs) var(--spacing-sm);
  background: var(--white-alpha-10);
  border-radius: var(--radius-sm);
  border-left: 3px solid var(--white-alpha-30);
}

.layout-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(60px, 1fr));
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
}

.layout-item {
  background: var(--white-alpha-15);
  padding: var(--spacing-xs);
  border-radius: var(--radius-sm);
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--white-alpha-85);
  border: 1px solid var(--white-alpha-10);
}

.layout-note {
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--white-alpha-70);
  font-style: italic;
}

.grid-demo-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.grid-demo {
  background: var(--white-alpha-05);
  padding: var(--spacing-sm);
  border-radius: var(--radius-sm);
  border: 1px solid var(--white-alpha-10);
}

.grid-label {
  font-size: var(--font-size-xs);
  color: var(--white-alpha-70);
  margin-bottom: var(--spacing-xs);
  text-align: center;
}

.auto-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(40px, 1fr));
  gap: 4px;
}

.fixed-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 4px;
}

.mini-item {
  background: var(--white-alpha-20);
  height: 16px;
  border-radius: 3px;
}

.display-features {
  margin-top: var(--spacing-md);
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

/* 统一的特性条目样式 */
.feature-item {
  font-size: var(--font-size-sm);
  color: var(--white-alpha-85);
  padding: var(--spacing-xs) 0;
}

/* 旧的 Header 临时演示样式已删除，使用真实 GlassHeaderCard 组件展示 */

/* use-cases 的新样式定义保留在此处，无需重复旧规则 */

.showcase-demo {
  background: var(--white-alpha-05);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
  border: 1px solid var(--white-alpha-10);
}

.showcase-header {
  margin-bottom: var(--spacing-md);
  text-align: center;
}

.demo-title {
  font-weight: var(--font-weight-semibold);
  color: white;
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-xs);
}

.demo-desc {
  font-size: var(--font-size-xs);
  color: var(--white-alpha-70);
}

.showcase-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-xs);
}

.showcase-item {
  background: var(--white-alpha-15);
  padding: var(--spacing-sm);
  border-radius: var(--radius-sm);
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--white-alpha-85);
  border: 1px solid var(--white-alpha-10);
}
</style>
