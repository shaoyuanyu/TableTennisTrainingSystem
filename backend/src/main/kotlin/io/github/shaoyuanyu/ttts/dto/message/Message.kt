package io.github.shaoyuanyu.ttts.dto.message

import java.time.Instant

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

data class CreateMessageRequest(
    val recipientId: String,
    val title: String,
    val content: String,
    val type: String
)

data class UpdateMessageRequest(
    val isRead: Boolean? = null
)

data class MessageListResponse(
    val messages: List<Message>,
    val totalCount: Long
)

data class UnreadCountResponse(
    val count: Int
)