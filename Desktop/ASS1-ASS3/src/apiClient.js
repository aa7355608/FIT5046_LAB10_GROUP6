// src/apiClient.js
import axios from "axios";

const BASE =
  import.meta.env.VITE_API_BASE?.trim() ||
  (import.meta.env.DEV ? "http://localhost:3000" : ""); // 生产缺失则留空，后面兜底报错

const api = axios.create({
  baseURL: BASE,
  timeout: 15000,
});

// 如果生产环境缺 baseURL，直接给出明确报错，避免去打前端域名返回 HTML
api.interceptors.request.use((config) => {
  if (!config.baseURL) {
    throw new Error(
      "API baseURL is not configured. Set VITE_API_BASE in .env.production"
    );
  }
  return config;
});

// 返回的不是 JSON 时，打印关键信息，避免 “Unexpected token '<'”
api.interceptors.response.use(
  (res) => {
    const ct = res.headers?.["content-type"] || "";
    if (!ct.includes("application/json")) {
      console.error("API returned non-JSON:", {
        url: res.config?.url,
        status: res.status,
        ct,
        preview: typeof res.data === "string" ? res.data.slice(0, 120) : res.data,
      });
      throw new Error("api_non_json_response");
    }
    return res;
  },
  (err) => Promise.reject(err)
);

export default api;
