package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Competition(
    val id: String? = null,
    val name: String,
    val type: String,
    val campusId: Int,
    val campusName: String? = null,
    val date: LocalDate,
    val registrationDeadline: LocalDate,
    val fee: Float,
    val description: String,
)
