package tech.didiprasetyo.data.local.entity

import tech.didiprasetyo.domain.model.Reminder
import java.util.UUID

data class ReminderEntity(
    val id: UUID,
    val boardingHouseId: UUID,
    val value: String,
    val imageUrl: String,
    val createdAt: Long,
    val updatedAt: Long
){
    fun intoReminder(): Reminder{
        return Reminder(
            value = value,
            imageUrl = imageUrl
        )
    }
}