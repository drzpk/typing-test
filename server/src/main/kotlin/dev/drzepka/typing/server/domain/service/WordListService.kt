package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.entity.table.WordListsTable
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Mockable
import dev.drzepka.typing.server.domain.util.ValidationErrors
import dev.drzepka.typing.server.domain.value.Language

@Mockable
class WordListService {

    private val log by Logger()

    fun createWordList(request: CreateWordListRequest): WordList {
        validateCreateWordList(request)

        return WordList.new {
            name = request.name
            language = Language.fromSymbol(request.language)!!
        }
    }

    fun getWordList(id: Int): WordList? {
        return WordList.findById(id)
    }

    fun listWordLists(): Collection<WordList> {
        return WordList.all().toList()
    }

    fun deleteWordList(id: Int): Boolean {
        val found = WordList.findById(id)
        return if (found != null) {
            log.info("Deleting word list {}", found.id)
            found.delete()
            true
        } else {
            false
        }
    }

    private fun validateCreateWordList(request: CreateWordListRequest) {
        val errors = ValidationErrors()
        if (request.name.isEmpty() || request.name.length > 128)
            errors.addFieldError("name", "Name must have between 1 and 128 characters.")

        val language = Language.fromSymbol(request.language)
        if (language == null)
            errors.addFieldError("language", "Language '${request.language}' wasn't found.")

        val count = WordList.find { WordListsTable.name eq request.name }.count()
        if (count > 0)
            errors.addFieldError("name", "Word list with given name already exists.")

        errors.verify()
    }
}