package io.github.shaoyuanyu.ttts.utils

import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.ktor.server.routing.RoutingCall
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

/**
 * 从会话中获取用户ID，如果未登录则抛出异常
 */
fun getUserIdFromCall(call: RoutingCall): String =
    call.sessions.get<UserSession>().let {
        if (it == null) {
            throw UnauthorizedException("未登录")
        }
        it.userId
    }
