package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.serialization.Serializable

@Serializable
data class CompetitionQueryRequest(
    val tableId: Int,              // 球台号
    val group: String,             // 小组名称
    val myUsername: String,        // 自己的用户名
    val opponentUsername: String?  // 对手的用户名（可能为null）
)
