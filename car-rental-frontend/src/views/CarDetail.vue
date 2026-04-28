<template>
  <div class="car-detail-page">
    <!-- Navigation -->
    <AppNav transparent />

    <!-- Loading -->
    <LoadingSkeleton v-if="loading" type="card" :count="1" />

    <template v-else-if="vehicle">
      <!-- Hero -->
      <section class="hero">
        <div class="hero-bg">
          <div class="hero-gradient"></div>
          <div class="hero-orb hero-orb-1"></div>
          <div class="hero-orb hero-orb-2"></div>
        </div>
        <div class="hero-content">
          <div class="hero-visual">
            <!-- Economy SVG -->
            <svg v-if="vehicle.type === 'economy'" viewBox="0 0 600 220" fill="none" class="hero-car-svg">
              <defs>
                <linearGradient id="carBodyGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#667eea" stop-opacity="0.18"/>
                  <stop offset="100%" stop-color="#667eea" stop-opacity="0.04"/>
                </linearGradient>
                <linearGradient id="carStroke" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
              <!-- Shadow -->
              <ellipse cx="300" cy="200" rx="220" ry="14" fill="rgba(102,126,234,0.06)"/>
              <!-- Body -->
              <path d="M70 145 L95 100 L160 80 L440 80 L505 100 L530 145" stroke="url(#carStroke)" stroke-width="3" fill="none"/>
              <!-- Roof -->
              <path d="M155 80 L155 60 L250 50 L350 50 L445 60 L445 80" stroke="url(#carStroke)" stroke-width="3" fill="none"/>
              <!-- Windows -->
              <path d="M165 78 L165 64 L248 54 L248 78" stroke="url(#carStroke)" stroke-width="1.5" fill="rgba(102,126,234,0.08)"/>
              <path d="M255 78 L255 56 L345 56 L345 78" stroke="url(#carStroke)" stroke-width="1.5" fill="rgba(102,126,234,0.08)"/>
              <path d="M352 78 L352 58 L435 64 L435 78" stroke="url(#carStroke)" stroke-width="1.5" fill="rgba(102,126,234,0.08)"/>
              <!-- Bottom -->
              <line x1="40" y1="145" x2="560" y2="145" stroke="url(#carStroke)" stroke-width="2.5" opacity="0.7"/>
              <!-- Wheels -->
              <circle cx="140" cy="145" r="38" stroke="url(#carStroke)" stroke-width="3.5" fill="none"/>
              <circle cx="140" cy="145" r="18" stroke="url(#carStroke)" stroke-width="2" fill="none"/>
              <circle cx="140" cy="145" r="6" stroke="url(#carStroke)" stroke-width="1.5" fill="none" opacity="0.6"/>
              <circle cx="460" cy="145" r="38" stroke="url(#carStroke)" stroke-width="3.5" fill="none"/>
              <circle cx="460" cy="145" r="18" stroke="url(#carStroke)" stroke-width="2" fill="none"/>
              <circle cx="460" cy="145" r="6" stroke="url(#carStroke)" stroke-width="1.5" fill="none" opacity="0.6"/>
              <!-- Headlights -->
              <circle cx="530" cy="120" r="9" stroke="url(#carStroke)" stroke-width="1.5" opacity="0.4"/>
              <circle cx="70" cy="120" r="9" stroke="url(#carStroke)" stroke-width="1.5" opacity="0.4"/>
            </svg>
            <!-- Luxury SVG -->
            <svg v-else viewBox="0 0 600 220" fill="none" class="hero-car-svg">
              <defs>
                <linearGradient id="carBodyGradL" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#764ba2" stop-opacity="0.18"/>
                  <stop offset="100%" stop-color="#764ba2" stop-opacity="0.04"/>
                </linearGradient>
                <linearGradient id="carStrokeL" x1="0%" y1="0%" x2="100%" y2="0%">
                  <stop offset="0%" stop-color="#667eea"/>
                  <stop offset="100%" stop-color="#764ba2"/>
                </linearGradient>
              </defs>
              <ellipse cx="300" cy="200" rx="230" ry="14" fill="rgba(118,75,162,0.06)"/>
              <!-- Body -- longer, lower -->
              <path d="M55 140 L85 90 L155 70 L445 70 L515 90 L545 140" stroke="url(#carStrokeL)" stroke-width="3.2" fill="none"/>
              <!-- Roof -- sleeker -->
              <path d="M150 70 L150 48 L260 38 L340 38 L450 48 L450 70" stroke="url(#carStrokeL)" stroke-width="3.2" fill="none"/>
              <!-- Windows -->
              <path d="M162 68 L162 52 L256 42 L256 68" stroke="url(#carStrokeL)" stroke-width="1.5" fill="rgba(118,75,162,0.08)"/>
              <path d="M264 68 L264 44 L336 44 L336 68" stroke="url(#carStrokeL)" stroke-width="1.5" fill="rgba(118,75,162,0.08)"/>
              <path d="M344 68 L344 46 L438 52 L438 68" stroke="url(#carStrokeL)" stroke-width="1.5" fill="rgba(118,75,162,0.08)"/>
              <line x1="30" y1="140" x2="570" y2="140" stroke="url(#carStrokeL)" stroke-width="2.8" opacity="0.7"/>
              <!-- Wheels -->
              <circle cx="130" cy="140" r="42" stroke="url(#carStrokeL)" stroke-width="3.5" fill="none"/>
              <circle cx="130" cy="140" r="20" stroke="url(#carStrokeL)" stroke-width="2" fill="none"/>
              <circle cx="130" cy="140" r="8" stroke="url(#carStrokeL)" stroke-width="1.5" fill="none" opacity="0.6"/>
              <circle cx="470" cy="140" r="42" stroke="url(#carStrokeL)" stroke-width="3.5" fill="none"/>
              <circle cx="470" cy="140" r="20" stroke="url(#carStrokeL)" stroke-width="2" fill="none"/>
              <circle cx="470" cy="140" r="8" stroke="url(#carStrokeL)" stroke-width="1.5" fill="none" opacity="0.6"/>
              <!-- Grille -->
              <line x1="545" y1="98" x2="545" y2="130" stroke="url(#carStrokeL)" stroke-width="2" opacity="0.4"/>
              <line x1="538" y1="100" x2="538" y2="126" stroke="url(#carStrokeL)" stroke-width="1.5" opacity="0.3"/>
              <!-- Mirror -->
              <circle cx="150" cy="54" r="5" stroke="url(#carStrokeL)" stroke-width="1" opacity="0.4"/>
            </svg>
          </div>
          <div class="hero-info">
            <span class="hero-badge" :class="'badge-' + vehicle.type">{{ vehicle.typeName }}</span>
            <h1 class="hero-title">{{ vehicle.model }}</h1>
            <p class="hero-plate">{{ vehicle.plateNo }}</p>
          </div>
        </div>
      </section>

      <!-- Specs Bar -->
      <section class="specs-bar">
        <div class="specs-inner">
          <div class="spec-item">
            <span class="spec-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/></svg>
            </span>
            <div>
              <span class="spec-val">{{ vehicle.dailyRate }}</span>
              <span class="spec-lbl">日租金(元)</span>
            </div>
          </div>
          <div class="spec-item">
            <span class="spec-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M18 8h1a4 4 0 010 8h-1"/><path d="M2 8h16v8H2z"/></svg>
            </span>
            <div>
              <span class="spec-val">{{ vehicle.freeMileage }}</span>
              <span class="spec-lbl">每日免里程(km)</span>
            </div>
          </div>
          <div class="spec-item">
            <span class="spec-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            </span>
            <div>
              <span class="spec-val">{{ vehicle.overtimeRate }}</span>
              <span class="spec-lbl">超时费(元/h)</span>
            </div>
          </div>
          <div class="spec-item">
            <span class="spec-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11l3 3L22 4"/><path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/></svg>
            </span>
            <div>
              <span class="spec-val">{{ vehicle.mileageRate }}</span>
              <span class="spec-lbl">超里程费(元/km)</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Booking Flow -->
      <section class="booking-section">
        <div class="section-inner">
          <div class="section-header">
            <h2 class="section-title">开始预订</h2>
            <p class="section-desc">三步完成，轻松出行</p>
          </div>

          <!-- Step Indicator -->
          <div class="steps-indicator">
            <div class="step-dot" :class="{ active: step >= 1, done: step > 1 }" @click="step > 1 && goStep(1)">
              <span v-if="step > 1" class="step-check">&#10003;</span>
              <span v-else class="step-num">1</span>
            </div>
            <div class="step-line" :class="{ done: step > 1 }"></div>
            <div class="step-dot" :class="{ active: step >= 2, done: step > 2 }" @click="step > 2 && goStep(2)">
              <span v-if="step > 2" class="step-check">&#10003;</span>
              <span v-else class="step-num">2</span>
            </div>
            <div class="step-line" :class="{ done: step > 2 }"></div>
            <div class="step-dot" :class="{ active: step >= 3 }">
              <span class="step-num">3</span>
            </div>
          </div>
          <div class="steps-labels">
            <span class="step-label" :class="{ active: step >= 1 }">选择取车时间</span>
            <span class="step-label" :class="{ active: step >= 2 }">选择还车时间</span>
            <span class="step-label" :class="{ active: step >= 3 }">确认预订</span>
          </div>

          <!-- Step Content -->
          <div class="step-content">
            <!-- Step 1: Pickup Time -->
            <transition name="step-fade" mode="out-in">
              <div v-if="step === 1" key="s1" class="step-panel">
                <div class="step-illustration">
                  <svg viewBox="0 0 120 80" fill="none">
                    <circle cx="60" cy="40" r="38" stroke="#667eea" stroke-width="1.5" fill="none" opacity="0.15"/>
                    <circle cx="60" cy="40" r="28" stroke="#667eea" stroke-width="1.5" fill="none" opacity="0.1"/>
                    <path d="M60 18v20l16 8" stroke="#667eea" stroke-width="2" stroke-linecap="round"/>
                  </svg>
                </div>
                <h3 class="step-title">选择取车时间</h3>
                <p class="step-desc">请选择您计划到店取车的日期和时间</p>
                <div class="step-form">
                  <el-date-picker
                    v-model="bookingForm.pickupTime" type="datetime"
                    placeholder="选择取车日期和时间" size="large"
                    :disabled-date="d => d.getTime() < Date.now() - 86400000"
                    class="step-datepicker" />
                </div>
                <button class="btn-primary btn-lg" :disabled="!bookingForm.pickupTime" @click="goStep(2)">
                  下一步
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                    <path d="M5 12h14M12 5l7 7-7 7"/>
                  </svg>
                </button>
              </div>

              <!-- Step 2: Return Time -->
              <div v-else-if="step === 2" key="s2" class="step-panel">
                <div class="step-illustration">
                  <svg viewBox="0 0 120 80" fill="none">
                    <rect x="10" y="10" width="100" height="60" rx="8" stroke="#667eea" stroke-width="1.5" fill="none" opacity="0.15"/>
                    <line x1="10" y1="30" x2="110" y2="30" stroke="#667eea" stroke-width="1.2" opacity="0.2"/>
                    <rect x="30" y="16" width="28" height="10" rx="3" stroke="#667eea" stroke-width="1.5" fill="none" opacity="0.4"/>
                    <rect x="62" y="16" width="28" height="10" rx="3" stroke="#667eea" stroke-width="1.5" fill="none" opacity="0.4"/>
                  </svg>
                </div>
                <h3 class="step-title">选择还车时间</h3>
                <p class="step-desc">取车时间：{{ formatPickup }}</p>
                <div class="step-form">
                  <el-date-picker
                    v-model="bookingForm.returnTime" type="datetime"
                    placeholder="选择还车日期和时间" size="large"
                    :disabled-date="d => d.getTime() < Date.now() - 86400000"
                    class="step-datepicker" />
                </div>
                <div v-if="rentalDays > 0" class="step-preview">
                  <div class="preview-row">
                    <span>租车天数</span>
                    <span class="preview-val">{{ rentalDays }} 天</span>
                  </div>
                  <div class="preview-row">
                    <span>每日租金</span>
                    <span class="preview-val">¥{{ vehicle.dailyRate }}</span>
                  </div>
                  <div class="preview-row highlight">
                    <span>预计租金</span>
                    <span class="preview-val">¥{{ estimatedRent }}</span>
                  </div>
                </div>
                <div class="step-buttons">
                  <button class="btn-ghost" @click="goStep(1)">上一步</button>
                  <button class="btn-primary btn-lg" :disabled="!bookingForm.returnTime" @click="goStep(3)">
                    下一步
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18">
                      <path d="M5 12h14M12 5l7 7-7 7"/>
                    </svg>
                  </button>
                </div>
              </div>

              <!-- Step 3: Confirm -->
              <div v-else-if="step === 3" key="s3" class="step-panel">
                <div class="step-illustration">
                  <svg viewBox="0 0 120 80" fill="none">
                    <path d="M30 42l20 20 40-44" stroke="#4ecdc4" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
                    <circle cx="60" cy="40" r="38" stroke="#4ecdc4" stroke-width="1.5" fill="none" opacity="0.15"/>
                  </svg>
                </div>
                <h3 class="step-title">确认预订</h3>
                <p class="step-desc">请核对以下预订信息</p>
                <div class="confirm-card">
                  <div class="confirm-row">
                    <label>车辆</label>
                    <span>{{ vehicle.model }} · {{ vehicle.plateNo }}</span>
                  </div>
                  <div class="confirm-divider"></div>
                  <div class="confirm-row">
                    <label>类型</label>
                    <span>{{ vehicle.typeName }}</span>
                  </div>
                  <div class="confirm-divider"></div>
                  <div class="confirm-row">
                    <label>取车时间</label>
                    <span>{{ formatPickup }}</span>
                  </div>
                  <div class="confirm-divider"></div>
                  <div class="confirm-row">
                    <label>还车时间</label>
                    <span>{{ formatReturn }}</span>
                  </div>
                  <div class="confirm-divider"></div>
                  <div class="confirm-row">
                    <label>租车天数</label>
                    <span>{{ rentalDays }} 天</span>
                  </div>
                  <div class="confirm-divider"></div>
                  <div class="confirm-row highlight">
                    <label>预计租金</label>
                    <span class="confirm-price">¥{{ estimatedRent }}</span>
                  </div>
                </div>
                <div class="confirm-hint">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                    <circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>
                  </svg>
                  <span>还车时系统将根据实际超时和超里程情况计算最终费用</span>
                </div>
                <div class="step-buttons">
                  <button class="btn-ghost" @click="goStep(2)">上一步</button>
                  <button class="btn-primary btn-lg" :loading="submitting" @click="submitBooking">
                    确认预订
                  </button>
                </div>
              </div>
            </transition>
          </div>
        </div>
      </section>

      <!-- Back CTA -->
      <section class="back-section">
        <button class="btn-ghost" @click="$router.push('/fleet')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          查看更多车型
        </button>
      </section>
    </template>

    <!-- Error -->
    <EmptyState v-else title="车辆信息加载失败" description="请返回车型列表重新选择">
      <template #icon>
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"/><path d="M12 8v4M12 16h.01"/>
        </svg>
      </template>
      <template #action>
        <button class="btn-primary" @click="$router.push('/fleet')">返回车型一览</button>
      </template>
    </EmptyState>

    <AppFooter />
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVehicleById } from '../api/vehicle'
import { createBooking } from '../api/booking'
import AppNav from '../components/AppNav.vue'
import AppFooter from '../components/AppFooter.vue'
import LoadingSkeleton from '../components/LoadingSkeleton.vue'
import EmptyState from '../components/EmptyState.vue'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const vehicle = ref(null)
const step = ref(1)
const submitting = ref(false)

