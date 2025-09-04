<template>
  <div class="min-h-screen bg-white text-slate-900">
    <!-- 1) HERO：大图+标题 -->
    <section
      class="relative overflow-hidden rounded-2xl border border-slate-200 bg-slate-50"
    >
      <img
        class="absolute inset-0 h-full w-full object-cover opacity-70"
        :src="FEATURED_IMAGES[0].src"
        :alt="FEATURED_IMAGES[0].alt"
        loading="lazy"
      />
      <div class="relative z-10 max-w-7xl mx-auto px-6 py-16">
        <h1 class="text-4xl sm:text-5xl font-extrabold tracking-tight text-slate-900">
          Check water quality & beach safety
        </h1>
        <p class="mt-3 max-w-2xl text-slate-800/90 text-lg">
          Current and forecast water quality for Port Phillip Bay, safety alerts,
          and guidance to help you choose a safer beach.
        </p>
        <div class="mt-6 flex gap-3">
          <RouterLink
            to="/map"
            class="inline-flex items-center rounded-xl bg-sky-600 px-5 py-2.5 text-white font-semibold hover:bg-sky-700 transition"
          >
            Open Map
          </RouterLink>
          <a
            href="https://www.epa.vic.gov.au/check-air-and-water-quality"
            target="_blank" rel="noopener"
            class="inline-flex items-center rounded-xl bg-white/90 px-5 py-2.5 text-slate-900 font-semibold ring-1 ring-slate-300 hover:bg-white transition"
            >EPA Victoria – Beach Report</a
          >
        </div>
      </div>
    </section>

    <!-- 2) 三列配图（可作专题入口） -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-10">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <article
          v-for="(img, i) in FEATURED_IMAGES.slice(1)"
          :key="i"
          class="group relative overflow-hidden rounded-2xl border border-slate-200 bg-white"
        >
          <img :src="img.src" :alt="img.alt" class="h-56 w-full object-cover group-hover:scale-[1.02] transition" loading="lazy" />
          <div class="p-5">
            <h3 class="text-base font-semibold">{{ img.title }}</h3>
            <p class="mt-2 text-sm text-slate-600">{{ img.caption }}</p>
          </div>
        </article>
      </div>
    </section>

    <!-- 3) 新闻卡片栅格 -->
    <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-12">
      <div class="flex items-baseline justify-between mb-4">
        <h2 class="text-2xl font-bold">Latest related news</h2>
        <RouterLink to="/map" class="text-sky-700 hover:underline text-sm">Check beaches on the map →</RouterLink>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <article
          v-for="item in NEWS_ITEMS"
          :key="item.url"
          class="overflow-hidden rounded-2xl border border-slate-200 bg-white hover:shadow-md transition"
        >
          <img v-if="item.image" :src="item.image" :alt="item.title" class="h-44 w-full object-cover" loading="lazy" />
          <div class="p-5">
            <div class="text-xs text-slate-500 mb-1">{{ item.source }} · {{ item.when }}</div>
            <a :href="item.url" target="_blank" rel="noopener" class="block">
              <h3 class="text-base font-semibold leading-6 hover:underline">
                {{ item.title }}
              </h3>
            </a>
            <p class="mt-2 text-sm text-slate-600">{{ item.summary }}</p>
          </div>
        </article>
      </div>

      <!-- 数据与方法简介 -->
      <div class="mt-10 grid grid-cols-1 lg:grid-cols-3 gap-6">
        <article class="rounded-2xl border border-slate-200 bg-white p-6">
          <h3 class="text-lg font-semibold mb-2">How we assess water quality</h3>
          <p class="text-slate-600 leading-7">
            We analyse historical trends (1984–2024), seasonal patterns, and recent rainfall to estimate risk.
            Confidence is shown via rolling variability metrics and clear annotations.
          </p>
        </article>
        <article class="rounded-2xl border border-slate-200 bg-white p-6">
          <h3 class="text-lg font-semibold mb-2">Monitoring data (EPA)</h3>
          <p class="text-slate-600 leading-7">
            EPA Victoria forecasts & monitors 36 beaches in Port Phillip Bay and 4 Yarra River sites.
            Historical datasets (1984–2024) are available openly for analysis.
          </p>
          <div class="mt-3 flex gap-3">
            <a
              href="https://www.epa.vic.gov.au/check-air-and-water-quality"
              target="_blank" rel="noopener"
              class="rounded-lg bg-slate-900 text-white text-sm px-3 py-1.5 hover:bg-black"
              >EPA Beach Report</a
            >
            <a
              href="https://data.gov.au/data/dataset/epa-port-phillip-bay-water-quality-data-1984-2024"
              target="_blank" rel="noopener"
              class="rounded-lg bg-slate-100 text-slate-900 text-sm px-3 py-1.5 ring-1 ring-slate-200 hover:bg-white"
              >Open dataset</a
            >
          </div>
        </article>
        <article class="rounded-2xl border border-slate-200 bg-white p-6">
          <h3 class="text-lg font-semibold mb-2">Safety tip</h3>
          <p class="text-slate-600 leading-7">
            EPA advises avoiding swimming for up to 48 hours after heavy rain due to higher bacteria levels from stormwater “first flush”.
          </p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'

