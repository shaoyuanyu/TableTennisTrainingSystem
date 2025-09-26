package io.github.shaoyuanyu.ttts.plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        anyMethod()

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        allowOrigins({
            true
        })

        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowSameOrigin = true
    }
}