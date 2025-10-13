<!-- src/components/TrendsView.vue -->
<template>
  <div class="beach-insights">
    <div class="page-container">
      <header class="page-header">
        <div class="header-content">
          <div class="header-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M2 12c0-2.8 2.2-5 5-5s5 2.2 5 5 2.2 5 5 5 5-2.2 5-5"/>
              <path d="M2 17h20"/>
            </svg>
          </div>
          <div>
        <h1>Beach Water Quality Insights</h1>
            <p class="header-subtitle">Analyze and compare water quality data across Port Phillip Bay beaches</p>
          </div>
        </div>
      </header>

      <!-- Horizontal menu within component (sub-navigation) -->
      <nav ref="subnavRef" class="subnav">
        <button
          :class="['subnav-item', activeTab==='single' && 'active']"
          @click="scrollToSection('single-section')"
        >
          Single Analysis
        </button>
        <button
          :class="['subnav-item', activeTab==='comparison' && 'active']"
          @click="scrollToSection('comparison-section')"
        >
          Comparison
        </button>
        <span class="subnav-spacer"></span>
        <button class="subnav-item ghost" @click="scrollToTop">Back to Top ↑</button>
      </nav>

      <!-- Vertical stack: Single beach analysis / Beach comparison -->
      <section class="stack">
        <!-- ===== Single Beach Analysis ===== -->
        <div id="single-section" class="card analysis-card" aria-labelledby="single-title">
          <div class="card-header">
            <div class="card-header-content">
              <div class="card-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 3v18h18"/>
                  <path d="m19 9-5 5-4-4-3 3"/>
                </svg>
              </div>
              <div>
            <h2 id="single-title">Single Beach Analysis</h2>
                <p>Detailed water quality trends and test results for individual beaches</p>
              </div>
            </div>
          </div>

          <!-- Controls -->
          <div class="controls-panel">
            <div class="controls-grid">
              <div class="field-group">
                <label for="beach-select" class="field-label">Beach Location</label>
                <select id="beach-select" class="field-input" v-model="selectedBeachId">
                  <option value="">Select a beach...</option>
                  <option v-for="b in beaches" :key="b.site_id" :value="String(b.site_id)">
                    {{ b.site_name }}
                  </option>
                </select>
              </div>

              <div class="field-group">
                <label for="year-select" class="field-label">Analysis Period</label>
                <select id="year-select" class="field-input" v-model="selectedYear">
                  <option value="">All available data</option>
                  <option v-for="y in years" :key="y" :value="String(y)">{{ y }}</option>
                </select>
              </div>

              <button class="btn-primary" @click="analyzeBeach">Analyze</button>
            </div>
          </div>

          <!-- Content: Chart + Sidebar (with horizontal scroll wrapper .hscroll inside card) -->
          <div class="hscroll">
            <div class="content-area">
              <div class="chart-section">
                <!-- Recommended Beaches Above Chart -->
                <div id="top-beaches-widget" class="recommended-beaches-section" v-show="topBeachesHtml !== 'Loading recommendations...'">
                  <div class="rec-section-title">Recommended Beaches</div>
                  <div id="top-beaches-content" v-html="topBeachesHtml"></div>
                </div>

                <div class="chart-title" v-show="!!chartTitle">{{ chartTitle }}</div>
                <div ref="chartContainer" class="chart-container">
                  <div v-if="showWelcome" class="empty-state">
                    <div class="welcome">
                      <div class="welcome-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                          <path d="M12 2a9 9 0 1 0 9 9c0-5-4-9-9-9Z"/>
                          <path d="M12 2v20"/>
                          <path d="M2 12h20"/>
                          <path d="M12 12l7-7"/>
                        </svg>
                      </div>
                      <div class="welcome-title">Find Your Perfect Beach</div>
                      <p class="welcome-text">
                        Discover the best swimming spots in Port Phillip Bay with real water quality data.
                        Select a beach above to see detailed analysis.
                      </p>
                    </div>
                  </div>
                </div>

                <!-- KPIs + Recommendation -->
                <div class="summary-wrap" v-show="showBreakdown">
                  <div class="summary">
                    <div class="summary-head">
                      <h3>Water Quality Summary</h3>
                      <div class="kpi-row">
                        <div class="kpi">
                          <div class="kpi-val">{{ metrics.totalTests }}</div>
                          <div class="kpi-sub">Total Tests</div>
                        </div>
                        <div class="kpi">
                          <div class="kpi-val">{{ metrics.avgRatingDisplay }}</div>
                          <div class="kpi-sub">Avg Rating</div>
                        </div>
                      </div>
                    </div>

                    <div class="kpi-grid">
                      <div class="kpi-pill green">
                        <span class="dot"></span><span>Safe</span><strong>{{ metrics.safe }}</strong>
                      </div>
                      <div class="kpi-pill amber">
                        <span class="dot"></span><span>Relatively Safe</span><strong>{{ metrics.relativelySafe }}</strong>
                      </div>
                      <div class="kpi-pill orange">
                        <span class="dot"></span><span>Caution</span><strong>{{ metrics.caution }}</strong>
                      </div>
                      <div class="kpi-pill red">
                        <span class="dot"></span><span>Unsafe</span><strong>{{ metrics.unsafe }}</strong>
                      </div>
                    </div>
                  </div>

                  <div class="recommend">
                    <div class="bar"></div>
                    <h3>Swimming Recommendation</h3>
                    <div class="recommend-text" v-html="recommendationHtml" />
                  </div>
                </div>
              </div>

              <!-- Right Sidebar -->
              <aside class="sidebar">
                <div class="insight-box" v-show="beachInfoVisible">
                  <div class="insight-title">Beach Information</div>
                  <div class="insight-body">
                    <div class="name">{{ beachInfo.name }}</div>
                    <div class="meta">📍 Port Phillip Bay, Melbourne</div>
                    <div class="period">
                      <small>ANALYSIS PERIOD</small>
                      <div>{{ beachInfo.period }}</div>
                    </div>
                  </div>
                </div>
              </aside>
            </div>
          </div>
        </div>

        <!-- Divider -->
        <div class="divider" aria-hidden="true"></div>

        <!-- ===== Beach Comparison ===== -->
        <div id="comparison-section" class="card comparison-card" aria-labelledby="comparison-title">
          <div class="card-header">
            <div class="card-header-content">
              <div class="card-icon comparison-icon">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <line x1="12" y1="5" x2="12" y2="19"/>
                  <polyline points="5 12 12 5 19 12"/>
                  <line x1="5" y1="12" x2="5" y2="19"/>
                  <line x1="19" y1="12" x2="19" y2="19"/>
                </svg>
              </div>
              <div>
            <h2 id="comparison-title">Beach Comparison</h2>
                <p>Compare water quality trends across multiple beaches over time</p>
              </div>
            </div>
          </div>

          <div class="controls-panel sticky-controls">
            <div class="controls-grid controls-3">
              <div class="field-group">
                <label class="field-label" for="beach1">Beach 1</label>
                <select id="beach1" class="field-input" v-model="cmp.beach1">
                  <option value="">Select first beach...</option>
                  <option v-for="b in beaches" :key="b.site_id + '-1'" :value="String(b.site_id)">
                    {{ b.site_name }}
                  </option>
                </select>
              </div>

              <div class="field-group">
                <label class="field-label" for="beach2">Beach 2</label>
                <select id="beach2" class="field-input" v-model="cmp.beach2">
                  <option value="">Select second beach...</option>
                  <option v-for="b in beaches" :key="b.site_id + '-2'" :value="String(b.site_id)">
                    {{ b.site_name }}
                  </option>
                </select>
              </div>

              <div class="field-group">
                <label class="field-label" for="beach3">Beach 3 (Optional)</label>
                <select id="beach3" class="field-input" v-model="cmp.beach3">
                  <option value="">Select third beach...</option>
                  <option v-for="b in beaches" :key="b.site_id + '-3'" :value="String(b.site_id)">
                    {{ b.site_name }}
                  </option>
                </select>
              </div>

              <button class="btn-primary" @click="compareBeaches">Compare</button>
            </div>
          </div>

          <!-- Content (one-col) also with horizontal scroll wrapper .hscroll -->
          <div class="hscroll">
            <div class="content-area one-col">
              <div class="chart-section">
                <div class="chart-title" v-show="!!comparisonTitle">{{ comparisonTitle }}</div>
                <div ref="comparisonContainer" class="chart-container soft">
                  <div v-if="showComparisonEmpty" class="empty-state">
                    Select 2-3 beaches to compare water quality trends
                  </div>
                </div>

                <div class="insight-box" v-show="comparisonInsightsVisible" style="margin-top: 14px;">
                  <div class="insight-title">Comparison Results</div>
                  <div id="comparison-details" v-html="comparisonHtml"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as d3 from 'd3'

