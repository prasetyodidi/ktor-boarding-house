package tech.didiprasetyo.domain.model

@kotlinx.serialization.Serializable
data class UserRegister(
    val id: String,
    val name: String,
    val email: String,
    val password: String
)
