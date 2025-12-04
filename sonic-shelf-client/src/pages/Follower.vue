<script setup>
import {onMounted, reactive, ref} from "vue";
import FollowGrid from "@/components/list/FollowGrid.vue";
import request from "@/utils/request.js";
import {useUserStore} from "../store/userStore.js";

const userSelect = reactive({
  page: 1,
})
const items = ref([]);

onMounted(async () => {
  let res = await request.get("follows/follower")
  if (res.code === "200") {
    items.value = res.data
  }
})
</script>

<template>
  <div class="follow-container">
    <div class="follow-title">
      {{useUserStore().getNickname}}的粉丝
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