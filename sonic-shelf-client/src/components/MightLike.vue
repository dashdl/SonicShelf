<script setup>
import {onMounted, ref} from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";
import { getFullUrl } from '@/utils/urlConfig';

const pageSize = ref(4);
const items = ref([]);

const getPlaylist = async () => {
  let res = await request.get('playlists/recommend-card', {params: {pageSize: 4}})
  if (res.code === '200') {
    items.value = res.data.list
  }
}

const timeState = () => {
  const hour = new Date().getHours();
  if (hour >= 5 && hour < 12) {
    return 'morning';
  } else if (hour >= 12 && hour < 14) {
    return 'noon';
  } else if (hour >= 14 && hour < 18) {
    return 'afternoon';
  } else {
    return 'evening';
  }
}

onMounted(() => {
  getPlaylist()
})


</script>

<template>
  <div class="might-container">
    <div class="hello">
      <span v-if="timeState() === 'morning'" style="font-size: 22px;font-weight: bold">早上好,</span>
      <span v-else-if="timeState() === 'noon'" style="font-size: 22px;font-weight: bold">中午好,</span>
      <span v-else-if="timeState() === 'afternoon'" style="font-size: 22px;font-weight: bold">下午好,</span>
      <span v-else style="font-size: 22px;font-weight: bold">晚上好,</span>
      <span>猜你最近喜欢听</span>
    </div>
    <div class="playlists">
      <div @click="router.push(`/playlist/${item.id}`)" v-for="item in items" class="item-card">
        <img :src="item.coverImage ? getFullUrl(item.coverImage) : '/images/default/cover.png'" alt="">
      </div>
    </div>
  </div>
</template>

<style scoped>
.might-container {
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: white;
  border-radius: 10px;
}

.item-card {
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.item-card:hover {
  transform: scale(1.05);
  cursor: pointer;
}

.playlists {
  display: grid;
  column-gap: 12px;
  grid-template-columns: repeat(4, minmax(80px, 1fr));
}

@media (min-width: 1200px) {
  .might-container {
    padding: 20px;
  }
}

@media (min-width: 1440px) {
  .might-container {
    padding: 25px;
  }
}
</style>