<script setup>

import {ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const emit = defineEmits(["close"])

const title = ref('')
const isPublic = ref(true)

const createPlaylist = async () => {

  let playlist = {
    isPublic: isPublic.value,
    title: title.value
  }

  const res = await request.post('playlists/create', playlist)
  if (res.code === '200') {
    ElMessage.success("歌单创建成功")
    emit('close')
  }
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="create-form">
    <img @click="emit('close')" id="close" src="/icons/status/close.svg" alt="">
    <span style="user-select:none;font-size: 24px;font-weight: bold;color: #333333;margin-bottom: 20px;">创建歌单</span>
    <div class="title-wrapper">
      <textarea v-model="title" placeholder="输入歌单标题"/>
    </div>
    <div @click="isPublic=!isPublic" class="public-bar">
      <div :class="[{'active':!isPublic},'click-box']">
        <img v-if="!isPublic" src="/icons/status/hookWhite.svg" width="15px" alt="">
      </div>
      设为隐私歌单
    </div>
    <div @click="createPlaylist" class="create-button">
      发布
    </div>
  </div>
</template>

<style scoped>
.create-form {
  position: fixed;
  padding: 40px 20px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 445px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 2px 2px 10px #eaeaea;
  z-index: 10;
}

#close {
  user-select: none;
  position: absolute;
  top: 20px;
  right: 20px;
  width: 18px;
  cursor: pointer;
}

.title-wrapper {
  margin-bottom: 20px;
  padding: 20px;
  height: 85px;
  width: 100%;
  background: #f8f9f9;
  border-radius: 10px;

  textarea {
    padding: 0;
    width: 100%;
    height: 100%;
    border: none;
    resize: none;
    color: #999999;
    background-color: transparent;
    outline: none;
    overflow-y: scroll;
  }
}

.public-bar {
  user-select: none;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: start;
}

.click-box {
  margin-right: 10px;
  height: 18px;
  width: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: #e4e8ec solid 1px;
  border-radius: 5px;
}

.click-box:hover {
  border: #333333 solid 1px;
  cursor: pointer;
}

.active {
  background: linear-gradient(to right, #fc3b5b, #fc3d49);
}

.create-button {
  user-select: none;
  width: 95px;
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