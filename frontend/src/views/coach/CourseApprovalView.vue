<template>
  <div class="course-approval">
    <!-- 页面头部 -->
    <GlassCardBase 
      title="课程审批" 
      subtitle="审核学员的课程预约申请" 
      icon="📋"
      variant="enhanced"
      class="header-card"
    />

    <!-- 筛选器 -->
    <GlassCard 
      title="筛选条件" 
      icon="🔍"
      variant="content"
      class="filter-card"
    >
      <el-form :model="filters" :inline="true" @submit.prevent="fetchPendingCourses">
        <el-form-item label="预约日期">
          <el-date-picker 
            v-model="filters.dateRange" 
            type="daterange" 
            range-separator="至" 
            start-placeholder="开始日期"
            end-placeholder="结束日期" 
            format="YYYY-MM-DD" 
            value-format="YYYY-MM-DD"
            class="date-picker" />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="请选择状态" class="status-select">
            <el-option label="全部状态" value="" />
            <el-option label="待审批" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <PrimaryButton @click="fetchPendingCourses">搜索</PrimaryButton>
          <OutlineButton @click="resetFilters" type="secondary">重置</OutlineButton>
        </el-form-item>
      </el-form>
    </GlassCard>

    <!-- 待审批课程列表 -->
    <GlassCard 
      title="待审批列表" 
      icon="📝"
      variant="content"
      class="list-card"
    >
      <el-table 
        :data="pendingCourses" 
        v-loading="loading"
        stripe
        style="width: 100%"
        class="approval-table"
      >
        <el-table-column prop="id" label="课程ID" width="80" />
        
        <el-table-column prop="title" label="课程标题" width="150" />
        
        <el-table-column label="学员信息" width="180">
          <template #default="{ row }">
            <div class="student-info">
              <el-avatar :size="32" :src="row.student?.avatar || ''">
                {{ row.studentName ? row.studentName.charAt(0) : '?' }}
              </el-avatar>
              <div class="student-details">
                <div class="student-name">{{ row.studentName || '未知学员' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ row.date }} {{ getStartTime(row.NO) }} - {{ getEndTime(row.NO) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="price" label="费用(¥)" width="100">
          <template #default="{ row }">
            {{ row.price }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="showDetailDialog(row)">
              详情
            </el-button>
            <el-button size="small" type="success" @click="approveCourse(row)" :disabled="row.status !== 'pending'">
              通过
            </el-button>
            <el-button size="small" type="danger" @click="rejectCourse(row)" :disabled="row.status !== 'pending'">
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper" v-if="pagination.total > 0">
        <el-pagination 
          v-model:current-page="pagination.page" 
          v-model:page-size="pagination.size"
          :total="pagination.total" 
          :page-sizes="[10, 20, 50, 100]" 
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchPendingCourses" 
          @current-change="fetchPendingCourses" />
      </div>
      
      <el-empty v-else description="暂无待审批的课程" />
    </GlassCard>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="课程详情" width="600px">
      <div v-if="selectedCourse">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程ID">
            {{ selectedCourse.id }}
          </el-descriptions-item>
          
          <el-descriptions-item label="课程标题">
            {{ selectedCourse.title }}
          </el-descriptions-item>
          
          <el-descriptions-item label="学员姓名">
            {{ selectedCourse.studentName || '未知学员' }}
          </el-descriptions-item>
          
          <el-descriptions-item label="预约日期">
            {{ selectedCourse.date }}
          </el-descriptions-item>
          
          <el-descriptions-item label="上课时间">
            {{ getStartTime(selectedCourse.NO) }} - {{ getEndTime(selectedCourse.NO) }}
          </el-descriptions-item>
          
          <el-descriptions-item label="课程费用">
            ¥{{ selectedCourse.price }}
          </el-descriptions-item>
          
          <el-descriptions-item label="课程状态">
            <el-tag :type="getStatusTagType(selectedCourse.status)">
              {{ getStatusText(selectedCourse.status) }}
            </el-tag>
          </el-descriptions-item>
          
          <el-descriptions-item label="校区">
            {{ selectedCourse.campusName || '未知校区' }}
          </el-descriptions-item>
          
          <el-descriptions-item label="特殊要求" :span="2">
            {{ selectedCourse.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <OutlineButton @click="detailDialogVisible = false">关闭</OutlineButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import GlassCard from '@/components/cards/base/GlassCard.vue'
import GlassCardBase from '@/components/cards/base/GlassCardBase.vue'

// 数据列表
const pendingCourses = ref([])
const selectedCourse = ref(null)
const detailDialogVisible = ref(false)

// 状态
const loading = ref(false)

// 筛选器
const filters = reactive({
  dateRange: [],
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 获取待审批课程列表
const fetchPendingCourses = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    
    // 添加日期筛选条件
    if (filters.dateRange && filters.dateRange.length === 2) {
      params.dateFrom = filters.dateRange[0]
      params.dateTo = filters.dateRange[1]
    }
    
    // 添加状态筛选条件
    if (filters.status) {
      params.status = filters.status
    }

    const response = await api.get('/courses/querypending', { params })
    pendingCourses.value = response.data || []
    pagination.total = pendingCourses.value.length
  } catch (error) {
    console.error('获取待审批课程列表失败:', error)
    ElMessage.error('获取待审批课程列表失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  filters.dateRange = []
  filters.status = ''
  fetchPendingCourses()
}

// 显示详情对话框
const showDetailDialog = (course) => {
  selectedCourse.value = course
  detailDialogVisible.value = true
}

// 通过课程预约
const approveCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过学员 ${course.studentName || '未知学员'} 的课程预约吗？`, 
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    // 调用API通过课程预约
    await api.post('/courses/coach-judge', {
      courseId: course.id,
      confirmed: true
    })

    ElMessage.success('课程预约已通过')
    fetchPendingCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 拒绝课程预约
const rejectCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝学员 ${course.studentName || '未知学员'} 的课程预约吗？`, 
      '确认拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用API拒绝课程预约
    await api.post('/courses/coach-judge', {
      courseId: course.id,
      confirmed: false
    })

    ElMessage.success('课程预约已拒绝')
    fetchPendingCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败: ' + (error.message || '未知错误'))
    }
  }
}

// 根据课程节数获取开始时间
const getStartTime = (NO) => {
  const startHour = 7 + NO
  return `${startHour.toString().padStart(2, '0')}:00`
}

// 根据课程节数获取结束时间
const getEndTime = (NO) => {
  const endHour = 8 + NO
  return `${endHour.toString().padStart(2, '0')}:00`
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待审批',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPendingCourses()
})
</script>

<style scoped>
.course-approval {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-card {
  margin-bottom: 24px;
}

.filter-card {
  margin-bottom: 24px;
}

.list-card {
  margin-bottom: 24px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.student-details {
  flex: 1;
}

.student-name {
  font-weight: 500;
  color: #333;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-table .el-button) {
  margin-right: 8px;
}

:deep(.el-table .el-button:last-child) {
  margin-right: 0;
}

:deep(.el-descriptions-item__label) {
  font-weight: 500;
}

:deep(.approval-table) {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.date-picker {
  width: 300px;
}

.status-select {
  width: 150px;
}
</style>