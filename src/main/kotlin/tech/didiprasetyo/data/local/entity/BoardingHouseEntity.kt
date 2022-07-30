package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class BoardingHouseEntity(
    val id: UUID,
    val idOwner: UUID,
    val name: String,
    val address: String,
    val mapUrl: String,
    val imageUrl: String,
    val createdAt: Long,
    val updatedAt: Long
)
