<script setup>
import {onMounted, onUnmounted, ref} from "vue";
import {usePlayerStore} from "@/store/player.js";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import Lyric from "@/components/common/Lyric.vue";
import router from "@/router/index.js";
import CurrentPlaylist from "@/components/list/CurrentPlaylist.vue";

const playerStore = usePlayerStore();

const emit = defineEmits(['collect']);

const showVolumeControl = ref(false);
let volumeHideTimeout = null;

const handleVolumeButtonEnter = () => {
  if (volumeHideTimeout) {
    clearTimeout(volumeHideTimeout);
    volumeHideTimeout = null;
  }
  showVolumeControl.value = true;
};

const handleVolumeButtonLeave = () => {
  volumeHideTimeout = setTimeout(() => {
    showVolumeControl.value = false;
  }, 300);
};

const handleVolumeContainerEnter = () => {
  if (volumeHideTimeout) {
    clearTimeout(volumeHideTimeout);
    volumeHideTimeout = null;
  }
};

const handleVolumeContainerLeave = () => {
  volumeHideTimeout = setTimeout(() => {
    showVolumeControl.value = false;
  }, 300);
};

const show = () => {
  isClick.value = !isClick.value;
}

let isClick = ref(false);

const titleRef = ref(null);

const titleWidth = ref(0);
const shouldScroll = ref(false);

const progress = ref(0);
const volumeProgress = ref(0);
const duration = ref(0);
const currentTime = ref(0);
const currentVolume = ref(0);
const progressBar = ref(null);
const volumeProgressBar = ref(null);

const isDragging = ref(false);
const isVolumeDragging = ref(false);

const favorite = () => {
  if (playerStore.currentPlaylist[playerStore.currentIndex].favorite === false) {
    request.post('favorites/music/' + playerStore.currentPlaylist[playerStore.currentIndex].id).then(res => {
      if (res.code === '200') {
        ElMessage.success('收藏成功');
        playerStore.currentPlaylist[playerStore.currentIndex].favorite = true
        localStorage.setItem("playlist", JSON.stringify(playerStore.currentPlaylist));
      } else {
        ElMessage.error(res.message)
        playerStore.currentPlaylist[playerStore.currentIndex].favorite = true
      }
    })
  } else {
    request.delete('favorites/music/' + playerStore.currentPlaylist[playerStore.currentIndex].id).then(res => {
      if (res.code === '200') {
        ElMessage.success('取消收藏成功');
        playerStore.currentPlaylist[playerStore.currentIndex].favorite = false
        localStorage.setItem("playlist", JSON.stringify(playerStore.currentPlaylist));
      } else {
        ElMessage.error(res.message)
        playerStore.currentPlaylist[playerStore.currentIndex].favorite = true
      }
    })
  }
}

const collect = () => {
  emit('collect', playerStore.currentPlaylist[playerStore.currentIndex].id);
}

const updateProgressFromStore = () => {
  if (isDragging.value) return;
  if (playerStore.duration > 0) {
    progress.value = (playerStore.currentTime / playerStore.duration) * 100;
    duration.value = playerStore.duration;
    currentTime.value = playerStore.currentTime;
  }
};
const updateVolumeProgressFromStore = () => {
  if (isVolumeDragging.value) return;
  volumeProgress.value = playerStore.volume * 100;
  currentVolume.value = playerStore.volume;
};

let progressInterval = null;
let VolumeProgressInterval = null;

const startProgressUpdate = () => {
  if (progressInterval) clearInterval(progressInterval);
  progressInterval = setInterval(updateProgressFromStore, 100); // 每100ms更新一次
};
const startVolumeProgressUpdate = () => {
  if (VolumeProgressInterval) clearInterval(VolumeProgressInterval);
  VolumeProgressInterval = setInterval(updateVolumeProgressFromStore, 100); // 每100ms更新一次
};

const stopProgressUpdate = () => {
  if (progressInterval) {
    clearInterval(progressInterval);
    progressInterval = null;
  }
};
const stopVolumeProgressUpdate = () => {
  if (VolumeProgressInterval) {
    clearInterval(VolumeProgressInterval);
    VolumeProgressInterval = null;
  }
};

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// ref="volumeProgressBar"
// @click="handleVolumeClick"
// @mousedown="startVolumeDrag"

// 点击跳转
const handleClick = (e) => {
  const rect = progressBar.value.getBoundingClientRect();
  const percent = (e.clientX - rect.left) / rect.width * 100;
  updateProgress(percent);
  // 点击后直接设置时间
  const newTime = percent / 100 * playerStore.duration;
  playerStore.setCurrentTime(newTime);
};
const handleVolumeClick = (e) => {
  const rect = volumeProgressBar.value.getBoundingClientRect();
  // 垂直进度条，从下往上
  const percent = Math.max(0, Math.min(100, ((rect.bottom - e.clientY) / rect.height) * 100));
  updateVolumeProgress(percent);
  // 点击后直接设置音量
  const newVolume = percent / 100;
  playerStore.setVolume(newVolume);
};

