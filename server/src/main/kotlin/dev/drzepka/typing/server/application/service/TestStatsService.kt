package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource
import dev.drzepka.typing.server.application.dto.teststats.TestStatsResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.util.TestStatsCalculator
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.util.Logger

/**
 * Manages test statistics.
 */
class TestStatsService(
    private val testDefinitionRepository: TestDefinitionRepository,
    private val testResultRepository: TestResultRepository
) {

    private val log by Logger()

    /**
     * Calculates user stats of given test. Computation is done on the fly.
     * @param user user for which to calculate test stats
     * @param testDefinitionId id of test definition
     * @return calculated test statistics
     */
    fun calculateTestStats(user: User, testDefinitionId: Int): TestStatsResource {
        val definition = getTestDefinition(testDefinitionId)
        val (offset, limit) = getOffsetAndLimit(user.id!!, testDefinitionId)
        val sequence = testResultRepository.find(user.id!!, testDefinitionId, offset, limit)

        val calculator = TestStatsCalculator(limit)
        sequence.forEach { calculator.include(it) }

        return calculator.createResult(definition)
    }

    fun getTestDefinitionsWithStats(user: User): Collection<TestDefinitionResource> {
        return testDefinitionRepository.findAllActiveWithCompletedTests(user.id!!)
            .map { TestDefinitionResource.fromEntity(it) }
    }

    private fun getTestDefinition(testDefinitionId: Int): TestDefinition {
        return testDefinitionRepository.findById(testDefinitionId)
            ?: ErrorCode.TEST_DEFINITION_NOT_FOUND.throwException(testDefinitionId)
    }

    private fun getOffsetAndLimit(userId: Int, testDefinitionId: Int): Pair<Int, Int> {
        val totalTests = testResultRepository.count(userId, testDefinitionId)
        val offset: Int
        val limit: Int

        if (totalTests > STATS_TEST_LIMIT) {
            log.info(
                "There are more than {} tests for user {} and definition {}, limiting calculation to the last {} tests",
                STATS_TEST_LIMIT, userId, testDefinitionId, STATS_TEST_LIMIT
            )

            offset = totalTests - STATS_TEST_LIMIT
            limit = STATS_TEST_LIMIT
        } else {
            offset = 0
            limit = totalTests
        }

        return Pair(offset, limit)
    }

    companion object {
        /**
         * Maximum number of the most recent tests from which stats will be calculated. This limit
         * is enforced for performance reasons.
         */
        private const val STATS_TEST_LIMIT = 1_000
    }
}