package io.github.shaoyuanyu.ttts.dto.table

import kotlinx.serialization.Serializable

@Serializable
data class Table(
    val id: String,
    val status: String,
    val group: String,
    val indexInCampus: Int,
    val campusId: Int,
    val campusName: String,
)