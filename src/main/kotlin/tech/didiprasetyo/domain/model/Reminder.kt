package tech.didiprasetyo.domain.model

@kotlinx.serialization.Serializable
data class Reminder(
    val value: String,
    val imageUrl: String
)
