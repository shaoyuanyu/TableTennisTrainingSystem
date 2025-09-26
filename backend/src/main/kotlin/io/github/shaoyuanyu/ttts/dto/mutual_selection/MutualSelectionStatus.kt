package io.github.shaoyuanyu.ttts.dto.mutual_selection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 学生-教练关系状态枚举
 */
@Serializable
enum class MutualSelectionStatus {
    /**
     * 待处理 - 学生已申请，教练尚未处理
     */
    @SerialName("PENDING")
    PENDING,

    /**
     * 已批准 - 教练已批准，等待系统激活
     */
    @SerialName("APPROVED")
    APPROVED,

    /**
     * 已拒绝 - 教练已拒绝申请
     */
    @SerialName("REJECTED")
    REJECTED,

    /**
     * 活跃 - 关系已生效
     */
    @SerialName("ACTIVE")
    ACTIVE,

    /**
     * 非活跃 - 关系已解除
     */
    @SerialName("INACTIVE")
    INACTIVE
}