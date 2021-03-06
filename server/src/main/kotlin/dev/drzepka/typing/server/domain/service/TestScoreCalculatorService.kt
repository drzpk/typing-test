package dev.drzepka.typing.server.domain.service

import dev.drzepka.typing.server.domain.util.Mockable
import kotlin.math.floor

@Mockable
class TestScoreCalculatorService {

    /**
     * Calculates test score based on given parameters.
     * @param speed test speed
     * @param accuracy test accuracy
     * @param testTimeSeconds test duration in seconds
     * @return calculated score
     */
    fun calculateScore(speed: Float, accuracy: Float, testTimeSeconds: Int): Int {
        val effectiveSpeed = speed * accuracy
        val testTimeBonus = testTimeSeconds / 60f * accuracy * 100 / 3
        return floor((effectiveSpeed + testTimeBonus) * 10 + 0.5f).toInt()
    }
}