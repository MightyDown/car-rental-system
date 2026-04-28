<template>
  <div class="my-bookings-page">
    <!-- Navigation -->
    <AppNav transparent />

    <!-- Page Hero -->
    <section class="page-hero">
      <div class="page-hero-inner">
        <div class="page-header">
          <div>
            <h1 class="page-title">我的预订</h1>
            <p class="page-desc">查看和管理您的租车预订</p>
          </div>
          <button class="btn-primary" @click="openCreate">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
            创建预订
          </button>
        </div>

        <div class="status-tabs">
          <button class="status-tab" :class="{ active: filters.status === '' }" @click="setStatus('')">全部</button>
          <button class="status-tab" :class="{ active: filters.status === 'pending' }" @click="setStatus('pending')">待确认</button>
          <button class="status-tab" :class="{ active: filters.status === 'confirmed' }" @click="setStatus('confirmed')">已确认</button>
          <button class="status-tab" :class="{ active: filters.status === 'picked_up' }" @click="setStatus('picked_up')">已取车</button>
          <button class="status-tab" :class="{ active: filters.status === 'completed' || filters.status === 'cancelled' }" @click="setStatus('completed,cancelled')">已完成</button>
        </div>
      </div>
    </section>

    <!-- Content -->
    <div class="page-content">
      <div class="bookings-list">
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <span>加载中...</span>
        </div>

        <template v-else-if="bookings.length > 0">
          <div class="booking-card" v-for="b in bookings" :key="b.id">
            <div class="card-header">
              <div class="card-ref">
                <span class="ref-no">{{ b.bookingNo }}</span>
                <span class="status-tag" :class="'status-' + b.status">{{ b.statusName }}</span>
              </div>
              <div class="card-actions">
                <button v-if="b.status === 'pending' || b.status === 'confirmed'"
                        class="action-btn cancel" @click="handleCancel(b)">取消预订</button>
              </div>
            </div>

            <div class="card-body">
              <div class="card-vehicle">
                <div class="vehicle-visual">
                  <svg viewBox="0 0 64 32" fill="none">
                    <path d="M8 22 L14 12 L24 8 L40 8 L50 12 L56 22" stroke="currentColor" stroke-width="1.5" fill="none"/>
                    <line x1="6" y1="22" x2="58" y2="22" stroke="currentColor" stroke-width="1.5"/>
                    <circle cx="16" cy="22" r="4" stroke="currentColor" stroke-width="1.5" fill="none"/>
                    <circle cx="48" cy="22" r="4" stroke="currentColor" stroke-width="1.5" fill="none"/>
                  </svg>
                </div>
                <div>
                  <h3 class="vehicle-name">{{ b.vehicleModel }}</h3>
                  <span class="vehicle-plate">{{ b.vehiclePlateNo }}</span>
                </div>
              </div>

              <div class="card-details">
                <div class="detail-col">
                  <div class="detail-item">
                    <label>计划取车</label>
                    <span>{{ formatTime(b.plannedPickupTime) }}</span>
                  </div>
                  <div class="detail-item">
                    <label>实际取车</label>
                    <span>{{ b.actualPickupTime ? formatTime(b.actualPickupTime) : '-' }}</span>
                  </div>
                </div>
                <div class="detail-divider"></div>
                <div class="detail-col">
                  <div class="detail-item">
                    <label>计划还车</label>
                    <span>{{ formatTime(b.plannedReturnTime) }}</span>
                  </div>
                  <div class="detail-item">
                    <label>实际还车</label>
                    <span>{{ b.actualReturnTime ? formatTime(b.actualReturnTime) : '-' }}</span>
                  </div>
                </div>
                <div class="detail-divider"></div>
                <div class="detail-col">
                  <div class="detail-item">
                    <label>预计租金</label>
                    <span class="fee-value">{{ b.estimatedRent ? '¥' + b.estimatedRent : '-' }}</span>
                  </div>
                  <div class="detail-item">
                    <label>实际费用</label>
                    <span class="fee-value" :class="{ highlight: b.totalFee }">{{ b.totalFee ? '¥' + b.totalFee : '-' }}</span>
                  </div>
                </div>
              </div>

              <div class="card-extra" v-if="b.totalFee">
                <span v-if="b.overtimeFee">超时费 ¥{{ b.overtimeFee }} · </span>
                <span v-if="b.excessMileageFee">超里程费 ¥{{ b.excessMileageFee }}</span>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="empty-state">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <rect x="3" y="4" width="18" height="16" rx="2"/>
            <path d="M16 2v4M8 2v4M3 10h18"/>
          </svg>
          <p>暂无预订记录</p>
          <button class="btn-primary" @click="openCreate">立即预订</button>
        </div>
      </div>

      <div class="pagination-wrap" v-if="pagination.total > pagination.size">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchBookings"
          @current-change="fetchBookings"
        />
      </div>
    </div>

    <!-- Create Booking Dialog -->
    <el-dialog v-model="createVisible" title="创建预订" width="500px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="createForm" label-width="100px" label-position="left">
        <el-form-item label="选择车辆">
          <el-select v-model="createForm.vehicleId" filterable placeholder="请选择车辆"
                     style="width: 100%" @change="onVehicleSelect">
            <el-option v-for="v in availableVehicles" :key="v.id"
                       :label="v.model + ' · ' + v.plateNo + ' (日租¥' + v.dailyRate + ')'"
                       :value="v.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="取车时间" required>
          <el-date-picker v-model="createForm.plannedPickupTime" type="datetime"
                          placeholder="选择取车时间" style="width: 100%"
                          value-format="YYYY-MM-DD HH:mm:ss"
                          :disabled-date="d => d.getTime() < Date.now() - 86400000" />
        </el-form-item>
        <el-form-item label="还车时间" required>
          <el-date-picker v-model="createForm.plannedReturnTime" type="datetime"
                          placeholder="选择还车时间" style="width: 100%"
                          value-format="YYYY-MM-DD HH:mm:ss"
                          :disabled-date="d => d.getTime() < Date.now() - 86400000" />
        </el-form-item>
        <el-form-item label="信息确认" v-if="createForm.vehicleId">
          <div class="confirm-info">
            <div class="confirm-row"><label>车辆</label><span>{{ selectedVehicle?.model }} · {{ selectedVehicle?.plateNo }}</span></div>
            <div class="confirm-row"><label>类型</label><span>{{ selectedVehicle?.typeName }}</span></div>
            <div class="confirm-row"><label>日租金</label><span class="fee">¥{{ selectedVehicle?.dailyRate }}/天</span></div>
            <div class="confirm-row"><label>免里程</label><span>{{ selectedVehicle?.dailyFreeMileage }}km/天</span></div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate" :loading="submitting">确认预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBookings, createBooking, cancelBooking } from '../api/booking'
