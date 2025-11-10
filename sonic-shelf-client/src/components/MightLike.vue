<script>
export default {
  name: "MightLike"
}
</script>

<template>
  <div class="might-container">
    <div class="hello">
      <span style="font-size: 22px;font-weight: bold">下午好,</span>
      <span>猜你最近喜欢听</span>
    </div>
    <div class="playlists">
      <div class="item-card">
        <img src="/images/default/cover.png" alt="">
      </div>
      <div class="item-card">
        <img src="/images/default/cover.png" alt="">
      </div>
      <div class="item-card">
        <img src="/images/default/cover.png" alt="">
      </div>
      <div class="item-card">
        <img src="/images/default/cover.png" alt="">
      </div>
    </div>
  </div>
</template>

<style scoped>
.might-container{
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: white;
  border-radius: 10px;
}

.item-card{
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.3s ease;
}
.item-card:hover{
  transform: scale(1.05);
  cursor: pointer;
}

.playlists{
  display: grid;
  column-gap: 12px;
  grid-template-columns: repeat(4, minmax(80px, 1fr));
}
</style>