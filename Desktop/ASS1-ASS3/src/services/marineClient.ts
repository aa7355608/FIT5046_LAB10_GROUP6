// src/services/marineClient.ts
// Open-Meteo Marine API — Sea Surface Temperature (SST)

export type SSTPoint = {
  t: string;      // ISO time
  sst: number;    // ℃
};

export type SSTSeries = {
  hourly: SSTPoint[];
  latest?: SSTPoint;     // 最接近现在(或所选日)的一条
  min?: number;          // 该时间窗最低
  max?: number;          // 该时间窗最高
  mean?: number;         // 该时间窗平均
};

const BASE = 'https://marine-api.open-meteo.com/v1/marine';

// 简单 fetch 包装（自动抛错）
async function get(url: string): Promise<any> {
  const res = await fetch(url);
  if (!res.ok) {
    const txt = await res.text().catch(() => '');
    throw new Error(`Marine API ${res.status}: ${txt || res.statusText}`);
  }
  return res.json();
}

/**
 * 取某点海温（小时级），时间范围通常设为“选择日期的 00:00~23:59”。
 * @param lat 经度
 * @param lon 纬度
 * @param start 'YYYY-MM-DD'
 * @param end   'YYYY-MM-DD'
 * @param tz    例如 'Australia/Sydney' | 'auto'
 */
export async function fetchSST(
  lat: number,
  lon: number,
  start: string,
  end: string,
  tz: string = 'Australia/Sydney'
): Promise<SSTSeries> {
  const params = new URLSearchParams({
    latitude: String(lat),
    longitude: String(lon),
    hourly: 'sea_surface_temperature',
    start_date: start,
    end_date: end,
    timezone: tz,
  });

  const url = `${BASE}?${params.toString()}`;
  const data = await get(url);

  const times: string[] = data?.hourly?.time ?? [];
  const temp: number[] = data?.hourly?.sea_surface_temperature ?? [];

  const hourly: SSTPoint[] = times.map((t, i) => ({
    t,
    sst: typeof temp[i] === 'number' ? temp[i] : NaN,
  })).filter(d => Number.isFinite(d.sst));

  // 统计
  const vals = hourly.map(d => d.sst);
  const min = vals.length ? Math.min(...vals) : undefined;
  const max = vals.length ? Math.max(...vals) : undefined;
  const mean = vals.length ? +(vals.reduce((a, b) => a + b, 0) / vals.length).toFixed(2) : undefined;

  // 取“最靠近当天中午”的一条作为 latest（更稳定）
  let latest: SSTPoint | undefined;
  if (hourly.length) {
    const target = new Date(`${start}T12:00:00`);
    let best = 0, bestDiff = Infinity;
    hourly.forEach((d, i) => {
      const diff = Math.abs(new Date(d.t).getTime() - target.getTime());
      if (diff < bestDiff) { best = i; bestDiff = diff; }
    });
    latest = hourly[best];
  }

  return { hourly, latest, min, max, mean };
}

/** 批量请求（并发控制可按需加，这里直接 Promise.allSettled） */
export async function fetchSSTBatch(
  pts: { lat: number; lon: number; key: string }[],
  start: string,
  end: string,
  tz: string = 'Australia/Sydney'
): Promise<Record<string, SSTSeries | null>> {
  const out: Record<string, SSTSeries | null> = {};
  const tasks = pts.map(async p => {
    try {
      out[p.key] = await fetchSST(p.lat, p.lon, start, end, tz);
    } catch {
      out[p.key] = null;
    }
  });
  await Promise.allSettled(tasks);
  return out;
}
