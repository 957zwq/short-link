<template>
  <div class="login-container">
    <div class="login-box card">
      <h1 class="title">短链接管理后台</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <input v-model="form.username" type="text" class="input" placeholder="请输入用户名" />
        </div>
        <div class="form-item">
          <label class="form-label">密码</label>
          <input v-model="form.password" type="password" class="input" placeholder="请输入密码" />
        </div>
        <div class="form-error" v-if="errorMsg">{{ errorMsg }}</div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      <div class="footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})
const errorMsg = ref('')
const loading = ref(false)

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  errorMsg.value = ''

  try {
    const res = await login(form.value)
    // 响应拦截器已处理成功/失败，这里 res 一定是成功响应
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    router.push('/users')
  } catch (error) {
    errorMsg.value = error.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

form button {
  width: 100%;
  margin-top: 10px;
}

.footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.footer a {
  color: #1890ff;
  text-decoration: none;
}

.footer a:hover {
  text-decoration: underline;
}
</style>
