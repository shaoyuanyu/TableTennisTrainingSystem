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