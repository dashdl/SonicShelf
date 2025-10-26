<script setup>
defineProps({
  icon: {
    type: String,
    required: true  // 必须传递图标路径
  },
  label: {
    type: String,
    required: true  // 必须传递标签文字
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click')
}
</script>

<template>
  <div class="sidebar-card" @click="handleClick">
    <img :src="icon" style="width: 25px">
    <span style="margin-top: 5px;margin-left: 5px;">{{ label }}</span>
  </div>
</template>

<style scoped lang="scss">
.sidebar-card{
  display: flex;
  height: 35px;
  border-radius: $radius;
  padding-left: 10px;
  margin-bottom: 5px;
  align-items: center;
  cursor: pointer;
}
.sidebar-card:hover{
  background-color: $item-hover;
}
</style>