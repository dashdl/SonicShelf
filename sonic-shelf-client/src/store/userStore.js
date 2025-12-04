// src/store/userStore.js
import {defineStore} from 'pinia'
import request from "@/utils/request.js";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";

export const useUserStore = defineStore('user', {
    // 状态
    state: () => ({
        userInfo: null,
        isLoggedIn: false,
        token: null
    }),

    // 计算属性
    getters: {
        getNickname: (state) => state.userInfo?.nickname || '未登录',
        getAvatar: (state) => state.userInfo?.avatar || '/icons/user.svg',
        getBio: (state) => state.userInfo?.bio || '暂无简介',
        getGender: (state) => state.userInfo?.gender || '未知',
        getBirthday: (state) => state.userInfo?.birthday || '未设置',
        getLocation: (state) => state.userInfo?.location || '未设置',
        getFollowers_count: (state) => state.userInfo?.followers_count || 0,
        getFollowing_count: (state) => state.userInfo?.following_count || 0,
        getUserId: (state) => state.userInfo?.id || null,
        getEmail: (state) => state.userInfo?.email || '未设置',
        getCreatedTime: (state) => state.userInfo?.createdAt || '未知时间',
        getDynamicCount: (state) => state.userInfo?.dynamicCount || 0
    },

    // 动作
    actions: {
        // 清除用户信息（退出登录）
        logout() {
            this.userInfo = null
            this.isLoggedIn = false
            this.token = null
            localStorage.removeItem('token')
            router.push('/home')
        },

        // 从 localStorage 恢复用户状态
        async restoreUserState() {
            const token = localStorage.getItem('token')
            if (token!==null) {
                this.token = token
                try {
                    const res = await request.get("/users/profile")
                    if (res.code === '200') {
                        this.userInfo = res.data
                        this.isLoggedIn = true
                    } else {
                        // token 无效，清除
                        this.logout()
                    }
                } catch (error) {
                    this.logout()
                }
            }else {
                ElMessage.success("请登录或注册账号吧")
            }
        },

        // 更新用户信息（部分更新）
        updateUserInfo(partialInfo) {
            if (this.userInfo) {
                this.userInfo = {...this.userInfo, ...partialInfo}
            }
        },

        // 更新用户头像
        updateAvatar(avatarUrl) {
            if (this.userInfo) {
                this.userInfo.avatar = avatarUrl
            }
        },

        // 更新用户昵称
        updateNickname(nickname) {
            if (this.userInfo) {
                this.userInfo.nickname = nickname
            }
        },

        // 更新个人简介
        updateBio(bio) {
            if (this.userInfo) {
                this.userInfo.bio = bio
            }
        },

        // 更新性别
        updateGender(gender) {
            if (this.userInfo) {
                this.userInfo.gender = gender
            }
        },

        // 更新生日
        updateBirthday(birthday) {
            if (this.userInfo) {
                this.userInfo.birthday = birthday
            }
        },

        // 更新位置
        updateLocation(location) {
            if (this.userInfo) {
                this.userInfo.location = location
            }
        },

        // 检查是否已登录
        checkLoginStatus() {
            return this.isLoggedIn && this.token !== null
        },

        // 获取完整的用户信息（用于调试或需要完整对象时）
        getUserInfo() {
            return this.userInfo
        },

        // 清空用户信息但保留 token（用于重新获取用户信息）
        clearUserInfo() {
            this.userInfo = null
            this.isLoggedIn = false
        }
    }
})