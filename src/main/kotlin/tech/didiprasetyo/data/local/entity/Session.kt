package tech.didiprasetyo.data.local.entity

import java.util.UUID

data class Session(
    val id: UUID,
    val idUser: UUID,
    val device: String,
    val createAt: Long
)