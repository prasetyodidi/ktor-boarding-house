package tech.didiprasetyo.domain.model

@kotlinx.serialization.Serializable
data class RoomInfo(
    val roomId: String,
    val rules: List<String>,
    val reminders: List<Reminder>,
    val dateEntry: Long,
    val dateOut: Long
)
