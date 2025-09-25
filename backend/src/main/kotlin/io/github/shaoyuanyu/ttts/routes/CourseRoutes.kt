package io.github.shaoyuanyu.ttts.routes

import io.github.shaoyuanyu.ttts.dto.course.*
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.persistence.CourseService
import io.github.shaoyuanyu.ttts.plugins.LOGGER
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
            authenticate("auth-form") {

            }
            // 学生权限路由
            authenticate("auth-session-student") {
                getStudentSchedule(courseService)
                getCoachScheduleForStudent(courseService)
                submitStudentFeedback(courseService)
                bookCourse(courseService)
            }
            
            // 教练权限路由
            authenticate("auth-session-coach") {
                getCoachSchedule(courseService)
                updateCourseStatusByCoach(courseService)
                getPendingCoursesForCoach(courseService)
                coachJudgeCourse(courseService)
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

/**
 * 课程预约
 */
fun Route.bookCourse(courseService: CourseService) {
    post("/book") {
        val request = call.receive<CourseBookingRequest>()
        LOGGER.info(request.toString())
        courseService.bookCourse(request)
        call.respond(HttpStatusCode.Created)
    }
}

/**
 * 教练查询待确认的课程
 */
fun Route.getPendingCoursesForCoach(courseService: CourseService) {
    get("/querypending") {
        val userId = getUserIdFromCall(call)

        val courses = courseService.queryconfirmCourse(coachId = userId)

        call.respond(HttpStatusCode.OK, courses)
    }
}

/**
 * 教练审核课程
 */
fun Route.coachJudgeCourse(courseService: CourseService) {
    post("/coach-judge") {
        val coachId = getUserIdFromCall(call)
        val courseId = call.receive<CourseConfirmRequest>().courseId
        val judge = call.receive<CourseConfirmRequest>().confirmed
        val course = courseService.judegeCourse(coachId, courseId, judge)
        call.respond(HttpStatusCode.OK, course)
    }
}