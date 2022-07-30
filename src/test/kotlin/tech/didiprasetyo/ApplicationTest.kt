package tech.didiprasetyo

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import tech.didiprasetyo.plugins.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTest {

    @BeforeAll
    fun setUp() = testApplication {
        application {
            configureKoin()
        }
    }

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            Assertions.assertEquals(HttpStatusCode.OK, status)
            Assertions.assertEquals("Hello World!", bodyAsText())
        }
    }

    @Test
    fun testInvalidToken() = testApplication{
        application {
            configureJWT()
            routing {
                authenticate("auth-jwt") {
                    get("/test-jwt"){
                        call.respond("jwt valid")
                    }
                }
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.get("/test-jwt").apply {
            Assertions.assertEquals("Token is not valid or has expired", bodyAsText())
        }
    }

    @Test
    fun testValidToken() = testApplication {
        application {
            configureJWT()
            routing {
                authenticate("auth-jwt"){
                    get("/test-jwt") {
                        call.respond("jwt valid")
                    }
                }
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.get("test-jwt"){
            val token = ""
            header(HttpHeaders.Authorization, "Bearer $token")
        }.apply {
            Assertions.assertEquals("jwt valid", bodyAsText())
        }
    }
}