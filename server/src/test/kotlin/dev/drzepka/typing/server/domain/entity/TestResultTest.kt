package dev.drzepka.typing.server.domain.entity

import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class TestResultTest {

    @Test
    fun `should calculate correctness`() {
        val selectedWords = WordSelection().apply { deserialize("these|are|example|words|to|test|accuracy") }
        val enteredWords = WordSelection().apply { deserialize("these|ar|examplee|words|to|TEST|accuracy") }

        val test = createTest(selectedWords, Duration.ofMinutes(1))
        test.enteredWords = enteredWords
        val result = TestResult()
        result.calculateResult(test)

        then(result.correctWords).isEqualTo(4)
        then(result.incorrectWords).isEqualTo(3)
        then(result.correctKeystrokes).isEqualTo(5 + 2 + 7 + 5 + 2 + 8)
        then(result.incorrectKeystrokes).isEqualTo(1 + 1 + 4) // Missing keystrokes are counted as bad ones.
    }

    @Test
    fun `should calculate correctness - short entered words`() {
        val selectedWords = WordSelection().apply { deserialize("word1|word2|word3|word4|word5|word6|word7") }
        val enteredWords = WordSelection().apply { deserialize("word1|word2|woxxx|word4") }

        val test = createTest(selectedWords, Duration.ofMinutes(1))
        test.enteredWords = enteredWords
        val result = TestResult()
        result.calculateResult(test)

        then(result.correctWords).isEqualTo(3)
        then(result.incorrectWords).isEqualTo(1)
        then(result.correctKeystrokes).isEqualTo(5 * 3 + 2)
        then(result.incorrectKeystrokes).isEqualTo(3) // Missing keystrokes are counted as bad ones.
    }

    @Test
    fun `should calculate accuracy - no backspaces`() {
        val selectedWords = WordSelection().apply { deserialize("these|are|example|words|to|test|accuracy") }
        val enteredWords = WordSelection().apply { deserialize("these|ar|examplee|words|to|TEST|accuracy") }

        val test = createTest(selectedWords, Duration.ofMinutes(1))
        test.enteredWords = enteredWords
        val result = TestResult()
        result.calculateResult(test)

        then(result.accuracy).isEqualTo(29f / 35, Offset.offset(0.02f))
    }

    @Test
    fun `should calculate accuracy - with backspaces`() {
        val backspaces = 7

        val selectedWords = WordSelection().apply { deserialize("these|are|example|words|to|test|accuracy") }
        val enteredWords = WordSelection().apply { deserialize("these|are|example|words|to|test|accuXXX") }

        val test = createTest(selectedWords, Duration.ofMinutes(1))
        test.enteredWords = enteredWords
        test.backspaceCount = backspaces
        val result = TestResult()
        result.calculateResult(test)

        then(result.accuracy).isEqualTo(31f / (35 + backspaces * 0.5f), Offset.offset(0.02f))
    }

    @Test
    fun `should calculate speed`() {
        val selectedWords = WordSelection().apply { deserialize("good0|good1|good2|good3|good4|good5|good6|good7|good8|good9|long_good_A|long_good_B|long_good_C") }
        val enteredWords = WordSelection().apply { deserialize("good0|bad|good2|good3|good4|good5|bad|good7|bad|good9|long_good_A|bad|long_good_C") }

        val test = createTest(selectedWords, Duration.ofSeconds(10))
        test.enteredWords = enteredWords
        val result = TestResult()
        result.calculateResult(test)

        // This value is calculated based on keystrokes, not words.
        then(result.wordsPerMinute).isEqualTo(57 / (10 / 60f) / 5, Offset.offset(0.01f))
    }

    private fun createTest(
        selectedWords: WordSelection,
        duration: Duration
    ): dev.drzepka.typing.server.domain.entity.Test {
        val definition = TestDefinition().apply {
            this.duration = duration
            this.wordList = WordList()
        }
        val test = Test(definition, User(), selectedWords)
        test.startedAt = Instant.now()
        test.finishedAt = Instant.now().plus(duration)
        test.backspaceCount = 0
        return test
    }
}