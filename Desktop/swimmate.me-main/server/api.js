// server/api.js
import express from "express";
import cors from "cors";
import mysql from "mysql2/promise";
import "dotenv/config";

const app = express();
app.use(cors());

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME, // .env 中应为 DB_NAME=swim
  waitForConnections: true,
  connectionLimit: 5,
  // 
  ssl: process.env.DB_SSL ? { rejectUnauthorized: false } : undefined,
});

// 可选：根路径提示
app.get("/", (_req, res) => {
  res.type("text").send("API OK. Try /api/ping or /api/sites");
});

// 健康检查
app.get("/api/ping", async (_req, res) => {
  try {
    const [rows] = await pool.query("SELECT NOW() AS now");
    res.json({ ok: true, now: rows[0].now });
  } catch (e) {
    res.status(500).json({ ok: false, error: e.message });
  }
});

// 列出沙滩点位（来自 swim.site）
app.get("/api/sites", async (_req, res) => {
  try {
    const [rows] = await pool.query(
      "SELECT " +
      "  site_id, " +
      "  site_name, " +
      "  water_body, " +
      "  CAST(latitude  AS DECIMAL(10,6))  AS latitude, " +
      "  CAST(longitude AS DECIMAL(10,6))  AS longitude " +
      "FROM `swim`.`site` " +                      // ✅ 表名为 site（单数）
      "WHERE latitude IS NOT NULL AND longitude IS NOT NULL " +
      "LIMIT 1000"
    );
    res.json(rows);
  } catch (e) {
    console.error(e);
    res.status(500).json({ error: e.message });
  }
});

// 按 ID 查询单个站点
app.get("/api/sites/:id", async (req, res) => {
  try {
    const [rows] = await pool.query(
      "SELECT " +
      "  site_id, site_name, water_body, " +
      "  CAST(latitude  AS DECIMAL(10,6))  AS latitude, " +
      "  CAST(longitude AS DECIMAL(10,6))  AS longitude " +
      "FROM `swim`.`site` WHERE site_id = ?",
      [req.params.id]
    );
    if (!rows.length) return res.status(404).json({ error: "not_found" });
    res.json(rows[0]);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

const PORT = Number(process.env.PORT || 8787);
app.listen(PORT, () => console.log(`API listening at http://localhost:${PORT}`));
