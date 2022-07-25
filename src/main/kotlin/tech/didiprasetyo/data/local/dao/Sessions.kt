package tech.didiprasetyo.data.local.dao

import org.jetbrains.exposed.sql.Table

object Sessions: Table() {
    val id = uuid("id")
    val idUser = uuid("id_user")
    val device = varchar("device", 255)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}