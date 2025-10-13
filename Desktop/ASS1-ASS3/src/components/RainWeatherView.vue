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

    <!-- 指标表 + Summary -->
    <section class="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8 mt-8 pb-16">
      <div class="rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div class="px-4 sm:px-6 pt-6">
          <h2 class="text-xl sm:text-2xl font-bold">
            Recreational Water Quality Indicators and Analysis for {{ activeLake }} in {{ MONTH_NAMES[month] }}
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
                <tr v-for="r in tableRows" :key="r.year" class="border-b last:border-0">
                  <td class="px-3 py-2 font-medium">{{ r.year }}</td>
                  <td class="px-3 py-2"><span :class="pill(r.ph_category)">{{ r.ph ?? '—' }}</span></td>
                  <td class="px-3 py-2"><span :class="pill(r.do_category)">{{ r.dosat ?? '—' }}</span></td>
                  <td class="px-3 py-2"><span :class="pill(r.temp_category)">{{ r.temp ?? '—' }}</span></td>
                  <td class="px-3 py-2"><span :class="algaeTag(r.level)">{{ r.level }}</span></td>
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
                    No record for {{ activeLake }} in {{ MONTH_NAMES[month] }} (last 5y up to {{ year }}).
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

      <!-- Summary：True / 全部 -->
      <div class="mt-8 flex justify-center">
        <div class="w-full sm:w-[680px] rounded-2xl bg-gradient-to-b from-sky-400 to-sky-600 text-white shadow-lg p-6 text-center">
          <h3 class="text-lg font-semibold tracking-wide">Water Quality Summary</h3>
          <p class="mt-2 text-xl">
            Overall, {{ activeLake }} in {{ MONTH_NAMES[month] }} {{ year }} is
            <span class="font-extrabold">{{ suitabilityPct.toFixed(0) }}%</span>
            suitable for recreational activities.
          </p>
          <p class="mt-1 text-sm opacity-90">
            (qualified=True / all tests for selected lake & month & year)
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
/* ---------------- Imports ---------------- */
import { ref, computed, onMounted, watch } from "vue";
import * as L from "leaflet";
import "leaflet/dist/leaflet.css";
import lakesGeoUrl from "@/assets/geo/gippsland_lakes.geojson?url";
import csvUrl from "@/assets/data/water_quality.csv?url";

/* ---------------- Constants ---------------- */
type Level = "Low" | "Moderate" | "High" | "UNK";
const COLORS: Record<Level, string> = {
  Low: "#22c55e",
  Moderate: "#f59e0b",
  High: "#ef4444",
  UNK: "#9ca3af",
};
const MONTH_NAMES: Record<number, string> = {
  1: "January", 2: "February", 3: "March", 4: "April", 5: "May", 6: "June",
  7: "July", 8: "August", 9: "September", 10: "October", 11: "November", 12: "December",
};
/* Site ↔ Lake name */
const LAKE_SITE_MAP: Record<string, number> = {
  "Lake Wellington": 2306,
  "Lake Victoria": 2316,
  "Lake King North": 2314,
  "Lake King South": 2311,
};
const SITE_LAKE_MAP: Record<number, string> = Object.fromEntries(
  Object.entries(LAKE_SITE_MAP).map(([k, v]) => [v, k])
);

/* ---------------- Core reactive state (DECLARE FIRST!) ---------------- */
const lakeNames = Object.keys(LAKE_SITE_MAP);
const activeLake = ref<string>(lakeNames[3]);   // Default Lake King South
const month = ref<number>(4);                   // Default April
const year = ref<number>(2025);                 // Default 2025, will be corrected after CSV loads

/* ---------------- CSV loading & parsing ---------------- */
type CsvRow = {
  site_id: number;
  year: number;
  month: number;
  temp?: number | null;
  dosat?: number | null;
  ph?: number | null;
  algae?: string | null;
  alert?: string | null;
  qualified: boolean;
};

const rows = ref<CsvRow[]>([]);

