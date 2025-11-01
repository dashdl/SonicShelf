<script setup>

</script>

<template>
  <div class="player-component">
    <div class="cover-container">
      <div class="cover-content">
        <img src="/images/default/cover.png" style="height: 45px;border-radius: 23px" alt="">
      </div>
      <div class="info-container">
        <div class="title-container">
          <div class="scroll-wrapper">
            <div class="title-content">
              this is title123123123123123123123123
            </div>
            <div class="title-content">
              this is title123123123123123123123123
            </div>
          </div>
        </div>
        <div class="button-group">
          <div class="button">
            <img src="/icons/player/favorite.svg" style="width: 20px" alt="">
          </div>
          <div class="button">
            <img src="/icons/player/comment.svg" style="width: 20px" alt="">
          </div>
          <div class="button">
            <img src="/icons/player/share.svg" style="width: 20px" alt="">
          </div>
          <div class="button">
            <img src="/icons/player/download.svg" style="width: 20px" alt="">
          </div>
        </div>
      </div>
    </div>
    <div class="player-container">
      <div class="player-menu">
        <div class="menu-button">
          <img src="/icons/player/like.svg" style="height: 22px" alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/last.svg" style="height: 30px;" alt="">
        </div>
        <div class="play-button">
          <img src="/icons/player/play.svg" style="width: 30px;margin-left: 1px;" alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/next.svg" style="height: 30px;" alt="">
        </div>
        <div class="menu-button">
          <img src="/icons/player/order.svg" style="height: 18px" alt="">
        </div>
      </div>
      <div class="progress-container">
        <span>114</span>
        <div class="progress-content">
          <div class="progress-bar"></div>
        </div>
        <span>514</span>
      </div>
    </div>
    <div class="selection-container">
      <div class="selection-button">
        <img src="/icons/player/volume.svg" style="height: 22px" alt="">
      </div>
      <div class="selection-button">
        <img src="/icons/player/playlist.svg" style="height: 23px" alt="">
      </div>
    </div>
  </div>
</template>

<style scoped>
.player-component {
  width: 100%;
  min-width: 1056px;
  padding: 0 30px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.cover-container {
  display: flex;
  justify-self: left;
  flex: 1;
  overflow: hidden;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.cover-content {
  height: 65px;
  width: 65px;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 33px;
  background-color: #0d0d0d;
  animation: rotate 40s linear infinite;
}

.info-container {
  margin-left: 10px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: end;
}

@keyframes scroll {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

.title-container {
  position: absolute;
  top: 3px;
  width: 265px;
  display: flex;
  white-space: nowrap;
  font-size: 18px;
  overflow: hidden;
}

.scroll-wrapper {
  display: flex;
  white-space: nowrap;
  font-size: 18px;
  animation: scroll 10s linear infinite;
}

.title-content {
  flex: 0 0 auto;
  padding-right: 20px; /* 文本间添加间距 */
}

.button-group {
  margin-bottom: 8px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: left;
}

.button-group .button {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 25px;
}

.player-container {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-self: center;
  justify-content: space-between;
}

.player-menu {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.menu-button {
  margin: 0 13px;
}

.play-button {
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 23px;
  background-color: #fc3d4e;
}

.progress-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.progress-container span {
  font-size: 13px;
  color: #c4c8cd;
}

.progress-content {
  margin: 0 8px;
  height: 5px;
  width: 340px;
  background-color: #dadcdf;
  border-radius: 3px;
  display: flex;
  justify-content: left;
}

.progress-bar{
  height: 5px;
  width: 100px;
  background-color: #f26c79;
  border-radius: 3px;
}

.selection-container {
  display: flex;
  justify-content: end;
  align-items: center;
  flex: 1;
}

.selection-button{
  margin-left: 24px;
}
</style>