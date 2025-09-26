package io.github.shaoyuanyu.ttts.dto.coach

import kotlinx.serialization.Serializable

@Serializable
data class CoachApproved(
    val uuid: String,
    val realName: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String,
    val photoUrl: String? = null,
    val achievements: String? = null,
)