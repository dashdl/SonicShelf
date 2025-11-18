<!--
  歌手管理页面
  
  数据流说明：
  1. 页面加载时调用 getSingerList() 获取歌手列表
  2. 搜索功能通过 handleSearch() 触发，支持按歌手名称和性别筛选
  3. 分页功能通过 handleCurrentChange() 和 handleSizeChange() 实现
  4. 表单提交通过 handleSubmit() 处理，支持添加和编辑歌手
  5. 删除功能通过 handleDelete() 实现，带确认对话框
  
  API替换说明：
  1. 当前使用 mockService.singer 模拟数据
  2. 替换为真实API时，请导入 src/api/singer.js 中的方法
  3. 保持相同的返回格式：{ code: '200', data: {...} }
  
  后端API要求：
  - GET /admin/singers - 获取歌手列表（支持分页、搜索）
  - POST /admin/singers - 添加歌手
  - PUT /admin/singers/{id} - 更新歌手
  - DELETE /admin/singers/{id} - 删除歌手
  
  详细的请求/响应格式：
  
  1. 获取歌手列表 (getSingerList)
     请求: GET /api/v1/artist/getAll?pageNum=1&pageSize=10&keyword=ed
     响应: {
       "code": "200",
       "message": "请求成功",
       "data": {
         "total": 1,
         "list": [
           {
             "id": 3,
             "name": "Ed Sheeran",
             "gender": 1,             // 0:组合, 1:男, 2:女
             "country": "UK",
             "description": "English singer-songwriter",
             "coverImage": "/uploads/avatars/v2bb941bd1693eb6e012b9cacf695d57aeb.jpg",
             "musicCount": 18,
             "createdAt": "2025-10-22 17:01:05"
           }
         ],
         "pageNum": 1,
         "pageSize": 10,
         "size": 1,
         "startRow": 1,
         "endRow": 1,
         "pages": 1,
         "prePage": 0,
         "nextPage": 0,
         "isFirstPage": true,
         "isLastPage": true,
         "hasPreviousPage": false,
         "hasNextPage": false,
         "navigatePages": 8,
         "navigatepageNums": [1],
         "navigateFirstPage": 1,
         "navigateLastPage": 1
       }
     }
  
  2. 添加歌手 (handleSubmit - 添加)
     请求: POST /api/v1/artist
     请求体: {
       "name": "新歌手",
       "gender": 1,                    // 0:组合, 1:男, 2:女
       "country": "中国",
       "description": "歌手简介",
       "coverImage": "/uploads/avatars/xxx.jpg"
     }
     响应: {
       "code": "200",
       "message": "请求成功",
       "data": {
         "id": 123,
         "name": "新歌手",
         "gender": 1,
         "country": "中国",
         "description": "歌手简介",
         "coverImage": "/uploads/avatars/xxx.jpg",
         "musicCount": 0,
         "createdAt": "2024-01-20 15:30:00"
       }
     }
  
  3. 更新歌手 (handleSubmit - 编辑)
     请求: PUT /api/v1/artist/123
     请求体: {
       "id": 123,
       "name": "更新的歌手名",
       "gender": 2,                    // 0:组合, 1:男, 2:女
       "country": "中国香港",
       "description": "更新后的简介",
       "coverImage": "/uploads/avatars/new-avatar.jpg"
     }
     响应: {
       "code": "200",
       "message": "请求成功",
       "data": {
         "id": 123,
         "name": "更新的歌手名",
         "gender": 2,
         "country": "中国香港",
         "description": "更新后的简介",
         "coverImage": "/uploads/avatars/new-avatar.jpg",
         "musicCount": 150,
         "createdAt": "2024-01-15 10:30:00"
       }
     }
  
  4. 删除歌手 (handleDelete)
     请求: DELETE /api/v1/artist/123
     响应: {
       "code": "200",
       "message": "请求成功"
     }
  
  替换步骤：
  1. 将 baseURL 改为实际后端地址
  2. 修改请求路径从 /admin/singers 改为 /api/v1/artist
  3. 根据实际响应结构调整字段映射 (avatarUrl -> coverImage, createTime -> createdAt)
  4. 更新分页参数名从 page/pageSize 改为 pageNum/pageSize
  5. 性别字段已改为数字格式 (0:组合, 1:男, 2:女)
  6. 更新头像上传API路径为 /api/v1/artistCover/{id}/
  7. coverImage相对路径需要拼接后端地址 (如: `http://localhost:8080${coverImage}`)
  8. 根据后端要求调整请求头
