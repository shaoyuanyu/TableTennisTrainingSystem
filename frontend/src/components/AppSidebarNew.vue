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

      <!-- 超级管理员菜单 - 优化版本 -->
      <template v-if="isSuperAdmin">
        <el-sub-menu index="admin" class="menu-item">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/campus">
            <el-icon><OfficeBuilding /></el-icon>
            <template #title>校区管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/service">
            <el-icon><CreditCard /></el-icon>
            <template #title>服务状态</template>
          </el-menu-item>
          <el-menu-item index="/admin/data">
            <el-icon><Document /></el-icon>
            <template #title>数据导出</template>
          </el-menu-item>
          <el-menu-item index="/admin/logs">
            <el-icon><Document /></el-icon>
            <template #title>系统日志</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- 校区管理员菜单 - 简化版本 -->
      <template v-if="isCampusAdmin">
        <el-sub-menu index="campus" class="menu-item">
          <template #title>
            <el-icon><Management /></el-icon>
            <span>校区管理</span>
          </template>
          <el-menu-item index="/campus/students">
            <el-icon><User /></el-icon>
            <template #title>学员管理</template>
          </el-menu-item>
          <el-menu-item index="/campus/coaches">
            <el-icon><Avatar /></el-icon>
            <template #title>教练管理</template>
          </el-menu-item>
          <el-menu-item index="/campus/appointments">
            <el-icon><Calendar /></el-icon>
            <template #title>预约管理</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- 学员菜单 - 扁平化 -->
      <template v-if="isStudent">
        <el-menu-item index="/student/coaches" class="menu-item">
          <el-icon><Search /></el-icon>
          <template #title>查找教练</template>
        </el-menu-item>
        <el-menu-item index="/student/my-coaches" class="menu-item">
          <el-icon><UserFilled /></el-icon>
          <template #title>我的教练</template>
        </el-menu-item>
        <el-menu-item index="/student/booking" class="menu-item">
          <el-icon><Calendar /></el-icon>
          <template #title>预约培训</template>
        </el-menu-item>
        <el-menu-item index="/student/schedule" class="menu-item">
          <el-icon><Clock /></el-icon>
          <template #title>我的课程</template>
        </el-menu-item>
      </template>

      <!-- 教练菜单 - 扁平化 -->
      <template v-if="isCoach">
        <el-menu-item index="/coach/dashboard" class="menu-item">
          <el-icon><Grid /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        <el-menu-item index="/coach/schedule" class="menu-item">
          <el-icon><Calendar /></el-icon>
          <template #title>课程安排</template>
        </el-menu-item>
        <el-menu-item index="/coach/approval" class="menu-item">
          <el-icon><Checked /></el-icon>
          <template #title>预约审批</template>
        </el-menu-item>
        <el-menu-item index="/coach/feedback" class="menu-item">
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
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { usePermissions } from '@/composables/usePermissions'
import {
  Odometer,
  Setting,
  OfficeBuilding,
  CreditCard,
  Document,
  Management,
  User,
  Avatar,
  Calendar,
  Search,
  UserFilled,
  Clock,
  Grid,
  Checked,
  EditPen,
} from '@element-plus/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()

// 直接使用权限hooks，避免额外的computed包装
const { 
  isSuperAdmin, 
  isCampusAdmin, 
  isStudent, 
  isCoach
} = usePermissions()

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
.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  transition: background-color 0.15s ease;
}

.sidebar-menu :deep(.el-menu--collapse .el-sub-menu__title) {
  text-align: center;
  padding: 0 20px;
}

.sidebar-menu :deep(.el-menu--collapse .el-menu-item) {
  text-align: center;
  padding: 0 20px;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(64, 158, 255, 0.2);
  color: #ffffff;
  border-right: 3px solid #409eff;
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.sidebar-menu :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #ffffff;
  background-color: rgba(64, 158, 255, 0.1);
}

/* 子菜单样式优化 */
.sidebar-menu :deep(.el-menu--popup) {
  background-color: rgba(48, 65, 86, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-menu :deep(.el-menu--popup .el-menu-item) {
  background-color: transparent;
}

.sidebar-menu :deep(.el-menu--popup .el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.2);
}
</style>
