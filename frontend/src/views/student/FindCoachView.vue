<template>
  <div class="find-coach">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>选择教练</h2>
      <p>找到适合你的乒乓球教练，开始你的训练之旅</p>
    </div>

    <!-- 教练列表 -->
    <div v-loading="loading" class="coaches-grid">
      <el-card v-for="coach in coachList" :key="coach.id" class="coach-card" @click="viewCoachDetail(coach)">
        <div class="coach-avatar">
          <el-avatar :size="80" :src="coach.avatar">
            {{ coach.name.charAt(0) }}
          </el-avatar>
          <el-tag v-if="coach.isOnline" type="success" size="small" class="online-status">
            在线
          </el-tag>
          <el-tag v-if="!coach.isSameCampus" type="warning" size="small" class="campus-status">
            非本校区
          </el-tag>
        </div>

        <div class="coach-info">
          <h3>{{ coach.name }}</h3>
          <p class="coach-level">{{ getLevelText(coach.level) }}</p>

          <div class="coach-rating">
            <el-rate v-model="coach.rating" disabled show-score text-color="#ff9900" score-template="{value}分" />
            <span class="rating-count">({{ coach.reviewCount }}条评价)</span>
          </div>

          <div class="coach-experience">
            <el-icon>
              <Calendar />
            </el-icon>
            <span>{{ coach.experience }}年经验</span>
            <el-icon>
              <User />
            </el-icon>
            <span>{{ coach.studentCount }}位学员</span>
          </div>

          <div class="coach-specialty">
            <el-tag v-for="specialty in coach.specialties" :key="specialty" size="small" type="info">
              {{ getSpecialtyText(specialty) }}
            </el-tag>
          </div>

          <div class="coach-price">
            <span class="price">¥{{ coach.hourlyRate }}</span>
            <span class="price-unit">/小时</span>
          </div>

          <div class="coach-actions">
            <PrimaryButton 
              v-if="coach.applicationStatus === 'APPROVED' || coach.applicationStatus === 'ACTIVE'"
              size="sm" 
              type="success"
              @click.stop
            >
              已建立关系
            </PrimaryButton>
            <PrimaryButton 
              v-else-if="coach.applicationStatus === 'PENDING'"
              size="sm" 
              type="warning"
              @click.stop
            >
              申请审核中
            </PrimaryButton>
            <PrimaryButton 
              v-else-if="!coach.isSameCampus"
              size="sm"
              disabled
              @click.stop
            >
              仅同校可申请
            </PrimaryButton>
            <PrimaryButton 
              v-else
              size="sm" 
              @click.stop="applyForCoach(coach)"
            >
              申请双选
            </PrimaryButton>
            <OutlineButton size="sm" @click.stop="viewCoachDetail(coach)">查看详情</OutlineButton>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && coachList.length === 0" class="empty-state">
      <el-empty description="没有找到符合条件的教练">
        <PrimaryButton @click="loadCoaches">刷新</PrimaryButton>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div v-if="coachList.length > 0" class="pagination-wrapper">
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
        :total="pagination.total" :page-sizes="[12, 24, 48]" layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadCoaches" @current-change="loadCoaches" />
    </div>

    <!-- 教练详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="教练详情" width="800px" @close="selectedCoach = null">
      <div v-if="selectedCoach" class="coach-detail">
        <div class="detail-header">
          <el-avatar :size="100" :src="selectedCoach.avatar">
            {{ selectedCoach.name.charAt(0) }}
          </el-avatar>
          <div class="header-info">
            <h2>{{ selectedCoach.name }}</h2>
            <p>{{ getLevelText(selectedCoach.level) }}</p>
            <div class="rating-section">
              <el-rate v-model="selectedCoach.rating" disabled show-score text-color="#ff9900"
                score-template="{value}分" />
              <span>({{ selectedCoach.reviewCount }}条评价)</span>
            </div>
            <div class="status-tags">
              <el-tag v-if="!selectedCoach.isSameCampus" type="warning" size="small">
                非本校区教练
              </el-tag>
              <el-tag 
                v-if="selectedCoach.applicationStatus === 'APPROVED' || selectedCoach.applicationStatus === 'ACTIVE'"
                type="success" 
                size="small"
              >
                已建立关系
              </el-tag>
              <el-tag 
                v-else-if="selectedCoach.applicationStatus === 'PENDING'"
                type="warning" 
                size="small"
              >
                申请审核中
              </el-tag>
            </div>
          </div>
          <div class="header-stats">
            <div class="stat-item">
              <div class="stat-number">{{ selectedCoach.experience }}</div>
              <div class="stat-label">年经验</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ selectedCoach.studentCount }}</div>
              <div class="stat-label">位学员</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ selectedCoach.completedClasses }}</div>
              <div class="stat-label">节课程</div>
            </div>
          </div>
        </div>

        <el-tabs>
          <el-tab-pane label="基本信息" name="basic">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="教练等级">
                {{ getLevelText(selectedCoach.level) }}
              </el-descriptions-item>
              <el-descriptions-item label="课时费用">
                ¥{{ selectedCoach.hourlyRate }}/小时
              </el-descriptions-item>
              <el-descriptions-item label="专业特长" :span="2">
                <el-tag v-for="specialty in selectedCoach.specialties" :key="specialty" size="small" type="info"
                  style="margin-right: 8px">
                  {{ getSpecialtyText(specialty) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="自我介绍" :span="2">
                {{ selectedCoach.introduction }}
              </el-descriptions-item>
            </el-descriptions>
          </el-tab-pane>

          <el-tab-pane label="学员评价" name="reviews">
            <div class="reviews-section">
              <div v-for="review in selectedCoach.reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <el-avatar :size="32" :src="review.student.avatar">
                    {{ review.student.name.charAt(0) }}
                  </el-avatar>
                  <div class="review-info">
                    <h5>{{ review.student.name }}</h5>
                    <div class="review-rating">
                      <el-rate v-model="review.rating" disabled size="small" />
                      <span class="review-date">{{ formatDate(review.createdAt) }}</span>
                    </div>
                  </div>
                </div>
                <p class="review-content">{{ review.content }}</p>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="可预约时间" name="schedule">
            <div class="schedule-section">
              <el-calendar v-model="scheduleDate" @panel-change="loadSchedule">
                <template #date-cell="{ data }">
                  <div class="schedule-cell">
                    <span class="date-text">{{ data.day.split('-').slice(1).join('/') }}</span>
                    <div v-if="getAvailableSlots(data.day).length > 0" class="available-slots">
                      {{ getAvailableSlots(data.day).length }}个时段可约
                    </div>
                  </div>
                </template>
              </el-calendar>
            </div>
          </el-tab-pane>
        </el-tabs>

        <div class="detail-actions">
          <OutlineButton size="lg" @click="detailDialogVisible = false">关闭</OutlineButton>
          <PrimaryButton 
            v-if="(selectedCoach.applicationStatus === 'APPROVED' || selectedCoach.applicationStatus === 'ACTIVE') && selectedCoach.isSameCampus"
            size="lg" 
            @click="selectCoach(selectedCoach)"
          >
            选择这位教练
          </PrimaryButton>
          <PrimaryButton 
            v-else-if="selectedCoach.applicationStatus === 'PENDING'"
            size="lg"
            disabled
          >
            申请审核中
          </PrimaryButton>
          <PrimaryButton 
            v-else-if="!selectedCoach.isSameCampus"
            size="lg"
            disabled
          >
            仅同校可选择
          </PrimaryButton>
          <PrimaryButton 
            v-else
            size="lg" 
            @click="applyForCoach(selectedCoach)"
          >
            申请双选
          </PrimaryButton>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref, watch} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Calendar, User} from '@element-plus/icons-vue'
