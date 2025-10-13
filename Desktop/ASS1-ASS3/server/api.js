// server/api.js
import express from "express";
import cors from "cors";
import mysql from "mysql2/promise";
import "dotenv/config";

const app = express();

/* ========== CORS ========== */
const allowList = (process.env.ALLOWED_ORIGINS || "")
  .split(",")
  .map(s => s.trim())
  .filter(Boolean);
app.use(allowList.length ? cors({ origin: allowList }) : cors());
app.use(express.json()); // Parse JSON body

/* ========== MySQL Pool ========== */
const DB_NAME = process.env.DB_NAME || "swim";
// Detail data table (default enterococci; to switch tables, configure ANALYTICS_TABLE=report in cloud)
const ANALYTICS_TABLE = (process.env.ANALYTICS_TABLE || "enterococci").trim();
const T_ANALYTICS = `\`${DB_NAME}\`.\`${ANALYTICS_TABLE}\``;
const T_SITE = `\`${DB_NAME}\`.\`site\``;

const pool = mysql.createPool({
  host: process.env.DB_HOST || "127.0.0.1",
  port: Number(process.env.DB_PORT || 3306),
  user: process.env.DB_USER || "readonly_user",
  password: process.env.DB_PASS || "fit5120ta04",
  database: DB_NAME,
  waitForConnections: true,
  connectionLimit: 5,
  ssl: process.env.DB_SSL ? { rejectUnauthorized: false } : undefined,
});

/* ========== Fixed site list (for map aggregation) ========== */
const SITES_FROM_SPEC = [
  { site_id: 99020, site_name: "Port Melbourne", water_body: "Port Phillip Bay", latitude: "-37.843866", longitude: "144.937766" },
  { site_id: 99060, site_name: "Hampton", water_body: "Port Phillip Bay", latitude: "-37.938000", longitude: "144.997116" },
  { site_id: 99070, site_name: "Half Moon Bay", water_body: "Port Phillip Bay", latitude: "-37.968566", longitude: "145.011066" },
  { site_id: 99100, site_name: "Mentone", water_body: "Port Phillip Bay", latitude: "-37.989100", longitude: "145.058733" },
  { site_id: 99160, site_name: "Seaford", water_body: "Port Phillip Bay", latitude: "-38.103316", longitude: "145.124550" },
  { site_id: 99170, site_name: "Frankston Surf Life Saving Club", water_body: "Port Phillip Bay", latitude: "-38.142333", longitude: "145.118540" },
  { site_id: 99240, site_name: "Safety Beach", water_body: "Port Phillip Bay", latitude: "-38.311266", longitude: "144.992366" },
  { site_id: 99280, site_name: "Blairgowrie", water_body: "Port Phillip Bay", latitude: "-38.360950", longitude: "144.781716" },
  { site_id: 99290, site_name: "Sorrento", water_body: "Port Phillip Bay", latitude: "-38.343516", longitude: "144.752800" },
  { site_id: 99500, site_name: "The Dell", water_body: "Port Phillip Bay", latitude: "-38.152496", longitude: "144.563007" },
  { site_id: 99510, site_name: "Eastern Beach", water_body: "Port Phillip Bay", latitude: "-38.146666", longitude: "144.370850" },
  { site_id: 99520, site_name: "Portsea", water_body: "Port Phillip Bay", latitude: "-38.318983", longitude: "144.713816" },
  { site_id: 99530, site_name: "Rye", water_body: "Port Phillip Bay", latitude: "-38.370183", longitude: "144.832100" },
  { site_id: 99550, site_name: "Dromana", water_body: "Port Phillip Bay", latitude: "-38.332033", longitude: "144.964633" },
  { site_id: 99580, site_name: "Canadian Bay", water_body: "Port Phillip Bay", latitude: "-38.171750", longitude: "145.083083" },
  { site_id: 99590, site_name: "Frankston Coast Guard", water_body: "Port Phillip Bay", latitude: "-38.147043", longitude: "145.115502" },
  { site_id: 99620, site_name: "Mordialloc", water_body: "Port Phillip Bay", latitude: "-38.008817", longitude: "145.083300" },
  { site_id: 99630, site_name: "Sandringham", water_body: "Port Phillip Bay", latitude: "-37.950796", longitude: "145.001354" },
  { site_id: 99650, site_name: "Elwood", water_body: "Port Phillip Bay", latitude: "-37.889061", longitude: "144.983613" },
  { site_id: 99660, site_name: "St Kilda", water_body: "Port Phillip Bay", latitude: "-37.868166", longitude: "144.974033" },
  { site_id: 99690, site_name: "Sandridge", water_body: "Port Phillip Bay", latitude: "-37.840033", longitude: "144.916412" },
  { site_id: 99700, site_name: "Williamstown", water_body: "Port Phillip Bay", latitude: "-37.867962", longitude: "144.893233" },
  { site_id: 99710, site_name: "Altona", water_body: "Port Phillip Bay", latitude: "-37.871066", longitude: "144.831250" },
  { site_id: 99730, site_name: "Werribee Sth", water_body: "Port Phillip Bay", latitude: "-37.974916", longitude: "144.692783" },
  { site_id: 99761, site_name: "Portarlington Beach (new site)", water_body: "Port Phillip Bay", latitude: "-38.112672", longitude: "144.650817" },
  { site_id: 99770, site_name: "St Leonards", water_body: "Port Phillip Bay", latitude: "-38.170783", longitude: "144.719166" },
  { site_id: 99990, site_name: "Beaumaris", water_body: "Port Phillip Bay", latitude: "-37.994966", longitude: "145.032750" },
  { site_id: 99991, site_name: "Brighton Life Saving Club", water_body: "Port Phillip Bay", latitude: "-37.917297", longitude: "144.986083" },
  { site_id: 99992, site_name: "Mornington Life Saving Club", water_body: "Port Phillip Bay", latitude: "-38.213630", longitude: "145.044181" },
  { site_id: 99993, site_name: "Rosebud Life Saving Club", water_body: "Port Phillip Bay", latitude: "-38.347306", longitude: "144.928060" },
  { site_id: 99994, site_name: "Mt Martha Life Saving Club", water_body: "Port Phillip Bay", latitude: "-38.265248", longitude: "145.013913" },
  { site_id: 99995, site_name: "Carrum Surf Life Saving Club", water_body: "Port Phillip Bay", latitude: "-38.076393", longitude: "145.120640" },
  { site_id: 99996, site_name: "Aspendale Life Saving Club", water_body: "Port Phillip Bay", latitude: "-38.028173", longitude: "145.100253" },
  { site_id: 99997, site_name: "South Melbourne Life Saving Club", water_body: "Port Phillip Bay", latitude: "-37.847497", longitude: "144.946250" },
  { site_id: 99998, site_name: "Santa Casa", water_body: "Port Phillip Bay", latitude: "-38.272293", longitude: "144.648588" },
  { site_id: 99999, site_name: "Black Rock Life Saving Club", water_body: "Port Phillip Bay", latitude: "-37.974777", longitude: "145.014242" }
];

