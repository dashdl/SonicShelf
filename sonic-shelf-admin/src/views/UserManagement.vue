<template>
  <!--
    用户管理页面
    
    数据流说明：
    1. 组件加载时调用 getUserList() 获取用户数据
    2. getUserList() 调用 mockService.user.getList() 获取模拟数据
    3. 真实API替换时，只需要修改 getUserList() 中的API调用
    
    真实API替换步骤：
    1. 创建 src/api/user.js 文件
    2. 导入真实的API方法：import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'
    3. 替换 getUserList() 中的 mockService.user.getList() 调用
    4. 替换 handleSubmit() 中的 mockService.user.add/update 调用
    5. 替换 handleDelete() 中的 mockService.user.delete 调用
    
    后端需要返回的数据格式（参考 src/api/data-format-guide.js）：
    
    1. 用户列表数据格式：
    {
      code: '200',
      data: {
        list: [
          {
            id: 1,                    // 用户ID
            username: 'admin',         // 用户名
            email: 'admin@example.com', // 邮箱
            nickname: '管理员',        // 昵称
            status: '1',               // 状态（1:启用，0:禁用）
            createTime: '2024-01-01 10:00:00',  // 创建时间
            lastLoginTime: '2024-01-15 15:30:00' // 最后登录时间
          }
        ],
        total: 100  // 总记录数
      }
    }
    
    2. 添加/编辑用户成功响应：
    {
      code: '200',
      message: '操作成功',
      data: { ... }  // 返回更新后的用户数据
    }
    
    3. 删除用户成功响应：
    {
      code: '200',
      message: '删除成功'
    }
    
    4. 错误响应：
    {
      code: '400',  // 或其他错误码
      message: '具体的错误信息'
    }
  -->
  <div class="admin-management-container">
    <el-card shadow="hover" class="user-management-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            添加用户
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
            v-model="searchQuery"
            placeholder="搜索用户名或邮箱"
            prefix-icon="Search"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
        />
        <el-select
            v-model="statusFilter"
            placeholder="筛选状态"
            clearable
            class="filter-select"
        >
          <el-option label="启用" value="1"/>
          <el-option label="禁用" value="0"/>
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon>
            <Search/>
          </el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon>
            <Refresh/>
          </el-icon>
          重置
        </el-button>
      </div>

      <!-- 用户列表 -->
      <el-table
          :data="users"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left"/>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="username" label="用户名" min-width="150"/>
        <el-table-column prop="email" label="邮箱" min-width="250"/>
        <el-table-column prop="nickname" label="昵称" min-width="150"/>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="0"
                @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="200"/>
        <el-table-column prop="lastLoginAt" label="最后登录" min-width="200"/>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                @click="handleEdit(scope.row)"
            >
              <el-icon>
                <Edit/>
              </el-icon>
              编辑
            </el-button>
            <el-button
                type="primary"
                size="small"
                @click="handleDelete(scope.row)"
            >
              <el-icon>
                <Delete/>
              </el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>

      <!-- 用户表单对话框 -->
      <el-dialog
          v-model="dialogVisible"
          :title="formTitle"
          width="500px"
      >
        <el-form
            ref="userFormRef"
            :model="userForm"
            :rules="userRules"
            label-position="top"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" placeholder="请输入用户名"/>
          </el-form-item>
          <el-form-item label="密码" prop="password" v-if="!userForm.id">
            <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password/>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" placeholder="请输入邮箱"/>
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称"/>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-switch
                v-model="userForm.status"
                :active-value="1"
                :inactive-value="0"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确认</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 用户管理组件逻辑
 *
 * 核心功能：
 * 1. 用户列表展示（分页、搜索、筛选）
 * 2. 用户增删改查操作
 * 3. 用户状态管理
 *
 * 数据获取流程：
 * 1. 组件挂载时调用 getUserList() 获取初始数据
 * 2. 搜索、分页、筛选操作时重新调用 getUserList()
 * 3. 增删改操作成功后刷新列表
 *
 * API替换要点：
 * 1. 保持现有的参数结构和返回格式
 * 2. 确保返回 Promise 对象
 * 3. 错误处理保持现有模式（显示错误消息）
 */

import {ref, reactive, onMounted, computed} from 'vue'
import mockService from '@/mock/mockService'  // TODO: 替换为真实API导入
import {ElMessage, ElMessageBox} from 'element-plus'
import request from "@/utils/request.js";


// 用户列表数据
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const statusFilter = ref('')

// 对话框
const dialogVisible = ref(false)
const userFormRef = ref()
const userForm = reactive({
  id: '',
  username: '',
  password: '',
  email: '',
  nickname: '',
  status: 1
})

// 表单验证规则
const userRules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur'}
  ],
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur'}
  ],
  nickname: [
    {required: true, message: '请输入昵称', trigger: 'blur'},
    {min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur'}
  ]
}

// 选中的用户
const selectedUsers = ref([])

