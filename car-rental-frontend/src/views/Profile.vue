<template>
  <div class="profile-page">
    <AppNav transparent />

    <!-- Page Hero -->
    <section class="page-hero">
      <div class="page-hero-inner">
        <h1 class="page-title">个人中心</h1>
        <p class="page-desc">管理您的个人信息和账号设置</p>
      </div>
    </section>

    <!-- Content -->
    <div class="page-content">
      <div class="profile-grid">
        <!-- Info Card -->
        <div class="profile-card">
          <div class="card-header">
            <div class="card-avatar">{{ userInitial }}</div>
            <div class="card-user">
              <h2 class="user-name">{{ profile.realName || profile.username }}</h2>
              <span class="user-role">{{ profile.role === 'ADMIN' ? '管理员' : '普通用户' }}</span>
            </div>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <label>用户名</label>
              <span>{{ profile.username }}</span>
            </div>
            <div class="info-item">
              <label>信用分</label>
              <span class="credit-score" :class="scoreClass">{{ profile.creditScore ?? 100 }}</span>
            </div>
            <div class="info-item">
              <label>注册时间</label>
              <span>{{ formatDate(profile.createTime) }}</span>
            </div>
            <div class="info-item">
              <label>账号状态</label>
              <span class="status-tag" :class="profile.status === 1 ? 'status-active' : 'status-disabled'">
                {{ profile.status === 1 ? '正常' : '已禁用' }}
              </span>
            </div>
          </div>
        </div>

        <!-- Edit Card -->
        <div class="profile-card">
          <h3 class="form-title">编辑资料</h3>
          <el-form :model="form" label-width="80px" label-position="left" class="edit-form">
            <el-form-item label="用户名">
              <el-input :model-value="profile.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" maxlength="50" show-word-limit placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="驾照号">
              <el-input v-model="form.licenseNo" maxlength="18" show-word-limit placeholder="请输入驾照号" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" maxlength="20" show-word-limit placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <button class="btn-save" :disabled="saving" @click="handleSave">
                {{ saving ? '保存中...' : '保存修改' }}
              </button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="danger-zone">
        <h3 class="zone-title">快捷操作</h3>
        <div class="zone-actions">
          <button class="btn-ghost" @click="$router.push('/my-bookings')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="4" width="18" height="16" rx="2"/>
              <path d="M16 2v4M8 2v4M3 10h18"/>
            </svg>
            我的预订
          </button>
          <button class="btn-ghost" @click="$router.push('/fleet')">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="1" y="3" width="15" height="11"/>
              <polygon points="16,8 22,14 22,14 16,14"/>
              <circle cx="5.5" cy="18.5" r="2.5"/>
              <circle cx="18.5" cy="18.5" r="2.5"/>
            </svg>
            车型一览
          </button>
        </div>
      </div>
    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getMe, updateMe } from '../api/user'
import AppNav from '../components/AppNav.vue'
import AppFooter from '../components/AppFooter.vue'

const loading = ref(true)
const saving = ref(false)
const profile = ref({})

const form = reactive({
  realName: '',
  licenseNo: '',
  phone: '',
})

const userInitial = computed(() => (profile.value.realName || profile.value.username || '?')[0])

const scoreClass = computed(() => {
  const s = profile.value.creditScore ?? 100
  if (s >= 80) return 'score-good'
  if (s >= 60) return 'score-warn'
  return 'score-bad'
})

