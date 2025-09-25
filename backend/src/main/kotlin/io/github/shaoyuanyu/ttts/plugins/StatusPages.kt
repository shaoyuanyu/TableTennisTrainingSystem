package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        // 授权异常（登录验证失败、访问未授权资源）
        exception<UnauthorizedException> { call, cause ->
            LOGGER.warn("Unauthorized: ${cause.message}, caused from: ${call.request}")
            call.respondText(text = cause.message ?: "Unauthorized", status = HttpStatusCode.Unauthorized)
        }

        // 404异常（某项资源未找到）
        exception<NotFoundException> { call, cause ->
            LOGGER.warn("Not Found: ${cause.message}, caused from: ${call.request}")
            call.respondText(text = cause.message ?: "Not Found", status = HttpStatusCode.NotFound)
        }

        // 参数异常（前端向后端传递的参数不符合标准或无效）
        exception<BadRequestException> { call, cause ->
            LOGGER.warn("Bad Request: ${cause.message}, caused from: ${call.request}")
            call.respondText(text = cause.message ?: "Bad Request", status = HttpStatusCode.BadRequest)
        }
        
        // 非法参数异常
        exception<IllegalArgumentException> { call, cause ->
            LOGGER.warn("Illegal Argument: ${cause.message}, caused from: ${call.request}")
            call.respondText(text = cause.message ?: "Bad Request", status = HttpStatusCode.BadRequest)
        }
        
        // 安全异常（权限不足等）
        exception<SecurityException> { call, cause ->
            LOGGER.warn("Forbidden: ${cause.message}, caused from: ${call.request}")
            call.respondText(text = cause.message ?: "Forbidden", status = HttpStatusCode.Forbidden)
        }
        
        // 通用异常处理
        exception<Throwable> { call, cause ->
            LOGGER.error("Internal Server Error: ${cause.message}, caused from: ${call.request}")
            call.respondText(text = cause.message ?: "Internal Server Error", status = HttpStatusCode.InternalServerError)
        }
    }
}