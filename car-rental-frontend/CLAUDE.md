# 驰速租车管理系统 — 前端项目

## 技术栈
- Vue 3 + Vite + Element Plus
- Vue Router（history 模式）
- CSS 变量驱动的设计系统

## 设计规范
本项目有完整的前端设计系统 skill：`.claude/skills/car-rental-style.md`

**所有前端页面开发必须遵循 car-rental-style 设计规范。** 包括：
- 色彩体系（紫蓝渐变 #667eea → #764ba2）
- 浅色微紫背景（禁止暗黑/纯灰风格）
- 圆角、阴影、字体、按钮、卡片、动画的统一规范
- 滚动渐显动画（IntersectionObserver）
- Element Plus 组件样式覆盖
- 响应式断点（900px / 600px）

## 项目结构
```
src/
  views/        — 页面组件（Home, Login, Register）
  components/   — 通用组件
  api/          — API 请求
  router/       — 路由配置
  stores/       — 状态管理
  utils/        — 工具函数
  style.css     — 全局样式变量（设计令牌）
  App.vue       — 根组件
```

## 风格基准页面
`src/views/Home.vue` 是本项目的视觉风格基准，新页面应以首页为参照。
