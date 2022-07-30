package tech.didiprasetyo.domain.model

import java.util.*

data class Room(
    val id: UUID,
    val name: String,
    val imageUrl: String,
    val dateEntry: Long?,
    val dateOut: Long?
)
