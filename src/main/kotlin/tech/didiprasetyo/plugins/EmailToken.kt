package tech.didiprasetyo.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*
import java.util.*

class EmailToken(config: HoconApplicationConfig) {
    private val secret = config.property("jwt.secret").getString()
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    val verifier: JWTVerifier = JWT.require(algorithm).build()
    fun sign(email: String): String = JWT.create()
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + 60000))
        .sign(algorithm)
}