package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.application.dto.wordlist.CreateWordListRequest
import dev.drzepka.typing.server.application.dto.wordlist.UpdateWordListRequest
import dev.drzepka.typing.server.application.dto.wordlist.WordListTypeDTO
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.exception.ErrorCodeException
import dev.drzepka.typing.server.application.exception.ValidationException
import dev.drzepka.typing.server.application.service.WordListService
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import dev.drzepka.typing.server.domain.value.Language
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class WordListServiceTest {

    private val wordListRepository = mock<WordListRepository>()
    private val testDefinitionRepository = mock<TestDefinitionRepository>()

    @Test
    fun `should create word list with random words`() {
        val request = CreateWordListRequest().apply {
            name = "name"
            language = "pl"
            type = WordListTypeDTO.RANDOM
        }

        val created = getService().createWordList(request)

        then(created.name).isEqualTo("name")
        then(created.language).isEqualTo(Language.POLISH)
        then(created.text).isNull()

        val captor = argumentCaptor<WordList>()
        verify(wordListRepository, times(1)).save(captor.capture())
        then(captor.firstValue).isSameAs(created)
    }

    @Test
    fun `should create word list with fixed text`() {
        val request = CreateWordListRequest().apply {
            name = "name"
            language = "en"
            type = WordListTypeDTO.FIXED
            text = "an example text"
        }

        val created = getService().createWordList(request)

        then(created.name).isEqualTo("name")
        then(created.language).isEqualTo(Language.ENGLISH)

        val expectedSelection = WordSelection().apply { loadFromText("an example text") }
        then(created.text).isEqualTo(expectedSelection)
    }

    @Test
    fun `should throw validation error on wrong create request`() {
        val caught = catchThrowable {
            getService().createWordList(CreateWordListRequest())
        }

        then(caught).isInstanceOf(ValidationException::class.java)

        val validationException = caught as ValidationException
        then(validationException.validationErrors.errors).hasSize(2)
    }

    @Test
    fun `should update word list's name`() {
        val entity = WordList()
        entity.name = "old name"

        val request = UpdateWordListRequest()
        request.id = 123
        request.name = "new name"

        whenever(wordListRepository.findById(eq(request.id))).thenReturn(entity)

        getService().updateWordList(request)

        then(entity.name).isEqualTo(request.name)
    }

    @Test
    fun `should update word list's text`() {
        val oldSelection = WordSelection()
        oldSelection.loadFromText("old static text")
        val entity = WordList()
        entity.name = "old name"
        entity.fixedTextType(oldSelection)

        val request = UpdateWordListRequest()
        request.id = 349
        request.text = "new text"

        whenever(wordListRepository.findById(eq(request.id))).thenReturn(entity)

        getService().updateWordList(request)

        val newSelection = WordSelection()
        newSelection.loadFromText("new text")
        then(entity.text).isEqualTo(newSelection)
        then(entity.name).isEqualTo("old name")
    }

    @Test
    fun `should validate word list update request`() {
        val entity = WordList()
        entity.randomTextType()

        val request = UpdateWordListRequest()
        request.id = 321
        request.name = "xyz".repeat(100)
        request.text = "some text"

        whenever(wordListRepository.findById(eq(request.id))).thenReturn(entity)

        val exception = catchThrowable { getService().updateWordList(request) }
        then(exception).isInstanceOf(ValidationException::class.java)

        exception as ValidationException
        then(exception.validationErrors.errors[0].message).contains("Name must have between 1 and 128 characters")
        then(exception.validationErrors.errors[1].message).contains("Cannot set text of non-FIXED word list type")
    }

    @Test
    fun `should delete word list`() {
        val wordListId = 382

        whenever(wordListRepository.delete(eq(wordListId))).thenReturn(true)

        val status = getService().deleteWordList(wordListId)
        then(status).isTrue
    }

    @Test
    fun `should not delete word list used by test definitions`() {
        val wordListId = 11

        val firstTestDefinition = TestDefinition().apply { id = 1; name = "first" }
        val secondTestDefinition = TestDefinition().apply { id = 2; name = "second" }
        whenever(testDefinitionRepository.findByWordList(eq(wordListId)))
            .thenReturn(listOf(firstTestDefinition, secondTestDefinition))

        val exception = catchThrowable { getService().deleteWordList(wordListId) }
        then(exception).isInstanceOf(ErrorCodeException::class.java)

        exception as ErrorCodeException
        then(exception.code).isEqualTo(ErrorCode.WORD_LIST_USED_BY_TEST_DEFINITIONS)
        then(exception.`object`).isEqualTo(wordListId)

        val expectedAdditionalData = mapOf(Pair("1", "first"), Pair("2", "second"))
        then(exception.additionalData).isEqualTo(expectedAdditionalData)
    }

    private fun getService(): WordListService = WordListService(wordListRepository, testDefinitionRepository)
}