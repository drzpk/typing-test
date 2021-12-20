package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.application.DomainTestDefinitionService
import dev.drzepka.typing.server.domain.entity.TestDefinition
import dev.drzepka.typing.server.domain.repository.TestDefinitionRepository
import dev.drzepka.typing.server.domain.repository.TestRepository
import dev.drzepka.typing.server.domain.repository.WordListRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class TestDefinitionServiceTest {

    private val domainTestDefinitionService = mock<DomainTestDefinitionService>()
    private val testDefinitionRepository = mock<TestDefinitionRepository>()
    private val testRepository = mock<TestRepository>()
    private val wordListRepository = mock<WordListRepository>()

    @Test
    fun `should delete test definition by setting deleted at property`() {
        val testDefinitionId = 342
        val testDefinition = TestDefinition().apply {
            id = testDefinitionId
        }

        whenever(testRepository.countByTestDefinitionId(eq(testDefinitionId))).thenReturn(3)
        whenever(testDefinitionRepository.findById(eq(testDefinitionId))).thenReturn(testDefinition)

        val status = getService().deleteTestDefinition(testDefinitionId)

        then(status).isTrue
        then(testDefinition.deletedAt).isNotNull
        verify(testDefinitionRepository).save(same(testDefinition))
    }

    @Test
    fun `should permanently delete test definition from the database`() {
        val testDefinitionId = 991
        whenever(testRepository.countByTestDefinitionId(eq(testDefinitionId))).thenReturn(0)
        whenever(testDefinitionRepository.delete(eq(testDefinitionId))).thenReturn(true)

        val status = getService().deleteTestDefinition(testDefinitionId)

        then(status).isTrue
        verify(testDefinitionRepository).delete(eq(testDefinitionId))
    }

    private fun getService(): TestDefinitionService =
        TestDefinitionService(domainTestDefinitionService, testDefinitionRepository, testRepository, wordListRepository)
}