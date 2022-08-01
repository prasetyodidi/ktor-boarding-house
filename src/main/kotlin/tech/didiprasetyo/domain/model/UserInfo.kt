package tech.didiprasetyo.domain.model

@kotlinx.serialization.Serializable
data class UserInfo(
    val id: String,
    val name: String,
    val email: String,
    val noTelp: String? = null,
    val avatarUrl: String? = null
)