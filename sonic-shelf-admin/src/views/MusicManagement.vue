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
         artistId: String,     // 歌手ID（筛选条件）
         categoryIds: String   // 分类ID列表，逗号分隔（筛选条件）- 需要后端支持
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
             categories: Array<{
               id: String,    // 分类ID
               name: String   // 分类名称
             }>,              // 音乐所属分类列表 - 需要后端支持
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
         coverImage: String,   // 封面图片URL
         categoryIds: Array<String> // 分类ID列表 - 需要后端支持
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
         coverImage: String,   // 封面图片URL
         categoryIds: Array<String> // 分类ID列表 - 需要后端支持
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
                  :src="scope.row.coverImage.startsWith('data:') ? scope.row.coverImage : `http://localhost:8080${scope.row.coverImage}`"
                  :fit="'cover'"
                  class="music-cover"
              />
              <span class="music-title">{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="artistName" label="歌手" min-width="150"/>
        <el-table-column prop="albumTitle" label="专辑" min-width="150"/>
        <el-table-column prop="categories" label="分类" min-width="150">
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
          <!-- 分类选择 - 多级标签选择 -->
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
                  <div v-if="formTags.length === 0" class="empty-tags">
                    <el-icon>
                      <InfoFilled/>
                    </el-icon>
                    <span>暂无标签</span>
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="音乐文件" prop="fileUrl">
            <el-upload
                class="upload-demo"
                action=""
                :before-upload="beforeFileUpload"
                :auto-upload="false"
                ref="fileUploadRef"
                :on-change="handleMusicFileChange"
                :on-remove="handleMusicFileRemove"
                accept=".mp3,.wav,.flac"
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
                  支持上传MP3、WAV、FLAC等格式的音乐文件，文件大小不超过50MB<br>
                  <span style="color: #67C23A;">* 系统会自动从音乐文件中提取标题、歌手、专辑和封面信息</span>
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
                :on-remove="handleCoverFileRemove"
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
                :src="musicForm.coverImage.startsWith('data:') ? musicForm.coverImage : `http://localhost:8080${musicForm.coverImage}`"
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
import {ref, reactive, onMounted, watch, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Plus, Edit, Delete, Close, ArrowDown, ArrowUp, InfoFilled} from '@element-plus/icons-vue'
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
const categoryFilter = ref([])

// 分类筛选相关数据
const showCategoryFilter = ref(false)
const filterUserSelected = ref(1) // 默认选中第一个分类
const filterTags = ref([]) // 当前分类下的标签
const selectedFilterTags = ref([]) // 选中的筛选标签对象数组
const selectedFilterTagIds = ref([]) // 选中的筛选标签ID数组

// 下拉选择数据
const albums = ref([])
const artists = ref([])
const categories = ref([])

// 表单分类选择相关数据
const showCategoryForm = ref(false)
const formUserSelected = ref(1) // 默认选中第一个分类
const formTags = ref([]) // 当前分类下的标签
const selectedFormTags = ref([]) // 选中的表单标签对象数组
const selectedFormTagIds = ref([]) // 选中的表单标签ID数组

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
  coverImage: '',
  categoryIds: []
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
  ],
  categoryIds: [
    {
      type: 'array', validator: (_, value, callback) => {
        if (!value || value.length === 0) {
          callback(new Error('请至少选择一个分类'))
        } else {
          callback()
        }
      }, trigger: 'change'
    }
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
      artistId: artistFilter.value,
      categoryIds: selectedFilterTagIds.value.join(',')
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

// 获取分类列表
const getCategories = async () => {
  try {
    const res = await request.get('categories')
    if (res.code === '200') {
      categories.value = res.data
      // 获取默认分类的子分类
      if (categories.value.length > 0) {
        await getSubCategories(filterUserSelected.value, 'filter')
        await getSubCategories(formUserSelected.value, 'form')
      }
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
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

// 获取子分类
const getSubCategories = async (parentId, type) => {
  try {
    const res = await request.get('categories', {params: {parentId}})
    if (res.code === '200') {
      if (type === 'filter') {
        filterTags.value = res.data
      } else if (type === 'form') {
        formTags.value = res.data
      }
    }
  } catch (error) {
    console.error('获取子分类失败:', error)
  }
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
  musicForm.categoryIds = [...selectedFormTagIds.value]
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
  }
  // 更新表单数据
  musicForm.categoryIds = [...selectedFormTagIds.value]
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
  categoryFilter.value = []
  selectedFilterTags.value = []
  selectedFilterTagIds.value = []
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
  // 重置分类选择
  selectedFormTagIds.value = []
  selectedFormTags.value = []
  dialogVisible.value = true
}

// 编辑音乐
const handleEdit = (row) => {
  Object.assign(musicForm, row)

  // 初始化分类选择
  selectedFormTagIds.value = [...(row.categoryIds || [])]
  selectedFormTags.value = []

  // 根据分类ID获取分类信息
  if (row.categoryIds && row.categoryIds.length > 0) {
    row.categoryIds.forEach(id => {
      const category = categories.value.find(cat => cat.id === id)
      if (category) {
        selectedFormTags.value.push(category)
      }
    })
  }

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
      const res = await request.delete('musics/delete/'+row.id)
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

        const uploadRes = await request.post(`upload/musicFile/${musicForm.id}`, formData, {
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
      res = await request.put(`musics/update`, editData)
    } else {
      // 添加模式 - 先创建音乐
      const {fileUrl, coverImage, ...addData} = musicForm
      res = await request.post('musics/add', addData)

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

            const uploadRes = await request.post(`upload/musicFile/${newMusicId}`, formData, {
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
    coverImage: '',
    categoryIds: []
  })
  // 清空待上传文件
  pendingMusicFile.value = null
  pendingCoverFile.value = null
  // 重置分类选择
  selectedFormTagIds.value = []
  selectedFormTags.value = []
  if (musicFormRef.value) {
    musicFormRef.value.resetFields()
  }
}

// 文件上传前处理
const beforeFileUpload = (file) => {
  const isMP3 = file.type === 'audio/mpeg'
  const isWAV = file.type === 'audio/wav'
  const isFLAC = file.type === 'audio/flac'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isMP3 && !isWAV && !isFLAC) {
    ElMessage.error('只能上传MP3、WAV或FLAC格式的音乐文件')
  }
  if (!isLt50M) {
    ElMessage.error('音乐文件大小不能超过50MB')
  }
  return (isMP3 || isWAV || isFLAC) && isLt50M
}

// 封面上传前处理
const beforeCoverUpload = (file) => {
  // 如果是从音乐文件中提取的封面，允许通过
  if (file.name && file.name.startsWith('cover_')) {
    return true;
  }

  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isJPG && !isPNG) {
    ElMessage.error('只能上传JPG或PNG格式的图片');
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB');
  }
  return (isJPG || isPNG) && isLt5M;
}

// 音乐文件变化处理
const handleMusicFileChange = (uploadFile) => {
  const file = uploadFile.raw
  pendingMusicFile.value = file

  // 动态加载jsmediatags库
  if (typeof jsmediatags === 'undefined') {
    const script = document.createElement('script');
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/jsmediatags/3.9.5/jsmediatags.min.js';
    script.onload = () => {
      extractMusicMetadata(file);
    };
    document.body.appendChild(script);
  } else {
    extractMusicMetadata(file);
  }
}

// 提取音乐元数据的函数
const extractMusicMetadata = (file) => {
  // 提取音频文件的元数据和封面
  if (file && (file.type === 'audio/mpeg' || file.type === 'audio/wav' || file.type === 'audio/flac')) {
    jsmediatags.read(file, {
      onSuccess: function (tag) {
        // 提取标题
        if (tag.tags.title && !musicForm.title) {
          musicForm.title = tag.tags.title;
        }

        // 尝试从文件名获取标题（如果标签中没有）
        if (!musicForm.title) {
          const fileName = file.name;
          musicForm.title = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        // 提取歌手信息（尝试匹配现有歌手）
        if (tag.tags.artist && artists.value.length > 0) {
          const artistName = tag.tags.artist;
          const matchedArtist = artists.value.find(artist =>
              artist.name.toLowerCase() === artistName.toLowerCase()
          );
          if (matchedArtist && !musicForm.artistId) {
            musicForm.artistId = matchedArtist.id;
          }
        }

        // 提取专辑信息（尝试匹配现有专辑）
        if (tag.tags.album && albums.value.length > 0) {
          const albumName = tag.tags.album;
          const matchedAlbum = albums.value.find(album =>
              album.title.toLowerCase() === albumName.toLowerCase()
          );
          if (matchedAlbum && !musicForm.albumId) {
            musicForm.albumId = matchedAlbum.id;
          }
        }

        // 提取封面图片
        if (tag.tags.picture) {
          const {data, format} = tag.tags.picture;
          let base64String = '';
          for (let i = 0; i < data.length; i++) {
            base64String += String.fromCharCode(data[i]);
          }
          const coverBase64 = `data:${format};base64,${window.btoa(base64String)}`;

          // 将封面图片转换为Blob对象并设置为待上传文件
          fetch(coverBase64)
              .then(res => res.blob())
              .then(blob => {
                // 创建一个临时文件名
                const coverFileName = `cover_${Date.now()}.${format.split('/')[1]}`;
                const coverFile = new File([blob], coverFileName, {type: format});
                pendingCoverFile.value = coverFile;

                // 更新表单中的封面图片预览
                // 注意：这里只是预览，实际上传需要等待表单提交时处理
                musicForm.coverImage = coverBase64;
              });
        }

        ElMessage.success('已从音乐文件中提取元数据');
      },
      onError: function (error) {
        console.log('提取音乐元数据失败:', error);
        // 提取失败不影响继续上传
      }
    });

    // 使用Audio API获取音频时长
    const audio = new Audio();
    const fileURL = URL.createObjectURL(file);

    audio.onloadedmetadata = function () {
      // 获取时长（秒）并取整
      const duration = Math.floor(audio.duration);
      if (duration > 0 && !musicForm.duration) {
        musicForm.duration = duration;
      }
      // 释放URL对象
      URL.revokeObjectURL(fileURL);
    };

    audio.onerror = function () {
      console.log('获取音频时长失败');
      // 释放URL对象
      URL.revokeObjectURL(fileURL);
    };

    audio.src = fileURL;
  }
};

// 封面文件变化处理
const handleCoverFileChange = (uploadFile) => {
  pendingCoverFile.value = uploadFile.raw
}

// 音乐文件移除处理
const handleMusicFileRemove = () => {
  pendingMusicFile.value = null
}

// 封面文件移除处理
const handleCoverFileRemove = () => {
  pendingCoverFile.value = null
}

onMounted(() => {
  getMusicList();
  getAlbums();
  getArtists();
  getCategories();
});
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
  flex-shrink: 0;
}

.music-title {
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

/* 分类选择器样式 */
.category-filter-container,
.category-form-container {
  position: relative;
  min-width: 280px;
  max-width: 320px;
}

.category-selector,
.category-form-selector {
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

.selected-filter-tag,
.selected-form-tag {
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

.categories-filter-form,
.categories-form-panel {
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

.categories-filter-title,
.categories-form-title {
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

.filter-grid-list,
.form-grid-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
  gap: 6px;
  max-height: 280px;
  overflow-y: auto;
  padding: 6px;
}

.filter-tag,
.form-tag {
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

  &.filter-tag-selected,
  &.form-tag-selected {
    background: linear-gradient(135deg, #409eff, #66b1ff);
    color: #fff;
    border-color: #409eff;
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.25);
    font-weight: 500;

    &::after {
      content: '✓';
      position: absolute;
      top: 1px;
      right: 2px;
      font-size: 9px;
      font-weight: bold;
    }
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

/* 空状态 */
.empty-tags {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  color: #909399;
  font-size: 13px;
  gap: 6px;

  .el-icon {
    font-size: 16px;
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
    opacity: 0.8;

    &:hover {
      opacity: 1;
      transform: scale(1.1);
    }
  }
}
</style>