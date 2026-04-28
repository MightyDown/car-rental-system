<template>
  <div class="empty-state">
    <div class="empty-icon">
      <slot name="icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <rect x="3" y="4" width="18" height="16" rx="2"/>
          <path d="M16 2v4M8 2v4M3 10h18"/>
        </svg>
      </slot>
    </div>
    <h3 v-if="title" class="empty-title">{{ title }}</h3>
    <p v-if="description" class="empty-desc">{{ description }}</p>
    <div v-if="$slots.action" class="empty-action">
      <slot name="action"></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '' },
  description: { type: String, default: '' },
})
</script>

<style scoped>
.empty-state {
  display: flex; flex-direction: column; align-items: center; gap: 16px;
  padding: 80px 0; color: #8a8aaa;
}

.empty-icon {
  width: 56px; height: 56px;
  display: flex; align-items: center; justify-content: center;
  color: rgba(102, 126, 234, 0.25);
}
.empty-icon svg { width: 48px; height: 48px; }

.empty-title {
  font-family: var(--font-cn);
  font-size: 17px; font-weight: 600; color: #6a6a8a;
}
.empty-desc { font-size: 14px; color: #8a8aaa; }
.empty-action { margin-top: 8px; }
</style>
