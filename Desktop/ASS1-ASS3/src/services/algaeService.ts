// src/services/algaeService.ts
export type AlgaeLevel = 'Low' | 'Medium' | 'High';

// ① 如果你在 vite 用了代理（见第 3 步），这里留空即可；否则用环境变量指定基地址
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

  // 后端已尽量自适应列名；这里将 segment_id / lake_name 归一化为同一个 key
  for (const row of data as any[]) {
    const seg = normKey(row.segment_id);
    const lake = normKey(row.lake_name);
    const key = seg || lake;              // 优先 segment_id，其次 lake_name
    const lvl = String(row.level || '').trim();
    if (!key) continue;
    if (lvl === 'Low' || lvl === 'Medium' || lvl === 'High') {
      map[key] = lvl as AlgaeLevel;
    }
  }

  console.log('[algae] rows=', data.length, 'keys=', Object.keys(map).length);
  return map;
}
