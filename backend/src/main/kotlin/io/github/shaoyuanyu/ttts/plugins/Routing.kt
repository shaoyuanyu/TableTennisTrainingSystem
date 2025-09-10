package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.persistence.UserService
import io.github.shaoyuanyu.ttts.persistence.MessageService
import io.github.shaoyuanyu.ttts.routes.userRoutes
import io.github.shaoyuanyu.ttts.routes.messageRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    messageService: MessageService
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
}