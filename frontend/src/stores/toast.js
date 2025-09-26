import {defineStore} from 'pinia'
import {ref} from 'vue'

let idSeed = 0

export const useToastStore = defineStore('toast', () => {
  const toasts = ref([])

  const push = ({ type = 'info', message = '', duration = 3000 } = {}) => {
    const id = ++idSeed
    const item = { id, type, message, duration }
    toasts.value.push(item)
    if (duration > 0) {
      setTimeout(() => remove(id), duration)
    }
    return id
  }

  const remove = (id) => {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }

  const clear = () => {
    toasts.value = []
  }

  return { toasts, push, remove, clear }
})