/** ========= New: Unified API base path ========= */
const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '')

async function apiFetch (path, init) {
  const res = await fetch(`${API_BASE}${path}`, init)
  const ct = (res.headers.get('content-type') || '').toLowerCase()
  if (!ct.includes('application/json')) {
    const text = await res.text()
    throw new Error(`Non-JSON response from ${path}: ${text.slice(0, 200)}`)
  }
  return res.json()
}

/** ========= Utility: Identify actual scroll container (fix sub-navigation issues) ========= */
function getScrollParent (element) {
  let el = element?.parentElement
  while (el) {
    const style = getComputedStyle(el)
    const overflowY = style.overflowY
    if (/(auto|scroll|overlay)/.test(overflowY)) return el
    el = el.parentElement
  }
  return document.scrollingElement || document.documentElement
}
function getContainerTop (targetEl, scroller) {
  const targetRect = targetEl.getBoundingClientRect()
  const scrollerRect = scroller === document.scrollingElement
    ? { top: 0 }
    : scroller.getBoundingClientRect()
  const currentScrollTop = scroller === document.scrollingElement
    ? (window.pageYOffset || document.documentElement.scrollTop || 0)
    : scroller.scrollTop
  return currentScrollTop + (targetRect.top - scrollerRect.top)
}

/** ========= Sub-navigation (horizontal menu) ========= */
const subnavRef = ref(null)
const activeTab = ref('single')

function scrollToSection (id) {
  const el = document.getElementById(id)
  const nav = subnavRef.value
  if (!el) return
  const scroller = getScrollParent(el)
  const offset = (nav?.offsetHeight || 0) + 12
  const top = getContainerTop(el, scroller) - offset
  if (scroller === document.scrollingElement) {
    window.scrollTo({ top, behavior: 'smooth' })
  } else {
    scroller.scrollTo({ top, behavior: 'smooth' })
  }
}
function scrollToTop () {
  const scroller = getScrollParent(subnavRef.value || document.body)
  if (scroller === document.scrollingElement) {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } else {
    scroller.scrollTo({ top: 0, behavior: 'smooth' })
  }
}
function setupScrollSpy () {
  const single = document.getElementById('single-section')
  const comparison = document.getElementById('comparison-section')
  const rootEl = getScrollParent(subnavRef.value || document.body)
  if (!single || !comparison) return
  const options = {
    root: rootEl === document.scrollingElement ? null : rootEl,
    rootMargin: '-40% 0px -55% 0px',
    threshold: 0
  }
  const io = new IntersectionObserver(entries => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        activeTab.value = e.target.id === 'single-section' ? 'single' : 'comparison'
      }
    })
  }, options)
  io.observe(single)
  io.observe(comparison)
}

/** ========= Shared data ========= */
const beaches = ref([])
const years = ref([])

onMounted(async () => {
  await checkConnection()
  await loadBeaches()
  populateYears()
  await loadTopBeachesRecommendations()
  setupScrollSpy()
})

async function checkConnection () {
  try {
    // Originally /api/ping (backend doesn't have that route), changed to existing endpoint for health check
    await apiFetch('/api/beaches')
  } catch {}
}
async function loadBeaches () {
  try {
    const res = await apiFetch('/api/beaches')
    beaches.value = res
  } catch (e) {
    console.error('Failed to load beaches:', e)
  }
}
function populateYears () {
  const currentYear = new Date().getFullYear()
  const ys = []
  for (let y = currentYear; y >= 2013; y--) ys.push(y)
  years.value = ys
}

