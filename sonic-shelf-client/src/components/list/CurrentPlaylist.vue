<script setup>

import {usePlayerStore} from "@/store/player.js";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import { getFullUrl } from '@/utils/urlConfig';

const playerStore = usePlayerStore()

const favoriteAll = (currentPlaylist) => {
  const playlistCopy = JSON.parse(JSON.stringify(currentPlaylist));
  request.post('favorites/playlist', playlistCopy).then(res => {
    ElMessage.success(res.data || '收藏成功');
  }).catch(err => {
    console.error('收藏失败:', err);
    ElMessage.error('收藏失败，请稍后重试');
  });
}

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}


</script>

<template>
  <div class="list-form">
    <div class="form-head">
      <div class="form-title">
        <span style="font-size: 20px;font-weight: bold">播放列表</span>
        <span id="count">{{ playerStore.currentPlaylist.length }}</span>
      </div>
      <div class="form-action">
        <img @click="favoriteAll(playerStore.currentPlaylist)" src="/icons/player/favorite.svg"
             style="height: 15px;align-self: center;margin-right: 3px;margin-bottom: 2px" alt="">
        <span @click="favoriteAll(playerStore.currentPlaylist)" style="margin-right: 25px">收藏全部</span>
        <img @click="playerStore.clearPlaylist();" src="/icons/player/favorite.svg"
             style="height: 15px;align-self: center;margin-right: 3px;margin-bottom: 2px" alt="">
        <span @click="playerStore.clearPlaylist();">清空</span>
      </div>
    </div>
    <div class="form-info">
      <div v-for="(item,index) in playerStore.currentPlaylist" class="list-item">
        <div @click="playerStore.playSong(index)" class="item-cover">
          <img :src="getFullUrl(item.coverImage)||'/images/default/cover.png'"
               style="height: 50px;width: 50px;border-radius: 5px" alt="">
          <img id="play" src="/icons/player/play.svg" style="height: 30px;" alt="">
        </div>
        <div class="music-info">
          <div class="info-left">
            <span style="font-size: 20px;color: #333333">{{ item.title }}</span>
            <span>{{ item.artistName }}</span>
          </div>
          <div class="info-right">
            <span>{{ formatTime(item.duration) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.list-form {
  position: fixed;
  height: calc(100% - 195px);
  width: 438px;
  right: 0;
  top: 50%;
  transform: translateX(450px) translateY(-50%);
  background-color: #fafafa;
  border-radius: 10px;
  box-shadow: 0 0 10px #eaeaea;
  transition: transform 0.5s ease;
  z-index: 11;
}

.form-head {
  padding-left: 20px;
  padding-right: 30px;
  height: 60px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

}

.form-title {
  position: relative;
}

#count {
  position: absolute;
  top: -4px;
}

.form-action {
  display: flex;
  flex-direction: row;
}

.form-action img:hover, .form-action span:hover {
  cursor: pointer;
}

.form-info {
  height: calc(100% - 65px);
  overflow-y: scroll;
}

.list-item {
  padding: 9px 30px 9px 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  color: #7b818f;

}

.list-item:hover {
  background-color: #edeeef;
}

.list-item:hover #play {
  opacity: 1;
}

.item-cover {
  position: relative;
  width: 60px;
  margin-right: 10px;
}

.item-cover:hover {
  cursor: pointer;
}

#play {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translateY(-50%) translateX(-50%);
  opacity: 0;
}

.music-info {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.info-left {
  display: flex;
  flex-direction: column;
}

.info-right {
  display: flex;
  align-items: center;
}
</style>