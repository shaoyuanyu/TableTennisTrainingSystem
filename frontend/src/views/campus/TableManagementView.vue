<template>
  <div class="table-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>{{ campusName }}球台管理</h2>
      <div class="header-actions">
        <PrimaryButton @click="showAddDialog">
          <template #icon-left>
            <el-icon>
              <Plus />
            </el-icon>
          </template>
          增加球台
        </PrimaryButton>
      </div>
    </div>

    <!-- 球台统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon>
                <Management />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">球台总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon free">
              <el-icon>
                <Select />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.free }}</div>
              <div class="stat-label">空闲球台</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon busy">
              <el-icon>
                <Loading />
              </el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-number">{{ stats.occupied }}</div>
              <div class="stat-label">使用中</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 球台列表 -->
    <el-card>
      <el-table :data="tableList" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="indexInCampus" label="编号" width="80" />

        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="group" label="分组" width="100">
          <template #default="{ row }">
            <el-tag :type="getGroupType(row.group)">
              {{ getGroupText(row.group) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showDetailDialog(row)"> 详情 </el-button>
            <el-button size="small" @click="showEditDialog(row)"> 编辑 </el-button>
            <el-button size="small" type="danger" @click="deleteTable(row)"> 删除 </el-button>
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
          @size-change="fetchTables" 
          @current-change="fetchTables" />
      </div>
    </el-card>

    <!-- 新增/编辑球台对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="tableForm" :rules="formRules" label-width="100px">
        <el-form-item label="球台编号" prop="indexInCampus">
          <el-input-number 
            v-model="tableForm.indexInCampus" 
            :min="1" 
            :max="100" 
            style="width: 100%"
            :disabled="isEdit" />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="tableForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" value="free" />
            <el-option label="部分占用" value="partlyoccupy" />
            <el-option label="占用" value="occupy" />
          </el-select>
        </el-form-item>

        <el-form-item label="分组" prop="group">
          <el-select v-model="tableForm.group" placeholder="请选择分组" style="width: 100%">
            <el-option label="A组" value="A" />
            <el-option label="B组" value="B" />
            <el-option label="C组" value="C" />
            <el-option label="上课中" value="onclass" />
            <el-option label="空闲" value="free" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="dialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="saveTable" :loading="saving">
          {{ isEdit ? '更新' : '创建' }}
        </PrimaryButton>
      </template>
    </el-dialog>

    <!-- 球台详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="球台详情" width="600px">
      <div v-if="selectedTable">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="球台编号">
            {{ selectedTable.indexInCampus }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedTable.status)">
              {{ getStatusText(selectedTable.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="分组">
            <el-tag :type="getGroupType(selectedTable.group)">
              {{ getGroupText(selectedTable.group) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="所属校区">
            {{ campusName }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 使用记录 -->
        <div style="margin-top: 20px">
          <h4>最近使用记录</h4>
          <el-table :data="selectedTable.recentUsage" size="small">
            <el-table-column prop="date" label="使用日期" width="120" />
            <el-table-column prop="time" label="使用时间" width="150" />
            <el-table-column prop="user" label="使用者" />
            <el-table-column prop="purpose" label="用途" />
          </el-table>
        </div>
      </div>
    </el-dialog>

    <!-- 增加球台对话框 -->
    <el-dialog v-model="addDialogVisible" title="增加球台" width="500px">
      <el-form label-width="100px">
        <el-form-item label="增加数量">
          <el-input-number v-model="addForm.number" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
      </el-form>

      <template #footer>
        <OutlineButton @click="addDialogVisible = false">取消</OutlineButton>
        <PrimaryButton @click="addTables" :loading="adding"> 增加 </PrimaryButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Management, Plus, Select } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import { useUserStore } from '@/stores/user'

// 数据列表
const tableList = ref([])
const selectedTable = ref(null)

// 状态
const loading = ref(false)
const saving = ref(false)
const adding = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const addDialogVisible = ref(false)
const isEdit = ref(false)

// 校区信息
const userStore = useUserStore()
const campusName = computed(() => userStore.userInfo.campusName || '当前校区')

// 统计数据
const stats = reactive({
  total: 0,
  free: 0,
  occupied: 0
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 表单数据
const tableForm = reactive({
  id: null,
  indexInCampus: 1,
  status: 'free',
  group: 'free'
})

// 增加球台表单
const addForm = reactive({
  number: 1
})

// 表单引用
const formRef = ref()

// 对话框标题
const dialogTitle = computed(() => (isEdit.value ? '编辑球台' : '新增球台'))

// 表单验证规则
const formRules = {
  indexInCampus: [
    { required: true, message: '请输入球台编号', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  group: [
    { required: true, message: '请选择分组', trigger: 'change' }
  ]
}

// 获取球台列表
const fetchTables = async () => {
  loading.value = true
  try {
    const response = await api.get('/campus/queryFreeTables')
    
    // 后端返回的数据需要处理
    if (Array.isArray(response.data)) {
      tableList.value = response.data.map((table, index) => ({
        ...table,
        id: table.id || index + 1
      }))
      
      pagination.total = response.data.length
      
      // 更新统计数据
      stats.total = response.data.length
      stats.free = response.data.filter(t => t.status === 'free').length
      stats.occupied = response.data.filter(t => t.status !== 'free').length
    } else {
      tableList.value = []
      pagination.total = 0
      stats.total = 0
      stats.free = 0
      stats.occupied = 0
    }
  } catch (error) {
    console.error('获取球台列表失败:', error)
    ElMessage.error('获取球台列表失败')
  } finally {
    loading.value = false
  }
}

// 显示新增对话框
const showAddDialog = () => {
  addDialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (table) => {
  isEdit.value = true
  Object.assign(tableForm, table)
  dialogVisible.value = true
}

// 显示详情对话框
const showDetailDialog = (table) => {
  selectedTable.value = {
    ...table,
    recentUsage: [
      // 模拟数据
      { date: '2025-09-20', time: '14:00-15:00', user: '张三', purpose: '训练' },
      { date: '2025-09-20', time: '10:00-11:00', user: '李四', purpose: '训练' },
      { date: '2025-09-19', time: '16:00-17:00', user: '王五', purpose: '比赛' }
    ]
  }
  detailDialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(tableForm, {
    id: null,
    indexInCampus: 1,
    status: 'free',
    group: 'free'
  })
}

// 保存球台
const saveTable = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    saving.value = true

    if (isEdit.value) {
      // 编辑功能需要后端API支持
      ElMessage.info('编辑功能暂未实现')
    } else {
      // 创建功能需要后端API支持
      ElMessage.info('创建功能暂未实现')
    }

    dialogVisible.value = false
    await fetchTables()
  } catch {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

// 增加球台
const addTables = async () => {
  try {
    adding.value = true
    
    await api.post('/campus/addTable', {
      number: addForm.number
    })
    
    ElMessage.success(`成功增加${addForm.number}个球台`)
    addDialogVisible.value = false
    await fetchTables()
  } catch (error) {
    console.error('增加球台失败:', error)
    ElMessage.error('增加球台失败')
  } finally {
    adding.value = false
  }
}

// 删除球台
const deleteTable = async (table) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除球台"${table.indexInCampus}"吗？此操作不可恢复！`, 
      '确认删除', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 删除功能需要后端API支持
    ElMessage.info('删除功能暂未实现')
    await fetchTables()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    free: 'success',
    partlyoccupy: 'warning',
    occupy: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    free: '空闲',
    partlyoccupy: '部分占用',
    occupy: '占用'
  }
  return texts[status] || status
}

// 获取分组类型
const getGroupType = (group) => {
  const types = {
    A: 'primary',
    B: 'success',
    C: 'warning',
    onclass: 'danger',
    free: 'info'
  }
  return types[group] || 'info'
}

// 获取分组文本
const getGroupText = (group) => {
  const texts = {
    A: 'A组',
    B: 'B组',
    C: 'C组',
    onclass: '上课中',
    free: '空闲'
  }
  return texts[group] || group
}

// 组件挂载
onMounted(() => {
  fetchTables()
})
</script>

<style scoped>
.table-management {
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

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.free {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon.busy {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
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