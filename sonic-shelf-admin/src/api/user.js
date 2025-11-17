/**
 * 用户管理API
 * 
 * 这个文件提供用户管理相关的真实API接口调用
 * 用于替换 src/mock/mockService.js 中的模拟数据
 * 
 * 使用说明：
 * 1. 确保后端服务已启动（默认端口：8080）
 * 2. 检查 vite.config.js 中的代理配置是否正确
 * 3. 在组件中导入使用：import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'
 * 
 * 错误处理：
 * - 所有API调用都会自动处理401未授权错误（跳转到登录页）
 * - 其他错误会在组件中显示错误消息
 */



import request from "@/utils/request.js";

/**
 * 获取用户列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码（默认：1）
 * @param {number} params.pageSize - 每页条数（默认：20）
 * @param {string} params.keyword - 搜索关键词（用户名、邮箱、昵称）
 * @param {string} params.status - 状态筛选（1:启用，0:禁用）
 * @returns {Promise} 返回用户列表数据
 * 
 * 返回格式：
 * {
 *   code: '200',
 *   data: {
 *     list: [
 *       {
 *         id: 1,
 *         username: 'admin',
 *         email: 'admin@example.com',
 *         nickname: '管理员',
 *         status: '1',
 *         createTime: '2024-01-01 10:00:00',
 *         lastLoginTime: '2024-01-15 15:30:00'
 *       }
 *     ],
 *     total: 100
 *   }
 * }
 */
export const getUserList = (params) => {
  return request.get('/admin/users', { params })
}

/**
 * 添加用户
 * @param {Object} user - 用户数据
 * @param {string} user.username - 用户名（必填，3-20字符）
 * @param {string} user.password - 密码（必填，6-20字符）
 * @param {string} user.email - 邮箱（必填，有效邮箱格式）
 * @param {string} user.nickname - 昵称（必填，2-20字符）
 * @param {string} user.status - 状态（1/0，默认：1）
 * @returns {Promise} 返回添加的用户数据
 * 
 * 成功返回：
 * {
 *   code: '200',
 *   data: {
 *     id: 123,
 *     username: 'newuser',
 *     email: 'newuser@example.com',
 *     nickname: '新用户',
 *     status: '1',
 *     createTime: '2024-01-15 16:00:00',
 *     lastLoginTime: null
 *   }
 * }
 */
export const addUser = (user) => {
  return request.post('/admin/users', user)
}

/**
 * 更新用户
 * @param {Object} user - 用户数据
 * @param {number} user.id - 用户ID（必填）
 * @param {string} user.username - 用户名（可选）
 * @param {string} user.email - 邮箱（可选）
 * @param {string} user.nickname - 昵称（可选）
 * @param {string} user.status - 状态（可选）
 * @returns {Promise} 返回更新的用户数据
 * 
 * 成功返回：
 * {
 *   code: '200',
 *   data: {
 *     id: 123,
 *     username: 'updateduser',
 *     email: 'updated@example.com',
 *     nickname: '更新后的用户',
 *     status: '1',
 *     createTime: '2024-01-01 10:00:00',
 *     lastLoginTime: '2024-01-15 15:30:00'
 *   }
 * }
 */
export const updateUser = (user) => {
  return request.put(`/admin/users/${user.id}`, user)
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 * @returns {Promise} 返回删除结果
 * 
 * 成功返回：
 * {
 *   code: '200',
 *   message: '删除成功'
 * }
 * 
 * 失败返回：
 * {
 *   code: '500',
 *   message: '用户不存在'
 * }
 */
export const deleteUser = (id) => {
  return request.delete(`/admin/users/${id}`)
}

/**
 * 批量删除用户
 * @param {Array} ids - 用户ID数组
 * @returns {Promise} 返回批量删除结果
 * 
 * 成功返回：
 * {
 *   code: '200',
 *   data: {
 *     success: 3,  // 成功删除数量
 *     failed: 1    // 失败数量
 *   }
 * }
 */
export const batchDeleteUsers = (ids) => {
  return request.delete('/admin/users/batch', { data: { ids } })
}

/**
 * 重置用户密码
 * @param {number} id - 用户ID
 * @param {string} newPassword - 新密码
 * @returns {Promise} 返回重置结果
 * 
 * 成功返回：
 * {
 *   code: '200',
 *   message: '密码重置成功'
 * }
 */
export const resetUserPassword = (id, newPassword) => {
  return request.put(`/admin/users/${id}/reset-password`, { newPassword })
}

/**
 * 导出用户列表
 * @param {Object} params - 导出参数
 * @param {string} params.keyword - 搜索关键词
 * @param {string} params.status - 状态筛选
 * @returns {Promise} 返回文件下载
 * 
 * 使用示例：
 * exportUserList({ keyword: 'admin' }).then(response => {
 *   // 处理文件下载
 *   const blob = new Blob([response.data])
 *   const url = window.URL.createObjectURL(blob)
 *   const link = document.createElement('a')
 *   link.href = url
 *   link.download = '用户列表.xlsx'
 *   link.click()
 * })
 */
export const exportUserList = (params) => {
  return request.get('/admin/users/export', { 
    params,
    responseType: 'blob'  // 重要：指定响应类型为blob
  })
}