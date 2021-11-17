package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.testresult.TestBestResultResource
import dev.drzepka.typing.server.application.dto.testresult.TestResultResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.service.TestScoreCalculatorService
import java.time.Duration

class TestResultService(
    private val testResultRepository: TestResultRepository,
    private val testResultsDAO: TestResultDAO,
    private val testScoreCalculatorService: TestScoreCalculatorService
) {

    fun getResultForTest(testId: Int): TestResultResource {
        val entity = testResultRepository.findById(testId) ?: ErrorCode.TEST_RESULT_NOT_FOUND.throwException(testId)
        return TestResultResource.fromEntity(entity)
    }

    /**
     * Returns a list of best results for given test definition. The list
     * is ordered by a total score in decreasing order.
     */
    fun getBestResults(testDefinitionId: Int): List<TestBestResultResource> {
        // Find best results in speed and accuracy separately
        val bestSpeedResults = testResultsDAO.findHighestResultsBySpeed(testDefinitionId, BEST_RESULTS_SIZE)
        val bestAccuracyResults = testResultsDAO.findHighestResultsByAccuracy(testDefinitionId, BEST_RESULTS_SIZE)
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