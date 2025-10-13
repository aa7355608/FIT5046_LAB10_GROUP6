// src/services/weatherService.js
// Fetches rainfall & weather for a given date using Open-Meteo.
// No API key needed. Works for both historical and forecast dates.

const DEFAULT_LAT = -37.81;          // Melbourne CBD
const DEFAULT_LON = 144.96;
const TZ  = 'Australia/Sydney';
const BASE = 'https://api.open-meteo.com/v1/forecast';

// === Public API used by <RainWeatherView /> ===
// 支持传入 (dateStr, lat, lon)，否则用默认值
export async function getConditionsForDate(dateStr, lat = DEFAULT_LAT, lon = DEFAULT_LON) {
  try {
    const day = toISODate(dateStr);

    // 区间日期
    const pastStart = addDays(day, -2);   // day-2, day-1, day => 72h
    const pastEnd   = day;
    const nextStart = day;
    const nextEnd   = day;

    // === 3 请求并发 ===
    const [pastDaily, nextHourly, dayHourly] = await Promise.all([
      // 过去72小时：用 daily rain_sum 求和
      fetchJson(BASE + '?' + toQS({
        latitude: lat, longitude: lon, timezone: TZ,
        daily: 'rain_sum',
        start_date: pastStart, end_date: pastEnd,
      })),

      // 当天的逐小时降水量（取作未来24小时或该日实际）
      fetchJson(BASE + '?' + toQS({
        latitude: lat, longitude: lon, timezone: TZ,
        hourly: 'precipitation',
        start_date: nextStart, end_date: nextEnd,
      })),

      // 当天逐小时天气：用于“今日天气”
      fetchJson(BASE + '?' + toQS({
        latitude: lat, longitude: lon, timezone: TZ,
        hourly: 'weathercode,temperature_2m,wind_speed_10m',
        start_date: day, end_date: day,
      })),
    ]);

    // === 过去72小时合计 ===
    const rainPast72 = sum(pastDaily?.daily?.rain_sum) ?? 0;

    // === 当天24小时合计 ===
    const nextArr = nextHourly?.hourly?.precipitation ?? [];
    const rainNext24 = round1(sum(nextArr));

    // === 今日天气（选中日期）===
    const times = dayHourly?.hourly?.time ?? [];
    const codes = dayHourly?.hourly?.weathercode ?? [];
    const temps = dayHourly?.hourly?.temperature_2m ?? [];
    const winds = dayHourly?.hourly?.wind_speed_10m ?? [];

    const noonIdx = indexOfTime(times, day + 'T12:00');
    const pickIdx = noonIdx ?? (times.length ? Math.floor(times.length / 2) : null);

    const condition = pickIdx != null ? mapWeatherCode(codes[pickIdx]) : '—';
    const temperature = pickIdx != null ? Math.round(Number(temps[pickIdx])) : null;
    const windAvg = winds.length ? round1(avg(winds)) : null;

    return {
      today: {
        condition: condition || '—',
        temperature: isFiniteNum(temperature) ? temperature : null,
        wind: isFiniteNum(windAvg) ? `${windAvg} km/h` : '—',
      },
      rain: {
        past72: round1(rainPast72),
        next24: round1(rainNext24),
      },
    };
  } catch (err) {
    console.error('[weatherService] Open-Meteo error, using fallback:', err);
    return fallback();
  }
}

/* ---------------- helper functions ---------------- */

function toISODate(s) {
  if (/^\d{4}-\d{2}-\d{2}$/.test(String(s))) return s;
  const d = new Date(s);
  if (isNaN(d)) return new Date().toISOString().slice(0, 10);
  return d.toISOString().slice(0, 10);
}
function addDays(isoDate, delta) {
  const d = new Date(isoDate + 'T00:00:00');
  d.setDate(d.getDate() + delta);
  return d.toISOString().slice(0, 10);
}
function toQS(obj) {
  const sp = new URLSearchParams();
  Object.entries(obj).forEach(([k, v]) => sp.append(k, String(v)));
  return sp.toString();
}
async function fetchJson(url) {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}
function sum(arr) {
  if (!Array.isArray(arr) || !arr.length) return 0;
  return arr.reduce((a, b) => a + (Number(b) || 0), 0);
}
function avg(arr) {
  if (!Array.isArray(arr) || !arr.length) return 0;
  return sum(arr) / arr.length;
}
function round1(n) {
  return Math.round(Number(n) * 10) / 10;
}
function isFiniteNum(n) {
  return Number.isFinite(Number(n));
}
function indexOfTime(times, target) {
  if (!Array.isArray(times)) return null;
  const idx = times.indexOf(target);
  return idx >= 0 ? idx : null;
}
function mapWeatherCode(code) {
  const c = Number(code);
  const map = {
    0: 'Clear sky',
    1: 'Mainly clear',
    2: 'Partly cloudy',
    3: 'Overcast',
    45: 'Fog',
    48: 'Rime fog',
    51: 'Light drizzle',
    53: 'Drizzle',
    55: 'Dense drizzle',
    61: 'Light rain',
    63: 'Moderate rain',
    65: 'Heavy rain',
    80: 'Rain showers',
    81: 'Rain showers',
    82: 'Violent showers',
  };
  return map[c] || 'Unknown';
}
function fallback() {
  return {
    today: { condition: 'Partly Cloudy', temperature: 22, wind: '15 km/h SW' },
    rain:  { past72: 45, next24: 12 },
  };
}
