@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.mutual_selection.MutualSelectionStatus
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.MutualSelectionService
import io.github.shaoyuanyu.ttts.plugins.LOGGER
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.mutualSelectionRoutes(mutualSelectionService: MutualSelectionService) {
    routing {
        route("/mutual-selection") {
            // 学生权限路由
            authenticate("auth-session-student") {
                applyForCoach(mutualSelectionService)
                getStudentApplications(mutualSelectionService)
                withdrawApplication(mutualSelectionService)
                getStudentCurrentCoaches(mutualSelectionService)
                cancelApprovedRelation(mutualSelectionService)
            }

            // 教练权限路由
            authenticate("auth-session-coach") {
                getCoachApplications(mutualSelectionService)
                reviewApplication(mutualSelectionService)
                getCoachCurrentStudents(mutualSelectionService)
                getCoachHistoricalStudents(mutualSelectionService)
            }

            // 管理员权限路由
            authenticate("auth-session-campus-admin", "auth-session-super-admin") {
                adminCreateRelation(mutualSelectionService)
                getAllRelations(mutualSelectionService)
                getRelationById(mutualSelectionService)
                updateRelation(mutualSelectionService)
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

        LOGGER.info(params.toString())

        val application = mutualSelectionService.applyForCoach(
            studentId = userId,
            coachId = coachId
        )
        
        call.respond(HttpStatusCode.Created, application)
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
        
        val result = mutualSelectionService.withdrawApplication(
            studentUUID = userId,
            relationId = relationId
        )
        
        call.respond(HttpStatusCode.OK, result)
    }
}

/**
 * 学生取消已批准的关系
 */
fun Route.cancelApprovedRelation(mutualSelectionService: MutualSelectionService) {
    post("/cancel-approved") {
        val userId = getUserIdFromCall(call)
        
        val params = call.receiveParameters()
        val relationId = params["relationId"] ?: throw BadRequestException("缺少关系ID参数")
        
        val result = mutualSelectionService.cancelApprovedRelation(
            studentUUID = userId,
            relationId = relationId
        )
        
        call.respond(HttpStatusCode.OK, result)
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
        
        val applications = mutualSelectionService.getCoachApplications(
            coachUUID = userId,
            status = status,
            page = page,
            size = size
        )
        
        call.respond(HttpStatusCode.OK, applications)
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
        
        val result = mutualSelectionService.reviewApplication(
            coachUUID = userId,
            relationId = relationId,
            approve = approve
        )
        
        call.respond(HttpStatusCode.OK, result)
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
        
        val result = mutualSelectionService.adminCreateRelation(
            studentUUID = studentId,
            coachUUID = coachId
        )
        
        call.respond(HttpStatusCode.Created, result)
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
        
        val relations = mutualSelectionService.getAllRelations(
            status = status,
            page = page,
            size = size
        )
        
        call.respond(HttpStatusCode.OK, relations)
    }
}

/**
 * 学生获取当前已建立关系的教练列表
 */
fun Route.getStudentCurrentCoaches(mutualSelectionService: MutualSelectionService) {
    get("/student/current-coaches") {
        val userId = getUserIdFromCall(call)

        val coaches = mutualSelectionService.getStudentCurrentCoaches(
            studentUUID = userId
        )
        
        call.respond(HttpStatusCode.OK, coaches)
    }
}

/**
 * 教练获取当前已建立关系的学生列表
 */
fun Route.getCoachCurrentStudents(mutualSelectionService: MutualSelectionService) {
    get("/coach/current-students") {
        val userId = getUserIdFromCall(call)

        val students = mutualSelectionService.getCoachCurrentStudents(
            coachUUID = userId
        )
        
        call.respond(HttpStatusCode.OK, students)
    }
}

/**
 * 教练获取历史学生列表（包括已结束关系的学生）
 */
fun Route.getCoachHistoricalStudents(mutualSelectionService: MutualSelectionService) {
    get("/coach/historical-students") {
        val userId = getUserIdFromCall(call)

        val students = mutualSelectionService.getCoachHistoricalStudents(
            coachUUID = userId
        )
        
        call.respond(HttpStatusCode.OK, students)
    }
}

/**
 * 管理员根据ID获取师生关系详细信息
 */
fun Route.getRelationById(mutualSelectionService: MutualSelectionService) {
    get("/admin/relation/{relationId}") {
        val relationId = call.parameters["relationId"] ?: throw BadRequestException("缺少关系ID参数")
        
        val relation = mutualSelectionService.getRelationById(
            relationId = relationId
        )
        
        call.respond(HttpStatusCode.OK, relation)
    }
}

/**
 * 管理员更新师生关系信息
 */
fun Route.updateRelation(mutualSelectionService: MutualSelectionService) {
    post("/admin/relation/{relationId}") {
        val relationId = call.parameters["relationId"] ?: throw BadRequestException("缺少关系ID参数")
        
        val params = call.receiveParameters()
        val status: MutualSelectionStatus? = params["status"]?.let { 
            MutualSelectionStatus.valueOf(it.uppercase()) 
        }
        // 移除对时间参数的处理
        
        val updatedRelation = mutualSelectionService.updateRelation(
            relationId = relationId,
            status = status
            // 移除对时间参数的处理
        )
        
        call.respond(HttpStatusCode.OK, updatedRelation)
    }
}