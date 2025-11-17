import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: {},
    isLoggedIn: false,
    token: localStorage.getItem('token') || '',
    loginTime: localStorage.getItem('loginTime') || ''
  }),

  getters: {
    // 获取用户名
    username: state => state.userInfo?.username,
    // 获取用户角色
    role: state => state.userInfo?.role,
    // 获取用户权限
    permissions: state => state.userInfo?.permissions || []
  },

  actions: {
    /**
     * 登录
     * @param {Object} userInfo - 用户登录信息
     * @returns {Promise}
     */
    async login(userInfo) {
      try {
        // 模拟登录验证
        if (userInfo.username === 'admin' && userInfo.password === 'admin123') {
          const token = 'mock-admin-token-' + Date.now()
          // 保存token
          this.token = token
          localStorage.setItem('token', token)
          // 保存登录时间
          const now = new Date().toISOString()
          this.loginTime = now
          localStorage.setItem('loginTime', now)
          // 获取用户信息
          await this.getUserInfo()
          this.isLoggedIn = true
          return { code: '200', data: { token } }
        } else {
          return Promise.reject({ msg: '用户名或密码错误' })
        }
      } catch (error) {
        return Promise.reject(error)
      }
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
        const mockUserInfo = {
          id: 1,
          username: 'admin',
          email: 'admin@example.com',
          nickname: '管理员',
          role: 'admin',
          permissions: ['all']
        }
        this.userInfo = mockUserInfo
        this.isLoggedIn = true
        return { code: '200', data: mockUserInfo }
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