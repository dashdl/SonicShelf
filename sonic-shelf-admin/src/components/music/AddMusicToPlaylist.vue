<!-- 
添加音乐到歌单组件 - AddMusicToPlaylist.vue

功能说明：
1. 提供音乐搜索功能
2. 支持多选音乐
3. 支持将选中的音乐添加到指定歌单

后端接口需求：
- GET /musics/getAll - 搜索音乐
- POST /playlists/{playlistId}/add-musics - 添加音乐到歌单

数据格式说明：
- 音乐信息：包含id, title, artistName, albumTitle, coverImage, duration
-->

<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`添加音乐到歌单 - ${playlistTitle}`"
    width="600px"
    append-to-body
  >
    <div class="add-music-to-playlist">
      <!-- 搜索框 -->
      <el-input
        v-model="searchQuery"
        placeholder="搜索音乐名称或歌手（输入关键词实时搜索）"
        :prefix-icon="Search"
        clearable
        class="search-input"
        @clear="handleClearSearch"
      />
      
      <!-- 搜索结果表格 -->
      <div class="search-results">
        <el-table
          v-loading="loading"
          :data="musicList"
          stripe
          size="small"
          class="music-table"
          @selection-change="handleSelectionChange"
          v-infinite-scroll="loadMore"
          :infinite-scroll-disabled="!hasMore || loading"
          :infinite-scroll-distance="100"
          infinite-scroll-immediate="false"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="音乐" min-width="200">
            <template #default="scope">
              <div class="music-info">
                <el-image
                  v-if="scope.row.coverImage"
                  :src="formatImageUrl(scope.row.coverImage)"
                  :fit="'cover'"
                  class="music-cover"
                />
                <div class="music-details">
                  <div class="music-title">{{ scope.row.title }}</div>
                  <div class="music-artist">{{ scope.row.artistName }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="albumTitle" label="专辑" min-width="150" />
          <el-table-column label="时长" width="100">
            <template #default="scope">
              {{ formatDuration(scope.row.duration) }}
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <div v-if="!loading && musicList.length === 0" class="empty-state">
          <el-empty :description="searchQuery ? '未找到相关音乐' : '暂无音乐数据'" />
        </div>
        
        <!-- 加载更多状态 -->
        <div v-if="loading && musicList.length > 0" class="loading-more">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <span>正在加载更多音乐...</span>
        </div>
        
        <!-- 没有更多数据提示 -->
        <div v-if="!hasMore && musicList.length > 0" class="no-more-data">
          <span>没有更多音乐了</span>
        </div>
      </div>

      <!-- 选中统计 -->
      <div v-if="selectedMusics.length > 0" class="selection-info">
        已选中 {{ selectedMusics.length }} 首音乐
      </div>

      <!-- 操作按钮 -->
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          :disabled="selectedMusics.length === 0"
          :loading="adding"
          @click="handleConfirm"
        >
          添加选中音乐
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Loading } from '@element-plus/icons-vue'
import request from "@/utils/request.js"

// 组件属性
const props = defineProps({
  playlistId: {
    type: [String, Number],
    required: true
  },
  playlistTitle: {
    type: String,
    required: true
  }
})

// 组件事件
const emit = defineEmits(['added', 'cancel'])

// 对话框显示状态
const dialogVisible = ref(false)

// 搜索相关状态
const searchQuery = ref('')
const musicList = ref([])
const loading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const pageSize = 20

// 选择相关状态
const selectedMusics = ref([])

// 添加操作状态
const adding = ref(false)

// 监听搜索查询变化，实现实时搜索
watch(searchQuery, (newQuery) => {
  if (newQuery.trim()) {
    // 重置分页状态
    currentPage.value = 1
    musicList.value = []
    hasMore.value = true
    
    // 防抖处理，避免频繁请求
    clearTimeout(searchMusic.timeoutId)
    searchMusic.timeoutId = setTimeout(() => {
      searchMusic()
    }, 500)
  } else {
    // 清空搜索时重置状态
    musicList.value = []
    selectedMusics.value = []
    currentPage.value = 1
    hasMore.value = true
  }
})

/**
 * 打开对话框
 */
const open = async () => {
  dialogVisible.value = true
  searchQuery.value = ''
  musicList.value = []
  selectedMusics.value = []
  
  // 打开对话框时自动加载所有音乐
  await loadAllMusic()
}

/**
 * 关闭对话框
 */
const close = () => {
  dialogVisible.value = false
}

