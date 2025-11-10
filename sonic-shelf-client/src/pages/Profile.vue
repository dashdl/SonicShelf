<script setup>
import router from "@/router/index.js";
import GridList from "@/components/list/GridList.vue";
import {onBeforeUnmount, onMounted, onUnmounted, reactive, ref, watch} from "vue";
import TableList from "@/components/list/TableList.vue";
import {ElMessage} from "element-plus";
import request from "@/utils/request.js";
import { useRoute } from 'vue-router';
const route = useRoute();

const userId = ref(null);

const userInfo = reactive({
  avatar: '',
  nickname: '',
  bio: '',
  gender: '',
  location: '',
  followersCount: '',
  followingCount: '',
})

let playlistInfo = reactive([])
let favoritePlaylistInfo = reactive([])

const page = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0,
  pages: 0,
})
const next = () => {
  if (page.pageNum * page.pageSize <= page.total) {
    page.pageNum++
    load()
  }
}
const last = () => {
  if (page.pageNum > 1) {
    page.pageNum--
    load()
  }
}
const jump = (n) => {
  page.pageNum = n;
  load()
}

const pageFavorite = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0,
  pages: 0,
})
const nextFavorite = () => {
  if (pageFavorite.pageNum * pageFavorite.pageSize <= pageFavorite.total) {
    pageFavorite.pageNum++
    loadFavorites()
  }
}
const lastFavorite = () => {
  if (pageFavorite.pageNum > 1) {
    pageFavorite.pageNum--
    loadFavorites()
  }
}
const jumpFavorite = (n) => {
  pageFavorite.pageNum = n;
  loadFavorites()
}

const load = async () => {
  await request.get("playlists", {
    params: {
      pageNum: page.pageNum,
      pageSize: page.pageSize,
      id: userId.value,
    }
  }).then(res => {
    if (res.code === '200') {
      playlistInfo.length = 0;
      for (const item of res.data.list) {
        playlistInfo.push(item);
      }
      page.total = res.data.total;
      page.pageNum = res.data.pageNum;
      page.pageSize = res.data.pageSize;
      page.pages = res.data.pages;

    } else {
      ElMessage.error("歌单列表获取失败")
    }
  })
}

const loadFavorites = async () => {
  await request.get("favorites/playlists", {
    params: {
      pageNum: pageFavorite.pageNum,
      pageSize: pageFavorite.pageSize,
      id: userId.value,
    }
  }).then(res => {
    if (res.code === '200') {
      favoritePlaylistInfo.length = 0;  //先清空再加入，因为会破坏响应式数据，直接替换就会像之前一样出现加载问题
      for (const item of res.data.list) {
        favoritePlaylistInfo.push(item);
      }
      pageFavorite.total = res.data.total;
      pageFavorite.pageNum = res.data.pageNum;
      pageFavorite.pageSize = res.data.pageSize;
      pageFavorite.pages = res.data.pages;
    } else {
      ElMessage.error("收藏列表获取失败")
    }
  })
}

const loadPlaylist = async () => {
  await request.get("playlists", {
    params: {
      pageNum: page.pageNum,
      pageSize: page.pageSize,
      id: userId.value,
    }
  }).then(res => {
    if (res.code === '200') {
      playlistInfo.length = 0;  //先清空再加入，因为会破坏响应式数据，直接替换就会像之前一样出现加载问题
      for (const item of res.data.list) {
        playlistInfo.push(item);
      }
      page.total = res.data.total;
      page.pageNum = res.data.pageNum;
      page.pageSize = res.data.pageSize;
      page.pages = res.data.pages;
    } else {
      ElMessage.error("歌单列表获取失败")
    }
  })
}

const loadUserInfo = async (targetUserId) => {
  userInfo.avatar = '';
  userInfo.nickname = '';
  userInfo.bio = '';
  userInfo.gender = '';
  userInfo.location = '';
  userInfo.followersCount = '';
  userInfo.followingCount = '';
  request.get("users/profile/" + targetUserId).then(res => {
    if (res.code === '200') {
      userInfo.avatar = res.data.avatar || '';
      userInfo.nickname = res.data.nickname || '';
      userInfo.bio = res.data.bio || '';
      userInfo.gender = res.data.gender || '';
      userInfo.location = res.data.location || '';
      userInfo.followersCount = res.data.followers_count || 0;
      userInfo.followingCount = res.data.following_count || 0;
    }
  })
}

const resetPageState = () => {
  userInfo.avatar = '';
  userInfo.nickname = '';
  userInfo.bio = '';
  userInfo.gender = '';
  userInfo.location = '';
  userInfo.followersCount = '';
  userInfo.followingCount = '';

  playlistInfo.length = 0;
  favoritePlaylistInfo.length = 0;

  page.pageNum = 1;
  page.pageSize = 20;
  page.total = 0;
  page.pages = 0;

  pageFavorite.pageNum = 1;
  pageFavorite.pageSize = 20;
  pageFavorite.total = 0;
  pageFavorite.pages = 0;
};

watch(() => route.params.userId, (newUserId, oldUserId) => {
  if (newUserId && newUserId !== oldUserId) {
    userId.value = newUserId;
    resetPageState();
    loadUserInfo(newUserId);
    loadPlaylist();
    loadFavorites();
  }
}, {deep: true});

const listSwitch = reactive({
  createTable: false,
  favoritesTable: false,
  createGrid: true,
  favoritesGrid: true,
})
const createGrid = () => {
  listSwitch.createGrid = true;
  listSwitch.createTable = false;
}
const createTable = () => {
  listSwitch.createGrid = false;
  listSwitch.createTable = true;
}
const favoritesGrid = () => {
  listSwitch.favoritesGrid = true;
  listSwitch.favoritesTable = false;
}
const favoritesTable = () => {
  listSwitch.favoritesGrid = false;
  listSwitch.favoritesTable = true;
}

