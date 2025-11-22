<script setup>
import {onMounted, onUnmounted, ref, nextTick, watch, useTemplateRef} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import {usePlayerStore} from "@/store/player.js";
import router from "@/router/index.js";
import InteractCard from "@/components/common/cards/InteractCard.vue";

const playerStore = usePlayerStore();

const props = defineProps({
  items: {
    type: Array,
    default: () => [],
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  hasMore: {
    type: Boolean,
    default: true
  },
  loadMore: {
    type: Function,
    required: true
  },
  threshold: {
    type: Number,
    default: 300
  },
  showDelete: {
    type: Boolean,
    default: false
  }
})

const reactiveItems = ref([]);

watch(() => props.items, (newItems) => {
  reactiveItems.value = JSON.parse(JSON.stringify(newItems)).map(item => ({
    ...item,
    showInteract: false,
    interactPosition: 'bottom'
  }));
}, {immediate: true, deep: true});

const toggleInteract = (item,event) => {
  reactiveItems.value.forEach(i => {
    if (i.id !== item.id) {
      i.showInteract = false;
    }
  });

  // 判断按钮在视口中的位置
  const buttonRect = event.currentTarget.getBoundingClientRect();
  const viewportHeight = window.innerHeight;

  // 如果按钮在视口上半部分，交互卡片显示在下方；否则显示在上方
  item.interactPosition = buttonRect.top < viewportHeight / 2 ? 'top' : 'bottom';
  item.showInteract = !item.showInteract;
};

const closeAllInteract = () => {
  reactiveItems.value.forEach(item => {
    item.showInteract = false;
  });
};

const play = (id) => {
  playerStore.checkMusicId(id)
}

let currentContainer = null;
const tableRef = ref(null);

const debounce = (func, wait) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

const debouncedLoadMore = debounce(() => {
  props.loadMore();
}, 30); // 300ms延迟，避免频繁触发

const handleScroll = (event) => {
  if (props.loading || !props.hasMore) return;

  const container = event.target || currentContainer;
  if (!container) return;

  const {scrollTop, scrollHeight, clientHeight} = container;

  if (scrollHeight - scrollTop - clientHeight < props.threshold) {
    debouncedLoadMore();
  }
};

const goToMusic = (musicId) => {
  router.push({
    name: 'Music',
    params: {musicId: musicId}
  })
};

const emit = defineEmits(['updateFavorite', 'collect']);

const collect = (id) => {
  emit("collect", id);
}

const favorite = (id, favorite) => {
  if (favorite === false) {
    request.post('favorites/music/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('收藏成功');
        const item = reactiveItems.value.find(item => item.id === id);
        if (item) {
          item.favorite = true;
        }
        emit('updateFavorite', id, true);
      } else {
        ElMessage.error(res.message);
        // 请求失败，恢复原状态
        const item = reactiveItems.value.find(item => item.id === id);
        if (item) item.favorite = false;
      }
    }).catch(err => {
      console.error('收藏失败:', err);
      ElMessage.error('收藏失败，请稍后重试');
      // 请求失败，恢复原状态
      const item = reactiveItems.value.find(item => item.id === id);
      if (item) item.favorite = false;
    });
  } else {
    request.delete('favorites/music/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success('取消收藏成功');
        const item = reactiveItems.value.find(item => item.id === id);
        if (item) {
          item.favorite = false;
        }
        emit('updateFavorite', id, false);
      } else {
        ElMessage.error(res.message);
        // 请求失败，恢复原状态
        const item = reactiveItems.value.find(item => item.id === id);
        if (item) item.favorite = true;
      }
    }).catch(err => {
      console.error('取消收藏失败:', err);
      ElMessage.error('取消收藏失败，请稍后重试');
      // 请求失败，恢复原状态
      const item = reactiveItems.value.find(item => item.id === id);
      if (item) item.favorite = true;
    });
  }
}

const findScrollableParent = (element) => {
  let parent = element.parentElement;
  while (parent && parent !== document.body) {
    if (parent.classList.contains('main-content')) {
      return parent;
    }
    const style = window.getComputedStyle(parent);
    if ((style.overflow === 'auto' || style.overflow === 'scroll') &&
        parent.scrollHeight > parent.clientHeight) {
      return parent;
    }
    parent = parent.parentElement;
  }
  return window;
};

