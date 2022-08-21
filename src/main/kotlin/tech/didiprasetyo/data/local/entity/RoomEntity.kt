package tech.didiprasetyo.data.local.entity

import tech.didiprasetyo.domain.model.Room
import java.util.UUID

data class RoomEntity(
    val id: UUID,
    val idBoardingHouse: UUID,
    val idTenant: UUID?,
    val name: String,
    val imageUrl: String,
    val dateEntry: Long?,
    val dateOut: Long?,
    val createdAt: Long,
    val updatedAt: Long,
){
    fun intoRoom(): Room {
        return Room(
            id = this.id.toString(),
            name = this.name,
            imageUrl = this.imageUrl,
            dateEntry = this.dateEntry,
            dateOut = this.dateOut
        )
    }
}
