# 环境配置说明

本项目使用 Vite 的环境变量系统，严格区分不同环境的配置。

## 文件结构

```text
.env                    # 通用配置（所有环境共享）
.env.development       # 开发环境配置
.env.production        # 生产环境配置
.env.local.example     # 本地配置示例
.env.local            # 本地个人配置（不提交到版本控制）
```

## 加载优先级（从高到低）

1. **`.env.local`** - 本地个人配置（最高优先级）
2. **`.env.[mode].local`** - 特定模式的本地配置
3. **`.env.[mode]`** - 特定模式配置（development/production）
4. **`.env`** - 通用配置（最低优先级）

## 配置分类

### 🌐 通用配置 (`.env`)

- 应用基础信息（标题、版本等）
- 默认超时时间
- 分页配置
- 文件上传限制
- 基础功能开关

### 🔧 开发环境 (`.env.development`)

- 开发服务器API地址
- 调试模式开启
- 开发工具配置
- 详细日志级别

### 🚀 生产环境 (`.env.production`)

- 生产API地址
- 性能优化配置
- 监控和分析配置
- 安全策略配置

### 👤 本地个人配置 (`.env.local`)

- 个人开发环境定制
- 本地数据库配置
- 个人调试偏好
- 实验性功能开关

## 使用方法

### 首次使用

```bash
# 复制本地配置模板
cp .env.local.example .env.local

# 根据个人需求编辑 .env.local
vim .env.local
```

### 环境切换

```bash
# 开发环境（默认）
npm run dev

# 生产环境构建
npm run build

# 预览生产构建
npm run preview
```

## 安全注意事项

⚠️ **重要提醒**：

- 不要在任何 `.env` 文件中存储敏感信息（如密钥、密码）
- 敏感配置应通过环境变量或配置管理服务注入
- `.env.local` 文件不会被提交到版本控制
- 生产环境敏感配置应使用 `.env.production.local`

## 变量命名规范

- 所有变量必须以 `VITE_` 开头才能在客户端访问
- 使用大写字母和下划线分隔：`VITE_API_BASE_URL`
- 布尔值使用 `true`/`false` 字符串
- 数字值使用字符串形式

## 示例用法

```javascript
// 在代码中使用环境变量
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
const isDebugMode = import.meta.env.VITE_ENABLE_DEBUG === 'true'
const appTitle = import.meta.env.VITE_APP_TITLE
```
