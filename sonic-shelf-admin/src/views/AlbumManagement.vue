<!--
  专辑管理页面
  
  数据流说明：
  1. 页面加载时调用 getAlbumList() 获取专辑列表
  2. 搜索功能通过 handleSearch() 触发，支持按专辑名称和歌手筛选
  3. 分页功能通过 handleCurrentChange() 和 handleSizeChange() 实现
  4. 表单提交通过 handleSubmit() 处理，支持添加和编辑专辑
  5. 删除功能通过 handleDelete() 实现，带确认对话框
  
  API替换说明：
  1. 当前使用 mockService.album 模拟数据
  2. 替换为真实API时，请导入 src/api/album.js 中的方法
  3. 保持相同的返回格式：{ code: '200', data: {...} }
  
  后端API详细文档：
  
  1. 获取专辑列表
     - URL: GET /admin/albums
     - 请求参数（查询参数）：
       { 
         pageNum: Number,      // 当前页码，从1开始
         pageSize: Number,     // 每页条数
         keyword: String,      // 搜索关键词（专辑名称）
         singerId: String      // 歌手ID（筛选条件）
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: { 
           list: Array<{       // 专辑列表
             id: String,       // 专辑ID
             name: String,     // 专辑名称
             singerId: String, // 歌手ID
             singerName: String, // 歌手名称
             releaseDate: String, // 发行日期（YYYY-MM-DD）
             description: String, // 专辑简介
             coverUrl: String, // 封面图片URL
             musicCount: Number, // 歌曲数量
             createTime: String, // 创建时间（YYYY-MM-DD HH:mm:ss）
             updateTime: String  // 更新时间（YYYY-MM-DD HH:mm:ss）
           }>,
           total: Number       // 总记录数
         }
       }
  
  2. 获取歌手列表
     - URL: GET /admin/singers
     - 请求参数（查询参数）：
       { 
         pageSize: Number      // 每页条数（用于下拉选择，通常设为较大值）
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
  
  3. 添加专辑
     - URL: POST /admin/albums
     - 请求体：
       { 
         name: String,         // 专辑名称
         singerId: String,     // 歌手ID
         releaseDate: String,  // 发行日期（YYYY-MM-DD）
         description: String,  // 专辑简介
         coverUrl: String      // 封面图片URL
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: {               // 新增的专辑信息
           id: String,         // 专辑ID
           name: String,       // 专辑名称
           singerId: String,   // 歌手ID
           singerName: String, // 歌手名称
           releaseDate: String, // 发行日期
           description: String, // 专辑简介
           coverUrl: String,   // 封面图片URL
           musicCount: Number, // 歌曲数量（初始为0）
           createTime: String, // 创建时间
           updateTime: String  // 更新时间
         }
       }
  
  4. 更新专辑
     - URL: PUT /admin/albums/{id}
     - 请求体：
       { 
         id: String,           // 专辑ID
         name: String,         // 专辑名称
         singerId: String,     // 歌手ID
         releaseDate: String,  // 发行日期
         description: String,  // 专辑简介
         coverUrl: String      // 封面图片URL
       }
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: {               // 更新后的专辑信息
           id: String,         // 专辑ID
           name: String,       // 专辑名称
           singerId: String,   // 歌手ID
           singerName: String, // 歌手名称
           releaseDate: String, // 发行日期
           description: String, // 专辑简介
           coverUrl: String,   // 封面图片URL
           musicCount: Number, // 歌曲数量
           createTime: String, // 创建时间
           updateTime: String  // 更新时间
         }
       }
  
  5. 删除专辑
     - URL: DELETE /admin/albums/{id}
     - 请求参数（路径参数）：
       id: String             // 专辑ID
     - 返回格式：
       { 
         code: '200',          // 状态码
         data: null            // 无数据返回
       }
  
  6. 上传封面图片（预留接口）
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
    <el-card shadow="hover" class="album-management-card">
      <template #header>
        <div class="card-header">
          <span>专辑管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加专辑
          </el-button>
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索专辑名称"
          :prefix-icon="Search"
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
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <!-- 专辑列表 -->
      <el-table
        :data="albumList"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
        class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="专辑名称" min-width="200">
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
        <el-table-column prop="singerName" label="歌手" min-width="150" />
        <el-table-column prop="releaseDate" label="发行日期" min-width="150" />
        <el-table-column prop="musicCount" label="歌曲数量" min-width="120" />
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
import request from "@/utils/request.js";

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
// API: GET /admin/albums
// 请求参数: { pageNum, pageSize, keyword, singerId }
// 返回格式: { code: '200', data: { list: Array<Album>, total: Number } }
// Album字段详细说明:
// id: String - 专辑唯一标识符
// name: String - 专辑名称
// singerId: String - 歌手ID
// singerName: String - 歌手名称
// releaseDate: String - 发行日期 (YYYY-MM-DD)
// description: String - 专辑简介
// coverUrl: String - 封面图片URL
// musicCount: Number - 歌曲数量
// createTime: String - 创建时间 (YYYY-MM-DD HH:mm:ss)
// updateTime: String - 更新时间 (YYYY-MM-DD HH:mm:ss)
const getAlbumList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,     // 当前页码
      pageSize: pageSize.value,       // 每页条数
      keyword: searchQuery.value,     // 搜索关键词（专辑名称）
      singerId: singerFilter.value    // 歌手ID筛选
    }
    const res = await mockService.album.getList(params)
    if (res.code === '200') {
      albumList.value = res.data.list  // 专辑列表数据
      total.value = res.data.total     // 总记录数
    }
  } catch (error) {
    console.error('获取专辑列表失败:', error)
    ElMessage.error('获取专辑列表失败')
  }
}

