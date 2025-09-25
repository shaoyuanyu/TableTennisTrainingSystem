<template>
  <div class="coach-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ campusName }}教练管理</h2>
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
          新增教练
        </PrimaryButton>
      </div>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="fetchCoaches">
        <el-form-item label="教练状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable>
            <el-option label="已审核" value="approved" />
            <el-option label="待审核" value="pending" />
            <el-option label="已停用" value="disabled" />
          </el-select>
        </el-form-item>

        <el-form-item label="教练等级">
          <el-select v-model="filters.level" placeholder="全部等级" clearable>
            <el-option label="初级教练" value="junior" />
            <el-option label="中级教练" value="intermediate" />
            <el-option label="高级教练" value="senior" />
            <el-option label="资深教练" value="expert" />
          </el-select>
        </el-form-item>

        <el-form-item label="搜索">
          <el-input v-model="filters.keyword" placeholder="姓名/手机号" style="width: 200px"
                    @keyup.enter="fetchCoaches" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="fetchCoaches" :icon="Search"> 搜索 </el-button>
          <el-button @click="resetFilters" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 教练统计 -->
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
              <div class="stat-label">在职教练</div>
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
              <div class="stat-number">{{ stats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon students">
              <el-icon>
                <User />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.totalRelations }}</div>
              <div class="stat-label">师生关系总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 教练列表 -->
    <el-card>
      <el-table :data="coachList" v-loading="loading" stripe @selection-change="handleSelectionChange" style="width: 100%">
        <el-table-column type="selection" width="55" />

        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar">
              {{ row.username.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="用户名" />

        <el-table-column prop="name" label="姓名" />

        <el-table-column prop="phone" label="手机号" />

        <el-table-column prop="level" label="教练等级" width="120">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="hourlyRate" label="课时费" width="100">
          <template #default="{ row }"> ¥{{ row.hourlyRate }}/节 </template>
        </el-table-column>

        <el-table-column prop="students" label="学员数" width="100">
          <template #default="{ row }"> {{ row.currentStudents }}/{{ row.maxStudents }} </template>
        </el-table-column>

        <el-table-column prop="relations" label="关系状态" width="150">
          <template #default="{ row }">
            <div v-if="row.relations && row.relations.length > 0">
              <el-tag
                v-for="relation in row.relations"
                :key="relation.selectionId"
                :type="getRelationStatusType(relation.status)"
                style="margin: 2px;"
                size="small"
              >
                {{ getStudentName(relation.studentId, relation.studentName) }}
              </el-tag>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.isApproved)">
              {{ getStatusText(row.isApproved) }}
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
                  <el-dropdown-item command="approve" v-if="row.status === 'pending'">审核通过</el-dropdown-item>
                  <el-dropdown-item command="disable">停用</el-dropdown-item>
                  <el-dropdown-item command="enable" v-if="row.status === 'disabled'">启用</el-dropdown-item>
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
                       @size-change="fetchCoaches" @current-change="fetchCoaches" />
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
              <el-date-picker v-model="coachForm.birthDate" type="date" placeholder="选择出生日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="coachForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教练等级" prop="level">
              <el-select v-model="coachForm.level" placeholder="选择教练等级">
                <el-option label="初级教练" value="junior" />
                <el-option label="中级教练" value="intermediate" />
                <el-option label="高级教练" value="senior" />
                <el-option label="资深教练" value="expert" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课时费" prop="hourlyRate">
              <el-input-number v-model="coachForm.hourlyRate" :min="0" :step="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="最大学员数" prop="maxStudents">
          <el-slider v-model="coachForm.maxStudents" :min="1" :max="50" show-input />
        </el-form-item>

        <el-form-item label="资质证书">
          <el-upload
            class="certificate-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleCertificateSuccess"
            :before-upload="beforeCertificateUpload"
          >
            <img v-if="coachForm.certificateUrl" :src="coachForm.certificateUrl" class="certificate" />
            <el-icon v-else class="certificate-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="个人成就">
          <el-input v-model="coachForm.achievements" type="textarea" :rows="3" placeholder="个人成就介绍" />
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="dialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveCoach" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </PrimaryButton>
      </template>
    </el-dialog>

    <!-- 教练详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="教练详情" width="800px">
      <div v-if="selectedCoach">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">
            {{ selectedCoach.username }}
          </el-descriptions-item>
          <el-descriptions-item label="姓名">
            {{ selectedCoach.name }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ selectedCoach.gender === 'male' ? '男' : '女' }}
          </el-descriptions-item>
          <el-descriptions-item label="年龄">
            {{ calculateAge(selectedCoach.birthDate) }}岁
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
          <el-descriptions-item label="课时费">
            ¥{{ selectedCoach.hourlyRate }}/节
          </el-descriptions-item>
          <el-descriptions-item label="学员数量">
            {{ selectedCoach.currentStudents }}/{{ selectedCoach.maxStudents }}
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDateTime(selectedCoach.registeredAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedCoach.status)">
              {{ getStatusText(selectedCoach.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 资质证书 -->
        <div style="margin-top: 20px" v-if="selectedCoach.certificateUrl">
          <h4>资质证书</h4>
          <el-image
            style="width: 100%; height: 200px"
            :src="selectedCoach.certificateUrl"
            :preview-src-list="[selectedCoach.certificateUrl]"
            fit="contain"
          />
        </div>

        <!-- 个人成就 -->
        <div style="margin-top: 20px" v-if="selectedCoach.achievements">
          <h4>个人成就</h4>
          <div class="achievements-content">
            {{ selectedCoach.achievements }}
          </div>
        </div>

        <!-- 学员列表 -->
        <div style="margin-top: 20px">
          <h4>当前学员</h4>
          <el-table :data="selectedCoach.students" size="small">
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="140" />
            <el-table-column prop="memberLevel" label="会员等级" width="120">
              <template #default="{ row }">
                <el-tag :type="getMemberLevelType(row.memberLevel)">
                  {{ getMemberLevelText(row.memberLevel) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="courseCount" label="剩余课时" width="100" />
            <el-table-column prop="registeredAt" label="注册时间" width="120">
              <template #default="{ row }">
                {{ formatDate(row.registeredAt) }}
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 关系状态 -->
        <div style="margin-top: 20px">
          <h4>关系状态</h4>
          <div v-if="selectedCoach.relations && selectedCoach.relations.length > 0">
            <el-tag
              v-for="relation in selectedCoach.relations"
              :key="relation.selectionId"
              :type="getRelationStatusType(relation.status)"
              style="margin: 4px;"
            >
              {{ getStudentName(relation.studentId, relation.studentName) }} - {{ getRelationStatusText(relation.status) }}
            </el-tag>
          </div>
          <div v-else>
            <el-alert title="暂无学员关系" type="info" :closable="false" />
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 批量操作对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量操作" width="400px">
      <el-form label-width="100px">
        <el-form-item label="操作类型">
          <el-radio-group v-model="batchOperation.type">
            <el-radio label="approve">批量审核</el-radio>
            <el-radio label="disable">批量停用</el-radio>
            <el-radio label="enable">批量启用</el-radio>
          </el-radio-group>
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
import { getAllRelations } from '@/api/mutualSelection'

// 数据列表
const coachList = ref([])
const selectedCoach = ref(null)
const selectedCoaches = ref([])

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
  pending: 0,
  totalRelations: 0,
})

// 筛选器
const filters = reactive({
  status: '',
  level: '',
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
  hourlyRate: 100,
  maxStudents: 10,
  certificateUrl: '',
  achievements: '',
})

// 批量操作
const batchOperation = reactive({
  type: 'approve',
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
  birthDate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
  level: [{ required: true, message: '请选择教练等级', trigger: 'change' }],
  hourlyRate: [{ required: true, message: '请输入课时费', trigger: 'change' }],
  maxStudents: [{ required: true, message: '请设置最大学员数', trigger: 'change' }],
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

    // 修改API调用路径为正确的后端端点
    const response = await api.get('/admin/coaches', { params })

    console.log('coaches', response.data)

    // 获取所有师生关系数据
    const relationsResponse = await api.get('/mutual-selection/all')
    const allRelations = relationsResponse.data || []

    console.log('allRelations', allRelations)

    coachList.value = (response.data || []).map(coach => {
      // 获取教练的关系信息
      const coachRelations = allRelations.filter(rel =>
        rel.coachId === coach.coachId && (rel.status === 'PENDING' || rel.status === 'APPROVED' || rel.status === 'ACTIVE')
      )

      return {
        ...coach,
        // 映射后端返回的字段到前端使用的字段
        id: coach.coachId,
        name: coach.realName,
        username: coach.username,
        phone: coach.phoneNumber,
        avatar: coach.photoUrl,
        registeredAt: coach.createdAt,
        // 关系信息
        relations: coachRelations
      }
    })

    pagination.total = response.data.total || 0

    // 更新统计数据
    updateStats()
  } catch (error) {
    console.error('获取教练列表失败:', error)
    ElMessage.error('获取教练列表失败')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = async () => {
  // 确保教练列表存在
  if (!coachList.value) {
    stats.active = 0
    stats.pending = 0
    stats.totalRelations = 0
    return
  }

  console.log(coachList)

  // 计算在职教练数量 (已审核通过的教练)
  stats.active = coachList.value.filter(coach => coach.isApproved === true).length

  // 计算待审核教练数量
  stats.pending = coachList.value.filter(coach => coach.isApproved === false).length

  // 获取师生关系总数
  try {
    const allRelations = await getAllRelations()
    stats.totalRelations = allRelations.length || 0
  } catch (error) {
    console.error('获取师生关系总数失败:', error)
    stats.totalRelations = 0
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    status: '',
    level: '',
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

// 显示批量操作对话框
const showBatchDialog = () => {
  if (selectedCoaches.value.length === 0) {
    ElMessage.warning('请先选择要操作的教练')
    return
  }
  batchDialogVisible.value = true
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
    hourlyRate: 100,
    maxStudents: 10,
    certificateUrl: '',
    achievements: '',
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
      await api.put(`/admin/coaches/${coachForm.id}`, coachForm)
      ElMessage.success('教练信息更新成功')
    } else {
      await api.post('/admin/coaches', coachForm)
      ElMessage.success('教练创建成功')
    }

    dialogVisible.value = false
    await fetchCoaches()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

// 处理操作
const handleAction = async (command, coach) => {
  switch (command) {
    case 'approve':
      await updateCoachStatus(coach.id, 'approved')
      break
    case 'disable':
      await updateCoachStatus(coach.id, 'disabled')
      break
    case 'enable':
      await updateCoachStatus(coach.id, 'approved')
      break
    case 'delete':
      await deleteCoach(coach)
      break
  }
}

// 更新教练状态
const updateCoachStatus = async (coachId, status) => {
  try {
    await api.put(`/admin/coaches/${coachId}/status`, { status })
    ElMessage.success('状态更新成功')
    await fetchCoaches()
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
  }
}

// 删除教练
const deleteCoach = async (coach) => {
  try {
    await ElMessageBox.confirm(`确定要删除教练"${coach.name}"吗？此操作不可恢复！`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await api.delete(`/admin/coaches/${coach.id}`)
    ElMessage.success('教练删除成功')
    await fetchCoaches()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedCoaches.value = selection
}

// 执行批量操作
const executeBatchOperation = async () => {
  try {
    const coachIds = selectedCoaches.value.map((c) => c.id)

    await api.post('/admin/coaches/batch', {
      operation: batchOperation.type,
      coachIds,
    })

    ElMessage.success('批量操作执行成功')
    batchDialogVisible.value = false
    await fetchCoaches()
  } catch (error) {
    console.error('批量操作失败:', error)
    ElMessage.error('批量操作失败')
  }
}

// 证书上传成功处理
const handleCertificateSuccess = (response, file) => {
  coachForm.certificateUrl = response.data.url
}

// 证书上传前处理
const beforeCertificateUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

// 计算年龄
const calculateAge = (birthDate) => {
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

// 获取关系状态类型
const getRelationStatusType = (status) => {
  // PENDING-待审批(橙色), APPROVED-已审批(蓝色), ACTIVE-已激活(绿色), 其他状态(灰色)
  const types = {
    PENDING: 'warning',
    APPROVED: 'primary',
    ACTIVE: 'success',
    REJECTED: 'danger',
    INACTIVE: 'info'
  }
  return types[status] || 'info'
}

// 获取关系状态文本
const getRelationStatusText = (status) => {
  const texts = {
    PENDING: '待审批',
    APPROVED: '已审批',
    ACTIVE: '已激活',
    REJECTED: '已拒绝',
    INACTIVE: '非激活'
  }
  return texts[status] || status
}

// 获取学员姓名
const getStudentName = (studentId, studentName) => {
  return studentName || '未知学员'
}

// 获取教练等级类型
const getLevelType = (level) => {
  const types = {
    junior: 'info',
    intermediate: 'primary',
    senior: 'warning',
    expert: 'success',
  }
  return types[level] || 'info'
}

// 获取教练等级文本
const getLevelText = (level) => {
  const texts = {
    junior: '初级教练',
    intermediate: '中级教练',
    senior: '高级教练',
    expert: '资深教练',
  }
  return texts[level] || level
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    true: 'success',
    false: 'warning',
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    true: '在职',
    false: '待审核',
  }
  return texts[status] || status
}

// 获取会员等级类型
const getMemberLevelType = (level) => {
  const types = {
    normal: 'info',
    silver: 'primary',
    gold: 'warning',
    diamond: 'success',
  }
  return types[level] || 'info'
}

// 获取会员等级文本
const getMemberLevelText = (level) => {
  const texts = {
    normal: '普通会员',
    silver: '银卡会员',
    gold: '金卡会员',
    diamond: '钻石会员',
  }
  return texts[level] || level
}

// 组件挂载
onMounted(() => {
  fetchCoaches()
})

// 监听教练列表变化以更新统计
watch(coachList, () => {
  updateStats()
}, { deep: true })
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

.stat-icon.new {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.students {
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

.certificate-uploader .certificate {
  width: 178px;
  height: 178px;
  display: block;
}

.certificate-uploader .certificate-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.certificate-uploader .certificate-uploader-icon:hover {
  border-color: #409eff;
}

.achievements-content {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

</style>
