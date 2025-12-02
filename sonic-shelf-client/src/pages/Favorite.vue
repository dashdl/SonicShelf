<script setup>
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import InfiniteTable from "@/components/list/InfiniteTable.vue";
import Comment from "@/components/list/Comment.vue";
import {computed, onMounted, reactive, ref, watch} from "vue";
import {usePlayerStore} from "@/store/player.js";
import router from "@/router/index.js";
import Collectors from "@/components/list/Collectors.vue";
import {useUserStore} from "@/store/userStore.js";

const route = useRoute();
const userStore = useUserStore();

const emit = defineEmits(['collect'])
const collect = (id) => {
  emit('collect', id)
}

const userSelect = reactive({
  page: 1
});

let musicInfo = ref([])

let collectorNum = 0;

const data = reactive({
  pageNum: 1,
  pageSize: 40,
  total: 1,
  commentCount: 0,
})

const replace = async () => {
  await request.get('favorites/musics').then(res => {
    const playerStore = usePlayerStore();
    playerStore.updatePlaylist(res.data)
    localStorage.setItem("playlist", JSON.stringify(res.data))
    playerStore.playSong(0)
    playerStore.isPlaylist = false;
    localStorage.setItem("isPlaylist", JSON.stringify(playerStore.isPlaylist))
  })
}

const loading = ref(false)
const hasMore = ref(false)

const Info = reactive({
  id: '',
  coverImage: '',
  musicCount: '',
  title: '',
  description: '',
  nickname: null,
  userId: null,
  createTime: '',
  userAvatar: '',
  playCount: '',
  favorite: false,
})

const loadMore = async () => {
  if (!hasMore.value || loading.value) return
  loading.value = true
  try {
    await load()
    if (data.pageNum * data.pageSize >= data.total) hasMore.value = false
  } catch (error) {
    console.error('加载更多数据失败:', error)
    ElMessage.error('加载更多音乐失败')
  } finally {
    loading.value = false
  }
}

const loadPlaylistData = async () => {
  userSelect.page = 1;
  data.pageNum = 1;
  data.total = 1;
  musicInfo.value = [];
  await Promise.all([
    request.get('favorites/musics', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
      }
    }).then(res => {
      data.total = res.data.total;
      musicInfo.value = res.data.list;
      hasMore.value = res.data.hasNextPage;
    }),
    // request.get('comments', {
    //   params: {
    //     targetType: 'music',
    //     targetId: playlistId
    //   }
    // }).then(res => {
    //   if (res.code === '200') {
    //     data.commentCount = res.data.length
    //   } else {
    //     ElMessage.error("歌单信息获取失败")
    //     console.log('isMe')
    //   }
    // }),
    // request.get('favorites/collectors', {
    //   params: {
    //     targetType: 'playlist',
    //     targetId: route.params.id
    //   }
    // }).then((res) => {
    //   collectorNum = res.data.length;
    // })
  ]);
}

const load = async () => {
  await request.get('favorites/musics', {
    params: {
      pageNum: ++data.pageNum,
      pageSize: data.pageSize,
    }
  }).then(res => {
    musicInfo.value = [...musicInfo.value, ...res.data.list];
  })
}

const handleUpdateFavorite = (musicId, newFavoriteState) => {
  const itemIndex = musicInfo.value.findIndex(item => item.id === musicId);
  if (itemIndex !== -1) {
    musicInfo.value[itemIndex].favorite = newFavoriteState;
  }
};

onMounted(async () => {
  await loadPlaylistData();
});

