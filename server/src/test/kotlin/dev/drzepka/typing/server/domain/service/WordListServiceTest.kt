package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.application.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.application.exception.ValidationException
import dev.drzepka.typing.server.application.service.WordListService
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.value.Language
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class WordListServiceTest {

    private val wordListRepository = mock<WordListRepository>()

    @Test
    fun `should create word list`() {
        val request = CreateWordListRequest().apply {
            name = "name"
            language = "pl"
        }

        val created = getService().createWordList(request)

        then(created.name).isEqualTo("name")
        then(created.language).isEqualTo(Language.POLISH)

        val captor = argumentCaptor<WordList>()
        verify(wordListRepository, times(1)).save(captor.capture())
        then(captor.firstValue).isSameAs(created)
    }

    @Test
    fun `should throw validation error on wrong request`() {
        val caught = catchThrowable {
            getService().createWordList(CreateWordListRequest())
        }

        then(caught).isInstanceOf(ValidationException::class.java)

        val validationException = caught as ValidationException
        then(validationException.validationErrors.errors).hasSize(2)
    }

    private fun getService(): WordListService = WordListService(wordListRepository)
}