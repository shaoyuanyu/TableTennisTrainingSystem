<template>
  <div class="campus-management">
    <!-- 头部卡片：采用统一 GlassHeaderCard 设计语言 -->
    <GlassHeaderCard title="校区管理" icon="🏢" size="large" class="campus-header-card">
      <template #headerActions>
        <div class="header-actions-group">
          <!-- 搜索框 -->
          <el-input
            v-model="search"
            placeholder="搜索：名称 / 地址 / 联系人 / 电话"
            clearable
            class="search-input"
            size="large"
            :disabled="loading"
            aria-label="搜索校区"
          >
            <template #prefix>
              <el-icon><i class="icon-search">🔍</i></el-icon>
            </template>
          </el-input>

          <!-- 未来：可加入筛选（占位） -->
          <!-- <el-select v-model="filterStatus" placeholder="状态" clearable size="large" class="status-filter">
            <el-option label="全部" value="" />
            <el-option label="主校区" value="main" />
          </el-select> -->

          <PrimaryButton @click="showAddDialog" size="md" class="add-campus-btn">
            <template #icon-left>
              <el-icon><Plus /></el-icon>
            </template>
            新增校区
          </PrimaryButton>
        </div>
      </template>

      <!-- 表格区域 -->
      <GlassTable
        :data="filteredCampuses"
        v-loading="loading"
        :stripe="true"
        density="lg"
        empty-title="暂无校区"
        empty-description="点击上方“新增校区”按钮创建第一个校区"
      >
        <el-table-column prop="campusName" label="校区名称" width="200">
          <template #default="{ row }">
            <div class="cell-campus-name">
              <span class="name-text">{{ row.campusName }}</span>
              <TagBadge
                v-if="row.isMain"
                text="主"
                type="success"
                size="small"
                variant="solid"
                extra-classes="main-campus-badge"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="240" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button link size="small" type="danger" @click="deleteCampus(row)">删除</el-button>
          </template>
        </el-table-column>
      </GlassTable>

      <!-- 底部操作/统计（可按需扩展） -->
      <div class="table-footer-hint" v-if="!loading && filteredCampuses.length">
        共 {{ filteredCampuses.length }} 个校区
      </div>
    </GlassHeaderCard>

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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import GlassTable from '@/components/data/Table.vue'
import { GlassHeaderCard } from '@/components/cards'
import TagBadge from '@/components/TagBadge.vue'
import { Plus } from '@element-plus/icons-vue'

// 数据列表
const campusList = ref([])
const availableUsers = ref([]) // 预留：管理员分配用户

// 过滤与搜索
const search = ref('')
// const filterStatus = ref('') // 预留筛选字段

const normalized = (v) => (v || '').toString().toLowerCase()
const filteredCampuses = computed(() => {
  if (!search.value) return campusList.value
  const key = normalized(search.value)
  return campusList.value.filter((c) => {
    return [c.campusName, c.address, c.contactPerson, c.phone, c.email]
      .filter(Boolean)
      .some((field) => normalized(field).includes(key))
  })
})

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
    const raw = response?.data?.first || []
    // 统一字段 & 兼容后端可能返回 name
    campusList.value = raw.map((item) => ({
      ...item,
      campusName: item.campusName || item.name || '未命名校区',
    }))
  } catch (error) {
    console.error('获取校区列表失败:', error)
    ElMessage.error('获取校区列表失败')
  } finally {
    loading.value = false
  }
}

// 获取可用用户列表
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
    await ElMessageBox.confirm(
      `确定要删除校区 "${campus.campusName || campus.name}" 吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )

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
  padding: var(--spacing-xl);
  padding-top: var(--spacing-lg);
}

.campus-header-card {
  --header-min-height: 64px;
}

.header-actions-group {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.search-input {
  width: 360px;
  max-width: 100%;
}

.add-campus-btn {
  /* 轻微凸显主操作 */
  box-shadow: var(--shadow-sm);
}

.cell-campus-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.name-text {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-footer-hint {
  margin-top: var(--spacing-lg);
  font-size: var(--font-size-sm);
  color: var(--text-dim, #4a5568);
  opacity: 0.85;
}

@media (max-width: 960px) {
  .search-input { width: 100%; }
  .header-actions-group { flex-direction: column; align-items: stretch; }
  .add-campus-btn { align-self: flex-end; }
  .name-text { max-width: 120px; }
}

@media (max-width: 560px) {
  .campus-management { padding: var(--spacing-lg); }
  .name-text { max-width: 100px; }
  .table-footer-hint { text-align: right; }
}
</style>
