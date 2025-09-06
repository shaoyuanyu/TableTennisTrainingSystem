package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.persistence.UserService
import io.github.shaoyuanyu.ttts.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(userService: UserService) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    userRoutes(userService)
}
