<template>
  <div class="user-management-container">
    <el-card shadow="hover" class="user-management-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><i-ep-plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索用户名或邮箱"
          prefix-icon="i-ep-search"
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
          <el-option label="启用" value="1" />
          <el-option label="禁用" value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><i-ep-search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><i-ep-refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 用户列表 -->
      <el-table
        :data="users"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="email" label="邮箱" width="250" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="role" label="角色" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
              :disabled="scope.row.id === 1"
            >
              <el-icon><i-ep-edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
              :disabled="scope.row.id === 1"
            >
              <el-icon><i-ep-delete /></el-icon>
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
            <el-input v-model="userForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password" v-if="!userForm.id">
            <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="角色" prop="role">
            <el-select v-model="userForm.role" placeholder="请选择角色">
              <el-option label="管理员" value="admin" />
              <el-option label="普通用户" value="user" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-switch
              v-model="userForm.status"
              active-value="1"
              inactive-value="0"
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
import { ref, reactive, onMounted, computed } from 'vue'
import mockService from '@/mock/mockService'
import { ElMessage, ElMessageBox } from 'element-plus'

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
  role: 'user',
  status: '1'
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 选中的用户
const selectedUsers = ref([])

// 表单标题
const formTitle = computed(() => {
  return userForm.id ? '编辑用户' : '添加用户'
})

// 获取用户列表
const getUserList = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      status: statusFilter.value
    }
    const res = await mockService.user.getList(params)
    if (res.code === '200') {
      users.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
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
  Object.assign(userForm, row)
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

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  try {
    await userFormRef.value.validate()
    let res
    if (userForm.id) {
      // 编辑用户
      res = await mockService.user.update(userForm)
    } else {
      // 添加用户
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
    role: 'user',
    status: '1'
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
      row.status = row.status === '1' ? '0' : '1'
      ElMessage.error('更新状态失败')
    }
  } catch (error) {
    console.error('更新状态失败:', error)
    // 恢复原始状态
    row.status = row.status === '1' ? '0' : '1'
    ElMessage.error('更新状态失败')
  }
}

onMounted(() => {
  getUserList()
})
</script>

<style scoped lang="scss">
// 页面样式使用全局统一样式
</style>