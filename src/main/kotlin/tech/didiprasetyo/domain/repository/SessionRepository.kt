package tech.didiprasetyo.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import tech.didiprasetyo.data.local.entity.SessionEntity
import java.util.*

interface SessionRepository {

    suspend fun getByUserId(userId: UUID): List<SessionEntity>

    suspend fun getById(id: UUID): SessionEntity?

    suspend fun createSession(idUserData: UUID, deviceData: String): UUID

    suspend fun delete(id: UUID)

    fun ResultRow.intoEntity(): SessionEntity
}