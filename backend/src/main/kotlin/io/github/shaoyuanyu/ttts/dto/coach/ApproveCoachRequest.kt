package io.github.shaoyuanyu.ttts.dto.coach

import kotlinx.serialization.Serializable

@Serializable
data class ApproveCoachRequest(
    val coachId: String,
    val level: String,
)