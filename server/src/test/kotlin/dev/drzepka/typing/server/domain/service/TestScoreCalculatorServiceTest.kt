package dev.drzepka.typing.server.domain.service

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class TestScoreCalculatorServiceTest {

    @Test
    fun `should calculate score`() {
        val service = TestScoreCalculatorService()
        val score = service.calculateScore(70F, 0.89f, 60)
        then(score).isEqualTo(920)
    }
}