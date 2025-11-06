<script setup>

import {onMounted, reactive, ref} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const props = defineProps({
  targetType: {
    type: String,
    required: true
  },
  targetId: {
    type: String,
    required: true
  }
})

const comments = ref([]);

const comment = reactive({
  content: '',
  targetType: 'playlist',
  targetId: props.targetId,
  parentId: '',
});

const formatCommentContent = (content) => {
  if (!content || typeof content !== 'string') {
    return content || '';
  }
  const replyPattern = /^回复\s+[^：]+：/;
  const match = content.match(replyPattern);

  if (match) {
    return content.replace(replyPattern, '').trim();
  }
  comment.parentId = '';
  return content.trim();

};

const handleLike = (id, like) => {
  if (like === true) {
    request.delete('like', {
      params: {
        targetType: 'comment',
        targetId: id
      }
    }).then(res => {
      if (res.code === '200') {
        comments.value.find(comment => comment.id === id).like = false;
        ElMessage.success("取消点赞");
      } else {
        ElMessage.error("取消点赞失败");
      }
    })
  } else {
    request.post('like', {}, {
      params: {
        targetType: 'comment',
        targetId: id
      }
    }).then(res => {
      if (res.code === '200') {
        comments.value.find(comment => comment.id === id).like = true;
        ElMessage.success("点赞成功");
      } else {
        ElMessage.error("点赞失败");
      }
    })
  }
}
const handleReply = (parentId, nickname) => {
  comment.parentId = parentId
  comment.content = "回复 " + nickname + '：' + comment.content
}

const postComment = () => {
  comment.content = formatCommentContent(comment.content);
  console.log(comment.content)

  request.post("comments", comment).then(res => {
    if (res.code === '200') {
      ElMessage.success("发布成功")
    } else {
      ElMessage.error("发布失败")
    }
  })
  loadPlaylistComments(props.targetId)
}

onMounted(() => {
  loadPlaylistComments(props.targetId)
})

const loadPlaylistComments = async (playlistId) => {
  await Promise.all([
    request.get('comments', {
      params: {
        targetType: props.targetType,
        targetId: playlistId
      }
    }).then(res => {
      if (res.code === '200') {
        comments.value = [...comments.value, ...res.data];
      } else {
        ElMessage.error("评论信息获取失败")
      }
    }),
  ]);
}

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="comment-main">
    <div class="comment-top">
      <div class="all-comment">
        <div class="count">{{ comments.length }}</div>
        <span style="font-size: 18px;font-weight: bold;color: #0d0d0d">全部评论</span>
      </div>
      <div class="editor">
        <textarea v-model="comment.content" placeholder="说点什么吧"
                  oninput="this.style.height='auto';this.style.height=this.scrollHeight+'px'"
        />
        <div class="editor-info">
          <span @click="postComment" style="line-height: 1">发布</span>
        </div>
      </div>
    </div>
    <div class="comment-list">
      <div class="title">精彩评论</div>
      <div class="comment-item" v-for="item in comments" :key="item.id">
        <img :src="baseUrl + item.avatar ||'/images/default/avatar.jpg'"
             style="margin-right: 15px;border-radius: 23px;height: 45px;width: 45px;"
             alt="">
        <div class="comment-text">
          <span style="color: dodgerblue">{{ item.nickname }}</span>
          <span style="margin-bottom: 5px">{{ item.content }}</span>
          <div v-if="item.parentComment != null" class="parent-comment">
            <hr>
            <span style="color: dodgerblue">@{{ item.parentComment.nickname }}:</span>
            <span>{{ item.parentComment.content }}</span>
          </div>
          <div class="comment-info">
            <span style="font-size: 11px;color: #a9adb7">{{ item.createdAt.substring(0, 10) }}</span>
            <div class="interact">
              <div class="likeCount">{{ item.likeCount }}</div>
              <img @click="handleLike(item.id,item.like)"
                   :src=" item.like ? '/icons/status/thumbs.svg' : '/icons/status/unThumb.svg'"
                   style="height: 18px;width: 18px" alt="">
              <img @click="handleReply(item.id,item.nickname)" src="/icons/player/comment.svg"
                   style="margin-left: 30px;height: 18px;width: 18px" alt="">
            </div>
          </div>
        </div>
        <hr id="hr">
      </div>
    </div>
  </div>
</template>

<style scoped>

.all-comment {
  position: relative;
  margin-bottom: 8px;
}

.count {
  position: absolute;
  top: -2px;
  left: 72px;
  font-size: 12px;
  color: #0d0d0d;
}

.editor {
  position: sticky;
  top: 0;
  margin-bottom: 35px;
  padding: 20px;
  min-width: 775px;
  min-height: 85px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: #f0f2f5;
  border: #e4e8ec 1px solid;
  border-radius: 10px;
  z-index: 100;
}

.editor textarea {
  width: auto;
  border: none;
  resize: none;
  background-color: transparent;
  padding: 0;
  outline: none;
  overflow-y: auto;
}

.editor-info {
  display: flex;
  flex-direction: column;
  align-items: end;
}

.title {
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: bold;
  color: #0d0d0d;
}

.comment-item {
  position: relative;
  margin-bottom: 30px;
  display: flex;
  flex-direction: row;
}

#hr {
  position: absolute;
  width: 100%;
  height: 2px;
  bottom: -15px;
  border: none;
  background-color: #f0f2f5;
}

.comment-text {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.parent-comment {
  display: flex;
  flex-direction: row;
}

.parent-comment hr {
  margin-right: 5px;
  width: 1px;
  height: 100%;
  bottom: -15px;
  border: none;
  background-color: #dad6d6;
}

.comment-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.interact {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.interact img:hover {
  filter: brightness(0.7);
  cursor: pointer;
}

.likeCount {
  padding-top: 3px;
  display: flex;
  align-items: center;
  line-height: 1;
  margin-right: 5px
}
</style>