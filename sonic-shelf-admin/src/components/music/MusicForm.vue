<template>
  <el-dialog
      v-model="dialogVisible"
      :title="formTitle"
      width="600px"
  >
    <el-form
        ref="musicFormRef"
        :model="musicForm"
        :rules="musicRules"
        label-position="top"
    >
      <el-form-item label="音乐标题" prop="title">
        <el-input v-model="musicForm.title" placeholder="请输入音乐标题"/>
      </el-form-item>
      <el-form-item label="歌手" prop="artistId">
        <el-select v-model="musicForm.artistId" placeholder="请选择歌手" filterable clearable style="width: 100%">
          <el-option
              v-for="artist in artists"
              :key="artist.id"
              :label="artist.name"
              :value="artist.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="专辑" prop="albumId">
        <el-select v-model="musicForm.albumId" placeholder="请选择专辑" filterable clearable style="width: 100%">
          <el-option
              v-for="album in albums"
              :key="album.id"
              :label="album.title"
              :value="album.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="时长(秒)" prop="duration">
        <el-input-number v-model="musicForm.duration" :min="0" placeholder="请输入时长"/>
      </el-form-item>
      <!-- 分类选择 - 使用拆分出来的CategorySelector组件 -->
      <el-form-item label="分类" prop="categoryIds">
        <CategorySelector
          v-model:selectedTagIds="selectedFormTagIds"
          v-model:selectedTags="selectedFormTags"
          placeholder="选择分类标签"
        />
      </el-form-item>
      <el-form-item label="音乐文件" prop="fileUrl">
        <el-upload
            class="upload-demo"
            action=""
            :before-upload="beforeFileUpload"
            :auto-upload="false"
            ref="fileUploadRef"
            :on-change="handleMusicFileChange"
            :on-remove="handleMusicFileRemove"
            :limit="1"
            accept=".mp3,.wav,.flac"
        >
          <template #trigger>
            <el-button type="primary">
              <el-icon>
                <Upload/>
              </el-icon>
              {{ musicForm.id ? '更换音乐文件' : '上传音乐文件' }}
            </el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              支持上传MP3、WAV、FLAC等格式的音乐文件，文件大小不超过50MB<br>
              <span style="color: #67C23A;">* 系统会自动从音乐文件中提取标题、歌手、专辑和封面信息</span>
            </div>
          </template>
        </el-upload>
        <div v-if="musicForm.fileUrl" style="margin-top: 10px;">
          <el-tag type="info">当前文件: {{ getFileNameFromUrl(musicForm.fileUrl) }}</el-tag>
        </div>
      </el-form-item>
      <el-form-item label="封面图片" prop="coverImage">
        <el-upload
            class="upload-demo"
            action=""
            :before-upload="beforeCoverUpload"
            :auto-upload="false"
            ref="coverUploadRef"
            :on-change="handleCoverFileChange"
            :on-remove="handleCoverFileRemove"
            :limit="1"
        >
          <template #trigger>
            <el-button type="primary">
              <el-icon>
                <Upload/>
              </el-icon>
              {{ musicForm.coverImage ? '更换封面图片' : '上传封面图片' }}
            </el-button>
          </template>
          <template #tip>
            <div class="el-upload__tip">
              支持上传JPG、PNG格式的图片，建议尺寸300x300，文件大小不超过5MB
            </div>
          </template>
        </el-upload>
        <el-image
            v-if="musicForm.coverImage"
            :src="musicForm.coverImage.startsWith('data:') ? musicForm.coverImage : `http://localhost:8080${musicForm.coverImage}`"
            :fit="'cover'"
            style="width: 100px; height: 100px; margin-top: 10px"
        />
      </el-form-item>
      <el-form-item label="歌词" prop="lyrics">
        <el-input
            v-model="musicForm.lyrics"
            type="textarea"
            :rows="8"
            placeholder="请输入歌词"
            resize="vertical"
            show-word-limit
            :maxlength="10000"
        />
        <div class="lyrics-tip">
          支持输入完整歌词，系统会自动处理换行和格式
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, reactive, computed} from 'vue'
import {ElMessage} from 'element-plus'
import {Upload} from '@element-plus/icons-vue'
import request from "@/utils/request.js";
import CategorySelector from "@/components/common/CategorySelector.vue";

