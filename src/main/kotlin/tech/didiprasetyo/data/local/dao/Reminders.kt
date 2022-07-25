package tech.didiprasetyo.data.local.dao

import org.jetbrains.exposed.sql.Table

object Reminders: Table() {
    val id = uuid("id")
    val idBoardingHouse = reference("id_boarding_house", BoardingHouses.id)
    val reminder = varchar("reminder", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(Rules.id)
}