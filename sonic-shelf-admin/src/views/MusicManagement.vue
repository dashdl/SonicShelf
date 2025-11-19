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
  
  后端API详细文档：
  
  1. 获取音乐列表
     - URL: GET /admin/musics
     - 请求参数（查询参数）：
       { 
         pageNum: Number,      // 当前页码，从1开始
         pageSize: Number,     // 每页条数
         keyword: String,      // 搜索关键词（音乐名称或歌手名称）
         albumId: String,      // 专辑ID（筛选条件）
         artistId: String      // 歌手ID（筛选条件）
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: { 
           list: Array<{       // 音乐列表
             id: String,       // 音乐ID
             title: String,       // 音乐标题
             artistId: String, // 歌手ID
             artistName: String, // 歌手名称
             albumId: String,  // 专辑ID
             albumTitle: String, // 专辑标题
             duration: Number, // 时长（秒）
             fileUrl: String,  // 音乐文件URL
             coverImage: String, // 封面图片URL
             playCount: Number, // 播放次数
             createdAt: String, // 创建时间（YYYY-MM-DD HH:mm:ss）
             updateAt: String  // 更新时间（YYYY-MM-DD HH:mm:ss）
           }>,
           total: Number       // 总记录数
         }
       }
  
  2. 获取专辑列表（下拉选择）
     - URL: GET /admin/albums
     - 请求参数（查询参数）：
       { 
         pageSize: Number      // 每页条数（设置较大值获取所有专辑）
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: { 
           list: Array<{       // 专辑列表
             id: String,       // 专辑ID
             name: String      // 专辑名称
           }>,
           total: Number       // 总记录数
         }
       }
  
  3. 获取歌手列表（下拉选择）
     - URL: GET /admin/singers
     - 请求参数（查询参数）：
       { 
         pageSize: Number      // 每页条数（设置较大值获取所有歌手）
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: { 
           list: Array<{       // 歌手列表
             id: String,       // 歌手ID
             name: String      // 歌手名称
           }>,
           total: Number       // 总记录数
         }
       }
  
  4. 添加音乐
     - URL: POST /admin/musics
     - 请求体：
       { 
         title: String,       // 音乐标题
         artistId: String,     // 歌手ID
         albumId: String,      // 专辑ID
         duration: Number,     // 时长（秒）
         fileUrl: String,      // 音乐文件URL
         coverImage: String    // 封面图片URL
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: {               // 新增的音乐信息
           id: String,         // 音乐ID
           title: String,       // 音乐标题
           artistId: String, // 歌手ID
           artistName: String, // 歌手名称
             albumId: String,  // 专辑ID
             albumTitle: String, // 专辑标题
             duration: Number, // 时长（秒）
             fileUrl: String,  // 音乐文件URL
             coverImage: String, // 封面图片URL
             playCount: Number, // 播放次数
             createdAt: String, // 创建时间
             updateAt: String  // 更新时间
         }
       }
  
  5. 更新音乐
     - URL: PUT /admin/musics/{id}
     - 请求体：
       { 
         id: String,           // 音乐ID
         title: String,       // 音乐标题
         artistId: String,     // 歌手ID
         albumId: String,      // 专辑ID
         duration: Number,     // 时长（秒）
         fileUrl: String,      // 音乐文件URL
         coverImage: String    // 封面图片URL
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: {               // 更新后的音乐信息
           id: String,         // 音乐ID
           title: String,       // 音乐标题
           artistId: String, // 歌手ID
           artistName: String, // 歌手名称
             albumId: String,  // 专辑ID
             albumTitle: String, // 专辑标题
             duration: Number, // 时长（秒）
             fileUrl: String,  // 音乐文件URL
             coverImage: String, // 封面图片URL
             playCount: Number, // 播放次数
             createdAt: String, // 创建时间
             updateAt: String  // 更新时间
         }
       }
  
  6. 删除音乐
     - URL: DELETE /admin/musics/{id}
     - 请求参数（路径参数）：
       id: String             // 音乐ID
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: null            // 无数据返回
       }
  
  7. 上传音乐文件（预留接口）
     - URL: POST /admin/upload/music
     - 请求体：FormData
       { 
         file: File            // 音乐文件
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: { 
           url: String         // 上传后的文件URL
         }
       }
  
  8. 上传封面图片（预留接口）
     - URL: POST /admin/upload/cover
     - 请求体：FormData
       { 
         file: File            // 图片文件
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: { 
           url: String         // 上传后的图片URL
         }
       }
-->

