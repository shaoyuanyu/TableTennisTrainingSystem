package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
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
                changeSelfPassword(userService)
            }

            // campus admin
            authenticate("auth-session-admin") {
                getTotalNum(userService)
            }

            // super admin
            authenticate("auth-session-super-admin") {
                createUser(userService)
                deleteUser(userService)
                resetCampusAdminPassword(userService)
                getAllUsers(userService)
                getUserByUsername(userService)

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
        val userId = getUserIdFromCall(call)

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
        val userId = getUserIdFromCall(call)

        val updatedUser = call.receive<User>()

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
 * 修改密码
 *
 * 该函数用于用户修改自己的密码，需要提供旧密码进行验证
 *
 * @param userService UserService实例，用于修改密码
 */
fun Route.changeSelfPassword(userService: UserService) {
    put("/change-password") {
        val userId = getUserIdFromCall(call)

        // 从表单数据中获取密码信息
        val formData = call.receiveParameters()
        val oldPassword = formData["oldPassword"] ?: throw BadRequestException("缺少旧密码参数")
        val newPassword = formData["newPassword"] ?: throw BadRequestException("缺少新密码参数")

        userService.changeUserPassword(userId, oldPassword, newPassword)
        
        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf("message" to "密码修改成功"))
    }
}

/**
 * 获取所有用户（分页）
 *
 * 该函数用于查询用户列表，支持分页和过滤。仅限超级管理员
 *
 * @param userService UserService实例，用于查询用户列表
 */
fun Route.getAllUsers(userService: UserService) {
    get("/users") {
        // 获取查询参数
        val page = 1
        val size = 100
        val role = call.request.queryParameters["role"]?.let { UserRole.valueOf(it) }
        val campusId = call.request.queryParameters["campusId"]?.toIntOrNull()
        
        val users = userService.queryUsers(page, size, role, campusId)
        
        call.respond(mapOf(
            "users" to users,
            "page" to page,
            "size" to size,
        ))
    }
}
/**
 * 获取用户总数
 */
fun Route.getTotalNum(userService: UserService){
    get("/totalUserNum") {
        val userId=getUserIdFromCall(call)
        val campusId=userService.queryUserByUUID(userId).campusId
        // 获取用户总数
        val totalCount = userService.countUsers(campusId)
        call.respond(mapOf("totalCount" to totalCount))
    }
}
/**
 * 管理员创建用户
 */
fun Route.createUser(userService: UserService) {
    post("/create") {
        val newUser = call.receive<User>()
        
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
        
        // 重置密码为默认密码 "123456"
        // TODO: 默认密码改为通过配置文件读取
        val defaultPassword = "123456"
        userService.changeUserPassword(userId, defaultPassword, defaultPassword)
        
        call.response.status(HttpStatusCode.OK)
        call.respond(mapOf("message" to "校区管理员密码已重置为默认密码"))
    }
}

/**
 * 根据用户名获取用户信息
 *
 * 该函数用于超级管理员根据用户名查询用户详细信息。
 *
 * @param userService UserService实例，用于查询用户信息
 */
fun Route.getUserByUsername(userService: UserService) {
    get("/users/{username}") {
        val username = call.parameters["username"] ?: throw BadRequestException("缺少用户名参数")
        
        try {
            val user = userService.queryUserByUsername(username)
            call.respond(user)
        } catch (_: NotFoundException) {
            throw BadRequestException("用户不存在")
        }
    }
}