package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

/**
 * 教练确认课程请求
 */
@Serializable
data class CourseConfirmRequest(
    val courseId: String,
    val confirmed: Boolean, // true=确认, false=拒绝
    val reason: String? = null // 拒绝原因（可选）
)

/**
 * 更新课程内容请求（教练课后填写）
 */
@Serializable
data class LessonContentUpdateRequest(
    val courseId: String,
    val lessonContent: String
)

/**
 * 学生反馈请求
 */
@Serializable
data class StudentFeedbackRequest(
    val courseId: String,
    val attendanceStatus: String, // PRESENT, ABSENT, LATE
    val studentFeedback: String? = null,
    val studentRating: Int? = null // 1-5
)

/**
 * 更新付款状态请求
 */
@Serializable
data class PaymentStatusUpdateRequest(
    val courseId: String,
    val paymentStatus: String // PENDING, PAID, REFUNDED
)

/**
 * 课程查询结果
 */
@Serializable
data class CourseScheduleResponse(
    val courses: List<Course>,
    val total: Int,
    val page: Int,
    val size: Int
)
