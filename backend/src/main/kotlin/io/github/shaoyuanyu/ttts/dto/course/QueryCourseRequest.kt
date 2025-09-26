package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

@Serializable
data class QueryCourseRequest(
    val status: String? = null, // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val studentId: String? = null, // UUID
    val coachId: String? = null, // UUID
    val campusId: Int? = null,
    val dateFrom: String? = null, // YYYY-MM-DD
    val dateTo: String? = null, // YYYY-MM-DD
    val page: Int = 1, // 改为从1开始，更符合用户习惯
    val size: Int = 20
)