import { getVehicles } from '../api/vehicle'
import AppNav from '../components/AppNav.vue'

const router = useRouter()
const route = useRoute()

// ===== Bookings List =====
const loading = ref(false)
const bookings = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ status: '' })

function setStatus(s) {
  filters.status = s
  pagination.page = 1
  fetchBookings()
}

async function fetchBookings() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.size }
    if (filters.status) params.status = filters.status
    const res = await getMyBookings(params)
    bookings.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) { console.error('获取预订列表失败:', e) } finally { loading.value = false }
}

function formatTime(t) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 16)
}

// ===== Create =====
const createVisible = ref(false)
const availableVehicles = ref([])
const submitting = ref(false)
const createForm = reactive({ vehicleId: null, plannedPickupTime: '', plannedReturnTime: '' })
const selectedVehicle = computed(() => availableVehicles.value.find(v => v.id === createForm.vehicleId))

async function openCreate() {
  createForm.vehicleId = null
  createForm.plannedPickupTime = ''
  createForm.plannedReturnTime = ''
  try {
    const res = await getVehicles({ page: 1, size: 50, status: 'idle' })
    availableVehicles.value = res.data?.records || []
  } catch (e) { console.error('获取可用车辆失败:', e) }
  createVisible.value = true
}

function onVehicleSelect() {}