/** ========= Single beach analysis ========= */
const selectedBeachId = ref('')
const selectedYear = ref('')
const chartContainer = ref(null)
const chartTitle = ref('')
const showWelcome = ref(true)
const showBreakdown = ref(false)
const beachInfoVisible = ref(false)
const topBeachesHtml = ref('Loading recommendations...')
const recommendationHtml = ref('Select a beach and analyze to see swimming recommendations based on water quality data.')
const beachInfo = reactive({ name: '', period: '' })
const metrics = reactive({ totalTests: '-', avgRatingDisplay: '-', safe: 0, relativelySafe: 0, caution: 0, unsafe: 0 })
const popularBeaches = [99020, 99060, 99070, 99290, 99160]

function showLoading (containerRef) {
  if (containerRef?.value) containerRef.value.innerHTML = '<div class="empty-state">Loading...</div>'
}

async function loadTopBeachesRecommendations () {
  try {
    const recs = []
    if (!beaches.value.length) { try { await loadBeaches() } catch {} }
    for (const id of popularBeaches.slice(0, 3)) {
      try {
        const data = await apiFetch(`/api/beach-data?beachId=${id}&year=2024`)
        if (data.totalTests > 0) {
          const b = beaches.value.find(x => String(x.site_id) === String(id))
          const safePercent = ((data.qualityCounts.Safe || 0) / data.totalTests * 100)
          const avgRating = parseFloat(data.averageRating)
          recs.push({ name: b?.site_name || 'Unknown', safePercent: safePercent.toFixed(0), rating: avgRating.toFixed(1), id })
        }
      } catch {}
    }
    recs.sort((a, b) => parseFloat(b.safePercent) - parseFloat(a.safePercent))

    let html = ''
    if (recs.length) {
      html += `
        <div style="margin-bottom: 16px;">
          <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 14px;">
            <div style="width: 36px; height: 36px; background: linear-gradient(135deg, #0ea5e9, #0284c7); border-radius: 10px; display: flex; align-items: center; justify-content: center; color: white;">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6M18 9h1.5a2.5 2.5 0 0 0 0-5H18M4 22h16M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22M18 2H6v7a6 6 0 0 0 12 0V2Z"/>
              </svg>
            </div>
            <div>
              <div style="font-size: 15px; font-weight: 700; color: #1e293b; line-height: 1.2;">Top Recommended Beaches</div>
              <div style="font-size: 12px; color: #64748b; margin-top: 2px;">Based on 2024 water quality data</div>
            </div>
          </div>
          <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px;">`
      
      recs.forEach((b, i) => {
        const rank = i + 1
        const ratingColor = parseFloat(b.rating) >= 3.5 ? '#10b981' : parseFloat(b.rating) >= 3 ? '#3b82f6' : '#f59e0b'
        
        let rankBg = '#f1f5f9'
        let rankColor = '#64748b'
        let borderColor = '#e2e8f0'
        let bgGradient = 'white'
        let nameColor = '#1e293b'
        let fontSize = '15px'
        
        if (rank === 1) {
          rankBg = 'linear-gradient(135deg, #fbbf24, #f59e0b)'
          rankColor = 'white'
          borderColor = '#fbbf24'
          bgGradient = 'linear-gradient(135deg, #fffbeb, white)'
          nameColor = '#92400e'
          fontSize = '16px'
        } else if (rank === 2) {
          rankBg = 'linear-gradient(135deg, #94a3b8, #64748b)'
          rankColor = 'white'
          borderColor = '#cbd5e1'
          bgGradient = 'linear-gradient(135deg, #f8fafc, white)'
          nameColor = '#334155'
        } else if (rank === 3) {
          rankBg = 'linear-gradient(135deg, #fb923c, #f97316)'
          rankColor = 'white'
          borderColor = '#fed7aa'
          bgGradient = 'linear-gradient(135deg, #fff7ed, white)'
          nameColor = '#78350f'
        }
        
        html += `
          <div style="display: flex; flex-direction: column; background: ${bgGradient}; border: 2px solid ${borderColor}; border-radius: 14px; padding: 16px; transition: all 0.2s ease; cursor: pointer;" 
               onmouseover="this.style.transform='translateY(-4px)'; this.style.boxShadow='0 8px 20px rgba(0,0,0,0.12)';" 
               onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 2px 8px rgba(0,0,0,0.06)';">
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px;">
              <div style="width: 42px; height: 42px; background: ${rankBg}; color: ${rankColor}; border-radius: 10px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 800;">
                ${rank}
              </div>
              <div style="display: flex; align-items: center; gap: 5px; padding: 5px 10px; background: ${ratingColor}15; color: ${ratingColor}; border: 1px solid ${ratingColor}40; border-radius: 8px; font-size: 13px; font-weight: 700;">
                <svg width="13" height="13" viewBox="0 0 24 24" fill="${ratingColor}" stroke="none">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
                <span>${b.rating}</span>
              </div>
            </div>
            <div>
              <div style="font-size: ${fontSize}; font-weight: 700; color: ${nameColor}; line-height: 1.3; margin-bottom: 8px;">${b.name}</div>
              <div style="display: inline-flex; align-items: center; gap: 6px; padding: 5px 10px; background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; border-radius: 6px; font-size: 12px; font-weight: 600;">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3">
                  <polyline points="20 6 9 17 4 12"/>
                </svg>
                <span>${b.safePercent}% safe</span>
              </div>
            </div>
          </div>`
      })
      
      html += `
          </div>
        </div>`
      
      if (!beachInfoVisible.value) {
        html += `
          <div style="display: flex; align-items: center; gap: 10px; padding: 12px 16px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 10px; font-size: 12px; color: #64748b; margin-top: 12px;">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#0ea5e9" stroke-width="2" style="flex-shrink: 0;">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 16v-4"/>
              <path d="M12 8h.01"/>
            </svg>
            <span style="font-weight: 500;">These beaches have the best water quality ratings based on 2024 test data</span>
          </div>`
      }
    } else {
      html = '<div style="text-align: center; color: #64748b; padding: 32px 20px; font-size: 14px; font-weight: 500;">Unable to load recommendations at this time.</div>'
    }

    topBeachesHtml.value = html
    await nextTick()
  } catch (e) {
    console.error('Failed to load beach recommendations:', e)
    topBeachesHtml.value = '<div class="muted">Unable to load recommendations.</div>'
  }
}

