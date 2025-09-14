package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val title: String,
    val description: String? = null, // 课程简介（可选）
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:MM
    val endTime: String, // HH:MM
    val duration: Int, // minutes
    val location: String,
    val status: String, // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val price: Int, // cents
    val coachId: String, // UUID
    val coachName: String,
    val studentId: String, // UUID
    val studentName: String,
    val campusId: Int,
    val campusName: String,
    val tableId: String? = null, // 关联的球桌ID，可为空
    val tableIndex: Int? = null, // 球桌在校区内的编号，可为空
    val lessonContent: String? = null, // 教练课后填写的课程内容
    // 原StudentCourse的字段，统一到Course中
    val paymentStatus: String, // PENDING, PAID, REFUNDED
    val attendanceStatus: String? = null, // PRESENT, ABSENT, LATE (课程完成后填写)
    val studentFeedback: String? = null, // 学生对课程的反馈
    val studentRating: Int? = null, // 学生评分 1-5
    val createdAt: String // ISO datetime
)