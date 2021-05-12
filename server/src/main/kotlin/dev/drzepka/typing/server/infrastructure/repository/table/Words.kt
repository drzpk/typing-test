package dev.drzepka.typing.server.infrastructure.repository.table

import org.jetbrains.exposed.dao.id.IntIdTable

object Words : IntIdTable("words") {
    val word = varchar("word", 64)
    val popularity = integer("popularity")
    val wordList = reference("word_list", WordLists)
}