async function analyzeBeach () {
  if (!selectedBeachId.value) { alert('Please select a beach to analyze'); return }
  const beach = beaches.value.find(b => String(b.site_id) === String(selectedBeachId.value))
  if (!beach) return
  showLoading(chartContainer)
  try {
    const url = `/api/beach-data?beachId=${selectedBeachId.value}${selectedYear.value ? `&year=${selectedYear.value}` : ''}`
    const data = await apiFetch(url)
    if (data.totalTests === 0) { if (chartContainer.value) chartContainer.value.innerHTML = '<div class="empty-state">No data available for selected period</div>'; return }
    await nextTick()
    renderBeachAnalysis(data, beach, selectedYear.value)
    updateBeachInsights(data, beach, selectedYear.value)
    await loadTopBeachesRecommendations()
  } catch (e) {
    console.error('Analysis failed:', e)
    if (chartContainer.value) chartContainer.value.innerHTML = '<div class="empty-state">Analysis failed. Please try again.</div>'
  }
}

function renderBeachAnalysis (data, beach, year) {
  if (!chartContainer.value) return
  chartContainer.value.innerHTML = ''
  chartTitle.value = `${beach.site_name} - Water Quality Analysis ${year ? `(${year})` : '(All Years)'} `
  showWelcome.value = false

  const width = 920, height = 420
  const margin = { top: 20, right: 120, bottom: 60, left: 70 }
  const svg = d3.select(chartContainer.value).append('svg').attr('width', width).attr('height', height)
  const g = svg.append('g').attr('transform', `translate(${margin.left},${margin.top})`)
  const innerWidth = width - margin.left - margin.right
  const innerHeight = height - margin.top - margin.bottom

  const monthlyData = (data.monthlyData || []).map(d => ({
    ...d,
    date: new Date(d.month + '-01'),
    safeCount: Math.round(d.total * (+d.goodPercent) / 100),
    relativelySafeCount: Math.round(d.total * (+d.fairPercent) / 100),
    cautionCount: Math.round(d.total * (+d.poorPercent) / 100),
    unsafeCount: d.total
      - Math.round(d.total * (+d.goodPercent) / 100)
      - Math.round(d.total * (+d.fairPercent) / 100)
      - Math.round(d.total * (+d.poorPercent) / 100)
  })).sort((a, b) => a.date - b.date)

  if (!monthlyData.length) { chartContainer.value.innerHTML = '<div class="empty-state">No monthly data</div>'; return }

  const xScale = d3.scaleBand().domain(monthlyData.map(d => d3.timeFormat('%b %Y')(d.date))).range([0, innerWidth]).padding(0.2)
  const yScale = d3.scaleLinear().domain([0, Math.ceil(d3.max(monthlyData, d => d.total) * 1.1)]).range([innerHeight, 0])
  const yScalePercent = d3.scaleLinear().domain([0, 100]).range([innerHeight, 0])

  const colors = { safe: '#10b981', relativelySafe: '#f59e0b', caution: '#f97316', unsafe: '#ef4444' }

  const stackedData = monthlyData.map(d => ({
    month: d3.timeFormat('%b %Y')(d.date),
    date: d.date,
    safePercent: +d.goodPercent,
    values: [
      { key: 'safe', value: d.safeCount, color: colors.safe },
      { key: 'relativelySafe', value: d.relativelySafeCount, color: colors.relativelySafe },
      { key: 'caution', value: d.cautionCount, color: colors.caution },
      { key: 'unsafe', value: d.unsafeCount, color: colors.unsafe }
    ]
  }))

  g.append('g').attr('transform', `translate(0,${innerHeight})`).call(d3.axisBottom(xScale))
    .selectAll('text').style('text-anchor', 'end').attr('dx', '-.8em').attr('dy', '.15em').attr('transform', 'rotate(-35)').style('font-size', '11px')
  g.append('g').call(d3.axisLeft(yScale).tickFormat(d3.format('d')))
    .append('text').attr('transform', 'rotate(-90)').attr('y', -50).attr('x', -innerHeight / 2).attr('dy', '1em')
    .style('text-anchor', 'middle').style('fill', '#4a5568').style('font-size', '12px').text('Test Count')

  const barGroups = g.selectAll('.bar-group').data(stackedData).enter().append('g').attr('class', 'bar-group').attr('transform', d => `translate(${xScale(d.month)},0)`)
  const barWidth = xScale.bandwidth() / 4
  const defs = svg.append('defs')
  const grad = (id, c1, c2) => { const lg = defs.append('linearGradient').attr('id', id).attr('x1', '0%').attr('y1', '0%').attr('x2', '0%').attr('y2', '100%'); lg.append('stop').attr('offset', '0%').attr('stop-color', c1); lg.append('stop').attr('offset', '100%').attr('stop-color', c2) }
  grad('safeGradient', '#34d399', '#10b981'); grad('relativelySafeGradient', '#fbbf24', '#f59e0b'); grad('cautionGradient', '#fb923c', '#f97316'); grad('unsafeGradient', '#f87171', '#ef4444')

  barGroups.each(function (m) {
    const group = d3.select(this)
    const gradientMap = { safe: 'url(#safeGradient)', relativelySafe: 'url(#relativelySafeGradient)', caution: 'url(#cautionGradient)', unsafe: 'url(#unsafeGradient)' }
    m.values.forEach((q, i) => {
      if (q.value > 0) {
        group.append('rect')
          .attr('x', i * barWidth).attr('width', barWidth - 1)
          .attr('y', yScale(q.value)).attr('height', innerHeight - yScale(q.value))
          .attr('fill', gradientMap[q.key] || q.color).attr('stroke', 'white').attr('stroke-width', 0.5).attr('rx', 2).attr('ry', 2)
          .append('title').text(`${q.key}: ${q.value} tests`)
      }
    })
  })

  const lineGradient = defs.append('linearGradient').attr('id', 'lineGradient').attr('x1', '0%').attr('y1', '0%').attr('x2', '100%').attr('y2', '0%')
  lineGradient.append('stop').attr('offset', '0%').attr('stop-color', '#10b981')
  lineGradient.append('stop').attr('offset', '50%').attr('stop-color', '#34d399')
  lineGradient.append('stop').attr('offset', '100%').attr('stop-color', '#10b981')

  const safeLine = d3.line().x(d => xScale(d.month) + xScale.bandwidth() / 2).y(d => yScalePercent(d.safePercent)).curve(d3.curveMonotoneX)
  g.append('path').datum(stackedData).attr('fill', 'none').attr('stroke', 'url(#lineGradient)').attr('stroke-width', 4).attr('d', safeLine)

  g.selectAll('.safe-point').data(stackedData).enter().append('circle').attr('class', 'safe-point')
    .attr('cx', d => xScale(d.month) + xScale.bandwidth() / 2).attr('cy', d => yScalePercent(d.safePercent)).attr('r', 6)
    .attr('fill', '#10b981').attr('stroke', 'white').attr('stroke-width', 3)
    .on('mouseover', function () { d3.select(this).transition().duration(200).attr('r', 8).attr('stroke-width', 4) })
    .on('mouseout', function () { d3.select(this).transition().duration(200).attr('r', 6).attr('stroke-width', 3) })
    .append('title').text(d => `${d.month}: ${d.safePercent}% safe`)

  const rightAxis = g.append('g').attr('class', 'right-axis-percentage').attr('transform', `translate(${innerWidth},0)`).call(d3.axisRight(yScalePercent).tickFormat(d => d + '%').tickValues([0, 20, 40, 60, 80, 100]))
  rightAxis.selectAll('text').style('fill', '#065f46').style('font-weight', 'bold').style('font-size', '14px')
  rightAxis.selectAll('line').style('stroke', '#10b981').style('stroke-width', '2').style('opacity', '0.8')
  rightAxis.select('.domain').style('stroke', '#10b981').style('stroke-width', '3').style('opacity', '0.9')
  rightAxis.append('text').attr('transform', 'rotate(-90)').attr('y', 65).attr('x', -innerHeight / 2).attr('dy', '1em').style('text-anchor', 'middle').style('fill', '#065f46').style('font-size', '14px').style('font-weight', 'bold').text('Safe Percentage (0-100%)')

  const legend = d3.select(chartContainer.value).append('div').attr('class', 'chart-legend')
  ;[
    { color: '#10b981', label: 'Safe', type: 'bar' },
    { color: '#f59e0b', label: 'Relatively Safe', type: 'bar' },
    { color: '#f97316', label: 'Caution', type: 'bar' },
    { color: '#ef4444', label: 'Unsafe', type: 'bar' },
    { color: '#22c55e', label: 'Safe % Trend', type: 'line' }
  ].forEach(item => { 
    const li = legend.append('div').attr('class', 'chart-legend-item')
    li.append('div').attr('class', item.type === 'bar' ? 'legend-bar' : 'legend-line').style('background-color', item.color)
    li.append('span').text(item.label) 
  })
}

