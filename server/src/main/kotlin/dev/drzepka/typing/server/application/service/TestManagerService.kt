package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.test.CreateTestRequest
import dev.drzepka.typing.server.application.dto.test.FinishTestRequest
import dev.drzepka.typing.server.application.dto.test.TestResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.exception.SecurityException
import dev.drzepka.typing.server.domain.entity.Test
import dev.drzepka.typing.server.domain.entity.TestResult
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.service.TestService
import dev.drzepka.typing.server.domain.util.Logger
import dev.drzepka.typing.server.domain.value.TestState
import dev.drzepka.typing.server.domain.value.WordSelection
import java.time.Instant

/**
 * Manages test instances.
 */
class TestManagerService(
    private val testDefinitionRepository: TestDefinitionRepository,
    private val testRepository: TestRepository,
    private val testResultRepository: TestResultRepository,
    private val testService: TestService
) {

    private val log by Logger()

    /**
     * Creates a test from its definition.
     */
    fun createTest(request: CreateTestRequest, creator: User): TestResource {
        log.info("Creating test from definition {} with user {}", request.testDefinitionId, creator.id)

        val reusedTests =
            testRepository.findNotStartedByUserIdAndTestDefinitionId(creator.id!!, request.testDefinitionId)
        val firstToReuse = reusedTests.firstOrNull { it.state == TestState.CREATED }

        val test = if (firstToReuse != null) {
            log.info("Found test {} to reuse", firstToReuse.id)
            reuseExistingTest(firstToReuse)
            firstToReuse
        } else {
            createNewTest(request, creator)
        }

        return TestResource.fromEntity(test)
    }

    fun getTest(id: Int, user: User): TestResource {
        val test = doGetTest(id)
        checkPermissionsToTest(user, test)
        return TestResource.fromEntity(test)
    }

    fun deleteTest(id: Int, user: User) {
        log.info("Deleting test {}", id)

        val test = doGetTest(id)
        checkPermissionsToTest(user, test)

        if (test.state == TestState.FINISHED)
            ErrorCode.CANNOT_DELETE_FINISHED_TEST.throwException(id)

        testRepository.delete(id)
    }

    /**
     * Starts a test or returns an error if start time limit has been reached.
     */
    fun startTest(id: Int, user: User): TestResource {
        log.info("Starting test {}", id)
        val test = doGetTest(id)
        checkPermissionsToTest(user, test)

        if (test.state == TestState.CREATED_TIMEOUT)
            ErrorCode.TEST_START_TIMEOUT.throwException(id, mapOf("timeout" to test.createdAt.plus(test.startTimeLimit!!)))
        else if (test.state != TestState.CREATED)
            ErrorCode.TEST_START_WRONG_STATE.throwException(id, mapOf("state" to TestResource.State.fromValue(test.state)))

        test.start()
        testRepository.save(test)

        return TestResource.fromEntity(test)
    }

    /**
     * Finishes a test or returns an error if finish time limit has been exceeded.
     */
    fun finishTest(id: Int, request: FinishTestRequest, user: User): TestResource {
        log.info("Finishing test {}", id)
        val test = doGetTest(id)
        checkPermissionsToTest(user, test)

        if (test.state == TestState.STARTED_TIMEOUT)
            ErrorCode.TEST_FINISH_TIMEOUT.throwException(
                id, mapOf("timeout" to test.startedAt!!.plus(test.finishTimeLimit!!))
            )
        else if (test.state != TestState.STARTED)
            ErrorCode.TEST_FINISH_WRONG_STATE.throwException(id, mapOf("state" to TestResource.State.fromValue(test.state)))

        test.finish()
        test.enteredWords = WordSelection().deserialize(request.enteredWords)
        test.backspaceCount = request.backspaceCount

        testRepository.save(test)

        val result = TestResult()
        result.calculateResult(test)
        testResultRepository.save(result)

        return TestResource.fromEntity(test)
    }

    /**
     * Generates a new random word list for given test. Test cannot be started or finished.
     */
    fun regenerateWordList(testId: Int, user: User): TestResource {
        log.info("Regenerating word list of test {}", testId)

        val test = doGetTest(testId)
        checkPermissionsToTest(user, test)

        if (test.state != TestState.CREATED)
            ErrorCode.TEST_REGENERATE_WORD_ERROR.throwException(
                testId, mapOf("state" to TestResource.State.fromValue(test.state))
            )

        if (testService.regenerateWordList(test))
            testRepository.save(test)

        return TestResource.fromEntity(test)
    }

    private fun checkPermissionsToTest(user: User, test: Test) {
        if (test.takenBy.id != user.id && !user.isAdmin())
            throw SecurityException("User {} doesn't have permissions to test {}".format(user.id, test.id))
    }

    private fun reuseExistingTest(test: Test) {
        testService.regenerateWordList(test)

        // Treat this test as if it were new
        test.createdAt = Instant.now()
        test.wordRegenerationCount = 0

        testRepository.save(test)
    }

    private fun createNewTest(request: CreateTestRequest, creator: User): Test {
        val testDefinition = testDefinitionRepository.findById(request.testDefinitionId)
        if (testDefinition == null || !testDefinition.isActive)
            ErrorCode.TEST_DEFINITION_NOT_FOUND.throwException(request.testDefinitionId)

        val createdTest = testService.createTest(testDefinition, creator)
        testRepository.save(createdTest)

        log.info("Created test {}", createdTest.id)
        return createdTest
    }

    private fun doGetTest(id: Int): Test {
        return testRepository.findById(id) ?: ErrorCode.TEST_DEFINITION_NOT_FOUND.throwException(id)
    }
}