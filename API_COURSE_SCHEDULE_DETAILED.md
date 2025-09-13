# 课程表管理系统 API 接口文档

## 概述
本文档详细描述了乒乓球训练系统中学员课程表和教练课程表的增删改查功能API接口，包括完整的请求和响应格式。

## 数据结构定义

### 课程表基础数据结构

#### Course（基础课程）
```typescript
interface Course {
  id: string                    // 课程唯一标识
  title: string                 // 课程标题
  description?: string          // 课程描述
  type: 'individual' | 'group'  // 课程类型：个人课/小组课
  level: 'beginner' | 'intermediate' | 'advanced' // 课程等级
  date: string                  // 课程日期 (YYYY-MM-DD)
  startTime: string             // 开始时间 (HH:mm)
  endTime: string               // 结束时间 (HH:mm)
  duration: number              // 课程时长（分钟）
  location: string              // 上课地点
  status: 'scheduled' | 'confirmed' | 'completed' | 'cancelled' // 课程状态
  maxStudents?: number          // 最大学员数（小组课）
  currentStudents: number       // 当前报名学员数
  price: number                 // 课程价格
  coachId: string              // 教练ID
  coachName: string            // 教练姓名
  campusId: string             // 校区ID
  campusName: string           // 校区名称
  createdAt: string            // 创建时间 ISO格式
  updatedAt: string            // 更新时间 ISO格式
}
```

#### StudentCourse（学员课程）
```typescript
interface StudentCourse extends Course {
  enrollmentId: string         // 报名记录ID
  enrollmentStatus: 'enrolled' | 'waitlist' | 'cancelled' // 报名状态
  paymentStatus: 'pending' | 'paid' | 'refunded' // 支付状态
  enrolledAt: string           // 报名时间 ISO格式
  attendanceStatus?: 'present' | 'absent' | 'late' // 出勤状态（课程结束后）
  feedback?: string            // 课程反馈
  rating?: number             // 课程评分 (1-5)
}
```

#### CoachCourse（教练课程）
```typescript
interface CoachCourse extends Course {
  studentList: Array<{         // 学员列表
    studentId: string
    studentName: string
    enrollmentStatus: 'enrolled' | 'waitlist' | 'cancelled'
    paymentStatus: 'pending' | 'paid' | 'refunded'
    attendanceStatus?: 'present' | 'absent' | 'late'
    enrolledAt: string
  }>
  notes?: string              // 教练备注
  materials?: string[]        // 课程材料
  objectives?: string[]       // 训练目标
}
```

## 学员课程表 API

### 1. 获取学员课程表列表
**接口**: `GET /api/student/courses`

**请求参数**:
```typescript
interface StudentCoursesQuery {
  startDate?: string          // 开始日期 (YYYY-MM-DD)，默认当前日期
  endDate?: string            // 结束日期 (YYYY-MM-DD)，默认30天后
  status?: string[]           // 课程状态筛选 ['scheduled', 'confirmed', 'completed', 'cancelled']
  type?: 'individual' | 'group' // 课程类型筛选
  coachId?: string            // 教练ID筛选
  page?: number               // 页码，默认1
  pageSize?: number           // 每页数量，默认20，最大100
}
```

**请求示例**:
```http
GET /api/student/courses?startDate=2024-01-01&endDate=2024-01-31&status=confirmed&page=1&pageSize=20
```

**响应数据**:
```typescript
interface StudentCoursesResponse {
  success: boolean
  data: {
    courses: StudentCourse[]
    pagination: {
      total: number           // 总记录数
      page: number            // 当前页码
      pageSize: number        // 每页数量
      totalPages: number      // 总页数
    }
  }
  message?: string
  timestamp: string           // 响应时间 ISO格式
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "courses": [
      {
        "id": "course_001",
        "title": "乒乓球基础训练",
        "description": "适合初学者的基础技术训练",
        "type": "individual",
        "level": "beginner",
        "date": "2024-01-15",
        "startTime": "14:00",
        "endTime": "15:00",
        "duration": 60,
        "location": "训练场A",
        "status": "confirmed",
        "maxStudents": 1,
        "currentStudents": 1,
        "price": 150,
        "coachId": "coach_001",
        "coachName": "张教练",
        "campusId": "campus_001",
        "campusName": "中心校区",
        "enrollmentId": "enroll_001",
        "enrollmentStatus": "enrolled",
        "paymentStatus": "paid",
        "enrolledAt": "2024-01-10T10:00:00Z",
        "createdAt": "2024-01-10T09:00:00Z",
        "updatedAt": "2024-01-10T10:00:00Z"
      }
    ],
    "pagination": {
      "total": 25,
      "page": 1,
      "pageSize": 20,
      "totalPages": 2
    }
  },
  "message": "获取成功",
  "timestamp": "2024-01-20T12:00:00Z"
}
```

