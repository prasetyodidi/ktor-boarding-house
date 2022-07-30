package tech.didiprasetyo.data.repository

import org.jetbrains.exposed.sql.*
import tech.didiprasetyo.data.local.AppDatabase
import tech.didiprasetyo.data.local.dao.BoardingHouses
import tech.didiprasetyo.data.local.entity.BoardingHouseEntity
import tech.didiprasetyo.domain.repository.BoardingHouseRepository
import java.util.*

class BoardingHouseRepositoryImpl: BoardingHouseRepository {
    override suspend fun getById(id: UUID): BoardingHouseEntity? = AppDatabase.dbQuery {
        BoardingHouses.select { BoardingHouses.id eq id }.firstOrNull()?.intoEntity()
    }

    override suspend fun insert(item: BoardingHouseEntity): Unit = AppDatabase.dbQuery {
        BoardingHouses.insert {
            it[id] = item.id
            it[idOwner] = item.idOwner
            it[name] = item.name
            it[address] = item.address
            it[mapUrl] = item.mapUrl
            it[imageUrl] = item.imageUrl
            it[createdAt] = item.createdAt
            it[updatedAt] = item.updatedAt
        }
    }

    override suspend fun update(item: BoardingHouseEntity): Unit = AppDatabase.dbQuery {
        val now = System.currentTimeMillis()/1000
        BoardingHouses.update({ BoardingHouses.id eq item.id}) {
            it[name] = item.name
            it[address] = item.address
            it[mapUrl] = item.mapUrl
            it[imageUrl] = item.imageUrl
            it[updatedAt] = now
        }
    }

    override suspend fun delete(item: BoardingHouseEntity):Unit = AppDatabase.dbQuery {
        BoardingHouses.deleteWhere { BoardingHouses.id eq item.id }
    }

    override fun ResultRow.intoEntity(): BoardingHouseEntity {
        return BoardingHouseEntity(
            id = this[BoardingHouses.id],
            idOwner = this[BoardingHouses.idOwner],
            name = this[BoardingHouses.name],
            address = this[BoardingHouses.address],
            mapUrl = this[BoardingHouses.mapUrl],
            imageUrl = this[BoardingHouses.imageUrl],
            createdAt = this[BoardingHouses.createdAt],
            updatedAt = this[BoardingHouses.updatedAt],
        )
    }
}