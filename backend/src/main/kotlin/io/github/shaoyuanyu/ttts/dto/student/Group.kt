package io.github.shaoyuanyu.ttts.dto.student

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Group{
    @SerialName("A")
    A,

    @SerialName("B")
    B,

    @SerialName("C")
    C,

    @SerialName("onclass")
    onclass,
}
@Serializable
enum class Status{
    @SerialName("free")
    free,

    @SerialName("partlyoccupy")
    partlyoccupy,

    @SerialName("occupy")
    occupy,
}