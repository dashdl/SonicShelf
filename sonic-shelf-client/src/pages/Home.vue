<script setup>
import Carousel from '@/components/Carousel.vue'
import MightLike from "@/components/MightLike.vue";
import PlaylistCard from "@/components/common/cards/PlaylistCard.vue";
import {computed, onMounted, onUnmounted, ref} from "vue";
import request from "@/utils/request.js";
import MusicCard from "@/components/common/cards/MusicCard.vue";
import {useUserStore} from "@/store/userStore.js";

const recommendCount = ref(5) // 默认值

const recommendPlaylists = ref([])
const guestPlaylists=ref([])
const likeRecommend = ref([])

const musicList = ref([])
const guestMusicList = ref([])

const emit = defineEmits(["collect"])

const updateRecommendCount = () => {
  const width = window.innerWidth
  let newRecommendCount, newLikeRecommend, newMusicCount
  if (width < 1200) {
    newRecommendCount = 5
    newLikeRecommend = 2
    newMusicCount = 6
  } else if (width < 1485) {
    newRecommendCount = 6
    newLikeRecommend = 2
    newMusicCount = 6
  } else {
    newRecommendCount = 7
    newLikeRecommend = 3
    newMusicCount = 9
  }

  if (recommendCount.value !== newRecommendCount ||
      likeRecommend.value !== newLikeRecommend) {
    recommendCount.value = newRecommendCount
    likeRecommend.value = newLikeRecommend
    getRecommends(newRecommendCount)
    getGuestRecommends(newRecommendCount)
    getRecommendsMusic(newMusicCount)
    getGuestRecommendsMusic(newMusicCount)
  }
}

const gridTemplateColumns = computed(() => {
  return `repeat(${recommendCount.value}, minmax(140px, 1fr))`
})

const gridLikeColumns = computed(() => {
  return `repeat(${likeRecommend.value}, minmax(380px, 1fr))`
})

const getRecommends = (playlistNumb,) => {
  request.get('playlists/recommend', {
    params: {
      limit: playlistNumb,
    }
  }).then(res => {
    if (res.code === '200') {
      recommendPlaylists.value = res.data
    }
  })
}

const getRecommendsMusic = async (musicNumb) => {
  const res = await request.get('recommend/music/likes', {
    params: {
      limit: musicNumb
    }
  })
  if (res.code === '200') {
    musicList.value = res.data;
  }
}

const getGuestRecommends = (playlistNumb,) => {
  request.get('playlists/recommend-guest', {
    params: {
      limit: playlistNumb,
    }
  }).then(res => {
    if (res.code === '200') {
      guestPlaylists.value = res.data
    }
  })
}

const getGuestRecommendsMusic = async (musicNumb) => {
  const res = await request.get('musics/guest', {
    params: {
      limit: musicNumb
    }
  })
  if (res.code === '200') {
    guestMusicList.value = res.data;
  }
}

const collect = (id) => {
  emit("collect", id);
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
    <div v-if="useUserStore().isLoggedIn" class="separator-container">推荐歌单</div>
    <div v-if="useUserStore().isLoggedIn" class="playlist-recommend" :style="{ gridTemplateColumns }">
      <PlaylistCard v-for="item in recommendPlaylists"
                    :item="item"
      />
    </div>
    <div v-if="useUserStore().isLoggedIn" class="separator-container">根据你喜欢的推荐</div>
    <div v-if="useUserStore().isLoggedIn" class="like-recommend" :style="{ 'grid-template-columns': gridLikeColumns }">
      <MusicCard
          v-for="item in musicList"
          @collect="collect"
          :item="item"
      />
    </div>
    <div class="separator-container">精选歌单</div>
    <div class="playlist-recommend" :style="{ gridTemplateColumns }">
      <PlaylistCard v-for="item in guestPlaylists"
                    :item="item"
      />
    </div>
    <div class="separator-container">精选推荐</div>
    <div class="like-recommend" :style="{ 'grid-template-columns': gridLikeColumns }">
      <MusicCard
          v-for="item in guestMusicList"
          @collect="collect"
          :item="item"
      />
    </div>
  </div>
</template>

<style scoped>
.home-container {
  width: 100%;
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