/* ========== Helpers ========== */
function normalizeLevel(raw) {
  if (!raw) return "unknown";
  const t = raw.trim().toLowerCase();
  if (t === "safe" || t === "relatively safe") return "green";
  if (t === "caution") return "amber";
  if (t === "unsafe") return "red";
  return "amber";
}
function decideStatus(b) {
  if (!b || b.total === 0) return "amber";
  const redRatio = b.red / b.total;
  const amberRatio = b.amber / b.total;
  const greenRatio = b.green / b.total;
  if (redRatio >= 0.05) return "red";
  if (amberRatio >= 0.1) return "amber";
  if (greenRatio >= 0.9) return "green";
  return "amber";
}

/* ========== Health ========== */
app.get("/", (_req, res) => {
  res.type("text").send("✅ API OK. Try /api/sites");
});
app.get("/api/health/db", async (_req, res) => {
  try {
    const [rows] = await pool.query(`SELECT 1 AS ok`);
    res.json({ ok: true, rows });
  } catch (e) {
    res.status(500).json({ ok: false, error: String(e.message || e) });
  }
});

/* ========== /api/sites (map aggregation) ========== */
app.get("/api/sites", async (_req, res) => {
  try {
    const siteIds = SITES_FROM_SPEC.map(s => s.site_id);
    const placeholders = siteIds.map(() => "?").join(",");

    const [agg] = await pool.query(
      `
      SELECT
        enterococci_site_id AS site_id,
        LOWER(TRIM(enterococci_quality_level)) AS raw_level,
        COUNT(*) AS cnt
      FROM \`${DB_NAME}\`.\`enterococci\`
      WHERE enterococci_site_id IN (${placeholders})
      GROUP BY enterococci_site_id, LOWER(TRIM(enterococci_quality_level))
      `,
      siteIds
    );

    const buckets = new Map();
    for (const r of agg) {
      const id = Number(r.site_id);
      const color = normalizeLevel(r.raw_level);
      if (!buckets.has(id)) buckets.set(id, { green: 0, amber: 0, red: 0, total: 0 });
      const b = buckets.get(id);
      if (color === "green") b.green += Number(r.cnt);
      else if (color === "red") b.red += Number(r.cnt);
      else b.amber += Number(r.cnt);
      b.total += Number(r.cnt);
    }

    const result = SITES_FROM_SPEC.map(s => {
      const b = buckets.get(Number(s.site_id));
      return {
        site_id: s.site_id,
        site_name: s.site_name,
        water_body: s.water_body,
        latitude: Number(s.latitude),
        longitude: Number(s.longitude),
        status: decideStatus(b),
        counts: b || { green: 0, amber: 0, red: 0, total: 0 }
      };
    });

    res.json(result);
  } catch (e) {
    console.error("Sites error:", e);
    res.status(500).json({ error: e.code || "db_error", message: e.message });
  }
});

