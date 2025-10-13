<template>
  <div class="min-h-screen bg-white text-slate-900">
    <!-- Banner -->
    <section class="w-full">
      <img
        src="https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2000&auto=format&fit=crop"
        alt="Beach banner"
        class="w-full h-40 sm:h-48 md:h-56 object-cover"
        loading="lazy"
      />
    </section>

    <!-- Page container -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Map card -->
      <section class="mt-6 rounded-2xl border border-slate-200 bg-white shadow-sm">
        <header class="px-4 sm:px-6 pt-6">
          <h2 class="text-2xl sm:text-3xl font-extrabold text-center">
            Port Phillip Bay Safety Map
          </h2>
        </header>

        <!-- Controls -->
        <div class="px-4 sm:px-6 mt-6">
          <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <!-- Search -->
            <div class="flex items-center gap-3 rounded-2xl border border-slate-400 bg-slate-200 w-full px-5 py-3 shadow-sm">
              <input
                v-model="kw"
                @keyup.enter="handleSearch"
                placeholder="Search suburb / postcode / beach name…"
                class="flex-1 bg-transparent outline-none text-lg h-11 text-slate-900 font-medium"
              />
              <button class="btn-chip-blue" @click="handleSearch">Search</button>
              <button class="btn-chip-blue" title="Fullscreen" @click="toggleFullscreen">⤢</button>
            </div>

            <!-- Date -->
            <div class="flex items-center gap-3">
              <label class="text-sm font-semibold text-slate-700">Date</label>
              <input
                type="date"
                :max="todayStr"
                v-model="selectedDate"
                @change="handleDateChange"
                class="rounded-xl border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
              />
              <button class="btn-ui" @click="resetToToday">Today</button>
            </div>
          </div>
        </div>

        <!-- Map -->
        <div class="px-4 sm:px-6">
          <div class="relative mt-5 mb-6 overflow-hidden rounded-xl border border-slate-300">
            <div ref="mapEl" class="h-[68vh] min-h-[460px] w-full"></div>

            <!-- Bottom-right buttons -->
            <div class="absolute right-3 bottom-3 flex gap-2">
              <button class="btn-ui" @click="showDisclaimer = !showDisclaimer">Disclaimer</button>
              <button class="btn-ui" @click="openLegend">View pin legend</button>
            </div>

            <!-- Floating disclaimer -->
            <div
              v-if="showDisclaimer"
              class="absolute right-3 bottom-16 w-80 rounded-xl border border-slate-200 bg-white/95 p-3 shadow"
            >
              <b class="text-sm">Notes</b>
              <p class="mt-1 text-sm leading-relaxed text-slate-600">
                Colours &amp; badges use <b>hourly precipitation (24h/48h)</b> and <b>sea surface temperature</b>
                on the selected date (local time). Data by Open-Meteo.
              </p>
            </div>
          </div>
        </div>

        <!-- Legend -->
        <div class="px-4 sm:px-6 pb-6">
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="legend-card border-green-200 bg-green-50">
              <div class="flex items-start gap-3">
                <span class="mt-1 inline-block h-3.5 w-3.5 rounded-full bg-green-500"></span>
                <div>
                  <div class="font-semibold text-green-700">Safe</div>
                  <p class="text-sm text-green-700/80">Little/no rain &amp; comfortable water temp.</p>
                </div>
              </div>
            </div>
            <div class="legend-card border-amber-200 bg-amber-50">
              <div class="flex items-start gap-3">
                <span class="mt-1 inline-block h-3.5 w-3.5 rounded-full bg-amber-500"></span>
                <div>
                  <div class="font-semibold text-amber-700">Caution</div>
                  <p class="text-sm text-amber-700/80">Recent light rain or cool/hot water.</p>
                </div>
              </div>
            </div>
            <div class="legend-card border-rose-200 bg-rose-50">
              <div class="flex items-start gap-3">
                <span class="mt-1 inline-block h-3.5 w-3.5 rounded-full bg-rose-500"></span>
                <div>
                  <div class="font-semibold text-rose-700">Very Unsafe</div>
                  <p class="text-sm text-rose-700/80">Heavy rain or very cold water.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Feature cards -->
      <section class="mt-10">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="feature-card bg-indigo-600">
            <div class="feature-card-body">
              <div class="text-3xl">📈</div>
              <h3 class="mt-2 text-white text-lg font-semibold">Historical Data</h3>
              <p class="mt-1 text-white/90 text-sm">Trends to build confidence.</p>
              <button class="feature-card-btn" @click="go('/trends')">View Trends</button>
            </div>
          </div>
          <div class="feature-card bg-green-600">
            <div class="feature-card-body">
              <div class="text-3xl">🛡️</div>
              <h3 class="mt-2 text-white text-lg font-semibold">Safety Guidelines</h3>
              <p class="mt-1 text-white/90 text-sm">What to do before & after.</p>
              <button class="feature-card-btn" @click="go('/learn')">Read Guide</button>
            </div>
          </div>
          <div class="feature-card bg-violet-600">
            <div class="feature-card-body">
              <div class="text-3xl">👥</div>
              <h3 class="mt-2 text-white text-lg font-semibold">Community</h3>
              <p class="mt-1 text-white/90 text-sm">Share updates & tips.</p>
              <button class="feature-card-btn" @click="go('/community')">Join Community</button>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA -->
      <section class="mt-12 rounded-2xl bg-gradient-to-b from-sky-700 to-sky-800 text-white px-6 py-12 text-center shadow">
        <h3 class="text-2xl sm:text-3xl font-extrabold">Plan your next safe swim.</h3>
        <p class="mt-2 text-white/90">Pick a date; we’ll check rainfall + water temperature.</p>
      </section>

      <!-- Trusted by -->
      <section class="mt-10 mb-16 text-center">
        <h4 class="text-slate-900 font-semibold">Trusted By</h4>
        <div class="mt-4 flex flex-wrap items-center justify-center gap-3">
          <span class="chip-trusted">EPA Victoria</span>
          <span class="chip-trusted">Bureau of Meteorology</span>
          <span class="chip-trusted">Beach Reports</span>
        </div>
        <p class="mt-4 text-xs text-slate-500">
          Proudly supporting UN SDG 06 – Clean Water & Sanitation.
        </p>
      </section>
    </div>

    <!-- Footer -->
    <footer class="bg-slate-900 text-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        <div class="flex items-center justify-between text-sm">
          <div>© 2025 SwimMate · Built for Port Phillip Bay.</div>
          <nav class="flex gap-6">
            <a href="#" class="hover:underline">Privacy</a>
            <a href="#" class="hover:underline">Accessibility</a>
            <a href="#" class="hover:underline">Contact</a>
          </nav>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import * as L from 'leaflet'
