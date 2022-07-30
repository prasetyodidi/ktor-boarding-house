package tech.didiprasetyo.domain.service

import tech.didiprasetyo.data.local.entity.UserEntity
import tech.didiprasetyo.domain.model.UserInfo
import tech.didiprasetyo.domain.repository.UserRepository
import java.util.UUID

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun getUserInfo(userId: UUID): UserInfo? {
        return userRepository.getById(userId)?.intoUserInfo()
    }

    suspend fun updateUser(updateData: UserEntity){
        userRepository.update(updateData)
    }
}