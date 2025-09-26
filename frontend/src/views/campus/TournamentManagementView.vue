<template>
  <div class="tournament-management container">
    <!-- 现代化页头：使用设计系统 .page-header 与头部布局 -->
    <div class="page-header hover-lift">
      <div class="header-left">
        <div class="header-icon-wrapper"><span class="header-icon">🏓</span></div>
        <div class="header-text">
          <h3>比赛管理</h3>
          <p>创建与管理校区比赛，查看列表与报名截止情况</p>
        </div>
      </div>
    </div>

    <!-- 创建比赛：使用设计系统 GlassHeaderCard 包裹，统一头部与间距 -->
  <GlassHeaderCard ref="createSectionRef" title="创建新比赛" icon="🏆" size="medium">
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="120px"
        class="create-form stack"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="比赛名称" prop="name">
              <el-input v-model="createForm.name" placeholder="请输入比赛名称" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="比赛类型" prop="type">
              <el-input v-model="createForm.type" placeholder="请输入比赛类型" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="比赛日期" prop="date">
              <el-date-picker
                v-model="createForm.date"
                type="date"
                placeholder="请选择比赛日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="报名截止日期" prop="registrationDeadline">
              <el-date-picker
                v-model="createForm.registrationDeadline"
                type="date"
                placeholder="请选择报名截止日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报名费用" prop="fee">
              <el-input-number
                v-model="createForm.fee"
                :min="0"
                :step="1"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="比赛描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入比赛描述"
          />
        </el-form-item>

        <div class="form-actions cluster">
          <el-button
            type="primary"
            @click="createTournament"
            :loading="creating"
          >
            创建比赛
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </div>
      </el-form>
    </GlassHeaderCard>

    <!-- 比赛列表：使用 GlassHeaderCard 并在头部右侧放操作按钮 -->
    <GlassHeaderCard title="比赛列表" icon="📋" size="medium">
      <template #headerActions>
        <el-button type="primary" @click="fetchTournaments" :icon="Refresh">刷新</el-button>
      </template>

      <el-table
        :data="tournaments"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="name" label="比赛名称" min-width="160" />
        <el-table-column prop="type" label="类型" min-width="120">
          <template #default="scope">
            {{ scope.row.type }}
          </template>
        </el-table-column>
        <el-table-column prop="date" label="比赛日期" min-width="140" />
        <el-table-column prop="registrationDeadline" label="报名截止" min-width="140" />
        <el-table-column prop="fee" label="报名费" width="120">
          <template #default="scope">
            {{ scope.row.fee }} 元
          </template>
        </el-table-column>
        <el-table-column label="报名人数" width="120">
          <template #default="scope">
            {{ scope.row.currentSignupStudentNumber ?? 0 }} 人
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getTournamentStatusType(scope.row)">
              {{ getTournamentStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
            <el-button
              v-if="scope.row.status === '未开始'"
              size="small"
              type="success"
              @click="startTournament(scope.row)"
            >
              开始比赛
            </el-button>
            <el-button
              v-else
              size="small"
              type="danger"
              @click="deleteTournament(scope.row)"
              :disabled="scope.row.status === '已结束'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </GlassHeaderCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { GlassHeaderCard } from '@/components/cards'
import api from '@/utils/api'
import { useUserStore } from '@/stores/user'
// 页头动作：平滑滚动到创建表单
const createSectionRef = ref(null)
// 表单引用
const createFormRef = ref()

// 获取用户状态
const userStore = useUserStore()

// 表单数据
const createForm = ref({
  name: '',
  type: '',
  date: '',
  registrationDeadline: '',
  fee: 0,
  campusId: userStore.campusId, // 从用户状态中获取校区ID
  description: ''
})

// 表单验证规则
const createRules = {
  name: [
    { required: true, message: '请输入比赛名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请输入比赛类型', trigger: 'blur' }
  ],
  date: [
    { required: true, message: '请选择比赛日期', trigger: 'change' }
  ],
  registrationDeadline: [
    { required: true, message: '请选择报名截止日期', trigger: 'change' }
  ],
  fee: [
    { required: true, message: '请输入报名费用', trigger: 'blur' }
  ]
}

// 状态管理
const creating = ref(false)
const loading = ref(false)
const tournaments = ref([])
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 方法
const createTournament = async () => {
  try {
    creating.value = true
    // 验证表单
    const valid = await createFormRef.value.validate()
    if (!valid) return

    // 确保campusId始终从用户状态中获取
    createForm.value.campusId = userStore.campusId

    await api.post('/competition/create', createForm.value)
    ElMessage.success('比赛创建成功')
    resetForm()
    fetchTournaments()
  } catch (error) {
    ElMessage.error('创建失败：' + (error.response?.data?.message || error.message))
  } finally {
    creating.value = false
  }
}

const resetForm = () => {
  createForm.value = {
    name: '',
    type: '',
    date: '',
    registrationDeadline: '',
    fee: 30,
    campusId: userStore.campusId,
    description: ''
  }
}

const fetchTournaments = async () => {
  try {
    loading.value = true
    const response = await api.get('/competition/self-campus', {
      params: {
        page: pagination.value.currentPage,
        size: pagination.value.pageSize
      }
    })
    tournaments.value = response.data
    console.log(tournaments.value)
    pagination.value.total = response.data.total
  } catch (error) {
    ElMessage.error('获取比赛列表失败：' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

const viewDetails = (tournament) => {
  ElMessageBox.alert(
    `<strong>比赛名称：</strong>${tournament.name}<br>
     <strong>比赛类型：</strong>${getTournamentTypeText(tournament.type)}<br>
     <strong>比赛日期：</strong>${tournament.date}<br>
     <strong>报名截止：</strong>${tournament.registrationDeadline}<br>
     <strong>报名费用：</strong>${tournament.fee}元<br>
     <strong>比赛描述：</strong>${tournament.description || '无'}<br>
     <strong>比赛状态：</strong>${tournament.status}`,
    '比赛详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const deleteTournament = (tournament) => {
  ElMessageBox.confirm(
    `确定要删除比赛 "${tournament.name}" 吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await api.delete(`/competition/tournaments/${tournament.id}`)
      ElMessage.success('删除成功')
      fetchTournaments()
    } catch (error) {
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message))
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 获取比赛状态 (pending, ongoing, completed)
const getTournamentStatus = (tournament) => {
  switch (tournament.status) {
    case '未开始':
      return 'pending'
    case '进行中':
      return 'ongoing'
    case '已结束':
      return 'completed'
    default:
      return 'unknown'
  }
}

// 获取比赛状态文本
const getTournamentStatusText = (tournament) => {
  return tournament.status || '未知'
}

// 获取比赛状态标签类型
const getTournamentStatusType = (tournament) => {
  switch (tournament.status) {
    case '未开始':
      return 'warning' // 黄色
    case '进行中':
      return 'success' // 绿色
    case '已结束':
      return 'info' // 灰色
    default:
      return 'info'
  }
}

// 开始比赛
const startTournament = async (tournament) => {
  ElMessageBox.confirm(
    `确定要提前开始比赛 "${tournament.name}" 吗？`,
    '开始比赛确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await api.post(`/competition/arrange/${tournament.id}`)
      ElMessage.success('比赛开始成功')
      fetchTournaments()
    } catch (error) {
      ElMessage.error('开始比赛失败：' + (error.response?.data?.message || error.message))
    }
  }).catch(() => {
    // 用户取消操作
  })
}

const getTournamentTypeText = (type) => {
  return type
}

const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  fetchTournaments()
}

const handleCurrentChange = (val) => {
  pagination.value.currentPage = val
  fetchTournaments()
}

// 组件挂载时获取比赛列表
onMounted(() => {
  fetchTournaments()
})
</script>

<style scoped>
/* 页面容器与间距采用设计系统工具类，补充少量定制 */
.tournament-management {
  padding-top: var(--spacing-2xl);
  padding-bottom: var(--spacing-3xl);
  color: var(--white-alpha-90);
}

/* 页头右侧动作区间距与换行适配 */
.page-header .header-actions {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.glass-header-card + .glass-header-card {
  /* 邻接卡片之间拉开间距，提高层次性 */
  margin-top: var(--spacing-2xl);
}

.create-form :deep(.el-form-item) {
  margin-bottom: var(--spacing-lg);
}

.form-actions {
  justify-content: flex-start;
}

.pagination-container {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

/* 表格细节微调，增强可读性 */
:deep(.el-table__header th) {
  background: #f7f7f8; /* 更浅的表头底色提升分隔 */
  color: var(--gray-700); /* 深灰增加对比度 */
}
:deep(.el-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
  color: var(--gray-800); /* 表格主体文本使用深灰 */
}
:deep(.el-table .cell),
:deep(.el-table td),
:deep(.el-table th) {
  color: var(--gray-800);
}

/* 表单可读性与可触性增强：输入框/数字框/日期/文本域 统一玻璃风格 */
:deep(.el-form-item__label) {
  color: var(--white-alpha-90);
}

:deep(.el-input__wrapper) {
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-md);
  box-shadow: none;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 3px var(--focus-color);
  background: var(--white-alpha-15);
  border-color: var(--white-alpha-30);
}

:deep(.el-input__inner),
:deep(.el-textarea__inner) {
  color: var(--white-alpha-90);
}

:deep(.el-input__inner::placeholder),
:deep(.el-textarea__inner::placeholder) {
  color: var(--white-alpha-80);
}

/* 文本域 */
:deep(.el-textarea__inner) {
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-md);
}

/* 数字输入 */
:deep(.el-input-number .el-input__wrapper) {
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-md);
}

/* 日期选择器（使用相同输入外观） */
:deep(.el-date-editor.el-input .el-input__wrapper),
:deep(.el-date-editor--date .el-input__wrapper) {
  background: var(--white-alpha-10);
  border: 1px solid var(--white-alpha-20);
  border-radius: var(--radius-md);
}

</style>
