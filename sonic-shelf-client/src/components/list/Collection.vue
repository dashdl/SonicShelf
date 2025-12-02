<script setup>
import { getFullUrl } from '@/utils/urlConfig';

const props = defineProps({
  userPlaylist: {
    type: Array
  }
})

const emit = defineEmits(['selectedPlaylist', 'closeCollection'])

const handleClick = (id) => {
  emit('selectedPlaylist', id)
}
</script>

<template>
  <div class="collection-container">
    <img @click="emit('closeCollection')" id="close" src="/icons/status/close.svg" alt="">
    <div class="title">收藏到歌单</div>
    <div class="selection-container">
      <span>默认排序</span>
      <hr>
      <span>常用排序</span>
    </div>
    <div class="collection-playlist">
      <div class="playlist-item">
        <div class="cover" style="border-radius: 10px;overflow: hidden;margin-right: 20px">
          <img src="/images/default/cover.png" style="height: 50px;width: 50px;object-fit: cover" alt="">
        </div>
        <div class="text" style="display: flex;flex-direction: column;justify-content: center;">
          <span style="font-size: 17px;color: #7b818f">创建歌单</span>
        </div>
      </div>
      <div v-for="item in props.userPlaylist" @click="handleClick(item.id)" class="playlist-item">
        <div class="cover">
          <img :src="item.coverImage ? getFullUrl(item.coverImage) : '/images/default/cover.png'"
               style="height: 50px;width: 50px;object-fit: cover" alt="">
        </div>
        <div class="text">
          <span id="title">{{
              item.title
            }}</span>
          <span style="color: #a9adb7">{{ item.musicCount }}首</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
span {
  line-height: 1;
}

.collection-container {
  user-select: none;
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  height: 530px;
  width: 440px;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 20px;
  transition: all .3s ease-in-out;
  z-index: 11;
}

#close {
  position: absolute;
  top: 28px;
  right: 28px;
  height: 20px;
}

#close:hover {
  cursor: pointer;
}

.title {
  margin-top: 35px;
  font-size: 20px;
  font-weight: bold;
  color: #444444;
  text-align: center;
}

.selection-container {
  margin-top: 35px;
  margin-bottom: 20px;
  padding: 0 28px;
  display: flex;
}

hr {
  margin: 0 15px;
  border: none;
  width: 1px;
  background: #e4e8ec;
}

.collection-playlist {
  display: flex;
  flex-direction: column;
  overflow: auto;
}

.playlist-item {
  padding: 8px 28px;
  display: flex;
  flex-direction: row;
}

.playlist-item:hover {
  background-color: #f2f3f4;
  cursor: pointer;
}

.cover {
  border-radius: 10px;
  overflow: hidden;
  margin-right: 20px;
  width: 50px;
}

.text {
  width: calc(100% - 70px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

#title {
  width: 100%;
  font-size: 17px;
  margin-bottom: 5px;
  color: #7b818f;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>