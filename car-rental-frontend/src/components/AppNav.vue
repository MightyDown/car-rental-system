<template>
  <nav class="app-nav" :class="{ scrolled: navScrolled, transparent }">
    <div class="nav-inner">
      <div class="nav-brand" @click="$router.push('/')">
        <span class="brand-icon">C</span>
        <span class="brand-text">驰速租车</span>
      </div>
      <div class="nav-links">
        <slot name="links">
          <a v-for="link in links" :key="link.to" :href="link.to" class="nav-link" @click.prevent="handleNav(link.to)">
            {{ link.label }}
          </a>
        </slot>
      </div>
      <div class="nav-actions">
        <slot name="actions">
          <template v-if="isLoggedIn">
            <button class="btn-ghost" @click="$router.push('/my-bookings')">我的预订</button>
            <button class="btn-ghost" @click="$router.push('/profile')">个人中心</button>
            <span class="nav-user">{{ userName }}</span>
            <button class="btn-ghost" @click="handleLogout">退出</button>
          </template>
          <template v-else>
            <button class="btn-ghost" @click="$router.push('/login')">登录</button>
            <button class="btn-primary" @click="$router.push('/register')">免费注册</button>
          </template>
        </slot>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  links: { type: Array, default: () => [] },
  transparent: { type: Boolean, default: true },
})

const router = useRouter()
const navScrolled = ref(false)

function readLoginState() {
  const token = !!localStorage.getItem('token')
  let user = {}
  try { user = JSON.parse(localStorage.getItem('user') || '{}') } catch {}
  return { token, name: user.realName || user.username || '' }
}

const login = ref(readLoginState())
const isLoggedIn = ref(login.value.token)
const userName = ref(login.value.name)

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  isLoggedIn.value = false
  userName.value = ''
  router.push('/')
}

function handleNav(to) {
  const id = to.replace('#', '')
  document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' })
}

const handleScroll = () => { navScrolled.value = window.scrollY > 50 }

onMounted(() => window.addEventListener('scroll', handleScroll))
onUnmounted(() => window.removeEventListener('scroll', handleScroll))
</script>

<style scoped>
.app-nav {
  position: fixed;
  top: 0; left: 0; right: 0; z-index: 100;
  padding: 20px 0;
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.app-nav.scrolled {
  padding: 12px 0;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(24px) saturate(1.4);
  box-shadow: 0 1px 3px rgba(102, 126, 234, 0.08), 0 8px 32px rgba(0, 0, 0, 0.06);
}

.app-nav:not(.transparent) {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(24px) saturate(1.4);
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
}

.nav-inner {
  max-width: 1200px; margin: 0 auto; padding: 0 40px;
  display: flex; align-items: center; justify-content: space-between;
}

.nav-brand {
  display: flex; align-items: center; gap: 10px; cursor: pointer;
  transition: transform 0.3s;
}
.nav-brand:hover { transform: scale(1.03); }

.brand-icon {
  width: 38px; height: 38px;
  display: flex; align-items: center; justify-content: center;
  background: var(--gradient-main); color: #fff;
  font-family: var(--font-display); font-weight: 800; font-size: 18px;
  border-radius: 10px; box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  transform: rotate(-3deg); transition: transform 0.3s, box-shadow 0.3s;
}
.nav-brand:hover .brand-icon {
  transform: rotate(0deg);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
}

.brand-text {
  font-family: var(--font-cn); font-weight: 700; font-size: 18px; color: #1a1a2e;
  letter-spacing: 0.02em;
}

.nav-links { display: flex; gap: 32px; }

.nav-link {
  font-family: var(--font-body); font-size: 14px; font-weight: 500;
  color: #5a5a7a; text-decoration: none;
  transition: all 0.3s; letter-spacing: 0.02em;
  position: relative;
}
.nav-link::after {
  content: ''; position: absolute; bottom: -4px; left: 0;
  width: 0; height: 2px;
  background: var(--gradient-main); border-radius: 1px;
  transition: width 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}
.nav-link:hover { color: #1a1a2e; }
.nav-link:hover::after { width: 100%; }

.nav-actions { display: flex; gap: 12px; align-items: center; }
.nav-user { font-family: var(--font-cn); font-size: 14px; font-weight: 600; color: #1a1a2e; }

.btn-ghost {
  padding: 8px 20px; background: transparent; border: none;
  color: #5a5a7a; font-family: var(--font-cn); font-size: 14px; font-weight: 500;
  cursor: pointer; border-radius: 8px; transition: all 0.3s;
}
.btn-ghost:hover { color: var(--purple-start); background: rgba(102, 126, 234, 0.06); }

.btn-primary {
  padding: 9px 24px; background: var(--gradient-main); border: none; color: #fff;
  font-family: var(--font-cn); font-size: 14px; font-weight: 600; border-radius: 10px;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  display: inline-flex; align-items: center; gap: 8px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
  position: relative; overflow: hidden;
}
.btn-primary::before {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, transparent 50%);
  opacity: 0; transition: opacity 0.3s;
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35); }
.btn-primary:hover::before { opacity: 1; }

@media (max-width: 600px) {
  .nav-links { display: none; }
  .nav-inner { padding: 0 20px; }
}
</style>
