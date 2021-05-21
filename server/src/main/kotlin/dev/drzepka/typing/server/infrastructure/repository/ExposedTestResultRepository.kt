package dev.drzepka.typing.server.infrastructure.repository

import dev.drzepka.typing.server.domain.entity.TestResult
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.infrastructure.repository.table.TestResults
import dev.drzepka.typing.server.infrastructure.repository.table.Tests
import dev.drzepka.typing.server.infrastructure.repository.table.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import java.time.Instant

class ExposedTestResultRepository(private val testRepository: TestRepository) : TestResultRepository() {

    override fun doSave(testResult: TestResult) {
        val id = TestResults.insertAndGetId {
            testResultToRow(testResult, it)
        }

        testResult.id = id.value
    }

    override fun findById(id: Int): TestResult? {
        return TestResults.select { TestResults.id eq id }
            .singleOrNull()
            ?.let { rowToTestResult(it) }
    }

    override fun findByUserIdAndTimeRange(
        userId: Int,
        fromInclusive: Instant,
        toExclusive: Instant
    ): kotlin.sequences.Sequence<TestResult> {
        return (TestResults innerJoin Tests innerJoin Users)
            .select { (Users.id eq userId) and (Tests.createdAt greaterEq fromInclusive) and (Tests.createdAt less toExclusive) }
            .orderBy(Tests.createdAt to SortOrder.ASC)
            .asSequence()
            .map { rowToTestResult(it) }
    }

    override fun deleteByTestId(testId: Int) {
        TestResults.deleteWhere { TestResults.id eq testId }
    }

    private fun testResultToRow(result: TestResult, stmt: UpdateBuilder<Int>) {
        stmt[TestResults.id] = result.test.id!!
        stmt[TestResults.correctWords] = result.correctWords
        stmt[TestResults.incorrectWords] = result.incorrectWords

        stmt[TestResults.correctKeystrokes] = result.correctKeystrokes
        stmt[TestResults.incorrectKeystrokes] = result.incorrectKeystrokes

        stmt[TestResults.accuracy] = result.accuracy
        stmt[TestResults.wordsPerMinute] = result.wordsPerMinute
    }

    private fun rowToTestResult(row: ResultRow): TestResult {
        return TestResult().apply {
            id = row[TestResults.id].value
            test = testRepository.findById(row[TestResults.id].value)!!

            correctWords = row[TestResults.correctWords]
            incorrectWords = row[TestResults.incorrectWords]

            correctKeystrokes = row[TestResults.correctKeystrokes]
            incorrectKeystrokes = row[TestResults.incorrectKeystrokes]

            accuracy = row[TestResults.accuracy]
            wordsPerMinute = row[TestResults.wordsPerMinute]
        }
    }
}