<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <!-- 顶部：选择器 + 标题 -->
    <section class="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8 py-6">
      <div class="flex flex-wrap items-center gap-3">
        <label class="text-sm">Select Year:</label>
        <select v-model.number="year" class="control w-28">
          <option v-for="y in availableYears" :key="y" :value="y">{{ y }}</option>
        </select>

        <label class="text-sm ml-2">Select Month:</label>
        <select v-model.number="month" class="control w-36">
          <option v-for="m in 12" :key="m" :value="m">{{ MONTH_NAMES[m] }}</option>
        </select>

        <label class="text-sm ml-2">Lake:</label>
        <select v-model="activeLake" class="control w-56">
          <option v-for="n in lakeNames" :key="n" :value="n">{{ n }}</option>
        </select>
      </div>

      <h1 class="mt-4 text-2xl sm:text-3xl font-extrabold">
        Historical Recreational Water Quality for Gippsland Lakes in {{ MONTH_NAMES[month] }} {{ year }}
      </h1>
      <p class="text-slate-600">
        Comprehensive water quality analysis and recreational suitability assessment
      </p>
    </section>

    <!-- 地图 -->
    <section class="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
      <div ref="mapEl" class="h-[520px] w-full rounded-xl border border-slate-200 shadow-sm overflow-hidden"></div>
    </section>

    <!-- 第二组图：指标表 + Summary -->
    <section class="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8 mt-8 pb-16">
      <div class="rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div class="px-4 sm:px-6 pt-6">
          <h2 class="text-xl sm:text-2xl font-bold">
            Recreational Water Quality Indicators and Analysis for {{ activeLake }} in {{ MONTH_NAMES[month] }} {{ year }}
          </h2>
        </div>

        <div class="p-4 sm:p-6">
          <div class="overflow-x-auto">
            <table class="min-w-full text-sm rounded-lg overflow-hidden">
              <thead class="bg-slate-800 text-white">
                <tr>
                  <th class="px-3 py-3 text-left font-semibold">Year</th>
                  <th class="px-3 py-3 text-left font-semibold">pH</th>
                  <th class="px-3 py-3 text-left font-semibold">Dissolved Oxygen (%)</th>
                  <th class="px-3 py-3 text-left font-semibold">Temperature (°C)</th>
                  <th class="px-3 py-3 text-left font-semibold">Algae Level</th>
                  <th class="px-3 py-3 text-left font-semibold">Recreational Alerts?</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(r, idx) in tableRows" :key="r.year + '-' + idx" class="border-b last:border-0">
                  <td class="px-3 py-2 font-medium">{{ r.year }}</td>
                  <td class="px-3 py-2">
                    <span :class="pill(r.ph_category)">{{ r.ph?.toFixed(1) ?? '—' }}</span>
                  </td>
                  <td class="px-3 py-2">
                    <span :class="pill(r.do_category)">{{ r.do_pct?.toFixed(0) ?? '—' }}%</span>
                  </td>
                  <td class="px-3 py-2">
                    <span :class="pill(r.temp_category)">{{ r.temp_c?.toFixed(0) ?? '—' }}°C</span>
                  </td>
                  <td class="px-3 py-2">
                    <span :class="algaeTag(r.level)">{{ r.level ?? 'UNK' }}</span>
                  </td>
                  <td class="px-3 py-2">
                    <span :class="alertTag(r.alert)">
                      <template v-if="r.alert === 'Yes'">⚠️ Yes</template>
                      <template v-else-if="r.alert === 'Caution'">⚠️ Caution</template>
                      <template v-else>🟢 No</template>
                    </span>
                  </td>
                </tr>
                <tr v-if="!tableRows.length">
                  <td colspan="6" class="px-3 py-6 text-center text-slate-500">
                    No record for {{ activeLake }} in {{ MONTH_NAMES[month] }} {{ year }}.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="flex flex-wrap gap-4 mt-3 text-xs text-slate-500">
            <span><span class="legend-pill bg-blue-100 text-blue-700">Below range</span></span>
            <span><span class="legend-pill bg-rose-100 text-rose-700">Above range</span></span>
            <span><span class="legend-pill bg-emerald-100 text-emerald-700">Low algae</span></span>
            <span><span class="legend-pill bg-amber-100 text-amber-700">Moderate algae</span></span>
            <span><span class="legend-pill bg-green-700 text-white">High algae</span></span>
          </div>
        </div>
      </div>

      <!-- Summary（按所选湖泊+月份，统计“最近5年(<=所选年)”的适宜占比） -->
      <div class="mt-8 flex justify-center">
        <div class="w-full sm:w-[680px] rounded-2xl bg-gradient-to-b from-sky-400 to-sky-600 text-white shadow-lg p-6 text-center">
          <h3 class="text-lg font-semibold tracking-wide">Water Quality Summary</h3>
          <p class="mt-2 text-xl">
            Overall, {{ activeLake }} in {{ MONTH_NAMES[month] }} (up to {{ year }}) is
            <span class="font-extrabold">{{ suitabilityPct.toFixed(0) }}%</span>
            suitable for recreational activities.
          </p>
          <p class="mt-1 text-sm opacity-90">
            (Low + Moderate / records in the last 5 years for this month & lake, up to selected year.)
          </p>
        </div>
      </div>

      <div class="mt-6 text-center text-sm text-slate-500">
        For real-time algal bloom notifications, visit DEECA Gippsland Facebook
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch, computed } from "vue";
import * as L from "leaflet";
import "leaflet/dist/leaflet.css";