// Props定义
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  formData: {
    type: Object,
    default: () => ({
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
  },
  albums: {
    type: Array,
    default: () => []
  },
  artists: {
    type: Array,
    default: () => []
  }
})

// Emits定义
const emit = defineEmits(['update:visible', 'success', 'cancel'])

// 对话框显示状态
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 表单引用
const musicFormRef = ref()

// 表单数据
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

// 表单验证规则
const musicRules = {
  title: [
    {required: true, message: '请输入音乐标题', trigger: 'blur'},
    {min: 1, max: 100, message: '音乐标题长度在 1 到 100 个字符', trigger: 'blur'}
  ],
  artistId: [
    {required: true, message: '请选择歌手', trigger: 'change'}
  ],
  albumId: [
    {required: true, message: '请选择专辑', trigger: 'change'}
  ],
  duration: [
    {required: true, message: '请输入时长', trigger: 'blur'},
    {type: 'number', min: 1, message: '时长必须大于0', trigger: 'blur'}
  ],
  categoryIds: [
    {
      validator: (_, value, callback) => {
        if (!selectedFormTagIds.value || selectedFormTagIds.value.length === 0) {
          callback(new Error('请至少选择一个分类'))
        } else {
          callback()
        }
      }, trigger: 'change'
    }
  ]
}

// 上传组件引用
const fileUploadRef = ref()
const coverUploadRef = ref()

// 待上传的文件
const pendingMusicFile = ref(null)
const pendingCoverFile = ref(null)

// 表单分类选择相关数据
const selectedFormTags = ref([]) // 选中的表单标签对象数组
const selectedFormTagIds = ref([]) // 选中的表单标签ID数组

// 表单标题
const formTitle = computed(() => {
  return musicForm.id ? '编辑音乐' : '添加音乐'
})

// 从URL获取文件名
const getFileNameFromUrl = (url) => {
  if (!url) return ''
  const parts = url.split('/')
  return parts[parts.length - 1] || '未知文件'
}



// 提交表单
const handleSubmit = async () => {
  if (!musicFormRef.value) return
  try {
    await musicFormRef.value.validate()

    let res
    let newMusicId = null

    // 准备请求数据，使用selectedFormTagIds
    const requestData = {
      ...musicForm,
      categoryIds: selectedFormTagIds.value
    }

    if (musicForm.id) {
      // 编辑模式
      // 如果有待上传的音乐文件，先上传音乐文件
      if (pendingMusicFile.value) {
        const formData = new FormData()
        formData.append('file', pendingMusicFile.value)

        const uploadRes = await request.post(`upload/musicFile/${musicForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (uploadRes.code === '200') {
          // 上传成功后更新音乐表单中的文件路径
          requestData.fileUrl = uploadRes.data
          ElMessage.success('音乐文件上传成功')
        } else {
          ElMessage.error('音乐文件上传失败')
          return // 音乐文件上传失败，不继续更新音乐信息
        }
      }

      // 如果有待上传的封面文件，先上传封面
      if (pendingCoverFile.value) {
        const formData = new FormData()
        formData.append('file', pendingCoverFile.value)

        const uploadRes = await request.post(`upload/musicCover/${musicForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })

        if (uploadRes.code === '200') {
          // 上传成功后更新音乐表单中的封面路径
          requestData.coverImage = uploadRes.data
          ElMessage.success('封面图片上传成功')
        } else {
          ElMessage.error('封面图片上传失败')
          return // 封面图片上传失败，不继续更新音乐信息
        }
      }

      const {fileUrl, coverImage} = requestData
      res = await request.put(`musics/update`, requestData)
    } else {
      // 添加模式 - 先创建音乐
      const {fileUrl, coverImage} = requestData
      res = await request.post('musics/add', requestData)

      if (res.code === '200' && res.data && res.data.id) {
        newMusicId = res.data.id
        musicForm.id = newMusicId
      }
    }

    if (res.code === '200') {
      // 如果是添加模式且有文件需要上传
      if (newMusicId) {
        // 上传音乐文件
        if (pendingMusicFile.value) {
          try {
            const formData = new FormData()
            formData.append('file', pendingMusicFile.value)

            const uploadRes = await request.post(`upload/musicFile/${newMusicId}`, formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            })

            if (uploadRes.code === '200') {
              ElMessage.success('音乐文件上传成功')
            } else {
              ElMessage.warning('音乐创建成功，但音乐文件上传失败')
            }
          } catch (uploadError) {
            console.error('音乐文件上传失败:', uploadError)
            ElMessage.warning('音乐创建成功，但音乐文件上传失败')
          }
        }

        // 上传封面图片
        if (pendingCoverFile.value) {
          try {
            const formData = new FormData()
            formData.append('file', pendingCoverFile.value)

            const uploadRes = await request.post(`upload/musicCover/${newMusicId}`, formData, {
              headers: {
                'Content-Type': 'multipart/form-data'
              }
            })

            if (uploadRes.code === '200') {
              ElMessage.success('封面图片上传成功')
            } else {
              ElMessage.warning('音乐创建成功，但封面图片上传失败')
            }
          } catch (uploadError) {
            console.error('封面图片上传失败:', uploadError)
            ElMessage.warning('音乐创建成功，但封面图片上传失败')
          }
        }
      }

      ElMessage.success(musicForm.id ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      // 清空待上传文件
      pendingMusicFile.value = null
      pendingCoverFile.value = null
      emit('success')
    }
  } catch (error) {
    console.error(musicForm.id ? '编辑音乐失败:' : '添加音乐失败:', error)
    ElMessage.error(musicForm.id ? '编辑音乐失败' : '添加音乐失败')
  }
}

