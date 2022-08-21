package tech.didiprasetyo.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.didiprasetyo.domain.model.DateRange
import tech.didiprasetyo.domain.model.Room
import tech.didiprasetyo.domain.model.RoomInfo
import tech.didiprasetyo.domain.service.RoomService
import tech.didiprasetyo.util.Response
import tech.didiprasetyo.util.Status
import java.util.*

fun Route.roomRouting() {
    val roomService by inject<RoomService>()
    get("room/tenant") {
        try {
            // call method room info
            val principal = call.principal<JWTPrincipal>()
            val verified = principal!!.payload.getClaim("verified").asBoolean()
            if (!verified) return@get call.respond(
                Response(
                    status = Status.Fail,
                    message = "You must verify email",
                    data = emptyList<String>()
                )
            )
            val userId = principal.payload.getClaim("user").asString()
            val userIdUUID = UUID.fromString(userId)
            // get room
            val room = roomService.getTenantRoom(userIdUUID)
            call.respond(
                Response(
                    status = Status.Success,
                    message = "success get data",
                    data = room
                )
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                Response<Room>(
                    status = Status.Fail,
                    message = "room id invalid",
                    data = emptyList()
                )
            )
        }
    }
    route("room/{roomId}") {
        get("/rule") {
            val roomId = call.parameters["roomId"] ?: return@get call.respond(
                Response<Any>(
                    status = Status.Fail,
                    message = "room id not found",
                    data = null
                )
            )
            val rules = roomService.getRulesOfRoom(UUID.fromString(roomId))
            call.respond(
                Response(
                    status = Status.Success,
                    message = "get rules",
                    data = rules
                )
            )
        }
        get("/reminder") {
            val roomId = call.parameters["roomId"] ?: return@get call.respond(
                Response<Any>(
                    status = Status.Fail,
                    message = "room id not found",
                    data = null
                )
            )
            val reminders = roomService.getRemindersOfRoom(UUID.fromString(roomId))
            call.respond(
                Response(
                    status = Status.Success,
                    message = "get reminders",
                    data = reminders
                )
            )
        }
        get("/reminder-date") {
            val roomId = call.parameters["roomId"] ?: return@get call.respond(
                Response<Any>(
                    status = Status.Fail,
                    message = "room id not found",
                    data = null
                )
            )
            val reminderDate = roomService.getReminderDate(UUID.fromString(roomId))
            call.respond(
                Response(
                    status = Status.Success,
                    message = "get reminder date",
                    data = listOf(reminderDate)
                )
            )
        }
        get("/reminder-date-range") {
            val roomId = call.parameters["roomId"] ?: return@get call.respond(
                Response<Any>(
                    status = Status.Fail,
                    message = "room id not found",
                    data = null
                )
            )

            try{
                val dateRange = roomService.getReminderDateRange(UUID.fromString(roomId))
                call.respond(
                    Response(
                        status = Status.Success,
                        message = "get reminder date range",
                        data = listOf(dateRange)
                    )
                )
            } catch (e: IllegalArgumentException){
                call.respond(
                    Response<DateRange>(
                        status = Status.Fail,
                        message = "room does not have tenant",
                        data = emptyList()
                    )
                )
            } catch (e: Exception){
                call.respond(
                    Response<DateRange>(
                        status = Status.Fail,
                        message = "unknown error",
                        data = emptyList()
                    )
                )
            }
        }
        get("/room-info") {
            // get room id from parameter request
            val roomId = call.parameters["roomId"] ?: return@get call.respond(
                Response<Any>(
                    status = Status.Fail,
                    message = "room id not found",
                    data = null
                )
            )
            try {
                val roomIdUUID = UUID.fromString(roomId)
                // call method room info
                val principal = call.principal<JWTPrincipal>()
                val userId = principal!!.payload.getClaim("user").asString()
                val userIdUUID = UUID.fromString(userId)
                val roomInfo = roomService.getRoomInfo(roomIdUUID, userIdUUID)
                // return
                call.respond(
                    Response(
                        status = Status.Success,
                        message = "success get room info",
                        data = listOf(roomInfo)
                    )
                )
            } catch (e: IllegalArgumentException) {
                call.respond(
                    Response<RoomInfo>(
                        status = Status.Fail,
                        message = "room id invalid",
                        data = emptyList()
                    )
                )
            } catch (e: Exception){
                call.respond(
                    Response<RoomInfo>(
                        status = Status.Fail,
                        message = "unknown error",
                        data = emptyList()
                    )
                )
            }
        }
    }
}
