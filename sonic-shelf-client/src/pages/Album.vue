<script setup>

import {useRoute} from "vue-router";
import {onMounted, reactive, ref, watch} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {usePlayerStore} from "@/store/player.js";
import InfiniteTable from "@/components/list/InfiniteTable.vue";
import Comment from "@/components/list/Comment.vue";
import router from "@/router/index.js";
import {getFullUrl} from '@/utils/urlConfig';

const emit = defineEmits(['collect'])

const route = useRoute()

const album = reactive({
  coverImage: '',
  id: 0,
  musicCount: 0,
  releaseDate: '',
  title: '',
  description: '',
  artistId: 0,
  artistName: '',
  artistCover: ''
})

const userSelect = reactive({
  page: 1,
  favorite: true
})

const musics = ref([])

const commentCount = ref(0)

const collect = (id) => {
  emit('collect', id)
}

const replace = async () => {
  const playerStore = usePlayerStore();
  playerStore.updatePlaylist(musics.value)
  localStorage.setItem("playlist", JSON.stringify(musics.value))
  await playerStore.playSong(0)
  playerStore.isPlaylist = false;
  localStorage.setItem("isPlaylist", JSON.stringify(playerStore.isPlaylist))
}

const favorite = async () => {
  let res;
  if (userSelect.favorite === false) {
    res = await request.post('favorites/album/' + route.params.id)
  } else {
    res = await request.delete('favorites/album/' + route.params.id)
  }
  if (res.code === '200') {
    ElMessage.success(res.message)
    userSelect.favorite = !userSelect.favorite
  } else if (res.code === '400') {
    ElMessage.warning(res.message)
  } else {
    ElMessage.error(res.message)
  }
}

const initial = async () => {
  let res = await request.get('album/' + route.params.id)
  if (res.code === '200') {
    album.id = res.data.id;
    album.coverImage = res.data.coverImage;
    album.musicCount = res.data.musicCount;
    album.releaseDate = res.data.releaseDate;
    album.description = res.data.description;
    album.title = res.data.title;
    album.artistCover = res.data.artistCover;
    album.artistName = res.data.artistName;
    album.artistId = res.data.artistId;
  } else {
    ElMessage.warning("专辑信息获取失败")
  }

  res = await request.get('album/' + route.params.id + '/musics')
  if (res.code === '200') {
    musics.value = [...res.data]
  }

  res = await request.get('comments', {
    params: {
      targetType: 'album',
      targetId: route.params.id
    }
  })
  if (res.code === '200') {
    commentCount.value = res.data.length
  } else {
    ElMessage.error("专辑信息获取失败")
  }

  res = await request.get('favorites/isFavorite', {
    params: {
      targetType: 'playlist',
      targetId: route.params.id,
    }
  })
  if (res.code === '200') {
    userSelect.favorite = res.data
  }

}

watch(() => route.params.id, (newId, oldId) => {
  if (newId !== oldId) {
    initial();
  }
});

onMounted(() => {
  initial()
})

</script>

<template>
  <div class="album-container">
    <div class="profile-container">
      <div class="cover" style="position: relative;">
        <img :src="album.coverImage ? getFullUrl(album.coverImage) :'/images/default/cover.png'"
             style="position:relative;width: 220px;height: 220px;border-radius: 10px;margin-right: 40px;z-index: 2;"
             alt="">
        <div class="record"></div>
      </div>
      <div class="profile-content">
        <div class="title">
          <span style="margin-top: -5px; margin-right: 8px;font-size: 24px;font-weight: bold;">{{ album.title }}</span>
        </div>
        <div class="profile">
          <img @click="router.push('/artist/'+album.artistId)"
               id="artist"
               :src="album.artistCover ? getFullUrl(album.artistCover) : '/images/default/avatar.jpg'"
               style="width: 25px;height: 25px;border-radius: 23px;margin-right: 8px;object-fit: cover"
               alt="">
          <span @click="router.push('/artist/'+album.artistId)"
                id="artist"
                style="font-size: 14px;color: #7b818f;margin-right: 12px">{{ album.artistName }}</span>
          <span style="font-size: 14px;color: #b7bac4">{{ album.releaseDate }}发布</span>
        </div>
        <div class="button-group">
          <div @click="replace" class="button"
               style="color: #ffffff;background: linear-gradient(to right, #fc3b5b, #fc3d49);">
            <img src="/icons/player/play.svg" style="height: 15px;margin-right: 5px" alt="">
            播放全部
          </div>
          <div @click="favorite" class="follow-button">
            <img :src="userSelect.favorite ? '/icons/status/hook.svg' : '/icons/status/follow.svg'"
                 style="height: 15px;margin-right: 5px" alt="">
            <span v-if="userSelect.favorite">已</span>
            <span>收藏</span>
          </div>
        </div>
      </div>
    </div>
    <div class="separator-content">
      <div class="select-button">
        <div class="button">
          <div class="numb">
            <span style="font-size:12px;font-weight: bold">{{ album.musicCount }}</span>
          </div>
          <span @click="userSelect.page=1" :class="{ 'bold-text': userSelect.page === 1 }">歌曲</span>
          <div v-if="userSelect.page === 1" class="button-underline"></div>
        </div>
        <div class="button">
          <div class="numb">
            <span style="font-size:12px;font-weight: bold">{{ commentCount }}</span>
          </div>
          <span @click="userSelect.page=2" :class="{ 'bold-text': userSelect.page === 2 }">评论</span>
          <div v-if="userSelect.page === 2" class="button-underline"></div>
        </div>
        <div class="button">
          <span @click="userSelect.page=3" :class="{ 'bold-text': userSelect.page === 3 }">专辑详情</span>
          <div v-if="userSelect.page === 3" class="button-underline"></div>
        </div>
      </div>
    </div>
    <InfiniteTable
        v-if="userSelect.page===1"
        load-more=""
        :items="musics"
        @collect="collect"/>
    <Comment
        v-if="userSelect.page===2"
        :target-id="route.params.id"
        :target-type="'album'"
    />
    <div v-if="userSelect.page===3" class="artist-description" v-html="album.description"/>
  </div>

</template>

<style scoped>

.artist-description ::v-deep h1 {
  font-size: 1.5em;
  margin: 1em 0 0.5em;
  color: #333;
}

.artist-description ::v-deep p {
  line-height: 1.6;
  margin: 1em 0;
  color: #666;
}

.profile-container {
  position: relative;
  margin-bottom: 25px;
  margin-top: 12px;
  display: flex;
  flex-direction: row;
}

.record {
  position: absolute;
  top: -10px;
  height: 100%;
  aspect-ratio: 1/1;
  border-radius: 50%;
  background-color: #0d0d0d;
  z-index: 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
}

.title {
  margin-bottom: 10px;
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

#artist:hover {
  cursor: pointer;
}

.button-group {
  position: absolute;
  bottom: 0;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.button-group .button {
  margin-right: 10px;
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

.follow-button {
  display: flex;
  height: 40px;
  width: 80px;
  border-radius: 10px;
  align-items: center;
  justify-content: center;
  color: #333333;
  background: #e9eaec;
  border: 2px solid #e4e8ec;
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

</style>