<template>
    <div class="dashboard">
        <!-- 背景装饰元素 -->
        <div class="background-decorations">
            <div class="floating-element ping-pong-1">🏓</div>
            <div class="floating-element ping-pong-2">🏓</div>
            <div class="floating-element ping-pong-3">🏓</div>
            <div class="floating-element trophy-1">🏆</div>
            <div class="floating-element star-1">⭐</div>
            <div class="floating-element star-2">⭐</div>
            <div class="geometric-shape shape-1"></div>
            <div class="geometric-shape shape-2"></div>
            <div class="geometric-shape shape-3"></div>
            <div class="artistic-text main-title">PING PONG</div>
            <div class="artistic-text sub-title">培训管理系统</div>
        </div>

        <!-- 欢迎横幅 -->
        <div class="welcome-banner">
            <div class="banner-left">
                <div class="welcome-content">
                    <h1 class="welcome-title">{{ getWelcomeMessage() }}，{{ userName }}！</h1>
                    <p class="welcome-subtitle">{{ getWelcomeSubtitle() }}</p>
                    <div class="banner-stats">
                        <div class="stat-item">
                            <span class="stat-value">{{ getTodayStats().today }}</span>
                            <span class="stat-label">今日活动</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-value">{{ getTodayStats().week }}</span>
                            <span class="stat-label">本周目标</span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="banner-right">
                <div class="avatar-container">
                    <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
                        <el-icon size="40">
                            <User />
                        </el-icon>
                    </el-avatar>
                    <div class="avatar-badge">
                        <el-tag :type="getUserStatusType()" size="small">{{ getUserStatus() }}</el-tag>
                    </div>
                </div>
            </div>
        </div>

        <!-- 数据统计卡片 -->
        <el-row :gutter="24" class="stats-row">
            <el-col :xs="12" :sm="6" v-for="stat in stats" :key="stat.key">
                <el-card class="stat-card" shadow="hover">
                    <div class="stat-content">
                        <div class="stat-icon" :style="{ background: stat.gradient }">
                            <component :is="stat.icon" />
                        </div>
                        <div class="stat-info">
                            <div class="stat-value">{{ stat.value }}</div>
                            <div class="stat-label">{{ stat.label }}</div>
                            <div class="stat-trend" :class="stat.trend">
                                <el-icon v-if="stat.trend === 'up'">
                                    <TrendCharts />
                                </el-icon>
                                <el-icon v-if="stat.trend === 'down'">
                                    <TrendCharts />
                                </el-icon>
                                <span>{{ stat.trendText }}</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 主要功能区域 -->
        <el-row :gutter="24" class="main-content">
            <!-- 左侧内容 -->
            <el-col :xs="24" :lg="16">
                <!-- 今日课程安排 -->
                <el-card class="content-card schedule-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <div class="header-left">
                                <el-icon class="header-icon">
                                    <Calendar />
                                </el-icon>
                                <span>今日安排</span>
                            </div>
                            <el-button type="primary" size="small" @click="goToSchedule">查看完整课表</el-button>
                        </div>
                    </template>

                    <div v-if="todaySchedule.length > 0" class="schedule-timeline">
                        <div v-for="item in todaySchedule" :key="item.id" class="timeline-item">
                            <div class="timeline-time">{{ item.time }}</div>
                            <div class="timeline-content">
                                <div class="timeline-title">{{ item.title }}</div>
                                <div class="timeline-description">{{ item.description }}</div>
                                <div class="timeline-meta">
                                    <el-tag :type="getScheduleType(item.type)" size="small">{{ item.type }}</el-tag>
                                    <span class="timeline-location">{{ item.location }}</span>
                                </div>
                            </div>
                            <div class="timeline-status">
                                <el-button v-if="item.status === 'upcoming'" size="small" type="primary"
                                    @click="handleScheduleAction(item)">
                                    {{ getActionText(item.type) }}
                                </el-button>
                                <el-tag v-else :type="getStatusType(item.status)" size="small">
                                    {{ getStatusText(item.status) }}
                                </el-tag>
                            </div>
                        </div>
                    </div>

                    <div v-else class="empty-schedule">
                        <el-empty description="今天没有安排" :image-size="100">
                            <template #image>
                                <div class="empty-icon">🎯</div>
                            </template>
                            <el-button type="primary" @click="createSchedule">安排新活动</el-button>
                        </el-empty>
                    </div>
                </el-card>

                <!-- 最新动态 -->
                <el-card class="content-card activity-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <div class="header-left">
                                <el-icon class="header-icon">
                                    <Bell />
                                </el-icon>
                                <span>最新动态</span>
                            </div>
                            <el-link type="primary" @click="goToMessages">查看全部</el-link>
                        </div>
                    </template>

                    <div class="activity-feed">
                        <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
                            <div class="activity-avatar">
                                <el-avatar :size="32" :src="activity.avatar">
                                    <el-icon>
                                        <component :is="activity.icon" />
                                    </el-icon>
                                </el-avatar>
                            </div>
                            <div class="activity-content">
                                <div class="activity-text">{{ activity.text }}</div>
                                <div class="activity-time">{{ formatTime(activity.createdAt) }}</div>
                            </div>
                            <div class="activity-action" v-if="activity.actionable">
                                <el-button size="small" type="primary" text @click="handleActivity(activity)">
                                    查看
                                </el-button>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧边栏 -->
            <el-col :xs="24" :lg="8">
                <!-- 天气信息 -->
                <el-card class="sidebar-card weather-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon class="header-icon">
                                <Sunny />
                            </el-icon>
                            <span>今日天气</span>
                        </div>
                    </template>
                    <div class="weather-content">
                        <div class="weather-main">
                            <div class="weather-temp">{{ weather.temperature }}°C</div>
                            <div class="weather-desc">{{ weather.description }}</div>
                        </div>
                        <div class="weather-icon">{{ weather.icon }}</div>
                    </div>
                    <div class="weather-details">
                        <div class="weather-detail">
                            <span>湿度</span>
                            <span>{{ weather.humidity }}%</span>
                        </div>
                        <div class="weather-detail">
                            <span>风速</span>
                            <span>{{ weather.windSpeed }} km/h</span>
                        </div>
                    </div>
                    <div class="weather-suggestion">
                        <el-alert :title="weather.suggestion" type="info" :closable="false" />
                    </div>
                </el-card>

                <!-- 快捷操作 -->
                <el-card class="sidebar-card actions-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon class="header-icon">
                                <Operation />
                            </el-icon>
                            <span>快捷操作</span>
                        </div>
                    </template>
                    <div class="quick-actions">
                        <el-button v-for="action in quickActions" :key="action.key" :type="action.type"
                            @click="action.handler" class="action-button" size="large">
                            <template #icon>
                                <component :is="action.icon" />
                            </template>
                            {{ action.label }}
                        </el-button>
                    </div>
                </el-card>

                <!-- 本周统计 -->
                <el-card class="sidebar-card weekly-stats-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon class="header-icon">
                                <TrendCharts />
                            </el-icon>
                            <span>本周统计</span>
                        </div>
                    </template>
                    <div class="weekly-stats">
                        <div v-for="stat in weeklyStats" :key="stat.key" class="weekly-stat-item">
                            <div class="weekly-stat-info">
                                <div class="weekly-stat-label">{{ stat.label }}</div>
                                <div class="weekly-stat-value">{{ stat.value }}</div>
                            </div>
                            <div class="weekly-stat-progress">
                                <el-progress :percentage="stat.percentage" :color="stat.color" :stroke-width="6"
                                    :show-text="false" />
                            </div>
                        </div>
                    </div>
                </el-card>

                <!-- 成就徽章 -->
                <el-card class="sidebar-card achievements-card" shadow="hover">
                    <template #header>
                        <div class="card-header">
                            <el-icon class="header-icon">
                                <Trophy />
                            </el-icon>
                            <span>最新成就</span>
                        </div>
                    </template>
                    <div class="achievements">
                        <div v-for="achievement in recentAchievements" :key="achievement.id" class="achievement-item">
                            <div class="achievement-icon">{{ achievement.icon }}</div>
                            <div class="achievement-info">
                                <div class="achievement-title">{{ achievement.title }}</div>
                                <div class="achievement-date">{{ formatDate(achievement.date) }}</div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'
