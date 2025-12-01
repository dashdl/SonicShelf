<script setup>

import {usePlayerStore} from "@/store/player.js";
import {ref, watch} from "vue";
import router from "@/router/index.js";

const player = usePlayerStore();

const lyricMain = ref(null)

const emit = defineEmits(['close', 'favorite', 'show', 'mouseenter', 'mouseleave', 'collect'])

const closeLyric = () => {
  emit('close')
}

const favorite = () => {
  emit('favorite')
}

const show = () => {
  emit('show')
}

const collect = () => {
  emit('collect', player.currentPlaylist[player.currentIndex].id);
}

const jumpToMusic = () => {
  router.push({
    name: 'Music',
    params: {musicId: player.currentPlaylist[player.currentIndex].id}
  })
  emit('close')
}

// watch(() => player.currentLyricIndex, (newIndex, oldIndex) => {
//   if (newIndex >= 0 && lyricMain.value) {
//     const activeLine = lyricMain.value.querySelector('.lyric-line.active-text');
//     if (activeLine) {
//       const containerHeight = lyricMain.value.clientHeight;
//       const lineHeight = activeLine.offsetHeight;
//       const scrollTop = activeLine.offsetTop - (containerHeight / 2) + (lineHeight / 2);
//
//       requestAnimationFrame(() => {
//         lyricMain.value.scrollTo({
//           top: scrollTop,
//           behavior: 'smooth'
//         });
//       });
//     }
//   }
// })


const getPlayModeIcon = ()=>{
  switch (player.playMode) {
    case 'sequence':
      return '/icons/player/order.svg';
    case 'loop':
      return '/icons/player/repeatOne.svg';
    case 'random':
      return '/icons/player/shuffle.svg';
    case 'listLoop':
      return '/icons/player/repeatAll.svg';
    default:
      return '/icons/player/order.svg';
  }
}

watch(() => player.currentLyricIndex, (newIndex, oldIndex) => {
  if (newIndex >= 0 && newIndex < player.parsedLyrics.length && lyricMain.value) {
    if (newIndex !== oldIndex) {
      const activeLine = lyricMain.value.children[newIndex];
      if (activeLine) {
        const containerHeight = lyricMain.value.clientHeight;
        const lineHeight = activeLine.offsetHeight;
        const offsetTop = activeLine.offsetTop;

        let scrollTop = offsetTop - (containerHeight / 2) + (lineHeight / 2);
        const maxScrollTop = lyricMain.value.scrollHeight - containerHeight;
        scrollTop = Math.max(0, Math.min(scrollTop, maxScrollTop));

        requestAnimationFrame(() => {
          const isFastChange = Math.abs(newIndex - (oldIndex || 0)) > 3;
          lyricMain.value.scrollTo({
            top: scrollTop,
            behavior: isFastChange ? 'auto' : 'smooth'
          });
        });
      }
    }
  }
});

</script>

