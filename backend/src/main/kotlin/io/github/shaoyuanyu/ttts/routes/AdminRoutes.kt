package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.AdminService
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 管理员路由配置
 */
fun Application.adminRoutes(adminService: AdminService) {
    routing {
        route("/admin") {
            // 管理员权限路由
            authenticate("auth-session-admin") {
                getCampusStudents(adminService)
                getCampusCoaches(adminService)
            }
        }
    }
}

/**
 * 获取当前校区所有学生
 */
fun Route.getCampusStudents(adminService: AdminService) {
    get("/students") {
        // 从会话中获取用户ID
        val adminId = getUserIdFromCall(call)

        // 获取查询参数
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        // 参数验证
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }

        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        val students = adminService.getCampusStudents(adminId, page, size)

        call.respond(HttpStatusCode.OK, students)
    }
}

/**
 * 获取当前校区所有教练
 */
fun Route.getCampusCoaches(adminService: AdminService) {
    get("/coaches") {
        // 从会话中获取用户ID
        val adminId = getUserIdFromCall(call)

        // 获取查询参数
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        // 参数验证
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }

        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        val coaches = adminService.getCampusCoaches(adminId, page, size)

        call.respond(HttpStatusCode.OK, coaches)
    }
}