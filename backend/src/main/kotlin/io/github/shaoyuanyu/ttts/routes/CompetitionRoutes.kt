package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.student.CompetitionResult
import io.github.shaoyuanyu.ttts.dto.student.comsignupRequest
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

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
                signupCompetition(studentService)
                querysignup(studentService)
                getUserSchedule(studentService)
                getLatestTournament(studentService) // 添加获取最新比赛信息接口
            }
            //管理员权限
            authenticate ("auth-session-campus-admin"){
                getCampusCompetitions(studentService)
                enterCompetitionResults(studentService)
                getTournaments(studentService) // 添加获取比赛列表接口
            }
            // 超级管理员权限
            authenticate("auth-session-super-admin") {
                getAllCompetitions(studentService)
                getTournaments(studentService) // 添加获取比赛列表接口
            }
        }
    }
}
/**
 * 竞赛报名
 */
fun Route.signupCompetition(studentService: StudentService) {
    post("/signup") {
        val request = call.receive<comsignupRequest>()
        val groupName=request.group
        val userId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
        studentService.signupCompetition(userId,groupName)
        call.respond(
            HttpStatusCode.OK,
            "报名成功"
        )
 }
}
/**
 * 查询比赛报名情况
 */
fun Route.querysignup(studentService: StudentService) {
    get("/querysignup") {
        val userId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
        val detail = studentService.querySignup(userId)
        call.respond(
            HttpStatusCode.OK,
            detail
        )
 }
}
/**
 * 获取用户个人比赛安排
 */
fun Route.getUserSchedule(studentService: StudentService) {
    get("/my-schedule") {
        val userId = call.sessions.get<UserSession>()?.userId
            ?: throw UnauthorizedException("未登录")
            
        val schedule = studentService.getUserSchedule(userId)
        call.respond(
            HttpStatusCode.OK,
            schedule
        )
    }
}
/**
 * 获取所有竞赛信息
 */
fun Route.getAllCompetitions(studentService: StudentService) {
    get("/allcompetitions") {
        val competitions = studentService.getAllCompetitions()
        call.respond(
            HttpStatusCode.OK,
            competitions
        )
 }
}
/**
 * 获取本校区所有竞赛信息
 */
fun Route.getCampusCompetitions(studentService: StudentService) {
    get("/campuscompetitions") {
        val userId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
        val competitions = studentService.getCampusCompetitions(userId)
        call.respond(
            HttpStatusCode.OK,
            competitions
        )
 }
}
/**
 * 录入本校区比赛成绩
 */
fun Route.enterCompetitionResults(studentService: StudentService) {
    post("/enterresults") {
        val user=call.receive<CompetitionResult>()
        val winner=user.winnerName
        val loser=user.loserName
        studentService.enterResults(winner,loser)
        call.respond(
            HttpStatusCode.OK,
            "成绩录入成功"
        )
    }
}

/**
 * 获取比赛列表
 */
fun Route.getTournaments(studentService: StudentService) {
    get("/tournaments") {
        // TODO: 实现获取比赛列表的逻辑
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "items" to emptyList<Any>(),
                "total" to 0
            )
        )
    }
}

/**
 * 获取最新的比赛信息
 */
fun Route.getLatestTournament(studentService: StudentService) {
    get("/latest") {
        // TODO: 实现获取最新比赛信息的逻辑
        call.respond(
            HttpStatusCode.OK,
            mapOf(
                "date" to "",
                "registrationDeadline" to ""
            )
        )
    }
}
