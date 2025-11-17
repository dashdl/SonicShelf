/**
 * HTTP请求工具模块
 * 
 * 基于axios封装的请求工具，提供统一的请求处理和错误处理
 * 
 * 重要概念解释：
 * Promise: 不是权限，是JavaScript中处理异步操作的对象，表示一个异步操作的最终完成或失败
 * 简单说：Promise就是一个承诺，承诺将来会返回结果（成功或失败）
 * 
 * 使用示例：
 * request.get('/api/users').then(response => {
 *   console.log('成功:', response)
 * }).catch(error => {
 *   console.log('失败:', error)
 * })
 */

import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router";

/**
 * 创建axios实例
 * 
 * 配置说明：
 * baseURL: 所有请求的基准路径，这里设置为'/api/v1/'，配合Vite代理使用
 *           实际请求会变成：http://localhost:8080/api/v1/ + 你的接口路径
 * timeout: 请求超时时间，30秒
 * withCredentials: 是否允许跨域请求携带cookie，这里设置为false
 * 
 * 代理配置在 vite.config.js 中：
 * proxy: {
 *   '/api': {
 *     target: 'http://localhost:8080',  // 后端服务地址
 *     changeOrigin: true
 *   }
 * }
 */
const request = axios.create({
    baseURL: '/api/v1/', // 使用相对路径，配合Vite代理
    timeout: 30000,  // 后台接口超时时间
    withCredentials: false  // 允许跨域请求携带凭证信息
})

/**
 * 请求拦截器
 * 
 * 作用：在请求发送前进行统一处理
 * 
 * 处理内容：
 * 1. 设置请求头Content-Type（非FormData数据）
 * 2. 添加认证token（如果存在）
 * 
 * 认证机制：
 * - 从localStorage获取token
 * - 添加到请求头的Authorization字段
 * - 格式：Bearer token值
 * 
 * 为什么FormData不设置Content-Type？
 * 让浏览器自动设置，包含boundary信息，用于文件上传
 */
request.interceptors.request.use(config => {
    // 对于FormData类型的数据，不要设置Content-Type，让浏览器自动处理
    if (!(config.data instanceof FormData)) {
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
    }
    
    // 获取并添加认证token
    let token = localStorage.getItem("token");
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    return config
}, error => {
    return Promise.reject(error)
});

/**
 * 响应拦截器
 * 
 * 作用：在收到响应后进行统一处理
 * 
 * 处理逻辑：
 * 1. 解析响应数据（兼容字符串格式的JSON）
 * 2. 检查业务状态码（code）
 * 3. 处理错误情况
 * 4. 处理HTTP状态码错误
 * 
 * 状态码说明：
 * code: '200' - 业务成功
 * code: '401' - 未授权（token过期或无效）
 * 其他code - 业务错误
 * 
 * HTTP状态码处理：
 * 404 - 接口不存在
 * 500 - 服务器内部错误
 * 401 - 未授权（HTTP层面）
 */
request.interceptors.response.use(
    response => {
        let res = response.data;
        
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        
        // 检查业务状态码（不是HTTP状态码）
        if (res.code !== '200') {
            // 显示错误消息
            ElMessage.error(res.message || '请求失败');
            
            // 如果是401未授权，清除token并跳转到登录页
            if (res.code === '401') {
                localStorage.removeItem('token');
                router.push('/login');
            }
            
            // 返回拒绝的Promise，让调用方处理错误
            return Promise.reject(res);
        }
        
        // 返回成功的数据
        return res;
    },
    error => {
        // HTTP层面的错误处理
        if (error.response) {
            if (error.response.status === 404) {
                ElMessage.error('未找到请求接口')
            } else if (error.response.status === 500) {
                ElMessage.error('系统异常，请查看后端控制台报错')
            } else if (error.response.status === 401) {
                ElMessage.error('登录已过期，请重新登录');
                localStorage.removeItem('token');
                router.push('/login');
            } else {
                console.error('请求错误:', error.message)
            }
        } else {
            console.error('网络错误:', error.message)
        }
        
        return Promise.reject(error)
    }
)

/**
 * 导出请求实例
 * 
 * 使用方式：
 * 
 * 1. 在API文件中导入：
 *    import request from '@/utils/request'
 * 
 * 2. 发送GET请求：
 *    request.get('/users').then(res => {
 *      console.log(res.data)
 *    })
 * 
 * 3. 发送POST请求：
 *    request.post('/users', { name: '张三' }).then(res => {
 *      console.log(res.data)
 *    })
 * 
 * 4. 使用async/await：
 *    const res = await request.get('/users')
 *    console.log(res.data)
 * 
 * 5. 文件上传：
 *    const formData = new FormData()
 *    formData.append('file', file)
 *    request.post('/upload', formData)
 * 
 * 错误处理：
 * - 所有HTTP错误和业务错误都会自动显示错误消息
 * - 401错误会自动跳转到登录页
 * - 其他错误需要在调用处处理（使用try/catch或.catch()）
 */
export default request