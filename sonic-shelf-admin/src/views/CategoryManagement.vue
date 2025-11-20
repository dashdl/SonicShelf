<!--
  分类管理页面
  
  数据流说明：
  1. 页面加载时调用 getCategoryList() 获取分类列表
  2. 搜索功能通过 handleSearch() 触发，支持按分类名称筛选
  3. 分页功能通过 handleCurrentChange() 和 handleSizeChange() 实现
  4. 表单提交通过 handleSubmit() 处理，支持添加和编辑分类
  5. 删除功能通过 handleDelete() 实现，带确认对话框
  
  API替换说明：
  1. 当前使用 mockService.category 模拟数据
  2. 替换为真实API时，请导入 src/api/category.js 中的方法
  3. 保持相同的返回格式：{ code: '200', data: {...} }
  
  后端API要求：
  - GET /admin/categories - 获取分类列表（支持分页、搜索）
  - POST /admin/categories - 添加分类
  - PUT /admin/categories/{id} - 更新分类
  - DELETE /admin/categories/{id} - 删除分类
-->

<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="category-management-card">
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAddCategory">
            <el-icon>
              <Plus/>
            </el-icon>
            添加分类
          </el-button>
        </div>
      </template>

      <!-- 一级分类选项卡 -->
      <div class="category-tabs">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <!-- 一级分类选项卡 -->
          <el-tab-pane
              v-for="category in parentCategories"
              :key="category.id"
              :name="category.id.toString()"
          >
            <template #label>
              <div class="tab-label-container">
                <span class="tab-label">{{ category.name }}</span>
                <div class="tab-actions">
                  <el-button
                      size="small"
                      @click.stop="handleEditParentCategory(category)"
                      class="tab-action-button"
                  >
                    <el-icon>
                      <Edit/>
                    </el-icon>
                  </el-button>
                  <el-button
                      size="small"
                      type="danger"
                      @click.stop="handleDeleteParentCategory(category)"
                      class="tab-action-button"
                  >
                    <el-icon>
                      <Delete/>
                    </el-icon>
                  </el-button>
                </div>
              </div>
            </template>
            <!-- 当前一级分类下的子分类列表 -->
            <div class="subcategory-content">
              <!-- 搜索 -->
              <div class="search-filter">
                <el-input
                    v-model="searchQuery"
                    placeholder="搜索子分类名称"
                    :prefix-icon="Search"
                    clearable
                    class="search-input"
                    @keyup.enter="handleSearch"
                />
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

              <!-- 子分类列表 -->
              <el-table
                  :data="filteredSubcategories"
                  stripe
                  style="width: 100%"
                  @selection-change="handleSelectionChange"
                  class="flexible-table"
              >
                <el-table-column type="selection" width="55" fixed="left"/>
                <el-table-column prop="id" label="ID" width="80"/>
                <el-table-column prop="name" label="子分类名称" min-width="200"/>
                <el-table-column prop="description" label="子分类描述" min-width="300"/>
                <el-table-column prop="musicCount" label="音乐数量" min-width="120"/>
                <el-table-column prop="playlistCount" label="歌单数量" min-width="120"/>
                <el-table-column prop="createdAt" label="创建时间" min-width="200"/>
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="scope">
                    <el-button
                        type="primary"
                        size="small"
                        @click="handleEditSubcategory(scope.row)"
                    >
                      <el-icon>
                        <Edit/>
                      </el-icon>
                      编辑
                    </el-button>
                    <el-button
                        type="danger"
                        size="small"
                        @click="handleDeleteSubcategory(scope.row)"
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
                    :total="filteredSubcategories.length"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
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
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称"/>
          </el-form-item>
          <el-form-item label="分类描述" prop="description">
            <el-input
                v-model="categoryForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入分类描述"
            />
          </el-form-item>
          <!-- 子分类需要选择父分类 -->
          <el-form-item v-if="!isEditingParent" label="父分类" prop="parentId">
            <el-select v-model="categoryForm.parentId" placeholder="请选择父分类">
              <el-option
                  label="无（添加一级分类）"
                  :value="0"
              />
              <el-option
                  v-for="category in parentCategories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
              />
            </el-select>
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
import {ref, reactive, onMounted, computed, h} from 'vue'
import mockService from '@/mock/mockService'
import {ElMessage, ElMessageBox} from 'element-plus'
import request from "@/utils/request.js";
import {Search, Plus, Edit, Delete, Refresh} from '@element-plus/icons-vue'

// 分类列表数据
const categoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 一级分类列表
const parentCategories = ref([])
// 子分类数据，按一级分类ID存储
const subCategoriesMap = ref(new Map())
// 所有分类列表（包含子分类）
const allCategories = ref([])

// 激活的选项卡
const activeTab = ref('')

// 搜索
const searchQuery = ref('')

