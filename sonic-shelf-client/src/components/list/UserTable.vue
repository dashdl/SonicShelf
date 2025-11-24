<script setup>
import UserCard from "@/components/common/cards/UserCard.vue";
import {onMounted, ref} from "vue";
import request from "@/utils/request.js";

const props = defineProps({
  keyword: {
    type: String,
    required: true
  },
  type: {
    type: String,
    required: true
  }
})

const items =ref()

onMounted(async () => {
  let res = await request.get(`search/${props.type}s`, {params: {pageNum: 1, pageSize: 1000, keyword: props.keyword}})
  if (res.code === '200') {
    items.value = res.data.list
  }
})

</script>

<template>
  <div class="grid-template">
    <UserCard
        v-for="item in items"
        :type="props.type"
        :item="item"
    />
  </div>
</template>

<style scoped>

.grid-template {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
}

@media (min-width: 1200px) {
  .grid-template {
    grid-template-columns: repeat(5, 1fr);
  }
}

@media (min-width: 1440px) {
  .grid-template {
    grid-template-columns: repeat(6, 1fr);
  }
}
</style>