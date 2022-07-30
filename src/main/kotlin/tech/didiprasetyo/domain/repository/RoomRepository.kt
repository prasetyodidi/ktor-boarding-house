package tech.didiprasetyo.domain.repository

import org.jetbrains.exposed.sql.ResultRow
import tech.didiprasetyo.data.local.entity.RoomEntity
import java.util.*

interface RoomRepository {

    suspend fun getById(id: UUID): RoomEntity?

    suspend fun getByTenantId(tenantId: UUID): List<RoomEntity>

    suspend fun getByBoardingHouseId(boardingHouseId: UUID): List<RoomEntity>

    suspend fun insert(item: RoomEntity)

    suspend fun update(item: RoomEntity)

    suspend fun delete(item: RoomEntity)

    fun ResultRow.intoEntity(): RoomEntity
}