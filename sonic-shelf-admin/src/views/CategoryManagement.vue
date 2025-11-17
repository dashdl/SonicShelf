<template>
  <div class="category-management-container">
    <el-card shadow="hover" class="category-management-card">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><i-ep-plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>
      
      <!-- 搜索 -->
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索分类名称"
          prefix-icon="i-ep-search"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">
          <el-icon><i-ep-search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><i-ep-refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 分类列表 -->
      <el-table
        :data="categoryList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" width="200" />
        <el-table-column prop="description" label="分类描述" width="300" />
        <el-table-column prop="musicCount" label="音乐数量" width="120" />
        <el-table-column prop="playlistCount" label="歌单数量" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              <el-icon><i-ep-edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
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

      <!-- 分类表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="formTitle"
        width="500px"
      >
        <el-form
          ref="categoryFormRef"
          :model="categoryForm"
          :rules="categoryRules"
          label-position="top"
        >
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="分类描述" prop="description">
            <el-input
              v-model="categoryForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入分类描述"
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

// 分类列表数据
const categoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索
const searchQuery = ref('')

// 对话框
const dialogVisible = ref(false)
const categoryFormRef = ref()
const categoryForm = reactive({
  id: '',
  name: '',
  description: ''
})

// 表单验证规则
const categoryRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 选中的分类
const selectedCategories = ref([])

// 表单标题
const formTitle = computed(() => {
  return categoryForm.id ? '编辑分类' : '添加分类'
})

// 获取分类列表
const getCategoryList = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value
    }
    const res = await mockService.category.getList(params)
    if (res.code === '200') {
      categoryList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    ElMessage.error('获取分类列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getCategoryList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  currentPage.value = 1
  getCategoryList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getCategoryList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getCategoryList()
}

// 选择分类
const handleSelectionChange = (selection) => {
  selectedCategories.value = selection
}

// 添加分类
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑分类
const handleEdit = (row) => {
  Object.assign(categoryForm, row)
  dialogVisible.value = true
}

// 删除分类
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await mockService.category.delete(row.id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getCategoryList()
      }
    } catch (error) {
      console.error('删除分类失败:', error)
      ElMessage.error('删除分类失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!categoryFormRef.value) return
  try {
    await categoryFormRef.value.validate()
    let res
    if (categoryForm.id) {
      // 编辑分类
      res = await mockService.category.update(categoryForm)
    } else {
      // 添加分类
      res = await mockService.category.add(categoryForm)
    }
    if (res.code === '200') {
      ElMessage.success(categoryForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      getCategoryList()
    }
  } catch (error) {
    console.error(categoryForm.id ? '编辑分类失败:' : '添加分类失败:', error)
    ElMessage.error(categoryForm.id ? '编辑分类失败' : '添加分类失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(categoryForm, {
    id: '',
    name: '',
    description: ''
  })
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields()
  }
}

onMounted(() => {
  getCategoryList()
})
</script>

<style scoped lang="scss">
// 页面样式使用全局统一样式
</style>