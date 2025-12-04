<script setup>
import {getFullUrl} from "@/utils/urlConfig.js";
import router from "@/router/index.js";

const props = defineProps({
  items: {
    type: Array,
    required: true,
  }
})

const format = (description) => {
  if (!description) return '';

  // 1. 去除所有HTML标签
  const withoutTags = description.replace(/<\/?[^>]+(>|$)/g, '');

  // 2. 去除多余的空格和换行
  const cleanText = withoutTags.replace(/\s+/g, ' ').trim();

  // 3. 截断到30个字符
  if (cleanText.length <= 30) {
    return cleanText;
  } else {
    return cleanText.substring(0, 30);
  }
}

const jump = (item) => {
  if (item.type === 'user') {
    router.push(`/profile/${item.id}`)
  } else {
    router.push(`/artist/${item.id}`)
  }
}
</script>

<template>
  <div class="follow-grid">
    <div @click="jump(item)" v-for="item in items" class="follow-card">
      <div class="cover">
        <img :src="item.coverImage ? getFullUrl(item.coverImage) : '/icons/user.svg'"
             style="height: 60px;width: 60px;object-fit: cover;border-radius: 30px;"
             alt="">
      </div>
      <div class="info">
        <div class="info-name">{{ item.name }}</div>
        <div class="info-description">{{ format(item.description) }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.follow-grid {
  display: grid;
  row-gap: 10px;
  column-gap: 20px;
  grid-template-columns: 1fr 1fr;
}

.follow-card {
  padding: 0 10px;
  height: 80px;
  display: flex;
  flex-direction: row;
  align-items: center;
  border-radius: 15px;
  cursor: pointer;
  overflow: hidden;

  .cover {
    margin-right: 10px;
  }

  .info {
    flex: 1;
    min-width: 0;

    .info-name, .info-description {
      width: 100%;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .info-name {
      font-size: 16px;
    }

    .info-description {
      font-size: 12px;
    }
  }
}

.follow-card:hover {
  background: #ffffff;
}

@media (min-width: 1440px) {
  .follow-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>