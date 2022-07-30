package tech.didiprasetyo

import io.ktor.server.application.*
import org.koin.ktor.ext.inject
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureKoin()
    val db by inject<AppDatabase>()
    db.connect()
    configureSerialization()
    configureJWT()
    configureRouting()
}
