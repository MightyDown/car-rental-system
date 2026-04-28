<template>
  <div class="booking-management">
    <div class="page-header scroll-reveal">
      <div>
        <h1 class="page-title">预订管理</h1>
        <p class="page-desc">管理所有租车预订，确认或取消预订申请</p>
      </div>
    </div>

    <div class="status-tabs">
      <button class="status-tab" :class="{ active: filters.status === '' }" @click="setStatus('')">
        全部
        <span class="tab-count">{{ statusCounts.all }}</span>
      </button>
      <button class="status-tab" :class="{ active: filters.status === 'pending' }" @click="setStatus('pending')">
        待确认
        <span class="tab-count pending">{{ statusCounts.pending }}</span>
      </button>
      <button class="status-tab" :class="{ active: filters.status === 'confirmed' }" @click="setStatus('confirmed')">
        已确认
      </button>
      <button class="status-tab" :class="{ active: filters.status === 'picked_up' }" @click="setStatus('picked_up')">
        已取车
      </button>
      <button class="status-tab" :class="{ active: filters.status === 'overdue' }" @click="setStatus('overdue')">
        超时
      </button>
      <button class="status-tab" :class="{ active: filters.status === 'returned' }" @click="setStatus('returned')">
        已还车
      </button>
      <button class="status-tab" :class="{ active: filters.status === 'cancelled' }" @click="setStatus('cancelled')">
        已取消
      </button>
    </div>

    <div class="table-card">
      <el-table :data="bookings" v-loading="loading" stripe empty-text="暂无预订记录"
                style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column prop="bookingNo" label="预订编号" min-width="150" />
        <el-table-column label="用户" min-width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <span class="user-name">{{ row.userRealName || row.userName || '-' }}</span>
              <span class="user-id">ID:{{ row.userId }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="车辆" min-width="140">
          <template #default="{ row }">
            <div class="vehicle-cell">
              <span class="vehicle-plate">{{ row.vehiclePlateNo }}</span>
              <span class="vehicle-model">{{ row.vehicleModel }} · {{ row.vehicleTypeName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="计划取车" min-width="150">
          <template #default="{ row }">
            {{ formatTime(row.plannedPickupTime) }}
          </template>
        </el-table-column>
        <el-table-column label="计划还车" min-width="150">
          <template #default="{ row }">
            {{ formatTime(row.plannedReturnTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="plannedDays" label="天数" width="65" align="center" />
        <el-table-column label="预计租金" width="100" align="center">
          <template #default="{ row }">
            ¥{{ row.estimatedRent }}
          </template>
        </el-table-column>
        <el-table-column label="总费用" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.totalFee !== null" class="total-fee">¥{{ row.totalFee }}</span>
            <span v-else class="no-fee">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="90" align="center">
          <template #default="{ row }">
            <span class="status-tag" :class="'status-' + row.status">{{ row.statusName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <button v-if="row.status === 'pending'" class="action-btn confirm" @click="handleConfirm(row)">
                确认
              </button>
              <button v-if="row.status === 'pending' || row.status === 'confirmed'"
                      class="action-btn cancel" @click="handleCancel(row)">
                取消
              </button>
              <span v-if="row.status !== 'pending' && row.status !== 'confirmed'" class="no-action">-</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchBookings"
        @current-change="fetchBookings"
      />
    </div>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="预订详情" width="600px" destroy-on-close>
      <div v-if="detail" class="booking-detail">
        <div class="detail-section">
          <h4 class="detail-title">基本信息</h4>
          <div class="detail-grid">
            <div class="detail-item"><label>预订编号</label><span>{{ detail.bookingNo }}</span></div>
            <div class="detail-item"><label>状态</label>
              <span class="status-tag" :class="'status-' + detail.status">{{ detail.statusName }}</span>
            </div>
            <div class="detail-item"><label>用户</label><span>{{ detail.userRealName || detail.userName }}</span></div>
            <div class="detail-item"><label>车辆</label><span>{{ detail.vehiclePlateNo }} · {{ detail.vehicleModel }}</span></div>
            <div class="detail-item"><label>计划取车</label><span>{{ formatTime(detail.plannedPickupTime) }}</span></div>
            <div class="detail-item"><label>计划还车</label><span>{{ formatTime(detail.plannedReturnTime) }}</span></div>
            <div class="detail-item"><label>计划天数</label><span>{{ detail.plannedDays }} 天</span></div>
            <div class="detail-item"><label>预计租金</label><span class="money">¥{{ detail.estimatedRent }}</span></div>
          </div>
        </div>
        <div v-if="detail.actualPickupTime" class="detail-section">
          <h4 class="detail-title">取还车记录</h4>
          <div class="detail-grid">
            <div class="detail-item"><label>实际取车</label><span>{{ formatTime(detail.actualPickupTime) }}</span></div>
            <div class="detail-item"><label>取车里程</label><span>{{ detail.pickupMileage }} km</span></div>
            <div class="detail-item"><label>实际还车</label><span>{{ formatTime(detail.actualReturnTime) || '-' }}</span></div>
            <div class="detail-item"><label>还车里程</label><span>{{ detail.returnMileage !== null ? detail.returnMileage + ' km' : '-' }}</span></div>
          </div>
        </div>
        <div v-if="detail.totalFee !== null" class="detail-section">
          <h4 class="detail-title">费用明细</h4>
          <div class="detail-grid">
            <div class="detail-item"><label>超时分钟</label><span>{{ detail.overtimeMinutes || 0 }} 分钟</span></div>
            <div class="detail-item"><label>超时费</label><span class="money">¥{{ detail.overtimeFee || 0 }}</span></div>
            <div class="detail-item"><label>超里程费</label><span class="money">¥{{ detail.excessMileageFee || 0 }}</span></div>
            <div class="detail-item"><label>总费用</label><span class="money total">¥{{ detail.totalFee }}</span></div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookings, confirmBooking, cancelBooking } from '../../api/booking'

const loading = ref(false)
const bookings = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ status: '' })
const statusCounts = reactive({ all: 0, pending: 0 })

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

function setStatus(s) {
  filters.status = s
  pagination.page = 1
  fetchBookings()
}

async function fetchBookings() {
  loading.value = true
  try {
    const res = await getBookings({
      page: pagination.page,
      size: pagination.size,
      status: filters.status || undefined,
    })
    bookings.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch {} finally { loading.value = false }
}

async function fetchPendingCount() {
  try {
    const res = await getBookings({ page: 1, size: 1, status: 'pending' })
    statusCounts.pending = res.data?.total || 0
    const all = await getBookings({ page: 1, size: 1 })
    statusCounts.all = all.data?.total || 0
  } catch {}
}

async function handleConfirm(row) {
  try {
    await ElMessageBox.confirm(
      `确认预订「${row.bookingNo}」？确认后客户可到店取车。`,
      '确认预订',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
    )
  } catch { return }
  try {
    await confirmBooking(row.id)
    ElMessage.success('预订已确认')
    fetchBookings()
    fetchPendingCount()
  } catch {}
}

async function handleCancel(row) {
  try {
    await ElMessageBox.confirm(
      `确定取消预订「${row.bookingNo}」？车辆将恢复空闲状态。`,
      '取消预订',
      { confirmButtonText: '确定取消', cancelButtonText: '返回', type: 'warning' }
    )
  } catch { return }
  try {
    await cancelBooking(row.id)
    ElMessage.success('预订已取消')
    fetchBookings()
    fetchPendingCount()
  } catch {}
}

// Detail dialog
const detailVisible = ref(false)
const detail = ref(null)

onMounted(async () => {
  fetchBookings()
  fetchPendingCount()
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
.booking-management { max-width: 1200px; }

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

/* ===== Status Tabs ===== */
.status-tabs {
  display: flex;
  gap: 6px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.status-tab {
  padding: 8px 18px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 100px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-tab:hover {
  border-color: rgba(102, 126, 234, 0.3);
  color: var(--accent);
}

.status-tab.active {
  background: var(--gradient-main);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.tab-count {
  font-family: var(--font-display);
  font-size: 11px;
  padding: 1px 7px;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 100px;
}

.status-tab.active .tab-count {
  background: rgba(255, 255, 255, 0.25);
}

/* ===== Table ===== */
.table-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
}

.user-cell, .vehicle-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name, .vehicle-plate {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.user-id, .vehicle-model {
  font-size: 11px;
  color: var(--text-muted);
}

.total-fee {
  font-family: var(--font-display);
  font-weight: 700;
  color: var(--accent);
}

.no-fee { color: var(--text-muted); }

/* ===== Status Tags ===== */
.status-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending { background: rgba(230, 162, 60, 0.1); color: #e6a23c; }
.status-confirmed { background: rgba(102, 126, 234, 0.12); color: var(--accent); }
.status-picked_up { background: rgba(78, 205, 196, 0.12); color: var(--success); }
.status-overdue { background: rgba(232, 72, 72, 0.1); color: var(--danger); }
.status-returned { background: rgba(118, 75, 162, 0.1); color: var(--purple-end); }
.status-cancelled { background: rgba(138, 138, 170, 0.1); color: var(--text-muted); }

/* ===== Actions ===== */
.actions { display: flex; gap: 6px; justify-content: center; }

.action-btn {
  padding: 4px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  background: #fff;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
}

.action-btn.confirm { color: var(--accent); }
.action-btn.confirm:hover { background: rgba(102, 126, 234, 0.06); border-color: var(--accent); }

.action-btn.cancel { color: var(--danger); }
.action-btn.cancel:hover { background: rgba(232, 72, 72, 0.06); border-color: var(--danger); }

.no-action { font-size: 12px; color: var(--text-muted); }

/* ===== Pagination ===== */
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }

/* ===== Detail Dialog ===== */
.booking-detail { display: flex; flex-direction: column; gap: 20px; }

.detail-section {
  padding: 16px;
  background: #faf9ff;
  border-radius: 12px;
  border: 1px solid var(--border);
}

.detail-title {
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border);
}

.detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }

.detail-item { display: flex; flex-direction: column; gap: 2px; }

.detail-item label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.detail-item span {
  font-size: 14px;
  color: var(--text-primary);
  font-weight: 500;
}

.money {
  font-family: var(--font-display);
  font-weight: 600 !important;
  color: var(--accent) !important;
}

.money.total {
  font-size: 18px !important;
  color: var(--purple-end) !important;
}

.scroll-reveal { opacity: 0; transform: translateY(40px); transition: all 0.7s cubic-bezier(0.22,1,0.36,1); transition-delay: var(--delay,0s); }
.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }
</style>

<style>
.booking-management .el-table {
  --el-table-border-color: rgba(102, 126, 234, 0.06);
}

.booking-management .el-table th.el-table__cell { background: #faf9ff; }

.booking-management .el-table .el-table__row:hover > td.el-table__cell {
  background: rgba(102, 126, 234, 0.03);
}

.booking-management .el-pagination .el-pager li.is-active {
  background: var(--gradient-main);
  border-radius: 6px;
  color: #fff;
}

.booking-management .el-dialog { border-radius: 16px; }

.booking-management .el-dialog__header {
  border-bottom: 1px solid var(--border);
  padding: 20px 24px;
}

.booking-management .el-dialog__title {
  font-family: var(--font-cn);
  font-weight: 600;
  color: var(--text-primary);
}

.booking-management .el-dialog__body { padding: 24px; }
.booking-management .el-dialog__footer { padding: 16px 24px; border-top: 1px solid var(--border); }

.booking-management .el-button--primary {
  background: var(--gradient-main);
  border: none;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}
</style>