// 取消操作
const handleCancel = () => {
  dialogVisible.value = false
  emit('cancel')
}

// 重置表单
const resetForm = () => {
  // 先调用组件的resetFields()重置表单状态
  if (musicFormRef.value) {
    musicFormRef.value.resetFields()
  }
  
  // 手动重置表单数据
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
  
  // 清空待上传文件
  pendingMusicFile.value = null
  pendingCoverFile.value = null
  
  // 清空文件上传组件的文件列表
  fileUploadRef.value?.clearFiles()
  coverUploadRef.value?.clearFiles()
  
  // 重置分类选择
  selectedFormTagIds.value = []
  selectedFormTags.value = []
}

// 文件上传前处理
const beforeFileUpload = (file) => {
  const isMP3 = file.type === 'audio/mpeg'
  const isWAV = file.type === 'audio/wav'
  const isFLAC = file.type === 'audio/flac'
  const isLt50M = file.size / 1024 / 1024 < 50

  if (!isMP3 && !isWAV && !isFLAC) {
    ElMessage.error('只能上传MP3、WAV或FLAC格式的音乐文件')
  }
  if (!isLt50M) {
    ElMessage.error('音乐文件大小不能超过50MB')
  }
  return (isMP3 || isWAV || isFLAC) && isLt50M
}

// 封面上传前处理
const beforeCoverUpload = (file) => {
  // 如果是从音乐文件中提取的封面，允许通过
  if (file.name && file.name.startsWith('cover_')) {
    return true;
  }

  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isJPG && !isPNG) {
    ElMessage.error('只能上传JPG或PNG格式的图片');
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB');
  }
  return (isJPG || isPNG) && isLt5M;
}

// 音乐文件变化处理
const handleMusicFileChange = (uploadFile) => {
  const file = uploadFile.raw
  pendingMusicFile.value = file

  // 动态加载jsmediatags库
  if (typeof jsmediatags === 'undefined') {
    const script = document.createElement('script');
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/jsmediatags/3.9.5/jsmediatags.min.js';
    script.onload = () => {
      extractMusicMetadata(file);
    };
    document.body.appendChild(script);
  } else {
    extractMusicMetadata(file);
  }
}

