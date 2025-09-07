package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.userRoutes(userService: UserService) {
    routing {
        route("/user") {
            // 无权限限制
            run {
                signup(userService) // 注册
            }

            // 无身份登录
            authenticate("auth-form") {
                login(userService)
            }

            // super admin
            authenticate("auth-session-super-admin") {
                createUser(userService)
            }
        }
    }
}

/**
 *  注册
 */
fun Route.signup(userService: UserService) {
    post("/signup") {
        val newUser = call.receive<User>()

        println(newUser)

        if (newUser.role == UserRole.STUDENT || newUser.role == UserRole.COACH) {
            val userId = userService.createUser(newUser)
            call.sessions.set(
                UserSession(userId = userId, username = newUser.username, userRole = newUser.role)
            )
            call.respondText("signup success")
        } else {
            call.respondText("role error")
        }
    }
}

/**
 *  登录
 */
fun Route.login(userService: UserService) {
    post("/login") {
        val userId = call.principal<UserIdPrincipal>()?.name.toString()

        println("user uuid: $userId\n\n\n")

        val user = userService.queryUserByUUID(userId)

        call.sessions.set(
            UserSession(userId = userId, username = user.username, userRole = user.role)
        )

        call.respond(user)
    }
}

/**
 * 管理员创建用户
 */
fun Route.createUser(userService: UserService) {
    post("/create") {
        val newUser = call.receive<User>()
    }
}