// 用 ?url + fetch 解析本地 GeoJSON（避免 Unexpected token ':'）
import lakesGeoUrl from "@/assets/geo/gippsland_lakes.geojson?url";

/** 月份名 */
const MONTH_NAMES: Record<number, string> = {
  1:"January",2:"February",3:"March",4:"April",5:"May",6:"June",
  7:"July",8:"August",9:"September",10:"October",11:"November",12:"December"
};

/** 颜色 & 等级 */
type Level = "Low"|"Moderate"|"High"|"UNK";
const COLORS: Record<Level, string> = {
  Low:"#22c55e", Moderate:"#f59e0b", High:"#ef4444", UNK:"#9ca3af"
};

/** 湖区 ↔ 站点 */
const LAKE_SITE_MAP: Record<string, number> = {
  "Lake Wellington": 2306,
  "Lake Victoria": 2316,
  "Lake King North": 2314,
  "Lake King South": 2311
};
const lakeNames = Object.keys(LAKE_SITE_MAP);
const activeLake = ref<string>(lakeNames[3]); // 默认 Lake King South
const month = ref<number>(4);                 // 默认 April（可改）
/** 数据（节选自你提供的数据；可继续补充） */
type Row = {
  site_id:number; year:number; month:number;
  temp_c?:number; do_pct?:number; ph?:number;
  level?: "Low"|"Moderate"|"High";
  alert?: "Yes"|"No"|"Caution";
};
const DATA: Row[] = [
  // 2311 Lake King South — Dec (示例多年的历史)
  { site_id:2311, year:2020, month:12, temp_c:20.2003, do_pct:111.48, ph:8.723, level:"Low",       alert:"No" },
  { site_id:2311, year:2021, month:12, temp_c:20.1044, do_pct:98.63,  ph:8.557, level:"Moderate",  alert:"Caution" },
  { site_id:2311, year:2022, month:12, temp_c:19.3987, do_pct:103.72, ph:8.069, level:"Low",       alert:"No" },
  { site_id:2311, year:2023, month:12, temp_c:21.3924, do_pct:95.28,  ph:7.512, level:"Moderate",  alert:"No" },

  // 2306 Lake Wellington — 2024/2–4, 2025/1–2（演示 High/Moderate/Low）
  { site_id:2306, year:2024, month:2, temp_c:21.0321, do_pct:97.97,  ph:7.858, level:"Moderate", alert:"No" },
  { site_id:2306, year:2024, month:3, temp_c:18.3697, do_pct:104.06, ph:7.946, level:"High",     alert:"Yes" },
  { site_id:2306, year:2024, month:4, temp_c:14.4728, do_pct:101.45, ph:8.054, level:"Low",      alert:"No" },
  { site_id:2306, year:2025, month:1, temp_c:24.2134, do_pct:101.82, ph:8.161, level:"High",     alert:"Yes" },
  { site_id:2306, year:2025, month:2, temp_c:21.9806, do_pct:109.01, ph:8.119, level:"High",     alert:"Yes" },

  // 2316 Lake Victoria — 2024/2–5 低
  { site_id:2316, year:2024, month:2, temp_c:22.5563, do_pct:134.35, ph:8.45,  level:"Low", alert:"No" },
  { site_id:2316, year:2024, month:3, temp_c:20.4148, do_pct:113.36, ph:8.02,  level:"Low", alert:"No" },
  { site_id:2316, year:2024, month:4, temp_c:15.6885, do_pct:99.55,  ph:8.016, level:"Low", alert:"No" },
  { site_id:2316, year:2024, month:5, temp_c:13.9834, do_pct:110.39, ph:7.672, level:"Low", alert:"No" },

  // 2314 Lake King North — 2024/2–5 低
  { site_id:2314, year:2024, month:2, temp_c:21.2436, do_pct:110.83, ph:8.147, level:"Low", alert:"No" },
  { site_id:2314, year:2024, month:3, temp_c:19.0884, do_pct:103.83, ph:7.85,  level:"Low", alert:"No" },
  { site_id:2314, year:2024, month:4, temp_c:15.4707, do_pct:97.75,  ph:8.062, level:"Low", alert:"No" },
  { site_id:2314, year:2024, month:5, temp_c:13.4907, do_pct:103.5,  ph:7.552, level:"Low", alert:"No" },
];

