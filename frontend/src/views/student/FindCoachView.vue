<template>
  <div class="find-coach">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>选择教练</h2>
      <p>找到适合你的乒乓球教练，开始你的训练之旅</p>
    </div>

    <!-- 筛选器 -->
    <el-card class="filter-card">
      <el-form :model="filters" :inline="true" @submit.prevent="searchCoaches">
        <el-form-item label="教练等级">
          <el-select v-model="filters.level" placeholder="全部等级" clearable>
            <el-option label="初级教练" value="junior" />
            <el-option label="中级教练" value="intermediate" />
            <el-option label="高级教练" value="senior" />
            <el-option label="特级教练" value="expert" />
          </el-select>
        </el-form-item>

        <el-form-item label="专业特长">
          <el-select v-model="filters.specialty" placeholder="全部特长" clearable>
            <el-option label="基础技术" value="basic" />
            <el-option label="进阶技术" value="advanced" />
            <el-option label="实战训练" value="combat" />
            <el-option label="体能训练" value="fitness" />
            <el-option label="少儿乒乓" value="kids" />
          </el-select>
        </el-form-item>

        <el-form-item label="价格范围">
          <el-select v-model="filters.priceRange" placeholder="全部价格" clearable>
            <el-option label="100元以下" value="0-100" />
            <el-option label="100-150元" value="100-150" />
            <el-option label="150-200元" value="150-200" />
            <el-option label="200元以上" value="200-999" />
          </el-select>
        </el-form-item>

        <el-form-item label="评分">
          <el-select v-model="filters.rating" placeholder="全部评分" clearable>
            <el-option label="4.5分以上" value="4.5" />
            <el-option label="4.0分以上" value="4.0" />
            <el-option label="3.5分以上" value="3.5" />
          </el-select>
        </el-form-item>

        <el-form-item label="搜索">
          <el-input v-model="filters.keyword" placeholder="教练姓名" style="width: 150px" @keyup.enter="searchCoaches" />
        </el-form-item>

        <el-form-item>
          <PrimaryButton @click="searchCoaches" class="filter-btn">
            <el-icon>
              <Search />
            </el-icon> 搜索
          </PrimaryButton>
          <OutlineButton @click="resetFilters" class="filter-btn">
            <el-icon>
              <Refresh />
            </el-icon> 重置
          </OutlineButton>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 排序选项 -->
    <div class="sort-options">
      <span class="sort-label">排序方式：</span>
      <el-radio-group v-model="sortBy" @change="searchCoaches">
        <el-radio-button label="rating">评分最高</el-radio-button>
        <el-radio-button label="experience">经验最多</el-radio-button>
        <el-radio-button label="price_asc">价格最低</el-radio-button>
        <el-radio-button label="students">学员最多</el-radio-button>
      </el-radio-group>
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
            <PrimaryButton size="sm" @click.stop="applyForCoach(coach)">申请双选</PrimaryButton>
            <OutlineButton size="sm" @click.stop="viewCoachDetail(coach)">查看详情</OutlineButton>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && coachList.length === 0" class="empty-state">
      <el-empty description="没有找到符合条件的教练">
        <PrimaryButton @click="resetFilters">重置筛选条件</PrimaryButton>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div v-if="coachList.length > 0" class="pagination-wrapper">
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.size"
        :total="pagination.total" :page-sizes="[12, 24, 48]" layout="total, sizes, prev, pager, next, jumper"
        @size-change="searchCoaches" @current-change="searchCoaches" />
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
          <PrimaryButton size="lg" @click="selectCoach(selectedCoach)">
            选择这位教练
          </PrimaryButton>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/stores/user'
import {ElMessage, ElMessageBox} from 'element-plus'
import {Calendar, Refresh, Search, User} from '@element-plus/icons-vue'
import {OutlineButton, PrimaryButton} from '@/components/buttons'
import dayjs from 'dayjs'
import api from '@/utils/api'

const router = useRouter()

// 数据状态
const coachList = ref([])
const selectedCoach = ref(null)
const scheduleDate = ref(new Date())
const availableSchedule = ref({})

// 加载和对话框状态
const loading = ref(false)
const detailDialogVisible = ref(false)

// 筛选器
const filters = reactive({
  level: '',
  specialty: '',
  priceRange: '',
  rating: '',
  keyword: '',
})

// 排序
const sortBy = ref('rating')

// 分页
const pagination = reactive({
  page: 1,
  size: 12,
  total: 0,
})

// 搜索教练
const searchCoaches = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      sortBy: sortBy.value,
      ...filters,
    }

    console.log('发送教练查询请求:', { url: '/coaches', params })
    const response = await api.get('/coaches', { params })
    coachList.value = response.data.list || response.data.content || []
    pagination.total = response.data.total || response.data.totalElements || 0
    console.log('获取教练列表成功:', { count: coachList.value.length, total: pagination.total })
  } catch (error) {
    console.error('获取教练列表失败:', error)
    ElMessage.error('获取教练列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 重置筛选器
const resetFilters = () => {
  Object.assign(filters, {
    level: '',
    specialty: '',
    priceRange: '',
    rating: '',
    keyword: '',
  })
  sortBy.value = 'rating'
  pagination.page = 1
  searchCoaches()
}

// 查看教练详情
const viewCoachDetail = async (coach) => {
  try {
    console.log('获取教练详情:', coach.id)
    const response = await api.get(`/coaches/${coach.id}`)
    selectedCoach.value = response.data
    detailDialogVisible.value = true
    await loadSchedule()
  } catch (error) {
    console.error('获取教练详情失败:', error)
    ElMessage.error('获取教练详情失败: ' + (error.response?.data?.message || error.message))
  }
}

// 申请双选
const applyForCoach = async (coach) => {
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

    await api.post('/mutual-selection/apply', { coachId: coach.id })

    ElMessage.success('双选申请已提交，请等待教练审核')

    // 刷新教练列表
    await searchCoaches()
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
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    console.warn('用户未登录，暂不获取教练列表')
    return
  }

  console.log('开始获取教练列表，用户角色:', userStore.userRole)
  await searchCoaches()
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

.filter-card {
  margin-bottom: 20px;
}

.sort-options {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.sort-label {
  margin-right: 16px;
  color: #666;
  font-weight: 500;
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
