package tech.didiprasetyo.data.repository

import org.jetbrains.exposed.sql.*
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.local.dao.Rooms
import tech.didiprasetyo.data.local.entity.RoomEntity
import tech.didiprasetyo.domain.repository.RoomRepository
import java.util.*

class RoomRepositoryImpl: RoomRepository {

    override suspend fun getById(id: UUID): RoomEntity? = AppDatabase.dbQuery {
        Rooms.select { Rooms.id eq id }.firstOrNull()?.intoEntity()
    }

    override suspend fun getByTenantId(tenantId: UUID): List<RoomEntity> = AppDatabase.dbQuery {
        Rooms.select { Rooms.idTenant eq tenantId}.map { it.intoEntity() }
    }

    override suspend fun getByBoardingHouseId(boardingHouseId: UUID): List<RoomEntity> = AppDatabase.dbQuery {
        Rooms.select { Rooms.idBoardingHouse eq boardingHouseId }.map { it.intoEntity() }
    }

    override suspend fun insert(item: RoomEntity): Unit = AppDatabase.dbQuery{
        val date: Long = System.currentTimeMillis()/1000
        Rooms.insert {
            it[id] = item.id
            it[idBoardingHouse] = item.idBoardingHouse
            it[name] = item.name
            it[imageUrl] = item.imageUrl
            it[createdAt] = date
            it[updatedAt] = date
        }
    }

    override suspend fun update(item: RoomEntity): Unit = AppDatabase.dbQuery{
        val update = System.currentTimeMillis()/1000
        Rooms.update ({ Rooms.id eq item.id }){
            it[idTenant] = item.idTenant
            it[name] = item.name
            it[imageUrl] = item.imageUrl
            it[dateEntry] = item.dateEntry
            it[dateOut] = item.dateOut
            it[updatedAt] = update
        }
    }

    override suspend fun delete(item: RoomEntity): Unit = AppDatabase.dbQuery {
        Rooms.deleteWhere { Rooms.id eq item.id }
    }

    override fun ResultRow.intoEntity(): RoomEntity {
        return RoomEntity(
            id = this[Rooms.id],
            idBoardingHouse = this[Rooms.idBoardingHouse],
            idTenant = this[Rooms.idTenant],
            name = this[Rooms.name],
            imageUrl = this[Rooms.imageUrl],
            dateEntry = this[Rooms.dateEntry],
            dateOut = this[Rooms.dateOut],
            createdAt = this[Rooms.createdAt],
            updatedAt = this[Rooms.updatedAt],
        )
    }
}