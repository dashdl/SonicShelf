<script setup>
import SidebarItem from "@/components/common/SidebarItem.vue";

const menuItems = [
  {icon: '/icons/sidebar/home.png', label: '首页', key: 'home'},
  {icon: '/icons/sidebar/home-b.png', label: '发现', key: 'explore'},
  {icon: '/icons/sidebar/home-b.png', label: '关注', key: 'explore'},
]
</script>

<template>
  <div class="main-content">
    <div class="title">
      <img src="/icons/logo.png" alt="logo" style="width: 30px">
    </div>
    <div class="menu-content">
      <SidebarItem
          v-for="item in menuItems"
          :key="item.key"
          :icon="item.icon"
          :label="item.label"
          :alt-text="item.label"
          @click="handleItemClick(item.key)"
      />
      <hr>
      <SidebarItem
          v-for="item in menuItems"
          :key="item.key"
          :icon="item.icon"
          :label="item.label"
          :alt-text="item.label"
          @click="handleItemClick(item.key)"
      />
      <hr>
    </div>
  </div>
</template>

<style scoped lang="scss">
.main-content {
  background-color: $sidebar-color;
  color: #50596b;
  display: flex;
  flex-direction: column;
  padding-left: 25px;
}

.title {
  display: flex;
  height: 70px;
  align-items: center;
  padding-left: 20px;
}

hr {
  width: 85%;
  margin: 10px 0;
  border: none;
  height: 2px;
  justify-self: center;
  background-color: #e4e8ec;
}

</style>