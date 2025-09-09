package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnauthorizedException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = HttpStatusCode.Unauthorized)
        }

        exception<NotFoundException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = HttpStatusCode.NotFound)
        }

        exception<BadRequestException> { call, cause ->
            call.respondText(text = cause.message.toString(), status = HttpStatusCode.BadRequest)
        }
    }
}
