package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.ktor.http.HttpStatusCode
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

            // 所有用户
            authenticate("auth-session-all") {
                logout()
                getSelfInfo(userService)
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

        if (newUser.role == UserRole.STUDENT || newUser.role == UserRole.COACH) {
            val userId = userService.createUser(newUser)
            call.sessions.set(
                UserSession(userId = userId, username = newUser.username, userRole = newUser.role)
            )
            call.respondText("signup success")
        } else {
            // TODO: 使用 Status Page
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

        val user = userService.queryUserByUUID(userId)

        call.sessions.set(
            UserSession(userId = userId, username = user.username, userRole = user.role)
        )

        call.respond(user)
    }
}

/**
 *  登出
 */
fun Route.logout() {
    post("/logout") {
        call.sessions.clear<UserSession>()
        call.response.status(HttpStatusCode.OK)
    }
}

/**
 *  获取自己的信息
 */
fun Route.getSelfInfo(userService: UserService) {
    get("/info") {
        val userId = call.sessions.get<UserSession>().let {
            if (it == null) {
                Exception("未登录")
            }
            it!!.userId
        }

        call.respond(
            userService.queryUserByUUID(userId)
        )
    }
}

/**
 * 管理员创建用户
 */
fun Route.createUser(userService: UserService) {
    post("/create") {
//        val newUser = call.receive<User>()
    }
}
