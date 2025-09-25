package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.competition.Competition
import io.github.shaoyuanyu.ttts.dto.competition.CompetitionSignupRequest
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.CompetitionService
import io.github.shaoyuanyu.ttts.plugins.LOGGER
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
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

fun Application.competitionRoutes(
    competitionService: CompetitionService,
) {
    routing {
        route("/competition") {
            // 无权限限制
            run {
            }

            // 所有登录用户
            authenticate("auth-session-all") {
                getCampusCompetitions(competitionService)
            }

            // 学生权限
            authenticate("auth-session-student") {
                signupCompetition(competitionService)
                queryUserSignup(competitionService)
                getUserCompetitionSchedule(competitionService)
            }

            // 管理员权限
            authenticate ("auth-session-admin"){
                createCompetition(competitionService)
                arrangeCompetition(competitionService)
            }

            // 超级管理员权限
            authenticate("auth-session-super-admin") {
                getAllCompetitions(competitionService)
            }
        }
    }
}

/**
 * 创建比赛
 */
fun Route.createCompetition(competitionService: CompetitionService) {
    post("/create") {
        val request = call.receive<Competition>()

        competitionService.createCompetition(
            name = request.name,
            type = request.type,
            campusId = request.campusId,
            date = request.date,
            registrationDeadline = request.registrationDeadline,
            fee = request.fee,
            description = request.description
        )

        call.respond(HttpStatusCode.OK, "比赛创建成功")
    }
}

/**
 * 获取本校区所有比赛
 */
fun Route.getCampusCompetitions(competitionService: CompetitionService) {
    get("/self-campus") {
        val userId = getUserIdFromCall(call)

        val competitions = competitionService.queryCampusCompetition(userId)

        LOGGER.info(competitions.toString())

        call.respond(HttpStatusCode.OK, competitions)
    }
}

/**
 * 获取所有校区所有比赛
 */
fun Route.getAllCompetitions(competitionService: CompetitionService) {
    get("/all") {
        val competitions = competitionService.queryAllCompetitions()
        call.respond(HttpStatusCode.OK, competitions)
    }
}

/**
 * 竞赛报名
 */
fun Route.signupCompetition(competitionService: CompetitionService) {
    post("/signup") {
        val userId = getUserIdFromCall(call)

        val request = call.receive<CompetitionSignupRequest>()
        val competitionId = request.competitionId
        val groupName = request.group

        competitionService.signupCompetition(userId, competitionId, groupName)

        call.respond(HttpStatusCode.OK, "报名成功")
    }
}

/**
 * 查询比赛报名情况
 */
fun Route.queryUserSignup(competitionService: CompetitionService) {
    get("/signup") {
        val userId = getUserIdFromCall(call)

        call.respond(HttpStatusCode.OK, competitionService.queryCompetitionSignup(userId))
    }
}

fun Route.arrangeCompetition(competitionService: CompetitionService) {
    post("/arrange/{competitionId}") {
        val competitionId = call.parameters["competitionId"] ?: throw BadRequestException("缺少比赛ID参数")

        competitionService.arrangeCompetition(competitionId)

        call.respond(HttpStatusCode.OK, "比赛安排成功")
    }
}

/**
 * 获取用户个人比赛安排
 */
fun Route.getUserCompetitionSchedule(competitionService: CompetitionService) {
    get("/my-schedule/{competitionId}") {
        val userId = getUserIdFromCall(call)

        val competitionId = call.parameters["competitionId"] ?: throw BadRequestException("缺少比赛ID参数")

        val schedule = competitionService.getUserCompetitionSchedule(userId, competitionId)

        call.respond(HttpStatusCode.OK, schedule)
    }
}
