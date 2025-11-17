/**
 * 后端API数据格式规范
 * 
 * 这个文件详细说明了前端组件需要的数据格式
 * 后端开发人员需要按照这个格式返回数据
 * 
 * 统一的响应格式：
 * {
 *   code: '200',        // 状态码，200表示成功
 *   message: '成功',     // 消息，可选
 *   data: { ... }       // 具体数据
 * }
 * 
 * 错误响应格式：
 * {
 *   code: '500',        // 错误状态码
 *   message: '错误信息'  // 错误描述
 * }
 */

// ==================== 用户管理模块 ====================

/**
 * 1. 获取用户列表 /admin/users (GET)
 * 
 * 请求参数：
 * {
 *   pageNum: 1,     // 当前页码，默认1
 *   pageSize: 20,   // 每页条数，默认20
 *   keyword: '',    // 搜索关键词（用户名、邮箱、昵称），可选
 *   status: ''      // 状态筛选（1:启用，0:禁用），可选
 * }
 * 
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '获取成功',
 *   data: {
 *     list: [
 *       {
 *         id: 1,                    // 用户ID（数字）
 *         username: 'admin',         // 用户名（字符串）
 *         email: 'admin@example.com', // 邮箱（字符串）
 *         nickname: '管理员',        // 昵称（字符串）
 *         status: '1',               // 状态（1:启用，0:禁用）
 *         createTime: '2024-01-01 10:00:00',  // 创建时间（字符串）
 *         lastLoginTime: '2024-01-15 15:30:00' // 最后登录时间（字符串，可为null）
 *       },
 *       {
 *         id: 2,
 *         username: 'user1',
 *         email: 'user1@example.com',
 *         nickname: '普通用户',
 *         status: '1',
 *         createTime: '2024-01-02 11:00:00',
 *         lastLoginTime: null
 *       }
 *     ],
 *     total: 100  // 总记录数（数字），用于分页显示
 *   }
 * }
 */

