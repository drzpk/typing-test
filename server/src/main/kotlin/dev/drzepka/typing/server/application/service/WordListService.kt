package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Mockable
import dev.drzepka.typing.server.domain.value.Language

/**
 * Manages word lists used in typing tests.
 */
@Mockable
class WordListService(private val wordListRepository: WordListRepository) {
    private val log by Logger()

    fun createWordList(request: CreateWordListRequest): WordList {
        validateCreateWordList(request)

        val wordList = WordList()
        wordList.name = request.name
        wordList.language = Language.fromSymbol(request.language)!!
        wordListRepository.save(wordList)

        return wordList
    }

    fun getWordList(id: Int): WordList? {
        return wordListRepository.findById(id)
    }

    fun listWordLists(): Collection<WordList> {
        return wordListRepository.findAll()
    }

    fun deleteWordList(id: Int): Boolean {
        log.info("Deleting word list {}", id)
        return wordListRepository.delete(id)
    }

    private fun validateCreateWordList(request: CreateWordListRequest) {
        val errors = ValidationState()
        if (request.name.isEmpty() || request.name.length > 128)
            errors.addFieldError("name", "Name must have between 1 and 128 characters.")

        val language = Language.fromSymbol(request.language)
        if (language == null)
            errors.addFieldError("language", "Language '${request.language}' wasn't found.")

        val found = wordListRepository.findbyName(request.name)
        if (found != null)
            errors.addFieldError("name", "Word list with given name already exists.")

        errors.verify()
    }
}