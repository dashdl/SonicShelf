<script setup>
import {ref, onMounted} from 'vue'
import router from "@/router/index.js";

const props = defineProps({
  item: {
    type: Object,
  }
})

const backgroundImg = ref(null)
const titleBackground = ref(null)
const infoElement = ref(null)
const extractedColor = ref('')

function getDominantColor(image) {
  const canvas = document.createElement('canvas')
  const ctx = canvas.getContext('2d')

  canvas.width = 50
  canvas.height = 50

  ctx.drawImage(image, 0, 0, canvas.width, canvas.height)

  try {
    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
    const data = imageData.data

    const colorGroups = {}
    let maxCount = 0
    let dominantColor = ''

    for (let i = 0; i < data.length; i += 16) {
      const r = data[i]
      const g = data[i + 1]
      const b = data[i + 2]
      const a = data[i + 3]

      if (a < 200) continue

      const colorKey = `${Math.round(r / 10) * 10},${Math.round(g / 10) * 10},${Math.round(b / 10) * 10}`

      if (!colorGroups[colorKey]) {
        colorGroups[colorKey] = {
          count: 0,
          rSum: 0,
          gSum: 0,
          bSum: 0
        }
      }

      colorGroups[colorKey].count++
      colorGroups[colorKey].rSum += r
      colorGroups[colorKey].gSum += g
      colorGroups[colorKey].bSum += b

      if (colorGroups[colorKey].count > maxCount) {
        maxCount = colorGroups[colorKey].count
        const avgR = Math.round(colorGroups[colorKey].rSum / colorGroups[colorKey].count)
        const avgG = Math.round(colorGroups[colorKey].gSum / colorGroups[colorKey].count)
        const avgB = Math.round(colorGroups[colorKey].bSum / colorGroups[colorKey].count)

        dominantColor = `#${avgR.toString(16).padStart(2, '0')}${avgG.toString(16).padStart(2, '0')}${avgB.toString(16).padStart(2, '0')}`
      }
    }

    return dominantColor || '#e4e7ed'
  } catch (error) {
    console.error('提取颜色失败:', error)
    return '#e4e7ed'
  }
}

function applyDominantColor() {
  if (!backgroundImg.value || !titleBackground.value) {
    return
  }

  const dominantColor = getDominantColor(backgroundImg.value)

  if (titleBackground.value && dominantColor) {
    titleBackground.value.style.backgroundColor = dominantColor
    extractedColor.value = dominantColor
  }
}

function handleMouseEvent(isEnter) {
  if (infoElement.value) {
    if (isEnter && extractedColor.value) {
      infoElement.value.style.background = `linear-gradient(to bottom, ${extractedColor.value}00 0%, ${extractedColor.value}ff 70px)`
    } else {
      infoElement.value.style.background = 'none'
    }
  }
}

onMounted(() => {
  if (backgroundImg.value) {
    if (backgroundImg.value.complete) {
      applyDominantColor()
    } else {
      backgroundImg.value.addEventListener('load', applyDominantColor)
    }
  }
})

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="playlistCard" @click="router.push(`playlist/${item.id}`)" @mouseenter="() => handleMouseEvent(true)" @mouseleave="() => handleMouseEvent(false)">
    <div class="playCount">
      <img style="height: 15px" src="/icons/content/headphone.svg" alt="">
      {{ item.playCount }}
    </div>
    <div class="info" ref="infoElement">
      <div class="title">{{ props.item.title }}</div>
      <div class="songs">
        <span>1 {{ item.songs[0] }}</span>
        <span style="width: calc(100% - 30px);">2 {{ item.songs[1] }}</span>
        <span style="width: calc(100% - 30px);">3 {{ item.songs[2] }}</span>
      </div>
    </div>
    <img id="playButton" src="/icons/player/play.svg" alt="">
    <div class="title-background" ref="titleBackground"/>
    <img id="background" ref="backgroundImg"
         :src="item.coverImage ? baseUrl+item.coverImage : '/images/default/cover.png'"
         style="height: 100%;object-fit: cover"
         crossorigin="anonymous"
         alt="">
  </div>
</template>

<style scoped>
.playlistCard {
  width: 100%;
  aspect-ratio: 14/19;
  position: relative;
  overflow: hidden;
  border-radius: 10px;
}

.playlistCard:hover {
  cursor: pointer;
}

.playlistCard:hover #playButton {
  z-index: 3;
}

.playlistCard:hover .info {
  transform: translateY(-49px);
}

.playCount {
  position: absolute;
  display: flex;
  top: 10px;
  right: 10px;
  flex-direction: row;
  color: #ffffff;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.8);
  line-height: 1;
  z-index: 2;
}

#playButton {
  position: absolute;
  right: -2px;
  bottom: 0;
  height: 40px;
  z-index: 0;
}

.info {
  position: absolute;
  padding: 55px 10px 0 10px;
  height: 116px;
  width: 100%;
  transform: translateY(15px);
  bottom: 0;
  color: #ffffff;
  transition: transform 0.3s ease, background 0.3s ease;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.8);
  z-index: 2;
}

.title {
  margin-bottom: 5px;
  height: 38px;
  font-size: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: normal;
  word-wrap: break-word;
  word-break: break-word;
  line-height: 1.2;
}

.songs {
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.songs span {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.title-background {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 50px;
  z-index: 1;
  background-color: #e4e7ed;
  transition: background-color 0.2s ease;
}

#background {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
</style>