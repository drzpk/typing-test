package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.entity.Word

interface WordRepository {
    fun findById(id: Int): Word?
    fun findPaged(wordListId: Int, page: Int, size: Int): Page<Word>
    fun findAll(wordListId: Int, sortDescendingByPopularity: Boolean = false): Collection<Word>
    fun findByWordListAndWord(wordListId: Int, word: String): Word?
    fun save(word: Word)
    fun delete(id: Int): Boolean
}