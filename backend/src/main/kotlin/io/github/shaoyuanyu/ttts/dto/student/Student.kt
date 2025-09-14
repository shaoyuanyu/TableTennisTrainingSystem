package io.github.shaoyuanyu.ttts.dto.student

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val uuid: String? = null,
    val username: String,
    val balance: Float,
    val maxCoach: Int,
    val currentCoach: Int,
)
@Serializable
data class comsignupRequest(
    val group:String,
)
@Serializable
data class ComQueryRequest(
    val tableId: Int,              // 球台号
    val group: String,             // 小组名称
    val myUsername: String,        // 自己的用户名
    val opponentUsername: String?  // 对手的用户名（可能为null）
)
// 比赛信息数据类
@Serializable
data class CompetitionInfo(
    val tableId: Int,              // 球桌号
    val campusId: Int,          // 校园号/校区ID
    val group: String,             // 组别（甲、乙、丙）
    val player1Username: String,   // 选手1用户名
    val player2Username: String    // 选手2用户名
)