# frontend

This template should help get you started developing with Vue 3 in Vite.

## Recommended IDE Setup

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (and disable Vetur).

## Customize configuration

See [Vite Configuration Reference](https://vite.dev/config/).

## Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

### Run Unit Tests with [Vitest](https://vitest.dev/)

```sh
npm run test:unit
```

### Lint with [ESLint](https://eslint.org/)

```sh
npm run lint
```

## 设计语言与管理页面优化说明

本项目集成自研 Glassmorphism 设计语言（参见 `DesignSystemTestView.vue`）。2025-09 对超级管理员 **校区管理** 页面进行了 UI 优化，核心改动：

- 采用 `GlassHeaderCard` 统一页面头部与操作区域，提升层次性与留白一致性。
- 引入搜索输入框（即时本地过滤，支持名称/地址/联系人/电话/邮箱模糊匹配）。
- 表格第一列整合主校区标签（使用 `TagBadge`），并兼容后端既有 `campusName` / `name` 字段。
- 空状态文案统一：`暂无校区` + 引导动作提示。
- 操作列按钮改为 link 风格 + 分隔线，降低视觉噪音。
- 删除确认对话框字段修正：使用 `campus.campusName || campus.name`，避免旧字段造成展示异常。
- 响应式：窄屏下操作区域转为纵向排布，主操作按钮靠下对齐。

后续可扩展：

1. 状态筛选（预留注释位）。
2. 批量操作（可在 headerActions 添加复选与批量按钮）。
3. 服务端分页与排序（现为一次性拉取 100 条再前端过滤）。
4. 编辑功能实现（当前 `isEdit` 分支仅提示未实现）。

欢迎在保持设计 token 语义的前提下继续扩展，新增样式请优先复用 CSS 变量（`--spacing-*`, `--font-size-*`, `--white-alpha-*` 等）。