// 开始拖动
const startDrag = (e) => {
  e.preventDefault(); // 防止文本选择
  isDragging.value = true;
  document.addEventListener('mousemove', onDrag);
  document.addEventListener('mouseup', stopDrag);
};
const startVolumeDrag = (e) => {
  e.preventDefault(); // 防止文本选择
  isVolumeDragging.value = true;
  document.addEventListener('mousemove', onVolumeDrag);
  document.addEventListener('mouseup', stopVolumeDrag);
};

// 拖动中
const onDrag = (e) => {
  if (!isDragging.value) return;
  const rect = progressBar.value.getBoundingClientRect();
  const percent = (e.clientX - rect.left) / rect.width * 100;
  updateProgress(percent);
};
const onVolumeDrag = (e) => {
  if (!isVolumeDragging.value) return;
  const rect = volumeProgressBar.value.getBoundingClientRect();
  // 垂直进度条，从下往上
  const percent = Math.max(0, Math.min(100, ((rect.bottom - e.clientY) / rect.height) * 100));
  updateVolumeProgress(percent);
};

// 停止拖动
const stopDrag = (e) => {
  isDragging.value = false;
  const rect = progressBar.value.getBoundingClientRect();
  const percent = (e.clientX - rect.left) / rect.width;
  const newTime = percent * playerStore.duration;
  playerStore.setCurrentTime(newTime);
  document.removeEventListener('mousemove', onDrag);
  document.removeEventListener('mouseup', stopDrag);
};
const stopVolumeDrag = (e) => {
  isVolumeDragging.value = false;
  const rect = volumeProgressBar.value.getBoundingClientRect();
  // 垂直进度条，从下往上
  const percent = Math.max(0, Math.min(100, ((rect.bottom - e.clientY) / rect.height)));
  playerStore.setVolume(percent / 100);
  document.removeEventListener('mousemove', onVolumeDrag);
  document.removeEventListener('mouseup', stopVolumeDrag);
};

// 更新进度
const updateProgress = (percent) => {
  progress.value = Math.max(0, Math.min(100, percent));
  // 实时更新当前时间显示
  currentTime.value = (percent / 100) * playerStore.duration;
};
const updateVolumeProgress = (percent) => {
  volumeProgress.value = Math.max(0, Math.min(100, percent));
  // 实时更新当前音量显示
  currentVolume.value = percent / 100;
};

const lyricTop = ref(0)
const showLyric = () => {
  lyricTop.value = -100
}
const hideLyric = () => {
  lyricTop.value = 0
}

// 切歌时重置唱片旋转状态
const resetRotation = () => {
  const coverContent = document.querySelector('.cover-content');
  if (coverContent) {
    // 临时保存当前播放状态
    const wasPlaying = playerStore.isPlaying;
    
    // 移除动画类
    coverContent.style.animation = 'none';
    // 强制重排
    void coverContent.offsetWidth;
    // 重新设置动画，这会导致动画从起始状态重新开始
    coverContent.style.animation = 'rotate 40s linear infinite';
    // 设置播放状态
    coverContent.style.animationPlayState = wasPlaying ? 'running' : 'paused';
  }
}

onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag);
  document.removeEventListener('mouseup', stopDrag);
  document.removeEventListener('mousemove', onVolumeDrag);
  document.removeEventListener('mouseup', stopVolumeDrag);
  if (volumeHideTimeout) {
    clearTimeout(volumeHideTimeout);
  }
});

onMounted(() => {
  playerStore.initAudio();
  startProgressUpdate();
  startVolumeProgressUpdate(); // 启动音量更新

  // 监听播放状态变化和当前歌曲索引变化
  let prevIndex = playerStore.currentIndex;
  const unwatch = playerStore.$subscribe((mutation, state) => {
    if (state.isPlaying) {
      startProgressUpdate();
    } else {
      stopProgressUpdate();
    }
    // 监听音量变化
    updateVolumeProgressFromStore();

    // 监听当前索引变化，实现切歌时回正
    if (state.currentIndex !== prevIndex) {
      resetRotation();
      prevIndex = state.currentIndex;
    }
  });

  const titleObserver = new ResizeObserver(entries => {
    for (let entry of entries) {
      titleWidth.value = entry.contentRect.width;
      shouldScroll.value = titleWidth.value > 265;
    }
  });
  titleObserver.observe(titleRef.value);

  onUnmounted(() => {
    unwatch();
    stopProgressUpdate();
    stopVolumeProgressUpdate(); // 停止音量更新
  });
});

