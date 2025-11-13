<script setup>
import Comment from "@/components/list/Comment.vue";
import {useRoute} from "vue-router";
import {onMounted, reactive, ref, watch} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const route = useRoute();

const id = ref(route.params.musicId);

const music = reactive({
  coverImage: '',
  title: '',
  albumTitle: '',
  artistName: '',
})

watch(() => route.params.musicId, (newId, oldId) => {
  if (newId !== oldId) {
    loadMusic(newId);
    id.value = newId;
  }
});

const loadMusic = async (id) => {
  const res = await request.get('musics/' + id)
  if (res.code === '200') {
    music.coverImage = res.data.coverImage;
    music.title = res.data.title;
    music.albumTitle = res.data.albumTitle;
    music.artistName = res.data.artistName;
  } else {
    ElMessage.error(res.message);
  }
}

onMounted(() => {
  loadMusic(id.value)
})

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="music-container">
    <div class="cover">
      <img :src="music.coverImage ? baseUrl + music.coverImage : '/images/default/music.jpg'"
           style="height: 65px;width: 65px;object-fit: cover" alt="">
    </div>
    <div class="info-container">
      <div class="music-title">
        <span>{{ music.title }}</span>
      </div>
      <div class="music-info">
        <span style="margin-right: 20px">专辑：{{ music.albumTitle }}</span>
        <span>歌手：{{ music.artistName }}</span>
      </div>
    </div>
  </div>
  <Comment
      :target-id="id"
      target-type="music"/>
</template>

<style scoped>
.music-container {
  margin-top: 20px;
  margin-bottom: 25px;
  display: flex;
  flex-direction: row;
}

.cover {
  margin-right: 10px;
  overflow: hidden;
  border-radius: 8px;
}

.info-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.music-title {
  margin-bottom: 3px;
  font-size: 16px;
  font-weight: bold;
  color: #0d0d0d;
}

.music-info {
  font-size: 13px;
  color: #888888;
}
</style>