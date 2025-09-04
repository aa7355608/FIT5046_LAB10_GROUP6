// 前端地理编码工具（Nominatim）。带内存缓存 + 强制英文。
const cache = new Map();
const REV_GEO_LANG = 'en';

function key(...parts) {
  return parts.map(x => String(x)).join('|');
}

// ---------- 反向地理编码：经纬度 -> 地址 ----------
export async function reverseGeocodeClient(lat, lng) {
  const k = key('rev', lat.toFixed(5), lng.toFixed(5), REV_GEO_LANG);
  if (cache.has(k)) return cache.get(k);

  const url = new URL('https://nominatim.openstreetmap.org/reverse');
  url.search = new URLSearchParams({
    format: 'jsonv2',
    lat,
    lon: lng,
    zoom: '16',
    addressdetails: '1',
    'accept-language': REV_GEO_LANG,
  }).toString();

  const res = await fetch(url.toString(), { headers: { Accept: 'application/json' } });
  if (!res.ok) throw new Error(`revgeo_failed_${res.status}`);

  const j = await res.json();
  const data = { address: j.display_name || '', parts: j.address || {} };
  cache.set(k, data);
  // 礼貌延时（可去掉）
  await new Promise(r => setTimeout(r, 700));
  return data;
}

// ---------- 正向地理编码：文本/邮编 -> 坐标 ----------
export async function forwardGeocodeClient(query) {
  const q = String(query || '').trim();
  if (!q) return null;

  const k = key('fwd', q.toLowerCase(), REV_GEO_LANG);
  if (cache.has(k)) return cache.get(k);

  const isPostcode = /^\d{4,5}$/.test(q);
  const url = new URL('https://nominatim.openstreetmap.org/search');
  const params = {
    format: 'jsonv2',
    limit: '1',
    addressdetails: '1',
    'accept-language': REV_GEO_LANG,
    countrycodes: 'au', // 你主要看 Port Phillip Bay，可限定澳洲
  };
  if (isPostcode) params.postalcode = q; else params.q = q;
  url.search = new URLSearchParams(params).toString();

  const res = await fetch(url.toString(), { headers: { Accept: 'application/json' } });
  if (!res.ok) return null;

  const arr = await res.json();
  const first = arr?.[0];
  if (!first) return null;

  const data = {
    lat: Number(first.lat),
    lng: Number(first.lon),
    label: first.display_name || q,
  };
  cache.set(k, data);
  return data;
}
