<template>
  <!-- Full-screen welcome (no header/footer) -->
  <div class="h-screen w-screen overflow-hidden relative text-white">
    <!-- 背景图 -->
    <img
      src="https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=2000&auto=format&fit=crop"
      alt="Sunny beach"
      class="absolute inset-0 h-full w-full object-cover"
    />

    <!-- 半透明渐变遮罩，保证文字对比度 -->
    <div class="absolute inset-0 bg-gradient-to-b from-black/40 via-black/30 to-black/40"></div>

    <div class="relative h-full w-full flex items-center justify-center">
      <!-- Watermark -->
      <div
        aria-hidden="true"
        class="pointer-events-none select-none absolute inset-0 flex items-center justify-center opacity-10"
        style="font-weight:800; font-size:22vw; line-height:1; letter-spacing:-0.04em;"
      >
        <span>OceanMate</span>
      </div>

      <!-- Content -->
      <div class="relative z-10 max-w-4xl w-full px-6 text-left">
        <!-- 主标题更大 -->
        <h1 class="text-5xl sm:text-6xl md:text-7xl font-extrabold tracking-tight drop-shadow-lg">
          Rain &amp; Water Safety Outlook
        </h1>

        <!-- 副标题更大 -->
        <p class="mt-6 text-xl sm:text-2xl text-white/95 drop-shadow">
          See how recent rainfall and upcoming weather may affect swimming conditions.
        </p>

        <!-- 说明文字更大 -->
        <p class="mt-4 text-lg sm:text-xl text-white/90 leading-relaxed max-w-2xl drop-shadow">
          Real-time water quality, rainfall alerts, and historical safety trends for Port Phillip Bay — all in one place.
        </p>

        <!-- Date selector -->
        <div class="mt-10">
          <label for="date" class="block text-lg font-medium mb-3">
            Select a Date to View Conditions
          </label>
          <input
            id="date"
            type="date"
            v-model="selectedDate"
            class="rounded-lg px-5 py-3 text-lg text-slate-900"
          />
        </div>

        <!-- Actions -->
        <div class="mt-8 flex flex-wrap gap-4">
          <button
            @click="viewConditions"
            class="inline-flex items-center rounded-xl bg-white text-sky-900 px-6 py-3 font-semibold shadow text-lg hover:bg-white/95 transition"
          >
            View Conditions
          </button>

          <button
            @click="goMap"
            class="inline-flex items-center rounded-xl border border-white/70 bg-transparent px-6 py-3 font-semibold text-white text-lg hover:bg-white/10 transition"
          >
            View Map
          </button>
        </div>
      </div>

      <!-- Bottom fade -->
      <div class="pointer-events-none absolute inset-x-0 bottom-0 h-32 bg-gradient-to-t from-black/20 to-transparent"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedDate = ref(new Date().toISOString().slice(0, 10))

function markSeen() {
  try { localStorage.setItem('swm_seen_welcome', '1') } catch {}
}

function viewConditions() {
  markSeen()
  router.push({ path: '/rain-weather', query: { date: selectedDate.value } })
}

function goMap() {
  markSeen()
  router.push('/map')
}
</script>

<style scoped>
:host {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
