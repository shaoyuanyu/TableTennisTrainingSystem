package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureStatusPages() {
    install(StatusPages) {
        // 授权异常（登录验证失败、访问未授权资源）
        exception<UnauthorizedException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = HttpStatusCode.Unauthorized)
        }

        // 404异常（某项资源未找到）
        exception<NotFoundException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = HttpStatusCode.NotFound)
        }

        // 参数异常（前端向后端传递的参数不符合标准或无效）
        exception<BadRequestException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = HttpStatusCode.BadRequest)
        }
    }
}