/**
 * 处理清空搜索
 */
const handleClearSearch = () => {
  musicList.value = []
  selectedMusics.value = []
  currentPage.value = 1
  hasMore.value = true
  // 清空搜索后重新加载所有音乐
  loadAllMusic()
}

/**
 * 处理取消
 */
const handleCancel = () => {
  close()
  emit('cancel')
}

/**
 * 处理确认添加
 */
const handleConfirm = async () => {
  await addMusicToPlaylist()
}

/**
 * 处理选择变化
 */
const handleSelectionChange = (selection) => {
  selectedMusics.value = selection
}

/**
 * 加载更多音乐
 */
const loadMore = async () => {
  if (!hasMore.value || loading.value) return
  
  await searchMusic()
}

/**
 * 加载所有音乐（无关键词搜索）
 */
const loadAllMusic = async () => {
  try {
    loading.value = true
    currentPage.value = 1
    const res = await request.get('/musics/getAll', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize
      }
    })
    
    if (res.code === '200') {
      const newMusicList = res.data?.list || []
      musicList.value = newMusicList
      
      // 判断是否还有更多数据
      hasMore.value = newMusicList.length >= pageSize
      currentPage.value++
    } else {
      ElMessage.error(res.message || '加载音乐失败')
    }
  } catch (error) {
    console.error('加载音乐失败:', error)
    ElMessage.error('加载音乐失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索音乐
 * API: GET /musics/getAll?keyword={keyword}&pageNum=1&pageSize=20
 * 请求体: 无
 * 响应体: {
 *   code: '200',
 *   message: '成功',
 *   data: {
 *     list: [
 *       {
 *         id: '音乐ID',
 *         title: '音乐标题',
 *         artistName: '艺术家名称',
 *         albumTitle: '专辑名称',
 *         coverImage: '封面图片URL',
 *         duration: 时长(秒)
 *       }
 *     ],
 *     total: 总数
 *   }
 * }
 */
const searchMusic = async () => {
  try {
    loading.value = true
    const res = await request.get('/musics/getAll', {
      params: {
        keyword: searchQuery.value,
        pageNum: currentPage.value,
        pageSize: pageSize
      }
    })
    
    if (res.code === '200') {
      const newMusicList = res.data?.list || []
      
      if (currentPage.value === 1) {
        // 第一页，替换整个列表
        musicList.value = newMusicList
      } else {
        // 后续页面，追加到列表
        musicList.value = [...musicList.value, ...newMusicList]
      }
      
      // 判断是否还有更多数据
      hasMore.value = newMusicList.length >= pageSize
      currentPage.value++
    } else {
      ElMessage.error(res.message || '搜索失败')
    }
  } catch (error) {
    console.error('搜索音乐失败:', error)
    ElMessage.error('搜索音乐失败')
  } finally {
    loading.value = false
  }
}

/**
 * 添加音乐到歌单
 * API: POST /playlists/{playlistId}/add-musics
 * 请求体: { musicIds: ['音乐ID1', '音乐ID2', ...] }
 * 响应体: { code: '200', message: '添加成功' }
 */
const addMusicToPlaylist = async () => {
  try {
    adding.value = true
    const musicIds = selectedMusics.value.map(music => music.id)
    
    const res = await request.post(`/playlists/${props.playlistId}/add-musics`, {
      musicIds: musicIds
    })
    
    if (res.code === '200') {
      ElMessage.success('添加成功')
      close()
      emit('added', musicIds)
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('添加音乐失败:', error)
    ElMessage.error('添加音乐失败')
  } finally {
    adding.value = false
  }
}

/**
 * 格式化图片URL
 */
const formatImageUrl = (url) => {
  if (!url) return ''
  return url.startsWith('data:') ? url : `http://localhost:8080${url}`
}

/**
 * 格式化音乐时长（秒转分:秒）
 */
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 暴露方法给父组件
defineExpose({
  open,
  close
})
</script>

<style scoped>
.add-music-to-playlist {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-input {
  margin-bottom: 8px;
}

.search-results {
  min-height: 300px;
  max-height: 400px;
  overflow-y: auto;
}

.music-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.music-cover {
  width: 40px;
  height: 40px;
  border-radius: 4px;
}

.music-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.music-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.music-artist {
  font-size: 12px;
  color: #909399;
}

.selection-info {
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.loading-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  gap: 8px;
  color: #909399;
  font-size: 14px;
}

.no-more-data {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  color: #909399;
  font-size: 14px;
}
</style>