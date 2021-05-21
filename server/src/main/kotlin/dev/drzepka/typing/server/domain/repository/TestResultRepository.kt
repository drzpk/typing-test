package dev.drzepka.typing.server.domain.repository

import dev.drzepka.typing.server.domain.entity.TestResult
import dev.drzepka.typing.server.domain.util.Mockable
import java.time.Instant

@Mockable
abstract class TestResultRepository {
    fun save(testResult: TestResult) {
        if (testResult.isStored())
            throw UnsupportedOperationException("Updating test result is not supported")

        doSave(testResult)
    }

    protected abstract fun doSave(testResult: TestResult)
    abstract fun findById(id: Int): TestResult?
    abstract fun findByUserIdAndTimeRange(userId: Int, fromInclusive: Instant, toExclusive: Instant): Sequence<TestResult>
    abstract fun deleteByTestId(testId: Int)
}