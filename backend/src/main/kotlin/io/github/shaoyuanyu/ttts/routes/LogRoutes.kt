package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.log.Log
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 日志路由配置
 */
fun Application.logRoutes() {
    routing {
        route("/logs") {
            // 需要管理员权限访问日志
            authenticate("auth-session-admin") {
                getLogs()
            }
        }
    }
}

/**
 * 获取日志文件内容（分页）
 */
fun Route.getLogs() {
    get {
        // 获取查询参数
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
        val dateStr = call.request.queryParameters["date"]

        // 参数验证
        if (page <= 0) {
            throw BadRequestException("页码必须大于0")
        }

        if (size !in 1..1000) {
            throw BadRequestException("每页大小必须在1-1000之间")
        }

        // 确定要读取的日志文件
        val logFileName = if (dateStr == null) {
            // 如果没有提供日期，则读取最新的日志文件
            "application.log"
        } else {
            // 验证日期格式
            try {
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                "application.$dateStr.0.log"
            } catch (e: DateTimeParseException) {
                throw BadRequestException("日期格式错误，应为 yyyy-MM-dd")
            }
        }

        // 检查日志目录是否存在
        val logDir = File("logs")
        if (!logDir.exists() || !logDir.isDirectory) {
            throw BadRequestException("日志目录不存在")
        }

        // 检查请求的日志文件是否存在
        val logFile = File(logDir, logFileName)
        if (!logFile.exists() || logFile.isDirectory) {
            throw BadRequestException("日志文件不存在: $logFileName")
        }

        // 读取日志文件内容
        val lines = Files.readAllLines(Paths.get(logFile.absolutePath))
        val totalLines = lines.size
        val totalPages = if (totalLines == 0) 0 else (totalLines - 1) / size + 1

        // 计算分页
        val startIndex = (page - 1) * size
        val endIndex = minOf(startIndex + size, totalLines)

        // 获取当前页的日志内容
        val pageLines = if (totalLines > 0 && startIndex < totalLines) {
            lines.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // 返回结果
        call.respond(
            HttpStatusCode.OK,
            Log(
                content = pageLines,
                page = page,
                size = size,
                totalPages = totalPages,
                totalElements = totalLines
            )
        )
    }
}