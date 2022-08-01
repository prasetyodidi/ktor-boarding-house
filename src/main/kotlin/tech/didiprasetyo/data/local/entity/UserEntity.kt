package tech.didiprasetyo.data.local.entity

import tech.didiprasetyo.domain.model.UserInfo
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
){
    fun intoUserInfo(): UserInfo{
        return UserInfo(
            id = id.toString(),
            name = name,
            email = email,
            noTelp = noTelp,
            avatarUrl = avatarUrl
        )
    }
}