<template>
  <div class="accident-management">
    <div class="page-header scroll-reveal">
      <div>
        <h1 class="page-title">事故管理</h1>
        <p class="page-desc">登记事故、扣减信用分、跟踪维修进度</p>
      </div>
      <button class="btn-primary" @click="openCreate">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
        登记事故
      </button>
    </div>

    <div class="status-tabs">
      <button class="status-tab" :class="{ active: filters.status === '' }" @click="setStatus('')">全部</button>
      <button class="status-tab" :class="{ active: filters.status === 'pending' }" @click="setStatus('pending')">待处理</button>
      <button class="status-tab" :class="{ active: filters.status === 'in_progress' }" @click="setStatus('in_progress')">处理中</button>
      <button class="status-tab" :class="{ active: filters.status === 'completed' }" @click="setStatus('completed')">已完成</button>
    </div>

    <div class="table-card">
      <el-table :data="accidents" v-loading="loading" stripe empty-text="暂无事故记录"
                style="width: 100%" :header-cell-style="headerStyle">
        <el-table-column prop="id" label="ID" width="65" align="center" />
        <el-table-column label="关联预订" min-width="150">
          <template #default="{ row }">
            <div class="ref-cell">
              <span class="ref-primary">{{ row.bookingNo || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="用户" min-width="100">
          <template #default="{ row }">{{ row.userRealName || row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column label="车辆" min-width="130">
          <template #default="{ row }">{{ row.vehiclePlateNo }} · {{ row.vehicleModel }}</template>
        </el-table-column>
        <el-table-column label="事故描述" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ row.description }}</template>
        </el-table-column>
        <el-table-column label="扣分" width="70" align="center">
          <template #default="{ row }">
            <span v-if="row.deductionPoints !== null" class="deduction">-{{ row.deductionPoints }}</span>
            <span v-else class="no-val">-</span>
          </template>
        </el-table-column>
        <el-table-column label="预计完成" width="110" align="center">
          <template #default="{ row }">{{ row.expectedCompletionDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="实际完成" width="110" align="center">
          <template #default="{ row }">{{ row.actualCompletionDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="85" align="center">
          <template #default="{ row }">
            <span class="status-tag" :class="'status-' + row.status">{{ row.statusName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <button v-if="row.status === 'pending'" class="action-btn process"
                      @click="openProcess(row)">处理</button>
              <button v-if="row.status === 'in_progress'" class="action-btn complete"
                      @click="handleComplete(row)">维修完成</button>
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
        @size-change="fetchAccidents"
        @current-change="fetchAccidents"
      />
    </div>

    <!-- ===== 登记事故弹窗 ===== -->
    <el-dialog v-model="createVisible" title="登记事故" width="520px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="createForm" label-width="90px" label-position="left">
        <el-form-item label="关联预订">
          <el-select v-model="createForm.bookingId" filterable placeholder="选择预订编号"
                     style="width: 100%" @change="onBookingSelect">
            <el-option v-for="b in returnedBookings" :key="b.id"
                       :label="b.bookingNo + ' — ' + (b.userRealName || b.userName) + ' · ' + b.vehiclePlateNo"
                       :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆">
          <el-input :model-value="createForm.vehicleInfo" disabled placeholder="选择预订后自动关联" />
        </el-form-item>
        <el-form-item label="事故描述" required>
          <el-input v-model="createForm.description" type="textarea" :rows="3"
                    maxlength="500" show-word-limit placeholder="请详细描述事故情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate" :loading="submitting">确认登记</el-button>
      </template>
    </el-dialog>

    <!-- ===== 处理事故弹窗 ===== -->
    <el-dialog v-model="processVisible" title="处理事故" width="460px" destroy-on-close :close-on-click-modal="false">
      <div v-if="processTarget" class="process-info">
        <div class="info-card">
          <div class="info-row"><label>事故ID</label><span>#{{ processTarget.id }}</span></div>
          <div class="info-row"><label>车辆</label><span>{{ processTarget.vehiclePlateNo }} · {{ processTarget.vehicleModel }}</span></div>
          <div class="info-row"><label>描述</label><span>{{ processTarget.description }}</span></div>
        </div>
        <el-form :model="processForm" label-width="120px" label-position="left" class="process-form">
          <el-form-item label="扣分值" required>
            <el-input-number v-model="processForm.deductionPoints" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="预计完成日期" required>
            <el-date-picker v-model="processForm.expectedCompletionDate" type="date"
                            placeholder="选择日期" style="width: 100%"
                            :disabled-date="d => d.getTime() < Date.now() - 86400000" />
          </el-form-item>
        </el-form>
        <div class="process-hint">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/>
          </svg>
          <span>扣分值将从用户信用分中扣除（最低扣至0分），同时车辆设为维修中状态。</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProcess" :loading="submitting">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAccidents, createAccident, processAccident, completeAccident } from '../../api/accident'
import { getBookings } from '../../api/booking'

const loading = ref(false)
const accidents = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ status: '' })

