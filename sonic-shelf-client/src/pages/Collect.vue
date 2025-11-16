<script setup>

import GridList from "@/components/list/GridList.vue";
import {onMounted, reactive} from "vue";
import request from "@/utils/request.js";

const albums = reactive([])

onMounted(async () => {
  const res = await request.get('favorites/albums')
  if (res.code === '200') {
    albums.value = res.data;
  }
})

</script>

<template>
  <GridList style="max-width: 1490px;margin-top: 20px;margin-bottom: 50px;"
            :info="albums.value" type="album" :show-artist="true"
  />
</template>

<style scoped>

</style>