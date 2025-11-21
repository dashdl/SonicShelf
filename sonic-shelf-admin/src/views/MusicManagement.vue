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
      
        <CategorySelector
          v-model:selectedTagIds="selectedFilterTagIds"
          placeholder="选择分类标签"
          @update:selectedTags="handleFilterSelectedTagsChange"
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
        <el-table-column prop="albumTitle" label="专辑" min-width="150">
          <template #default="scope">
            <span class="album-title">{{ scope.row.albumTitle }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="categories" label="分类" min-width="130">
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
        <el-table-column prop="lyrics" label="歌词" min-width="200">
          <template #default="scope">
            <div class="lyrics-preview">
              <span class="lyrics-text">{{ scope.row.lyrics ? scope.row.lyrics : '-' }}</span>
              <el-button 
                type="text" 
                size="small" 
                @click="handleViewLyrics(scope.row)"
                v-if="scope.row.lyrics">
                查看完整歌词
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" min-width="100">
          <template #default="scope">
            <span>{{ formatDuration(scope.row.duration) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="playCount" label="播放次数" min-width="120"/>
        <el-table-column prop="createdAt" label="添加时间" min-width="180"/>
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
      <MusicForm
          v-model:visible="dialogVisible"
          :form-data="musicForm"
          :albums="albums"
          :artists="artists"
          @success="handleFormSuccess"
          @cancel="handleFormCancel"
      />

      <!-- 歌词查看对话框 -->
      <el-dialog
          v-model="lyricsDialogVisible"
          :title="`${currentMusicTitle} - 完整歌词`"
          width="800px"
      >
        <div class="lyrics-full">
          <pre>{{ currentLyrics }}</pre>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="lyricsDialogVisible = false">关闭</el-button>
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
import CategorySelector from "@/components/common/CategorySelector.vue";
import MusicForm from "@/components/music/MusicForm.vue";

// 音乐列表数据
const musicList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const albumFilter = ref('')
const artistFilter = ref('')

// 分类筛选相关数据
const selectedFilterTagIds = ref([]) // 选中的筛选标签ID数组，用于传递给后端

// 下拉选择数据
const albums = ref([])
const artists = ref([])

// 表单分类选择相关数据
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
  categoryIds: [],
  lyrics: ''
})



// 选中的音乐
const selectedMusic = ref([])

// 表单成功提交事件
const handleFormSuccess = () => {
  dialogVisible.value = false
  ElMessage.success(musicForm.id ? '编辑成功' : '添加成功')
  getMusicList()
}

// 表单取消事件
const handleFormCancel = () => {
  dialogVisible.value = false
}

// 歌词查看对话框
const lyricsDialogVisible = ref(false)
const currentLyrics = ref('')
const currentMusicTitle = ref('')

// 查看完整歌词
const handleViewLyrics = (row) => {
  currentMusicTitle.value = row.title
  currentLyrics.value = row.lyrics
  lyricsDialogVisible.value = true
}

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
      categoryIds: selectedFilterTagIds.value.join(',') // 分类筛选条件，逗号分隔的标签ID列表
      // 说明：categoryIds参数需要后端API支持，用于根据选中的标签筛选音乐列表
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
  selectedFilterTagIds.value = []
  currentPage.value = 1
  getMusicList()
}

// 处理筛选分类标签变化
const handleFilterSelectedTagsChange = (selectedTags) => {
  // 已使用CategorySelector组件，无需处理
}

// 处理表单分类标签变化
const handleFormSelectedTagsChange = (selectedTags) => {
  selectedFormTags.value = selectedTags
  // 更新表单数据
  musicForm.categoryIds = [...selectedFormTagIds.value]
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
  // 重置表单数据
  Object.assign(musicForm, {
    id: '',
    title: '',
    artistId: '',
    albumId: '',
    duration: 0,
    fileUrl: '',
    coverImage: '',
    categoryIds: [],
    lyrics: ''
  })
  // 重置分类选择
  selectedFormTagIds.value = []
  selectedFormTags.value = []
  dialogVisible.value = true
}

// 编辑音乐
const handleEdit = (row) => {
  Object.assign(musicForm, row)

  // 初始化分类选择 - 从categories字段中提取标签ID
  const categoryIds = row.categories ? row.categories.map(cat => cat.id) : []
  selectedFormTagIds.value = [...categoryIds]
  // selectedFormTags.value 由CategorySelector组件自动计算，不需要手动设置

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





onMounted(() => {
  getMusicList();
  getAlbums();
  getArtists();
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
    width: 250px;
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

.album-title {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  max-height: 2.8em;
}

.lyrics-preview {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.lyrics-text {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  max-height: 1.4em;
}

.lyrics-full pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: Arial, sans-serif;
  line-height: 1.6;
  margin: 0;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
  max-height: 500px;
  overflow-y: auto;
}

.lyrics-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  font-style: italic;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

</style>