/* ========== Dashboard APIs (using ANALYTICS_TABLE, default enterococci) ========== */
app.get("/api/beaches", async (_req, res) => {
  try {
    const [rows] = await pool.query(
      `
      SELECT site_id, site_name, water_body
      FROM ${T_SITE}
      ORDER BY site_name ASC
      `
    );
    res.json(rows);
  } catch (e) {
    console.error("Beaches error:", e);
    res.status(500).json({ error: "Failed to list beaches", detail: e.message });
  }
});

app.get("/api/beach-data", async (req, res) => {
  const { beachId, year } = req.query;
  if (!beachId || !/^\d+$/.test(beachId)) {
    return res.status(400).json({ error: "Invalid beach ID" });
  }
  try {
    const [beachRows] = await pool.query(
      `SELECT site_name FROM ${T_SITE} WHERE site_id = ?`,
      [beachId]
    );
    if (beachRows.length === 0) {
      return res.status(404).json({ error: "Beach not found" });
    }
    const beachName = beachRows[0].site_name;

    // Note: your date data is in format 2013-12-10, which corresponds to %Y-%m-%d
    let sql = `
      SELECT 
        enterococci_date,
        enterococci_quality_level,
        enterococci_value,
        enterococci_sample_type,
        YEAR(STR_TO_DATE(enterococci_date, '%Y-%m-%d')) AS year,
        MONTH(STR_TO_DATE(enterococci_date, '%Y-%m-%d')) AS month
      FROM ${T_ANALYTICS}
      WHERE enterococci_site_id = ?
    `;
    const params = [beachId];
    if (year && /^\d{4}$/.test(year)) {
      sql += " AND YEAR(STR_TO_DATE(enterococci_date, '%Y-%m-%d')) = ?";
      params.push(year);
    }
    sql += " ORDER BY STR_TO_DATE(enterococci_date, '%Y-%m-%d') ASC";

    const [dataRows] = await pool.query(sql, params);

    const totalTests = dataRows.length;
    const qualityCounts = {};
    const monthlyData = {};
    let totalRating = 0;

    dataRows.forEach(row => {
      const quality = String(row.enterococci_quality_level || "").trim();
      qualityCounts[quality] = (qualityCounts[quality] || 0) + 1;
      const ratingMap = { "Safe": 4, "Relatively Safe": 3, "Caution": 2, "Unsafe": 1 };
      totalRating += ratingMap[quality] || 0;

      const monthKey = `${row.year}-${String(row.month).padStart(2, "0")}`;
      if (!monthlyData[monthKey]) {
        monthlyData[monthKey] = { total: 0, good: 0, fair: 0, poor: 0 };
      }
      monthlyData[monthKey].total++;
      if (quality === "Safe") monthlyData[monthKey].good++;
      else if (quality === "Relatively Safe") monthlyData[monthKey].fair++;
      else monthlyData[monthKey].poor++;
    });

    const averageRating = totalTests > 0 ? Number((totalRating / totalTests).toFixed(2)) : 0;

    const monthlyArray = Object.entries(monthlyData).map(([month, d]) => ({
      month,
      total: d.total,
      goodPercent: d.total ? Number(((d.good / d.total) * 100).toFixed(1)) : 0,
      fairPercent: d.total ? Number(((d.fair / d.total) * 100).toFixed(1)) : 0
    }));

    console.log("[beach-data]", { table: ANALYTICS_TABLE, beachId, year, rows: totalTests, db: DB_NAME });
    res.json({ beachName, totalTests, averageRating, qualityCounts, monthlyData: monthlyArray, rawData: dataRows });
  } catch (e) {
    console.error("Beach-data error:", e);
    res.status(500).json({ error: "Failed to get beach data", detail: e.message });
  }
});

