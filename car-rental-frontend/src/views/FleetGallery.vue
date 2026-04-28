<template>
  <div class="fleet-page">
    <!-- Navigation -->
    <AppNav :links="[{to:'#fleet-grid',label:'车型'}]" transparent />

    <!-- Hero -->
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
          {{ vehicles.length }} 款车型可选
        </div>
        <h1 class="hero-title anim-fade-up" style="--delay: 0.1s">
          探索我们的<span class="gradient-text">车队</span>
        </h1>
        <p class="hero-desc anim-fade-up" style="--delay: 0.2s">
          从经济实用到豪华尊享，每一辆车都经过精心维护，随时待命出发
        </p>
      </div>
      <div class="scroll-hint" @click="scrollTo('fleet-grid')">
        <span class="scroll-line"></span>
        <span class="scroll-text">向下探索</span>
      </div>
    </section>

    <!-- Fleet Grid -->
    <section id="fleet-grid" class="fleet-section">
      <div class="section-inner">
        <div class="section-header scroll-reveal">
          <span class="section-tag">FLEET</span>
          <h2 class="section-title">全部车型</h2>
        </div>

        <div class="filter-tabs scroll-reveal" style="--delay: 0.1s">
          <button class="filter-tab" :class="{ active: filters.type === '' }" @click="setType('')">全部</button>
          <button class="filter-tab" :class="{ active: filters.type === 'economy' }" @click="setType('economy')">经济型</button>
          <button class="filter-tab" :class="{ active: filters.type === 'luxury' }" @click="setType('luxury')">豪华型</button>
        </div>

        <!-- Loading -->
        <LoadingSkeleton v-if="loading" type="card" :count="3" />

        <!-- Vehicle Cards -->
        <div v-else-if="vehicles.length > 0" class="vehicles-grid">
          <div class="vehicle-card scroll-reveal" v-for="(v, idx) in vehicles" :key="v.id"
               :style="{ '--card-color': v.type === 'luxury' ? '#764ba2' : '#667eea', '--delay': idx * 0.12 + 's' }"
               @click="$router.push(`/vehicles/${v.id}`)">
            <div class="card-shimmer"></div>
            <div class="card-type" :class="'type-' + v.type">{{ v.typeName }}</div>
            <div class="card-visual">
              <!-- Economy car SVG -->
              <svg v-if="v.type === 'economy'" viewBox="0 0 200 100" fill="none" class="car-svg">
                <defs>
                  <linearGradient :id="'grad-e-' + v.id" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stop-color="rgba(102,126,234,0.12)"/>
                    <stop offset="100%" stop-color="rgba(102,126,234,0.04)"/>
                  </linearGradient>
                </defs>
                <!-- body -->
                <path d="M30 62 L38 42 L68 34 L132 34 L162 42 L170 62" stroke="currentColor" stroke-width="1.8" fill="none"/>
                <!-- roof -->
                <path d="M62 34 L62 22 L100 18 L138 22 L138 34" stroke="currentColor" stroke-width="1.8" fill="none"/>
                <!-- windows -->
                <path d="M66 33 L66 25 L98 21 L98 33" stroke="currentColor" stroke-width="1" fill="url(#grad-e-${v.id})" opacity="0.6"/>
                <path d="M102 33 L102 23 L134 25 L134 33" stroke="currentColor" stroke-width="1" fill="url(#grad-e-${v.id})" opacity="0.6"/>
                <!-- bottom line -->
                <line x1="20" y1="62" x2="180" y2="62" stroke="currentColor" stroke-width="1.5" opacity="0.6"/>
                <!-- wheels -->
                <circle cx="56" cy="62" r="14" stroke="currentColor" stroke-width="1.8" fill="none"/>
                <circle cx="56" cy="62" r="7" stroke="currentColor" stroke-width="1.2" fill="none"/>
                <circle cx="144" cy="62" r="14" stroke="currentColor" stroke-width="1.8" fill="none"/>
                <circle cx="144" cy="62" r="7" stroke="currentColor" stroke-width="1.2" fill="none"/>
                <!-- details -->
                <line x1="52" y1="22" x2="52" y2="28" stroke="currentColor" stroke-width="1" opacity="0.3"/>
                <circle cx="28" cy="52" r="4" stroke="currentColor" stroke-width="1" opacity="0.4"/>
              </svg>
              <!-- Luxury car SVG -->
              <svg v-else viewBox="0 0 200 100" fill="none" class="car-svg">
                <defs>
                  <linearGradient :id="'grad-l-' + v.id" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stop-color="rgba(118,75,162,0.12)"/>
                    <stop offset="100%" stop-color="rgba(118,75,162,0.04)"/>
                  </linearGradient>
                </defs>
                <!-- body - longer/lower -->
                <path d="M22 58 L32 38 L70 30 L130 30 L168 38 L178 58" stroke="currentColor" stroke-width="2" fill="none"/>
                <!-- roof - sleeker -->
                <path d="M64 30 L64 18 L100 14 L136 18 L136 30" stroke="currentColor" stroke-width="2" fill="none"/>
                <!-- windows -->
                <path d="M68 29 L68 21 L98 17 L98 29" stroke="currentColor" stroke-width="1" :fill="'url(#grad-l-' + v.id + ')'" opacity="0.6"/>
                <path d="M102 29 L102 19 L132 21 L132 29" stroke="currentColor" stroke-width="1" :fill="'url(#grad-l-' + v.id + ')'" opacity="0.6"/>
                <!-- bottom -->
                <line x1="14" y1="58" x2="186" y2="58" stroke="currentColor" stroke-width="1.8" opacity="0.6"/>
                <!-- wheels -->
                <circle cx="52" cy="58" r="15" stroke="currentColor" stroke-width="2" fill="none"/>
                <circle cx="52" cy="58" r="8" stroke="currentColor" stroke-width="1.2" fill="none"/>
                <circle cx="52" cy="58" r="3" stroke="currentColor" stroke-width="1" fill="none" opacity="0.5"/>
                <circle cx="148" cy="58" r="15" stroke="currentColor" stroke-width="2" fill="none"/>
                <circle cx="148" cy="58" r="8" stroke="currentColor" stroke-width="1.2" fill="none"/>
                <circle cx="148" cy="58" r="3" stroke="currentColor" stroke-width="1" fill="none" opacity="0.5"/>
                <!-- grille -->
                <line x1="178" y1="42" x2="178" y2="54" stroke="currentColor" stroke-width="1.5" opacity="0.4"/>
                <line x1="174" y1="44" x2="174" y2="52" stroke="currentColor" stroke-width="1" opacity="0.3"/>
              </svg>
            </div>
            <h3 class="card-name">{{ v.model }}</h3>
            <p class="card-plate">{{ v.plateNo }}</p>
            <div class="card-specs">
              <div class="spec">
                <span class="spec-value">{{ v.dailyRate }}</span>
                <span class="spec-label">日租金(元)</span>
              </div>
              <div class="spec-divider"></div>
              <div class="spec">
                <span class="spec-value">{{ v.freeMileage }}</span>
                <span class="spec-label">免里程(km)</span>
              </div>
              <div class="spec-divider"></div>
              <div class="spec">
                <span class="spec-value">{{ v.overtimeRate }}</span>
                <span class="spec-label">超时费(元/h)</span>
              </div>
            </div>
            <button class="btn-primary-outline" @click.stop="$router.push(`/vehicles/${v.id}`)">
              查看详情
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
                <path d="M5 12h14M12 5l7 7-7 7"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- Empty -->
        <EmptyState v-else title="暂无可用车型" description="请稍后再来查看">
          <template #icon>
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="1" y="3" width="15" height="11"/><polygon points="16,8 22,14 22,14 16,14"/>
              <circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/>
            </svg>
          </template>
        </EmptyState>
      </div>
    </section>

    <!-- CTA -->
    <section class="cta-section">
      <div class="section-inner">
        <div class="cta-card scroll-reveal">
          <h2 class="cta-title">找到心仪的座驾了吗？</h2>
          <p class="cta-desc">选择车型，设定时间，即刻开启旅程</p>
          <button class="btn-primary btn-lg" @click="scrollTo('fleet-grid')">
            开始选择
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
        </div>
      </div>
    </section>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getVehicles } from '../api/vehicle'