// 下拉年份（自动从 DATA 汇总）
const availableYears = computed(()=>{
  const s = new Set<number>();
  for(const r of DATA) s.add(r.year);
  return Array.from(s).sort((a,b)=>b-a); // 倒序，最新在前
});
const year = ref<number>(availableYears.value[0] ?? new Date().getFullYear());

// —— 地图颜色：按“所选 年+月”的记录着色（没有则 UNK）
function levelFor(lake:string, y:number, m:number):Level{
  const site = LAKE_SITE_MAP[lake];
  const row = DATA.find(r=>r.site_id===site && r.year===y && r.month===m);
  return (row?.level ?? "UNK") as Level;
}

/** —— Leaflet：左上角 Legend Control —— */
function buildLegendControl(){
  const Legend = L.Control.extend({
    options:{ position:"topleft" },
    onAdd(){
      const div = L.DomUtil.create("div","leaflet-control legend-card");
      div.innerHTML = `
        <div class="legend-title">Algae Levels</div>
        <div class="legend-row"><span class="legend-dot" style="background:${COLORS.Low}"></span>Low</div>
        <div class="legend-row"><span class="legend-dot" style="background:${COLORS.Moderate}"></span>Moderate</div>
        <div class="legend-row"><span class="legend-dot" style="background:${COLORS.High}"></span>High</div>
      `;
      return div;
    }
  });
  return new Legend();
}

const mapEl = ref<HTMLDivElement|null>(null);
let map:L.Map|undefined;
let gj:L.GeoJSON|undefined;

function styleFn(feat:any):L.PathOptions{
  const name = feat?.properties?.lake_name ?? "UNKNOWN";
  const lvl = levelFor(name, year.value, month.value);
  return { color:"#334155", weight:1, fillOpacity:0.6, fillColor: COLORS[lvl] };
}
function onEach(feat:any, layer:L.Layer){
  const name = feat?.properties?.lake_name ?? "UNKNOWN";
  const lvl = levelFor(name, year.value, month.value);
  (layer as any).bindTooltip(
    `<div style="font-weight:600">${name}</div>
     <div>When: ${MONTH_NAMES[month.value]} ${year.value}</div>
     <div>Level: <b>${lvl}</b></div>`,
    { sticky:true }
  );
  layer.on("click", ()=>{ activeLake.value = name; });
}

onMounted(async ()=>{
  map = L.map(mapEl.value as HTMLDivElement, { zoomControl:true, attributionControl:false })
        .setView([-38.0,147.55],9);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",{maxZoom:18}).addTo(map);

  const resp = await fetch(lakesGeoUrl);
  const geojson = await resp.json();
  gj = L.geoJSON(geojson, { style:styleFn, onEachFeature:onEach }).addTo(map);
  map.fitBounds(gj.getBounds(), { padding:[10,10] });

  buildLegendControl().addTo(map);
});

