<template>
  <div class="dashboard-container">
    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>

      <div class="statistics-cards">
        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="用户总数"
              :value="userCount"
              :precision="0"
              suffix="人"
              value-style="color: #3f8600"
          >
            <template #prefix>
              <el-icon color="#3f8600">
                <User/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>

        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="音乐总数"
              :value="musicCount"
              :precision="0"
              suffix="首"
              value-style="color: #cf1322"
          >
            <template #prefix>
              <el-icon color="#cf1322">
                <Headset/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>

        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="专辑总数"
              :value="albumCount"
              :precision="0"
              suffix="张"
              value-style="color: #1890ff"
          >
            <template #prefix>
              <el-icon color="#1890ff">
                <Collection/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>

        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="歌手总数"
              :value="artistCount"
              :precision="0"
              suffix="位"
              value-style="color: #fa8c16"
          >
            <template #prefix>
              <el-icon color="#fa8c16">
                <User/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>

        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="歌单总数"
              :value="playlistCount"
              :precision="0"
              suffix="个"
              value-style="color: #722ed1"
          >
            <template #prefix>
              <el-icon color="#722ed1">
                <Collection/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>

        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="分类总数"
              :value="categoryCount"
              :precision="0"
              suffix="个"
              value-style="color: #eb2f96"
          >
            <template #prefix>
              <el-icon color="#eb2f96">
                <Setting/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>

        <el-card shadow="hover" class="statistic-card">
          <el-statistic
              title="评论总数"
              :value="commentCount"
              :precision="0"
              suffix="条"
              value-style="color: #13c2c2"
          >
            <template #prefix>
              <el-icon color="#13c2c2">
                <ChatDotRound/>
              </el-icon>
            </template>
          </el-statistic>
        </el-card>
      </div>

      <!-- 评论分布统计 -->
      <el-card shadow="hover" class="comment-distribution-card">
        <template #header>
          <div class="card-header">
            <span>评论分布统计</span>
          </div>
        </template>
        <div class="distribution-charts">
          <div class="distribution-item">
            <div class="distribution-label">音乐评论</div>
            <div class="distribution-value">{{ musicCommentCount }} 条</div>
            <el-progress
                :percentage="musicCommentPercentage"
                :color="customColors"
                :show-text="false"
            />
          </div>
          <div class="distribution-item">
            <div class="distribution-label">歌单评论</div>
            <div class="distribution-value">{{ playlistCommentCount }} 条</div>
            <el-progress
                :percentage="playlistCommentPercentage"
                :color="customColors"
                :show-text="false"
            />
          </div>
          <div class="distribution-item">
            <div class="distribution-label">歌手评论</div>
            <div class="distribution-value">{{ artistCommentCount }} 条</div>
            <el-progress
                :percentage="artistCommentPercentage"
                :color="customColors"
                :show-text="false"
            />
          </div>
          <div class="distribution-item">
            <div class="distribution-label">专辑评论</div>
            <div class="distribution-value">{{ albumCommentCount }} 条</div>
            <el-progress
                :percentage="albumCommentPercentage"
                :color="customColors"
                :show-text="false"
            />
          </div>
          <div class="distribution-item">
            <div class="distribution-label">动态评论</div>
            <div class="distribution-value">{{ dynamicCommentCount }} 条</div>
            <el-progress
                :percentage="dynamicCommentPercentage"
                :color="customColors"
                :show-text="false"
            />
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="recent-operations-card">
        <template #header>
          <div class="card-header">
            <span>最近操作</span>
          </div>
        </template>
        <el-table :data="recentOperations" stripe style="width: 100%" class="flexible-table">
          <el-table-column prop="time" label="时间" min-width="180"/>
          <el-table-column prop="user" label="用户" min-width="120"/>
          <el-table-column prop="action" label="操作" min-width="200"/>
          <el-table-column prop="result" label="结果" min-width="100"/>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import request from '@/utils/request'
import {ChatDotRound} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";

