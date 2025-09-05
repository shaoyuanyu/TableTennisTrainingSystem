package plugins

import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600 // TODO: 生产环境下 60 * 60 * 24 * 30 一个月
        }
    }

    install(Authentication) {
        // web-form 验证（登录用）
        form("auth-form") {
            //
        }
    }
}