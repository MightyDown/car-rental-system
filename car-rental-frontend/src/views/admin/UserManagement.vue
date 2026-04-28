<template>
  <div class="user-management">
    <div class="page-header scroll-reveal">
      <div>
        <h1 class="page-title">用户管理</h1>
        <p class="page-desc">管理系统用户账号、角色权限与信用分</p>
      </div>
    </div>

    <div class="toolbar">
      <div class="search-bar">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="11" cy="11" r="8"/>
          <path d="M21 21l-4.35-4.35"/>
        </svg>
        <input
          v-model="keyword"
          placeholder="搜索用户名、姓名或手机号..."
          class="search-input"
          @keyup.enter="handleSearch"
        />
      </div>
      <button class="btn-search" @click="handleSearch">搜索</button>
    </div>

    <div class="table-card">
      <el-table :data="users" v-loading="loading" stripe empty-text="暂无用户数据"
                style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="120">
          <template #default="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="licenseNo" label="驾照号" min-width="140">
          <template #default="{ row }">
            {{ row.licenseNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="90" align="center">
          <template #default="{ row }">
            <span class="role-tag" :class="row.role === 'ADMIN' ? 'role-admin' : 'role-user'">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <span class="status-dot" :class="row.status === 1 ? 'status-on' : 'status-off'"></span>
            {{ row.status === 1 ? '正常' : '禁用' }}
          </template>
        </el-table-column>
        <el-table-column prop="creditScore" label="信用分" width="90" align="center">
          <template #default="{ row }">
            <span class="credit-score" :class="creditClass(row.creditScore)">
              {{ row.creditScore ?? 100 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" min-width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <button class="action-btn edit" @click="openEdit(row)">编辑</button>
              <button class="action-btn" :class="row.status === 1 ? 'disable' : 'enable'"
                      @click="toggleStatus(row)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </button>
              <button class="action-btn delete" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchUsers"
        @current-change="fetchUsers"
      />
    </div>

    <el-dialog v-model="editVisible" title="编辑用户" width="520px" destroy-on-close
               :close-on-click-modal="false">
      <el-form :model="editForm" label-width="80px" label-position="left">
        <el-form-item label="用户名">
          <el-input :model-value="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" maxlength="50" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" maxlength="20" />
        </el-form-item>
        <el-form-item label="驾照号">
          <el-input v-model="editForm.licenseNo" maxlength="18" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="信用分">
          <el-input-number v-model="editForm.creditScore" :min="0" :max="200" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUser, updateUserStatus, deleteUser } from '../../api/user'

const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const loading = ref(false)

const currentUserId = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('user') || '{}').id
  } catch { return null }
})

const headerStyle = {
  background: '#faf9ff',
  color: '#1a1a2e',
  fontWeight: 600,
  fontSize: '13px',
}

function formatTime(t) {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

function creditClass(score) {
  const s = score ?? 100
  if (s >= 80) return 'credit-good'
  if (s >= 60) return 'credit-warn'
  return 'credit-bad'
}

async function fetchUsers() {
  loading.value = true
  try {
    const res = await getUsers({
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword.value || undefined,
    })
    users.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch {
    users.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchUsers()
}

async function toggleStatus(row) {
  const action = row.status === 1 ? '禁用' : '启用'
  if (row.id === currentUserId.value) {
    ElMessage.warning('不能修改自己的账号状态')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要${action}用户「${row.username}」吗？`, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch { return }

  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`已${action}`)
    fetchUsers()
  } catch {}
}

async function handleDelete(row) {
  if (row.id === currentUserId.value) {
    ElMessage.warning('不能删除自己的账号')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？此操作不可恢复。`, '确认删除', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch { return }

  try {
    await deleteUser(row.id)
    ElMessage.success('已删除')
    if (users.value.length === 1 && currentPage.value > 1) {
      currentPage.value--
    }
    fetchUsers()
  } catch {}
}

// ===== Edit Dialog =====
const editVisible = ref(false)
const submitting = ref(false)
const editForm = ref({})

function openEdit(row) {
  editForm.value = {
    id: row.id,
    username: row.username,
    realName: row.realName || '',
    phone: row.phone || '',
    licenseNo: row.licenseNo || '',
    role: row.role,
    creditScore: row.creditScore ?? 100,
  }
  editVisible.value = true
}

async function submitEdit() {
  submitting.value = true
  try {
    await updateUser(editForm.value.id, {
      realName: editForm.value.realName,
      phone: editForm.value.phone,
      licenseNo: editForm.value.licenseNo,
      role: editForm.value.role,
      creditScore: editForm.value.creditScore,
    })
    ElMessage.success('更新成功')
    editVisible.value = false
    fetchUsers()
  } catch {} finally {
    submitting.value = false
  }
}

onMounted(async () => {
  fetchUsers()
  const { nextTick } = await import('vue')
  await nextTick()
  initScrollReveal()
})

function initScrollReveal() {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) { entry.target.classList.add('revealed'); observer.unobserve(entry.target) }
      })
    },
    { threshold: 0.15, rootMargin: '0px 0px -40px 0px' }
  )
  document.querySelectorAll('.scroll-reveal').forEach(el => observer.observe(el))
}
</script>

