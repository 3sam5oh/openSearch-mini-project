import { createRouter, createWebHistory } from 'vue-router'
import SearchMain from '@/views/SearchMain.vue'

const routes = [
  {
    path: '/',
    name: '/',
    component: SearchMain
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
