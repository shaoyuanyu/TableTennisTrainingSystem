<template>
  <div class="tournament-management">
    <PageHeader title="比赛管理" />
    
    <el-card class="create-tournament-card">
      <template #header>
        <div class="card-header">
          <span>创建新比赛</span>
        </div>
      </template>
      
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="120px"
        class="create-form"
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
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="createTournament"
            :loading="creating"
          >
            创建比赛
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card class="tournaments-list">
      <template #header>
        <div class="card-header">
          <span>比赛列表</span>
          <el-button type="primary" @click="fetchTournaments" :icon="Refresh" circle />
        </div>
      </template>
      
      <el-table
        :data="tournaments"
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="name" label="比赛名称" />
        <el-table-column prop="type" label="类型">
          <template #default="scope">
            <el-tag>{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="比赛日期" />
        <el-table-column prop="registrationDeadline" label="报名截止" />
        <el-table-column prop="fee" label="报名费">
          <template #default="scope">
            {{ scope.row.fee }} 元
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewDetails(scope.row)">详情</el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteTournament(scope.row)"
              :disabled="scope.row.status === 'completed'"
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import api from '@/utils/api'

// 表单引用
const createFormRef = ref()

// 表单数据
const createForm = ref({
  name: '',
  type: '',
  date: '',
  registrationDeadline: '',
  fee: 30,
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
    
    await api.post('/competition/tournaments/create', createForm.value)
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
    description: ''
  }
}

const fetchTournaments = async () => {
  try {
    loading.value = true
    const response = await api.get('/competition/tournaments', {
      params: {
        page: pagination.value.currentPage,
        size: pagination.value.pageSize
      }
    })
    tournaments.value = response.data.items
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
     <strong>比赛描述：</strong>${tournament.description || '无'}`,
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


const handleSizeChange = (val) => {
  pagination.value.pageSize = val
  fetchTournaments()
}

const handleCurrentChange = (val) => {
  pagination.value.currentPage = val
  fetchTournaments()
}

// 生命周期
onMounted(() => {
  fetchTournaments()
})
</script>

<style scoped>
.tournament-management {
  padding: 20px;
}

.create-tournament-card,
.tournaments-list {
  margin-bottom: 20px;
}

.create-form {
  max-width: 800px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>