package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val coachId: String, // UUID
    val coachName: String,
    val studentId: String, // UUID
    val studentName: String,
    val campusId: Int,  // UUID
    val campusName: String,
    val tableId: String? = null, // UUID
    val tableIndex: Int? = null, // 球桌在校区内的编号
    val title: String,
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:MM
    val endTime: String, // HH:MM
    val duration: Int, // minutes
    val status: String, // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val price: Int,
    val paymentStatus: String? = null, // PENDING, PAID, REFUNDED
    val attendanceStatus: String? = null, // PRESENT, ABSENT, LATE (课程完成后填写)
    val studentFeedback: String? = null, // 学生对课程的反馈
    val studentRating: Int? = null, // 学生评分 1-5
    val createdAt: String // ISO datetime
)