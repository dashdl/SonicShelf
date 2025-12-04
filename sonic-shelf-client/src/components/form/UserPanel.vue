<script setup>
import {useUserStore} from "@/store/userStore.js";
import router from "@/router/index.js";

const userStore = useUserStore();

const emit = defineEmits(['closePanel', 'login'])

const handleClick = (path) => {
  emit('closePanel');
  router.push(path);
}

const login = () => {
  emit('login')
  emit('closePanel')
}

const logout = () => {
  userStore.logout()
  emit('closePanel')
}

</script>

<template>
  <div class="panel-container">
    <div class="panel-top">
      <div @click="router.push(`/profile/${userStore.getUserId}/2`)" class="numb-container">
        <span class="numb">
          {{ userStore.getDynamicCount }}
        </span>
        <span class="title">
          动态
        </span>
      </div>
      <hr style="height: 30px;width:1px;border:none;background-color: #e4e8ec;"/>
      <div @click="router.push(`/following`)" class="numb-container">
        <span class="numb">
          {{ userStore.getFollowing_count }}
        </span>
        <span class="title">
          关注
        </span>
      </div>
      <hr style="height: 30px;width:1px;border:none;background-color: #e4e8ec;"/>
      <div @click="router.push(`/follower`)" class="numb-container">
        <span class="numb">
          {{ userStore.getFollowers_count }}
        </span>
        <span class="title">
          粉丝
        </span>
      </div>
    </div>
    <div class="panel-item">
      <div class="panel-card" @click="handleClick('/profile-settings')">
        <img src="/icons/actions/edit.svg" style="width: 20px;margin-right: 10px;margin-left: 10px" alt="">
        <span style="margin-top: 3px;">
          个人信息设置
        </span>
      </div>
      <div v-if="!userStore.isLoggedIn" class="panel-card" @click="login">
        <img src="/icons/sidebar/home.png" style="width: 25px;margin-right: 10px" alt="">
        <span style="margin-top: 3px;">
          登录
        </span>
      </div>
      <div v-if="userStore.isLoggedIn" @click="logout" class="panel-card">
        <img src="/icons/actions/logout.svg" style="width: 20px;margin-right: 10px;margin-left: 10px" alt="">
        <span style="margin-top: 3px;">
          退出登录
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as vars;

.panel-container {
  display: flex;
  flex-direction: column;
  width: 300px;
  border-radius: 15px;
  background: #fff;
  padding: 15px;
}

.panel-top {
  height: 50px;
  padding: 10px 20px;
  display: flex;
  flex-direction: row;
  width: 100%;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 30px;
}

.numb-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-items: center;
  align-items: center;
  cursor: pointer;
}

.numb {
  font-weight: bold;
  font-size: 30px;

}

.title {
  font-size: 15px;
  color: #a9adb7;
}

.panel-item {
  display: flex;
  flex-direction: column;
}

.panel-card {
  height: 35px;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.panel-card:hover {
  border-radius: 10px;
  background-color: #dee2e6;
  cursor: pointer;
}
</style>