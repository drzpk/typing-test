package dev.drzepka.typing.server.infrastructure.repository.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp

object Sessions : IntIdTable("sessions") {
    val userId = optReference("user_id", Users.id)
    val createdAt = timestamp("created_at")
    val lastSeen = timestamp("last_seen")
    val ipAddress = varchar("ip_address", 45)
    val userAgent = varchar("user_agent", 256)
}