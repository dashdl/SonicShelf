<script setup>

import router from "@/router/index.js";
import {onMounted, onUnmounted, ref, nextTick} from "vue";

const props = defineProps({
  items: {
    type: Array,
    default: () => []
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
    default: 300 // 增加默认阈值，减少触发频率
  }
})

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

  const { scrollTop, scrollHeight, clientHeight } = container;

  if (scrollHeight - scrollTop - clientHeight < props.threshold) {
    debouncedLoadMore();
  }
};

const goToMusic = (musicId) => {
  // router.push(`/playlist/${musicId}`);
};

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

onMounted(async () => {
  await nextTick();
  setupScrollContainer();
});

onUnmounted(() => {
  cleanupScrollListener();
});

const baseUrl = 'http://localhost:8080';
</script>

<template>
  <div class="table-container" ref="tableRef">
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
    <div class="table-row" v-for="(item, index) in props.items" @click="goToMusic(item.id)" :key="index">
      <div class="left-cell">
        <div class="rank-cell">
          <span id="title" style="font-size: 12px">{{ index + 1 }}</span>
          <div class="play-button">
            <img src="/icons/player/play.svg" style="width: 20px;filter: brightness(0.4);" alt="">
          </div>
        </div>
        <div class="title-cell">
          <div class="cover">
            <img :src="baseUrl+item.coverImage||'/images/default/cover.png'"
                 style="width: 50px;height: 50px;border-radius: 8px;margin-right: 12px;object-fit: cover;" alt="">
          </div>
          <div class="title">
            <span style="font-size: 16px;color: #333333;">{{ item.title }}</span>
            <span style="">{{ item.artistName }}</span>
          </div>
        </div>
      </div>
      <div class="right-cell">
        <div class="total-cell"><span>{{ item.albumTitle }}</span></div>
        <div class="like-cell"><img src="/icons/status/like.svg" style="width: 18px;" alt=""></div>
        <div class="time-cell"><span style="color: #7b818f">04:55</span></div>
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
}

.title-cell {
  display: flex;
  flex-grow: 1;
  align-items: center;
}

.title {
  display: flex;
  flex-direction: column;
}

.right-cell {
  width: 47%;
  min-width: 320px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.total-cell {
  width: 70%;
}

.like-cell {
  width: 10%;
}

.time-cell {
  width: 20%;
  margin-left: 20px;
}

</style>