<template>
  <div class="coach-schedule-view">
    <h2><span class="schedule-icon">🏓</span> 我的教学课表</h2>

    <ScheduleCalendar
      ref="scheduleCalendarRef"
      user-type="coach"
      :user-id="userId"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {useUserStore} from '@/stores/user'
import ScheduleCalendar from '@/components/ScheduleCalendar.vue'

// 获取用户信息
const userStore = useUserStore()
const userId = userStore.userId || ''

// 引用课表组件
const scheduleCalendarRef = ref(null)
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

/* 响应式优化 */
@media (max-width: 768px) {
  .coach-schedule-view {
    padding: var(--spacing-lg);
  }
}
</style>
