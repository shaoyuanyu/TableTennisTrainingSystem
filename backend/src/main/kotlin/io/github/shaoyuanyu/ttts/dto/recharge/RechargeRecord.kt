@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.dto.recharge

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class RechargeRecord (
    val userId: String,
    val amount: Float,
    val createdAt: Instant
)
/**
 * 充值请求数据类
 */
@Serializable
data class RechargeRequest(
    val amount: Float,
)