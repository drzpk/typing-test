package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.entity.WordList

interface WordListRepository {
    fun findAll(): Collection<WordList>
    fun findById(id: Int): WordList?
    fun findbyName(name: String): WordList?
    fun save(wordList: WordList)
    fun delete(id: Int): Boolean
}