function updateBeachInsights (data, beach, year) {
  beachInfo.name = beach.site_name
  beachInfo.period = year || 'All available data'
  beachInfoVisible.value = true

  metrics.totalTests = data.totalTests
  metrics.avgRatingDisplay = `${data.averageRating}/4`
  metrics.safe = data.qualityCounts.Safe || 0
  metrics.relativelySafe = data.qualityCounts['Relatively Safe'] || 0
  metrics.caution = data.qualityCounts.Caution || 0
  metrics.unsafe = data.qualityCounts.Unsafe || 0
  showBreakdown.value = true

  const safePercent = ((data.qualityCounts.Safe || 0) / data.totalTests * 100)
  const relSafePercent = ((data.qualityCounts['Relatively Safe'] || 0) / data.totalTests * 100)
  const cautionPercent = ((data.qualityCounts.Caution || 0) / data.totalTests * 100)
  const unsafePercent = ((data.qualityCounts.Unsafe || 0) / data.totalTests * 100)
  const combinedSafePercent = safePercent + relSafePercent

  let trendIndicator = ''
  if (data.monthlyData && data.monthlyData.length >= 6) {
    const recent = data.monthlyData.slice(-3)
    const early = data.monthlyData.slice(0, 3)
    const recentAvg = recent.reduce((s, m) => s + parseFloat(m.goodPercent), 0) / 3
    const earlyAvg = early.reduce((s, m) => s + parseFloat(m.goodPercent), 0) / 3
    if (recentAvg > earlyAvg + 5) trendIndicator = ' Recent improvements noted.'
    else if (recentAvg < earlyAvg - 5) trendIndicator = ' Recent decline in quality observed.'
  }

  let icon = '🤔', status = 'Mixed conditions - check recent reports.', text = 'Water quality varies significantly at this location. Check current conditions and recent weather before swimming.'
  if (combinedSafePercent >= 85 && unsafePercent <= 5) { icon = '🏊‍♀️'; status = 'Excellent choice for swimming!'; text = `This beach consistently maintains high water quality standards with ${safePercent.toFixed(1)}% safe and ${relSafePercent.toFixed(1)}% relatively safe tests.` }
  else if (combinedSafePercent >= 70 && unsafePercent <= 10) { icon = '✅'; status = 'Good choice for swimming.'; text = `Generally reliable water quality with ${combinedSafePercent.toFixed(1)}% of tests rating safe or relatively safe. Monitor conditions during peak season.` }
  else if (combinedSafePercent >= 50 && unsafePercent <= 20) { icon = '⚠️'; status = 'Swim with caution.'; text = `Variable water quality - ${cautionPercent.toFixed(1)}% of tests show caution levels. Check recent conditions and consider swimming after dry weather.` }
  else if (unsafePercent >= 20) { icon = '🚫'; status = 'High risk - consider alternatives.'; text = `${unsafePercent.toFixed(1)}% of tests rated as unsafe. This beach shows frequent water quality issues. Consider nearby alternatives for safer swimming.` }

  recommendationHtml.value = `${icon} <strong>${status}</strong> ${text}${trendIndicator}`
}

