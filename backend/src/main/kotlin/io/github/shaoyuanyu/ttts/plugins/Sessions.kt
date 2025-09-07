package io.github.shaoyuanyu.ttts.plugins

import io.github.shaoyuanyu.ttts.dto.user.UserSession
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.directorySessionStorage
import java.io.File

fun Application.configureSessions() {
    val sessionStoragePath = environment.config.property("session.storage.path").getString()

    install(Sessions) {
        cookie<UserSession>(
            "user_session",
            directorySessionStorage(File(sessionStoragePath))
        ) {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 600 // TODO: 生产环境下 60 * 60 * 24 * 30 一个月
        }
    }
}