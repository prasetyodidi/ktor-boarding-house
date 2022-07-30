package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class SessionEntity(
    val id: UUID,
    val idUser: UUID,
    val device: String,
    val createAt: Long
)