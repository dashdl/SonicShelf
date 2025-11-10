<script>
export default {
  name: "MusicCard"
}
</script>

<template>
  <div class="music-card">
    <div class="info">
      <div class="cover">
        <img id="cover" src="/images/default/cover.png" alt="">
        <img id="playButton" src="/icons/player/play.svg" alt="">
      </div>
      <div class="text">
        <span style="font-size: 16px;color:#333333;">love yourself</span>
        <span>justin bieber</span>
      </div>
    </div>
    <div class="button-group">
      <div class="button"><img src="/icons/player/like.svg" style="width: 20px;" alt=""></div>
      <div class="button"><img src="/icons/status/more.svg" style="height: 20px" alt=""></div>
    </div>
  </div>
</template>

<style scoped>
.music-card {
  user-select: none;
  padding-left: 10px;
  height: 90px;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  border-radius: 10px;
}

.music-card:hover {
  background-color: #ffffff;
  box-shadow: 2px 2px 10px #eaeaea;
}

.music-card:hover #cover {
  filter: brightness(90%);
}

.info {
  display: flex;
}

.cover {
  position: relative;
  margin-right: 10px;
  width: 65px;
  height: 65px;
  border-radius: 5px;
  overflow: hidden;
}

.cover:hover {
  cursor: pointer;

}

.cover:hover #playButton {
  opacity: 1;
  transform: translate(-50%, -50%) scale(1.2);
}

#playButton {
  position: absolute;
  height: 35px;
  top: 50%;
  left: 50%;
  opacity: 70%;
  transition: all 0.1s ease;
  transform: translate(-50%, -50%);
}

.text {
  margin-right: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;

}

.text span {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.button-group {
  display: flex;
  flex-direction: row;
}

.button-group .button {
  display: flex;
  margin-right: 25px;
  align-items: center;
  justify-items: center;
}
.button-group .button:hover{
  cursor: pointer;
  filter: brightness(80%);
}
</style>