package tech.didiprasetyo.data.local.dao

import org.jetbrains.exposed.sql.Table

object Rules: Table() {
    val id = uuid("id")
    val idBoardingHouse = reference("id_boarding_house", BoardingHouses.id)
    val rule = varchar("rule", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}