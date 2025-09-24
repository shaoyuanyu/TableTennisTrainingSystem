package io.github.shaoyuanyu.ttts.dto.student

import kotlinx.serialization.Serializable

@Serializable
data class StudentRecord(
    val studentId: String,
    val username: String,
    val realName: String,
    val gender: String,
    val age: Int,
    val campusId: Int,
    val photoUrl: String,
    val skillLevel: String,
    val balance: Float,
    val maxCoach: Int,
    val currentCoach: Int
)