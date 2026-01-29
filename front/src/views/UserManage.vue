<template>
  <div class="manage-container">
    <div class="header">
      <h1>用户管理</h1>
      <div class="user-info">
        <span>{{ username }}</span>
        <button @click="handleLogout" class="btn btn-secondary">退出登录</button>
      </div>
    </div>

    <div class="content card">
      <div class="search-box">
        <input v-model="searchUsername" type="text" class="input" placeholder="输入用户名查询" style="width: 200px;" />
        <button @click="handleSearch" class="btn btn-primary">查询</button>
        <button @click="handleReset" class="btn btn-secondary">重置</button>
      </div>

      <div class="table-container" v-if="user">
        <table class="table">
          <thead>
            <tr>
              <th>用户名</th>
              <th>真实姓名</th>
              <th>手机号</th>
              <th>邮箱</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>{{ user.username }}</td>
              <td>{{ user.realName || '-' }}</td>
              <td>{{ user.phone || '-' }}</td>
              <td>{{ user.mail || '-' }}</td>
              <td>{{ formatDate(user.createTime) }}</td>
              <td>
                <button @click="openEditModal" class="btn btn-primary" style="padding: 5px 10px; font-size: 12px;">编辑</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="empty" v-else>
        <p>请输入用户名查询用户信息</p>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div class="modal-overlay" v-if="showEditModal" @click.self="showEditModal = false">
      <div class="modal card">
        <h2>编辑用户</h2>
        <form @submit.prevent="handleUpdate">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <input :value="user?.username" type="text" class="input" disabled />
          </div>
          <div class="form-item">
            <label class="form-label">真实姓名</label>
            <input v-model="editForm.realName" type="text" class="input" />
          </div>
          <div class="form-item">
            <label class="form-label">手机号</label>
            <input v-model="editForm.phone" type="text" class="input" />
          </div>
          <div class="form-item">
            <label class="form-label">邮箱</label>
            <input v-model="editForm.mail" type="text" class="input" />
          </div>
          <div class="form-error" v-if="updateError">{{ updateError }}</div>
          <div class="modal-actions">
            <button type="button" @click="showEditModal = false" class="btn btn-secondary">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="updateLoading">
              {{ updateLoading ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserByUsername, updateUser, logout } from '@/api/user'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')
const searchUsername = ref('')
const user = ref(null)
const showEditModal = ref(false)
const editForm = ref({
  username: '',
  realName: '',
  phone: '',
  mail: ''
})
const updateError = ref('')
const updateLoading = ref(false)

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const handleSearch = async () => {
  if (!searchUsername.value) {
    alert('请输入用户名')
    return
  }

  try {
    const res = await getUserByUsername(searchUsername.value)
    user.value = res.data
  } catch (error) {
    alert('查询失败: ' + error.message)
    user.value = null
  }
}

const handleReset = () => {
  searchUsername.value = ''
  user.value = null
}

const handleUpdate = async () => {
  updateLoading.value = true
  updateError.value = ''

  try {
    await updateUser(editForm.value)
    alert('修改成功')
    showEditModal.value = false
    handleSearch()
  } catch (error) {
    updateError.value = error.message || '修改失败'
  } finally {
    updateLoading.value = false
  }
}

// 打开编辑弹窗时填充数据
const openEditModal = () => {
  if (user.value) {
    editForm.value = {
      username: user.value.username,
      realName: user.value.realName || '',
      phone: user.value.phone || '',
      mail: user.value.mail || ''
    }
    showEditModal.value = true
  }
}

const handleLogout = async () => {
  try {
    await logout()
  } catch (error) {
    console.log('logout error:', error)
  } finally {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    router.push('/login')
  }
}

onMounted(() => {
  if (!username.value) {
    router.push('/login')
  }
})
</script>

<style scoped>
.manage-container {
  min-height: 100vh;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: 400px;
  max-width: 90%;
}

.modal h2 {
  margin-bottom: 20px;
  color: #333;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}
</style>
