<script setup>
import {onMounted, reactive, ref} from "vue";
import FollowGrid from "@/components/list/FollowGrid.vue";
import request from "@/utils/request.js";

const userSelect = reactive({
  page: 1,
})
const items = ref([]);

const change = async (page, type) => {
  if (userSelect.page === page) return;
  userSelect.page = page;
  let res
  if (type !== '') {
    res = await request.get("follows/" + type)
  } else {
    res = await request.get("follows")
  }
  if (res.code === "200") {
    items.value = res.data
  }
}

onMounted(async () => {
  let res = await request.get("follows")
  if (res.code === "200") {
    items.value = res.data
  }
})
</script>

<template>
  <div class="follow-container">
    <div class="follow-title">
      我的关注
    </div>
    <div class="separator-content">
      <div class="select-button">
        <div class="button">
          <span @click="change(1,'')" :class="{ 'bold-text': userSelect.page === 1 }">全部</span>
          <div v-if="userSelect.page === 1" class="button-underline"></div>
        </div>
        <div class="button">
          <span @click="change(2,'artist')" :class="{ 'bold-text': userSelect.page === 2 }">歌手</span>
          <div v-if="userSelect.page === 2" class="button-underline"></div>
        </div>
        <div class="button">
          <span @click="change(3,'user')" :class="{ 'bold-text': userSelect.page === 3 }">用户</span>
          <div v-if="userSelect.page === 3" class="button-underline"></div>
        </div>
      </div>
    </div>
    <FollowGrid :items="items"/>
  </div>

</template>

<style scoped>
.follow-container {
  display: flex;
  flex-direction: column;
}

.follow-title {
  margin-bottom: 25px;
  font-size: 24px;
  font-weight: bold;
}

.select-button {
  margin-bottom: 20px;
  width: 185px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 18px;
  color: #888888;
}

.select-button .button:hover {
  cursor: pointer;
}

.bold-text {
  color: #333333;
  font-weight: bold;
}

.select-button .button {
  position: relative;
}

.button-underline {
  position: absolute;
  height: 3px;
  width: 18px;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background-color: #fc3b56;
  border-radius: 2px;
}
</style>