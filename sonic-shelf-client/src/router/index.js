import {createRouter, createWebHistory} from 'vue-router'
import Layout from "@/components/Layout.vue";
import Home from "@/pages/Home.vue";
import UserPanel from "@/components/form/UserPanel.vue";
import LoginForm from "@/components/form/LoginForm.vue";
import Profile from "@/pages/Profile.vue";
import upload from "@/pages/upload.vue";
import {useUserStore} from "@/store/userStore.js";
import ProfileSettings from "@/pages/ProfileSettings.vue";
import GridList from "@/components/list/GridList.vue";
import TableList from "@/components/list/TableList.vue";
import Playlist from "@/pages/Playlist.vue";
import InfiniteTable from "@/components/list/InfiniteTable.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/', component: Layout,
            children: [
                {path: '', component: InfiniteTable},
                {path: '/profile', component: Profile},
                {path: '/profile-settings', component: ProfileSettings},
                {
                    path: '/playlist/:id',
                    component: () => import("@/pages/Playlist.vue"),
                    props: true // 启用props接收参数
                },
                {
                    path: '/infinite-scroll-demo',
                    name: 'InfiniteScrollDemo',
                    component: () => import('@/pages/InfiniteScrollDemo.vue'),
                    meta: { title: '滚动加载示例' }
                }
            ]
        },
        {path: '/:pathMatch(.*)', redirect: '/notFound'},
        {path: '/notFound', component: import('../pages/404.vue')},
        // {path: '/login', component: import('../views/Login.vue')},

    ],
})

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore();

    if (!userStore.userInfo && localStorage.getItem('token')) {
        try {
            await userStore.restoreUserState();
        } catch (error) {
            console.error('恢复用户状态失败:', error);
            localStorage.removeItem('token')
        }
    }

    next();
});
export default router
