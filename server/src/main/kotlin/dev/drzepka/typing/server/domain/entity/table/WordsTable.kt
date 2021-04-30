package dev.drzepka.typing.server.domain.entity.table

import org.jetbrains.exposed.dao.id.IntIdTable

object WordsTable : IntIdTable("words") {
    val word = varchar("word", 64)
    val popularity = integer("popularity")
    val wordList = reference("word_list", WordListsTable)
}