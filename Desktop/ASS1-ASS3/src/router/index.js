// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// 页面组件导入
import WelcomeView     from '@/components/WelcomeView.vue'
import HomeView        from '@/components/HomeView.vue'
import MapView         from '@/components/MapView.vue'
import TrendsView      from '@/components/TrendsView.vue'
import LearnView       from '@/components/LearnView.vue'
import ContactView     from '@/components/ContactView.vue'
import RainWeatherView from '@/components/RainWeatherView.vue'   
import OceanMateCommunity from '@/components/OceanMateCommunity.vue'   // ✅ 新增导入

// 路由配置
const routes = [
  // 默认首页
  { path: '/', name: 'welcome', component: WelcomeView },

  // 真实 Home 页面
  { path: '/home', name: 'home', component: HomeView },

  // 主要功能页
  { path: '/map', name: 'map', component: MapView },
  { path: '/trends', name: 'trends', component: TrendsView },
  { path: '/learn', name: 'learn', component: LearnView },
  { path: '/contact', name: 'contact', component: ContactView },
  { path: '/rain-weather', name: 'rain-weather', component: RainWeatherView },

  // ✅ 新增社区页面
  { path: '/community', name: 'community', component: OceanMateCommunity },

  // 兜底重定向
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

// 创建并导出路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

export default router
