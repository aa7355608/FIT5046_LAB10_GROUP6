<template>
  <!-- 访问 '/'（欢迎页）时，只渲染页面本身 -->
  <RouterView v-if="isWelcome" />

  <!-- 其余页面：保留背景 + 头部 + 内容 + 页脚 -->
  <div v-else class="relative min-h-screen text-slate-900">
    <!-- 背景层 -->
    <div class="pointer-events-none absolute inset-0 -z-10 overflow-hidden">
      <div
        class="absolute -top-40 -left-32 h-[42rem] w-[42rem] rounded-full
               bg-gradient-to-br from-sky-300/45 via-indigo-200/35 to-fuchsia-200/35
               blur-3xl"
      ></div>
      <div
        class="absolute top-1/4 -right-40 h-[38rem] w-[38rem] rounded-full
               bg-gradient-to-tr from-amber-200/45 via-rose-200/35 to-sky-200/35
               blur-3xl"
      ></div>
      <div
        class="absolute inset-0 opacity-30"
        style="
          background-image:
            radial-gradient(circle at 1px 1px, rgba(148,163,184,.35) 1px, transparent 0);
          background-size: 22px 22px;
        "
      ></div>
      <div class="absolute inset-0 bg-noise opacity-[0.08] mix-blend-multiply"></div>
      <div class="absolute inset-x-0 top-0 h-64 bg-gradient-to-b from-white/70 via-white/30 to-transparent"></div>
    </div>

    <SiteHeader />
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <RouterView />
    </main>
    <SiteFooter />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, RouterView } from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'

const route = useRoute()
const isWelcome = computed(() => route.path === '/')
</script>

<style>
.bg-noise {
  background-image:
    repeating-linear-gradient(0deg, rgba(0,0,0,0.08) 0, rgba(0,0,0,0.08) 1px, transparent 1px, transparent 3px),
    repeating-linear-gradient(90deg, rgba(0,0,0,0.06) 0, rgba(0,0,0,0.06) 1px, transparent 1px, transparent 2px);
  filter: contrast(110%);
}
</style>
