<template>
  <div class="register-container">
    <div class="register-box card">
      <h1 class="title">用户注册</h1>
      <form @submit.prevent="handleRegister">
        <div class="form-item">
          <label class="form-label">用户名</label>
          <input v-model="form.username" type="text" class="input" placeholder="请输入用户名" />
          <div class="form-error" v-if="errors.username">{{ errors.username }}</div>
        </div>
        <div class="form-item">
          <label class="form-label">密码</label>
          <input v-model="form.password" type="password" class="input" placeholder="请输入密码（至少6位）" />
          <div class="form-error" v-if="errors.password">{{ errors.password }}</div>
        </div>
        <div class="form-item">
          <label class="form-label">真实姓名</label>
          <input v-model="form.realName" type="text" class="input" placeholder="请输入真实姓名" />
          <div class="form-error" v-if="errors.realName">{{ errors.realName }}</div>
        </div>
        <div class="form-item">
          <label class="form-label">手机号</label>
          <input v-model="form.phone" type="text" class="input" placeholder="请输入手机号" />
          <div class="form-error" v-if="errors.phone">{{ errors.phone }}</div>
        </div>
        <div class="form-item">
          <label class="form-label">邮箱</label>
          <input v-model="form.mail" type="text" class="input" placeholder="请输入邮箱" />
          <div class="form-error" v-if="errors.mail">{{ errors.mail }}</div>
        </div>
        <div class="form-error" v-if="errorMsg">{{ errorMsg }}</div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      <div class="footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'

const router = useRouter()
const form = ref({
  username: '',
  password: '',
  realName: '',
  phone: '',
  mail: ''
})
const errors = ref({})
const errorMsg = ref('')
const loading = ref(false)

const validate = () => {
  errors.value = {}
  const { username, password, phone, mail } = form.value

  if (!username) {
    errors.value.username = '请输入用户名'
  }
  if (!password) {
    errors.value.password = '请输入密码'
  } else if (password.length < 6) {
    errors.value.password = '密码至少6位'
  }
  if (!phone) {
    errors.value.phone = '请输入手机号'
  } else if (!/^1[3-9]\d{9}$/.test(phone)) {
    errors.value.phone = '手机号格式不正确'
  }
  if (!mail) {
    errors.value.mail = '请输入邮箱'
  } else if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(mail)) {
    errors.value.mail = '邮箱格式不正确'
  }

  return Object.keys(errors.value).length === 0
}

const handleRegister = async () => {
  if (!validate()) return

  loading.value = true
  errorMsg.value = ''

  try {
    await register(form.value)
    // 响应拦截器已处理成功/失败，这里能执行说明注册成功
    alert('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    errorMsg.value = error.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 0;
}

.register-box {
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
