<script setup>
import MusicCard from "@/components/common/cards/MusicCard.vue";
import request from "@/utils/request.js";
import {onMounted, ref, watch} from "vue";
import GridCard from "@/components/common/cards/GridCard.vue";
import UserCard from "@/components/common/cards/UserCard.vue";

const props = defineProps({
  keyword: {
    type: String,
    required: true
  },
})
const emit = defineEmits(["userSelect"])

const musics = ref([])
const playlists = ref([])
const albums = ref([])
const artists = ref([])
const users = ref([])

const search = async () => {
  let res = await request.get('search/musics', {params: {pageNum: 1, pageSize: 6, keyword: props.keyword}})
  if (res.code === '200') {
    musics.value = res.data.list
  }
  res = await request.get('search/playlists', {params: {pageNum: 1, pageSize: 6, keyword: props.keyword}})
  if (res.code === '200') {
    playlists.value = res.data.list
  }
  res = await request.get('search/albums', {params: {pageNum: 1, pageSize: 6, keyword: props.keyword}})
  if (res.code === '200') {
    albums.value = res.data.list
  }
  res = await request.get('search/artists', {params: {pageNum: 1, pageSize: 6, keyword: props.keyword}})
  if (res.code === '200') {
    artists.value = res.data.list
  }
  res = await request.get('search/users', {params: {pageNum: 1, pageSize: 6, keyword: props.keyword}})
  if (res.code === '200') {
    users.value = res.data.list
  }
  console.log(artists.value.length)
}

watch(() => props.keyword, async (newKeyword, oldKeyword) => {
  await search()
})

const userSelect = (page) => {
  emit("userSelect", page)
}
onMounted(async () => {
  await search()
})
</script>

<template>
  <div class="search-comprehensive">
    <div v-if="musics.length>0" @click="userSelect(2)" class="separator-bar">
      单曲
      <img src="/icons/status/right.svg" alt="">
    </div>
    <div v-if="musics.length>0" class="grid-list">
      <MusicCard
          v-for="item in musics"
          :item="item"
      />
    </div>
    <div v-if="playlists.length>0" @click="userSelect(3)" class="separator-bar">
      歌单
      <img src="/icons/status/right.svg" alt="">
    </div>
    <div v-if="playlists.length>0" class="grid-row">
      <GridCard
          v-for="item in playlists"
          :key="item.id"
          :item="item"/>
    </div>
    <div v-if="albums.length>0" @click="userSelect(4)" class="separator-bar">
      专辑
      <img src="/icons/status/right.svg" alt="">
    </div>
    <div v-if="albums.length>0" class="grid-row">
      <GridCard
          v-for="item in albums"
          :key="item.id"
          :item="item"
          type="album"
      />
    </div>
    <div v-if="artists.length>0" @click="userSelect(5)" class="separator-bar">
      歌手
      <img src="/icons/status/right.svg" alt="">
    </div>
    <div v-if="artists.length>0" class="grid-row">
      <UserCard
          v-for="item in artists"
          :key="item.id"
          :item="item"
          type="artist"
      />
    </div>
    <div v-if="users.length>0" @click="userSelect(6)" class="separator-bar">
      用户
      <img src="/icons/status/right.svg" alt="">
    </div>
    <div v-if="users.length>0" class="grid-row">
      <UserCard
          v-for="item in users"
          :key="item.id"
          :item="item"
          type="user"
      />
    </div>
  </div>
</template>

<style scoped>
.search-comprehensive{
  margin-bottom: 10px;
}
.separator-bar {
  margin: 15px 0;
  display: flex;
  flex-direction: row;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #283248;
  cursor: pointer;

  img {
    height: 18px;
  }
}

.grid-list {
  width: calc(100% + 6px);
  display: grid;
  grid-template-columns: 1fr 1fr;
  column-gap: 20px;
}

.grid-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  column-gap: 20px;
}

@media (min-width: 1200px) {
  .grid-row {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (min-width: 1440px) {
  .grid-row {
    grid-template-columns: repeat(6, 1fr);
  }
}
</style>