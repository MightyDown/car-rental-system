<template>
  <div class="dashboard">
    <div class="page-header scroll-reveal">
      <div>
        <h1 class="page-title">控制台</h1>
        <p class="page-desc">欢迎回来，<span class="highlight-name">{{ userName }}</span></p>
      </div>
    </div>

    <div class="stats-grid">
      <div class="stat-card scroll-reveal" v-for="(stat, idx) in stats" :key="stat.label"
           :style="{ '--delay': idx * 0.1 + 's' }">
        <div class="stat-icon" :style="{ '--icon-color': stat.color, '--icon-bg': stat.bg }">
          <span v-html="stat.icon"></span>
        </div>
        <div class="stat-info">
          <span class="stat-value">
            <AnimatedNumber v-if="typeof stat.value === 'number'" :target="stat.value" />
            <template v-else>{{ stat.value }}</template>
          </span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <div class="quick-links scroll-reveal" style="--delay: 0.6s">
      <h2 class="section-title">快捷操作</h2>
      <div class="links-grid">
        <router-link to="/dashboard/users" class="link-card">
          <div class="link-icon" style="--icon-color: #667eea; --icon-bg: rgba(102,126,234,0.12)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 00-3-3.87"/>
              <path d="M16 3.13a4 4 0 010 7.75"/>
            </svg>
          </div>
          <div class="link-content">
            <span class="link-title">用户管理</span>
            <span class="link-desc">管理用户账号、角色与状态</span>
          </div>
          <svg class="link-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
        <router-link to="/dashboard/vehicles" class="link-card">
          <div class="link-icon" style="--icon-color: #764ba2; --icon-bg: rgba(118,75,162,0.12)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="1" y="3" width="15" height="11"/>
              <polygon points="16,8 22,14 22,14 16,14"/>
              <circle cx="5.5" cy="18.5" r="2.5"/>
              <circle cx="18.5" cy="18.5" r="2.5"/>
            </svg>
          </div>
          <div class="link-content">
            <span class="link-title">车辆管理</span>
            <span class="link-desc">管理车辆信息、状态与费率</span>
          </div>
          <svg class="link-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
        <router-link to="/dashboard/bookings" class="link-card">
          <div class="link-icon" style="--icon-color: #4ecdc4; --icon-bg: rgba(78,205,196,0.12)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="4" width="18" height="16" rx="2"/>
              <path d="M16 2v4M8 2v4M3 10h18"/>
            </svg>
          </div>
          <div class="link-content">
            <span class="link-title">预订管理</span>
            <span class="link-desc">确认或取消用户预订申请</span>
          </div>
          <svg class="link-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
        <router-link to="/dashboard/rentals" class="link-card">
          <div class="link-icon" style="--icon-color: #f093fb; --icon-bg: rgba(240,147,251,0.12)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M9 11l3 3L22 4"/>
              <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
            </svg>
          </div>
          <div class="link-content">
            <span class="link-title">取还车管理</span>
            <span class="link-desc">处理车辆取还记录与费用</span>
          </div>
          <svg class="link-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { getUsers } from '../../api/user'
import AnimatedNumber from '../../components/AnimatedNumber.vue'

const user = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}') } catch { return {} }
})
const userName = computed(() => user.value.realName || user.value.username || '')

const totalUsers = ref(0)

onMounted(async () => {
  try {
    const res = await getUsers({ page: 1, size: 1 })
    totalUsers.value = res.data?.total || 0
  } catch {}
  await nextTick()
  initScrollReveal()
})

const stats = [
  {
    label: '用户总数',
    value: totalUsers,
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>',
    color: '#667eea',
    bg: 'rgba(102,126,234,0.12)',
  },
  {
    label: '信用体系',
    value: '60+',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>',
    color: '#4ecdc4',
    bg: 'rgba(78,205,196,0.12)',
  },
  {
    label: '系统状态',
    value: '运行中',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>',
    color: '#764ba2',
    bg: 'rgba(118,75,162,0.12)',
  },
  {
    label: '管理员',
    value: '在线',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 15a3 3 0 100-6 3 3 0 000 6z"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>',
    color: '#f093fb',
    bg: 'rgba(240,147,251,0.12)',
  },
]

