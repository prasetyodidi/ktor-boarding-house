package tech.didiprasetyo.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.didiprasetyo.domain.model.Email
import tech.didiprasetyo.domain.model.Token
import tech.didiprasetyo.domain.model.UserLogin
import tech.didiprasetyo.domain.model.UserRegister
import tech.didiprasetyo.domain.service.AuthService
import tech.didiprasetyo.util.Response
import tech.didiprasetyo.util.Status
import java.util.*

fun Route.authRouting(){
    val authService by inject<AuthService>()

    post("/register") {
        // get name, email, password from body request
        val userData = call.receive<UserRegister>()
        // verify request
        if (userData.password.length < 6){
            return@post call.respond(Response(
                status = Status.Fail,
                message = "password length at least 6 character",
                data = listOf(Email(userData.email))
            ))
        }
        val register = authService.register(userData)
        call.respond(register)
    }

    post("/login") {
        // get email & password from body request
        val userData = call.receive<UserLogin>()
        val device = call.request.userAgent() ?: return@post call.respond(Response(
            status = Status.Fail,
            message = "user agent not found",
            data = emptyList<String>()
        ))
        // call login method, callback token
        val token = authService.login(userData, device) ?: return@post call.respond(Response(
            status = Status.Fail,
            message = "cannot create token",
            data = emptyList<String>()
        ))
        // send token
        call.respond(Response(
            status = Status.Success,
            message = "login success",
            data = listOf(Token(token))
        ))
    }

    post("/logout") {
        // get session
        val principal = call.principal<JWTPrincipal>()
        val sessionId = principal!!.payload.getClaim("session").asString()
        // call method delete session
        val logout = authService.logout(UUID.fromString(sessionId))
        if (logout){
            call.respond(Response(
                status = Status.Success,
                message = "logout success",
                data = emptyList<Any>()
            ))
        } else {
            call.respond(Response(
                status = Status.Fail,
                message = "logout fail",
                data = emptyList<Any>()
            ))
        }
    }

    post("reset-password") {
        // get email from body request
        // call send email reset password
        // return
        // isi email
        // kirim email
        // user click email
        // redirect user
        // user input password
        // update password
    }

    get("verify-email/{token}") {
        // convert token from string to uuid
        // call update user
        // return
    }
    get("/delete-email/{token}") {
        // get token
        // verify token
        // call delete user
        // return
    }

    post("/reset-password/{token}") {

    }

    post("/update-password") {

    }
}