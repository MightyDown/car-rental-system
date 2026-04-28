<template>
  <div class="auth-page">
    <!-- Background Decorations -->
    <div class="auth-bg">
      <div class="auth-grid"></div>
      <div class="auth-orb orb-1"></div>
      <div class="auth-orb orb-2"></div>
      <div class="auth-orb orb-3"></div>
      <span v-for="i in 6" :key="i" class="auth-particle" :style="{ '--i': i }"></span>
    </div>

    <!-- Card -->
    <div class="auth-card">
      <div class="auth-header anim-fade-up" style="--delay: 0s">
        <div class="auth-brand">
          <span class="brand-icon">C</span>
        </div>
        <h1>欢迎回来</h1>
        <p>登录您的驰速租车账号</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="auth-form anim-fade-up"
        style="--delay: 0.1s"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="auth-btn"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer anim-fade-up" style="--delay: 0.2s">
        <el-link type="primary" @click="$router.push('/')">返回首页</el-link>
        <span class="footer-divider">|</span>
        还没有账号？
        <el-link type="primary" @click="$router.push('/register')">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '../api/auth'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({ username: loginForm.username, password: loginForm.password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    ElMessage.success('登录成功')
    router.push(res.data.user.role === 'ADMIN' ? '/dashboard' : '/')
  } catch {} finally { loading.value = false }
}
</script>

<style scoped>
/* ===== Animations ===== */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes orbDrift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -20px) scale(1.05); }
  50% { transform: translate(-10px, 20px) scale(0.95); }
  75% { transform: translate(-30px, -10px) scale(1.02); }
}

@keyframes particleFloat {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.3; }
  50% { transform: translateY(-40px) scale(1.5); opacity: 0.7; }
}

@keyframes glowPulse {
  0%, 100% { box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.08); }
  50% { box-shadow: 0 0 0 5px rgba(102, 126, 234, 0.15); }
}

.anim-fade-up {
  opacity: 0;
  animation: fadeInUp 0.7s cubic-bezier(0.22, 1, 0.36, 1) var(--delay, 0s) forwards;
}

/* ===== Layout ===== */
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #f0f0ff 0%, #f8f6ff 30%, #fef8ff 60%, #f5f0ff 100%);
  position: relative;
  overflow: hidden;
}

/* ===== Background Decorations ===== */
.auth-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.auth-grid {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(rgba(102,126,234,0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(102,126,234,0.06) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 50% 40% at 50% 50%, black 20%, transparent 70%);
}

.auth-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.45;
  animation: orbDrift 12s ease-in-out infinite;
}

.orb-1 {
  width: 300px; height: 300px;
  top: -8%; right: -5%;
  background: rgba(102, 126, 234, 0.25);
}

.orb-2 {
  width: 200px; height: 200px;
  bottom: -5%; left: -3%;
  background: rgba(118, 75, 162, 0.2);
  animation-delay: -4s;
}

.orb-3 {
  width: 160px; height: 160px;
  top: 50%; left: 40%;
  background: rgba(240, 147, 251, 0.12);
  animation-delay: -8s;
}

.auth-particle {
  position: absolute;
  width: 6px; height: 6px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 0 10px rgba(102, 126, 234, 0.4);
  left: calc(var(--i) * 15% + 10%);
  top: calc(var(--i) * 10% + 5%);
  animation: particleFloat calc(3s + var(--i) * 0.5s) ease-in-out calc(var(--i) * 0.3s) infinite;
}

/* ===== Card ===== */
.auth-card {
  width: 420px;
  padding: 44px 40px;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(102, 126, 234, 0.06), 0 24px 80px rgba(102, 126, 234, 0.08);
  backdrop-filter: blur(24px) saturate(1.4);
  position: relative;
  z-index: 1;
}

.auth-header {
  text-align: center;
  margin-bottom: 36px;
}

.auth-brand {
  margin-bottom: 20px;
}

.brand-icon {
  width: 48px; height: 48px;
  display: inline-flex; align-items: center; justify-content: center;
  background: var(--gradient-main);
  color: #fff;
  font-family: var(--font-display); font-weight: 800; font-size: 22px;
  border-radius: 14px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.35);
  transform: rotate(-3deg);
  transition: transform 0.3s, box-shadow 0.3s;
}

.brand-icon:hover {
  transform: rotate(0deg);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.45);
}

.auth-header h1 {
  font-family: var(--font-cn);
  font-size: 26px; font-weight: 700; color: #1a1a2e;
  margin: 0 0 6px; letter-spacing: -0.01em;
}

.auth-header p {
  font-size: 14px; color: #8a8aaa; margin: 0;
}

/* ===== Form ===== */
.auth-form { margin-top: 8px; }

.auth-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.1) !important;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  padding: 12px 16px;
}

.auth-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.3) !important;
}

.auth-form :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #667eea, 0 0 0 4px rgba(102, 126, 234, 0.1) !important;
}

.auth-btn {
  width: 100%;
  margin-top: 4px;
}

.auth-btn :deep(.el-button) {
  height: 46px;
  font-size: 16px;
  letter-spacing: 0.08em;
}

/* ===== Footer ===== */
.auth-footer {
  text-align: center;
  font-size: 14px;
  color: #8a8aaa;
  margin-top: 24px;
}

.auth-footer :deep(.el-link--primary) { color: #667eea; font-weight: 500; }

.footer-divider {
  margin: 0 8px;
  color: rgba(102, 126, 234, 0.15);
}

@media (max-width: 480px) {
  .auth-card { width: 92%; padding: 36px 24px; }
}
</style>