/** ========= Beach comparison ========= */
const comparisonContainer = ref(null)
const comparisonTitle = ref('')
const showComparisonEmpty = ref(true)
const comparisonInsightsVisible = ref(false)
const comparisonHtml = ref('')
const cmp = reactive({ beach1: '', beach2: '', beach3: '' })

async function compareBeaches () {
  if (!cmp.beach1 || !cmp.beach2) { alert('Please select at least 2 beaches to compare'); return }
  showLoading(comparisonContainer)
  const ids = [cmp.beach1, cmp.beach2, cmp.beach3].filter(Boolean).join(',')
  try {
    const data = await apiFetch(`/api/beach-comparison?beachIds=${ids}`)
    await nextTick()
    renderBeachComparison(data)
    updateComparisonInsights(data)
  } catch (e) {
    console.error('Comparison failed:', e)
    if (comparisonContainer.value) comparisonContainer.value.innerHTML = '<div class="empty-state">Comparison failed. Please try again.</div>'
  }
}

function renderBeachComparison (data) {
  if (!comparisonContainer.value) return
  const container = comparisonContainer.value
  Array.from(container.children).forEach(el => { if (!el.classList?.contains('empty-state')) container.removeChild(el) })

  comparisonTitle.value = 'Beach Water Quality Comparison'
  showComparisonEmpty.value = false

  const width = 920, height = 420
  const margin = { top: 20, right: 100, bottom: 60, left: 70 }
  const svg = d3.select(container).append('svg').attr('width', width).attr('height', height)
  const g = svg.append('g').attr('transform', `translate(${margin.left},${margin.top})`)
  const innerWidth = width - margin.left - margin.right
  const innerHeight = height - margin.top - margin.bottom

  const allYears = [...new Set(data.flatMap(b => b.yearlyData.map(d => d.year)))].sort()
  const xScale = d3.scaleLinear().domain(d3.extent(allYears)).range([0, innerWidth])
  const yScale = d3.scaleLinear().domain([0, 100]).range([innerHeight, 0])

  g.append('g').attr('transform', `translate(0,${innerHeight})`).call(d3.axisBottom(xScale).tickFormat(d3.format('d')))
  g.append('g').call(d3.axisLeft(yScale)).append('text')
    .attr('transform', 'rotate(-90)').attr('y', -50).attr('x', -innerHeight / 2).attr('dy', '1em')
    .style('text-anchor', 'middle').style('fill', '#4a5568').style('font-size', '12px').text('Safe Water Quality (%)')

  const palette = ['#2b6cb0', '#48bb78', '#ed8936', '#7c3aed', '#e11d48']

  data.forEach((beach, i) => {
    const color = palette[i % palette.length]
    const line = d3.line().x(d => xScale(d.year)).y(d => yScale(+d.goodPercent)).curve(d3.curveMonotoneX)
    g.append('path').datum(beach.yearlyData).attr('fill', 'none').attr('stroke', color).attr('stroke-width', 3).attr('d', line)
    g.selectAll(`.point-${i}`).data(beach.yearlyData).enter().append('circle').attr('class', `point-${i}`)
      .attr('cx', d => xScale(d.year)).attr('cy', d => yScale(+d.goodPercent)).attr('r', 4).attr('fill', color)
  })

  const legend = d3.select(container).append('div').attr('class', 'chart-legend')
  data.forEach((beach, i) => { 
    const li = legend.append('div').attr('class', 'chart-legend-item')
    li.append('div').attr('class', 'legend-line').style('background-color', palette[i % palette.length])
    li.append('span').text(beach.beachName) 
  })
}

function updateComparisonInsights (data) {
  const avgRatings = data.map(beach => {
    const ratings = beach.yearlyData.map(y => parseFloat(y.rating))
    const avg = ratings.reduce((s, r) => s + r, 0) / ratings.length
    const latestYear = Math.max(...beach.yearlyData.map(d => d.year))
    const latest = beach.yearlyData.find(d => d.year === latestYear)
    return { name: beach.beachName, rating: avg, latestSafe: parseFloat(latest?.goodPercent || 0), trend: beach.yearlyData.length > 1 ? (parseFloat(beach.yearlyData[beach.yearlyData.length - 1].rating) - parseFloat(beach.yearlyData[0].rating)) : 0 }
  }).sort((a, b) => b.rating - a.rating)

  let html = '<div style="margin-bottom:12px;">'
  html += `<div style="display:flex; align-items:center; gap:8px; margin-bottom:10px;"><span style="font-size:20px;">🏆</span><div style="font-weight:700; color:#2b6cb0; font-size:16px;">Best Choice: ${avgRatings[0].name}</div></div>`
  html += '<div style="background:#f0f9ff; border:1px solid #0ea5e9; border-radius:8px; padding:12px; margin-bottom:12px;">'
  html += `<div style="font-size:13px; color:#0c4a6e; margin-bottom:6px;"><strong>Why this beach wins:</strong></div>`
  html += `<div style="font-size:12px; color:#0c4a6e; line-height:1.4;">• Highest average rating: <strong>${avgRatings[0].rating.toFixed(1)}/4.0</strong><br>• Recent safe tests: <strong>${avgRatings[0].latestSafe.toFixed(0)}%</strong><br>• Quality trend: ${avgRatings[0].trend > 0 ? '📈 Improving' : avgRatings[0].trend < -0.1 ? '📉 Declining' : '➡️ Stable'}</div></div>`

  html += '<div style="margin-bottom:10px;">'
  html += '<div style="font-weight:700; color:#1a202c; margin-bottom:6px;">Complete Ranking:</div>'
  avgRatings.forEach((b, i) => {
    const emoji = i === 0 ? '🥇' : i === 1 ? '🥈' : '🥉'
    const ratingColor = b.rating >= 3.5 ? '#22c55e' : b.rating >= 3 ? '#3b82f6' : '#f59e0b'
    const trendIcon = b.trend > 0.1 ? '📈' : b.trend < -0.1 ? '📉' : '➡️'
    html += `
      <div style="display:flex; justify-content:space-between; align-items:center; padding:8px 12px; background:${i === 0 ? '#f0f9ff' : '#f8fafc'}; border-radius:6px; margin-bottom:4px; border:1px solid ${i === 0 ? '#0ea5e9' : '#e2e8f0'};">
        <div style="display:flex; align-items:center; gap:8px;">
          <span>${emoji}</span>
          <span style="font-weight:600; color:#1a202c; font-size:14px;">${b.name}</span>
        </div>
        <div style="display:flex; align-items:center; gap:12px;">
          <div style="text-align:right;">
            <div style="font-weight:700; color:${ratingColor}; font-size:13px;">${b.rating.toFixed(1)}/4</div>
            <div style="font-size:11px; color:#64748b;">${b.latestSafe.toFixed(0)}% safe</div>
          </div>
          <span style="font-size:12px;" title="Quality trend">${trendIcon}</span>
        </div>
      </div>`
  })
  html += '</div>'
  html += '<div style="background:#fffbeb; border:1px solid #f59e0b; border-radius:6px; padding:10px;">'
  html += '<div style="font-size:12px; color:#92400e; line-height:1.4;"><strong>💡 Quick Decision Guide:</strong><br>'
  html += `• <strong>Best Overall:</strong> ${avgRatings[0].name}<br>`
  const mostStable = avgRatings.find(b => Math.abs(b.trend) < 0.1)?.name || avgRatings[0].name
  html += `• <strong>Most Consistent:</strong> ${mostStable}<br>`
  html += '• Always check recent conditions before swimming</div></div>'
  comparisonHtml.value = html
  comparisonInsightsVisible.value = true
}
</script>

