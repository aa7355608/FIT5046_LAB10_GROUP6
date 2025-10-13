// src/services/algaeService.ts
export type AlgaeLevel = 'Low' | 'Medium' | 'High';

// ① If you use proxy in vite (see step 3), leave blank; otherwise use env variable to specify base URL
const API_BASE = import.meta.env.VITE_API_BASE ?? '';

function normKey(s: any) {
  return String(s ?? '').trim().toLowerCase();
}

export async function getAlgaeBySegment(): Promise<Record<string, AlgaeLevel>> {
  const url = `${API_BASE}/api/algae`;
  const res = await fetch(url);
  if (!res.ok) {
    console.error('[algae] fetch failed', res.status, await res.text());
    return {};
  }
  const data = await res.json();
  const map: Record<string, AlgaeLevel> = {};

  // Backend already adapts column names; here we normalize segment_id / lake_name to the same key
  for (const row of data as any[]) {
    const seg = normKey(row.segment_id);
    const lake = normKey(row.lake_name);
    const key = seg || lake;              // Prefer segment_id, fallback to lake_name
    const lvl = String(row.level || '').trim();
    if (!key) continue;
    if (lvl === 'Low' || lvl === 'Medium' || lvl === 'High') {
      map[key] = lvl as AlgaeLevel;
    }
  }

  console.log('[algae] rows=', data.length, 'keys=', Object.keys(map).length);
  return map;
}
