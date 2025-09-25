package io.github.shaoyuanyu.ttts

import io.github.shaoyuanyu.ttts.persistence.CampusService
import io.github.shaoyuanyu.ttts.persistence.CoachService
import io.github.shaoyuanyu.ttts.persistence.CourseService
import io.github.shaoyuanyu.ttts.persistence.UserService
import io.github.shaoyuanyu.ttts.persistence.MessageService
import io.github.shaoyuanyu.ttts.persistence.MutualSelectionService
import io.github.shaoyuanyu.ttts.persistence.StudentService
import io.github.shaoyuanyu.ttts.persistence.AdminService
import io.github.shaoyuanyu.ttts.persistence.CompetitionService
import io.github.shaoyuanyu.ttts.plugins.configureAuthentication
import io.github.shaoyuanyu.ttts.plugins.configureCORS
import io.github.shaoyuanyu.ttts.plugins.configureSerialization
import io.github.shaoyuanyu.ttts.plugins.configureDatabase
import io.github.shaoyuanyu.ttts.plugins.configureMonitoring
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.github.shaoyuanyu.ttts.plugins.configureRouting
import io.github.shaoyuanyu.ttts.plugins.configureSessions
import io.github.shaoyuanyu.ttts.plugins.configureStatusPages

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    // persistence
    val database = configureDatabase(
        url = environment.config.property("database.url").getString(),
        driver = environment.config.property("database.driver").getString(),
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString()
    )

    // 创建各类服务
    val campusService = CampusService(database)
    val userService = UserService(database)
    val messageService = MessageService(database)
    val studentService = StudentService(database, userService)
    val coachService = CoachService(database, userService)
    val mutualSelectionService = MutualSelectionService(database)
    val courseService = CourseService(database, studentService)
    val adminService = AdminService(database)
    val competitionService = CompetitionService(database, studentService)

    // monitoring
    configureMonitoring()

    // serialization
    configureSerialization()

    // CORS
    configureCORS()

    // session & authentication
    configureSessions()
    configureAuthentication(userService)

    // status page
    configureStatusPages()

    // routing
    configureRouting(
        userService = userService,
        messageService = messageService,
        studentService = studentService,
        campusService = campusService,
        coachService = coachService,
        mutualSelectionService = mutualSelectionService,
        courseService = courseService,
        adminService = adminService,
        competitionService = competitionService
    )
}