package io.github.shaoyuanyu.ttts.dto.student

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import kotlinx.serialization.Serializable

/**
 * 学生注册数据传输对象
 * 包含学生注册所需的基本信息和学生特有信息
 */
@Serializable
data class StudentRegistration(
    // 用户基本信息
    val username: String,
    val plainPassword: String,
    val realName: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String,
    val campusId: Int,
    
    // 学生特有信息
    val initialBalance: Float = 0.0f,
    val maxCoach: Int = 3,
    
    // 固定角色为学生
    val role: UserRole = UserRole.STUDENT,
    
    // 默认状态
    val status: String = "active"
)

fun StudentRegistration.toUser() = User(
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