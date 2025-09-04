// src/services/dbApi.js
export async function fetchSites() {
  const res = await fetch('/api/sites')
  if (!res.ok) throw new Error('fetch_sites_failed')
  return res.json()
}
