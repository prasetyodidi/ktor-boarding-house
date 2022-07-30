package tech.didiprasetyo.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import tech.didiprasetyo.data.local.entity.UserEntity
import java.util.*

interface UserRepository {

    suspend fun getById(id: UUID): UserEntity?

    suspend fun getByEmail(email: String): UserEntity?

    suspend fun insert(item: UserEntity)

    suspend fun update(item: UserEntity)

    suspend fun delete(item: UserEntity)

    fun ResultRow.intoEntity(): UserEntity
}