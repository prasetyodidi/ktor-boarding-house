package tech.didiprasetyo.domain.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.server.config.*
import org.apache.commons.mail.HtmlEmail
import org.mindrot.jbcrypt.BCrypt
import tech.didiprasetyo.data.local.entity.SessionEntity
import tech.didiprasetyo.data.local.entity.UserEntity
import tech.didiprasetyo.domain.model.Email
import tech.didiprasetyo.domain.model.UserLogin
import tech.didiprasetyo.domain.model.UserRegister
import tech.didiprasetyo.domain.repository.SessionRepository
import tech.didiprasetyo.domain.repository.UserRepository
import tech.didiprasetyo.plugins.EmailToken
import tech.didiprasetyo.util.Response
import tech.didiprasetyo.util.Status
import java.util.*

class AuthService(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val emailToken: EmailToken,
    private val config: HoconApplicationConfig
) {
    private val secret = config.property("jwt.secret").getString()
    private val audience = config.property("jwt.audience").getString()
    private val issuer = config.property("jwt.issuer").getString()

    suspend fun login(user: UserLogin, device: String): String? {
        // check password
        val userExist = userRepository.getByEmail(user.email)
        if (userExist == null || !BCrypt.checkpw(user.password, userExist.password)) {
            return null
        }
        // create session
        val session = sessionRepository.createSession(userExist.id, device)
        // create token
        return createUserToken(userExist, session.toString())
    }

    suspend fun register(user: UserRegister): Response<Email> {
        return try {
            // check email user
            val email = userRepository.getByEmail(user.email)
            if (email != null) {
                return Response(
                    status = Status.Success,
                    message = "email already exist",
                    data = listOf(Email(user.email))
                )
            }
            // insert user
            val now = System.currentTimeMillis() / 1000
            val userDate = UserEntity(
                id = UUID.fromString(user.id),
                name = user.name,
                email = user.email,
                password = user.password,
                noTelp = null,
                avatarUrl = null,
                verifiedAt = null,
                createdAt = now,
                updatedAt = now
            )
            userRepository.insert(userDate)
            val token = emailToken.sign(user.email)

            // send email verification
            sendVerifyEmail(user.email, token)
            Response(
                status = Status.Success,
                message = "success register new account",
                data = listOf(Email(user.email))
            )
        } catch (e: Throwable) {
            Response(
                status = Status.Success,
                message = "error: ${e.message}",
                data = emptyList()
            )
        }
    }

    suspend fun logout(sessionId: UUID): Boolean {
        try {
            sessionRepository.delete(sessionId)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    fun resetPassword() {

    }

    suspend fun getSessionByUserId(id: UUID): List<SessionEntity> {
        return sessionRepository.getByUserId(id)
    }

    suspend fun getSessionById(id: UUID): SessionEntity? {
        return sessionRepository.getById(id)
    }

    suspend fun verifyEmail(token: String) {
        try {
            // verify token
            val verify = emailToken.verifier.verify(token)

            // get email claim
            val email = verify.getClaim("email").asString()

            // update user
            val user = userRepository.getByEmail(email) ?: throw IllegalArgumentException("email now found")
            val now = System.currentTimeMillis() / 1000
            userRepository.update(user.copy(verifiedAt = now))
        } catch (e: JWTVerificationException) {
            println("error : ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("error : ${e.message}")
        }
    }

    suspend fun deleteEmail(token: String) {
        try {
            // verify token
            val verify = emailToken.verifier.verify(token)

            // get email claim
            val email = verify.getClaim("email").asString()

            // delete user
            val user = userRepository.getByEmail(email) ?: throw IllegalArgumentException("email now found")
            if (user.verifiedAt == null){
                userRepository.delete(user)
            }
        } catch (e: JWTVerificationException) {
            println("error : ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("error : ${e.message}")
        }
    }

    private fun createUserToken(user: UserEntity, session: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("user", user.id.toString())
            .withClaim("session", session)
            .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 3600))
            .sign(Algorithm.HMAC256(secret))
    }

    private fun sendVerifyEmail(toEmail: String, token: String) {
        val email = HtmlEmail()
//        val hostName = config.property("").getString()
        email.hostName = "smtp.mailtrap.io"
        email.setSmtpPort(2525)
        email.setAuthentication("08311fb1f4ad18", "5662f1b73a11f0")
        email.setFrom("noreply@mykos.tech")
        email.subject = "verify-email"
        email.setHtmlMsg(
            "\n" +
                    "<a href=\"http://127.0.0.1/verify-email/$token\">link verify</a><br>\n" +
                    "\n" +
                    "<a href=\"http://127.0.0.1/delete-email/$token\">link delete account</a>\n" +
                    "\n"
        )
        email.setTextMsg("Your email client does not support HTML messages")
        email.addTo(toEmail)
        email.send()
    }

}