<style scoped>
/* Page framework */
.beach-insights { 
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #2d3748; 
  min-height: 100vh; 
  padding: 24px 0;
}
.page-container { 
  max-width: 1180px; 
  margin: 0 auto; 
  padding: 0 20px; 
}
.page-header { 
  margin-bottom: 24px; 
  background: white;
  padding: 24px 28px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
}
.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}
.header-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}
.page-header h1 { 
  font-size: 32px; 
  font-weight: 800; 
  color: #1a202c; 
  margin: 0;
}
.header-subtitle {
  font-size: 15px;
  color: #64748b;
  margin: 4px 0 0;
}

/* Horizontal menu within component (sub-navigation) */
.subnav {
  position: sticky; top: 0; z-index: 20;
  display: flex; align-items: center; gap: 8px;
  background: white;
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 10px; 
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}
.subnav-item {
  padding: 10px 18px; 
  border: none; 
  border-radius: 12px;
  background: transparent; 
  cursor: pointer; 
  font-weight: 600; 
  color: #64748b;
  transition: all 0.3s ease;
}
.subnav-item:hover { 
  background: #f1f5f9; 
  color: #1e293b;
}
.subnav-item.active { 
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff; 
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
.subnav-item.ghost { 
  font-weight: 500; 
  color: #94a3b8; 
}
.subnav-spacer { flex: 1 1 auto; }

/* Vertical stack container */
.stack { display: flex; flex-direction: column; gap: 20px; }

/* Card and header */
.card { 
  background: #ffffff; 
  border-radius: 20px; 
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  border: none;
  overflow: hidden; 
}
.card-header { 
  padding: 24px 28px; 
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0; 
}
.card-header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}
.card-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}
.card-icon.comparison-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}
.card-header h2 { 
  font-size: 20px; 
  font-weight: 800; 
  color: #1a202c; 
  margin: 0;
}
.card-header p { 
  color: #64748b; 
  margin: 4px 0 0;
  font-size: 14px;
}

/* Divider */
.divider { 
  height: 1px; 
  width: 100%; 
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.5), transparent);
  margin: 32px 0;
}

