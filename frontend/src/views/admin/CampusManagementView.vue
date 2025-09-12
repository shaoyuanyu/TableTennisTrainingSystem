<template>
  <div class="campus-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>校区管理</h2>
      <el-button type="primary" @click="showAddDialog" :icon="Plus"> 新增校区 </el-button>
    </div>

    <!-- 校区列表 -->
    <el-card>
      <el-table :data="campusList" v-loading="loading" stripe>
        <el-table-column prop="name" label="校区名称" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="contact" label="联系人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '运营中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="统计" width="120">
          <template #default="{ row }">
            <div class="stats-cell">
              <span>学员: {{ row.studentCount }}</span>
              <span>教练: {{ row.coachCount }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showEditDialog(row)"> 编辑 </el-button>
            <el-button size="small" type="warning" @click="showAdminDialog(row)">
              管理员
            </el-button>
            <el-button size="small" type="danger" @click="deleteCampus(row)" :disabled="row.isMain">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑校区对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="campusForm" :rules="formRules" label-width="80px">
        <el-form-item label="校区名称" prop="name">
          <el-input v-model="campusForm.name" placeholder="请输入校区名称" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input
            v-model="campusForm.address"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>

        <el-form-item label="联系人" prop="contact">
          <el-input v-model="campusForm.contact" placeholder="请输入联系人姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="campusForm.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="campusForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="campusForm.status"
            active-value="active"
            inactive-value="inactive"
            active-text="运营中"
            inactive-text="已停用"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCampus" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 校区管理员设置对话框 -->
    <el-dialog v-model="adminDialogVisible" title="设置校区管理员" width="500px">
      <div v-if="selectedCampus">
        <p>
          为 <strong>{{ selectedCampus.name }}</strong> 设置管理员
        </p>

        <el-form :model="adminForm" label-width="100px">
          <el-form-item label="当前管理员">
            <div v-if="selectedCampus.admin">
              <el-tag type="success">{{ selectedCampus.admin.name }}</el-tag>
              <span class="admin-info">（{{ selectedCampus.admin.username }}）</span>
            </div>
            <el-text v-else type="warning">暂未设置管理员</el-text>
          </el-form-item>

          <el-form-item label="选择管理员">
            <el-radio-group v-model="adminForm.type">
              <el-radio label="existing">从现有用户中选择</el-radio>
              <el-radio label="new">创建新管理员账号</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item v-if="adminForm.type === 'existing'" label="选择用户">
            <el-select
              v-model="adminForm.userId"
              placeholder="请选择用户"
              filterable
              style="width: 100%"
            >
              <el-option
                v-for="user in availableUsers"
                :key="user.id"
                :label="`${user.name} (${user.username})`"
                :value="user.id"
              />
            </el-select>
          </el-form-item>

          <template v-if="adminForm.type === 'new'">
            <el-form-item label="用户名">
              <el-input v-model="adminForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="adminForm.name" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="adminForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="adminForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </template>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="adminDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAdmin" :loading="saving"> 设置管理员 </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import { debugSidebar } from '@/utils/debug'

// 数据列表
const campusList = ref([])
const availableUsers = ref([])

// 加载状态
const loading = ref(false)
const saving = ref(false)

// 对话框控制
const dialogVisible = ref(false)
const adminDialogVisible = ref(false)
const isEdit = ref(false)
const selectedCampus = ref(null)

// 表单数据
const campusForm = reactive({
  id: null,
  name: '',
  address: '',
  contact: '',
  phone: '',
  email: '',
  status: 'active',
})

const adminForm = reactive({
  type: 'existing',
  userId: null,
  username: '',
  name: '',
  phone: '',
  email: '',
})

// 表单引用
const formRef = ref()

// 对话框标题
const dialogTitle = computed(() => (isEdit.value ? '编辑校区' : '新增校区'))

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入校区名称', trigger: 'blur' },
    { min: 2, max: 50, message: '校区名称长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度在 5 到 200 个字符', trigger: 'blur' },
  ],
  contact: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { min: 2, max: 20, message: '联系人姓名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' },
  ],
}

// 获取校区列表
const fetchCampusList = async () => {
  loading.value = true
  try {
    const response = await api.get('/admin/campuses')
    campusList.value = response.data || []
  } catch {
    ElMessage.error('获取校区列表失败')
  } finally {
    loading.value = false
  }
}

// 获取可用用户列表
const fetchAvailableUsers = async () => {
  try {
    const response = await api.get('/admin/users?role=campus_admin&available=true')
    availableUsers.value = response.data || []
  } catch {
    ElMessage.error('获取用户列表失败')
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (campus) => {
  isEdit.value = true
  Object.assign(campusForm, campus)
  dialogVisible.value = true
}

// 显示管理员设置对话框
const showAdminDialog = (campus) => {
  selectedCampus.value = campus
  adminDialogVisible.value = true
  fetchAvailableUsers()
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(campusForm, {
    id: null,
    name: '',
    address: '',
    contact: '',
    phone: '',
    email: '',
    status: 'active',
  })
}

// 保存校区
const saveCampus = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    saving.value = true

    if (isEdit.value) {
      await api.put(`/admin/campuses/${campusForm.id}`, campusForm)
      ElMessage.success('校区更新成功')
    } else {
      await api.post('/admin/campuses', campusForm)
      ElMessage.success('校区创建成功')
    }

    dialogVisible.value = false
    fetchCampusList()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

// 删除校区
const deleteCampus = async (campus) => {
  if (campus.isMain) {
    ElMessage.warning('中心校区不能删除')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除校区"${campus.name}"吗？此操作不可恢复！`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    await api.delete(`/admin/campuses/${campus.id}`)
    ElMessage.success('校区删除成功')
    fetchCampusList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 保存管理员设置
const saveAdmin = async () => {
  try {
    saving.value = true

    const data = {
      campusId: selectedCampus.value.id,
      type: adminForm.type,
      ...adminForm,
    }

    await api.post('/admin/campus-admin', data)
    ElMessage.success('管理员设置成功')
    adminDialogVisible.value = false
    fetchCampusList()
  } catch {
    ElMessage.error('设置失败')
  } finally {
    saving.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  debugSidebar.logComponentLoad('CampusManagementView')
  debugSidebar.startPerfMeasure('campus-management-init')
  
  try {
    fetchCampusList()
    debugSidebar.endPerfMeasure('campus-management-init')
  } catch (error) {
    debugSidebar.logError(error, 'CampusManagementView onMounted')
  }
})
</script>

<style scoped>
.campus-management {
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

.stats-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.admin-info {
  margin-left: 8px;
  font-size: 12px;
  color: #666;
}

:deep(.el-table .el-button) {
  margin-right: 8px;
}

:deep(.el-table .el-button:last-child) {
  margin-right: 0;
}
</style>