// 提取音乐元数据的函数
const extractMusicMetadata = (file) => {
  // 提取音频文件的元数据和封面
  if (file && (file.type === 'audio/mpeg' || file.type === 'audio/wav' || file.type === 'audio/flac')) {
    jsmediatags.read(file, {
      onSuccess: function (tag) {
        // 提取标题
        if (tag.tags.title && !musicForm.title) {
          musicForm.title = tag.tags.title;
        }

        // 尝试从文件名获取标题（如果标签中没有）
        if (!musicForm.title) {
          const fileName = file.name;
          musicForm.title = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        // 提取歌手信息（尝试匹配现有歌手）
        if (tag.tags.artist && props.artists.length > 0) {
          const artistName = tag.tags.artist;
          const matchedArtist = props.artists.find(artist =>
              artist.name.toLowerCase() === artistName.toLowerCase()
          );
          if (matchedArtist && !musicForm.artistId) {
            musicForm.artistId = matchedArtist.id;
          }
        }

        // 提取专辑信息（尝试匹配现有专辑）
        if (tag.tags.album && props.albums.length > 0) {
          const albumName = tag.tags.album;
          const matchedAlbum = props.albums.find(album =>
              album.title.toLowerCase() === albumName.toLowerCase()
          );
          if (matchedAlbum && !musicForm.albumId) {
            musicForm.albumId = matchedAlbum.id;
          }
        }



        // 提取封面图片
        if (tag.tags.picture) {
          const {data, format} = tag.tags.picture;
          let base64String = '';
          for (let i = 0; i < data.length; i++) {
            base64String += String.fromCharCode(data[i]);
          }
          const coverBase64 = `data:${format};base64,${window.btoa(base64String)}`;

          // 将封面图片转换为Blob对象并设置为待上传文件
          fetch(coverBase64)
              .then(res => res.blob())
              .then(blob => {
                // 创建一个临时文件名
                const coverFileName = `cover_${Date.now()}.${format.split('/')[1]}`;
                const coverFile = new File([blob], coverFileName, {type: format});
                pendingCoverFile.value = coverFile;

                // 更新表单中的封面图片预览
                // 注意：这里只是预览，实际上传需要等待表单提交时处理
                musicForm.coverImage = coverBase64;
              });
        }

        ElMessage.success('已从音乐文件中提取元数据');
      },
      onError: function (error) {
        console.log('提取音乐元数据失败:', error);
        // 提取失败不影响继续上传
      }
    });

    // 使用Audio API获取音频时长
    const audio = new Audio();
    const fileURL = URL.createObjectURL(file);

    audio.onloadedmetadata = function () {
      // 获取时长（秒）并取整
      const duration = Math.floor(audio.duration);
      if (duration > 0 && !musicForm.duration) {
        musicForm.duration = duration;
      }
      // 释放URL对象
      URL.revokeObjectURL(fileURL);
    };

    audio.onerror = function () {
      console.log('获取音频时长失败');
      // 释放URL对象
      URL.revokeObjectURL(fileURL);
    };

    audio.src = fileURL;
  }
};

// 封面文件变化处理
const handleCoverFileChange = (uploadFile) => {
  pendingCoverFile.value = uploadFile.raw
}

// 音乐文件移除处理
const handleMusicFileRemove = () => {
  pendingMusicFile.value = null
}

// 封面文件移除处理
const handleCoverFileRemove = () => {
  pendingCoverFile.value = null
}

// 监听props变化，更新表单数据
import {watch} from 'vue'

watch(() => props.formData, (newFormData) => {
  Object.assign(musicForm, newFormData)
  
  // 初始化分类选择
  if (newFormData.categories) {
    const categoryIds = newFormData.categories.map(cat => cat.id)
    selectedFormTagIds.value = [...categoryIds]
  } else if (newFormData.categoryIds) {
    selectedFormTagIds.value = [...newFormData.categoryIds]
  } else {
    selectedFormTagIds.value = []
  }
}, {deep: true, immediate: true})

// 监听对话框显示状态变化
watch(dialogVisible, (newVisible) => {
  if (newVisible) {
    // 对话框打开时，确保内部musicForm与props.formData同步
    Object.assign(musicForm, props.formData)
    
    // 初始化分类选择
    if (props.formData.categories) {
      const categoryIds = props.formData.categories.map(cat => cat.id)
      selectedFormTagIds.value = [...categoryIds]
    } else if (props.formData.categoryIds) {
      selectedFormTagIds.value = [...props.formData.categoryIds]
    } else {
      selectedFormTagIds.value = []
    }
  } else {
    // 对话框关闭时重置表单
    resetForm()
  }
})
</script>

<style scoped lang="scss">
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.lyrics-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  font-style: italic;
}
</style>