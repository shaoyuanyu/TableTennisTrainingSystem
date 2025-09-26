# 卡片组件架构说明

## 📁 目录结构

```
src/components/cards/
├── index.js                 # 统一导出文件
├── base/                    # 基础卡片组件
│   ├── GlassCardBase.vue   # 最基础的卡片组件 - 所有其他卡片的基础
│   ├── GlassCard.vue       # 标准功能卡片 - 基于 GlassCardBase
│   ├── GlassCardBody.vue   # 卡片内容布局 - 提供多种内容布局
│   └── GlassCardWithHeader.vue # 带头部的卡片 - 基于 GlassCardBase
├── layout/                  # 布局组件
│   └── GlassCardsGrid.vue  # 卡片网格布局 - 响应式网格容器
├── specialized/             # 专用卡片组件
│   ├── GlassDisplayCard.vue # 展示型卡片 - 用于内容展示
│   └── GlassHeaderCard.vue # 带头部的通用卡片 - 基于 Element Plus，白色头部设计
└── showcase/                # 展示组件
    └── CardShowcase.vue    # 组件展示容器 - 用于设计系统展示
```

## 🏗️ 组件层次关系

### 基础层 (base/)

- **GlassCardBase.vue**: 最核心的基础组件，提供统一的玻璃卡片外观和基础功能
- **GlassCard.vue**: 对 GlassCardBase 的轻量级包装，适用于一般业务场景
- **GlassCardWithHeader.vue**: 对 GlassCardBase 的包装，提供兼容性支持
- **GlassCardBody.vue**: 独立的内容布局组件，可配合任何卡片使用

### 布局层 (layout/)

- **GlassCardsGrid.vue**: 响应式网格布局，为卡片提供统一的排列方式

### 专用层 (specialized/)

- **GlassDisplayCard.vue**: 基于 GlassCardBase，专门用于内容展示的增强型卡片
- **GlassAccentCard.vue**: 基于 Element Plus el-card，带彩色装饰线，用于状态信息显示

### 展示层 (showcase/)

- **CardShowcase.vue**: 设计系统专用，用于展示和测试各种卡片组件

## 📦 使用方式

### 方式1：统一导入

```javascript
import { GlassCard, GlassCardBody, GlassCardsGrid } from '@/components/cards'
```

### 方式2：按需导入

```javascript
import GlassCard from '@/components/cards/base/GlassCard.vue'
import GlassCardsGrid from '@/components/cards/layout/GlassCardsGrid.vue'
```

## 🎯 选择指南

### 选择基础组件

- **GlassCardBase**: 需要完全自定义卡片结构时
- **GlassCard**: 一般业务场景，需要基础卡片功能
- **GlassCardWithHeader**: 需要兼容旧版本API时

### 选择专用组件

- **GlassDisplayCard**: 内容展示、组件介绍、特性说明
- **GlassHeaderCard**: 项目管理、数据面板、设置页面、信息展示、团队协作工具

### 选择布局组件

- **GlassCardsGrid**: 需要响应式排列多个卡片时
- **GlassCardBody**: 需要在卡片内部进行复杂内容布局时

## 🔄 迁移指导

如果您的代码中还在使用旧的导入路径，请按以下方式更新：

```javascript
// 旧路径 → 新路径
'@/components/GlassCard.vue' → '@/components/cards/base/GlassCard.vue'
'@/components/GlassCardBody.vue' → '@/components/cards/base/GlassCardBody.vue'
'@/components/GlassCardWithHeader.vue' → '@/components/cards/base/GlassCardWithHeader.vue'
'@/components/GlassCardsGrid.vue' → '@/components/cards/layout/GlassCardsGrid.vue'
'@/components/GlassDisplayCard.vue' → '@/components/cards/specialized/GlassDisplayCard.vue'
'@/components/cards/GlassStatusCard.vue' → '@/components/cards/specialized/GlassHeaderCard.vue'
'@/components/cards/CardShowcase.vue' → '@/components/cards/showcase/CardShowcase.vue'
```

## ✅ 整理完成项目

- [x] 识别并分类所有卡片组件
- [x] 设计合理的目录结构
- [x] 移动组件到对应目录
- [x] 更新所有导入路径
- [x] 验证功能正常
- [x] 创建统一导出文件
- [x] 编写架构说明文档
