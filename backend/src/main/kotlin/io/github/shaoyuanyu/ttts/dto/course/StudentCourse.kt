package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

@Serializable
data class StudentCourse(
    val studentId: String,
    val courseId: String,
    val courseTitle: String,
    val courseDescription: String? = null,
    val courseType: String, // individual or group
    val courseLevel: String, // beginner, intermediate, advanced
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:MM
    val endTime: String, // HH:MM
    val location: String,
    val enrollmentStatus: String, // enrolled, waitlist, cancelled
    val paymentStatus: String, // pending, paid, refunded
    val attendanceStatus: String? = null, // present, absent, late
    val feedback: String? = null,
    val rating: Int? = null, // 1-5
    val coachName: String,
    val campusName: String,
    val enrolledAt: String // ISO datetime
)