<template>
  <section class="min-h-screen bg-white">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Main content -->
      <div class="mt-6 grid grid-cols-1 lg:grid-cols-2 gap-8">
        <!-- Left: composer -->
        <div>
          <div class="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
            <div class="px-6 pt-6">
              <h1 class="text-3xl font-extrabold tracking-tight">Share Your Beach Update</h1>
              <p class="mt-2 text-slate-600">See what other swimmers are saying in real time.</p>

              <!-- Safety chips -->
              <div class="mt-5 flex flex-wrap gap-3">
                <button type="button" @click="setStatus('Safe')"    :class="statusClass('Safe')">Safe</button>
                <button type="button" @click="setStatus('Caution')" :class="statusClass('Caution')">Caution</button>
                <button type="button" @click="setStatus('Unsafe')"  :class="statusClass('Unsafe')">Unsafe</button>
              </div>

              <!-- Input -->
              <div class="mt-4 space-y-3">
                <input v-model="draft.text" type="text" placeholder="What did you notice?"
                       class="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-[15px] focus:outline-none focus:ring-2 focus:ring-sky-500"/>

                <!-- Image picker -->
                <div class="flex items-center gap-3">
                  <label class="inline-flex items-center gap-2 cursor-pointer">
                    <span class="inline-flex items-center justify-center w-9 h-9 rounded-lg bg-white ring-1 ring-slate-200">📷</span>
                    <input type="file" accept="image/*" multiple class="hidden" @change="onPickImages">
                    <span class="text-slate-700">Add Photo(s)</span>
                  </label>
                  <button v-if="picked.length" class="text-sm text-slate-600 hover:underline" @click="clearPicked">Clear selected ({{ picked.length }})</button>
                </div>

                <!-- Preview -->
                <div v-if="picked.length" class="grid grid-cols-3 gap-3">
                  <div v-for="(p,i) in picked" :key="p.id" class="relative aspect-video overflow-hidden rounded-xl ring-1 ring-slate-200">
                    <img :src="p.url" alt="preview" class="h-full w-full object-cover"/>
                    <button class="absolute top-1 right-1 rounded-md bg-black/50 text-white text-xs px-1.5 py-0.5"
                            @click="removePicked(i)">✕</button>
                  </div>
                </div>

                <label class="inline-flex items-center gap-2 cursor-pointer">
                  <input type="checkbox" v-model="draft.asAlias" class="rounded border-slate-300 text-sky-600 focus:ring-sky-500"/>
                  <span>Post as alias (hide my name)</span>
                </label>
              </div>
            </div>

            <!-- footer controls -->
            <div class="px-6 py-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 bg-slate-50 border-t border-slate-100">
              <div class="flex flex-wrap items-center gap-4 text-sm text-slate-700">
                <span class="text-slate-500">Local DB: <b>IndexedDB</b> (oceanmate_local / posts)</span>
              </div>
              <div class="flex items-center gap-3">
                <button @click="postUpdate"
                        class="inline-flex items-center justify-center rounded-xl bg-gradient-to-r from-[#0e6efd] to-[#1196ff] px-6 py-2.5 font-semibold text-white shadow-sm hover:opacity-95 disabled:opacity-60"
                        :disabled="!canPost">
                  Post Update
                </button>
                <button class="rounded-xl bg-white px-4 py-2 ring-1 ring-slate-300 text-slate-700 hover:bg-slate-50"
                        @click="clearAll">Clear local DB</button>
              </div>
            </div>
          </div>
        </div>

        <!-- Right: feed -->
        <div>
          <div class="rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
              <h2 class="text-lg font-semibold">Recent Updates</h2>
              <button class="text-sm text-slate-600 hover:underline" @click="reload">Refresh</button>
            </div>

            <div v-if="loading" class="p-6 text-slate-500 text-sm">Loading…</div>

            <div v-else-if="updates.length === 0" class="p-6 text-slate-500 text-sm">
              No updates yet. Be the first to share.
            </div>

            <div v-else class="divide-y divide-slate-100">
              <article v-for="u in updates" :key="u.id" class="p-6">
                <header class="flex items-start justify-between">
                  <div>
                    <div class="flex items-center gap-2 text-[15px]">
                      <span class="font-semibold">{{ u.user }}</span>
                      <span class="text-slate-400">•</span>
                      <span class="text-slate-500">{{ formatTime(u.ts) }}</span>
                    </div>
                  </div>
                  <span :class="pillClass(u.status)">{{ u.status.toUpperCase() }}</span>
                </header>

                <p class="mt-3 text-slate-800 leading-6">{{ u.text }}</p>

                <!-- Images (from Blob -> objectURL) -->
                <div v-if="u.images && u.images.length" class="mt-4 grid grid-cols-2 md:grid-cols-3 gap-3">
                  <div v-for="(img, idx) in u.images" :key="idx" class="overflow-hidden rounded-xl ring-1 ring-slate-200">
                    <img :src="img.url" :alt="img.name || 'image'" class="w-full h-48 object-cover"/>
                  </div>
                </div>

                <footer class="mt-4 flex items-center gap-5 text-slate-500 text-sm">
                  <span class="inline-flex items-center gap-1">👍 {{ u.likes || 0 }}</span>
                  <span class="inline-flex items-center gap-1">💬 {{ u.comments || 0 }}</span>
                  <span class="inline-flex items-center gap-1">↻ {{ u.shares || 0 }}</span>
                </footer>
              </article>
            </div>
          </div>

          <footer class="mt-6 text-xs text-slate-500 text-center">
            © 2025 OceanMate • Built for Port Phillip Bay
          </footer>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'

