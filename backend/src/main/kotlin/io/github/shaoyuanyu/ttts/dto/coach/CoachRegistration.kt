package io.github.shaoyuanyu.ttts.dto.coach

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import kotlinx.serialization.Serializable

/**
 * 教练注册数据传输对象
 * 包含教练注册所需的基本信息和教练特有信息
 */
@Serializable
data class CoachRegistration(
    // 用户基本信息
    val username: String,
    val plainPassword: String,
    val realName: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String,
    val campusId: Int,
    
    // 教练特有信息
    val photoUrl: String? = null,
    val achievements: String? = null,
    val level: String? = null,
    val hourlyRate: Float = 0.0f,
    
    // 固定角色为教练
    val role: UserRole = UserRole.COACH,
    
    // 默认状态
    val status: String = "pending_approval"
)

fun CoachRegistration.toUser() = User(
    username = username,
    plainPassword = plainPassword,
    realName = realName,
    gender = gender,
    age = age,
    phoneNumber = phoneNumber,
    email = email,
    campusId = campusId,
    role = role,
    status = status
)