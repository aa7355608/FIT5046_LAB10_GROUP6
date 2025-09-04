<template>
  <div class="min-h-screen bg-white">
    <!-- 只保留地图工具条 + 地图卡片；不再有任何外围文案 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <!-- 顶部工具条（保留原有占位/按钮/交互与文本） -->
      <div
        style="display:flex; flex-wrap:wrap; gap:8px; align-items:center;
               border:1px solid #e5e7eb; background:#fff; border-radius:16px; padding:8px 10px;"
      >
        <div
          style="flex:1; display:flex; gap:6px; align-items:center; border:1px solid #e5e7eb; border-radius:12px; padding:6px 8px;"
        >
          <input
            v-model="kw"
            @keyup.enter="handleSearch"
            placeholder="Suburb/postcode or beach name..."
            style="border:0; outline:none; width:100%;"
          />
          <button class="chip" @click="handleSearch">Search</button>
          <button class="icon" title="options">⚙️</button>
          <button class="icon" title="fullscreen" @click="toggleFullscreen">⤢</button>
        </div>
      </div>

      <!-- 地图卡片（地图内所有文案与弹窗保持原样） -->
      <div
        style="position:relative; margin-top:12px; border:1px solid #e5e7eb; border-radius:18px; overflow:hidden;"
      >
        <div ref="mapEl" style="height:68vh; min-height:420px; width:100%;"></div>

        <!-- 右下角按钮（保留原文案） -->
        <div style="position:absolute; right:12px; bottom:12px; display:flex; gap:8px;">
          <button class="btn" @click="showDisclaimer = !showDisclaimer">Disclaimer</button>
          <button class="btn" @click="openLegend">View pin legend</button>
        </div>

        <!-- 简易浮层（保留原文案） -->
        <div
          v-if="showDisclaimer"
          style="position:absolute; right:12px; bottom:60px; background:#fff; border:1px solid #e5e7eb; border-radius:12px; padding:10px; width:280px;"
        >
          <b>Notes</b>
          <p style="font-size:12px; line-height:1.4; color:#475569">
            Type a <b>beach name</b> (e.g. “Safety Beach”) or a <b>postcode</b> (e.g. 3934).<br />
            Name → focus matching pins; Postcode → geocode then show nearest beaches.
          </p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import * as L from 'leaflet'
import { fetchSites } from '@/services/dbApi'
import { reverseGeocodeClient, forwardGeocodeClient } from '@/services/revgeoClient'

const kw = ref('')
const showDisclaimer = ref(false)

const mapEl = ref(null)
let map, markersLayer
let pinAtQuery = null // 临时显示“邮编位置”的圈

// 兼容 Vite 的默认图标（否则小蓝针可能不显示）
const DefaultIcon = L.icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [0, -28],
  shadowSize: [41, 41],
})
L.Marker.prototype.options.icon = DefaultIcon

function statusColor(s) {
  const t = (s || 'green').toLowerCase()
  if (t === 'green') return '#22c55e'
  if (t === 'amber') return '#f59e0b'
  return '#ef4444'
}

function toggleFullscreen() {
  const el = mapEl.value
  if (!document.fullscreenElement) el.requestFullscreen?.()
  else document.exitFullscreen?.()
}

function haversine(lat1, lon1, lat2, lon2) {
  const toRad = d => d * Math.PI / 180
  const R = 6371 // km
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLon / 2) ** 2
  return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

