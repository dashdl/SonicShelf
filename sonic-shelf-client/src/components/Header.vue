<script setup>

</script>

<template>
  <div class="header-container">
    <div class="left-content">
      <div class="left-button">

      </div>
      <div class="search">
        <img src="/icons/logo.png" style="height: 20px;margin-left: 10px;margin-right: 10px;" alt="">
        <input>
      </div>
    </div>
    <div class="right-content">
      <img src="" alt="">
      <span>1</span>
      <img src="" alt="">
    </div>
  </div>
</template>

<style scoped lang="scss">
.header-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding-right: 45px;
  align-items: center;
}

.left-content {
  display: flex;
  height: 40px;
}

.right-content {
  display: flex;
  flex-direction: column;
  height: 40px;
}

.left-button {
  display: block;
  margin-right: 10px;
  height: 40px;
  width: 30px;
  border: 1px solid #e4e8ec;
  border-radius: $radius;
  justify-items: center;
  align-items: center;
}

.search{
  display: flex;
  width: 260px;
  border-radius: $radius;
  border: 1px solid #e4e8ec;
  align-items: center;

}
.search input {
  border: none !important;
  outline: none !important;
  background: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  padding: 0 !important;
  margin: 0 !important;
  font: inherit !important;
  color: inherit !important;
}

</style>