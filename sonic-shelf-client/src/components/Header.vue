<template>
  <div class="header-container">
    <div class="left-content">
      <div class="left-button" @click="formSwitch">
        <img src="/icons/navigation/back.svg" style="height: 15px;" alt="">
      </div>
      <div class="search">
        <img src="/icons/navigation/search.svg" style="height: 18px;margin-left: 10px;margin-right: 10px;" alt="">
        <input>
      </div>
    </div>
    <div class="right-content">
      <img id="avatar" @click="router.push('/profile')" :src="avatarUrl" style="height: 30px;border-radius: 15px; margin-right: 5px;cursor: pointer;" alt="">
      <span>{{ userStore.getNickname }}</span>
      <img src="" alt="">
      <img @click="data.userPanelVisible=!data.userPanelVisible" src="/icons/status/down.svg"
           style="width: 13px;cursor: pointer" alt="">
    </div>
    <div v-if="data.formVisible" class="login-modal">
      <div class="modal-content">
        <LoginForm @close="formSwitch"/>
      </div>
    </div>

    <div v-if="data.userPanelVisible" class="panel-modal">
      <div class="modal-overlay" @click="data.userPanelVisible=!data.userPanelVisible"></div>
      <div class="modal-content">
        <UserPanel @closePanel="data.userPanelVisible=false"/>
      </div>
    </div>
  </div>

</template>

<script setup>
import {computed, reactive} from "vue";
import LoginForm from "@/components/form/LoginForm.vue";
import UserPanel from "@/components/form/UserPanel.vue";
import {onMounted, onUnmounted} from 'vue';
import {useUserStore} from "@/store/userStore.js";
import router from "@/router/index.js";

const userStore = useUserStore();
const baseUrl = 'http://localhost:8080';

const avatarUrl = computed(() => {
  if (userStore.getAvatar && userStore.getAvatar.startsWith('/icons/')) {
    return userStore.getAvatar;
  }
  return baseUrl + userStore.getAvatar;
});

const data = reactive({
  formVisible: false,
  userPanelVisible: false,
})

const formSwitch = () => {
  data.formVisible = !data.formVisible;
  document.body.style.overflow = data.formVisible ? 'hidden' : 'auto';
}

const handleClickOutside = (event) => {
  if (data.userPanelVisible) {
    const panelModal = document.querySelector('.panel-modal');
    const headerRightContent = document.querySelector('.right-content');

    if (panelModal && !panelModal.contains(event.target) &&
        headerRightContent && !headerRightContent.contains(event.target)) {
      data.userPanelVisible = false;
    }
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});


</script>

<style scoped lang="scss">
.header-container {
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  padding-right: 40px;
  align-items: center;
  user-select: none;
}

.left-content {
  display: flex;
  height: 40px;
}

.right-content {
  display: flex;
  flex-direction: column;
  height: 40px;
}

.left-button {
  display: flex;
  margin-right: 10px;
  height: 40px;
  width: 30px;
  border: 1px solid #e4e8ec;
  border-radius: $radius;
  justify-content: center;
  align-items: center;
}

.search {
  display: flex;
  width: 260px;
  border-radius: $radius;
  border: 1px solid #e4e8ec;
  align-items: center;

}

.search input {
  border: none !important;
  outline: none !important;
  background: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
  padding: 0 !important;
  margin: 0 !important;
  font: inherit !important;
  color: inherit !important;
}

.right-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  color: #666d7e;
}

.right-content span {
  margin-right: 5px;
  font-size: 13px;
  width: 80px;
  white-space: nowrap; /* 禁止换行 */
  overflow: hidden; /* 隐藏溢出内容 */
  text-overflow: ellipsis; /* 显示省略号 */
}

.login-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.panel-modal {
  position: absolute;
  top: 290px;
  right: 235px;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

//410+30
.modal-content {
  position: fixed;
  z-index: 1001;
}

</style>