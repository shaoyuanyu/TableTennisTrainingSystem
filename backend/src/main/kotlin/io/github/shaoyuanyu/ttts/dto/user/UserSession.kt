package io.github.shaoyuanyu.ttts.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val userId: String, // uuid
    val username: String,
    val userRole: UserRole,
)
