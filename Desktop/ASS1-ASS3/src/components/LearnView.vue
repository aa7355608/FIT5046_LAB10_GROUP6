<template>
  <section class="min-h-screen bg-white">
    <!-- Top: quick entry -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight">Learn</h1>
      <p class="mt-2 text-slate-600">
        Simple guides to safer swimming in Port Phillip Bay.
      </p>

      <div class="mt-6 grid grid-cols-1 sm:grid-cols-3 gap-4">
        <button @click="scrollTo('#rain')"
                class="group rounded-2xl border border-slate-200 bg-gradient-to-br from-sky-50 to-slate-50 p-5 text-left hover:shadow-md transition">
          <div class="flex items-center gap-3">
            <div class="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-sky-100 text-sky-700">🌧️</div>
            <div>
              <div class="text-base font-semibold">Rain & stormwater</div>
              <div class="text-sm text-slate-500">Why rain elevates risk</div>
            </div>
          </div>
        </button>

        <button @click="scrollTo('#awareness')"
                class="group rounded-2xl border border-slate-200 bg-gradient-to-br from-amber-50 to-slate-50 p-5 text-left hover:shadow-md transition">
          <div class="flex items-center gap-3">
            <div class="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-amber-100 text-amber-700">⚠️</div>
            <div>
              <div class="text-base font-semibold">Pollution awareness</div>
              <div class="text-sm text-slate-500">Plain-language, quick visuals</div>
            </div>
          </div>
        </button>

        <button @click="scrollTo('#guidance')"
                class="group rounded-2xl border border-slate-200 bg-gradient-to-br from-emerald-50 to-slate-50 p-5 text-left hover:shadow-md transition">
          <div class="flex items-center gap-3">
            <div class="inline-flex h-10 w-10 items-center justify-center rounded-xl bg-emerald-100 text-emerald-700">📋</div>
            <div>
              <div class="text-base font-semibold">Official guidance</div>
              <div class="text-sm text-slate-500">EPA rules & updates</div>
            </div>
          </div>
        </button>
      </div>
    </div>

    <!-- Rain & stormwater -->
    <div id="rain" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 scroll-mt-24">
      <div class="rounded-2xl border border-slate-200 p-6 bg-white shadow-sm">
        <div class="flex items-start gap-4">
          <div class="text-3xl">🌧️</div>
          <div class="flex-1">
            <h2 class="text-xl font-semibold">How rainfall & stormwater runoff affect water safety</h2>
            <p class="mt-3 text-slate-700 leading-7">
              Heavy rain washes pollutants from streets, parks and drains into the bay (“first-flush”),
              temporarily raising bacteria and other contaminants near outfalls. Risk is higher for ~24–48 hours
              after rain and longer after floods. Use forecasts before you swim.
            </p>

            <!-- mini diagram -->
            <div class="mt-4">
              <div class="rounded-xl border border-slate-200 p-4 bg-slate-50">
                <div class="flex items-center justify-between text-slate-700">
                  <div class="flex flex-col items-center gap-1">
                    <div class="text-2xl">🏠</div>
                    <div class="text-xs">roofs/roads</div>
                  </div>
                  <div class="text-slate-400">➜</div>
                  <div class="flex flex-col items-center gap-1">
                    <div class="text-2xl">🕳️</div>
                    <div class="text-xs">storm drains</div>
                  </div>
                  <div class="text-slate-400">➜</div>
                  <div class="flex flex-col items-center gap-1">
                    <div class="text-2xl">🌊</div>
                    <div class="text-xs">the bay</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- video -->
            <div class="mt-6">
              <div class="rounded-xl overflow-hidden border border-slate-200">
                <iframe
                  class="w-full aspect-video"
                  src="https://www.youtube.com/embed/1N585ZChTRs"
                  title="Stormwater & the urban water cycle (Melbourne context)"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                  referrerpolicy="strict-origin-when-cross-origin"
                  allowfullscreen
                ></iframe>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Pollution awareness -->
    <div id="awareness" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 scroll-mt-24">
      <div class="rounded-2xl border border-slate-200 p-6 bg-white shadow-sm">
        <div class="flex items-start gap-4">
          <div class="text-3xl">⚠️</div>
          <div class="flex-1">
            <h2 class="text-xl font-semibold">Pollution awareness: understand risks fast</h2>
            <ul class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-3">
              <li class="flex items-start gap-3 rounded-xl border border-slate-200 p-4">
                <span class="text-xl">🚫</span>
                <p class="text-slate-700">
                  Avoid swimming near stormwater outfalls or after heavy rain (“first-flush”).
                </p>
              </li>
              <li class="flex items-start gap-3 rounded-xl border border-slate-200 p-4">
                <span class="text-xl">👀</span>
                <p class="text-slate-700">Skip if water looks murky, smells bad, or foam/debris is present.</p>
              </li>
              <li class="flex items-start gap-3 rounded-xl border border-slate-200 p-4">
                <span class="text-xl">📱</span>
                <p class="text-slate-700">Check today’s forecast before you go; conditions can change quickly.</p>
              </li>
              <li class="flex items-start gap-3 rounded-xl border border-slate-200 p-4">
                <span class="text-xl">🩺</span>
                <p class="text-slate-700">If you feel unwell after swimming, seek medical advice.</p>
              </li>
            </ul>

            <div class="mt-6 grid grid-cols-1 lg:grid-cols-2 gap-4">
              <a href="https://www.water.vic.gov.au/about-us/school-water-education-resources/going-with-the-flow-of-stormwater-and-drainage"
                 target="_blank" rel="noopener"
                 class="block rounded-xl border border-slate-200 p-4 hover:bg-slate-50 transition">
                <div class="text-sm text-slate-500 mb-1">DEECA • Melbourne Water</div>
                <div class="font-medium">Drainage story (wetlands & stormwater)</div>
                <div class="text-xs text-slate-500 mt-1">Short videos & explainer</div>
              </a>
              <a href="https://www.youtube.com/@MelbourneWater/videos"
                 target="_blank" rel="noopener"
                 class="block rounded-xl border border-slate-200 p-4 hover:bg-slate-50 transition">
                <div class="text-sm text-slate-500 mb-1">Melbourne Water • YouTube</div>
                <div class="font-medium">Stormwater, spills & waterway care</div>
                <div class="text-xs text-slate-500 mt-1">Official channel playlists</div>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== NEW: Latest related news ===== -->
    <div id="news" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 scroll-mt-24">
      <div class="rounded-2xl border border-slate-200 p-6 bg-white shadow-sm">
        <div class="flex items-baseline justify-between mb-4">
          <h2 class="text-xl font-semibold">Latest related news</h2>
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
      </div>
    </div>
    <!-- ===== /news ===== -->

    <!-- Official guidance -->
    <div id="guidance" class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 scroll-mt-24">
      <div class="rounded-2xl border border-slate-200 p-6 bg-white shadow-sm">
        <div class="flex items-start gap-4">
          <div class="text-3xl">📋</div>
          <div class="flex-1">
            <h2 class="text-xl font-semibold">Official guidance & seasonal updates</h2>
            <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="rounded-xl border border-slate-200 p-4">
                <div class="text-sm text-slate-500">EPA Victoria</div>
                <div class="font-medium mt-1">Avoid swimming for ~48 hours after heavy rain</div>
                <p class="text-slate-700 text-sm mt-2">
                  After rain, stormwater can carry microbes and pollutants into the bay.
                  Wait around 24–48 hours (longer after floods) and avoid discoloured/smelly water or flowing drains.
                </p>
                <div class="mt-3 text-sm">
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.epa.vic.gov.au/floodwater-and-your-health">Floodwater & your health</a>
                </div>
              </div>

              <div class="rounded-xl border border-slate-200 p-4">
                <div class="text-sm text-slate-500">EPA Victoria</div>
                <div class="font-medium mt-1">Beach Report (Port Phillip Bay)</div>
                <p class="text-slate-700 text-sm mt-2">
                  EPA publishes forecasts (summer daily / often twice-daily updates) showing water quality
                  as Good / Fair / Poor for many bay beaches. You can also sign up for SMS alerts.
                </p>
                <div class="mt-3 flex flex-wrap gap-3 text-sm">
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.epa.vic.gov.au/water-quality-forecast-ratings">Forecast ratings</a>
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.epa.vic.gov.au/mixed-bag-summer-swimmers-epa-launches-beach-report-2024">Beach Report 2024 launch</a>
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.portphillip.vic.gov.au/council-services/health-and-safety/public-health/beach-report/">City of Port Phillip explainer</a>
                </div>
              </div>

              <div class="rounded-xl border border-slate-200 p-4">
                <div class="text-sm text-slate-500">EPA & Vic Gov data</div>
                <div class="font-medium mt-1">Seasonal & event-based updates</div>
                <p class="text-slate-700 text-sm mt-2">
                  Wet springs and flood events can increase sediment/nutrients and reduce water quality in Port Phillip Bay.
                  Use official reports & datasets to see trends and context.
                </p>
                <div class="mt-3 flex flex-wrap gap-3 text-sm">
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.epa.vic.gov.au/water-quality-data-and-reports">Water quality: data & reports</a>
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://discover.data.vic.gov.au/dataset/epa-port-phillip-bay-water-quality-data-1984-2024">PPB data 1984–2024</a>
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.marineandcoasts.vic.gov.au/port-phillip-bay-emp/pathogens/results/port-phillip-bay">Pathogens results (PPB)</a>
                </div>
              </div>

              <div class="rounded-xl border border-slate-200 p-4">
                <div class="text-sm text-slate-500">EPA report</div>
                <div class="font-medium mt-1">Beach Report & Yarra Watch – long-term condition</div>
                <p class="text-slate-700 text-sm mt-2">
                  Long-term analyses show how stormwater after rain influences microbial water quality and which sites are more susceptible.
                </p>
                <div class="mt-3 flex flex-wrap gap-3 text-sm">
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.epa.vic.gov.au/beach-report-and-yarra-watch-long-term-condition-report">Report page</a>
                  <a class="text-sky-700 hover:underline" target="_blank" rel="noopener"
                     href="https://www.epa.vic.gov.au/sites/default/files/epa/publications/2056-beach-report-and-yarra-watch-long-term-condition-report_.pdf">PDF (Publication 2056)</a>
                </div>
              </div>
            </div>

            <div class="mt-6">
              <button @click="scrollTo('body')"
                      class="rounded-lg border border-slate-200 px-3 py-2 text-sm hover:bg-slate-50">
                ↑ Back to top
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
const scrollTo = (sel) => {
  const el = typeof sel === 'string' ? document.querySelector(sel) : sel
  if (!el) return
  el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

// You can replace these items later with live data.
const NEWS_ITEMS = [
  {
    title: 'Dozens of Victorian beaches unsafe after heavy rain',
    source: '7NEWS',
    when: 'Recent',
    url: 'https://7news.com.au/stories/swim-warning-at-dozens-of-victorian-beaches-amid-potential-stormwater-pollution-from-days-of-heavy-rain/',
    summary: 'EPA rated water quality at many bay beaches as “poor” due to stormwater after days of rain.',
    image: 'https://images.unsplash.com/photo-1470167290877-7d5d3446de4c?q=80&w=1200&auto=format&fit=crop'
  },
  {
    title: 'Open data: Port Phillip Bay water quality 1984–2024',
    source: 'data.gov.au',
    when: 'Dataset',
    url: 'https://data.gov.au/data/dataset/epa-port-phillip-bay-water-quality-data-1984-2024',
    summary: 'Monthly samples across the bay with nutrients and physical parameters for long-term analysis.',
    image: 'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?q=80&w=1200&auto=format&fit=crop'
  },
  {
    title: 'EPA advice: avoid swimming for 48h after heavy rain',
    source: 'City of Port Phillip',
    when: 'Guidance',
    url: 'https://www.portphillip.vic.gov.au/council-services/health-and-safety/public-health/beach-report/',
    summary: '“First flush” after storms carries built-up pollution into the bay; risk of illness is higher.',
    image: 'https://images.unsplash.com/photo-1468413253725-0d5181091126?q=80&w=1200&auto=format&fit=crop'
  }
]
</script>

<style scoped>
:target { scroll-margin-top: 96px; }
</style>
