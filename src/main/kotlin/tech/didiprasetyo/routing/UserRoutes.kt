package tech.didiprasetyo.routing

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.didiprasetyo.domain.service.UserService
import tech.didiprasetyo.util.Response
import tech.didiprasetyo.util.Status
import java.io.File
import java.util.UUID

fun Route.userRouting(){
    val userService by inject<UserService>()
    route("/user/{userId}"){
        get{
            // get parameter userId
            val userId = call.parameters["userId"] ?: return@get call.respond(Response(
                status = Status.Fail,
                message = "user id not found",
                data = emptyList<String>()
            ))
            // get user info
            try {
                val id = UUID.fromString(userId)
                userService.getUserInfo(id)
            } catch (e: IllegalArgumentException){
                return@get call.respond(Response(
                    status = Status.Fail,
                    message = "user id invalid",
                    data = emptyList<String>()
                ))
            } catch (e: Exception){
                return@get call.respond(Response(
                    status = Status.Fail,
                    message = "user id not found",
                    data = emptyList<String>()
                ))
            }
            // return
        }
        post{
            // get parameter userId
            val userId = call.parameters["userId"] ?: return@post call.respond(Response(
                status = Status.Fail,
                message = "user id not found",
                data = emptyList<String>()
            ))
//            val updateData = call.receive<>()
            // update user
//            userService.updateUser()
            // return
        }
    }
    var fileDescription = ""
    var fileName = ""
    post("/upload") {
        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    fileDescription = part.value
                }
                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    var fileBytes = part.streamProvider().readBytes()
                    File("uploads/$fileName").writeBytes(fileBytes)
                }
                else -> {
                    fileDescription = "else"
                }
            }
        }

        call.respondText("$fileDescription is uploaded to 'uploads/$fileName'")
    }
}