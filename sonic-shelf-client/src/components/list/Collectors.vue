<script setup>
import {onMounted, reactive} from "vue";
import request from "@/utils/request.js";
import router from "@/router/index.js";
import { getFullUrl } from '@/utils/urlConfig';

const props = defineProps({
  targetType: {
    type: String,
    required: true
  },
  targetId: {
    type: String,
    required: true
  }
})

const collectors = reactive([{
  id: '',
  bio: '',
  nickname: '',
  avatar: ''
}])

const loadCollectors = async () => {
  const res = await request.get('favorites/collectors', {
    params: {
      targetType: props.targetType,
      targetId: props.targetId
    }
  })
  if (res.code === '200') {
    collectors.value = [...res.data];
  }
}

const jump = (id) => {
  router.push({
    name: 'Profile',
    params: { userId: id }
  })
}

onMounted(async () => {
  await loadCollectors();
});


</script>

<template>
  <div class="collectors-container">
    <div @click="jump(item.id)" v-for="item in collectors.value" class="collector-card">
      <img :src="getFullUrl(item.avatar) || '/images/default/avatar.jpg'"
           style="margin-bottom: 18px;width: 77%;border-radius: 50%"
           alt="">
      <div class="nickname" style="display: flex">
        {{ item.nickname }}
        <img :src="item.gender === 1 ? '/icons/status/men.svg' : '/icons/status/woman.svg'"
             style="height: 18px;width: 18px;" alt="">
      </div>
      <span style="color: #a9adb7">{{ item.bio }}</span>
    </div>
  </div>
</template>

<style scoped>
.collectors-container {
  display: grid;
  column-gap: 20px;
  grid-template-columns: repeat(4, minmax(180px, 1fr));
  grid-template-rows: 1fr;
  align-items: stretch;
}

.collector-card {
  aspect-ratio: 3/4;
  min-height: 240px;
  min-width: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
}

.collector-card:hover {
  background: #fff;
  cursor: pointer;
}


@media (min-width: 1080px) {
  .collectors-container {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (min-width: 1440px) and (max-width: 1669px) {
  .collectors-container {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (min-width: 1670px) {
  .collectors-container {
    grid-template-columns: repeat(6, 1fr);
  }
}
</style>