async function drawSites() {
  const rows = await fetchSites() // [{ site_id, site_name, water_body, latitude, longitude }, ...]

  markersLayer?.clearLayers()
  const layer = L.layerGroup()
  const bounds = L.latLngBounds()

  rows.forEach(s => {
    const lat = Number(s.latitude)
    const lng = Number(s.longitude)
    if (Number.isNaN(lat) || Number.isNaN(lng)) return

    const color = statusColor(s.status || 'green')
    const marker = L.circleMarker([lat, lng], {
      radius: 8,
      color,
      weight: 2,
      fillColor: color,
      fillOpacity: 0.65
    })

    // 初始弹窗与懒加载反地理编码 —— 全部保留原文案
    marker.bindPopup(`
      <div style="min-width:220px">
        <strong>${s.site_name ?? 'Unnamed beach'}</strong><br/>
        ${(s.water_body ?? 'Port Phillip Bay')}<br/>
        <em style="color:#64748b">Loading address…</em>
      </div>
    `)

    let fetched = false
    marker.on('popupopen', async () => {
      if (fetched) return
      fetched = true
      try {
        const r = await reverseGeocodeClient(lat, lng)
        marker.setPopupContent(`
          <div style="min-width:220px">
            <strong>${s.site_name ?? 'Unnamed beach'}</strong><br/>
            ${(s.water_body ?? 'Port Phillip Bay')}<br/>
            ${
              r?.address
                ? `<span>${r.address}</span>`
                : `<em style="color:#64748b">Address unavailable</em>`
            }
          </div>
        `)
      } catch {
        marker.setPopupContent(`
          <div style="min-width:220px">
            <strong>${s.site_name ?? 'Unnamed beach'}</strong><br/>
            ${(s.water_body ?? 'Port Phillip Bay')}<br/>
            <em style="color:#64748b">Address unavailable</em>
          </div>
        `)
      }
    })

    layer.addLayer(marker)
    bounds.extend([lat, lng])

    // 给搜索用的元信息
    marker._meta = {
      name: (s.site_name || '').toLowerCase(),
      ll: { lat, lng }
    }
  })

  layer.addTo(map)
  markersLayer = layer
  if (bounds.isValid()) map.fitBounds(bounds.pad(0.1))
}

// 名称或邮编搜索（保留原文案逻辑）
async function handleSearch() {
  const q = kw.value.trim()
  if (!q) return

  const lower = q.toLowerCase()
  const matched = []

  // 1) 名称模糊匹配
  markersLayer?.eachLayer(m => {
    if (m._meta?.name?.includes(lower)) matched.push(m)
  })

  if (matched.length > 0) {
    const b = L.latLngBounds(matched.map(m => m.getLatLng()))
    map.fitBounds(b.pad(0.15))
    matched[0].openPopup()
    if (pinAtQuery) {
      map.removeLayer(pinAtQuery)
      pinAtQuery = null
    }
    return
  }

  // 2) 邮编 / 地点 → 正向地理编码
  const pt = await forwardGeocodeClient(q)
  if (!pt) {
    alert('No result for this query.')
    return
  }

  // 临时圈出邮编位置
  if (pinAtQuery) map.removeLayer(pinAtQuery)
  pinAtQuery = L.circle([pt.lat, pt.lng], {
    radius: 800,
    color: '#0ea5e9',
    weight: 2,
    fillOpacity: 0
  }).addTo(map)

  // 最近站点并缩放
  const items = []
  markersLayer?.eachLayer(m => {
    const ll = m.getLatLng()
    const d = haversine(ll.lat, ll.lng, pt.lat, pt.lng)
    items.push({ m, d })
  })
  items.sort((a, b) => a.d - b.d)

  const top = items.slice(0, 8).map(x => x.m)
  const b = L.latLngBounds(top.map(m => m.getLatLng()))
  if (b.isValid()) map.fitBounds(b.pad(0.2))
  top[0]?.openPopup()
}

function openLegend() {
  alert('Legend:\n● Green = Safe\n● Amber = Caution\n● Red = Unsafe')
}

onMounted(async () => {
  map = L.map(mapEl.value, { zoomControl: true }).setView([-38.05, 144.9], 10)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
  }).addTo(map)

  try {
    await drawSites()
  } catch (e) {
    console.error('fetchSites failed:', e)
    alert('Failed to load sites from API.')
  }
})
</script>

<style scoped>
/* 沿用你原来的按钮样式 */
.pill{ border:1px solid #e5e7eb; background:#f8fafc; border-radius:12px; padding:8px 10px; font-size:14px; font-weight:600; }
.pill .muted{ color:#64748b; font-weight:500; margin-left:6px; }
.pill.active.water{ border:2px solid #0ea5e9; background:#0369a1; color:#fff; }
.pill.active.incidents{ border:2px solid #fb7185; background:#e11d48; color:#fff; }
.chip{ border:0; background:#e5e7eb; border-radius:10px; padding:6px 8px; cursor:pointer; }
.icon{ border:0; background:#f1f5f9; border-radius:8px; padding:4px 6px; cursor:pointer; }
.btn{
  background:rgba(255,255,255,.92); border:1px solid #cbd5e1;
  border-radius:12px; padding:8px 12px; font-size:14px; color:#0f172a;
  box-shadow:0 8px 20px -10px rgba(0,0,0,.25);
}
.btn:hover{ background:#fff; }
</style>
