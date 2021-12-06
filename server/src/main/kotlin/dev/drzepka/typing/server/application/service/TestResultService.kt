package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.testresult.TestBestResultResource
import dev.drzepka.typing.server.application.dto.testresult.TestResultRange
import dev.drzepka.typing.server.application.dto.testresult.TestResultResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.domain.dto.TestResultQueryData
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.service.TestScoreCalculatorService
import java.time.Duration
import java.time.LocalTime
import java.time.Period
import java.time.temporal.TemporalAmount

class TestResultService(
    private val testResultRepository: TestResultRepository,
    private val testRepository: TestRepository,
    private val testResultsDAO: TestResultDAO,
    private val testScoreCalculatorService: TestScoreCalculatorService
) {

    fun getResultForTest(testId: Int): TestResultResource {
        val testResult = testResultRepository.findById(testId) ?: ErrorCode.TEST_RESULT_NOT_FOUND.throwException(testId)
        val test = testRepository.findById(testId) ?: ErrorCode.TEST_RESULT_NOT_FOUND.throwException(testId)
        return TestResultResource.fromEntity(testResult, test)
    }

    /**
     * Returns a list of best results for given test definition. The list
     * is ordered by a total score in decreasing order.
     */
    fun getBestResults(testDefinitionId: Int, range: TestResultRange?): List<TestBestResultResource> {
        val query = TestResultQueryData(testDefinitionId, convertRangeToTemporalAmount(range), BEST_RESULTS_SIZE)

        // Find best results in speed and accuracy separately
        val bestSpeedResults = testResultsDAO.findHighestResultsBySpeed(query)
        val bestAccuracyResults = testResultsDAO.findHighestResultsByAccuracy(query)
        val combinedDTOs = ArrayList(bestSpeedResults).apply { addAll(bestAccuracyResults) }

        val uniqueResults = HashSet<Int>()
        val resourceList = ArrayList<TestBestResultResource>(BEST_RESULTS_SIZE)

        // todo: is this correct?
        for (dto in combinedDTOs) {
            if (uniqueResults.contains(dto.testResultId))
                continue

            uniqueResults.add(dto.testResultId)
            resourceList.add(convertToResource(dto))
        }

        return resourceList.sortedByDescending { it.score }.subList(0, minOf(resourceList.size, BEST_RESULTS_SIZE))
    }

    private fun convertRangeToTemporalAmount(range: TestResultRange?): TemporalAmount? {
        return when(range ?: TestResultRange.TODAY) {
            TestResultRange.TODAY -> Duration.between(LocalTime.MIN, LocalTime.now())
            TestResultRange.LAST_WEEK -> Period.ofWeeks(1)
            TestResultRange.LAST_MONTH -> Period.ofDays(30)
            TestResultRange.ALL_TIME -> null
        }
    }

    private fun convertToResource(input: TestResultDataDTO): TestBestResultResource {
        val duration = getTestDurationSeconds(input)
        return TestBestResultResource(
            input.userDisplayName,
            input.testStartedAt,
            input.speed,
            input.accuracy,
            duration,
            testScoreCalculatorService.calculateScore(input.speed, input.accuracy, duration)
        )
    }

    private fun getTestDurationSeconds(input: TestResultDataDTO): Int {
        return input.testDuration
            ?: (Duration.between(input.testStartedAt, input.testFinishedAt)).seconds.toInt()
    }

    companion object {
        private const val BEST_RESULTS_SIZE = 10
    }
}