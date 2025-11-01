<script setup>
import SidebarItem from "@/components/common/SidebarItem.vue";

const menuItems = [
  {icon: '/icons/sidebar/home.png', label: '首页', key: 'home'},
  {icon: '/icons/sidebar/home-b.png', label: '发现', key: 'explore'},
  {icon: '/icons/sidebar/home-b.png', label: '关注', key: 'explore'},
]
</script>

<template>
  <div class="sidebar-content">
    <div class="title">
      <img src="/logo.svg" style="width: 30px;margin-right: 14px" alt="logo">
      <img src="/sloganCN.svg" style="height:30px" alt="">
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
.sidebar-content {
  width: 220px;
  padding-left: 25px;
  padding-right: 20px;
  background-color: $sidebar-color;
  color: #50596b;
  display: flex;
  flex-direction: column;
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