function initScrollReveal() {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.15, rootMargin: '0px 0px -40px 0px' }
  )
  document.querySelectorAll('.scroll-reveal').forEach(el => observer.observe(el))
}
</script>

<style scoped>
.dashboard { max-width: 1200px; }

.page-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 32px;
}

.page-title {
  font-family: var(--font-cn); font-size: 28px; font-weight: 700;
  color: var(--text-primary); margin-bottom: 4px;
}

.page-desc { font-size: 14px; color: var(--text-secondary); }

.highlight-name {
  background: var(--gradient-main);
  -webkit-background-clip: text; background-clip: text;
  -webkit-text-fill-color: transparent; color: transparent;
  font-weight: 600;
}

/* ===== Stats ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  padding: 24px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  display: flex; align-items: center; gap: 16px;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
  position: relative; overflow: hidden;
}

.stat-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: linear-gradient(90deg, var(--icon-color, #667eea), transparent);
  opacity: 0; transition: opacity 0.3s;
}

.stat-card:hover {
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.08);
}

.stat-card:hover::before { opacity: 1; }

.stat-icon {
  width: 48px; height: 48px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 12px;
  color: var(--icon-color); background: var(--icon-bg); flex-shrink: 0;
  transition: transform 0.3s;
}

.stat-card:hover .stat-icon { transform: scale(1.08) rotate(-3deg); }

.stat-icon :deep(svg) { width: 24px; height: 24px; }

.stat-info { display: flex; flex-direction: column; gap: 2px; }

.stat-value {
  font-family: var(--font-number); font-size: 24px; font-weight: 700;
  color: var(--text-primary);
}

.stat-label { font-size: 13px; color: var(--text-muted); }

/* ===== Quick Links ===== */
.section-title {
  font-family: var(--font-cn); font-size: 20px; font-weight: 600;
  color: var(--text-primary); margin-bottom: 20px;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.link-card {
  display: flex; align-items: center; gap: 16px;
  padding: 20px 24px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 14px;
  text-decoration: none;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
  position: relative; overflow: hidden;
}

.link-card::after {
  content: ''; position: absolute; inset: 0;
  background: linear-gradient(105deg, transparent 40%, rgba(102,126,234,0.02) 45%, rgba(118,75,162,0.02) 50%, transparent 55%);
  background-size: 200% 100%;
  animation: shimmer 4s ease-in-out infinite;
  pointer-events: none;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.link-card:hover {
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(102, 126, 234, 0.08);
}

.link-icon {
  width: 44px; height: 44px;
  display: flex; align-items: center; justify-content: center;
  border-radius: 12px;
  color: var(--icon-color); background: var(--icon-bg); flex-shrink: 0;
  transition: transform 0.3s;
}

.link-card:hover .link-icon { transform: scale(1.06) rotate(-2deg); }

.link-icon svg { width: 22px; height: 22px; }

.link-content { flex: 1; display: flex; flex-direction: column; gap: 2px; min-width: 0; }

.link-title {
  font-family: var(--font-cn); font-size: 15px; font-weight: 600;
  color: var(--text-primary);
}

.link-desc { font-size: 13px; color: var(--text-muted); }

.link-arrow {
  width: 18px; height: 18px; color: var(--text-muted); flex-shrink: 0;
  transition: transform 0.3s;
}

.link-card:hover .link-arrow {
  transform: translateX(4px); color: var(--accent);
}

/* ===== Scroll Reveal ===== */
.scroll-reveal {
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.7s cubic-bezier(0.22, 1, 0.36, 1);
  transition-delay: var(--delay, 0s);
}

.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }
</style>
