<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'

const props = defineProps({
  // 是否正在加载数据
  loading: {
    type: Boolean,
    default: false
  },
  // 是否还有更多数据
  hasMore: {
    type: Boolean,
    default: true
  },
  // 加载更多数据的回调函数
  loadMore: {
    type: Function,
    required: true
  },
  // 触发加载的阈值（距离底部的像素数）
  threshold: {
    type: Number,
    default: 100
  },
})

const emit = defineEmits(['load', 'loading-change'])

const loading = ref(false)
const containerRef = ref(null)

// 计算属性：是否可以触发加载
const canLoad = computed(() => {
  return  !props.loading && !loading.value && props.hasMore
})

// 处理滚动事件
const handleScroll = () => {
  if (!containerRef.value || !canLoad.value) return

  const container = containerRef.value
  const { scrollTop, scrollHeight, clientHeight } = container

  // 当滚动到距离底部小于阈值时，触发加载
  if (scrollHeight - scrollTop - clientHeight < props.threshold) {
    loadData()
  }
}

// 加载数据
const loadData = async () => {
  if (!canLoad.value) return

  loading.value = true
  emit('loading-change', true)

  try {
    await props.loadMore()
    emit('load')
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
    emit('loading-change', false)
  }
}

// 手动检查是否需要加载（用于初始加载）
const checkAndLoad = () => {
  if (!containerRef.value) return

  const { scrollHeight, clientHeight } = containerRef.value
  // 如果内容高度小于容器高度，说明一屏就可以显示完，需要立即加载
  if (scrollHeight <= clientHeight && canLoad.value) {
    loadData()
  }
}

// 暴露方法给父组件
defineExpose({
  checkAndLoad,
  loadData
})

onMounted(() => {
  if (containerRef.value) {
    containerRef.value.addEventListener('scroll', handleScroll, { passive: true })
    // 初始检查是否需要加载
    setTimeout(checkAndLoad, 0)
  }
})

onUnmounted(() => {
  if (containerRef.value) {
    containerRef.value.removeEventListener('scroll', handleScroll)
  }
})
</script>

<template>
  <div
      ref="containerRef"
      class="infinite-scroll-container"
  >
    <!-- 插槽：放置需要滚动的内容 -->
    <slot></slot>

    <!-- 加载状态指示器 -->
    <div v-if="loading || props.loading" class="infinite-scroll-loading">
      <slot name="loading">
        <div class="loading-spinner">加载中...</div>
      </slot>
    </div>

    <!-- 没有更多数据的提示 -->
    <div v-else-if="!hasMore" class="infinite-scroll-no-more">
      <slot name="no-more">
        <div class="no-more-text">没有更多数据了</div>
      </slot>
    </div>
  </div>
</template>

<style scoped>
.infinite-scroll-container {
  overflow-y: auto;
  height: 400px;
  position: relative;
}

.infinite-scroll-container.is-disabled {
  pointer-events: none;
}

.infinite-scroll-loading,
.infinite-scroll-no-more {
  padding: 20px 0;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.no-more-text {
  color: #ccc;
}
</style>