<script setup>

import {onMounted, reactive, ref} from "vue";
import request from "@/utils/request.js";
import {usePlayerStore} from "@/store/player.js";
import { getFullUrl } from '@/utils/urlConfig';

const emit = defineEmits(["back", "selected"])

const historyMusics = ref([])
const searchMusics = ref([])
const searchPlaylists = ref([])
const keywords = ref('')
const userSelect = reactive({
  page: 1,
});

const search = async () => {
  let res;
  if (userSelect.page === 1) {
    res = await request.get("musics/search", {
      params: {
        pageNum: 1,
        pageSize: 100,
        keyword: keywords.value,
      }
    });
    if (res.code === '200') {
      searchMusics.value = res.data.list;
    }
  } else {
    res = await request.get("playlists/search", {
      params: {
        pageNum: 1,
        pageSize: 100,
        keyword: keywords.value,
      }
    });
    if (res.code === '200') {
      searchPlaylists.value = res.data.list;
    }
  }
}

const selected = (id, type, coverImage, title, name) => {
  emit('selected', id, type, coverImage, title, name)
  emit('back')
}

const loadHistory = async () => {
  const res = await request.get('play-histories')
  if (res.code === '200') {
    historyMusics.value = res.data.list
  }
}
onMounted(() => {
  loadHistory()
})

</script>

<template>
  <div class="add-form">
    <img @click="emit('close')" id="close" src="/icons/status/close.svg" alt="">
    <img @click="emit('back')" id="back" src="/icons/status/left.svg" alt="">
    <span style="align-self: center;font-size: 24px;font-weight: bold;color: #333333;margin-bottom: 20px">发笔记</span>
    <div class="search-bar">
      <img src="/icons/navigation/search.svg" style="height: 20px;margin-right: 10px" alt="">
      <input @keydown="search" v-model="keywords" placeholder="单曲/歌单">
    </div>
    <span v-if="keywords===''" style="margin:10px;">最近播放：</span>
    <div v-if="keywords===''" class="grid-list">
      <div @click="selected(item.id,'music',item.coverImage,item.title,item.artistName)" v-for="item in historyMusics"
           class="music-card">
        <div class="info">
          <div @click="" class="cover">
            <img id="cover" :src="item.coverImage ? getFullUrl(item.coverImage) : '/images/default/cover.png'" alt="">
            <img @click="usePlayerStore().checkMusicId(item.id)" id="playButton" src="/icons/player/play.svg" alt="">
          </div>
          <div class="text">
            <span style="font-size: 16px;color:#333333;">{{ item.title }}</span>
            <span>{{ item.artistName }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="keywords!==''" class="separator-container">
      <div class="separator-content">
        <div class="select-button">
          <div @click="search" class="button">
            <span @click="userSelect.page=1" :class="{ 'bold-text': userSelect.page === 1 }">歌曲</span>
            <div v-if="userSelect.page === 1" class="button-underline"></div>
          </div>
          <div @click="search" class="button">
            <span @click="userSelect.page=2" :class="{ 'bold-text': userSelect.page === 2 }">歌单</span>
            <div v-if="userSelect.page === 2" class="button-underline"></div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="keywords!=='' && userSelect.page===1" class="grid-list">
      <div @click="selected(item.id,'music',item.coverImage,item.title,item.artistName)" v-for="item in searchMusics"
           class="music-card">
        <div class="info">
          <div @click="" class="cover">
            <img id="cover" :src="item.coverImage ? getFullUrl(item.coverImage) : '/images/default/cover.png'" alt="">
            <img @click="usePlayerStore().checkMusicId(item.id)" id="playButton" src="/icons/player/play.svg" alt="">
          </div>
          <div class="text">
            <span style="font-size: 16px;color:#333333;">{{ item.title }}</span>
            <span>{{ item.artistName }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="keywords!=='' && userSelect.page===2" class="grid-list">
      <div @click="selected(item.id,'playlist',item.coverImage,item.title,item.creatorName)"
           v-for="item in searchPlaylists"
           class="music-card">
        <div class="info">
          <div @click="" class="cover">
            <img id="cover" :src="item.coverImage ? getFullUrl(item.coverImage) : '/images/default/cover.png'" alt="">
            <img id="playButton" src="/icons/player/play.svg" alt="">
          </div>
          <div class="text">
            <span style="font-size: 16px;color:#333333;">{{ item.title }}</span>
            <span>{{ item.creatorName }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.add-form {
  position: fixed;
  padding: 40px 20px;
  width: 580px;
  top: 50%;
  left: 50%;
  display: flex;
  flex-direction: column;

  transform: translate(-50%, -50%);
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 2px 2px 10px #eaeaea;
  z-index: 2;
}

#close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 18px;
  cursor: pointer;
}

#back {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 18px;
  cursor: pointer;
}

.search-bar {
  padding: 0 20px;
  width: 100%;
  height: 35px;
  display: flex;
  align-items: center;
  border: #e4e8ec solid 1px;
  border-radius: 5px;
  font-size: 16px;

  input {
    width: 100%;
  }
}

.grid-list {
  width: calc(100% + 6px);
  height: 255px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 20px;
  overflow-y: scroll;
}

.music-card {
  user-select: none;
  padding-left: 10px;
  height: 65px;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  border-radius: 10px;
}

.music-card:hover {
  background-color: #f2f3f4;
  box-shadow: 2px 2px 10px #eaeaea;
  cursor: pointer;
}

.music-card:hover #cover {
  filter: brightness(90%);
}

.music-card:hover #playButton {
  opacity: 70%;
}

.info {
  display: flex;
}

.cover {
  position: relative;
  margin-right: 10px;
  width: 50px;
  height: 50px;
  border-radius: 5px;
  overflow: hidden;
}

.cover:hover {
  cursor: pointer;
}

.cover:hover #playButton {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.2);
}

#playButton {
  position: absolute;
  height: 35px;
  top: 50%;
  left: 50%;
  opacity: 0;
  transition: all 0.1s ease;
  transform: translate(-50%, -50%);
}

.text {
  margin-right: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.text span {
  width: 177px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.separator-content {
  margin-top: 20px;
  display: flex;
  flex-direction: row
}

.select-button {
  margin-bottom: 20px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 18px;
  color: #888888;
}

.select-button .button:hover {
  cursor: pointer;
}

.bold-text {
  color: #333333;
  font-weight: bold;
}

.select-button .button {
  position: relative;
  margin-right: 40px;
}

.button-underline {
  position: absolute;
  height: 3px;
  width: 18px;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background-color: #fc3b56;
  border-radius: 2px;
}

</style>