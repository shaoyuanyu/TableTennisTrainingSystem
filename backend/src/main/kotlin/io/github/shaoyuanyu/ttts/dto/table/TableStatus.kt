package io.github.shaoyuanyu.ttts.dto.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TableStatus {
    @SerialName("FREE")
    FREE,

    @SerialName("PARTLY_OCCUPIED")
    PARTLY_OCCUPIED,

    @SerialName("OCCUPIED")
    OCCUPIED,
}