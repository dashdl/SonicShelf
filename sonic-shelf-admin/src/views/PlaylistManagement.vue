<template>
  <div class="admin-management-container">
    <!-- 歌单音乐编辑组件 -->
    <PlaylistMusicEditor ref="playlistMusicEditorRef" />
    
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
        <!-- 分类筛选 - 使用CategorySelector组件 -->
        <CategorySelector
          v-model:selectedTagIds="selectedFilterTagIds"
          v-model:selectedTags="selectedFilterTags"
          placeholder="选择分类标签"
        />
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
        <el-table-column label="歌曲数量" min-width="120">
          <template #default="scope">
            <div class="music-count-cell">
              <span>{{ scope.row.musicCount }}</span>
              <el-button
                v-if="scope.row.isPublic === 2"
                type="primary"
                size="small"
                @click="handleEditMusic(scope.row)"
                class="edit-music-btn"
              >
                <el-icon><Edit /></el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>
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
            <CategorySelector
              v-model:selectedTagIds="selectedFormTagIds"
              v-model:selectedTags="selectedFormTags"
              placeholder="选择分类标签"
            />
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
import CategorySelector from "@/components/common/CategorySelector.vue";
import PlaylistMusicEditor from "@/components/music/PlaylistMusicEditor.vue";

// 歌单列表数据
const playlistList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const categoryFilter = ref('')
const isPublicFilter = ref('2') // 默认筛选官方歌单

// 分类选择相关数据 - 由CategorySelector组件使用
const selectedFilterTagIds = ref([]) // 选中的筛选标签ID数组
const selectedFilterTags = ref([]) // 选中的筛选标签对象数组
const selectedFormTagIds = ref([]) // 选中的表单标签ID数组
const selectedFormTags = ref([]) // 选中的表单标签对象数组

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

// 歌单音乐编辑组件引用
const playlistMusicEditorRef = ref()

// 待上传的封面文件
const pendingCoverFile = ref(null)

// 选中的歌单
const selectedPlaylists = ref([])

// 表单标题
const formTitle = computed(() => {
  return playlistForm.id ? '编辑歌单' : '添加歌单'
})

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

// 编辑歌单音乐信息
const handleEditMusic = (row) => {
  // 调用歌单音乐编辑组件打开对话框
  playlistMusicEditorRef.value.open(row)
}

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

const handleSubmit = async () => {
  if (!playlistFormRef.value) return
  try {
    await playlistFormRef.value.validate()
    let res
    let newPlaylistId = null

    // 准备请求数据，适配后端API字段名称
    const requestData = {
      title: playlistForm.title, // 直接使用title字段
      categoryIds: selectedFormTagIds.value,
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



// 重置搜索和筛选
const handleReset = () => {
  searchQuery.value = ''
  isPublicFilter.value = '2' // 重置为默认筛选官方歌单
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
})
</script>

<style scoped lang="scss">
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

.music-count-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .edit-music-btn {
    margin-left: 8px;
  }
}
</style>