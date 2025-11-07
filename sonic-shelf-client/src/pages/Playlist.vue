<script setup>

import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import InfiniteTable from "@/components/list/InfiniteTable.vue";
import Comment from "@/components/list/Comment.vue";
import {onMounted, reactive, ref, watch} from "vue";
import {usePlayerStore} from "@/store/player.js";
import router from "@/router/index.js";
import Collectors from "@/components/list/Collectors.vue";

const route = useRoute();

const userSelect = reactive({
  page: 1
});

let musicInfo = ref([])

const data = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 1,
  commentCount: 0,
})

const replace = async () => {
  const currentId = route.params.id;
  await request.get('playlists/' + currentId + '/musics').then(res => {
    usePlayerStore().updatePlaylist(res.data)
    localStorage.setItem("playlist", JSON.stringify(res.data))
    usePlayerStore().playSong(0);
  })
}

const loading = ref(false)
const hasMore = ref(true)

const Info = reactive({
  id: '',
  coverImage: '',
  musicCount: '',
  title: '',
  description: '',
  userName: null,
  createTime: '',
  userAvatar: '',
  playCount: '',
  isFavorite: null,
})

const loadMore = async () => {
  if (!hasMore.value || loading.value) return
  loading.value = true
  try {
    await load()
    // 加载完成后检查是否还有更多数据
    if (data.pageNum * data.pageSize >= data.total) hasMore.value = false
  } catch (error) {
    console.error('加载更多数据失败:', error)
    ElMessage.error('加载更多音乐失败')
  } finally {
    loading.value = false
  }
}

const loadPlaylistData = async (playlistId) => {
  data.pageNum = 1;
  data.total = 1;
  hasMore.value = true;
  musicInfo.value = [];
  await Promise.all([
    request.get('playlists/' + playlistId).then(res => {
      if (res.code === '200') {
        Info.id = res.data.id;
        Info.title = res.data.title;
        Info.description = res.data.description;
        Info.userAvatar = res.data.userAvatar;
        Info.userName = res.data.userName;
        Info.coverImage = res.data.coverImage;
        Info.musicCount = res.data.musicCount;
        Info.playCount = res.data.playCount;
        Info.createTime = res.data.createTime.substring(0, 10);
      } else {
        ElMessage.error("歌单信息获取失败")
      }
    }),
    request.get('playlists/' + playlistId + '/musics', {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
      }
    }).then(res => {
      data.total = res.data.total;
      musicInfo.value = res.data.list;
    }),
    request.get('comments', {
      params: {
        targetType: 'playlist',
        targetId: playlistId
      }
    }).then(res => {
      if (res.code === '200') {
        data.commentCount= res.data.length
      } else {
        ElMessage.error("歌单信息获取失败")
      }
    })
  ]);
}

const load = async () => {
  const currentId = route.params.id;
  await request.get('playlists/' + currentId + '/musics', {
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
    musicInfo.value[itemIndex].isFavorite = newFavoriteState;
  }
};

watch(() => route.params.id, (newId, oldId) => {
  if (newId !== oldId) {
    loadPlaylistData(newId);
  }
});

onMounted(async () => {
  await loadPlaylistData(route.params.id);
});

const goToEditPlaylist = () => {
  const currentId = route.params.id
  router.push(`/Playlist-edit/${currentId}`);
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="main-container">
    <div class="profile-container">
      <div class="cover" style="position: relative;">
        <div class="headphone">
          <img src="/icons/content/headphone.svg" style="width: 15px" alt="">
          <span style="font-size: 12px;color: #ffffff;font-weight: bold;">{{ Info.playCount }}</span>
        </div>
        <img :src="baseUrl + Info.coverImage ||'/images/default/cover.png'"
             style="width: 195px;height: 195px;border-radius: 10px;margin-right: 40px"
             alt="">
      </div>
      <div class="profile-content">
        <div class="title">
          <span style="margin-top: -5px; margin-right: 8px;font-size: 24px;font-weight: bold;">{{ Info.title }}</span>
          <img @click="goToEditPlaylist()" src="/icons/actions/edit.svg" style="width: 20px" alt="">
        </div>
        <div class="description" style="margin-bottom: 10px;">
          <span style="color: #7b818f;">{{ Info.description }}</span>
        </div>
        <div class="profile">
          <img :src="baseUrl+Info.userAvatar||'/images/default/avatar.jpg'"
               style="width: 25px;height: 25px;border-radius: 23px;margin-right: 8px;"
               alt="">
          <span style="font-size: 13px;color: #7b818f;margin-right: 12px">{{ Info.userName }}</span>
          <span style="font-size: 12px;color: #b7bac4">{{ Info.createTime }}创建</span>
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
            <span style="font-size:12px;font-weight: bold">{{ Info.musicCount }}</span>
          </div>
          <span @click="userSelect.page=1" :class="{ 'bold-text': userSelect.page === 1 }">歌曲</span>
          <div v-if="userSelect.page === 1" class="button-underline"></div>
        </div>
        <div class="button">
          <div class="numb">
            <span style="font-size:12px;font-weight: bold">{{ data.commentCount }}</span>
          </div>
          <span @click="userSelect.page=2" :class="{ 'bold-text': userSelect.page === 2 }">评论</span>
          <div v-if="userSelect.page === 2" class="button-underline"></div>
        </div>
        <div class="button">
          <span @click="userSelect.page=3">收藏者</span>
          <div v-if="userSelect.page === 3" class="button-underline"></div>
        </div>
      </div>
      <div class="search">
        <img src="/icons/navigation/search.svg" style="height: 13px;margin-right: 3px;" alt="">
        <input style="color: #999999;font-size: 13px" placeholder="搜索">
      </div>
    </div>
    <div class="list-container">
      <InfiniteTable
          v-if="userSelect.page===1"
          :items="musicInfo"
          :has-more="hasMore"
          :loading="loading"
          :load-more="loadMore"
          @update-favorite="handleUpdateFavorite"
      />
      <Comment
          v-if="userSelect.page===2"
          :target-id="route.params.id"
          :target-type="'playlist'"
      />
      <Collectors
        v-if="userSelect.page===3"
        :target-id="route.params.id"
        :target-type="'playlist'"
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

.headphone {
  position: absolute;
  display: flex;
  right: 48px;
  top: 8px;
  z-index: 1;
}

.profile-content {
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
  left: 37px;
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