<script setup>
import {onMounted, ref} from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";

const pageSize = ref(4);
const items = ref([]);

const getPlaylist = async () => {
  let res = await request.get('playlists/recommend-card', {params: {pageSize: 4}})
  if (res.code === '200') {
    items.value = res.data.list
  }
}

onMounted(() => {
  getPlaylist()
  console.log("114514")
})

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="might-container">
    <div class="hello">
      <span style="font-size: 22px;font-weight: bold">下午好,</span>
      <span>猜你最近喜欢听</span>
    </div>
    <div class="playlists">
      <div @click="router.push(`/playlist/${item.id}`)" v-for="item in items" class="item-card">
        <img :src="item.coverImage ? baseUrl + item.coverImage : '/images/default/cover.png'" alt="">
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
</style>