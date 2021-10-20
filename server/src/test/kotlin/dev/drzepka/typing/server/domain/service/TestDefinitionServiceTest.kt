package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.exception.FixedTextTooShortException
import dev.drzepka.typing.server.domain.repository.ConfigurationRepository
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Duration

@ExtendWith(MockitoExtension::class)
internal class TestDefinitionServiceTest {
    private val testCpm = 50

    private val configurationRepository = mock<ConfigurationRepository> {
        on { maxCPM() }.doReturn(testCpm)
    }

    @Test
    fun `should throw exception if fixed text is too short`() {
        val service = getService()

        fun checkException(durationSeconds: Int, text: String?, exceptionExpected: Boolean) {
            val definition = getTestDefinitionWithText(durationSeconds, text)
            val exception = catchThrowable { service.checkIfFixedTextIsLongEnough(definition) }

            if (exceptionExpected)
                then(exception).isInstanceOf(FixedTextTooShortException::class.java)
            else
                then(exception).isNull()
        }

        checkException(60, "0".repeat(testCpm * 2), false) // Text is long enough
        checkException(120, "0".repeat(testCpm), true) // Text is not long enough
        checkException(10, null, false) // Test definition doesn't use fixed text
        checkException(60, "0".repeat(testCpm), false) // Text is long enough (edge case)
        checkException(60, "0".repeat(testCpm - 1), true) // Text is not long enough (edge case)
    }

    private fun getTestDefinitionWithText(durationSeconds: Int, text: String?): TestDefinition {
        val wordList = WordList().apply { id = 1 }
        if (text != null) {
            val selection = WordSelection()
            selection.loadFromText(text)
            wordList.fixedTextType(selection)
        }

        return TestDefinition().apply {
            id = 1
            duration = Duration.ofSeconds(durationSeconds.toLong())
            this.wordList = wordList
        }
    }

    private fun getService(): TestDefinitionService {
        return TestDefinitionService(configurationRepository)
    }
}