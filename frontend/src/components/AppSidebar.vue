<template>
  <div class="sidebar-container">
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :router="true"
      class="sidebar-menu"
      background-color="#fff"
      text-color="#333"
      active-text-color="#1890ff"
    >
      <!-- 仪表盘 -->
      <el-menu-item index="/dashboard" class="menu-item">
        <el-icon>
          <Odometer />
        </el-icon>
        <template #title>仪表盘</template>
      </el-menu-item>

      <!-- 超级管理员菜单 -->
      <template v-if="isSuperAdmin">
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
      <template v-if="isCampusAdmin">
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
      <template v-if="isStudent">
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
      <template v-if="isCoach">
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
            <Setting />
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

// 当前激活的菜单项
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar-container {
  height: 100%;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.menu-item {
  font-size: 14px;
}

:deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 20px;
}

:deep(.el-menu--collapse .el-menu-item) {
  padding: 0 20px;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 40px !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #e6f7ff;
  border-right: 2px solid #1890ff;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #f0f0f0;
}

:deep(.el-menu-item:hover) {
  background-color: #f0f0f0;
}
</style>