import {
    User,
    Calendar,
    Star,
    TrendCharts,
    Bell,
    Trophy,
    UserFilled,
    Document,
    Edit,
    Message,
    Setting,
    Sunny,
    Operation
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 获取用户信息
const userInfo = computed(() => userStore.userInfo || {})
const userName = computed(() => userInfo.value.name || '用户')

// 获取欢迎信息
const getWelcomeMessage = () => {
    const hour = dayjs().hour()
    if (hour < 6) return '夜深了'
    if (hour < 12) return '早上好'
    if (hour < 18) return '下午好'
    return '晚上好'
}

const getWelcomeSubtitle = () => {
    const role = userInfo.value.role
    const subtitles = {
        student: '今天也要加油训练哦！',
        coach: '让我们为学员们创造更好的体验！',
        campus_admin: '管理好校区，服务好大家！',
        super_admin: '系统运行正常，一切尽在掌握！'
    }
    return subtitles[role] || '欢迎使用乒乓球培训管理系统！'
}

// 获取用户状态
const getUserStatus = () => {
    const role = userInfo.value.role
    const statusMap = {
        student: '学员',
        coach: '教练',
        campus_admin: '校区管理员',
        super_admin: '超级管理员'
    }
    return statusMap[role] || '用户'
}

const getUserStatusType = () => {
    const role = userInfo.value.role
    const typeMap = {
        student: 'primary',
        coach: 'success',
        campus_admin: 'warning',
        super_admin: 'danger'
    }
    return typeMap[role] || 'info'
}

// 今日统计
const getTodayStats = () => {
    const role = userInfo.value.role
    if (role === 'student') {
        return { today: '2', week: '8' }
    } else if (role === 'coach') {
        return { today: '6', week: '24' }
    } else if (role === 'campus_admin') {
        return { today: '15', week: '68' }
    } else {
        return { today: '45', week: '188' }
    }
}

// 数据统计
const stats = computed(() => {
    const role = userInfo.value.role

    if (role === 'student') {
        return [
            {
                key: 'courses',
                label: '已报课程',
                value: '12',
                icon: Calendar,
                gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                trend: 'up',
                trendText: '+2 本月'
            },
            {
                key: 'hours',
                label: '训练时长',
                value: '48h',
                icon: TrendCharts,
                gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                trend: 'up',
                trendText: '+8h 本周'
            },
            {
                key: 'coach',
                label: '专属教练',
                value: '3',
                icon: UserFilled,
                gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                trend: 'stable',
                trendText: '保持稳定'
            },
            {
                key: 'level',
                label: '技能等级',
                value: 'B',
                icon: Star,
                gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                trend: 'up',
                trendText: '进步中'
            }
        ]
    } else if (role === 'coach') {
        return [
            {
                key: 'students',
                label: '学员数量',
                value: '25',
                icon: UserFilled,
                gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                trend: 'up',
                trendText: '+3 本月'
            },
            {
                key: 'courses',
                label: '课程数量',
                value: '8',
                icon: Calendar,
                gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                trend: 'stable',
                trendText: '保持稳定'
            },
            {
                key: 'rating',
                label: '平均评分',
                value: '4.8',
                icon: Star,
                gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                trend: 'up',
                trendText: '+0.2 本月'
            },
            {
                key: 'income',
                label: '本月收入',
                value: '¥5200',
                icon: TrendCharts,
                gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                trend: 'up',
                trendText: '+12% 环比'
            }
        ]
    } else if (role === 'campus_admin') {
        return [
            {
                key: 'students',
                label: '学员总数',
                value: '156',
                icon: UserFilled,
                gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                trend: 'up',
                trendText: '+12 本月'
            },
            {
                key: 'coaches',
                label: '教练总数',
                value: '12',
                icon: User,
                gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                trend: 'stable',
                trendText: '保持稳定'
            },
            {
                key: 'revenue',
                label: '本月营收',
                value: '¥45600',
                icon: TrendCharts,
                gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                trend: 'up',
                trendText: '+18% 环比'
            },
            {
                key: 'satisfaction',
                label: '满意度',
                value: '96%',
                icon: Star,
                gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                trend: 'up',
                trendText: '+2% 本月'
            }
        ]
    } else {
        return [
            {
                key: 'campuses',
                label: '校区数量',
                value: '8',
                icon: Trophy,
                gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                trend: 'up',
                trendText: '+1 本季度'
            },
            {
                key: 'users',
                label: '用户总数',
                value: '1250',
                icon: UserFilled,
                gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                trend: 'up',
                trendText: '+85 本月'
            },
            {
                key: 'revenue',
                label: '总营收',
                value: '¥280K',
                icon: TrendCharts,
                gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                trend: 'up',
                trendText: '+22% 环比'
            },
            {
                key: 'growth',
                label: '增长率',
                value: '+15%',
                icon: Star,
                gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                trend: 'up',
                trendText: '超预期'
            }
        ]
    }
})

// 今日课程安排
const todaySchedule = ref([
    {
        id: 1,
        time: '09:00',
        title: '基础训练课',
        description: '基础技能训练，包括正手、反手练习',
        type: '训练课',
        location: '训练室A',
        status: 'upcoming'
    },
    {
        id: 2,
        time: '14:00',
        title: '一对一指导',
        description: '个人技术指导和战术分析',
        type: '指导课',
        location: '训练室B',
        status: 'completed'
    },
    {
        id: 3,
        time: '16:30',
        title: '模拟比赛',
        description: '实战演练，提升比赛技巧',
        type: '比赛',
        location: '比赛区',
        status: 'upcoming'
    }
])

// 最新活动
const recentActivities = ref([
    {
        id: 1,
        text: '张教练为您的训练课进行了点评',
        avatar: '',
        icon: Star,
        createdAt: dayjs().subtract(5, 'minute').toDate(),
        actionable: true
    },
    {
        id: 2,
        text: '您的课程《基础训练》已安排在明天上午',
        avatar: '',
        icon: Calendar,
        createdAt: dayjs().subtract(30, 'minute').toDate(),
        actionable: true
    },
    {
        id: 3,
        text: '系统更新：新增了训练视频功能',
        avatar: '',
        icon: Bell,
        createdAt: dayjs().subtract(2, 'hour').toDate(),
        actionable: false
    },
    {
        id: 4,
        text: '李同学完成了本月训练目标',
        avatar: '',
        icon: Trophy,
        createdAt: dayjs().subtract(3, 'hour').toDate(),
        actionable: false
    }
])

// 天气信息
const weather = ref({
    temperature: 22,
    description: '晴朗',
    icon: '☀️',
    humidity: 65,
    windSpeed: 8,
    suggestion: '天气很棒，适合户外训练！'
})

// 快捷操作
const quickActions = computed(() => {
    const role = userInfo.value.role

    if (role === 'student') {
        return [
            { key: 'booking', label: '预约课程', type: 'primary', icon: Calendar, handler: () => router.push('/courses') },
            { key: 'evaluation', label: '课程评价', type: 'success', icon: Star, handler: () => router.push('/evaluations') },
            { key: 'profile', label: '个人资料', type: 'info', icon: User, handler: () => router.push('/profile') },
            { key: 'messages', label: '消息中心', type: 'warning', icon: Message, handler: () => router.push('/messages') }
        ]
    } else if (role === 'coach') {
        return [
            { key: 'schedule', label: '课程安排', type: 'primary', icon: Calendar, handler: () => router.push('/coach/schedule') },
            { key: 'students', label: '学员管理', type: 'success', icon: UserFilled, handler: () => router.push('/coach/students') },
            { key: 'evaluations', label: '评价查看', type: 'info', icon: Star, handler: () => router.push('/coach/evaluations') },
            { key: 'reports', label: '教学报告', type: 'warning', icon: Document, handler: () => router.push('/coach/reports') }
        ]
    } else if (role === 'campus_admin') {
        return [
            { key: 'coaches', label: '教练管理', type: 'primary', icon: UserFilled, handler: () => router.push('/admin/coaches') },
            { key: 'courses', label: '课程管理', type: 'success', icon: Calendar, handler: () => router.push('/admin/courses') },
            { key: 'finance', label: '财务管理', type: 'info', icon: TrendCharts, handler: () => router.push('/admin/finance') },
            { key: 'reports', label: '数据报表', type: 'warning', icon: Document, handler: () => router.push('/admin/reports') }
        ]
    } else {
        return [
            { key: 'campuses', label: '校区管理', type: 'primary', icon: Trophy, handler: () => router.push('/super/campuses') },
            { key: 'users', label: '用户管理', type: 'success', icon: UserFilled, handler: () => router.push('/super/users') },
            { key: 'system', label: '系统设置', type: 'info', icon: Setting, handler: () => router.push('/super/settings') },
            { key: 'analytics', label: '数据分析', type: 'warning', icon: TrendCharts, handler: () => router.push('/super/analytics') }
        ]
    }
})

// 本周统计
const weeklyStats = ref([
    { key: 'training', label: '训练完成', value: '12/15', percentage: 80, color: '#67c23a' },
    { key: 'attendance', label: '出勤率', value: '95%', percentage: 95, color: '#409eff' },
    { key: 'progress', label: '技能进步', value: '85%', percentage: 85, color: '#e6a23c' },
    { key: 'satisfaction', label: '满意度', value: '92%', percentage: 92, color: '#f56c6c' }
])

// 最新成就
const recentAchievements = ref([
    { id: 1, icon: '🏆', title: '连续训练7天', date: dayjs().subtract(1, 'day').toDate() },
    { id: 2, icon: '⭐', title: '技能等级提升', date: dayjs().subtract(3, 'day').toDate() },
    { id: 3, icon: '🎯', title: '完成月度目标', date: dayjs().subtract(1, 'week').toDate() }
])

// 工具方法
const getScheduleType = (type) => {
    const typeMap = {
        '训练课': 'primary',
        '指导课': 'success',
        '比赛': 'warning'
    }
    return typeMap[type] || 'info'
}

const getStatusType = (status) => {
    const statusMap = {
        'completed': 'success',
        'upcoming': 'warning',
        'cancelled': 'danger'
    }
    return statusMap[status] || 'info'
}

const getStatusText = (status) => {
    const textMap = {
        'completed': '已完成',
        'upcoming': '即将开始',
        'cancelled': '已取消'
    }
    return textMap[status] || status
}

const getActionText = (type) => {
    const actionMap = {
        '训练课': '开始训练',
        '指导课': '进入指导',
        '比赛': '进入比赛'
    }
    return actionMap[type] || '查看详情'
}

// 时间格式化
const formatTime = (date) => {
    return dayjs(date).format('MM-DD HH:mm')
}

const formatDate = (date) => {
    return dayjs(date).format('MM月DD日')
}

// 事件处理
const goToSchedule = () => {
    router.push('/schedule')
}

const goToMessages = () => {
    router.push('/messages')
}

const createSchedule = () => {
    router.push('/schedule/create')
}

const handleScheduleAction = (item) => {
    console.log('处理课程行动:', item)
}

const handleActivity = (activity) => {
    console.log('处理活动:', activity)
}

onMounted(() => {
    // 可以在这里加载更多数据
})
</script>

<style scoped>
/* 背景装饰元素 */
.background-decorations {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 0;
    overflow: hidden;
}

.floating-element {
    position: absolute;
    font-size: 2rem;
    opacity: 0.1;
    animation: float 6s ease-in-out infinite;
}

.ping-pong-1 {
    top: 10%;
    left: 10%;
    animation-delay: 0s;
}

.ping-pong-2 {
    top: 30%;
    right: 15%;
    animation-delay: 2s;
}

.ping-pong-3 {
    bottom: 20%;
    left: 20%;
    animation-delay: 4s;
}

.trophy-1 {
    top: 50%;
    right: 10%;
    animation-delay: 1s;
}

.star-1 {
    top: 20%;
    left: 50%;
    animation-delay: 3s;
}

.star-2 {
    bottom: 30%;
    right: 30%;
    animation-delay: 5s;
}

.geometric-shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.05;
    background: linear-gradient(45deg, #667eea, #764ba2);
    animation: rotate 20s linear infinite;
}

.shape-1 {
    width: 200px;
    height: 200px;
    top: 15%;
    right: 20%;
}

.shape-2 {
    width: 150px;
    height: 150px;
    bottom: 25%;
    left: 15%;
    animation-delay: 5s;
}

.shape-3 {
    width: 100px;
    height: 100px;
    top: 60%;
    left: 45%;
    animation-delay: 10s;
}

.artistic-text {
    position: absolute;
    font-weight: bold;
    opacity: 0.03;
    pointer-events: none;
    color: #333;
}

.main-title {
    font-size: 8rem;
    top: 20%;
    left: 50%;
    transform: translateX(-50%);
}

.sub-title {
    font-size: 3rem;
    top: 35%;
    left: 50%;
    transform: translateX(-50%);
}

/* 动画 */
@keyframes float {

    0%,
    100% {
        transform: translateY(0px);
    }

    50% {
        transform: translateY(-20px);
    }
}

@keyframes rotate {
    from {
        transform: rotate(0deg);
    }

    to {
        transform: rotate(360deg);
    }
}

/* 主要内容 */
.dashboard {
    position: relative;
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px;
    z-index: 1;
}

/* 欢迎横幅 */
.welcome-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 20px;
    padding: 40px;
    color: white;
    margin-bottom: 30px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
    backdrop-filter: blur(10px);
    position: relative;
    overflow: hidden;
}