const coverImage = computed(() => {
  if (musicInfo.value.length > 0 && musicInfo.value[0].coverImage) {
    return baseUrl + musicInfo.value[0].coverImage
  }
  return '/images/default/cover.png'
})

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="main-container">
    <div class="profile-container">
      <div class="cover" style="position: relative;">
        <img :src="coverImage"
             style="width: 195px;height: 195px;border-radius: 10px;margin-right: 40px"
             alt="">
      </div>
      <div class="profile-content">
        <div class="title">
          <span style="margin-top: -5px; margin-right: 8px;font-size: 24px;font-weight: bold;">我喜欢的音乐</span>
        </div>
        <div class="profile">
          <img id="profile" @click="router.push('/profile/'+userStore.getUserId)"
               :src="userStore.getAvatar !== '/icons/user.svg' ? baseUrl+userStore.getAvatar : '/icons/user.svg'"
               style="width: 25px;height: 25px;border-radius: 23px;margin-right: 8px;"
               alt="">
          <span id="profile" @click="router.push('/profile/'+userStore.getUserId)"
                style="font-size: 13px;color: #7b818f;margin-right: 12px">
            {{ userStore.getNickname }}
          </span>
          <span style="font-size: 12px;color: #b7bac4">{{ userStore.getCreatedTime }}创建</span>
        </div>
        <div class="button-group">
          <div @click="replace" class="button"
               style="color: #ffffff;background: linear-gradient(to right, #fc3b5b, #fc3d49);">
            <img src="/icons/player/play.svg" style="height: 15px;margin-right: 5px" alt="">
            播放全部
          </div>
        </div>
      </div>
    </div>
    <div class="favorites-content">
      <div class="select-button">
        <div class="button">
          <div class="numb">
            <span style="font-size:12px;font-weight: bold">{{ data.total }}</span>
          </div>
          <span @click="userSelect.page=1" :class="{ 'bold-text': userSelect.page === 1 }">歌曲</span>
          <div v-if="userSelect.page === 1" class="button-underline"></div>
        </div>
        <!--        <div class="button">-->
        <!--          <div class="numb">-->
        <!--            <span style="font-size:12px;font-weight: bold">{{ data.commentCount }}</span>-->
        <!--          </div>-->
        <!--          <span @click="userSelect.page=2" :class="{ 'bold-text': userSelect.page === 2 }">评论</span>-->
        <!--          <div v-if="userSelect.page === 2" class="button-underline"></div>-->
        <!--        </div>-->
        <!--        <div class="button">-->
        <!--          <div class="numb">-->
        <!--            <span style="font-size:12px;font-weight: bold">{{ collectorNum }}</span>-->
        <!--          </div>-->
        <!--          <span @click="userSelect.page=3" :class="{ 'bold-text': userSelect.page === 3 }">收藏者</span>-->
        <!--          <div v-if="userSelect.page === 3" class="button-underline"></div>-->
        <!--        </div>-->
      </div>
      <div class="search">
        <img src="/icons/navigation/search.svg" style="height: 13px;margin-right: 3px;" alt="">
        <input style="color: #999999;font-size: 13px" placeholder="搜索">
      </div>
    </div>
    <div class="list-container">
      <InfiniteTable
          :items="musicInfo"
          :has-more="hasMore"
          :loading="loading"
          :load-more="loadMore"
          :show-delete="false"
          @update-favorite="handleUpdateFavorite"
          @collect="collect"
      />
    </div>
  </div>
</template>

<style scoped>
.main-container {
  user-select: none;
  display: flex;
  flex-direction: column;
}

.profile-container {
  margin-bottom: 25px;
  margin-top: 12px;
  display: flex;
  flex-direction: row;
}

#profile:hover {
  cursor: pointer;
}

.profile-content {
  position: relative;
  display: flex;
  flex-direction: column;
}

.title {
  margin-bottom: 5px;
  display: flex;
}

.title img:hover {
  cursor: pointer;
}

.profile {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 54px;
}

.button-group {
  position: absolute;
  bottom: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.button-group .button {
  display: flex;
  height: 40px;
  width: 95px;
  border-radius: 10px;
  align-items: center;
  justify-content: center;
}

.button-group .button:hover {
  cursor: pointer;
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

.favorites-content {
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.select-button .button {
  position: relative;
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

.search {
  height: 30px;
  width: 75px;
  padding-left: 15px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-items: center;
  border: 1px solid #e4e8ec;
  border-radius: 15px;
  background-color: #ffffff;
}

.search:focus-within {
  width: 180px;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

input {
  width: 100%;
}

.list-container {
  width: 100%;
  flex: 1;
}
</style>