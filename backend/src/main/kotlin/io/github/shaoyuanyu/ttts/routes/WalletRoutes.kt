package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.recharge.RechargeRequest
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.walletRoutes(walletService: StudentService) {
    routing {
        route("/wallet") {
            // 所有登录用户
            authenticate("auth-session-all") {
                getWalletBalance(walletService)
                getRechargeHistory(walletService)
            }

            // 学生权限
            authenticate("auth-session-student") {
                rechargeWallet(walletService)
            }

            // 超级管理员权限
            authenticate("auth-session-super-admin") {
                getAllRechargeRecords(walletService)
                getRechargeRecordsByUserId(walletService)
            }
        }
    }
}

/**
 * 获取钱包余额
 *
 * 该函数用于获取当前登录用户的钱包余额
 *
 * @param walletService WalletService实例，用于查询余额
 */
fun Route.getWalletBalance(walletService: StudentService) {
    get("/balance") {
        val userId = getUserIdFromCall(call)

        val balance = walletService.queryBalance(userId)

        call.response.status(HttpStatusCode.OK)
        call.respond(
            mapOf("balance" to balance)
        )
    }
}

/**
 * 学生充值钱包
 *
 * 该函数用于学生用户进行钱包充值
 *
 * @param walletService WalletService实例，用于处理充值操作
 */
fun Route.rechargeWallet(walletService: StudentService) {
    post("/recharge") {
        // 从会话中获取用户ID，如果未登录则抛出异常
        val userId = getUserIdFromCall(call)

        // 验证当前用户角色是否为学生
        val userRole = call.sessions.get<UserSession>()?.userRole
        if (userRole != UserRole.STUDENT) {
            throw BadRequestException("只有学生可以进行充值")
        }

        // 接收充值请求数据
        val rechargeRequest = call.receive<RechargeRequest>()
        val amount = rechargeRequest.amount

        // 参数验证
        if (amount <= 0) {
            throw BadRequestException("充值金额必须大于0")
        }

        if (amount > 10000) {
            throw BadRequestException("单次充值金额不能超过10000元")
        }

        // 执行充值操作
        walletService.recharge(userId, amount)

        val newBalance = walletService.queryBalance(userId)
        //返回现在余额
        call.response.status(HttpStatusCode.OK)
        call.respond(
            mapOf("balance" to newBalance)
        )
    }
}

/**
 * 获取充值记录
 *
 * 该函数用于获取当前登录用户的充值历史记录
 *
 * @param walletService WalletService实例，用于查询充值记录
 */
fun Route.getRechargeHistory(walletService: StudentService) {
    get("/recharge/history") {
        // 从会话中获取用户ID，如果未登录则抛出异常
        val userId = getUserIdFromCall(call)

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

        val records= walletService.getRechargeHistory(userId, page, size)

        call.respond(HttpStatusCode.OK, records)
    }
}

/**
 * 获取所有充值记录（超级管理员）
 *
 * 该函数用于超级管理员查看所有用户的充值记录
 *
 * @param walletService WalletService实例，用于查询充值记录
 */
fun Route.getAllRechargeRecords(walletService: StudentService) {
    get("/recharge/records") {
        // 获取查询参数
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
//        val userId = call.request.queryParameters["userId"]
//        val startDate = call.request.queryParameters["startDate"]
//        val endDate = call.request.queryParameters["endDate"]

        // 参数验证
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }

        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        val records = walletService.getAllRechargeRecords(page,size)

        call.respond(HttpStatusCode.OK, records)
    }
}

/**
 * 按用户ID获取充值记录（超级管理员）
 *
 * 该函数用于超级管理员查看指定用户的充值记录
 *
 * @param walletService WalletService实例，用于查询充值记录
 */
fun Route.getRechargeRecordsByUserId(walletService: StudentService) {
    get("/recharge/records/{username}") {
        val targetUsername = call.parameters["username"] ?: throw BadRequestException("缺少用户name参数")

        // 获取查询参数
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
//        val startDate = call.request.queryParameters["startDate"]
//        val endDate = call.request.queryParameters["endDate"]

        // 参数验证
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }

        if (size !in 1..100) {
            throw BadRequestException("每页大小必须在1-100之间")
        }

        val records= walletService.getRechargeHistoryByusername(targetUsername, page, size)

        call.respond(HttpStatusCode.OK, records)
    }
}


