<template>
  <div class="home-page">
    <!-- Navigation -->
    <AppNav :links="[{to:'#vehicles',label:'车型'},{to:'#features',label:'优势'},{to:'#process',label:'流程'},{to:'#stats',label:'数据'}]" transparent />

    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-gradient"></div>
        <div class="hero-grid"></div>
        <div class="hero-orb hero-orb-1"></div>
        <div class="hero-orb hero-orb-2"></div>
        <div class="hero-orb hero-orb-3"></div>
      </div>
      <div class="hero-content">
        <div class="hero-badge anim-fade-up" style="--delay: 0s">
          <span class="badge-dot"></span>
          智能租车，即刻出发
        </div>
        <h1 class="hero-title">
          <span class="title-line anim-fade-up" style="--delay: 0.1s">驾驭自由</span>
          <span class="title-line gradient-text anim-fade-up" style="--delay: 0.2s">驰骋无限</span>
        </h1>
        <p class="hero-desc anim-fade-up" style="--delay: 0.3s">
          从预订到还车，全流程智能化管理。超时精准计费、里程自动核算、
          信用体系护航——让每一次出行都安心无忧。
        </p>
        <div class="hero-actions anim-fade-up" style="--delay: 0.4s">
          <button class="btn-primary btn-lg" @click="handleBooking">
            立即开始
            <svg class="btn-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
          <button class="btn-glass btn-lg" @click="scrollTo('#process')">
            了解流程
          </button>
        </div>
        <div class="hero-metrics anim-fade-up" style="--delay: 0.5s">
          <div class="metric">
            <span class="metric-value">3</span>
            <span class="metric-label">分钟完成预订</span>
          </div>
          <div class="metric-divider"></div>
          <div class="metric">
            <span class="metric-value">100%</span>
            <span class="metric-label">费用自动计算</span>
          </div>
          <div class="metric-divider"></div>
          <div class="metric">
            <span class="metric-value">24/7</span>
            <span class="metric-label">全天候服务</span>
          </div>
        </div>
      </div>
      <div class="hero-visual anim-fade-up" style="--delay: 0.6s">
        <div class="car-display">
          <div class="car-silhouette">
            <svg viewBox="0 0 800 300" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M120 200 L180 120 L280 80 L520 80 L620 120 L680 200" stroke="url(#carGrad)" stroke-width="2.5" fill="none" opacity="0.8"/>
              <path d="M100 200 L700 200" stroke="url(#carGrad)" stroke-width="2" opacity="0.5"/>
              <circle cx="200" cy="200" r="35" stroke="url(#carGrad)" stroke-width="2" fill="none" opacity="0.6"/>
              <circle cx="600" cy="200" r="35" stroke="url(#carGrad)" stroke-width="2" fill="none" opacity="0.6"/>
              <circle cx="200" cy="200" r="20" stroke="url(#carGrad)" stroke-width="1.5" fill="none" opacity="0.4"/>
              <circle cx="600" cy="200" r="20" stroke="url(#carGrad)" stroke-width="1.5" fill="none" opacity="0.4"/>
              <path d="M250 85 L400 85 L400 130 L240 130 Z" fill="url(#windowGrad)" stroke="url(#carGrad)" stroke-width="1" opacity="0.5"/>
              <path d="M410 85 L530 85 L560 130 L410 130 Z" fill="url(#windowGrad)" stroke="url(#carGrad)" stroke-width="1" opacity="0.5"/>
              <defs>
                <linearGradient id="carGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
                <linearGradient id="windowGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="rgba(102,126,234,0.15)"/>
                  <stop offset="100%" stop-color="rgba(118,75,162,0.15)"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div class="car-particles">
            <span v-for="i in 8" :key="i" class="particle" :style="{ '--delay': i * 0.3 + 's', '--x': (i * 13 % 100) + '%', '--hue': (i * 45) + 'deg' }"></span>
          </div>
        </div>
      </div>
      <div class="scroll-hint" @click="scrollTo('#vehicles')">
        <span class="scroll-line"></span>
        <span class="scroll-text">向下探索</span>
      </div>
    </section>

    <!-- Vehicles Section -->
    <section id="vehicles" class="section vehicles-section">
      <div class="section-inner">
        <div class="section-header scroll-reveal">
          <span class="section-tag">FLEET</span>
          <h2 class="section-title">精选车型</h2>
          <p class="section-desc">从经济实用到豪华享受，满足每一次出行需求</p>
        </div>
        <div class="vehicles-grid">
          <div class="vehicle-card scroll-reveal" v-for="(car, idx) in vehicles" :key="car.id"
               :style="{ '--card-color': car.color, '--card-bg': car.cardBg, '--delay': idx * 0.15 + 's' }"
               @click="$router.push(`/vehicles/${car.id}`)">
            <div class="card-shimmer"></div>
            <div class="card-type">{{ car.typeName }}</div>
            <div class="card-visual">
              <div class="car-icon">
                <svg viewBox="0 0 64 32" fill="none">
                  <path d="M8 22 L14 12 L24 8 L40 8 L50 12 L56 22" :stroke="car.color" stroke-width="1.5" fill="none"/>
                  <line x1="6" y1="22" x2="58" y2="22" :stroke="car.color" stroke-width="1.5"/>
                  <circle cx="16" cy="22" r="4" :stroke="car.color" stroke-width="1.5" fill="none"/>
                  <circle cx="48" cy="22" r="4" :stroke="car.color" stroke-width="1.5" fill="none"/>
                </svg>
              </div>
            </div>
            <h3 class="card-name">{{ car.model }}</h3>
            <p class="card-plate">{{ car.plateNo }}</p>
            <div class="card-specs">
              <div class="spec">
                <span class="spec-value">{{ car.dailyRate }}</span>
                <span class="spec-label">日租金(元)</span>
              </div>
              <div class="spec-divider"></div>
              <div class="spec">
                <span class="spec-value">{{ car.freeMileage }}</span>
                <span class="spec-label">免里程(km)</span>
              </div>
              <div class="spec-divider"></div>
              <div class="spec">
                <span class="spec-value">{{ car.overtimeRate }}</span>
                <span class="spec-label">超时费(元/h)</span>
              </div>
            </div>
            <button class="btn-primary-outline" @click.stop="$router.push(`/vehicles/${car.id}`)">
              立即预订
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section id="features" class="section features-section">
      <div class="section-inner">
        <div class="section-header scroll-reveal">
          <span class="section-tag">WHY US</span>
          <h2 class="section-title">核心优势</h2>
          <p class="section-desc">每一处细节都经过精心设计，只为极致体验</p>
        </div>
        <div class="features-grid">
          <div class="feature-card scroll-reveal" v-for="(feature, idx) in features" :key="feature.title"
               :style="{ '--icon-color': feature.color, '--icon-bg': feature.bg, '--delay': idx * 0.1 + 's' }">
            <div class="feature-icon">
              <span v-html="feature.icon"></span>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-desc">{{ feature.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Process Section -->
    <section id="process" class="section process-section">
      <div class="section-inner">
        <div class="section-header scroll-reveal">
          <span class="section-tag">HOW IT WORKS</span>
          <h2 class="section-title">租车流程</h2>
          <p class="section-desc">四步完成，轻松出行</p>
        </div>
        <div class="process-flow">
          <div class="process-step scroll-reveal" v-for="(step, idx) in processSteps" :key="step.title"
               :style="{ '--step': idx, '--delay': idx * 0.15 + 's' }">
            <div class="step-number">{{ String(idx + 1).padStart(2, '0') }}</div>
            <div class="step-icon" v-html="step.icon"></div>
            <div class="step-content">
              <h3 class="step-title">{{ step.title }}</h3>
              <p class="step-desc">{{ step.desc }}</p>
            </div>
            <div class="step-connector" v-if="idx < processSteps.length - 1">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section id="stats" class="section stats-section">
      <div class="section-inner">
        <div class="stats-display">
          <div class="stat-item scroll-reveal" v-for="(stat, idx) in stats" :key="stat.label"
               :style="{ '--delay': idx * 0.1 + 's' }">
            <span class="stat-value gradient-text">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="section cta-section">
      <div class="section-inner">
        <div class="cta-card scroll-reveal">
          <div class="cta-gradient"></div>
          <div class="cta-particles">
            <span v-for="i in 5" :key="i" class="cta-particle" :style="{ '--i': i }"></span>
          </div>
          <h2 class="cta-title" v-text="isLoggedIn ? '继续探索更多车型' : '准备好出发了吗？'"></h2>
          <p class="cta-desc" v-text="isLoggedIn ? '查看所有可用车型，立即预订心仪车辆' : '注册即享全新体验，智能租车从此刻开始'"></p>
          <div class="cta-actions">
            <button class="btn-primary btn-lg" @click="handleBooking">
              {{ isLoggedIn ? '立即预订' : '创建账号' }}
              <svg class="btn-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <AppFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getVehicles } from '../api/vehicle'
import AppNav from '../components/AppNav.vue'
import AppFooter from '../components/AppFooter.vue'

const router = useRouter()

const vehicles = ref([])
const loadingVehicles = ref(false)
const isLoggedIn = computed(() => !!localStorage.getItem('token'))

function handleBooking() {
  router.push(isLoggedIn.value ? '/fleet' : '/register')
}

async function fetchVehicles() {
  loadingVehicles.value = true
  try {
    const res = await getVehicles({ page: 1, size: 3 })
    vehicles.value = (res.data?.records || []).map(v => ({
      ...v,
      color: v.type === 'luxury' ? '#764ba2' : '#667eea',
      cardBg: v.type === 'luxury'
        ? 'linear-gradient(135deg, rgba(118,75,162,0.08) 0%, rgba(118,75,162,0.02) 100%)'
        : 'linear-gradient(135deg, rgba(102,126,234,0.08) 0%, rgba(102,126,234,0.02) 100%)',
    }))
  } catch (e) { console.error('获取首页车辆失败:', e) }
  loadingVehicles.value = false
  await nextTick()
  initScrollReveal()
}

const features = [
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>',
    title: '超时精准计费',
    desc: '向上取整算法确保公平计费，超5分钟算1小时，透明无争议',
    color: '#667eea',
    bg: 'rgba(102,126,234,0.12)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 10h18M9 4v6"/></svg>',
    title: '时间冲突检测',
    desc: '智能检测车辆时间重叠，含交接时间预留，杜绝双重预订',
    color: '#4ecdc4',
    bg: 'rgba(78,205,196,0.12)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/></svg>',
    title: '费用自动核算',
    desc: '租金、超时费、超里程费全由系统计算，零人工干预，精准可靠',
    color: '#764ba2',
    bg: 'rgba(118,75,162,0.12)',
  },
  {
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>',
    title: '信用体系保障',
    desc: '信用分动态管理，低分限制租车，超时自动扣分，守护用车秩序',
    color: '#f093fb',
    bg: 'rgba(240,147,251,0.12)',
  },
]

