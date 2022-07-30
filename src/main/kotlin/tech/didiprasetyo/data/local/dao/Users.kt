package tech.didiprasetyo.data.local.dao

import org.jetbrains.exposed.sql.*

object Users : Table() {
    val id = uuid("id")
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val noTelp = varchar("no_telp", 255).uniqueIndex().nullable()
    val avatarUrl = varchar("avatar_url", 255).nullable()
    val verifiedAt = long("verified_at").nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}