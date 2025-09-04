<template>
  <div class="relative min-h-screen text-slate-900">
    <!-- 背景层（不遮挡交互） -->
    <div class="pointer-events-none absolute inset-0 -z-10 overflow-hidden">
      <!-- 柔和彩色云团：左上 -->
      <div
        class="absolute -top-40 -left-32 h-[42rem] w-[42rem] rounded-full
               bg-gradient-to-br from-sky-300/45 via-indigo-200/35 to-fuchsia-200/35
               blur-3xl"
      ></div>

      <!-- 柔和彩色云团：右侧 -->
      <div
        class="absolute top-1/4 -right-40 h-[38rem] w-[38rem] rounded-full
               bg-gradient-to-tr from-amber-200/45 via-rose-200/35 to-sky-200/35
               blur-3xl"
      ></div>

      <!-- 细格网（很淡的纸感） -->
      <div
        class="absolute inset-0 opacity-30"
        style="
          background-image:
            radial-gradient(circle at 1px 1px, rgba(148,163,184,.35) 1px, transparent 0);
          background-size: 22px 22px;
        "
      ></div>

      <!-- 轻微颗粒（降低过度“塑料感”） -->
      <div class="absolute inset-0 bg-noise opacity-[0.08] mix-blend-multiply"></div>

      <!-- 顶部到中部的柔光遮罩，避免内容区过亮 -->
      <div
        class="absolute inset-x-0 top-0 h-64
               bg-gradient-to-b from-white/70 via-white/30 to-transparent"
      ></div>
    </div>

    <!-- 内容 -->
    <SiteHeader />

    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <!-- 你的各个页面在这里渲染 -->
      <RouterView />
    </main>

    <SiteFooter />
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router'
import SiteHeader from '@/components/SiteHeader.vue'
import SiteFooter from '@/components/SiteFooter.vue'
</script>

<style>
/* 颗粒纹理（纯 CSS，避免外链图片；开销极小） */
.bg-noise {
  /* 两组不同方向的细条叠加，营造很淡的“纸张颗粒” */
  background-image:
    repeating-linear-gradient(
      0deg,
      rgba(0,0,0,0.08) 0,
      rgba(0,0,0,0.08) 1px,
      transparent 1px,
      transparent 3px
    ),
    repeating-linear-gradient(
      90deg,
      rgba(0,0,0,0.06) 0,
      rgba(0,0,0,0.06) 1px,
      transparent 1px,
      transparent 2px
    );
  filter: contrast(110%);
}
</style>