app.get("/api/beach-comparison", async (req, res) => {
  const { beachIds } = req.query;
  if (!beachIds) {
    return res.status(400).json({ error: "Beach IDs required" });
  }
  const ids = beachIds
    .split(",")
    .map(id => id.trim())
    .filter(id => /^\d+$/.test(id))
    .slice(0, 3);

  if (ids.length === 0) {
    return res.status(400).json({ error: "Valid beach IDs required" });
  }

  try {
    const results = [];
    for (const beachId of ids) {
      const [beachRows] = await pool.query(
        `SELECT site_name FROM ${T_SITE} WHERE site_id = ?`,
        [beachId]
      );
      if (beachRows.length === 0) continue;
      const beachName = beachRows[0].site_name;

      const [dataRows] = await pool.query(
        `
        SELECT 
          YEAR(STR_TO_DATE(enterococci_date, '%Y-%m-%d')) AS year,
          enterococci_quality_level,
          COUNT(*) AS count
        FROM ${T_ANALYTICS}
        WHERE enterococci_site_id = ?
        GROUP BY year, enterococci_quality_level
        ORDER BY year ASC
        `,
        [beachId]
      );

      const yearlyStats = {};
      dataRows.forEach(row => {
        if (!yearlyStats[row.year]) yearlyStats[row.year] = { total: 0, good: 0, fair: 0 };
        yearlyStats[row.year].total += row.count;
        const q = String(row.enterococci_quality_level || "").trim();
        if (q === "Safe") yearlyStats[row.year].good += row.count;
        else if (q === "Relatively Safe") yearlyStats[row.year].fair += row.count;
      });

      const yearlyArray = Object.entries(yearlyStats).map(([year, stats]) => ({
        year: Number(year),
        goodPercent: stats.total ? Number(((stats.good / stats.total) * 100).toFixed(1)) : 0,
        fairPercent: stats.total ? Number(((stats.fair / stats.total) * 100).toFixed(1)) : 0,
        rating: stats.total ? Number((((stats.good * 4) + (stats.fair * 3)) / stats.total).toFixed(2)) : 0
      }));

      results.push({ beachId, beachName, yearlyData: yearlyArray });
    }

    console.log("[beach-comparison]", { table: ANALYTICS_TABLE, ids: results.map(r => r.beachId), db: DB_NAME });
    res.json(results);
  } catch (e) {
    console.error("Beach-comparison error:", e);
    res.status(500).json({ error: "Failed to get comparison data", detail: e.message });
  }
});

/* ========== Algae risk for Gippsland Lakes (newly added) ========== */
/**
 * Read table: gipps_lake_quality.gipps_lake_algae
 * Returns: [{ segment_id?: string|number, lake_name?: string, level: 'Low'|'Medium'|'High' }]
 * Description: Auto-adapt column names (segment_id / lake_name / quality|hazard|risk|alert|level),
 *      Requires current MySQL user to have SELECT permission on this database
 */
