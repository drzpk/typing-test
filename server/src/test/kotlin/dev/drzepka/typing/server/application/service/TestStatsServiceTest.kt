package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.entity.TestResult
import dev.drzepka.typing.server.domain.entity.User
import dev.drzepka.typing.server.domain.entity.WordList
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class TestStatsServiceTest {

    private val testDefinitionRepository = mock<TestDefinitionRepository>()
    private val testResultRepository = mock<TestResultRepository>()

    @Test
    fun `should calculate test stats - less than stats limit`() {
        val user = User().apply { id = 123 }
        val definition = TestDefinition().apply {
            id = 129
            wordList = WordList().apply { id = 99 }
        }
        whenever(testDefinitionRepository.findById(eq(definition.id!!))).thenReturn(definition)
        whenever(testResultRepository.count(any(), any())).thenReturn(97)
        whenever(testResultRepository.find(any(), any(), any(), any()))
            .thenReturn(Sequence { emptyList<TestResult>().iterator() })

        whenever(testResultRepository.count(isNull(), any())).thenReturn(7)
        whenever(testResultRepository.find(isNull(), any(), any(), any())).thenReturn(emptySequence())

        getService().calculateTestStats(user, definition.id!!)

        verify(testResultRepository).find(isNull(), eq(definition.id!!), eq(0), eq(7))
        verify(testResultRepository).find(eq(user.id!!), eq(definition.id!!), eq(0), eq(97))
    }

    @Test
    fun `should calculate test stats - more than stats limit`() {
        val user = User().apply { id = 123 }
        val definition = TestDefinition().apply {
            id = 129
            wordList = WordList().apply { id = 99 }
        }
        whenever(testDefinitionRepository.findById(eq(definition.id!!))).thenReturn(definition)
        whenever(testResultRepository.count(any(), any())).thenReturn(1099)
        whenever(testResultRepository.find(any(), any(), any(), any()))
            .thenReturn(Sequence { emptyList<TestResult>().iterator() })

        whenever(testResultRepository.count(isNull(), any())).thenReturn(2000)
        whenever(testResultRepository.find(isNull(), any(), any(), any())).thenReturn(emptySequence())

        getService().calculateTestStats(user, definition.id!!)

        verify(testResultRepository).find(isNull(), eq(definition.id!!), eq(1000), eq(1000))
        verify(testResultRepository).find(eq(user.id!!), eq(definition.id!!), eq(999), eq(100))
    }

    private fun getService(): TestStatsService = TestStatsService(testDefinitionRepository, testResultRepository)

}