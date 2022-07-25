package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class Room(
    val id: UUID,
    val idBoardingHouse: UUID,
    val idTenant: UUID?,
    val name: String,
    val imageUrl: String,
    val dateEntry: Long?,
    val dateOut: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)
