package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.ConfigurationRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.Assertions.within
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class TestServiceTest {

    private val testDefinitionService = mock<TestDefinitionService>()
    private val configurationRepository = mock<ConfigurationRepository>()
    private val wordRepository = mock<WordRepository>()

    @Test
    fun `should create random word selection`() {
        whenever(wordRepository.findAll(any(), eq(true))).thenReturn(generateWordList())
        whenever(configurationRepository.testStartTimeLimit()).thenReturn(Duration.ofMinutes(5))
        whenever(configurationRepository.maxWPM()).thenReturn(10_000) // For better statistical distribution

        val test = getService().createTest(getTestDefinition(), getUser())
        then(test.startTimeLimit).isEqualTo(Duration.ofMinutes(5))

        val selection = test.selectedWords
        then(selection).isNotNull
        then(selection.size()).isEqualTo(10_000)

        val serialized = selection.serialize()
        val lessWordCount = countWordOccurrences(serialized, "less")
        val moreWordCount = countWordOccurrences(serialized, "more")
        val ratio = moreWordCount.toDouble() / lessWordCount

        then(ratio).isCloseTo(3.0, within(0.5))
    }

    @Test
    fun `should regenerate word selection`() {
        whenever(wordRepository.findAll(any(), any())).thenReturn(emptySequence())

        val service = getService()
        val test = service.createTest(getTestDefinition(), getUser())

        val originalSelection = test.selectedWords
        val regenerated = service.regenerateWordList(test)

        then(originalSelection).isNotSameAs(test.selectedWords)
        then(test.wordRegenerationCount).isEqualTo(1)
        then(regenerated).isTrue
    }

    @Test
    fun `should not regenerate word selection for test with fixed words`() {
        val service = getService()
        val selection = WordSelection().apply { loadFromText("fixed text") }
        val testDefinition = getTestDefinition().apply {
            wordList.fixedTextType(selection)
        }

        val test = service.createTest(testDefinition, getUser())
        val regenerated = service.regenerateWordList(test)

        then(test.selectedWords).isEqualTo(selection)
        then(regenerated).isFalse
    }

    private fun getTestDefinition(): TestDefinition = TestDefinition().apply {
        id = 100
        duration = Duration.ofMinutes(1)
        wordList = WordList().apply { id = 50 }
    }

    private fun getUser(): User = User().apply {
        id = 200
    }

    private fun generateWordList(): Sequence<Word> {
        val words = ArrayList<Word>()

        // Generate a high quantity of words with small popularity to decrease amount of "Trial limit has been reached" messages
        for (i in 0 until 500) {
            words.add(Word().apply { popularity = 1; word = i.toString() })
        }

        words.add(Word().apply { popularity = 25; word = "less" })
        words.add(Word().apply { popularity = 75; word = "more" })

        return words.asSequence()
    }

    private fun getService(): TestService = TestService(testDefinitionService, configurationRepository, wordRepository)

    private fun countWordOccurrences(string: String, word: String): Int {
        val lengthBefore = string.length
        val lengthAfter = string.replace(word, "").length

        return (lengthBefore - lengthAfter) / word.length
    }
}