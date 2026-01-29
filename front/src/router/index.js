import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/users',
    name: 'UserManage',
    component: () => import('@/views/UserManage.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const username = localStorage.getItem('username')

  if (to.meta.requiresAuth) {
    if (!token || !username) {
      next('/login')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
