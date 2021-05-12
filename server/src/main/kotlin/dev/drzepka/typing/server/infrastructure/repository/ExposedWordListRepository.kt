package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.infrastructure.repository.table.WordLists
import org.jetbrains.exposed.sql.*

class ExposedWordListRepository : WordListRepository {

    override fun findAll(): Collection<WordList> {
        return WordLists.selectAll().map { rowToWordList(it) }
    }

    override fun findById(id: Int): WordList? {
        return WordLists.select { WordLists.id eq id }
            .singleOrNull()
            ?.let { rowToWordList(it) }
    }

    override fun findbyName(name: String): WordList? {
        return WordLists.select { WordLists.name eq name }
            .singleOrNull()
            ?.let { rowToWordList(it) }
    }

    override fun save(wordList: WordList) {
        if (wordList.isStored()) {
            WordLists.update({ WordLists.id eq wordList.id }) {
                it[name] = wordList.name
                it[language] = wordList.language
            }
        } else {
            val storedId = WordLists.insertAndGetId {
                it[name] = wordList.name
                it[language] = wordList.language
            }

            wordList.id = storedId.value
        }
    }

    override fun delete(id: Int): Boolean {
        val count = WordLists.deleteWhere { WordLists.id eq id }
        return count > 0
    }

    private fun rowToWordList(row: ResultRow): WordList {
        return WordList().apply {
            id = row[WordLists.id].value
            name = row[WordLists.name]
            language = row[WordLists.language]
        }
    }
}