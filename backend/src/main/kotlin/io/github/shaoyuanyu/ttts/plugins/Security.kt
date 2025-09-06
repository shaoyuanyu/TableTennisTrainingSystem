package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.cookie
import java.io.File

fun Application.configureSecurity() {
    val sessionStoragePath = environment.config.property("session.storage.path").getString()

    install(Sessions) {
        cookie<UserSession>(
            "user_session",
            directorySessionStorage(File(sessionStoragePath))
        ) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600 // TODO: 生产环境下 60 * 60 * 24 * 30 一个月
        }
    }

    install(Authentication) {
        // web-form 验证（登录用）
        form("auth-form") {
            // 声明 web-form 中用户名和密码字段名
            userParamName = "username"
            passwordParamName = "password"

            // more
        }

        // session 验证（student）
        session<UserSession>("auth-session-student") {
            validate { session ->
                if (session.userRole == UserRole.STUDENT) {
                    session
                } else {
//                    throw UserAuthorityException()
                }
            }
        }

        // session 验证（coach）
        session<UserSession>("auth-session-coach") {
            validate { session ->
                if (session.userRole == UserRole.COACH) {
                    session
                } else {
//                    throw UserAuthorityException()
                }
            }
        }

        // session 验证（super admin）
        session<UserSession>("auth-session-super-admin") {
            validate { session ->
                if (session.userRole == UserRole.SUPER_ADMIN) {
                    session
                } else {
//                    throw UserAuthorityException()
                }
            }
        }

        // session 验证（campus admin）
        session<UserSession>("auth-session-campus-admin") {
            validate { session ->
                if (session.userRole == UserRole.CAMPUS_ADMIN) {
                    session
                } else {
//                    throw UserAuthorityException()
                }
            }
        }
    }
}