import { fetchSites } from '@/services/dbApi'
import { reverseGeocodeClient, forwardGeocodeClient } from '@/services/revgeoClient'

/* ===== Rain thresholds (mm) ===== */
const RAIN_RED_24 = 10      // heavy in 24h
const RAIN_RED_48 = 15      // heavy in 48h
/* ===== Water temperature thresholds (°C) ===== */
const TEMP_RED_COLD = 16
const TEMP_AMBER_COOL_MAX = 18
const TEMP_GREEN_MIN = 18
const TEMP_GREEN_MAX = 25
const TEMP_AMBER_HOT_MIN = 28

const router = useRouter()
const go = (path) => router.push(path)
const kw = ref('')
const showDisclaimer = ref(false)

const mapEl = ref(null)
let map, markersLayer, pinAtQuery

// Date state
const todayStr = new Date().toISOString().slice(0,10)
const selectedDate = ref(todayStr)
function resetToToday() { selectedDate.value = todayStr; handleDateChange() }
function handleDateChange() { drawSites().catch(() => {}) }

// caches
const rainCache = new Map()
const tempCache = new Map()

/* Leaflet icon */
const DefaultIcon = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [0, -28], shadowSize: [41, 41],
})
L.Marker.prototype.options.icon = DefaultIcon

function statusColor(s){ const t=(s||'green').toLowerCase()
  if (t==='amber') return '#f59e0b'
  if (t==='red') return '#ef4444'
  return '#22c55e'
}
function toggleFullscreen() {
  const el = mapEl.value
  if (!document.fullscreenElement) el?.requestFullscreen?.()
  else document.exitFullscreen?.()
}
function openLegend() {
  alert('Legend (rain + water temperature):\n' +
        'Green = Safe\nAmber = Caution\nRed = Very Unsafe\n\n' +
        'Computed from hourly precipitation (24h/48h) and sea surface temperature.')
}

