<!--
  音乐管理页面
  
  数据流说明：
  1. 页面加载时调用 getMusicList() 获取音乐列表
  2. 搜索功能通过 handleSearch() 触发，支持按音乐名称、歌手、专辑筛选
  3. 分页功能通过 handleCurrentChange() 和 handleSizeChange() 实现
  4. 表单提交通过 handleSubmit() 处理，支持添加和编辑音乐
  5. 删除功能通过 handleDelete() 实现，带确认对话框
  
  API替换说明：
  1. 当前使用 mockService.music 模拟数据
  2. 替换为真实API时，请导入 src/api/music.js 中的方法
  3. 保持相同的返回格式：{ code: '200', data: {...} }
  
  后端API要求：
  - GET /admin/musics - 获取音乐列表（支持分页、搜索）
  - POST /admin/musics - 添加音乐
  - PUT /admin/musics/{id} - 更新音乐
  - DELETE /admin/musics/{id} - 删除音乐
-->

<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="music-management-card">
      <template #header>
        <div class="card-header">
          <span>音乐管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加音乐
          </el-button>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索音乐名称或歌手"
          :prefix-icon="Search"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        />
        <el-select
          v-model="albumFilter"
          placeholder="筛选专辑"
          clearable
          filterable
          class="filter-select"
        >
          <el-option
            v-for="album in albums"
            :key="album.id"
            :label="album.name"
            :value="album.id"
          />
        </el-select>
        <el-select
          v-model="singerFilter"
          placeholder="筛选歌手"
          clearable
          filterable
          class="filter-select"
        >
          <el-option
            v-for="singer in singers"
            :key="singer.id"
            :label="singer.name"
            :value="singer.id"
          />
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

      <!-- 音乐列表 -->
      <el-table
        :data="musicList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="音乐名称" min-width="200">
          <template #default="scope">
            <div class="music-name">
              <el-image
                v-if="scope.row.coverUrl"
                :src="scope.row.coverUrl"
                :fit="'cover'"
                class="music-cover"
              />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="singerName" label="歌手" min-width="150" />
        <el-table-column prop="albumName" label="专辑" min-width="150" />
        <el-table-column prop="duration" label="时长" min-width="100">
          <template #default="scope">
            <span>{{ formatDuration(scope.row.duration) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="playCount" label="播放次数" min-width="120" />
        <el-table-column prop="createTime" label="添加时间" min-width="200" />
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

      <!-- 音乐表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="formTitle"
        width="600px"
      >
        <el-form
          ref="musicFormRef"
          :model="musicForm"
          :rules="musicRules"
          label-position="top"
        >
          <el-form-item label="音乐名称" prop="name">
            <el-input v-model="musicForm.name" placeholder="请输入音乐名称" />
          </el-form-item>
          <el-form-item label="歌手" prop="singerId">
            <el-select v-model="musicForm.singerId" placeholder="请选择歌手" filterable clearable style="width: 100%">
              <el-option
                v-for="singer in singers"
                :key="singer.id"
                :label="singer.name"
                :value="singer.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="专辑" prop="albumId">
            <el-select v-model="musicForm.albumId" placeholder="请选择专辑" filterable clearable style="width: 100%">
              <el-option
                v-for="album in albums"
                :key="album.id"
                :label="album.name"
                :value="album.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="时长(秒)" prop="duration">
            <el-input-number v-model="musicForm.duration" :min="0" placeholder="请输入时长" />
          </el-form-item>
          <el-form-item label="音乐文件" prop="fileUrl" v-if="!musicForm.id">
            <el-upload
              class="upload-demo"
              action=""
              :on-success="handleFileUpload"
              :before-upload="beforeFileUpload"
              :auto-upload="false"
              ref="fileUploadRef"
            >
              <template #trigger>
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  上传音乐文件
                </el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传MP3、WAV等格式的音乐文件
                </div>
              </template>
            </el-upload>
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
              v-if="musicForm.coverUrl"
              :src="musicForm.coverUrl"
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

// 音乐列表数据
const musicList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const albumFilter = ref('')
const singerFilter = ref('')

// 下拉选择数据
const albums = ref([])
const singers = ref([])

// 对话框
const dialogVisible = ref(false)
const musicFormRef = ref()
const musicForm = reactive({
  id: '',
  name: '',
  singerId: '',
  albumId: '',
  duration: 0,
  fileUrl: '',
  coverUrl: ''
})

// 表单验证规则
const musicRules = {
  name: [
    { required: true, message: '请输入音乐名称', trigger: 'blur' },
    { min: 1, max: 100, message: '音乐名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  singerId: [
    { required: true, message: '请选择歌手', trigger: 'change' }
  ],
  albumId: [
    { required: true, message: '请选择专辑', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入时长', trigger: 'blur' },
    { type: 'number', min: 1, message: '时长必须大于0', trigger: 'blur' }
  ]
}

// 上传组件引用
const fileUploadRef = ref()
const coverUploadRef = ref()

// 选中的音乐
const selectedMusic = ref([])

// 表单标题
const formTitle = computed(() => {
  return musicForm.id ? '编辑音乐' : '添加音乐'
})

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 获取音乐列表
const getMusicList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      albumId: albumFilter.value,
      singerId: singerFilter.value
    }
    const res = await mockService.music.getList(params)
    if (res.code === '200') {
      musicList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取音乐列表失败:', error)
    ElMessage.error('获取音乐列表失败')
  }
}

// 获取专辑列表
const getAlbums = async () => {
  try {
    const res = await mockService.album.getList({ pageNum: 1, pageSize: 1000 })
    if (res.code === '200') {
      albums.value = res.data.list
    }
  } catch (error) {
    console.error('获取专辑列表失败:', error)
  }
}

// 获取歌手列表
const getSingers = async () => {
  try {
    const res = await mockService.singer.getList({ pageNum: 1, pageSize: 1000 })
    if (res.code === '200') {
      singers.value = res.data.list
    }
  } catch (error) {
    console.error('获取歌手列表失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getMusicList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  albumFilter.value = ''
  singerFilter.value = ''
  currentPage.value = 1
  getMusicList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getMusicList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getMusicList()
}

// 选择音乐
const handleSelectionChange = (selection) => {
  selectedMusic.value = selection
}

// 添加音乐
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑音乐
const handleEdit = (row) => {
  Object.assign(musicForm, row)
  dialogVisible.value = true
}

// 删除音乐
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该音乐吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await mockService.music.delete(row.id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getMusicList()
      }
    } catch (error) {
      console.error('删除音乐失败:', error)
      ElMessage.error('删除音乐失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!musicFormRef.value) return
  try {
    await musicFormRef.value.validate()
    let res
    if (musicForm.id) {
      // 编辑音乐
      res = await mockService.music.update(musicForm)
    } else {
      // 添加音乐
      res = await mockService.music.add(musicForm)
    }
    if (res.code === '200') {
      ElMessage.success(musicForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      getMusicList()
    }
  } catch (error) {
    console.error(musicForm.id ? '编辑音乐失败:' : '添加音乐失败:', error)
    ElMessage.error(musicForm.id ? '编辑音乐失败' : '添加音乐失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(musicForm, {
    id: '',
    name: '',
    singerId: '',
    albumId: '',
    duration: 0,
    fileUrl: '',
    coverUrl: ''
  })
  if (musicFormRef.value) {
    musicFormRef.value.resetFields()
  }
}

// 文件上传前处理
const beforeFileUpload = (file) => {
  const isMP3 = file.type === 'audio/mpeg'
  const isWAV = file.type === 'audio/wav'
  const isLt50M = file.size / 1024 / 1024 < 50
  
  if (!isMP3 && !isWAV) {
    ElMessage.error('只能上传MP3或WAV格式的音乐文件')
  }
  if (!isLt50M) {
    ElMessage.error('音乐文件大小不能超过50MB')
  }
  return isMP3 || isWAV && isLt50M
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

// 文件上传成功处理
const handleFileUpload = (response) => {
  if (response.code === '200') {
    musicForm.fileUrl = response.data.url
    ElMessage.success('文件上传成功')
  }
}

// 封面上传成功处理
const handleCoverUpload = (response) => {
  if (response.code === '200') {
    musicForm.coverUrl = response.data.url
    ElMessage.success('封面上传成功')
  }
}

onMounted(() => {
  getMusicList()
  getAlbums()
  getSingers()
})
</script>

<style scoped lang="scss">
.music-management-container {
  padding: 0;
}

.music-management-card {
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

.music-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.music-cover {
  width: 40px;
  height: 40px;
  border-radius: 4px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>