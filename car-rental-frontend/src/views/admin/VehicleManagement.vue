<template>
  <div class="vehicle-management">
    <div class="page-header scroll-reveal">
      <div>
        <h1 class="page-title">车辆管理</h1>
        <p class="page-desc">管理车辆信息、状态与费率配置</p>
      </div>
      <button class="btn-primary" @click="openAdd">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 5v14M5 12h14"/></svg>
        添加车辆
      </button>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs">
      <!-- ==================== 车辆列表 Tab ==================== -->
      <el-tab-pane label="车辆列表" name="vehicles">
        <div class="toolbar scroll-reveal" style="--delay: 0.1s">
          <div class="search-bar">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/>
            </svg>
            <input v-model="filters.keyword" placeholder="搜索车牌号或车型..." class="search-input"
                   @keyup.enter="fetchVehicles" />
          </div>
          <el-select v-model="filters.type" placeholder="车辆类型" clearable style="width:130px"
                     @change="fetchVehicles">
            <el-option label="经济型" value="economy" />
            <el-option label="豪华型" value="luxury" />
          </el-select>
          <el-select v-model="filters.status" placeholder="车辆状态" clearable style="width:130px"
                     @change="fetchVehicles">
            <el-option label="空闲" value="idle" />
            <el-option label="已预约" value="reserved" />
            <el-option label="已预留" value="confirmed" />
            <el-option label="已租用" value="rented" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </div>

        <div class="table-card scroll-reveal" style="--delay: 0.2s">
          <el-table :data="vehicles" v-loading="loading" stripe empty-text="暂无车辆数据"
                    style="width: 100%" :header-cell-style="headerStyle">
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="plateNo" label="车牌号" min-width="110" />
            <el-table-column prop="model" label="车型" min-width="100" />
            <el-table-column prop="typeName" label="类型" width="90" align="center">
              <template #default="{ row }">
                <span class="type-tag" :class="row.type === 'luxury' ? 'type-luxury' : 'type-economy'">
                  {{ row.typeName }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="statusName" label="状态" width="90" align="center">
              <template #default="{ row }">
                <span class="status-tag" :class="'status-' + row.status">{{ row.statusName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="currentMileage" label="里程(km)" width="100" align="center" />
            <el-table-column label="日租金" width="100" align="center">
              <template #default="{ row }">
                ¥{{ row.dailyRate }}
              </template>
            </el-table-column>
            <el-table-column label="超时费/h" width="90" align="center">
              <template #default="{ row }">
                ¥{{ row.overtimeRate }}
              </template>
            </el-table-column>
            <el-table-column label="免里程/d" width="95" align="center">
              <template #default="{ row }">
                {{ row.freeMileage }}km
              </template>
            </el-table-column>
            <el-table-column label="操作" width="260" align="center" fixed="right">
              <template #default="{ row }">
                <div class="actions">
                  <button class="action-btn edit" @click="openEdit(row)">编辑</button>
                  <button v-if="row.status === 'maintenance'"
                          class="action-btn enable" @click="setStatus(row, 'idle')">恢复空闲</button>
                  <button v-if="row.status !== 'maintenance'"
                          class="action-btn warn" @click="setStatus(row, 'maintenance')">维修</button>
                  <button class="action-btn delete" @click="handleDelete(row)">删除</button>
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
            @size-change="fetchVehicles"
            @current-change="fetchVehicles"
          />
        </div>
      </el-tab-pane>

      <!-- ==================== 费率配置 Tab ==================== -->
      <el-tab-pane label="费率配置" name="config">
        <div class="config-grid">
          <div class="config-card" v-for="cfg in configs" :key="cfg.id">
            <div class="config-header">
              <span class="config-type-tag" :class="cfg.type === 'luxury' ? 'type-luxury' : 'type-economy'">
                {{ cfg.name }}
              </span>
              <span class="config-type-label">类型: {{ cfg.type }}</span>
            </div>
            <div class="config-body">
              <div class="config-row">
                <span class="config-label">日租金</span>
                <span class="config-value">¥{{ cfg.dailyRate }} / 天</span>
              </div>
              <div class="config-row">
                <span class="config-label">超时费率</span>
                <span class="config-value">¥{{ cfg.overtimeRate }} / 小时</span>
              </div>
              <div class="config-row">
                <span class="config-label">每日免费里程</span>
                <span class="config-value">{{ cfg.freeMileage }} 公里</span>
              </div>
              <div class="config-row">
                <span class="config-label">超里程费率</span>
                <span class="config-value">¥{{ cfg.mileageRate }} / 公里</span>
              </div>
            </div>
            <button class="btn-primary-outline config-btn" @click="openConfigEdit(cfg)">修改配置</button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- ===== 添加/编辑车辆弹窗 ===== -->
    <el-dialog v-model="vehicleDialogVisible" :title="isEditing ? '编辑车辆' : '添加车辆'"
               width="520px" destroy-on-close :close-on-click-modal="false">
      <el-form :model="vehicleForm" label-width="90px" label-position="left">
        <el-form-item label="车牌号" required>
          <el-input v-model="vehicleForm.plateNo" maxlength="20" placeholder="如：京A12345" />
        </el-form-item>
        <el-form-item label="车型名称" required>
          <el-input v-model="vehicleForm.model" maxlength="50" placeholder="如：凯美瑞" />
        </el-form-item>
        <el-form-item label="车辆类型" required>
          <el-select v-model="vehicleForm.type" style="width: 100%">
            <el-option label="经济型" value="economy" />
            <el-option label="豪华型" value="luxury" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前里程(km)" required>
          <el-input-number v-model="vehicleForm.currentMileage" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="车辆图片">
          <el-input v-model="vehicleForm.image" maxlength="255" placeholder="图片URL（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="vehicleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitVehicle" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>

    <!-- ===== 修改配置弹窗 ===== -->
    <el-dialog v-model="configDialogVisible" title="修改费率配置" width="460px"
               destroy-on-close :close-on-click-modal="false">
      <el-form :model="configForm" label-width="120px" label-position="left">
        <el-form-item label="日租金(元/天)">
          <el-input-number v-model="configForm.dailyRate" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="超时费率(元/小时)">
          <el-input-number v-model="configForm.overtimeRate" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="每日免费里程(km)">
          <el-input-number v-model="configForm.freeMileage" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="超里程费率(元/km)">
          <el-input-number v-model="configForm.mileageRate" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConfig" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getVehicles, createVehicle, updateVehicle, deleteVehicle,
  getConfigs, updateConfig, updateVehicleStatus,
} from '../../api/vehicle'

const activeTab = ref('vehicles')
const loading = ref(false)

// ===== 车辆列表 =====
const vehicles = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', type: '', status: '' })

const headerStyle = {
  background: '#faf9ff',
  color: '#1a1a2e',
  fontWeight: 600,
  fontSize: '13px',
}

async function fetchVehicles() {
  loading.value = true
  try {
    const res = await getVehicles({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword || undefined,
      type: filters.type || undefined,
      status: filters.status || undefined,
    })
    vehicles.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch { /* handled by interceptor */ } finally { loading.value = false }
}

// ===== 车辆增删改 =====
const vehicleDialogVisible = ref(false)
const isEditing = ref(false)
const editingId = ref(null)
const submitting = ref(false)
const vehicleForm = reactive({
  plateNo: '', model: '', type: 'economy', currentMileage: 0, image: '',
})

function openAdd() {
  isEditing.value = false
  editingId.value = null
  vehicleForm.plateNo = ''
  vehicleForm.model = ''
  vehicleForm.type = 'economy'
  vehicleForm.currentMileage = 0
  vehicleForm.image = ''
  vehicleDialogVisible.value = true
}

function openEdit(row) {
  isEditing.value = true
  editingId.value = row.id
  vehicleForm.plateNo = row.plateNo
  vehicleForm.model = row.model
  vehicleForm.type = row.type
  vehicleForm.currentMileage = row.currentMileage
  vehicleForm.image = row.image || ''
  vehicleDialogVisible.value = true
}

async function submitVehicle() {
  if (!vehicleForm.plateNo.trim()) return ElMessage.warning('请输入车牌号')
  if (!vehicleForm.model.trim()) return ElMessage.warning('请输入车型名称')
  submitting.value = true
  try {
    const data = { ...vehicleForm, plateNo: vehicleForm.plateNo.trim(), model: vehicleForm.model.trim() }
    if (isEditing.value) {
      await updateVehicle(editingId.value, data)
      ElMessage.success('更新成功')
    } else {
      await createVehicle(data)
      ElMessage.success('添加成功')
    }
    vehicleDialogVisible.value = false
    fetchVehicles()
  } catch {} finally { submitting.value = false }
}

async function setStatus(row, status) {
  const labels = { idle: '恢复空闲', maintenance: '设为维修' }
  try {
    await ElMessageBox.confirm(
      `确定将车辆「${row.plateNo}」${labels[status]}吗？`,
      '确认操作',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
  } catch { return }
  try {
    await updateVehicleStatus(row.id, status)
    ElMessage.success('状态已更新')
    fetchVehicles()
  } catch {}
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除车辆「${row.plateNo}」吗？此操作不可恢复。`,
      '确认删除',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch { return }
  try {
    await deleteVehicle(row.id)
    ElMessage.success('已删除')
    if (vehicles.value.length === 1 && pagination.page > 1) pagination.page--
    fetchVehicles()
  } catch {}
}

// ===== 费率配置 =====
const configs = ref([])
const configDialogVisible = ref(false)
const editingConfigId = ref(null)
const configForm = reactive({ dailyRate: 0, overtimeRate: 0, freeMileage: 0, mileageRate: 0 })

async function fetchConfigs() {
  try {
    const res = await getConfigs()
    configs.value = res.data || []
  } catch {}
}

function openConfigEdit(cfg) {
  editingConfigId.value = cfg.id
  configForm.dailyRate = cfg.dailyRate
  configForm.overtimeRate = cfg.overtimeRate
  configForm.freeMileage = cfg.freeMileage
  configForm.mileageRate = cfg.mileageRate
  configDialogVisible.value = true
}

async function submitConfig() {
  submitting.value = true
  try {
    await updateConfig(editingConfigId.value, { ...configForm })
    ElMessage.success('配置已更新')
    configDialogVisible.value = false
    fetchConfigs()
    fetchVehicles()
  } catch {} finally { submitting.value = false }
}

onMounted(async () => {
  fetchVehicles()
  fetchConfigs()
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
.vehicle-management {
  max-width: 1200px;
}

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

.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
}

/* ===== Buttons ===== */
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

/* ===== Toolbar ===== */
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-bar {
  flex: 1;
  min-width: 200px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
}

.search-bar:focus-within {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.08);
}

.search-icon { width: 18px; height: 18px; color: var(--text-muted); flex-shrink: 0; }

.search-input {
  flex: 1; border: none; outline: none; font-size: 14px;
  background: transparent; color: var(--text-primary); font-family: var(--font-body);
}

.search-input::placeholder { color: var(--text-muted); }

/* ===== Table ===== */
.table-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
}

/* ===== Tags ===== */
.type-tag, .status-tag {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 100px;
  font-size: 12px;
  font-weight: 500;
}

.type-economy { background: rgba(102, 126, 234, 0.1); color: var(--accent); }
.type-luxury { background: rgba(118, 75, 162, 0.1); color: var(--purple-end); }

.status-idle { background: rgba(78, 205, 196, 0.1); color: var(--success); }
.status-reserved { background: rgba(102, 126, 234, 0.1); color: var(--accent); }
.status-confirmed { background: rgba(102, 126, 234, 0.15); color: #5568d6; }
.status-rented { background: rgba(240, 147, 251, 0.12); color: #c44fb2; }
.status-maintenance { background: rgba(232, 72, 72, 0.1); color: var(--danger); }

/* ===== Actions ===== */
.actions { display: flex; gap: 6px; justify-content: center; flex-wrap: wrap; }

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

.action-btn.edit { color: var(--accent); }
.action-btn.edit:hover { background: rgba(102, 126, 234, 0.06); border-color: var(--accent); }

.action-btn.warn { color: #e6a23c; }
.action-btn.warn:hover { background: rgba(230, 162, 60, 0.06); border-color: #e6a23c; }

.action-btn.enable { color: var(--success); }
.action-btn.enable:hover { background: rgba(78, 205, 196, 0.06); border-color: var(--success); }

.action-btn.delete { color: var(--danger); }
.action-btn.delete:hover { background: rgba(232, 72, 72, 0.06); border-color: var(--danger); }

/* ===== Pagination ===== */
.pagination-wrap { margin-top: 20px; display: flex; justify-content: flex-end; }

/* ===== Config Tab ===== */
.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.config-card {
  padding: 28px;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 16px;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
}

.config-card:hover {
  border-color: rgba(102, 126, 234, 0.2);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.08);
}

.config-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border);
}

.config-type-tag {
  padding: 4px 14px;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
}

.config-type-label { font-size: 13px; color: var(--text-muted); }

.config-body { display: flex; flex-direction: column; gap: 12px; }

.config-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.config-label { font-size: 14px; color: var(--text-secondary); }
.config-value {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.config-btn {
  width: 100%;
  margin-top: 20px;
  padding: 10px 20px;
  background: transparent;
  border: 1.5px solid rgba(102, 126, 234, 0.3);
  color: var(--accent);
  font-family: var(--font-cn);
  font-size: 14px;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.config-btn:hover {
  border-color: var(--accent);
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-1px);
}

/* ===== Tabs Override ===== */
.custom-tabs :deep(.el-tabs__nav-wrap::after) { background-color: var(--border); }
.custom-tabs :deep(.el-tabs__item) {
  font-family: var(--font-cn);
  font-weight: 500;
  color: var(--text-secondary);
}
.custom-tabs :deep(.el-tabs__item.is-active) { color: var(--accent); }
.custom-tabs :deep(.el-tabs__active-bar) { background: var(--gradient-main); }

.scroll-reveal {
  opacity: 0;
  transform: translateY(40px);
  transition: all 0.7s cubic-bezier(0.22, 1, 0.36, 1);
  transition-delay: var(--delay, 0s);
}

.scroll-reveal.revealed { opacity: 1; transform: translateY(0); }
</style>

<style>
/* Global overrides inside this page */
.vehicle-management .el-table {
  --el-table-border-color: rgba(102, 126, 234, 0.06);
}

.vehicle-management .el-table th.el-table__cell { background: #faf9ff; }

.vehicle-management .el-table .el-table__row:hover > td.el-table__cell {
  background: rgba(102, 126, 234, 0.03);
}

.vehicle-management .el-pagination .el-pager li.is-active {
  background: var(--gradient-main);
  border-radius: 6px;
  color: #fff;
}

.vehicle-management .el-dialog {
  border-radius: 16px;
}

.vehicle-management .el-dialog__header {
  border-bottom: 1px solid var(--border);
  padding: 20px 24px;
}

.vehicle-management .el-dialog__title {
  font-family: var(--font-cn);
  font-weight: 600;
  color: var(--text-primary);
}

.vehicle-management .el-dialog__body { padding: 24px; }
.vehicle-management .el-dialog__footer { padding: 16px 24px; border-top: 1px solid var(--border); }

.vehicle-management .el-button--primary {
  background: var(--gradient-main);
  border: none;
  border-radius: 10px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.25);
}

.vehicle-management .el-button--primary:hover {
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);
  transform: translateY(-1px);
}

.vehicle-management .el-input__wrapper,
.vehicle-management .el-select .el-input__wrapper,
.vehicle-management .el-input-number .el-input__wrapper {
  border-radius: 10px;
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.12);
}

.vehicle-management .el-input__wrapper:hover,
.vehicle-management .el-select .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px rgba(102, 126, 234, 0.3);
}

.vehicle-management .el-input.is-focus .el-input__wrapper,
.vehicle-management .el-select .el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--accent), 0 0 0 3px rgba(102, 126, 234, 0.08);
}
</style>