const bookingForm = reactive({
  pickupTime: '',
  returnTime: '',
})

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const formatPickup = computed(() => formatDate(bookingForm.pickupTime))
const formatReturn = computed(() => formatDate(bookingForm.returnTime))

const rentalDays = computed(() => {
  if (!bookingForm.pickupTime || !bookingForm.returnTime) return 0
  const diff = new Date(bookingForm.returnTime) - new Date(bookingForm.pickupTime)
  return Math.max(1, Math.ceil(diff / 86400000))
})

const estimatedRent = computed(() => {
  if (!rentalDays.value || !vehicle.value) return 0
  return rentalDays.value * (vehicle.value.dailyRate || 0)
})

function formatDate(t) {
  if (!t) return ''
  const d = new Date(t)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  const s = String(d.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${day} ${h}:${min}:${s}`
}

function goStep(n) {
  if (n === 2 && !bookingForm.pickupTime) return ElMessage.warning('请先选择取车时间')
  if (n === 3 && !bookingForm.returnTime) return ElMessage.warning('请先选择还车时间')
  step.value = n
}

async function submitBooking() {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录后再预订')
    router.push('/login')
    return
  }
  if (!bookingForm.pickupTime || !bookingForm.returnTime) return

  const pickup = new Date(bookingForm.pickupTime)
  const ret = new Date(bookingForm.returnTime)
  if (ret <= pickup) return ElMessage.warning('还车时间必须晚于取车时间')

  submitting.value = true
  try {
    await createBooking({
      vehicleId: vehicle.value.id,
      plannedPickupTime: formatPickup.value,
      plannedReturnTime: formatReturn.value,
    })
    ElMessage.success('预订已提交，等待工作人员确认')
    router.push('/my-bookings')
  } catch (e) { console.error('预订提交失败:', e) } finally { submitting.value = false }
}

// Watch for pickup change to validate return time
watch(() => bookingForm.pickupTime, () => {
  if (bookingForm.returnTime && new Date(bookingForm.returnTime) <= new Date(bookingForm.pickupTime)) {
    bookingForm.returnTime = ''
  }
})

onMounted(async () => {
  try {
    const res = await getVehicleById(Number(route.params.id))
    vehicle.value = res.data
  } catch (e) { console.error('获取车辆详情失败:', e) } finally { loading.value = false }
})
</script>

<style scoped>

/* ===== Animations ===== */
@keyframes orbDrift {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, -15px) scale(1.08); }
}
@keyframes float { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }

.step-fade-enter-active, .step-fade-leave-active { transition: all 0.35s cubic-bezier(0.22,1,0.36,1); }
.step-fade-enter-from { opacity: 0; transform: translateY(20px); }
.step-fade-leave-to { opacity: 0; transform: translateY(-20px); }

.car-detail-page { min-height: 100vh; background: #faf9ff; }
.btn-primary { padding: 9px 24px; background: var(--gradient-main); border: none; color: #fff; font-family: var(--font-cn); font-size: 14px; font-weight: 600; border-radius: 10px; cursor: pointer; transition: all 0.3s cubic-bezier(0.22,1,0.36,1); display: inline-flex; align-items: center; gap: 8px; box-shadow: 0 4px 16px rgba(102,126,234,0.25); }
.btn-primary:hover { transform: translateY(-2px); box-shadow: 0 8px 32px rgba(102,126,234,0.35); }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; transform: none; box-shadow: none; }
.btn-lg { padding: 14px 36px; font-size: 16px; border-radius: 12px; }
.btn-ghost { padding: 10px 24px; background: rgba(255,255,255,0.7); border: 1px solid rgba(102,126,234,0.15); color: #5a5a7a; font-family: var(--font-cn); font-size: 14px; font-weight: 500; border-radius: 10px; cursor: pointer; transition: all 0.3s cubic-bezier(0.22,1,0.36,1); display: inline-flex; align-items: center; gap: 6px; backdrop-filter: blur(8px); }
.btn-ghost:hover { background: rgba(255,255,255,0.9); border-color: rgba(102,126,234,0.3); color: #667eea; box-shadow: 0 4px 16px rgba(102,126,234,0.1); }

/* ===== Hero ===== */
.hero {
  padding: 80px 40px 60px;
  background: linear-gradient(160deg, #f0f0ff 0%, #f8f6ff 40%, #fef8ff 70%, #f5f0ff 100%);
  position: relative; overflow: hidden;
}
.hero-bg { position: absolute; inset: 0; }
.hero-gradient { position: absolute; inset: 0; background: radial-gradient(ellipse 60% 50% at center, rgba(102,126,234,0.08) 0%, transparent 70%); }
.hero-orb { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.4; animation: orbDrift 10s ease-in-out infinite; }
.hero-orb-1 { width: 250px; height: 250px; top: -30px; right: 10%; background: rgba(102,126,234,0.18); }
.hero-orb-2 { width: 180px; height: 180px; bottom: -20px; left: 5%; background: rgba(118,75,162,0.12); animation-delay: -5s; }
.hero-content { max-width: 900px; margin: 0 auto; position: relative; z-index: 1; text-align: center; }
.hero-visual { margin-bottom: 32px; animation: float 5s ease-in-out infinite; }
.hero-car-svg { width: 100%; max-width: 500px; height: auto; }
.hero-info { text-align: center; }
.hero-badge { display: inline-block; padding: 5px 16px; border-radius: 100px; font-size: 12px; font-weight: 600; margin-bottom: 12px; }
.badge-economy { background: rgba(102,126,234,0.1); color: #667eea; }
.badge-luxury { background: rgba(118,75,162,0.1); color: #764ba2; }
.hero-title { font-family: var(--font-cn); font-size: clamp(32px, 5vw, 48px); font-weight: 800; color: #1a1a2e; margin-bottom: 8px; }
.hero-plate { font-size: 16px; color: #8a8aaa; }

/* ===== Specs Bar ===== */
.specs-bar { padding: 0 40px; margin-top: -30px; position: relative; z-index: 2; }
.specs-inner {
  max-width: 900px; margin: 0 auto; display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px;
  background: #fff; border: 1px solid rgba(102,126,234,0.08); border-radius: 16px;
  padding: 24px 20px; box-shadow: 0 4px 24px rgba(0,0,0,0.04);
}
.spec-item { display: flex; align-items: center; gap: 12px; }
.spec-icon { width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; color: #667eea; background: rgba(102,126,234,0.06); border-radius: 10px; flex-shrink: 0; }
.spec-icon svg { width: 20px; height: 20px; }
.spec-item > div { display: flex; flex-direction: column; }
.spec-val { font-family: var(--font-number); font-size: 18px; font-weight: 700; color: #1a1a2e; }
.spec-lbl { font-size: 11px; color: #8a8aaa; }

/* ===== Booking Section ===== */
.booking-section { padding: 80px 40px; }
.section-inner { max-width: 640px; margin: 0 auto; }
.section-header { text-align: center; margin-bottom: 40px; }
.section-title { font-family: var(--font-cn); font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.section-desc { font-size: 14px; color: #6a6a8a; }

/* Step Indicator */
.steps-indicator { display: flex; align-items: center; justify-content: center; margin-bottom: 12px; }
.step-dot {
  width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center;
  background: #fff; border: 2px solid rgba(102,126,234,0.15); color: #8a8aaa;
  font-family: var(--font-number); font-size: 16px; font-weight: 700;
  transition: all 0.4s cubic-bezier(0.22,1,0.36,1);
}
.step-dot.active { border-color: #667eea; color: #667eea; background: rgba(102,126,234,0.05); box-shadow: 0 0 0 6px rgba(102,126,234,0.06); }
.step-dot.done { background: var(--gradient-main); border-color: transparent; color: #fff; cursor: pointer; }
.step-line { width: 60px; height: 2px; background: rgba(102,126,234,0.1); margin: 0 8px; transition: background 0.4s; }
.step-line.done { background: var(--gradient-main); }
.step-check { font-size: 18px; }
.steps-labels { display: flex; justify-content: center; gap: 80px; margin-bottom: 48px; }
.step-label { font-size: 13px; color: #8a8aaa; font-weight: 500; transition: color 0.3s; }
.step-label.active { color: #667eea; font-weight: 600; }

/* Step Panel */
.step-content { min-height: 300px; }
.step-panel { text-align: center; }
.step-illustration { margin-bottom: 20px; display: flex; justify-content: center; }
.step-illustration svg { width: 100px; height: 60px; }
.step-title { font-family: var(--font-cn); font-size: 22px; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.step-desc { font-size: 15px; color: #6a6a8a; margin-bottom: 28px; }
.step-form { margin-bottom: 24px; display: flex; justify-content: center; }
.step-datepicker { width: 320px; }
.step-buttons { display: flex; gap: 12px; justify-content: center; margin-top: 8px; }

/* Preview Card (Step 2) */
.step-preview {
  max-width: 340px; margin: 0 auto 24px; padding: 16px 20px;
  background: #faf9ff; border: 1px solid rgba(102,126,234,0.08); border-radius: 12px;
}
.preview-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 14px; color: #6a6a8a; }
.preview-row.highlight { border-top: 1px solid rgba(102,126,234,0.08); margin-top: 6px; padding-top: 10px; color: #1a1a2e; font-weight: 600; }
.preview-val { font-family: var(--font-number); font-weight: 600; color: #1a1a2e; }
.preview-row.highlight .preview-val { color: #667eea; }

/* Confirm Card (Step 3) */
.confirm-card {
  max-width: 420px; margin: 0 auto 20px; padding: 24px;
  background: #fff; border: 1px solid rgba(102,126,234,0.08); border-radius: 16px;
  text-align: left;
}
.confirm-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; }
.confirm-row label { font-size: 13px; color: #8a8aaa; }
.confirm-row span { font-size: 14px; font-weight: 500; color: #1a1a2e; }
.confirm-row.highlight span { font-weight: 700; }
.confirm-price { font-family: var(--font-number); color: #667eea !important; font-size: 18px !important; }
.confirm-divider { height: 1px; background: rgba(102,126,234,0.06); }
.confirm-hint { display: flex; align-items: flex-start; gap: 8px; padding: 12px; background: rgba(102,126,234,0.03); border-radius: 8px; max-width: 420px; margin: 0 auto 24px; text-align: left; }
.confirm-hint svg { width: 16px; height: 16px; color: #667eea; flex-shrink: 0; margin-top: 1px; }
.confirm-hint span { font-size: 12px; color: #6a6a8a; }

/* Back */
.back-section { text-align: center; padding: 20px 40px 60px; }


@media (max-width: 600px) {
  .hero { padding: 60px 16px 40px; }
  .specs-bar { padding: 0 16px; }
  .specs-inner { grid-template-columns: repeat(2, 1fr); }
  .booking-section { padding: 60px 16px; }
  .steps-labels { gap: 30px; }
  .step-label { font-size: 11px; }
}
</style>

<style>
.car-detail-page .el-date-editor { width: 320px !important; }
.car-detail-page .el-input__wrapper {
  border-radius: 12px;
  box-shadow: 0 0 0 1px rgba(102,126,234,0.12) !important;
  padding: 12px 16px;
}
.car-detail-page .el-input.is-focus .el-input__wrapper,
.car-detail-page .el-select .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px #667eea, 0 0 0 3px rgba(102,126,234,0.08) !important;
}
</style>