/* date helpers */
function ymdShift(dateStr, days){
  const t = new Date(dateStr); t.setDate(t.getDate()+days)
  return t.toISOString().slice(0,10)
}

/* ===== Classification rules ===== */
function tempFlag(temp){
  if (temp == null || Number.isNaN(temp)) return 'unknown'
  if (temp < TEMP_RED_COLD) return 'red_cold'
  if (temp < TEMP_AMBER_COOL_MAX) return 'amber_cool'  // [16,18)
  if (temp > TEMP_AMBER_HOT_MIN) return 'amber_hot'    // >28
  if (temp >= TEMP_GREEN_MIN && temp <= TEMP_GREEN_MAX) return 'green_ok'
  return 'green_warm'
}

function computeOverall({ sum24, sum48, isNowRaining, tflag }) {
  if (sum24 >= RAIN_RED_24 || sum48 >= RAIN_RED_48 || tflag === 'red_cold') return 'red'
  if (isNowRaining || sum24 > 0 || sum48 > 0 || tflag === 'amber_cool' || tflag === 'amber_hot') return 'amber'
  return 'green'
}

/* ===== Fetch rain (precipitation-first) ===== */
async function fetchRainFor(lat, lng, selectedDateStr){
  const key = `${lat.toFixed(2)},${lng.toFixed(2)},${selectedDateStr}`
  const cached = rainCache.get(key)
  if (cached && Date.now() - cached.ts < 10*60*1000) return cached

  let sum24 = 0, sum48 = 0, isNowRaining = false
  const startDate = ymdShift(selectedDateStr, -2)
  const endDate = selectedDateStr

  try {
    const url = new URL('https://api.open-meteo.com/v1/forecast')
    url.searchParams.set('latitude', lat)
    url.searchParams.set('longitude', lng)
    url.searchParams.set('hourly', 'precipitation,rain')
    url.searchParams.set('start_date', startDate)
    url.searchParams.set('end_date', endDate)
    url.searchParams.set('timezone', 'Australia/Sydney')
    const res = await fetch(url.toString())
    if (!res.ok) throw new Error('rain_fetch_failed')
    const data = await res.json()

    const times   = data?.hourly?.time ?? []
    const arrPrec = data?.hourly?.precipitation ?? []
    const arrRain = data?.hourly?.rain ?? []
    const vals = (arrPrec?.length ? arrPrec : arrRain) ?? []

    if (!times.length || !vals.length) throw new Error('hourly_empty')

    // Index of last hour of the day (prefer 23:00)
    const tail = [`${selectedDateStr}T23:00`,`${selectedDateStr}T22:00`,`${selectedDateStr}T21:00`]
    let endIdx = -1
    for (const c of tail){ endIdx = times.lastIndexOf(c); if (endIdx !== -1) break }
    if (endIdx === -1) { endIdx = times.findLastIndex(t => t.startsWith(selectedDateStr)); if (endIdx === -1) endIdx = times.length - 1 }

    const lastVal = Number(vals[endIdx] ?? 0)
    isNowRaining = Number.isFinite(lastVal) && lastVal > 0

    const from24 = Math.max(0, endIdx - 23)
    const from48 = Math.max(0, endIdx - 47)
    for (let i=from24;i<=endIdx;i++) sum24 += Number(vals[i])||0
    for (let i=from48;i<=endIdx;i++) sum48 += Number(vals[i])||0
  } catch (e) {
    // Fallback to daily
    try {
      const url = new URL('https://api.open-meteo.com/v1/forecast')
      url.searchParams.set('latitude', lat)
      url.searchParams.set('longitude', lng)
      url.searchParams.set('daily', 'precipitation_sum,rain_sum')
      url.searchParams.set('start_date', ymdShift(selectedDateStr,-1))
      url.searchParams.set('end_date', selectedDateStr)
      url.searchParams.set('timezone', 'Australia/Sydney')
      const r = await fetch(url.toString())
      if (!r.ok) throw new Error('daily_failed')
      const d = await r.json()
      const sums = d?.daily?.precipitation_sum ?? d?.daily?.rain_sum ?? []
      const last = Number(sums[sums.length-1])||0
      const prev = Number(sums[sums.length-2])||0
      sum24 = last; sum48 = last + prev
      isNowRaining = last > 0
    } catch {
      sum24 = 0; sum48 = 0; isNowRaining = false
    }
  }

  const out = { sum24: +sum24.toFixed(1), sum48: +sum48.toFixed(1), isNowRaining, ts: Date.now() }
  rainCache.set(key, out); return out
}

