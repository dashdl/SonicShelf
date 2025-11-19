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
              <span class="singer-title">{{ scope.row.name }}</span>
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
                :on-remove="handleAvatarRemove"
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
                  请上传JPG、PNG格式的图片
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
import {ElMessage, ElMessageBox} from 'element-plus'
import request from "@/utils/request.js";

const singerList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

const searchQuery = ref('')
const genderFilter = ref('')

const dialogVisible = ref(false)
const singerFormRef = ref()
const singerForm = reactive({
  id: '',
  name: '',
  gender: 1,
  country: '',
  description: '',
  coverImage: ''
})

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

// 头像文件移除处理
const handleAvatarRemove = () => {
  pendingAvatarFile.value = null
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
  flex-shrink: 0;
}

.singer-title {
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
</style>