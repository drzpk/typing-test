package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.application.dto.wordlist.UpdateWordListRequest
import dev.drzepka.typing.server.application.dto.wordlist.WordListTypeDTO
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Mockable
import dev.drzepka.typing.server.domain.value.Language
import dev.drzepka.typing.server.domain.value.WordSelection

/**
 * Manages word lists used in typing tests.
 */
@Mockable
class WordListService(
    private val wordListRepository: WordListRepository,
    private val testDefinitionRepository: TestDefinitionRepository
) {
    private val log by Logger()

    fun createWordList(request: CreateWordListRequest): WordList {
        log.info("Creating word list (name=${request.name}, lang=${request.language}, type=${request.type})")

        if (request.type == WordListTypeDTO.FIXED && request.text == null)
            request.text = ""
        validateCreateWordList(request)

        val wordList = WordList()
        wordList.name = request.name
        wordList.language = Language.fromSymbol(request.language)!!
        if (request.type == WordListTypeDTO.RANDOM)
            wordList.randomTextType()
        else
            wordList.fixedTextType(WordSelection().apply { loadFromText(request.text!!) })

        wordListRepository.save(wordList)

        log.info("Created word list id=${wordList.id}")
        return wordList
    }

    fun updateWordList(request: UpdateWordListRequest): WordList {
        log.info("Updating word list ${request.id}")
        val wordList = getWordList(request.id)
        validateUpdateWordList(request, wordList)

        var changed = false
        if (request.name != null) {
            wordList!!.name = request.name!!
            changed = true
        }

        if (request.text != null) {
            val selection = WordSelection()
            selection.loadFromText(request.text!!)
            wordList!!.fixedTextType(selection)
            changed = true
        }

        if (changed)
            wordListRepository.save(wordList!!)

        return wordList!!
    }

    fun getWordList(id: Int): WordList? {
        return wordListRepository.findById(id)
    }

    fun listWordLists(): Collection<WordList> {
        return wordListRepository.findAll()
    }

    fun deleteWordList(id: Int): Boolean {
        val usages = testDefinitionRepository.findByWordList(id)
        if (usages.isNotEmpty()) {
            log.warn("Cannot delete word list {} because it is used by {} test definition(s)", id, usages.size)
            val testDefinitions = usages.associate { Pair(it.id.toString(), it.name) }
            ErrorCode.WORD_LIST_USED_BY_TEST_DEFINITIONS.throwException(id, testDefinitions)
        }

        log.info("Deleting word list {}", id)
        return wordListRepository.delete(id)
    }

    private fun validateCreateWordList(request: CreateWordListRequest) {
        val errors = ValidationState()
        validateWordListName(request.name, errors)
        validateWordListText(request.text, request.type, errors)

        val language = Language.fromSymbol(request.language)
        if (language == null)
            errors.addFieldError("language", "Language '${request.language}' wasn't found.")

        val found = wordListRepository.findbyName(request.name)
        if (found != null)
            errors.addFieldError("name", "Word list with given name already exists.")

        errors.verify()
    }

    private fun validateUpdateWordList(request: UpdateWordListRequest, wordList: WordList?) {
        val errors = ValidationState()
        if (wordList == null)
            errors.addObjectError("Word list with id ${request.id} wasn't found")

        if (request.name != null)
            validateWordListName(request.name!!, errors)

        if (wordList != null)
            validateWordListText(request.text, WordListTypeDTO.fromValue(wordList.type), errors)

        errors.verify()
    }

    private fun validateWordListName(name: String, errors: ValidationState) {
        if (name.isEmpty() || name.length > 128)
            errors.addFieldError("name", "Name must have between 1 and 128 characters.")
    }

    private fun validateWordListText(text: String?, type: WordListTypeDTO, errors: ValidationState) {
        if (text != null && type != WordListTypeDTO.FIXED)
            errors.addFieldError("text", "Cannot set text of non-FIXED word list type")
        else if (text == null && type == WordListTypeDTO.FIXED)
            errors.addFieldError("text", "No text provided for FIXED word list type")
    }
}