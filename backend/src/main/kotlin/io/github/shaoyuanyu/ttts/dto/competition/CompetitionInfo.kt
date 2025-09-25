package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.serialization.Serializable

// 比赛信息
@Serializable
data class CompetitionInfo(
    val tableId: Int,              // 球桌号
    val campusId: Int,          // 校园号/校区ID
    val group: String,             // 组别（甲、乙、丙）
    val player1Username: String,   // 选手1用户名
    val player2Username: String    // 选手2用户名
)