// 对话框
const dialogVisible = ref(false)
const categoryFormRef = ref()
// 分类表单数据
// 数据结构说明：
// id: 分类ID（编辑时必填，新增时为空）
// name: 分类名称（必填，1-50个字符）
// description: 分类描述（可选）
// parentId: 父分类ID（0表示一级分类，其他值表示二级分类的父ID）
const categoryForm = reactive({
  id: '',
  name: '',
  description: '',
  parentId: 0
})

// 是否正在编辑一级分类
const isEditingParent = ref(false)

// 表单验证规则
const categoryRules = {
  name: [
    {required: true, message: '请输入分类名称', trigger: 'blur'},
    {min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur'}
  ]
}

// 选中的分类
const selectedCategories = ref([])

// 表单标题
const formTitle = computed(() => {
  return categoryForm.id ? (isEditingParent.value ? '编辑一级分类' : '编辑子分类') : (categoryForm.parentId === 0 ? '添加一级分类' : '添加子分类')
})

// 获取当前选项卡对应的子分类
const currentSubcategories = computed(() => {
  if (!activeTab.value) return []
  const parentId = parseInt(activeTab.value)
  return subCategoriesMap.value.get(parentId) || []
})

// 搜索过滤后的子分类
const filteredSubcategories = computed(() => {
  if (!searchQuery.value) {
    return currentSubcategories.value
  }
  return currentSubcategories.value.filter(category =>
      category.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 获取一级分类列表
const getParentCategories = async () => {
  try {
    const res = await request.get('categories/getAll')
    if (res.code === '200') {
      // 只保存parentId为null的分类作为一级分类
      parentCategories.value = res.data.filter(category => category.parentId === null)
      allCategories.value = [...parentCategories.value]

      // 设置默认激活的选项卡
      if (parentCategories.value.length > 0 && !activeTab.value) {
        activeTab.value = parentCategories.value[0].id.toString()
        // 获取默认选项卡的子分类
        getSubCategories(activeTab.value)
      }
    }
  } catch (error) {
    console.error('获取一级分类列表失败:', error)
    ElMessage.error('获取一级分类列表失败')
  }
}

// 获取指定一级分类的子分类
const getSubCategories = async (parentId) => {
  try {
    // 如果已经获取过该一级分类的子分类，直接返回
    if (subCategoriesMap.value.has(parseInt(parentId))) {
      return
    }

    const res = await request.get('categories/getAll', {params: {parentId}})
    if (res.code === '200' && res.data) {
      const parentIdNum = parseInt(parentId)
      // 保存子分类
      subCategoriesMap.value.set(parentIdNum, res.data)
      // 将子分类添加到allCategories中
      allCategories.value = [...allCategories.value, ...res.data]
    }
  } catch (error) {
    console.error('获取子分类列表失败:', error)
    ElMessage.error('获取子分类列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  currentPage.value = 1
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 选择分类
const handleSelectionChange = (selection) => {
  selectedCategories.value = selection
}

// 选项卡点击事件
const handleTabClick = (tab) => {
  // 切换选项卡时重置搜索和分页
  searchQuery.value = ''
  currentPage.value = 1

  // 获取点击的选项卡对应的子分类
  getSubCategories(tab.props.name)
}

// 添加分类按钮点击事件
// 功能：打开分类添加对话框，根据当前是否有激活的选项卡设置默认的parentId
// 默认行为：
// - 如果有激活的一级分类选项卡，则默认添加该选项卡下的子分类
// - 如果没有激活的选项卡，则默认添加一级分类
const handleAddCategory = () => {
  resetForm()
  // 如果有激活的选项卡，默认添加该选项卡下的子分类
  if (activeTab.value) {
    categoryForm.parentId = parseInt(activeTab.value)
    isEditingParent.value = false
  } else {
    // 否则添加一级分类
    categoryForm.parentId = 0
    isEditingParent.value = false
  }
  dialogVisible.value = true
}

// 编辑一级分类事件
// 功能：打开分类编辑对话框，填充一级分类数据
// 参数：row - 选中的一级分类数据对象
// 数据结构：{id, name, description, parentId, musicCount, playlistCount, createdAt}
// 注意：编辑一级分类时，parentId会被忽略
const handleEditParentCategory = (row) => {
  Object.assign(categoryForm, row)
  isEditingParent.value = true
  dialogVisible.value = true
}

// 编辑子分类事件
// 功能：打开分类编辑对话框，填充子分类数据
// 参数：row - 选中的子分类数据对象
// 数据结构：{id, name, description, parentId, musicCount, playlistCount, createdAt}
const handleEditSubcategory = (row) => {
  Object.assign(categoryForm, row)
  isEditingParent.value = false
  dialogVisible.value = true
}

// 删除一级分类
const handleDeleteParentCategory = (row) => {
  ElMessageBox.confirm('确定要删除该一级分类吗？删除后该分类下的所有子分类也将被删除。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里应该调用API删除一级分类及其子分类
      // const res = await request.delete(`categories/${row.id}`)

      // 使用mockService模拟删除
      const res = await request.delete('categories/delete/' + row.id)

      if (res.code === '200') {
        ElMessage.success('删除成功')

        // 从子分类映射中删除该一级分类的子分类
        subCategoriesMap.value.delete(row.id)

        // 如果删除的是当前激活的选项卡，将activeTab设置为空
        // 这样getParentCategories()方法就会自动设置第一个选项卡为激活状态
        if (activeTab.value === row.id.toString()) {
          activeTab.value = ''
        }

        // 重新获取一级分类列表
        getParentCategories()
      }
    } catch (error) {
      console.error('删除一级分类失败:', error)
      ElMessage.error('删除一级分类失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 删除子分类
const handleDeleteSubcategory = (row) => {
  ElMessageBox.confirm('确定要删除该子分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里应该调用API删除子分类
      // const res = await request.delete(`categories/${row.id}`)

      // 使用mockService模拟删除
      const res = await request.delete('categories/delete/' + row.id)

      if (res.code === '200') {
        ElMessage.success('删除成功')

        // 从子分类映射中删除该子分类
        const parentId = row.parentId
        const subCategories = subCategoriesMap.value.get(parentId)
        if (subCategories) {
          const updatedSubCategories = subCategories.filter(category => category.id !== row.id)
          subCategoriesMap.value.set(parentId, updatedSubCategories)
        }

        // 从allCategories中删除该子分类
        const categoryIndex = allCategories.value.findIndex(category => category.id === row.id)
        if (categoryIndex !== -1) {
          allCategories.value.splice(categoryIndex, 1)
        }
      }
    } catch (error) {
      console.error('删除子分类失败:', error)
      ElMessage.error('删除子分类失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
// 功能：处理分类的添加和编辑操作
// 处理逻辑：
// 1. 表单验证
// 2. 准备提交数据（将parentId=0转换为null以匹配数据库要求）
// 3. 根据是否有id判断是编辑还是添加操作
// 4. 调用相应的API接口
// 5. 处理成功或失败的响应
// 6. 更新页面数据
//
// 请求体结构：
// 添加分类：POST /categories
// {
//   name: string,          // 分类名称（必填）
//   description: string,   // 分类描述（可选）
//   parentId: number|null  // 父分类ID（null表示一级分类，其他值表示二级分类的父ID）
// }
//
// 编辑分类：PUT /categories/:id
// {
//   id: string,            // 分类ID（必填）
//   name: string,          // 分类名称（必填）
//   description: string,   // 分类描述（可选）
//   parentId: number|null  // 父分类ID（null表示一级分类，其他值表示二级分类的父ID）
// }
//
// 响应体结构：
// {
//   code: '200',           // 状态码，200表示成功
//   message: string,       // 操作结果消息
//   data: object|null      // 返回的分类数据（可选）
// }
const handleSubmit = async () => {
  if (!categoryFormRef.value) return
  try {
    await categoryFormRef.value.validate()
    let res

    // 准备提交的数据
    const submitData = {
      ...categoryForm,
      // 将parentId=0转换为null，以匹配数据库要求
      parentId: categoryForm.parentId === 0 ? null : categoryForm.parentId
    }

    if (categoryForm.id) {
      // 编辑分类
      // res = await request.put(`categories/${categoryForm.id}`, submitData)

      // 使用mockService模拟编辑
      res = await request.put('categories/update', submitData)
    } else {
      // 添加分类
      // res = await request.post('categories', submitData)

      // 使用mockService模拟添加
      res = await request.post('categories/add', submitData)
    }

    if (res.code === '200') {
      ElMessage.success(categoryForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false

      // 如果是一级分类操作，重新获取一级分类列表
      if (categoryForm.parentId === 0) {
        // 清空子分类数据
        subCategoriesMap.value.clear()
        // 重新获取一级分类
        getParentCategories()
      } else {
        // 如果是子分类操作，重新获取该子分类所属的一级分类的子分类
        // 清空该一级分类的子分类数据
        subCategoriesMap.value.delete(categoryForm.parentId)
        // 重新获取该一级分类的子分类
        getSubCategories(categoryForm.parentId)
      }
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
    description: '',
    parentId: 0
  })
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields()
  }
}


onMounted(() => {
  getParentCategories()
})
</script>

<style scoped lang="scss">
.category-management-card {
  .tab-label-container {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;

    .tab-label {
      margin-right: 10px;
    }

    .tab-actions {
      display: flex;
      gap: 3px;

      .tab-action-button {
        padding: 0;
        min-width: auto;
        height: 18px;
        font-size: 12px;
        margin: 0;
      }
    }
  }

  .category-tabs {
    margin-bottom: 20px;
    padding: 0 10px;
  }

  .search-filter {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    
    .search-input {
      width: 300px;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>