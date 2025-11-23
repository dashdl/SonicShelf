<script setup>

import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {usePlayerStore} from "@/store/player.js";
import InteractCard from "@/components/common/cards/InteractCard.vue";
import {ref, watch} from "vue";

const player = usePlayerStore();

const props = defineProps({
  item: {type: Object, required: true},
})

const emit = defineEmits(["collect"])

const favorite = () => {
  if (props.item.favorite === false) {
    request.post('favorites/music/' + props.item.id).then(res => {
      if (res.code === '200') {
        ElMessage.success('收藏成功');
        props.item.favorite = true;
      } else {
        ElMessage.error('收藏失败，请稍后重试');
      }
    })
  } else {
    request.delete('favorites/music/' + props.item.id).then(res => {
      if (res.code === '200') {
        ElMessage.success('取消收藏成功');
        props.item.favorite = false;
      } else {
        ElMessage.error('取消收藏失败，请稍后重试');
      }
    })
  }
}

const showInteract = ref(false)
const interactPosition = ref(null)

const toggleInteract = (item, event) => {
  // 判断按钮在视口中的位置
  const buttonRect = event.currentTarget.getBoundingClientRect();
  const viewportHeight = window.innerHeight;
  interactPosition.value = buttonRect.top < viewportHeight / 2 ? 'top' : 'bottom';
  const viewportWidth = window.innerWidth;

  // 计算元素右侧到视口右侧的距离
  const distanceToRight = viewportWidth - buttonRect.right;
  if (distanceToRight < 200) {
    interactPosition.value += 'left'
  }
  showInteract.value = !showInteract.value;
};
const hideInteractCard = () => {
  setTimeout(() => {
    showInteract.value = false;
  }, 100);
};

const collect = (id) => {
  emit("collect", id);
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="music-card" @mouseleave="hideInteractCard">
    <div class="info">
      <div @click="player.checkMusicId(props.item.id)" class="cover">
        <img id="cover" :src="props.item.coverImage ? baseUrl + props.item.coverImage : '/images/default/cover.png'"
             alt="">
        <img id="playButton" src="/icons/player/play.svg" alt="">
      </div>
      <div class="text">
        <span style="font-size: 16px;color:#333333;">{{ props.item.title }}</span>
        <span>{{ props.item.artistName }}</span>
      </div>
    </div>
    <div class="button-group">
      <div class="button">
        <img @click="favorite"
             :src=" props.item.favorite ?'/icons/player/like.svg' :'/icons/player/unlike.svg'"
             style="width: 20px;" alt="">
      </div>
      <div class="button">
        <img @click.stop="toggleInteract(item,$event)" src="/icons/status/more.svg" style="height: 20px" alt="">
      </div>
      <InteractCard
          v-if="showInteract"
          :music-id="props.item.id"
          :show-delete="false"
          :position="interactPosition"
          @click.stop
          @collect="collect"
      />
    </div>
  </div>
</template>

<style scoped>
.music-card {
  user-select: none;
  padding-left: 10px;
  height: 90px;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  border-radius: 10px;
}

.music-card:hover {
  background-color: #ffffff;
  box-shadow: 2px 2px 10px #eaeaea;
}

.music-card:hover #cover {
  filter: brightness(90%);
}

.music-card:hover #playButton {
  opacity: 70%;
}

.info {
  display: flex;
  align-items: center;
}

.cover {
  position: relative;
  margin-right: 10px;
  width: 65px;
  height: 65px;
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
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.button-group {
  position: relative;
  display: flex;
  flex-direction: row;
}

.button-group .button {
  display: flex;
  margin-right: 25px;
  align-items: center;
  justify-items: center;
}

.button-group .button:hover {
  cursor: pointer;
  filter: brightness(80%);
}
</style>