package io.github.shaoyuanyu.ttts.dto.table

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TableOccupiedByGroup {
    @SerialName("A")
    A,

    @SerialName("B")
    B,

    @SerialName("C")
    C,

    @SerialName("ON_CLASS")
    ON_CLASS,

    @SerialName("FREE")
    FREE,
}
