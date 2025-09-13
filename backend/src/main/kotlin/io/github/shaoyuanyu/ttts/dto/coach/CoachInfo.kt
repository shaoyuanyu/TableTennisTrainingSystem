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
    val approvedBy: Int = -1
)