<script setup>

import AddMusic from "@/components/form/AddMusic.vue";
import {ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import { getFullUrl } from '@/utils/urlConfig';

const showAddForm = ref(false);
const coverImage = ref('');
const content = ref('');
const type = ref('');
const id = ref(0);
const title = ref('')
const name = ref('')
const uploadedImages = ref([]);


const openFilePicker = () => {

  if (uploadedImages.value.length >= 9) {
    ElMessage.warning(`最多只能上传9张图片`);
    return;
  }

  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = 'image/jpeg,image/jpg,image/png,image/gif,image/webp'; // 限制图片格式
  fileInput.multiple = true;

  fileInput.onchange = (e) => {
    const files = e.target.files;
    if (files.length > 0) {
      handleImageUpload(files);
    }
  };

  fileInput.click();
};

// 新增：处理图片上传
const handleImageUpload = (files) => {
  // 验证文件类型和大小
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
  const maxSize = 10 * 1024 * 1024; // 10MB

  const remainingSlots = 9 - uploadedImages.value.length;
  if (remainingSlots <= 0) {
    ElMessage.warning(`最多只能上传9张图片`);
    return;
  }

  const filesToProcess = Array.from(files).slice(0, remainingSlots);

  for (let i = 0; i < filesToProcess.length; i++) {

    const file = filesToProcess[i];

    // 检查文件类型
    if (!allowedTypes.includes(file.type)) {
      ElMessage.warning(`文件 ${file.name} 格式不支持，请选择 JPEG、PNG、GIF 或 WebP 格式的图片`);
      continue;
    }

    // 检查文件大小
    if (file.size > maxSize) {
      ElMessage.warning(`文件 ${file.name} 大小超过 10MB 限制`);
      continue;
    }

    // 创建预览
    const reader = new FileReader();
    reader.onload = (e) => {
      uploadedImages.value.push({
        id: Date.now() + i,
        url: e.target.result,
        file: file,
        name: file.name
      });
    };
    reader.readAsDataURL(file);
  }
};

// 新增：删除已上传的图片
const removeImage = (index) => {
  uploadedImages.value.splice(index, 1);
};


const emit = defineEmits(["close"]);

const close = () => {
  emit("close")
}

const selected = (selectedId, selectedType, selectedCoverImage, selectedTitle, selectedName) => {
  id.value = selectedId;
  type.value = selectedType;
  coverImage.value = selectedCoverImage;
  title.value = selectedTitle;
  name.value = selectedName;
}

const publish = async () => {
  if (type.value !== 'music' && type.value !== 'playlist') {
    type.value = 'text';
  }
  let dynamic = {
    targetId: id.value,
    type: type.value,
    content: content.value,
  }
  const res = await request.post('dynamic/add-dynamic', dynamic)
  if (res.code === '200') {
    ElMessage.success(res.message)
  } else {
    ElMessage.warning(res.message)
  }

  if (uploadedImages.value.length > 0) {
    try {
      const formData = new FormData();
      uploadedImages.value.forEach((img, index) => {
        formData.append('files', img.file);
      });

      if (!res.data) {
        ElMessage.warning('获取动态ID失败，无法上传图片');
        return;
      }
      const uploadRes = await request.post(`upload/dynamicImage/${res.data}`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      if (uploadRes.code !== '200') {
        ElMessage.warning('图片上传失败');
      }
    } catch (error) {
      ElMessage.warning('图片上传出错');
    }
  }

  emit('close');
}


</script>

<template>
  <div class="publish-form">
    <img @click="close" id="close" src="/icons/status/close.svg" alt="">
    <span style="font-size: 24px;font-weight: bold;color: #333333;margin-bottom: 20px">发笔记</span>
    <div class="content-wrapper">
      <textarea v-model="content" placeholder="说点什么吧"/>
      <img @click="openFilePicker" src="/icons/ui/image.svg" width="20px" style="cursor: pointer" alt="">
      <hr style="height: 1px;border: none;background: #e4e7ed;margin: 10px 0;">
      <div @click="showAddForm=true" class="add-content">
        <div class="add-button">
          <img v-if="coverImage===''" src="/icons/ui/addMusic.svg" height="20px" alt="">
          <img v-else :src="getFullUrl(coverImage)" style="height: 38px;width: 38px;object-fit: cover;border-radius: 5px"
               alt="">
        </div>
        <span v-if="type==='' && id===0">给笔记配上音乐</span>
        <div v-if="title!=='' && name!==''" class="info" style="display: flex;flex-direction: column;">
          <span>{{ title }}</span>
          <span>{{ name }}</span>
        </div>
      </div>
    </div>
    <div v-if="uploadedImages.length>0" class="upload-list">
      <div v-for="(item,index) in uploadedImages" class="image">
        <div @click="removeImage(index)" class="delete">
          <img src="/icons/ui/close.svg" alt="">
        </div>
        <img :src="item.url" style="width: 55px;height: 55px;object-fit: cover;border-radius: 5px"
             alt="">
      </div>
      <div @click="openFilePicker" v-if="uploadedImages.length<9" class="add-more"><img src="/icons/ui/follow.svg"
                                                                                        width="15px" alt=""></div>
    </div>
    <div @click="publish" class="publish-button">
      发布
    </div>
  </div>
  <AddMusic
      v-if="showAddForm"
      @back="showAddForm = false"
      @selected="selected"
      @close="close"
  />
</template>

<style scoped>
.publish-form {
  position: fixed;
  padding: 40px 20px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 580px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 2px 2px 10px #eaeaea;
}

#close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 18px;
  cursor: pointer;
}

.content-wrapper {
  margin-bottom: 20px;
  padding: 20px;
  height: 220px;
  width: 100%;
  background: #f8f9f9;
  border-radius: 10px;

  textarea {
    padding: 0;
    width: 100%;
    height: 95px;
    border: none;
    resize: none;
    color: #999999;
    background-color: transparent;
    outline: none;
    overflow-y: scroll;
  }
}

.add-content {
  user-select: none;
  display: flex;
  flex-direction: row;
  align-items: center;
  cursor: pointer;
}

.add-button {
  margin-right: 10px;
  height: 38px;
  width: 38px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #ebedee;
  border: 1px #e4e8ec solid;
  border-radius: 10px;
}

.upload-list {
  margin-bottom: 20px;
  width: 100%;
  display: grid;
  grid-template-columns: repeat(9, 1fr);
  gap: 5px;
}

.image {
  position: relative;

  .delete {
    position: absolute;
    top: 3px;
    right: 3px;
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    background: #000000;
    opacity: 0;
    cursor: pointer;

    img {
      width: 10px;
      height: 10px;
    }
  }
}

.image:hover .delete {
  opacity: 0.5;
}

.add-more {
  width: 54px;
  height: 54px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e4e8ec;
  border-radius: 5px;
  cursor: pointer;
}

.publish-button {
  user-select: none;
  width: 180px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  color: #ffffff;
  background: linear-gradient(to right, #fc3b5b, #fc3d49);
  border-radius: 20px;
  cursor: pointer;
}
</style>