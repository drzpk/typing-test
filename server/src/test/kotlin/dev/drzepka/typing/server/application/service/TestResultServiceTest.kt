package dev.drzepka.typing.server.application.service

import dev.drzepka.typing.server.domain.dao.TestResultDAO
import dev.drzepka.typing.server.domain.dto.TestResultDataDTO
import dev.drzepka.typing.server.domain.repository.TestResultRepository
import dev.drzepka.typing.server.domain.service.TestScoreCalculatorService
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class TestResultServiceTest {

    private val testResultRepository = mock<TestResultRepository>()
    private val testResultDAO = mock<TestResultDAO>()

    @Test
    fun `should get best results`() {
        val bestSpeedResults = listOf(
            TestResultDataDTO(1, "disp1", Instant.now(), 60, 30f, 0.67f),
            TestResultDataDTO(2, "disp2", Instant.now(), 90, 80f, 0.67f)
        )
        val bestAccuracyResults = listOf(
            // Deliberately the same ID as above
            TestResultDataDTO(1, "disp1", Instant.now(), 60, 30f, 0.67f)
        )

        whenever(testResultDAO.findHighestResultsBySpeed(any(), any())).thenReturn(bestSpeedResults)
        whenever(testResultDAO.findHighestResultsByAccuracy(any(), any())).thenReturn(bestAccuracyResults)

        val bestResults = getService().getBestResults(12)

        then(bestResults).hasSize(2)
        then(bestResults[0].userDisplayName).isEqualTo("disp2")
        then(bestResults[1].userDisplayName).isEqualTo("disp1")
    }

    private fun getService(): TestResultService =
        TestResultService(testResultRepository, testResultDAO, TestScoreCalculatorService())
}