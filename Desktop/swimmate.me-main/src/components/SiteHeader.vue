<!-- src/components/SiteHeader.vue -->
<template>
  <header class="bg-[#2A52BE] text-white">
    <div
      class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-28 flex items-center justify-between"
    >
      <div class="flex items-center gap-3">
        <div class="flex items-center gap-3">
          <span
            class="inline-flex items-center justify-center w-10 h-10 rounded bg-white/10 ring-1 ring-white/20 text-lg"
          >
            🌊
          </span>

          <!-- 品牌名：使用手写体 -->
          <span class="logo-script">SwimMate</span>
        </div>

        <!-- 导航 -->
        <nav
          class="hidden md:flex items-center gap-10 text-sm text-white/85 ml-12"
        >
           <RouterLink to="/" class="hover:text-white font-medium">Home</RouterLink>
           <RouterLink to="/map" class="hover:text-white">Map</RouterLink>
           <RouterLink to="/trends" class="hover:text-white">Trends</RouterLink>
           <RouterLink to="/learn" class="hover:text-white">Learn</RouterLink>
          <a class="hover:text-white" href="#">Portal</a>
        </nav>
      </div>

      <a
        href="#"
        class="hidden sm:inline-flex items-center gap-2 bg-white/10 hover:bg-white/15
               px-6 py-2 rounded-lg text-sm ring-1 ring-white/20"
      >
        <span class="text-white">Water quality & beach safety</span>
      </a>
    </div>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'
// 只修改导航逻辑：显式引入 RouterLink，保证路由跳转生效
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