async function submitCreate() {
  if (!createForm.vehicleId) return ElMessage.warning('请选择车辆')
  if (!createForm.plannedPickupTime) return ElMessage.warning('请选择取车时间')
  if (!createForm.plannedReturnTime) return ElMessage.warning('请选择还车时间')

  // value-format="YYYY-MM-DD HH:mm:ss" 返回字符串，可直接字典序比较
  if (createForm.plannedReturnTime <= createForm.plannedPickupTime)
    return ElMessage.warning('还车时间必须晚于取车时间')

  submitting.value = true
  try {
    await createBooking({
      vehicleId: createForm.vehicleId,
      plannedPickupTime: createForm.plannedPickupTime,
      plannedReturnTime: createForm.plannedReturnTime,
    })
    ElMessage.success('预订已提交，等待工作人员确认')
    createVisible.value = false
    fetchBookings()
  } catch (e) { console.error('创建预订失败:', e) } finally { submitting.value = false }
}

// ===== Cancel =====
async function handleCancel(row) {
  try {
    await ElMessageBox.confirm(
      `确认取消预订「${row.bookingNo}」？`,
      '取消预订', { confirmButtonText: '确定', cancelButtonText: '返回', type: 'warning' }
    )
  } catch { return }
  try {
    await cancelBooking(row.id)
    ElMessage.success('预订已取消')
    fetchBookings()
  } catch (e) { console.error('取消预订失败:', e) }
}

onMounted(() => {
  fetchBookings()
  if (route.query.create === '1') {
    openCreate()
    router.replace({ path: '/my-bookings', query: {} })
  }
})
</script>

