package dev.drzepka.typing.server.application.util

import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource
import dev.drzepka.typing.server.application.dto.teststats.TestStatsResource
import dev.drzepka.typing.server.application.dto.teststats.TimedData
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.TestResult

class TestStatsCalculator(initialCapacity: Int) {

    private var takenTests = 0
    private var totalWordsPerMinute = 0f
    private var totalAccuracy = 0f
    private var wordsPerMinuteValues = ArrayList<TimedData<Float>>()
    private var accuracyValues = ArrayList<TimedData<Float>>()

    init {
        wordsPerMinuteValues.ensureCapacity(initialCapacity)
        accuracyValues.ensureCapacity(initialCapacity)
    }

    fun include(result: TestResult) {
        takenTests++
        totalWordsPerMinute += result.wordsPerMinute
        totalAccuracy += result.accuracy

        val testTime = result.test.startedAt!!
        wordsPerMinuteValues.add(TimedData(testTime, result.wordsPerMinute))
        accuracyValues.add(TimedData(testTime, result.accuracy))
    }

    fun createResult(definition: TestDefinition): TestStatsResource {
        return TestStatsResource(
            TestDefinitionResource.fromEntity(definition),
            takenTests,
            totalWordsPerMinute / takenTests,
            totalAccuracy / takenTests,
            wordsPerMinuteValues,
            accuracyValues
        )
    }
}