<style scoped>
.user-management {
  max-width: 1200px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-family: var(--font-cn);
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
}

/* ===== Toolbar ===== */
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.search-bar {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.search-bar:focus-within {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.08);
}

.search-icon {
  width: 18px;
  height: 18px;
  color: var(--text-muted);
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  background: transparent;
  color: var(--text-primary);
  font-family: var(--font-body);
}

.search-input::placeholder {
  color: var(--text-muted);
}

.btn-search {
  padding: 10px 24px;
  background: var(--gradient-main);
  border: none;
  color: #fff;
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}

.btn-search:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
}

/* ===== Table Card ===== */
.table-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
}

/* ===== Tags ===== */
.role-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
}

.role-admin {
  background: rgba(102, 126, 234, 0.1);
  color: var(--accent);
}

.role-user {
  background: rgba(118, 75, 162, 0.06);
  color: #764ba2;
}

.status-dot {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  margin-right: 4px;
}

.status-on {
  background: var(--success);
  box-shadow: 0 0 6px rgba(78, 205, 196, 0.5);
}

.status-off {
  background: #ccc;
}

/* ===== Credit Score ===== */
.credit-good {
  color: var(--success);
  font-weight: 600;
}

.credit-warn {
  color: #e6a23c;
  font-weight: 600;
}

.credit-bad {
  color: var(--danger);
  font-weight: 600;
}

/* ===== Actions ===== */
.actions {
  display: flex;
  gap: 6px;
  justify-content: center;
}

.action-btn {
  padding: 4px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: #fff;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
}

.action-btn.edit {
  color: var(--accent);
}

.action-btn.edit:hover {
  background: rgba(102, 126, 234, 0.06);
  border-color: var(--accent);
}

.action-btn.disable {
  color: #e6a23c;
}

.action-btn.disable:hover {
  background: rgba(230, 162, 60, 0.06);
  border-color: #e6a23c;
}

.action-btn.enable {
  color: var(--success);
}

.action-btn.enable:hover {
  background: rgba(78, 205, 196, 0.06);
  border-color: var(--success);
}

.action-btn.delete {
  color: var(--danger);
}

.action-btn.delete:hover {
  background: rgba(232, 72, 72, 0.06);
  border-color: var(--danger);
}

/* ===== Pagination ===== */
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.scroll-reveal { opacity: 0; transform: translateY(40px); transition: all 0.7s cubic-bezier(0.22,1,0.36,1); transition-delay: var(--delay,0s); }
.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }
</style>

<style>
.user-management .el-table {
  --el-table-border-color: rgba(102, 126, 234, 0.06);
  --el-table-header-bg-color: #faf9ff;
}

.user-management .el-table th.el-table__cell {
  background: #faf9ff;
}

.user-management .el-table .el-table__row:hover > td.el-table__cell {
  background: rgba(102, 126, 234, 0.03);
}

.user-management .el-pagination .el-pager li.is-active {
  background: var(--gradient-main);
  border-radius: 6px;
  color: #fff;
}

.user-management .el-dialog {
  border-radius: 16px;
}

.user-management .el-dialog__header {
  border-bottom: 1px solid var(--border);
  padding: 20px 24px;
}

.user-management .el-dialog__title {
  font-family: var(--font-cn);
  font-weight: 600;
  color: var(--text-primary);
}

.user-management .el-dialog__body {
  padding: 24px;
}

.user-management .el-dialog__footer {
  padding: 16px 24px;
  border-top: 1px solid var(--border);
}

.user-management .el-button--primary {
  background: var(--gradient-main);
  border: none;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}

.user-management .el-button--primary:hover {
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
  transform: translateY(-1px);
}

.user-management .el-input__wrapper,
.user-management .el-select .el-input__wrapper,
.user-management .el-input-number .el-input__wrapper {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.12);
}

.user-management .el-input__wrapper:hover,
.user-management .el-select .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.3);
}

.user-management .el-input.is-focus .el-input__wrapper,
.user-management .el-select .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--accent), 0 0 0 3px rgba(102, 126, 234, 0.08);
}
</style>
