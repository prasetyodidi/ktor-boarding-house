package tech.didiprasetyo

import com.typesafe.config.ConfigFactory
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.mail.SimpleEmail
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.ktor.ext.inject
import org.mindrot.jbcrypt.BCrypt
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.domain.model.Email
import tech.didiprasetyo.domain.model.Token
import tech.didiprasetyo.domain.model.UserLogin
import tech.didiprasetyo.domain.model.UserRegister
import tech.didiprasetyo.plugins.configureKoin
import tech.didiprasetyo.routing.authRouting
import tech.didiprasetyo.util.Response
import java.util.*

class AuthTest {

    @Test
    fun `test register password length too short`() = testApplication{
        application {
            configureKoin()
            val db by inject<AppDatabase>()
            db.connect()
            routing {
                authRouting()
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.post("/register"){
            val id = UUID.randomUUID()
            val name = "ucup"
            val email = "milly.hoppe@hotmail.test"
            val password = "pass"
            contentType(ContentType.Application.Json)
            setBody(UserRegister(
                id = id.toString(),
                name = name,
                email = email,
                password = password
            ))
        }.apply {
            val response = body<Response<Email>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("password length at least 6 character", message)
        }
        stopKoin()
    }

    @Test
    fun `test register already user`() = testApplication{
        application {
            configureKoin()
            val db by inject<AppDatabase>()
            db.connect()
            routing {
                authRouting()
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.post("/register"){
            val id = UUID.randomUUID()
            val name = "ucup"
            val email = "milly.hoppe@hotmail.test"
            val password = "password"
            contentType(ContentType.Application.Json)
            setBody(UserRegister(
                id = id.toString(),
                name = name,
                email = email,
                password = password
            ))
        }.apply {
            val response = body<Response<Email>>()
            val status = response.status
            Assertions.assertEquals("Success", status.name)
        }
        stopKoin()
    }

    @Test
    fun `test success register`() = testApplication{
        application {
            configureKoin()
            val db by inject<AppDatabase>()
            db.connect()
            routing {
                authRouting()
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.post("/register"){
            val id = UUID.randomUUID()
            val name = "ucup"
            val email = "ucup@gmail.com"
            val password = BCrypt.hashpw("password", BCrypt.gensalt())
            header(HttpHeaders.UserAgent, "testing-device")
            contentType(ContentType.Application.Json)
            setBody(UserRegister(
                id = id.toString(),
                name = name,
                email = email,
                password = password
            ))
        }.apply {
            val response = body<Response<Email>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("success register new account", message)
            Assertions.assertEquals("Success", status.name)
        }
        stopKoin()
    }

    @Test
    fun testSuccessLogin() = testApplication{
        application {
            configureKoin()
            val db by inject<AppDatabase>()
            db.connect()
            routing {
                authRouting()
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.post("/login"){
            val email = "milly.hoppe@hotmail.test"
            val password = "password"
            header(HttpHeaders.UserAgent, "testing-device")
            contentType(ContentType.Application.Json)
            setBody(UserLogin(
                email,
                password
            ))
        }.apply {
            val status = body<Response<Token>>().status
            Assertions.assertEquals("Success", status.name)
            println("response login : ${body<Response<Token>>()}")
        }
        stopKoin()
    }

    @Test
    fun `test user agent not found`() = testApplication{
        application {
            stopKoin()
            configureKoin()
            val db by inject<AppDatabase>()
            db.connect()
            routing {
                authRouting()
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.post("/login"){
            val email = "ucup@gmail.com"
            val password = "password"
            setBody(UserLogin(
                email,
                password
            ))
            contentType(ContentType.Application.Json)
        }.apply {
            val response = body<Response<Token>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("user agent not found", message)
        }
        stopKoin()
    }

    @Test
    fun `test user property login invalid`() = testApplication{
        application {
            configureKoin()
            val db by inject<AppDatabase>()
            db.connect()
            routing {
                authRouting()
            }
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        client.post("/login"){
            val email = "ucup@gmail.com"
            val password = "invalidpassword"
            setBody(UserLogin(
                email,
                password
            ))
            header(HttpHeaders.UserAgent, "testing-device")
            contentType(ContentType.Application.Json)
        }.apply {
            val response = body<Response<Token>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("cannot create token", message)
            println("response login : ${body<Response<Token>>()}")
        }
        stopKoin()
    }

    @Test
    @Disabled
    fun testSendEmail() {
        sendVerifyEmail()
    }

    @Test
    @Disabled
    fun testSendEmailWithHtml(){
        sendVerifyEmailWithHtml()
    }

    @Test
    @Disabled
    fun testGetEnvProperty() {
        val config = HoconApplicationConfig(ConfigFactory.load())
        println("issuer : ${config.property("jwt.issuer").getString()}")
    }

    private fun sendVerifyEmail() {
        val email = SimpleEmail()
        email.hostName = "smtp.mailtrap.io"
        email.setSmtpPort(2525)
        email.setAuthentication("08311fb1f4ad18", "5662f1b73a11f0")
        email.setFrom("noreply@mykos.tect")
        email.subject = "verify-email"
        email.setMsg("link verify : link <br> link remove account : link")
        email.addTo("ucup@gmail.com")
        email.send()
    }

    private fun sendVerifyEmailWithHtml() {
        val email = HtmlEmail()
        email.hostName = "smtp.mailtrap.io"
        email.setSmtpPort(2525)
        email.setAuthentication("08311fb1f4ad18", "5662f1b73a11f0")
        email.setFrom("noreply@mykos.tect")
        email.subject = "verify-email"
        email.setHtmlMsg(
            "\n" +
                    "<a href=\"url\">link verify</a><br>\n" +
                    "\n" +
                    "<a href=\"url\">link delete account</a>\n" +
                    "\n"
        )
        email.setTextMsg("Your email client does not support HTML messages")
        email.addTo("ucup@gmail.com")
        email.send()
    }

//    companion object {
//        @BeforeAll
//        @JvmStatic
//        fun setUp() = testApplication{
//            application {
//                configureKoin()
//                val db by inject<AppDatabase>()
//                db.connect()
//                routing {
//                    authRouting()
//                }
//                stopKoin()
//            }
//        }
//    }
}