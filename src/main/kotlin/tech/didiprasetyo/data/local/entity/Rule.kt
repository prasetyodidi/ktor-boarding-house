package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class Rule(
    val id: UUID,
    val boardingHouseId: UUID,
    val value: String,
    val createdAt: Long,
    val updatedAt: Long
)
