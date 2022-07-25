package tech.didiprasetyo.data.local.dao

import org.jetbrains.exposed.sql.Table

object Rooms: Table() {
    val id = uuid("id")
    val idBoardingHouse = reference("id_boarding_house", BoardingHouses.id)
    val idTenant = reference("id_tenant", Users.id).nullable()
    val name = varchar("name", 255)
    val imageUrl = varchar("image_url", 255)
    val dateEntry = long("date_entry").nullable()
    val dateOut = long("date_out").nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}