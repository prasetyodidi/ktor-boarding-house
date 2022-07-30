package tech.didiprasetyo.domain.model

@kotlinx.serialization.Serializable
data class RoomInfo(
    val rules: List<String>,
    val reminders: List<Reminder>,
    val reminderDate: Long = 0
)
