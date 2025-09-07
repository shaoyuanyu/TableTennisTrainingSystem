package io.github.shaoyuanyu.ttts.dto.user

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uuid: String? = null,
    val username: String,
    val plainPassword: String? = null,
    val encryptedPassword: String? = null,
    val realName: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String,
    val campusId: Int,
    val role: UserRole,
    val status: String,
    val createdAt: LocalDateTime? = null,
    val lastLoginAt: LocalDateTime? = null,
)
