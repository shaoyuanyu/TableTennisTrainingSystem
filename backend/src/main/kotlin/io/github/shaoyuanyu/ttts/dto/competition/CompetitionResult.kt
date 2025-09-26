package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionResult(
    val winnerName: String,    // 获胜者用户名
    val loserName: String      // 失败者用户名
)