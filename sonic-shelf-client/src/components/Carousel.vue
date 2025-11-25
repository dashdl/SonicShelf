<template>
  <div class="carousel-container" @mouseenter="pauseAutoPlay" @mouseleave="resumeAutoPlay">
    <div class="carousel-track" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
      <div
          v-for="(item, index) in carouselItems"
          :key="index"
          class="carousel-item"
          @click="handleItemClick(item)"
      >
        <img :src="item.imageUrl" style="border-radius: 10px;" :alt="item.title"/>
      </div>
    </div>

    <div class="carousel-indicators" v-if="carouselItems.length > 1">
      <button
          v-for="(item, index) in carouselItems"
          :key="index"
          :class="{ active: currentIndex === index }"
          @mouseover="goToSlide(index)"
      ></button>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted} from 'vue'
import router from "@/router/index.js";

const carouselItems = ref([
  {
    id: 1,
    title: '热门歌单推荐',
    imageUrl: '/images/carousel/OVP.jpg',
    targetUrl: '/playlist/69'
  },
  {
    id: 2,
    title: 'Troye Sivan新专首发',
    imageUrl: '/images/carousel/1764041481384_1764041444945_cover_1764041423833.jpeg',
    targetUrl: '/album/25'
  },
  {
    id: 3,
    title: '特洛耶·希文（Troye Sivan），1995年6月5日出生于南非共和国约翰内斯堡，澳大利亚流行乐男歌手、影视演员、词曲作者、音乐制作人',
    imageUrl: '/images/carousel/109951169426920702.jpg',
    targetUrl: '/artist/171'
  }
])

const currentIndex = ref(0)
let autoPlayTimer = null

const startAutoPlay = () => {
  if (carouselItems.value.length > 1) {
    autoPlayTimer = setInterval(() => {
      currentIndex.value = (currentIndex.value + 1) % carouselItems.value.length
    }, 5000)
  }
}

const pauseAutoPlay = () => {
  if (autoPlayTimer) {
    clearInterval(autoPlayTimer)
    autoPlayTimer = null
  }
}

const resumeAutoPlay = () => {
  if (!autoPlayTimer && carouselItems.value.length > 1) {
    startAutoPlay()
  }
}

const goToSlide = (index) => {
  currentIndex.value = index
}

const handleItemClick = (item) => {
  if (item.targetUrl) {
    router.push(item.targetUrl)
  }
}

onMounted(() => {
  startAutoPlay()
})

onUnmounted(() => {
  pauseAutoPlay()
})
</script>

<style scoped>
.carousel-container {
  position: relative;
  width: 100%;
  aspect-ratio: 19/7;
  border-radius: 10px;
  overflow: hidden;
}

.carousel-track {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease;
}

.carousel-item {
  flex: 0 0 100%;
  cursor: pointer;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.carousel-indicators button {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: background 0.3s;
}

.carousel-indicators button.active {
  background: white;
}
</style>