<template>
  <div class="campus-management">
    <!-- 页面头部和列表合并为一个卡片 -->
    <GlassCard 
      class="campus-management-card" 
      variant="enhanced" 
      :show-decoration="false"
      title="校区管理"
      icon="🏢"
    >
      <template #default>
        <GlassTable 
          :data="campusList" 
          v-loading="loading" 
          :stripe="true"
          density="lg"
        >
          <el-table-column prop="campusName" label="校区名称" width="180" />
          <el-table-column prop="address" label="地址" min-width="200" />
          <el-table-column prop="contactPerson" label="联系人" width="120" />
          <el-table-column prop="phone" label="联系电话" width="140" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="primary" @click="showEditDialog(row)"> 编辑 </el-button>
              <el-button size="small" type="danger" @click="deleteCampus(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </GlassTable>

        <!-- 新增校区按钮移到表格下方 -->
        <div class="add-campus-button-container">
          <PrimaryButton @click="showAddDialog" class="add-campus-button">
            <template #icon-left>
              <el-icon>
                <Plus />
              </el-icon>
            </template>
            新增校区
          </PrimaryButton>
        </div>
      </template>
    </GlassCard>

    <!-- 新增/编辑校区对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="campusForm" :rules="formRules" label-width="100px">
        <el-form-item label="校区名称" prop="campusName">
          <el-input v-model="campusForm.campusName" placeholder="请输入校区名称" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="campusForm.address" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>

        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="campusForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>

        <el-form-item label="管理员用户名" prop="username">
          <el-input v-model="campusForm.username" placeholder="请输入管理员用户名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="campusForm.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="campusForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="dialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveCampus" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </PrimaryButton>
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
            <el-select v-model="adminForm.userId" placeholder="请选择用户" filterable style="width: 100%">
              <el-option v-for="user in availableUsers" :key="user.id" :label="`${user.name} (${user.username})`"
                :value="user.id" />
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
        <OutlineButton @click="adminDialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveAdmin" :loading="saving">设置管理员</PrimaryButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import GlassHeaderCard from '@/components/cards/specialized/GlassHeaderCard.vue'
import GlassTable from '@/components/data/Table.vue'
import GlassCard from '@/components/cards/base/GlassCard.vue'
import {Plus} from "@element-plus/icons-vue";

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
  campusName: '',
  address: '',
  contactPerson: '',
  username: '',
  phone: '',
  email: '',
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
  campusName: [
    { required: true, message: '请输入校区名称', trigger: 'blur' },
    { min: 2, max: 50, message: '校区名称长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  address: [
    { required: true, message: '请输入地址', trigger: 'blur' },
    { min: 5, max: 200, message: '地址长度在 5 到 200 个字符', trigger: 'blur' },
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' },
    { min: 2, max: 20, message: '联系人姓名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  username: [
    { required: true, message: '请输入管理员用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
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
    const response = await api.get('/campus/names?page=1&size=100')
    campusList.value = response.data.first || []
  } catch (error) {
    console.error('获取校区列表失败:', error)
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
    campusName: '',
    address: '',
    contactPerson: '',
    username: '',
    phone: '',
    email: '',
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
      ElMessage.info('编辑功能暂未实现')
    } else {
      // 创建校区
      await api.post('/campus/create', campusForm)
      ElMessage.success('校区创建成功')
    }

    dialogVisible.value = false
    fetchCampusList()
  } catch (error) {
    console.error('保存校区失败:', error)
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
  try {
    fetchCampusList()
  } catch (error) {
    console.error('CampusManagementView onMounted error:', error)
  }
})
</script>

<style scoped>
.campus-management {
  padding: 20px;
}

.campus-management-card {
  margin-top: 20px;
}

/* 确保卡片内的所有文字都有良好的对比度 */
:deep(.glass-card-base) {
  color: #1a202c;
}

:deep(.glass-card-title) {
  color: #1a202c;
  text-shadow: none;
}

/* 新增校区按钮容器样式 */
.add-campus-button-container {
  display: flex;
  justify-content: flex-start;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--white-alpha-20);
}

.add-campus-button {
  display: flex;
  align-items: center;
}
</style>