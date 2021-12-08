package dev.drzepka.typing.server.application.util

import dev.drzepka.typing.server.application.dto.teststats.TestStatsCalculationResult
import dev.drzepka.typing.server.application.dto.teststats.TimedData
import dev.drzepka.typing.server.domain.entity.TestResult

/**
 * Calculates statistics based on provided test results.
 */
class TestStatsCalculator(initialCapacity: Int, private val calculateTimeData: Boolean) {

    private var takenTests = 0
    private var totalWordsPerMinute = 0f
    private var totalAccuracy = 0f
    private var wordsPerMinuteValues = ArrayList<TimedData<Float>>()
    private var accuracyValues = ArrayList<TimedData<Float>>()

    init {
        if (calculateTimeData) {
            wordsPerMinuteValues.ensureCapacity(initialCapacity)
            accuracyValues.ensureCapacity(initialCapacity)
        }
    }

    fun include(result: TestResult) {
        takenTests++
        totalWordsPerMinute += result.wordsPerMinute
        totalAccuracy += result.accuracy

        if (calculateTimeData) {
            val testTime = result.test.startedAt!!
            wordsPerMinuteValues.add(TimedData(testTime, result.wordsPerMinute))
            accuracyValues.add(TimedData(testTime, result.accuracy))
        }
    }

    fun createResult(): TestStatsCalculationResult {
        return TestStatsCalculationResult(
            takenTests,
            if (takenTests > 0) totalWordsPerMinute / takenTests else 0f,
            if (takenTests > 0) totalAccuracy / takenTests else 0f,
            wordsPerMinuteValues,
            accuracyValues
        )
    }
}