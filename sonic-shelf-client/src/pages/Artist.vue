<script setup>

import {onMounted, reactive, ref, watch} from "vue";
import {useRoute} from "vue-router";
import request from "@/utils/request.js";
import InfiniteTable from "@/components/list/InfiniteTable.vue";
import {ElMessage} from "element-plus";

const route = useRoute();

const emit = defineEmits(['collect']);

const collect = (id) => {
  emit('collect', id)
}

const userSelect = reactive({
  page: 1
});

const artist = reactive({
  id: route.params.artistId,
  coverImage: "",
  name: "",
  description: "",
  translatedName: "",
  followerCount: 0,
  albumCount: 0,
})

const items = ref([])

const data = reactive({
  pageNum: 0,
  pageSize: 40,
  total: 0,
  commentCount: 0,
})

const loading = ref(false)
const hasMore = ref(false)

const initialPage = async () => {
  data.pageNum = 1;
  let res = await request.get('artist/' + route.params.artistId)
  if (res.code === '200') {
    artist.coverImage = res.data.coverImage;
    artist.name = res.data.name;
    artist.description = res.data.description;
    artist.translatedName = res.data.translatedName;
    artist.followerCount = res.data.followerCount;
    artist.albumCount = res.data.albumCount;
  }
  await request.get('artist/musics/' + route.params.artistId, {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
    }
  }).then(res => {
    data.total = res.data.total;
    items.value = res.data.list;
  })
}

const loadMusics = async () => {
  await request.get('artist/musics/' + route.params.artistId, {
    params: {
      pageNum: ++data.pageNum,
      pageSize: data.pageSize,
    }
  }).then(res => {
    hasMore.value = res.data.hasNextPage;
    items.value = [...items.value, ...res.data.list];
  })
}

const loadMore = async () => {
  if (!hasMore.value || loading.value) return
  loading.value = true
  try {
    await loadMusics()
  } catch (error) {
    console.error('加载更多数据失败:', error)
    ElMessage.error('加载更多音乐失败')
  } finally {
    loading.value = false
  }
}

const handleUpdateFavorite = (musicId, newFavoriteState) => {
  const itemIndex = items.value.findIndex(item => item.id === musicId);
  if (itemIndex !== -1) {
    items.value[itemIndex].favorite = newFavoriteState;
  }
};

watch(() => route.params.artistId, () => {
  initialPage();
});

onMounted(async () => {
  await initialPage();
})

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="artist-container">
    <div class="profile-container">
      <img :src="artist.coverImage ? baseUrl + artist.coverImage : '/images/default/avatar.jpg'"
           style="width: 200px;height: 200px;border-radius: 100px;margin-right: 40px"
           alt="">
      <div class="profile-content">
        <div class="name">
          <span style="margin-right: 8px;font-size: 24px;font-weight: bold;">{{ artist.name }}</span>
        </div>
        <div class="profile">
          <span>{{ artist.translatedName }}</span>
          <span style="margin-left: 20px">个人页 ></span>
        </div>
        <div class="button-group">
          <div @click="replace" class="play-button">
            <img src="/icons/player/play.svg" style="height: 25px;" alt="">
            播放全部
          </div>
          <div @click="replace" class="follow-button">
            <img src="/icons/status/follow.svg" style="height: 15px;margin-right: 5px" alt="">
            关注
          </div>
        </div>
      </div>
    </div>
    <div class="separator-content">
      <div class="select-button">
        <div class="button">
          <span @click="userSelect.page=1" :class="{ 'bold-text': userSelect.page === 1 }">歌曲</span>
          <div v-if="userSelect.page === 1" class="button-underline"></div>
        </div>
        <div class="button">
          <div class="numb">
            <span style="font-size:12px;font-weight: bold">{{ artist.albumCount }}</span>
          </div>
          <span @click="userSelect.page=2" :class="{ 'bold-text': userSelect.page === 2 }">专辑</span>
          <div v-if="userSelect.page === 2" class="button-underline"></div>
        </div>
        <div class="button">
          <span @click="userSelect.page=3" :class="{ 'bold-text': userSelect.page === 3 }">歌手详情</span>
          <div v-if="userSelect.page === 3" class="button-underline"></div>
        </div>
      </div>
    </div>
    <InfiniteTable
        v-if="userSelect.page===1"
        :items="items"
        :has-more="hasMore"
        :loading="loading"
        :load-more="loadMore"
        :show-delete="false"
        @update-favorite="handleUpdateFavorite"
        @collect="collect"/>
  </div>
</template>

<style scoped>
.profile-container {
  user-select: none;
  margin-bottom: 25px;
  margin-top: 12px;
  display: flex;
  flex-direction: row;
}

.profile-content {
  display: flex;
  flex-direction: column;
}

.name {
  margin-bottom: 10px;
  display: flex;
}

.profile {
  margin-bottom: 20px;
  display: flex;
  flex-direction: row;
}

.button-group {
  display: flex;
}

.play-button {
  margin-right: 10px;
  display: flex;
  height: 40px;
  width: 95px;
  border-radius: 10px;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  background: linear-gradient(to right, #fc3b5b, #fc3d49);
}

.play-button:hover {
  filter: brightness(0.9);
  cursor: pointer;
}

.follow-button {
  margin-right: 10px;
  display: flex;
  height: 40px;
  width: 80px;
  border-radius: 10px;
  align-items: center;
  justify-content: center;
  color: #333333;
  background: #e9eaec;
  border: 2px solid #e4e8ec;
}

.separator-content {
  display: flex;
  flex-direction: row
}

.select-button {
  margin-bottom: 20px;
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
  margin-right: 40px;
}

.numb {
  position: absolute;
  left: 100%;
  top: -8px;
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