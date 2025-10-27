<template>
  <div class="header-container">
    <div class="left-content">
      <div class="left-button" @click="formSwitch">

      </div>
      <div class="search">
        <img src="/icons/logo.png" style="height: 20px;margin-left: 10px;margin-right: 10px;" alt="">
        <input>
      </div>
    </div>
    <div class="right-content">
      <img src="/icons/头像.jpg" style="height: 30px;border-radius: 15px; margin-right: 5px" alt="">
      <span>被剥夺名字的人</span>
      <img src="" alt="">
      <img @click="data.userPanelVisible=!data.userPanelVisible" src="/icons/down.svg"
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
        <UserPanel/>
      </div>
    </div>
  </div>

</template>

<script setup>
import {reactive} from "vue";
import LoginForm from "@/components/form/LoginForm.vue";
import UserPanel from "@/components/form/UserPanel.vue";

const data = reactive({
  formVisible: false,
  userPanelVisible: false,
})

const formSwitch = () => {
  data.formVisible = !data.formVisible;
  document.body.style.overflow = data.formVisible ? 'hidden' : 'auto';
}

import {onMounted, onUnmounted} from 'vue';

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
  padding-right: 45px;
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
  display: block;
  margin-right: 10px;
  height: 40px;
  width: 30px;
  border: 1px solid #e4e8ec;
  border-radius: $radius;
  justify-items: center;
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