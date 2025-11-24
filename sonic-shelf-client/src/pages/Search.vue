<script setup>

import {reactive, ref, watch} from "vue";
import Comprehensive from "@/components/list/Comprehensive.vue";
import InfiniteTable from "@/components/list/InfiniteTable.vue";
import request from "@/utils/request.js";
import PlaylistTable from "@/components/list/PlaylistTable.vue";
import AlbumTable from "@/components/list/AlbumTable.vue";
import UserTable from "@/components/list/UserTable.vue";

const props = defineProps({
  keyword: {
    type: String,
    required: true,
    default: 'justin'
  }
})

const emit = defineEmits()
const userSelect = reactive({
  page: 1,
})

const userSelected = (page) => {
  userSelect.page = page;
}

const loadMore = async () => {

}

const musics = ref([])

watch(() => userSelect.page, async (newPage, oldPage) => {
  if (newPage === 2) {
    let res = await request.get("search/musics", {params: {pageNum: 1, pageSize: 1000, keyword: props.keyword}})
    if (res.code === '200') {
      musics.value = res.data.list
    }
  }
})

watch(() => props.keyword, async (newKeyword, oldKeyword) => {
  // if (newPage === 2) {
  //   let res = await request.get("search/musics", {params: {pageNum: 1, pageSize: 1000, keyword: props.keyword}})
  //   if (res.code === '200') {
  //     musics.value = res.data.list
  //   }
  // }
  userSelect.page = 1;

})

// const loadHistory = async () => {
//   const res = await request.get('play-histories')
//   if (res.code === '200') {
//     historyMusics.value = res.data.list
//   }
// }
// onMounted(async () => {
//   await loadHistory()
//   await request.get('artist/' + 152 + '/albums').then(res => {
//     albums.value = res.data;
//   })
// })
</script>

<template>
  <div class="search-container">
    <span style="font-size: 24px;font-weight: bold;color: #283248;">{{ props.keyword || '' }}</span>
    <div v-if="props.keyword!==''" class="separator-container">
      <div class="separator-content">
        <div class="select-button">
          <div @click="search" class="button">
            <span @click="userSelect.page=1" :class="{ 'bold-text': userSelect.page === 1 }">综合</span>
            <div v-if="userSelect.page === 1" class="button-underline"></div>
          </div>
          <div @click="search" class="button">
            <span @click="userSelect.page=2" :class="{ 'bold-text': userSelect.page === 2 }">单曲</span>
            <div v-if="userSelect.page === 2" class="button-underline"></div>
          </div>
          <div @click="search" class="button">
            <span @click="userSelect.page=3" :class="{ 'bold-text': userSelect.page === 3 }">歌单</span>
            <div v-if="userSelect.page === 3" class="button-underline"></div>
          </div>
          <div @click="search" class="button">
            <span @click="userSelect.page=4" :class="{ 'bold-text': userSelect.page === 4 }">专辑</span>
            <div v-if="userSelect.page === 4" class="button-underline"></div>
          </div>
          <div @click="search" class="button">
            <span @click="userSelect.page=5" :class="{ 'bold-text': userSelect.page === 5 }">歌手</span>
            <div v-if="userSelect.page === 5" class="button-underline"></div>
          </div>
          <div @click="search" class="button">
            <span @click="userSelect.page=6" :class="{ 'bold-text': userSelect.page === 6 }">用户</span>
            <div v-if="userSelect.page === 6" class="button-underline"></div>
          </div>
        </div>
      </div>
    </div>
    <Comprehensive
        v-if="userSelect.page === 1"
        @user-select="userSelected"
        :keyword="keyword"
    />
    <InfiniteTable
        v-if="userSelect.page === 2"
        :load-more="loadMore"
        :items="musics"/>
    <PlaylistTable
        v-if="userSelect.page === 3"
        :keyword="keyword"/>
    <AlbumTable
        v-if="userSelect.page === 4"
        :keyword="keyword"/>
    <UserTable
        v-if="userSelect.page === 5"
        type="artist"
        :keyword="keyword"/>
    <UserTable
        v-if="userSelect.page === 6"
        type="user"
        :keyword="keyword"/>
  </div>
</template>

<style scoped>
.search-container {
  margin-top: 10px;
}

.separator-content {
  margin-top: 20px;
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
  color: #283248;
  font-weight: bold;
}

.select-button .button {
  position: relative;
  margin-right: 40px;
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