package io.github.shaoyuanyu.ttts.dto.log

import kotlinx.serialization.Serializable

@Serializable
data class Log(
    val content: List<String>,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Int,
)
