<script setup>
import Carousel from '@/components/Carousel.vue'
import MightLike from "@/components/MightLike.vue";
import PlaylistCard from "@/components/common/cards/PlaylistCard.vue";
import {computed, onMounted, onUnmounted, ref} from "vue";
import request from "@/utils/request.js";
import MusicCard from "@/components/common/cards/MusicCard.vue";

const recommendCount = ref(5) // 默认值

const recommendPlaylists = ref([])
const likeRecommend = ref([])


const updateRecommendCount = () => {
  const width = window.innerWidth
  if (width < 1200) {
    recommendCount.value = 5
    getRecommends(5)
    likeRecommend.value = 2
  } else if (width < 1360) {
    recommendCount.value = 6
    getRecommends(6)
  } else {
    recommendCount.value = 7
    getRecommends(7)
    likeRecommend.value = 3
  }
}

const gridTemplateColumns = computed(() => {
  return `repeat(${recommendCount.value}, minmax(140px, 1fr))`
})

const gridLikeColumns = computed(() => {
  return `repeat(${likeRecommend.value}, minmax(380px, 1fr))`
})

const getRecommends = (numb) => {
  request.get('playlists/recommend', {
    params: {
      limit: numb,
    }
  }).then(res => {
    if (res.code === '200') {
      recommendPlaylists.value = res.data
    }
  })
}

onMounted(() => {
  updateRecommendCount() // 初始化
  window.addEventListener('resize', updateRecommendCount)

})

onUnmounted(() => {
  window.removeEventListener('resize', updateRecommendCount)
})
</script>

<template>
  <div class="home-container">
    <div class="top-container">
      <Carousel/>
      <MightLike/>
    </div>
    <div class="separator-container">推荐歌单</div>
    <div class="playlist-recommend" :style="{ gridTemplateColumns }">
      <PlaylistCard v-for="item in recommendPlaylists"
                    :item="item"
      />
    </div>
    <div class="separator-container">根据你喜欢的推荐</div>
    <div class="like-recommend" :style="{ 'grid-template-columns': gridLikeColumns }">
      <MusicCard/>
      <MusicCard/>
      <MusicCard/>
      <MusicCard/>
      <MusicCard/>
      <MusicCard/>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
}

.top-container {
  display: grid;
  column-gap: 20px;
  grid-template-columns: repeat(2, minmax(380px, 1fr));
  margin-bottom: 40px;
}

.separator-container {
  margin-bottom: 20px;
  font-size: 18px;
  color: #333333;
  font-weight: bold;
}

.playlist-recommend {
  margin-bottom: 20px;
  display: grid;
  column-gap: 20px;

}

.like-recommend {
  display: grid;
  grid-column-gap: 20px;
  grid-row-gap: 10px;
}
</style>