/**
 * 2. 添加用户 /admin/users (POST)
 * 
 * 请求体示例：
 * {
 *   username: 'newuser',      // 用户名（必填）
 *   password: '123456',       // 密码（必填）
 *   email: 'new@example.com', // 邮箱（必填）
 *   nickname: '新用户',       // 昵称（必填）
 *   status: '1'               // 状态（1/0，默认1）
 * }

/**
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '添加成功',
 *   data: {
 *     id: 123,                   // 新用户的ID
 *     username: 'newuser',
 *     email: 'new@example.com',
 *     nickname: '新用户',
 *     status: '1',
 *     createTime: '2024-01-15 16:00:00',
 *     lastLoginTime: null
 *   }
 * }

/**
 * 3. 更新用户 /admin/users/{id} (PUT)
 * 
 * 请求体示例（所有字段都是可选的）：
 * {
 *   id: 123,                  // 用户ID（路径参数）
 *   username: 'updateduser',  // 用户名（可选）
 *   email: 'updated@example.com', // 邮箱（可选）
 *   nickname: '更新用户',     // 昵称（可选）
 *   status: '1'                // 状态（可选）
 * }

/**
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '更新成功',
 *   data: {
 *     id: 123,
 *     username: 'updateduser',
 *     email: 'updated@example.com',
 *     nickname: '更新用户',
 *     status: '1',
 *     createTime: '2024-01-01 10:00:00',
 *     lastLoginTime: '2024-01-15 15:30:00'
 *   }
 * }

/**
 * 4. 删除用户 /admin/users/{id} (DELETE)
 * 
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '删除成功',
 *   data: null  // 或者可以返回已删除的用户ID
 * }

/**
 * 5. 批量删除用户 /admin/users/batch (DELETE)
 * 
 * 请求体示例：
 * {
 *   ids: [1, 2, 3]  // 要删除的用户ID数组
 * }

/**
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '批量删除成功',
 *   data: {
 *     success: 3,  // 成功删除的数量
 *     failed: 0    // 失败的数量
 *   }
 * }

// ==================== 音乐管理模块 ====================

/**
 * 1. 获取音乐列表 /admin/music (GET)
 * 
 * 请求参数：
 * {
 *   pageNum: 1,
 *   pageSize: 20,
 *   keyword: '',     // 搜索关键词（歌名、歌手、专辑）
 *   singerId: '',    // 歌手ID
 *   albumId: '',     // 专辑ID
 *   status: ''       // 状态（1:启用，0:禁用）
 * }
 * 
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '获取成功',
 *   data: {
 *     list: [
 *       {
 *         id: 1,                      // 音乐ID
 *         name: '七里香',             // 歌名
 *         duration: '4:59',           // 时长
 *         fileSize: '5.2MB',         // 文件大小
 *         audioUrl: '/files/songs/七里香.mp3',  // 音频文件路径
 *         coverUrl: '/files/cover/七里香.jpg',  // 封面图片路径
 *         singerId: 1,                // 歌手ID
 *         singerName: '周杰伦',       // 歌手名
 *         albumId: 1,                 // 专辑ID
 *         albumName: '七里香',         // 专辑名
 *         status: '1',                // 状态
 *         playCount: 15234,           // 播放次数
 *         downloadCount: 523,        // 下载次数
 *         createTime: '2024-01-01 10:00:00'
 *       }
 *     ],
 *     total: 50
 *   }
 * }

/**
 * 2. 添加音乐 /admin/music (POST)
 * 
 * 请求体（FormData格式，支持文件上传）：
 * - name: 歌名
 * - singerId: 歌手ID
 * - albumId: 专辑ID
 * - audioFile: 音频文件（文件对象）
 * - coverFile: 封面文件（文件对象，可选）
 * 
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '添加成功',
 *   data: {
 *     id: 123,
 *     name: '新歌',
 *     duration: '3:45',
 *     fileSize: '4.2MB',
 *     audioUrl: '/files/songs/新歌.mp3',
 *     coverUrl: '/files/cover/新歌.jpg',
 *     singerId: 1,
 *     singerName: '周杰伦',
 *     albumId: 1,
 *     albumName: '新专辑',
 *     status: '1',
 *     playCount: 0,
 *     downloadCount: 0,
 *     createTime: '2024-01-15 16:00:00'
 *   }
 * }

// ==================== 专辑管理模块 ====================

/**
 * 1. 获取专辑列表 /admin/albums (GET)
 * 
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '获取成功',
 *   data: {
 *     list: [
 *       {
 *         id: 1,                      // 专辑ID
 *         name: '七里香',             // 专辑名
 *         description: '周杰伦专辑', // 描述
 *         coverUrl: '/files/cover/七里香.jpg',  // 封面路径
 *         singerId: 1,                // 歌手ID
 *         singerName: '周杰伦',       // 歌手名
 *         releaseDate: '2004-08-03',   // 发行日期
 *         songCount: 10,               // 歌曲数量
 *         status: '1',                 // 状态
 *         createTime: '2024-01-01 10:00:00'
 *       }
 *     ],
 *     total: 20
 *   }
 * }

// ==================== 歌手管理模块 ====================

/**
 * 1. 获取歌手列表 /admin/singers (GET)
 * 
 * 成功响应示例：
 * {
 *   code: '200',
 *   message: '获取成功',
 *   data: {
 *     list: [
 *       {
 *         id: 1,                      // 歌手ID
 *         name: '周杰伦',             // 歌手名
 *         description: '华语流行歌手',  // 描述
 *         avatarUrl: '/files/avatar/周杰伦.jpg',  // 头像路径
 *         nationality: '中国',         // 国籍
 *         birthDate: '1979-01-18',    // 出生日期
 *         debutYear: 2000,            // 出道年份
 *         albumCount: 15,             // 专辑数量
 *         songCount: 150,             // 歌曲数量
 *         status: '1',                // 状态
 *         createTime: '2024-01-01 10:00:00'
 *       }
 *     ],
 *     total: 30
 *   }
 * }

// ==================== 通用错误响应格式 ====================

/**
 * 常见错误响应示例：
 * 
 * // 400 - 参数错误
 * {
 *   code: '400',
 *   message: '参数错误：用户名不能为空'
 * }
 * 
 * // 401 - 未授权
 * {
 *   code: '401',
 *   message: '未登录或登录已过期'
 * }
 * 
 * // 403 - 无权限
 * {
 *   code: '403',
 *   message: '没有操作权限'
 * }
 * 
 * // 404 - 资源不存在
 * {
 *   code: '404',
 *   message: '用户不存在'
 * }
 * 
 * // 500 - 服务器错误
 * {
 *   code: '500',
 *   message: '服务器内部错误'
 * }

/**
 * 注意事项：
 * 
 * 1. 所有时间字段都使用字符串格式：'YYYY-MM-DD HH:mm:ss'
 * 2. 状态字段使用字符串：'1'（启用）或 '0'（禁用）
 * 3. ID字段使用数字类型
 * 4. 文件上传使用FormData格式
 * 5. 分页参数：pageNum从1开始，pageSize默认20
 * 6. 统一响应格式，前端会自动处理
 * 7. 错误信息要清晰明了，方便用户理解
 */

// 导出数据格式规范供其他文件使用
export const dataFormat = {
  // 可以在这里添加数据格式验证函数
  validateResponse,
  validateUserData
}

// 响应数据验证函数
function validateResponse(response) {
  return response && 
         response.code === '200' && 
         response.data !== undefined
}

// 用户数据验证函数  
function validateUserData(user) {
  return user && 
         typeof user.id === 'number' && 
         typeof user.username === 'string' && 
         typeof user.status === 'string' && 
         (user.status === '1' || user.status === '0')
}