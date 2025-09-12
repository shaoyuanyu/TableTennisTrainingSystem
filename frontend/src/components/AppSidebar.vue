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
      @select="handleMenuSelect"
    >
      <!-- 仪表盘 -->
      <el-menu-item index="/dashboard" class="menu-item">
        <el-icon>
          <Odometer />
        </el-icon>
        <template #title>仪表盘</template>
      </el-menu-item>

      <!-- 超级管理员菜单 -->
      <template v-if="permissions.isSuperAdmin">
        <el-sub-menu index="admin" class="menu-item">
          <template #title>
            <el-icon>
              <Setting />
            </el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/campus">
            <el-icon>
              <OfficeBuilding />
            </el-icon>
            <template #title>校区管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/service">
            <el-icon>
              <CreditCard />
            </el-icon>
            <template #title>服务状态</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- 校区管理员菜单 -->
      <template v-if="permissions.isCampusAdmin">
        <el-sub-menu index="campus" class="menu-item">
          <template #title>
            <el-icon>
              <Management />
            </el-icon>
            <span>校区管理</span>
          </template>
          <el-menu-item index="/campus/students">
            <el-icon>
              <User />
            </el-icon>
            <template #title>学员管理</template>
          </el-menu-item>
          <el-menu-item index="/campus/coaches">
            <el-icon>
              <Avatar />
            </el-icon>
            <template #title>教练管理</template>
          </el-menu-item>
          <el-menu-item index="/campus/appointments">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>预约管理</template>
          </el-menu-item>
          <el-menu-item index="/campus/logs">
            <el-icon>
              <Document />
            </el-icon>
            <template #title>日志查询</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- 学员菜单 -->
      <template v-if="permissions.isStudent">
        <el-sub-menu index="student" class="menu-item">
          <template #title>
            <el-icon>
              <User />
            </el-icon>
            <span>学员中心</span>
          </template>
          <el-menu-item index="/student/coaches">
            <el-icon>
              <Search />
            </el-icon>
            <template #title>教练查询</template>
          </el-menu-item>
          <el-menu-item index="/student/my-coaches">
            <el-icon>
              <UserFilled />
            </el-icon>
            <template #title>我的教练</template>
          </el-menu-item>
          <el-menu-item index="/student/appointment">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>课程预约</template>
          </el-menu-item>
          <el-menu-item index="/student/schedule">
            <el-icon>
              <Clock />
            </el-icon>
            <template #title>我的课表</template>
          </el-menu-item>
          <el-menu-item index="/student/recharge">
            <el-icon>
              <Wallet />
            </el-icon>
            <template #title>账户充值</template>
          </el-menu-item>
          <el-menu-item index="/student/tournament">
            <el-icon>
              <Trophy />
            </el-icon>
            <template #title>比赛报名</template>
          </el-menu-item>
          <el-menu-item index="/student/matches">
            <el-icon>
              <Medal />
            </el-icon>
            <template #title>我的比赛</template>
          </el-menu-item>
          <el-menu-item index="/student/evaluation">
            <el-icon>
              <EditPen />
            </el-icon>
            <template #title>训练评价</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- 教练菜单 -->
      <template v-if="permissions.isCoach">
        <el-sub-menu index="coach" class="menu-item">
          <template #title>
            <el-icon>
              <Avatar />
            </el-icon>
            <span>教练中心</span>
          </template>
          <el-menu-item index="/coach/appointments">
            <el-icon>
              <Checked />
            </el-icon>
            <template #title>预约审核</template>
          </el-menu-item>
          <el-menu-item index="/coach/schedule">
            <el-icon>
              <Clock />
            </el-icon>
            <template #title>我的课表</template>
          </el-menu-item>
          <el-menu-item index="/coach/evaluation">
            <el-icon>
              <EditPen />
            </el-icon>
            <template #title>学员评价</template>
          </el-menu-item>
        </el-sub-menu>
      </template>

      <!-- 通用菜单 -->
      <!-- 消息中心和个人中心已移至头部栏，避免重复 -->

      <!-- 开发工具 -->
      <el-sub-menu index="dev-tools" class="menu-item">
        <template #title>
          <el-icon>
            <Grid />
          </el-icon>
          <span>开发工具</span>
        </template>
        <el-menu-item index="/dev-tools?tool=debug">
          <el-icon>
            <EditPen />
          </el-icon>
          <template #title>权限调试</template>
        </el-menu-item>
        <el-menu-item index="/dev-tools?tool=roles">
          <el-icon>
            <User />
          </el-icon>
          <template #title>角色测试</template>
        </el-menu-item>
        <el-menu-item index="/dev-tools?tool=pages">
          <el-icon>
            <Document />
          </el-icon>
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
import { debugSidebar } from '@/utils/debug'
import {
  Odometer,
  Setting,
  OfficeBuilding,
  CreditCard,
  Management,
  User,
  Avatar,
  Calendar,
  Document,
  Search,
  UserFilled,
  Clock,
  Wallet,
  Trophy,
  Medal,
  EditPen,
  Checked,
  Grid,
} from '@element-plus/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()
const { 
  isSuperAdmin, 
  isCampusAdmin, 
  isStudent, 
  isCoach
} = usePermissions()

