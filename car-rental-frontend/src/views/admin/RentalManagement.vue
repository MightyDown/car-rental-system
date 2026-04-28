<template>
  <div class="rental-management">
    <div class="page-header scroll-reveal">
      <div>
        <h1 class="page-title">取还车管理</h1>
        <p class="page-desc">办理取车与还车手续，系统自动计算全部费用</p>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs" @tab-change="onTabChange">
      <!-- ==================== 取车处理 ==================== -->
      <el-tab-pane label="取车处理" name="pickup">
        <div class="table-card">
          <el-table :data="confirmedBookings" v-loading="loadingPickup" stripe
                    empty-text="暂无待取车预订" style="width: 100%"
                    :header-cell-style="headerStyle" @row-click="onPickupRowClick"
                    highlight-current-row>
            <el-table-column prop="bookingNo" label="预订编号" min-width="150" />
            <el-table-column label="用户" min-width="100">
              <template #default="{ row }">{{ row.userRealName || row.userName }}</template>
            </el-table-column>
            <el-table-column label="车辆" min-width="130">
              <template #default="{ row }">{{ row.vehiclePlateNo }} · {{ row.vehicleModel }}</template>
            </el-table-column>
            <el-table-column label="计划取车" min-width="150">
              <template #default="{ row }">{{ formatTime(row.plannedPickupTime) }}</template>
            </el-table-column>
            <el-table-column label="计划还车" min-width="150">
              <template #default="{ row }">{{ formatTime(row.plannedReturnTime) }}</template>
            </el-table-column>
            <el-table-column prop="plannedDays" label="天数" width="65" align="center" />
            <el-table-column label="预计租金" width="100" align="center">
              <template #default="{ row }">¥{{ row.estimatedRent }}</template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <button class="action-btn pickup-btn" @click.stop="openPickup(row)">取车</button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- ==================== 还车处理 ==================== -->
      <el-tab-pane label="还车处理" name="return">
        <div class="table-card" style="margin-bottom: 0;">
          <el-table :data="activeBookings" v-loading="loadingReturn" stripe
                    empty-text="暂无待还车预订" style="width: 100%"
                    :header-cell-style="headerStyle" @row-click="onReturnRowClick"
                    highlight-current-row>
            <el-table-column prop="bookingNo" label="预订编号" min-width="150" />
            <el-table-column label="用户" min-width="100">
              <template #default="{ row }">{{ row.userRealName || row.userName }}</template>
            </el-table-column>
            <el-table-column label="车辆" min-width="130">
              <template #default="{ row }">{{ row.vehiclePlateNo }} · {{ row.vehicleModel }}</template>
            </el-table-column>
            <el-table-column label="取车时间" min-width="150">
              <template #default="{ row }">{{ formatTime(row.actualPickupTime) }}</template>
            </el-table-column>
            <el-table-column label="计划还车" min-width="150">
              <template #default="{ row }">
                <span :class="{ 'overdue-text': isOverdue(row) }">
                  {{ formatTime(row.plannedReturnTime) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="取车里程" width="90" align="center">
              <template #default="{ row }">{{ row.pickupMileage }}km</template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <span class="status-tag" :class="'status-' + row.status">{{ row.statusName }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <button class="action-btn return-btn" @click.stop="openReturn(row)">还车</button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- ===== 取车弹窗 ===== -->
    <el-dialog v-model="pickupVisible" title="办理取车" width="460px"
               destroy-on-close :close-on-click-modal="false">
      <div class="pickup-info" v-if="pickupTarget">
        <div class="info-card">
          <div class="info-row"><label>预订编号</label><span>{{ pickupTarget.bookingNo }}</span></div>
          <div class="info-row"><label>用户</label><span>{{ pickupTarget.userRealName || pickupTarget.userName }}</span></div>
          <div class="info-row"><label>车辆</label><span>{{ pickupTarget.vehiclePlateNo }} · {{ pickupTarget.vehicleModel }}</span></div>
          <div class="info-row"><label>计划天数</label><span>{{ pickupTarget.plannedDays }} 天</span></div>
        </div>
        <el-form :model="pickupForm" label-width="90px" class="pickup-form">
          <el-form-item label="取车里程(km)" required>
            <el-input-number v-model="pickupForm.pickupMileage" :min="0" style="width: 100%"
                             placeholder="请输入当前里程表读数" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="pickupVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPickup" :loading="submitting">确认取车</el-button>
      </template>
    </el-dialog>

    <!-- ===== 还车弹窗 ===== -->
    <el-dialog v-model="returnVisible" title="办理还车" width="560px"
               destroy-on-close :close-on-click-modal="false">
      <div class="return-info" v-if="returnTarget">
        <div class="info-card">
          <div class="info-row"><label>预订编号</label><span>{{ returnTarget.bookingNo }}</span></div>
          <div class="info-row"><label>用户</label><span>{{ returnTarget.userRealName || returnTarget.userName }}</span></div>
          <div class="info-row"><label>车辆</label><span>{{ returnTarget.vehiclePlateNo }} · {{ returnTarget.vehicleModel }}</span></div>
          <div class="info-row"><label>取车时间</label><span>{{ formatTime(returnTarget.actualPickupTime) }}</span></div>
          <div class="info-row"><label>计划还车</label><span>{{ formatTime(returnTarget.plannedReturnTime) }}</span></div>
          <div class="info-row"><label>取车里程</label><span>{{ returnTarget.pickupMileage }} km</span></div>
          <div class="info-row"><label>预计租金</label><span class="money">¥{{ returnTarget.estimatedRent }}</span></div>
        </div>
        <el-form :model="returnForm" label-width="100px" class="return-form">
          <el-form-item label="还车里程(km)" required>
            <el-input-number v-model="returnForm.returnMileage" :min="returnTarget.pickupMileage || 0"
                             style="width: 100%" placeholder="请输入当前里程表读数" />
          </el-form-item>
        </el-form>
        <div class="return-hint">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>
          </svg>
          <span>还车里程必须 ≥ 取车里程（{{ returnTarget.pickupMileage }} km）。费用由系统自动计算。</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn" :loading="submitting">确认还车</el-button>
      </template>
    </el-dialog>

    <!-- ===== 还车结果弹窗 ===== -->
    <el-dialog v-model="resultVisible" title="还车完成 · 费用明细" width="540px" destroy-on-close>
      <div v-if="returnResult" class="fee-result">
        <div class="result-header">
          <svg viewBox="0 0 24 24" fill="none" stroke="var(--success)" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 11-5.93-9.14"/>
            <polyline points="22,4 12,14.01 9,11.01"/>
          </svg>
          <span>还车成功，费用明细如下</span>
        </div>
        <div class="fee-grid">
          <div class="fee-item">
            <span class="fee-label">预计租金</span>
            <span class="fee-value">¥{{ returnResult.estimatedRent }}</span>
          </div>
          <div class="fee-item">
            <span class="fee-label">超时分钟</span>
            <span class="fee-value">{{ returnResult.overtimeMinutes || 0 }} 分钟</span>
          </div>
          <div class="fee-item">
            <span class="fee-label">超时费</span>
            <span class="fee-value overtime">¥{{ returnResult.overtimeFee || 0 }}</span>
          </div>
          <div class="fee-item">
            <span class="fee-label">超里程费</span>
            <span class="fee-value">¥{{ returnResult.excessMileageFee || 0 }}</span>
          </div>
          <div class="fee-divider"></div>
          <div class="fee-item total">
            <span class="fee-label">总费用</span>
            <span class="fee-value">¥{{ returnResult.totalFee }}</span>
          </div>
        </div>
        <div v-if="returnResult.overtimeMinutes > 0" class="result-note">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/>
            <line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
          <span>检测到超时还车，已自动扣除用户 5 分信用分。</span>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="resultVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBookings } from '../../api/booking'
import { pickup, returnVehicle } from '../../api/rental'

const activeTab = ref('pickup')
const loadingPickup = ref(false)
const loadingReturn = ref(false)
const submitting = ref(false)

const headerStyle = {
  background: '#faf9ff', color: '#1a1a2e', fontWeight: 600, fontSize: '13px',
}

function formatTime(t) {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit',
  })
}