### 2. 获取学员课程表详情
**接口**: `GET /api/student/courses/{courseId}`

**路径参数**:
- `courseId`: 课程ID (必需)

**响应数据**:
```typescript
interface StudentCourseDetailResponse {
  success: boolean
  data: StudentCourse
  message?: string
  timestamp: string
}
```

### 3. 学员报名课程
**接口**: `POST /api/student/courses/{courseId}/enroll`

**路径参数**:
- `courseId`: 课程ID (必需)

**请求体**:
```typescript
interface EnrollCourseRequest {
  notes?: string              // 报名备注，最大255字符
}
```

**请求示例**:
```json
{
  "notes": "希望加强正手技术训练"
}
```

**响应数据**:
```typescript
interface EnrollCourseResponse {
  success: boolean
  data: {
    enrollmentId: string
    course: StudentCourse
    paymentUrl?: string        // 支付链接（如需要支付）
  }
  message?: string
  timestamp: string
}
```

### 4. 学员取消课程
**接口**: `DELETE /api/student/courses/{courseId}/enroll`

**路径参数**:
- `courseId`: 课程ID (必需)

**响应数据**:
```typescript
interface CancelCourseResponse {
  success: boolean
  data: {
    refundAmount?: number     // 退款金额（分为单位）
    refundStatus: 'processing' | 'completed' | 'failed' // 退款状态
    cancellationFee?: number  // 取消费用（分为单位）
  }
  message?: string
  timestamp: string
}
```

### 5. 学员课程反馈
**接口**: `PUT /api/student/courses/{courseId}/feedback`

**路径参数**:
- `courseId`: 课程ID (必需)

**请求体**:
```typescript
interface CourseFeedbackRequest {
  rating: number              // 评分 1-5，必填
  feedback: string            // 反馈内容，必填，最大1000字符
}
```

**请求示例**:
```json
{
  "rating": 5,
  "feedback": "教练很专业，课程安排合理，技术进步明显！"
}
```

**响应数据**:
```typescript
interface CourseFeedbackResponse {
  success: boolean
  data: StudentCourse
  message?: string
  timestamp: string
}
```

## 教练课程表 API

### 1. 获取教练课程表列表
**接口**: `GET /api/coach/courses`

**请求参数**:
```typescript
interface CoachCoursesQuery {
  startDate?: string          // 开始日期 (YYYY-MM-DD)
  endDate?: string            // 结束日期 (YYYY-MM-DD)
  status?: string[]           // 课程状态筛选
  type?: 'individual' | 'group' // 课程类型筛选
  campusId?: string           // 校区ID筛选
  page?: number               // 页码，默认1
  pageSize?: number           // 每页数量，默认20
}
```

**响应数据**:
```typescript
interface CoachCoursesResponse {
  success: boolean
  data: {
    courses: CoachCourse[]
    pagination: {
      total: number
      page: number
      pageSize: number
      totalPages: number
    }
  }
  message?: string
  timestamp: string
}
```

### 2. 获取教练课程详情
**接口**: `GET /api/coach/courses/{courseId}`

**路径参数**:
- `courseId`: 课程ID (必需)

**响应数据**:
```typescript
interface CoachCourseDetailResponse {
  success: boolean
  data: CoachCourse
  message?: string
  timestamp: string
}
```

### 3. 创建新课程
**接口**: `POST /api/coach/courses`

**请求体**:
```typescript
interface CreateCourseRequest {
  title: string               // 课程名称，必填，最大100字符
  description?: string        // 课程描述，最大500字符
  type: 'individual' | 'group' // 课程类型，必填
  level: 'beginner' | 'intermediate' | 'advanced' // 课程等级，必填
  date: string                // 课程日期 YYYY-MM-DD，必填
  startTime: string           // 开始时间 HH:mm，必填
  endTime: string             // 结束时间 HH:mm，必填
  location: string            // 上课地点，必填，最大50字符
  maxStudents?: number        // 最大学员数，小组课必填，范围2-10
  price: number               // 课程价格，必填，最小值1
  campusId: string            // 校区ID，必填
  notes?: string              // 教练备注，最大500字符
  materials?: string[]        // 课程材料，每项最大100字符
  objectives?: string[]       // 训练目标，每项最大50字符
}
```

**请求示例**:
```json
{
  "title": "乒乓球进阶技术训练",
  "description": "针对有基础的学员进行技术提升训练",
  "type": "group",
  "level": "intermediate",
  "date": "2024-01-25",
  "startTime": "19:00",
  "endTime": "20:30",
  "location": "训练场B",
  "maxStudents": 4,
  "price": 200,
  "campusId": "campus_001",
  "notes": "需要学员具备基础技术",
  "materials": ["乒乓球拍", "训练球"],
  "objectives": ["正手攻球", "反手技术", "步法训练"]
}
```

