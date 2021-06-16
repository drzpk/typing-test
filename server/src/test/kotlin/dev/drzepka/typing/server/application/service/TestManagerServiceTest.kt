package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.dto.test.CreateTestRequest
import dev.drzepka.typing.server.application.dto.test.FinishTestRequest
import dev.drzepka.typing.server.application.dto.test.TestResource
import dev.drzepka.typing.server.application.exception.ErrorCode
import dev.drzepka.typing.server.application.exception.ErrorCodeException
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.service.TestService
import dev.drzepka.typing.server.domain.value.TestState
import dev.drzepka.typing.server.domain.value.WordSelection
import org.assertj.core.api.BDDAssertions.catchThrowable
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class TestManagerServiceTest {

    private val testDefinitionRepository = mock<TestDefinitionRepository>()
    private val testRepository = mock<TestRepository>()
    private val testResultRepository = mock<TestResultRepository>()
    private val testService = mock<TestService>()

    @Test
    fun `should create test`() {
        val testDefinition = getTestDefinition(10)
        whenever(testDefinitionRepository.findById(10)).thenReturn(testDefinition)

        val creator = User().apply { id = 98 }
        val wordSelection = WordSelection()
        val test = dev.drzepka.typing.server.domain.entity.Test(testDefinition, creator, wordSelection).apply {
            id = 200
        }
        whenever(testService.createTest(same(testDefinition), same(creator))).thenReturn(test)

        val request = CreateTestRequest().apply { testDefinitionId = 10 }
        val created = getService().createTest(request, creator)

        then(created.id).isEqualTo(200)
        then(created.state).isEqualTo(TestResource.State.CREATED)
        verify(testRepository, times(1)).save(same(test))
    }

    @Test
    fun `should reuse the same test if it wasn't started`() {
        val user = User().apply { id = 500 }
        val existingTest =
            dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(20), user, WordSelection()).apply {
                id = 123
                wordRegenerationCount = 123
            }

        whenever(testRepository.findNotStartedByUserIdAndTestDefinitionId(eq(500), eq(20)))
            .thenReturn(listOf(existingTest))

        val request = CreateTestRequest().apply { testDefinitionId = 20 }
        val createdTest = getService().createTest(request, user)

        then(createdTest.id).isEqualTo(123)
        then(createdTest.wordRegenerationCount).isZero()
        verify(testRepository, times(1)).save(same(existingTest))
        verify(testService, times(0)).createTest(any(), any())
    }

    @Test
    fun `should delete test`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(1), User(), WordSelection())
        whenever(testRepository.findById(123)).thenReturn(test)

        getService().deleteTest(123, getUser())
        verify(testRepository, times(1)).delete(eq(123))
    }

    @Test
    fun `should forbid from deleting finished test`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(1), User(), WordSelection()).apply {
            startedAt = Instant.now().plusSeconds(60)
            finishedAt = Instant.now().plusSeconds(120)
        }
        whenever(testRepository.findById(123)).thenReturn(test)

        val throwable = catchThrowable { getService().deleteTest(123, getUser()) }

        then(throwable).isInstanceOf(ErrorCodeException::class.java)
        val exception = throwable as ErrorCodeException
        then(exception.code).isEqualTo(ErrorCode.CANNOT_DELETE_FINISHED_TEST)
    }

    @Test
    fun `should start test`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(1), User(), WordSelection()).apply {
            id = 13
        }
        whenever(testRepository.findById(13)).thenReturn(test)

        getService().startTest(13, getUser())

        then(test.state).isEqualTo(TestState.STARTED)
        verify(testRepository, times(1)).save(same(test))
    }

    @Test
    fun `should forbid from starting test in wrong state`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(300), User(), WordSelection()).apply {
            startedAt = Instant.now()
        }
        whenever(testRepository.findById(12)).thenReturn(test)

        val throwable = catchThrowable { getService().startTest(12, getUser()) }

        then(throwable).isInstanceOf(ErrorCodeException::class.java)
        val exception = throwable as ErrorCodeException
        then(exception.code).isEqualTo(ErrorCode.TEST_START_WRONG_STATE)
    }

    @Test
    fun `should finish test and save result`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(1), User(), WordSelection()).apply {
            id = 13
            startedAt = Instant.now()
            selectedWords = WordSelection().deserialize("abc|def")
        }
        whenever(testRepository.findById(13)).thenReturn(test)

        val request = FinishTestRequest().apply { enteredWords = "abc"; backspaceCount = 132 }
        getService().finishTest(13, request, getUser())

        then(test.state).isEqualTo(TestState.FINISHED)
        then(test.backspaceCount).isEqualTo(132)
        then(test.enteredWords).isNotNull
        verify(testRepository, times(1)).save(same(test))
        verify(testResultRepository, times(1)).save(any())
    }

    @Test
    fun `should forbid from finishing test in wrong state`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(300), User(), WordSelection()).apply {
            startedAt = Instant.now()
            finishedAt = Instant.now().plusSeconds(60)
        }
        whenever(testRepository.findById(12)).thenReturn(test)

        val throwable = catchThrowable { getService().finishTest(12, FinishTestRequest(), getUser()) }

        then(throwable).isInstanceOf(ErrorCodeException::class.java)
        val exception = throwable as ErrorCodeException
        then(exception.code).isEqualTo(ErrorCode.TEST_FINISH_WRONG_STATE)
    }

    @Test
    fun `should regenerate word list`() {
        val originalSelection = WordSelection()
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(9), User(), originalSelection).apply {
            id = 11
        }
        whenever(testRepository.findById(11)).thenReturn(test)
        whenever(testService.regenerateWordList(any())).thenAnswer { WordSelection() }

        getService().regenerateWordList(11, getUser())

        verify(testService, times(1)).regenerateWordList(same(test))
    }

    @Test
    fun `should forbid from regenerating word list from test in non-CREATED state`() {
        val test = dev.drzepka.typing.server.domain.entity.Test(getTestDefinition(3), User(), WordSelection()).apply {
            startedAt = Instant.now()
        }
        whenever(testRepository.findById(99)).thenReturn(test)

        val caught = catchThrowable { getService().regenerateWordList(99, getUser()) }

        then(caught).isInstanceOf(ErrorCodeException::class.java)
        val exception = caught as ErrorCodeException
        then(exception.code).isEqualTo(ErrorCode.TEST_REGENERATE_WORD_ERROR)
    }

    private fun getTestDefinition(id: Int): TestDefinition {
        return TestDefinition().apply {
            this.id = id
            wordList = WordList().apply { this.id = 2 }
        }
    }

    private fun getUser(): User = User().apply { email = "admin@drzepka.dev" }

    private fun getService(): TestManagerService =
        TestManagerService(testDefinitionRepository, testRepository, testResultRepository, testService)
}