// 年或月改变 -> 重新上色
watch([year, month], ()=>{
  if(gj) gj.setStyle(styleFn as any);
});

/** —— 第二组图：表格（只显示“所选 年+月”的记录，若无则空） —— */
const tableRows = computed(()=>{
  const site = LAKE_SITE_MAP[activeLake.value];
  const rows = DATA
    .filter(r=>r.site_id===site && r.month===month.value && r.year===year.value)
    .sort((a,b)=>b.year-a.year);

  return rows.map(r=>{
    // 简单区间：pH理想 6.5~8.5；DO理想 80~120；温度 10~30（演示用）
    const ph_category  = r.ph     == null ? "muted" : r.ph < 6.5 ? "below" : r.ph > 8.5 ? "above" : "ok";
    const do_category  = r.do_pct == null ? "muted" : r.do_pct < 80 ? "below" : r.do_pct > 120 ? "above" : "ok";
    const temp_category= r.temp_c == null ? "muted" : r.temp_c < 10 ? "below" : r.temp_c > 30 ? "above" : "ok";
    const alert = r.alert ?? (r.level==="High" ? "Yes" : r.level==="Moderate" ? "Caution" : "No");
    return { ...r, ph_category, do_category, temp_category, alert };
  });
});

/** —— Summary：按所选湖泊+月份，统计“近5年(<=所选年)”的适宜占比（Low/Moderate 记入） —— */
const suitabilityPct = computed(()=>{
  const site = LAKE_SITE_MAP[activeLake.value];
  const from = year.value - 4; // 包含所选年在内，最多5年窗口
  const sample = DATA
    .filter(r=>r.site_id===site && r.month===month.value && r.year<=year.value && r.year>=from);
  if(!sample.length) return 0;
  const good = sample.filter(r=>r.level==="Low" || r.level==="Moderate").length;
  return good / sample.length * 100;
});

/** —— 表格标签样式工具 —— */
function pill(cat:"ok"|"below"|"above"|"muted"){
  if(cat==="ok")    return "inline-flex items-center px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700";
  if(cat==="below") return "inline-flex items-center px-2 py-0.5 rounded-full bg-blue-100 text-blue-700";
  if(cat==="above") return "inline-flex items-center px-2 py-0.5 rounded-full bg-rose-100 text-rose-700";
  return "inline-flex items-center px-2 py-0.5 rounded-full bg-slate-100 text-slate-500";
}
function algaeTag(level?:Level){
  if(level==="Low")       return "inline-flex items-center px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-700";
  if(level==="Moderate")  return "inline-flex items-center px-2 py-0.5 rounded-md bg-amber-100 text-amber-700";
  if(level==="High")      return "inline-flex items-center px-2 py-0.5 rounded-md bg-green-700 text-white";
  return "inline-flex items-center px-2 py-0.5 rounded-md bg-slate-100 text-slate-500";
}
function alertTag(a:"Yes"|"No"|"Caution"){
  if(a==="Yes")      return "inline-flex items-center px-2 py-0.5 rounded-md bg-rose-100 text-rose-700";
  if(a==="Caution")  return "inline-flex items-center px-2 py-0.5 rounded-md bg-amber-100 text-amber-700";
  return "inline-flex items-center px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-700";
}
</script>

<style scoped>
.control { @apply h-9 rounded-md border border-slate-300 bg-white px-2 text-sm; }
.legend-pill { @apply inline-flex items-center px-2 py-0.5 rounded-full; }

/* Leaflet 自定义图例卡片（左上角） */
.legend-card{
  background: #ffffff;
  border: 1px solid rgba(15,23,42,0.12);
  border-radius: 12px;
  padding: 10px 12px;
  box-shadow: 0 6px 24px rgba(15,23,42,0.08);
  font-size: 12px;
  color: #334155;
}
.legend-title{ font-weight: 700; margin-bottom: 6px; }
.legend-row{ display:flex; align-items:center; gap:8px; line-height: 18px; }
.legend-dot{
  width: 12px; height: 12px; border-radius: 9999px;
  border: 1px solid rgba(0,0,0,0.15); display:inline-block;
}
</style>
