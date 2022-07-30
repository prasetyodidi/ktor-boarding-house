package tech.didiprasetyo.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import tech.didiprasetyo.data.local.entity.RuleEntity
import java.util.*

interface RuleRepository {

    suspend fun getByBoardingHouseId(id: UUID): List<RuleEntity>

    suspend fun insert(item: RuleEntity)

    suspend fun update(item: RuleEntity)

    suspend fun delete(item: RuleEntity)

    fun ResultRow.intoEntity(): RuleEntity
}