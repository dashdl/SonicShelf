<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="800px"
    :before-close="handleClose"
    class="playlist-music-dialog"
  >
    <div class="playlist-music-editor">
      <!-- 歌单基本信息 -->
      <div class="playlist-info">
        <el-image
          v-if="playlistInfo.coverImage"
          :src="playlistInfo.coverImage.startsWith('data:') ? playlistInfo.coverImage : `http://localhost:8080${playlistInfo.coverImage}`"
          :fit="'cover'"
          class="playlist-cover"
        />
        <div class="playlist-details">
          <h3>{{ playlistInfo.title }}</h3>
          <p class="playlist-description">{{ playlistInfo.description || '暂无描述' }}</p>
          <div class="playlist-stats">
            <span>歌曲数量: {{ playlistInfo.musicCount || 0 }}</span>
            <span>播放次数: {{ playlistInfo.playCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 音乐列表 -->
      <div class="music-list-section">
        <div class="section-header">
          <h4>音乐列表</h4>
          <el-button type="primary" size="small" @click="handleAddMusic">
            <el-icon><Plus /></el-icon>
            添加音乐
          </el-button>
        </div>

        <el-table
          :data="musicList"
          stripe
          size="small"
          class="music-table"
        >
          <el-table-column label="音乐" min-width="200">
            <template #default="scope">
              <div class="music-info">
                <el-image
                  v-if="scope.row.coverImage"
                  :src="scope.row.coverImage.startsWith('data:') ? scope.row.coverImage : `http://localhost:8080${scope.row.coverImage}`"
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
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button
                type="danger"
                size="small"
                @click="handleRemoveMusic(scope.row)"
              >
                <el-icon><Delete /></el-icon>
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 空状态 -->
        <div v-if="musicList.length === 0" class="empty-state">
          <el-empty description="暂无音乐" />
        </div>
      </div>

      <!-- 添加音乐到歌单组件 -->
      <AddMusicToPlaylist
        ref="addMusicDialogRef"
        :playlist-id="playlistInfo.id"
        :playlist-title="playlistInfo.title"
        @added="handleMusicAdded"
        @cancel="handleAddMusicCancel"
      />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import request from "@/utils/request.js"
import AddMusicToPlaylist from './AddMusicToPlaylist.vue'

// 定义emit事件
const emit = defineEmits(['music-updated'])

// 对话框显示状态
const dialogVisible = ref(false)

// 歌单信息 - 包含歌单的基本信息
const playlistInfo = reactive({
  id: '',           // 歌单ID
  title: '',        // 歌单标题
  description: '',  // 歌单描述
  coverImage: '',  // 封面图片URL
  musicCount: 0,   // 歌曲数量
  playCount: 0     // 播放次数
})

// 音乐列表 - 当前歌单中的音乐列表
const musicList = ref([])

// 添加音乐对话框引用
const addMusicDialogRef = ref()

// 计算对话框标题 - 动态显示歌单名称
const dialogTitle = computed(() => {
  return `编辑歌单音乐 - ${playlistInfo.title}`
})

/**
 * 打开歌单音乐编辑对话框
 * @param {Object} playlist - 歌单信息对象
 */
const open = async (playlist) => {
  // 设置歌单信息
  Object.assign(playlistInfo, playlist)
  // 显示对话框
  dialogVisible.value = true
  // 加载歌单音乐列表
  await loadPlaylistMusic()
}

/**
 * 关闭对话框并重置数据
 */
const handleClose = () => {
  dialogVisible.value = false
  resetData()
}

/**
 * 重置所有数据到初始状态
 */
const resetData = () => {
  Object.assign(playlistInfo, {
    id: '',
    title: '',
    description: '',
    coverImage: '',
    musicCount: 0,
    playCount: 0
  })
  musicList.value = []
}

/**
 * 加载歌单音乐列表
 * API: GET /playlists/{playlistId}/musics
 * 请求体: 无
 * 响应体: {
 *   code: '200',
 *   message: '成功',
 *   data: [
 *     {
 *       id: '音乐ID',
 *       title: '音乐标题',
 *       artistName: '艺术家名称',
 *       albumTitle: '专辑名称',
 *       coverImage: '封面图片URL',
 *       duration: 时长(秒)
 *     }
 *   ]
 * }
 */
const loadPlaylistMusic = async () => {
  try {
    const res = await request.get(`playlists/${playlistInfo.id}/musics/admin`)
    if (res.code === '200') {
      musicList.value = res.data || []
    }
  } catch (error) {
    console.error('加载歌单音乐失败:', error)
    ElMessage.error('加载歌单音乐失败')
  }
}

/**
 * 格式化音乐时长（秒转分:秒）
 * @param {number} seconds - 时长（秒）
 * @returns {string} 格式化后的时长字符串
 */
const formatDuration = (seconds) => {
  if (!seconds) return '00:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

/**
 * 打开添加音乐对话框
 */
const handleAddMusic = () => {
  addMusicDialogRef.value?.open()
}

/**
 * 处理音乐添加成功
 */
const handleMusicAdded = async () => {
  // 重新加载歌单音乐列表
  await loadPlaylistMusic()
  // 触发父组件更新事件
  emit('music-updated', playlistInfo.id)
}

/**
 * 处理添加音乐取消
 */
const handleAddMusicCancel = () => {
  // 可以在这里处理取消逻辑
}

/**
 * 从歌单移除音乐
 * API: DELETE /playlists/{playlistId}/remove-music/{musicId}
 * 请求体: 无
 * 响应体: {
 *   code: '200',
 *   message: '移除成功'
 * }
 * @param {Object} music - 要移除的音乐对象
 */
const handleRemoveMusic = async (music) => {
  try {
    // 确认对话框
    await ElMessageBox.confirm(`确定要从歌单中移除音乐"${music.title}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await request.delete(`playlists/${playlistInfo.id}/remove-music/${music.id}`)
    
    if (res.code === '200') {
      ElMessage.success('移除音乐成功')
      await loadPlaylistMusic()
      // 更新歌单信息中的歌曲数量
      playlistInfo.musicCount = musicList.value.length
      // 触发父组件更新事件
      emit('music-updated', playlistInfo.id)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除音乐失败:', error)
      ElMessage.error('移除音乐失败')
    }
  }
}

/**
 * 保存操作（目前仅显示成功消息）
 */
const handleSave = () => {
  ElMessage.success('歌单音乐信息已保存')
  handleClose()
}

// 监听搜索查询变化，实时搜索的功能已移至 AddMusicToPlaylist 组件中

// 暴露方法给父组件
defineExpose({
  open
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.playlist-music-dialog {
  .el-dialog {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
}

.playlist-music-editor {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
  
  /* 自定义滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }
  
  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
    
    &:hover {
      background: #a8a8a8;
    }
  }
  
  .playlist-info {
    display: flex;
    align-items: flex-start;
    gap: $spacing-lg;
    margin-bottom: $spacing-xl;
    padding: $spacing-lg;
    background: #f8f9fa;
    border-radius: $radius-base;

    .playlist-cover {
      width: 80px;
      height: 80px;
      border-radius: $radius-sm;
      object-fit: cover;
      flex-shrink: 0;
    }

    .playlist-details {
      flex: 1;

      h3 {
        margin: 0 0 $spacing-sm 0;
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }

      .playlist-description {
        margin: 0 0 $spacing-md 0;
        color: #666;
        line-height: 1.5;
      }

      .playlist-stats {
        display: flex;
        gap: $spacing-xl;
        color: #888;
        font-size: 14px;
      }
    }
  }

  .music-list-section {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-md;

      h4 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }
    }

    .music-table {
      margin-bottom: $spacing-lg;

      .music-info {
        display: flex;
        align-items: center;
        gap: $spacing-md;

        .music-cover {
          width: 40px;
          height: 40px;
          border-radius: $radius-sm;
          object-fit: cover;
          flex-shrink: 0;
        }

        .music-details {
          .music-title {
            font-weight: 500;
            margin-bottom: 2px;
          }

          .music-artist {
            font-size: 12px;
            color: #666;
          }
        }
      }
    }

    .empty-state {
      text-align: center;
      padding: $spacing-xl 0;
      color: #999;
    }
  }

  .add-music-dialog {
    .search-input {
      margin-bottom: $spacing-md;
    }

    .search-music-table {
      margin-bottom: $spacing-lg;
      max-height: 300px;
      overflow-y: auto;

      .music-info {
        display: flex;
        align-items: center;
        gap: $spacing-md;

        .music-cover {
          width: 40px;
          height: 40px;
          border-radius: $radius-sm;
          object-fit: cover;
          flex-shrink: 0;
        }

        .music-details {
          .music-title {
            font-weight: 500;
            margin-bottom: 2px;
          }

          .music-artist {
            font-size: 12px;
            color: #666;
          }
        }
      }
    }

    .dialog-footer {
      display: flex;
      justify-content: flex-end;
      gap: $spacing-md;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: $spacing-md;
}
</style>