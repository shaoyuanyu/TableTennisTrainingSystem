@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.coach.ApproveCoachRequest
import io.github.shaoyuanyu.ttts.dto.coach.QueryCoachRequest
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.CoachService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.coachRoutes(coachService: CoachService) {
    routing {
        route("/coach") {
            // 无权限限制
            run {

            }
            // 所有登录用户
            authenticate("auth-session-all") {

            }

            // 学生权限
            authenticate("auth-session-student") {
                getAllCoach(coachService)
                queryCoach(coachService)
            }
            
            // 教练权限
            authenticate("auth-session-coach") {
                reviewStudentApplication(coachService)
                getCoachApplications(coachService)
                getPendingApplicationCount(coachService)
            }
            
            //管理员权限
            authenticate ("auth-session-campus-admin"){
                getCampusCoachesNotApproved(coachService)
                approveCoach(coachService)
            }
            // 超级管理员权限
            authenticate("auth-session-super-admin") {
                getAllCoachesNotApproved(coachService)
                approveCoach(coachService)
            }
        }
    }
}

/**
 * 获取本校区所有教练（分页）
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

        val coaches = coachService.getAllCoaches(page,size)

        call.respond(HttpStatusCode.OK,
            coaches
        )
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
 * 教练审核学生申请
 */
fun Route.reviewStudentApplication(coachService: CoachService) {
    post("/review-application") {
        // 从会话中获取教练ID
        val coachId = call.sessions.get<UserSession>()?.userId
            ?: throw UnauthorizedException("未登录")

        // 接收请求参数
        val params = call.receiveParameters()
        val relationId = params["relationId"] ?: throw BadRequestException("关系ID不能为空")
        val approveStr = params["approve"] ?: throw BadRequestException("批准状态不能为空")
        val message = params["message"]
        
        val approve = approveStr.toBoolean()
        
        try {
            // 调用服务层函数
            val relation = coachService.reviewStudentApplication(coachId, relationId, approve, message)

            // 返回成功响应
            call.respond(HttpStatusCode.OK, mapOf(
                "success" to true,
                "data" to mapOf(
                    "id" to relation.id.value.toString(),
                    "status" to relation.status,
                    "updatedAt" to relation.updatedAt.toString()
                )
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("success" to false, "message" to e.message))
        }
    }
}

/**
 * 获取教练申请记录
 */
fun Route.getCoachApplications(coachService: CoachService) {
    get("/applications") {
        // 从会话中获取教练ID
        val coachId = call.sessions.get<UserSession>()?.userId
            ?: throw UnauthorizedException("未登录")

        // 获取查询参数
        val status = call.request.queryParameters["status"]
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        // 参数验证
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }

        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        try {
            // 调用服务层函数
            val applications = coachService.getCoachApplications(coachId, status, page, size)

            // 返回成功响应
            call.respond(HttpStatusCode.OK, mapOf(
                "success" to true,
                "data" to applications.map { relation ->
                    mapOf(
                        "id" to relation.id.value.toString(),
                        "studentName" to relation.studentID.userId.realName,
                        "status" to relation.status,
                        "applicationTime" to relation.applicationTime.toString(),
                        "expectedStartTime" to relation.expectedStartTime.toString(),
                        "studentMessage" to relation.studentMessage,
                        "coachMessage" to relation.coachMessage
                    )
                }
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("success" to false, "message" to e.message))
        }
    }
}

/**
 * 获取教练待处理申请数量
 */
fun Route.getPendingApplicationCount(coachService: CoachService) {
    get("/pending-count") {
        // 从会话中获取教练ID
        val coachId = call.sessions.get<UserSession>()?.userId
            ?: throw UnauthorizedException("未登录")

        try {
            // 调用服务层函数
            val count = coachService.getPendingApplicationCount(coachId)

            // 返回成功响应
            call.respond(HttpStatusCode.OK, mapOf(
                "success" to true,
                "data" to mapOf("count" to count)
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("success" to false, "message" to e.message))
        }
    }
}

/**
 * 获取本校区所有未审核通过的教练（分页）
 */
fun  Route.getCampusCoachesNotApproved(coachService: CoachService) {
    get("/coaches/campusNotApproved") {
        // 从会话中获取用户ID，如果未登录则抛出异常
        val adminId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
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
        val adminId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
        val request = call.receive<ApproveCoachRequest>()
        val coachId = request.coachId
        val newlevel = request.level

        // 调用服务层函数
        coachService.updateCoachLevel(coachId, adminId,newlevel)

        // 返回成功响应
        call.respond(HttpStatusCode.OK)
    }
}