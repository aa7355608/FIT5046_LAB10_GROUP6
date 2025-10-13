<!-- src/components/SiteHeader.vue -->
<template>
  <header class="bg-[#2A52BE] text-white">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-28 flex items-center justify-between">
      <!-- 左侧：Logo + 品牌 + 导航 -->
      <div class="flex items-center gap-3">
        <div class="flex items-center gap-3">
          <span class="inline-flex items-center justify-center w-10 h-10 rounded bg-white/10 ring-1 ring-white/20 text-lg">
            🌊
          </span>
          <span class="logo-script">OceanMate</span>
        </div>

        <!-- 桌面导航 -->
        <nav class="hidden md:flex items-center gap-10 text-sm text-white/85 ml-12">
          <!-- 每次回到 Welcome -->
          <RouterLink
            to="/"
            class="hover:text-white font-medium"
            :class="linkClass('/')"
          >Home</RouterLink>

          <RouterLink
            to="/map"
            class="hover:text-white"
            :class="linkClass('/map')"
          >Map</RouterLink>

          <RouterLink
            to="/trends"
            class="hover:text-white"
            :class="linkClass('/trends')"
          >Trends</RouterLink>

          <RouterLink
            to="/learn"
            class="hover:text-white"
            :class="linkClass('/learn')"
          >Learn</RouterLink>

          <!-- 原 'Portal' 改为 Contact Us -->
          <RouterLink
            to="/contact"
            class="hover:text-white"
            :class="linkClass('/contact')"
          >Contact Us</RouterLink>
        </nav>
      </div>
    </div>
  </header>
</template>

<script setup>
import { RouterLink, useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

const linkClass = (path) => computed(() => {
  const active = route.path === path || route.path.startsWith(path + '/')
  return active ? 'text-white font-semibold' : 'text-white/85'
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Pacifico&display=swap');

.logo-script {
  font-family: 'Pacifico', cursive;
  font-size: 34px;
  letter-spacing: 1px;
  line-height: 1;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