/** 展示图：Unsplash 免费可商用 */
const FEATURED_IMAGES = [
  {
    src: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1600&auto=format&fit=crop',
    alt: 'Calm ocean at sunset',
    title: 'Plan a safer beach day',
    caption: 'Use forecasts, recent rainfall and site notes to choose where to swim.'
  },
  {
    src: 'https://images.unsplash.com/photo-1500375592092-40eb2168fd21?q=80&w=1200&auto=format&fit=crop',
    alt: 'Coastline aerial',
    title: 'Port Phillip Bay',
    caption: 'Seasonal patterns and recent weather can change local risk quickly.'
  },
  {
    src: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1200&auto=format&fit=crop',
    alt: 'Gentle waves',
    title: 'Know before you go',
    caption: 'Check the Beach Report and rain history before you swim.'
  }
]

/** 新闻卡片：手动整理的近期相关话题（可继续追加/替换） */
const NEWS_ITEMS = [
  {
    title: 'Dozens of Victorian beaches unsafe after heavy rain',
    source: '7NEWS',
    when: '5 days ago',
    url: 'https://7news.com.au/stories/swim-warning-at-dozens-of-victorian-beaches-amid-potential-stormwater-pollution-from-days-of-heavy-rain/',
    summary: 'EPA rated water quality at 36 bay beaches as “poor” due to stormwater pollution following days of rain.',
    image: 'https://images.unsplash.com/photo-1470167290877-7d5d3446de4c?q=80&w=1200&auto=format&fit=crop'
  },
  {
  title: 'Microplastics discovered in Melbourne’s waterways',
  source: 'The Age',
  when: 'Recent',
  url: 'https://www.theage.com.au/environment',
  summary: 'Researchers report widespread microplastic pollution in local rivers and creeks, raising concerns for bay water quality.',
  image: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?q=80&w=1200&auto=format&fit=crop'},

  {
    title: 'Open data: Port Phillip Bay water quality 1984–2024',
    source: 'data.gov.au',
    when: 'Dataset',
    url: 'https://data.gov.au/data/dataset/epa-port-phillip-bay-water-quality-data-1984-2024',
    summary: 'Monthly samples across the bay with nutrients and physical parameters for long-term analysis.',
    image: 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=1200&auto=format&fit=crop'
  },
  {
    title: 'EPA advice: avoid swimming for 48h after heavy rain',
    source: 'City of Port Phillip',
    when: 'Guidance',
    url: 'https://www.portphillip.vic.gov.au/council-services/health-and-safety/public-health/beach-report/',
    summary: '“First flush” after storms carries built-up pollution into the bay; risk of illness is higher.',
    image: 'https://images.unsplash.com/photo-1468413253725-0d5181091126?q=80&w=1200&auto=format&fit=crop'
  },
  {
    title: 'RMIT project: “Unseen Threats” contaminants in Port Phillip Bay',
    source: 'RMIT University',
    when: 'Project',
    url: 'https://www.rmit.edu.au/about/schools-colleges/science/research/research-centres-groups/aquatic-environmental-stress/projects/exploring-unseen-threats-contaminants-in-port-phillip-bay',
    summary: 'Monitoring contaminants and sharing results to better understand risks across 34 estuaries feeding the bay.',
    image: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=1200&auto=format&fit=crop'
  },
  {
    title: 'Burrunan dolphin threatened by legacy chemicals',
    source: 'The Guardian',
    when: '5 months ago',
    url: 'https://www.theguardian.com/environment/2025/mar/19/victorias-unique-dolphin-population-threatened-by-legacy-of-forever-chemicals',
    summary: 'Research highlights PCBs/DDT risks for Victoria’s rare dolphin population; mitigation actions urged.',
    image: 'https://images.unsplash.com/photo-1505761671935-60b3a7427bad?q=80&w=1200&auto=format&fit=crop'
  }
]
</script>

<style scoped>
/* 可选的小修饰：让图片卡片在深色边框下更柔和 */
</style>