/* ===== Fetch sea surface temperature ===== */
async function fetchTempFor(lat, lng, selectedDateStr){
  const key = `${lat.toFixed(2)},${lng.toFixed(2)},${selectedDateStr}`
  const cached = tempCache.get(key)
  if (cached && Date.now() - cached.ts < 10*60*1000) return cached

  const endDate = selectedDateStr
  const startDate = ymdShift(selectedDateStr, -1)
  let temp = null

  try {
    const url = new URL('https://api.open-meteo.com/v1/marine')
    url.searchParams.set('latitude', lat)
    url.searchParams.set('longitude', lng)
    url.searchParams.set('hourly', 'sea_surface_temperature')
    url.searchParams.set('start_date', startDate)
    url.searchParams.set('end_date', endDate)
    url.searchParams.set('timezone', 'Australia/Sydney')
    const res = await fetch(url.toString())
    if (!res.ok) throw new Error('sst_fetch_failed')
    const data = await res.json()
    const times = data?.hourly?.time ?? []
    const vals = data?.hourly?.sea_surface_temperature ?? []
    if (times.length && vals.length){
      let endIdx = times.findLastIndex(t => t.startsWith(selectedDateStr))
      if (endIdx === -1) endIdx = times.length - 1
      temp = Number(vals[endIdx])
      if (!Number.isFinite(temp)) temp = null
    } else { throw new Error('sst_hourly_empty') }
  } catch {
    try {
      const u = new URL('https://api.open-meteo.com/v1/marine')
      u.searchParams.set('latitude', lat)
      u.searchParams.set('longitude', lng)
      u.searchParams.set('daily', 'sea_surface_temperature_max,sea_surface_temperature_min')
      u.searchParams.set('start_date', startDate)
      u.searchParams.set('end_date', endDate)
      u.searchParams.set('timezone', 'Australia/Sydney')
      const r = await fetch(u.toString())
      if (!r.ok) throw new Error('sst_daily_failed')
      const d = await r.json()
      const maxs = d?.daily?.sea_surface_temperature_max ?? []
      const mins = d?.daily?.sea_surface_temperature_min ?? []
      if (maxs.length && mins.length){
        const lastAvg = (Number(maxs[maxs.length-1]) + Number(mins[mins.length-1])) / 2
        temp = Number.isFinite(lastAvg) ? +lastAvg.toFixed(1) : null
      }
    } catch { temp = null }
  }

  const out = { temp: temp!=null ? +Number(temp).toFixed(1) : null, ts: Date.now() }
  tempCache.set(key, out); return out
}

/* UI helpers */
function statusBadgeHtml(st){
  const text = st === 'red' ? 'Very Unsafe' : (st === 'amber' ? 'Caution' : 'Safe')
  const bg = st === 'red' ? '#fee2e2' : (st === 'amber' ? '#fef3c7' : '#dcfce7')
  const fg = st === 'red' ? '#b91c1c' : (st === 'amber' ? '#92400e' : '#166534')
  return `<span style="display:inline-block;padding:2px 8px;border-radius:12px;background:${bg};color:${fg};font-weight:600;font-size:12px">${text}</span>`
}
function tempFlagText(flag){
  if (flag==='red_cold') return 'Very cold'
  if (flag==='amber_cool') return 'Cool'
  if (flag==='amber_hot') return 'Hot'
  if (flag==='green_ok') return 'Comfortable'
  if (flag==='green_warm') return 'Warm'
  return 'N/A'
}