const headerStyle = {
  background: '#faf9ff', color: '#1a1a2e', fontWeight: 600, fontSize: '13px',
}

function setStatus(s) {
  filters.status = s
  pagination.page = 1
  fetchAccidents()
}

async function fetchAccidents() {
  loading.value = true
  try {
    const res = await getAccidents({
      page: pagination.page,
      size: pagination.size,
      status: filters.status || undefined,
    })
    accidents.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) { console.error('获取事故列表失败:', e) } finally { loading.value = false }
}

// ===== Create =====
const createVisible = ref(false)
const returnedBookings = ref([])
const submitting = ref(false)
const createForm = reactive({ bookingId: null, vehicleId: null, vehicleInfo: '', description: '' })

async function openCreate() {
  createForm.bookingId = null
  createForm.vehicleId = null
  createForm.vehicleInfo = ''
  createForm.description = ''
  try {
    const res = await getBookings({ page: 1, size: 200 })
    returnedBookings.value = res.data?.records || []
  } catch (e) {
    console.error('获取预订列表失败:', e)
  }
  createVisible.value = true
}

function onBookingSelect(val) {
  const b = returnedBookings.value.find(r => r.id === val)
  if (b) {
    createForm.vehicleId = b.vehicleId
    createForm.vehicleInfo = `${b.vehiclePlateNo} · ${b.vehicleModel}`
  }
}

async function submitCreate() {
  if (!createForm.bookingId) return ElMessage.warning('请选择关联预订')
  if (!createForm.description.trim()) return ElMessage.warning('请输入事故描述')
  submitting.value = true
  try {
    await createAccident({
      bookingId: createForm.bookingId,
      vehicleId: createForm.vehicleId,
      description: createForm.description.trim(),
    })
    ElMessage.success('事故已登记，车辆状态已更新为维修中')
    createVisible.value = false
    fetchAccidents()
  } catch (e) { console.error('登记事故失败:', e) } finally { submitting.value = false }
}

// ===== Process =====
const processVisible = ref(false)
const processTarget = ref(null)
const processForm = reactive({ deductionPoints: 10, expectedCompletionDate: '' })

function openProcess(row) {
  processTarget.value = row
  processForm.deductionPoints = 10
  processForm.expectedCompletionDate = ''
  processVisible.value = true
}

async function submitProcess() {
  if (!processForm.deductionPoints && processForm.deductionPoints !== 0)
    return ElMessage.warning('请输入扣分值')
  if (!processForm.expectedCompletionDate) return ElMessage.warning('请选择预计完成日期')
  submitting.value = true
  try {
    await processAccident(processTarget.value.id, {
      deductionPoints: processForm.deductionPoints,
      expectedCompletionDate: processForm.expectedCompletionDate,
    })
    ElMessage.success('事故已处理，信用分已扣减')
    processVisible.value = false
    fetchAccidents()
  } catch (e) { console.error('处理事故失败:', e) } finally { submitting.value = false }
}

// ===== Complete =====
async function handleComplete(row) {
  try {
    await ElMessageBox.confirm(
      `确认维修已完成？车辆「${row.vehiclePlateNo}」将恢复为空闲状态。`,
      '维修完成', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'info' }
    )
  } catch { return }
  try {
    await completeAccident(row.id)
    ElMessage.success('维修已完成，车辆已恢复空闲')
    fetchAccidents()
  } catch (e) { console.error('完成维修失败:', e) }
}

