package dev.drzepka.typing.server.domain.entity.table

import dev.drzepka.typing.server.domain.value.Language
import org.jetbrains.exposed.dao.id.IntIdTable

object WordListsTable : IntIdTable("word_lists") {
    val name = varchar("name", 128)
    val language = customEnumeration("language", "CHAR(2)", { Language.fromSymbol(it.toString())!! }, { it.symbol })
}