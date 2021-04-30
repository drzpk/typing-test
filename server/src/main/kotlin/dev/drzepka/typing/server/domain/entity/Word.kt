package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.entity.table.WordsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.Serializable

class Word(id: EntityID<Int>) : IntEntity(id), Serializable {
    companion object : IntEntityClass<Word>(WordsTable)

    var word by WordsTable.word
    var popularity by WordsTable.popularity
    var wordList by WordList referencedOn WordsTable.wordList
}