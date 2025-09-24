package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.course.*
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.CourseService
import io.github.shaoyuanyu.ttts.utils.getUserIdFromCall
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * 课程管理路由配置
 */
fun Application.courseRoutes(courseService: CourseService) {
    routing {
        route("/courses") {
            // 学生权限路由
            authenticate("auth-session-student") {
                getStudentSchedule(courseService)
                getCoachScheduleForStudent(courseService)
                submitStudentFeedback(courseService)
            }
            
            // 教练权限路由
            authenticate("auth-session-coach") {
                getCoachSchedule(courseService)
                confirmCourse(courseService)
                updateCourseStatusByCoach(courseService)
            }
            
            // 管理员权限路由
            authenticate("auth-session-campus-admin", "auth-session-super-admin") {
                createCourse(courseService)
                queryCourses(courseService)
            }
        }
    }
}

/**
 * 学生查看自己的课程安排
 */
fun Route.getStudentSchedule(courseService: CourseService) {
    get("/my-schedule") {
        val userId = getUserIdFromCall(call)
        
        val dateFrom = call.request.queryParameters["dateFrom"]
        val dateTo = call.request.queryParameters["dateTo"]
        
        val courses = courseService.getStudentSchedule(
            studentId = userId,
            dateFrom = dateFrom,
            dateTo = dateTo
        )
        
        call.respond(HttpStatusCode.OK, courses)
    }
}

/**
 * 学生查看可选教练的时间安排
 */
fun Route.getCoachScheduleForStudent(courseService: CourseService) {
    get("/coach-schedule/{coachId}") {
        val coachId = call.parameters["coachId"] 
            ?: throw BadRequestException("缺少教练ID")
        
        val dateFrom = call.request.queryParameters["dateFrom"]
        val dateTo = call.request.queryParameters["dateTo"]
        
        val courses = courseService.getCoachScheduleForStudent(
            coachId = coachId,
            dateFrom = dateFrom,
            dateTo = dateTo
        )
        
        call.respond(HttpStatusCode.OK, courses)
    }
}

/**
 * 学生提交课程反馈
 */
fun Route.submitStudentFeedback(courseService: CourseService) {
    post("/feedback") {
        val request = call.receive<StudentFeedbackRequest>()
        val course = courseService.submitFeedback(request)
        call.respond(HttpStatusCode.OK, course)
    }
}

/**
 * 教练查看自己的课程安排
 */
fun Route.getCoachSchedule(courseService: CourseService) {
    get("/coach-schedule") {
        val userId = getUserIdFromCall(call)
        
        val dateFrom = call.request.queryParameters["dateFrom"]
        val dateTo = call.request.queryParameters["dateTo"]
        
        val courses = courseService.getCoachSchedule(
            coachId = userId,
            dateFrom = dateFrom,
            dateTo = dateTo
        )
        
        call.respond(HttpStatusCode.OK, courses)
    }
}

/**
 * 教练确认/拒绝课程
 */
fun Route.confirmCourse(courseService: CourseService) {
    post("/confirm") {
        call.respond(HttpStatusCode.NotImplemented, "功能尚未实现")
    }
}

/**
 * 教练更新课程状态
 */
fun Route.updateCourseStatusByCoach(courseService: CourseService) {
    put("/{courseId}/status") {
        val courseId = call.parameters["courseId"]
            ?: throw BadRequestException("缺少课程ID")
        
        val statusParam = call.request.queryParameters["status"]
            ?: throw BadRequestException("缺少状态参数")
        
        val status = try {
            CourseStatus.valueOf(statusParam.uppercase())
        } catch (_: IllegalArgumentException) {
            throw BadRequestException("无效的状态值")
        }
        
        val course = courseService.updateCourseStatus(courseId, status)
        call.respond(HttpStatusCode.OK, course)
    }
}

/**
 * 创建课程（管理员代理）
 */
fun Route.createCourse(courseService: CourseService) {
    post("/create") {
        val request = call.receive<CourseCreateRequest>()
        val course = courseService.createCourse(request)
        call.respond(HttpStatusCode.Created, course)
    }
}

/**
 * 查询所有课程
 */
fun Route.queryCourses(courseService: CourseService) {
    post("/query") {
        val request = call.receive<QueryCourseRequest>()
        val response = courseService.queryCourses(request)
        call.respond(HttpStatusCode.OK, response)
    }
}