const processSteps = [
  {
    title: '在线预订',
    desc: '选择车型、设定时间，系统实时检查车辆可用性与时间冲突',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="2" y="3" width="20" height="18" rx="2"/><path d="M8 7h8M8 11h5"/></svg>',
  },
  {
    title: '到店取车',
    desc: '工作人员确认预订，记录取车时间与里程，手续便捷高效',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/></svg>',
  },
  {
    title: '自由驰骋',
    desc: '享受驾驶乐趣，系统自动监测还车时间与里程使用情况',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 2a15 15 0 014 10 15 15 0 01-4 10M12 2a15 15 0 00-4 10 15 15 0 004 10M2 12h20"/></svg>',
  },
  {
    title: '轻松还车',
    desc: '还车时系统自动计算全部费用——租金、超时、超里程，一笔清算',
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 2v6l3 3"/><circle cx="12" cy="12" r="10"/></svg>',
  },
]

const stats = [
  { value: '2+', label: '车型选择' },
  { value: '100%', label: '费用系统计算' },
  { value: '0', label: '人工核算误差' },
  { value: '60+', label: '信用分门槛保障' },
]

const scrollTo = (selector) => {
  document.querySelector(selector)?.scrollIntoView({ behavior: 'smooth' })
}

const initScrollReveal = () => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('revealed')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.15, rootMargin: '0px 0px -40px 0px' }
  )
  document.querySelectorAll('.scroll-reveal').forEach((el) => observer.observe(el))
}

