package tech.didiprasetyo.data.repository

import org.jetbrains.exposed.sql.*
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.local.dao.Users
import tech.didiprasetyo.data.local.entity.UserEntity
import tech.didiprasetyo.domain.repository.UserRepository
import java.util.*

class UserRepositoryImpl: UserRepository {
    override suspend fun getById(id: UUID): UserEntity? = AppDatabase.dbQuery {
        Users.select { Users.id eq id}.firstOrNull()?.intoEntity()
    }

    override suspend fun getByEmail(email: String): UserEntity? = AppDatabase.dbQuery{
        Users.select { Users.email eq email }.firstOrNull()?.intoEntity()
    }

    override suspend fun insert(item: UserEntity): Unit = AppDatabase.dbQuery {
        val now = System.currentTimeMillis()/1000
        Users.insert {
            it[id] = item.id
            it[name] = item.name
            it[email] = item.email
            it[password] = item.password
            it[createdAt] = now
            it[updatedAt] = now
        }
    }

    override suspend fun update(item: UserEntity): Unit = AppDatabase.dbQuery {
        val now = System.currentTimeMillis()/1000
        Users.update({Users.id eq item.id}) {
            it[name] = item.name
            it[email] = item.email
            it[password] = item.password
            it[noTelp] = item.noTelp
            it[verifiedAt] = item.verifiedAt
            it[avatarUrl] = item.avatarUrl
            it[updatedAt] = now
        }
    }

    override suspend fun delete(item: UserEntity): Unit = AppDatabase.dbQuery {
        Users.deleteWhere { Users.id eq item.id}
    }

    override fun ResultRow.intoEntity(): UserEntity {
        return UserEntity(
            id = this[Users.id],
            name = this[Users.name],
            email = this[Users.email],
            password = this[Users.password],
            noTelp = this[Users.noTelp],
            avatarUrl = this[Users.avatarUrl],
            verifiedAt = this[Users.verifiedAt],
            createdAt = this[Users.createdAt],
            updatedAt = this[Users.updatedAt],
        )
    }
}