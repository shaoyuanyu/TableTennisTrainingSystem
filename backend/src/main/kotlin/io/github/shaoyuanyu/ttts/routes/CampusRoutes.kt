package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.campus.AddTableRequest
import io.github.shaoyuanyu.ttts.dto.campus.CampusCreateRequest
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.CampusService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions

fun Application.campusRoutes(campusService: CampusService) {
    routing {
        route("/campus") {
            run{
                getCampusNames(campusService)
            }

            // 所有用户
            authenticate("auth-session-all") {
            }
            // 校区管理员
            authenticate("auth-session-campus-admin") {
                addTable(campusService)
            }
            // 超级管理员
            authenticate("auth-session-super-admin") {
                createCampus(campusService)
            }
        }
    }
}

/**
 * 创建校区 - 仅限超级管理员
 */
fun Route.createCampus(campusService: CampusService) {
    post("/create") {
        val campusData = call.receive<CampusCreateRequest>()

        // 验证必要字段
        if (campusData.campusName.isBlank()) {
            throw BadRequestException("校区名称不能为空")
        }

        if (campusData.address.isBlank()) {
            throw BadRequestException("校区地址不能为空")
        }

        if (campusData.contactPerson.isBlank()) {
            throw BadRequestException("联系人不能为空")
        }

        if(campusData.contactPerson.isBlank()) {
            throw BadRequestException("管理员用户名不能为空")
        }

        if (campusData.phone.isBlank()) {
            throw BadRequestException("联系电话不能为空")
        }
        if (campusData.email.isBlank()) {
            throw BadRequestException("邮箱不能为空")
        }

        campusService.createCampus(campusData)

        call.respond(HttpStatusCode.Created, mapOf("message" to "创建校区成功"))
    }
}

/**
 * 获取全部校区名称 - 所有认证用户都有权限
 */
fun Route.getCampusNames(campusService: CampusService) {
    get("/names") {
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

        val records = campusService.getAllCampusNames(page,size)

        call.respond(HttpStatusCode.OK, records)
    }
}
/**
 * 增加球桌 - 仅限校区管理员
 */
fun Route.addTable(campusService: CampusService) {
    post("/addTable") {
        val campusData = call.receive<AddTableRequest>()
        val userId=call.sessions.get<UserSession>().let {
            if (it == null) {
                throw UnauthorizedException("未登录")
            }
            it.userId
        }
        val number = campusData.number

        campusService.addTable(userId,number)

        call.respond(HttpStatusCode.Created, mapOf("message" to "增加球桌成功"))
    }
}