<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="music-management-card">
      <template #header>
        <div class="card-header">
          <span>音乐管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
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
              :label="album.title"
              :value="album.id"
          />
        </el-select>
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

      <!-- 音乐列表 -->
      <el-table
          :data="musicList"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left"/>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="name" label="音乐名称" min-width="200">
          <template #default="scope">
            <div class="music-name">
              <el-image
                  v-if="scope.row.coverImage"
                  :src="`http://localhost:8080${scope.row.coverImage}`"
                  :fit="'cover'"
                  class="music-cover"
              />
              <span>{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="artistName" label="歌手" min-width="150"/>
        <el-table-column prop="albumTitle" label="专辑" min-width="150"/>
        <el-table-column prop="duration" label="时长" min-width="100">
          <template #default="scope">
            <span>{{ formatDuration(scope.row.duration) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="playCount" label="播放次数" min-width="120"/>
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
          <el-form-item label="音乐标题" prop="title">
            <el-input v-model="musicForm.title" placeholder="请输入音乐标题"/>
          </el-form-item>
          <el-form-item label="歌手" prop="artistId">
            <el-select v-model="musicForm.artistId" placeholder="请选择歌手" filterable clearable style="width: 100%">
              <el-option
                  v-for="artist in artists"
                  :key="artist.id"
                  :label="artist.name"
                  :value="artist.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="专辑" prop="albumId">
            <el-select v-model="musicForm.albumId" placeholder="请选择专辑" filterable clearable style="width: 100%">
              <el-option
                  v-for="album in albums"
                  :key="album.id"
                  :label="album.title"
                  :value="album.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="时长(秒)" prop="duration">
            <el-input-number v-model="musicForm.duration" :min="0" placeholder="请输入时长"/>
          </el-form-item>
          <el-form-item label="音乐文件" prop="fileUrl">
            <el-upload
                class="upload-demo"
                action=""
                :before-upload="beforeFileUpload"
                :auto-upload="false"
                ref="fileUploadRef"
                :on-change="handleMusicFileChange"
            >
              <template #trigger>
                <el-button type="primary">
                  <el-icon>
                    <Upload/>
                  </el-icon>
                  {{ musicForm.id ? '更换音乐文件' : '上传音乐文件' }}
                </el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传MP3、WAV等格式的音乐文件，文件大小不超过50MB
                </div>
              </template>
            </el-upload>
            <div v-if="musicForm.fileUrl" style="margin-top: 10px;">
              <el-tag type="info">当前文件: {{ getFileNameFromUrl(musicForm.fileUrl) }}</el-tag>
            </div>
          </el-form-item>
          <el-form-item label="封面图片" prop="coverImage">
            <el-upload
                class="upload-demo"
                action=""
                :before-upload="beforeCoverUpload"
                :auto-upload="false"
                ref="coverUploadRef"
                :on-change="handleCoverFileChange"
            >
              <template #trigger>
                <el-button type="primary">
                  <el-icon>
                    <Upload/>
                  </el-icon>
                  {{ musicForm.coverImage ? '更换封面图片' : '上传封面图片' }}
                </el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传JPG、PNG格式的图片，建议尺寸300x300，文件大小不超过5MB
                </div>
              </template>
            </el-upload>
            <el-image
                v-if="musicForm.coverImage"
                :src="`http://localhost:8080${musicForm.coverImage}`"
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
import mockService from '@/mock/mockService'
import {ElMessage, ElMessageBox} from 'element-plus'
import request from "@/utils/request.js";

// 音乐列表数据
const musicList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const albumFilter = ref('')
const artistFilter = ref('')

// 下拉选择数据
const albums = ref([])
const artists = ref([])

// 对话框
const dialogVisible = ref(false)
const musicFormRef = ref()
// 音乐列表数据
const musicForm = reactive({
  id: '',
  title: '',
  artistId: '',
  albumId: '',
  duration: 0,
  fileUrl: '',
  coverImage: ''
})

// 表单验证规则
const musicRules = {
  title: [
    {required: true, message: '请输入音乐标题', trigger: 'blur'},
    {min: 1, max: 100, message: '音乐标题长度在 1 到 100 个字符', trigger: 'blur'}
  ],
  artistId: [
    {required: true, message: '请选择歌手', trigger: 'change'}
  ],
  albumId: [
    {required: true, message: '请选择专辑', trigger: 'change'}
  ],
  duration: [
    {required: true, message: '请输入时长', trigger: 'blur'},
    {type: 'number', min: 1, message: '时长必须大于0', trigger: 'blur'}
  ]
}

// 上传组件引用
const fileUploadRef = ref()
const coverUploadRef = ref()

// 待上传的文件
const pendingMusicFile = ref(null)
const pendingCoverFile = ref(null)

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

// 从URL获取文件名
const getFileNameFromUrl = (url) => {
  if (!url) return ''
  const parts = url.split('/')
  return parts[parts.length - 1] || '未知文件'
}

// 获取音乐列表
const getMusicList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      albumId: albumFilter.value,
      artistId: artistFilter.value
    }
    const res = await request.get('musics/getAll', {params})
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
    const res = await request.get('album/getAll', {params: {pageNum: 1, pageSize: 1000}})
    if (res.code === '200') {
      albums.value = res.data.list
    }
  } catch (error) {
    console.error('获取专辑列表失败:', error)
  }
}

