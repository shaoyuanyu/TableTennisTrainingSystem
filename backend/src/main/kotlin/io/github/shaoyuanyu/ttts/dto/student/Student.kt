package io.github.shaoyuanyu.ttts.dto.student

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val uuid: String? = null,
    val username: String,
    val balance: Float,
    val maxCoach: Int,
    val currentCoach: Int,
    val createdAt: LocalDateTime? = null,
    val lastLoginAt: LocalDateTime? = null,
)