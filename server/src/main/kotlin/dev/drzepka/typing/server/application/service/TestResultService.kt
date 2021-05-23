package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.testresult.TestResultResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.domain.repository.TestResultRepository

class TestResultService(private val testResultRepository: TestResultRepository) {

    fun getResultForTest(testId: Int): TestResultResource {
        val entity = testResultRepository.findById(testId) ?: ErrorCode.TEST_RESULT_NOT_FOUND.throwError(testId)
        return TestResultResource.fromEntity(entity)
    }
}