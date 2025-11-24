<script setup>

import Comment from "@/components/list/Comment.vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {usePlayerStore} from "@/store/player.js";
import router from "@/router/index.js";
import {ref} from "vue";

const props = defineProps({
  item: Object,
  userSelect: Number
})

const emit = defineEmits(["select","delete"]);

const showDeleteCard = ref(false)

const deleteDynamic = async (id) => {
  const res = await request.delete(`dynamic/delete-dynamic/${id}`);
  if (res.code === '200') {
    ElMessage.success("删除成功")
    emit("delete")
  }
}

const handleLike = (id, like) => {
  if (like === true) {
    request.delete('like', {
      params: {
        targetType: 'dynamic',
        targetId: id
      }
    }).then(res => {
      if (res.code === '200') {
        props.item.like = false;
        props.item.likeCount -= 1;
        ElMessage.success("取消点赞");
      } else {
        ElMessage.error("取消点赞失败");
      }
    })
  } else {
    request.post('like', {}, {
      params: {
        targetType: 'dynamic',
        targetId: id
      }
    }).then(res => {
      if (res.code === '200') {
        props.item.like = true;
        props.item.likeCount += 1;
        ElMessage.success("点赞成功");
      } else {
        ElMessage.error("点赞失败");
      }
    })
  }
}
const handleClick = () => {
  if (props.item.type === "music") {
    usePlayerStore().checkMusicId(props.item.targetId)
  } else if (props.item.type === "playlist") {
    router.push(`/playlist/${props.item.targetId}`)
  }
}
const replace = () => {
  if (props.item.type === "music") {
    usePlayerStore().checkMusicId(props.item.targetId)
  } else if (props.item.type === "playlist") {
    request.get('playlists/' + props.item.targetId + '/musics').then(res => {
      const playerStore = usePlayerStore();
      playerStore.updatePlaylist(res.data)
      localStorage.setItem("playlist", JSON.stringify(res.data))
      playerStore.playSong(0)
      playerStore.playlistId = props.item.targetId;
      playerStore.isPlaylist = true;
      localStorage.setItem("playlistId", props.item.targetId)
      localStorage.setItem("isPlaylist", JSON.stringify(playerStore.isPlaylist))
    })
  }
}

const formatTime = (time) => {
  return time.substring(5, 10)
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="dynamic-card">
    <img @click="showDeleteCard=!showDeleteCard" id="interact" src="/icons/status/more.svg" alt="">
    <div @click="deleteDynamic(item.id)" v-if="showDeleteCard" class="delete-bar">
      删除
    </div>
    <div class="dynamic-header">
      <img :src="item.avatar ? baseUrl + item.avatar : '/images/default/avatar.jpg'"
           style="margin-right: 10px;border-radius: 23px;height: 45px;width: 45px;"
           alt="">
      <div class="dynamic-user">
        <span style="color: dodgerblue">{{ item.userName }}</span>
        <span style="font-size: 11px;color: #a9adb7">
          {{ formatTime(item.createdAt) }}
        </span>
      </div>
    </div>
    <span class="content">{{ item.content }}</span>
    <div class="dynamic-content">
      <div v-if="item.images.length>0" class="dynamic-img">
        <img v-for="image in item.images" :src="image ? baseUrl + image : '/images/default/test.jpg'"
             style="height: 280px;width: 280px;object-fit: cover;border-radius: 5px;border: 2px solid #e4e8ec" alt="">
      </div>
    </div>
    <div v-if="item.type !== 'text'" class="dynamic-media">
      <div @click="handleClick" class="music-card">
        <div class="info">
          <div @click.stop="replace" class="cover">
            <img id="cover" :src="item.coverImage ? baseUrl + item.coverImage : '/images/default/cover.png'"
                 alt="">
            <img id="playButton" src="/icons/player/play.svg" alt="">
          </div>
          <div class="text">
            <span style="font-size: 18px;color:#333333;">{{ item.title }}</span>
            <span>{{ item.artistName ? item.artistName : item.creatorName }}</span>
          </div>
        </div>
      </div>
    </div>
    <div @click="jump" class="interact-card">
      <div class="img">
        <div class="numb">{{ item.likeCount }}</div>
        <img @click="handleLike(item.id,item.like)"
             :src="item.like ? '/icons/status/thumbs.svg' : '/icons/status/unThumb.svg'" alt="">
      </div>
      <div class="img">
        <div class="numb">{{ item.shareCount }}</div>
        <img src="/icons/player/share.svg" style="transform: translateY(1px);" alt="">
      </div>
      <div class="img">
        <div class="numb">{{ item.commentCount }}</div>
        <img @click="emit('select',item.id)" src="/icons/player/comment.svg" alt="">
      </div>
    </div>
    <Comment
        v-if="userSelect === item.id"
        :target-id="item.id.toString()"
        target-type="dynamic"/>
  </div>
</template>

<style scoped>
.dynamic-card {
  position: relative;
  padding: 20px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 10px;
}

#interact {
  position: absolute;
  width: 20px;
  right: 20px;
  cursor: pointer;
}

.delete-bar {
  position: absolute;
  top: 40px;
  right: 30px;
  width: 120px;
  height: 30px;
  background: #ffffff;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 5px rgba(100, 100, 100, 0.2);
  cursor: pointer;
}

.dynamic-header {
  margin-bottom: 15px;
  display: flex;
  flex-direction: row;
}

.dynamic-user {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.content {
  width: 100%;
  display: inline-block;
  font-size: 18px;
  margin-bottom: 20px;
  word-wrap: break-word;
  word-break: break-all;
  white-space: normal;
}

.dynamic-content {
  width: 570px;
  margin-bottom: 20px;
}

.dynamic-img {
  margin-top: 5px;
  display: grid;
  grid-template-columns: repeat(2, 280px);
  gap: 10px;
}

.music-card {
  user-select: none;
  margin-bottom: 20px;
  padding-left: 10px;
  height: 90px;
  width: 570px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  background: #f0f2f5;
  border-radius: 10px;
  cursor: pointer;

  .info {
    display: flex;
  }

  .cover {
    position: relative;
    margin-right: 10px;
    width: 65px;
    height: 65px;
    border-radius: 5px;
    overflow: hidden;
  }

  .cover:hover #playButton {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.2);
  }

  #playButton {
    position: absolute;
    height: 35px;
    top: 50%;
    left: 50%;
    opacity: 0;
    transition: all 0.1s ease;
    transform: translate(-50%, -50%);
  }

  .text {
    margin-right: 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;

  }

  .text span {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}

.music-card:hover {
  box-shadow: 2px 2px 10px #eaeaea;
}

.music-card:hover #cover {
  filter: brightness(90%);
}

.music-card:hover #playButton {
  opacity: 70%;
}

.interact-card {
  margin-bottom: 20px;
  display: flex;
  flex-direction: row;

  .img {
    position: relative;

    .numb {
      position: absolute;
      left: 20px;
      font-size: 9px;
    }
  }

  img {
    width: 17px;
    margin-right: 60px;
    cursor: pointer;
  }
}

</style>