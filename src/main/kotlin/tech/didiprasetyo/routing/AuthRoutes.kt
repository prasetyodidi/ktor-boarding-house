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

    authenticate("auth-jwt"){
        get("/logout") {
            // get session
            val principal = call.principal<JWTPrincipal>()
            val sessionId = principal!!.payload.getClaim("session").asString()
            // call method delete session
            val logout = authService.logout(UUID.fromString(sessionId))
            if (logout){
                call.respond(Response(
                    status = Status.Success,
                    message = "logout success",
                    data = emptyList<String>()
                ))
            } else {
                call.respond(Response(
                    status = Status.Fail,
                    message = "logout fail",
                    data = emptyList<String>()
                ))
            }
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
        // get token
        val token = call.parameters["token"] ?: return@get call.respond(
            Response<String>(
                status = Status.Fail,
                message = "token invalid",
                data = emptyList()
            )
        )
        val verify = authService.verifyEmail(token)
        if (verify){
            call.respond(
                Response<String>(
                    status = Status.Success,
                    message = "email has been verified",
                    data = emptyList()
                )
            )
        } else{
            call.respond(
                Response<String>(
                    status = Status.Fail,
                    message = "email not verified",
                    data = emptyList()
                )
            )
        }
    }
    get("delete-email/{token}") {
        // get token
        val token = call.parameters["token"] ?: return@get call.respond(
            Response<String>(
                status = Status.Fail,
                message = "token invalid",
                data = emptyList()
            )
        )
        val delete = authService.deleteEmail(token)
        if (delete){
            call.respond(
                Response<String>(
                    status = Status.Success,
                    message = "email has been deleted",
                    data = emptyList()
                )
            )
        } else{
            call.respond(
                Response<String>(
                    status = Status.Fail,
                    message = "email not deleted",
                    data = emptyList()
                )
            )
        }
    }

    post("/reset-password/{token}") {

    }

    post("/update-password") {

    }
}