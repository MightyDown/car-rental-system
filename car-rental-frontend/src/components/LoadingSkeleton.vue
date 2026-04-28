<template>
  <div class="skeleton-wrap">
    <!-- Card Grid Skeleton -->
    <div v-if="type === 'card'" class="skeleton-cards">
      <div v-for="i in count" :key="i" class="skeleton-card">
        <div class="skeleton-line short"></div>
        <div class="skeleton-block"></div>
        <div class="skeleton-line medium"></div>
        <div class="skeleton-line full"></div>
        <div class="skeleton-row">
          <div class="skeleton-line small"></div>
          <div class="skeleton-line small"></div>
          <div class="skeleton-line small"></div>
        </div>
      </div>
    </div>

    <!-- Table Skeleton -->
    <div v-else-if="type === 'table'" class="skeleton-table">
      <div class="skeleton-header">
        <div v-for="i in columns" :key="i" class="skeleton-line medium"></div>
      </div>
      <div v-for="i in count" :key="i" class="skeleton-row">
        <div v-for="j in columns" :key="j" class="skeleton-line full"></div>
      </div>
    </div>

    <!-- List Skeleton -->
    <div v-else-if="type === 'list'" class="skeleton-list">
      <div v-for="i in count" :key="i" class="skeleton-item">
        <div class="skeleton-avatar"></div>
        <div class="skeleton-content">
          <div class="skeleton-line medium"></div>
          <div class="skeleton-line full"></div>
        </div>
      </div>
    </div>

    <!-- Single block -->
    <div v-else class="skeleton-block simple"></div>
  </div>
</template>

<script setup>
defineProps({
  type: { type: String, default: 'card' },
  count: { type: Number, default: 3 },
  columns: { type: Number, default: 4 },
})
</script>

<style scoped>
@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

.skeleton-line, .skeleton-block, .skeleton-avatar {
  background: linear-gradient(90deg, #f0eef8 25%, #e8e4f6 37%, #f0eef8 63%);
  background-size: 200% 100%;
  animation: shimmer 1.8s ease-in-out infinite;
  border-radius: 6px;
}

.skeleton-line { height: 14px; }
.skeleton-line.short { width: 40%; }
.skeleton-line.medium { width: 65%; }
.skeleton-line.full { width: 100%; }
.skeleton-line.small { flex: 1; }

.skeleton-block { width: 100%; height: 60px; border-radius: 10px; }

.skeleton-avatar { width: 44px; height: 44px; border-radius: 10px; flex-shrink: 0; }

/* Cards */
.skeleton-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 28px;
}

.skeleton-card {
  padding: 36px 32px;
  background: #fff;
  border: 1px solid rgba(102, 126, 234, 0.06);
  border-radius: 20px;
  display: flex; flex-direction: column; gap: 16px;
}

.skeleton-row { display: flex; gap: 16px; }

/* Table */
.skeleton-table {
  background: #fff;
  border: 1px solid rgba(102, 126, 234, 0.06);
  border-radius: 16px; overflow: hidden;
}

.skeleton-header {
  display: flex; gap: 24px;
  padding: 16px 24px;
  background: #faf9ff;
  border-bottom: 1px solid rgba(102, 126, 234, 0.06);
}

.skeleton-table .skeleton-row {
  display: flex; gap: 24px;
  padding: 14px 24px;
  border-bottom: 1px solid rgba(102, 126, 234, 0.04);
}

/* List */
.skeleton-list {
  display: flex; flex-direction: column; gap: 16px;
}

.skeleton-item {
  display: flex; gap: 16px; align-items: center;
  padding: 20px 24px;
  background: #fff;
  border: 1px solid rgba(102, 126, 234, 0.06);
  border-radius: 16px;
}

.skeleton-content {
  flex: 1;
  display: flex; flex-direction: column; gap: 10px;
}

/* Simple */
.skeleton-block.simple {
  height: 200px;
  background: #fff;
  border: 1px solid rgba(102, 126, 234, 0.06);
  border-radius: 16px;
}
</style>
