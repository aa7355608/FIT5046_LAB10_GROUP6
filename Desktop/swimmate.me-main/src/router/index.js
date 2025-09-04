// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

import HomeView from '@/components/HomeView.vue'
import MapView from '@/components/MapView.vue'
import TrendsView from '@/components/TrendsView.vue'
import LearnView from '@/components/LearnView.vue'

const routes = [
  // 首页 -> HomeView
  { path: '/', name: 'home', component: HomeView },

  // 地图 -> MapView
  { path: '/map', name: 'map', component: MapView },

  // 其他页面
  { path: '/trends', name: 'trends', component: TrendsView },
  { path: '/learn', name: 'learn', component: LearnView },

  // 兜底：未知路径回首页
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
