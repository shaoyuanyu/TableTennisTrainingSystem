package io.github.shaoyuanyu.ttts.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("SUPER_ADMIN")
    SUPER_ADMIN,

    @SerialName("CAMPUS_ADMIN")
    CAMPUS_ADMIN,

    @SerialName("COACH")
    COACH,

    @SerialName("STUDENT")
    STUDENT,
}