@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.message

import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.datetime.timestamp
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object MessageTable : UUIDTable("messages") {
    val senderId = reference("sender_id", UserTable, ReferenceOption.CASCADE).nullable()
    val recipientId = reference("recipient_id", UserTable, ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val content = text("content")
    val type = varchar("type", 50) // system, appointment, evaluation, user
    val isRead = bool("is_read").default(false)
    val createdAt = timestamp("created_at")
    val readAt = timestamp("read_at").nullable()
}