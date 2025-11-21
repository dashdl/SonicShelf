<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="album-management-card">
      <template #header>
        <div class="card-header">
          <span>专辑管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            添加专辑
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
            v-model="searchQuery"
            placeholder="搜索专辑名称"
            :prefix-icon="Search"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
        />
        <el-select
            v-model="artistFilter"
            placeholder="筛选歌手"
            clearable
            filterable
            class="filter-select"
        >
          <el-option
              v-for="artist in artists"
              :key="artist.id"
              :label="artist.name"
              :value="artist.id"
          />
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

      <!-- 专辑列表 -->
      <el-table
          :data="albumList"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left"/>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="title" label="专辑名称" min-width="200">
          <template #default="scope">
            <div class="album-name">
              <el-image
                  v-if="scope.row.coverImage"
                  :src="`http://localhost:8080${scope.row.coverImage}`"
                  :fit="'cover'"
                  class="album-cover"
              />
              <span class="album-title">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="artistName" label="歌手" min-width="150"/>
        <el-table-column prop="releaseDate" label="发行日期" min-width="150"/>
        <el-table-column prop="musicCount" label="歌曲数量" min-width="120"/>
        <el-table-column prop="createdAt" label="添加时间" min-width="200"/>
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
                type="danger"
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

      <!-- 专辑表单对话框 -->
      <el-dialog
          v-model="dialogVisible"
          :title="formTitle"
          width="600px"
      >
        <el-form
            ref="albumFormRef"
            :model="albumForm"
            :rules="albumRules"
            label-position="top"
        >
          <el-form-item label="专辑名称" prop="title">
            <el-input v-model="albumForm.title" placeholder="请输入专辑名称"/>
          </el-form-item>
          <el-form-item label="歌手" prop="artistId">
            <el-select v-model="albumForm.artistId" placeholder="请选择歌手" filterable clearable style="width: 100%;">
              <el-option
                  v-for="artist in artists"
                  :key="artist.id"
                  :label="artist.name"
                  :value="artist.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="发行日期" prop="releaseDate">
            <el-date-picker
                v-model="albumForm.releaseDate"
                type="date"
                placeholder="选择发行日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="专辑简介" prop="description">
            <el-input
                v-model="albumForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入专辑简介"
            />
          </el-form-item>
          <el-form-item label="封面图片" prop="coverImage">
            <el-upload
                class="upload-demo"
                action=""
                :before-upload="beforeCoverUpload"
                :on-change="handleCoverChange"
                :on-remove="handleCoverRemove"
                :auto-upload="false"
                ref="coverUploadRef"
                :limit="1"
            >
              <template #trigger>
                <el-button type="primary">
                  <el-icon>
                    <Upload/>
                  </el-icon>
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
                v-if="albumForm.coverImage"
                :src="`http://localhost:8080${albumForm.coverImage}`"
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
import {ref, reactive, onMounted, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Plus, Edit, Delete, Refresh, Upload} from '@element-plus/icons-vue'
import request from "@/utils/request.js";

// 专辑列表数据
const albumList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const artistFilter = ref('')

// 下拉选择数据
const artists = ref([])

// 对话框
const dialogVisible = ref(false)
const albumFormRef = ref()
const albumForm = reactive({
  id: '',
  title: '',
  artistId: '',
  releaseDate: '',
  description: '',
  coverImage: ''
})

// 表单验证规则
const albumRules = {
  title: [
    {required: true, message: '请输入专辑名称', trigger: 'blur'},
    {min: 1, max: 100, message: '专辑名称长度在 1 到 100 个字符', trigger: 'blur'}
  ],
  artistId: [
    {required: true, message: '请选择歌手', trigger: 'change'}
  ],
  releaseDate: [
    {required: true, message: '请选择发行日期', trigger: 'change'}
  ]
}

// 上传组件引用
const coverUploadRef = ref()

// 待上传的封面文件
const pendingCoverFile = ref(null)

// 选中的专辑
const selectedAlbums = ref([])

// 表单标题
const formTitle = computed(() => {
  return albumForm.id ? '编辑专辑' : '添加专辑'
})

const getAlbumList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      artistId: artistFilter.value
    }
    const res = await request.get('album/getAll', {params})
    if (res.code === '200') {
      albumList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取专辑列表失败:', error)
    ElMessage.error('获取专辑列表失败')
  }
}