import AppNav from '../components/AppNav.vue'
import AppFooter from '../components/AppFooter.vue'
import LoadingSkeleton from '../components/LoadingSkeleton.vue'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const vehicles = ref([])
const filters = reactive({ type: '' })

function setType(t) {
  filters.type = t
  fetchVehicles()
}

async function fetchVehicles() {
  loading.value = true
  try {
    const params = { page: 1, size: 50 }
    if (filters.type) params.type = filters.type
    const res = await getVehicles(params)
    vehicles.value = res.data?.records || []
  } catch (e) {
    console.error('获取车辆列表失败:', e)
  }
  loading.value = false
  await nextTick()
  initScrollReveal()
}

const scrollTo = (id) => { document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' }) }

const initScrollReveal = () => {
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

onMounted(() => {
  fetchVehicles()
  initScrollReveal()
})
</script>

<style scoped>
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
.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }

/* ===== Buttons ===== */
.btn-primary {
  padding: 9px 24px; background: var(--gradient-main); border: none; color: #fff;
  font-family: var(--font-cn); font-size: 14px; font-weight: 600; border-radius: 10px;
  cursor: pointer; transition: all 0.3s cubic-bezier(0.22,1,0.36,1);
  display: inline-flex; align-items: center; gap: 8px;
  box-shadow: 0 4px 16px rgba(102,126,234,0.25);
}
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 32px rgba(102,126,234,0.35); }
.btn-lg { padding: 14px 36px; font-size: 16px; border-radius: 12px; }

