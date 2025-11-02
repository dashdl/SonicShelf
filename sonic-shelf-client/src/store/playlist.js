import {defineStore} from 'pinia'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

export const usePlaylistStore = defineStore('playlist', {
    // 状态
    state: () => ({
        userPlaylist: [],
        favoritePlaylist: [],
    }),

    // 计算属性
    getters: {
    },

    // 动作
    actions: {
        async loadPlaylist(id) {
            await request.get("playlists", {
                params: {
                    id: id,
                }
            }).then(res => {
                if (res.code === '200') {
                    this.userPlaylist = res.data
                } else {
                    ElMessage.error("歌单列表获取失败")
                }
            })
            await request.get("favorites/playlists", {
                params: {
                    id: id,
                }
            }).then(res => {
                if (res.code === '200') {
                    this.favoritePlaylist = res.data
                } else {
                    ElMessage.error("歌单列表获取失败")
                }
            })
        }
    }
})