package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.BulkImportWordsDTO
import dev.drzepka.typing.server.application.dto.ErrorHandlingMode
import dev.drzepka.typing.server.application.dto.word.AddWordRequest
import dev.drzepka.typing.server.application.validation.ValidationState
import dev.drzepka.typing.server.domain.util.Logger

class WordBulkManagementService(private val wordListService: WordListService, private val wordService: WordService) {

    private val log by Logger()
    private val wordSplitPattern = Regex("^(.+)=(\\d+)$")

    fun bulkImportWords(dto: BulkImportWordsDTO) {
        if (!wordListExists(dto.wordListId)) {
            val validation = ValidationState()
            validation.addFieldError("wordListId", "Word list with specified id doesn't exist.")
            validation.verify()
        }

        val wordList = dto.content.replace("\r\n", "\n").split("\n")
        for (rawWord in wordList) {
            importSingleWord(rawWord, dto)
        }
    }

    private fun importSingleWord(rawWord: String, dto: BulkImportWordsDTO) {
        val request = convertToRequest(rawWord, dto.wordListId)
        if (request != null) {
            try {
                wordService.addWord(request)
            } catch (e: Exception) {
                if (dto.errorHandlingMode == ErrorHandlingMode.ABORT)
                    throw e
            }
        } else {
            log.warn("Couldn't parse raw word \"$rawWord\"")
            if (dto.errorHandlingMode == ErrorHandlingMode.ABORT) {
                val validation = ValidationState()
                validation.addFieldError("content", "Couldn't parse line \"$rawWord\".")
                validation.verify()
            }
        }
    }

    private fun wordListExists(wordListId: Int): Boolean {
        return wordListService.getWordList(wordListId) != null
    }

    private fun convertToRequest(source: String, wordListId: Int): AddWordRequest? {
        val result = wordSplitPattern.matchEntire(source) ?: return null
        AddWordRequest()
        return AddWordRequest().apply {
            word = result.groups[1]!!.value
            popularity = result.groups[2]?.value?.toInt()!!
            this.wordListId = wordListId
        }
    }
}