package io.github.shaoyuanyu.ttts.dto.user

import io.github.shaoyuanyu.ttts.dto.student.StudentInfo
import io.github.shaoyuanyu.ttts.dto.coach.CoachInfo
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
    
    // 角色特定信息
    val studentInfo: StudentInfo? = null,
    val coachInfo: CoachInfo? = null,
)