// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

// Page component imports
import WelcomeView     from '@/components/WelcomeView.vue'
import HomeView        from '@/components/HomeView.vue'
import MapView         from '@/components/MapView.vue'
import TrendsView      from '@/components/TrendsView.vue'
import LearnView       from '@/components/LearnView.vue'
import ContactView     from '@/components/ContactView.vue'
import RainWeatherView from '@/components/RainWeatherView.vue'   
import OceanMateCommunity from '@/components/OceanMateCommunity.vue'   // ✅ Newly added import

// Route configuration
const routes = [
  // Default home page
  { path: '/', name: 'welcome', component: WelcomeView },

  // Real Home page
  { path: '/home', name: 'home', component: HomeView },

  // Main feature pages
  { path: '/map', name: 'map', component: MapView },
  { path: '/trends', name: 'trends', component: TrendsView },
  { path: '/learn', name: 'learn', component: LearnView },
  { path: '/contact', name: 'contact', component: ContactView },
  { path: '/rain-weather', name: 'rain-weather', component: RainWeatherView },

  // ✅ Newly added community page
  { path: '/community', name: 'community', component: OceanMateCommunity },

  // Fallback redirect
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

// Create and export router instance
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

export default router
