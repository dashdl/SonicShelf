import {defineStore} from 'pinia'
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: {},
        isLoggedIn: false,
        token: localStorage.getItem('token') || '',
        loginTime: localStorage.getItem('loginTime') || ''
    }),

    getters: {
        username: state => state.userInfo?.username,
    },

    actions: {
        /**
         * 登录
         * @param {Object} userInfo - 用户登录信息
         * @returns {Promise}
         */
        async login(userInfo) {
            return request.post("auth/admin/login", userInfo).then(res => {
                if (res.code === "200") {
                    localStorage.setItem("token", res.data.access_token);
                    this.token = res.data.access_token;
                    const now = new Date().toISOString()
                    this.loginTime = now
                    localStorage.setItem('loginTime', now)
                    this.isLoggedIn = true
                    ElMessage.success("登录成功");
                    return Promise.resolve(res);
                } else {
                    return Promise.reject(res);
                }
            })
        },

        /**
         * 登出
         */
        logout() {
            this.token = ''
            this.userInfo = {}
            this.isLoggedIn = false
            this.loginTime = ''
            localStorage.removeItem('token')
            localStorage.removeItem('loginTime')
        },

        /**
         * 获取用户信息
         * @returns {Promise}
         */
        async getUserInfo() {
            try {
                // 模拟获取用户信息
                // const mockUserInfo = {
                //     id: 1,
                //     username: 'admin',
                //     email: 'admin@example.com',
                //     nickname: '管理员',
                // }

                let res = await request.get('auth/admin/getInfo')

                this.userInfo = res.data
                this.isLoggedIn = true
                return {code: '200', data: res.data}
            } catch (error) {
                return Promise.reject(error)
            }
        },

        /**
         * 从localStorage恢复用户状态
         */
        restoreUserState() {
            const token = localStorage.getItem('token')
            const loginTime = localStorage.getItem('loginTime')
            if (token && loginTime) {
                this.token = token
                this.loginTime = loginTime
                // 验证token有效性
                this.verifyToken()
            }
        },

        /**
         * 验证token有效性
         */
        async verifyToken() {
            try {
                await this.getUserInfo()
            } catch (error) {
                // token无效，清除状态
                this.logout()
            }
        }
    }
})