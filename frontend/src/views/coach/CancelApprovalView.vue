<template>
  <div class="cancel-approval">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>取消预约审批</h2>
      <p>审核学员的取消预约申请</p>
    </div>

    <!-- 课程列表（GlassTable 包裹） -->
    <el-card class="table-card">
      <GlassTable 
        title="取消预约申请列表" 
        :data="cancelList" 
        :loading="loading" 
        density="md" 
        :stripe="true"
        :table-layout="'auto'"
      >
        <el-table-column prop="id" label="课程ID" min-width="100" />

        <el-table-column prop="student" label="学员信息" min-width="150">
          <template #default="{ row }">
            <div class="student-info">
              <el-avatar :size="32" :src="row.student.avatar" class="student-avatar">
                {{ row.student.name.charAt(0) }}
              </el-avatar>
              <div class="student-details">
                <div class="student-name">{{ row.student.name }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="date" label="预约日期" min-width="100">
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>

        <el-table-column label="时间段" min-width="100">
          <template #default="{ row }"> {{ row.startTime }} - {{ row.endTime }} </template>
        </el-table-column>

        <el-table-column prop="title" label="课程标题" min-width="120" />

        <el-table-column prop="price" label="费用" min-width="80">
          <template #default="{ row }"> ¥{{ row.price }} </template>
        </el-table-column>

        <el-table-column label="球桌号" min-width="80">
          <template #default="{ row }">
            <span v-if="row.tableIndex">{{ row.tableIndex }}</span>
            <span v-else>未分配</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="申请时间" min-width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleApprove(row)" class="action-button">
              确认
            </el-button>
            <el-button size="small" type="danger" @click="handleReject(row)" class="action-button">
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </GlassTable>

      <div class="pagination-wrapper" v-if="pagination.total > pagination.size">
        <el-pagination 
          v-model:current-page="pagination.page" 
          v-model:page-size="pagination.size"
          :total="pagination.total" 
          :page-sizes="[10, 20, 50, 100]" 
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchCancelCourses" 
          @current-change="fetchCancelCourses" 
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import api from '@/utils/api'
import GlassTable from '@/components/data/Table.vue'

// 数据列表
const cancelList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0,
})

// 获取取消预约课程列表
const fetchCancelCourses = async () => {
  loading.value = true
  try {
    const response = await api.get('/courses/querycancel')
    
    let courses = []
    let total = 0

    if (Array.isArray(response.data)) {
      courses = response.data
      total = courses.length
    } else {
      courses = []
      total = 0
    }

    cancelList.value = courses.map(course => {
      return {
        ...course,
        student: course.student || {
          name: course.studentName || '未知学员',
          phone: '',
          avatar: ''
        }
      }
    })
    pagination.total = total
  } catch (error) {
    console.error('获取取消预约列表失败:', error)
    ElMessage.error('获取取消预约列表失败: ' + (error.message || '未知错误'))
    cancelList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 处理确认操作
const handleApprove = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要确认学员 ${course.student?.name || '未知学员'} 的取消预约申请吗？`,
      '确认取消预约',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用API确认取消预约
    await api.post('/courses/judgecancel', {
      courseId: course.id,
      judge: true
    }, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    ElMessage.success('取消预约已确认')
    fetchCancelCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 处理拒绝操作
const handleReject = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝学员 ${course.student?.name || '未知学员'} 的取消预约申请吗？`,
      '拒绝取消预约',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用API拒绝取消预约
    await api.post('/courses/judgecancel', {
      courseId: course.id,
      judge: false
    }, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    ElMessage.success('取消预约已拒绝')
    fetchCancelCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
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
    PENDING: 'warning',
    CONFIRMED: 'success',
    CANCELLED: 'danger',
    COMPLETED: 'info',
    CANCELPENDING: 'warning'
  }
  const type = types[status] || ''
  console.log(`状态类型映射: ${status} => ${type}`) // 调试日志
  return type
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待审核',
    CONFIRMED: '已通过',
    CANCELLED: '已拒绝',
    COMPLETED: '已完成',
    CANCELLPENDING: '取消待审核'
  }
  const text = texts[status] || status
  console.log(`状态文本映射: ${status} => ${text}`) // 调试日志
  return text
}

// 组件挂载
onMounted(() => {
  fetchCancelCourses()
})
</script>

<style scoped>
.cancel-approval {
  padding: 20px;
  width: 100%;
}

.page-header {
  margin-bottom: 20px;
  padding: 16px 20px;
  background: linear-gradient(120deg, #e0f7fa, #bbdefb);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #01579b;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #0288d1;
  font-size: 14px;
}

.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: none;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-avatar {
  background-color: #4fc3f7;
}

.student-details {
  flex: 1;
  min-width: 0;
}

.student-name {
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  margin: 0;
  font-weight: 500;
}

.action-button {
  margin-right: 8px;
}

.action-button:last-child {
  margin-right: 0;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cancel-approval {
    padding: 10px;
  }

  .page-header {
    padding: 12px 16px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-table .el-button) {
    padding: 6px 8px;
    font-size: 12px;
  }
}
</style>