/* ===== Hero ===== */
.hero {
  min-height: 80vh; display: flex; align-items: center; position: relative;
  overflow: hidden; padding: 160px 40px 100px;
  background: linear-gradient(160deg, #f0f0ff 0%, #f8f6ff 30%, #fef8ff 60%, #f5f0ff 100%);
}
.hero-bg { position: absolute; inset: 0; z-index: 0; }
.hero-gradient {
  position: absolute; inset: 0;
  background: radial-gradient(ellipse 70% 40% at 60% 35%, rgba(102,126,234,0.14) 0%, transparent 60%),
              radial-gradient(ellipse 50% 35% at 35% 55%, rgba(118,75,162,0.08) 0%, transparent 60%);
}
.hero-grid {
  position: absolute; inset: 0;
  background-image: linear-gradient(rgba(102,126,234,0.05) 1px, transparent 1px),
                    linear-gradient(90deg, rgba(102,126,234,0.05) 1px, transparent 1px);
  background-size: 80px 80px;
  mask-image: radial-gradient(ellipse 60% 50% at 50% 50%, black 30%, transparent 70%);
}
.hero-orb {
  position: absolute; border-radius: 50%; filter: blur(60px); opacity: 0.5;
  animation: orbDrift 12s ease-in-out infinite;
}
.hero-orb-1 { width: 300px; height: 300px; top: 10%; right: 10%; background: rgba(102,126,234,0.25); }
.hero-orb-2 { width: 200px; height: 200px; bottom: 15%; left: 10%; background: rgba(118,75,162,0.18); animation-delay: -4s; }
.hero-orb-3 { width: 150px; height: 150px; top: 35%; left: 45%; background: rgba(240,147,251,0.12); animation-delay: -8s; }
.hero-content { max-width: 1200px; margin: 0 auto; position: relative; z-index: 2; width: 100%; text-align: center; }
.hero-badge {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 8px 18px; background: rgba(255,255,255,0.8);
  border: 1px solid rgba(102,126,234,0.15); border-radius: 100px;
  font-size: 13px; font-weight: 500; color: #5a5a7a;
  margin-bottom: 28px; backdrop-filter: blur(8px);
}
.badge-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--gradient-main); animation: pulse 2s ease-in-out infinite; }
.hero-title {
  font-family: var(--font-cn); font-weight: 900;
  font-size: clamp(40px, 7vw, 72px); line-height: 1.15;
  margin-bottom: 20px; color: #1a1a2e;
}
.hero-desc { font-size: 18px; color: #6a6a8a; max-width: 480px; margin: 0 auto; line-height: 1.6; }

.scroll-hint {
  position: absolute; bottom: 32px; left: 50%; transform: translateX(-50%);
  display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; z-index: 2;
}
.scroll-line {
  width: 2px; height: 40px;
  background: linear-gradient(to bottom, var(--purple-start), var(--purple-end));
  border-radius: 1px; animation: scrollLine 2s ease-in-out infinite;
}
.scroll-text { font-size: 11px; color: #8a8aaa; letter-spacing: 0.15em; text-transform: uppercase; }

/* ===== Fleet Section ===== */
.fleet-section { padding: 80px 40px 100px; background: linear-gradient(180deg, #faf9ff 0%, #f5f3ff 100%); }
.section-inner { max-width: 1200px; margin: 0 auto; }
.section-header { text-align: center; margin-bottom: 40px; }
.section-tag {
  font-family: var(--font-display); font-size: 12px; font-weight: 600; letter-spacing: 0.2em;
  background: var(--gradient-main); -webkit-background-clip: text; background-clip: text;
  -webkit-text-fill-color: transparent; color: transparent; text-transform: uppercase;
}
.section-title { font-family: var(--font-cn); font-size: clamp(28px, 4vw, 40px); font-weight: 700; margin-top: 8px; color: #1a1a2e; }

/* Filter Tabs */
.filter-tabs { display: flex; gap: 8px; justify-content: center; margin-bottom: 40px; }
.filter-tab {
  padding: 10px 28px; background: #fff; border: 1px solid rgba(102,126,234,0.08);
  border-radius: 100px; font-size: 14px; font-weight: 500;
  color: #5a5a7a; cursor: pointer;
  transition: all 0.3s cubic-bezier(0.22,1,0.36,1); font-family: var(--font-cn);
}
.filter-tab:hover { border-color: rgba(102,126,234,0.25); color: var(--purple-start); }
.filter-tab.active {
  background: var(--gradient-main); color: #fff; border-color: transparent;
  box-shadow: 0 4px 16px rgba(102,126,234,0.25);
}

/* ===== Vehicle Cards ===== */
.vehicles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 28px;
}
.vehicle-card {
  position: relative; padding: 36px 32px; background: #fff;
  border: 1px solid rgba(102,126,234,0.08); border-radius: 20px;
  overflow: hidden; cursor: pointer;
  transition: all 0.4s cubic-bezier(0.22,1,0.36,1);
}
.vehicle-card::before {
  content: ''; position: absolute; top: 0; left: 0; right: 0; height: 3px;
  background: var(--gradient-main); opacity: 0; transition: opacity 0.3s;
}
.vehicle-card:hover {
  border-color: rgba(102,126,234,0.2);
  transform: translateY(-6px);
  box-shadow: 0 16px 48px rgba(102,126,234,0.1), 0 4px 16px rgba(0,0,0,0.04);
}
.vehicle-card:hover::before { opacity: 1; }
.card-shimmer {
  position: absolute; inset: 0;
  background: linear-gradient(105deg, transparent 40%, rgba(102,126,234,0.03) 45%, rgba(118,75,162,0.03) 50%, transparent 55%);
  background-size: 200% 100%; animation: shimmer 4s ease-in-out infinite; pointer-events: none;
}
.card-type {
  font-family: var(--font-display); font-size: 11px; font-weight: 600; letter-spacing: 0.15em;
  text-transform: uppercase; margin-bottom: 16px; color: var(--card-color, var(--purple-start));
}
.card-visual { margin-bottom: 20px; display: flex; justify-content: center; }
.car-svg { width: 180px; height: 80px; color: var(--card-color, var(--purple-start)); }
.card-name { font-family: var(--font-cn); font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.card-plate { font-size: 13px; color: #8a8aaa; margin-bottom: 20px; }
.card-specs { display: flex; align-items: center; gap: 16px; padding: 16px 0; border-top: 1px solid rgba(102,126,234,0.06); }
.spec { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4px; }
.spec-value { font-family: var(--font-number); font-size: 20px; font-weight: 700; color: #1a1a2e; letter-spacing: -0.01em; }
.spec-label { font-size: 11px; color: #8a8aaa; }
.spec-divider { width: 1px; height: 32px; background: rgba(102,126,234,0.08); }

.btn-primary-outline {
  width: 100%; padding: 12px 20px; margin-top: 20px;
  background: transparent; border: 1.5px solid rgba(102,126,234,0.2);
  color: var(--purple-start); font-family: var(--font-cn); font-size: 14px; font-weight: 600;
  border-radius: 10px; cursor: pointer; transition: all 0.3s;
  display: flex; align-items: center; justify-content: center; gap: 6px;
}
.btn-primary-outline:hover {
  border-color: var(--purple-start); background: rgba(102,126,234,0.04);
  transform: translateY(-1px); box-shadow: 0 4px 16px rgba(102,126,234,0.1);
}


/* ===== CTA ===== */
.cta-section { padding: 60px 40px; background: #fff; }
.cta-card {
  padding: 64px; text-align: center;
  background: linear-gradient(135deg, #f8f6ff 0%, #fef8ff 50%, #f5f0ff 100%);
  border: 1px solid rgba(102,126,234,0.08); border-radius: 24px;
}
.cta-title { font-family: var(--font-cn); font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.cta-desc { font-size: 15px; color: #6a6a8a; margin-bottom: 28px; }


@media (max-width: 600px) {
  .hero { padding: 120px 20px 60px; }
  .fleet-section { padding: 60px 16px; }
  .vehicles-grid { grid-template-columns: 1fr; }
  .cta-card { padding: 40px 20px; }
}
</style>
