@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlin.time.Instant

fun Application.studentRoutes(studentService: StudentService) {
    routing {
        route("/student") {
            // 学生权限
            authenticate("auth-session-student") {
                applyForCoach(studentService)
                getStudentApplications(studentService)
            }
        }
    }
}

/**
 * 学生申请教练
 */
fun Route.applyForCoach(studentService: StudentService) {
    post("/apply-coach") {
        // 从会话中获取学生ID
        val studentId = call.sessions.get<UserSession>()?.userId
            ?: throw UnauthorizedException("未登录")

        // 接收请求参数
        val params = call.receiveParameters()
        val coachUuid = params["coachUuid"] ?: throw BadRequestException("教练UUID不能为空")
        val expectedStartTimeStr = params["expectedStartTime"] ?: throw BadRequestException("预期开始时间不能为空")
        val message = params["message"]
        
        try {
            val expectedStartTime = Instant.parse(expectedStartTimeStr)
            
            // 调用服务层函数
            val relation = studentService.applyForCoach(studentId, coachUuid, expectedStartTime, message)

            // 返回成功响应
            call.respond(HttpStatusCode.OK, mapOf(
                "success" to true,
                "data" to mapOf(
                    "id" to relation.id.value.toString(),
                    "status" to relation.status,
                    "applicationTime" to relation.applicationTime.toString()
                )
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, mapOf("success" to false, "message" to e.message))
        }
    }
}

/**
 * 获取学生申请记录
 */
fun Route.getStudentApplications(studentService: StudentService) {
    get("/applications") {
        // 从会话中获取学生ID
        val studentId = call.sessions.get<UserSession>()?.userId
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
            val applications = studentService.getStudentApplications(studentId, status, page, size)

            // 返回成功响应
            call.respond(HttpStatusCode.OK, mapOf(
                "success" to true,
                "data" to applications.map { relation ->
                    mapOf(
                        "id" to relation.id.value.toString(),
                        "coachName" to relation.coachID.userId.realName,
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