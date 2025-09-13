package io.github.shaoyuanyu.ttts.dto.student_coach

import kotlinx.serialization.Serializable

/**
 * 学生-教练关系状态枚举
 */
@Serializable
enum class StudentCoachRelationStatus(val value: String) {
    /**
     * 待处理 - 学生已申请，教练尚未处理
     */
    PENDING("pending"),

    /**
     * 已批准 - 教练已批准，等待系统激活
     */
    APPROVED("approved"),

    /**
     * 已拒绝 - 教练已拒绝申请
     */
    REJECTED("rejected"),

    /**
     * 活跃 - 关系已生效
     */
    ACTIVE("active"),

    /**
     * 非活跃 - 关系已解除
     */
    INACTIVE("inactive")
}