/* Draw map & pins */
async function drawSites() {
  const rows = await fetchSites()
  markersLayer?.clearLayers()
  const layer = L.layerGroup()
  const bounds = L.latLngBounds()

  rows.forEach(s => {
    const lat = Number(s.latitude), lng = Number(s.longitude)
    if (Number.isNaN(lat) || Number.isNaN(lng)) return

    const marker = L.circleMarker([lat, lng], {
      radius: 8, color: '#64748b', weight: 2,       fillColor: '#64748b', fillOpacity: 0.65
    })

    // Cache environment data: ensure popup and marker color are consistent
    let envData = null

    marker.bindPopup(`
      <div style="min-width:280px">
        <strong>${s.site_name ?? 'Unnamed beach'}</strong><br/>
        ${s.water_body ?? 'Port Phillip Bay'}<br/>
        <div style="margin-top:6px;font-size:13px;color:#334155">
          <em>Loading address…</em><br/>
          <em>Loading rainfall & water temperature…</em>
        </div>
        <a href="https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}" target="_blank" rel="noopener" style="color:#2563eb;text-decoration:underline;display:inline-block;margin-top:6px;">Open in Google Maps</a>
      </div>
    `)

    let addrFetched = false
    async function ensureAddress() {
      if (addrFetched) return ''
      addrFetched = true
      try { const r = await reverseGeocodeClient(lat, lng); return cleanAddress(r?.address || '') }
      catch { return '' }
    }

    async function ensureEnvAndUpdate(){
      if (envData) return envData
      try{
        const [rain, sst] = await Promise.all([
          fetchRainFor(lat, lng, selectedDate.value),
          fetchTempFor(lat, lng, selectedDate.value),
        ])
        const tflag = tempFlag(sst?.temp)
        const overall = computeOverall({ sum24: rain.sum24, sum48: rain.sum48, isNowRaining: rain.isNowRaining, tflag })
        envData = { sum24: rain.sum24, sum48: rain.sum48, isNowRaining: rain.isNowRaining, temp: sst?.temp, tflag, overall }
        const c = statusColor(envData.overall)
        marker.setStyle({ color: c, fillColor: c })
        return envData
      } catch {
        envData = { sum24: 0, sum48: 0, isNowRaining: false, temp: null, tflag: 'unknown', overall: 'green' }
        const c = statusColor(envData.overall)
        marker.setStyle({ color: c, fillColor: c })
        return envData
      }
    }

    marker.on('popupopen', async () => {
      const [shortAddr, env] = await Promise.all([ensureAddress(), ensureEnvAndUpdate()])
      const info = env
      const badge = statusBadgeHtml(info.overall)
      marker.setPopupContent(`
        <div style="min-width:280px">
          <strong>${s.site_name ?? 'Unnamed beach'}</strong> ${badge}<br/>
          ${s.water_body ?? 'Port Phillip Bay'}<br/>
          ${ shortAddr ? `<span style="font-size:13px;color:#334155">${shortAddr}</span>` : `<em style="font-size:13px;color:#64748b">Address unavailable</em>` }
          <div style="margin-top:6px;font-size:13px;color:#334155">
            Date: <b>${selectedDate.value}</b><br/>
            Rain 24h: <b>${info.sum24}mm</b> · 48h: <b>${info.sum48}mm</b><br/>
            Now raining: <b>${info.isNowRaining ? 'Yes' : 'No'}</b><br/>
            Water temp: <b>${info.temp!=null ? info.temp + '°C' : 'N/A'}</b> (${tempFlagText(info.tflag)})<br/>
            Overall: ${badge}
          </div>
          <a href="https://www.google.com/maps/dir/?api=1&destination=${lat},${lng}" target="_blank" rel="noopener" style="color:#2563eb;text-decoration:underline;display:inline-block;margin-top:6px;">Open in Google Maps</a>
        </div>
      `)
    })

    // Prefetch and color
    ensureEnvAndUpdate().catch(() => {})

    layer.addLayer(marker)
    bounds.extend([lat, lng])
    marker._meta = { name: (s.site_name || '').toLowerCase(), ll: { lat, lng } }
  })

  layer.addTo(map)
  markersLayer = layer
  if (bounds.isValid()) map.fitBounds(bounds.pad(0.1))
}