.welcome-banner::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
    pointer-events: none;
}

.banner-left {
    flex: 1;
    z-index: 1;
}

.welcome-title {
    font-size: 32px;
    font-weight: 700;
    margin: 0 0 12px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-subtitle {
    font-size: 18px;
    opacity: 0.9;
    margin: 0 0 20px 0;
}

.banner-stats {
    display: flex;
    gap: 30px;
}

.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stat-value {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 4px;
}

.stat-label {
    font-size: 14px;
    opacity: 0.8;
}

.banner-right {
    z-index: 1;
}

.avatar-container {
    position: relative;
    text-align: center;
}

.user-avatar {
    border: 4px solid rgba(255, 255, 255, 0.3);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.avatar-badge {
    margin-top: 10px;
}

/* 统计卡片 */
.stats-row {
    margin-bottom: 30px;
}

.stat-card {
    border: none;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    backdrop-filter: blur(10px);
    background: rgba(255, 255, 255, 0.9);
}

.stat-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-content {
    display: flex;
    align-items: center;
    gap: 16px;
}

.stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 24px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.stat-info {
    flex: 1;
}

.stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #333;
    line-height: 1;
    margin-bottom: 4px;
}

.stat-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
}

.stat-trend {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: #999;
}

.stat-trend.up {
    color: #67c23a;
}

