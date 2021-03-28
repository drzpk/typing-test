package dev.drzepka.typing.server.domain.entity.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp

object UsersTable : IntIdTable("users") {
    val email = varchar("email", 256)
    val displayName = varchar("display_name", 64)
    val password = varchar("password", 256)
    val createdAt = timestamp("created_at")
    val activatedAt = timestamp("activated_at").nullable()
}