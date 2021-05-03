package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.AbstractDatabaseTest
import dev.drzepka.typing.server.application.dto.BulkImportWordsDTO
import dev.drzepka.typing.server.application.dto.ErrorHandlingMode
import dev.drzepka.typing.server.domain.dto.word.AddWordRequest
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.entity.table.WordListsTable
import dev.drzepka.typing.server.domain.exception.ValidationException
import dev.drzepka.typing.server.domain.service.WordListService
import dev.drzepka.typing.server.domain.service.WordService
import org.assertj.core.api.BDDAssertions.then
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class WordBulkManagementServiceTest : AbstractDatabaseTest() {

    private val wordListService = mock<WordListService>()
    private val wordService = mock<WordService>()

    @Test
    fun `should bulk-import words`() = transaction {
        val dto = BulkImportWordsDTO(
            123,
            """
                asdf=123
                word=122
            """.trimIndent(),
            ErrorHandlingMode.ABORT
        )

        whenever(wordListService.getWordList(123)).thenReturn(WordList(EntityID(123, WordListsTable)))

        val service = getService()
        assertDoesNotThrow {
            service.bulkImportWords(dto)
        }

        val captor = argumentCaptor<AddWordRequest>()
        verify(wordService, times(2)).addWord(captor.capture())

        val values = captor.allValues
        then(values).hasSize(2)

        then(values[0].wordListId).isEqualTo(123)
        then(values[0].word).isEqualTo("asdf")
        then(values[0].popularity).isEqualTo(123)

        then(values[1].wordListId).isEqualTo(123)
        then(values[1].word).isEqualTo("word")
        then(values[1].popularity).isEqualTo(122)

        Unit
    }

    @Test
    fun `should throw exception on wrong word format`() = transaction {
        val dto = BulkImportWordsDTO(
            123,
            """
                xyz
            """.trimIndent(),
            ErrorHandlingMode.ABORT
        )

        whenever(wordListService.getWordList(123)).thenReturn(WordList(EntityID(123, WordListsTable)))

        val service = getService()
        assertThrows<ValidationException> {
            service.bulkImportWords(dto)
        }
        verify(wordService, times(0)).addWord(any())

        Unit
    }

    private fun getService(): WordBulkManagementService = WordBulkManagementService(wordListService, wordService)
}