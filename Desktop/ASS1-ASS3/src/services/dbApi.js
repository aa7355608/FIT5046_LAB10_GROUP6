import api from "@/apiClient";

/**
 * 获取站点列表（从后端 /api/sites）
 * 返回的每个站点包含：
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
