package routes

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.userRoutes() {
    routing {
        route("/user") {
            // 无权限限制
            run {
                signup()
            }

            // 登录
            authenticate("auth-form") {
                login()
            }
        }
    }
}

fun Route.signup() {
    post("/signup") {
        call.respondText("signup")
    }
}

fun Route.login() {
    post("/login") {
        call.respondText("login")
    }
}