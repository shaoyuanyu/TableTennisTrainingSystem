package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.persistence.CampusService
import io.github.shaoyuanyu.ttts.persistence.CoachService
import io.github.shaoyuanyu.ttts.persistence.CourseService
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.github.shaoyuanyu.ttts.persistence.MessageService
import io.github.shaoyuanyu.ttts.persistence.MutualSelectionService
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.github.shaoyuanyu.ttts.persistence.AdminService
import io.github.shaoyuanyu.ttts.persistence.CompetitionService
import io.github.shaoyuanyu.ttts.routes.campusRoutes
import io.github.shaoyuanyu.ttts.routes.coachRoutes
import io.github.shaoyuanyu.ttts.routes.competitionRoutes
import io.github.shaoyuanyu.ttts.routes.courseRoutes
import io.github.shaoyuanyu.ttts.routes.userRoutes
import io.github.shaoyuanyu.ttts.routes.messageRoutes
import io.github.shaoyuanyu.ttts.routes.mutualSelectionRoutes
import io.github.shaoyuanyu.ttts.routes.walletRoutes
import io.github.shaoyuanyu.ttts.routes.adminRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    messageService: MessageService,
    studentService: StudentService,
    campusService: CampusService,
    coachService: CoachService,
    mutualSelectionService: MutualSelectionService,
    courseService: CourseService,
    adminService: AdminService,
    competitionService: CompetitionService,
) {
    routing {
        // open api
        run {
            openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
            swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        }
    }

    userRoutes(userService)
    messageRoutes(messageService)
    walletRoutes(studentService)
    campusRoutes(campusService)
    coachRoutes(coachService)
    mutualSelectionRoutes(mutualSelectionService)
    competitionRoutes(competitionService)
    courseRoutes(courseService)
    adminRoutes(adminService)
}