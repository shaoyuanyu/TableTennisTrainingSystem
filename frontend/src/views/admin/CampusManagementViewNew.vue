<template>
  <div class="campus-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>校区管理</h2>
      <PrimaryButton @click="showAddDialog">
        <template #icon-left>
          <el-icon>
            <Plus />
          </el-icon>
        </template>
        新增校区
      </PrimaryButton>
    </div>

    <!-- 校区列表 -->
    <el-card>
      <el-table :data="campusList" v-loading="loading" stripe :lazy="true" :height="400" style="width: 100%">
        <el-table-column prop="name" label="校区名称" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="contact" label="联系人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status === 'active' ? '运营中' : '已停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showEditDialog(row)"> 编辑 </el-button>
            <el-button size="small" type="danger" @click="deleteCampus(row)" :disabled="row.isMain">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑校区对话框 - 延迟加载 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm" :destroy-on-close="true">
      <el-form ref="formRef" :model="campusForm" :rules="formRules" label-width="80px">
        <el-form-item label="校区名称" prop="name">
          <el-input v-model="campusForm.name" placeholder="请输入校区名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="campusForm.address" placeholder="请输入校区地址" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="campusForm.contact" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="campusForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="campusForm.status">
            <el-radio label="active">运营中</el-radio>
            <el-radio label="inactive">已停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="dialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveCampus" :loading="saving">
          {{ isEdit ? '保存' : '新增' }}
        </PrimaryButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'

// 数据
const campusList = ref([])

// 状态
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

// 表单数据 - 使用 shallowReactive 优化性能
const campusForm = reactive({
  id: null,
  name: '',
  address: '',
  contact: '',
  phone: '',
  status: 'active',
})

// 表单引用
const formRef = ref()

// 计算属性
const dialogTitle = computed(() => (isEdit.value ? '编辑校区' : '新增校区'))

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入校区名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入校区地址', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
}

// 获取校区列表 - 优化：减少不必要的数据
const fetchCampusList = async () => {
  loading.value = true
  try {
    const response = await api.get('/admin/campuses?fields=id,name,address,contact,phone,status')
    campusList.value = response.data || []
  } catch (error) {
    console.error('获取校区列表失败:', error)
    ElMessage.error('获取校区列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 显示新增对话框
const showAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (campus) => {
  isEdit.value = true
  Object.assign(campusForm, campus)
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  Object.assign(campusForm, {
    id: null,
    name: '',
    address: '',
    contact: '',
    phone: '',
    status: 'active',
  })

  // 使用 nextTick 确保 DOM 更新后再清除验证
  nextTick(() => {
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  })
}

// 保存校区
const saveCampus = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
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
  } catch (error) {
    if (error !== 'validation failed') {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    }
  } finally {
    saving.value = false
  }
}

// 删除校区
const deleteCampus = async (campus) => {
  try {
    await ElMessageBox.confirm(`确定要删除校区"${campus.name}"吗？`, '确认删除', {
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

// 组件挂载
onMounted(async () => {
  // 延迟加载，避免阻塞初始渲染
  await nextTick()
  fetchCampusList()
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

/* 优化表格性能 */
:deep(.el-table) {
  --el-table-border-color: #ebeef5;
}

:deep(.el-table .el-button) {
  margin-right: 8px;
}

:deep(.el-table .el-button:last-child) {
  margin-right: 0;
}

/* 优化对话框性能 */
:deep(.el-dialog) {
  --el-dialog-border-radius: 8px;
}
</style>
