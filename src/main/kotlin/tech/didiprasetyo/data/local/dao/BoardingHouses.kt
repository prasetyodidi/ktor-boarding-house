package tech.didiprasetyo.data.local.dao

import org.jetbrains.exposed.sql.Table

object BoardingHouses: Table(name = "boarding_houses") {
    val id = uuid("id")
    val idOwner = reference("id_owner", Users.id)
    val name = varchar("name", 255)
    val address = varchar("address", 255)
    val mapUrl = varchar("map_url", 255)
    val imageUrl = varchar("image_url", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}