function formatDate(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

async function fetchProfile() {
  loading.value = true
  try {
    const res = await getMe()
    profile.value = res.data
    form.realName = res.data.realName || ''
    form.licenseNo = res.data.licenseNo || ''
    form.phone = res.data.phone || ''
  } catch (e) {
    console.error('获取个人信息失败:', e)
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  saving.value = true
  try {
    const res = await updateMe({
      realName: form.realName,
      licenseNo: form.licenseNo,
      phone: form.phone,
    })
    profile.value = res.data
    // 同步更新 localStorage
    const stored = JSON.parse(localStorage.getItem('user') || '{}')
    stored.realName = res.data.realName
    stored.licenseNo = res.data.licenseNo
    stored.phone = res.data.phone
    localStorage.setItem('user', JSON.stringify(stored))
    ElMessage.success('个人信息已更新')
  } catch (e) {
    console.error('更新个人信息失败:', e)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
/* ===== Layout ===== */
.profile-page {
  min-height: 100vh;
  background: #faf9ff;
}

.page-hero {
  padding: 120px 40px 32px;
  background: linear-gradient(160deg, #f0f0ff 0%, #f8f6ff 30%, #fef8ff 60%, #f5f0ff 100%);
}

.page-hero-inner {
  max-width: 840px;
  margin: 0 auto;
}

.page-title {
  font-family: var(--font-cn);
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.page-desc { font-size: 14px; color: #6a6a8a; }

.page-content {
  max-width: 840px;
  margin: 0 auto;
  padding: 32px 24px 64px;
}

/* ===== Grid ===== */
.profile-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.profile-card {
  background: #fff;
  border: 1px solid rgba(102,126,234,0.08);
  border-radius: 16px;
  padding: 28px;
}

/* ===== Info Card ===== */
.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(102,126,234,0.06);
}

.card-avatar {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-main);
  color: #fff;
  font-family: var(--font-cn);
  font-weight: 700;
  font-size: 22px;
  border-radius: 14px;
  flex-shrink: 0;
}

.user-name {
  font-family: var(--font-cn);
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 2px;
}

.user-role {
  font-size: 13px;
  color: #8a8aaa;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-size: 12px;
  color: #8a8aaa;
}

.info-item span {
  font-size: 15px;
  font-weight: 500;
  color: #1a1a2e;
}

.credit-score { font-family: var(--font-number); font-weight: 700 !important; }
.score-good { color: #67c23a; }
.score-warn { color: #e6a23c; }
.score-bad { color: #e84848; }

.status-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
  width: fit-content;
}

.status-active { background: rgba(103,194,58,0.1); color: #67c23a; }
.status-disabled { background: rgba(144,147,153,0.1); color: #909399; }

/* ===== Edit Form ===== */
.form-title {
  font-family: var(--font-cn);
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 20px;
}

.edit-form {
  margin-top: 4px;
}

.btn-save {
  padding: 10px 32px;
  background: var(--gradient-main);
  border: none;
  color: #fff;
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.22,1,0.36,1);
  box-shadow: 0 4px 16px rgba(102,126,234,0.25);
}

.btn-save:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(102,126,234,0.35);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* ===== Danger Zone ===== */
.danger-zone {
  background: #fff;
  border: 1px solid rgba(102,126,234,0.08);
  border-radius: 16px;
  padding: 24px 28px;
}

.zone-title {
  font-family: var(--font-cn);
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 16px;
}

.zone-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-ghost {
  padding: 10px 18px;
  background: #faf9ff;
  border: 1px solid rgba(102,126,234,0.1);
  color: #5a5a7a;
  font-family: var(--font-cn);
  font-size: 13px;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.25s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-ghost svg { width: 16px; height: 16px; }

.btn-ghost:hover {
  border-color: rgba(102,126,234,0.3);
  color: #667eea;
  background: rgba(102,126,234,0.04);
}

@media (max-width: 600px) {
  .profile-grid { grid-template-columns: 1fr; }
  .page-hero { padding: 100px 20px 24px; }
  .page-content { padding: 24px 16px 48px; }
}
</style>

<style>
.profile-page .el-input__wrapper {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(102,126,234,0.12);
}

.profile-page .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px #667eea, 0 0 0 3px rgba(102,126,234,0.08);
}

.profile-page .el-input.is-disabled .el-input__wrapper {
  background: #faf9ff;
  box-shadow: 0 0 0 1px rgba(102,126,234,0.06);
}
</style>
