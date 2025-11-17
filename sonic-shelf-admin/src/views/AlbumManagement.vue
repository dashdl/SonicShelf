<template>
  <div class="album-management-container">
    <el-card shadow="hover" class="album-management-card">
      <template #header>
        <div class="card-header">
          <span>专辑管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><i-ep-plus /></el-icon>
            添加专辑
          </el-button>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索专辑名称"
          prefix-icon="i-ep-search"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        />
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
          <el-icon><i-ep-search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><i-ep-refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 专辑列表 -->
      <el-table
        :data="albumList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="专辑名称" width="200">
          <template #default="scope">
            <div class="album-name">
              <el-image
                v-if="scope.row.coverUrl"
                :src="scope.row.coverUrl"
                :fit="'cover'"
                class="album-cover"
              />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="singerName" label="歌手" width="150" />
        <el-table-column prop="releaseDate" label="发行日期" width="150" />
        <el-table-column prop="musicCount" label="歌曲数量" width="120" />
        <el-table-column prop="createTime" label="添加时间" width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              <el-icon><i-ep-edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              <el-icon><i-ep-delete /></el-icon>
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
          <el-form-item label="专辑名称" prop="name">
            <el-input v-model="albumForm.name" placeholder="请输入专辑名称" />
          </el-form-item>
          <el-form-item label="歌手" prop="singerId">
            <el-select v-model="albumForm.singerId" placeholder="请选择歌手" filterable clearable style="width: 100%;">
              <el-option
                v-for="singer in singers"
                :key="singer.id"
                :label="singer.name"
                :value="singer.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="发行日期" prop="releaseDate">
            <el-date-picker
              v-model="albumForm.releaseDate"
              type="date"
              placeholder="选择发行日期"
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
                  <el-icon><i-ep-upload /></el-icon>
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
              v-if="albumForm.coverUrl"
              :src="albumForm.coverUrl"
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

// 专辑列表数据
const albumList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
const searchQuery = ref('')
const singerFilter = ref('')

// 下拉选择数据
const singers = ref([])

// 对话框
const dialogVisible = ref(false)
const albumFormRef = ref()
const albumForm = reactive({
  id: '',
  name: '',
  singerId: '',
  releaseDate: '',
  description: '',
  coverUrl: ''
})

// 表单验证规则
const albumRules = {
  name: [
    { required: true, message: '请输入专辑名称', trigger: 'blur' },
    { min: 1, max: 100, message: '专辑名称长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  singerId: [
    { required: true, message: '请选择歌手', trigger: 'change' }
  ],
  releaseDate: [
    { required: true, message: '请选择发行日期', trigger: 'change' }
  ]
}

// 上传组件引用
const coverUploadRef = ref()

// 选中的专辑
const selectedAlbums = ref([])

// 表单标题
const formTitle = computed(() => {
  return albumForm.id ? '编辑专辑' : '添加专辑'
})

// 获取专辑列表
const getAlbumList = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      singerId: singerFilter.value
    }
    const res = await mockService.album.getList(params)
    if (res.code === '200') {
      albumList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取专辑列表失败:', error)
    ElMessage.error('获取专辑列表失败')
  }
}

// 获取歌手列表
const getSingers = async () => {
  try {
    const res = await mockService.singer.getList({ pageSize: 1000 })
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
  getAlbumList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  singerFilter.value = ''
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
  Object.assign(albumForm, row)
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
      const res = await mockService.album.delete(row.id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getAlbumList()
      }
    } catch (error) {
      console.error('删除专辑失败:', error)
      ElMessage.error('删除专辑失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!albumFormRef.value) return
  try {
    await albumFormRef.value.validate()
    let res
    if (albumForm.id) {
      // 编辑专辑
      res = await mockService.album.update(albumForm)
    } else {
      // 添加专辑
      res = await mockService.album.add(albumForm)
    }
    if (res.code === '200') {
      ElMessage.success(albumForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      getAlbumList()
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
    name: '',
    singerId: '',
    releaseDate: '',
    description: '',
    coverUrl: ''
  })
  if (albumFormRef.value) {
    albumFormRef.value.resetFields()
  }
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

// 封面上传成功处理
const handleCoverUpload = (response) => {
  if (response.code === '200') {
    albumForm.coverUrl = response.data.url
    ElMessage.success('封面上传成功')
  }
}

onMounted(() => {
  getAlbumList()
  getSingers()
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
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>