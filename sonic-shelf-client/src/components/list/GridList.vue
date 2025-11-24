<script setup>
import router from "@/router/index.js";
import GridCard from "@/components/common/cards/GridCard.vue";

const props = defineProps({
  info: {
    type: Array,
    default: null
  },
  type: {
    type: String,
    required: false,
  },
  showArtist: {
    type: Boolean,
    required: false,
  },
})

const goTo = (id) => {
  router.push(`/${props.type}` + `/${id}`);
};

</script>

<template>
  <div class="grid-list">
    <div class="grid-container">
      <GridCard v-for="item in info"
                :item="item"
                :type="type"
                :show-artist="props.showArtist"
      />
    </div>
  </div>
</template>

<style scoped>

.grid-container {
  min-width: 757px;
  display: grid;
  grid-template-columns: repeat(4, minmax(180px, 1fr));
  grid-row-gap: 20px;
  grid-column-gap: 20px;
  user-select: none;
}

@media (min-width: 1280px) and (max-width: 1679px) {
  .grid-container {
    grid-template-columns: repeat(5, minmax(180px, 1fr));
  }
}

@media (min-width: 1680px) {
  .grid-container {
    grid-template-columns: repeat(6, 1fr);
  }
}

</style>