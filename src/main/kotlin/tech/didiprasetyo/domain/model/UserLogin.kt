package tech.didiprasetyo.domain.model

@kotlinx.serialization.Serializable
data class UserLogin(
    val email: String,
    val password: String
)