function isOverdue(row) {
  return row.status === 'overdue' || (row.plannedReturnTime && new Date(row.plannedReturnTime) < new Date())
}

// ===== Pickup =====
const confirmedBookings = ref([])
const pickupVisible = ref(false)
const pickupTarget = ref(null)
const pickupForm = reactive({ pickupMileage: 0 })

async function fetchConfirmedBookings() {
  loadingPickup.value = true
  try {
    const res = await getBookings({ page: 1, size: 100, status: 'confirmed' })
    confirmedBookings.value = res.data?.records || []
  } catch {} finally { loadingPickup.value = false }
}

function onPickupRowClick(row) { openPickup(row) }

function openPickup(row) {
  pickupTarget.value = row
  pickupForm.pickupMileage = row.currentMileage || 0
  pickupVisible.value = true
}

async function submitPickup() {
  submitting.value = true
  try {
    const res = await pickup({ bookingId: pickupTarget.value.id, pickupMileage: pickupForm.pickupMileage })
    ElMessage.success('取车成功')
    pickupVisible.value = false
    fetchConfirmedBookings()
  } catch {} finally { submitting.value = false }
}

// ===== Return =====
const activeBookings = ref([])
const returnVisible = ref(false)
const returnTarget = ref(null)
const returnForm = reactive({ returnMileage: 0 })
const resultVisible = ref(false)
const returnResult = ref(null)

