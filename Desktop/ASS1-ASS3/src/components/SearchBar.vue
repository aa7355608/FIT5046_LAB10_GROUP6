<template>
    <div class="flex flex-col sm:flex-row gap-4">
      <!-- Search Input -->
      <div class="flex-1 relative">
        <IconSearch class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
        <input
          type="text"
          placeholder="Search by location, street, or parking facility..."
          v-model="localSearch"
          @input="emitSearch"
          class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>
  
      <!-- Zone Select & Filter Button -->
      <div class="flex gap-4">
        <div class="relative">
          <IconMapPin class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <select
            v-model="localZone"
            @change="emitZone"
            class="pl-10 pr-8 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white"
          >
            <option v-for="zone in zones" :key="zone" :value="zone">
              {{ zone }}
            </option>
          </select>
        </div>
  
        <button
          class="flex items-center space-x-2 px-6 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
          @click="$emit('filter-click')"
        >
          <IconFilter class="w-5 h-5 text-gray-500" />
          <span class="text-gray-700">Filters</span>
        </button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, watch } from 'vue'
  
  const props = defineProps({
    searchQuery: { type: String, default: '' },
    selectedZone: { type: String, default: null }
  })
  
  const emit = defineEmits(['update:searchQuery', 'update:selectedZone', 'filter-click'])
  
  const zones = [
    'All Zones',
    'Collins Street',
    'Bourke Street',
    'Flinders Street',
    'Elizabeth Street',
    'Swanston Street',
    'Queen Street',
    'King Street',
    'Spencer Street'
  ]
  
  const localSearch = ref(props.searchQuery)
  const localZone = ref(props.selectedZone || 'All Zones')
  
  watch(() => props.searchQuery, val => localSearch.value = val)
  watch(() => props.selectedZone, val => localZone.value = val || 'All Zones')
  
  function emitSearch() {
    emit('update:searchQuery', localSearch.value)
  }
  
  function emitZone() {
    emit('update:selectedZone', localZone.value === 'All Zones' ? null : localZone.value)
  }
  </script>
  
  <!-- Minimal inline SVG icons -->
  <script>
  export default {
    components: {
      IconSearch: {
        props: { class: String },
        template: `<svg :class="class" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
        </svg>`
      },
      IconMapPin: {
        props: { class: String },
        template: `<svg :class="class" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 10c0 6-9 12-9 12S3 16 3 10a9 9 0 0 1 18 0z"/>
          <circle cx="12" cy="10" r="3"/>
        </svg>`
      },
      IconFilter: {
        props: { class: String },
        template: `<svg :class="class" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polygon points="22 3 2 3 10 13 10 19 14 21 14 13 22 3"/>
        </svg>`
      }
    }
  }
  </script>
  