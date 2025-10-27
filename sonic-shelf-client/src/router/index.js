import {createRouter, createWebHistory} from 'vue-router'
import Layout from "@/components/Layout.vue";
import Home from "@/pages/Home.vue";
import UserPanel from "@/components/form/UserPanel.vue";
import LoginForm from "@/components/form/LoginForm.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/', component:Layout,
            children: [
                {path: '', component:Home},
            ]
        },
        {path: '/:pathMatch(.*)', redirect: '/notFound'},
        {path: '/notFound', component: import('../pages/404.vue')},
        // {path: '/login', component: import('../views/Login.vue')},

    ],
})

export default router
