package tech.didiprasetyo

import com.typesafe.config.ConfigFactory
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.mail.SimpleEmail
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mindrot.jbcrypt.BCrypt
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.domain.model.Email
import tech.didiprasetyo.domain.model.Token
import tech.didiprasetyo.domain.model.UserLogin
import tech.didiprasetyo.domain.model.UserRegister
import tech.didiprasetyo.util.Response
import java.util.*

class AuthTest : KoinTest {
    @Test
    fun `test register password length too short`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/register") {
            val id = UUID.randomUUID()
            val name = "ucup"
            val email = "milly.hoppe@hotmail.test"
            val password = "pass"
            contentType(ContentType.Application.Json)
            setBody(
                UserRegister(
                    id = id.toString(),
                    name = name,
                    email = email,
                    password = password
                )
            )
        }.apply {
            val response = body<Response<Email>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("password length at least 6 character", message)
        }

    }

    @Test
    fun `test register already user`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/register") {
            val id = UUID.randomUUID()
            val name = "ucup"
            val email = "milly.hoppe@hotmail.test"
            val password = "password"
            contentType(ContentType.Application.Json)
            setBody(
                UserRegister(
                    id = id.toString(),
                    name = name,
                    email = email,
                    password = password
                )
            )
        }.apply {
            val response = body<Response<Email>>()
            val status = response.status
            Assertions.assertEquals("Success", status.name)
        }
        stopKoin()
    }

    @Test
