import {useToastStore} from '@/stores/toast'

export function useToast() {
  const store = useToastStore()
  return {
    info: (message, duration) => store.push({ type: 'info', message, duration }),
    success: (message, duration) => store.push({ type: 'success', message, duration }),
    warning: (message, duration) => store.push({ type: 'warning', message, duration }),
    error: (message, duration) => store.push({ type: 'error', message, duration }),
    clear: () => store.clear(),
  }
}
