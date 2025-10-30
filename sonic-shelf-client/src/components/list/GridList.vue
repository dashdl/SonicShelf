<script setup>
defineProps({
  info:[{
    title: {type: String, required: false},
    playback: {type: String, required: false},
    total: {type: String, required: false},
    cover: {type: String, required: false},
    link: {type: String, required: false},
  }],
})

// const infoTest = [
//   {
//     title: '/icons/avatar.jpg',
//     playback: "test",
//     total: 'test',
//     cover: '/icons/avatar.jpg',
//   },
//   {
//     title: '/icons/avatar.jpg',
//     playback: "test",
//     total: 'test',
//     cover: '',
//   },
//   {
//     title: '/icons/avatar.jpg',
//     playback: "test",
//     total: 'test',
//     cover: '/icons/test.png',
//   }
// ]

</script>

<template>
  <div class="grid-list">
    <div class="grid-container">
      <div v-for="item in info" class="grid-item">
        <img :src="item.cover||'/images/default/cover.png'" style="height: 100%;object-fit: cover;" alt="">
        <div class="background"></div>
        <div class="play-button">
          <img src="/icons/player/play.svg" style="width: 26px" alt="">
        </div>
        <div class="info">
          <span style="color: #333333;font-size: 14px;margin-bottom: -2px">{{ item.title }}</span>
          <span style="color: #888888;font-size: 12px">{{ item.total }}</span>
        </div>
        <div class="headphone">
          <img v-if="item.playback>=1" src="/icons/content/headphone.svg" style="width: 15px" alt="">
          <span v-if="item.playback>=1" style="font-size: 12px;color: #ffffff;font-weight: bold;">{{item.playback }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.grid-container {

  min-width: 757px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  grid-row-gap: 20px;
  grid-column-gap: 20px;
  user-select: none;
}

.grid-item {
  position: relative;
  display: flex;
  flex-direction: column;
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
  right: 18px;
  bottom: 65px;
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