const jumpToMusic = () => {
  router.push({
    name: 'Music',
    params: {musicId: playerStore.currentPlaylist[playerStore.currentIndex].id}
  })
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="player-component">
    <div class="cover-container">
      <div @click="showLyric" class="cover-content"
           :style="{ animationPlayState: playerStore.isPlaying ? 'running' : 'paused' }">
        <img
            :src=" playerStore.currentPlaylist && playerStore.currentIndex >= 0 && playerStore.currentIndex < playerStore.currentPlaylist.length ? baseUrl + playerStore.currentPlaylist[playerStore.currentIndex].coverImage : '/images/default/cover.png'"
            style="height: 45px;border-radius: 23px" alt="">
      </div>
      <div class="info-container">
        <div class="title-container">
          <div :class="shouldScroll ? 'scroll-wrapper animation-scroll' : 'scroll-wrapper'">
            <div ref="titleRef" class="title-content">
              <span style="color: #333333">{{ playerStore.currentTitle || '未知歌曲' }} - </span>
              <span style="font-size: 14px;color: #7b818f">{{ playerStore.currentName || '未知艺术家' }}</span>
            </div>
            <div v-if="shouldScroll" class="title-content">
              <span style="color: #333333">{{ playerStore.currentTitle || '未知歌曲' }} - </span>
              <span style="font-size: 14px;color: #7b818f">{{ playerStore.currentName || '未知艺术家' }}</span>
            </div>
          </div>
        </div>
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
      </div>
    </div>
    <div class="player-container">
      <div class="player-menu">
        <div class="menu-button">
          <img
              @click="favorite"
              :src="playerStore.currentPlaylist && playerStore.currentIndex >= 0 && playerStore.currentIndex < playerStore.currentPlaylist.length && playerStore.currentPlaylist[playerStore.currentIndex].favorite===true ? '/icons/player/like.svg': '/icons/player/unlike.svg' "
              style="height: 22px" alt="">
        </div>
        <div class="menu-button">
          <img @click="playerStore.prev()" src="/icons/player/last.svg" style="height: 30px;" alt="">
        </div>
        <div @click="playerStore.togglePlay" class="play-button">
          <img :src="playerStore.isPlaying ? '/icons/player/pause.svg' : '/icons/player/play.svg'"
               style="width: 30px;margin-left: 1px;"
               alt="">
        </div>
        <div @click="playerStore.next()" class="menu-button">
          <img src="/icons/player/next.svg" style="height: 30px;" alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/order.svg" style="height: 18px" alt="">
        </div>
      </div>
      <div class="progress-container">
        <span>{{ formatTime(duration) }}</span>
        <div class="progress-content"
             ref="progressBar"
             @click="handleClick"
             @mousedown="startDrag">
          <div class="progress-bar" :style="{ width: progress + '%' }"></div>
        </div>
        <span>{{ formatTime(currentTime) }}</span>
      </div>
    </div>
    <div class="selection-container">
      <div class="selection-button"
           @mouseenter="handleVolumeButtonEnter"
           @mouseleave="handleVolumeButtonLeave">
        <img src="/icons/player/volume.svg" style="width: 22px;" alt="">
      </div>
      <div class="volume-container"
           v-show="showVolumeControl"
           @mouseenter="handleVolumeContainerEnter"
           @mouseleave="handleVolumeContainerLeave">
        <div class="volume-content"
             ref="volumeProgressBar"
             @click="handleVolumeClick"
             @mousedown="startVolumeDrag">
          <div class="volume-progress" :style="{ height: volumeProgress + '%'}"></div>
          <span>{{ Math.round(volumeProgress) }}%</span>
        </div>
      </div>
      <div class="selection-button">
        <img @click="show" src="/icons/player/playlist.svg" style="height: 23px" alt="">
      </div>
    </div>
    <CurrentPlaylist :class="{show:isClick}"/>
  </div>
  <Lyric
      :style="{ transform: 'translateY('+lyricTop+'vh)'}"
      @close="hideLyric"
      @favorite="favorite"
      @show="show"
      @mouseenter="handleVolumeButtonEnter"
      @mouseleave="handleVolumeButtonLeave"
      @collect="collect"
  />
</template>

<style scoped>


.player-component {
  user-select: none;
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
  transform-origin: center center;
  animation: rotate 40s linear infinite;
}

.cover-content:hover {
  cursor: pointer;
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
}

.animation-scroll {
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

.button-group .button:hover {
  cursor: pointer;
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

.menu-button:hover {
  filter: brightness(80%);
  cursor: pointer;
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

.play-button:hover {
  transform: scale(1.05);
  cursor: pointer;
}

.play-button:active {
  transform: scale(0.95);
}

.progress-container {
  margin-top: 1px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
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
  cursor: pointer;
}

.show {
  transform: translateX(10px) translateY(-50%);
}

.volume-container {
  position: fixed;
  height: 135px;
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 0 10px #eaeaea;
  right: 65px;
  bottom: 60px;
  z-index: 100;
}

.volume-content {
  height: 100px;
  width: 6px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  background-color: #e9eaec;
  border-radius: 3px;
  position: relative;
}

.volume-progress {
  width: 6px;
  height: 50%;
  border-radius: 3px;
  background-color: #f26c79;
}

.volume-content span {
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
  font-size: 10px;
  margin-bottom: 3px
}
</style>