// 表单标题
const formTitle = computed(() => {
  return userForm.id ? '编辑用户' : '添加用户'
})

/**
 * 获取用户列表
 *
 * 功能说明：
 * 1. 构建查询参数（分页、搜索、筛选）
 * 2. 调用API获取数据
 * 3. 更新本地数据状态
 *
 * 真实API替换：
 * 将 mockService.user.getList(params) 替换为真实API调用
 * 例如：await getUserList(params)
 *
 * 后端API要求：
 * GET /admin/users?pageNum=1&pageSize=20&keyword=搜索词&status=1
 *
 * 必须返回的数据格式：
 * {
 *   code: '200',
 *   data: {
 *     list: [
 *       {
 *         id: 1,                    // 用户ID（数字）
 *         username: 'admin',         // 用户名（字符串）
 *         email: 'admin@example.com', // 邮箱（字符串）
 *         nickname: '管理员',        // 昵称（字符串）
 *         status: '1',               // 状态（1:启用，0:禁用）字符串
 *         createTime: '2024-01-01 10:00:00',  // 创建时间
 *         lastLoginTime: '2024-01-15 15:30:00' // 最后登录时间（可为null）
 *       }
 *     ],
 *     total: 100  // 总记录数（数字）
 *   }
 * }
 *
 * 重要字段说明：
 * - id: 必须是数字，用于表格显示和操作
 * - status: 必须是字符串'1'或'0'，用于开关组件
 * - 时间字段: 必须是字符串格式，用于表格显示
 * - 所有字段都不能缺少，否则表格会显示异常
 */
const getUserList = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    keyword: searchQuery.value,
    status: statusFilter.value
  }

  const res = await request.get('users/getAll', { params })
  if (res.code === '200') {
    // 适配真实数据结构：将时间字段统一转换为驼峰命名并确保 status 为字符串
    users.value = res.data.list.map(item => ({
      ...item,
      createdAt: item.createTime ?? item.createdAt ?? '',
      lastLoginAt: item.lastLoginTime ?? item.lastLoginAt ?? '',
      status: String(item.status)
    }))
    total.value = res.data.total
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getUserList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  currentPage.value = 1
  getUserList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getUserList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getUserList()
}

// 选择用户
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 添加用户
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  Object.assign(userForm, {
    ...row,
    status: Number(row.status) || 0
  })
  dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await mockService.user.delete(row.id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getUserList()
      }
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 提交用户表单（添加/编辑）
 *
 * 功能说明：
 * 1. 表单验证
 * 2. 根据是否有ID判断是添加还是编辑
 * 3. 调用对应的API方法
 * 4. 操作成功后刷新列表
 *
 * 真实API替换：
 * 将 mockService.user.update/add 替换为真实API调用
 * 例如：
 * if (userForm.id) {
 *   res = await updateUser(userForm)
 * } else {
 *   res = await addUser(userForm)
 * }
 *
 * 后端API要求：
 * 添加用户：POST /admin/users
 * 编辑用户：PUT /admin/users/{id}
 *
 * 添加用户请求体（必须包含密码）：
 * {
 *   username: 'newuser',
 *   password: '123456',    // 添加时必须
 *   email: 'new@example.com',
 *   nickname: '新用户',
 *   status: '1'
 * }
 *
 * 编辑用户请求体（不需要密码，id在URL中）：
 * {
 *   username: 'updateduser',
 *   email: 'updated@example.com',
 *   nickname: '更新用户',
 *   status: '1'
 * }
 *
 * 成功响应格式：
 * {
 *   code: '200',
 *   message: '操作成功',
 *   data: { ... }  // 返回更新后的完整用户数据
 * }
 *
 * 注意：保持现有的错误处理逻辑
 */
const handleSubmit = async () => {
  if (!userFormRef.value) return
  try {
    await userFormRef.value.validate()
    let res
    if (userForm.id) {
      // 编辑用户 - TODO: 替换为真实API
      res = await mockService.user.update(userForm)
    } else {
      // 添加用户 - TODO: 替换为真实API
      res = await mockService.user.add(userForm)
    }
    if (res.code === '200') {
      ElMessage.success(userForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      getUserList()
    }
  } catch (error) {
    console.error(userForm.id ? '编辑用户失败:' : '添加用户失败:', error)
    ElMessage.error(userForm.id ? '编辑用户失败' : '添加用户失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(userForm, {
    id: '',
    username: '',
    password: '',
    email: '',
    nickname: '',
    status: 1
  })
  if (userFormRef.value) {
    userFormRef.value.resetFields()
  }
}

// 状态改变
const handleStatusChange = async (row) => {
  try {
    const res = await mockService.user.update({
      id: row.id,
      status: row.status
    })
    if (res.code !== '200') {
      // 恢复原始状态
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error('更新状态失败')
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    // 恢复原始状态
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('更新状态失败')
  }
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped lang="scss">
</style>