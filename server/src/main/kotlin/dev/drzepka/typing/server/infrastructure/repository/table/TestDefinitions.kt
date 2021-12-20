package dev.drzepka.typing.server.infrastructure.repository.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.timestamp

object TestDefinitions : IntIdTable("test_definitions") {
    val name = varchar("name", 128)
    val wordList = reference("word_list", WordLists)
    val duration = integer("duration").nullable()
    val isActive = bool("is_active")
    val createdAt = timestamp("created_at")
    val modifiedAt = timestamp("modified_at")
    val deletedAt = timestamp("deleted_at").nullable()
}