import {OutlineButton, PrimaryButton} from '@/components/buttons'
import dayjs from 'dayjs'
import api from '@/utils/api'
import { getStudentApplications } from '@/api/mutualSelection'

const router = useRouter()
const userStore = useUserStore()

// 数据状态
const coachList = ref([])
const selectedCoach = ref(null)
const scheduleDate = ref(new Date())
const availableSchedule = ref({})

// 加载和对话框状态
const loading = ref(false)
const detailDialogVisible = ref(false)

// 学生申请状态
const studentApplications = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 12,
  total: 0,
})

// 加载学生申请状态
const loadStudentApplications = async () => {
  try {
    const response = await getStudentApplications()
    studentApplications.value = response.content || response.list || response
    
    // 更新教练列表中的申请状态
    updateCoachListWithApplicationStatus()
  } catch (error) {
    console.error('获取学生申请状态失败:', error)
  }
}

// 更新教练列表中的申请状态
const updateCoachListWithApplicationStatus = () => {
  if (!studentApplications.value.length || !coachList.value.length) return
  
  // 创建教练ID到申请状态的映射
  const applicationMap = {}
  studentApplications.value.forEach(app => {
    applicationMap[app.coachId] = app.status
  })
  
  // 更新教练列表中的申请状态
  coachList.value = coachList.value.map(coach => ({
    ...coach,
    applicationStatus: applicationMap[coach.id] || null
  }))
}

