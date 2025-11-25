<script setup>
import {onMounted, reactive, watch, ref, onUnmounted} from 'vue';
import request from '@/utils/request.js';
import {ElMessage} from 'element-plus';
import {useRoute} from "vue-router";

const route = useRoute();

let categories = reactive([]);
const userSelected = ref(1);
const tags = reactive([])
const tagForm = ref(false)
let selectedTags = reactive([])
let confirmTags = reactive([])

const formData = reactive({
  id: '',
  title: '',
  description: '',
  coverImage: '',
  tags: []
});

const avatarPreview = ref('');
const fileInput = ref(null);
let selectedFile = null;

const changeTags = (parentId) => {
  request.get('categories', {
    params: {
      parentId: parentId,
    }
  }).then((res) => {
    if (res.code === '200') {
      tags.length = 0; // 清空数组
      tags.push(...res.data); // 添加新数据
      console.log(tags)
    }
  })
  userSelected.value = parentId;
}

const handleUserSelect = (id) => {
  if (!selectedTags.includes(id)) {
    selectedTags.push(id);
  } else {
    const index = selectedTags.indexOf(id);
    if (index > -1) {
      selectedTags.splice(index, 1); // 使用splice删除
    }
  }
  if (selectedTags.length > 0) {
    request.get('categories', {
      params: {
        tags: selectedTags.join(',')
      }
    }).then((res) => {
      confirmTags.length = 0;
      confirmTags.push(...res.data);
    })
  } else {
    confirmTags.length = 0;
  }
}
const deleteTag = (id) => {
  const index = selectedTags.indexOf(id);
  if (index > -1) {
    selectedTags.splice(index, 1); // 使用splice删除
  }
  if (selectedTags.length > 0) {
    request.get('categories', {
      params: {
        tags: selectedTags.join(',')
      }
    }).then((res) => {
      confirmTags.length = 0;
      confirmTags.push(...res.data);
    })
  } else {
    confirmTags.length = 0;
  }
}

const triggerFileUpload = () => {
  fileInput.value.click();
};

const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (file) {
    // 验证文件类型和大小
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
    const maxSize = 2 * 1024 * 1024; // 2MB

    if (!validTypes.includes(file.type)) {
      ElMessage.warning('只支持JPG、PNG、GIF格式的图片');
      return;
    }

    if (file.size > maxSize) {
      ElMessage.warning('图片大小不能超过2MB');
      return;
    }
    selectedFile = file;
    if (avatarPreview.value) {
      URL.revokeObjectURL(avatarPreview.value); // 清理之前的预览以避免内存泄漏
    }
    avatarPreview.value = URL.createObjectURL(file);
  }
};

onMounted(async () => {

  const currentId = route.params.id;
  await request.get('playlists/' + currentId).then(res => {
    Object.assign(formData, res.data)
    if (formData.tags && Array.isArray(formData.tags)) {
      selectedTags = [...formData.tags];
    } else {
      selectedTags = [];
    }
  })

  if (selectedTags.length > 0) {
    request.get('categories', {
      params: {
        tags: selectedTags
      }
    }).then((res) => {
      confirmTags.length = 0;
      confirmTags.push(...res.data);
    })
  }

  const coverPath = formData.coverImage;
  const baseUrl = 'http://localhost:8080';
  const imgElement = document.getElementById('playlistCover');
  if (imgElement) {
    imgElement.src = coverPath ? baseUrl + coverPath : '/images/default/avatar.jpg';
  }

  request.get('categories').then((res) => {
    if (res.code === '200') {
      categories = res.data;
    }
  })

  request.get('categories', {
    params: {
      parentId: 1,
    }
  }).then((res) => {
    if (res.code === '200') {
      tags.length = 0; // 清空数组
      tags.push(...res.data); // 添加新数据

    }
  })
});

const saveProfile = async () => {

  if (selectedFile) {
    try {
      const formForUpload = new FormData();
      formForUpload.append('file', selectedFile);

      const uploadResponse = await request.post('/upload/cover/' + formData.id, formForUpload);

      if (uploadResponse.code === '200') {
        formData.coverImage = uploadResponse.data.coverPath;
      } else {
        ElMessage.error(uploadResponse.message || '头像上传失败');
        return;
      }
    } catch (error) {
      console.error('头像上传错误详情:', error);
      console.error('错误状态:', error.response?.status);
      console.error('错误数据:', error.response?.data);
      ElMessage.error('头像上传失败');
      return;
    }
  }

  formData.tags = selectedTags;

  request.put('/playlists/' + formData.id, formData).then(async res => {
    if (res.code === '200') {
      ElMessage.success('歌单信息保存成功');
      try {
        console.log(res.data)
      } catch (error) {
        ElMessage.error("歌单状态加载失败");
      }
    } else {
      ElMessage.error(res.message || '歌单信息保存失败');
    }
  }).catch(err => {
    ElMessage.error('歌单信息保存失败');
  });

};

const cancelEdit = () => {
  ElMessage.info('已取消编辑');
};

onUnmounted(() => {
  if (avatarPreview.value) {
    URL.revokeObjectURL(avatarPreview.value);
  }
});