async function fetchActiveBookings() {
  loadingReturn.value = true
  try {
    const [pickedUpRes, overdueRes] = await Promise.all([
      getBookings({ page: 1, size: 100, status: 'picked_up' }),
      getBookings({ page: 1, size: 100, status: 'overdue' }),
    ])
    activeBookings.value = [
      ...(overdueRes.data?.records || []),
      ...(pickedUpRes.data?.records || []),
    ]
  } catch {} finally { loadingReturn.value = false }
}

function onReturnRowClick(row) { openReturn(row) }

function openReturn(row) {
  returnTarget.value = row
  returnForm.returnMileage = 0
  returnVisible.value = true
}

async function submitReturn() {
  if (returnTarget.value.pickupMileage && returnForm.returnMileage < returnTarget.value.pickupMileage) {
    ElMessage.warning(`还车里程不能低于取车里程（${returnTarget.value.pickupMileage}km）`)
    return
  }
  submitting.value = true
  try {
    const res = await returnVehicle({
      bookingId: returnTarget.value.id,
      returnMileage: returnForm.returnMileage,
    })
    returnResult.value = res.data
    returnVisible.value = false
    resultVisible.value = true
    fetchActiveBookings()
  } catch {} finally { submitting.value = false }
}

function onTabChange(tab) {
  if (tab === 'pickup') fetchConfirmedBookings()
  else fetchActiveBookings()
}

onMounted(async () => {
  fetchConfirmedBookings()
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
.rental-management { max-width: 1200px; }

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-title {
  font-family: var(--font-cn);
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.page-desc { font-size: 14px; color: var(--text-secondary); }

/* ===== Table ===== */
.table-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
}

.overdue-text { color: var(--danger); font-weight: 600; }

/* ===== Status Tags ===== */
.status-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
}

.status-picked_up { background: rgba(78, 205, 196, 0.12); color: var(--success); }
.status-overdue { background: rgba(232, 72, 72, 0.1); color: var(--danger); }

/* ===== Action Buttons ===== */
.action-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
}

