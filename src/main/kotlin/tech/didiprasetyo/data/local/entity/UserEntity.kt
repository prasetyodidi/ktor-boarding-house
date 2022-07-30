package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class UserEntity(
    val id: UUID,
    val name: String,
    val email: String,
    val password: String,
    val noTelp: String?,
    val avatarUrl: String?,
    val verifiedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long
)