**响应数据**:
```typescript
interface CreateCourseResponse {
  success: boolean
  data: CoachCourse
  message?: string
  timestamp: string
}
```

### 4. 更新课程信息
**接口**: `PUT /api/coach/courses/{courseId}`

**路径参数**:
- `courseId`: 课程ID (必需)

**请求体**: 同创建课程请求体，所有字段可选

**响应数据**:
```typescript
interface UpdateCourseResponse {
  success: boolean
  data: CoachCourse
  message?: string
  timestamp: string
}
```

### 5. 删除课程
**接口**: `DELETE /api/coach/courses/{courseId}`

**路径参数**:
- `courseId`: 课程ID (必需)

**响应数据**:
```typescript
interface DeleteCourseResponse {
  success: boolean
  data: {
    affectedStudents: number  // 受影响的学员数量
    notificationsSent: boolean // 是否已发送通知
  }
  message?: string
  timestamp: string
}
```

### 6. 课程出勤管理
**接口**: `PUT /api/coach/courses/{courseId}/attendance`

**路径参数**:
- `courseId`: 课程ID (必需)

**请求体**:
```typescript
interface UpdateAttendanceRequest {
  attendance: Array<{
    studentId: string
    status: 'present' | 'absent' | 'late'
    notes?: string             // 出勤备注，最大255字符
  }>
}
```

**请求示例**:
```json
{
  "attendance": [
    {
      "studentId": "student_001",
      "status": "present",
      "notes": "表现积极"
    },
    {
      "studentId": "student_002",
      "status": "late",
      "notes": "迟到15分钟"
    }
  ]
}
```

**响应数据**:
```typescript
interface UpdateAttendanceResponse {
  success: boolean
  data: CoachCourse
  message?: string
  timestamp: string
}
```

## 通用查询 API

### 1. 获取可用时间段
**接口**: `GET /api/courses/available-slots`

**请求参数**:
```typescript
interface AvailableSlotsQuery {
  coachId?: string            // 指定教练（教练查看自己，学员查看指定教练）
  date: string                // 查询日期 YYYY-MM-DD，必填
  duration: number            // 课程时长（分钟），默认60
  campusId?: string           // 校区ID
}
```

**请求示例**:
```http
GET /api/courses/available-slots?coachId=coach_001&date=2024-01-25&duration=90
```

**响应数据**:
```typescript
interface AvailableSlotsResponse {
  success: boolean
  data: {
    date: string
    slots: Array<{
      startTime: string       // HH:mm
      endTime: string         // HH:mm
      available: boolean
      reason?: string         // 不可用原因
    }>
  }
  message?: string
  timestamp: string
}
```

**响应示例**:
```json
{
  "success": true,
  "data": {
    "date": "2024-01-25",
    "slots": [
      {
        "startTime": "09:00",
        "endTime": "10:30",
        "available": true
      },
      {
        "startTime": "10:30",
        "endTime": "12:00",
        "available": false,
        "reason": "已有课程安排"
      }
    ]
  },
  "timestamp": "2024-01-20T12:00:00Z"
}
```

### 2. 课程统计信息
**接口**: `GET /api/courses/statistics`

**响应数据**:
```typescript
interface CourseStatisticsResponse {
  success: boolean
  data: {
    todayCourses: number      // 今日课程数
    weekCourses: number       // 本周课程数
    monthCourses: number      // 本月课程数
    totalStudents?: number    // 教练视图：总学员数
    totalCourses?: number     // 学员视图：总课程数
    completedCourses?: number // 已完成课程数
    upcomingCourses: number   // 即将开始的课程数
    revenue?: {               // 教练视图：收入统计
      thisWeek: number
      thisMonth: number
      total: number
    }
    expenses?: {              // 学员视图：费用统计
      thisWeek: number
      thisMonth: number
      total: number
    }
  }
  message?: string
  timestamp: string
}
```

### 3. 导出课程表
**接口**: `GET /api/courses/export/ical`

**请求参数**:
```typescript
interface ExportParams {
  startDate?: string          // 开始日期
  endDate?: string            // 结束日期
  format?: 'ical' | 'csv'     // 导出格式，默认ical
}
```

**响应**: 文件流 (Content-Type: text/calendar 或 text/csv)

### 4. 发送课程表邮件
**接口**: `POST /api/courses/send-email`

**请求体**:
```typescript
interface SendEmailRequest {
  recipients: string[]        // 收件人邮箱列表
  subject?: string            // 邮件主题
  includeAttachment: boolean  // 是否包含附件
  dateRange?: {
    startDate: string
    endDate: string
  }
}
```

