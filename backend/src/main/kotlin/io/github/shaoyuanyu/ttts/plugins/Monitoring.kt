package io.github.shaoyuanyu.ttts.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.request.queryString
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val uri = call.request.uri
            val queryString = call.request.queryString().let { if (it.isNotEmpty()) "?$it" else "" }
            val cookies = call.request.headers["Cookie"] ?: ""
            val contentType = call.request.headers["Content-Type"] ?: ""
            val contentLength = call.request.headers["Content-Length"] ?: ""
            val referer = call.request.headers["Referer"] ?: ""
            val accept = call.request.headers["Accept"] ?: ""
            val acceptLanguage = call.request.headers["Accept-Language"] ?: ""
            val acceptEncoding = call.request.headers["Accept-Encoding"] ?: ""
            val origin = call.request.headers["Origin"] ?: ""
            
            "Status: $status, HTTP method: $httpMethod, URI: $uri$queryString, " +
            "User agent: $userAgent, Cookies: $cookies, Content-Type: $contentType, " +
            "Content-Length: $contentLength, Referer: $referer, Accept: $accept, " +
            "Accept-Language: $acceptLanguage, Accept-Encoding: $acceptEncoding, Origin: $origin"
        }
        
        mdc("userId") { call ->
            call.principal<UserIdPrincipal>()?.name
        }
    }
}