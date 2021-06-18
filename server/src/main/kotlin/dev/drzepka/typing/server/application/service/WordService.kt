package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.word.AddWordRequest
import dev.drzepka.typing.server.application.dto.word.ListWordsRequest
import dev.drzepka.typing.server.application.dto.word.PatchWordRequest
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.domain.Page
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.util.Mockable

/**
 * Manages words used in typing tests.
 */
@Mockable
class WordService(private val wordListRepository: WordListRepository, private val wordRepository: WordRepository) {

    fun addWord(request: AddWordRequest): Word {
        validateAddWord(request)

        val word = Word()
        word.word = request.word
        word.popularity = request.popularity
        word.wordListId = request.wordListId
        wordRepository.save(word)

        return word
    }

    fun listWords(request: ListWordsRequest): Page<Word> {
        return wordRepository.findPaged(request.wordListId, request.page, request.size)
    }

    fun updateWord(request: PatchWordRequest): Boolean {
        if (request.popularity < 1)
            ValidationState.throwFieldError("popularity", "Popularity must be a positive integer.")

        val word = wordRepository.findById(request.wordId) ?: return false
        word.popularity = request.popularity
        wordRepository.save(word)

        return true
    }

    fun deleteWord(wordId: Int): Boolean {
        return wordRepository.delete(wordId)
    }

    private fun validateAddWord(request: AddWordRequest) {
        val validation = ValidationState()
        if (wordListRepository.findById(request.wordListId) == null)
            validation.addFieldError("wordListId", "Word list with id ${request.wordListId} wasn't found.")
        if (request.popularity < 1)
            validation.addFieldError("popularity", "Popularity must be a positive integer.")
        if (request.word.isEmpty() || request.word.length > 64)
            validation.addFieldError("word", "Word length must be between 1 and 64 characters.")

        if (request.word.isNotEmpty() && wordRepository.findByWordListAndWord(request.wordListId, request.word) != null)
            validation.addFieldError("word", "Word '${request.word}' already exists for given list.")

        validation.verify()
    }
}