/* Simple CSV parsing (supports quotes and \r\n), returns 2D array */
function parseCSV(text: string): string[][] {
  const out: string[][] = [];
  let i = 0, field = "", row: string[] = [], inQ = false;
  while (i < text.length) {
    const c = text[i];
    if (inQ) {
      if (c === '"' && text[i + 1] === '"') { field += '"'; i += 2; continue; }
      if (c === '"') { inQ = false; i++; continue; }
      field += c; i++; continue;
    } else {
      if (c === '"') { inQ = true; i++; continue; }
      if (c === ",") { row.push(field); field = ""; i++; continue; }
      if (c === "\n" || c === "\r") {
        if (field.length || row.length) { row.push(field); out.push(row); }
        field = ""; row = [];
        if (c === "\r" && text[i + 1] === "\n") i += 2; else i++;
        continue;
      }
      field += c; i++; continue;
    }
  }
  if (field.length || row.length) { row.push(field); out.push(row); }
  return out.filter(r => r.some(c => c.trim().length));
}
const toNum = (s?: string | null) => {
  if (s == null) return null;
  const t = s.trim();
  if (!t) return null;
  const n = Number(t);
  return Number.isFinite(n) ? n : null;
};
const truthy = (v?: string | null) => {
  const s = (v ?? "").trim().toLowerCase();
  return s === "true" || s === "yes" || s === "y" || s === "1" || s === "t";
};

onMounted(async () => {
  // 地图初始化
  map = L.map(mapEl.value as HTMLDivElement, { zoomControl: true, attributionControl: false })
    .setView([-38.0, 147.55], 9);
  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", { maxZoom: 18 }).addTo(map);

  // GeoJSON
  const gjResp = await fetch(lakesGeoUrl);
  const gjData = await gjResp.json();
  gj = L.geoJSON(gjData, { style: styleFn, onEachFeature: onEach }).addTo(map);
  map.fitBounds(gj.getBounds(), { padding: [10, 10] });
  buildLegendControl().addTo(map);

  // CSV
  try {
    const t = await (await fetch(csvUrl)).text();
    const grid = parseCSV(t);
    const header = grid[0].map((h) => h.trim().toLowerCase());

    const idxSite = header.indexOf("gipps_lake_quality_site");
    const idxYear = header.indexOf("gipps_lake_quality_year");
    const idxMon = header.indexOf("gipps_lake_quality_month");
    const idxTemp = header.indexOf("gipps_lake_quality_temp");
    const idxDo = header.indexOf("gipps_lake_quality_dosat");
    const idxPh = header.indexOf("gipps_lake_quality_ph");
    const idxAlg = header.indexOf("gipps_lake_algae");
    const idxAlert = header.indexOf("gipps_lake_quality_alert");
    const idxQual = header.indexOf("gipps_lake_quality_qualified");

    const list: CsvRow[] = [];
    for (let r = 1; r < grid.length; r++) {
      const row = grid[r];
      const site = Number(row[idxSite]);
      const y = Number(row[idxYear]);
      const m = Number(row[idxMon]);
      if (!Number.isFinite(site) || !Number.isFinite(y) || !Number.isFinite(m)) continue;

      list.push({
        site_id: site,
        year: y,
        month: m,
        temp: toNum(row[idxTemp]),
        dosat: toNum(row[idxDo]),
        ph: toNum(row[idxPh]),
        algae: (row[idxAlg] ?? "").trim() || null,
        alert: (row[idxAlert] ?? "").trim() || null,
        qualified: truthy(row[idxQual]),
      });
    }
    rows.value = list;

    // Default year: maximum year in CSV
    const ys = Array.from(new Set(list.map((r) => r.year))).sort((a, b) => b - a);
    if (ys.length) year.value = ys[0];
  } catch (e) {
    console.error("Failed to load water_quality.csv:", e);
  }
});

/* ---------------- Available years ---------------- */
const availableYears = computed(() => {
  const ys = new Set<number>();
  rows.value.forEach((r) => ys.add(r.year));
  return Array.from(ys).sort((a, b) => b - a);
});

/* ---------------- Map (Leaflet) ---------------- */
const mapEl = ref<HTMLDivElement | null>(null);
let map: L.Map | undefined;
let gj: L.GeoJSON | undefined;

const normLevel = (algae?: string | null): Level => {
  const s = (algae ?? "").trim().toLowerCase();
  if (s === "low") return "Low";
  if (s === "medium" || s === "moderate") return "Moderate";
  if (s === "high") return "High";
  return "UNK";
};

function levelFor(lakeName: string, y: number, m: number): Level {
  const site = LAKE_SITE_MAP[lakeName];
  const row = rows.value.find((r) => r.site_id === site && r.year === y && r.month === m);
  return normLevel(row?.algae);
}

