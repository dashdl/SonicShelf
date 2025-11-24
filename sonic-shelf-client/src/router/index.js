import {createRouter, createWebHistory} from 'vue-router'
import Layout from "@/components/Layout.vue";
import Home from "@/pages/Home.vue";
import UserPanel from "@/components/form/UserPanel.vue";
import LoginForm from "@/components/form/LoginForm.vue";
import Profile from "@/pages/Profile.vue";
import {useUserStore} from "@/store/userStore.js";
import ProfileSettings from "@/pages/ProfileSettings.vue";
import GridList from "@/components/list/GridList.vue";
import TableList from "@/components/list/TableList.vue";
import Playlist from "@/pages/Playlist.vue";
import InfiniteTable from "@/components/list/InfiniteTable.vue";
import PlaylistEdit from "@/pages/PlaylistEdit.vue";
import PlaylistForm from "@/components/form/PlaylistForm.vue";
import Comment from "@/components/list/Comment.vue";
import Collectors from "@/components/list/Collectors.vue";
import PlaylistCard from "@/components/common/cards/PlaylistCard.vue";
import MusicCard from "@/components/common/cards/MusicCard.vue";
import Lyric from "@/components/common/Lyric.vue";
import Collection from "@/components/list/Collection.vue";
import Music from "@/pages/Music.vue";
import InteractCard from "@/components/common/cards/InteractCard.vue";
import CurrentPlaylist from "@/components/list/CurrentPlaylist.vue";
import Artist from "@/pages/Artist.vue";
import AlbumCard from "@/components/common/cards/AlbumCard.vue";
import Dynamic from "@/components/common/cards/DynamicCard.vue";
import Favorite from "@/pages/Favorite.vue";
import Collect from "@/pages/Collect.vue";
import DynamicCard from "@/components/common/cards/DynamicCard.vue";
import Publish from "@/components/form/Publish.vue";
import Search from "@/pages/Search.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/', component: Layout,
            children: [
                {path: '', component: Home},
                {path: '/profile/:userId', component: Profile, name: 'Profile'},
                {path: '/profile-settings', component: ProfileSettings},
                {path: '/home', component: Home},
                {path: '/my-favorite', component: Favorite},
                {path: '/my-collect', component: Collect},
                {
                    path: '/playlist/:id',
                    component: () => import("@/pages/Playlist.vue"),
                    props: true // 启用props接收参数
                },
                {
                    path: '/album/:id',
                    component: () => import("@/pages/Album.vue"),
                    props: true // 启用props接收参数
                },
                {
                    path: '/Playlist-edit/:id',
                    component: () => import("@/pages/PlaylistEdit.vue"),
                    props: true
                },
                {
                    path: '/music/:musicId',
                    component: () => import("@/pages/Music.vue"),
                    props: true,
                    name: 'Music'
                },
                {
                    path: '/artist/:artistId',
                    component: () => import("@/pages/Artist.vue"),
                    props: true,
                },
                {
                    path: '/histories',
                    component: () => import("@/pages/PlayHistory.vue"),
                    props: true,
                },
                {
                    path: '/dynamic',
                    component: () => import("@/pages/Dynamic.vue"),
                    props: true,
                },
                {
                    path: '/search/:keyword',
                    component: () => import("@/pages/Search.vue"),
                    props: true,
                },
            ]
        },
        // {path: '/:pathMatch(.*)', redirect: '/notFound'},
        // {path: '/notFound', component: import('../pages/404.vue')},
        // {path: '/login', component: import('../views/Login.vue')},
    ],
})

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore();

    if (localStorage.getItem('token')) {
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
