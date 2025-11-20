<template>
  <div class="dashboard-container">
    <el-card shadow="hover" class="dashboard-card">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      
      <!-- 统计卡片 -->
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
              <el-icon color="#3f8600"><User /></el-icon>
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
              <el-icon color="#cf1322"><Headset /></el-icon>
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
              <el-icon color="#1890ff"><Collection /></el-icon>
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
              <el-icon color="#fa8c16"><User /></el-icon>
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
              <el-icon color="#722ed1"><Collection /></el-icon>
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
              <el-icon color="#eb2f96"><Setting /></el-icon>
            </template>
          </el-statistic>
        </el-card>
      </div>

      <!-- 最近操作记录 -->
      <el-card shadow="hover" class="recent-operations-card">
        <template #header>
          <div class="card-header">
            <span>最近操作</span>
          </div>
        </template>
        <el-table :data="recentOperations" stripe style="width: 100%" class="flexible-table">
        <el-table-column prop="time" label="时间" min-width="180" />
        <el-table-column prop="user" label="用户" min-width="120" />
        <el-table-column prop="action" label="操作" min-width="200" />
        <el-table-column prop="result" label="结果" min-width="100" />
      </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

// 统计数据
const userCount = ref(0)
const musicCount = ref(0)
const albumCount = ref(0)
const artistCount = ref(0)
const playlistCount = ref(0)
const categoryCount = ref(0)

// 最近操作记录
const recentOperations = ref([
  { time: '2024-01-15 14:30:22', user: 'admin', action: '添加新用户', result: '成功' },
  { time: '2024-01-15 13:15:45', user: 'admin', action: '上传音乐', result: '成功' },
  { time: '2024-01-15 11:45:12', user: 'admin', action: '更新专辑信息', result: '成功' },
  { time: '2024-01-14 16:20:33', user: 'admin', action: '删除歌手', result: '成功' },
  { time: '2024-01-14 15:10:18', user: 'admin', action: '创建歌单', result: '成功' }
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
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
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
</style>