<style scoped>
/* ===== Color System ===== */
:root {
  --purple-start: #667eea;
  --purple-end: #764ba2;
  --gradient-main: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.my-bookings-page {
  min-height: 100vh;
  background: #faf9ff;
}

/* ===== Page Hero ===== */
.page-hero {
  padding: 120px 40px 32px;
  background: linear-gradient(160deg, #f0f0ff 0%, #f8f6ff 30%, #fef8ff 60%, #f5f0ff 100%);
}

.page-hero-inner {
  max-width: 960px;
  margin: 0 auto;
}

/* ===== Page Content ===== */
.page-content {
  max-width: 960px;
  margin: 0 auto;
  padding: 32px 24px 64px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.page-title {
  font-family: var(--font-cn);
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.page-desc { font-size: 14px; color: #6a6a8a; }

.btn-primary {
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
  display: inline-flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}

.btn-primary svg { width: 18px; height: 18px; }

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
}

/* ===== Status Tabs ===== */
.status-tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.status-tab {
  padding: 8px 18px;
  background: #fff;
  border: 1px solid rgba(102, 126, 234, 0.08);
  border-radius: 100px;
  font-size: 13px;
  font-weight: 500;
  color: #5a5a7a;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
}

.status-tab:hover { border-color: rgba(102, 126, 234, 0.3); color: var(--purple-start); }

.status-tab.active {
  background: var(--gradient-main);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

/* ===== Booking Cards ===== */
.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.booking-card {
  background: #fff;
  border: 1px solid rgba(102, 126, 234, 0.08);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.booking-card:hover {
  border-color: rgba(102, 126, 234, 0.15);
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.06);
}

.card-ref { display: flex; align-items: center; gap: 12px; }

.ref-no {
  font-family: var(--font-number);
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
}

/* Status Tags */
.status-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending { background: rgba(230, 162, 60, 0.1); color: #e6a23c; }
.status-confirmed { background: rgba(102, 126, 234, 0.1); color: var(--purple-start); }
.status-picked_up { background: rgba(78, 205, 196, 0.12); color: #4ecdc4; }
.status-completed { background: rgba(103, 194, 58, 0.1); color: #67c23a; }
.status-cancelled { background: rgba(144, 147, 153, 0.1); color: #909399; }
.status-overdue { background: rgba(232, 72, 72, 0.08); color: #e84848; }

/* Card Body */
.card-body { padding: 20px 24px; }

.card-vehicle {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.vehicle-visual {
  width: 64px;
  height: 32px;
  color: var(--purple-start);
  flex-shrink: 0;
}

.vehicle-visual svg { width: 64px; height: 32px; }

.vehicle-name {
  font-family: var(--font-cn);
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}

.vehicle-plate {
  font-size: 13px;
  color: #8a8aaa;
}

.card-details {
  display: flex;
  gap: 24px;
  padding: 16px;
  background: #faf9ff;
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.detail-col { flex: 1; display: flex; flex-direction: column; gap: 10px; }

.detail-item { display: flex; flex-direction: column; gap: 2px; }
.detail-item label { font-size: 11px; color: #8a8aaa; }
.detail-item span { font-size: 14px; font-weight: 500; color: #1a1a2e; }

.fee-value { font-family: var(--font-number); }
.fee-value.highlight { color: var(--purple-start); }

.detail-divider {
  width: 1px;
  background: rgba(102, 126, 234, 0.08);
}

.card-extra {
  margin-top: 12px;
  font-size: 13px;
  color: #6a6a8a;
}

/* Action Buttons */
.action-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
}

.action-btn.cancel {
  background: #fff;
  color: #e84848;
  border: 1px solid rgba(232, 72, 72, 0.25);
}

.action-btn.cancel:hover {
  background: #e84848;
  color: #fff;
}

/* ===== Empty / Loading ===== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 80px 0;
  color: #8a8aaa;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid rgba(102, 126, 234, 0.1);
  border-top-color: var(--purple-start);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 80px 0;
  color: #8a8aaa;
}

.empty-state svg { width: 48px; height: 48px; }
.empty-state p { font-size: 15px; }

/* ===== Confirm Info ===== */
.confirm-info {
  padding: 16px;
  background: #faf9ff;
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.06);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.confirm-row { display: flex; gap: 12px; align-items: center; }
.confirm-row label { font-size: 12px; color: #8a8aaa; min-width: 55px; }
.confirm-row span { font-size: 14px; font-weight: 500; color: #1a1a2e; }
.confirm-row span.fee { font-family: var(--font-number); color: var(--purple-start); }

/* ===== Pagination ===== */
.pagination-wrap { margin-top: 24px; display: flex; justify-content: flex-end; }

@media (max-width: 600px) {
  .page-hero { padding: 100px 20px 24px; }
  .page-content { padding: 24px 16px 48px; }
  .card-details { flex-direction: column; }
  .detail-divider { width: 100%; height: 1px; }
}
</style>

<style>
.my-bookings-page .el-dialog { border-radius: 16px; }
.my-bookings-page .el-dialog__header { border-bottom: 1px solid rgba(102, 126, 234, 0.08); padding: 20px 24px; }
.my-bookings-page .el-dialog__title { font-family: var(--font-cn); font-weight: 600; color: #1a1a2e; }
.my-bookings-page .el-dialog__body { padding: 24px; }
.my-bookings-page .el-dialog__footer { padding: 16px 24px; border-top: 1px solid rgba(102, 126, 234, 0.08); }
.my-bookings-page .el-button--primary {
  background: var(--gradient-main);
  border: none;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}
.my-bookings-page .el-button--primary:hover {
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
  transform: translateY(-1px);
}
.my-bookings-page .el-input__wrapper,
.my-bookings-page .el-select .el-input__wrapper {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.12);
}
.my-bookings-page .el-input.is-focus .el-input__wrapper,
.my-bookings-page .el-select .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px #667eea, 0 0 0 3px rgba(102, 126, 234, 0.08);
}
.my-bookings-page .el-pagination .el-pager li.is-active {
  background: var(--gradient-main);
  border-radius: 6px;
  color: #fff;
}
</style>
