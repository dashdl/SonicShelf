/**
 * API使用示例和Promise详解
 * 
 * 这个文件帮助你理解Promise和如何使用API
 */

// ==================== Promise详解 ====================

/**
 * Promise是什么？
 * 
 * Promise是JavaScript中处理异步操作的对象
 * 想象它就像一个"承诺"：承诺现在开始一个操作，将来会告诉你结果
 * 
 * 三种状态：
 * 1. Pending（进行中）- 刚开始，还没结果
 * 2. Fulfilled（已成功）- 操作成功完成
 * 3. Rejected（已失败）- 操作失败
 * 
 * 为什么需要Promise？
 * 因为JavaScript的很多操作是异步的（比如网络请求、文件读取）
 * 我们不能让程序停下来等待，所以需要Promise来管理异步操作
 */

// ==================== 基本使用示例 ====================

// 示例1：使用then/catch（传统方式）
function example1() {
  // 假设这是你的API调用
  request.get('/api/users')
    .then(response => {
      // 成功时执行
      console.log('获取用户成功:', response.data)
    })
    .catch(error => {
      // 失败时执行
      console.log('获取用户失败:', error)
    })
}

// 示例2：使用async/await（现代方式，推荐）
async function example2() {
  try {
    // 等待请求完成，就像同步代码一样
    const response = await request.get('/api/users')
    console.log('获取用户成功:', response.data)
  } catch (error) {
    console.log('获取用户失败:', error)
  }
}

// ==================== 实际API使用示例 ====================

// 导入真实的API（假设你已经创建了这些文件）
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'
import { getMusicList, addMusic } from '@/api/music'

// 示例3：获取用户列表
async function getUsersExample() {
  try {
    // 准备查询参数
    const params = {
      pageNum: 1,
      pageSize: 20,
      keyword: '张三'
    }
    
    // 调用API
    const response = await getUserList(params)
    
    // 检查返回的数据结构
    if (response.code === '200') {
      console.log('用户列表:', response.data.list)
      console.log('总用户数:', response.data.total)
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 示例4：添加用户
async function addUserExample() {
  try {
    // 准备用户数据
    const userData = {
      username: '新用户',
      password: '123456',
      email: 'user@example.com',
      nickname: '新用户昵称',
      status: '1'
    }
    
    // 调用API
    const response = await addUser(userData)
    
    if (response.code === '200') {
      console.log('用户添加成功:', response.data)
      alert('添加成功！')
    }
  } catch (error) {
    console.error('添加用户失败:', error)
  }
}

// 示例5：文件上传（添加音乐）
async function uploadMusicExample() {
  try {
    // 创建FormData对象（用于文件上传）
    const formData = new FormData()
    
    // 添加普通字段
    formData.append('name', '新歌名')
    formData.append('singerId', '1')
    formData.append('albumId', '1')
    
    // 添加文件字段（假设你从文件输入获取）
    const audioFile = document.getElementById('audioFile').files[0]
    const coverFile = document.getElementById('coverFile').files[0]
    
    if (audioFile) {
      formData.append('audioFile', audioFile)
    }
    if (coverFile) {
      formData.append('coverFile', coverFile)
    }
    
    // 调用API
    const response = await addMusic(formData)
    
    if (response.code === '200') {
      console.log('音乐上传成功:', response.data)
      alert('上传成功！')
    }
  } catch (error) {
    console.error('音乐上传失败:', error)
  }
}

// 示例6：在Vue组件中使用
export default {
  data() {
    return {
      users: [],
      loading: false
    }
  },
  
  methods: {
    // 获取用户列表
    async loadUsers() {
      this.loading = true
      try {
        const response = await getUserList({
          pageNum: 1,
          pageSize: 20
        })
        
        if (response.code === '200') {
          this.users = response.data.list
        }
      } catch (error) {
        console.error('加载用户失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    // 删除用户
    async deleteUser(userId) {
      if (confirm('确定要删除这个用户吗？')) {
        try {
          const response = await deleteUser(userId)
          if (response.code === '200') {
            alert('删除成功！')
            // 重新加载列表
            await this.loadUsers()
          }
        } catch (error) {
          console.error('删除失败:', error)
          alert('删除失败，请重试')
        }
      }
    }
  },
  
  async mounted() {
    // 组件加载时获取数据
    await this.loadUsers()
  }
}

// ==================== 常见问题解答 ====================

/**
 * Q: Promise和回调函数有什么区别？
 * A: Promise更现代、更优雅，避免了"回调地狱"，可以用async/await让异步代码看起来像同步代码
 * 
 * Q: 为什么API调用要返回Promise？
 * A: 因为网络请求需要时间，不能立即返回结果。Promise让我们可以优雅地处理异步操作
 * 
 * Q: 什么时候用then/catch，什么时候用async/await？
 * A: 推荐用async/await，代码更清晰。then/catch在简单的链式调用时也可以使用
 * 
 * Q: 错误处理一定要try/catch吗？
 * A: 对于async/await，是的。对于then/catch，错误会在catch中处理
 * 
 * Q: 为什么有些API返回的数据结构是{code, data, message}？
 * A: 这是统一的响应格式，code表示业务状态，data是具体数据，message是错误信息
 */