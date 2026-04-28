<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-brand" @click="$router.push('/dashboard')">
        <span class="brand-icon">C</span>
        <span class="brand-text">驰速租车</span>
      </div>

      <nav class="sidebar-nav">
        <router-link to="/dashboard" class="nav-item" exact-active-class="active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="3" y="3" width="7" height="7" rx="1"/>
            <rect x="14" y="3" width="7" height="7" rx="1"/>
            <rect x="3" y="14" width="7" height="7" rx="1"/>
            <rect x="14" y="14" width="7" height="7" rx="1"/>
          </svg>
          <span>控制台</span>
        </router-link>
        <router-link to="/dashboard/users" class="nav-item" active-class="active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M23 21v-2a4 4 0 00-3-3.87"/>
            <path d="M16 3.13a4 4 0 010 7.75"/>
          </svg>
          <span>用户管理</span>
        </router-link>
        <router-link to="/dashboard/vehicles" class="nav-item" active-class="active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="1" y="3" width="15" height="11"/>
            <polygon points="16,8 22,14 22,14 16,14"/>
            <circle cx="5.5" cy="18.5" r="2.5"/>
            <circle cx="18.5" cy="18.5" r="2.5"/>
          </svg>
          <span>车辆管理</span>
        </router-link>
        <router-link to="/dashboard/bookings" class="nav-item" active-class="active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="3" y="4" width="18" height="16" rx="2"/>
            <path d="M16 2v4M8 2v4M3 10h18"/>
          </svg>
          <span>预订管理</span>
        </router-link>
        <router-link to="/dashboard/rentals" class="nav-item" active-class="active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 11l3 3L22 4"/>
            <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
          </svg>
          <span>取还车管理</span>
        </router-link>
        <router-link to="/dashboard/accidents" class="nav-item" active-class="active">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <span>事故管理</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <span class="user-avatar">{{ userInitial }}</span>
          <div class="user-meta">
            <span class="user-name">{{ userName }}</span>
            <span class="user-role">{{ userRole === 'ADMIN' ? '管理员' : '用户' }}</span>
          </div>
        </div>
        <button class="logout-btn" @click="handleLogout" title="退出登录">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4"/>
            <polyline points="16,17 21,12 16,7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </button>
      </div>
    </aside>

    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

function readUser() {
  try { return JSON.parse(localStorage.getItem('user') || '{}') } catch { return {} }
}

const user = ref(readUser())
const userName = ref(user.value.realName || user.value.username || '未登录')
const userRole = ref(user.value.role || 'USER')
const userInitial = ref((user.value.realName || user.value.username || '?')[0])

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = {}
  userName.value = '未登录'
  userRole.value = 'USER'
  userInitial.value = '?'
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ===== Sidebar ===== */
.sidebar {
  width: 240px;
  background: #fff;
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 50;
}

.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.sidebar-brand:hover {
  transform: scale(1.02);
}

.brand-icon {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-main);
  color: #fff;
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 16px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
  transform: rotate(-3deg);
}

.brand-text {
  font-family: var(--font-cn);
  font-weight: 700;
  font-size: 17px;
  color: var(--text-primary);
}

/* ===== Nav ===== */
.sidebar-nav {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border-radius: 10px;
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}

.nav-item svg {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-item:hover {
  background: rgba(102, 126, 234, 0.06);
  color: var(--accent);
}

.nav-item.active {
  background: var(--gradient-main);
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

/* ===== Footer ===== */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.user-avatar {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-main);
  color: #fff;
  font-family: var(--font-cn);
  font-weight: 700;
  font-size: 14px;
  border-radius: 8px;
  flex-shrink: 0;
}

.user-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role {
  font-size: 11px;
  color: var(--text-muted);
}

.logout-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  flex-shrink: 0;
}

.logout-btn svg {
  width: 18px;
  height: 18px;
}

.logout-btn:hover {
  border-color: var(--danger);
  color: var(--danger);
  background: rgba(232, 72, 72, 0.06);
}

/* ===== Main ===== */
.main-content {
  margin-left: 240px;
  flex: 1;
  padding: 32px;
  min-height: 100vh;
}

@media (max-width: 900px) {
  .sidebar {
    width: 200px;
  }

  .main-content {
    margin-left: 200px;
    padding: 24px 16px;
  }
}

@media (max-width: 600px) {
  .sidebar {
    width: 60px;
  }

  .brand-text,
  .nav-item span,
  .user-meta,
  .sidebar-brand {
    display: none;
  }

  .sidebar-brand {
    padding: 20px 0;
    justify-content: center;
  }

  .sidebar-nav {
    padding: 8px;
  }

  .nav-item {
    justify-content: center;
    padding: 11px 0;
  }

  .sidebar-footer {
    justify-content: center;
    padding: 12px 8px;
  }

  .main-content {
    margin-left: 60px;
    padding: 16px 12px;
  }
}
</style>
