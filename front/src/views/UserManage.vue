<template>
  <div class="manage-container">
    <div class="header">
      <h1>用户中心</h1>
      <div class="user-info">
        <span>{{ currentUser.username }}</span>
        <button @click="handleLogout" class="btn btn-secondary">退出登录</button>
      </div>
    </div>

    <!-- 个人信息卡片 -->
    <div class="section card">
      <div class="section-header">
        <h2>个人信息</h2>
        <button @click="openEditUserModal" class="btn btn-primary" style="padding: 5px 15px;">编辑</button>
      </div>
      <div class="user-detail">
        <div class="detail-row">
          <span class="label">用户名：</span>
          <span class="value">{{ currentUser.username }}</span>
        </div>
        <div class="detail-row">
          <span class="label">真实姓名：</span>
          <span class="value">{{ currentUser.realName || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">手机号：</span>
          <span class="value">{{ currentUser.phone || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">邮箱：</span>
          <span class="value">{{ currentUser.mail || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span class="value">{{ formatDate(currentUser.createTime) }}</span>
        </div>
      </div>
    </div>

    <!-- 分组管理卡片 -->
    <div class="section card">
      <div class="section-header">
        <h2>我的分组</h2>
        <button @click="openCreateGroupModal" class="btn btn-primary">+ 新增分组</button>
      </div>

      <div class="table-container" v-if="groups.length > 0">
        <table class="table">
          <thead>
            <tr>
              <th width="60">排序</th>
              <th>分组名称</th>
              <th>分组标识</th>
              <th>创建时间</th>
              <th width="180">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(group, index) in groups" :key="group.gid">
              <td>
                <span class="sort-controls">
                  <span class="sort-btn" :class="{ disabled: index === 0 }" @click="moveUp(index)">↑</span>
                  <span class="sort-btn" :class="{ disabled: index === groups.length - 1 }" @click="moveDown(index)">↓</span>
                </span>
              </td>
              <td>{{ group.name }}</td>
              <td class="gid-text">{{ group.gid }}</td>
              <td>{{ formatDate(group.createTime) }}</td>
              <td>
                <button @click="openEditGroupModal(group)" class="btn-link">编辑</button>
                <button @click="handleDeleteGroup(group)" class="btn-link btn-link-danger">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="empty" v-else>
        <p>暂无分组，点击上方按钮创建分组</p>
      </div>
    </div>

    <!-- 编辑用户信息弹窗 -->
    <div class="modal-overlay" v-if="showEditUserModal" @click.self="closeEditUserModal">
      <div class="modal card">
        <h2>编辑个人信息</h2>
        <form @submit.prevent="handleUpdateUser">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <input :value="currentUser.username" type="text" class="input" disabled />
          </div>
          <div class="form-item">
            <label class="form-label">真实姓名</label>
            <input v-model="userEditForm.realName" type="text" class="input" />
          </div>
          <div class="form-item">
            <label class="form-label">手机号</label>
            <input v-model="userEditForm.phone" type="text" class="input" />
          </div>
          <div class="form-item">
            <label class="form-label">邮箱</label>
            <input v-model="userEditForm.mail" type="text" class="input" />
          </div>
          <div class="form-error" v-if="userEditError">{{ userEditError }}</div>
          <div class="modal-actions">
            <button type="button" @click="closeEditUserModal" class="btn btn-secondary">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="userEditLoading">
              {{ userEditLoading ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 新增/编辑分组弹窗 -->
    <div class="modal-overlay" v-if="showGroupModal" @click.self="closeGroupModal">
      <div class="modal card">
        <h2>{{ isEditGroup ? '编辑分组' : '新增分组' }}</h2>
        <form @submit.prevent="handleGroupSubmit">
          <div class="form-item">
            <label class="form-label">分组名称</label>
            <input
              v-model="groupForm.name"
              type="text"
              class="input"
              placeholder="请输入分组名称（最多64个字符）"
              maxlength="64"
            />
            <div class="form-error" v-if="groupFormError.name">{{ groupFormError.name }}</div>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeGroupModal" class="btn btn-secondary">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="groupSubmitting">
              {{ groupSubmitting ? '保存中...' : '保存' }}
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
import { getGroups, createGroup, updateGroup, deleteGroup, sortGroup } from '@/api/group'

const router = useRouter()
const currentUser = ref({
  username: localStorage.getItem('username') || '',
  realName: '',
  phone: '',
  mail: '',
  createTime: ''
})

// 用户编辑相关
const showEditUserModal = ref(false)
const userEditForm = ref({
  username: '',
  realName: '',
  phone: '',
  mail: ''
})
const userEditError = ref('')
const userEditLoading = ref(false)

// 分组相关
const groups = ref([])
const showGroupModal = ref(false)
const isEditGroup = ref(false)
const groupSubmitting = ref(false)
const groupForm = ref({
  gid: '',
  name: ''
})
const groupFormError = ref({
  name: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 加载当前用户信息
const loadCurrentUser = async () => {
  try {
    const res = await getUserByUsername(currentUser.value.username)
    currentUser.value = res.data
  } catch (error) {
    alert('加载用户信息失败: ' + error.message)
  }
}

// 加载分组列表
const loadGroups = async () => {
  try {
    const res = await getGroups()
    groups.value = res.data || []
  } catch (error) {
    alert('加载分组失败: ' + error.message)
  }
}

// 用户编辑
const openEditUserModal = () => {
  userEditForm.value = {
    username: currentUser.value.username,
    realName: currentUser.value.realName || '',
    phone: currentUser.value.phone || '',
    mail: currentUser.value.mail || ''
  }
  userEditError.value = ''
  showEditUserModal.value = true
}

const closeEditUserModal = () => {
  showEditUserModal.value = false
}

const handleUpdateUser = async () => {
  userEditLoading.value = true
  userEditError.value = ''

  try {
    await updateUser(userEditForm.value)
    alert('修改成功')
    closeEditUserModal()
    loadCurrentUser()
  } catch (error) {
    userEditError.value = error.message || '修改失败'
  } finally {
    userEditLoading.value = false
  }
}

// 分组管理
const openCreateGroupModal = () => {
  isEditGroup.value = false
  groupForm.value = { gid: '', name: '' }
  groupFormError.value = { name: '' }
  showGroupModal.value = true
}

const openEditGroupModal = (group) => {
  isEditGroup.value = true
  groupForm.value = { gid: group.gid, name: group.name }
  groupFormError.value = { name: '' }
  showGroupModal.value = true
}

const closeGroupModal = () => {
  showGroupModal.value = false
}

const handleGroupSubmit = async () => {
  groupFormError.value = { name: '' }
  if (!groupForm.value.name.trim()) {
    groupFormError.value.name = '请输入分组名称'
    return
  }
  if (groupForm.value.name.length > 64) {
    groupFormError.value.name = '分组名称不能超过64个字符'
    return
  }

  groupSubmitting.value = true
  try {
    if (isEditGroup.value) {
      await updateGroup({ gid: groupForm.value.gid, name: groupForm.value.name })
      alert('修改成功')
    } else {
      await createGroup({ name: groupForm.value.name })
      alert('创建成功')
    }
    closeGroupModal()
    loadGroups()
  } catch (error) {
    alert((isEditGroup.value ? '修改' : '创建') + '失败: ' + error.message)
  } finally {
    groupSubmitting.value = false
  }
}

const handleDeleteGroup = (group) => {
  if (!confirm(`确定要删除分组"${group.name}"吗？`)) return
  deleteGroup(group.gid)
    .then(() => {
      alert('删除成功')
      loadGroups()
    })
    .catch((error) => {
      alert('删除失败: ' + error.message)
    })
}

const moveUp = (index) => {
  if (index === 0) return
  const newGroups = [...groups.value]
  ;[newGroups[index - 1], newGroups[index]] = [newGroups[index], newGroups[index - 1]]
  groups.value = newGroups
  saveSort()
}

const moveDown = (index) => {
  if (index === groups.value.length - 1) return
  const newGroups = [...groups.value]
  ;[newGroups[index], newGroups[index + 1]] = [newGroups[index + 1], newGroups[index]]
  groups.value = newGroups
  saveSort()
}

const saveSort = async () => {
  const sortData = groups.value.map((g, index) => ({
    gid: g.gid,
    sortOrder: index
  }))
  try {
    await sortGroup(sortData)
  } catch (error) {
    alert('排序保存失败: ' + error.message)
    loadGroups()
  }
}

// 退出登录
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
  if (!currentUser.value.username) {
    router.push('/login')
    return
  }
  loadCurrentUser()
  loadGroups()
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

.section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header h2 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.user-detail {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.detail-row {
  display: flex;
}

.detail-row .label {
  color: #666;
  min-width: 80px;
}

.detail-row .value {
  color: #333;
}

.table-container {
  overflow-x: auto;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.gid-text {
  font-family: monospace;
  color: #666;
  font-size: 13px;
}

.btn-link {
  background: none;
  border: none;
  color: #1890ff;
  cursor: pointer;
  padding: 4px 8px;
  font-size: 14px;
}

.btn-link:hover {
  text-decoration: underline;
}

.btn-link-danger {
  color: #ff4d4f;
}

.btn-link-danger:hover {
  color: #ff7875;
}

.sort-controls {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.sort-btn {
  display: inline-block;
  cursor: pointer;
  color: #1890ff;
  font-size: 16px;
  line-height: 1;
  padding: 2px 6px;
  user-select: none;
}

.sort-btn:hover {
  background: #e6f7ff;
}

.sort-btn.disabled {
  color: #d9d9d9;
  cursor: not-allowed;
}

.sort-btn.disabled:hover {
  background: none;
}

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