// 监听用户登录状态变化
watch(() => userStore.userId, (newUserId) => {
  if (newUserId) {
    loadStudentApplications()
  }
}, { immediate: true })

// 加载教练列表
const loadCoaches = async () => {
  loading.value = true
  try {
    // 先获取所有教练
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    console.log('发送教练查询请求:', { url: '/coach/coaches', params })
    const response = await api.get('/coach/coaches', { params })

    console.log('获取教练列表:', response)

    // 处理后端返回的数据格式并映射到前端需要的字段
    const allCoaches = response.data || []
    
    // 映射数据字段以匹配前端组件的期望
    coachList.value = allCoaches.map(coach => ({
      id: coach.coachId,
      name: coach.realName,
      username: coach.username,
      level: coach.level,
      hourlyRate: coach.hourlyRate,
      avatar: coach.photoUrl,
      experience: 0, // 后端暂未提供此字段
      studentCount: coach.currentStudents,
      rating: 5, // 默认评分
      reviewCount: 0, // 默认评价数
      specialties: [], // 后端暂未提供此字段
      campusId: coach.campusId,
      isOnline: true, // 默认在线状态
      isSameCampus: coach.campusId === userStore.campusId // 添加是否为同校区的标识
    }))

    // 更新教练列表中的申请状态
    updateCoachListWithApplicationStatus()

    pagination.total = allCoaches.length
    console.log('获取教练列表成功:', { count: coachList.value.length, total: pagination.total })
  } catch (error) {
    console.error('获取教练列表失败:', error)
    ElMessage.error('获取教练列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 查看教练详情
const viewCoachDetail = async (coach) => {
  try {
    console.log('获取教练详情:', coach.id)
    const response = await api.get(`/coach/coaches/${coach.id}`)
    selectedCoach.value = {
      ...response.data,
      id: response.data.coachId,
      name: response.data.realName,
      isSameCampus: response.data.campusId === userStore.campusId
    }
    
    // 添加申请状态
    const application = studentApplications.value.find(app => app.coachId === selectedCoach.value.id)
    if (application) {
      selectedCoach.value.applicationStatus = application.status
    }
    
    detailDialogVisible.value = true
    await loadSchedule()
  } catch (error) {
    console.error('获取教练详情失败:', error)
    ElMessage.error('获取教练详情失败: ' + (error.response?.data?.message || error.message))
  }
}

// 申请双选
const applyForCoach = async (coach) => {
  // 检查是否为同校区教练，如果不是则不允许申请
  if (!coach.isSameCampus) {
    ElMessage.warning('只能向同校区的教练申请双选')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要向教练 ${coach.name} 提交双选申请吗？`,
      '确认申请',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 修改这里：使用表单格式发送数据而不是JSON，并明确指定Content-Type
    const formData = new FormData()
    formData.append('coachId', coach.id)
    await api.post('/mutual-selection/apply', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    ElMessage.success('双选申请已提交，请等待教练审核')

    // 刷新教练列表和申请状态
    await loadStudentApplications()
    await loadCoaches()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('申请提交失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 选择教练（用于预约课程）
const selectCoach = (coach) => {
  router.push({
    path: '/student/book-training',
    query: { coachId: coach.id },
  })
}

// 加载教练排班
const loadSchedule = async () => {
  if (!selectedCoach.value) return

  try {
    const month = dayjs(scheduleDate.value).format('YYYY-MM')
    const response = await api.get(`/student/coaches/${selectedCoach.value.id}/schedule`, {
      params: { month },
    })
    availableSchedule.value = response.data || {}
  } catch {
    ElMessage.error('获取排班信息失败')
  }
}

// 获取可用时段
const getAvailableSlots = (date) => {
  return availableSchedule.value[date] || []
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

// 获取等级文本
const getLevelText = (level) => {
  const texts = {
    junior: '初级教练',
    intermediate: '中级教练',
    senior: '高级教练',
    expert: '特级教练',
    '初级教练员': '初级教练',
    '中级教练员': '中级教练',
    '高级教练员': '高级教练'
  }
  return texts[level] || level
}

// 获取特长文本
const getSpecialtyText = (specialty) => {
  const texts = {
    basic: '基础技术',
    advanced: '进阶技术',
    combat: '实战训练',
    fitness: '体能训练',
    kids: '少儿乒乓',
  }
  return texts[specialty] || specialty
}

// 组件挂载
onMounted(async () => {
  // 添加延迟，确保用户认证状态稳定
  await new Promise((resolve) => setTimeout(resolve, 200))

  // 检查用户是否已经正确登录
  if (!userStore.isLoggedIn) {
    console.warn('用户未登录，暂不获取教练列表')
    return
  }

  console.log('开始获取教练列表，用户角色:', userStore.userRole)
  await loadStudentApplications()
  await loadCoaches()
})
</script>

<style scoped>
.find-coach {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
}

.coaches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.coach-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.coach-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.coach-avatar {
  text-align: center;
  position: relative;
  margin-bottom: 16px;
}

.online-status {
  position: absolute;
  top: 0;
  right: 50%;
  transform: translateX(50%);
}

.campus-status {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
}

.coach-info h3 {
  margin: 0 0 4px 0;
  text-align: center;
  color: #333;
}

.coach-level {
  text-align: center;
  color: #666;
  font-size: 14px;
  margin: 0 0 12px 0;
}

.coach-rating {
  text-align: center;
  margin-bottom: 12px;
}

.rating-count {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.coach-experience {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.coach-specialty {
  text-align: center;
  margin-bottom: 12px;
}

.coach-price {
  text-align: center;
  margin-bottom: 16px;
}

.price {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.price-unit {
  font-size: 14px;
  color: #666;
}

.coach-actions {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.pagination-wrapper {
  text-align: center;
  margin-top: 40px;
}

.coach-detail {
  padding: 20px 0;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.header-info {
  flex: 1;
}

.header-info h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.header-info p {
  margin: 0 0 12px 0;
  color: #666;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-tags {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.header-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.reviews-section {
  max-height: 400px;
  overflow-y: auto;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-info h5 {
  margin: 0;
  color: #333;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.schedule-section {
  padding: 0 20px;
}

.schedule-cell {
  text-align: center;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.date-text {
  font-size: 14px;
  color: #333;
}

.available-slots {
  font-size: 10px;
  color: #67c23a;
  margin-top: 2px;
}

.detail-actions {
  margin-top: 30px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>