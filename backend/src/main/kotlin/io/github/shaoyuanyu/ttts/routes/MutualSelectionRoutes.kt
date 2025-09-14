@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.SecurityException
import io.github.shaoyuanyu.ttts.persistence.MutualSelectionService
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Clock

fun Application.mutualSelectionRoutes(mutualSelectionService: MutualSelectionService) {
    routing {
        route("/mutual-selection") {
            // 学生权限路由
            authenticate("auth-session-student") {
                applyForCoach(mutualSelectionService)
                getStudentApplications(mutualSelectionService)
                withdrawApplication(mutualSelectionService)
            }

            // 教练权限路由
            authenticate("auth-session-coach") {
                getCoachApplications(mutualSelectionService)
                reviewApplication(mutualSelectionService)
            }

            // 管理员权限路由
            authenticate("auth-session-campus-admin", "auth-session-super-admin") {
                adminCreateRelation(mutualSelectionService)
                getAllRelations(mutualSelectionService)
            }
        }
    }
}

/**
 * 学生申请教练
 */
fun Route.applyForCoach(mutualSelectionService: MutualSelectionService) {
    post("/apply") {
        val userId = getUserIdFromCall(call)
        
        // 接收请求参数
        val params = call.receiveParameters()
        val coachId = params["coachId"] ?: throw BadRequestException("缺少教练ID参数")
        params["expectedStartTime"] ?: Clock.System.now().toString() //throw BadRequestException("缺少预期开始时间参数")

//        val expectedStartTime = expectedStartTimeStr.toLongOrNull()?.let {
//            Instant.fromEpochMilliseconds(it)
//        } ?: throw BadRequestException("时间格式错误")
        
        try {
            val application = mutualSelectionService.applyForCoach(
                studentId = userId,
                coachId = coachId,
                expectedStartTime = Clock.System.now() // TODO: 处理时间格式
            )
            
            call.respond(HttpStatusCode.Created, application)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "申请失败")
        }
    }
}

/**
 * 获取学生申请记录
 */
fun Route.getStudentApplications(mutualSelectionService: MutualSelectionService) {
    get("/student-applications") {
        val userId = getUserIdFromCall(call)

        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
        
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }
        
        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        val applications = mutualSelectionService.getStudentApplications(
            studentUUID = userId,
            page = page,
            size = size
        )

        call.respond(HttpStatusCode.OK, applications)
    }
}

/**
 * 学生撤回申请
 */
fun Route.withdrawApplication(mutualSelectionService: MutualSelectionService) {
    post("/withdraw") {
        val userId = getUserIdFromCall(call)
        
        val params = call.receiveParameters()
        val relationId = params["relationId"] ?: throw BadRequestException("缺少关系ID参数")
        
        try {
            val result = mutualSelectionService.withdrawApplication(
                studentUUID = userId,
                relationId = relationId
            )
            
            call.respond(HttpStatusCode.OK, result)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "撤回申请失败")
        } catch (e: SecurityException) {
            throw SecurityException(e.message ?: "无权限撤回该申请")
        }
    }
}

/**
 * 获取教练收到的申请记录
 */
fun Route.getCoachApplications(mutualSelectionService: MutualSelectionService) {
    get("/coach-applications") {
        val userId = getUserIdFromCall(call)
        
        val status = call.request.queryParameters["status"]
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
        
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }
        
        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }
        
        try {
            val applications = mutualSelectionService.getCoachApplications(
                coachUUID = userId,
                status = status,
                page = page,
                size = size
            )
            
            call.respond(HttpStatusCode.OK, applications)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "获取申请记录失败")
        }
    }
}

/**
 * 教练审核学生申请
 */
fun Route.reviewApplication(mutualSelectionService: MutualSelectionService) {
    post("/review") {
        val userId = getUserIdFromCall(call)

        val params = call.receiveParameters()
        val relationId = params["selectionId"] ?: throw BadRequestException("缺少关系ID参数")
        val approveStr = params["approve"] ?: throw BadRequestException("缺少审核结果参数")
        val approve = approveStr.toBooleanStrictOrNull() ?: throw BadRequestException("审核结果参数格式错误")
        
        try {
            val result = mutualSelectionService.reviewApplication(
                coachUUID = userId,
                relationId = relationId,
                approve = approve
            )
            
            call.respond(HttpStatusCode.OK, result)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "审核申请失败")
        } catch (e: SecurityException) {
            throw SecurityException(e.message ?: "无权限审核该申请")
        }
    }
}

/**
 * 管理员直接建立关系
 */
fun Route.adminCreateRelation(mutualSelectionService: MutualSelectionService) {
    post("/admin-create") {
        val params = call.receiveParameters()
        val studentId = params["studentId"] ?: throw BadRequestException("缺少学生ID参数")
        val coachId = params["coachId"] ?: throw BadRequestException("缺少教练ID参数")
        
        try {
            val result = mutualSelectionService.adminCreateRelation(
                studentUUID = studentId,
                coachUUID = coachId
            )
            
            call.respond(HttpStatusCode.Created, result)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "创建关系失败")
        }
    }
}

/**
 * 获取所有关系记录（管理员使用）
 */
fun Route.getAllRelations(mutualSelectionService: MutualSelectionService) {
    get("/all") {
        val status = call.request.queryParameters["status"]
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
        
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }
        
        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }
        
        try {
            val relations = mutualSelectionService.getAllRelations(
                status = status,
                page = page,
                size = size
            )
            
            call.respond(HttpStatusCode.OK, relations)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException(e.message ?: "获取关系记录失败")
        }
    }
}