//    @Disabled("must verify user does not exist")
    fun `test success register`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/register") {
            val id = UUID.randomUUID()
            val name = "ucup"
            val email = "ucup@gmail.com"
            val password = BCrypt.hashpw("password", BCrypt.gensalt())
            header(HttpHeaders.UserAgent, "testing-device")
            contentType(ContentType.Application.Json)
            setBody(
                UserRegister(
                    id = id.toString(),
                    name = name,
                    email = email,
                    password = password
                )
            )
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
    fun testSuccessLogin() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/login") {
            val email = "milly.hoppe@hotmail.test"
            val password = "password"
            header(HttpHeaders.UserAgent, "testing-device")
            contentType(ContentType.Application.Json)
            setBody(
                UserLogin(
                    email,
                    password
                )
            )
        }.apply {
            val status = body<Response<Token>>().status
            Assertions.assertEquals("Success", status.name)
            println("response login : ${body<Response<Token>>()}")
        }
        stopKoin()
    }

    @Test
    fun `test user agent not found`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/login") {
            val email = "ucup@gmail.com"
            val password = "password"
            setBody(
                UserLogin(
                    email,
                    password
                )
            )
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
    fun `test user property login invalid`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/login") {
            val email = "ucup@gmail.com"
            val password = "invalidpassword"
            setBody(
                UserLogin(
                    email,
                    password
                )
            )
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
    fun `test fail verify email`() = testApplication {
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        val invalidToken = "fgsgfsgsg4f34tfr"
        client.get("/verify-email/$invalidToken").apply {
            val response = body<Response<String>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("email not verified", message)
        }
    }

    @Test
    @Disabled("must configure valid token email")
    fun `test success verify email`() = testApplication {
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        val token = ""
        client.get("/verify-email/$token").apply {
            val response = body<Response<String>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("email has been verified", message)
        }
    }

    @Test
    fun `test fail delete email`() = testApplication {
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        val token = ""
        client.get("delete-email/$token").apply {
            println("error ${bodyAsText()}")
            val response = body<Response<String>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Fail", status.name)
            Assertions.assertEquals("email not deleted", message)
        }
    }

    @Test
    @Disabled("must configure valid token email")
    fun `test success delete email`() = testApplication {
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        val token = ""
        client.get("/delete-email/$token").apply {
            val response = body<Response<String>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Success", status.name)
            Assertions.assertEquals("email has been deleted", message)
        }
    }

    @Test
    fun `test fail logout`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val invalidToken = ""
        client.get("/logout") {
            header(HttpHeaders.Authorization, "Bearer $invalidToken")
        }.apply {
            Assertions.assertEquals("Token is not valid or has expired", body<String>())
        }
    }

    @Test
    @Disabled("must configure valid token")
    fun `test success logout`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJodHRwOi8vMC4wLjAuMDo4MDgwL2hlbGxvIiwic2Vzc2lvbiI6ImE1YjA5NTY4LWRlMDUtNDhkZS1iNjlhLTYzYWI0NDlmOThhOCIsImlzcyI6Imh0dHA6Ly8wLjAuMC4wOjgwODAvIiwidmVyaWZpZWQiOnRydWUsImV4cCI6MTY1OTM2OTAwMywidXNlciI6ImMzODcyMzEyLTc4ODQtNDJmNi04NDQ5LWYxNDZhYWQzMmY5ZiJ9.SCQ5xVgARO3Ld0KzmJdnr0NCqB1PhJXYi2ez8JmFcfc"
        client.get("/logout") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.apply {
            val response = body<Response<String>>()
            val status = response.status
            val message = response.message
            Assertions.assertEquals("Success", status.name)
            Assertions.assertEquals("logout success", message)
        }
    }

    @Test
    @Disabled
    fun testSendEmail() {
        sendVerifyEmail()
    }

    @Test
    @Disabled
    fun testSendEmailWithHtml() {
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
        email.setHtmlMsg(setHtmlVerifyEmail("Ucup", "example token"))
        email.setTextMsg("Your email client does not support HTML messages")
        email.addTo("ucup@gmail.com")
        email.send()
    }


    private fun setHtmlVerifyEmail(name: String, token: String):String {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\n" +
                "  <title>Email Confirmation</title>\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "  <style type=\"text/css\">\n" +
                "  /**\n" +
                "   * Google webfonts. Recommended to include the .woff version for cross-client compatibility.\n" +
                "   */\n" +
                "  @media screen {\n" +
                "    @font-face {\n" +
                "      font-family: 'Source Sans Pro';\n" +
                "      font-style: normal;\n" +
                "      font-weight: 400;\n" +
                "      src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\n" +
                "    }\n" +
                "\n" +
                "    @font-face {\n" +
                "      font-family: 'Source Sans Pro';\n" +
                "      font-style: normal;\n" +
                "      font-weight: 700;\n" +
                "      src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Avoid browser level font resizing.\n" +
                "   * 1. Windows Mobile\n" +
                "   * 2. iOS / OSX\n" +
                "   */\n" +
                "  body,\n" +
                "  table,\n" +
                "  td,\n" +
                "  a {\n" +
                "    -ms-text-size-adjust: 100%; /* 1 */\n" +
                "    -webkit-text-size-adjust: 100%; /* 2 */\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Remove extra space added to tables and cells in Outlook.\n" +
                "   */\n" +
                "  table,\n" +
                "  td {\n" +
                "    mso-table-rspace: 0pt;\n" +
                "    mso-table-lspace: 0pt;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Better fluid images in Internet Explorer.\n" +
                "   */\n" +
                "  img {\n" +
                "    -ms-interpolation-mode: bicubic;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Remove blue links for iOS devices.\n" +
                "   */\n" +
                "  a[x-apple-data-detectors] {\n" +
                "    font-family: inherit !important;\n" +
                "    font-size: inherit !important;\n" +
                "    font-weight: inherit !important;\n" +
                "    line-height: inherit !important;\n" +
                "    color: inherit !important;\n" +
                "    text-decoration: none !important;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Fix centering issues in Android 4.4.\n" +
                "   */\n" +
                "  div[style*=\"margin: 16px 0;\"] {\n" +
                "    margin: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  body {\n" +
                "    width: 100% !important;\n" +
                "    height: 100% !important;\n" +
                "    padding: 0 !important;\n" +
                "    margin: 0 !important;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * Collapse table borders to avoid space between cells.\n" +
                "   */\n" +
                "  table {\n" +
                "    border-collapse: collapse !important;\n" +
                "  }\n" +
                "\n" +
                "  a {\n" +
                "    color: #1a82e2;\n" +
                "  }\n" +
                "\n" +
                "  img {\n" +
                "    height: auto;\n" +
                "    line-height: 100%;\n" +
                "    text-decoration: none;\n" +
                "    border: 0;\n" +
                "    outline: none;\n" +
                "  }\n" +
                "  </style>\n" +
                "\n" +
                "</head>\n" +
                "<body style=\"background-color: #e9ecef;\">\n" +
                "\n" +
                "  <!-- start preheader -->\n" +
                "  <div class=\"preheader\" style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n" +
                "    A preheader is the short summary text that follows the subject line when an email is viewed in the inbox.\n" +
                "  </div>\n" +
                "  <!-- end preheader -->\n" +
                "\n" +
                "  <!-- start body -->\n" +
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "\n" +
                "    <!-- start logo -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "          <tr>\n" +
                "            <td align=\"center\" valign=\"top\" style=\"padding: 36px 24px;\">\n" +
                "              <a href=\"https://sendgrid.com\" target=\"_blank\" style=\"display: inline-block;\">\n" +
                "                <img src=\"https://i.postimg.cc/Nf2tPHRp/Group-43.png\" alt=\"Logo\" border=\"0\" width=\"48\" style=\"display: block; width: 120px; max-width: 120px; min-width: 48px;\">\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end logo -->\n" +
                "\n" +
                "    <!-- start hero -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n" +
                "              <h1 style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Confirm Your Email Address</h1>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </table>\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end hero -->\n" +
                "\n" +
                "    <!-- start copy block -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "\n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "              <p style=\"font-size: 20px;\">Hi $name,</p>\n" +
                "              <p style=\"margin: 0;\">Tap the button below to confirm your email address.</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "          <!-- start button -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\">\n" +
                "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "                <tr>\n" +
                "                  <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\n" +
                "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                      <tr>\n" +
                "                        <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\n" +
                "                          <a href=\"http://127.0.0.1/verify-email/$token\" target=\"_blank\" style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">Verify Email Address</a>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                    </table>\n" +
                "                  </td>\n" +
                "                </tr>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end button -->\n" +
                "          \n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "              <p style=\"margin: 0;\">Did you receive this email without signing up? <a href=\"http://127.0.0.1/delete-email/$token\">click here</a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\n" +
                "              <p style=\"margin: 0;\">If that doesn't work, copy and paste the following link in your browser:</p>\n" +
                "              <p style=\"margin: 0;\"><a href=\"https://sendgrid.com\" target=\"_blank\">https://same-link-as-button.url/xxx-xxx-xxxx</a></p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "          <!-- start copy -->\n" +
                "          <tr>\n" +
                "            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf\">\n" +
                "              <p style=\"margin: 0;\">Cheers,<br> Paste</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end copy -->\n" +
                "\n" +
                "        </table>\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end copy block -->\n" +
                "\n" +
                "    <!-- start footer -->\n" +
                "    <tr>\n" +
                "      <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n" +
                "        <tr>\n" +
                "        <td align=\"center\" valign=\"top\" width=\"600\">\n" +
                "        <![endif]-->\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px;\">\n" +
                "\n" +
                "          <!-- start permission -->\n" +
                "          <tr>\n" +
                "            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n" +
                "              <p style=\"margin: 0;\">You received this email because we received a request for [type_of_action] for your account. If you didn't request [type_of_action] you can safely delete this email.</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end permission -->\n" +
                "\n" +
                "          <!-- start unsubscribe -->\n" +
                "          <tr>\n" +
                "            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n" +
                "              <p style=\"margin: 0;\">To stop receiving these emails, you can <a href=\"https://sendgrid.com\" target=\"_blank\">unsubscribe</a> at any time.</p>\n" +
                "              <p style=\"margin: 0;\">Paste 1234 S. Broadway St. City, State 12345</p>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "          <!-- end unsubscribe -->\n" +
                "\n" +
                "        </table>\n" +
                "        <!--[if (gte mso 9)|(IE)]>\n" +
                "        </td>\n" +
                "        </tr>\n" +
                "        </table>\n" +
                "        <![endif]-->\n" +
                "      </td>\n" +
                "    </tr>\n" +
                "    <!-- end footer -->\n" +
                "\n" +
                "  </table>\n" +
                "  <!-- end body -->\n" +
                "\n" +
                "</body>\n" +
                "</html>\n"
    }

    companion object {
        lateinit var db: AppDatabase

        @BeforeAll
        @JvmStatic
        fun setUp() {
            db = AppDatabase(HoconApplicationConfig(ConfigFactory.load()))
            db.connect()
        }
    }
}