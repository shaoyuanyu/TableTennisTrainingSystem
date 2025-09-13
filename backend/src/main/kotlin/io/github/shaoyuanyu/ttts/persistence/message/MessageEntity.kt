@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.message

import io.github.shaoyuanyu.ttts.dto.message.Message
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*
import kotlin.time.ExperimentalTime

class MessageEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<MessageEntity>(MessageTable)
    
    var sender by UserEntity optionalReferencedOn MessageTable.senderId
    var recipient by UserEntity referencedOn MessageTable.recipientId
    var title by MessageTable.title
    var content by MessageTable.content
    var type by MessageTable.type
    var isRead by MessageTable.isRead
    var createdAt by MessageTable.createdAt
    var readAt by MessageTable.readAt
}

fun MessageEntity.expose(): Message {
    return Message(
        id = id.value.toString(),
        senderId = sender?.id?.value?.toString(),
        recipientId = recipient.id.value.toString(),
        title = title,
        content = content,
        type = type,
        isRead = isRead,
        createdAt = createdAt,
        readAt = readAt
    )
}