/* -------------------- Local DB (IndexedDB) -------------------- */
const DB_NAME = 'oceanmate_local'
const DB_VER = 1
const STORE_POSTS = 'posts'

function openDB () {
  return new Promise((resolve, reject) => {
    const req = indexedDB.open(DB_NAME, DB_VER)
    req.onupgradeneeded = () => {
      const db = req.result
      if (!db.objectStoreNames.contains(STORE_POSTS)) {
        const s = db.createObjectStore(STORE_POSTS, { keyPath: 'id' })
        s.createIndex('ts', 'ts', { unique: false })
      }
    }
    req.onsuccess = () => resolve(req.result)
    req.onerror = () => reject(req.error)
  })
}

async function dbPutPost(post) {
  const db = await openDB()
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_POSTS, 'readwrite')
    tx.objectStore(STORE_POSTS).put(post)
    tx.oncomplete = () => resolve()
    tx.onerror = () => reject(tx.error)
  })
}

async function dbGetAllPostsDesc() {
  const db = await openDB()
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_POSTS, 'readonly')
    const idx = tx.objectStore(STORE_POSTS).index('ts')
    const req = idx.getAll()
    req.onsuccess = () => {
      const arr = req.result || []
      arr.sort((a,b) => b.ts - a.ts)
      resolve(arr)
    }
    req.onerror = () => reject(req.error)
  })
}

async function dbClearAll() {
  const db = await openDB()
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_POSTS, 'readwrite')
    tx.objectStore(STORE_POSTS).clear()
    tx.oncomplete = () => resolve()
    tx.onerror = () => reject(tx.error)
  })
}

/* -------------------- UI state -------------------- */
const draft = ref({ status: 'Safe', text: '', asAlias: false })
const picked = ref([]) // [{id, file, url}]
const updates = ref([]) // [{id, ts, user, status, text, images:[{name, type, url(derived)}]}]
const loading = ref(false)

const canPost = computed(() => draft.value.text.trim().length > 0 || picked.value.length > 0)

/* -------------------- Helpers -------------------- */
function setStatus(s) { draft.value.status = s }

