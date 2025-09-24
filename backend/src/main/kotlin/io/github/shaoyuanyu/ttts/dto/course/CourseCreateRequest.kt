package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

@Serializable
data class CourseCreateRequest(
    val title: String,
    val description: String? = null, // 课程简介（可选）
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:MM
    val endTime: String, // HH:MM
    val location: String,
    val price: Float, // cents
    val coachId: String, // UUID - 学生选择的教练
    val studentId: String, // UUID - 预约的学生
    val campusId: Int,
    var tableId: String? = null // 可选指定球桌ID，为空则自动分配
)
@Serializable
data class CourseBookingRequest(
    val title: String,
    val description: String? = null,
    val coachId: String,
    val studentId: String,
    val date: String, // YYYY-MM-DD
    val NO: Int, // 课程节数
    val campusId: Int,
    val price: Float,
    val tableId: String? = null // 可选指定球桌ID，为空则自动分配
)