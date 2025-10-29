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
          <RouterView/>
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
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.top-container {
  display: flex;
  flex: 1;
}

.player-container {
  display: flex;
  height: 80px;
}

.left-container {
  display: flex;
  width: 200px;
}

.right-container {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding-left: 40px;
  background-color: #f7f9fc;
}

.header-container {
  height: 70px;
}

.main-content {
  flex: 1;
  padding-right: 40px;
}

@media (min-width: 1790px) {
  .header-container{
    margin-left: -40px;
  }

  .right-container {
    padding-left: 115px;
  }
}
</style>