<template>
  <div class="design-language-showcase">
    <!-- 页面头部 -->
    <div class="design-language-hero">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="badge-icon">🏓</span>
          <span>教练工作台</span>
        </div>
        <h1 class="hero-title">我的教学课表</h1>
        <p class="hero-description">
          智能管理您的教学安排，轻松掌控每一堂训练课程
          <br>基于现代化设计语言的课程管理平台
        </p>
        <div class="hero-stats">
          <div class="stat-item">
            <div class="stat-number">{{ todayCourses }}</div>
            <div class="stat-label">今日课程</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ weekCourses }}</div>
            <div class="stat-label">本周课程</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalStudents }}</div>
            <div class="stat-label">学生总数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ currentView === 'week' ? '周' : '月' }}</div>
            <div class="stat-label">当前视图</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 组件导航 -->
    <div class="component-navigation">
      <h2 class="section-title">🎛️ 课表控制面板</h2>
      <div class="nav-grid">
        <div class="nav-card" @click="setView('week')" :class="{ active: currentView === 'week' }">
          <div class="nav-icon">📊</div>
          <h3>周视图</h3>
          <p>查看详细的周课程安排</p>
        </div>
        
        <div class="nav-card" @click="setView('month')" :class="{ active: currentView === 'month' }">
          <div class="nav-icon">📅</div>
          <h3>月视图</h3>
          <p>查看月度课程概览</p>
        </div>
        
        <div class="nav-card" @click="goToToday">
          <div class="nav-icon">🎯</div>
          <h3>今天</h3>
          <p>快速跳转到今日课程</p>
        </div>

        <div class="nav-card" @click="addCourse">
          <div class="nav-icon">➕</div>
          <h3>添加课程</h3>
          <p>创建新的教学安排</p>
        </div>
        <div class="nav-card" @click="refreshSchedule">
          <div class="nav-icon">🔄</div>
          <h3>刷新课表</h3>
          <p>获取最新课程信息</p>
        </div>

        <div class="nav-card" @click="exportSchedule">
          <div class="nav-icon">📤</div>
          <h3>导出课表</h3>
          <p>导出课表文件</p>
        </div>

        <div class="nav-card" @click="sendScheduleEmail">
          <div class="nav-icon">📧</div>
          <h3>邮件发送</h3>
          <p>发送课表给学员</p>
        </div>
      </div>
      
      <!-- 日期导航区域 -->
      <div class="date-control-section">
        <div class="date-navigation-glass">
          <button class="nav-btn-glass" @click="previousPeriod">
            <span class="btn-icon">◀</span>
          </button>
          <div class="current-period-glass">{{ currentPeriodText }}</div>
          <button class="nav-btn-glass" @click="nextPeriod">
            <span class="btn-icon">▶</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 周视图 -->
    <div v-if="currentView === 'week'" class="component-showcase">
      <h2 class="section-title">📊 周视图课表</h2>
      <p class="section-description">
        查看本周的详细教学安排，基于现代化设计语言的课程展示
      </p>
      
      <div class="showcase-grid">
        <div class="content-card week-schedule-container">
          <div class="week-grid">
          <!-- 时间列头部 -->
          <div class="grid-header">
            <div class="time-header">时间</div>
            <div
              v-for="day in weekDays"
              :key="day.date"
              class="day-header"
              :class="{ 'is-today': day.isToday }"
            >
              <div class="day-name">{{ day.name }}</div>
              <div class="day-date">{{ day.date }}</div>
            </div>
          </div>
          
          <!-- 课表网格 -->
          <div class="grid-body">
            <div v-for="hour in timeSlots" :key="hour" class="time-row">
              <div class="time-cell">{{ hour }}</div>
              <div
                v-for="day in weekDays"
                :key="`${day.date}-${hour}`"
                class="schedule-cell"
                @click="handleCellClick(day.date, hour)"
              >
                <div
                  v-for="schedule in getSchedulesForDayAndHour(day.date, hour)"
                  :key="schedule.id"
                  class="course-card"
                  :class="getCourseCardClass(schedule)"
                  @click.stop="handleScheduleClick(schedule)"
                >
                  <div class="course-header">
                    <span class="course-type-icon">{{ getCourseIcon(schedule.type) }}</span>
                    <span class="course-title">{{ schedule.title }}</span>
                  </div>
                  <div class="course-details">
                    <div class="course-student">
                      <span class="detail-icon">👤</span>
                      {{ studentNames(schedule.students) }}
                    </div>
                    <div class="course-location">
                      <span class="detail-icon">📍</span>
                      {{ schedule.location }}
                    </div>
                  </div>
                  <div class="course-status">
                    <span class="status-badge" :class="schedule.status">
                      {{ getStatusText(schedule.status) }}
                    </span>
                  </div>
                </div>
                <div v-if="getSchedulesForDayAndHour(day.date, hour).length === 0" class="empty-slot">
                  <button class="add-course-btn" @click="handleCellClick(day.date, hour)">
                    <span class="btn-icon">➕</span>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 月视图 -->
    <div v-if="currentView === 'month'" class="component-showcase">
      <h2 class="section-title">📅 月视图课表</h2>
      <p class="section-description">
        查看本月的完整教学概览，一目了然的月度课程安排
      </p>
      
      <div class="showcase-grid">
        <div class="content-card month-schedule-container">
          <div class="month-grid">
          <!-- 星期头部 -->
          <div class="month-header">
            <div v-for="dayName in weekDayNames" :key="dayName" class="weekday-header">
              {{ dayName }}
            </div>
          </div>
          
          <!-- 月份日历 -->
          <div class="month-body">
            <div v-for="week in monthWeeks" :key="week[0]?.date || Math.random()" class="month-week">
              <div
                v-for="day in week"
                :key="day?.date || Math.random()"
                class="month-day"
                :class="{
                  'is-today': day?.isToday,
                  'is-other-month': day?.isOtherMonth,
                  'has-courses': day && getSchedulesForDay(day.date).length > 0,
                }"
                @click="handleDayClick(day)"
              >
                <div v-if="day" class="day-container">
                  <div class="day-number">{{ day.dayNumber }}</div>
                  <div class="day-courses">
                    <div
                      v-for="schedule in getSchedulesForDay(day.date).slice(0, 2)"
                      :key="schedule.id"
                      class="course-indicator"
                      :class="getCourseIndicatorClass(schedule)"
                      @click.stop="handleScheduleClick(schedule)"
                      :title="`${schedule.title} - ${studentNames(schedule.students)}`"
                    >
                      <span class="indicator-dot">●</span>
                      <span class="indicator-text">{{ schedule.title }}</span>
                    </div>
                    <div v-if="getSchedulesForDay(day.date).length > 2" class="more-courses">
                      +{{ getSchedulesForDay(day.date).length - 2 }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        </div>
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <div v-if="showScheduleDialog" class="schedule-dialog-overlay" @click="closeScheduleDialog">
      <div class="schedule-dialog" @click.stop>
        <div class="dialog-header">
          <h3 class="dialog-title">
            <span class="title-icon">📋</span>
            课程详情
          </h3>
          <button class="close-btn" @click="closeScheduleDialog">
            <span class="btn-icon">✖</span>
          </button>
        </div>
        
        <div v-if="selectedSchedule" class="dialog-content">
          <div class="course-info-card">
            <div class="info-header">
              <div class="course-type-badge" :class="selectedSchedule.type">
                {{ getCourseIcon(selectedSchedule.type) }}
              </div>
              <div class="course-title-section">
                <h4 class="course-name">{{ selectedSchedule.title }}</h4>
                <p class="course-subtitle">{{ getScheduleTypeName(selectedSchedule.type) }}</p>
              </div>
              <div class="status-badge" :class="selectedSchedule.status">
                {{ getStatusText(selectedSchedule.status) }}
              </div>
            </div>
            
            <div class="info-grid">
              <div class="info-item">
                <span class="info-icon">👤</span>
                <div class="info-content">
                  <span class="info-label">学生</span>
                  <span class="info-value">{{ studentNames(selectedSchedule.students) }}</span>
                </div>
              </div>
              
              <div class="info-item">
                <span class="info-icon">⏰</span>
                <div class="info-content">
                  <span class="info-label">时间</span>
                  <span class="info-value">{{ formatScheduleTime(selectedSchedule) }}</span>
                </div>
              </div>
              
              <div class="info-item">
                <span class="info-icon">📍</span>
                <div class="info-content">
                  <span class="info-label">地点</span>
                  <span class="info-value">{{ selectedSchedule.location }}</span>
                </div>
              </div>
              
              <div v-if="selectedSchedule.description" class="info-item full-width">
                <span class="info-icon">📝</span>
                <div class="info-content">
                  <span class="info-label">课程描述</span>
                  <span class="info-value">{{ selectedSchedule.description }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="dialog-actions">
            <button class="action-btn secondary" @click="closeScheduleDialog">
              <span class="btn-icon">✖</span>
              关闭
            </button>
            <button 
              v-if="selectedSchedule?.status === 'confirmed' && canCancelSchedule(selectedSchedule)"
              class="action-btn danger"
              @click="cancelSchedule"
            >
              <span class="btn-icon">❌</span>
              取消课程
            </button>
            <button class="action-btn primary" @click="editSchedule">
              <span class="btn-icon">✏️</span>
              编辑课程
            </button>
          </div>
        </div>
      </div>
    </div>

  <!-- 邮件发送对话框 -->
  <el-dialog
    v-model="showEmailDialog"
    title="📧 发送课表邮件"
    width="600px"
    :before-close="() => showEmailDialog = false"
  >
    <el-form :model="emailOptions" label-width="120px">
      <el-form-item label="收件人邮箱" required>
        <div class="email-recipients">
          <div 
            v-for="(email, index) in emailOptions.recipients" 
            :key="index"
            class="recipient-item"
          >
            <el-input
              v-model="emailOptions.recipients[index]"
              placeholder="请输入邮箱地址"
              type="email"
            />
            <el-button
              type="danger"
              text
              @click="removeEmailRecipient(index)"
              style="margin-left: 8px;"
            >
              删除
            </el-button>
          </div>
          <el-button
            type="primary"
            text
            @click="addEmailRecipient"
            class="add-recipient-btn"
          >
            + 添加收件人
          </el-button>
        </div>
      </el-form-item>
      
      <el-form-item label="附件选项">
        <el-checkbox v-model="emailOptions.includeAttachment">
          包含 iCal 课表文件
        </el-checkbox>
      </el-form-item>
      
      <el-form-item label="提醒设置">
        <el-checkbox v-model="emailOptions.sendReminder">
          启用课程提醒邮件
        </el-checkbox>
      </el-form-item>
      
      <el-form-item label="邮件预览">
        <div class="email-preview">
          <p><strong>主题：</strong>您的乒乓球训练课表 - {{ dayjs().format('YYYY年MM月DD日') }}</p>
          <p><strong>内容：</strong>包含完整的训练课表安排和日历文件</p>
          <p><strong>收件人数：</strong>{{ emailOptions.recipients.filter(email => email.trim()).length }} 人</p>
        </div>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showEmailDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleEmailSend"
          :disabled="emailOptions.recipients.filter(email => email.trim()).length === 0"
        >
          发送邮件
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 同步设置对话框 -->
  <el-dialog
    v-model="showSyncDialog"
    title="⚙️ 同步设置"
    width="500px"
    :before-close="() => showSyncDialog = false"
  >
    <el-form :model="syncOptions" label-width="120px">
      <el-form-item label="同步平台">
        <el-select v-model="syncOptions.platform" style="width: 100%">
          <el-option label="Google Calendar" value="google" />
          <el-option label="Outlook Calendar" value="outlook" />
          <el-option label="Apple Calendar" value="apple" />
        </el-select>
      </el-form-item>
      
      <el-form-item label="提醒设置">
        <el-checkbox v-model="syncOptions.enableReminder">
          启用课程提醒
        </el-checkbox>
      </el-form-item>
      
      <el-form-item label="同步频率">
        <el-select v-model="syncOptions.syncFrequency" style="width: 100%">
          <el-option label="实时同步" value="realtime" />
          <el-option label="每小时" value="hourly" />
          <el-option label="每日同步" value="daily" />
          <el-option label="手动同步" value="manual" />
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showSyncDialog = false">取消</el-button>
        <el-button type="primary" @click="showSyncDialog = false">
          保存设置
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 课程管理对话框 -->
  <el-dialog
    v-model="showCourseManagementDialog"
    title="📋 课程管理"
    width="90%"
    :before-close="() => showCourseManagementDialog = false"
  >
    <div class="course-management-panel">
      <!-- 操作工具栏 -->
      <div class="management-toolbar">
        <div class="toolbar-left">
          <el-button @click="openCreateDialog" type="primary">
            ➕ 创建课程
          </el-button>
          <el-select v-model="courseFilter.status" placeholder="课程状态" clearable @change="filterCourses">
            <el-option label="全部" value="" />
            <el-option label="已预约" value="scheduled" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
          <el-select v-model="courseFilter.type" placeholder="课程类型" clearable @change="filterCourses" style="margin-left: 10px;">
            <el-option label="全部" value="" />
            <el-option label="一对一" value="individual" />
            <el-option label="小组课" value="group" />
          </el-select>
          <el-date-picker
            v-model="courseFilter.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="filterCourses"
            style="margin-left: 10px;"
          />
        </div>
        <div class="toolbar-right">
          <el-button @click="refreshCourses" :loading="courseListLoading">
            🔄 刷新
          </el-button>
        </div>
      </div>

      <!-- 课程列表 -->
      <el-table 
        :data="filteredCourses" 
        v-loading="courseListLoading"
        style="width: 100%; margin-top: 20px;"
        @row-click="viewCourseDetail"
      >
        <el-table-column prop="date" label="日期" width="120">
          <template #default="{ row }">
            <el-tag :type="isToday(row.date) ? 'success' : 'info'" size="small">
              {{ formatDate(row.date) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="时间" width="140">
          <template #default="{ row }">
            <div class="time-range">
              <span>{{ row.startTime }} - {{ row.endTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="title" label="课程名称" min-width="150" />

        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'individual' ? 'warning' : 'success'" size="small">
              {{ row.type === 'individual' ? '一对一' : '小组课' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="学员" width="100">
          <template #default="{ row }">
            <span>{{ row.currentStudents }}/{{ row.maxStudents || 1 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="地点" width="120" />

        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span>¥{{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              @click.stop="viewCourseDetail(row)"
            >
              详情
            </el-button>
            <el-button 
              v-if="canEdit(row)"
              size="small" 
              @click.stop="openEditDialog(row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="canDelete(row)"
              size="small" 
              type="danger" 
              @click.stop="deleteCourse(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="coursePagination.page"
          v-model:page-size="coursePagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="coursePagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadCoachCourses"
          @current-change="loadCoachCourses"
        />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showCourseManagementDialog = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 课程详情对话框 -->
  <el-dialog 
    v-model="showCourseDetailDialog" 
    title="课程详情" 
    width="70%"
    @close="selectedCourseDetail = null"
  >
    <div v-if="selectedCourseDetail" class="course-detail">
      <div class="detail-header">
        <h3>{{ selectedCourseDetail.title }}</h3>
        <div class="course-badges">
          <el-tag :type="selectedCourseDetail.type === 'individual' ? 'warning' : 'success'">
            {{ selectedCourseDetail.type === 'individual' ? '一对一课程' : '小组课程' }}
          </el-tag>
          <el-tag :type="getStatusType(selectedCourseDetail.status)">
            {{ getStatusText(selectedCourseDetail.status) }}
          </el-tag>
        </div>
      </div>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="日期时间">
              {{ selectedCourseDetail.date }} {{ selectedCourseDetail.startTime }} - {{ selectedCourseDetail.endTime }}
            </el-descriptions-item>
            <el-descriptions-item label="课程时长">
              {{ selectedCourseDetail.duration }} 分钟
            </el-descriptions-item>
            <el-descriptions-item label="上课地点">
              {{ selectedCourseDetail.location }}
            </el-descriptions-item>
            <el-descriptions-item label="课程等级">
              {{ getLevelText(selectedCourseDetail.level) }}
            </el-descriptions-item>
            <el-descriptions-item label="课程价格">
              ¥{{ selectedCourseDetail.price }}
            </el-descriptions-item>
            <el-descriptions-item label="学员人数">
              {{ selectedCourseDetail.currentStudents }}/{{ selectedCourseDetail.maxStudents || 1 }}
            </el-descriptions-item>
          </el-descriptions>
        </el-col>

        <el-col :span="12">
          <div class="student-list">
            <h4>报名学员</h4>
            <el-table :data="selectedCourseDetail.studentList || []" style="width: 100%">
              <el-table-column prop="studentName" label="学员姓名" />
              <el-table-column prop="enrollmentStatus" label="报名状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getEnrollmentStatusType(row.enrollmentStatus)" size="small">
                    {{ getEnrollmentStatusText(row.enrollmentStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="attendanceStatus" label="出勤" width="80">
                <template #default="{ row }">
                  <el-tag 
                    v-if="row.attendanceStatus"
                    :type="getAttendanceStatusType(row.attendanceStatus)" 
                    size="small"
                  >
                    {{ getAttendanceStatusText(row.attendanceStatus) }}
                  </el-tag>
                  <span v-else>-</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>

      <div v-if="selectedCourseDetail.description" class="description-section">
        <h4>课程描述</h4>
        <p>{{ selectedCourseDetail.description }}</p>
      </div>

      <div v-if="selectedCourseDetail.notes" class="notes-section">
        <h4>教练备注</h4>
        <p>{{ selectedCourseDetail.notes }}</p>
      </div>

      <div v-if="selectedCourseDetail.objectives && selectedCourseDetail.objectives.length" class="objectives-section">
        <h4>训练目标</h4>
        <el-tag 
          v-for="objective in selectedCourseDetail.objectives" 
          :key="objective"
          style="margin: 0 5px 5px 0;"
        >
          {{ objective }}
        </el-tag>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showCourseDetailDialog = false">关闭</el-button>
        <el-button 
          v-if="selectedCourseDetail && canEdit(selectedCourseDetail)"
          @click="openEditDialog(selectedCourseDetail)"
        >
          编辑课程
        </el-button>
        <el-button 
          v-if="selectedCourseDetail && canManageAttendance(selectedCourseDetail)"
          type="primary"
          @click="openAttendanceDialog(selectedCourseDetail)"
        >
          出勤管理
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 创建/编辑课程对话框 -->
  <el-dialog 
    v-model="showFormDialog" 
    :title="isEdit ? '编辑课程' : '创建课程'" 
    width="60%"
  >
    <el-form 
      :model="courseForm" 
      :rules="courseRules" 
      ref="courseFormRef"
      label-width="100px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="课程名称" prop="title">
            <el-input v-model="courseForm.title" placeholder="请输入课程名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程类型" prop="type">
            <el-select v-model="courseForm.type" placeholder="选择课程类型" style="width: 100%">
              <el-option label="一对一" value="individual" />
              <el-option label="小组课" value="group" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="课程日期" prop="date">
            <el-date-picker
              v-model="courseForm.date"
              type="date"
              placeholder="选择日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="开始时间" prop="startTime">
            <el-time-select
              v-model="courseForm.startTime"
              start="06:00"
              step="00:30"
              end="22:00"
              placeholder="开始时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="结束时间" prop="endTime">
            <el-time-select
              v-model="courseForm.endTime"
              start="06:30"
              step="00:30"
              end="22:30"
              placeholder="结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="课程等级" prop="level">
            <el-select v-model="courseForm.level" placeholder="选择等级" style="width: 100%">
              <el-option label="初级" value="beginner" />
              <el-option label="中级" value="intermediate" />
              <el-option label="高级" value="advanced" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="上课地点" prop="location">
            <el-input v-model="courseForm.location" placeholder="请输入地点" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="课程价格" prop="price">
            <el-input-number 
              v-model="courseForm.price" 
              :min="0" 
              :step="10"
              style="width: 100%" 
              placeholder="课程价格"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20" v-if="courseForm.type === 'group'">
        <el-col :span="12">
          <el-form-item label="最大人数" prop="maxStudents">
            <el-input-number 
              v-model="courseForm.maxStudents" 
              :min="2" 
              :max="10"
              style="width: 100%" 
              placeholder="最大学员数"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="校区" prop="campusId">
            <el-select v-model="courseForm.campusId" placeholder="选择校区" style="width: 100%">
              <el-option 
                v-for="campus in campusList" 
                :key="campus.id" 
                :label="campus.name" 
                :value="campus.id" 
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="课程描述">
        <el-input 
          v-model="courseForm.description" 
          type="textarea" 
          :rows="3"
          placeholder="课程描述（可选）"
        />
      </el-form-item>

      <el-form-item label="教练备注">
        <el-input 
          v-model="courseForm.notes" 
          type="textarea" 
          :rows="2"
          placeholder="教练备注（可选）"
        />
      </el-form-item>

      <el-form-item label="训练目标">
        <el-select
          v-model="courseForm.objectives"
          multiple
          filterable
          allow-create
          placeholder="添加训练目标（可选）"
          style="width: 100%"
        >
          <el-option label="基础技术" value="基础技术" />
          <el-option label="发球技巧" value="发球技巧" />
          <el-option label="接发球" value="接发球" />
          <el-option label="正手攻球" value="正手攻球" />
          <el-option label="反手技术" value="反手技术" />
          <el-option label="步法训练" value="步法训练" />
          <el-option label="战术配合" value="战术配合" />
          <el-option label="比赛技巧" value="比赛技巧" />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeFormDialog">取消</el-button>
        <el-button type="primary" @click="submitCourse" :loading="formLoading">
          {{ isEdit ? '保存修改' : '创建课程' }}
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 出勤管理对话框 -->
  <el-dialog v-model="showAttendanceDialog" title="出勤管理" width="50%">
    <div v-if="attendanceCourse" class="attendance-management">
      <div class="course-info">
        <h4>{{ attendanceCourse.title }}</h4>
        <p>{{ attendanceCourse.date }} {{ attendanceCourse.startTime }} - {{ attendanceCourse.endTime }}</p>
      </div>

      <el-table :data="attendanceList" style="width: 100%">
        <el-table-column prop="studentName" label="学员姓名" />
        <el-table-column label="出勤状态" width="150">
          <template #default="{ row, $index }">
            <el-select 
              v-model="row.status" 
              placeholder="选择状态"
              size="small"
              style="width: 100%"
            >
              <el-option label="出席" value="present" />
              <el-option label="缺席" value="absent" />
              <el-option label="迟到" value="late" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="备注">
          <template #default="{ row }">
            <el-input 
              v-model="row.notes" 
              placeholder="备注信息"
              size="small"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showAttendanceDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAttendance" :loading="attendanceLoading">
          保存出勤记录
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElDialog, ElButton, ElSelect, ElOption, ElForm, ElFormItem, ElInput } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import api from '@/utils/api'
import PrimaryButton from '@/components/buttons/PrimaryButton.vue'
import OutlineButton from '@/components/buttons/OutlineButton.vue'
import DangerButton from '@/components/buttons/DangerButton.vue'
import { useScheduleSync } from '@/utils/scheduleSyncExamples'

// 响应式数据
const currentView = ref('week')
const currentDate = ref(dayjs())
const schedules = ref([])
const loading = ref(false)
const showScheduleDialog = ref(false)
const selectedSchedule = ref(null)

// 同步功能相关数据
const isSyncing = ref(false)
const showSyncDialog = ref(false)
const showEmailDialog = ref(false)
const syncOptions = ref({
  platform: 'google',
  enableReminder: true,
  syncFrequency: 'hourly'
})
const emailOptions = ref({
  recipients: [],
  includeAttachment: true,
  sendReminder: false
})

// 初始化同步功能
const scheduleSync = useScheduleSync()

// 统计数据
const todayCourses = computed(() => {
  const today = dayjs().format('YYYY-MM-DD')
  return schedules.value.filter(schedule => 
    dayjs(schedule.startTime).format('YYYY-MM-DD') === today
  ).length
})

const weekCourses = computed(() => {
  const weekStart = dayjs().startOf('week').add(1, 'day')
  const weekEnd = weekStart.add(6, 'day')
  return schedules.value.filter(schedule => {
    const scheduleDate = dayjs(schedule.startTime)
    return scheduleDate.isAfter(weekStart.subtract(1, 'day')) && 
           scheduleDate.isBefore(weekEnd.add(1, 'day'))
  }).length
})

const totalStudents = computed(() => {
  const studentSet = new Set()
  schedules.value.forEach(schedule => {
    if (schedule.students) {
      schedule.students.forEach(student => {
        studentSet.add(student.id || student)
      })
    }
  })
  return studentSet.size
})

// 时间段设置（8:00-22:00）
const timeSlots = [
  '08:00',
  '09:00',
  '10:00',
  '11:00',
  '12:00',
  '13:00',
  '14:00',
  '15:00',
  '16:00',
  '17:00',
  '18:00',
  '19:00',
  '20:00',
  '21:00',
]

// 顺序改为周一到周日
const weekDayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 计算属性
const currentPeriodText = computed(() => {
  if (currentView.value === 'week') {
    const weekStart = currentDate.value.startOf('week').add(1, 'day')
    const weekEnd = weekStart.add(6, 'day')
    return `${weekStart.format('YYYY年MM月DD日')} - ${weekEnd.format('MM月DD日')}`
  } else {
    return currentDate.value.format('YYYY年MM月')
  }
})

// 以周一开始
const weekDays = computed(() => {
  let weekStart = currentDate.value.startOf('week').add(1, 'day')
  const days = []
  for (let i = 0; i < 7; i++) {
    const day = weekStart.add(i, 'day')
    days.push({
      date: day.format('YYYY-MM-DD'),
      name: weekDayNames[i],
      dayNumber: day.date(),
      isToday: day.isSame(dayjs(), 'day'),
    })
  }
  return days
})

const monthWeeks = computed(() => {
  const monthStart = currentDate.value.startOf('month')
  const monthEnd = currentDate.value.endOf('month')
  const calendarStart = monthStart.startOf('week').add(1, 'day')
  const calendarEnd = monthEnd.endOf('week').add(1, 'day')
  const weeks = []
  let currentWeekStart = calendarStart

  while (currentWeekStart.isBefore(calendarEnd)) {
    const week = []
    for (let i = 0; i < 7; i++) {
      const day = currentWeekStart.add(i, 'day')
      week.push({
        date: day.format('YYYY-MM-DD'),
        dayNumber: day.date(),
        isToday: day.isSame(dayjs(), 'day'),
        isOtherMonth: !day.isSame(currentDate.value, 'month'),
      })
    }
    weeks.push(week)
    currentWeekStart = currentWeekStart.add(7, 'day')
  }

  return weeks
})

// 方法
const setView = (view) => {
  currentView.value = view
}

const previousPeriod = () => {
  if (currentView.value === 'week') {
    currentDate.value = currentDate.value.subtract(1, 'week')
  } else {
    currentDate.value = currentDate.value.subtract(1, 'month')
  }
}

const nextPeriod = () => {
  if (currentView.value === 'week') {
    currentDate.value = currentDate.value.add(1, 'week')
  } else {
    currentDate.value = currentDate.value.add(1, 'month')
  }
}

const goToToday = () => {
  currentDate.value = dayjs()
}

// 教练课表显示学生名单
const studentNames = (students) => {
  if (!students || students.length === 0) return '无'
  // 假定 students 是数组 [{name: '张三'}, {name: '李四'}]
  return students.map((s) => s.name).join('，')
}

const getSchedulesForDay = (date) => {
  return schedules.value.filter((schedule) => schedule.date === date)
}

const getSchedulesForDayAndHour = (date, hour) => {
  return schedules.value.filter((schedule) => {
    if (schedule.date !== date) return false
    const scheduleHour = dayjs(schedule.startTime, 'HH:mm').format('HH:00')
    return scheduleHour === hour
  })
}

const getScheduleClass = (schedule) => {
  return [`schedule-${schedule.type}`, `status-${schedule.status}`]
}

const getScheduleTypeName = (type) => {
  const typeNames = {
    individual: '一对一训练',
    group: '小组训练',
    match: '比赛',
    tournament: '锦标赛',
  }
  return typeNames[type] || type
}

// 新增方法
const getCourseIcon = (type) => {
  const icons = {
    individual: '🎯',
    group: '👥',
    match: '🏆',
    tournament: '🥇'
  }
  return icons[type] || '🏓'
}

const getCourseCardClass = (schedule) => {
  return [`course-${schedule.type}`, `status-${schedule.status}`]
}

const getCourseIndicatorClass = (schedule) => {
  return [`indicator-${schedule.type}`, `status-${schedule.status}`]
}

const getStatusText = (status) => {
  const statusTexts = {
    confirmed: '已确认',
    pending: '待确认',
    cancelled: '已取消',
    completed: '已完成'
  }
  return statusTexts[status] || status
}

const closeScheduleDialog = () => {
  showScheduleDialog.value = false
  selectedSchedule.value = null
}

const addCourse = () => {
  showCourseManagementDialog.value = true
  loadCoachCourses()
}

const editSchedule = () => {
  // 编辑课程逻辑
  ElMessage.info('编辑课程功能开发中...')
}

const refreshSchedule = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('课表已刷新')
  }, 1000)
}

const getScheduleTagType = (type) => {
  const tagTypes = {
    individual: 'primary',
    group: 'success',
    match: 'warning',
    tournament: 'danger',
  }
  return tagTypes[type] || 'info'
}

const getStatusName = (status) => {
  const statusNames = {
    pending: '待确认',
    confirmed: '已确认',
    cancelled: '已取消',
    completed: '已完成',
  }
  return statusNames[status] || status
}

const getStatusTagType = (status) => {
  const tagTypes = {
    pending: 'warning',
    confirmed: 'success',
    cancelled: 'danger',
    completed: 'info',
  }
  return tagTypes[status] || 'info'
}

const formatScheduleTime = (schedule) => {
  return `${schedule.date} ${schedule.startTime} - ${schedule.endTime}`
}

const canCancelSchedule = (schedule) => {
  const scheduleDateTime = dayjs(`${schedule.date} ${schedule.startTime}`)
  const now = dayjs()
  return scheduleDateTime.diff(now, 'hour') >= 24 // 至少提前24小时取消
}

const handleCellClick = (date, hour) => {
  // 可以预留教练预约课程或加课接口
  console.log('Coach clicked cell:', date, hour)
}

// 同步和导出功能方法
const exportSchedule = () => {
  try {
    const filename = `教练课表_${dayjs().format('YYYY-MM-DD')}.ics`
    scheduleSync.exportSchedule(schedules.value, filename)
    ElMessage.success('课表导出成功！')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('课表导出失败，请重试')
  }
}

const syncToCalendar = async () => {
  if (isSyncing.value) return
  
  try {
    isSyncing.value = true
    ElMessage.info('开始同步到日历...')
    
    await scheduleSync.syncToCalendar(syncOptions.value.platform)
    
    ElMessage.success(`课表已成功同步到 ${syncOptions.value.platform === 'google' ? 'Google' : 'Outlook'} 日历`)
  } catch (error) {
    console.error('日历同步失败:', error)
    ElMessage.error('日历同步失败，请检查网络连接或授权状态')
  } finally {
    isSyncing.value = false
  }
}

const sendScheduleEmail = () => {
  showEmailDialog.value = true
}

const syncToMobile = async () => {
  try {
    ElMessage.info('开始同步到移动设备...')
    
    const deviceId = 'default-device' // 实际应用中应该从用户设置获取
    await scheduleSync.syncToMobile(deviceId, schedules.value)
    
    ElMessage.success('课表已同步到移动设备')
  } catch (error) {
    console.error('移动端同步失败:', error)
    ElMessage.error('移动端同步失败，请重试')
  }
}

const handleEmailSend = async () => {
  try {
    if (emailOptions.value.recipients.length === 0) {
      ElMessage.warning('请至少添加一个收件人')
      return
    }
    
    ElMessage.info('正在发送邮件...')
    
    // 获取所有学生邮箱（示例数据）
    const recipients = emailOptions.value.recipients.map(email => ({
      name: email.split('@')[0], // 简单从邮箱提取名称
      email: email
    }))
    
    // 批量发送邮件
    for (const recipient of recipients) {
      await scheduleSync.sendEmail('schedule', {
        email: recipient.email,
        name: recipient.name,
        scheduleData: schedules.value
      })
    }
    
    ElMessage.success(`邮件发送成功！共发送 ${recipients.length} 封邮件`)
    showEmailDialog.value = false
    
    // 如果启用了提醒，也发送课程提醒
    if (emailOptions.value.sendReminder) {
      setTimeout(() => {
        ElMessage.info('课程提醒也将在适当时间自动发送')
      }, 1000)
    }
    
  } catch (error) {
    console.error('邮件发送失败:', error)
    ElMessage.error('邮件发送失败，请重试')
  }
}

const addEmailRecipient = () => {
  emailOptions.value.recipients.push('')
}

const removeEmailRecipient = (index) => {
  emailOptions.value.recipients.splice(index, 1)
}

const handleDayClick = (day) => {
  if (day && !day.isOtherMonth) {
    currentDate.value = dayjs(day.date)
    if (currentView.value === 'month') {
      setView('week')
    }
  }
}

const handleScheduleClick = (schedule) => {
  selectedSchedule.value = schedule
  showScheduleDialog.value = true
}

const cancelSchedule = async () => {
  try {
    await ElMessageBox.confirm('确定要取消这个课程吗？取消后将无法恢复。', '确认取消', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    loading.value = true
    // 教练取消课程接口，和学生接口一致，只是权限校验在后端
    await api.put(`/schedules/${selectedSchedule.value.id}/cancel`)

    // 更新本地数据
    const index = schedules.value.findIndex((s) => s.id === selectedSchedule.value.id)
    if (index !== -1) {
      schedules.value[index].status = 'cancelled'
    }

    showScheduleDialog.value = false
    ElMessage.success('课程已取消')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消课程失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

const fetchSchedules = async () => {
  try {
    loading.value = true

    let startDate, endDate
    if (currentView.value === 'week') {
      // 以周一为开始
      startDate = currentDate.value.startOf('week').add(1, 'day').format('YYYY-MM-DD')
      endDate = currentDate.value.startOf('week').add(7, 'day').format('YYYY-MM-DD')
    } else {
      startDate = currentDate.value.startOf('month').format('YYYY-MM-DD')
      endDate = currentDate.value.endOf('month').format('YYYY-MM-DD')
    }

    const response = await api.get('/schedules/coach', {
      params: { startDate, endDate },
    })

    schedules.value = response.data || []
  } catch (error) {
    console.error('获取课表失败:', error)
    // 使用模拟数据用于开发测试
    schedules.value = generateMockCoachSchedules()
  } finally {
    loading.value = false
  }
}

// 生成模拟数据用于开发测试（教练视角）
const generateMockCoachSchedules = () => {
  const mockSchedules = []
  const today = dayjs()
  const mockStudentPool = [
    { name: '小明' },
    { name: '小红' },
    { name: '小强' },
    { name: '小丽' },
    { name: '小刚' },
  ]
  // 生成两周示例课程
  for (let i = 0; i < 14; i++) {
    const date = today.add(i, 'day')

    // 随机生成一对一、小组课
    if (Math.random() > 0.6) {
      const startHour = 9 + Math.floor(Math.random() * 10)
      const startTime = `${startHour.toString().padStart(2, '0')}:00`
      const endTime = `${(startHour + 1).toString().padStart(2, '0')}:00`
      const typeArr = ['individual', 'group', 'match']
      const type = typeArr[Math.floor(Math.random() * typeArr.length)]

      // 一对一课、只一个学生；小组课、随机2-4个学生
      let students = []
      if (type === 'individual') {
        students = [mockStudentPool[Math.floor(Math.random() * mockStudentPool.length)]]
      } else if (type === 'group') {
        const count = 2 + Math.floor(Math.random() * 3)
        // 随机抽取count个学生
        students = [...mockStudentPool].sort(() => Math.random() - 0.5).slice(0, count)
      } else {
        students = [...mockStudentPool].slice(0, 2)
      }

      mockSchedules.push({
        id: `coach-mock-${i}-1`,
        title: ['一对一训练', '小组训练', '比赛指导', '战术练习'][Math.floor(Math.random() * 4)],
        students,
        date: date.format('YYYY-MM-DD'),
        startTime,
        endTime,
        location: `训练场${Math.floor(Math.random() * 5) + 1}号`,
        type,
        status: ['confirmed', 'pending'][Math.floor(Math.random() * 2)],
        description: '教练安排的示例课程',
      })
    }

    // 可能有第二个课程
    if (Math.random() > 0.8) {
      const startHour = 15 + Math.floor(Math.random() * 4)
      const startTime = `${startHour.toString().padStart(2, '0')}:00`
      const endTime = `${(startHour + 1).toString().padStart(2, '0')}:00`
      mockSchedules.push({
        id: `coach-mock-${i}-2`,
        title: '比赛训练',
        students: [...mockStudentPool].slice(0, 3),
        date: date.format('YYYY-MM-DD'),
        startTime,
        endTime,
        location: `比赛场地`,
        type: 'match',
        status: 'confirmed',
        description: '教练带队专项训练',
      })
    }
  }
  return mockSchedules
}

// 课程管理功能
const showCourseManagementDialog = ref(false)
const showCourseDetailDialog = ref(false)
const showFormDialog = ref(false)
const showAttendanceDialog = ref(false)
const selectedCourseDetail = ref(null)
const courseListLoading = ref(false)
const formLoading = ref(false)
const attendanceLoading = ref(false)
const isEdit = ref(false)

// 课程列表数据
const coachCourses = ref([])
const filteredCourses = ref([])
const courseFilter = ref({
  status: '',
  type: '',
  dateRange: []
})
const coursePagination = ref({
  page: 1,
  pageSize: 20,
  total: 0
})

// 课程表单
const courseForm = ref({
  title: '',
  description: '',
  type: 'individual',
  level: 'beginner',
  date: '',
  startTime: '',
  endTime: '',
  location: '',
  maxStudents: 2,
  price: 100,
  campusId: '',
  notes: '',
  materials: [],
  objectives: []
})

const courseRules = {
  title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  level: [{ required: true, message: '请选择课程等级', trigger: 'change' }],
  date: [{ required: true, message: '请选择课程日期', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入上课地点', trigger: 'blur' }],
  price: [{ required: true, message: '请输入课程价格', trigger: 'blur' }],
  maxStudents: [{ required: true, message: '请输入最大学员数', trigger: 'blur' }],
  campusId: [{ required: true, message: '请选择校区', trigger: 'change' }]
}

const courseFormRef = ref()
const campusList = ref([])

// 出勤管理
const attendanceCourse = ref(null)
const attendanceList = ref([])

// 导入API函数
import { 
  getCoachCourses, 
  getCoachCourseDetail, 
  createCourse, 
  updateCourse, 
  deleteCourse as deleteCourseAPI, 
  updateCourseAttendance,
  getCampusList
} from '@/api/courses'

// 加载教练课程列表
const loadCoachCourses = async () => {
  try {
    courseListLoading.value = true
    const params = {
      page: coursePagination.value.page,
      pageSize: coursePagination.value.pageSize
    }

    if (courseFilter.value.status) params.status = [courseFilter.value.status]
    if (courseFilter.value.type) params.type = courseFilter.value.type
    if (courseFilter.value.dateRange && courseFilter.value.dateRange.length === 2) {
      params.startDate = courseFilter.value.dateRange[0]
      params.endDate = courseFilter.value.dateRange[1]
    }

    const response = await getCoachCourses(params)
    if (response.success) {
      coachCourses.value = response.data.courses || []
      filteredCourses.value = coachCourses.value
      coursePagination.value.total = response.data.pagination?.total || 0
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败：' + error.message)
  } finally {
    courseListLoading.value = false
  }
}

// 筛选课程
const filterCourses = () => {
  loadCoachCourses()
}

// 刷新课程
const refreshCourses = () => {
  loadCoachCourses()
  fetchSchedules() // 同时刷新日历视图
}

// 查看课程详情
const viewCourseDetail = async (course) => {
  try {
    const response = await getCoachCourseDetail(course.id)
    if (response.success) {
      selectedCourseDetail.value = response.data
      showCourseDetailDialog.value = true
    }
  } catch (error) {
    ElMessage.error('获取课程详情失败：' + error.message)
  }
}

// 打开创建对话框
const openCreateDialog = (date = null) => {
  isEdit.value = false
  resetForm()
  if (date) {
    courseForm.value.date = date
  }
  showFormDialog.value = true
  loadCampusList()
}

// 打开编辑对话框
const openEditDialog = (course) => {
  isEdit.value = true
  resetForm()
  // 填充表单数据
  Object.keys(courseForm.value).forEach(key => {
    if (course[key] !== undefined) {
      courseForm.value[key] = course[key]
    }
  })
  selectedCourseDetail.value = course
  showCourseDetailDialog.value = false
  showFormDialog.value = true
  loadCampusList()
}

// 关闭表单对话框
const closeFormDialog = () => {
  showFormDialog.value = false
  resetForm()
  selectedCourseDetail.value = null
}

// 重置表单
const resetForm = () => {
  Object.assign(courseForm.value, {
    title: '',
    description: '',
    type: 'individual',
    level: 'beginner',
    date: '',
    startTime: '',
    endTime: '',
    location: '',
    maxStudents: 2,
    price: 100,
    campusId: '',
    notes: '',
    materials: [],
    objectives: []
  })
  if (courseFormRef.value) {
    courseFormRef.value.clearValidate()
  }
}

// 提交课程
const submitCourse = async () => {
  if (!courseFormRef.value) return
  
  try {
    await courseFormRef.value.validate()
    formLoading.value = true

    // 计算课程时长
    const start = dayjs(`2000-01-01 ${courseForm.value.startTime}`)
    const end = dayjs(`2000-01-01 ${courseForm.value.endTime}`)
    const duration = end.diff(start, 'minute')
    
    const courseData = {
      ...courseForm.value,
      duration
    }

    let response
    if (isEdit.value) {
      response = await updateCourse(selectedCourseDetail.value.id, courseData)
    } else {
      response = await createCourse(courseData)
    }
    
    if (response.success) {
      ElMessage.success(isEdit.value ? '课程更新成功' : '课程创建成功')
      closeFormDialog()
      loadCoachCourses()
      fetchSchedules()
    }
  } catch (error) {
    if (error.errors) {
      return
    }
    ElMessage.error((isEdit.value ? '更新' : '创建') + '课程失败：' + error.message)
  } finally {
    formLoading.value = false
  }
}

// 删除课程
const deleteCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程"${course.title}"吗？删除后无法恢复，已报名的学员将会收到通知。`,
      '确认删除',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const response = await deleteCourseAPI(course.id)
    if (response.success) {
      ElMessage.success('课程删除成功')
      if (response.data.affectedStudents > 0) {
        ElMessage.info(`已通知 ${response.data.affectedStudents} 名学员`)
      }
      loadCoachCourses()
      fetchSchedules()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除课程失败：' + error.message)
    }
  }
}

// 打开出勤管理对话框
const openAttendanceDialog = (course) => {
  attendanceCourse.value = course
  // 准备出勤数据
  attendanceList.value = (course.studentList || []).map(student => ({
    studentId: student.studentId,
    studentName: student.studentName,
    status: student.attendanceStatus || 'present',
    notes: ''
  }))
  showAttendanceDialog.value = true
}

// 保存出勤记录
const saveAttendance = async () => {
  try {
    attendanceLoading.value = true
    
    const attendanceData = {
      attendance: attendanceList.value.map(item => ({
        studentId: item.studentId,
        status: item.status,
        notes: item.notes
      }))
    }

    const response = await updateCourseAttendance(attendanceCourse.value.id, attendanceData)
    if (response.success) {
      ElMessage.success('出勤记录保存成功')
      showAttendanceDialog.value = false
      loadCoachCourses()
      fetchSchedules()
    }
  } catch (error) {
    ElMessage.error('保存出勤记录失败：' + error.message)
  } finally {
    attendanceLoading.value = false
  }
}

// 加载校区列表
const loadCampusList = async () => {
  try {
    const response = await getCampusList()
    if (response.success) {
      campusList.value = response.data.first || []
    }
  } catch (error) {
    console.error('获取校区列表失败：', error)
  }
}

// 工具函数
const canEdit = (course) => {
  const courseDate = dayjs(`${course.date} ${course.startTime}`)
  const now = dayjs()
  return (
    ['scheduled', 'confirmed'].includes(course.status) &&
    courseDate.isAfter(now)
  )
}

const canDelete = (course) => {
  const courseDate = dayjs(`${course.date} ${course.startTime}`)
  const now = dayjs()
  return (
    ['scheduled'].includes(course.status) &&
    courseDate.isAfter(now.add(24, 'hour'))
  )
}

const canManageAttendance = (course) => {
  return ['confirmed', 'completed'].includes(course.status) && 
         course.currentStudents > 0
}

const isToday = (date) => {
  return dayjs(date).isSame(dayjs(), 'day')
}

const formatDate = (date) => {
  const day = dayjs(date)
  if (day.isSame(dayjs(), 'day')) return '今天'
  if (day.isSame(dayjs().add(1, 'day'), 'day')) return '明天'
  return day.format('MM-DD')
}

// 新增的工具函数
const getEnrollmentStatusType = (status) => {
  const typeMap = {
    'enrolled': 'success',
    'waitlist': 'warning',
    'cancelled': 'danger'
  }
  return typeMap[status] || 'info'
}

const getEnrollmentStatusText = (status) => {
  const textMap = {
    'enrolled': '已报名',
    'waitlist': '候补中',
    'cancelled': '已取消'
  }
  return textMap[status] || status
}

const getAttendanceStatusType = (status) => {
  const typeMap = {
    'present': 'success',
    'absent': 'danger',
    'late': 'warning'
  }
  return typeMap[status] || 'info'
}

const getAttendanceStatusText = (status) => {
  const textMap = {
    'present': '出席',
    'absent': '缺席',
    'late': '迟到'
  }
  return textMap[status] || status
}

const getLevelText = (level) => {
  const textMap = {
    'beginner': '初级',
    'intermediate': '中级',
    'advanced': '高级'
  }
  return textMap[level] || level
}

// 监听视图和日期变化
watch([currentView, currentDate], fetchSchedules, { immediate: false })

onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
/* 教练课表系统 - Glassmorphism设计风格 */
.design-language-showcase {
  min-height: 100vh;
  padding: 2rem;
  background: linear-gradient(135deg, 
    #667eea 0%, 
    #764ba2 25%, 
    #f093fb 50%, 
    #f5576c 75%, 
    #4facfe 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  position: relative;
  overflow-x: hidden;
}

.design-language-showcase::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse"><path d="M 20 0 L 0 0 0 20" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.6;
  pointer-events: none;
}

/* Hero 区域样式 - Glassmorphism */
.design-language-hero {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 20px;
  padding: 4rem 0;
  text-align: center;
  position: relative;
  z-index: 1;
  margin-bottom: 2rem;
  box-shadow: 
    0 8px 32px rgba(31, 38, 135, 0.37),
    inset 0 1px 0 rgba(255, 255, 255, 0.5),
    inset 0 -1px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.design-language-hero:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 12px 40px rgba(31, 38, 135, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.6),
    inset 0 -1px 0 rgba(0, 0, 0, 0.1);
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 var(--spacing-2xl, 2rem);
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-sm, 0.5rem);
  background: var(--white-alpha-25, rgba(255, 255, 255, 0.25));
  border: 1px solid var(--white-alpha-40, rgba(255, 255, 255, 0.4));
  border-radius: var(--radius-full, 9999px);
  padding: var(--spacing-md, 1rem) var(--spacing-xl, 1.5rem);
  margin-bottom: var(--spacing-2xl, 2rem);
  backdrop-filter: blur(10px);
  color: white;
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-semibold, 600);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
  transition: var(--transition-normal, all 0.2s ease);
}

.hero-badge:hover {
  background: var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  transform: translateY(-1px);
}

.badge-icon {
  font-size: 1.2em;
}

.hero-title {
  font-size: var(--font-size-5xl, 3rem);
  font-weight: var(--font-weight-bold, 700);
  color: white;
  margin: 0 0 var(--spacing-lg, 1.5rem) 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.hero-description {
  font-size: var(--font-size-lg, 1.125rem);
  color: var(--text-white-80, rgba(255, 255, 255, 0.8));
  margin: 0 0 var(--spacing-2xl, 2rem) 0;
  line-height: 1.6;
}

.hero-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: var(--spacing-xl, 1.5rem);
  max-width: 500px;
  margin: 0 auto;
}

.stat-item {
  text-align: center;
  color: white;
}

.stat-number {
  font-size: var(--font-size-3xl, 1.875rem);
  font-weight: var(--font-weight-bold, 700);
  line-height: 1;
  margin-bottom: var(--spacing-xs, 0.25rem);
}

.stat-label {
  font-size: var(--font-size-sm, 0.875rem);
  color: var(--text-white-70, rgba(255, 255, 255, 0.7));
}

/* 组件导航样式 */
.component-navigation {
  margin: 2rem 0;
  position: relative;
  z-index: 2;
}

.section-title {
  font-size: 2rem;
  font-weight: 800;
  color: white;
  text-align: center;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.nav-card {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 16px;
  padding: 1.5rem;
  text-decoration: none;
  color: white;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  text-align: center;
}

.nav-card:hover {
  background: rgba(255, 255, 255, 0.35);
  border-color: rgba(255, 255, 255, 0.4);
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(31, 38, 135, 0.5);
}

.nav-card.active {
  background: rgba(255, 255, 255, 0.4);
  border-color: rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.6);
}

.nav-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  display: block;
}

.nav-card h3 {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  color: white;
}

.nav-card p {
  font-size: 0.875rem;
  margin: 0;
  opacity: 0.8;
  line-height: 1.4;
}

/* 日期导航样式 */
.date-control-section {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.date-navigation-glass {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 50px;
  padding: 0.75rem 1.5rem;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
}

.nav-btn-glass {
  background: rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-btn-glass:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: scale(1.1);
}

.current-period-glass {
  font-weight: 700;
  color: white;
  min-width: 200px;
  text-align: center;
  font-size: 1.1rem;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 组件展示样式 */
.component-showcase {
  margin: 2rem 0;
  position: relative;
  z-index: 2;
}

.section-description {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
  text-align: center;
  margin: 0 auto 2rem;
  max-width: 600px;
  line-height: 1.6;
}

.showcase-grid {
  display: grid;
  gap: 2rem;
}

.content-card {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 20px;
  padding: 2rem;
  position: relative;
  overflow: hidden;
  box-shadow: 
    0 8px 32px rgba(31, 38, 135, 0.37),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
}

.content-card:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 12px 40px rgba(31, 38, 135, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
}

/* 课表网格基础样式 */
.week-grid, .month-grid {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.grid-header, .month-header {
  background: rgba(255, 255, 255, 0.2);
  display: grid;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.day-header, .time-header, .weekday-header {
  padding: 1rem;
  text-align: center;
  color: white;
  font-weight: 600;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
}

.day-header.is-today {
  background: rgba(255, 255, 255, 0.3);
  font-weight: 700;
}

.time-row, .month-week {
  display: grid;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.time-cell, .schedule-cell, .month-day {
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  padding: 0.5rem;
  min-height: 60px;
  position: relative;
}

.course-card, .course-indicator, .month-schedule-item {
  background: rgba(102, 126, 234, 0.8);
  color: white;
  border-radius: 8px;
  padding: 0.5rem;
  margin: 2px 0;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.course-card:hover, .course-indicator:hover, .month-schedule-item:hover {
  background: rgba(102, 126, 234, 1);
  transform: translateY(-1px);
}
.date-navigation {
  display: flex;
  align-items: center;
  gap: var(--spacing-md, 1rem);
}

.nav-btn {
  background: var(--white-alpha-15, rgba(255, 255, 255, 0.15));
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-full, 9999px);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  font-size: var(--font-size-sm, 0.875rem);
}

.nav-btn:hover {
  background: var(--white-alpha-25, rgba(255, 255, 255, 0.25));
  transform: translateY(-1px);
}

.current-period {
  background: var(--white-alpha-15, rgba(255, 255, 255, 0.15));
  backdrop-filter: blur(10px);
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-lg, 0.5rem);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-lg, 1.25rem);
  color: white;
  font-weight: var(--font-weight-semibold, 600);
  min-width: 200px;
  text-align: center;
}

/* 快捷操作 */
.quick-actions {
  display: flex;
  gap: var(--spacing-sm, 0.5rem);
}

.action-btn {
  background: var(--white-alpha-15, rgba(255, 255, 255, 0.15));
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-lg, 0.5rem);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  color: white;
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-medium, 500);
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
}

.action-btn:hover {
  background: var(--white-alpha-25, rgba(255, 255, 255, 0.25));
  transform: translateY(-1px);
}

.action-btn.today-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
  font-weight: var(--font-weight-semibold, 600);
}

/* 课表内容区域 */
.schedule-content {
  background: var(--white-alpha-10, rgba(255, 255, 255, 0.1));
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-radius: var(--radius-xl, 1rem);
  padding: var(--spacing-xl, 1.5rem);
  box-shadow: var(--shadow-lg, 0 10px 15px -3px rgba(0, 0, 0, 0.1));
}

.view-header {
  text-align: center;
  margin-bottom: var(--spacing-xl, 1.5rem);
}

.view-title {
  font-size: var(--font-size-2xl, 1.5rem);
  font-weight: var(--font-weight-bold, 700);
  color: white;
  margin: 0 0 var(--spacing-sm, 0.5rem) 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm, 0.5rem);
}

.title-icon {
  font-size: 1.3em;
}

.view-description {
  color: var(--text-white-80, rgba(255, 255, 255, 0.8));
  margin: 0;
}

/* 周视图样式 */
.week-schedule-container {
  overflow-x: auto;
}

.week-grid {
  min-width: 800px;
}

.grid-header {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));

  border-radius: var(--radius-lg, 0.5rem) var(--radius-lg, 0.5rem) 0 0;
  overflow: hidden;
}

.time-header {
  padding: var(--spacing-md, 1rem);
  text-align: center;
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-white-90, rgba(255, 255, 255, 0.9));
  background: var(--white-alpha-15, rgba(255, 255, 255, 0.15));
  border-right: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
}

.day-header {
  padding: var(--spacing-md, 1rem);
  text-align: center;
  border-right: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  color: var(--text-white-80, rgba(255, 255, 255, 0.8));
  transition: var(--transition-normal, all 0.2s ease);
}

.day-header.is-today {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: var(--font-weight-semibold, 600);
}

.day-name {
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-medium, 500);
  margin-bottom: var(--spacing-xs, 0.25rem);
}

.day-date {
  font-size: var(--font-size-lg, 1.125rem);
  font-weight: var(--font-weight-bold, 700);
}

.grid-body {
  background: var(--white-alpha-05, rgba(255, 255, 255, 0.05));
}

.time-row {
  display: grid;
  grid-template-columns: 100px repeat(7, 1fr);
  border-bottom: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  min-height: 80px;
}

.time-cell {
  padding: var(--spacing-md, 1rem);
  text-align: center;
  font-weight: var(--font-weight-medium, 500);
  color: var(--text-white-70, rgba(255, 255, 255, 0.7));
  background: var(--white-alpha-10, rgba(255, 255, 255, 0.1));
  border-right: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  display: flex;
  align-items: center;
  justify-content: center;
}

.schedule-cell {
  border-right: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  padding: var(--spacing-sm, 0.5rem);
  position: relative;
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  min-height: 80px;
}

.schedule-cell:hover {
  background: var(--white-alpha-05, rgba(255, 255, 255, 0.05));
}

/* 课程卡片样式 */
.course-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-md, 0.375rem);
  padding: var(--spacing-sm, 0.5rem);
  margin-bottom: var(--spacing-xs, 0.25rem);
  color: white;
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  box-shadow: var(--shadow-sm, 0 1px 2px 0 rgba(0, 0, 0, 0.05));
}

.course-card:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
}

.course-card.course-individual {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.course-card.course-group {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.course-card.course-match {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
}

.course-card.course-tournament {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
}

.course-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
  margin-bottom: var(--spacing-xs, 0.25rem);
}

.course-type-icon {
  font-size: 1.1em;
}

.course-title {
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-semibold, 600);
  flex: 1;
}

.course-details {
  font-size: var(--font-size-xs, 0.75rem);
  opacity: 0.9;
  margin-bottom: var(--spacing-xs, 0.25rem);
}

.course-student,
.course-location {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
  margin-bottom: 2px;
}

.detail-icon {
  font-size: 0.9em;
}

.course-status {
  display: flex;
  justify-content: flex-end;
}

.status-badge {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-radius: var(--radius-sm, 0.25rem);
  padding: 2px var(--spacing-xs, 0.25rem);
  font-size: var(--font-size-xs, 0.75rem);
  font-weight: var(--font-weight-medium, 500);
}

.empty-slot {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 60px;
}

.add-course-btn {
  background: var(--white-alpha-10, rgba(255, 255, 255, 0.1));
  border: 2px dashed var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-md, 0.375rem);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-white-50, rgba(255, 255, 255, 0.5));
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
}

.add-course-btn:hover {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
  color: var(--text-white-80, rgba(255, 255, 255, 0.8));
}

/* 月视图样式 */
.month-schedule-container {
  overflow-x: auto;
}

.month-grid {
  min-width: 800px;
}

.month-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-radius: var(--radius-lg, 0.5rem) var(--radius-lg, 0.5rem) 0 0;
}

.weekday-header {
  padding: var(--spacing-md, 1rem);
  text-align: center;
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-white-90, rgba(255, 255, 255, 0.9));
  border-right: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
}

.weekday-header:last-child {
  border-right: none;
}

.month-body {
  background: var(--white-alpha-05, rgba(255, 255, 255, 0.05));
}

.month-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
}

.month-day {
  min-height: 120px;
  padding: var(--spacing-sm, 0.5rem);
  border-right: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  position: relative;
}

.month-day:hover {
  background: var(--white-alpha-05, rgba(255, 255, 255, 0.05));
}

.month-day.is-today {
  background: var(--white-alpha-15, rgba(255, 255, 255, 0.15));
  border: 2px solid var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

.month-day.is-other-month {
  opacity: 0.4;
}

.day-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.day-number {
  font-size: var(--font-size-lg, 1.125rem);
  font-weight: var(--font-weight-bold, 700);
  color: var(--text-white-90, rgba(255, 255, 255, 0.9));
  margin-bottom: var(--spacing-xs, 0.25rem);
}

.day-courses {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.course-indicator {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: var(--radius-sm, 0.25rem);
  padding: 2px var(--spacing-xs, 0.25rem);
  font-size: var(--font-size-xs, 0.75rem);
  color: white;
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  display: flex;
  align-items: center;
  gap: 2px;
}

.course-indicator:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm, 0 1px 2px 0 rgba(0, 0, 0, 0.05));
}

.course-indicator.indicator-group {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.course-indicator.indicator-match {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
}

.course-indicator.indicator-tournament {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
}

.indicator-dot {
  font-size: 0.8em;
}

.indicator-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more-courses {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-radius: var(--radius-sm, 0.25rem);
  padding: 2px var(--spacing-xs, 0.25rem);
  font-size: var(--font-size-xs, 0.75rem);
  color: var(--text-white-80, rgba(255, 255, 255, 0.8));
  text-align: center;
  margin-top: 2px;
}

/* 课程详情对话框 */
.schedule-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xl, 1.5rem);
}

.schedule-dialog {
  background: var(--white-alpha-95, rgba(255, 255, 255, 0.95));
  backdrop-filter: blur(20px);
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-xl, 1rem);
  max-width: 500px;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
  box-shadow: var(--shadow-2xl, 0 25px 50px -12px rgba(0, 0, 0, 0.25));
}

.dialog-header {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  padding: var(--spacing-lg, 1.25rem);
  border-bottom: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dialog-title {
  font-size: var(--font-size-lg, 1.125rem);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-gray-800, #1f2937);
  margin: 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm, 0.5rem);
}

.close-btn {
  background: none;
  border: none;
  padding: var(--spacing-sm, 0.5rem);
  cursor: pointer;
  color: var(--color-gray-600, #4b5563);
  border-radius: var(--radius-md, 0.375rem);
  transition: var(--transition-normal, all 0.2s ease);
}

.close-btn:hover {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  color: var(--color-gray-800, #1f2937);
}

.dialog-content {
  padding: var(--spacing-lg, 1.25rem);
  overflow-y: auto;
}

.course-info-card {
  background: var(--white-alpha-10, rgba(255, 255, 255, 0.1));
  border: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-radius: var(--radius-lg, 0.5rem);
  padding: var(--spacing-lg, 1.25rem);
  backdrop-filter: blur(10px);
}

.info-header {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-md, 1rem);
  margin-bottom: var(--spacing-lg, 1.25rem);
  padding-bottom: var(--spacing-md, 1rem);
  border-bottom: 1px solid var(--white-alpha-20, rgba(255, 255, 255, 0.2));
}

.course-type-badge {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg, 0.5rem);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.course-type-badge.group {
  background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.course-type-badge.match {
  background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
}

.course-type-badge.tournament {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
}

.course-title-section {
  flex: 1;
}

.course-name {
  font-size: var(--font-size-xl, 1.25rem);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-gray-900, #111827);
  margin: 0 0 var(--spacing-xs, 0.25rem) 0;
}

.course-subtitle {
  font-size: var(--font-size-sm, 0.875rem);
  color: var(--color-gray-600, #4b5563);
  margin: 0;
}

.info-grid {
  display: grid;
  gap: var(--spacing-md, 1rem);
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-sm, 0.5rem);
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-icon {
  font-size: 1.2em;
  color: var(--color-gray-600, #4b5563);
  margin-top: 2px;
}

.info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs, 0.25rem);
}

.info-label {
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-gray-600, #4b5563);
}

.info-value {
  font-size: var(--font-size-base, 1rem);
  color: var(--color-gray-900, #111827);
  font-weight: var(--font-weight-medium, 500);
}

.dialog-actions {
  padding: var(--spacing-lg, 1.25rem);
  border-top: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  display: flex;
  gap: var(--spacing-sm, 0.5rem);
  justify-content: flex-end;
}

.dialog-actions .action-btn {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-lg, 0.5rem);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-medium, 500);
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
  color: var(--color-gray-700, #374151);
}

.dialog-actions .action-btn:hover {
  background: var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  transform: translateY(-1px);
}

.dialog-actions .action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

.dialog-actions .action-btn.danger {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
  color: white;
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

.dialog-actions .action-btn.secondary {
  background: var(--color-gray-100, #f3f4f6);
  border-color: var(--color-gray-300, #d1d5db);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coach-schedule-showcase {
    padding: var(--spacing-lg, 1.25rem);
  }
  
  .hero-content {
    padding: 0 var(--spacing-lg, 1.25rem);
  }
  
  .hero-title {
    font-size: var(--font-size-3xl, 1.875rem);
  }
  
  .controls-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-actions {
    flex-direction: column;
  }
  
  .week-grid,
  .month-grid {
    min-width: 600px;
  }
  
  .grid-header,
  .time-row {
    grid-template-columns: 80px repeat(7, 1fr);
  }
  
  .time-header,
  .time-cell {
    padding: var(--spacing-sm, 0.5rem);
    font-size: var(--font-size-xs, 0.75rem);
  }
  
  .day-header {
    padding: var(--spacing-sm, 0.5rem);
  }
  
  .day-name {
    font-size: var(--font-size-xs, 0.75rem);
  }
  
  .day-date {
    font-size: var(--font-size-sm, 0.875rem);
  }
  
  .month-day {
    min-height: 80px;
    padding: var(--spacing-xs, 0.25rem);
  }
  
  .schedule-dialog {
    margin: var(--spacing-lg, 1.25rem);
    max-height: 90vh;
  }
}

/* 加载和错误状态 */
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: var(--text-white-70, rgba(255, 255, 255, 0.7));
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  color: var(--color-red-400, #f87171);
  text-align: center;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-md, 1rem);
}

.error-message {
  font-size: var(--font-size-lg, 1.125rem);
  font-weight: var(--font-weight-medium, 500);
  margin-bottom: var(--spacing-sm, 0.5rem);
}

.error-description {
  font-size: var(--font-size-sm, 0.875rem);
  opacity: 0.8;
  margin-bottom: var(--spacing-md, 1rem);
}

.retry-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: var(--radius-lg, 0.5rem);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  color: white;
  font-weight: var(--font-weight-medium, 500);
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
}

.retry-btn:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.fade-in-up {
  animation: fadeInUp 0.5s ease-out forwards;
}

.slide-in-right {
  animation: slideInRight 0.3s ease-out forwards;
}

/* 工具提示样式 */
.tooltip {
  position: relative;
}

.tooltip:hover .tooltip-content {
  visibility: visible;
  opacity: 1;
}

.tooltip-content {
  visibility: hidden;
  opacity: 0;
  position: absolute;
  bottom: 125%;
  left: 50%;
  transform: translateX(-50%);
  background: var(--color-gray-900, #111827);
  color: white;
  padding: var(--spacing-xs, 0.25rem) var(--spacing-sm, 0.5rem);
  border-radius: var(--radius-md, 0.375rem);
  font-size: var(--font-size-xs, 0.75rem);
  white-space: nowrap;
  z-index: 1000;
  transition: var(--transition-normal, all 0.2s ease);
}

.tooltip-content::after {
  content: "";
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 5px solid transparent;
  border-top-color: var(--color-gray-900, #111827);
}

/* 滚动条样式 */
.schedule-container::-webkit-scrollbar,
.dialog-content::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.schedule-container::-webkit-scrollbar-track,
.dialog-content::-webkit-scrollbar-track {
  background: var(--white-alpha-10, rgba(255, 255, 255, 0.1));
  border-radius: 4px;
}

.schedule-container::-webkit-scrollbar-thumb,
.dialog-content::-webkit-scrollbar-thumb {
  background: var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: 4px;
}

.schedule-container::-webkit-scrollbar-thumb:hover,
.dialog-content::-webkit-scrollbar-thumb:hover {
  background: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

/* 打印样式 */
@media print {
  .coach-schedule-showcase {
    background: white !important;
    color: black !important;
  }
  
  .schedule-hero,
  .schedule-controls {
    display: none;
  }
  
  .schedule-container {
    margin-top: 0;
    box-shadow: none;
    border: 1px solid #ccc;
  }
  
  .course-card {
    background: #f0f0f0 !important;
    color: black !important;
    border: 1px solid #ccc;
  }
}

/* 高对比度模式支持 */
@media (prefers-contrast: high) {
  .course-card {
    border: 2px solid white;
  }
  
  .schedule-dialog {
    border: 2px solid black;
  }
  
  .info-item {
    border-bottom: 1px solid #ccc;
    padding-bottom: var(--spacing-sm, 0.5rem);
  }
}

/* 减少动画模式支持 */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* 同步功能样式 */
.sync-actions {
  display: flex;
  gap: var(--spacing-sm, 0.5rem);
  flex-wrap: wrap;
}

.sync-actions .action-btn {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-lg, 0.5rem);
  padding: var(--spacing-sm, 0.5rem) var(--spacing-md, 1rem);
  color: var(--text-white-90, rgba(255, 255, 255, 0.9));
  font-size: var(--font-size-sm, 0.875rem);
  font-weight: var(--font-weight-medium, 500);
  cursor: pointer;
  transition: var(--transition-normal, all 0.2s ease);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
  min-width: 100px;
  justify-content: center;
}

.sync-actions .action-btn:hover:not(:disabled) {
  background: var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  transform: translateY(-1px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
}

.sync-actions .action-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.sync-actions .export-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

.sync-actions .sync-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

.sync-actions .email-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

.sync-actions .mobile-btn {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  border-color: var(--white-alpha-50, rgba(255, 255, 255, 0.5));
}

/* 邮件对话框样式 */
.email-recipients {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm, 0.5rem);
}

.recipient-item {
  display: flex;
  align-items: center;
}

.add-recipient-btn {
  margin-top: var(--spacing-xs, 0.25rem);
}

.email-preview {
  background: var(--color-gray-50, #f9fafb);
  border: 1px solid var(--color-gray-200, #e5e7eb);
  border-radius: var(--radius-md, 0.375rem);
  padding: var(--spacing-md, 1rem);
  font-size: var(--font-size-sm, 0.875rem);
  color: var(--color-gray-600, #4b5563);
}

.email-preview p {
  margin: var(--spacing-xs, 0.25rem) 0;
}

/* 对话框内容样式 */
.dialog-footer {
  display: flex;
  gap: var(--spacing-sm, 0.5rem);
  justify-content: flex-end;
}

/* Element Plus 样式覆盖 */
.el-dialog {
  backdrop-filter: blur(10px);
  background: var(--white-alpha-95, rgba(255, 255, 255, 0.95));
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
}

.el-dialog__header {
  background: var(--white-alpha-20, rgba(255, 255, 255, 0.2));
  border-bottom: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  padding: var(--spacing-lg, 1.25rem);
}

.el-dialog__title {
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-gray-800, #1f2937);
}

.el-dialog__body {
  padding: var(--spacing-lg, 1.25rem);
}

.el-form-item {
  margin-bottom: var(--spacing-lg, 1.25rem);
}

.el-form-item__label {
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-gray-700, #374151);
}

.el-input__wrapper {
  background: var(--white-alpha-80, rgba(255, 255, 255, 0.8));
  border: 1px solid var(--white-alpha-30, rgba(255, 255, 255, 0.3));
  border-radius: var(--radius-md, 0.375rem);
  transition: var(--transition-normal, all 0.2s ease);
}

.el-input__wrapper:hover {
  border-color: var(--color-blue-400, #60a5fa);
}

.el-input__wrapper.is-focus {
  border-color: var(--color-blue-500, #3b82f6);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.el-select .el-input__wrapper {
  background: var(--white-alpha-80, rgba(255, 255, 255, 0.8));
}

.el-checkbox {
  color: var(--color-gray-700, #374151);
}

.el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: var(--color-blue-500, #3b82f6);
  border-color: var(--color-blue-500, #3b82f6);
}

.el-button {
  border-radius: var(--radius-lg, 0.5rem);
  font-weight: var(--font-weight-medium, 500);
  transition: var(--transition-normal, all 0.2s ease);
}

.el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-color: transparent;
}

.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md, 0 4px 6px -1px rgba(0, 0, 0, 0.1));
}

.el-button--danger.is-text {
  color: var(--color-red-500, #ef4444);
}

.el-button.is-text:hover {
  background: var(--white-alpha-10, rgba(255, 255, 255, 0.1));
}

/* 同步状态指示器 */
.sync-status {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs, 0.25rem);
  font-size: var(--font-size-sm, 0.875rem);
  color: var(--text-white-70, rgba(255, 255, 255, 0.7));
  margin-top: var(--spacing-sm, 0.5rem);
}

.sync-status.syncing {
  color: var(--color-blue-400, #60a5fa);
}

.sync-status.success {
  color: var(--color-green-400, #4ade80);
}

.sync-status.error {
  color: var(--color-red-400, #f87171);
}

.sync-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.sync-indicator.pulse {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

/* 移动端适配 */
@media (max-width: 768px) {
  .sync-actions {
    flex-direction: column;
  }
  
  .sync-actions .action-btn {
    width: 100%;
  }
  
  .el-dialog {
    width: 95% !important;
    margin: var(--spacing-lg, 1.25rem) auto;
  }
  
  .email-recipients {
    max-height: 200px;
    overflow-y: auto;
  }
  
  .recipient-item {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-xs, 0.25rem);
  }
}

/* 移除重复样式 */

/* 移除重复样式 */

.schedule-item.ultra {
  background: #2196f3;
  color: white;
  padding: 8px 14px 6px 14px;
  border-radius: 10px;
  margin-bottom: 4px;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 12px #2196f333;
  border: 2px solid #ffffff;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  position: relative;
  align-items: flex-start;
}

.schedule-item.ultra .field-icon {
  font-size: 1em;
  margin-right: 3px;
  vertical-align: middle;
}

.schedule-item.ultra .schedule-title {
  font-weight: 700;
  margin-bottom: 2px;
  font-size: 14px;
  letter-spacing: 1px;
}

.schedule-item.ultra .schedule-coach,
.schedule-item.ultra .schedule-location {
  font-size: 12px;
  opacity: 0.85;
  margin-top: 1px;
  font-weight: 500;
}

.schedule-item.ultra.schedule-individual {
  background: linear-gradient(90deg, #2196f3 60%, #21cbf3 100%);
}

.schedule-item.ultra.schedule-group {
  background: linear-gradient(90deg, #4caf50 60%, #8bc34a 100%);
}

.schedule-item.ultra.schedule-match {
  background: linear-gradient(90deg, #ff9800 60%, #ffd54f 100%);
}

.schedule-item.ultra.schedule-tournament {
  background: linear-gradient(90deg, #f44336 60%, #ff8a65 100%);
}

.schedule-item.ultra.status-pending {
  opacity: 0.7;
  border: 2px dashed rgba(255, 255, 255, 0.7);
}

.schedule-item.ultra.status-cancelled {
  background: #bdbdbd;
  text-decoration: line-through;
  color: #fff;
}

.month-view.ultra {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.09);
}

.month-header.ultra {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: linear-gradient(90deg, #e8f1fc 0%, #f8f9fa 100%);
  border-bottom: 2px solid #e0e0e0;
}

.month-day-header {
  padding: 19px 0 11px 0;
  text-align: center;
  font-weight: 700;
  border-right: 1.5px solid #e0e0e0;
  color: #0d121b;
  font-size: 1.12rem;
  background: linear-gradient(90deg, #697a88 0%, #c2ddfb 100%);
  letter-spacing: 1px;
}

.month-body {
  display: flex;
  flex-direction: column;
}

.month-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  border-bottom: 1.5px solid #e0e0e0;
  background: #fcfcfd;
}

.month-day {
  min-height: 120px;
  padding: 12px 5px;
  border-right: 1.5px solid #e0e0e0;
  cursor: pointer;
  transition: background-color 0.18s;
  position: relative;
  border-radius: 10px;
}

.month-day:hover {
  background: #eaf6fe;
  box-shadow: 0 2px 8px #2196f322;
}

.month-day.is-today {
  background: linear-gradient(120deg, #e3f2fd 60%, #b9eaff 100%);
  border: 3px solid #2196f3;
  box-shadow: 0 2px 16px #2196f344;
}

.month-day.is-other-month {
  color: #bbb;
  background: #fafafa;
}

.month-day.has-schedules .day-number.ultra {
  font-weight: 800;
  color: #1976d2;
  text-shadow: 0 2px 8px #b9eaff50;
}

.day-number.ultra {
  font-size: 19px;
  margin-bottom: 7px;
  font-weight: 700;
  letter-spacing: 1px;
}

.day-schedules.ultra {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.month-schedule-item.ultra {
  background: #2196f3;
  color: white;
  padding: 4px 14px;
  border-radius: 7px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: 2px solid #fff;
  margin-bottom: 2px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.month-schedule-item.ultra .dot-icon {
  color: #1565c0;
  font-size: 1.2em;
  margin-right: 1px;
}

.month-schedule-item.ultra.schedule-group {
  background: linear-gradient(90deg, #4caf50 60%, #8bc34a 100%);
}

.month-schedule-item.ultra.schedule-match {
  background: linear-gradient(90deg, #ff9800 60%, #ffd54f 100%);
}

.month-schedule-item.ultra.schedule-tournament {
  background: linear-gradient(90deg, #f44336 60%, #ff8a65 100%);
}

.more-schedules.ultra {
  font-size: 12px;
  color: #666;
  text-align: center;
  margin-top: 2px;
  font-weight: 700;
  letter-spacing: 1px;
  background: #e3f2fd;
  border-radius: 6px;
  padding: 2px 0;
}

.schedule-detail.ultra {
  padding: 24px 0 6px 0;
  font-size: 1.17rem;
  background: linear-gradient(110deg, #e3f2fd 80%, #f8f9fa 100%);
  border-radius: 12px;
}

.schedule-detail.ultra .field-icon {
  font-size: 1.1em;
  margin-right: 4px;
  vertical-align: middle;
}

.ultra-dialog ::deep(.el-dialog__body) {
  background: linear-gradient(110deg, #e0eafc 80%, #ffffff 100%);
  padding: 36px 28px 20px 28px;
  border-radius: 14px;
}

.dialog-footer.ultra {
  display: flex;
  justify-content: flex-end;
  gap: 22px;
  padding-top: 16px;
}

.ultra-loading {
  z-index: 9999;
}

/* 响应式优化 */
@media (max-width: 768px) {
  .schedule-header.ultra {
    flex-direction: column;
    gap: 20px;
    align-items: stretch;
    padding: 22px 7px;
  }

  .schedule-controls.ultra {
    flex-direction: column;
    gap: 19px;
  }

  .date-navigation.ultra {
    justify-content: center;
    gap: 12px;
    padding: 10px 10px;
  }

  .current-period.ultra {
    min-width: auto;
    font-size: 1.05rem;
    padding: 4px 4px;
  }

  .week-header.ultra,
  .time-row {
    grid-template-columns: 64px repeat(7, 1fr);
  }

  .time-column {
    padding: 9px 3px;
    font-size: 12px;
  }

  .day-header {
    padding: 9px 3px;
    font-size: 14px;
  }

  .day-name {
    font-size: 12px;
  }

  .day-date {
    font-size: 15px;
  }

  .month-day {
    min-height: 78px;
    padding: 5px;
    font-size: 12px;
  }

  .day-number.ultra {
    font-size: 14px;
    margin-bottom: 3px;
  }

  .month-schedule-item.ultra {
    font-size: 11px;
    padding: 2px 6px;
  }

  .schedule-detail.ultra {
    padding: 10px 2px 0 2px;
    font-size: 1rem;
  }

  .ultra-dialog ::deep(.el-dialog__body) {
    padding: 14px 8px 12px 8px;
  }

  .dialog-footer.ultra {
    gap: 9px;
  }
}

/* 课程管理样式 */
.course-management-panel {
  padding: 20px 0;
}

.management-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-right {
  display: flex;
  align-items: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.course-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-header h3 {
  margin: 0;
  font-size: 1.5rem;
  color: #1f2937;
}

.course-badges {
  display: flex;
  gap: 8px;
}

.student-list {
  margin-bottom: 20px;
}

.student-list h4 {
  margin: 0 0 12px 0;
  color: #374151;
}

.description-section,
.notes-section,
.objectives-section {
  margin-top: 20px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
}

.description-section h4,
.notes-section h4,
.objectives-section h4 {
  margin: 0 0 12px 0;
  color: #374151;
}

.description-section p,
.notes-section p {
  margin: 0;
  color: #6b7280;
  line-height: 1.6;
}

.attendance-management {
  padding: 10px 0;
}

.course-info {
  margin-bottom: 20px;
  padding: 16px;
  background: #f3f4f6;
  border-radius: 8px;
}

.course-info h4 {
  margin: 0 0 8px 0;
  color: #1f2937;
}

.course-info p {
  margin: 0;
  color: #6b7280;
}

.time-range {
  display: flex;
  align-items: center;
  gap: 4px;
}

</style>
