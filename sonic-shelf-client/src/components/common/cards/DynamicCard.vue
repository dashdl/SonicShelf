<script setup>


</script>

<template>
  <div class="dynamic-card">
    <div class="dynamic-header">
      <img :src="'/images/default/avatar.jpg'"
           style="margin-right: 10px;border-radius: 23px;height: 45px;width: 45px;"
           alt="">
      <div class="dynamic-user">
        <span style="color: dodgerblue">1111</span>
        <span style="font-size: 11px;color: #a9adb7">
          1111
      </span>
      </div>
    </div>
    <div class="dynamic-content">
      <span style="font-size: 18px;">114514</span>
      <div class="dynamic-img">
        <img src="/images/default/test.jpg"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
        <img src="/images/default/test.jpg"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
        <img src="/images/default/test.jpg"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
        <img src="/images/default/test.jpg"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
        <img src="/images/default/test.jpg"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
        <img src="/images/default/test.jpg"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
      </div>
    </div>
  </div>
</template>

<style scoped>
.dynamic-card {
  padding: 20px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 10px;
}

.dynamic-header {
  margin-bottom: 15px;
  display: flex;
  flex-direction: row;
}

.dynamic-user {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.dynamic-img {
  margin-top: 5px;
  display: grid;
  grid-template-columns: repeat(2, 280px);
  gap: 10px;
}
</style>