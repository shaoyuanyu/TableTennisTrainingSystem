package io.github.shaoyuanyu.ttts.dto.coach

import kotlinx.serialization.Serializable

/**
 * 教练信息数据传输对象
 * 封装教练特有的信息
 */
@Serializable
data class CoachInfo(
    val photoUrl: String? = null,
    val achievements: String? = null,
    val level: String? = null,
    val hourlyRate: Float = 0.0f,
    val balance: Float = 0.0f,
    val maxStudents: Int = 20,
    val currentStudents: Int = 0,
    val isApproved: Boolean = false,
    val approvedBy: String? =null
)
@Serializable
data class CoachRecord(
    var username: String,
    var realName: String,
    var gender: String,
    var age : Int,
    var photoUrl : String,
    var achievements : String,
    var level : String,
    var hourlyRate : Float,
    var currentStudents : Int,
)
@Serializable
data class QueryCoachRequest(
    val username: String
)
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
@Serializable
data class ApproveCoachRequest(
    val coachId: String,
    val level: String,
)