// 获取歌手列表
// API: GET /admin/singers
// 请求参数: { pageSize } - 设置较大值以获取所有歌手用于下拉选择
// 返回格式: { code: '200', data: { list: Array<Singer>, total: Number } }
const getSingers = async () => {
  try {
    const res = await request.get('artist/getAll',{
      params: {
        pageNum: 1,
        pageSize: 1000,
      }
    })  // 每页1000条，确保获取所有歌手
    if (res.code === '200') {
      singers.value = res.data.list  // 歌手列表数据，用于下拉选择
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
// API: DELETE /admin/albums/{id}
// 请求参数: { id } - 专辑ID（路径参数）
// 返回格式: { code: '200', data: null }
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该专辑吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await mockService.album.delete(row.id)  // 传入专辑ID删除指定专辑
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getAlbumList()  // 删除成功后重新获取列表
      }
    } catch (error) {
      console.error('删除专辑失败:', error)
      ElMessage.error('删除专辑失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单（添加/编辑专辑）
// 添加API: POST /admin/albums
// 编辑API: PUT /admin/albums/{id}
// 请求体: { id, name, singerId, releaseDate, description, coverUrl }
// 返回格式: { code: '200', data: Album }
// Album字段详细说明:
// id: String - 专辑唯一标识符
// name: String - 专辑名称
// singerId: String - 歌手ID
// singerName: String - 歌手名称
// releaseDate: String - 发行日期 (YYYY-MM-DD)
// description: String - 专辑简介
// coverUrl: String - 封面图片URL
// musicCount: Number - 歌曲数量
// createTime: String - 创建时间 (YYYY-MM-DD HH:mm:ss)
// updateTime: String - 更新时间 (YYYY-MM-DD HH:mm:ss)
const handleSubmit = async () => {
  if (!albumFormRef.value) return
  try {
    await albumFormRef.value.validate()  // 表单验证
    let res
    if (albumForm.id) {
      // 编辑专辑
      res = await mockService.album.update(albumForm)  // 传入完整专辑信息更新
    } else {
      // 添加专辑
      res = await mockService.album.add(albumForm)     // 传入专辑信息添加新专辑
    }
    if (res.code === '200') {
      ElMessage.success(albumForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false  // 关闭对话框
      getAlbumList()  // 重新获取专辑列表
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
// API: POST /admin/upload/cover
// 请求体: FormData { file: File }
// 返回格式: { code: '200', data: { url: String } }
const handleCoverUpload = (response) => {
  if (response.code === '200') {
    albumForm.coverUrl = response.data.url  // 保存上传后的图片URL
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