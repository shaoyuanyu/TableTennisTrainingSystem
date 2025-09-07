# 乒乓球培训管理系统

本项目是吉林大学软件工程课程设计的作业，旨在为乒乓球培训机构提供一套完整的信息化管理解决方案。

## 项目概述

随着乒乓球运动的普及，培训机构面临着学员和教练管理效率低下、缺乏有效预约机制以及课程安排混乱等问题。本系统通过数字化管理方式，帮助培训机构提高管理效率，优化用户体验。

### 核心功能

- 用户管理：支持学员、教练和管理员的注册、登录和权限控制
- 课程管理：教练发布课程，管理员审核课程，学员查看和报名
- 预约系统：学员与教练之间的预约安排和状态更新
- 消息通知：系统内消息提醒和通知功能
- 数据统计：管理员查看培训数据和报表

## 技术架构

本项目采用前后端分离的架构模式，通过RESTful API进行通信。

### 前端技术栈

- Vue 3 (基于 Composition API)
- Vue Router 4 - 路由管理
- Pinia - 状态管理
- Element Plus - UI 组件库
- Axios - HTTP 客户端
- Vite - 构建工具

### 后端技术栈

- Kotlin 2.1.10
- Ktor 3.2.3 - Web 框架
- Ktor Server - 服务器端框架
- Exposed 1.0.0-beta-5 - 数据库 ORM 框架
- MySQL 9.1.0 - 数据库
- HikariCP 6.0.0 - 数据库连接池
- Logback 1.5.13 - 日志框架

## 项目结构

```
.
├── backend              # 后端项目目录
│   ├── src              # 源代码目录
│   │   └── main
│   │       ├── kotlin   # Kotlin 源代码
│   │       │   └── io/github/shaoyuanyu/ttts
│   │       │       ├── dto         # 数据传输对象
│   │       │       ├── persistence # 数据访问层
│   │       │       ├── plugins     # Ktor 插件配置
│   │       │       ├── routes      # 路由定义
│   │       │       ├── utils       # 工具类
│   │       │       └── Application.kt # 应用入口
│   │       └── resources           # 资源文件
│   └── build.gradle.kts # 构建配置文件
├── frontend             # 前端项目目录
│   ├── src              # 源代码目录
│   │   ├── assets       # 静态资源
│   │   ├── components   # Vue 组件
│   │   ├── layouts      # 布局组件
│   │   ├── router       # 路由配置
│   │   ├── stores       # 状态管理
│   │   ├── utils        # 工具类
│   │   ├── views        # 页面视图
│   │   ├── App.vue      # 根组件
│   │   └── main.js      # 应用入口
│   ├── package.json     # 依赖配置文件
│   └── vite.config.js   # 构建配置文件
├── documents            # 项目文档目录
│   ├── 迭代01_20250907
│   │   ├── 第一次迭代工作总结.md
│   │   ├── 第一次迭代技术文档.md
│   │   └── 第一次迭代需求分析.md
│   └── README.md
├── tests                # 测试用例目录
└── docker-compose.yml   # Docker 部署配置
```

## 快速开始

### 环境要求

- Node.js >= 20.19.0
- Java JDK >= 17
- MySQL >= 8.0

### 后端运行

1. 配置数据库连接：
   在 `backend/src/main/resources/application.conf` 中修改数据库配置

2. 运行后端服务：
```bash
cd backend
./gradlew run
```

### 前端运行

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

3. 构建生产版本：
```bash
npm run build
```

## 分工

- 需求分析 + 设计文档
- 前端开发
- 后端开发
- 测试(需形成测试计划和测试用例) + 测试报告

## 项目开发计划

### 迭代一（已完成）
- 完成需求分析文档
- 搭建前后端基础框架
- 实现用户注册登录功能
- 实现基本权限控制

## 项目管理

### Git 分支规范
- 主分支(发布分支): `master`
- 开发分支主干: `dev`
- 功能分支: `feature/*`
- 热修复分支: `hotfix/*`

### 项目目录规范

采用 Monorepo 策略进行管理：
- 前端项目目录: `frontend`
- 后端项目目录: `backend`
- 项目文档目录: `documents`
- 测试用例目录: `tests`