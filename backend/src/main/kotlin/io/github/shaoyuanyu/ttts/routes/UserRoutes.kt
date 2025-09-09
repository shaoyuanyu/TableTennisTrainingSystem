package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.ktor.http.*
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

            // 登录
            authenticate("auth-form") {
                login(userService)
            }

            // 所有用户
            authenticate("auth-session-all") {
                logout()
                getSelfInfo(userService)
                updateSelfInfo(userService)
            }

            // super admin
            authenticate("auth-session-super-admin") {
                createUser(userService)
                deleteUser(userService)
                resetCampusAdminPassword(userService)
            }
        }
    }
}

/**
 *  注册
 */
fun Route.signup(userService: UserService) {
    post("/signup") {
        val user = call.receive<User>()
        
        when (user.role) {
            UserRole.STUDENT, UserRole.COACH -> {
                val userId = userService.createUser(user)

                // 设置 session
                call.sessions.set(
                    UserSession(userId = userId, username = user.username, userRole = user.role)
                )
                
                call.response.status(HttpStatusCode.Created)
                call.respond(mapOf("message" to "注册成功", "userId" to userId))
            }
            
            else -> {
                throw BadRequestException("只允许注册为学生或教练")
            }
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
                throw Exception("未登录")
            }
            it.userId
        }

        call.respond(
            userService.queryUserByUUID(userId)
        )
    }
}

/**
 * 更新自己的信息
 *
 * 该函数用于更新当前登录用户的信息。支持更新基本信息以及学生和教练的专有信息。
 *
 * @param userService UserService实例，用于更新用户信息
 */
fun Route.updateSelfInfo(userService: UserService) {
    put("/info") {
        val updatedUser = call.receive<User>()
        
        // 从会话中获取用户ID，如果未登录则抛出异常
        val userId = call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
        
        // 确保用户只能更新自己的信息
        if (updatedUser.uuid != userId) {
            throw BadRequestException("只能更新自己的信息")
        }
        
        userService.updateUser(updatedUser)
        
        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf("message" to "用户信息更新成功"))
    }
}

/**
 * 管理员创建用户
 */
fun Route.createUser(userService: UserService) {
    post("/create") {
        val newUser = call.receive<User>()
        
        // 当前版本只允许创建校区管理员
        if (newUser.role != UserRole.CAMPUS_ADMIN) {
            throw BadRequestException("当前版本只能创建校区管理员")
        }
        
        val userId = userService.createUser(newUser)
        
        call.response.status(HttpStatusCode.Created)
        call.respond(mapOf("message" to "校区管理员创建成功", "userId" to userId))
    }
}

/**
 * 管理员删除用户
 */
fun Route.deleteUser(userService: UserService) {
    delete("/{userId}") {
        val userId = call.parameters["userId"] ?: throw BadRequestException("缺少用户ID参数")
        
        // 检查要删除的用户是否是校区管理员
        val user = userService.queryUserByUUID(userId)
        if (user.role != UserRole.CAMPUS_ADMIN) {
            throw BadRequestException("只能删除校区管理员")
        }
        
        userService.deleteUser(userId)
        
        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf("message" to "校区管理员删除成功"))
    }
}

/**
 * 重置校区管理员密码
 */
fun Route.resetCampusAdminPassword(userService: UserService) {
    put("/{userId}/reset-password") {
        val userId = call.parameters["userId"] ?: throw BadRequestException("缺少用户ID参数")
        
        // 检查要重置密码的用户是否是校区管理员
        val user = userService.queryUserByUUID(userId)
        if (user.role != UserRole.CAMPUS_ADMIN) {
            throw BadRequestException("只能重置校区管理员密码")
        }
        
        // 重置密码为默认密码 "123456"
        // TODO: 默认密码改为通过配置文件读取
        val defaultPassword = "123456"
        userService.updateUserPassword(userId, defaultPassword, defaultPassword)
        
        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf("message" to "校区管理员密码已重置为默认密码"))
    }
}