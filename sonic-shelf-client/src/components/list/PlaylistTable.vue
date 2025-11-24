<script setup>

import router from "@/router/index.js";
import request from "@/utils/request.js";
import {usePlayerStore} from "@/store/player.js";
import {onMounted, ref} from "vue";

const props = defineProps({
  keyword: {
    type: String,
    required: true
  }
})

const items =ref([])

const replace = async (id) => {
  await request.get('playlists/' + id + '/musics').then(res => {
    const playerStore = usePlayerStore();
    playerStore.updatePlaylist(res.data)
    localStorage.setItem("playlist", JSON.stringify(res.data))
    playerStore.playSong(0)
    playerStore.playlistId = id;
    playerStore.isPlaylist = true;
    localStorage.setItem("playlistId", id)
    localStorage.setItem("isPlaylist", JSON.stringify(playerStore.isPlaylist))
  })
}

onMounted(async () => {
  let res = await request.get("search/playlists", {params: {pageNum: 1, pageSize: 1000, keyword: props.keyword}})
  if (res.code === '200') {
    items.value = res.data.list
  }
})

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="table-container">
    <div class="table-row" style="margin-bottom: -12px">
      <div class="left-cell">
        <div class="rank-cell">
          <span>#</span>
        </div>
        <div class="title-cell">
          <span>标题</span>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell">
          <span>歌曲数</span>
        </div>
        <div class="creator-cell">
          <span>创建者</span>
        </div>
        <div class="playCount-cell">
          <span>播放量</span>
        </div>
      </div>
    </div>
    <div @click="router.push(`/playlist/`+item.id);" v-for="(item,index) in items" class="table-row">
      <div class="left-cell">
        <div class="rank-cell">
          <span id="rank" style="font-size: 12px">{{ index + 1 }}</span>
          <div class="play-button">
            <img @click="replace(item.id)" src="/icons/player/play.svg" style="width: 20px;filter: brightness(0.4);"
                 alt="">
          </div>
        </div>
        <div class="title-cell">
          <div class="cover">
            <img :src="baseUrl+item.coverImage||'/images/default/cover.png'"
                 style="width: 50px;height: 50px;border-radius: 8px;margin-right: 12px;object-fit: cover;" alt="">
          </div>
          <div class="title">
            <span style="font-size: 16px;color: #333333;">{{ item.title }}</span>
          </div>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell">
          <span>{{ item.musicCount }}首</span>
        </div>
        <div class="creator-cell">
          <span @click.stop="router.push('/profile/'+item.userId)" style="cursor: pointer">{{ item.nickname }}</span>
        </div>
        <div class="playCount-cell">
          <span>{{ item.playCount }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
span {
  font-size: 14px;
  color: #7b818f;
}

.table-container {
  user-select: none;
  display: flex;
  flex-direction: column;
}

.table-row {
  margin-bottom: 4px;
  min-height: 65px;
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-between;
  border-radius: 10px;
}

.table-row:hover {
  .play-button {
    z-index: 1;
  }

  #rank {
    z-index: -1;
  }
}

.table-row:not(:first-child):hover {
  background-color: #ffffff;
  box-shadow: 0 2px 2px rgba(0, 0, 0, 0.02);
}

.left-cell {
  height: 65px;
  width: 50%;
  display: flex;
  flex-direction: row;

  .rank-cell {
    width: 50px;
    position: relative;
    display: flex;
    justify-content: space-around;
    align-items: center;
  }

  .play-button {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: -1;
  }

  .play-button:hover {
    cursor: pointer;
    filter: brightness(0.4);
  }

  .title-cell {
    position: relative;
    display: flex;
    flex-grow: 1;
    align-items: center;
  }

  .cover{
    margin-right: 10px;
  }

  .title {
    padding-right: 20px;
    display: flex;
    flex-direction: column;
    width: 100%;

    span {
      font-size: 16px;
      color: #333333;
      display: -webkit-box;
      -webkit-line-clamp: 2; /* 最多显示2行 */
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.4;
      max-height: 2.8em; /* 1.4 * 2 */
    }

  }
}

.right-cell {
  width: 50%;
  display: flex;
  flex-direction: row;

  .total-cell {
    flex: 1.1;
  }

  .creator-cell {
    flex: 1.7;
    display: flex;
  }

  .playCount-cell {
    flex: 1;
    display: flex;
    align-items: center;
  }
}

</style>