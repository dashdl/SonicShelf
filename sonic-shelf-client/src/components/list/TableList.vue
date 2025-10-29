<script setup>
defineProps({
  info: [{
    title: {type: String, required: false},
    playback: {type: String, required: false},
    total: {type: String, required: false},
    cover: {type: String, required: false},
    link: {type: String, required: false},
  }],
})
</script>

<template>
  <div class="table-container">
    <div class="table-row" style="">
      <div class="left-cell">
        <div class="rank-cell">
          <span>#</span>
        </div>
        <div class="title-cell">
          <span>标题</span>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell">
          <span>歌曲数</span>
        </div>
        <div class="creator-cell">
          <span>创建者</span>
        </div>
      </div>
    </div>

    <div class="table-row">
      <div class="left-cell">
        <div class="rank-cell">
          <span id="title" style="font-size: 12px">01</span>
          <div class="play-button">
            <img src="/icons/play.svg" style="width: 20px;filter: brightness(0.4);" alt="">
          </div>
        </div>
        <div class="title-cell">
          <div class="cover">
            <img src="/icons/头像.jpg" style="width: 50px;border-radius: 8px;margin-right: 10px" alt="">
          </div>
          <div class="title">
            <span
                style="font-size: 20px;color: #333333;">title</span>
          </div>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell"><span>14首</span></div>
        <div class="creator-cell"><span>zhongxin</span></div>
      </div>
    </div>

  </div>
</template>

<style scoped>
span {
  font-size: 14px;
  color: #7b818f;
}

.table-container {
  display: flex;
  flex-direction: column;
  user-select: none;
}

.table-row {
  height: 65px;
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-between;
  border-radius: 10px;
}

.table-row:not(:first-child):hover{
  background-color: #ffffff;
  box-shadow: 0 2px 2px rgba(0,0,0,0.02);
}
.table-row:hover .play-button{
  z-index: 1;
}
.table-row:hover #title{
  z-index: -1;
}

.left-cell {
  display: flex;
  flex-direction: row;
}

.right-cell {
  min-width: 320px;
  max-width: 560px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.rank-cell {
  width: 50px;
  position: relative;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.play-button{
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: -1;
}

.play-button:hover{
  cursor: pointer;
}

.title-cell {
  display: flex;
  align-items: center;
}

.total-cell {
  min-width: 150px;
}

.creator-cell {
  min-width: 170px;
}

</style>