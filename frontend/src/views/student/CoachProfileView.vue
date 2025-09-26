<template>
  <div class="coach-profile-view">
    <el-page-header 
      title="返回" 
      content="教练详情" 
      @back="$router.back()" 
      class="page-header"
    />
    
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="profile-content">
          <el-card class="profile-card">
            <el-skeleton-item variant="circle" style="width: 100px; height: 100px;" />
            <el-skeleton-item variant="text" style="width: 50%; height: 24px; margin-top: 20px;" />
            <el-skeleton-item variant="text" style="width: 30%; height: 20px; margin-top: 10px;" />
          </el-card>
        </div>
      </template>
      
      <template #default>
        <div v-if="coach" class="profile-content">
          <!-- 教练基本信息 -->
          <el-card class="profile-card">
            <div class="profile-header">
              <el-avatar :size="100" :src="coach.photoUrl || undefined">
                {{ coach.realName?.charAt(0) }}
              </el-avatar>
              <div class="profile-info">
                <h2>{{ coach.realName }}</h2>
                <p class="coach-level">{{ coach.level }}</p>
                <div class="coach-stats">
                  <div class="stat-item">
                    <span class="stat-value">{{ coach.currentStudents }}</span>
                    <span class="stat-label">当前学员</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value">{{ coach.maxStudents }}</span>
                    <span class="stat-label">最大容量</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value">¥{{ coach.hourlyRate }}</span>
                    <span class="stat-label">时薪</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="profile-details">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="用户名">{{ coach.username }}</el-descriptions-item>
                <el-descriptions-item label="手机号">{{ coach.phoneNumber }}</el-descriptions-item>
                <el-descriptions-item label="成就">{{ coach.achievements || '暂无' }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="coach.isApproved ? 'success' : 'warning'">
                    {{ coach.isApproved ? '已认证' : '未认证' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
            
            <div class="profile-actions">
              <el-button 
                v-if="coach.isSameCampus && !coach.hasRelation" 
                type="primary" 
                @click="applyForCoach"
                :loading="applying"
              >
                申请双选
              </el-button>
              <el-button 
                v-else-if="coach.hasRelation" 
                type="success" 
                disabled
              >
                已建立关系
              </el-button>
              <el-button 
                v-else 
                type="info" 
                disabled
              >
                仅同校可申请
              </el-button>
              <el-button @click="goToBooking">预约课程</el-button>
            </div>
          </el-card>
        </div>
        <el-empty v-else description="未找到教练信息" />
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElSkeleton, ElSkeletonItem } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/utils/api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const applying = ref(false)
const coach = ref(null)

// 获取教练详情
const fetchCoachDetail = async () => {
  loading.value = true
  try {
    // 使用后端提供的queryCoachByUuid接口
    const response = await api.post('/coach/queryCoachByUuid', {
      username: route.params.id // 注意：前端传递的是coachId，但后端接口参数名为username
    })
    
    const coachData = response.data
    
    coach.value = {
      ...coachData,
      isSameCampus: coachData.campusId === userStore.campusId,
      hasRelation: false // 需要通过其他接口确认是否已建立关系
    }
    
    console.log('获取教练详情成功:', coach.value)
  } catch (error) {
    console.error('获取教练详情失败:', error)
    ElMessage.error('获取教练详情失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 申请双选
const applyForCoach = async () => {
  if (!coach.value.isSameCampus) {
    ElMessage.warning('只能向同校区的教练申请双选')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要向教练 ${coach.value.realName} 提交双选申请吗？`,
      '确认申请',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    applying.value = true
    const formData = new FormData()
    formData.append('coachId', coach.value.coachId)
    
    await api.post('/mutual-selection/apply', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    ElMessage.success('双选申请已提交，请等待教练审核')
    coach.value.hasRelation = true
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('申请提交失败: ' + (error.response?.data?.message || error.message))
    }
  } finally {
    applying.value = false
  }
}

// 跳转到预约页面
const goToBooking = () => {
  router.push({
    path: '/student/book-training',
    query: { coachId: coach.value.coachId }
  })
}

onMounted(() => {
  fetchCoachDetail()
})
</script>

<style scoped>
.coach-profile-view {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.profile-content {
  margin-top: 20px;
}

.profile-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.profile-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.coach-level {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #666;
}

.coach-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.profile-details {
  padding: 20px;
}

.profile-actions {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #eee;
}

.profile-actions .el-button {
  margin: 0 10px;
}
</style>