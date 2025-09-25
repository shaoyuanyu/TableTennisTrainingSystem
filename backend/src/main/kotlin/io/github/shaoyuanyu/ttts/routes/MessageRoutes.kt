package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.message.CreateMessageRequest
import io.github.shaoyuanyu.ttts.dto.message.MessageListResponse
import io.github.shaoyuanyu.ttts.dto.message.UnreadCountResponse
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.MessageService
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.routes.MessageRoutes")

/**
 * 消息路由配置
 *
 * @param messageService MessageService实例，用于处理消息相关业务逻辑
 */
fun Application.messageRoutes(messageService: MessageService) {
    routing {
        route("/messages") {
            authenticate("auth-session-all") {
                getMessages(messageService)
                getUnreadCount(messageService)
                markMessageAsRead(messageService)
                markAllMessagesAsRead(messageService)
                deleteMessage(messageService)
            }

            // 发送消息（当前版本仅限管理员）
            authenticate("auth-session-campus-admin", "auth-session-super-admin") {
                sendMessage(messageService)
            }
        }
    }
}

/**
 * 获取当前用户的消息列表
 *
 * @param messageService MessageService实例，用于获取消息列表
 */
fun Route.getMessages(messageService: MessageService) {
    get {
        val userId = getUserIdFromCall(call)

        val type = call.request.queryParameters["type"]
        val unreadOnly = call.request.queryParameters["unreadOnly"]?.toBoolean() ?: false
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 20

        // 参数验证
        if (page < 0) {
            throw BadRequestException("页码必须大于等于0")
        }

        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        val (messages, totalCount) = messageService.getUserMessages(
            userId,
            type,
            unreadOnly,
            page * size,
            size
        )

        call.respond(
            HttpStatusCode.OK,
            MessageListResponse(messages, totalCount)
        )
    }
}

/**
 * 获取未读消息数量
 *
 * @param messageService MessageService实例，用于获取未读消息数量
 */
fun Route.getUnreadCount(messageService: MessageService) {
    get("/unread-count") {
        val userId = getUserIdFromCall(call)

        val count = messageService.getUnreadCount(userId)
        call.respond(HttpStatusCode.OK, UnreadCountResponse(count))
    }
}

/**
 * 发送消息（仅限管理员）
 *
 * @param messageService MessageService实例，用于发送消息
 */
fun Route.sendMessage(messageService: MessageService) {
    post {
        val userId = getUserIdFromCall(call)

        val request = call.receive<CreateMessageRequest>()

        val messageId: String = messageService.sendMessage(
            senderId = userId,
            recipientId = request.recipientId,
            title = request.title,
            content = request.content,
            type = request.type
        )

        call.response.status(HttpStatusCode.Created)
        call.respond(mapOf("id" to messageId, "message" to "消息发送成功"))
    }
}

/**
 * 标记单条消息为已读
 *
 * @param messageService MessageService实例，用于标记消息为已读
 */
fun Route.markMessageAsRead(messageService: MessageService) {
    put("/{id}/read") {
        val userId = getUserIdFromCall(call)

        val messageId = call.parameters["id"]
            ?: throw BadRequestException("Message ID is required")

        messageService.markAsRead(messageId, userId)
        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf<String, Any>("success" to true, "message" to "消息已标记为已读"))
    }
}

/**
 * 标记所有消息为已读
 *
 * @param messageService MessageService实例，用于标记所有消息为已读
 */
fun Route.markAllMessagesAsRead(messageService: MessageService) {
    put("/read-all") {
        val userId = getUserIdFromCall(call)

        // 获取用户所有未读消息并标记为已读
        val (messages, _) = messageService.getUserMessages(
            userId = userId,
            type = null,
            unreadOnly = true,
            offset = 0,
            limit = 1000 // 限制一次处理的消息数量
        )

        var successCount = 0
        messages.forEach { message ->
            try {
                messageService.markAsRead(message.id, userId)
                successCount++
            } catch (e: Exception) {
                logger.warn("Failed to mark message ${message.id} as read", e)
            }
        }

        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf<String, Any>(
            "success" to true,
            "message" to "成功标记 $successCount 条消息为已读",
            "count" to successCount
        ))
    }
}

/**
 * 删除消息
 *
 * @param messageService MessageService实例，用于删除消息
 */
fun Route.deleteMessage(messageService: MessageService) {
    delete("/{id}") {
        val userId = getUserIdFromCall(call)

        val messageId = call.parameters["id"]
            ?: throw BadRequestException("Message ID is required")

        messageService.deleteMessage(messageId, userId)
        call.response.status(HttpStatusCode.NoContent)
        call.respond(mapOf("message" to "消息删除成功"))
    }
}