# 乒乓球训练系统 - 设计系统使用指南

## 概述

本设计系统基于 Glassmorphism 设计理念，为整个乒乓球训练系统提供统一的视觉风格和交互体验。

## 设计理念

- **Glassmorphism（玻璃形态）**: 半透明背景 + backdrop-filter 模糊效果
- **现代渐变**: 动态色彩渐变背景
- **微交互**: 丰富的悬停和过渡动画
- **一致性**: 统一的颜色、间距、圆角、阴影系统

## 核心特征

### 1. 颜色系统

```css
/* 主题色彩 */
--primary-color: #409eff;
--success-color: #67c23a;
--warning-color: #e6a23c;
--danger-color: #f56c6c;

/* 透明度层级 */
--white-alpha-10: rgba(255, 255, 255, 0.1);
--white-alpha-15: rgba(255, 255, 255, 0.15);
--white-alpha-20: rgba(255, 255, 255, 0.2);
```

### 2. 间距系统

```css
--spacing-xs: 4px; /* 最小间距 */
--spacing-sm: 8px; /* 小间距 */
--spacing-md: 12px; /* 中等间距 */
--spacing-lg: 16px; /* 大间距 */
--spacing-xl: 20px; /* 超大间距 */
--spacing-2xl: 24px; /* 更大间距 */
--spacing-3xl: 32px; /* 最大间距 */
```

### 3. 圆角系统

```css
--radius-sm: 6px; /* 小圆角 */
--radius-md: 8px; /* 中等圆角 */
--radius-lg: 12px; /* 大圆角 */
--radius-xl: 16px; /* 超大圆角 */
--radius-2xl: 20px; /* 最大圆角 */
```

## 组件使用指南

### 1. 卡片组件

#### 基础玻璃卡片

```html
<div class="glass-card">
  <h3>基础卡片</h3>
  <p>这是一个基础的玻璃形态卡片</p>
</div>
```

#### 增强版玻璃卡片（带动画效果）

```html
<div class="glass-card-enhanced">
  <h3>增强版卡片</h3>
  <p>具有光晕扫过动画效果</p>
</div>
```

#### 内容区域卡片

```html
<div class="content-card">
  <h2>内容标题</h2>
  <p>主要内容区域，适用于页面主体部分</p>
</div>
```

#### 侧边栏卡片

```html
<div class="sidebar-card">
  <h3>侧边栏组件</h3>
  <p>适用于侧边栏区域的小尺寸卡片</p>
</div>
```

### 2. 按钮组件

#### 基础按钮

```html
<button class="btn-modern btn-primary">主要按钮</button>
<button class="btn-modern btn-secondary">次要按钮</button>
<button class="btn-modern btn-success">成功按钮</button>
<button class="btn-modern btn-warning">警告按钮</button>
<button class="btn-modern btn-danger">危险按钮</button>
```

#### 不同尺寸

```html
<button class="btn-modern btn-primary btn-small">小按钮</button>
<button class="btn-modern btn-primary">标准按钮</button>
<button class="btn-modern btn-primary btn-large">大按钮</button>
```

#### 带图标的按钮

```html
<button class="btn-modern btn-primary">
  <el-icon><Plus /></el-icon>
  添加内容
</button>
```

### 3. 标签组件

```html
<span class="tag-glass tag-primary">主要标签</span>
<span class="tag-glass tag-success">成功标签</span>
<span class="tag-glass tag-warning">警告标签</span>
<span class="tag-glass tag-danger">危险标签</span>
```

### 4. 表单组件

#### 玻璃形态表单容器

```html
<div class="form-glass">
  <h2>登录表单</h2>
  <input type="text" class="input-glass" placeholder="用户名" />
  <input type="password" class="input-glass" placeholder="密码" />
  <button class="btn-modern btn-primary">登录</button>
</div>
```

### 5. 进度条组件

```html
<div class="progress-glass" style="height: 12px; width: 100%;">
  <div class="progress-bar" style="width: 75%;"></div>
</div>
```

### 6. 页面布局组件

#### 页面头部

```html
<div class="page-header">
  <h1 class="page-title">页面标题</h1>
  <p class="page-subtitle">页面副标题或描述</p>
</div>
```

## Element Plus 组件适配

为了让 Element Plus 组件与设计系统保持一致，可以使用以下类名：

### 按钮适配

```html
<!-- Element Plus 按钮 + 设计系统样式 -->
<el-button type="primary" class="btn-modern">现代化按钮</el-button>
```

### 卡片适配

```html
<!-- Element Plus 卡片 + 玻璃形态 -->
<el-card class="glass-card-enhanced">
  <template #header>
    <span>卡片标题</span>
  </template>
  卡片内容
</el-card>
```

### 表单适配

```html
<!-- Element Plus 输入框 + 玻璃样式 -->
<el-input class="input-glass" placeholder="请输入内容" />
```

## 动画效果

### 光晕扫过效果

```html
<div class="glass-card shimmer-effect">具有光晕扫过动画的卡片</div>
```

### 脉冲效果

```html
<div class="glass-card pulse-effect">具有脉冲动画的卡片</div>
```

### 悬浮效果

```html
<div class="glass-card hover-lift">鼠标悬浮时会上升的卡片</div>
```

## 工具类

### 布局工具类

```html
<div class="flex flex-between">
  <span>左侧内容</span>
  <span>右侧内容</span>
</div>

<div class="flex flex-center">
  <span>居中内容</span>
</div>
```

### 间距工具类

```html
<div class="mt-lg mb-md p-lg">具有外边距和内边距的元素</div>
```

### 文本工具类

```html
<h1 class="text-white">白色文字</h1>
<p class="text-white-80">80%透明度白色文字</p>
```

### 圆角工具类

```html
<div class="rounded-lg">圆角元素</div>
<div class="rounded-xl">更大圆角元素</div>
```

### 阴影工具类

```html
<div class="shadow-md">中等阴影</div>
<div class="shadow-lg">大阴影</div>
```

## 响应式设计

设计系统内置了响应式断点：

- **手机端** (≤ 768px): 自动调整内边距和字体大小
- **小屏手机** (≤ 480px): 进一步优化布局

## 最佳实践

### 1. 层次结构

- 使用不同的透明度级别来创建视觉层次
- 重要内容使用 `--white-alpha-20` 以上的透明度
- 次要内容使用 `--white-alpha-15` 及以下的透明度

### 2. 动画使用

- 避免过度使用动画，保持微妙和优雅
- 使用 `--transition-normal` (0.3s) 作为标准过渡时间
- 重要操作可以使用 `hover-lift` 增强反馈

### 3. 颜色搭配

- 主要操作使用 `btn-primary`
- 成功状态使用 `btn-success`
- 危险操作使用 `btn-danger`
- 中性操作使用 `btn-secondary`

### 4. 间距规范

- 卡片内部使用 `--spacing-xl` (20px)
- 卡片之间使用 `--spacing-2xl` (24px)
- 小组件内部使用 `--spacing-md` (12px)

## 未来扩展

设计系统支持轻松扩展：

1. **新组件**: 基于现有变量创建新组件
2. **主题定制**: 修改根变量实现主题切换
3. **动画效果**: 添加新的 keyframes 动画
4. **响应式**: 增加新的媒体查询断点

## 示例页面

参考以下页面查看设计系统的实际应用：

- `LoginView.vue` - 登录页面
- `StudentDashboard.vue` - 学生仪表盘
- `DevToolsView.vue` - 开发工具页面
- `CoachRegisterView.vue` - 教练注册页面
