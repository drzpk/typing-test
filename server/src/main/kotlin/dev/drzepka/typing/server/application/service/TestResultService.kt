package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.testresult.TestBestResultResource
import dev.drzepka.typing.server.application.dto.testresult.TestResultResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.service.TestScoreCalculatorService

class TestResultService(
    private val testResultRepository: TestResultRepository,
    private val testResultsDAO: TestResultDAO,
    private val testScoreCalculatorService: TestScoreCalculatorService
) {

    fun getResultForTest(testId: Int): TestResultResource {
        val entity = testResultRepository.findById(testId) ?: ErrorCode.TEST_RESULT_NOT_FOUND.throwError(testId)
        return TestResultResource.fromEntity(entity)
    }

    fun getBestResults(testDefinitionId: Int): List<TestBestResultResource> {
        // Find best results in speed and accuracy separately
        val bestSpeedResults = testResultsDAO.findHighestResultsBySpeed(testDefinitionId, BEST_RESULTS_SIZE)
        val bestAccuracyResults = testResultsDAO.findHighestResultsByAccuracy(testDefinitionId, BEST_RESULTS_SIZE)
        val combinedDTOs = ArrayList(bestSpeedResults).apply { addAll(bestAccuracyResults) }

        val uniqueResults = HashSet<Int>()
        val resourceList = ArrayList<TestBestResultResource>(BEST_RESULTS_SIZE)

        for (dto in combinedDTOs) {
            if (uniqueResults.contains(dto.testResultId))
                continue

            uniqueResults.add(dto.testResultId)
            resourceList.add(convertToResource(dto))
        }

        return resourceList.sortedByDescending { it.score }.subList(0, minOf(resourceList.size, BEST_RESULTS_SIZE))
    }

    private fun convertToResource(input: TestResultDataDTO): TestBestResultResource {
        return TestBestResultResource(
            input.userDisplayName,
            input.testCreatedAt,
            input.speed,
            input.accuracy,
            testScoreCalculatorService.calculateScore(input.speed, input.accuracy, input.testDuration)
        )
    }

    companion object {
        private const val BEST_RESULTS_SIZE = 10
    }
}