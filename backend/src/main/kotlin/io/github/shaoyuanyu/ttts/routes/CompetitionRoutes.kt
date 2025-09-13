package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.student.comsignupRequest
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
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
            }
            //管理员权限
            authenticate ("auth-session-campus-admin"){

            }
            // 超级管理员权限
            authenticate("auth-session-super-admin") {

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
 }
}
/**
 * 查询比赛报名情况
 */
fun Route.querySignup(studentService: StudentService) {
    post("/querysignup") {
        val userId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
//        studentService.querysignup()
 }
}