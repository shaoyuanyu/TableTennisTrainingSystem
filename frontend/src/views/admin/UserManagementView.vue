<template>
  <div class="user-management">
    <!-- 页面头部 -->
    <GlassHeaderCard
      title="用户管理"
      subtitle="管理系统中的所有用户"
      icon="👥"
      class="page-header"
    />

    <!-- 用户列表 -->
    <GlassCardWithHeader
      title="所有用户"
      icon="👥"
      class="user-list-card"
    >
      <template #headerActions>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名/真实姓名"
          style="width: 200px; margin-right: 12px;"
          clearable
          @keyup.enter="fetchUsers"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <PrimaryButton @click="fetchUsers" size="sm">
          搜索
        </PrimaryButton>
      </template>

      <div style="padding: 8px 0;">
        <GlassTable
          :data="userList"
          :loading="loading.users"
          density="sm"
          :stripe="true"
          empty-title="暂无用户"
          empty-description="系统中暂无用户数据"
        >
          <el-table-column prop="username" label="用户名" width="150" />
          <el-table-column prop="realName" label="真实姓名" width="120" />
          <el-table-column prop="campusName" label="校区" width="150" />
          <el-table-column prop="role" label="角色" width="120">
            <template #default="{ row }">
              <el-tag :type="getRoleTagType(row.role)">
                {{ formatRole(row.role) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phoneNumber" label="电话" width="150" />
          <el-table-column prop="email" label="邮箱" width="200" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                size="small" 
                @click="viewUserDetails(row)"
                link
              >
                查看详情
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDeleteUser(row.uuid)"
                link
                :disabled="row.role === 'SUPER_ADMIN'"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </GlassTable>

        <!-- 分页控件 -->
        <div class="pagination-wrapper" v-if="pagination.total > 0">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </GlassCardWithHeader>

    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="用户详情"
      width="500px"
      :before-close="handleDetailDialogClose"
    >
      <el-form
        v-if="selectedUser"
        :model="selectedUser"
        label-width="100px"
        label-position="left"
      >
        <el-form-item label="用户名:">
          <span>{{ selectedUser.username }}</span>
        </el-form-item>
        <el-form-item label="真实姓名:">
          <span>{{ selectedUser.realName }}</span>
        </el-form-item>
        <el-form-item label="性别:">
          <span>{{ selectedUser.gender || '未填写' }}</span>
        </el-form-item>
        <el-form-item label="年龄:">
          <span>{{ selectedUser.age || '未填写' }}</span>
        </el-form-item>
        <el-form-item label="校区:">
          <span>{{ selectedUser.campusName }}</span>
        </el-form-item>
        <el-form-item label="角色:">
          <el-tag :type="getRoleTagType(selectedUser.role)">
            {{ formatRole(selectedUser.role) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="电话:">
          <span>{{ selectedUser.phoneNumber || '未填写' }}</span>
        </el-form-item>
        <el-form-item label="邮箱:">
          <span>{{ selectedUser.email || '未填写' }}</span>
        </el-form-item>
        <el-form-item label="状态:">
          <span>{{ selectedUser.status || '未设置' }}</span>
        </el-form-item>
        <el-form-item label="创建时间:">
          <span>{{ formatTime(selectedUser.createdAt) }}</span>
        </el-form-item>
        <el-form-item v-if="selectedUser.studentInfo" label="学员信息:">
          <div style="width: 100%;">
            <p>余额: ¥{{ selectedUser.studentInfo.balance }}</p>
            <p>当前教练数: {{ selectedUser.studentInfo.currentCoach }}</p>
            <p>最多可选教练数: {{ selectedUser.studentInfo.maxCoach }}</p>
          </div>
        </el-form-item>
        <el-form-item v-if="selectedUser.coachInfo" label="教练信息:">
          <div style="width: 100%;">
            <p>是否已审核: {{ selectedUser.coachInfo.isApproved ? '是' : '否' }}</p>
            <p v-if="selectedUser.coachInfo.isApproved">审核人: {{ selectedUser.coachInfo.approvedBy }}</p>
            <p>等级: {{ selectedUser.coachInfo.level }}</p>
            <p>时薪: ¥{{ selectedUser.coachInfo.hourlyRate }}</p>
            <p>当前学员数: {{ selectedUser.coachInfo.currentStudents }}</p>
            <p>最多可带学员数: {{ selectedUser.coachInfo.maxStudents }}</p>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAllUsers, deleteUser } from '@/api/admin'
import {
  GlassHeaderCard,
  GlassCardWithHeader
} from '@/components/cards'
import GlassTable from '@/components/data/Table.vue'
import { PrimaryButton } from '@/components/buttons'
import dayjs from 'dayjs'

// 数据状态
const loading = ref({
  users: false
})

// 用户列表
const userList = ref([])

// 分页信息
const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

// 搜索关键词
const searchKeyword = ref('')

// 详情对话框
const detailDialogVisible = ref(false)

// 选中的用户
const selectedUser = ref(null)

// 格式化角色显示
const formatRole = (role) => {
  const roleMap = {
    'SUPER_ADMIN': '超级管理员',
    'CAMPUS_ADMIN': '校区管理员',
    'COACH': '教练',
    'STUDENT': '学员'
  }
  return roleMap[role] || role
}

// 获取角色标签类型
const getRoleTagType = (role) => {
  const typeMap = {
    'SUPER_ADMIN': 'danger',
    'CAMPUS_ADMIN': 'warning',
    'COACH': 'success',
    'STUDENT': 'primary'
  }
  return typeMap[role] || 'info'
}

// 格式化时间
const formatTime = (timestamp) => {
  return timestamp ? dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss') : '无'
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value.users = true
  try {
    const params = {
      page: pagination.value.page,
      size: pagination.value.size
    }

    const response = await getAllUsers(params)
    console.log('用户列表响应:', response) // 调试日志
    
    // 后端 /user/allUsers 接口返回的是 List<User>，不包含分页信息
    if (Array.isArray(response)) {
      userList.value = response
      // 由于后端不返回总数，我们只能假设至少有当前页的数据
      // 如果返回的数据少于请求的大小，说明已经是最后一页
      if (response.length < pagination.value.size) {
        pagination.value.total = (pagination.value.page - 1) * pagination.value.size + response.length
      } else {
        // 如果返回的数据等于请求的大小，我们无法确定是否还有更多数据
        // 这里我们暂时设置为一个较大的值，让用户可以继续翻页
        pagination.value.total = pagination.value.page * pagination.value.size
      }
    } else {
      userList.value = []
      pagination.value.total = 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败: ' + (error.message || '未知错误'))
    // 出错时清空列表
    userList.value = []
    pagination.value.total = 0
  } finally {
    loading.value.users = false
  }
}

// 处理分页变化
const handlePageChange = (page) => {
  pagination.value.page = page
  fetchUsers()
}

// 处理页面大小变化
const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.page = 1
  fetchUsers()
}

// 查看用户详情
const viewUserDetails = (user) => {
  selectedUser.value = user
  detailDialogVisible.value = true
}

// 关闭详情对话框
const handleDetailDialogClose = (done) => {
  selectedUser.value = null
  done()
}

// 删除用户处理函数
const handleDeleteUser = async (userId) => {
  ElMessageBox.confirm(
    '确认删除该用户吗？此操作不可恢复！',
    '确认删除',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await deleteUser(userId)
      ElMessage.success('用户删除成功')
      fetchUsers() // 重新加载用户列表
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 初始化数据
const initializeData = async () => {
  await fetchUsers()
}

// 组件挂载时获取数据
onMounted(() => {
  initializeData()
})
</script>

<style scoped>
.user-management {
  padding: 24px;
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.user-list-card {
  margin-bottom: 24px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>