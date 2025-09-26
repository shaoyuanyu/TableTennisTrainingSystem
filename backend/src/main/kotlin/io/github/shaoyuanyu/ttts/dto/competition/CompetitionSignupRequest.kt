package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionSignupRequest(
    val competitionId: String,
    val group: String,
)
