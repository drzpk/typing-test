package dev.drzepka.typing.server.infrastructure.dao

import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.infrastructure.repository.table.TestDefinitions
import dev.drzepka.typing.server.infrastructure.repository.table.TestResults
import dev.drzepka.typing.server.infrastructure.repository.table.Tests
import dev.drzepka.typing.server.infrastructure.repository.table.Users
import org.jetbrains.exposed.sql.*

class ExposedTestResultDAO : TestResultDAO {

    override fun findHighestResultsBySpeed(testDefinitionId: Int, limit: Int): List<TestResultDataDTO> {
        return getBaseHighestResultsQuery(testDefinitionId, limit)
            .orderBy(TestResults.wordsPerMinute to SortOrder.DESC)
            .map { rowToTestResultUserDTO(it) }
    }

    override fun findHighestResultsByAccuracy(testDefinitionId: Int, limit: Int): List<TestResultDataDTO> {
        return getBaseHighestResultsQuery(testDefinitionId, limit)
            .orderBy(TestResults.accuracy to SortOrder.DESC)
            .map { rowToTestResultUserDTO(it) }
    }

    private fun getBaseHighestResultsQuery(testDefinitionId: Int, limit: Int): Query {
        return TestResults.innerJoin(Tests, { TestResults.id }, { Tests.id })
            .innerJoin(TestDefinitions)
            .innerJoin(Users)
            .slice(
                TestResults.id,
                TestResults.wordsPerMinute,
                TestResults.accuracy,
                Tests.startedAt,
                Tests.finishedAt,
                TestDefinitions.duration,
                Users.displayName
            )
            .select { TestDefinitions.id eq testDefinitionId }
            .limit(limit)
    }

    private fun rowToTestResultUserDTO(row: ResultRow): TestResultDataDTO {
        return TestResultDataDTO(
            row[TestResults.id].value,
            row[Users.displayName],
            row[Tests.startedAt]!!,
            row[Tests.finishedAt]!!,
            row[TestDefinitions.duration],
            row[TestResults.wordsPerMinute],
            row[TestResults.accuracy]
        )
    }
}