const getArtists = async () => {
  try {
    const res = await request.get('artist/getAll', {
      params: {
        pageNum: 1,
        pageSize: 1000,
      }
    })
    if (res.code === '200') {
      artists.value = res.data.list
    }
  } catch (error) {
    console.error('获取歌手列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getAlbumList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  artistFilter.value = ''
  currentPage.value = 1
  getAlbumList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getAlbumList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getAlbumList()
}

// 选择专辑
const handleSelectionChange = (selection) => {
  selectedAlbums.value = selection
}

// 添加专辑
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑专辑
const handleEdit = (row) => {
  // 确保日期格式为YYYY-MM-DD
  let releaseDate = row.releaseDate
  if (releaseDate) {
    // 如果日期包含时间部分，只保留日期部分
    releaseDate = releaseDate.split('T')[0] || releaseDate
  }

  Object.assign(albumForm, {
    id: row.id,
    title: row.title,
    artistId: row.artistId,
    description: row.description,
    releaseDate: releaseDate,
    coverImage: row.coverImage
  })
  dialogVisible.value = true
}

// 删除专辑
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该专辑吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete('album/delete/' + row.id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        await getAlbumList()
      }
    } catch (error) {
      console.error('删除专辑失败:', error)
      ElMessage.error('删除专辑失败')
    }
  }).catch(() => {
  })
}

// 提交表单（添加/编辑专辑）
const handleSubmit = async () => {
  if (!albumFormRef.value) return
  try {
    await albumFormRef.value.validate()

    let res
    let newAlbumId = null

    if (albumForm.id) {
      if (pendingCoverFile.value) {
        const formData = new FormData()
        formData.append('file', pendingCoverFile.value)

        const uploadRes = await request.post(`upload/albumCover/${albumForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (uploadRes.code === '200') {
          albumForm.coverImage = uploadRes.data
          ElMessage.success('封面上传成功')
        } else {
          ElMessage.error('封面上传失败')
          return
        }
      }

      const {coverImage, ...editData} = albumForm
      res = await request.put('album/update', editData)
    } else {
      const {coverImage, ...editData} = albumForm
      res = await request.post('album/add', editData)

      if (res.code === '200' && res.data && res.data.id) {
        newAlbumId = res.data.id
        albumForm.id = newAlbumId
      }
    }

    if (res.code === '200') {
      if (newAlbumId && pendingCoverFile.value) {
        try {
          const formData = new FormData()
          formData.append('file', pendingCoverFile.value)

          const uploadRes = await request.post(`upload/albumCover/${newAlbumId}`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })

          if (uploadRes.code === '200') {
            ElMessage.success('封面上传成功')
          } else {
            ElMessage.warning('专辑创建成功，但封面上传失败')
          }
        } catch (uploadError) {
          console.error('封面上传失败:', uploadError)
          ElMessage.warning('专辑创建成功，但封面上传失败')
        }
      }

      ElMessage.success(albumForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      pendingCoverFile.value = null
      await getAlbumList()
    }
  } catch (error) {
    console.error(albumForm.id ? '编辑专辑失败:' : '添加专辑失败:', error)
    ElMessage.error(albumForm.id ? '编辑专辑失败' : '添加专辑失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(albumForm, {
    id: '',
    title: '',
    artistId: '',
    releaseDate: '',
    description: '',
    coverImage: ''
  })
  if (albumFormRef.value) {
    albumFormRef.value.resetFields()
  }
  coverUploadRef.value?.clearFiles()
  pendingCoverFile.value = null
}

// 封面上传前处理
const beforeCoverUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isJPG && !isPNG) {
    ElMessage.error('只能上传JPG或PNG格式的图片')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

// 处理封面文件变化
const handleCoverChange = (file, fileList) => {
  if (fileList.length > 0) {
    pendingCoverFile.value = fileList[0].raw
  } else {
    pendingCoverFile.value = null
  }
}

// 处理封面文件移除
const handleCoverRemove = () => {
  pendingCoverFile.value = null
}

const handleCoverUpload = (response) => {
  if (response.code === '200') {
    albumForm.coverImage = response.data.url
    ElMessage.success('封面上传成功')
  }
}

onMounted(() => {
  getAlbumList()
  getArtists()
})
</script>

<style scoped lang="scss">
.album-management-container {
  padding: 0;
}

.album-management-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.album-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.album-cover {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  flex-shrink: 0;
}

.album-title {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  max-height: 2.8em;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>