<template>
  <div
    style="
      display:flex; flex-wrap:wrap; gap:8px; align-items:center;
      border:1px solid #e5e7eb; background:#fff;
      border-radius:16px; padding:8px 10px;"
  >
    <button class="pill">Air <span class="muted">Current</span></button>
    <button class="pill">Air <span class="muted">Regional forecast</span></button>
    <button class="pill active water">🟦 Water <span>Forecasts and alerts</span></button>
    <button class="pill active incidents">🔺 Incidents</button>

    <div style="flex:1"></div>

    <div
      style="
        display:flex; gap:6px; align-items:center;
        border:1px solid #e5e7eb; border-radius:12px; padding:6px 8px;"
    >
      <input
        placeholder="Suburb/postcode..."
        style="border:0; outline:none; width:220px"
      />
      <button class="chip">Search</button>
      <button class="icon">⚙️</button>
      <button class="icon">⤢</button>
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.pill{
  border:1px solid #e5e7eb; background:#f8fafc;
  border-radius:12px; padding:8px 10px; font-size:14px; font-weight:600;
}
.pill .muted{ color:#64748b; font-weight:500; margin-left:6px; }
.pill.active.water{
  border:2px solid #0ea5e9; background:#0369a1; color:#fff;
}
.pill.active.incidents{
  border:2px solid #fb7185; background:#e11d48; color:#fff;
}
.chip{
  border:0; background:#e5e7eb; border-radius:10px; padding:6px 8px;
}
.icon{
  border:0; background:#f1f5f9; border-radius:8px; padding:4px 6px;
}
</style>
