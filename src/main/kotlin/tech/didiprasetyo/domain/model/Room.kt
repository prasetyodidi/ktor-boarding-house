package tech.didiprasetyo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: String,
    val name: String,
    val imageUrl: String,
    val dateEntry: Long?,
    val dateOut: Long?
)