-->

<template>
  <div class="admin-management-container">
    <el-card shadow="hover" class="singer-management-card">
      <template #header>
        <div class="card-header">
          <span>歌手管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            添加歌手
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input
            v-model="searchQuery"
            placeholder="搜索歌手名称"
            prefix-icon="Search"
            clearable
            class="search-input"
            @keyup.enter="handleSearch"
        />
        <el-select
            v-model="genderFilter"
            placeholder="筛选性别"
            clearable
            class="filter-select"
        >
          <el-option label="男" :value="1"/>
          <el-option label="女" :value="2"/>
          <el-option label="组合" :value="0"/>
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

      <!-- 歌手列表 -->
      <el-table
          :data="singerList"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
          class="flexible-table"
      >
        <el-table-column type="selection" width="55" fixed="left"/>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="name" label="歌手名称" min-width="200">
          <template #default="scope">
            <div class="singer-name">
              <el-image
                  v-if="scope.row.coverImage"
                  :src="`http://localhost:8080${scope.row.coverImage}`"
                  :fit="'cover'"
                  class="singer-avatar"
              />
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" min-width="100">
          <template #default="scope">
            <el-tag
                :type="scope.row.gender === 1 ? 'primary' : scope.row.gender === 2 ? 'success' : 'warning'"
            >
              {{ scope.row.gender === 1 ? '男' : scope.row.gender === 2 ? '女' : '组合' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="country" label="国家/地区" min-width="150"/>
        <el-table-column prop="musicCount" label="歌曲数量" min-width="120"/>
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

      <!-- 歌手表单对话框 -->
      <el-dialog
          v-model="dialogVisible"
          :title="formTitle"
          width="600px"
      >
        <el-form
            ref="singerFormRef"
            :model="singerForm"
            :rules="singerRules"
            label-position="top"
        >
          <el-form-item label="歌手名称" prop="name">
            <el-input v-model="singerForm.name" placeholder="请输入歌手名称"/>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="singerForm.gender">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="2">女</el-radio>
              <el-radio :label="0">组合</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="国家/地区" prop="country">
            <el-input v-model="singerForm.country" placeholder="请输入国家/地区"/>
          </el-form-item>
          <el-form-item label="歌手简介" prop="description">
            <el-input
                v-model="singerForm.description"
                type="textarea"
                :rows="3"
                placeholder="请输入歌手简介"
            />
          </el-form-item>
          <el-form-item label="头像图片" prop="coverImage">
            <el-upload
                class="upload-demo"
                action=""
                :on-success="handleAvatarUpload"
                :before-upload="beforeAvatarUpload"
                :auto-upload="false"
                ref="avatarUploadRef"
                :http-request="uploadAvatar"
                :on-change="handleAvatarChange"
            >
              <template #trigger>
                <el-button type="primary">
                  <el-icon>
                    <Upload/>
                  </el-icon>
                  上传头像图片
                </el-button>
              </template>
              <template #tip>
                <div class="el-upload__tip">
                  支持上传JPG、PNG格式的图片，建议尺寸300x300
                </div>
              </template>
            </el-upload>
            <el-image
                v-if="singerForm.coverImage"
                :src="`http://localhost:8080${singerForm.coverImage}`"
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

// TODO: API替换指南
// 1. 创建 src/api/singer.js 文件，导出以下方法：
//    - getSingerList(params) - 获取歌手列表
//    - addSinger(data) - 添加歌手  
//    - updateSinger(data) - 更新歌手
//    - deleteSinger(id) - 删除歌手
// 2. 替换导入语句：
//    import { getSingerList, addSinger, updateSinger, deleteSinger } from '@/api/singer'
// 3. 替换方法调用：
//    - mockService.singer.getList(params) → getSingerList(params)
//    - mockService.singer.add(data) → addSinger(data)
//    - mockService.singer.update(data) → updateSinger(data)  
//    - mockService.singer.delete(id) → deleteSinger(id)

// 歌手列表数据
// 数据结构说明:
// singerList: [{ id, name, gender, country, description, coverImage, musicCount, createdAt }]
const singerList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索和筛选
// searchQuery: 歌手名称关键词
// genderFilter: 性别筛选 (0/1/2)
const searchQuery = ref('')
const genderFilter = ref('')

// 对话框
const dialogVisible = ref(false)
const singerFormRef = ref()
// 歌手表单数据
// 添加时: { name, gender, country, description, coverImage } (id为空)
// 编辑时: { id, name, gender, country, description, coverImage }
const singerForm = reactive({
  id: '',
  name: '',
  gender: 1,
  country: '',
  description: '',
  coverImage: ''
})

// 表单验证规则
// TODO: 后端数据验证要求
// 歌手名称: 必填, 长度1-100字符
// 性别: 必填, 可选值: 0(组合), 1(男), 2(女)
// 国家/地区: 可选, 建议长度2-50字符
// 歌手简介: 可选, 建议长度不超过500字符
// 头像URL: 可选, 必须是有效的图片URL地址
const singerRules = {
  name: [
    {required: true, message: '请输入歌手名称', trigger: 'blur'},
    {min: 1, max: 100, message: '歌手名称长度在 1 到 100 个字符', trigger: 'blur'}
  ],
  gender: [
    {required: true, message: '请选择性别', trigger: 'change'}
  ]
}

// 上传组件引用
const avatarUploadRef = ref()

// 待上传的头像文件
const pendingAvatarFile = ref(null)

// 选中的歌手
const selectedSingers = ref([])

// 表单标题
const formTitle = computed(() => {
  return singerForm.id ? '编辑歌手' : '添加歌手'
})

// 获取歌手列表
// TODO: 替换为真实API - GET /api/v1/artist/getAll?pageNum=1&pageSize=10&keyword=xxx
// 期望响应: { code: '200', message: '请求成功', data: { total: 1, list: [...] } }
// list数组项结构: {
//   id: 3,                    // 歌手ID
//   name: 'Ed Sheeran',       // 歌手名称
//   country: 'UK',            // 国家/地区
//   gender: 1,                // 性别: 0(组合), 1(男), 2(女)
//   description: 'English singer-songwriter', // 歌手简介
//   coverImage: '/uploads/avatars/xxx.jpg', // 头像URL
//   musicCount: 18,           // 歌曲数量
//   createdAt: '2025-10-22 17:01:05'  // 创建时间
// }
const getSingerList = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value,
      gender: genderFilter.value
    }
    const res = await request.get('artist/getAll', {params})
    if (res.code === '200') {
      singerList.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取歌手列表失败:', error)
    ElMessage.error('获取歌手列表失败')
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  getSingerList()
}

// 重置
const handleReset = () => {
  searchQuery.value = ''
  genderFilter.value = ''
  currentPage.value = 1
  getSingerList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pageSize.value = size
  getSingerList()
}

// 页码改变
const handleCurrentChange = (page) => {
  currentPage.value = page
  getSingerList()
}

// 选择歌手
const handleSelectionChange = (selection) => {
  selectedSingers.value = selection
}

// 添加歌手
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑歌手
const handleEdit = (row) => {
  Object.assign(singerForm, row)
  dialogVisible.value = true
}

// 删除歌手
// TODO: 替换为真实API - DELETE /admin/singers/{id}
// 期望响应: { code: '200', message: '删除成功' }
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该歌手吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete('artist/delete/' + row.id)
      if (res.code === '200') {
        ElMessage.success('删除成功')
        getSingerList()
      }
    } catch (error) {
      console.error('删除歌手失败:', error)
      ElMessage.error('删除歌手失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
// TODO: 替换为真实API
// 添加歌手: POST /api/v1/artist
// 请求体: { name, gender, country, description, coverImage }
// 期望响应: { code: '200', data: { id, name, gender, country, description, coverImage, musicCount, createdAt } }
//
// 更新歌手: PUT /api/v1/artist/{id}  
// 请求体: { id, name, gender, country, description }
// 注意: 编辑时如果有新头像，先上传头像再更新歌手信息
// 期望响应: { code: '200', data: { id, name, gender, country, description, coverImage, musicCount, createdAt } }
const handleSubmit = async () => {
  if (!singerFormRef.value) return
  try {
    await singerFormRef.value.validate()

    let res
    let newArtistId = null

    if (singerForm.id) {
      // 编辑模式
      // 如果是编辑模式且有待上传的头像文件，先上传头像
      if (pendingAvatarFile.value) {
        const formData = new FormData()
        formData.append('file', pendingAvatarFile.value)

        const uploadRes = await request.post(`upload/artistCover/${singerForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (uploadRes.code === '200') {
          // 上传成功后更新歌手表单中的头像路径
          singerForm.coverImage = uploadRes.data
          ElMessage.success('头像上传成功')
        } else {
          ElMessage.error('头像上传失败')
          return // 头像上传失败，不继续更新歌手信息
        }
      }

      const {coverImage, ...editData} = singerForm
      res = await request.put('artist/update', editData)
    } else {
      // 添加模式 - 先创建歌手
      const {coverImage, ...editData} = singerForm
      res = await request.post('artist/add', editData)
      
      if (res.code === '200' && res.data && res.data.id) {
        newArtistId = res.data.id
        singerForm.id = newArtistId
      }
    }

    if (res.code === '200') {
      // 如果是添加模式且有待上传头像，上传头像
      if (newArtistId && pendingAvatarFile.value) {
        try {
          const formData = new FormData()
          formData.append('file', pendingAvatarFile.value)

          const uploadRes = await request.post(`upload/artistCover/${newArtistId}`, formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })

          if (uploadRes.code === '200') {
            ElMessage.success('头像上传成功')
          } else {
            ElMessage.warning('歌手创建成功，但头像上传失败')
          }
        } catch (uploadError) {
          console.error('头像上传失败:', uploadError)
          ElMessage.warning('歌手创建成功，但头像上传失败')
        }
      }

      ElMessage.success(singerForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      // 清空待上传文件
      pendingAvatarFile.value = null
      getSingerList()
    }
  } catch (error) {
    console.error(singerForm.id ? '编辑歌手失败:' : '添加歌手失败:', error)
    ElMessage.error(singerForm.id ? '编辑歌手失败' : '添加歌手失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(singerForm, {
    id: '',
    name: '',
    gender: 1,
    country: '',
    description: '',
    coverImage: ''
  })
  // 清空待上传文件
  pendingAvatarFile.value = null
  if (singerFormRef.value) {
    singerFormRef.value.resetFields()
  }
}

// 头像上传前处理
// 支持的文件格式: JPG, PNG
// 最大文件大小: 5MB
// 建议图片尺寸: 300x300 像素
const beforeAvatarUpload = (file) => {
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

// 头像上传成功处理
// 实际API: POST /api/v1/artistCover/{id}
// 请求头: Content-Type: multipart/form-data  
// 请求体: FormData 包含 file 文件
// 期望响应: {
//   "code": "200",
//   "message": "上传成功",
//   "data": "/cover/artistCover/文件名"
// }
const handleAvatarUpload = (response) => {
  if (response.code === '200') {
    singerForm.coverImage = response.data
    ElMessage.success('头像上传成功')
  }
}

// 头像文件变化处理 - 保存待上传文件但不立即上传
const handleAvatarChange = (uploadFile) => {
  pendingAvatarFile.value = uploadFile.raw
}

const uploadAvatar = async (options) => {
  const {file, onSuccess, onError} = options
  try {
    const formData = new FormData()
    formData.append('file', file)

    const res = await request.post(`upload/artistCover/${singerForm.id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.code === '200') {
      onSuccess(res)
    } else {
      onError(new Error(res.message || '上传失败'))
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
    onError(error)
  }
}

onMounted(() => {
  getSingerList()
})
</script>

<style scoped lang="scss">
.singer-management-container {
  padding: 0;
}

.singer-management-card {
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

.singer-name {
  display: flex;
  align-items: center;
  gap: 10px;
}

.singer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>