**响应数据**:
```typescript
interface SendEmailResponse {
  success: boolean
  data: {
    emailsSent: number
    failedEmails: string[]
  }
  message?: string
  timestamp: string
}
```

## 错误响应格式

所有API在发生错误时，返回统一格式：

```typescript
interface ErrorResponse {
  success: false
  error: {
    code: string              // 错误代码
    message: string           // 错误消息
    details?: any            // 错误详情
    field?: string           // 字段错误（表单验证）
  }
  timestamp: string           // 错误时间
}
```

### 常见错误代码

#### 业务错误 (4xxx)
- `4001 COURSE_NOT_FOUND`: 课程不存在
- `4002 COURSE_FULL`: 课程已满员
- `4003 ENROLLMENT_EXISTS`: 已报名该课程
- `4004 ENROLLMENT_NOT_FOUND`: 报名记录不存在
- `4005 COURSE_STARTED`: 课程已开始，无法操作
- `4006 TIME_CONFLICT`: 时间冲突
- `4007 PAYMENT_REQUIRED`: 需要支付
- `4008 CANCELLATION_NOT_ALLOWED`: 不允许取消
- `4009 FEEDBACK_ALREADY_EXISTS`: 反馈已存在
- `4010 ATTENDANCE_LOCKED`: 出勤记录已锁定

#### 权限错误 (4030-4039)
- `4030 INSUFFICIENT_PERMISSION`: 权限不足
- `4031 COACH_ONLY`: 仅教练可操作
- `4032 STUDENT_ONLY`: 仅学员可操作
- `4033 OWNER_ONLY`: 仅所有者可操作

#### 验证错误 (4220-4229)
- `4220 VALIDATION_FAILED`: 数据验证失败
- `4221 INVALID_DATE_RANGE`: 无效的日期范围
- `4222 INVALID_TIME_FORMAT`: 无效的时间格式
- `4223 INVALID_PHONE_NUMBER`: 无效的手机号码
- `4224 INVALID_EMAIL`: 无效的邮箱地址

#### 系统错误 (5xxx)
- `5000 INTERNAL_ERROR`: 内部系统错误
- `5001 DATABASE_ERROR`: 数据库错误
- `5002 EXTERNAL_SERVICE_ERROR`: 外部服务错误
- `5003 EMAIL_SEND_FAILED`: 邮件发送失败

### 错误响应示例

```json
{
  "success": false,
  "error": {
    "code": "4002",
    "message": "课程已满员，无法报名",
    "details": {
      "courseId": "course_001",
      "maxStudents": 4,
      "currentStudents": 4
    }
  },
  "timestamp": "2024-01-20T12:00:00Z"
}
```

## 状态码说明

- `200 OK`: 请求成功
- `201 Created`: 资源创建成功
- `400 Bad Request`: 请求参数错误
- `401 Unauthorized`: 未授权
- `403 Forbidden`: 权限不足
- `404 Not Found`: 资源不存在
- `409 Conflict`: 资源冲突
- `422 Unprocessable Entity`: 数据验证失败
- `500 Internal Server Error`: 服务器内部错误
- `503 Service Unavailable`: 服务不可用

## 请求头要求

所有需要认证的API请求必须包含以下头部：

```http
Authorization: Bearer <access_token>
Content-Type: application/json
Accept: application/json
X-Requested-With: XMLHttpRequest
```

## 分页和排序

所有列表API支持分页和排序：

**分页参数**:
- `page`: 页码，从1开始
- `pageSize`: 每页数量，默认20，最大100

**排序参数**:
- `sortBy`: 排序字段，如'date', 'createdAt', 'price'
- `sortOrder`: 排序方向，'asc' 或 'desc'

**示例**:
```http
GET /api/student/courses?page=2&pageSize=10&sortBy=date&sortOrder=asc
```

## 字段长度限制

| 字段 | 最大长度 | 说明 |
|------|---------|------|
| title | 100字符 | 课程标题 |
| description | 500字符 | 课程描述 |
| location | 50字符 | 上课地点 |
| notes | 500字符 | 备注信息 |
| feedback | 1000字符 | 课程反馈 |
| objectives | 50字符/项 | 训练目标 |
| materials | 100字符/项 | 课程材料 |

## 时间格式说明

- **日期**: YYYY-MM-DD (如: 2024-01-20)
- **时间**: HH:mm (如: 14:30)
- **日期时间**: ISO 8601格式 (如: 2024-01-20T14:30:00Z)
- **时区**: 所有时间均使用UTC时区

## 数据验证规则

1. **课程时间**: 结束时间必须晚于开始时间
2. **课程日期**: 不能创建过去时间的课程
3. **价格**: 必须为正数，支持小数点后两位
4. **评分**: 1-5的整数
5. **邮箱**: 符合RFC 5322标准
6. **手机号**: 支持中国大陆11位手机号格式