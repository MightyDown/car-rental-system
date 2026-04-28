<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="brand-icon">C</span>
        <span class="brand-text">驰速租车</span>
      </div>
      <div class="footer-links">
        <slot name="links">
          <a href="#vehicles">车型一览</a>
          <a href="#features">核心优势</a>
          <a href="#process">租车流程</a>
        </slot>
      </div>
      <div class="footer-copy">
        &copy; {{ new Date().getFullYear() }} 驰速租车 — 智能租车管理系统
      </div>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  padding: 48px 40px;
  border-top: 1px solid rgba(102, 126, 234, 0.08);
  background: #faf9ff;
}

.footer-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center; justify-content: space-between;
}

.footer-brand { display: flex; align-items: center; gap: 10px; }

.brand-icon {
  width: 28px; height: 28px;
  display: flex; align-items: center; justify-content: center;
  background: var(--gradient-main); color: #fff;
  font-family: var(--font-display); font-weight: 800; font-size: 14px;
  border-radius: 7px;
}

.brand-text { font-size: 15px; color: #1a1a2e; font-family: var(--font-cn); font-weight: 600; }

.footer-links { display: flex; gap: 24px; }
.footer-links a {
  font-size: 13px; color: #8a8aaa; text-decoration: none;
  transition: color 0.3s;
}
.footer-links a:hover { color: var(--purple-start); }

.footer-copy { font-size: 12px; color: #aaaacc; }

@media (max-width: 900px) {
  .footer-inner { flex-direction: column; gap: 16px; text-align: center; }
}
</style>
