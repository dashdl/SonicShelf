<script setup>

import InfiniteTable from "@/components/list/InfiniteTable.vue";
import request from "@/utils/request.js";
import {onMounted, reactive, ref} from "vue";
import {ElMessage} from "element-plus";

const data = reactive({
  pageNum: 0,
  pageSize: 40,
  total: 0,
})

const musics = ref([]);
const hasMore = ref(false);
const loading = ref(false)

const loadMore = async () => {
  if (!hasMore.value || loading.value) return
  loading.value = true
  try {
    await load()
    if (data.pageNum * data.pageSize >= data.total) hasMore.value = false
  } catch (error) {
    console.error('加载更多数据失败:', error)
    ElMessage.error('加载更多音乐失败')
  } finally {
    loading.value = false
  }
}

const load = async () => {
  await request.get('play-histories', {
    params: {
      pageNum: ++data.pageNum,
      pageSize: data.pageSize,
    }
  }).then(res => {
    musics.value = [...musics.value, ...res.data.list];
    data.total = res.data.total;
    hasMore.value = res.data.hasNextPage;
  })
}

onMounted(() => {
  data.pageNum = 0
  data.total = 0
  load();
})

</script>

<template>
  <InfiniteTable :load-more="loadMore" :items="musics"/>
</template>

<style scoped>

</style>