// 获取歌手列表
const getArtists = async () => {
  try {
    const res = await request.get('artist/getAll', {params: {pageNum: 1, pageSize: 1000}})
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
  getMusicList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  albumFilter.value = ''
  artistFilter.value = ''
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
    let newMusicId = null

    if (musicForm.id) {
      // 编辑模式
      // 如果有待上传的音乐文件，先上传音乐文件
      if (pendingMusicFile.value) {
        const formData = new FormData()
        formData.append('file', pendingMusicFile.value)

        const uploadRes = await request.post(`upload/music/${musicForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (uploadRes.code === '200') {
          // 上传成功后更新音乐表单中的文件路径
          musicForm.fileUrl = uploadRes.data
          ElMessage.success('音乐文件上传成功')
        } else {
          ElMessage.error('音乐文件上传失败')
          return // 音乐文件上传失败，不继续更新音乐信息
        }
      }

      // 如果有待上传的封面文件，先上传封面
      if (pendingCoverFile.value) {
        const formData = new FormData()
        formData.append('file', pendingCoverFile.value)

        const uploadRes = await request.post(`upload/musicCover/${musicForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (uploadRes.code === '200') {
          // 上传成功后更新音乐表单中的封面路径
          musicForm.coverImage = uploadRes.data
          ElMessage.success('封面图片上传成功')
        } else {
          ElMessage.error('封面图片上传失败')
          return // 封面图片上传失败，不继续更新音乐信息
        }
      }

      const {fileUrl, coverImage, ...editData} = musicForm
      res = await request.put('musics/update', editData)
    } else {
      // 添加模式 - 先创建音乐
      const {fileUrl, coverImage, ...editData} = musicForm
      res = await request.post('musics/add', editData)
      
      if (res.code === '200' && res.data && res.data.id) {
        newMusicId = res.data.id
        musicForm.id = newMusicId
      }
    }

    if (res.code === '200') {
      // 如果是添加模式且有文件需要上传
      if (newMusicId) {
        // 上传音乐文件
        if (pendingMusicFile.value) {
          try {
            const formData = new FormData()
            formData.append('file', pendingMusicFile.value)

            const uploadRes = await request.post(`upload/music/${newMusicId}`, formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            })

            if (uploadRes.code === '200') {
              ElMessage.success('音乐文件上传成功')
            } else {
              ElMessage.warning('音乐创建成功，但音乐文件上传失败')
            }
          } catch (uploadError) {
            console.error('音乐文件上传失败:', uploadError)
            ElMessage.warning('音乐创建成功，但音乐文件上传失败')
          }
        }

        // 上传封面图片
        if (pendingCoverFile.value) {
          try {
            const formData = new FormData()
            formData.append('file', pendingCoverFile.value)

            const uploadRes = await request.post(`upload/musicCover/${newMusicId}`, formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            })

            if (uploadRes.code === '200') {
              ElMessage.success('封面图片上传成功')
            } else {
              ElMessage.warning('音乐创建成功，但封面图片上传失败')
            }
          } catch (uploadError) {
            console.error('封面图片上传失败:', uploadError)
            ElMessage.warning('音乐创建成功，但封面图片上传失败')
          }
        }
      }

      ElMessage.success(musicForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      // 清空待上传文件
      pendingMusicFile.value = null
      pendingCoverFile.value = null
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
    title: '',
    artistId: '',
    albumId: '',
    duration: 0,
    fileUrl: '',
    coverImage: ''
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

// 音乐文件变化处理
const handleMusicFileChange = (uploadFile) => {
  pendingMusicFile.value = uploadFile.raw
}

// 封面文件变化处理
const handleCoverFileChange = (uploadFile) => {
  pendingCoverFile.value = uploadFile.raw
}

onMounted(() => {
  getMusicList()
  getAlbums()
  getArtists()
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