// main.js - 优化版本
import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'

// 导入权限指令
import { permissionDirective } from '@/composables/usePermissions'

const app = createApp(App)

// 配置 Element Plus
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default', // 添加全局尺寸配置
  zIndex: 3000, // 设置初始 z-index
})

// 批量注册图标（更优雅的方式）
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  app.component(`el-icon-${key.toLowerCase()}`, component)
})

// 注册全局权限指令
app.directive('permission', permissionDirective)

// 注册状态管理和路由
app.use(createPinia())
app.use(router)

app.mount('#app')
