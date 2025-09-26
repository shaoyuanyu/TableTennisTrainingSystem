package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionSignup(
    val competitionId: String,
    val competitionName: String,
    val userId: String,
    val group: String,
    val campusId: Int,
    val campusName: String,
    val status: String,
    val createdAt: String
)
