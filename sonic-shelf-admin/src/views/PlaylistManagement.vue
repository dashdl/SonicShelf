<template>
  <div class="playlist-management-container">
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
        <el-select
          v-model="categoryFilter"
          placeholder="筛选分类"
          clearable
          filterable
          class="filter-select"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>
        <el-select
          v-model="statusFilter"
          placeholder="筛选状态"
          clearable
          class="filter-select"
        >
          <el-option label="公开" value="public" />
          <el-option label="私有" value="private" />
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
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="歌单名称" width="200">
          <template #default="scope">
            <div class="playlist-name">
              <el-image
                v-if="scope.row.coverUrl"
                :src="scope.row.coverUrl"
                :fit="'cover'"
                class="playlist-cover"
              />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="creatorName" label="创建者" width="120" />
        <el-table-column prop="musicCount" label="歌曲数量" width="120" />
        <el-table-column prop="playCount" label="播放次数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.status === 'public' ? 'success' : 'info'"
            >
              {{ scope.row.status === 'public' ? '公开' : '私有' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
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
          <el-form-item label="歌单名称" prop="name">
            <el-input v-model="playlistForm.name" placeholder="请输入歌单名称" />
          </el-form-item>
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="playlistForm.categoryId" placeholder="请选择分类" filterable clearable style="width: 100%;">
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="歌单描述" prop="description">
            <el-input
              v-model="playlistForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入歌单描述"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="playlistForm.status">
              <el-radio label="public">公开</el-radio>
              <el-radio label="private">私有</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="封面图片" prop="coverUrl">
            <el-upload
              class="upload-demo"
              action=""
              :on-success="handleCoverUpload"
              :before-upload="beforeCoverUpload"
              :auto-upload="false"
              ref="coverUploadRef"
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
              v-if="playlistForm.coverUrl"
              :src="playlistForm.coverUrl"
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
import mockService from '@/mock/mockService'
import { ElMessage, ElMessageBox } from 'element-plus'

// 歌单列表数据
const playlistList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')

// 下拉选择数据
const categories = ref([])

// 对话框
const dialogVisible = ref(false)
const playlistFormRef = ref()
const playlistForm = reactive({
  id: '',
  name: '',
  categoryId: '',
  description: '',
  status: 'public',
  coverUrl: ''
})

// 表单验证规则
const playlistRules = {
  name: [
    { required: true, message: '请输入歌单名称', trigger: 'blur' },
    { min: 1, max: 100, message: '歌单名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
}

// 上传组件引用
const coverUploadRef = ref()

// 选中的歌单
const selectedPlaylists = ref([])

// 表单标题
const formTitle = computed(() => {
  return playlistForm.id ? '编辑歌单' : '添加歌单'
})

// 获取歌单列表
const getPlaylistList = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      categoryId: categoryFilter.value,
      status: statusFilter.value
    }
    const res = await mockService.playlist.getList(params)
    if (res.code === '200') {
      playlistList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取歌单列表失败:', error)
    ElMessage.error('获取歌单列表失败')
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await mockService.category.getList({ pageSize: 1000 })
    if (res.code === '200') {
      categories.value = res.data.list
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getPlaylistList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  categoryFilter.value = ''
  statusFilter.value = ''
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
const handleEdit = (row) => {
  Object.assign(playlistForm, row)
  dialogVisible.value = true
}

// 删除歌单
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该歌单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await mockService.playlist.delete(row.id)
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

// 提交表单
const handleSubmit = async () => {
  if (!playlistFormRef.value) return
  try {
    await playlistFormRef.value.validate()
    let res
    if (playlistForm.id) {
      // 编辑歌单
      res = await mockService.playlist.update(playlistForm)
    } else {
      // 添加歌单
      res = await mockService.playlist.add(playlistForm)
    }
    if (res.code === '200') {
      ElMessage.success(playlistForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
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
    name: '',
    categoryId: '',
    description: '',
    status: 'public',
    coverUrl: ''
  })
  if (playlistFormRef.value) {
    playlistFormRef.value.resetFields()
  }
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

// 封面上传成功处理
const handleCoverUpload = (response) => {
  if (response.code === '200') {
    playlistForm.coverUrl = response.data.url
    ElMessage.success('封面上传成功')
  }
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
}
</style>