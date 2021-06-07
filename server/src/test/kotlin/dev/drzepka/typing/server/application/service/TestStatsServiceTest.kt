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
        whenever(testResultRepository.count(any(), any())).thenReturn(513)
        whenever(testResultRepository.find(any(), any(), any(), any()))
            .thenReturn(Sequence { emptyList<TestResult>().iterator() })

        getService().calculateTestStats(user, definition.id!!)

        verify(testResultRepository, times(1)).find(eq(user.id!!), eq(definition.id!!), eq(0), eq(513))
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

        getService().calculateTestStats(user, definition.id!!)

        verify(testResultRepository, times(1)).find(eq(user.id!!), eq(definition.id!!), eq(99), eq(1000))
    }

    private fun getService(): TestStatsService = TestStatsService(testDefinitionRepository, testResultRepository)

}