<template>
  <div class="lyric-container">
    <div class="lyric-header">
      <div class="left-group">
        <div @click="closeLyric" class="left-button">
          <img src="/icons/view/down.svg"
               style="margin-top: 3px;height: 20px" alt="">
        </div>
        <div class="left-button"><img src="/icons/view/full.svg" style="height: 20px" alt=""></div>
      </div>
      <div class="right-group">
        <img @click="closeLyric" src="/icons/view/close.svg" style="height: 18px" alt="">
      </div>
    </div>
    <div class="lyric-main-container">
      <div class="info-container">
        <span style="font-size: 28px;color: #ffffff;margin-bottom: 5px;">{{ player.currentTitle }}</span>
        <span style="font-size: 16px;color: #8b8b8b;">{{ player.currentName }}</span>
      </div>
      <div class="lyric-main" ref="lyricMain">
        <div v-for="(item,index) in player.parsedLyrics"
             :class="[{'active-text': index === player.currentLyricIndex},'lyric-text']">
          <span style=" display: inline-block;text-align: center;">{{ item.text }}</span>
        </div>
      </div>
    </div>
    <div class="lyric-player">
      <div class="button-group">
        <div @click="collect" class="button">
          <img src="/icons/player/favorite.svg" style="width: 20px" alt="">
        </div>
        <div @click="jumpToMusic" class="button">
          <img src="/icons/player/comment.svg" style="width: 20px" alt="">
        </div>
        <div class="button">
          <img src="/icons/player/share.svg" style="width: 20px" alt="">
        </div>
        <div class="button">
          <img src="/icons/player/download.svg" style="width: 20px" alt="">
        </div>
      </div>
      <div class="player-menu">
        <div class="menu-button">
          <img
              @click="favorite"
              :src="player.currentPlaylist && player.currentIndex >= 0 && player.currentIndex < player.currentPlaylist.length && player.currentPlaylist[player.currentIndex].favorite===true ? '/icons/player/like.svg': '/icons/player/unlike.svg' "
              style="height: 22px" alt="">
        </div>
        <div class="menu-button">
          <img @click="player.prev()" src="/icons/ui/last.svg" style="height: 30px;" alt="">
        </div>
        <div @click="player.togglePlay" class="play-button">
          <img :src="player.isPlaying ? '/icons/player/pause.svg' : '/icons/player/play.svg'"
               style="width: 30px;margin-left: 1px;"
               alt="">
        </div>
        <div @click="player.next()" class="menu-button">
          <img src="/icons/ui/next.svg" style="height: 30px;" alt="">
        </div>
        <div class="menu-button">
          <img
              @click="player.togglePlayMod()"
              :src="getPlayModeIcon()"
              style="width: 20px"
              alt=""
          >
        </div>
      </div>
      <div class="selection-container">
        <div class="selection-button"
             @mouseleave="emit('mouseleave')"
             @mouseenter="emit('mouseenter')">
          <img src="/icons/player/volume.svg" style="width: 22px;" alt="">
        </div>
        <div class="selection-button">
          <img @click="show" src="/icons/player/playlist.svg" style="height: 23px" alt="">
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
span {
  line-height: 1;
}

.lyric-container {
  user-select: none;
  position: fixed;
  top: 100vh;
  left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  transition: transform .5s ease-in-out;
  background: rgb(86, 77, 77);
  z-index: 10;
}

.lyric-header {
  padding: 0 40px;
  margin-bottom: 10px;
  width: 100%;
  height: 85px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.left-group {
  display: flex;
  flex-direction: row;
}

.left-button {
  margin-right: 20px;
  height: 40px;
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: #e4e8ec solid 1px;
  border-radius: 10px;
}

.left-button:hover {
  cursor: pointer;
  background: rgba(228, 232, 236, 0.3);
}

.right-group img:hover {
  cursor: pointer;
}

.lyric-main-container {

  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  min-height: 0;
}

.info-container {
  margin-bottom: 25px;
  height: 49px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.lyric-main {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow: auto;
}

.lyric-text {
  margin-bottom: 25px;
  font-size: 24px;
  color: #8b8b8b;
  transition: all .2s ease-in-out;
}

.active-text {
  color: #e4e8ec;
  font-size: 28px;
}

.lyric-player {
  position: relative;
  padding: 0 40px;
  width: 100%;
  height: 85px;
  display: flex;
  justify-content: space-between;
  background-color: rgb(255, 255, 255, 0.02);
}

.button-group {
  display: flex;
  align-items: center;
}

.button {
  margin-right: 25px;
}

.button:hover {
  cursor: pointer;
}

.player-menu {
  position: absolute;
  top: 50%;
  left: 50%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  transform: translate(-50%, -50%);
}

.menu-button {
  margin: 0 13px;
}

.menu-button:hover {
  cursor: pointer;
}

.play-button {
  width: 43px;
  height: 43px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 23px;
  background-color: rgb(255, 255, 255, 0.1);
}

.play-button:hover img {
  transform: scale(1.05);
  cursor: pointer;
}

.play-button:hover {
  cursor: pointer;
}

.play-button:active {
  transform: scale(0.95);
}

.selection-container {
  display: flex;
  align-items: center;
}

.selection-button {
  margin-left: 24px;
  cursor: pointer;
}
</style>