<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="comment-management-card">
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
          <el-button type="primary" @click="handleRefresh">
            <el-icon>
              <Refresh/>
            </el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <div class="search-filter">
        <el-input
            v-model="searchQuery"
            placeholder="搜索评论内容"
            :prefix-icon="Search"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
        />
        <el-select
            v-model="targetTypeFilter"
            placeholder="筛选目标类型"
            clearable
            class="filter-select"
        >
          <el-option label="音乐" value="music"/>
          <el-option label="歌单" value="playlist"/>
          <el-option label="专辑" value="album"/>
          <el-option label="歌手" value="artist"/>
          <el-option label="动态" value="dynamic"/>
        </el-select>
        <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="date-picker"
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

      <!-- 批量操作区域 -->
      <div class="batch-actions" v-if="selectedComments.length > 0">
        <el-button type="danger" @click="handleBatchDelete">
          <el-icon>
            <Delete/>
          </el-icon>
          批量删除 ({{ selectedComments.length }})
        </el-button>
      </div>

      <el-table
          :data="commentList"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left"/>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="nickname" label="用户" min-width="180">
          <template #default="scope">
            <div class="user-info">
              <el-avatar
                  v-if="scope.row.avatar"
                  :src="scope.row.avatar.startsWith('data:') ? scope.row.avatar : `http://localhost:8080${scope.row.avatar}`"
                  size="small"
              />
              <span class="user-nickname">{{ scope.row.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="targetType" label="类型" min-width="120">
          <template #default="scope">
            <el-tag :type="getTargetTypeTagType(scope.row.targetType)">
              {{ getTargetTypeText(scope.row.targetType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="200">
          <template #default="scope">
            <div class="comment-content">
              <span class="content-text">{{ scope.row.content }}</span>
              <el-button 
                type="text" 
                size="small" 
                @click="handleViewContent(scope.row)"
                v-if="scope.row.content && scope.row.content.length > 50">
                查看完整内容
              </el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" label="回复类型" min-width="110">
          <template #default="scope">
            <el-tag v-if="scope.row.parentId" type="primary" @click="handleViewParentDetail(scope.row)" style="cursor: pointer;">回复评论</el-tag>
            <el-tag v-else type="info">主评论</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180"/>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
                type="primary"
                size="small"
                @click="handleViewDetail(scope.row)"
            >
              <el-icon>
                <View/>
              </el-icon>
              详情
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

      <!-- 评论详情对话框 -->
      <el-dialog
          v-model="detailDialogVisible"
          :title="`评论详情 - ID: ${currentComment.id}`"
          width="600px"
      >
        <div class="comment-detail">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="ID">{{ currentComment.id }}</el-descriptions-item>
            <el-descriptions-item label="用户信息">
              <div class="user-detail">
                <el-avatar :src="currentComment.avatar.startsWith('data:') ? currentComment.avatar : `http://localhost:8080${currentComment.avatar}`" size="small"/>
                <span style="margin-left: 8px;">{{ currentComment.nickname }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="目标类型">
              <el-tag :type="getTargetTypeTagType(currentComment.targetType)">
                {{ getTargetTypeText(currentComment.targetType) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="评论内容">
              <div class="content-detail">{{ currentComment.content }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="点赞数">{{ currentComment.likeCount }}</el-descriptions-item>
            <el-descriptions-item label="回复数">{{ currentComment.replyCount }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusTagType(currentComment.status)">
                {{ getStatusText(currentComment.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ currentComment.createdAt }}</el-descriptions-item>
          </el-descriptions>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="detailDialogVisible = false">关闭</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 完整内容查看对话框 -->
      <el-dialog
          v-model="contentDialogVisible"
          :title="`完整评论内容 - ID: ${currentComment.id}`"
          width="500px"
      >
        <div class="full-content">
          <pre>{{ currentComment.content }}</pre>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="contentDialogVisible = false">关闭</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, watch, computed} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Search, Refresh, View, Delete} from '@element-plus/icons-vue'
import request from "@/utils/request.js";

// 评论列表数据
const commentList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const targetTypeFilter = ref('')
const dateRange = ref([])

// 选中的评论
const selectedComments = ref([])

// 对话框
const detailDialogVisible = ref(false)
const contentDialogVisible = ref(false)

// 当前操作的评论
const currentComment = reactive({
  id: '',
  nickname: '',
  avatar: '',
  targetType: '',
  content: '',
  likeCount: 0,
  replyCount: 0,
  status: 1,
  createdAt: ''
})

// 获取评论列表
// 请求体：
// {
//   pageNum: number,    // 页码
//   pageSize: number,   // 每页大小
//   keyword?: string,   // 搜索关键词（评论内容）
//   targetType?: string, // 目标类型筛选
//   startDate?: string, // 开始日期
//   endDate?: string     // 结束日期
// }
// 
// 获取评论列表
// 请求参数：
// {
//   pageNum: number,      // 页码（必填）
//   pageSize: number,     // 每页数量（必填）
//   keyword?: string,     // 搜索关键词（可选）
//   targetType?: string,  // 目标类型（可选：music/playlist/album/artist/dynamic）
//   startDate?: string,   // 开始日期（可选，格式：YYYY-MM-DD）
//   endDate?: string      // 结束日期（可选，格式：YYYY-MM-DD）
// }
// 
// 响应体：
// {
//   code: string,
//   message: string,
//   data: {
//     list: Array<{
//       id: number,           // 评论ID
//       content: string,       // 评论内容
//       nickname: string,      // 用户昵称
//       avatar: string,       // 用户头像URL
//       targetType: string,    // 目标类型
//       parentId: number,     // 父评论ID（null表示主评论）
//       likeCount: number,    // 点赞数
//       replyCount: number,   // 回复数
//       status: number,       // 状态（1-正常，0-异常）
//       createdAt: string     // 创建时间
//     }>,
//     total: number,          // 总记录数
//     pageNum: number,        // 当前页码
//     pageSize: number,       // 每页数量
//     pages: number           // 总页数
//   }
// }
const getCommentList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      targetType: targetTypeFilter.value,
      startDate: dateRange.value ? dateRange.value[0] : '',
      endDate: dateRange.value ? dateRange.value[1] : ''
    }
    const res = await request.get('comments/getAll', {params})
    if (res.code === '200') {
      commentList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  }
}

// 获取目标类型显示文本
const getTargetTypeText = (targetType) => {
  const typeMap = {
    'music': '音乐',
    'playlist': '歌单',
    'album': '专辑',
    'artist': '歌手',
    'dynamic': '动态'
  }
  return typeMap[targetType] || targetType
}

// 获取目标类型标签样式
const getTargetTypeTagType = (targetType) => {
  const typeMap = {
    'music': 'primary',
    'playlist': 'success',
    'album': 'warning',
    'artist': 'danger',
    'dynamic': 'info'
  }
  return typeMap[targetType] || ''
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 1: return 'success' // 正常
    case 2: return 'warning' // 隐藏
    case 0: return 'danger'  // 删除
    default: return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1: return '正常'
    case 2: return '隐藏'
    case 0: return '已删除'
    default: return '未知'
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getCommentList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  targetTypeFilter.value = ''
  dateRange.value = []
  currentPage.value = 1
  getCommentList()
}

// 刷新
const handleRefresh = () => {
  getCommentList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getCommentList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getCommentList()
}

// 选择评论
const handleSelectionChange = (selection) => {
  selectedComments.value = selection
}

// 查看评论详情
const handleViewDetail = (comment) => {
  Object.assign(currentComment, comment)
  detailDialogVisible.value = true
}

// 查看被回复的评论详情
const handleViewParentDetail = async (comment) => {
  if (!comment.parentId) {
    ElMessage.warning('该评论没有回复其他评论')
    return
  }
  
  try {
    // 根据parentId获取被回复的评论详情
    const res = await request.get(`comments/getById/${comment.parentId}`)
    if (res.code === '200' && res.data) {
      Object.assign(currentComment, res.data)
      detailDialogVisible.value = true
    } else {
      ElMessage.error('获取被回复评论详情失败')
    }
  } catch (error) {
    console.error('获取被回复评论详情失败:', error)
    ElMessage.error('获取被回复评论详情失败')
  }
}

// 查看完整内容
const handleViewContent = (comment) => {
  Object.assign(currentComment, comment)
  contentDialogVisible.value = true
}

// 删除单个评论
// 请求体：
// {
//   id: number  // 评论ID（必填）
// }
// 
// 响应体：
// {
//   code: string,        // 状态码（200-成功）
//   message: string,     // 响应消息
//   data: null
// }
// 
// 使用场景：删除指定的评论记录
const handleDelete = async (comment) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除评论ID为 ${comment.id} 的评论吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    
    const res = await request.delete(`comments/delete/${comment.id}`)
    
    if (res.code === '200') {
      ElMessage.success('删除成功')
      getCommentList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除评论
// 请求体：
// {
//   ids: number[]  // 评论ID数组（必填，至少包含一个ID）
// }
// 
// 响应体：
// {
//   code: string,        // 状态码（200-成功）
//   message: string,     // 响应消息
//   data: null
// }
// 
// 使用场景：批量删除选中的多条评论记录
const handleBatchDelete = async () => {
  if (selectedComments.value.length === 0) {
    ElMessage.warning('请选择要删除的评论')
    return
  }
  
  try {
    await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedComments.value.length} 条评论吗？`,
        '批量删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    
    const ids = selectedComments.value.map(comment => comment.id)
    const res = await request.delete('comments/batchDelete', {
      data: { ids }
    })
    
    if (res.code === '200') {
      ElMessage.success('批量删除成功')
      selectedComments.value = []
      getCommentList()
    } else {
      ElMessage.error(res.message || '批量删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除评论失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 修改评论状态
// 请求体：
// {
//   id: number,     // 评论ID（必填）
//   status: number  // 状态（必填，1-正常，0-隐藏）
// }
// 
// 响应体：
// {
//   code: string,        // 状态码（200-成功）
//   message: string,     // 响应消息
//   data: null
// }
// 
// 使用场景：修改评论的显示状态（正常/隐藏）
const handleChangeStatus = async (comment, newStatus) => {
  try {
    const statusText = newStatus === 1 ? '正常' : '隐藏'
    await ElMessageBox.confirm(
        `确定要将评论ID为 ${comment.id} 的评论状态改为${statusText}吗？`,
        '状态修改确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    
    const res = await request.put('comments/updateStatus', {
      id: comment.id,
      status: newStatus
    })
    
    if (res.code === '200') {
      ElMessage.success('状态修改成功')
      getCommentList()
    } else {
      ElMessage.error(res.message || '状态修改失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('修改评论状态失败:', error)
      ElMessage.error('状态修改失败')
    }
  }
}

onMounted(() => {
  getCommentList()
})
</script>

<style scoped>
.admin-management-container {
  padding: 20px;
}

.comment-management-card {
  min-height: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-filter {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.batch-actions {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.search-input {
  width: 200px;
}

.filter-select {
  width: 150px;
}

.date-picker {
  width: 240px;
}

.user-info {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  min-height: 32px;
}

.user-info .el-avatar {
  flex-shrink: 0;
  width: 32px !important;
  height: 32px !important;
}

.user-nickname {
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;
  line-height: 1.4;
  max-height: 2.8em;
  flex: 1;
  min-width: 0;
}

.target-name {
  color: #409eff;
  font-weight: 500;
}

.comment-content {
  max-width: 300px;
}

.content-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  word-break: break-all;
  line-height: 1.4;
  max-height: 2.8em;
}

.content-detail {
  white-space: pre-wrap;
  word-break: break-all;
  line-height: 1.5;
}

.full-content pre {
  white-space: pre-wrap;
  word-break: break-all;
  line-height: 1.5;
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.user-detail {
  display: flex;
  align-items: center;
}

.flexible-table {
  width: 100%;
}

@media (max-width: 1200px) {
  .search-filter {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-input,
  .filter-select,
  .date-picker {
    width: 100%;
    max-width: 300px;
  }
}
</style>