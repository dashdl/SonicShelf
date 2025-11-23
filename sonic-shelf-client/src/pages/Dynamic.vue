<script setup>

import DynamicCard from "@/components/common/cards/DynamicCard.vue";
import {onMounted, ref} from "vue";
import request from "@/utils/request.js";
import Publish from "@/components/form/Publish.vue";

const dynamics = ref([])
const userSelect = ref(0)
const showPublish = ref(false)

const handleSelect = (id) => {
  userSelect.value = id;
  console.log(id)
}

const loadDynamic = async () => {
  const res = await request.get("dynamic/get-all", {params: {pageNum: 1, pageSize: 100}});
  if (res.code === '200') {
    dynamics.value = res.data.list;
  }
}
onMounted(() => {
  loadDynamic()
})
</script>

<template>
  <div class="dynamic-container">
    <div class="dynamic-header">
      <span style="font-size: 24px;font-weight: bold;">关注</span>
      <div @click="showPublish = true" class="publish">
        <img src="/icons/ui/publish.svg" style="height: 20px" alt="">
        发布
      </div>
    </div>
    <DynamicCard
        v-for="item in dynamics"
        :item="item"
        :key="item.id"
        :user-select="userSelect"
        @select="handleSelect"
    />
  </div>
  <Publish
      v-if="showPublish"
      @close="showPublish = false"
  />
</template>

<style scoped>
.dynamic-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dynamic-header {
  user-select: none;
  position: sticky;
  top: 0;
  width: 100%;
  height: 75px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f7f9fc;
  z-index: 1;
}

.publish {
  height: 40px;
  width: 95px;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to right, #fc3b5b, #fc3d49);
  border-radius: 10px;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
}
</style>