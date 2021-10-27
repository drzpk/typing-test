@file:Suppress("BooleanLiteralArgument")

package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.word.ImportWordsRequest
import dev.drzepka.typing.server.application.dto.word.WordDTO
import dev.drzepka.typing.server.application.exception.ValidationException
import dev.drzepka.typing.server.application.validation.FieldError
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.WordRepository
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class WordImportServiceTest {

    private val wordListService = mock<WordListService>()
    private val wordRepository = mock<WordRepository>()

    @Test
    fun `should import new words`() {
        val words = listOf(
            WordDTO("first", 10),
            WordDTO("second", 20)
        )
        val request = ImportWordsRequest(1, false, false, words)

        val wordList = WordList().apply { id = 1 }
        whenever(wordListService.getWordList(eq(1))).thenReturn(wordList)

        whenever(wordRepository.findAll(eq(1), any())).thenReturn(emptySequence())

        getService().importWords(request)

        val captor = argumentCaptor<Word>()
        verify(wordRepository, times(2)).save(captor.capture())

        val first = captor.firstValue
        then(first.word).isEqualTo("first")
        then(first.popularity).isEqualTo(10)
        then(first.wordListId).isEqualTo(1)

        val second = captor.secondValue
        then(second.word).isEqualTo("second")
        then(second.popularity).isEqualTo(20)
        then(second.wordListId).isEqualTo(1)
    }

    @Test
    fun `should update existing words`() {
        val newWords = listOf(
            WordDTO("existing", 30),
            WordDTO("new", 40)
        )
        val request = ImportWordsRequest(1, false, true, newWords)

        val wordList = WordList().apply { id = 1 }
        whenever(wordListService.getWordList(eq(1))).thenReturn(wordList)

        val existingWords = listOf(Word().apply { id = 88; word = "existing"; popularity = 20; wordListId = 1 })
        whenever(wordRepository.findAll(eq(1), any())).thenReturn(existingWords.asSequence())

        getService().importWords(request)

        val captor = argumentCaptor<Word>()
        verify(wordRepository, times(2)).save(captor.capture())

        val first = captor.firstValue
        then(first.id).isEqualTo(88)
        then(first.popularity).isEqualTo(30)

        val second = captor.secondValue
        then(second.word).isEqualTo("new")
    }

    @Test
    fun `should not update existing words`() {
        val newWords = listOf(WordDTO("existing", 40))
        val request = ImportWordsRequest(1, false, false, newWords)

        val wordList = WordList().apply { id = 1 }
        whenever(wordListService.getWordList(eq(1))).thenReturn(wordList)

        val existingWords = listOf(Word().apply { id = 1; word = "existing"; popularity = 30; wordListId = 1 })
        whenever(wordRepository.findAll(eq(1), any())).thenReturn(existingWords.asSequence())

        getService().importWords(request)

        verify(wordRepository, times(0)).save(any())
    }

    @Test
    fun `should delete existing words before importing`() {
        val newWords = listOf(WordDTO("new", 10))
        val request = ImportWordsRequest(1, true, false, newWords)

        val wordList = WordList().apply { id = 1 }
        whenever(wordListService.getWordList(eq(1))).thenReturn(wordList)

        getService().importWords(request)

        verify(wordRepository).deleteByWordList(eq(1))
    }

    @Test
    fun `should validate request`() {
        val words = listOf(
            WordDTO("x".repeat(100), 10),
            WordDTO("test", -10)
        )
        val request = ImportWordsRequest(1, false, false, words)

        val exception = catchThrowable { getService().importWords(request) }
        then(exception).isInstanceOf(ValidationException::class.java)
        exception as ValidationException

        then(exception.validationErrors.errors).hasSize(3)

        val first = exception.validationErrors.errors[0]
        then(first).isInstanceOf(FieldError::class.java)
        then(first.message).isEqualTo("Word list 1 doesn't exist.")

        val second = exception.validationErrors.errors[1]
        then(second).isInstanceOf(FieldError::class.java)
        then((second as FieldError).field).isEqualTo("words[0].word")
        then(second.message).isEqualTo("Word length must be between 1 and 64 characters.")

        val third = exception.validationErrors.errors[2]
        then(third).isInstanceOf(FieldError::class.java)
        then((third as FieldError).field).isEqualTo("words[1].popularity")
        then(third.message).isEqualTo("Popularity must be a positive integer.")
    }

    private fun getService(): WordImportService = WordImportService(wordListService, wordRepository)
}