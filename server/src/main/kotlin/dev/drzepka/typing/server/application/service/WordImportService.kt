package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.word.ImportWordsRequest
import dev.drzepka.typing.server.application.dto.word.WordDTO
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.value.WordListType

class WordImportService(
    private val wordListService: WordListService,
    private val wordRepository: WordRepository
) {

    private val log by Logger()

    fun importWords(request: ImportWordsRequest) {
        log.info(
            "Importing {} words for word list {} (deleteExisting = {}, updateExisting = {})",
            request.words.size, request.wordListId, request.deleteExisting, request.updateExisting
        )

        validate(request)

        val existingWords = if (request.deleteExisting) {
            val deleteCount = wordRepository.deleteByWordList(request.wordListId)
            log.info("Deleted {} existing words", deleteCount)
            HashMap()
        } else {
            getExistingWords(request.wordListId)
        }

        var imported = 0
        request.words.forEach {
            importWord(it, request.wordListId, existingWords, request.updateExisting)
            imported++
        }

        log.info("The import has been finished. Imported {} words", imported)
    }

    private fun validate(request: ImportWordsRequest) {
        val validation = ValidationState()
        validateWordList(request.wordListId, validation)
        validateWords(request.words, validation, 50)

        validation.verify()
    }

    private fun validateWordList(wordListId: Int, validation: ValidationState) {
        val wordList = wordListService.getWordList(wordListId)
        if (wordList == null)
            validation.addFieldError("wordListId", "Word list $wordListId doesn't exist.")
        else if (wordList.type != WordListType.RANDOM)
            validation.addFieldError("wordListId", "Word list $wordList must be of RANDOM type")
    }

    private fun validateWords(
        words: Collection<WordDTO>,
        validation: ValidationState,
        @Suppress("SameParameterValue") errorLimit: Int
    ) {
        words.forEachIndexed { index, word ->
            validateWord(word, index, validation)
            if (validation.errors.size >= errorLimit) {
                log.warn("Validation error limit ({}) has been exceeded", errorLimit)
                return
            }
        }
    }

    private fun validateWord(word: WordDTO, index: Int, validation: ValidationState) {
        if (word.popularity < 1)
            validation.addFieldError("words[$index].popularity", "Popularity must be a positive integer.")
        if (word.word.isEmpty() || word.word.length > 64)
            validation.addFieldError("words[$index].word", "Word length must be between 1 and 64 characters.")
    }

    private fun getExistingWords(wordListId: Int): MutableMap<String, Word> {
        val list = wordRepository.findAll(wordListId)
        return list.associateBy { it.word } as MutableMap<String, Word>
    }

    private fun importWord(
        word: WordDTO,
        wordListId: Int,
        existingWords: MutableMap<String, Word>,
        updateExisting: Boolean
    ): Boolean {
        val exists = existingWords.contains(word.word)
        if (exists && !updateExisting)
            return false

        val entity = if (exists) existingWords[word.word]!! else Word()
        entity.word = word.word
        entity.popularity = word.popularity
        entity.wordListId = wordListId

        wordRepository.save(entity)
        return true
    }

}