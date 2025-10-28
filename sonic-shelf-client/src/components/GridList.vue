<script setup>
defineProps({
  icon: {
    type: String,
    required: true  // 必须传递图标路径
  },
  label: {
    type: String,
    required: true  // 必须传递标签文字
  }
})
</script>

<template>
  <div class="grid-list">
    <div class="grid-container">
      <div class="grid-item">
        <img src="/icons/头像.jpg" alt="">
        <div class="background" style="height:65px;background-color: #dc3545;"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.grid-container {
  padding-right: 40px;
  min-width: 757px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  grid-row-gap: 20px;
  grid-column-gap: 20px;
}

.grid-item{
  display: flex;
  flex-direction: column;
}

.grid-item img {
  border-radius: 10px;
}

.grid-item img:hover {
  filter: brightness(0.9);
}

@media (min-width: 1510px) {
  .grid-list {

    display: flex;
    justify-content: center;
  }

  .grid-container {
    max-width: 1510px;
    grid-template-columns: repeat(6, 1fr);
  }
}

</style>