import {createApp} from 'vue'
import App from './App.vue'
import router from './router/index.js'
import './assets/styles/main.scss'
import 'element-plus/dist/index.css'
import {createPinia} from "pinia";
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

const pinia = createPinia()
const app = createApp(App)

// 全局注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.mount('#app')