package tech.didiprasetyo.data.repository

import org.jetbrains.exposed.sql.*
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.local.dao.Reminders
import tech.didiprasetyo.data.local.entity.ReminderEntity
import tech.didiprasetyo.domain.repository.ReminderRepository
import java.util.*

class ReminderRepositoryImpl: ReminderRepository {

    override suspend fun getByBoardingHouseId(boardingHouseId: UUID): List<ReminderEntity> = AppDatabase.dbQuery {
        Reminders.select { Reminders.idBoardingHouse eq boardingHouseId }.map { it.intoEntity() }
    }

    override suspend fun insert(item: ReminderEntity): Unit = AppDatabase.dbQuery{
        val date = System.currentTimeMillis()/1000
        Reminders.insert {
            it[id] = item.id
            it[idBoardingHouse] = item.boardingHouseId
            it[reminder] = item.value
            it[createdAt] = date
            it[updatedAt] = date
        }
    }

    override suspend fun update(item: ReminderEntity): Unit = AppDatabase.dbQuery {
        val updated = System.currentTimeMillis()/1000
        Reminders.update({ Reminders.id eq item.id }) {
            it[reminder] = item.value
            it[updatedAt] = updated
        }
    }

    override suspend fun delete(item: ReminderEntity): Unit = AppDatabase.dbQuery{
        Reminders.deleteWhere { Reminders.id eq item.id }
    }

    override fun ResultRow.intoEntity(): ReminderEntity {
        return ReminderEntity(
            id = this[Reminders.id],
            boardingHouseId = this[Reminders.idBoardingHouse],
            value = this[Reminders.reminder],
            imageUrl = this[Reminders.imageUrl],
            createdAt = this[Reminders.createdAt],
            updatedAt = this[Reminders.updatedAt],
        )
    }
}