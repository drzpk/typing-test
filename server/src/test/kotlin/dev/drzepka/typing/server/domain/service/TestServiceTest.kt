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
import org.mockito.kotlin.*
import java.time.Duration

@ExtendWith(MockitoExtension::class)
class TestServiceTest {

    private val testDefinitionService = mock<TestDefinitionService>()
    private val configurationRepository = mock<ConfigurationRepository>()
    private val wordRepository = mock<WordRepository>()

    @Test
    fun `should create test`() {
        val startTimeLimit = Duration.ofMinutes(23)
        val finishTimeLimit = Duration.ofMinutes(38)

        whenever(configurationRepository.testStartTimeLimit()).thenReturn(startTimeLimit)
        whenever(configurationRepository.testFinishTimeLimit()).thenReturn(finishTimeLimit)
        whenever(wordRepository.findAll(any(), eq(true))).thenReturn(generateWordList())

        val definition = getTestDefinition()
        val test = getService().createTest(definition, getUser())

        then(test.startTimeLimit).isEqualTo(startTimeLimit)
        then(test.finishTimeLimit).isEqualTo(finishTimeLimit + definition.duration!!)
    }

    @Test
    fun `should calculate test finish time limit if definition doesn't have the duration set`() {
        whenever(configurationRepository.minWPM()).thenReturn(5)

        val selection = WordSelection()
        selection.loadFromText("word ".repeat(13).trim())
        val wordList = WordList()
        wordList.fixedTextType(selection)

        val test = getService().createTest(getTestDefinition(null, wordList), getUser())

        then(test.finishTimeLimit).isEqualTo(Duration.ofSeconds(156))
        verify(configurationRepository, times(0)).testFinishTimeLimit()
    }

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

        then(ratio).isCloseTo(3.0, within(0.8))
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

    private fun getTestDefinition(): TestDefinition = getTestDefinition(
        Duration.ofMinutes(1),
        WordList().apply { id = 50 }
    )

    private fun getTestDefinition(duration: Duration?, wordList: WordList): TestDefinition =
        TestDefinition().apply {
            id = 100
            this.wordList = wordList
            this.duration = duration
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