<script setup>
import {ref, onMounted, reactive} from "vue";

const audio = ref(null);

const player = reactive({
  play: false,
});

let isClick = ref(false);
const show = () => {
  isClick.value = !isClick.value;
}

onMounted(() => {
  audio.value = new Audio('/player1.mp3');
});

const play = () => {
  if (!audio.value) {
    console.error('音频未初始化');
    return;
  }
  audio.value.play();
  player.play = true;
}

// 暂停
const pause = () => {
  audio.value.pause()
  player.play = false;
}

// 上一首
const previous = () => {
  // 切换到上一首歌曲的逻辑
  audio.src = '上一首的URL'
  audio.value.play()
}

// 下一首
const next = () => {
  // 切换到下一首歌曲的逻辑
  audio.src = '下一首的URL'
  audio.play()
}

// 设置音量（0-1）
const setVolume = (volume) => {
  audio.volume = volume
}

// 跳转到指定时间（秒）
const seek = (time) => {
  audio.currentTime = time
}

</script>

<template>
  <div class="player-component">
    <div class="cover-container">
      <div class="cover-content">
        <img src="/images/default/cover.png" style="height: 45px;border-radius: 23px" alt="">
      </div>
      <div class="info-container">
        <div class="title-container">
          <div class="scroll-wrapper">
            <div class="title-content">
              this is title123123123123123123123123
            </div>
            <div class="title-content">
              this is title123123123123123123123123
            </div>
          </div>
        </div>
        <div class="button-group">
          <div class="button">
            <img src="/icons/player/favorite.svg" style="width: 20px" alt="">
          </div>
          <div class="button">
            <img src="/icons/player/comment.svg" style="width: 20px" alt="">
          </div>
          <div class="button">
            <img src="/icons/player/share.svg" style="width: 20px" alt="">
          </div>
          <div class="button">
            <img src="/icons/player/download.svg" style="width: 20px" alt="">
          </div>
        </div>
      </div>
    </div>
    <div class="player-container">
      <div class="player-menu">
        <div class="menu-button">
          <img src="/icons/player/like.svg" style="height: 22px" alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/last.svg" style="height: 30px;" alt="">
        </div>
        <div class="play-button">
          <img v-if="!player.play" @click="play()" src="/icons/player/play.svg" style="width: 30px;margin-left: 1px;"
               alt="">
          <img v-if="player.play" @click="pause()" src="/icons/player/pause.svg" style="width: 30px;margin-left: 1px;"
               alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/next.svg" style="height: 30px;" alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/order.svg" style="height: 18px" alt="">
        </div>
      </div>
      <div class="progress-container">
        <span>114</span>
        <div class="progress-content">
          <div class="progress-bar"></div>
        </div>
        <span>514</span>
      </div>
    </div>
    <div class="selection-container">
      <div class="selection-button">
        <img src="/icons/player/volume.svg" style="height: 22px" alt="">
      </div>
      <div class="selection-button">
        <img @click="show" src="/icons/player/playlist.svg" style="height: 23px" alt="">
      </div>
    </div>
    <div :class="['list-form', {show:isClick}]">
      <div class="form-head">
        <div class="form-title">
          <span style="font-size: 20px;font-weight: bold">播放列表</span>
        </div>
        <div class="form-action">
          <img src="/icons/player/favorite.svg" style="height: 15px;align-self: center;margin-right: 3px" alt="">
          <span style="margin-right: 25px">收藏全部</span>
          <img src="/icons/player/favorite.svg" style="height: 15px;align-self: center;margin-right: 3px" alt="">
          <span>清空</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.player-component {
  position: relative;
  width: 100%;
  min-width: 1056px;
  padding: 0 30px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.cover-container {
  height: 100%;
  display: flex;
  align-items: center;
  flex: 1;
  overflow: hidden;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.cover-content {
  height: 63px;
  width: 63px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 33px;
  background-color: #0d0d0d;
  animation: rotate 40s linear infinite;
}

.info-container {
  height: 100%;
  margin-left: 10px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: end;
}

@keyframes scroll {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
}

.title-container {
  position: absolute;
  top: 10px;
  width: 265px;
  display: flex;
  white-space: nowrap;
  font-size: 18px;
  overflow: hidden;
}

.scroll-wrapper {
  display: flex;
  white-space: nowrap;
  font-size: 18px;
  animation: scroll 10s linear infinite;
}

.title-content {
  flex: 0 0 auto;
  padding-right: 20px; /* 文本间添加间距 */
}

.button-group {
  margin-bottom: 13px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: left;
}

.button-group .button {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 25px;
}

.player-container {
  height: 100%;
  display: flex;
  flex: 1;
  flex-direction: column;
}

.player-menu {
  margin-top: 12px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.menu-button {
  margin: 0 13px;
}

.play-button {
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 23px;
  background-color: #fc3d4e;
}

.progress-container {
  margin-top: 1px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.progress-container span {
  font-size: 13px;
  color: #c4c8cd;
}

.progress-content {
  margin: 0 8px;
  height: 5px;
  width: 340px;
  background-color: #dadcdf;
  border-radius: 3px;
  display: flex;
  justify-content: left;
}

.progress-bar {
  height: 5px;
  width: 100px;
  background-color: #f26c79;
  border-radius: 3px;
}

.selection-container {
  height: 100%;
  display: flex;
  justify-content: end;
  align-items: center;
  flex: 1;
}

.selection-button {
  margin-left: 24px;
}

.list-form {
  position: fixed;
  padding-left: 20px;
  padding-right: 30px;
  height: calc(100% - 195px);
  width: 438px;
  right: 0;
  top: 50%;
  transform: translateX(450px) translateY(-50%);
  background-color: #fafafa;
  border-radius: 10px;
  box-shadow: 0 0 10px 2px #7b818f;
  transition: transform 0.5s ease;
}

.show {
  transform: translateX(10px) translateY(-50%);
}

.form-head{
  height: 60px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;

}

.form-action{
  display: flex;
  flex-direction: row;
}

</style>