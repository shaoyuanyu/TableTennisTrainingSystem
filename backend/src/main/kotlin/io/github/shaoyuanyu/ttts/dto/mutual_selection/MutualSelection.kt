package io.github.shaoyuanyu.ttts.dto.mutual_selection

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class MutualSelection(
    val selectionId: String,
    val coachId: String,
    val coachName: String? = null,
    val studentId: String,
    val studentName: String? = null,
    val status: MutualSelectionStatus,
    val applicationTime: LocalDateTime,
    // 移除其他时间字段，只保留申请时间
)