.pickup-btn {
  background: var(--success);
  color: #fff;
}

.pickup-btn:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(78, 205, 196, 0.35); }

.return-btn {
  background: var(--gradient-main);
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.return-btn:hover { transform: translateY(-1px); box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35); }

/* ===== Info Card ===== */
.info-card {
  padding: 16px;
  background: #faf9ff;
  border-radius: 12px;
  border: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-row label {
  font-size: 13px;
  color: var(--text-muted);
  min-width: 70px;
}

.info-row span {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.money {
  font-family: var(--font-display);
  font-weight: 700 !important;
  color: var(--accent) !important;
}

.pickup-form, .return-form { margin-top: 20px; }

.return-hint {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
  margin-top: 4px;
}

.return-hint svg {
  width: 18px;
  height: 18px;
  color: var(--accent);
  flex-shrink: 0;
  margin-top: 1px;
}

.return-hint span { font-size: 12px; color: var(--text-secondary); line-height: 1.5; }

/* ===== Fee Result ===== */
.fee-result { display: flex; flex-direction: column; gap: 20px; }

.result-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px;
  background: rgba(78, 205, 196, 0.08);
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.result-header svg { width: 24px; height: 24px; flex-shrink: 0; }

.fee-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px;
  background: #faf9ff;
  border-radius: 12px;
  border: 1px solid var(--border);
}

.fee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fee-label { font-size: 14px; color: var(--text-secondary); }
.fee-value { font-size: 15px; font-weight: 600; color: var(--text-primary); }

.fee-value.overtime {
  font-family: var(--font-display);
  color: var(--danger);
}

.fee-divider {
  height: 1px;
  background: var(--border);
  margin: 4px 0;
}

.fee-item.total .fee-label {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.fee-item.total .fee-value {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 700;
  color: var(--purple-end);
}

.result-note {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(232, 72, 72, 0.06);
  border-radius: 8px;
}

.result-note svg {
  width: 18px;
  height: 18px;
  color: var(--danger);
  flex-shrink: 0;
  margin-top: 1px;
}

.result-note span { font-size: 13px; color: var(--danger); line-height: 1.5; }

.scroll-reveal { opacity: 0; transform: translateY(40px); transition: all 0.7s cubic-bezier(0.22,1,0.36,1); transition-delay: var(--delay,0s); }
.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }
</style>

<style>
.rental-management .el-table {
  --el-table-border-color: rgba(102, 126, 234, 0.06);
}

.rental-management .el-table th.el-table__cell { background: #faf9ff; }

.rental-management .el-table .el-table__row:hover > td.el-table__cell {
  background: rgba(102, 126, 234, 0.03);
}

.rental-management .el-table .el-table__row.current-row > td.el-table__cell {
  background: rgba(102, 126, 234, 0.05);
}

.rental-management .el-dialog { border-radius: 16px; }

.rental-management .el-dialog__header {
  border-bottom: 1px solid var(--border);
  padding: 20px 24px;
}

.rental-management .el-dialog__title {
  font-family: var(--font-cn);
  font-weight: 600;
  color: var(--text-primary);
}

.rental-management .el-dialog__body { padding: 24px; }
.rental-management .el-dialog__footer { padding: 16px 24px; border-top: 1px solid var(--border); }

.rental-management .el-button--primary {
  background: var(--gradient-main);
  border: none;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}

.rental-management .el-button--primary:hover {
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
  transform: translateY(-1px);
}

.rental-management .el-input-number .el-input__wrapper {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.12);
}

.rental-management .el-tabs__nav-wrap::after { background-color: var(--border); }

.rental-management .el-tabs__item {
  font-family: var(--font-cn);
  font-weight: 500;
  color: var(--text-secondary);
}

.rental-management .el-tabs__item.is-active { color: var(--accent); }

.rental-management .el-tabs__active-bar { background: var(--gradient-main); }
</style>
