<script setup>

import router from "@/router/index.js";
import request from "@/utils/request.js";
import {useRoute} from "vue-router";
import {ElMessage} from "element-plus";
import {usePlayerStore} from "@/store/player.js";
import {computed} from "vue";

const route = useRoute();
const player = usePlayerStore();

const props = defineProps({
  musicId: null,
  showDelete: {
    type: Boolean,
    default: false
  },
  position: {
    type: String,
    default: 'bottom' // 'top' 或 'bottom'
  }
})

const emit = defineEmits(['collect'])

const collect = () => {
  emit('collect', props.musicId);
}

const jumpToMusic = () => {
  router.push({
    name: 'Music',
    params: {musicId: props.musicId}
  })
}

const deleteMusic = async () => {
  const res = await request.delete('playlists/' + route.params.id + '/collect/' + props.musicId)
  if (res.code === '200') {
    ElMessage.success("删除成功");
  } else {
    ElMessage.error(res.message);
  }
}

const containerStyle = computed(() => {
  const style = {};

  // if (props.position.includes('top')) {
  //   return {
  //     top: '50%',
  //   };
  // } else {
  //   return {
  //     bottom: '50%',
  //   };
  // }

  if (props.position.includes('top')) {
    style.top = '50%';
  } else {
    style.bottom = '100%';
  }

  if (props.position.includes('left')) {
    style.right = '0';
    style.margin = '10px 0';
  }

  return style;

});
</script>

<template>
  <div class="interact-container" :style="containerStyle">
    <div @click="player.checkMusicId(musicId)" class="interact-item">
      <img src="/icons/status/play.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">播放</span>
    </div>
    <div @click="player.addNext(musicId)" class="interact-item">
      <img src="/icons/status/nextPlay.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">下一首播放</span>
    </div>
    <div @click="jumpToMusic" class="interact-item">
      <img src="/icons/player/comment.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">查看评论</span>
    </div>
    <hr style="width: calc(100% - 40px)"/>
    <div @click="collect" class="interact-item">
      <img src="/icons/player/favorite.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">收藏</span>
    </div>
    <div class="interact-item">
      <img src="/icons/player/share.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">分享</span>
    </div>
    <div class="interact-item">
      <img src="/icons/player/download.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">下载</span>
    </div>
    <div @click="deleteMusic" v-if="showDelete" class="interact-item">
      <img src="/icons/status/delete.svg" style="width: 17px;margin-right: 10px" alt="">
      <span style="font-size: 13px">从歌单删除</span>
    </div>
  </div>
</template>

<style scoped>
span {
  line-height: 1;
}

.interact-container {
  user-select: none;
  position: absolute;
  padding: 5px 0;
  width: 190px;
  right: -180px;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 2px 2px 10px #eaeaea;
  z-index: 10;
}

.interact-container hr {
  margin: 8px 0;
  border: none;
  background: #e4e8ec;
  height: 1px;
  transform: translateX(20px);
}

.interact-item {
  padding: 0 20px;
  height: 30px;
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  color: #686f7e;
}

.interact-item:hover {
  background-color: #f2f2f2;
  cursor: pointer;
}
</style>