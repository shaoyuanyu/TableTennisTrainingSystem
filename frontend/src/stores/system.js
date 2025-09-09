// src/stores/system.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useSystemStore = defineStore('system', () => {
  const loading = ref(false)
  const theme = ref('light')
  const language = ref('zh-cn')
  
  const isLoading = computed(() => loading.value)
  
  const setLoading = (status) => {
    loading.value = status
  }
  
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    document.documentElement.setAttribute('data-theme', theme.value)
  }
  
  return {
    loading,
    theme,
    language,
    isLoading,
    setLoading,
    toggleTheme
  }
})