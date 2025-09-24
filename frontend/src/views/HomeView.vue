<template>
  <div class="home-view">
    <!-- 学生首页 -->
    <StudentDashboard v-if="userStore.isStudent" />

    <!-- 教练首页 -->
    <CoachDashboard v-else-if="userStore.isCoach" />

    <!-- 校区管理员首页 -->
    <CampusAdminDashboard v-else-if="userStore.isCampusAdmin" />

    <!-- 超级管理员首页 -->
    <SuperAdminDashboard v-else-if="userStore.isSuperAdmin" />

    <!-- 默认首页（未登录或角色未识别） -->
    <DefaultDashboard v-else />
  </div>
</template>

<script setup>
import {useUserStore} from '@/stores/user'
import StudentDashboard from '@/components/dashboard/StudentDashboard.vue'
import CoachDashboard from '@/components/dashboard/CoachDashboard.vue'
import CampusAdminDashboard from '@/components/dashboard/CampusAdminDashboard.vue'
import SuperAdminDashboard from '@/components/dashboard/SuperAdminDashboard.vue'
import DefaultDashboard from '@/components/dashboard/DefaultDashboard.vue'

const userStore = useUserStore()
</script>

<style scoped>
.home-view {
  min-height: 100vh;
  background: transparent;
  padding: 24px;
}
</style>
