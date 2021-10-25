package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.infrastructure.repository.table.Words
import dev.drzepka.typing.server.infrastructure.util.countWhere
import org.jetbrains.exposed.sql.*

class ExposedWordRepository : WordRepository {

    override fun findById(id: Int): Word? {
        return Words.select { Words.id eq id }
            .singleOrNull()
            ?.let { rowToWord(it) }
    }

    override fun findPaged(wordListId: Int, page: Int, size: Int): Page<Word> {
        val result = Words.select { Words.wordList eq wordListId }
            .limit(size, (page - 1) * size.toLong())
            .map { rowToWord(it) }

        val total = Words.countWhere { Words.wordList eq wordListId }
        return Page(result, page, size, total)
    }

    override fun findAll(wordListId: Int, sortDescendingByPopularity: Boolean): Collection<Word> {
        val query = Words.select { Words.wordList eq wordListId }
        if (sortDescendingByPopularity)
            query.orderBy(Words.popularity to SortOrder.DESC)

        return query.map { rowToWord(it) }
    }

    override fun findByWordListAndWord(wordListId: Int, word: String): Word? {
        val found = Words.select {(Words.wordList eq wordListId) and (Words.word eq word) }
        return found.firstOrNull { it[Words.word] == word } // Exposed framework cannot perform WHERE BINARY search
            ?.let { rowToWord(it) }
    }

    override fun save(word: Word) {
        if (word.isStored()) {
            Words.update({Words.id eq word.id}) {
                it[Words.word] = word.word
                it[popularity] = word.popularity
                it[wordList] = word.wordListId
            }
        } else {
            val id = Words.insertAndGetId {
                it[Words.word] = word.word
                it[popularity] = word.popularity
                it[wordList] = word.wordListId
            }

            word.id = id.value
        }
    }

    override fun delete(id: Int): Boolean {
        val count = Words.deleteWhere { Words.id eq id }
        return count > 0
    }

    private fun rowToWord(row: ResultRow): Word {
        return Word().apply {
            id = row[Words.id].value
            word = row[Words.word]
            popularity = row[Words.popularity]
            wordListId = row[Words.wordList].value
        }
    }
}