function statusClass(s) {
  const base = 'inline-flex items-center justify-center px-5 py-2 rounded-xl font-semibold shadow-sm ring-1 text-sm';
  if (draft.value.status === s) {
    if (s === 'Safe') return `${base} bg-green-600 text-white ring-green-700`;
    if (s === 'Caution') return `${base} bg-amber-500 text-white ring-amber-600`;
    return `${base} bg-rose-600 text-white ring-rose-700`;
  }
  if (s === 'Safe') return `${base} bg-green-50 text-green-700 ring-green-200 hover:bg-green-100`;
  if (s === 'Caution') return `${base} bg-amber-50 text-amber-700 ring-amber-200 hover:bg-amber-100`;
  return `${base} bg-rose-50 text-rose-700 ring-rose-200 hover:bg-rose-100`;
}

function pillClass(s) {
  const base = 'inline-flex items-center rounded-md px-2 py-0.5 text-xs ring-1';
  if (s === 'Safe') return `${base} bg-green-50 text-green-700 ring-green-200`;
  if (s === 'Caution') return `${base} bg-amber-50 text-amber-700 ring-amber-200`;
  return `${base} bg-rose-50 text-rose-700 ring-rose-200`;
}

function formatTime(ts) {
  const d = new Date(ts)
  return d.toLocaleString()
}

/* -------------------- Image picking -------------------- */
function onPickImages(e) {
  const files = Array.from(e.target.files || [])
  files.forEach(f => {
    const id = `${Date.now()}-${Math.random().toString(36).slice(2,8)}`
    const url = URL.createObjectURL(f) // preview
    picked.value.push({ id, file: f, url })
  })
  e.target.value = '' // 允许重复选择同一文件
}

function removePicked(i) {
  const [p] = picked.value.splice(i, 1)
  if (p?.url) URL.revokeObjectURL(p.url)
}

function clearPicked() {
  picked.value.forEach(p => p?.url && URL.revokeObjectURL(p.url))
  picked.value = []
}

/* -------------------- CRUD: posts -------------------- */
async function postUpdate() {
  if (!canPost.value) return
  // 将 File 转存为 Blob（IndexedDB 可以直接存 Blob）
  const images = picked.value.map(p => ({
    name: p.file.name,
    type: p.file.type,
    blob: p.file,          // 直接保存 File/Blob
  }))

  const entry = {
    id: (crypto?.randomUUID?.() ?? String(Date.now() + Math.random())),
    ts: Date.now(),
    user: draft.value.asAlias ? 'Anonymous' : 'You',
    status: draft.value.status,
    text: draft.value.text.trim(),
    images,               // 保存 Blob；读取时再转为 objectURL
    likes: 0, comments: 0, shares: 0,
  }

  await dbPutPost(entry)
  await reload()

  // reset
  draft.value.text = ''
  draft.value.asAlias = false
  clearPicked()
}

async function reload() {
  loading.value = true
  try {
    const rows = await dbGetAllPostsDesc()
    // 为每条记录生成可显示的 objectURL（不修改原始数据）
    updates.value = rows.map(r => ({
      ...r,
      images: (r.images || []).map(img => ({
        ...img,
        url: URL.createObjectURL(img.blob)
      }))
    }))
  } finally {
    loading.value = false
  }
}

async function clearAll() {
  await dbClearAll()
  // 释放之前生成的 objectURL
  updates.value.forEach(u => u.images?.forEach(i => i.url && URL.revokeObjectURL(i.url)))
  updates.value = []
}

/* 回收：离开页面时 revoke objectURL */
onBeforeUnmount(() => {
  clearPicked()
  updates.value.forEach(u => u.images?.forEach(i => i.url && URL.revokeObjectURL(i.url)))
})

/* Init */
onMounted(async () => {
  await reload()
})
</script>

<style scoped>
/**** optional scrollbar tweaks ****/
::-webkit-scrollbar{height:10px;width:10px}
::-webkit-scrollbar-thumb{background:#cbd5e1;border-radius:8px}
</style>
