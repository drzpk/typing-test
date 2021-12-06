package dev.drzepka.typing.server.infrastructure.dao

import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.domain.dto.TestResultQueryData
import dev.drzepka.typing.server.infrastructure.repository.table.TestDefinitions
import dev.drzepka.typing.server.infrastructure.repository.table.TestResults
import dev.drzepka.typing.server.infrastructure.repository.table.Tests
import dev.drzepka.typing.server.infrastructure.repository.table.Users
import org.jetbrains.exposed.sql.*
import java.time.Instant

class ExposedTestResultDAO : TestResultDAO {

    override fun findHighestResultsBySpeed(query: TestResultQueryData): List<TestResultDataDTO> {
        return getBaseHighestResultsQuery(query)
            .orderBy(TestResults.wordsPerMinute to SortOrder.DESC)
            .map { rowToTestResultUserDTO(it) }
    }

    override fun findHighestResultsByAccuracy(query: TestResultQueryData): List<TestResultDataDTO> {
        return getBaseHighestResultsQuery(query)
            .orderBy(TestResults.accuracy to SortOrder.DESC)
            .map { rowToTestResultUserDTO(it) }
    }

    private fun getBaseHighestResultsQuery(query: TestResultQueryData): Query {
        val conditions: SqlExpressionBuilder.() -> Op<Boolean> = {
            val condition = TestDefinitions.id eq query.testDefinitionId
            if (query.range != null)
                condition and (Tests.startedAt greaterEq Instant.now().minus(query.range))
            else condition
        }

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
            .select(conditions)
            .limit(query.limit)
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