onMounted(() => {
  initScrollReveal()
  fetchVehicles()
})
</script>

<style scoped>
/* ===== Gradient Text Utility ===== */
.gradient-text {
  background: var(--gradient-main);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
}

/* ===== Animations ===== */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInScale {
  from { opacity: 0; transform: scale(0.92); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes orbDrift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -20px) scale(1.05); }
  50% { transform: translate(-10px, 20px) scale(0.95); }
  75% { transform: translate(-30px, -10px) scale(1.02); }
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

@keyframes scrollLine {
  0%, 100% { opacity: 0.3; transform: scaleY(0.6); }
  50% { opacity: 1; transform: scaleY(1); }
}

@keyframes particleFloat {
  0% { opacity: 0; transform: translateY(0) scale(0); }
  20% { opacity: 0.8; transform: translateY(-20px) scale(1); }
  80% { opacity: 0.6; transform: translateY(-60px) scale(0.8); }
  100% { opacity: 0; transform: translateY(-80px) scale(0); }
}

@keyframes ctaParticle {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0.3; }
  50% { transform: translateY(-30px) scale(1.5); opacity: 0.6; }
}

.anim-fade-up {
  opacity: 0;
  animation: fadeInUp 0.8s cubic-bezier(0.22, 1, 0.36, 1) var(--delay, 0s) forwards;
}

