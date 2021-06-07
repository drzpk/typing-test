package dev.drzepka.typing.server.application.util

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.TestResult
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class TestStatsCalculatorTest {

    @Test
    fun `should calculate stats`() {
        val calculator = TestStatsCalculator(3)
        calculator.include(createResult(LocalTime.of(11, 1, 0), 65.4f, 94.2f))
        calculator.include(createResult(LocalTime.of(11, 2, 0), 72.1f, 92.1f))
        calculator.include(createResult(LocalTime.of(11, 3, 0), 52.9f, 89.4f))

        val definition = createTestDefinition()
        val result = calculator.createResult(definition)

        then(result.testDefinition.id).isEqualTo(definition.id)
        then(result.finishedTests).isEqualTo(3)
        then(result.averageWordsPerMinute).isEqualTo(63.47f, Offset.offset(0.1f))
        then(result.averageAccuracy).isEqualTo(91.9f, Offset.offset(0.1f))

        then(result.wordsPerMinuteValues).hasSize(3)
        then(result.wordsPerMinuteValues).extracting("timestamp")
            .containsExactly(
                toInstant(LocalTime.of(11, 1, 0)),
                toInstant(LocalTime.of(11, 2, 0)),
                toInstant(LocalTime.of(11, 3, 0))
            )
        then(result.wordsPerMinuteValues).extracting("value")
            .containsExactly(65.4f, 72.1f, 52.9f)

        then(result.accuracyValues).hasSize(3)
        then(result.accuracyValues).extracting("timestamp")
            .containsExactly(
                toInstant(LocalTime.of(11, 1, 0)),
                toInstant(LocalTime.of(11, 2, 0)),
                toInstant(LocalTime.of(11, 3, 0))
            )
        then(result.accuracyValues).extracting("value")
            .containsExactly(94.2f, 92.1f, 89.4f)
    }

    private fun createResult(time: LocalTime, wpm: Float, accuracy: Float): TestResult {
        val test = dev.drzepka.typing.server.domain.entity.Test(createTestDefinition(), User(), WordSelection()).apply {
            id = 123
            startedAt = toInstant(time)
        }

        val result = TestResult()
        result.test = test
        result.wordsPerMinute = wpm
        result.accuracy = accuracy
        return result
    }

    private fun createTestDefinition(): TestDefinition {
        return TestDefinition().apply {
            id = 123
            wordList = WordList().apply { id = 123 }
        }
    }

    private fun toInstant(time: LocalTime): Instant {
        return time.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()
    }
}