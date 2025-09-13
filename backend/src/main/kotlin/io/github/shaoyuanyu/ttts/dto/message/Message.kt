@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.dto.message

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Message(
    val id: String,
    val senderId: String?,
    val recipientId: String,
    val title: String,
    val content: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: Instant,
    val readAt: Instant?
)

@Serializable
data class CreateMessageRequest(
    val recipientId: String,
    val title: String,
    val content: String,
    val type: String
)

@Serializable
data class UpdateMessageRequest(
    val isRead: Boolean? = null
)

@Serializable
data class MessageListResponse(
    val messages: List<Message>,
    val totalCount: Long
)

@Serializable
data class UnreadCountResponse(
    val count: Int
)