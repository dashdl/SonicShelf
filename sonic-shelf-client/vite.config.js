import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from "unplugin-vue-components/resolvers";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  // 跨域代理配置
  server: {
    port: 5173,
    proxy: {
      // 将 /api 请求代理到后端服务
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
        // 移除rewrite配置，保留/api前缀
      }
    }
  },
  css:{
    preprocessorOptions: {
      scss:{
        // 避免循环导入，直接导入variables.scss而不是main.scss
        // 使用with语法导出所有变量，使其在全局可用
        additionalData: `@use "@/assets/styles/variables.scss" as *;`
      }
    }
  }
})
