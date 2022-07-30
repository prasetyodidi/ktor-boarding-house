package tech.didiprasetyo.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import tech.didiprasetyo.domain.service.RoomService
import tech.didiprasetyo.util.Response
import tech.didiprasetyo.util.Status
import java.util.*

fun Route.roomRouting() {
    val roomService by inject<RoomService>()
    route("room/{roomId}"){
        get("/rules") {
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
            val dateRange = roomService.getReminderDateRange(UUID.fromString(roomId))
            call.respond(
                Response(
                    status = Status.Success,
                    message = "get reminder date range",
                    data = listOf(dateRange)
                )
            )
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
            // call method room info
            val roomInfo = roomService.getRoomInfo(UUID.fromString(roomId))
            // return
            call.respond(
                Response(
                    status = Status.Success,
                    message = "success get roominfo",
                    data = listOf(roomInfo)
                )
            )
        }
    }
}
