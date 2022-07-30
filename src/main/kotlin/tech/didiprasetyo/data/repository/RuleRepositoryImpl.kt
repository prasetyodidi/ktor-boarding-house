package tech.didiprasetyo.data.repository

import org.jetbrains.exposed.sql.*
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.local.dao.Rules
import tech.didiprasetyo.data.local.entity.RuleEntity
import tech.didiprasetyo.domain.repository.RuleRepository
import java.util.*

class RuleRepositoryImpl: RuleRepository {
    override suspend fun getByBoardingHouseId(id: UUID): List<RuleEntity> = AppDatabase.dbQuery {
        Rules.select { Rules.idBoardingHouse eq id }.map { it.intoEntity() }
    }

    override suspend fun insert(item: RuleEntity): Unit = AppDatabase.dbQuery {
        val date = System.currentTimeMillis()/1000
        Rules.insert {
            it[id] = item.id
            it[idBoardingHouse] = item.boardingHouseId
            it[rule] = item.value
            it[createdAt] = date
            it[updatedAt] = date
        }
    }

    override suspend fun update(item: RuleEntity): Unit = AppDatabase.dbQuery{
        val updated = System.currentTimeMillis()/1000
        Rules.update({ Rules.id eq item.id }) {
            it[rule] = item.value
            it[updatedAt] = updated
        }
    }

    override suspend fun delete(item: RuleEntity): Unit = AppDatabase.dbQuery {
        Rules.deleteWhere { Rules.id eq item.id }
    }

    override fun ResultRow.intoEntity(): RuleEntity {
        return RuleEntity(
            id = this[Rules.id],
            boardingHouseId = this[Rules.idBoardingHouse],
            value = this[Rules.rule],
            createdAt = this[Rules.createdAt],
            updatedAt = this[Rules.updatedAt],
        )
    }
}