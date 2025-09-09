package io.github.shaoyuanyu.ttts.dto.user

import io.github.shaoyuanyu.ttts.dto.coach.CoachRegistration
import io.github.shaoyuanyu.ttts.dto.student.StudentRegistration
import kotlinx.serialization.Serializable

/**
 * 统一注册请求数据传输对象
 * 用于接收前端注册请求，包含学生或教练的注册信息
 */
@Serializable
data class RegistrationRequest(
    val role: UserRole,
    val studentRegistration: StudentRegistration? = null,
    val coachRegistration: CoachRegistration? = null
)