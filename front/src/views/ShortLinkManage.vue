<template>
  <div class="shortlink-container">
    <div class="header">
      <h1>短链接管理</h1>
      <div class="user-info">
        <router-link to="/users" class="btn btn-secondary">返回用户中心</router-link>
        <span>{{ username }}</span>
        <button @click="handleLogout" class="btn btn-secondary">退出登录</button>
      </div>
    </div>

    <!-- 创建短链接卡片 -->
    <div class="section card">
      <div class="section-header">
        <h2>创建短链接</h2>
        <button @click="openCreateModal" class="btn btn-primary">+ 创建短链接</button>
      </div>
    </div>

    <!-- 短链接列表卡片 -->
    <div class="section card">
      <div class="section-header">
        <h2>短链接列表</h2>
        <div class="filter-controls">
          <select v-model="currentGid" @change="loadShortLinks" class="input" style="width: 200px;">
            <option value="">全部分组</option>
            <option v-for="group in groups" :key="group.gid" :value="group.gid">
              {{ group.name }}
            </option>
          </select>
        </div>
      </div>

      <div class="table-container" v-if="shortLinks.length > 0">
        <table class="table">
          <thead>
            <tr>
              <th width="80">序号</th>
              <th>完整短链接</th>
              <th>原始链接</th>
              <th>分组标识</th>
              <th>有效期类型</th>
              <th>有效期</th>
              <th>描述</th>
              <th>点击量</th>
              <th>创建时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(link, index) in shortLinks" :key="link.id">
              <td>{{ (current - 1) * size + index + 1 }}</td>
              <td>
                <a :href="link.fullShortUrl" target="_blank" class="link-text">{{ link.fullShortUrl }}</a>
                <button @click="copyToClipboard(link.fullShortUrl)" class="btn-link copy-btn">复制</button>
              </td>
              <td class="origin-url" :title="link.originUrl">{{ link.originUrl }}</td>
              <td class="gid-text">{{ link.gid }}</td>
              <td>{{ link.validDateType === 0 ? '永久' : '自定义' }}</td>
              <td>{{ formatDate(link.validDate) }}</td>
              <td>{{ link.describe || '-' }}</td>
              <td>{{ link.clickNum || 0 }}</td>
              <td>{{ formatDate(link.createTime) }}</td>
            </tr>
          </tbody>
        </table>

        <!-- 分页 -->
        <div class="pagination">
          <button 
            @click="changePage(current - 1)" 
            :disabled="current === 1" 
            class="btn btn-secondary"
          >
            上一页
          </button>
          <span class="page-info">第 {{ current }} 页，共 {{ pages }} 页，总计 {{ total }} 条</span>
          <button 
            @click="changePage(current + 1)" 
            :disabled="current === pages" 
            class="btn btn-secondary"
          >
            下一页
          </button>
        </div>
      </div>

      <div class="empty" v-else>
        <p>暂无短链接数据</p>
      </div>
    </div>

    <!-- 创建短链接弹窗 -->
    <div class="modal-overlay" v-if="showCreateModal" @click.self="closeCreateModal">
      <div class="modal card">
        <h2>创建短链接</h2>
        <form @submit.prevent="handleCreate">
          <div class="form-item">
            <label class="form-label">选择分组</label>
            <select v-model="createForm.gid" class="input">
              <option value="">请选择分组</option>
              <option v-for="group in groups" :key="group.gid" :value="group.gid">
                {{ group.name }}
              </option>
            </select>
            <div class="form-error" v-if="errors.gid">{{ errors.gid }}</div>
          </div>
          <div class="form-item">
            <label class="form-label">原始链接</label>
            <textarea 
              v-model="createForm.originUrl" 
              class="input textarea" 
              placeholder="请输入原始链接（URL）"
              rows="3"
            ></textarea>
            <div class="form-error" v-if="errors.originUrl">{{ errors.originUrl }}</div>
          </div>
          <div class="form-item">
            <label class="form-label">域名</label>
            <input 
              v-model="createForm.domain" 
              type="text" 
              class="input" 
              placeholder="请输入域名（如：http://suo.im）"
            />
            <div class="form-error" v-if="errors.domain">{{ errors.domain }}</div>
          </div>
          <div class="form-item">
            <label class="form-label">有效期类型</label>
            <select v-model="createForm.validDateType" class="input">
              <option :value="0">永久有效</option>
              <option :value="1">自定义有效期</option>
            </select>
          </div>
          <div class="form-item" v-if="createForm.validDateType === 1">
            <label class="form-label">有效期</label>
            <input 
              v-model="createForm.validDate" 
              type="datetime-local" 
              class="input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">描述</label>
            <input 
              v-model="createForm.describe" 
              type="text" 
              class="input" 
              placeholder="请输入描述信息（可选）"
            />
          </div>
          <div class="form-error" v-if="errorMsg">{{ errorMsg }}</div>
          <div class="modal-actions">
            <button type="button" @click="closeCreateModal" class="btn btn-secondary">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="creating">
              {{ creating ? '创建中...' : '创建' }}
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
import { createShortLink, pageShortLink } from '@/api/shortLink'
import { getGroups } from '@/api/group'
import { logout } from '@/api/user'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')