// 统计数据
const userCount = ref(0)
const musicCount = ref(0)
const albumCount = ref(0)
const artistCount = ref(0)
const playlistCount = ref(0)
const categoryCount = ref(0)
const commentCount = ref(0)

// 评论分布数据
const musicCommentCount = ref(0)
const playlistCommentCount = ref(0)
const artistCommentCount = ref(0)
const albumCommentCount = ref(0)
const dynamicCommentCount = ref(0)

// 自定义进度条颜色
const customColors = [
  {color: '#f56c6c', percentage: 20},
  {color: '#e6a23c', percentage: 40},
  {color: '#5cb87a', percentage: 60},
  {color: '#1989fa', percentage: 80},
  {color: '#6f7ad3', percentage: 100}
]

// 计算评论分布百分比
const musicCommentPercentage = computed(() => {
  if (commentCount.value === 0) return 0
  return Math.round((musicCommentCount.value / commentCount.value) * 100)
})

const playlistCommentPercentage = computed(() => {
  if (commentCount.value === 0) return 0
  return Math.round((playlistCommentCount.value / commentCount.value) * 100)
})

const artistCommentPercentage = computed(() => {
  if (commentCount.value === 0) return 0
  return Math.round((artistCommentCount.value / commentCount.value) * 100)
})

const albumCommentPercentage = computed(() => {
  if (commentCount.value === 0) return 0
  return Math.round((albumCommentCount.value / commentCount.value) * 100)
})

const dynamicCommentPercentage = computed(() => {
  if (commentCount.value === 0) return 0
  return Math.round((dynamicCommentCount.value / commentCount.value) * 100)
})

// 最近操作记录
const recentOperations = ref([
  {time: '2024-01-15 14:30:22', user: 'admin', action: '添加新用户', result: '成功'},
  {time: '2024-01-15 13:15:45', user: 'admin', action: '上传音乐', result: '成功'},
  {time: '2024-01-15 11:45:12', user: 'admin', action: '更新专辑信息', result: '成功'},
  {time: '2024-01-14 16:20:33', user: 'admin', action: '删除歌手', result: '成功'},
  {time: '2024-01-14 15:10:18', user: 'admin', action: '创建歌单', result: '成功'}
])

// 获取统计数据
const getStatistics = async () => {
  try {
    const res = await request.get('/dashboard/statistics')
    if (res.code === '200') {
      userCount.value = res.data.userCount
      musicCount.value = res.data.musicCount
      albumCount.value = res.data.albumCount
      artistCount.value = res.data.artistCount
      playlistCount.value = res.data.playlistCount
      categoryCount.value = res.data.categoryCount

      // 获取评论统计数据
      await getCommentStatistics()
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取评论统计数据
const getCommentStatistics = async () => {
  try {
    const totalRes = await request.get('/comments/count')
    if (totalRes.code === '200') {
      commentCount.value = totalRes.data || 0
    }
    const distributionRes = await request.get('/comments/distribution')
    if (distributionRes.code === '200') {
      musicCommentCount.value = distributionRes.data.music || 0
      playlistCommentCount.value = distributionRes.data.playlist || 0
      artistCommentCount.value = distributionRes.data.artist || 0
      albumCommentCount.value = distributionRes.data.album || 0
      dynamicCommentCount.value = distributionRes.data.dynamic || 0
    }
  } catch (error) {
    ElMessage.error('获取评论统计数据失败:', error)
  }
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables.scss' as *;

.statistics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: $spacing-lg;
  margin-bottom: $spacing-lg;
}

.statistic-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    transform: translateY(-5px);
    box-shadow: $shadow-lg !important;
  }
}

.comment-distribution-card {
  margin-bottom: $spacing-lg;

  .distribution-charts {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: $spacing-md;
  }

  .distribution-item {
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;

    .distribution-label {
      font-size: 14px;
      color: $text-color-secondary;
      font-weight: 500;
    }

    .distribution-value {
      font-size: 16px;
      color: $text-color;
      font-weight: 600;
    }

    .el-progress {
      margin-top: 4px;
    }
  }
}

.recent-operations-card {
  margin-bottom: $spacing-lg;
}
</style>