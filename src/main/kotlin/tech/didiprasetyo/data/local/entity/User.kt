package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
    val email: String,
    val password: String,
    val noTelp: String?,
    val avatarUrl: String?,
    val createdAt: Long?,
    val updated: Long?
)