<!--
  歌单管理页面
  
  数据流说明：
  1. 页面加载时调用 getPlaylistList() 获取歌单列表
  2. 搜索功能通过 handleSearch() 触发，支持按歌单名称和创建者筛选
  3. 分页功能通过 handleCurrentChange() 和 handleSizeChange() 实现
  4. 表单提交通过 handleSubmit() 处理，支持添加和编辑歌单
  5. 删除功能通过 handleDelete() 实现，带确认对话框
  
  API详细说明：
  1. 获取歌单列表 - GET /admin/playlists
  2. 获取分类列表 - GET /admin/categories
  3. 添加歌单 - POST /admin/playlists
  4. 编辑歌单 - PUT /admin/playlists/{id}
  5. 删除歌单 - DELETE /admin/playlists/{id}
  6. 封面上传 - POST /admin/upload/cover
  
  API替换说明：
  1. 当前使用 mockService.playlist 模拟数据
  2. 替换为真实API时，请导入 src/api/playlist.js 中的方法
  3. 保持相同的返回格式：{ code: '200', data: {...} }
-->

<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="playlist-management-card">
      <template #header>
        <div class="card-header">
          <span>歌单管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加歌单
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索歌单名称或描述"
          :prefix-icon="Search"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <!-- 分类筛选 - 多级标签选择 -->
        <div class="category-filter-container">
          <div class="category-selector" @click="showCategoryFilter = !showCategoryFilter">
            <div class="selected-tags-preview" v-if="selectedFilterTags.length > 0">
              <div class="selected-filter-tag" v-for="item in selectedFilterTags.slice(0, 3)" :key="item.id">
                {{ item.name }}
                <el-icon @click.stop="removeFilterTag(item.id)">
                  <Close/>
                </el-icon>
              </div>
              <span v-if="selectedFilterTags.length > 3" class="more-tags-count">+{{
                  selectedFilterTags.length - 3
                }}</span>
            </div>
            <span v-else class="placeholder-text">选择分类标签</span>
            <el-icon class="dropdown-icon">
              <ArrowDown v-if="!showCategoryFilter"/>
              <ArrowUp v-if="showCategoryFilter"/>
            </el-icon>
          </div>
          <div v-if="showCategoryFilter" class="categories-filter-form">
            <div class="categories-filter-title">
              <div class="title-content"
                   v-for="item in categories"
                   :key="item.id"
                   @click="changeFilterTags(item.id)"
                   :class="{ active: item.id === filterUserSelected }">
                <span :class="{ 'bold-text': item.id === filterUserSelected }">{{ item.name }}</span>
                <div v-if="item.id === filterUserSelected" class="select-underline"></div>
              </div>
            </div>
            <div class="categories-filter-content">
              <div class="filter-grid-list">
                <div @click="handleFilterTagSelect(item.id)"
                     v-for="item in filterTags"
                     :key="item.id"
                     :class="['filter-tag', { 'filter-tag-selected': selectedFilterTagIds.includes(item.id) }]">
                  {{ item.name }}
                </div>
              </div>
              <div v-if="filterTags.length === 0" class="empty-tags">
                <el-icon>
                  <InfoFilled/>
                </el-icon>
                <span>暂无标签</span>
              </div>
            </div>
          </div>
        </div>
        <el-select
          v-model="isPublicFilter"
          placeholder="筛选状态"
          clearable
          class="filter-select"
        >
          <el-option label="官方" value="2" />
          <el-option label="公开" value="1" />
          <el-option label="私密" value="0" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 歌单列表 -->
      <el-table
        :data="playlistList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="歌单名称" min-width="200">
          <template #default="scope">
            <div class="playlist-name">
              <el-image
                v-if="scope.row.coverImage"
                :src="`http://localhost:8080${scope.row.coverImage}`"
                :fit="'cover'"
                class="playlist-cover"
              />
              <span class="playlist-title">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="分类" min-width="150">
          <template #default="scope">
            <el-tag
                v-for="category in scope.row.categories"
                :key="category.id"
                size="small"
                style="margin-right: 5px; margin-bottom: 2px;"
            >
              {{ category.name }}
            </el-tag>
            <span v-if="!scope.row.categories || scope.row.categories.length === 0">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="userNickname" label="创建者" min-width="120" />
        <el-table-column prop="musicCount" label="歌曲数量" min-width="120" />
        <el-table-column prop="playCount" label="播放次数" min-width="120" />
        <el-table-column prop="isPublic" label="状态" min-width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.isPublic === 2 ? 'primary' : (scope.row.isPublic === 1 ? 'success' : 'danger')"
            >
              {{ scope.row.isPublic === 2 ? '官方' : (scope.row.isPublic === 1 ? '公开' : '私密') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <!-- 官方歌单允许编辑和删除 -->
              <template v-if="scope.row.isPublic === 2">
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(scope.row)"
              >
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row)"
              >
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
            <span v-else>-</span>
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

      <!-- 歌单表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="formTitle"
        width="600px"
      >
        <el-form
          ref="playlistFormRef"
          :model="playlistForm"
          :rules="playlistRules"
          label-position="top"
        >
          <el-form-item label="歌单名称" prop="title">
            <el-input v-model="playlistForm.title" placeholder="请输入歌单名称" />
          </el-form-item>
          <el-form-item label="分类" prop="categoryIds">
            <div class="category-form-container">
              <div class="category-form-selector" @click="showCategoryForm = !showCategoryForm">
                <div class="selected-tags-preview" v-if="selectedFormTags.length > 0">
                  <div class="selected-form-tag" v-for="item in selectedFormTags.slice(0, 3)" :key="item.id">
                    {{ item.name }}
                    <el-icon @click.stop="removeFormTag(item.id)">
                      <Close/>
                    </el-icon>
                  </div>
                  <span v-if="selectedFormTags.length > 3" class="more-tags-count">+{{
                      selectedFormTags.length - 3
                    }}</span>
                </div>
                <span v-else class="placeholder-text">选择分类标签</span>
                <el-icon class="dropdown-icon">
                  <ArrowDown v-if="!showCategoryForm"/>
                  <ArrowUp v-if="showCategoryForm"/>
                </el-icon>
              </div>
              <div v-if="showCategoryForm" class="categories-form-panel">
                <div class="categories-form-title">
                  <div class="title-content"
                       v-for="item in categories"
                       :key="item.id"
                       @click="changeFormTags(item.id)"
                       :class="{ active: item.id === formUserSelected }">
                    <span :class="{ 'bold-text': item.id === formUserSelected }">{{ item.name }}</span>
                    <div v-if="item.id === formUserSelected" class="select-underline"></div>
                  </div>
                </div>
                <div class="categories-form-content">
                  <div class="form-grid-list">
                    <div @click="handleFormTagSelect(item.id)"
                         v-for="item in formTags"
                         :key="item.id"
                         :class="['form-tag', { 'form-tag-selected': selectedFormTagIds.includes(item.id) }]">
                      {{ item.name }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="歌单描述" prop="description">
            <el-input
              v-model="playlistForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入歌单描述"
            />
          </el-form-item>

          <el-form-item label="封面图片" prop="coverImage">
            <el-upload
                class="upload-demo"
                action=""
                :before-upload="beforeCoverUpload"
                :on-remove="handleCoverRemove"
                :auto-upload="false"
                ref="coverUploadRef"
                :on-change="handleCoverChange"
            >
              <template #trigger>
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  上传封面图片
                </el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传JPG、PNG格式的图片，建议尺寸300x300
                </div>
              </template>
            </el-upload>
            <el-image
              v-if="playlistForm.coverImage"
              :src="playlistForm.coverImage.startsWith('data:') ? playlistForm.coverImage : `http://localhost:8080${playlistForm.coverImage}`"
              :fit="'cover'"
              style="width: 100px; height: 100px; margin-top: 10px"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Refresh, Close, ArrowDown, ArrowUp, InfoFilled } from '@element-plus/icons-vue'
import request from "@/utils/request.js";

// 歌单列表数据
const playlistList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const categoryFilter = ref('')
const isPublicFilter = ref('')

// 分类筛选相关数据
const showCategoryFilter = ref(false)
const filterUserSelected = ref(1) // 默认选中第一个分类
const filterTags = ref([]) // 当前分类下的标签
const selectedFilterTags = ref([]) // 选中的筛选标签对象数组
const selectedFilterTagIds = ref([]) // 选中的筛选标签ID数组

// 下拉选择数据
const categories = ref([])
const allCategories = ref({}) // 所有分类信息映射表 {id: category}

// 对话框
const dialogVisible = ref(false)
const playlistFormRef = ref()
const playlistForm = reactive({
    id: '',
    title: '',
    categoryIds: [],
    description: '',
    isPublic: 2, // 管理员默认创建官方歌单
    coverImage: ''
  })

// 表单分类选择相关数据
const showCategoryForm = ref(false)
const formUserSelected = ref(1) // 默认选中第一个分类
const formTags = ref([]) // 当前分类下的标签
const selectedFormTags = ref([]) // 选中的表单标签对象数组
const selectedFormTagIds = ref([]) // 选中的表单标签ID数组

// 表单验证规则
const playlistRules = {
  title: [
    { required: true, message: '请输入歌单名称', trigger: 'blur' },
    { min: 1, max: 100, message: '歌单名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  categoryIds: [
    { required: true, message: '请选择至少一个分类', trigger: 'change' },
    { type: 'array', min: 1, message: '请选择至少一个分类', trigger: 'change' }
  ]
}

// 上传组件引用
const coverUploadRef = ref()

// 待上传的封面文件
const pendingCoverFile = ref(null)

// 选中的歌单
const selectedPlaylists = ref([])

// 表单标题
const formTitle = computed(() => {
  return playlistForm.id ? '编辑歌单' : '添加歌单'
})

// 获取歌单列表
// API: GET /admin/playlists
// 请求参数:
// - pageNum: 页码，默认1
// - pageSize: 每页条数，默认20
// - keyword: 搜索关键词（歌单名称或描述）
// - categoryIds: 分类ID筛选（多个用逗号分隔）
// - isPublic: 状态筛选（1:公开, 0:私有）
// 响应体:
// {
//   "code": "200",
//   "message": "请求成功",
//   "data": {
//     "total": 2,
//     "list": [
//       {
//         "id": 1,
//         "title": "测试歌单芜湖起飞",
//         "categories": [
//           {
//             "id": "26",
//             "name": "华语"
//           },
//           {
//             "id": "28",
//             "name": "日语"
//           }
//         ],
//         "userNickname": "测试专用用户114514",
//         "description": "新描述",
//         "musicCount": 82,
//         "playCount": 14,
//         "isPublic": 1, // 1表示公开，0表示私有
//         "coverImage": "/uploads/covers/1762408080509_031899fab3727cff261714e3259740e5.jpg",
//         "createdAt": "2025-10-22 17:01:27"
//       }
//     ]
//   }
// }
const getPlaylistList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      categoryIds: selectedFilterTagIds.value.join(','),
      isPublic: isPublicFilter.value
    }
    // 使用真实API替代mockService
    const res = await request.get('playlists/getAll', { params })
    if (res.code === '200') {
          // 直接使用API返回的数据
      playlistList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取歌单列表失败:', error)
    ElMessage.error('获取歌单列表失败')
  }
}