/* Controls area */
.controls-panel { 
  padding: 24px 28px; 
  border-bottom: 1px solid #e2e8f0; 
  background: #ffffff;
}
.controls-grid { 
  display: grid; 
  grid-template-columns: 1fr 1fr auto; 
  gap: 16px; 
  align-items: end; 
}
.controls-grid.controls-3 { 
  grid-template-columns: 1fr 1fr 1fr auto; 
}
.field-group { 
  display: flex; 
  flex-direction: column; 
  gap: 8px; 
}
.field-label { 
  font-size: 13px; 
  font-weight: 600; 
  color: #475569;
  display: flex;
  align-items: center;
  gap: 6px;
}
.field-input { 
  padding: 12px 16px; 
  border: 2px solid #e2e8f0; 
  border-radius: 12px; 
  font-size: 14px; 
  background: #fff;
  transition: all 0.3s ease;
  font-weight: 500;
}
.field-input:focus { 
  outline: none; 
  border-color: #667eea; 
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}
.btn-primary { 
  padding: 12px 24px; 
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff; 
  border: none; 
  border-radius: 12px; 
  font-weight: 700; 
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
.btn-primary:hover { 
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

/* Horizontal scroll wrapper: horizontal scrollbar appears only when content overflows (inside card) */
.hscroll {
  overflow-x: auto;
  overflow-y: visible;
  padding-bottom: 8px;
  -webkit-overflow-scrolling: touch;
  border-radius: 0 0 12px 12px;
}
.hscroll > .content-area { min-width: 1180px; }
.hscroll > .content-area.one-col { min-width: 980px; }

/* Content layout */
.content-area { display: grid; grid-template-columns: 1.2fr .8fr; gap: 18px; padding: 18px; min-height: 520px; }
.content-area.one-col { grid-template-columns: 1fr; }
.chart-title { 
  text-align: center; 
  font-weight: 800; 
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  font-size: 18px;
}

/* Chart container */
.chart-container {
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px;
  min-height: 420px; position: relative; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
}
.chart-container.soft { background: linear-gradient(135deg, #ffffff, #eef2ff); }
.chart-container::before { content:''; position:absolute; inset:-2px; border-radius:14px; background: linear-gradient(135deg, #10b981, #3b82f6, #8b5cf6); opacity:.08; z-index:-1; }
.empty-state { 
  color: #64748b; 
  font-size: 15px; 
  text-align: center; 
}
.welcome {
  max-width: 400px;
  margin: 0 auto;
}
.welcome-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}
.welcome-title { 
  font-size: 24px; 
  font-weight: 800; 
  color: #1a202c; 
  margin-bottom: 12px; 
}
.welcome-text { 
  color: #64748b;
  line-height: 1.6;
}

/* Summary and KPI */
.summary-wrap { 
  display: grid; 
  grid-template-columns: 1fr; 
  gap: 16px; 
  margin-top: 20px; 
}
.summary { 
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 2px solid #e2e8f0;
  border-radius: 16px; 
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.summary-head { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  margin-bottom: 16px; 
}
.summary-head h3 { 
  margin: 0; 
  font-size: 18px; 
  color: #1a202c; 
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 8px;
}
.summary-head h3::before {
  content: '📊';
  font-size: 20px;
}
.kpi-row { 
  display: flex; 
  gap: 32px;
}
.kpi .kpi-val { 
  font-size: 28px; 
  font-weight: 800; 
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-align: center;
}
.kpi .kpi-sub { 
  font-size: 11px; 
  color: #64748b; 
  text-transform: uppercase; 
  letter-spacing: 0.5px; 
  text-align: center;
  font-weight: 600;
}
.kpi-grid { 
  display: grid; 
  grid-template-columns: repeat(4, 1fr); 
  gap: 12px;
}
.kpi-pill { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; 
  gap: 10px; 
  background: #f8fafc; 
  border: 2px solid #e2e8f0; 
  border-radius: 12px; 
  padding: 12px 14px; 
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s ease;
}
.kpi-pill:hover {
  border-color: #cbd5e1;
  transform: translateY(-1px);
}
.kpi-pill .dot { 
  width: 12px; 
  height: 12px; 
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}
.kpi-pill.green .dot { background: #22c55e; }
.kpi-pill.amber .dot { background: #f59e0b; }
.kpi-pill.orange .dot { background: #f97316; }
.kpi-pill.red .dot { background: #ef4444; }

/* Recommendation block */
.recommend { 
  background: linear-gradient(135deg, #dbeafe 0%, #e0f2fe 100%);
  border: 2px solid #0ea5e9;
  border-radius: 16px; 
  padding: 20px;
  box-shadow: 0 4px 12px rgba(14, 165, 233, 0.15);
}
.recommend .bar { 
  width: 6px; 
  height: 32px; 
  background: linear-gradient(180deg, #0ea5e9, #06b6d4);
  border-radius: 3px; 
  margin-bottom: 12px;
}
.recommend h3 { 
  margin: 0 0 8px; 
  font-size: 17px; 
  color: #0c4a6e; 
  font-weight: 800;
  display: flex;
  align-items: center;
  gap: 8px;
}
.recommend h3::before {
  content: '💡';
  font-size: 20px;
}
.recommend-text { 
  color: #0c4a6e; 
  font-weight: 600;
  line-height: 1.6;
}

/* Right sidebar */
.sidebar { 
  display: flex; 
  flex-direction: column; 
  gap: 16px; 
  min-width: 380px;
}
.insight-box { 
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border: 2px solid #e2e8f0;
  border-radius: 16px; 
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}
.insight-box:hover {
  border-color: #cbd5e1;
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
  transform: translateY(-2px);
}
.insight-title { 
  font-weight: 800; 
  color: #1a202c; 
  margin-bottom: 12px;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.insight-title::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}
.insight-body .name { 
  font-weight: 800; 
  color: #111827; 
  margin-bottom: 4px;
  font-size: 18px;
}
.insight-body .meta { 
  color: #6b7280; 
  font-size: 13px; 
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.insight-body .period { 
  background: linear-gradient(135deg, #dbeafe, #e0f2fe);
  border: 2px solid #0ea5e9;
  color: #0c4a6e;
  border-radius: 12px;
  padding: 14px;
}
.insight-body .period small { 
  display: block; 
  font-weight: 800; 
  font-size: 11px; 
  margin-bottom: 4px;
  letter-spacing: 0.5px;
}

/* Recommended Beaches Section (Above Chart) */
.recommended-beaches-section {
  background: linear-gradient(135deg, #e0f2fe 0%, #dbeafe 100%);
  border: 2px solid transparent;
  background-clip: padding-box;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  position: relative;
}
.recommended-beaches-section::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: 16px;
  padding: 2px;
  background: linear-gradient(135deg, #0ea5e9, #06b6d4);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
}
.rec-section-title {
  font-size: 18px;
  font-weight: 800;
  color: #0c4a6e;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.rec-section-title::before {
  content: '';
  width: 6px;
  height: 24px;
  background: linear-gradient(180deg, #0ea5e9, #06b6d4);
  border-radius: 3px;
}

/* ===== Beach Recommendations Section - Inline Styled (No CSS Needed) ===== */
/* All styles are applied inline for guaranteed rendering */

/* Chart Legend - Updated Styling */
.chart-legend { 
  display: flex; 
  justify-content: center; 
  align-items: center;
  gap: 20px; 
  margin-top: 16px; 
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  flex-wrap: wrap;
}
.chart-legend-item { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  font-size: 13px; 
  font-weight: 600;
  color: #1e293b;
}
.legend-bar { 
  width: 20px; 
  height: 12px; 
  border-radius: 3px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.legend-line { 
  width: 24px; 
  height: 3px; 
  border-radius: 2px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}

/* Small screen adaptation (comment out the following two lines if you want to force horizontal scrolling instead of reflowing content to single column) */
/*
@media (max-width: 1024px) {
  .content-area { grid-template-columns: 1fr; }
  .sidebar { min-width: 100% }
}
*/
</style>