</script>

<template>
  <div class="profile-container">
    <div class="profile" style=" margin-right: 45px;">
      <div class="title">
        编辑歌单信息
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px">名称：</label>
        <input v-model="formData.title" placeholder="请输入歌单名称">
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px;">简介：</label>
        <textarea v-model="formData.description" style="height: 230px;outline: none;resize: none;"
                  placeholder="请输入歌单简介"></textarea>
      </div>
      <div class="profile-item">
        <label style="margin-right: 30px;">标签：</label>
        <div @click="tagForm=!tagForm" class="select-button">
          <span v-if="confirmTags.length<=0">选择...</span>
          <div class="selected-grid">
            <div class="selected-t" v-for="item in confirmTags">
              {{ item.name }}
              <img @click="deleteTag(item.id)" src="/icons/status/close.svg" style="height: 10px" alt="">
            </div>
          </div>
          <img :src="!tagForm ? '/icons/status/down.svg' :'/icons/status/up.svg'" style="width: 14px;" alt="">
        </div>
        <div v-if="tagForm" class="categories-form">
          <div class="categories-title">
            <div class="title-content" v-for="item in categories">
              <div v-if="item.id===userSelected" class="select-underline"></div>
              <span @click="changeTags(item.id)" :class="{ 'bold-text': item.id === userSelected }"
                    style="font-size: 18px;">{{ item.name }}</span>
            </div>
          </div>
          <div class="categories-second">
            <div class="grid-list">
              <div @click="handleUserSelect(item.id)" v-for="item in tags"
                   :class="['tag', { 'tag-selected': selectedTags.includes(item.id) }]">{{ item.name }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="profile-item" style="padding-left: 74px;margin-top: 36px">
        <div class="save-button" @click="saveProfile">保存</div>
        <div class="cancel-button" @click="cancelEdit">取消</div>
      </div>
    </div>
    <div class="avatar">
      <img
          id="playlistCover"
          :src="avatarPreview || (formData.coverImage ? 'http://localhost:8080' + formData.coverImage : '/images/default/cover.jpg')"
          alt="点击上传封面"
          @click="triggerFileUpload"
      >
      <input type="file" ref="fileInput" @change="handleFileSelect" accept="image/*" style="display: none;">
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  width: 757px;
  display: flex;
  justify-items: left;
  padding: 15px 0;
  background-color: #f7f9fc;
  user-select: none;
}

.title {
  margin-bottom: 30px;
  font-weight: bold;
  font-size: 30px;
  color: #515a6c;
}

.profile-item {
  position: relative;
  display: flex;
  margin-bottom: 18px;
}

input, textarea {
  padding: 10px;
  height: 40px;
  width: 440px;
  border: 1px solid #e4e8ec;
  border-radius: 10px;
  background-color: #f0f3f6;
}

.save-button, .cancel-button {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 30px;
  height: 50px;
  width: 120px;
  border-radius: 25px;
  font-size: 20px;
  user-select: none;
  cursor: pointer;
}

.save-button {
  color: white;
  background-color: #fc3d49;
}

.cancel-button {
  border: 1px solid #e4e8ec;
  background-color: #f7f9fc;
}

.avatar {
  img {
    margin-top: 75px;
    height: 180px;
    width: 180px;
    border-radius: 15px;
    cursor: pointer;
    object-fit: cover;
    object-position: center;
    //aspect-ratio: 1 / 1;
  }
}

.select-button {
  min-width: 100px;
  min-height: 30px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid #e4e8ec;
  border-radius: 10px;
}

.select-button:hover {
  cursor: pointer;
}

.selected-grid {
  margin-right: 10px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
}

.selected-t {
  position: relative;
  margin: 5px;
  min-width: 60px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f2f3f4;
  border-radius: 15px;
  border: 1px solid #e4e8ec;
}

.selected-t img {
  position: absolute;
  right: 4px;
}

.selected-t img:hover {
  cursor: pointer;
}

.categories-form {
  position: absolute;
  bottom: -380px;
  left: 74px;
  height: 375px;
  width: 575px;
  padding: 20px 25px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 0 15px #7b818f;
  z-index: 1;
}

.categories-title {
  display: flex;
}

.title-content {
  position: relative;
  margin-right: 25px;
}

.select-underline {
  position: absolute;
  height: 3px;
  width: 18px;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background-color: #fc3b56;
  border-radius: 2px;
}

.bold-text {
  font-weight: bold;
}

.title-content span:hover {
  cursor: pointer;
}

.grid-list {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  row-gap: 15px;
}

.tag {
  height: 30px;
  width: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f2f3f4;
  border-radius: 15px;
  border: 1px solid #e4e8ec;
}

.tag:hover {
  background-color: #fff0f0;
  color: #ff3a3a;
  border-color: #ff6f6f;
  cursor: pointer;
}

.tag:active {
  scale: 0.95;
}

.tag-selected {
  background-color: #fff0f0;
  color: #ff3a3a;
  border-color: #ff6f6f;
  cursor: pointer;
}
</style>