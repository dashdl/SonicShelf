<script setup>

import router from "@/router/index.js";

const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  type: {
    type:String,
    required:true,
  },
})
const emit = defineEmits()

const jump=()=>{
  if(props.type==='user'){
    router.push(`/profile/${props.item.id}`)
  }else {
    router.push(`/artist/${props.item.id}`)
  }
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div @click="jump" class="user-card">
    <img :src="props.type==='user' ? baseUrl + props.item.avatar : baseUrl + props.item.coverImage"
         style="margin-bottom: 18px;width: 77%;border-radius: 50%;object-fit: cover;aspect-ratio: 1 / 1;"
         alt="">
    <div class="nickname" style="display: flex">
      {{ props.type === 'user' ? props.item.nickname : props.item.name }}
      <img v-if="props.type==='user'" :src="props.item.gender === 1 ? '/icons/status/men.svg' : '/icons/status/woman.svg'"
           style="height: 18px;width: 18px;" alt="">
    </div>
    <span v-if="props.type==='user'" style="color: #a9adb7;width: 100%;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;text-align: center;">{{ props.item.bio }}</span>
    <span v-if="props.type==='artist'" style="color: #a9adb7;">单曲：{{ props.item.musicCount }}</span>
  </div>
</template>

<style scoped>
.user-card {
  aspect-ratio: 3/4;
  min-height: 240px;
  min-width: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.user-card:hover {
  background: #fff;
  cursor: pointer;
}
</style>