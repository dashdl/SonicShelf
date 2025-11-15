<script setup>

</script>

<template>
  <div class="grid-item">
    <div class="record"></div>
    <img :src="'/images/default/cover.png'" style="height: 100%;object-fit: cover;"
         alt="">
    <div class="background"></div>
    <div class="play-button">
      <img src="/icons/player/play.svg" style="width: 40px" alt="">
    </div>
    <div class="info">
      <span style="color: #333333;font-size: 14px;margin-bottom: -2px">专辑</span>
      <span style="color: #888888;font-size: 12px">114514 首</span>
    </div>
  </div>
</template>

<style scoped>
.grid-item {
  margin-top: 20px;
  width: 180px;
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

.record{
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  aspect-ratio: 1/1;
  border-radius: 50%;
  background-color: #0d0d0d;
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
</style>