// 获取分类列表
// API: GET /admin/categories
// 请求参数: 无
// 响应体:
// {
//   "code": "200",
//   "data": [
//     {
//       "id": "1",
//       "name": "分类名称",
//       "description": "分类描述",
//       "musicCount": 100,
//       "playlistCount": 20,
//       "createTime": "2024-01-20 10:30:00"
//     }
//   ]
// }
const getCategories = async () => {
  try {
    const res = await request.get('categories')
    if (res.code === '200') {
      categories.value = res.data
      // 将分类添加到allCategories映射表
      categories.value.forEach(category => {
        allCategories.value[category.id] = category
      })
      // 获取默认分类的子分类
      if (categories.value.length > 0) {
        await getSubCategories(formUserSelected.value, 'form')
        await getSubCategories(filterUserSelected.value, 'filter')
      }
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取子分类
const getSubCategories = async (parentId, type) => {
  try {
    const res = await request.get('categories', {params: {parentId}})
    if (res.code === '200') {
      if (type === 'form') {
        formTags.value = res.data
      } else if (type === 'filter') {
        filterTags.value = res.data
      }
      // 将子分类添加到allCategories映射表
      res.data.forEach(category => {
        allCategories.value[category.id] = category
      })
    }
  } catch (error) {
    console.error('获取子分类失败:', error)
  }
}

// 表单分类 - 切换分类类型
const changeFormTags = async (categoryId) => {
  formUserSelected.value = categoryId
  await getSubCategories(categoryId, 'form')
}

// 表单分类 - 选择标签
const handleFormTagSelect = (tagId) => {
  const index = selectedFormTagIds.value.indexOf(tagId)
  const tag = formTags.value.find(item => item.id === tagId)

  if (index > -1) {
    // 取消选择
    selectedFormTagIds.value.splice(index, 1)
    const tagIndex = selectedFormTags.value.findIndex(item => item.id === tagId)
    if (tagIndex > -1) {
      selectedFormTags.value.splice(tagIndex, 1)
    }
  } else {
    // 选择
    selectedFormTagIds.value.push(tagId)
    if (tag) {
      selectedFormTags.value.push(tag)
    }
  }
  
  // 更新表单数据
  playlistForm.categoryIds = selectedFormTagIds.value
}

// 表单分类 - 移除标签
const removeFormTag = (tagId) => {
  const index = selectedFormTagIds.value.indexOf(tagId)
  if (index > -1) {
    selectedFormTagIds.value.splice(index, 1)
    const tagIndex = selectedFormTags.value.findIndex(item => item.id === tagId)
    if (tagIndex > -1) {
      selectedFormTags.value.splice(tagIndex, 1)
    }
    
    // 更新表单数据
    playlistForm.categoryIds = selectedFormTagIds.value
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getPlaylistList()
}



// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getPlaylistList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getPlaylistList()
}

// 选择歌单
const handleSelectionChange = (selection) => {
  selectedPlaylists.value = selection
}

// 添加歌单
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑歌单
const handleEdit = async (row) => {
  // 使用API返回的字段直接设置表单
  playlistForm.id = row.id
  playlistForm.title = row.title
  playlistForm.description = row.description
  playlistForm.isPublic = row.isPublic // 直接使用后端返回的数字值
  playlistForm.coverImage = row.coverImage
  
  // 设置分类选择状态
  selectedFormTagIds.value = row.categories ? row.categories.map(cat => cat.id) : []
  selectedFormTags.value = row.categories || []
  
  dialogVisible.value = true
}

// 删除歌单
// API: DELETE /admin/playlists/{id}
// 请求参数: 路径参数 id（歌单ID）
// 响应体:
// {
//   "code": "200",
//   "message": "删除成功"
// }
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该歌单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 使用真实API替代mockService
      const res = await request.delete(`playlists/delete/${row.id}`)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getPlaylistList()
      }
    } catch (error) {
      console.error('删除歌单失败:', error)
      ElMessage.error('删除歌单失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单（添加/编辑歌单）
// 添加歌单 API: POST /admin/playlists
// 编辑歌单 API: PUT /admin/playlists/{id}
// 请求体:
// {
//   "id": "1",          // 编辑时必填
//   "title": "歌单名称",  // 必填
//   "categoryIds": ["1", "2"], // 分类ID列表
//   "description": "歌单描述",
//   "isPublic": true   // true/false
// }
// 响应体:
// {
//   "code": "200",
//   "message": "操作成功"
// }
const handleSubmit = async () => {
  if (!playlistFormRef.value) return
  try {
    await playlistFormRef.value.validate()
    let res
    let newPlaylistId = null

    // 准备请求数据，适配后端API字段名称
    const requestData = {
      title: playlistForm.title, // 直接使用title字段
      categoryIds: playlistForm.categoryIds,
      description: playlistForm.description,
      isPublic: playlistForm.isPublic // 使用数字值（1:公开, 0:私有）
    }

    if (playlistForm.id) {
      // 编辑模式
      // 如果是编辑模式且有待上传的封面文件，先上传封面
      let coverImage = playlistForm.coverImage
      if (pendingCoverFile.value) {
        // 上传封面文件
        const formData = new FormData()
        formData.append('file', pendingCoverFile.value)
        const uploadRes = await request.post(`upload/playlistCover/${playlistForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        if (uploadRes.code === '200') {
          coverImage = uploadRes.data
          ElMessage.success('封面上传成功')
        } else {
          throw new Error('封面上传失败')
        }
      }

      // 添加封面URL到请求数据
      requestData.coverImage = coverImage
      requestData.id = playlistForm.id

      // 使用真实API替代mockService
        res = await request.put(`playlists/update`, requestData)
    } else {
      // 添加模式 - 先创建歌单
      // 使用真实API替代mockService
      res = await request.post('playlists/add', requestData)

      if (res.code === '200' && res.data && res.data.id) {
        newPlaylistId = res.data.id
        playlistForm.id = newPlaylistId
      }
    }

    // 如果是添加模式且有待上传封面，上传封面
    if (newPlaylistId && pendingCoverFile.value) {
      try {
        // 上传封面文件
        const formData = new FormData()
        formData.append('file', pendingCoverFile.value)
        const uploadRes = await request.post(`upload/playlistCover/${newPlaylistId}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        if (uploadRes.code === '200') {
          ElMessage.success('封面上传成功')
        }
      } catch (uploadError) {
        console.error('封面上传失败:', uploadError)
        ElMessage.warning('歌单创建成功，但封面上传失败')
      }
    }

    if (res.code === '200') {
      ElMessage.success(playlistForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      // 清空待上传文件
      pendingCoverFile.value = null
      getPlaylistList()
    }
  } catch (error) {
    console.error(playlistForm.id ? '编辑歌单失败:' : '添加歌单失败:', error)
    ElMessage.error(playlistForm.id ? '编辑歌单失败' : '添加歌单失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(playlistForm, {
    id: '',
    title: '',
    categoryIds: [],
    description: '',
    isPublic: 2, // 管理员只能创建官方歌单
    coverImage: ''
  })
  if (playlistFormRef.value) {
    playlistFormRef.value.resetFields()
  }
  // 重置分类选择状态
  selectedFormTags.value = []
  selectedFormTagIds.value = []
  // 清空待上传文件
  pendingCoverFile.value = null
}

// 筛选分类 - 切换分类类型
const changeFilterTags = async (categoryId) => {
  filterUserSelected.value = categoryId
  await getSubCategories(categoryId, 'filter')
}

// 筛选分类 - 选择标签
const handleFilterTagSelect = (tagId) => {
  const index = selectedFilterTagIds.value.indexOf(tagId)
  const tag = filterTags.value.find(item => item.id === tagId)

  if (index > -1) {
    // 取消选择
    selectedFilterTagIds.value.splice(index, 1)
    const tagIndex = selectedFilterTags.value.findIndex(item => item.id === tagId)
    if (tagIndex > -1) {
      selectedFilterTags.value.splice(tagIndex, 1)
    }
  } else {
    // 选择
    selectedFilterTagIds.value.push(tagId)
    if (tag) {
      selectedFilterTags.value.push(tag)
    }
  }
}

// 筛选分类 - 移除标签
const removeFilterTag = (tagId) => {
  const index = selectedFilterTagIds.value.indexOf(tagId)
  if (index > -1) {
    selectedFilterTagIds.value.splice(index, 1)
    const tagIndex = selectedFilterTags.value.findIndex(item => item.id === tagId)
    if (tagIndex > -1) {
      selectedFilterTags.value.splice(tagIndex, 1)
    }
  }
}

// 重置搜索和筛选
const handleReset = () => {
  searchQuery.value = ''
  isPublicFilter.value = ''
  selectedFilterTags.value = []
  selectedFilterTagIds.value = []
  showCategoryFilter.value = false
  currentPage.value = 1
  getPlaylistList()
}

// 封面上传前处理
const beforeCoverUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isJPG && !isPNG) {
    ElMessage.error('只能上传JPG或PNG格式的图片')
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
  }
  return isJPG || isPNG && isLt5M
}

// 封面文件变化处理 - 保存待上传文件但不立即上传
const handleCoverChange = (uploadFile) => {
  pendingCoverFile.value = uploadFile.raw
}

// 封面文件移除处理
const handleCoverRemove = () => {
  pendingCoverFile.value = null
}

onMounted(() => {
  getPlaylistList()
  getCategories()
})
</script>

<style scoped lang="scss">
// 只保留页面特有的样式，其他样式使用全局样式
@use '@/assets/styles/variables.scss' as *;

.playlist-name {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.playlist-cover {
  width: 40px;
  height: 40px;
  border-radius: $radius-sm;
  object-fit: cover;
  flex-shrink: 0;
}

.playlist-title {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  max-height: 2.8em;
}

// 分类标签选择器样式 - 与歌曲管理页面保持一致
.category-filter-container, .category-form-container {
  position: relative;
  min-width: 280px;
  max-width: 320px;
}

.category-selector, .category-form-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  height: 30px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
  }

  .selected-tags-preview {
    display: flex;
    flex-wrap: wrap;
    gap: 3px;
    flex: 1;
    overflow: hidden;
    margin-right: 6px;
    align-items: center;
  }

  .placeholder-text {
    color: #909399;
    font-size: 12px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .dropdown-icon {
    color: #c0c4cc;
    transition: transform 0.3s;
  }

  &.active .dropdown-icon {
    transform: rotate(180deg);
  }
}

.more-tags-count {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  background: #f0f2f5;
  color: #909399;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  margin-left: 4px;
}

.selected-form-tag, .selected-filter-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  border-radius: 4px;
  font-size: 11px;
  white-space: nowrap;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

  .el-icon {
    margin-left: 4px;
    cursor: pointer;
    font-size: 10px;
    transition: all 0.2s;

    &:hover {
      color: #ffd700;
      transform: scale(1.1);
    }
  }
}

.categories-form-panel, .categories-filter-form {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  padding: 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  z-index: 100;
  min-width: 320px;
  max-width: 400px;
  backdrop-filter: blur(10px);
}

.categories-form-title, .categories-filter-title {
  display: flex;
  gap: 4px;
  margin-bottom: 16px;
  padding: 8px 12px;
  border-bottom: 1px solid #f0f2f5;
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  flex-shrink: 0;

  .title-content {
    flex: 1;
    text-align: center;
    position: relative;
    cursor: pointer;
    padding: 10px 8px;
    border-radius: 6px;
    transition: all 0.3s;
    min-height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      background: rgba(64, 158, 255, 0.08);
    }

    &.active {
      background: rgba(64, 158, 255, 0.12);
    }

    span {
      font-size: 13px;
      color: #606266;
      transition: all 0.3s;
      font-weight: 500;
    }

    .bold-text {
      color: #409eff;
      font-weight: 600;
    }

    .select-underline {
      position: absolute;
      bottom: 2px;
      left: 25%;
      right: 25%;
      height: 2px;
      background: #409eff;
      border-radius: 1px;
    }
  }
}

.search-filter {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;

  .search-input {
    width: 300px;
  }

  .filter-select {
    width: 150px;
  }
}

.form-grid-list, .filter-grid-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
  gap: 6px;
  max-height: 280px;
  overflow-y: auto;
  padding: 6px;
}

