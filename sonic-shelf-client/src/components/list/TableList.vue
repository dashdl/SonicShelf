<script setup>
defineProps({
  info: [{
    title: {type: String, required: false},
    playback: {type: String, required: false},
    total: {type: String, required: false},
    cover: {type: String, required: false},
    link: {type: String, required: false},
    creator: {type: String, required: false},
  }],
})

// const infoTest = [
//   {
//     title: '/icons/avatar.jpg',
//     playback: "test",
//     total: 'test',
//     cover: '/icons/avatar.jpg',
//     creator: '/icons/avatar.jpg',
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
  <div class="table-container">
    <div class="table-row" style="margin-bottom: -12px">
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

    <div class="table-row" v-for="(item, index) in info" :key="index">
      <div class="left-cell">
        <div class="rank-cell">
          <span id="title" style="font-size: 12px">{{index+1}}</span>
          <div class="play-button">
            <img src="/icons/player/play.svg" style="width: 20px;filter: brightness(0.4);" alt="">
          </div>
        </div>
        <div class="title-cell">
          <div class="cover">
            <img :src="item.cover||'/icons/avatar.jpg'" style="width: 50px;height: 50px;border-radius: 8px;margin-right: 10px;object-fit: cover;" alt="">
          </div>
          <div class="title">
            <span style="font-size: 20px;color: #333333;">{{ item.title }}</span>
          </div>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell"><span>{{ item.total }}首</span></div>
        <div class="creator-cell"><span>{{item.creator}}</span></div>
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
  margin-bottom: 4px;
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
  max-width: 950px;
  min-width: 450px;
  display: flex;
  flex-direction: row;
}

.right-cell {
  width: 100%;
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
  width: 40%;
  max-width: 270px;
  min-width: 150px;
}

.creator-cell {
  width: 60%;
  max-width: 295px;
  min-width: 170px;
}

</style>