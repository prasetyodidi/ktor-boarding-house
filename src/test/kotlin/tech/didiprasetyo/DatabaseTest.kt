package tech.didiprasetyo

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.serpro69.kfaker.Faker
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseTest {
    private val dbUrl = "jdbc:postgresql://localhost:5432/kos"
    private val user = "user3"
    private val pass = "user3pass"
    lateinit var fake: Faker

    object TestTable: Table(){
        val id = uuid("id")
        val name = varchar("name", 255)

        override val primaryKey = PrimaryKey(id)
    }

    @BeforeAll
    internal fun setUp() {
        fake = Faker()
        connect()
        transaction {
            SchemaUtils.create(TestTable)
            for (i in 0..10){
                TestTable.insert {
                    it[id] = UUID.randomUUID()
                    it[name] = fake.name.firstName()
                }
            }
        }
    }

    @Test
    fun testTableTest(){
        transaction {
            val listData = TestTable.selectAll()
            listData.forEach {
                println("name : ${it[TestTable.name]}")
            }
            Assertions.assertNotNull(listData)
        }
    }

    fun connect() {
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource {
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

    @Test
    fun testFaker(){
        val randomLong = fake.random.nextLong()
        val randomLong2 = fake.random.nextLong()
        println("random1: $randomLong")
        println("random2: $randomLong2")
    }

}