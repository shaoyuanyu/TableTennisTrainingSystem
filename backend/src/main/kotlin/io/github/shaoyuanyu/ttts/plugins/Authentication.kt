package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.form
import io.ktor.server.auth.session
import org.slf4j.LoggerFactory

internal val LOGGER = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.plugins.Authentication")

fun Application.configureAuthentication(userService: UserService) {
    install(Authentication) {
        // web-form 验证（登录用）
        form("auth-form") {
            // 声明 web-form 中用户名和密码字段名
            userParamName = "username"
            passwordParamName = "password"

            validate { credentials ->
                val uuid: String = userService.validateUser(credentials.name, credentials.password)

                LOGGER.info("用户id: $uuid 登录成功")

                UserIdPrincipal(uuid)
            }
        }

        // session 验证（all user）
        session<UserSession>("auth-session-all") {
            validate { session ->
                session
            }

            challenge {
                throw UnauthorizedException("未登录")
            }
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
