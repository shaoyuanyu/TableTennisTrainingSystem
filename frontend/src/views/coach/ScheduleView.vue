<template>
  <div class="coach-schedule-view">
    <h2><span class="schedule-icon">🏓</span> 我的教学课表</h2>

    <!-- 调试信息 (开发模式下显示) -->
    <div v-if="isDev" class="debug-info">
      <h3>调试信息</h3>
      <pre>{{ debugInfo }}</pre>
    </div>

    <ScheduleCalendar
      ref="scheduleCalendarRef"
      user-type="coach"
      :user-id="userId"
    />
  </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import {useUserStore} from '@/stores/user'
import ScheduleCalendar from '@/components/ScheduleCalendar.vue'

// 获取用户信息
const userStore = useUserStore()
const userId = userStore.userId || ''

// 引用课表组件
const scheduleCalendarRef = ref(null)

// 开发模式检测
const isDev = computed(() => import.meta.env.MODE === 'development')

// 调试信息
const debugInfo = computed(() => ({
  mode: import.meta.env.MODE,
  userId: userStore.userId,
  userRole: userStore.userRole,
  userName: userStore.userName,
  isCoach: userStore.isCoach,
  isLoggedIn: userStore.isLoggedIn,
  userInfo: userStore.userInfo,
  timestamp: new Date().toISOString()
}))
</script>

<style scoped>
.coach-schedule-view {
  padding: var(--spacing-2xl);
  min-height: 100vh;
  font-family: var(--font-family-base);
}

.coach-schedule-view h2 {
  margin: 0 0 var(--spacing-2xl) 0;
  color: var(--text-white);
  font-size: 2rem;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.schedule-icon {
  font-size: 1.8rem;
  margin-right: var(--spacing-sm);
  vertical-align: middle;
}

.debug-info {
  background: var(--white-alpha-20);
  backdrop-filter: blur(10px);
  border: 1px solid var(--white-alpha-30);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
  font-family: 'Courier New', monospace;
}

.debug-info h3 {
  color: var(--text-white);
  margin: 0 0 var(--spacing-md) 0;
  font-size: 1.2rem;
}

.debug-info pre {
  color: var(--text-white-80);
  font-size: 0.9rem;
  line-height: 1.4;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .coach-schedule-view {
    padding: var(--spacing-lg);
  }

  .debug-info {
    padding: var(--spacing-md);
  }

  .debug-info pre {
    font-size: 0.8rem;
  }
}
</style>
