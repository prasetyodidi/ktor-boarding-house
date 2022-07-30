package tech.didiprasetyo.plugins

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.repository.*
import tech.didiprasetyo.domain.repository.*
import tech.didiprasetyo.domain.service.AuthService
import tech.didiprasetyo.domain.service.RoomService

fun Application.configureKoin(){
    val appModule = module {
        single { HoconApplicationConfig(ConfigFactory.load()) }

        single<RoomRepository> { RoomRepositoryImpl() }
        single<RuleRepository> { RuleRepositoryImpl() }
        single<ReminderRepository> { ReminderRepositoryImpl() }
        single<SessionRepository> { SessionRepositoryImpl() }
        single<UserRepository> { UserRepositoryImpl() }

        single { EmailToken(get()) }
        single<AuthService> { AuthService(get(), get(), get(), get()) }
        single { RoomService(get(), get(), get()) }

        single { AppDatabase(get()) }
    }

    install(Koin){
        slf4jLogger()
        modules(appModule)
    }
}