import { fileURLToPath, URL } from 'node:url'
import process from 'node:process'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // 将 .env.* 与 .env.local 变量加载进来（不限制前缀，方便读取非默认前缀）
  const env = loadEnv(mode, process.cwd(), '')

  const host = env.VITE_DEV_SERVER_HOST || '127.0.0.1'
  const port = Number(env.VITE_DEV_SERVER_PORT || '') || 5173

  return {
    plugins: [vue(), vueDevTools()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    // 使 .env 中的开发服务器配置真正生效
    server: {
      host,
      port,
      // 若端口被占用（例如后端也在 8080），默认会自动递增；如需强制该端口可改为 true
      // strictPort: true,
    },
  }
})
