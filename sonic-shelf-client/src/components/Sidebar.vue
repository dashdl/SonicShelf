<script setup>
import SidebarItem from "@/components/common/SidebarItem.vue";
import {reactive, watch} from "vue";
import router from "@/router/index.js";

const props = defineProps({
  userPlaylist: {
    type: Array,
    default: [],
  },
  favoritePlaylist: {
    type: Array,
    default: [],
  },
})
watch(() => props.userPlaylist, () => {
})

const handleItemClick = () => {
}

const playlistShow = reactive({
  favorite: false,
  creat: false,
});
const goToPlaylist = (playlistId) => {
  router.push(`/playlist/${playlistId}`);
};

const homeItems = [
  {icon: '/icons/sidebar/home.png', label: '推荐', key: 'home'},
  {icon: '/icons/sidebar/home-b.png', label: '精选', key: 'explore'},
  {icon: '/icons/sidebar/home-b.png', label: '播客', key: 'explore'},
  {icon: '/icons/sidebar/home-b.png', label: '关注', key: 'explore'},
]
const userItems = [
  {icon: '/icons/sidebar/home.png', label: '我喜欢的音乐', key: 'home'},
  {icon: '/icons/sidebar/home-b.png', label: '最近播放', key: 'explore'},
  {icon: '/icons/sidebar/home-b.png', label: '我的播客', key: 'explore'},
  {icon: '/icons/sidebar/home-b.png', label: '我的收藏', key: 'explore'},
]
const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="sidebar-content">
    <div class="title">
      <img src="/logo.svg" style="width: 30px;margin-right: 14px" alt="logo">
      <img src="/sloganCN.svg" style="height:30px" alt="">
    </div>
    <div class="menu-content">
      <SidebarItem
          v-for="item in homeItems"
          :key="item.key"
          :icon="item.icon"
          :label="item.label"
          :alt-text="item.label"
          @click="handleItemClick(item.key)"
      />
      <hr>
      <SidebarItem
          v-for="item in userItems"
          :key="item.key"
          :icon="item.icon"
          :label="item.label"
          :alt-text="item.label"
          @click="handleItemClick(item.key)"
      />
      <hr>
      <div class="playlist-sidebar">
        <div class="playlist-title">
          <div @click="playlistShow.creat=!playlistShow.creat" class="click-content">
            <span style="margin-right: 8px">创建的歌单</span>
            <span style="font-weight: bold;margin-right: 8px">{{ userPlaylist.length }}</span>
            <img v-if="!playlistShow.creat" src="/icons/status/down.svg" style="width: 12px;margin-bottom: 3px;" alt="">
            <img v-if="playlistShow.creat" src="/icons/status/up.svg" style="width: 12px;margin-bottom: 3px;" alt="">
          </div>
          <img id="create" src="/icons/sidebar/add.svg" style="width: 15px;margin-bottom: 3px;" alt="">
        </div>
        <div v-if="playlistShow.creat" v-for="item in userPlaylist" @click="goToPlaylist(item.id)" class="playlist-item">
          <img :src="baseUrl + item.coverImage ||'/images/default/cover.png'"
               style="margin-right: 8px;width: 35px;height: 35px;border-radius: 5px"
               alt="">
          <span>{{ item.title }}</span>
        </div>
      </div>
      <hr>
      <div class="playlist-sidebar">
        <div class="playlist-title">
          <div @click="playlistShow.favorite=!playlistShow.favorite" class="click-content">
            <span style="margin-right: 8px">收藏的歌单</span>
            <span style="font-weight: bold;margin-right: 8px">{{ favoritePlaylist.length }}</span>
            <img v-if="!playlistShow.favorite" src="/icons/status/down.svg" style="width: 12px;margin-bottom: 3px;"
                 alt="">
            <img v-if="playlistShow.favorite" src="/icons/status/up.svg" style="width: 12px;margin-bottom: 3px;" alt="">
          </div>
        </div>
        <div v-if="playlistShow.favorite" v-for="item in favoritePlaylist" @click="goToPlaylist(item.id)" class="playlist-item">
          <img :src="baseUrl + item.coverImage ||'/images/default/cover.png'"
               style="margin-right: 8px;width: 35px;height: 35px;border-radius: 5px"
               alt="">
          <span>{{ item.title }}</span>
        </div>
      </div>
      <hr>
    </div>
  </div>
</template>

<style scoped lang="scss">
.sidebar-content {
  user-select: none;
  height: 100%;
  width: 220px;
  padding-left: 25px;
  padding-right: 20px;
  background-color: $sidebar-color;
  color: #50596b;
  display: flex;
  flex-direction: column;
}

.title {
  display: flex;
  height: 70px;
  align-items: center;
  padding-left: 20px;
}

hr {
  width: 85%;
  margin: 10px 0;
  border: none;
  height: 2px;
  justify-self: center;
  background-color: #e4e8ec;
}

.menu-content {
  flex: 1;
  overflow: hidden;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.playlist-sidebar {
  margin-top: 25px;
  padding-left: 10px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.playlist-title {
  margin-bottom: 10px;
  width: 100%;
  position: relative;

}

.click-content {
  display: flex;
  width: fit-content;
}

.click-content:hover {
  font-weight: bold;
  cursor: pointer;
}

#create {
  position: absolute;
  left: 135px;
  top: 50%;
  transform: translateY(-50%);
}
#create:hover{
  cursor: pointer;
}

.playlist-item {
  margin-bottom: 4px;
  padding: 0 9px;
  height: 45px;
  width: 100%;
  display: flex;
  align-items: center;
  border-radius: 10px;
}

.playlist-item:hover {
  background-color: #e4e8ec;
  cursor: pointer;
}

.playlist-item span {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: break-word;
  word-break: break-word;
  line-height: 1.2;
}

</style>