.form-tag, .filter-tag {
  padding: 6px 4px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #f8f9fa;
  cursor: pointer;
  text-align: center;
  font-size: 11px;
  color: #606266;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
  min-height: 24px;
  line-height: 1.2;

  &:hover {
    border-color: #409eff;
    background: #f0f7ff;
    color: #409eff;
    transform: translateY(-1px);
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.15);
  }

  &.form-tag-selected, &.filter-tag-selected {
    background: linear-gradient(135deg, #409eff, #66b1ff);
    color: #fff;
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.25);

    &:hover {
      background: linear-gradient(135deg, #3a8ee6, #5c9eff);
      transform: translateY(-1px);
      box-shadow: 0 3px 12px rgba(64, 158, 255, 0.3);
    }
  }
}

.empty-tags {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  color: #909399;
  font-size: 14px;
  gap: 8px;

  .el-icon {
    font-size: 24px;
    color: #c0c4cc;
  }
}

/* 滚动条样式 */
.filter-grid-list::-webkit-scrollbar,
.form-grid-list::-webkit-scrollbar {
  width: 6px;
}

.filter-grid-list::-webkit-scrollbar-track,
.form-grid-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.filter-grid-list::-webkit-scrollbar-thumb,
.form-grid-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;

  &:hover {
    background: #a8a8a8;
  }
}

/* 选中标签样式优化 */
.selected-filter-tag,
.selected-form-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 6px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: #fff;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  margin: 1px;

  .el-icon {
    margin-left: 3px;
    cursor: pointer;
    font-size: 9px;
    transition: all 0.2s;

    &:hover {
      color: #ffd700;
      transform: scale(1.1);
    }
  }
}
</style>