// 性能优化：缓存权限计算结果
const permissions = computed(() => ({
  isSuperAdmin: isSuperAdmin.value,
  isCampusAdmin: isCampusAdmin.value,
  isStudent: isStudent.value,
  isCoach: isCoach.value
}))

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 菜单选择处理
const handleMenuSelect = (index, indexPath) => {
  // 添加到全局调试器
  if (window.debuggerAddLog) {
    window.debuggerAddLog(`🔄 菜单选择: ${index} (路径: ${indexPath.join(' > ')})`, 'info')
  }
  
  debugSidebar.logMenuClick(index, '菜单选择')
  debugSidebar.startPerfMeasure('menu-select')
  
  console.log('Menu selected:', { index, indexPath })
  
  // 特殊处理系统管理菜单
  if (index === 'admin' || indexPath.includes('admin')) {
    debugSidebar.logComponentLoad('SystemManagement')
    console.warn('系统管理菜单被点击，监控性能...')
    
    if (window.debuggerAddLog) {
      window.debuggerAddLog(`⚠️ 系统管理菜单点击，开始性能监控`, 'warning')
    }
  }
  
  debugSidebar.endPerfMeasure('menu-select')
}
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
  /* 性能优化：减少重排和重绘 */
  will-change: auto;
  transform: translate3d(0, 0, 0);
}

.menu-item {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.95);
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

/* 性能优化：简化深度选择器 */
.sidebar-menu :deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 20px;
}

.sidebar-menu :deep(.el-menu--collapse .el-menu-item) {
  padding: 0 20px;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 40px !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.25);
  border-right: 3px solid #ffffff;
  color: #ffffff !important;
  font-weight: 600;
  box-shadow: inset 2px 0 0 rgba(255, 255, 255, 0.2);
}

.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff !important;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff !important;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.95) !important;
  font-weight: 500 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
  /* 性能优化：简化过渡效果 */
  transition: background-color 0.15s ease !important;
  transform: translate3d(0, 0, 0);
  will-change: background-color;
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.95) !important;
  font-weight: 500 !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5) !important;
  /* 性能优化：简化过渡效果 */
  transition: background-color 0.15s ease !important;
  transform: translate3d(0, 0, 0);
  will-change: background-color;
}

.sidebar-menu :deep(.el-icon) {
  color: rgba(255, 255, 255, 0.95) !important;
  filter: drop-shadow(0 1px 1px rgba(0, 0, 0, 0.5)) !important;
}

/* 性能优化：简化箭头动画 */
.sidebar-menu :deep(.el-sub-menu__title .el-sub-menu__icon-arrow) {
  transition: transform 0.15s ease !important;
}

.sidebar-menu :deep(.el-menu--vertical .el-sub-menu > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transition: transform 0.15s ease !important;
}
</style>
