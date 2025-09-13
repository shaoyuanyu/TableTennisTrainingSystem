package io.github.shaoyuanyu.ttts.dto.studetn_coach

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable

class StudentCoach (
    val studentID: Int,
    val coachID: Int,
    val status: String,
    val start_time: LocalDateTime,
    val end_time: LocalDateTime,
)