package tech.didiprasetyo.data.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.local.dao.Sessions
import tech.didiprasetyo.data.local.entity.SessionEntity
import tech.didiprasetyo.domain.repository.SessionRepository
import java.util.*

class SessionRepositoryImpl: SessionRepository {
    override suspend fun getByUserId(userId: UUID): List<SessionEntity> = AppDatabase.dbQuery{
        Sessions.select { Sessions.idUser eq userId }.map { it.intoEntity() }
    }

    override suspend fun getById(id: UUID): SessionEntity? = AppDatabase.dbQuery{
        Sessions.select { Sessions.id eq id }.firstOrNull()?.intoEntity()
    }

    override suspend fun createSession(idUserData: UUID, deviceData: String): UUID = AppDatabase.dbQuery{
        val uuid = UUID.randomUUID()
        Sessions.insert {
            it[id] = uuid
            it[idUser] = idUserData
            it[device] = deviceData
            it[createdAt] = System.currentTimeMillis()/1000
        }
        uuid
    }

    override suspend fun delete(id: UUID): Unit = AppDatabase.dbQuery {
        Sessions.deleteWhere { Sessions.id eq id }
    }

    override fun ResultRow.intoEntity(): SessionEntity {
        return SessionEntity(
            id = this[Sessions.id],
            idUser = this[Sessions.idUser],
            device = this[Sessions.device],
            createAt = this[Sessions.createdAt]
        )
    }
}