.stat-trend.down {
    color: #f56c6c;
}

/* 主要内容区域 */
.main-content {
    margin-bottom: 30px;
}

.content-card,
.sidebar-card {
    border: none;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;
    backdrop-filter: blur(10px);
    background: rgba(255, 255, 255, 0.9);
}

.card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-weight: 600;
    color: #333;
}

.header-left {
    display: flex;
    align-items: center;
    gap: 8px;
}

.header-icon {
    color: #667eea;
}

/* 时间线 */
.schedule-timeline {
    max-height: 400px;
    overflow-y: auto;
}

.timeline-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 0;
    border-bottom: 1px solid #f0f0f0;
}

.timeline-item:last-child {
    border-bottom: none;
}

.timeline-time {
    width: 60px;
    font-size: 14px;
    font-weight: 600;
    color: #667eea;
    text-align: center;
}

.timeline-content {
    flex: 1;
}

.timeline-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 4px;
}

.timeline-description {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
}

.timeline-meta {
    display: flex;
    align-items: center;
    gap: 12px;
}

.timeline-location {
    font-size: 12px;
    color: #999;
}

/* 活动动态 */
.activity-feed {
    max-height: 400px;
    overflow-y: auto;
}

.activity-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
    border-bottom: none;
}

.activity-content {
    flex: 1;
}

