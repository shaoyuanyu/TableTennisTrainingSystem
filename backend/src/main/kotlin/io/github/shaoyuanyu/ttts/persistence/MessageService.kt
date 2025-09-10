@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.message.Message
import io.github.shaoyuanyu.ttts.persistence.message.MessageEntity
import io.github.shaoyuanyu.ttts.persistence.message.MessageTable
import io.github.shaoyuanyu.ttts.persistence.message.expose
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MessageService(
    private val database: Database
) {
    /**
     * 发送消息
     * @param senderId 发送者ID，可为空（系统消息）
     * @param recipientId 接收者ID
     * @param title 消息标题
     * @param content 消息内容
     * @param type 消息类型 (system, appointment, evaluation, user)
     * @return 消息ID
     */
    fun sendMessage(
        senderId: String?,
        recipientId: String,
        title: String,
        content: String,
        type: String
    ): String = transaction(database) {
        val message = MessageEntity.new {
            this.sender = senderId?.let {
                UserEntity.findById(UUID.fromString(it))
            }
            this.recipient = UserEntity.Companion[UUID.fromString(recipientId)]
            this.title = title
            this.content = content
            this.type = type
            this.createdAt = Clock.System.now()
        }
        message.id.value.toString()
    }

    /**
     * 获取用户消息列表
     * @param userId 用户ID
     * @param type 消息类型过滤
     * @param unreadOnly 是否只获取未读消息
     * @param offset 分页偏移
     * @param limit 分页大小
     * @return 消息列表和总数量
     */
    fun getUserMessages(
        userId: String,
        type: String?,
        unreadOnly: Boolean,
        offset: Int,
        limit: Int
    ): Pair<List<Message>, Long> = transaction(database) {
        var query = MessageEntity.find {
            MessageTable.recipientId eq UUID.fromString(userId)
        }.toList()

        // 未读过滤
        if (unreadOnly) {
            query = query.filter { !it.isRead }.toList()
        }

        // 类型过滤
        if (type != null) {
            query = query.filter { it.type == type }.toList()
        }

        // 获取总数
        val totalCount = query.size.toLong()

        // 分页和排序
        val messages = query
            .sortedByDescending { it.createdAt }
            .drop(offset)
            .take(limit)
            .map { it.expose() }

        messages to totalCount
    }

    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    fun getUnreadCount(userId: String): Int = transaction(database) {
        MessageEntity.find {
            MessageTable.recipientId eq UUID.fromString(userId)
        }.count { !it.isRead }
    }

    /**
     * 标记消息为已读
     * @param messageId 消息ID
     * @param userId 用户ID（用于权限验证）
     * @throws SecurityException 如果用户无权操作该消息
     */
    fun markAsRead(messageId: String, userId: String) = transaction(database) {
        val message = MessageEntity.findById(UUID.fromString(messageId))
            ?: throw IllegalArgumentException("Message not found")

        // 验证用户权限（只能操作自己的消息）
        if (message.recipient.id.value != UUID.fromString(userId)) {
            throw SecurityException("User can only mark their own messages as read")
        }

        if (!message.isRead) {
            message.isRead = true
            message.readAt = Clock.System.now()
        }
    }

    /**
     * 删除消息
     * @param messageId 消息ID
     * @param userId 用户ID（用于权限验证）
     * @throws SecurityException 如果用户无权操作该消息
     */
    fun deleteMessage(messageId: String, userId: String) = transaction(database) {
        val message = MessageEntity.findById(UUID.fromString(messageId))
            ?: throw IllegalArgumentException("Message not found")

        // 验证用户权限（只能删除自己的消息）
        if (message.recipient.id.value != UUID.fromString(userId)) {
            throw SecurityException("User can only delete their own messages")
        }

        message.delete()
    }
}
