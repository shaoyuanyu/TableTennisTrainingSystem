<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :router="true"
      class="sidebar-menu"
      background-color="transparent"
      text-color="rgba(255, 255, 255, 0.95)"
      active-text-color="#ffffff"
    >
      <!-- 仪表盘 -->
      <el-menu-item index="/dashboard" class="menu-item">
        <el-icon><Odometer /></el-icon>
        <template #title>仪表盘</template>
      </el-menu-item>

      <!-- 超级管理员菜单 - 扁平化 -->
      <template v-if="isSuperAdmin">
        <el-menu-item index="/admin/campus" class="menu-item">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>校区管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/students" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>学员管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/coaches" class="menu-item">
          <el-icon><Avatar /></el-icon>
          <template #title>教练管理</template>
        </el-menu-item>
        <el-menu-item index="/admin/service" class="menu-item">
          <el-icon><CreditCard /></el-icon>
          <template #title>服务状态</template>
        </el-menu-item>
        <el-menu-item index="/admin/data" class="menu-item">
          <el-icon><Document /></el-icon>
          <template #title>数据导出</template>
        </el-menu-item>
        <el-menu-item index="/admin/logs" class="menu-item">
          <el-icon><Document /></el-icon>
          <template #title>系统日志</template>
        </el-menu-item>
      </template>

      <!-- 校区管理员菜单 - 扁平化 -->
      <template v-if="isCampusAdmin">
        <el-menu-item index="/campus/students" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>学员管理</template>
        </el-menu-item>
        <el-menu-item index="/campus/coaches" class="menu-item">
          <el-icon><Avatar /></el-icon>
          <template #title>教练管理</template>
        </el-menu-item>
        <el-menu-item index="/campus/appointments" class="menu-item">
          <el-icon><Calendar /></el-icon>
          <template #title>预约管理</template>
        </el-menu-item>
      </template>

      <!-- 学员菜单 - 扁平化 -->
      <template v-if="isStudent">
        <el-menu-item index="/student/find-coach" class="menu-item">
          <el-icon><Search /></el-icon>
          <template #title>查找教练</template>
        </el-menu-item>
        <el-menu-item index="/student/mutual-selection" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>教练双选</template>
        </el-menu-item>
        <el-menu-item index="/student/book-training" class="menu-item">
          <el-icon><Calendar /></el-icon>
          <template #title>预约培训</template>
        </el-menu-item>
        <el-menu-item index="/student/schedule" class="menu-item">
          <el-icon><Clock /></el-icon>
          <template #title>我的课程</template>
        </el-menu-item>
        <el-menu-item index="/student/account-recharge" class="menu-item">
          <el-icon><Wallet /></el-icon>
          <template #title>我的钱包</template>
        </el-menu-item>
      </template>

      <!-- 教练菜单 - 扁平化 -->
      <template v-if="isCoach">
        <el-menu-item index="/coach/dashboard" class="menu-item">
          <el-icon><Grid /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        <el-menu-item index="/coach/mutual-selection" class="menu-item">
          <el-icon><User /></el-icon>
          <template #title>学生双选</template>
        </el-menu-item>
        <el-menu-item index="/coach/schedule" class="menu-item">
          <el-icon><Calendar /></el-icon>
          <template #title>课程安排</template>
        </el-menu-item>
        <el-menu-item index="/coach/appointment-approval" class="menu-item">
          <el-icon><Checked /></el-icon>
          <template #title>预约审批</template>
        </el-menu-item>
        <el-menu-item index="/coach/student-feedback" class="menu-item">
          <el-icon><EditPen /></el-icon>
          <template #title>学员反馈</template>
        </el-menu-item>
      </template>

      <!-- 开发工具菜单 -->
      <el-sub-menu index="dev-tools" class="menu-item">
        <template #title>
          <el-icon><Grid /></el-icon>
          <span>开发工具</span>
        </template>
        <el-menu-item index="/dev-tools?tool=debug">
          <el-icon><EditPen /></el-icon>
          <template #title>权限调试</template>
        </el-menu-item>
        <el-menu-item index="/dev-tools?tool=roles">
          <el-icon><User /></el-icon>
          <template #title>角色测试</template>
        </el-menu-item>
        <el-menu-item index="/dev-tools?tool=pages">
          <el-icon><Document /></el-icon>
          <template #title>页面测试</template>
        </el-menu-item>
        <el-menu-item index="/design-system-test">
          <el-icon><Picture /></el-icon>
          <template #title>设计语言样板间</template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute} from 'vue-router'
import {usePermissions} from '@/composables/usePermissions'
import {
  Avatar,
  Calendar,
  Checked,
  Clock,
  CreditCard,
  Document,
  EditPen,
  Grid,
  Management,
  Odometer,
  OfficeBuilding,
  Picture,
  Search,
  Setting,
  User,
  UserFilled,
  Wallet,
} from '@element-plus/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()

// 直接使用权限hooks，避免额外的computed包装
const { isSuperAdmin, isCampusAdmin, isStudent, isCoach } = usePermissions()

// 当前激活的菜单项
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  background: transparent;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
  background: transparent;
  /* 性能优化：启用硬件加速 */
  transform: translateZ(0);
  backface-visibility: hidden;
}

.menu-item {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  /* 性能优化：减少重绘 */
  will-change: background-color;
}

/* 优化过渡效果 */

/* 子菜单样式优化 */

</style>