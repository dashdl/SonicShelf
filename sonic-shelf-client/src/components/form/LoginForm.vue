<script setup>
import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

const data = reactive({
  registered: true,
})

const formData = reactive({
  username: null,
  password: null,
  email: null,
  nickname: null,
  passwordConfirm: null,
})

const validateForm = () => {
  if (!formData.username || formData.username.trim() === '') {
    ElMessage.warning('用户名不能为空');
    return false;
  }

  if (!formData.password) {
    ElMessage.warning('密码不能为空');
    return false;
  }
  if (formData.password.length < 6 || formData.password.length > 12) {
    ElMessage.warning('密码长度必须在6-12位之间');
    return false;
  }

  if (!data.registered) {

    if (!formData.passwordConfirm) {
      ElMessage.warning('请确认密码');
      return false;
    }
    if (formData.password !== formData.passwordConfirm) {
      ElMessage.warning('两次输入的密码不一致');
      return false;
    }
  }

  return true;
}

const switchForm = () => {
  data.registered = !data.registered;
}

// 定义关闭事件
const emit = defineEmits(['close']);

// 添加关闭方法
const closeForm = () => {
  emit('close');
};

const login = () => {
  if (validateForm()) {
    request.post("/auth/login", formData).then((res) => {
      if (res.code === "200") {
        localStorage.setItem("token", res.data.access_token);
        ElMessage.success("登录成功");
        closeForm();
      } else {
        ElMessage.error(res.message);
      }
    })
  }
}

const signup = () => {
  if (validateForm()) {
    request.post("/auth/register", formData).then((res) => {
      if (res.code === "200") {
        switchForm();
        ElMessage.success("注册成功");
      } else {
        ElMessage.error(res.message);
      }
    })
  }
}
</script>

<template>
  <div class="form-container">
    <div class="close-button" @click="closeForm" style="cursor: pointer;">
      <img src="/icons/status/close.svg" style="width: 30px" alt="">
    </div>
    <div class="title">
      <img src="/logo.svg" style="width: 40px;margin-right: 5px" alt="">
      <span style="font-size: 25px;font-weight: bold">声栖音乐</span>
    </div>
    <div class="form">
      <input v-model="formData.username" placeholder="请输入用户名">
      <input v-if="!data.registered" v-model="formData.nickname" placeholder="请输入昵称">
      <input v-model="formData.password" placeholder="请输入密码">
      <input v-if="!data.registered" v-model="formData.passwordConfirm" placeholder="请再次确认密码">
      <div class="subitem">
        <span style="margin-right: 10px; cursor: pointer;">忘记密码</span>
        <hr style="height: 12px;margin-right: 10px">
        <span v-if="data.registered" @click="switchForm" style="cursor: pointer;">注册账号</span>
        <span v-if="!data.registered" @click="switchForm" style="cursor: pointer;">登录账号</span>
      </div>
      <div v-if="data.registered" class="button" @click="login" style="cursor: pointer;">
        登录
      </div>
      <div v-if="!data.registered" class="button" @click="signup" style="cursor: pointer;">
        注册
      </div>
    </div>
  </div>
</template>

<style scoped>

.form-container {
  user-select: none;
  display: flex;
  flex-direction: column;
  height: 480px;
  width: 325px;
  border-radius: 15px;
  background: #fff;
  padding: 20px;
}

.close-button {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.title {
  margin-bottom: 45px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  color: #666d7e;
}

input {
  padding: 0 20px;
  border: #e4e8ec solid 1px;
  outline: none !important;
  background: #f8f9f9;
  box-shadow: none !important;
  border-radius: 20px !important;
  margin-bottom: 22px !important;
  font: inherit !important;
  color: inherit !important;
  height: 35px;
  width: 100%;
}

.subitem {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: end;
}

.button {
  margin-top: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  justify-self: center;
  border-radius: 35px !important;
  background: linear-gradient(to right, #ff1168, #fc3d49);
  color: #ffffff;
  height: 35px;
  width: 95%;
  cursor: pointer;
}
</style>