import request from "supertest"
import { describe, it, expect } from "vitest"

const BASE_URL = "http://localhost:8787"

describe("Backend API tests", () => {
  it("BE-01 /api/ping returns ok and timestamp", async () => {
    const res = await request(BASE_URL).get("/api/ping")
    expect(res.status).toBe(200)
    expect(res.body).toHaveProperty("ok", true)
    expect(res.body).toHaveProperty("now")
  })

  it("BE-02 /api/sites returns list of sites", async () => {
    const res = await request(BASE_URL).get("/api/sites")
    expect(res.status).toBe(200)
    expect(Array.isArray(res.body)).toBe(true)
    if (res.body.length > 0) {
      expect(res.body[0]).toHaveProperty("site_id")
      expect(res.body[0]).toHaveProperty("site_name")
      expect(res.body[0]).toHaveProperty("latitude")
      expect(res.body[0]).toHaveProperty("longitude")
    }
  })

  it("BE-03 /api/sites/:id returns single site or 404", async () => {
    const res = await request(BASE_URL).get("/api/sites/1")
    expect([200, 404]).toContain(res.status)
  })
})
