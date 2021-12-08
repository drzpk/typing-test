package dev.drzepka.typing.server.application.service

import com.google.common.cache.CacheBuilder
import dev.drzepka.typing.server.application.dto.testdefinition.TestDefinitionResource
import dev.drzepka.typing.server.application.dto.teststats.TestStatsCalculationResult
import dev.drzepka.typing.server.application.dto.teststats.TestStatsResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.util.TestStatsCalculator
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.util.Logger
import java.time.Duration

/**
 * Manages test statistics.
 */
class TestStatsService(
    private val testDefinitionRepository: TestDefinitionRepository,
    private val testResultRepository: TestResultRepository
) {

    private val log by Logger()

    private val globalTestStatsCache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .maximumSize(100)
        .build<Int, TestStatsCalculationResult>()

    /**
     * Calculates user stats of given test. Computation is done on the fly.
     * @param user user for which to calculate test stats
     * @param testDefinitionId id of test definition
     * @return calculated test statistics
     */
    fun calculateTestStats(user: User, testDefinitionId: Int): TestStatsResource {
        val (offset, limit) = getOffsetAndLimit(user.id!!, testDefinitionId, STATS_USER_TEST_LIMIT)
        val sequence = testResultRepository.find(user.id!!, testDefinitionId, offset, limit)

        val calculator = TestStatsCalculator(limit, true)
        sequence.forEach { calculator.include(it) }

        val userResult = calculator.createResult()
        return createResponse(testDefinitionId, userResult)
    }

    fun getTestDefinitionsWithStats(user: User): Collection<TestDefinitionResource> {
        return testDefinitionRepository.findAllActiveWithCompletedTests(user.id!!)
            .map { TestDefinitionResource.fromEntity(it) }
    }

    private fun createResponse(testDefinitionId: Int, userResult: TestStatsCalculationResult): TestStatsResource {
        val definition = getTestDefinition(testDefinitionId)
        val globalResult = calculateGlobalTestStats(testDefinitionId)

        val userStats = TestStatsResource.StatsGroup(userResult.averageSpeed, userResult.averageAccuracy)
        val globalStats = TestStatsResource.StatsGroup(globalResult.averageSpeed, globalResult.averageAccuracy)

        return TestStatsResource(
            TestDefinitionResource.fromEntity(definition),
            userResult.takenTests,
            userStats,
            globalStats,
            userResult.speedValues,
            userResult.accuracyValues
        )
    }

    private fun calculateGlobalTestStats(testDefinitionId: Int): TestStatsCalculationResult {
        val cacheResult = globalTestStatsCache.getIfPresent(testDefinitionId)
        if (cacheResult != null) {
            log.debug("Found cache hit of global test stats for definition {}", testDefinitionId)
            return cacheResult
        }

        val (offset, limit) = getOffsetAndLimit(null, testDefinitionId, STATS_GLOBAL_TEST_LIMIT)
        val sequence = testResultRepository.find(null, testDefinitionId, offset, limit)

        val calculator = TestStatsCalculator(STATS_GLOBAL_TEST_LIMIT, false)
        sequence.forEach { calculator.include(it) }

        val result = calculator.createResult()
        globalTestStatsCache.put(testDefinitionId, result)
        return result
    }

    private fun getTestDefinition(testDefinitionId: Int): TestDefinition {
        return testDefinitionRepository.findById(testDefinitionId)
            ?: ErrorCode.TEST_DEFINITION_NOT_FOUND.throwException(testDefinitionId)
    }

    private fun getOffsetAndLimit(userId: Int?, testDefinitionId: Int, getLimit: Int): Pair<Int, Int> {
        val totalTests = testResultRepository.count(userId, testDefinitionId)
        val offset: Int
        val limit: Int

        if (totalTests > getLimit) {
            log.info(
                "There are more than {} tests for user {} and definition {}, limiting calculation to the last {} tests",
                getLimit, userId, testDefinitionId, getLimit
            )

            offset = totalTests - getLimit
            limit = getLimit
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
        private const val STATS_USER_TEST_LIMIT = 100
        private const val STATS_GLOBAL_TEST_LIMIT = 1_000
    }
}