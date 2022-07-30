package tech.didiprasetyo.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import tech.didiprasetyo.data.local.entity.ReminderEntity
import java.util.*

interface ReminderRepository {

    suspend fun getByBoardingHouseId(boardingHouseId: UUID): List<ReminderEntity>

    suspend fun insert(item: ReminderEntity)

    suspend fun update(item: ReminderEntity)

    suspend fun delete(item: ReminderEntity)

    fun ResultRow.intoEntity(): ReminderEntity
}