package tech.didiprasetyo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DateRange(
    val entry: Long,
    val out: Long
)