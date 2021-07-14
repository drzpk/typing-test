package dev.drzepka.typing.server.infrastructure.repository.table

import dev.drzepka.typing.server.domain.value.Language
import org.jetbrains.exposed.dao.id.IntIdTable

object WordLists : IntIdTable("word_lists") {
    val name = varchar("name", 128)
    val language = customEnumeration("language", "CHAR(2)", { Language.fromSymbol(it.toString())!! }, { it.symbol })
    val type = char("type", 1)
    var text = text("text").nullable()
}