// 短链接相关
const shortLinks = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const pages = ref(0)
const currentGid = ref('')

// 分组相关
const groups = ref([])

// 创建弹窗相关
const showCreateModal = ref(false)
const creating = ref(false)
const errorMsg = ref('')
const errors = ref({})
const createForm = ref({
  gid: '',
  originUrl: '',
  domain: '',
  validDateType: 0,
  validDate: null,
  describe: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { 
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载分组列表
const loadGroups = async () => {
  try {
    const res = await getGroups()
    groups.value = res.data || []
  } catch (error) {
    alert('加载分组失败：' + error.message)
  }
}

// 加载短链接列表
const loadShortLinks = async () => {
  try {
    const params = {
      current: current.value,
      size: size.value,
      gid: currentGid.value || ''
    }
    const res = await pageShortLink(params)
    shortLinks.value = res.data.records || []
    total.value = res.data.total || 0
    pages.value = res.data.pages || 0
  } catch (error) {
    alert('加载短链接失败：' + error.message)
  }
}

// 分页
const changePage = (page) => {
  if (page < 1 || page > pages.value) return
  current.value = page
  loadShortLinks()
}

// 创建操作
const openCreateModal = () => {
  showCreateModal.value = true
  createForm.value = {
    gid: '',
    originUrl: '',
    domain: '',
    validDateType: 0,
    validDate: null,
    describe: ''
  }
  errors.value = {}
  errorMsg.value = ''
}

const closeCreateModal = () => {
  showCreateModal.value = false
}

const validate = () => {
  errors.value = {}
  const { gid, originUrl, domain } = createForm.value

  if (!gid) {
    errors.value.gid = '请选择分组'
  }
  if (!originUrl) {
    errors.value.originUrl = '请输入原始链接'
  } else if (!/^https?:\/\//.test(originUrl)) {
    errors.value.originUrl = '请输入有效的 URL（以 http:// 或 https:// 开头）'
  }
  if (!domain) {
    errors.value.domain = '请输入域名'
  } else if (!/^https?:\/\//.test(domain)) {
    errors.value.domain = '域名应包含协议（如：http://suo.im）'
  }

  return Object.keys(errors.value).length === 0
}

const handleCreate = async () => {
  if (!validate()) return

  creating.value = true
  errorMsg.value = ''

  try {
    await createShortLink(createForm.value)
    alert('创建成功')
    closeCreateModal()
    loadShortLinks()
  } catch (error) {
    errorMsg.value = error.message || '创建失败，请重试'
  } finally {
    creating.value = false
  }
}

// 复制链接
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    alert('复制成功')
  } catch (error) {
    alert('复制失败：' + error.message)
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
  if (!username.value) {
    router.push('/login')
    return
  }
  loadGroups()
  loadShortLinks()
})
</script>

<style scoped>
.shortlink-container {
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

.filter-controls {
  display: flex;
  gap: 10px;
}

.table-container {
  overflow-x: auto;
}

.empty {
  text-align: center;
  padding: 40px;
  color: #999;
}

.link-text {
  color: #1890ff;
  text-decoration: none;
  word-break: break-all;
}

.link-text:hover {
  text-decoration: underline;
}

.copy-btn {
  margin-left: 8px;
  padding: 2px 8px;
  font-size: 12px;
}

.origin-url {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.page-info {
  color: #666;
  font-size: 14px;
}

.textarea {
  resize: vertical;
  font-family: monospace;
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
  width: 600px;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
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
</style>
