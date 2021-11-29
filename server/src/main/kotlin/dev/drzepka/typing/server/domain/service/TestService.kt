package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.entity.Test
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.ConfigurationRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Mockable
import dev.drzepka.typing.server.domain.value.UserIdentity
import dev.drzepka.typing.server.domain.value.WordSelection
import java.time.Duration
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Manages [Test] entities.
 */
@Mockable
class TestService(
    private val testDefinitionService: TestDefinitionService,
    private val configurationRepository: ConfigurationRepository,
    private val wordRepository: WordRepository
) {

    private val log by Logger()
    private val random = Random()

    /**
     * Creates a new instance of given test definition.
     * @param definition test definition from which to create the test instance
     * @param creator [UserIdentity] that is taking the test
     */
    fun createTest(definition: TestDefinition, creator: UserIdentity): Test {
        log.info("Creating test from definition {}", definition.id)
        val wordSelection = getWordSelection(definition)
        val test = Test(definition, creator, wordSelection)
        test.startTimeLimit = configurationRepository.testStartTimeLimit()
        test.finishTimeLimit = getFinishTimeLimit(definition)

        return test
    }

    /**
     * Generates new word list for the given test.
     */
    fun regenerateWordList(test: Test): Boolean {
        if (!test.canGenerateWords()) {
            log.warn("Cannot regenerate word list of test {}", test.id)
            return false
        }

        log.info("Regenerating word list of test {}", test.id)
        test.selectedWords = createRandomWordSelection(test.testDefinition)
        test.incrementWordRegenerationCounter()
        return true
    }

    private fun getWordSelection(definition: TestDefinition): WordSelection {
        val fixedText = definition.getFixedText()
        if (fixedText != null) {
            testDefinitionService.checkIfFixedTextIsLongEnough(definition)
            return fixedText
        }

        return createRandomWordSelection(definition)
    }

    private fun createRandomWordSelection(definition: TestDefinition): WordSelection {
        val allWords = wordRepository.findAll(definition.wordList.id!!, true)
        return getRandomWords(definition, allWords.toList())
    }

    /**
     * Picks random words from given collection. Each word has it's own popularity, so more popular words
     * have proportionally higher probability to be picked.
     */
    private fun getRandomWords(definition: TestDefinition, orderedWords: Collection<Word>): WordSelection {
        val map = TreeMap<Long, String>()
        var totalPopularity = 0L
        orderedWords.forEach {
            totalPopularity += it.popularity
            map[totalPopularity] = it.word
        }

        val wordCount = getWordCountToDraw(definition)
        val selection = WordSelection()

        var lastWord: String? = null
        for (i in 0 until wordCount) {
            var currentWordTrial = 0

            do {
                val randomPopularity = floor(totalPopularity * random.nextDouble()).toLong()
                val word = map.higherEntry(randomPopularity).value

                val trialLimitReached = currentWordTrial + 1 == WORD_TRIAL_LIMIT

                if (word != lastWord || trialLimitReached) {
                    if (word == lastWord && trialLimitReached)
                        log.warn(
                            "Trial limit has been reached for test definition {} with word '{}'",
                            definition.id,
                            word
                        )

                    selection.addWord(word)
                    lastWord = word
                    break
                }

                currentWordTrial++
            } while (currentWordTrial < WORD_TRIAL_LIMIT)
        }

        return selection
    }

    /**
     * Returns a number of words to randomly select from the set so that their number is
     * greater than user can possibly type within the test duration.
     */
    private fun getWordCountToDraw(definition: TestDefinition): Int {
        val duration = definition.duration
            ?: throw IllegalArgumentException("Duration is null in test definition ${definition.id}")
        val minutes = duration.seconds.toFloat() / 60
        return ceil(configurationRepository.maxWPM() * minutes).toInt()
    }

    private fun getFinishTimeLimit(definition: TestDefinition): Duration {
        return if (definition.duration != null)
            definition.duration!! + configurationRepository.testFinishTimeLimit()
        else
            calculateDynamicTestFinishLimit(definition)
    }

    private fun calculateDynamicTestFinishLimit(definition: TestDefinition): Duration {
        val text = definition.wordList.text!!
        val maxTestMinutes = text.size().toFloat() / configurationRepository.minWPM()
        val maxTestSeconds = ceil(maxTestMinutes * 60).toLong()

        return Duration.ofSeconds(maxTestSeconds)
    }

    companion object {
        private const val WORD_TRIAL_LIMIT = 3
    }
}