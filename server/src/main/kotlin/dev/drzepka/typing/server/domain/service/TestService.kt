package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.entity.Test
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.Word
import dev.drzepka.typing.server.domain.repository.ConfigurationRepository
import dev.drzepka.typing.server.domain.repository.WordRepository
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.util.Mockable
import dev.drzepka.typing.server.domain.value.WordSelection
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Manages [Test] entities.
 */
@Mockable
class TestService(
    private val configurationRepository: ConfigurationRepository,
    private val wordRepository: WordRepository
) {

    private val log by Logger()
    private val random = Random()

    /**
     * Creates a new instance of given test definition.
     * @param definition test definition from which to create the test instance
     * @param user user that is taking the test
     */
    fun createTest(definition: TestDefinition, user: User): Test {
        log.info("Creating test from definition {}", definition.id)
        val wordSelection = definition.getFixedText() ?: createWordSelection(definition)
        val test = Test(definition, user, wordSelection)
        test.startTimeLimit = configurationRepository.testStartTimeLimit()
        test.finishTimeLimit = configurationRepository.testFinishTimeLimit()

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
        test.selectedWords = createWordSelection(test.testDefinition)
        test.incrementWordRegenerationCounter()
        return true
    }

    private fun createWordSelection(definition: TestDefinition): WordSelection {
        val allWords = wordRepository.findAll(definition.wordList.id!!, true)
        return getRandomWords(definition, allWords)
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
        val minutes = definition.duration.seconds.toFloat() / 60
        return ceil(configurationRepository.maxWPM() * minutes).toInt()
    }

    companion object {
        private const val WORD_TRIAL_LIMIT = 3
    }
}