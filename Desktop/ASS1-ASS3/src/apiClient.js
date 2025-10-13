// src/apiClient.js
import axios from "axios";

const BASE =
  import.meta.env.VITE_API_BASE?.trim() ||
  (import.meta.env.DEV ? "http://localhost:3000" : ""); // If missing in production, leave blank and handle error later

const api = axios.create({
  baseURL: BASE,
  timeout: 15000,
});

// If baseURL is missing in production, throw explicit error to avoid hitting frontend domain returning HTML
api.interceptors.request.use((config) => {
  if (!config.baseURL) {
    throw new Error(
      "API baseURL is not configured. Set VITE_API_BASE in .env.production"
    );
  }
  return config;
});

// When response is not JSON, log key info to avoid "Unexpected token '<'"
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
