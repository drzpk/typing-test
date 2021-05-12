package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.application.dto.word.AddWordRequest
import dev.drzepka.typing.server.application.exception.ValidationException
import dev.drzepka.typing.server.application.service.WordService
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.value.Language
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class WordServiceTest {

    private val wordListRepository = mock<WordListRepository>()
    private val wordRepository = mock<WordRepository>()

    @Test
    fun `should add word`() {
        val wordList = WordList().apply {
            id = 999
            name = "test"
            language = Language.ENGLISH
        }

        whenever(wordListRepository.findById(999)).thenReturn(wordList)

        val request = AddWordRequest().apply {
            word = "abc"
            popularity = 1
            wordListId = wordList.id!!
        }

        val added = getService().addWord(request)

        val captor = argumentCaptor<Word>()
        verify(wordRepository, times(1)).save(captor.capture())
        then(captor.firstValue).isSameAs(added)

        then(added.word).isEqualTo("abc")
        then(added.popularity).isEqualTo(1)
        then(added.wordListId).isEqualTo(wordList.id)
    }

    @Test
    fun `should throw validation error on wrong word data`() {
        val caught = catchThrowable {
            getService().addWord(AddWordRequest())
        }

        then(caught).isInstanceOf(ValidationException::class.java)
        then((caught as ValidationException).validationErrors.errors).isNotEmpty
    }

    private fun getService(): WordService = WordService(wordListRepository, wordRepository)
}