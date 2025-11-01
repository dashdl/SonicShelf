<script setup xmlns="">

import Header from "@/components/Header.vue";
import Sidebar from "@/components/Sidebar.vue";
import Player from "@/components/Player.vue";
import {useUserStore} from "@/store/userStore.js";
import {onMounted} from "vue";
import {ElMessage} from "element-plus";

const userStore = useUserStore();

onMounted(async () => {
  try {
    await userStore.restoreUserState();
  } catch (error) {
    ElMessage.error("用户状态加载失败");
  }
})

</script>

<template>
  <div class="page-container">
    <div class="top-container">
      <div class="left-container">
        <Sidebar/>
      </div>
      <div class="right-container">
        <div class="header-container">
          <Header/>
        </div>
        <div class="main-content">
          <div class="content-wrapper">
            <RouterView/>
          </div>
        </div>
      </div>
    </div>
    <div class="player-container">
      <Player/>
    </div>
  </div>


</template>

<style scoped>
.page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow-x: auto;
  position: relative;
}

.top-container {
  display: flex;
  flex: 1;
  min-height: 0;
}

.player-container {
  display: flex;
  height: 80px;
  flex-shrink: 0;
  position: relative;
}

.left-container {
  display: flex;
  width: 220px;
  flex-shrink: 0;
  position: relative;
}

.right-container {
  display: flex;
  flex: 1;
  padding-left: 45px;
  flex-direction: column;
  background-color: #f7f9fc;
  min-height: 0;
}

.header-container {
  height: 70px;
  flex-shrink: 0;
  position: relative;
}

.main-content{
  padding-right: 45px;
  flex: 1;
  overflow-x: auto;
  scroll-behavior: smooth;
  min-height: 0;
}

.content-wrapper {
  width: 100%;
  min-height: 0;
  overflow-x: visible;
}

@media (min-width: 1790px) {
  .header-container {
    margin-left: -40px;
  }

  .right-container {
    padding-left: 120px;
  }
}
</style>