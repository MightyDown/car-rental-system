import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' },
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页' },
  },
  {
    path: '/fleet',
    name: 'Fleet',
    component: () => import('../views/FleetGallery.vue'),
    meta: { title: '车型一览' },
  },
  {
    path: '/vehicles/:id',
    name: 'CarDetail',
    component: () => import('../views/CarDetail.vue'),
    meta: { title: '车辆详情', requiresAuth: true },
  },
  {
    path: '/my-bookings',
    name: 'MyBookings',
    component: () => import('../views/MyBookings.vue'),
    meta: { title: '我的预订', requiresAuth: true },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true },
  },
  {
    path: '/dashboard',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { title: '控制台' },
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'vehicles',
        name: 'VehicleManagement',
        component: () => import('../views/admin/VehicleManagement.vue'),
        meta: { title: '车辆管理' },
      },
      {
        path: 'bookings',
        name: 'BookingManagement',
        component: () => import('../views/admin/BookingManagement.vue'),
        meta: { title: '预订管理' },
      },
      {
        path: 'rentals',
        name: 'RentalManagement',
        component: () => import('../views/admin/RentalManagement.vue'),
        meta: { title: '取还车管理' },
      },
      {
        path: 'accidents',
        name: 'AccidentManagement',
        component: () => import('../views/admin/AccidentManagement.vue'),
        meta: { title: '事故管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 租车管理系统` : '租车管理系统'
  const token = localStorage.getItem('token')

  if (to.path !== '/' && to.path !== '/fleet' && to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
    return
  }

  if (to.matched.some(r => r.meta.requiresAdmin)) {
    try {
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      if (user.role !== 'ADMIN') {
        next('/')
        return
      }
    } catch {
      next('/login')
      return
    }
  }

  next()
})

export default router
