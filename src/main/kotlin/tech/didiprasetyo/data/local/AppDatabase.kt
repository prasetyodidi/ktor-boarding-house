package tech.didiprasetyo.data.local

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database

class AppDatabase {
    private val config = HoconApplicationConfig(ConfigFactory.load())
    private val dbUrl = config.property("db.jdbcUrl").getString()
    private val user = config.property("db.user").getString()
    private val pass = config.property("db.pass").getString()

    fun connect() {
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource{
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = dbUrl
        config.username = user
        config.password = pass
        config.maximumPoolSize = 5
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()

        return HikariDataSource(config)
    }
}