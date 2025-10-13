<template>
  <section class="min-h-screen bg-white">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <h1 class="text-2xl sm:text-3xl font-extrabold tracking-tight">Contact Us</h1>
      <p class="mt-2 text-slate-600">
        Share what you see at the beach today. Your report helps others choose safer spots.
      </p>

      <!-- alias -->
      <div class="mt-4">
        <span class="inline-flex items-center gap-2 rounded-xl bg-slate-100 px-3 py-1.5 text-slate-700 text-sm ring-1 ring-slate-200">
          <span class="text-xs uppercase tracking-wide text-slate-500">Your alias</span>
          <strong>{{ username }}</strong>
        </span>
        <button class="ml-3 text-sm text-sky-700 hover:underline" @click="regenUsername">Regenerate</button>
      </div>

      <!-- form -->
      <div class="mt-6 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <form @submit.prevent="handleSubmit" class="grid grid-cols-1 md:grid-cols-2 gap-5">
          <!-- Beach select (fixed list, not from database) -->
          <div class="col-span-1">
            <label class="block text-sm font-medium text-slate-700 mb-1">Beach</label>
            <select v-model="form.site_name" required
                    class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500">
              <option value="" disabled>Select a beach…</option>
              <option v-for="b in BEACHES_SOURCE" :key="b" :value="b">{{ b }}</option>
            </select>
            <p class="mt-1 text-xs text-slate-500">Select beach name from dropdown (data is hardcoded in frontend).</p>
          </div>

          <!-- Condition -->
          <div class="col-span-1">
            <label class="block text-sm font-medium text-slate-700 mb-1">Condition (your view)</label>
            <select v-model="form.condition" required
                    class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500">
              <option value="" disabled>Select…</option>
              <option value="Safe">Safe</option>
              <option value="Caution">Caution</option>
              <option value="Unsafe">Unsafe</option>
            </select>
          </div>

          <!-- Water clarity -->
          <div class="col-span-1">
            <label class="block text-sm font-medium text-slate-700 mb-1">Water clarity</label>
            <select v-model="form.clarity" required
                    class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500">
              <option value="" disabled>Select…</option>
              <option>Clear</option>
              <option>Slightly murky</option>
              <option>Murky</option>
              <option>Discoloured / foamy</option>
            </select>
          </div>

          <!-- Crowd / optional -->
          <div class="col-span-1">
            <label class="block text-sm font-medium text-slate-700 mb-1">Crowd (optional)</label>
            <select v-model="form.crowd"
                    class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500">
              <option value="">—</option>
              <option>Quiet</option>
              <option>Moderate</option>
              <option>Busy</option>
            </select>
          </div>

          <!-- Comment -->
          <div class="md:col-span-2">
            <label class="block text-sm font-medium text-slate-700 mb-1">Comment</label>
            <textarea v-model.trim="form.comment" rows="4" required
                      placeholder="Briefly describe what you observed (e.g., after rain, debris, unusual smell, etc.)"
                      class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 focus:outline-none focus:ring-2 focus:ring-sky-500"></textarea>
          </div>

          <!-- Submit -->
          <div class="md:col-span-2 flex items-center gap-3">
            <button type="submit"
                    :disabled="submitting"
                    class="inline-flex items-center rounded-xl bg-sky-600 px-5 py-2.5 font-semibold text-white hover:bg-sky-700 transition disabled:opacity-60">
              {{ submitting ? 'Submitting…' : 'Submit report' }}
            </button>
            <span v-if="justSaved" class="text-sm text-green-700">Thanks! Your report was posted.</span>
          </div>
        </form>
      </div>

      <!-- Recent reports -->
      <div class="mt-8 rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold">Recent community reports</h2>
          <button class="text-sm text-slate-600 hover:underline" @click="loadReports">Refresh</button>
        </div>

        <div v-if="submissions.length === 0" class="text-slate-500 text-sm">No reports yet.</div>

        <div v-else class="overflow-x-auto">
          <table class="min-w-full text-left text-sm">
            <thead class="text-slate-600">
              <tr>
                <th class="py-2 pr-4">When</th>
                <th class="py-2 pr-4">Beach</th>
                <th class="py-2 pr-4">Condition</th>
                <th class="py-2 pr-4">Clarity</th>
                <th class="py-2 pr-4">Crowd</th>
                <th class="py-2 pr-4">Comment</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="r in submissions" :key="r.report_id" class="border-t border-slate-100 align-top">
                <td class="py-2 pr-4 whitespace-nowrap">{{ formatTime(r.report_timestamp) }}</td>
                <td class="py-2 pr-4 whitespace-nowrap">{{ r.report_site_name }}</td>
                <td class="py-2 pr-4 whitespace-nowrap">
                  <span :class="badgeClass(r.report_condition)">{{ r.report_condition }}</span>
                </td>
                <td class="py-2 pr-4 whitespace-nowrap">{{ r.report_clarity || '—' }}</td>
                <td class="py-2 pr-4 whitespace-nowrap">{{ r.report_crowd || '—' }}</td>
                <td class="py-2 pr-4">{{ r.report_comment }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Cards -->
      <div class="mt-6 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <article v-for="r in submissions" :key="r.report_id + '-card'"
                 class="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
          <div class="flex items-center justify-between">
            <div class="text-sm font-semibold">{{ r.report_site_name }}</div>
            <div class="text-xs text-slate-500">{{ formatTime(r.report_timestamp) }}</div>
          </div>
          <p class="mt-2 text-slate-800 text-sm leading-6">{{ r.report_comment }}</p>
          <div class="mt-3 flex flex-wrap gap-2 text-xs">
            <span :class="badgeClass(r.report_condition)">{{ r.report_condition }}</span>
            <span class="rounded-md bg-slate-100 px-2 py-0.5 ring-1 ring-slate-200">{{ r.report_clarity }}</span>
            <span v-if="r.report_crowd" class="rounded-md bg-slate-100 px-2 py-0.5 ring-1 ring-slate-200">{{ r.report_crowd }}</span>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'

/* ---------- Fixed beach list (hardcoded in frontend) ---------- */
const BEACHES_SOURCE = [
  'Port Melbourne','Hampton','Half Moon Bay','Mentone','Seaford','Frankston Surf Life Saving Club',
  'Safety Beach','Blairgowrie','Sorrento','The Dell','Eastern Beach','Portsea','Rye','Dromana',
  'Canadian Bay','Frankston Coast Guard','Mordialloc','Sandringham','Elwood','St Kilda','Sandridge',
  'Williamstown','Altona','Werribee Sth','Portarlington Beach (new site)','St Leonards','Beaumaris',
  'Brighton Life Saving Club','Mornington Life Saving Club','Rosebud Life Saving Club',
  'Mt Martha Life Saving Club','Carrum Surf, Life Saving Club','Aspendale Life Saving Club',
  'South Melbourne Life Saving Club','Santa Casa','Black Rock Life Saving Club'
].sort((a,b)=>a.localeCompare(b))

/* ---------- state ---------- */
const submissions = ref([])
const justSaved = ref(false)
const submitting = ref(false)
const username = ref('')

const form = ref({
  // No longer depends on database site table, so site_id is not used; directly write report_site_name
  site_name: '',
  condition: '',
  clarity: '',
  crowd: '',
  comment: ''
})

/* ---------- helpers ---------- */
function genUsername() {
  const animals = ['Seastar','Dolphin','Pelican','Gull','Seal','Kelp','Coral','Sandpiper','Marlin','Albatross']
  const adj = ['Bright','Calm','Swift','Sunny','Brave','Lucky','Gentle','Tidy','Merry','Quiet']
  const id = Math.floor(1000 + Math.random() * 9000)
  return `${adj[Math.floor(Math.random()*adj.length)]}-${animals[Math.floor(Math.random()*animals.length)]}-${id}`
}
function regenUsername() {
  username.value = genUsername()
  try { localStorage.setItem('swm_user_alias', username.value) } catch {}
}
function formatTime(mysqlDatetime) {
  if (!mysqlDatetime) return ''
  return new Date(mysqlDatetime.replace(' ', 'T')).toLocaleString()
}
function badgeClass(cond) {
  const base = 'rounded-md px-2 py-0.5 ring-1 text-xs'
  if (cond === 'Safe')   return `${base} bg-green-50 text-green-700 ring-green-200`
  if (cond === 'Caution')return `${base} bg-amber-50 text-amber-700 ring-amber-200`
  return `${base} bg-rose-50 text-rose-700 ring-rose-200`
}

/* ---------- API ---------- */
async function apiGet(url) {
  const r = await fetch(url)
  if (!r.ok) throw new Error('HTTP ' + r.status)
  return r.json()
}
async function apiPost(url, data) {
  const r = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
  if (!r.ok) {
    const msg = await r.text().catch(()=> '')
    throw new Error('HTTP ' + r.status + ' ' + msg)
  }
  return r.json()
}

/* ---------- load reports ---------- */
async function loadReports() {
  try {
    submissions.value = await apiGet('/api/reports')
  } catch (e) {
    console.error('loadReports failed:', e)
    submissions.value = []
  }
}

/* ---------- submit ---------- */
async function handleSubmit() {
  const f = form.value
  if (!f.site_name || !f.condition || !f.clarity || !f.comment) return
  submitting.value = true
  try {
    await apiPost('/api/reports', {
      site_id: null,                     // Don't use site_id, set to NULL
      site_name: f.site_name,            // -> report_site_name (from dropdown fixed list)
      condition: f.condition,            // -> report_condition
      clarity: f.clarity,                // -> report_clarity
      crowd: f.crowd || '',              // -> report_crowd
      comment: `[${username.value}] ${f.comment}` // -> report_comment
    })
    justSaved.value = true
    setTimeout(() => (justSaved.value = false), 1800)
    // reset 可选
    f.crowd = ''
    f.comment = ''
    await loadReports()
  } catch (e) {
    alert('Submit failed: ' + e.message)
  } finally {
    submitting.value = false
  }
}

/* ---------- init ---------- */
onMounted(async () => {
  try {
    const alias = localStorage.getItem('swm_user_alias')
    username.value = alias || genUsername()
    if (!alias) localStorage.setItem('swm_user_alias', username.value)
  } catch { username.value = genUsername() }

  await loadReports()
})
</script>

<style scoped>
table th, table td { padding-right: 0.75rem; }
</style>
