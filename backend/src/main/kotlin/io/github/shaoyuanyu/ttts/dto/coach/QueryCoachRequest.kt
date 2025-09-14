package io.github.shaoyuanyu.ttts.dto.coach

import kotlinx.serialization.Serializable

@Serializable
data class QueryCoachRequest(
    val username: String
)