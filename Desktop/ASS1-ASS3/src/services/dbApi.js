import api from "@/apiClient";

/**
 * Get site list (from backend /api/sites)
 * Each returned site contains:
 * - site_id, site_name, water_body, latitude, longitude
 * - latest_date, quality_level
 * - status: 'green' | 'amber' | 'red'
 */
export async function fetchSites() {
  try {
    const res = await api.get("/api/sites");
    return res.data;
  } catch (err) {
    console.error("fetchSites failed:", err);
    throw new Error("fetch_sites_failed");
  }
}
