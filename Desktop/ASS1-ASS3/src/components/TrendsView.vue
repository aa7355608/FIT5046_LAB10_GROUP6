<!-- src/components/TrendsView.vue -->
<template>
  <div class="beach-insights">
    <div class="page-container">
      <header class="page-header">
        <h1>Beach Water Quality Insights</h1>
      </header>

      <!-- 组件内横向菜单（子导航） -->
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

      <!-- 上下堆叠：单沙滩分析 / 沙滩比较 -->
      <section class="stack">
        <!-- ===== 单沙滩分析 ===== -->
        <div id="single-section" class="card analysis-card" aria-labelledby="single-title">
          <div class="card-header">
            <h2 id="single-title">Single Beach Analysis</h2>
            <p>Pick a beach and time period to render test counts + safe% trend.</p>
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

          <!-- 内容：图表 + 侧栏（在卡片内部加横向滚动外壳 .hscroll） -->
          <div class="hscroll">
            <div class="content-area">
              <div class="chart-section">
                <div class="chart-title" v-show="!!chartTitle">{{ chartTitle }}</div>
                <div ref="chartContainer" class="chart-container">
                  <div v-if="showWelcome" class="empty-state">
                    <div class="welcome">
                      <div class="welcome-title">🏊‍♀️ Find Your Perfect Beach</div>
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

              <!-- 右侧栏 -->
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

                <div id="top-beaches-widget" class="insight-box">
                  <div class="insight-title">🏆 Recommended Beaches</div>
                  <div id="top-beaches-content" v-html="topBeachesHtml"></div>
                </div>
              </aside>
            </div>
          </div>
        </div>

        <!-- 分割线 -->
        <div class="divider" aria-hidden="true"></div>

        <!-- ===== 沙滩比较 ===== -->
        <div id="comparison-section" class="card comparison-card" aria-labelledby="comparison-title">
          <div class="card-header">
            <h2 id="comparison-title">Beach Comparison</h2>
            <p>Select 2–3 beaches and compare yearly safe percentages.</p>
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

          <!-- 内容（one-col）同样加横向滚动外壳 .hscroll -->
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
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as d3 from 'd3'

/** ========= 新增：统一 API 基础路径 ========= */
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

/** ========= 工具：识别真实滚动容器（修复子导航失效） ========= */
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

/** ========= 子导航（横向菜单） ========= */
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

/** ========= 共享数据 ========= */
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
    // 原先是 /api/ping（后端没有该路由），改为已有接口做健康检查
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

/** ========= 单沙滩分析 ========= */
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
let wireTimer = null
onBeforeUnmount(() => { if (wireTimer) clearTimeout(wireTimer) })

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
      html += '<div class="recs-intro">Based on 2024 water quality data:</div>'
      recs.forEach((b, i) => {
        const emoji = i === 0 ? '🥇' : i === 1 ? '🥈' : '🥉'
        const ratingColor = parseFloat(b.rating) >= 3.5 ? '#22c55e' : parseFloat(b.rating) >= 3 ? '#3b82f6' : '#f59e0b'
        html += `
          <div class="rec-item">
            <div class="rec-left">
              <div class="rec-row"><span class="med">${emoji}</span><span class="name">${b.name}</span></div>
              <div class="muted">${b.safePercent}% safe tests in 2024</div>
            </div>
            <div class="rec-right">
              <div class="rating" style="color:${ratingColor}">${b.rating}/4</div>
              <button class="rec-select-btn" data-beach="${b.name}">Select</button>
            </div>
          </div>`
      })
      if (!beachInfoVisible.value) {
        html += '<div class="tip">💡 <strong>Tip:</strong> Compare multiple beaches to find the best option for your visit</div>'
      }
    } else {
      html = '<div class="muted">Unable to load recommendations at this time.</div>'
    }

    topBeachesHtml.value = html
    await nextTick()
    wireRecommendationButtons()
  } catch (e) {
    console.error('Failed to load beach recommendations:', e)
    topBeachesHtml.value = '<div class="muted">Unable to load recommendations.</div>'
  }
}

