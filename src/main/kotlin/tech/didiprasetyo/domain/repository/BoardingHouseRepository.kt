package tech.didiprasetyo.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import tech.didiprasetyo.data.local.entity.BoardingHouseEntity
import java.util.*

interface BoardingHouseRepository {

    suspend fun getById(id: UUID): BoardingHouseEntity?

    suspend fun insert(item: BoardingHouseEntity)

    suspend fun update(item: BoardingHouseEntity)

    suspend fun delete(item: BoardingHouseEntity)

    fun ResultRow.intoEntity(): BoardingHouseEntity
}