onMounted(async () => {
  fetchAccidents()
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
.accident-management { max-width: 1200px; }

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

/* ===== Button + Tags ===== */
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
}

.status-tab:hover { border-color: rgba(102, 126, 234, 0.3); color: var(--accent); }

.status-tab.active {
  background: var(--gradient-main);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

/* ===== Table ===== */
.table-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
}

.ref-cell { display: flex; flex-direction: column; }
.ref-primary { font-size: 13px; font-weight: 500; color: var(--text-primary); }

.deduction {
  font-family: var(--font-display);
  font-weight: 700;
  color: var(--danger);
}

.no-val { color: var(--text-muted); }

/* ===== Status Tags ===== */
.status-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending { background: rgba(230, 162, 60, 0.1); color: #e6a23c; }
.status-in_progress { background: rgba(102, 126, 234, 0.12); color: var(--accent); }
.status-completed { background: rgba(78, 205, 196, 0.12); color: var(--success); }

/* ===== Actions ===== */
.actions { display: flex; gap: 6px; justify-content: center; }

.action-btn {
  padding: 5px 14px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
  font-family: var(--font-cn);
}

.action-btn.process {
  background: var(--accent);
  color: #fff;
}

.action-btn.process:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3); }

.action-btn.complete {
  background: var(--success);
  color: #fff;
}

.action-btn.complete:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(78, 205, 196, 0.3); }

/* ===== Info Card ===== */
.info-card {
  padding: 14px;
  background: #faf9ff;
  border-radius: 12px;
  border: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.info-row { display: flex; gap: 12px; align-items: flex-start; }
.info-row label { font-size: 12px; color: var(--text-muted); min-width: 55px; flex-shrink: 0; }
.info-row span { font-size: 14px; color: var(--text-primary); font-weight: 500; }

.process-form { margin-top: 4px; }

.process-hint {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px;
  background: rgba(232, 72, 72, 0.05);
  border-radius: 8px;
  margin-top: 8px;
}

.process-hint svg {
  width: 18px; height: 18px;
  color: var(--danger);
  flex-shrink: 0;
  margin-top: 1px;
}

.process-hint span { font-size: 12px; color: var(--text-secondary); line-height: 1.5; }

/* ===== Pagination ===== */
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }

.scroll-reveal { opacity: 0; transform: translateY(40px); transition: all 0.7s cubic-bezier(0.22,1,0.36,1); transition-delay: var(--delay,0s); }
.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }
</style>

<style>
.accident-management .el-table {
  --el-table-border-color: rgba(102, 126, 234, 0.06);
}

.accident-management .el-table th.el-table__cell { background: #faf9ff; }

.accident-management .el-table .el-table__row:hover > td.el-table__cell {
  background: rgba(102, 126, 234, 0.03);
}

.accident-management .el-pagination .el-pager li.is-active {
  background: var(--gradient-main);
  border-radius: 6px;
  color: #fff;
}

.accident-management .el-dialog { border-radius: 16px; }

.accident-management .el-dialog__header {
  border-bottom: 1px solid var(--border);
  padding: 20px 24px;
}

.accident-management .el-dialog__title {
  font-family: var(--font-cn);
  font-weight: 600;
  color: var(--text-primary);
}

.accident-management .el-dialog__body { padding: 24px; }
.accident-management .el-dialog__footer { padding: 16px 24px; border-top: 1px solid var(--border); }

.accident-management .el-button--primary {
  background: var(--gradient-main);
  border: none;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}

.accident-management .el-button--primary:hover {
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
  transform: translateY(-1px);
}

.accident-management .el-input__wrapper,
.accident-management .el-select .el-input__wrapper,
.accident-management .el-input-number .el-input__wrapper,
.accident-management .el-textarea__inner {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.12);
}

.accident-management .el-input.is-focus .el-input__wrapper,
.accident-management .el-select .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--accent), 0 0 0 3px rgba(102, 126, 234, 0.08);
}
</style>
