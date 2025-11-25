<script setup>

import router from "@/router/index.js";
import request from "@/utils/request.js";
import {usePlayerStore} from "@/store/player.js";

const props = defineProps({
  item: {
    title: {type: String, required: false},
    playCount: {type: String, required: false},
    musicCount: {type: String, required: false},
    coverImage: {type: String, required: false},
    id: {type: String, required: false},
    releaseDate: {type: String, required: false},
  },
  type: {
    type: String,
    required: false,
  },
  showArtist: {
    type: Boolean,
    required: false,
  },
})

const jump = () => {
  router.push(`/${props.type}/${props.item.id}`);
}

const replace = (id) => {
  const type = props.type === "album" ? "album" : "playlists";
  request.get(type + '/' + id + '/musics').then(res => {
    const playerStore = usePlayerStore();
    playerStore.updatePlaylist(res.data)
    localStorage.setItem("playlist", JSON.stringify(res.data))
    playerStore.playSong(0)
  })
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div @click.stop="jump" class="grid-item">
    <div v-if="type==='album'" class="record"></div>
    <img :src="item.coverImage ? baseUrl + item.coverImage : '/images/default/cover.png'"
         style="height: 100%;object-fit: cover;"
         alt="">
    <div class="background"></div>
    <div class="play-button">
      <img src="/icons/player/play.svg" style="width: 40px" alt="">
    </div>
    <div class="info">
      <span style="color: #333333;font-size: 14px;margin-bottom: -2px">{{ item.title }}</span>
      <div class="details" style="height: 18px">
        <span v-if="type !== 'collectAlbum' && !showArtist" style="color: #888888;font-size: 12px">
          {{ item.musicCount }}首</span>
        <span v-if="type==='album' && !showArtist" style="color: #888888;font-size: 12px">
           · {{ item.releaseDate }}</span>
        <span v-if="showArtist" @click.stop="router.push('/artist/' + item.artistId)"
              style="cursor: pointer">
          {{ item.artistName }}
        </span>
      </div>
    </div>
    <div v-if="type==='playlist' && item.playCount>=1" class="headphone">
      <img src="/icons/content/headphone.svg" style="width: 15px" alt="">
      <span style="font-size: 12px;color: #ffffff;font-weight: bold;">
        {{ item.playCount }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.grid-item {
  position: relative;
  display: flex;
  flex-direction: column;
}

.record {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  aspect-ratio: 1/1;
  border-radius: 50%;
  background-color: #0d0d0d;
}

.grid-item img {
  border-radius: 10px;
  z-index: 1;
}

.grid-item img:hover, .grid-item:hover img {
  filter: brightness(0.9);
  cursor: pointer;
}

.background {
  margin-top: -20px;
  height: 75px;
  border-radius: 10px;
  background-color: #ffffff;
  opacity: 0;
}

.grid-item:hover .background {
  opacity: 1;
  transition: opacity 0.2s ease-in-out;
  cursor: pointer;
}

.play-button {
  position: absolute;
  z-index: 2;
  right: 8px;
  bottom: 55px;
  opacity: 0;
}

.grid-item:hover .play-button {
  opacity: 1;
  transition: opacity 0.3s ease-in-out;
}

.play-button:hover {
  transform: scale(1.2);
  transition: scale 0.3s ease-in-out;
  filter: brightness(1.25);
}

.info {
  display: flex;
  flex-direction: column;
  position: absolute;
  bottom: 3px;
  left: 10px;
}

.headphone {
  position: absolute;
  display: flex;
  right: 8px;
  top: 10px;
  z-index: 1;
}
</style>