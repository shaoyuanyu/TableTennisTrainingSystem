package io.github.shaoyuanyu.ttts

import io.github.shaoyuanyu.ttts.plugins.configureSerialization
import plugins.configureDatabase
import io.ktor.server.application.*
import plugins.configureRouting
import plugins.configureSecurity

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // persistence
    val database = configureDatabase(
        url = environment.config.property("database.url").getString(),
        driver = environment.config.property("database.driver").getString(),
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString()
    )

    // serialization
    configureSerialization()

    // session & authority
    configureSecurity()

    // routing
    configureRouting()
}
