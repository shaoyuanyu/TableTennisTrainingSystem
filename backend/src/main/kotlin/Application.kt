package io.github.shaoyuanyu.ttts

import io.github.shaoyuanyu.ttts.persistence.configureDatabase
import io.ktor.server.application.*

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

    // routing
    configureRouting()
}