onMounted(() => {
  resetPageState();
  userId.value = route.params.userId
  loadUserInfo(userId.value);
  loadPlaylist();
  loadFavorites();
});

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="main-container">
    <div class="profile-container">
      <img :src="baseUrl + userInfo.avatar||'/images/default/avatar.jpg'"
           style="width: 200px;height: 200px;border-radius: 100px;margin-right: 40px"
           alt="">
      <div class="profile-content">
        <div class="nickname">
          <span style="margin-right: 8px;font-size: 24px;font-weight: bold;">{{ userInfo.nickname }}</span>
          <img @click="router.push('/profile-settings')" src="/icons/actions/edit.svg" style="width: 20px" alt="">
        </div>
        <div class="follow">
            <span style="margin-right: 12px">
              关注 {{ userInfo.followingCount }}
            </span>
          <hr>
          <span>
              粉丝 {{ userInfo.followersCount }}
            </span>
        </div>
        <div class="profile">
          <span style="color: #666666;">简介：{{ userInfo.bio }}</span>
          <span style="color: #999999;">地区：{{ userInfo.location }}</span>
        </div>
      </div>
    </div>
    <div class="favorites-content">
      <div class="select-button">
        <div class="button"><span>歌单</span></div>
        <div class="button"><span>笔记</span></div>
        <div class="button"><span>播客</span></div>
      </div>
      <div id="anchor1"></div>
      <div class="separate-content">
        <div class="left-content">
          <span style="font-size: 20px;font-weight: bold;color: #555555;">我创建的歌单</span>
        </div>
        <div class="right-content">
          <img @click="createGrid" src="/icons/view/grid.svg" style="width: 17px; margin-right: 8px" alt="">
          <img @click="createTable" src="/icons/view/table.svg" style="width: 20px" alt="">
        </div>
      </div>
      <GridList v-if="listSwitch.createGrid" style="max-width: 1490px; margin-bottom: 25px;"
                :info="playlistInfo"
      />
      <TableList v-if="listSwitch.createTable" style="max-width: 1490px; margin-bottom: 50px;"
                 :info="playlistInfo"
      />
      <div class="page-container">
        <div class="button-group">
          <a href="#anchor1">
            <div class="button" @click="last">
              <img src="/icons/status/left.svg" style="width: 14px" alt="">
            </div>
          </a>
          <a v-for="n in page.pages" :key="n" @click="jump(n)" href="#anchor1">
            <div class="button">
              <span style="padding-top: 3px">
                {{ n }}
              </span>
            </div>
          </a>
          <a href="#anchor1">
            <div class="button" @click="next">
              <img src="/icons/status/right.svg" style="width: 14px" alt="">
            </div>
          </a>
        </div>
      </div>
      <div id="anchor2"></div>
      <div class="separate-content">
        <div class="left-content">
          <span style="font-size: 20px;font-weight: bold;color: #555555;">我收藏的歌单</span>
        </div>
        <div class="right-content">
          <img @click="favoritesGrid" src="/icons/view/grid.svg" style="width: 17px; margin-right: 8px" alt="">
          <img @click="favoritesTable" src="/icons/view/table.svg" style="width: 20px" alt="">
        </div>
      </div>
      <GridList v-if="listSwitch.favoritesGrid" style="max-width: 1490px; margin-bottom: 50px;"
                :info="favoritePlaylistInfo"
      />
      <TableList v-if="listSwitch.favoritesTable" style="max-width: 1495px;margin-bottom: 50px;"
                 :info="favoritePlaylistInfo"
      />
      <div class="page-container">
        <div class="button-group">
          <a href="#anchor2">
            <div class="button" @click="lastFavorite">
              <img src="/icons/status/left.svg" style="width: 14px" alt="">
            </div>
          </a>
          <a v-for="n in pageFavorite.pages" :key="n" @click="jumpFavorite(n)" href="#anchor2">
            <div class="button">
              <span style="padding-top: 3px">
                {{ n }}
              </span>
            </div>
          </a>
          <a href="#anchor2">
            <div class="button" @click="nextFavorite">
              <img src="/icons/status/right.svg" style="width: 14px" alt="">
            </div>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
}

.profile-container {
  margin-bottom: 25px;
  margin-top: 12px;
  display: flex;
  flex-direction: row;
}

.profile-content {
  display: flex;
  flex-direction: column;
}

.nickname {
  margin-bottom: 5px;
  display: flex;
}

.nickname img:hover {
  cursor: pointer;
}

.follow {
  margin-bottom: 5px;
  display: flex;
  align-items: center;
}

.follow span {
  font-size: 18px;
}

hr {
  margin-right: 12px;
  height: 70%;
  width: 1px;
  border: none;
  background-color: #e4e8ec;
}

.profile {
  display: flex;
  flex-direction: column;
}

.select-button {
  margin-bottom: 20px;
  width: 165px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  font-size: 18px;
}

.separate-content {
  width: 100%;
  max-width: 1495px;
  margin-bottom: 10px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.right-content {
  display: flex;
  flex-direction: row;
}

.right-content img:hover {
  cursor: pointer;
}

.page-container {
  margin-bottom: 40px;
  display: flex;
  flex-direction: row;
  height: 50px;
  width: 100%;
  max-width: 1660px;
  align-items: center;
  justify-content: center;
}

.button-group {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}

.button-group .button {
  display: flex;
  width: 32px;
  height: 40px;
  border-radius: 8px;
  align-items: center;
  justify-content: center;
  color: #7b818f;
  margin: 0 5px;
  background-color: #eaedf1;
  cursor: pointer;
}

.button:hover {
  background-color: #eaedf1;
}
</style>