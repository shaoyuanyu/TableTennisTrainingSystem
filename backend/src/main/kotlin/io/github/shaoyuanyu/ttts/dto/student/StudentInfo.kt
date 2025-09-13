package io.github.shaoyuanyu.ttts.dto.student

import kotlinx.serialization.Serializable

/**
 * 学生信息数据传输对象
 * 封装学生特有的信息
 */
@Serializable
data class StudentInfo(
    val balance: Float = 0.0f,
    val maxCoach: Int = 3,
    val currentCoach: Int = 0
)