function normAlgaeLevel(v) {
  const t = String(v ?? "").trim().toLowerCase();
  if (t.startsWith("low")) return "Low";
  if (t.startsWith("med")) return "Medium";
  if (t.startsWith("high")) return "High";
  return "Low"; // Default to Low to avoid all gray; can change to null if strict mode needed
}
function pickKey(obj, patterns) {
  const keys = Object.keys(obj || {});
  for (const k of keys) {
    const kk = k.toLowerCase();
    if (patterns.some(p => kk.match(p))) return k;
  }
  return null;
}
app.get("/api/algae", async (_req, res) => {
  try {
    const [rows] = await pool.query(
      "SELECT * FROM `gipps_lake_quality`.`gipps_lake_algae`"
    );
    if (!rows || !rows.length) return res.json([]);

    // 动态识别列名
    const probe = rows[0];
    const colSegment = pickKey(probe, [/segment.*id/, /^segment_id$/, /seg.*id/, /gipps.*segment/]);
    const colLake    = pickKey(probe, [/lake.*name/, /^lake_name$/, /water.*name/, /gipps.*lake.*name/]);
    const colLevel   = pickKey(probe, [/quality.*alert/, /hazard/, /risk/, /level/]);

    if (!colLevel || (!colSegment && !colLake)) {
      console.warn("[/api/algae] columns not recognized:", Object.keys(probe));
      return res.json([]);
    }

    const map = new Map();
    for (const r of rows) {
      const key =
        (r[colSegment] != null && String(r[colSegment]).trim()) ||
        (r[colLake] != null && String(r[colLake]).trim()) ||
        null;
      if (!key) continue;

      const level = normAlgaeLevel(r[colLevel]);
      map.set(String(key), {
        segment_id: r[colSegment] ?? null,
        lake_name: r[colLake] ?? null,
        level
      });
    }
    res.json(Array.from(map.values()));
  } catch (e) {
    console.error("GET /api/algae error:", e);
    res.status(500).json({ error: "DB_ALGAE_FAILED", detail: e.message });
  }
});

/* ========== Community reports (newly added) ========== */
/** List (used by frontend Recent reports) */
app.get("/api/reports", async (_req, res) => {
  try {
    const [rows] = await pool.query(
      `
      SELECT
        report_id,
        report_site_id,
        report_site_name,
        report_condition,
        report_clarity,
        report_crowd,
        report_comment,
        report_timestamp
      FROM \`${DB_NAME}\`.\`report\`
      ORDER BY report_timestamp DESC
      LIMIT 200
      `
    );
    res.json(rows);
  } catch (e) {
    console.error("GET /api/reports error:", e);
    res.status(500).json({ error: "DB_REPORTS_FAILED", detail: e.message });
  }
});

/** Add new report (write to database) */
app.post("/api/reports", async (req, res) => {
  try {
    let {
      site_id = null,   // Can be null
      site_name,        // Required: text from dropdown selection
      condition,        // Required: Safe/Caution/Unsafe
      clarity,          // Required
      crowd = "",       // Optional
      comment           // Required
    } = req.body || {};

    if (!site_name || !condition || !clarity || !comment) {
      return res.status(400).json({ error: "MISSING_FIELDS" });
    }

    // 1) First try to match site_id by name from backend's built-in SITES_FROM_SPEC
    const norm = s => String(s || "").trim().toLowerCase();
    const match = SITES_FROM_SPEC.find(x => norm(x.site_name) === norm(site_name));

    // 2) Calculate final site_id to write:
    //    - Prioritize site_id from frontend (if provided and is a number)
    //    - Otherwise use site_id matched by name
    //    - Otherwise default to 0 (to avoid NOT NULL constraint error)
    let finalSiteId = 0;
    if (site_id !== null && site_id !== "" && !Number.isNaN(Number(site_id))) {
      finalSiteId = Number(site_id);
    } else if (match) {
      finalSiteId = Number(match.site_id);
    } // else keep as 0

    const ts = new Date().toISOString().slice(0, 19).replace("T", " ");

    const sql = `
      INSERT INTO \`${DB_NAME}\`.\`report\`
        (report_site_id, report_site_name, report_condition, report_clarity,
         report_crowd, report_comment, report_timestamp)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    `;
    const params = [
      finalSiteId,      // No longer null, defaulted to 0
      site_name,
      condition,
      clarity,
      crowd,
      comment,
      ts
    ];

    const [r] = await pool.query(sql, params);
    res.json({ ok: true, report_id: r.insertId });
  } catch (e) {
    console.error("POST /api/reports error:", e);
    res.status(500).json({ error: "DB_INSERT_FAILED", detail: e.message });
  }
});


/* ========== Start ========== */
const PORT = Number(process.env.PORT || 8787);
app.listen(PORT, () => {
  console.log(`🚀 API listening at http://localhost:${PORT}`);
});