function styleFn(feature: any): L.PathOptions {
  const name = feature?.properties?.lake_name ?? "UNKNOWN";
  const lvl = levelFor(name, year.value, month.value);
  return { color: "#334155", weight: 1, fillOpacity: 0.6, fillColor: COLORS[lvl] };
}
function onEach(feature: any, layer: L.Layer) {
  const name = feature?.properties?.lake_name ?? "UNKNOWN";
  const lvl = levelFor(name, year.value, month.value);
  (layer as any).bindTooltip(
    `<div style="font-weight:600">${name}</div>
     <div>When: ${MONTH_NAMES[month.value]} ${year.value}</div>
     <div>Level: <b>${lvl}</b></div>`,
    { sticky: true }
  );
  layer.on("click", () => (activeLake.value = name));
}
function buildLegendControl() {
  const Legend = L.Control.extend({
    options: { position: "topleft" },
    onAdd() {
      const div = L.DomUtil.create("div", "leaflet-control legend-card");
      div.innerHTML = `
        <div class="legend-title">Algae Levels</div>
        <div class="legend-row"><span class="legend-dot" style="background:${COLORS.Low}"></span>Low</div>
        <div class="legend-row"><span class="legend-dot" style="background:${COLORS.Moderate}"></span>Moderate</div>
        <div class="legend-row"><span class="legend-dot" style="background:${COLORS.High}"></span>High</div>
      `;
      return div;
    },
  });
  return new Legend();
}
watch([year, month], () => { if (gj) gj.setStyle(styleFn as any); });

/* ---------------- Table rows (last 5y up to selected year) ---------------- */
const tableRows = computed(() => {
  const site = LAKE_SITE_MAP[activeLake.value];
  const from = year.value - 4;
  const data = rows.value
    .filter((r) => r.site_id === site && r.month === month.value && r.year <= year.value && r.year >= from)
    .sort((a, b) => b.year - a.year);

  return data.map((r) => {
    const ph = r.ph != null ? r.ph.toFixed(1) : null;
    const dosat = r.dosat != null ? Math.round(r.dosat).toString() + "%" : null;
    const temp = r.temp != null ? Math.round(r.temp).toString() + "°C" : null;

    const phCat = r.ph == null ? "muted" : r.ph < 6.5 ? "below" : r.ph > 8.5 ? "above" : "ok";
    const doCat = r.dosat == null ? "muted" : r.dosat < 80 ? "below" : r.dosat > 120 ? "above" : "ok";
    const tCat = r.temp == null ? "muted" : r.temp < 10 ? "below" : r.temp > 30 ? "above" : "ok";

    let level: Level = normLevel(r.algae);
    let alert = (r.alert ?? "").trim();
    if (!alert) {
      // When no explicit alert, give default label based on level
      alert = level === "High" ? "Yes" : level === "Moderate" ? "Caution" : "No";
    }
    return {
      year: r.year,
      ph, dosat, temp,
      ph_category: phCat,
      do_category: doCat,
      temp_category: tCat,
      level,
      alert,
    };
  });
});

/* ---------------- Summary (qualified True / total) ---------------- */
const suitabilityPct = computed(() => {
  const site = LAKE_SITE_MAP[activeLake.value];
  const data = rows.value.filter((r) => r.site_id === site && r.year === year.value && r.month === month.value);
  const total = data.length;
  if (!total) return 0;
  const ok = data.filter((r) => r.qualified).length;
  return (ok / total) * 100;
});

/* ---------------- UI helpers ---------------- */
function pill(cat: "ok" | "below" | "above" | "muted") {
  if (cat === "ok") return "inline-flex items-center px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700";
  if (cat === "below") return "inline-flex items-center px-2 py-0.5 rounded-full bg-blue-100 text-blue-700";
  if (cat === "above") return "inline-flex items-center px-2 py-0.5 rounded-full bg-rose-100 text-rose-700";
  return "inline-flex items-center px-2 py-0.5 rounded-full bg-slate-100 text-slate-500";
}
function algaeTag(level: Level) {
  if (level === "Low") return "inline-flex items-center px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-700";
  if (level === "Moderate") return "inline-flex items-center px-2 py-0.5 rounded-md bg-amber-100 text-amber-700";
  if (level === "High") return "inline-flex items-center px-2 py-0.5 rounded-md bg-green-700 text-white";
  return "inline-flex items-center px-2 py-0.5 rounded-md bg-slate-100 text-slate-500";
}
function alertTag(a: string) {
  const s = (a ?? "").toLowerCase();
  if (s === "yes") return "inline-flex items-center px-2 py-0.5 rounded-md bg-rose-100 text-rose-700";
  if (s === "caution") return "inline-flex items-center px-2 py-0.5 rounded-md bg-amber-100 text-amber-700";
  return "inline-flex items-center px-2 py-0.5 rounded-md bg-emerald-100 text-emerald-700";
}
</script>

<style scoped>
.control { @apply h-9 rounded-md border border-slate-300 bg-white px-2 text-sm; }
.legend-pill { @apply inline-flex items-center px-2 py-0.5 rounded-full; }

/* Leaflet legend card (top-left corner) */
.legend-card{
  background: #fff;
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
