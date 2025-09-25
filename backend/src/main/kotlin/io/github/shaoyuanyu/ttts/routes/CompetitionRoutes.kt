package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.student.ComQueryRequest
import io.github.shaoyuanyu.ttts.dto.student.CompetitionInfo
import io.github.shaoyuanyu.ttts.dto.student.CompetitionResult
import io.github.shaoyuanyu.ttts.dto.student.Group
import io.github.shaoyuanyu.ttts.dto.student.Status
import io.github.shaoyuanyu.ttts.dto.student.comsignupRequest
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.github.shaoyuanyu.ttts.persistence.USER_LOGGER
import io.github.shaoyuanyu.ttts.persistence.competition.CompetitionSignupEntity
import io.github.shaoyuanyu.ttts.persistence.competition.CompetitionSignupTable
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.time.Clock

fun Application.competitionRoutes(studentService: StudentService) {
    routing {
        route("/competition") {
            // 无权限限制
            run {
            }

            // 所有登录用户
            authenticate("auth-session-all") {
            }

            // 学生权限
            authenticate("auth-session-student") {
//                signupCompetition(studentService)
//                querysignup(studentService)
//                getUserSchedule(studentService)
//                getLatestTournament(studentService) // 添加获取最新比赛信息接口
            }

            //管理员权限
            authenticate ("auth-session-campus-admin", "auth-session-super-admin"){
//                getCampusCompetitions(studentService)
//                enterCompetitionResults(studentService)
//                getTournaments(studentService) // 添加获取比赛列表接口
//                createTournament(studentService) // 添加创建比赛接口
//                deleteTournament(studentService) // 添加删除比赛接口
            }

            // 超级管理员权限
            authenticate("auth-session-super-admin") {
//                getAllCompetitions(studentService)
//                getTournaments(studentService) // 添加获取比赛列表接口
//                createTournament(studentService) // 添加创建比赛接口
//                deleteTournament(studentService) // 添加删除比赛接口
            }
        }
    }
}

/**
 * 竞赛报名
 */
//fun Route.signupCompetition(studentService: StudentService) {
//    post("/signup") {
//        val request = call.receive<comsignupRequest>()
//        val groupName=request.group
//        val userId=call.sessions.get<UserSession>().let {
//            if (it == null) {
//                throw UnauthorizedException("未登录")
//            }
//            it.userId
//        }
//        studentService.signupCompetition(userId, groupName)
//        call.respond(
//            HttpStatusCode.OK,
//            "报名成功"
//        )
//    }
//}

/**
 * 查询比赛报名情况
 */
//fun Route.querysignup(studentService: StudentService) {
//    get("/querysignup") {
//        val userId=call.sessions.get<UserSession>().let {
//            if (it == null) {
//                throw UnauthorizedException("未登录")
//            }
//            it.userId
//        }
//        val detail = studentService.querySignup(userId)
//        call.respond(
//            HttpStatusCode.OK,
//            detail
//        )
//    }
//}

/**
 * 获取用户个人比赛安排
 */
//fun Route.getUserSchedule(studentService: StudentService) {
//    get("/my-schedule") {
//        val userId = call.sessions.get<UserSession>()?.userId
//            ?: throw UnauthorizedException("未登录")
//
//        val schedule = studentService.getUserSchedule(userId)
//        call.respond(
//            HttpStatusCode.OK,
//            schedule
//        )
//    }
//}

/**
 * 获取所有竞赛信息
 */
//fun Route.getAllCompetitions(studentService: StudentService) {
//    get("/allcompetitions") {
//        val competitions = studentService.getAllCompetitions()
//        call.respond(
//            HttpStatusCode.OK,
//            competitions
//        )
//    }
//}

/**
 * 获取本校区所有竞赛信息
 */
//fun Route.getCampusCompetitions(studentService: StudentService) {
//    get("/campuscompetitions") {
//        val userId=call.sessions.get<UserSession>().let {
//            if (it == null) {
//                throw UnauthorizedException("未登录")
//            }
//            it.userId
//        }
//        val competitions = studentService.getCampusCompetitions(userId)
//        call.respond(
//            HttpStatusCode.OK,
//            competitions
//        )
//    }
//}

/**
 * 录入本校区比赛成绩
 */
//fun Route.enterCompetitionResults(studentService: StudentService) {
//    post("/enterresults") {
//        val user=call.receive<CompetitionResult>()
//        val winner=user.winnerName
//        val loser=user.loserName
//        studentService.enterResults(winner,loser)
//        call.respond(
//            HttpStatusCode.OK,
//            "成绩录入成功"
//        )
//    }
//}

/**
 * 获取比赛列表
 */
//fun Route.getTournaments(studentService: StudentService) {
//    get("/tournaments") {
//        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
//        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
//
//        try {
//            val result = studentService.getTournaments(page, size)
//            call.respond(HttpStatusCode.OK, result)
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
//        }
//    }
//}

/**
 * 创建比赛
 */
//fun Route.createTournament(studentService: StudentService) {
//    post("/tournaments/create") {
//        try {
//            val userId = call.sessions.get<UserSession>()?.userId
//                ?: throw UnauthorizedException("未登录")
//
//            val request = call.receive<Map<String, Any?>>()
//            val tournament = studentService.createTournament(request, userId)
//            call.respond(HttpStatusCode.OK, mapOf("message" to "比赛创建成功", "data" to tournament.expose()))
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
//        }
//    }
//}

/**
 * 删除比赛
 */
//fun Route.deleteTournament(studentService: StudentService) {
//    delete("/tournaments/{id}") {
//        try {
//            val id = call.parameters["id"]?.toIntOrNull()
//                ?: throw IllegalArgumentException("无效的比赛ID")
//
//            val success = studentService.deleteTournament(id)
//            if (success) {
//                call.respond(HttpStatusCode.OK, mapOf("message" to "比赛删除成功"))
//            } else {
//                call.respond(HttpStatusCode.NotFound, mapOf("error" to "比赛不存在"))
//            }
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
//        }
//    }
//}

///**
// * 获取最新比赛信息
// */
//fun Route.getLatestTournament(studentService: StudentService) {
//    get("/latest") {
//        try {
//            // 调用服务方法获取最新比赛信息
//            val latestTournament = studentService.getLatestTournament()
//            call.respond(HttpStatusCode.OK, latestTournament)
//        } catch (e: NotFoundException) {
//            // 如果没有找到比赛，返回空对象而不是错误
//            call.respond(HttpStatusCode.OK, mapOf(
//                "id" to null,
//                "name" to "",
//                "type" to "monthly",
//                "date" to "",
//                "registrationDeadline" to "",
//                "fee" to 30,
//                "description" to ""
//            ))
//        } catch (e: Exception) {
//            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
//        }
//    }
//}

