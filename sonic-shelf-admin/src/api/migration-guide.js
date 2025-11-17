/**
 * 从Mock数据切换到真实API的快速指南
 * 
 * 这个文件告诉你如何一步步把模拟数据改成真实API
 * 按照下面的步骤操作即可
 */

// ==================== 第一步：准备工作 ====================

/**
 * 1. 确保后端服务已经启动
 *    - 默认端口：8080
 *    - 检查 vite.config.js 中的代理配置：
 */
export const viteConfig = {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 你的后端地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
}

/**
 * 2. 检查你创建的真实API文件
 *    - src/api/user.js     ✅ 已创建
 *    - src/api/music.js    ✅ 已创建
 *    - src/api/album.js    ✅ 已创建
 *    - src/api/singer.js   ✅ 已创建
 */

// ==================== 第二步：替换用户管理 ====================

/**
 * 文件：src/views/UserManagement.vue
 * 
 * 找到这些位置并替换：
 */

// 1. 导入语句（约第25行）
// 原：import mockService from '@/mock/mockService'
// 新：import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'

// 2. getUserList 方法（约第85行）
// 原：const res = await mockService.user.getList(params)
// 新：const res = await getUserList(params)

// 3. handleSubmit 方法（约第140行）
// 原：res = await mockService.user.update(userForm)
// 新：res = await updateUser(userForm)
// 原：res = await mockService.user.add(userForm)
// 新：res = await addUser(userForm)

// 4. handleDelete 方法（约第165行）
// 原：const res = await mockService.user.delete(row.id)
// 新：const res = await deleteUser(row.id)

// ==================== 第三步：测试数据格式 ====================

/**
 * 测试你的后端API是否返回正确格式
 * 在浏览器控制台中执行：
 */

// 测试获取用户列表
async function testGetUsers() {
  try {
    const response = await fetch('http://localhost:8080/admin/users?pageNum=1&pageSize=20')
    const data = await response.json()
    console.log('用户列表数据：', data)
    
    // 检查数据格式是否正确
    if (data.code === '200' && data.data.list && typeof data.data.total === 'number') {
      console.log('✅ 数据格式正确')
    } else {
      console.log('❌ 数据格式错误，应该是：', {
        code: '200',
        data: {
          list: [...],
          total: 100
        }
      })
    }
  } catch (error) {
    console.log('❌ 请求失败：', error)
  }
}

// 测试添加用户
async function testAddUser() {
  try {
    const response = await fetch('http://localhost:8080/admin/users', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: 'testuser',
        password: '123456',
        email: 'test@example.com',
        nickname: '测试用户',
        status: '1'
      })
    })
    const data = await response.json()
    console.log('添加用户结果：', data)
  } catch (error) {
    console.log('❌ 请求失败：', error)
  }
}

// ==================== 第四步：常见问题解决 ====================

/**
 * ❌ 问题1：跨域错误（CORS）
 * 错误信息：Access to fetch at '...' from origin '...' has been blocked by CORS policy
 * 
 * 解决方案：
 * 1. 确保后端配置了CORS
 * 2. 检查 vite.config.js 中的代理配置是否正确
 * 3. 重启前端开发服务器
 */

/**
 * ❌ 问题2：数据格式不对
 * 错误现象：表格不显示数据，或显示异常
 * 
 * 解决方案：
 * 1. 检查返回的code是否为'200'
 * 2. 检查data.list是否存在且是数组
 * 3. 检查data.total是否存在且是数字
 * 4. 检查用户对象是否包含所有必需字段（id, username, email等）
 */

/**
 * ❌ 问题3：时间格式不对
 * 错误现象：时间显示异常
 * 
 * 解决方案：
 * 1. 确保时间字段是字符串格式
 * 2. 格式：'YYYY-MM-DD HH:mm:ss'
 * 3. 例如：'2024-01-15 14:30:00'
 */

/**
 * ❌ 问题4：状态字段不对
 * 错误现象：状态开关不工作
 * 
 * 解决方案：
 * 1. 确保status字段是字符串'1'或'0'
 * 2. 不能是数字1或0，必须是字符串
 * 3. '1'表示启用，'0'表示禁用
 */

// ==================== 第五步：验证替换成功 ====================

/**
 * 替换完成后，检查这些功能是否正常：
 * 
 * ✅ 1. 用户列表显示
 *    - 打开用户管理页面
 *    - 应该能看到用户列表
 *    - 分页应该正常工作
 * 
 * ✅ 2. 搜索功能
 *    - 在搜索框输入关键词
 *    - 应该能搜索到相关用户
 * 
 * ✅ 3. 添加用户
 *    - 点击"添加用户"按钮
 *    - 填写表单并提交
 *    - 新用户应该出现在列表中
 * 
 * ✅ 4. 编辑用户
 *    - 点击"编辑"按钮
 *    - 修改用户信息
 *    - 修改后应该立即更新
 * 
 * ✅ 5. 删除用户
 *    - 点击"删除"按钮
 *    - 确认删除
 *    - 用户应该从列表中消失
 * 
 * ✅ 6. 状态切换
 *    - 点击状态开关
 *    - 用户状态应该改变
 */

/**
 * 如果以上功能都正常，说明替换成功！
 * 
 * 接下来可以继续替换其他模块：
 * 1. 音乐管理（MusicManagement.vue）
 * 2. 专辑管理（AlbumManagement.vue）
 * 3. 歌手管理（SingerManagement.vue）
 * 
 * 替换步骤都是一样的：
 * 1. 修改导入语句
 * 2. 替换API调用
 * 3. 测试功能
 */