.scroll-reveal {
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.7s cubic-bezier(0.22, 1, 0.36, 1);
  transition-delay: var(--delay, 0s);
}

.scroll-reveal.revealed {
  opacity: 1;
  transform: translateY(0);
}

/* ===== Buttons (scoped to this page) ===== */
.btn-primary {
  padding: 9px 24px;
  background: var(--gradient-main);
  border: none;
  color: #fff;
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, transparent 50%);
  opacity: 0;
  transition: opacity 0.3s;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
}

.btn-primary:hover::before {
  opacity: 1;
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-lg {
  padding: 14px 36px;
  font-size: 16px;
  border-radius: 12px;
}

.btn-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s;
}

.btn-primary:hover .btn-arrow {
  transform: translateX(4px);
}

.btn-glass {
  padding: 8px 24px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(102, 126, 234, 0.15);
  color: #1a1a2e;
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  backdrop-filter: blur(8px);
}

.btn-glass:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.1);
}

.btn-lg.btn-glass {
  padding: 14px 36px;
  font-size: 16px;
  border-radius: 12px;
}

.btn-primary-outline {
  width: 100%;
  padding: 12px 20px;
  background: transparent;
  border: 1.5px solid rgba(102, 126, 234, 0.3);
  color: var(--purple-start);
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  margin-top: 20px;
  position: relative;
  overflow: hidden;
}

.btn-primary-outline::before {
  content: '';
  position: absolute;
  inset: 0;
  background: var(--gradient-soft);
  opacity: 0;
  transition: opacity 0.3s;
}

.btn-primary-outline:hover {
  border-color: var(--purple-start);
  transform: translateY(-1px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.btn-primary-outline:hover::before {
  opacity: 1;
}

/* ===== Hero Section ===== */
.hero {
  min-height: 100vh;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  padding: 120px 40px 80px;
  background: linear-gradient(160deg, #f0f0ff 0%, #f8f6ff 30%, #fef8ff 60%, #f5f0ff 100%);
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 80% 50% at 70% 40%, rgba(102,126,234,0.12) 0%, transparent 60%),
    radial-gradient(ellipse 60% 40% at 30% 60%, rgba(118,75,162,0.08) 0%, transparent 60%);
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(102,126,234,0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(102,126,234,0.06) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 60% 50% at 50% 50%, black 30%, transparent 70%);
}

.hero-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.5;
  animation: orbDrift 12s ease-in-out infinite;
}

.hero-orb-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  right: 15%;
  background: rgba(102, 126, 234, 0.25);
  animation-delay: 0s;
}

.hero-orb-2 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  left: 10%;
  background: rgba(118, 75, 162, 0.2);
  animation-delay: -4s;
}

.hero-orb-3 {
  width: 150px;
  height: 150px;
  top: 40%;
  left: 40%;
  background: rgba(240, 147, 251, 0.15);
  animation-delay: -8s;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  width: 100%;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 18px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(102, 126, 234, 0.15);
  border-radius: 100px;
  font-size: 13px;
  font-weight: 500;
  color: #5a5a7a;
  margin-bottom: 32px;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.06);
}

.badge-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--gradient-main);
  animation: pulse 2s ease-in-out infinite;
  box-shadow: 0 0 8px rgba(102, 126, 234, 0.4);
}

.hero-title {
  font-family: var(--font-cn);
  font-weight: 900;
  font-size: clamp(48px, 8vw, 96px);
  line-height: 1.1;
  letter-spacing: -0.02em;
  margin-bottom: 24px;
  color: #1a1a2e;
}

.title-line {
  display: block;
}

.title-line.gradient-text {
  background: var(--gradient-main);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 18px;
  line-height: 1.7;
  color: #6a6a8a;
  max-width: 540px;
  margin-bottom: 40px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 64px;
}

.hero-metrics {
  display: flex;
  align-items: center;
  gap: 24px;
}

.metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-value {
  font-family: var(--font-number);
  font-size: 32px;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: var(--gradient-main);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.metric-label {
  font-size: 13px;
  color: #8a8aaa;
}

.metric-divider {
  width: 1px;
  height: 40px;
  background: linear-gradient(to bottom, rgba(102,126,234,0.2), rgba(118,75,162,0.2));
}

/* ===== Hero Visual ===== */
.hero-visual {
  position: absolute;
  right: 5%;
  top: 50%;
  transform: translateY(-50%);
  width: 500px;
  height: 300px;
  z-index: 1;
}

.car-display {
  animation: float 6s ease-in-out infinite;
}

.car-silhouette svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 8px 32px rgba(102, 126, 234, 0.2));
}

.car-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  top: 50%;
  left: var(--x);
  opacity: 0;
  animation: particleFloat 3s ease-in-out var(--delay) infinite;
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 0 8px rgba(102, 126, 234, 0.5);
}

.scroll-hint {
  position: absolute;
  bottom: 32px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  z-index: 2;
}

.scroll-line {
  width: 2px;
  height: 40px;
  background: linear-gradient(to bottom, var(--purple-start), var(--purple-end));
  border-radius: 1px;
  animation: scrollLine 2s ease-in-out infinite;
}

.scroll-text {
  font-size: 11px;
  color: #8a8aaa;
  letter-spacing: 0.15em;
  text-transform: uppercase;
}

/* ===== Sections Common ===== */
.section {
  padding: 120px 40px;
  position: relative;
}

.section-inner {
  max-width: 1200px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 64px;
}

.section-tag {
  font-family: var(--font-display);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.2em;
  background: var(--gradient-main);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-transform: uppercase;
}

.section-title {
  font-family: var(--font-cn);
  font-size: clamp(32px, 4vw, 48px);
  font-weight: 700;
  margin-top: 12px;
  line-height: 1.2;
  color: #1a1a2e;
}

.section-desc {
  font-size: 16px;
  color: #6a6a8a;
  margin-top: 12px;
  line-height: 1.6;
}

/* ===== Vehicles ===== */
.vehicles-section {
  background: linear-gradient(180deg, #faf9ff 0%, #f5f3ff 100%);
}

.vehicles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 32px;
}

.vehicle-card {
  position: relative;
  padding: 36px;
  background: #ffffff;
  border: 1px solid rgba(102, 126, 234, 0.08);
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.vehicle-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--card-bg, var(--gradient-main));
  opacity: 0;
  transition: opacity 0.3s;
}

.vehicle-card:hover {
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-6px);
  box-shadow: 0 16px 48px rgba(102, 126, 234, 0.1), 0 4px 16px rgba(0, 0, 0, 0.04);
}

.vehicle-card:hover::before {
  opacity: 1;
}

.card-shimmer {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    105deg,
    transparent 40%,
    rgba(102, 126, 234, 0.04) 45%,
    rgba(118, 75, 162, 0.04) 50%,
    transparent 55%
  );
  background-size: 200% 100%;
  animation: shimmer 4s ease-in-out infinite;
  pointer-events: none;
}

.card-type {
  font-family: var(--font-display);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.15em;
  text-transform: uppercase;
  color: var(--card-color, var(--purple-start));
  margin-bottom: 20px;
}

.card-visual {
  margin-bottom: 20px;
}

.car-icon svg {
  width: 80px;
  height: 40px;
}

.card-name {
  font-family: var(--font-cn);
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #1a1a2e;
}

.card-desc {
  font-size: 14px;
  color: #6a6a8a;
  line-height: 1.5;
  margin-bottom: 24px;
}

.card-plate {
  font-size: 13px;
  color: #8a8aaa;
  margin-bottom: 20px;
}

.card-specs {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-top: 1px solid rgba(102, 126, 234, 0.06);
}

.spec {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.spec-value {
  font-family: var(--font-number);
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: #1a1a2e;
}

.spec-label {
  font-size: 11px;
  color: #8a8aaa;
}

.spec-divider {
  width: 1px;
  height: 32px;
  background: rgba(102, 126, 234, 0.1);
}

/* ===== Features ===== */
.features-section {
  background: #ffffff;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.feature-card {
  padding: 32px;
  background: #faf9ff;
  border: 1px solid rgba(102, 126, 234, 0.06);
  border-radius: 16px;
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  position: relative;
  overflow: hidden;
}

.feature-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--icon-color, var(--purple-start)), transparent);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.feature-card:hover {
  border-color: rgba(102, 126, 234, 0.15);
  transform: translateY(-4px);
  background: #fff;
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.08);
}

.feature-card:hover::after {
  transform: scaleX(1);
}

