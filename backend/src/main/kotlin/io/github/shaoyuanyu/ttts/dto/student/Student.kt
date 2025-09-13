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