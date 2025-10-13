// src/services/marineClient.ts
// Open-Meteo Marine API — Sea Surface Temperature (SST)

export type SSTPoint = {
  t: string;      // ISO time
  sst: number;    // ℃
};

export type SSTSeries = {
  hourly: SSTPoint[];
  latest?: SSTPoint;     // Closest to now (or selected day)
  min?: number;          // Minimum for this time window
  max?: number;          // Maximum for this time window
  mean?: number;         // Mean for this time window
};

const BASE = 'https://marine-api.open-meteo.com/v1/marine';

// Simple fetch wrapper (auto-throw on error)
async function get(url: string): Promise<any> {
  const res = await fetch(url);
  if (!res.ok) {
    const txt = await res.text().catch(() => '');
    throw new Error(`Marine API ${res.status}: ${txt || res.statusText}`);
  }
  return res.json();
}

/**
 * Fetch sea temperature at a point (hourly), time range usually set to "00:00~23:59 of selected date".
 * @param lat Latitude
 * @param lon Longitude
 * @param start 'YYYY-MM-DD'
 * @param end   'YYYY-MM-DD'
 * @param tz    e.g. 'Australia/Sydney' | 'auto'
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

  // Statistics
  const vals = hourly.map(d => d.sst);
  const min = vals.length ? Math.min(...vals) : undefined;
  const max = vals.length ? Math.max(...vals) : undefined;
  const mean = vals.length ? +(vals.reduce((a, b) => a + b, 0) / vals.length).toFixed(2) : undefined;

  // Pick the one "closest to noon of the day" as latest (more stable)
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

/** Batch requests (can add concurrency control if needed, here using Promise.allSettled directly) */
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
