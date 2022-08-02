package tech.didiprasetyo.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import tech.didiprasetyo.routing.authRouting
import tech.didiprasetyo.routing.roomRouting
import tech.didiprasetyo.routing.userRouting

fun Application.configureRouting() {

    routing {
        authRouting()
        authenticate("auth-jwt") {
            userRouting()
            roomRouting()
        }
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
