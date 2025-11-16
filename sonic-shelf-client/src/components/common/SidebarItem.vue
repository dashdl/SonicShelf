<script setup>
defineProps({
  icon: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click')
}
</script>

<template>
  <div class="sidebar-card" @click="handleClick">
    <img :src="icon" style="width: 25px" alt="">
    <span style="margin-top: 3px;margin-left: 5px;">{{ label }}</span>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as vars;
.sidebar-card{
  display: flex;
  height: 35px;
  border-radius: vars.$radius;
  padding-left: 10px;
  margin-bottom: 5px;
  align-items: center;
  cursor: pointer;
}
.sidebar-card:hover{
  background-color: vars.$item-hover;
  color: #333333;
}
</style>