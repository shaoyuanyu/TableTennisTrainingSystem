<template>
  <slot v-if="hasPermission" />
</template>

<script setup>
import { computed } from 'vue'
import { usePermissions } from '@/composables/usePermissions'

const props = defineProps({
  // 权限检查类型：'page' | 'action' | 'role'
  type: {
    type: String,
    default: 'page',
    validator: (value) => ['page', 'action', 'role'].includes(value)
  },
  
  // 权限值
  permission: {
    type: [String, Array],
    required: true
  },
  
  // 检查模式：'any' | 'all'（当 permission 为数组时）
  mode: {
    type: String,
    default: 'any',
    validator: (value) => ['any', 'all'].includes(value)
  },
  
  // 操作权限的上下文信息
  context: {
    type: Object,
    default: () => ({})
  }
})

const {
  canAccessPage,
  canPerformAction,
  userRole,
  hasAnyPermission,
  hasAllPermissions
} = usePermissions()

// 计算是否有权限
const hasPermission = computed(() => {
  const { type, permission, mode, context } = props
  
  if (Array.isArray(permission)) {
    const permissions = permission.map(p => {
      if (type === 'page') return p
      if (type === 'action') return { action: p, context }
      if (type === 'role') return p
      return p
    })
    
    if (type === 'role') {
      // 角色检查
      return mode === 'all' 
        ? permissions.every(role => userRole.value === role)
        : permissions.some(role => userRole.value === role)
    } else {
      // 权限检查
      return mode === 'all' 
        ? hasAllPermissions(permissions)
        : hasAnyPermission(permissions)
    }
  } else {
    switch (type) {
      case 'page':
        return canAccessPage(permission)
      case 'action':
        return canPerformAction(permission, context)
      case 'role':
        return userRole.value === permission
      default:
        return false
    }
  }
})
</script>