.feature-icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  color: var(--icon-color, var(--purple-start));
  background: var(--icon-bg, rgba(102,126,234,0.1));
  border-radius: 14px;
  transition: all 0.3s;
}

.feature-card:hover .feature-icon {
  transform: scale(1.08) rotate(-3deg);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.feature-icon :deep(svg) {
  width: 26px;
  height: 26px;
}

.feature-title {
  font-family: var(--font-cn);
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1a1a2e;
}

.feature-desc {
  font-size: 14px;
  color: #6a6a8a;
  line-height: 1.6;
}

/* ===== Process ===== */
.process-section {
  background: linear-gradient(180deg, #f5f3ff 0%, #faf9ff 100%);
}

.process-flow {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  position: relative;
}

.process-step {
  position: relative;
  padding: 36px 24px;
  text-align: center;
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.06);
  transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
}

.process-step:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.08);
  border-color: rgba(102, 126, 234, 0.15);
}

.step-number {
  font-family: var(--font-number);
  font-size: 48px;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, rgba(102,126,234,0.15) 0%, rgba(118,75,162,0.15) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.process-step:hover .step-number {
  background: var(--gradient-main);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.step-icon {
  width: 40px;
  height: 40px;
  margin: 0 auto 16px;
  color: var(--purple-start);
  display: flex;
  align-items: center;
  justify-content: center;
}

.step-icon :deep(svg) {
  width: 24px;
  height: 24px;
}

.step-content {
  position: relative;
}

.step-title {
  font-family: var(--font-cn);
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1a1a2e;
}

.step-desc {
  font-size: 14px;
  color: #6a6a8a;
  line-height: 1.6;
}

.step-connector {
  position: absolute;
  top: 50%;
  right: -14px;
  width: 24px;
  height: 24px;
  color: rgba(102, 126, 234, 0.25);
  z-index: 1;
}

.step-connector svg {
  width: 16px;
  height: 16px;
}

/* ===== Stats ===== */
.stats-section {
  padding: 80px 40px;
  background: var(--gradient-main);
  position: relative;
  overflow: hidden;
}

.stats-section::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 50%, rgba(255,255,255,0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(255,255,255,0.08) 0%, transparent 50%);
}

.stats-display {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
  text-align: center;
  position: relative;
  z-index: 1;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-value {
  font-family: var(--font-number);
  font-size: clamp(36px, 5vw, 56px);
  font-weight: 800;
  letter-spacing: -0.02em;
  color: #fff;
  line-height: 1;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.stat-value.gradient-text {
  background: none;
  -webkit-text-fill-color: #fff;
  color: #fff;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  font-weight: 500;
}

/* ===== CTA ===== */
.cta-section {
  padding: 80px 40px;
  background: #ffffff;
}

.cta-card {
  position: relative;
  padding: 80px;
  background: linear-gradient(135deg, #f8f6ff 0%, #fef8ff 50%, #f5f0ff 100%);
  border: 1px solid rgba(102, 126, 234, 0.1);
  border-radius: 24px;
  text-align: center;
  overflow: hidden;
}

.cta-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 20% 50%, rgba(102,126,234,0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(118,75,162,0.1) 0%, transparent 50%);
}

.cta-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.cta-particle {
  position: absolute;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.3);
  left: calc(var(--i) * 20%);
  top: calc(var(--i) * 15 + 20%);
  animation: ctaParticle 4s ease-in-out calc(var(--i) * 0.5s) infinite;
}

.cta-title {
  position: relative;
  font-family: var(--font-cn);
  font-size: clamp(28px, 4vw, 42px);
  font-weight: 700;
  margin-bottom: 12px;
  color: #1a1a2e;
}

.cta-desc {
  position: relative;
  font-size: 16px;
  color: #6a6a8a;
  margin-bottom: 32px;
}

.cta-actions {
  position: relative;
}


/* ===== Responsive ===== */
@media (max-width: 900px) {
  .hero-visual { display: none; }

  .process-flow {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }

  .step-connector { display: none; }

  .stats-display {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }

  .cta-card { padding: 48px 24px; }
}

@media (max-width: 600px) {
  .hero { padding: 100px 20px 60px; }

  .section { padding: 80px 20px; }

  .hero-actions { flex-direction: column; }

  .hero-metrics { flex-direction: column; align-items: flex-start; gap: 16px; }

  .metric-divider { display: none; }

  .process-flow { grid-template-columns: 1fr; }

  .stats-display { grid-template-columns: 1fr; gap: 32px; }

  .vehicles-grid { grid-template-columns: 1fr; }
}
</style>
