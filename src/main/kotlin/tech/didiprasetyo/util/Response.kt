package tech.didiprasetyo.util

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val status: Status,
    val message: String,
    val data: List<T>?
)

@Serializable
enum class Status{
    Success,
    Fail
}