package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.entity.table.WordListsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.Serializable

class WordList(id: EntityID<Int>) : IntEntity(id), Serializable {
    companion object : IntEntityClass<WordList>(WordListsTable)

    var name by WordListsTable.name
    var language by WordListsTable.language
}