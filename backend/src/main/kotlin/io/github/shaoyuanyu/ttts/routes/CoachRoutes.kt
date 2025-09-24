@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.coach.ApproveCoachRequest
import io.github.shaoyuanyu.ttts.dto.coach.QueryCoachRequest
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.CoachService
import io.github.shaoyuanyu.ttts.plugins.LOGGER
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.coachRoutes(coachService: CoachService) {
    routing {
        route("/coach") {
            // 无权限限制
            run {
            }

            // 所有登录用户
            authenticate("auth-session-all") {
                getAllCoach(coachService)
                queryCoach(coachService)
            }

            // 教练权限
            authenticate("auth-session-coach") {
            }
            
            //管理员权限
            authenticate ("auth-session-campus-admin"){
                getCampusCoachesNotApproved(coachService)
                approveCoach(coachService)
            }

            // 超级管理员权限
            authenticate("auth-session-super-admin") {
                getAllCoachesNotApproved(coachService)
            }
        }
    }
}

/**
 * 获取所有教练（分页）
 */
fun Route.getAllCoach(coachService: CoachService) {
    get("/coaches") {
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

        val coaches = coachService.getAllCoaches(page, size)

        LOGGER.info(coaches.toString())

        call.respond(HttpStatusCode.OK, coaches)
    }
}

/**
 * 根据用户名查询教练信息
 */
fun Route.queryCoach(coachService: CoachService) {
    post("/queryCoach") {
        val request = call.receive<QueryCoachRequest>()
        val username = request.username

        if (username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to "用户名不能为空"))
            return@post
        }

        // 调用服务层函数
        val coachRecord = coachService.queryCoachesByUsername(username)

        // 返回成功响应
        call.respond(HttpStatusCode.OK, coachRecord)

    }
}

/**
 * 获取本校区所有未审核通过的教练（分页）
 */
fun  Route.getCampusCoachesNotApproved(coachService: CoachService) {
    get("/coaches/campusNotApproved") {
        // 从会话中获取用户ID，如果未登录则抛出异常
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

        val coaches = coachService.getCampusCoachNotApproved(adminId,page,size)

        call.respond(
            HttpStatusCode.OK,
            coaches
        )
    }
}

/**
 * 获取所有未审核通过的教练（分页） - 仅限超级管理员
 */
fun Route.getAllCoachesNotApproved(coachService: CoachService) {
    get("/coaches/allNotApproved") {
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

        val coaches = coachService.getAllCoachNotApproved(page,size)

        call.respond(
            HttpStatusCode.OK,
            coaches
        )
    }
}

/**
 * 审核教练 - 仅限超级管理员和校区管理员
 * 接收教练uuid与新的level
 */
fun Route.approveCoach(coachService: CoachService) {
    post("/approve") {
        val adminId = getUserIdFromCall(call)

        val request = call.receive<ApproveCoachRequest>()
        val coachId = request.coachId
        val newLevel = request.level

        // 调用服务层函数
        coachService.updateCoachLevel(coachId, adminId,newLevel)

        // 返回成功响应
        call.respond(HttpStatusCode.OK)
    }
}