function wireRecommendationButtons () {
  if (wireTimer) clearTimeout(wireTimer)
  wireTimer = setTimeout(() => {
    const widget = document.getElementById('top-beaches-widget')
    if (!widget) return
    widget.querySelectorAll('.rec-select-btn').forEach(btn => {
      if (!btn.__wired) {
        btn.__wired = true
        btn.addEventListener('click', () => {
          const title = btn.getAttribute('data-beach') || ''
          const beach = beaches.value.find(b => b.site_name === title)
          if (beach) { selectedBeachId.value = String(beach.site_id); selectedYear.value = '2024'; analyzeBeach() }
        })
      }
    })
  }, 0)
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

  g.selectAll('.safe-label').data(stackedData).enter().append('text').attr('class', 'safe-label')
    .attr('x', d => xScale(d.month) + xScale.bandwidth() / 2 + 15).attr('y', d => yScalePercent(d.safePercent)).attr('dy', '0.35em')
    .style('text-anchor', 'start').style('fill', '#065f46').style('font-size', '13px').style('font-weight', '700').text(d => `${d.safePercent}%`)

  const rightAxis = g.append('g').attr('class', 'right-axis-percentage').attr('transform', `translate(${innerWidth},0)`).call(d3.axisRight(yScalePercent).tickFormat(d => d + '%').tickValues([0, 20, 40, 60, 80, 100]))
  rightAxis.selectAll('text').style('fill', '#065f46').style('font-weight', 'bold').style('font-size', '14px')
  rightAxis.selectAll('line').style('stroke', '#10b981').style('stroke-width', '2').style('opacity', '0.8')
  rightAxis.select('.domain').style('stroke', '#10b981').style('stroke-width', '3').style('opacity', '0.9')
  rightAxis.append('text').attr('transform', 'rotate(-90)').attr('y', 65).attr('x', -innerHeight / 2).attr('dy', '1em').style('text-anchor', 'middle').style('fill', '#065f46').style('font-size', '14px').style('font-weight', 'bold').text('Safe Percentage (0-100%)')

  const legend = d3.select(chartContainer.value).append('div').attr('class', 'legend')
  ;[
    { color: '#10b981', label: 'Safe', type: 'bar' },
    { color: '#f59e0b', label: 'Relatively Safe', type: 'bar' },
    { color: '#f97316', label: 'Caution', type: 'bar' },
    { color: '#ef4444', label: 'Unsafe', type: 'bar' },
    { color: '#22c55e', label: 'Safe %', type: 'line' }
  ].forEach(item => { const li = legend.append('div').attr('class', 'legend-item'); li.append('div').attr('class', item.type === 'bar' ? 'legend-bar' : 'legend-marker').style('background-color', item.color); li.append('span').text(item.label) })
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

/** ========= 沙滩比较 ========= */
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

  const legend = d3.select(container).append('div').attr('class', 'legend')
  data.forEach((beach, i) => { const li = legend.append('div').attr('class', 'legend-item'); li.append('div').attr('class', 'legend-marker').style('background-color', palette[i % palette.length]); li.append('span').text(beach.beachName) })
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
/* 页面框架 */
.beach-insights { background: #f7fafc; color: #2d3748; min-height: 100vh; }
.page-container { max-width: 1180px; margin: 24px auto; padding: 0 20px; }
.page-header { margin-bottom: 12px; }
.page-header h1 { font-size: 28px; font-weight: 800; color: #1a202c; }

/* 组件内横向菜单（子导航） */
.subnav {
  position: sticky; top: 0; z-index: 20;
  display: flex; align-items: center; gap: 8px;
  background: rgba(247,250,252,0.9); backdrop-filter: blur(6px);
  border: 1px solid #e2e8f0; border-radius: 12px;
  padding: 8px; margin-bottom: 18px;
  box-shadow: 0 2px 8px rgba(15,23,42,.05);
}
.subnav-item {
  padding: 8px 14px; border: 1px solid transparent; border-radius: 10px;
  background: transparent; cursor: pointer; font-weight: 700; color: #334155;
}
.subnav-item:hover { background: #eef2ff; }
.subnav-item.active { background: #2b6cb0; color: #fff; border-color: #2b6cb0; }
.subnav-item.ghost { font-weight: 600; color: #475569; }
.subnav-spacer { flex: 1 1 auto; }

/* 上下堆叠容器 */
.stack { display: flex; flex-direction: column; gap: 20px; }

/* 卡片与头部 */
.card { background: #ffffff; border-radius: 14px; box-shadow: 0 1px 3px rgba(0,0,0,0.08), 0 1px 2px rgba(0,0,0,0.06); border: 1px solid #e2e8f0; overflow: hidden; }
.card-header { padding: 18px 20px; background: linear-gradient(180deg, #f8fafc, #f1f5f9); border-bottom: 1px solid #e2e8f0; }
.card-header h2 { font-size: 18px; font-weight: 800; color: #1a202c; }
.card-header p { color: #64748b; margin-top: 4px; }

/* 分割线 */
.divider { height: 2px; width: 100%; background: linear-gradient(90deg, #eef2ff, #e2e8f0, #eef2ff); border-radius: 2px; }

/* 控件区 */
.controls-panel { padding: 16px 18px; border-bottom: 1px solid #e2e8f0; background: #fbfdff; }
.controls-grid { display: grid; grid-template-columns: 1fr 1fr auto; gap: 12px; align-items: end; }
.controls-grid.controls-3 { grid-template-columns: 1fr 1fr 1fr auto; }
.field-group { display: flex; flex-direction: column; gap: 6px; }
.field-label { font-size: 13px; font-weight: 700; color: #334155; }
.field-input { padding: 10px 12px; border: 1px solid #cbd5e0; border-radius: 8px; font-size: 14px; background: #fff; }
.field-input:focus { outline: none; border-color: #2b6cb0; box-shadow: 0 0 0 3px rgba(43,108,176,.12) }
.btn-primary { padding: 10px 16px; background: #2b6cb0; color: #fff; border: none; border-radius: 8px; font-weight: 700; cursor: pointer; }
.btn-primary:hover { background: #2c5282 }

/* 横向滚动外壳：只在内容溢出时出现横向滚动条（在卡片内部） */
.hscroll {
  overflow-x: auto;
  overflow-y: visible;
  padding-bottom: 8px;
  -webkit-overflow-scrolling: touch;
  border-radius: 0 0 12px 12px;
}
.hscroll > .content-area { min-width: 1180px; }
.hscroll > .content-area.one-col { min-width: 980px; }

/* 内容布局 */
.content-area { display: grid; grid-template-columns: 1.2fr .8fr; gap: 18px; padding: 18px; min-height: 520px; }
.content-area.one-col { grid-template-columns: 1fr; }
.chart-title { text-align: center; font-weight: 800; color: #1f2937; margin-bottom: 10px; }

/* 图表容器 */
.chart-container {
  background: linear-gradient(135deg, #ffffff, #f8fafc);
  border: 1px solid #e2e8f0; border-radius: 12px; padding: 16px;
  min-height: 420px; position: relative; display: flex; align-items: center; justify-content: center;
  box-shadow: 0 1px 2px rgba(0,0,0,0.03);
}
.chart-container.soft { background: linear-gradient(135deg, #ffffff, #eef2ff); }
.chart-container::before { content:''; position:absolute; inset:-2px; border-radius:14px; background: linear-gradient(135deg, #10b981, #3b82f6, #8b5cf6); opacity:.08; z-index:-1; }
.empty-state { color:#64748b; font-size: 15px; text-align:center; }
.welcome-title { font-size: 22px; font-weight: 800; color:#1a202c; margin-bottom: 8px; }
.welcome-text { color:#4b5563 }

/* 摘要与 KPI */
.summary-wrap { display: grid; grid-template-columns: 1fr; gap: 14px; margin-top: 16px; }
.summary { background:#fff; border:1px solid #e2e8f0; border-radius: 12px; padding: 16px; }
.summary-head { display:flex; align-items:center; justify-content:space-between; margin-bottom: 12px; }
.summary-head h3 { margin:0; font-size: 16px; color:#1a202c; font-weight:800 }
.kpi-row { display:flex; gap: 28px }
.kpi .kpi-val { font-size: 22px; font-weight: 800; color:#2b6cb0; text-align:center }
.kpi .kpi-sub { font-size: 11px; color:#64748b; text-transform: uppercase; letter-spacing:.5px; text-align:center }
.kpi-grid { display:grid; grid-template-columns: repeat(4, 1fr); gap:10px }
.kpi-pill { display:flex; align-items:center; justify-content:space-between; gap:10px; background:#f8fafc; border:1px solid #e2e8f0; border-radius: 10px; padding: 10px 12px; font-size: 13px }
.kpi-pill .dot { width:10px; height:10px; border-radius: 50% }
.kpi-pill.green .dot { background:#22c55e }
.kpi-pill.amber .dot { background:#f59e0b }
.kpi-pill.orange .dot { background:#f97316 }
.kpi-pill.red .dot { background:#ef4444 }

/* 推荐块 */
.recommend { background: linear-gradient(135deg, #eff6ff, #dbeafe); border:1px solid #3b82f6; border-radius: 12px; padding: 16px; }
.recommend .bar { width:6px; height:28px; background:#2563eb; border-radius: 3px; margin-bottom: 10px }
.recommend h3 { margin: 0 0 6px; font-size: 15px; color:#1e40af; font-weight:800 }
.recommend-text { color:#1e40af; font-weight: 600 }

/* 右侧栏 */
.sidebar { display:flex; flex-direction:column; gap: 12px; min-width: 380px }
.insight-box { background:#fff; border:1px solid #e2e8f0; border-radius: 12px; padding: 16px }
.insight-title { font-weight:800; color:#1a202c; margin-bottom: 8px }
.insight-body .name { font-weight:800; color:#111827; margin-bottom: 2px }
.insight-body .meta { color:#6b7280; font-size: 13px; margin-bottom: 8px }
.insight-body .period { background:#f0f9ff; border:1px solid #0ea5e9; color:#0c4a6e; border-radius:8px; padding:10px }
.insight-body .period small { display:block; font-weight:800; font-size:11px; margin-bottom: 2px }

/* 推荐列表 */
.recs-intro { margin-bottom:10px; font-size:12px; color:#64748b }
.rec-item { display:flex; justify-content:space-between; align-items:center; padding:10px 12px; margin-bottom:8px; background:#f8fafc; border:1px solid #e2e8f0; border-radius:10px }
.rec-left .rec-row { display:flex; align-items:center; gap:8px; margin-bottom:2px }
.rec-left .name { font-weight: 700; color:#1a202c; font-size: 14px }
.rec-left .muted { font-size: 12px; color:#64748b }
.rec-right { display:flex; flex-direction:column; align-items:center; gap:6px }
.rec-right .rating { font-weight: 800; font-size: 16px }
.rec-right .rec-select-btn { font-size: 12px; padding: 6px 12px; background:#2b6cb0; color:#fff; border:none; border-radius:6px; cursor:pointer; font-weight:600; min-width:60px }

/* 图例 */
.legend { display:flex; justify-content:center; gap:20px; margin-top: 10px; flex-wrap: wrap }
.legend-item { display:flex; align-items:center; gap:8px; font-size:13px; color:#4a5568 }
.legend-marker { width:16px; height:3px; border-radius:2px }

/* 小屏适配（如需强制横向滚动查看，不把内容重排为单列，可注释下面两行） */
/*
@media (max-width: 1024px) {
  .content-area { grid-template-columns: 1fr; }
  .sidebar { min-width: 100% }
}
*/
</style>

