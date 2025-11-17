import {createApp} from 'vue'
import App from './App.vue'
import router from './router/index.js'
import './assets/styles/main.scss'
import 'element-plus/dist/index.css'
import {createPinia} from "pinia";

const pinia = createPinia()
const app = createApp(App)
app.use(pinia)
app.use(router)
app.mount('#app')