.activity-text {
    font-size: 14px;
    color: #333;
    margin-bottom: 4px;
}

.activity-time {
    font-size: 12px;
    color: #999;
}

/* 天气卡片 */
.weather-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.weather-temp {
    font-size: 24px;
    font-weight: 600;
    color: #333;
}

.weather-desc {
    font-size: 14px;
    color: #666;
}

.weather-icon {
    font-size: 32px;
}

.weather-details {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
}

.weather-detail {
    display: flex;
    flex-direction: column;
    align-items: center;
    font-size: 12px;
    color: #666;
}

/* 快捷操作 */
.quick-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.action-button {
    justify-content: flex-start;
    text-align: left;
    height: 48px;
    border-radius: 12px;
    transition: all 0.3s ease;
}

.action-button:hover {
    transform: translateX(4px);
}

/* 本周统计 */
.weekly-stats {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.weekly-stat-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.weekly-stat-label {
    font-size: 14px;
    color: #666;
}

.weekly-stat-value {
    font-size: 16px;
    font-weight: 600;
    color: #333;
}

.weekly-stat-progress {
    width: 60px;
    margin-left: 12px;
}

/* 成就徽章 */
.achievements {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.achievement-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: background 0.3s ease;
}

.achievement-item:hover {
    background: #e9ecef;
}

.achievement-icon {
    font-size: 24px;
}

.achievement-title {
    font-size: 14px;
    font-weight: 600;
    color: #333;
}

.achievement-date {
    font-size: 12px;
    color: #999;
}

/* 空状态 */
.empty-schedule {
    text-align: center;
    padding: 40px 20px;
}

.empty-icon {
    font-size: 48px;
    margin-bottom: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
    .welcome-banner {
        flex-direction: column;
        text-align: center;
        gap: 20px;
        padding: 30px 20px;
    }

    .banner-stats {
        justify-content: center;
    }

    .welcome-title {
        font-size: 24px;
    }

    .main-title {
        font-size: 4rem;
    }

    .sub-title {
        font-size: 1.5rem;
    }

    .floating-element {
        font-size: 1.5rem;
    }

    .timeline-item {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }

    .timeline-time {
        width: auto;
        text-align: left;
    }
}
</style>
