<template>
  <div class="student-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ campusName }}学员管理</h2>
      <div class="header-actions">
        <OutlineButton @click="showBatchDialog">
          <template #icon-left>
            <el-icon>
              <Operation />
            </el-icon>
          </template>
          批量操作
        </OutlineButton>
        <PrimaryButton @click="showAddDialog">
          <template #icon-left>
            <el-icon>
              <Plus />
            </el-icon>
          </template>
          新增学员
        </PrimaryButton>
      </div>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchStudents">
        <el-form-item label="学员状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="正常" value="active" />
            <el-option label="暂停" value="paused" />
            <el-option label="已毕业" value="graduated" />
            <el-option label="已退费" value="refunded" />
          </el-select>
        </el-form-item>



        <el-form-item label="注册时间">
          <el-date-picker v-model="filters.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
                          end-placeholder="结束日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="搜索">
          <el-input v-model="filters.keyword" placeholder="姓名/手机号/用户名" style="width: 200px"
                    @keyup.enter="fetchStudents" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchStudents" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学员统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon active">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.active }}</div>
              <div class="stat-label">在读学员</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon new">
              <el-icon>
                <UserFilled />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.newThisMonth }}</div>
              <div class="stat-label">本月新增</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon balance">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">¥{{ stats.totalBalance }}</div>
              <div class="stat-label">账户余额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学员列表 -->
    <el-card>
      <el-table :data="studentList" v-loading="loading" stripe @selection-change="handleSelectionChange" style="width: 100%">
        <el-table-column type="selection" width="55" />

        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">
              {{ row.name.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="用户名" />

        <el-table-column prop="name" label="姓名" />

        <el-table-column prop="phone" label="手机号" />

        <el-table-column prop="age" label="年龄" width="80">
          <template #default="{ row }"> {{ row.age }}岁 </template>
        </el-table-column>

        <el-table-column prop="coaches" label="教练" width="200">
          <template #default="{ row }">
            <div v-if="row.coaches && row.coaches.length > 0">
              <el-tag v-for="coach in row.coaches" :key="coach.id" :type="getCoachTagType(coach.status)" style="margin: 2px;">
                {{ coach.name }}
              </el-tag>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="registeredAt" label="注册时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.registeredAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showDetailDialog(row)"> 详情 </el-button>
            <el-button size="small" @click="showEditDialog(row)"> 编辑 </el-button>
            <el-dropdown @command="(cmd) => handleAction(cmd, row)">
              <el-button size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="recharge">充值</el-dropdown-item>
                  <el-dropdown-item command="pause">暂停</el-dropdown-item>
                  <el-dropdown-item command="graduate">毕业</el-dropdown-item>
                  <el-dropdown-item divided command="delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
                       :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
                       @size-change="fetchStudents" @current-change="fetchStudents" />
      </div>
    </el-card>

    <!-- 新增/编辑学员对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="studentForm" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="studentForm.name" placeholder="请输入学员姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="studentForm.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="studentForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="studentForm.birthDate" type="date" placeholder="选择出生日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="studentForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>

        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="studentForm.emergencyContact" placeholder="紧急联系人姓名" />
        </el-form-item>

        <el-form-item label="紧急联系电话" prop="emergencyPhone">
          <el-input v-model="studentForm.emergencyPhone" placeholder="紧急联系电话" />
        </el-form-item>



        <el-form-item label="备注">
          <el-input v-model="studentForm.notes" type="textarea" :rows="3" placeholder="备注信息" />
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="dialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveStudent" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </PrimaryButton>
      </template>
    </el-dialog>

    <!-- 学员详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="学员详情" width="800px">
      <div v-if="selectedStudent">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">
            {{ selectedStudent.username }}
          </el-descriptions-item>
          <el-descriptions-item label="姓名">
            {{ selectedStudent.name }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ selectedStudent.gender === 'male' ? '男' : '女' }}
          </el-descriptions-item>
          <el-descriptions-item label="年龄">
            {{ calculateAge(selectedStudent.birthDate) }}岁
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ selectedStudent.phone }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ selectedStudent.email }}
          </el-descriptions-item>

          <el-descriptions-item label="账户余额">
            ¥{{ selectedStudent.balance }}
          </el-descriptions-item>
          <el-descriptions-item label="教练">
            <div v-if="selectedStudent.coaches && selectedStudent.coaches.length > 0">
              <el-tag v-for="coach in selectedStudent.coaches" :key="coach.id" :type="getCoachTagType(coach.status)" style="margin: 2px;">
                {{ coach.name }}
              </el-tag>
            </div>
            <span v-else>未分配</span>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDateTime(selectedStudent.registeredAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedStudent.status)">
              {{ getStatusText(selectedStudent.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 学习记录 -->
        <div style="margin-top: 20px">
          <h4>最近学习记录</h4>
          <el-table :data="selectedStudent.recentCourses" size="small">
            <el-table-column prop="date" label="上课时间" width="150" />
            <el-table-column prop="coach" label="教练" width="100" />
            <el-table-column prop="duration" label="时长" width="80" />
            <el-table-column prop="content" label="课程内容" />
            <el-table-column prop="rating" label="评分" width="80">
              <template #default="{ row }">
                <el-rate v-model="row.rating" disabled size="small" />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量操作" width="400px">
      <el-form label-width="100px">
        <el-form-item label="操作类型">
          <el-radio-group v-model="batchOperation.type">
            <el-radio label="pause">批量暂停</el-radio>
            <el-radio label="activate">批量激活</el-radio>
            <el-radio label="upgrade">批量升级</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="batchOperation.type === 'upgrade'" label="目标等级">
          <el-select v-model="batchOperation.targetLevel">
            <el-option label="银卡会员" value="silver" />
            <el-option label="金卡会员" value="gold" />
            <el-option label="钻石会员" value="diamond" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="batchDialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="executeBatchOperation"> 执行操作 </PrimaryButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {
  ArrowDown,
  Money,
  Operation,
  Plus,
  Refresh,
  Search,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import { useUserStore } from '@/stores/user'

// 数据列表
const studentList = ref([])
const selectedStudent = ref(null)
const selectedStudents = ref([])

// 状态
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const isEdit = ref(false)

// 校区信息
const userStore = useUserStore()
const campusName = computed(() => userStore.userInfo.campusName || '当前校区')

// 统计数据
const stats = reactive({
  active: 0,
  newThisMonth: 0,
  totalBalance: 0,
})

// 筛选器
const filters = reactive({
  status: '',
  dateRange: [],
  keyword: '',
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
})

// 表单数据
const studentForm = reactive({
  id: null,
  name: '',
  gender: 'male',
  phone: '',
  email: '',
  birthDate: '',
  emergencyContact: '',
  emergencyPhone: '',
  memberLevel: 'normal',
  notes: '',
})

// 批量操作
const batchOperation = reactive({
  type: 'pause',
  targetLevel: 'silver',
})

// 表单引用
const formRef = ref()

// 对话框标题
const dialogTitle = computed(() => (isEdit.value ? '编辑学员' : '新增学员'))

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入学员姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
}

// 获取学员列表
const fetchStudents = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    // 使用正确的API端点获取校区学生数据
    const response = await api.get('/admin/students', { params })

    // 根据后端实际返回的数据结构进行适配
    if (Array.isArray(response.data)) {
      // 获取所有师生关系数据
      const relationsResponse = await api.get('/mutual-selection/all')
      console.log('relationsResponse:', relationsResponse)
      const allRelations = relationsResponse.data || []

      // 将后端返回的StudentRecord对象映射为前端使用的格式
      studentList.value = response.data.map(student => {
        // 获取学生的教练信息
        const studentRelations = allRelations.filter(rel =>
          rel.studentId === student.studentId && (rel.status === 'PENDING' || rel.status === 'APPROVED' || rel.status === 'ACTIVE')
        )

        console.log('studentRelations:', studentRelations)

        const coaches = studentRelations.map(rel => ({
          id: rel.coachId,
          name: rel.coachName || '未知教练',
          status: rel.status
        }))

        return {
          id: student.studentId,
          studentId: student.studentId,
          name: student.realName,
          username: student.username,
          gender: student.gender.toLowerCase(), // 转换性别值
          age: student.age,
          campusId: student.campusId,
          balance: student.balance,
          maxCoach: student.maxCoach,
          currentCoach: student.currentCoach,
          // 添加一些默认值以避免前端报错
          phone: student.phoneNumber,
          status: 'active',
          registeredAt: student.createdAt,
          // 添加教练信息
          coaches: coaches
        }
      })
      pagination.total = response.data.length

      // 更新统计数据
      stats.active = studentList.value.length

      // 计算本月新增学员数
      const now = new Date()
      const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
      stats.newThisMonth = studentList.value.filter(student =>
        new Date(student.registeredAt) >= startOfMonth
      ).length

      // 计算总余额
      stats.totalBalance = studentList.value.reduce((sum, student) => sum + student.balance, 0)
    } else {
      studentList.value = []
      pagination.total = 0
      stats.active = 0
      stats.newThisMonth = 0
      stats.totalBalance = 0
    }
  } catch (error) {
    console.error('获取学员列表失败:', error)
    ElMessage.error('获取学员列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: '',
    dateRange: [],
    keyword: '',
  })
  fetchStudents()
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (student) => {
  isEdit.value = true
  Object.assign(studentForm, student)
  dialogVisible.value = true
}

// 显示详情对话框
const showDetailDialog = (student) => {
  selectedStudent.value = student
  detailDialogVisible.value = true
}

// 显示批量操作对话框
const showBatchDialog = () => {
  if (selectedStudents.value.length === 0) {
    ElMessage.warning('请先选择要操作的学员')
    return
  }
  batchDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(studentForm, {
    id: null,
    name: '',
    gender: 'male',
    phone: '',
    email: '',
    birthDate: '',
    emergencyContact: '',
    emergencyPhone: '',
    notes: '',
  })
}

// 保存学员
const saveStudent = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    saving.value = true

    if (isEdit.value) {
      // await api.put(`/admin/students/${studentForm.id}`, studentForm)
      // ElMessage.success('学员信息更新成功')
      ElMessage.error('暂时不支持更新学员信息')
    } else {
      // await api.post('/admin/students', studentForm)
      // ElMessage.success('学员创建成功')
      ElMessage.error('暂时不支持创建学员')
    }

    dialogVisible.value = false
    await fetchStudents()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

// 处理操作
const handleAction = async (command, student) => {
  switch (command) {
    case 'recharge':
      // TODO: 实现充值功能
      ElMessage.info('充值功能开发中')
      break
    case 'pause':
      await updateStudentStatus(student.id, 'paused')
      break
    case 'graduate':
      await updateStudentStatus(student.id, 'graduated')
      break
    case 'delete':
      await deleteStudent(student)
      break
  }
}

// 更新学员状态
const updateStudentStatus = async (studentId, status) => {
  try {
    await api.put(`/admin/students/${studentId}/status`, { status })
    ElMessage.success('状态更新成功')
    await fetchStudents()
  } catch {
    ElMessage.error('状态更新失败')
  }
}

// 删除学员
const deleteStudent = async (student) => {
  try {
    await ElMessageBox.confirm(`确定要删除学员"${student.name}"吗？此操作不可恢复！`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await api.delete(`/admin/students/${student.id}`)
    ElMessage.success('学员删除成功')
    await fetchStudents()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedStudents.value = selection
}

// 执行批量操作
const executeBatchOperation = async () => {
  try {
    const studentIds = selectedStudents.value.map((s) => s.id)

    await api.post('/admin/students/batch', {
      operation: batchOperation.type,
      studentIds,
      targetLevel: batchOperation.targetLevel,
    })

    ElMessage.success('批量操作执行成功')
    batchDialogVisible.value = false
    await fetchStudents()
  } catch {
    ElMessage.error('批量操作失败')
  }
}

// 计算年龄
const calculateAge = (birthDate) => {
  if (!birthDate) return 0
  return dayjs().diff(dayjs(birthDate), 'year')
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 格式化日期时间
const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}



// 获取状态类型
const getStatusType = (status) => {
  const types = {
    active: 'success',
    paused: 'warning',
    graduated: 'info',
    refunded: 'danger',
  }
  return types[status] || ''
}

// 获取教练标签类型
const getCoachTagType = (status) => {
  // PENDING-待审批(黄色), APPROVED-已审批(蓝色), ACTIVE-已激活(绿色), 其他状态(灰色)
  const types = {
    PENDING: 'warning',
    APPROVED: 'primary',
    ACTIVE: 'success',
    REJECTED: 'danger',
    INACTIVE: 'info'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    active: '正常',
    paused: '暂停',
    graduated: '已毕业',
    refunded: '已退费',
  }
  return texts[status] || status
}

// 组件挂载
onMounted(() => {
  fetchStudents()
})

// 监听学生列表变化以更新统计
watch(studentList, () => {
  // 更新统计数据
  stats.active = studentList.value.length

  // 计算本月新增学员数
  const now = new Date()
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1)
  stats.newThisMonth = studentList.value.filter(student =>
    new Date(student.registeredAt) >= startOfMonth
  ).length

  // 计算总余额
  stats.totalBalance = studentList.value.reduce((sum, student) => sum + student.balance, 0)
}, { deep: true })
</script>

<style scoped>
.student-management {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.new {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.balance {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-text {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

</style>