/* helpers */
function cleanAddress(addr) {
  if (!addr) return ''
  let s = String(addr)
  const drop = /^(australia|victoria|vic|new south wales|nsw|queensland|qld|south australia|sa|western australia|wa|tasmania|tas|northern territory|nt)$/i
  s = s.split(',').map(p => p.trim()).filter(p => p && !drop.test(p)).join(', ')
  return s.replace(/\s{2,}/g, ' ').trim()
}

async function handleSearch() {
  const q = kw.value.trim()
  if (!q || !markersLayer) return
  const lower = q.toLowerCase()
  const matched = []
  markersLayer.eachLayer(m => { if (m._meta?.name?.includes(lower)) matched.push(m) })
  if (matched.length) {
    const b = L.latLngBounds(matched.map(m => m.getLatLng()))
    map.fitBounds(b.pad(0.15)); matched[0].openPopup()
    if (pinAtQuery) { map.removeLayer(pinAtQuery); pinAtQuery = null }
    return
  }
  const pt = await forwardGeocodeClient(q).catch(() => null)
  if (!pt) { alert('No result for this query.'); return }
  if (pinAtQuery) map.removeLayer(pinAtQuery)
  pinAtQuery = L.circle([pt.lat, pt.lng], { radius: 800, color: '#0ea5e9', weight: 2, fillOpacity: 0 }).addTo(map)
  const items = []
  markersLayer.eachLayer(m => {
    const ll = m.getLatLng()
    items.push({ m, d: haversine(ll.lat, ll.lng, pt.lat, pt.lng) })
  })
  items.sort((a,b)=>a.d-b.d)
  const top = items.slice(0,8).map(x=>x.m)
  const b = L.latLngBounds(top.map(m=>m.getLatLng()))
  if (b.isValid()) map.fitBounds(b.pad(0.2))
  top[0]?.openPopup()
}

function haversine(a,b,c,d){ const R=6371, toRad=x=>x*Math.PI/180
  const dLat=toRad(c-a), dLon=toRad(d-b)
  const aa = Math.sin(dLat/2)**2 + Math.cos(toRad(a))*Math.cos(toRad(c))*Math.sin(dLon/2)**2
  return 2*R*Math.atan2(Math.sqrt(aa), Math.sqrt(1-aa))
}

onMounted(async () => {
  map = L.map(mapEl.value, { zoomControl: true }).setView([-38.05, 144.9], 10)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19, attribution: '© OpenStreetMap'
  }).addTo(map)
  try { await drawSites() } catch (e) { console.error('drawSites failed:', e) }
})
</script>

<style scoped>
.btn-chip-blue { @apply inline-flex items-center rounded-xl bg-sky-600 px-4 py-2 text-sm sm:text-base font-semibold text-white hover:bg-sky-700 transition; }
.btn-ui{ @apply rounded-xl bg-white/90 px-3 py-1.5 text-sm font-semibold text-slate-900 ring-1 ring-slate-300 hover:bg-white transition; }
.legend-card{ @apply rounded-xl border px-4 py-3; }
.feature-card{ @apply rounded-2xl shadow-md overflow-hidden; }
.feature-card-body{ @apply p-6 text-white; }
.feature-card-btn{ @apply inline-flex items-center rounded-xl bg-white/90 px-4 py-2 text-sm font-semibold text-slate-900 hover:bg-white transition; }
.chip-trusted{ @apply inline-flex items-center rounded-lg bg-slate-100 px-3 py-1.5 text-xs font-semibold text-slate-800 ring-1 ring-slate-200; }
</style>
