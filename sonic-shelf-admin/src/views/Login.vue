<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>SonicShelf 管理员登录</h2>
        <p>Welcome to SonicShelf Admin</p>
      </div>
      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
          class="login-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              clearable
              show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              :loading="loading"
              @click="handleLogin"
              class="login-button"
              round
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/userStore'
import {ElMessage} from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur'}
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    loading.value = true
    await loginFormRef.value.validate()
    await userStore.login(loginForm)
    await router.push('/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #fc3d49 0%, #fc3b5b 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 40px 30px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    color: #333;
    font-size: 24px;
    margin: 0 0 10px;
  }

  p {
    color: #666;
    font-size: 14px;
    margin: 0;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  .el-input {
    height: 40px;
  }
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  font-weight: bold;
  background: linear-gradient(135deg, #fc3b5b 0%, #fc3d49 100%);
  border: none;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }
}
</style>