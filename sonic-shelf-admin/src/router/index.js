import {createRouter, createWebHistory} from 'vue-router'
import Layout from "@/components/Layout.vue"
import Login from "@/views/Login.vue"
import Dashboard from "@/views/Dashboard.vue"
import {useUserStore} from "@/store/userStore"

// 路由白名单，不需要登录即可访问
const whiteList = ['/login']

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // 登录页面
        {path: '/login', component: Login, name: 'Login'},
        // 主布局
        {
            path: '/', component: Layout,
            children: [
                // 仪表盘
                {path: '', redirect: '/dashboard'},
                {path: '/dashboard', component: Dashboard, name: 'Dashboard'},
                // 用户管理
                {path: '/user', component: () => import("@/views/UserManagement.vue"), name: 'UserManagement'},
                // 音乐管理
                {path: '/music', component: () => import("@/views/MusicManagement.vue"), name: 'MusicManagement'},
                // 专辑管理
                {path: '/album', component: () => import("@/views/AlbumManagement.vue"), name: 'AlbumManagement'},
                // 歌手管理
                {path: '/singer', component: () => import("@/views/SingerManagement.vue"), name: 'SingerManagement'},
                // 歌单管理
                {
                    path: '/playlist',
                    component: () => import("@/views/PlaylistManagement.vue"),
                    name: 'PlaylistManagement'
                },
                // 分类管理
                {
                    path: '/category',
                    component: () => import("@/views/CategoryManagement.vue"),
                    name: 'CategoryManagement'
                },
                // 评论管理
                {
                    path: '/comment',
                    component: () => import("@/views/CommentManagement.vue"),
                    name: 'CommentManagement'
                },
            ]
        },
        // 404页面
        {path: '/:pathMatch(.*)*', redirect: '/dashboard'}
    ]
});

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const token = userStore.token

    // 如果有token
    if (token) {
        if (to.path === '/login') {
            // 已登录，跳转到首页
            next({path: '/dashboard'})
        } else {
            // 检查用户信息是否存在
            if (Object.keys(userStore.userInfo).length === 0) {
                // 获取用户信息
                userStore.getUserInfo().then(() => {
                    next()
                }).catch(() => {
                    // 获取用户信息失败，跳转到登录页
                    userStore.logout()
                    next({path: '/login', query: {redirect: to.path}})
                })
            } else {
                next()
            }
        }
    } else {
        // 没有token
        if (whiteList.includes(to.path)) {
            // 白名单页面，允许访问
            next()
        } else {
            // 非白名单页面，跳转到登录页
            next({path: '/login', query: {redirect: to.path}})
        }
    }
})

export default router