const setupScrollContainer = () => {
  if (tableRef.value) {
    const foundContainer = findScrollableParent(tableRef.value);
    if (foundContainer) {
      currentContainer = foundContainer;
      currentContainer.addEventListener('scroll', handleScroll);
    }
  }
};

const cleanupScrollListener = () => {
  if (currentContainer) {
    currentContainer.removeEventListener('scroll', handleScroll);
    currentContainer = null;
  }
};

const format=(duration)=>{
  if (!duration || isNaN(duration)) return '00:00'
  const minutes = Math.floor(duration / 60)
  const secs = Math.floor(duration % 60)
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

onMounted( async () => {
  await nextTick();
  setupScrollContainer();
});

onUnmounted(() => {
  cleanupScrollListener();
});

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="table-container" ref="tableRef" @click="closeAllInteract">
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
          <span>专辑</span>
        </div>
        <div class="like-cell">
          <span>喜欢</span>
        </div>
        <div class="time-cell">
          <span>时长</span>
        </div>
      </div>
    </div>
    <div @mouseleave="closeAllInteract" class="table-row" v-for="(item, index) in reactiveItems" :key="item.id">
      <div class="left-cell">
        <div class="rank-cell">
          <span id="title" style="font-size: 12px">{{ index + 1 }}</span>
          <div class="play-button">
            <img @click="play(item.id)" src="/icons/player/play.svg" style="width: 20px;filter: brightness(0.4);"
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
            <span @click="router.push('/artist/'+item.artistId)" id="artist" style="">{{ item.artistName }}</span>
          </div>
          <div class="interact-group">
            <div class="interact-button">
              <img @click="collect(item.id)" src="/icons/player/favorite.svg" style="width: 18px" alt="">
            </div>
            <div @click="goToMusic(item.id)" class="interact-button">
              <img src="/icons/player/comment.svg" style="width: 18px" alt="">
            </div>
            <div class="interact-button">
              <img @click.stop="toggleInteract(item,$event)" src="/icons/status/more.svg" style="width: 18px" alt="">
            </div>
            <InteractCard
                v-if="item.showInteract"
                :music-id="item.id"
                :show-delete="props.showDelete"
                :position="item.interactPosition"
                @click.stop
                @collect="collect"
            />
          </div>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell">
          <span @click="router.push(`/album/${item.albumId}`)">
            {{ item.albumTitle }}
          </span>
        </div>
        <div class="like-cell">
          <img @click="favorite(item.id,item.favorite)"
               :src="item.favorite===true ? '/icons/player/like.svg':'/icons/player/unlike.svg' " style="width: 18px;cursor: pointer;"
               alt="">
        </div>
        <div class="time-cell"><span style="color: #7b818f">{{ format(item.duration) }}</span></div>
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
  width: 100%;
  display: flex;
  flex-direction: column;
  user-select: none;
}

.table-row {
  margin-bottom: 4px;
  height: 65px;
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-between;
  border-radius: 10px;
}

.table-row:not(:first-child):hover {
  background-color: #ffffff;
  box-shadow: 0 2px 2px rgba(0, 0, 0, 0.02);
}

.table-row:hover .play-button {
  z-index: 1;
}

.table-row:hover #title {
  z-index: -1;
}

.table-row:hover .interact-group {
  opacity: 1;
}

.left-cell {
  width: 53%;
  max-width: 850px;
  min-width: 450px;
  display: flex;
  flex-direction: row;
}

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

.title {
  display: flex;
  flex-direction: column;
}

#artist:hover {
  cursor: pointer;
}

.interact-group {
  position: absolute;
  right: 25px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  opacity: 0;
}

.interact-button {
  margin-right: 15px;
}

.interact-button:hover {
  cursor: pointer;
  filter: brightness(0.4);
}

.right-cell {
  width: 47%;
  min-width: 320px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.total-cell {
  padding-right: 20px;
  width: 70%;
  overflow: hidden;
  display: flex;
}

.total-cell span {
  display: block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.total-cell span:hover{
  cursor: pointer;
}

.like-cell {
  width: 10%;
}

.time-cell {
  width: 20%;
  margin-left: 20px;
}

</style>