<template>
  <div class="coach-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>教练管理</h2>
      <div class="header-actions">
        <el-button @click="showScheduleDialog" :icon="Calendar"> 排班管理 </el-button>
        <el-button type="primary" @click="showAddDialog" :icon="Plus"> 新增教练 </el-button>
      </div>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchCoaches">
        <el-form-item label="教练状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="在职" value="active" />
            <el-option label="休假" value="leave" />
            <el-option label="离职" value="resigned" />
          </el-select>
        </el-form-item>

        <el-form-item label="专业等级">
          <el-select v-model="filters.level" placeholder="全部等级" clearable>
            <el-option label="初级教练" value="junior" />
            <el-option label="中级教练" value="intermediate" />
            <el-option label="高级教练" value="senior" />
            <el-option label="特级教练" value="expert" />
          </el-select>
        </el-form-item>

        <el-form-item label="入职时间">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="搜索">
          <el-input
            v-model="filters.keyword"
            placeholder="姓名/手机号/工号"
            style="width: 200px"
            @keyup.enter="fetchCoaches"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchCoaches" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 教练统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon active">
              <el-icon>
                <Avatar />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.active }}</div>
              <div class="stat-label">在职教练</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon students">
              <el-icon>
                <UserFilled />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.totalStudents }}</div>
              <div class="stat-label">管理学员</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue">
              <el-icon>
                <Money />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">¥{{ stats.monthlyRevenue }}</div>
              <div class="stat-label">月度收入</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rating">
              <el-icon>
                <Star />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.averageRating }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 教练列表 -->
    <el-card>
      <el-table :data="coachList" v-loading="loading" stripe>
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">
              {{ row.name.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column prop="coachId" label="工号" width="120" />

        <el-table-column prop="name" label="姓名" width="120" />

        <el-table-column prop="phone" label="手机号" width="140" />

        <el-table-column prop="level" label="等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="specialties" label="专长" width="150">
          <template #default="{ row }">
            <el-tag
              v-for="specialty in row.specialties"
              :key="specialty"
              size="small"
              class="specialty-tag"
            >
              {{ specialty }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="studentCount" label="学员数" width="100" />

        <el-table-column prop="rating" label="评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled size="small" show-score text-color="#ff9900" />
          </template>
        </el-table-column>

        <el-table-column prop="monthlyRevenue" label="月收入" width="120">
          <template #default="{ row }"> ¥{{ row.monthlyRevenue }} </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="joinedAt" label="入职时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.joinedAt) }}
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
                  <el-dropdown-item command="schedule">排班</el-dropdown-item>
                  <el-dropdown-item command="salary">薪资</el-dropdown-item>
                  <el-dropdown-item command="leave">请假</el-dropdown-item>
                  <el-dropdown-item divided command="resign">离职</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchCoaches"
          @current-change="fetchCoaches"
        />
      </div>
    </el-card>

    <!-- 新增/编辑教练对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="coachForm" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="coachForm.name" placeholder="请输入教练姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="coachForm.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="coachForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="coachForm.birthDate"
                type="date"
                placeholder="选择出生日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="coachForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>

        <el-form-item label="教练等级" prop="level">
          <el-select v-model="coachForm.level" placeholder="选择教练等级">
            <el-option label="初级教练" value="junior" />
            <el-option label="中级教练" value="intermediate" />
            <el-option label="高级教练" value="senior" />
            <el-option label="特级教练" value="expert" />
          </el-select>
        </el-form-item>

        <el-form-item label="专业特长" prop="specialties">
          <el-select
            v-model="coachForm.specialties"
            multiple
            placeholder="选择专业特长"
            style="width: 100%"
          >
            <el-option label="基础技术" value="基础技术" />
            <el-option label="进攻技术" value="进攻技术" />
            <el-option label="防守技术" value="防守技术" />
            <el-option label="战术指导" value="战术指导" />
            <el-option label="体能训练" value="体能训练" />
            <el-option label="心理辅导" value="心理辅导" />
          </el-select>
        </el-form-item>

        <el-form-item label="教学经验" prop="experience">
          <el-input
            v-model="coachForm.experience"
            type="textarea"
            :rows="3"
            placeholder="教学经验和成就"
          />
        </el-form-item>

        <el-form-item label="薪资标准" prop="hourlyRate">
          <el-input-number
            v-model="coachForm.hourlyRate"
            :min="0"
            :precision="2"
            placeholder="时薪"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCoach" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 教练详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="教练详情" width="800px">
      <div v-if="selectedCoach">
        <el-tabs type="border-card">
          <el-tab-pane label="基本信息">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="工号">
                {{ selectedCoach.coachId }}
              </el-descriptions-item>
              <el-descriptions-item label="姓名">
                {{ selectedCoach.name }}
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                {{ selectedCoach.gender === 'male' ? '男' : '女' }}
              </el-descriptions-item>
              <el-descriptions-item label="手机号">
                {{ selectedCoach.phone }}
              </el-descriptions-item>
              <el-descriptions-item label="邮箱">
                {{ selectedCoach.email }}
              </el-descriptions-item>
              <el-descriptions-item label="教练等级">
                <el-tag :type="getLevelType(selectedCoach.level)">
                  {{ getLevelText(selectedCoach.level) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="专业特长" :span="2">
                <el-tag
                  v-for="specialty in selectedCoach.specialties"
                  :key="specialty"
                  size="small"
                  style="margin-right: 8px"
                >
                  {{ specialty }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="入职时间">
                {{ formatDate(selectedCoach.joinedAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="getStatusType(selectedCoach.status)">
                  {{ getStatusText(selectedCoach.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>

          <el-tab-pane label="教学统计">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-statistic title="管理学员" :value="selectedCoach.studentCount" suffix="人" />
              </el-col>
              <el-col :span="8">
                <el-statistic title="月度收入" :value="selectedCoach.monthlyRevenue" prefix="¥" />
              </el-col>
              <el-col :span="8">
                <el-statistic
                  title="教学评分"
                  :value="selectedCoach.rating"
                  :precision="1"
                  suffix="分"
                />
              </el-col>
            </el-row>

            <div style="margin-top: 20px">
              <h4>最近教学记录</h4>
              <el-table :data="selectedCoach.recentClasses" size="small">
                <el-table-column prop="date" label="上课时间" width="150" />
                <el-table-column prop="student" label="学员" width="100" />
                <el-table-column prop="duration" label="时长" width="80" />
                <el-table-column prop="content" label="课程内容" />
                <el-table-column prop="rating" label="学员评分" width="100">
                  <template #default="{ row }">
                    <el-rate v-model="row.rating" disabled size="small" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <el-tab-pane label="排班信息">
            <el-calendar>
              <template #date-cell="{ data }">
                <div class="calendar-day">
                  <p>{{ data.day.split('-').slice(2).join('-') }}</p>
                  <div v-if="getScheduleForDate(data.day)" class="schedule-info">
                    <el-tag size="small">{{ getScheduleForDate(data.day) }}</el-tag>
                  </div>
                </div>
              </template>
            </el-calendar>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-dialog>

    <!-- 排班管理对话框 -->
    <el-dialog v-model="scheduleDialogVisible" title="排班管理" width="600px">
      <el-form label-width="100px">
        <el-form-item label="选择教练">
          <el-select v-model="scheduleForm.coachId" placeholder="选择教练">
            <el-option
              v-for="coach in coachList"
              :key="coach.id"
              :label="coach.name"
              :value="coach.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="排班日期">
          <el-date-picker
            v-model="scheduleForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>

        <el-form-item label="工作时间">
          <el-time-picker
            v-model="scheduleForm.workTime"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="HH:mm"
            value-format="HH:mm"
          />
        </el-form-item>

        <el-form-item label="休息日">
          <el-checkbox-group v-model="scheduleForm.restDays">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSchedule"> 保存排班 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Plus,
  Search,
  Refresh,
  Calendar,
  Avatar,
  UserFilled,
  Money,
  Star,
  ArrowDown,
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'

// 数据列表
const coachList = ref([])
const selectedCoach = ref(null)

// 状态
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const scheduleDialogVisible = ref(false)
const isEdit = ref(false)

// 统计数据
const stats = reactive({
  active: 0,
  totalStudents: 0,
  monthlyRevenue: 0,
  averageRating: 0,
})

// 筛选器
const filters = reactive({
  status: '',
  level: '',
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
const coachForm = reactive({
  id: null,
  name: '',
  gender: 'male',
  phone: '',
  email: '',
  birthDate: '',
  level: 'junior',
  specialties: [],
  experience: '',
  hourlyRate: 0,
})

// 排班表单
const scheduleForm = reactive({
  coachId: '',
  dateRange: [],
  workTime: [],
  restDays: [],
})

// 表单引用
const formRef = ref()

// 对话框标题
const dialogTitle = computed(() => (isEdit.value ? '编辑教练' : '新增教练'))

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入教练姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  level: [{ required: true, message: '请选择教练等级', trigger: 'change' }],
  hourlyRate: [{ required: true, message: '请输入时薪标准', trigger: 'blur' }],
}

// 获取教练列表
const fetchCoaches = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...filters,
    }

    const response = await api.get('/campus/coaches', { params })
    coachList.value = response.data.list || []
    pagination.total = response.data.total || 0

    // 获取统计数据
    const statsResponse = await api.get('/campus/coaches/stats')
    Object.assign(stats, statsResponse.data || {})
  } catch {
    ElMessage.error('获取教练列表失败')
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: '',
    level: '',
    dateRange: [],
    keyword: '',
  })
  fetchCoaches()
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (coach) => {
  isEdit.value = true
  Object.assign(coachForm, coach)
  dialogVisible.value = true
}

// 显示详情对话框
const showDetailDialog = (coach) => {
  selectedCoach.value = coach
  detailDialogVisible.value = true
}

// 显示排班对话框
const showScheduleDialog = () => {
  scheduleDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(coachForm, {
    id: null,
    name: '',
    gender: 'male',
    phone: '',
    email: '',
    birthDate: '',
    level: 'junior',
    specialties: [],
    experience: '',
    hourlyRate: 0,
  })
}

// 保存教练
const saveCoach = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    saving.value = true

    if (isEdit.value) {
      await api.put(`/campus/coaches/${coachForm.id}`, coachForm)
      ElMessage.success('教练信息更新成功')
    } else {
      await api.post('/campus/coaches', coachForm)
      ElMessage.success('教练创建成功')
    }

    dialogVisible.value = false
    fetchCoaches()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

// 保存排班
const saveSchedule = async () => {
  try {
    await api.post('/campus/coaches/schedule', scheduleForm)
    ElMessage.success('排班设置成功')
    scheduleDialogVisible.value = false
  } catch {
    ElMessage.error('排班设置失败')
  }
}

// 处理操作
const handleAction = async (command, coach) => {
  switch (command) {
    case 'schedule':
      scheduleForm.coachId = coach.id
      showScheduleDialog()
      break
    case 'salary':
      ElMessage.info('薪资管理功能开发中')
      break
    case 'leave':
      ElMessage.info('请假管理功能开发中')
      break
    case 'resign':
      await updateCoachStatus(coach.id, 'resigned')
      break
  }
}

// 更新教练状态
const updateCoachStatus = async (coachId, status) => {
  try {
    await api.put(`/campus/coaches/${coachId}/status`, { status })
    ElMessage.success('状态更新成功')
    fetchCoaches()
  } catch {
    ElMessage.error('状态更新失败')
  }
}

// 获取指定日期的排班信息
const getScheduleForDate = (date) => {
  // 模拟数据，实际应该从API获取
  const dayOfWeek = dayjs(date).day()
  if (dayOfWeek === 0 || dayOfWeek === 6) {
    return '休息'
  }
  return '09:00-18:00'
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 获取等级类型
const getLevelType = (level) => {
  const types = {
    junior: '',
    intermediate: 'success',
    senior: 'warning',
    expert: 'danger',
  }
  return types[level] || ''
}

// 获取等级文本
const getLevelText = (level) => {
  const texts = {
    junior: '初级教练',
    intermediate: '中级教练',
    senior: '高级教练',
    expert: '特级教练',
  }
  return texts[level] || level
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    active: 'success',
    leave: 'warning',
    resigned: 'danger',
  }
  return types[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    active: '在职',
    leave: '休假',
    resigned: '离职',
  }
  return texts[status] || status
}

// 组件挂载
onMounted(() => {
  fetchCoaches()
})
</script>

<style scoped>
.coach-management {
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

.stat-icon.students {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rating {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
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

.specialty-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.calendar-day {
  text-align: center;
}

.schedule-info {
  margin-top: 4px;
}

:deep(.el-table .el-button) {
  margin-right: 8px;
}

:deep(.el-table .el-button:last-child) {
  margin-right: 0;
}

:deep(.el-calendar-table .el-calendar-day) {
  height: 80px;
}
</style>
