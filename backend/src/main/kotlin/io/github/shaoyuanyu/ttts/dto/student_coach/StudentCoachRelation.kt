package io.github.shaoyuanyu.ttts.dto.student_coach

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StudentCoachRelation(
    val coachId: String,
    val coachName: String,
    val studentId: String,
    val studentName: String,
    val status: StudentCoachRelationStatus,
    val applicationTime: LocalDateTime,
    val expectedStartTime: LocalDateTime,
    val actualStartTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val studentMessage: String?,
    val coachMessage: String?
)