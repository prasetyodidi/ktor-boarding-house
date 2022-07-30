package tech.didiprasetyo

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtTokenTest {
    private val secret = "secretkey"
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private lateinit var token: String
    private lateinit var email: String
    private lateinit var action: Action
    private val verifier: JWTVerifier = JWT.require(algorithm).build()

    fun sign(email: String, action: Action): String = JWT.create()
        .withClaim("email", email)
        .withClaim("action", action.name)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(algorithm)

    @BeforeAll
    fun setUp(){
        email = "email@example.com"
        action = Action.Update
        token = sign(email, action)
    }

    @Test
    fun testGenerateToken(){
        val token2 = sign(email, Action.Update)
        val token3 = sign(email, Action.Delete)
        println("token1 : $token")
        println("token3 : $token3")
        Assertions.assertEquals(token, token2)
        Assertions.assertNotEquals(token, token3)
        val email = verifier
            .verify(token)
            .getClaim("email")
        println(email)
    }

    @Test
    fun testIllegalToken(){
        val illegalToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhY3Rpb24iOiJEZWxldGUiLCJleHAiOjE2NTg5MjgwNTUsImVtYWlsIjoiZW1haWxAZXhhbXBsZS5jb20ifQ.bMYJ3dLqyjqPajxjkVodjc1fcc-DNmz2ZnCJdZ-mjUs"
        Assertions.assertThrows(JWTVerificationException::class.java){
            verifier.verify(illegalToken)
        }
    }

    @Test
    fun testValidToken(){
        Assertions.assertDoesNotThrow{
            verifier.verify(token)
        }
        verifier.verify(token).run {
            val emailClaim = getClaim("email").asString()
            val actionClaim = getClaim("action").asString()
            Assertions.assertEquals(email, emailClaim)
            Assertions.assertEquals(action.name, actionClaim)
        }
    }

    @Test
    fun testConvertUUID(){
        val validStringUUID = UUID.randomUUID().toString()
        val notValidStringUUID = "sfdkjgf-fksgb;sfgbfk"

        Assertions.assertDoesNotThrow{
            UUID.fromString(validStringUUID)
        }
        Assertions.assertThrows(java.lang.IllegalArgumentException::class.java){
            UUID.fromString(notValidStringUUID)
        }
    }

    enum class Action{
        Update, Delete
    }

}